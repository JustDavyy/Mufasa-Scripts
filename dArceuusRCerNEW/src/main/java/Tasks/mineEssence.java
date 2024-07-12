package Tasks;

import helpers.utils.Tile;
import utils.Task;

import main.dArceuusRCerNEW;

import java.awt.*;
import java.util.List;

import static helpers.Interfaces.*;

public class mineEssence extends Task {

    @Override
    public boolean activate() {
        // Have your conditions to activate this task here
        Logger.log("Checking if we should execute mineEssence");

        return Player.within(dArceuusRCerNEW.miningArea) && !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        // Have the logic that needs to be executed with the task here
        Logger.log("Executing mineEssence!");

        if (!dArceuusRCerNEW.initialCheckDone) {
            resetToSouth();
            dArceuusRCerNEW.initialCheckDone = true;
        }

        dArceuusRCerNEW.lastItemTime = System.currentTimeMillis(); // Track the last time an item was gained
        dArceuusRCerNEW.lastEmptySlots = Inventory.emptySlots(); // Current number of empty slots

        if (!Inventory.isFull()) {
            if (System.currentTimeMillis() - dArceuusRCerNEW.lastItemTime > 30000) {
                // Reset if no new item has been gained in the last 45 seconds
                Logger.debugLog("No new essence blocks gained in 30 seconds, resetting to south runestone as we might be stuck.");
                resetToSouth();
                dArceuusRCerNEW.lastItemTime = System.currentTimeMillis(); // Reset the timer after resetting
            }
        }

        if (Player.leveledUp()) {
            if (Player.leveledUp()) {
                Client.sendKeystroke("KEYCODE_SPACE");
                Condition.sleep(dArceuusRCerNEW.generateRandomDelay(1000, 2000));

                if (java.util.Objects.equals(dArceuusRCerNEW.currentLoc, "south")) {
                    Client.tap(dArceuusRCerNEW.tapSouthRuneStoneSOUTH);
                } else {
                    Client.tap(dArceuusRCerNEW.tapNorthRuneStoneNORTH);
                }
            }
        }

        if (java.util.Objects.equals(dArceuusRCerNEW.currentLoc, "south")) {
            // Check if the runestone is inactive
            if (isRunestoneInactive(dArceuusRCerNEW.southRuneStoneROI)) {
                Client.tap(dArceuusRCerNEW.tapNorthRuneStoneSOUTH);  // Switch and start mining at the north runestone
                Logger.debugLog("Switching to north runestone, as south is inactive.");
                dArceuusRCerNEW.currentLoc = "north";
            }
        } else if (java.util.Objects.equals(dArceuusRCerNEW.currentLoc, "north")) {
            // Check if the runestone is inactive
            if (isRunestoneInactive(dArceuusRCerNEW.northRuneStoneROI)) {
                Client.tap(dArceuusRCerNEW.tapSouthRuneStoneNORTH);  // Switch and start mining at the south runestone
                Logger.debugLog("Switching to south runestone, as north is inactive.");
                dArceuusRCerNEW.currentLoc = "south";
            }
        }

        int currentEmptySlots = Inventory.emptySlots();
        if (currentEmptySlots < dArceuusRCerNEW.lastEmptySlots) {
            dArceuusRCerNEW.lastItemTime = System.currentTimeMillis(); // Update the last item time when a new item is gained
            dArceuusRCerNEW.lastEmptySlots = currentEmptySlots; // Update the last empty slot count
        }

        dArceuusRCerNEW.readXP();

        return false;
    }

    private boolean isRunestoneInactive(Rectangle roi) {
        // Check if the runestone is inactive by searching for specific colors within the region of interest
        List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCerNEW.inactiveRunestone, roi, 5);
        return !foundPoints.isEmpty();  // Returns true if any points are found, indicating inactivity
    }

    private void resetToSouth() {
        dArceuusRCerNEW.playerPos = Walker.getPlayerPosition();
        Walker.step(dArceuusRCerNEW.southDenseRunestone);
        Condition.wait(() -> Player.atTile(dArceuusRCerNEW.southDenseRunestone), 250, 10);
        dArceuusRCerNEW.currentLoc = "south";
        Client.tap(dArceuusRCerNEW.tapSouthRuneStoneSOUTH); // Start mining at the south runestone
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
}