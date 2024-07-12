package Tasks;

import helpers.utils.Tile;
import utils.Task;

import static helpers.Interfaces.*;

import main.dArceuusRCerNEW;

import java.awt.*;
import java.util.List;

public class venerateEssence extends Task {

    @Override
    public boolean activate() {
        // Have your conditions to activate this task here
        Logger.log("Checking if we should execute venerateEssence");
        return Player.within(dArceuusRCerNEW.venerateAlterArea) && Inventory.isFull() && !doneVenerating();
    }

    @Override
    public boolean execute() {
        // Have the logic that needs to be executed with the task here
        Logger.log("Executing venerateEssence!");

        List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCerNEW.venerateAltarColors, new Rectangle(312, 164, 205, 163), 5);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the venerate altar using the color finder, tapping.");
            Client.tap(foundPoints, true);
            Condition.wait(this::doneVenerating, 100, 50);
        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(dArceuusRCerNEW.venerateAltarTile);
            waitTillStopped(6);

            if (!Player.atTile(dArceuusRCerNEW.venerateAltarTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(dArceuusRCerNEW.venerateAltarTile);
                waitTillStopped(6);

                if (!Player.atTile(dArceuusRCerNEW.venerateAltarTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(dArceuusRCerNEW.venerateAltarTile);
                    waitTillStopped(6);
                }
            }

            if (Player.atTile(dArceuusRCerNEW.venerateAltarTile)) {
                Client.tap(dArceuusRCerNEW.venerateAltar);
                Condition.wait(this::doneVenerating, 100, 50);
            }
        }

        if (!doneVenerating()) {
            Logger.debugLog("Seems like venerating has failed, retrying!");
        } else {
            Logger.debugLog("Essences have been successfully venerated.");
        }

        return false;
    }

    private void waitTillStopped(int waitTimes) {
        // Wait till we stop moving
        Tile lastPosition = Walker.getPlayerPosition();
        int unchangedCount = 0; // Counter for how many times the position has remained the same
        boolean runEnabled = Player.isRunEnabled();

        Logger.debugLog("Waiting for us to stop moving...");
        while (unchangedCount < waitTimes) { // Loop until the position hasn't changed for the specified number of checks
            Tile currentPosition = Walker.getPlayerPosition();

            // Compare currentPosition and lastPosition by coordinates
            if (java.util.Objects.equals(currentPosition.toString(), lastPosition.toString())) {
                // If the current position is the same as the last position, increment the unchanged counter
                unchangedCount++;
            } else {
                // If the position has changed, reset the counter
                unchangedCount = 0;
            }

            lastPosition = currentPosition; // Update lastPosition for the next check

            // Adjust the sleep time based on whether the player is running or not
            if (runEnabled) {
                Condition.sleep(dArceuusRCerNEW.generateRandomDelay(100, 200));
            } else {
                Condition.sleep(dArceuusRCerNEW.generateRandomDelay(200, 300));
            }
        }
        Logger.debugLog("We have stopped moving!");
    }

    private boolean doneVenerating() {

        // Check if we have a cached known location for the last essence spot already, if not cache it
        if (dArceuusRCerNEW.essenceCachedLoc == null) {
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            dArceuusRCerNEW.essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + dArceuusRCerNEW.essenceCachedLoc);
        }

        // Calculate the center of the rectangle
        int centerX = dArceuusRCerNEW.essenceCachedLoc.x + dArceuusRCerNEW.essenceCachedLoc.width / 2;
        int centerY = dArceuusRCerNEW.essenceCachedLoc.y + dArceuusRCerNEW.essenceCachedLoc.height / 2;

        // Define the 5x5 area around the center point
        java.awt.Rectangle smallRect = new java.awt.Rectangle(centerX - 2, centerY - 2, 5, 5);

        // Check if the essence is dark instead of dense to verify if we have venerated correctly
        return Client.isColorInRect(Color.decode("#675b4e"), smallRect, 10);
    }
}