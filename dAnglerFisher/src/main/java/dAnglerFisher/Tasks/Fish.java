package dAnglerFisher.Tasks;

import helpers.utils.*;
import dAnglerFisher.Task;
import dAnglerFisher.dAnglerFisher;


import java.awt.*;
import java.time.Instant;
import java.util.Random;

import static helpers.Interfaces.*;

public class Fish extends Task {
    dAnglerFisher main;
    private final Random random = new Random();
    Rectangle rightRect = new Rectangle(451, 263, 30, 13);
    Rectangle bottomRect = new Rectangle(439, 275, 10, 25);
    boolean inventoryFull = false;
    Tile stepTile = new Tile(7319, 14829, 0);
    Tile walkToTile = new Tile(7319, 14837, 0);
    public Fish(dAnglerFisher main){
        super();
        super.name = "Fish";
        this.main = main;
    }
    @Override
    public boolean activate() {
        inventoryFull = Inventory.isFull();
        return !inventoryFull && dAnglerFisher.shouldFish() || Player.leveledUp() && !inventoryFull || !isSpotAgainstUs(true) && !inventoryFull;
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        if (!Player.within(dAnglerFisher.fishingArea)) {
            Walker.step(stepTile);
        }
        if (Player.within(dAnglerFisher.upperFishSpotArea) && !isSpotAgainstUs(false)) {
            Walker.walkTo(walkToTile);
        }
        Logger.log("Fishing.");
        GameTabs.openInventoryTab();
        performFish();
        Paint.setStatus("Fishing");
        dAnglerFisher.updatePaint();
        dAnglerFisher.updateStatLabel();
        return false;
    }

    private void performFish() {
        Paint.setStatus("Find fishing spot");
        Polygon fishSquare = Overlay.findNearest(OverlayColor.FISHING);
        if (fishSquare != null) {
            Paint.setStatus("Tap fishing spot");
            Point center = calculatePolygonCenter(fishSquare);
            Point randomizedPoint = randomizePoint(center);
            Client.tap(randomizedPoint);
            dAnglerFisher.lastXpGainTime = Instant.now();
            dAnglerFisher.lastActionTime = Instant.now();
            Condition.wait(() -> isSpotAgainstUs(false), 300, 40);
            Condition.sleep(8000);
        } else {
            Logger.log("Could not locate the nearest fishing spot, retrying.");
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
    private Point randomizePoint(Point point) {
        int randomizedX = point.x + random.nextInt(2 - -2 + 1) + -2;
        int randomizedY = point.y + random.nextInt(2 - -2 + 1) + -2;
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

        return false;
    }
}
