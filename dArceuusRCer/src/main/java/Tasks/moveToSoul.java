package Tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import static helpers.Interfaces.*;

import main.dArceuusRCer;

import java.awt.*;

public class moveToSoul extends Task {

    @Override
    public boolean activate() {
        if (!dArceuusRCer.runeType.equals("Soul rune")) {
            return false;
        }

        return Player.within(dArceuusRCer.venerateAlterArea) && Inventory.containsAll(new int[]{ItemList.DARK_ESSENCE_BLOCK_13446, ItemList.DARK_ESSENCE_FRAGMENTS_7938}, 0.8) && doneVenerating() && dArceuusRCer.runeType.equals("Soul rune");
    }

    @Override
    public boolean execute() {
        // Check if we have to hop first
        Paint.setStatus("Check hop timer");
        dArceuusRCer.hopActions();

        // Have the logic that needs to be executed with the task here
        Paint.setStatus("Move to soul altar");
        Logger.log("Executing moveToSoul!");

        Logger.debugLog("Now walking from Venerate altar to Soul Altar.");
        Walker.walkPath(dArceuusRCer.venerateToSoulAltarPath);
        waitTillStopped(5);
        Walker.step(dArceuusRCer.soulAltarTile);

        if (dArceuusRCer.essenceCachedLoc == null) {
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            dArceuusRCer.essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + dArceuusRCer.essenceCachedLoc);
        }

        // Verify if we are in the area multiple times, if not, try walking there again and in the end reset if failed.
        if (!Player.within(dArceuusRCer.soulAltarArea)) {
            Logger.debugLog("Seems like we failed walking to the soul altar, retrying...");
            Walker.walkPath(dArceuusRCer.venerateToSoulAltarPath);
            waitTillStopped(5);

            if (!Player.within(dArceuusRCer.soulAltarArea)) {
                Logger.debugLog("Seems like we failed walking to the soul altar again, retrying...");
                Walker.walkPath(dArceuusRCer.venerateToSoulAltarPath);
                waitTillStopped(5);

                if (!Player.within(dArceuusRCer.soulAltarArea)) {
                    Logger.debugLog("Seems like we failed walking to the soul altar, retrying but first moving away from our current spot randomly.");
                    Client.tap(new Rectangle(755, 64, 99, 43));
                    Condition.sleep(1500);
                    Walker.walkPath(dArceuusRCer.venerateToSoulAltarPath);
                    waitTillStopped(5);

                    if (!Player.within(dArceuusRCer.soulAltarArea)) {
                        Logger.debugLog("Seems like we failed walking to the soul altar, retrying but first moving away from our current spot randomly.");
                        Client.tap(new Rectangle(755, 64, 99, 43));
                        Condition.sleep(1500);
                        Walker.walkPath(dArceuusRCer.venerateToSoulAltarPath);
                        waitTillStopped(5);
                    }
                }
            }
        }

        if (Player.within(dArceuusRCer.soulAltarArea)) {
            Logger.debugLog("Player is now at the Soul Altar.");
        }

        // Update the statistics label
        dArceuusRCer.updateStatLabel();

        return false;
    }


    private void waitTillStopped(int waitTimes) {
        // Wait till we stop moving
        Tile lastPosition = Walker.getPlayerPosition();
        dArceuusRCer.playerPos = lastPosition;
        int unchangedCount = 0; // Counter for how many times the position has remained the same
        boolean runEnabled = Player.isRunEnabled();

        Logger.debugLog("Waiting for us to stop moving...");
        while (unchangedCount < waitTimes) { // Loop until the position hasn't changed for the specified number of checks
            Tile currentPosition = Walker.getPlayerPosition();
            dArceuusRCer.playerPos = lastPosition;

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
                Condition.sleep(dArceuusRCer.generateRandomDelay(100, 200));
            } else {
                Condition.sleep(dArceuusRCer.generateRandomDelay(200, 300));
            }
        }
        Logger.debugLog("We have stopped moving!");
    }

    private boolean doneVenerating() {

        // Check if we have a cached known location for the last essence spot already, if not cache it
        if (dArceuusRCer.essenceCachedLoc == null) {
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            dArceuusRCer.essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + dArceuusRCer.essenceCachedLoc);
        }

        // Calculate the center of the rectangle
        int centerX = dArceuusRCer.essenceCachedLoc.x + dArceuusRCer.essenceCachedLoc.width / 2;
        int centerY = dArceuusRCer.essenceCachedLoc.y + dArceuusRCer.essenceCachedLoc.height / 2;

        // Define the 5x5 area around the center point
        java.awt.Rectangle smallRect = new java.awt.Rectangle(centerX - 2, centerY - 2, 5, 5);

        // Check if the essence is dark instead of dense to verify if we have venerated correctly
        return Client.isColorInRect(Color.decode("#675b4e"), smallRect, 10);
    }
}