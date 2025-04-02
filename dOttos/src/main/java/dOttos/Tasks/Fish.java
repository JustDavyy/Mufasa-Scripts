package dOttos.Tasks;

import helpers.utils.*;
import dOttos.Task;
import dOttos.dOttos;


import java.awt.*;
import java.time.Instant;
import java.util.Random;

import static helpers.Interfaces.*;

public class Fish extends Task {
    dOttos main;
    private final Random random = new Random();
    Area southArea = new Area(
            new Tile(9993, 13709, 0),
            new Tile(10033, 13755, 0)
    );
    public static Area northArea = new Area(
            new Tile(10005, 13802, 0),
            new Tile(10045, 13827, 0)
    );
    Area westArea = new Area(
            new Tile(9983, 13751, 0),
            new Tile(10012, 13808, 0)
    );
    Rectangle rightRect = new Rectangle(465, 267, 57, 22);
    Rectangle bottomRect = new Rectangle(441, 303, 24, 43);
    Tile[] northToWestPath = new Tile[] {
            new Tile(10012, 13820, 0),
            new Tile(9998, 13808, 0),
            new Tile(9995, 13781, 0)
    };

    Tile[] northeastToWestPath = new Tile[] {
            new Tile(10075, 13831, 0),
            new Tile(10053, 13828, 0),
            new Tile(10025, 13826, 0),
            new Tile(10005, 13818, 0),
            new Tile(9996, 13804, 0),
            new Tile(9994, 13777, 0)
    };
    boolean inventoryFull = false;
    public Fish(dOttos main){
        super();
        super.name = "Fish";
        this.main = main;
    }
    @Override
    public boolean activate() {
        inventoryFull = Inventory.isFull();
        return !inventoryFull && dOttos.doneDropping || !inventoryFull && dOttos.shouldFish() || Player.leveledUp() && !inventoryFull || !isSpotAgainstUs(true) && !inventoryFull;
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        Logger.log("Fishing.");
        GameTabs.openInventoryTab();
        performFish();
        dOttos.doneDropping = false;
        Paint.setStatus("Fishing");
        return false;
    }

    private void performFish() {
        Paint.setStatus("Find fishing spot");
        Polygon fishSquare = Overlay.findNearest(OverlayColor.FISHING);
        if (fishSquare != null) {
            Paint.setStatus("Tap fishing spot");
            Point center = calculatePolygonCenter(fishSquare);
            Point randomizedPoint = randomizePoint(center, -2, 2);
            Client.tap(randomizedPoint);
            dOttos.lastXpGainTime = Instant.now();
            dOttos.lastActionTime = Instant.now();
            Condition.wait(() -> isSpotAgainstUs(false), 300, 40);
            Condition.sleep(5000);
        } else {
            Paint.setStatus("Move to other fishing area");
            Logger.log("Could not locate the nearest fishing spot, moving around to find different spots.");
            findNewSpot();
        }
    }

    public void findNewSpot(){
        if(Player.within(northArea)){
            Paint.setStatus("Move to west spot");
            Logger.log("Walking to west spots.");
            Walker.walkPath(northToWestPath);
            dOttos.lastActionTime = Instant.now();
            Condition.wait(() -> Player.within(westArea), 500, 20);
        }
        else if (Player.within(southArea)){
            Paint.setStatus("Move to west spot");
            Logger.log("Walking to west spots.");
            Walker.step(dOttos.westSpot);
            dOttos.lastActionTime = Instant.now();
            Condition.wait(() -> Player.atTile(dOttos.westSpot), 500, 20);
        }
        else if (Player.within(westArea)){
            Paint.setStatus("Move to north spot");
            Logger.log("Walking to north spots.");
            Walker.step(dOttos.northSpot);
            dOttos.lastActionTime = Instant.now();
            Condition.wait(() -> Player.atTile(dOttos.northSpot), 500, 20);
        }
        else if (Player.within(dOttos.fishingArea3)) {
            Paint.setStatus("Move to west spot");
            Logger.log("Walking to the west spots");
            Walker.walkPath(northeastToWestPath);
            dOttos.lastActionTime = Instant.now();
            Condition.wait(() -> Player.within(westArea), 500, 20);
        }
        else{
            Walker.step(dOttos.westSpot);
            Condition.wait(() -> Player.atTile(dOttos.westSpot), 500, 20);
            if (Player.within(westArea)){
                dOttos.lastActionTime = Instant.now();
            }
            else {
                Logger.log("Not at fishing area, stopping script.");
                Logout.logout();
                Script.stop();
            }
        }
    }

    private Point calculatePolygonCenter(Polygon polygon) {
        int sumX = 0;
        int sumY = 0;
        int n = polygon.npoints;

        for (int i = 0; i < n; i++) {
            sumX += polygon.xpoints[i];
            sumY += polygon.ypoints[i];
        }

        return new Point(sumX / n, sumY / n);
    }

    // Randomize the point within a given range
    private Point randomizePoint(Point point, int minOffset, int maxOffset) {
        int randomizedX = point.x + random.nextInt(maxOffset - minOffset + 1) + minOffset;
        int randomizedY = point.y + random.nextInt(maxOffset - minOffset + 1) + minOffset;
        return new Point(randomizedX, randomizedY);
    }

    private boolean isSpotAgainstUs(boolean log) {
        // Check if the color is present in the right or bottom rectangles
        if (Client.isColorInRect(OverlayColor.FISHING, rightRect, 5) ||
                Client.isColorInRect(OverlayColor.FISHING, bottomRect, 5)) {
            return true;
        }

        // Log that the spot we're using moved, since we couldn't find a spot
        if (log) {
            Logger.debugLog("Moving to a new spot as the current spot has moved.");
        }

        // We don't need to check top or left, as there are no spots for us there.
        // Client.isColorInRect(OverlayColor.FISHING, topRect, 5)
        // Client.isColorInRect(OverlayColor.FISHING, leftRect, 5)

        return false;
    }
}
