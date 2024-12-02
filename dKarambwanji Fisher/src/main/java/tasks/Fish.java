package tasks;

import helpers.utils.ItemList;
import helpers.utils.OverlayColor;
import helpers.utils.Tile;
import utils.Task;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import static helpers.Interfaces.*;
import static main.dKarambwanjiFisher.*;

public class Fish extends Task {

    @Override
    public boolean activate() {
        return shouldFish();
    }

    @Override
    public boolean execute() {

        Paint.setStatus("Start fish action");
        Logger.log("Start fish action.");
        Polygon fishSquare = Overlay.findNearest(OverlayColor.FISHING);
        if (fishSquare != null) {
            // Calculate the approximate center of the polygon
            Rectangle bounds = fishSquare.getBounds();
            int centerX = bounds.x + bounds.width / 2;
            int centerY = bounds.y + bounds.height / 2;

            // Add some random offset to make it less predictable
            int offsetX = (int) (Math.random() * 4) + 1; // Random value between 1 and 4
            int offsetY = (int) (Math.random() * 4) + 1; // Random value between 1 and 4

            // Randomly decide whether to add or subtract the offset
            centerX += Math.random() < 0.5 ? -offsetX : offsetX;
            centerY += Math.random() < 0.5 ? -offsetY : offsetY;

            Client.tap(new Point(centerX, centerY)); // Tap around the randomized center
            lastXpGainTime = Instant.now().plusSeconds(5);
            lastActionTime = Instant.now().plusSeconds(5);
            Condition.sleep(4000);
        } else {
            Logger.debugLog("Could not locate the nearest fishing spot, moving around to find different spots.");
            findNewSpot();
        }

        return true;
    }

    private void findNewSpot() {
        Paint.setStatus("Find new spot");
        Logger.log("Finding new fishing spot.");
        // Generate a random index to choose a spot
        Random random = new Random();
        int index;
        if (SafeModeOn) {
            // Exclude the South spot if safe mode is on
            index = random.nextInt(fishingSpots.length - 1);
        } else {
            // Include all spots if safe mode is off
            index = random.nextInt(fishingSpots.length);
        }

        Tile selectedSpot = fishingSpots[index];
        if (index == 0){
            FishingSpot = "North-East";
        } else if (index == 1) {
            FishingSpot = "East";
        } else if (index == 2) {
            FishingSpot = "North-West";
        } else if (index == 3) {
            FishingSpot = "South";
        }
        // Log the selected spot
        Logger.debugLog("Moving to a new fishing spot: " + FishingSpot);

        Walker.step(selectedSpot);
        lastActionTime = Instant.now();
        Condition.wait(() -> Player.atTile(selectedSpot), 500, 35);
    }
}