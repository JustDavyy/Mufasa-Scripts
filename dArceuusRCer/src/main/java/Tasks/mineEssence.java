package Tasks;

import utils.Task;

import main.dArceuusRCer;

import java.awt.*;
import java.util.List;

import static helpers.Interfaces.*;

public class mineEssence extends Task {

    @Override
    public boolean activate() {
        return Player.within(dArceuusRCer.miningArea) && !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        // check invent open
        GameTabs.openInventoryTab();

        if (!dArceuusRCer.initialCheckDone) {
            Paint.setStatus("Perform initial check");
            resetToSouth();
            dArceuusRCer.lastItemTime = System.currentTimeMillis();
            dArceuusRCer.lastEmptySlots = Inventory.emptySlots();
            dArceuusRCer.initialCheckDone = true;
        }

        if (!Inventory.isFull()) {
            if (System.currentTimeMillis() - dArceuusRCer.lastItemTime > 30000) {
                Paint.setStatus("Reset");
                // Reset if no new item has been gained in the last 30 seconds
                Logger.debugLog("No new essence blocks gained in 30 seconds, resetting as we might be stuck.");
                resetToSouth();
                dArceuusRCer.lastItemTime = System.currentTimeMillis(); // Reset the timer after resetting
            }
            else if (Chatbox.findChatboxMenu() != null) {
                Logger.debugLog("Detected chatbox open, current runestone is inactive.");

                if (java.util.Objects.equals(dArceuusRCer.currentLoc, "south")) {
                    Paint.setStatus("Switch to north runestone");
                    Client.tap(dArceuusRCer.tapNorthRuneStoneSOUTH);  // Switch and start mining at the north runestone
                    Logger.debugLog("Switching to north runestone, as south is inactive.");
                    dArceuusRCer.currentLoc = "north";
                    dArceuusRCer.playerPos = dArceuusRCer.northDenseRunestone;
                    Paint.setStatus("Mine at north runestone");
                    Condition.sleep(4500);
                }
                else if (java.util.Objects.equals(dArceuusRCer.currentLoc, "north")) {
                    Paint.setStatus("Switch to south runestone");
                    Client.tap(dArceuusRCer.tapSouthRuneStoneNORTH);  // Switch and start mining at the south runestone
                    Logger.debugLog("Switching to south runestone, as north is inactive.");
                    dArceuusRCer.currentLoc = "south";
                    dArceuusRCer.playerPos = dArceuusRCer.southDenseRunestone;
                    Paint.setStatus("Mine at south runestone");
                    Condition.sleep(4500);
                }
            }
        }


        if (Player.leveledUp()) {
            Paint.setStatus("Re-mine after levelup");
            Client.sendKeystroke("KEYCODE_SPACE");
            Condition.sleep(dArceuusRCer.generateRandomDelay(1000, 2000));

            if (java.util.Objects.equals(dArceuusRCer.currentLoc, "south")) {
                Paint.setStatus("Tap south runestone");
                Client.tap(dArceuusRCer.tapSouthRuneStoneSOUTH);
                dArceuusRCer.playerPos = dArceuusRCer.southDenseRunestone;
            } else {
                Paint.setStatus("Tap north runestone");
                Client.tap(dArceuusRCer.tapNorthRuneStoneNORTH);
                dArceuusRCer.playerPos = dArceuusRCer.northDenseRunestone;
            }
        }


        if (java.util.Objects.equals(dArceuusRCer.currentLoc, "south")) {
            Paint.setStatus("Check south runestone");
            // Check if the runestone is inactive
            if (isRunestoneInactive(dArceuusRCer.southRuneStoneROI)) {
                Paint.setStatus("Switch to north runestone");
                Client.tap(dArceuusRCer.tapNorthRuneStoneSOUTH);  // Switch and start mining at the north runestone
                Logger.debugLog("Switching to north runestone, as south is inactive.");
                dArceuusRCer.currentLoc = "north";
                dArceuusRCer.playerPos = dArceuusRCer.northDenseRunestone;
                Paint.setStatus("Mine at north runestone");
            }
        } else if (java.util.Objects.equals(dArceuusRCer.currentLoc, "north")) {
            Paint.setStatus("Check north runestone");
            // Check if the runestone is inactive
            if (isRunestoneInactive(dArceuusRCer.northRuneStoneROI)) {
                Paint.setStatus("Switch to south runestone");
                Client.tap(dArceuusRCer.tapSouthRuneStoneNORTH);  // Switch and start mining at the south runestone
                Logger.debugLog("Switching to south runestone, as north is inactive.");
                dArceuusRCer.currentLoc = "south";
                dArceuusRCer.playerPos = dArceuusRCer.southDenseRunestone;
                Paint.setStatus("Mine at south runestone");
            }
        }

        int currentEmptySlots = Inventory.emptySlots();
        if (currentEmptySlots < dArceuusRCer.lastEmptySlots) {
            dArceuusRCer.lastItemTime = System.currentTimeMillis(); // Update the last item time when a new item is gained
            dArceuusRCer.lastEmptySlots = currentEmptySlots; // Update the last empty slot count
        }

        dArceuusRCer.readXP();

        // Update the statistics label
        dArceuusRCer.updateStatLabel();

        return false;
    }

    private boolean isRunestoneInactive(Rectangle roi) {
        // Check if the runestone is inactive by searching for specific colors within the region of interest
        List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCer.inactiveRunestone, roi, 5);
        return !foundPoints.isEmpty();  // Returns true if any points are found, indicating inactivity
    }

    private void resetToSouth() {
        if (Player.tileEquals(dArceuusRCer.playerPos, dArceuusRCer.southDenseRunestone)) {
            Paint.setStatus("Tap south runestone");
            Client.tap(dArceuusRCer.tapSouthRuneStoneSOUTH);
            dArceuusRCer.playerPos = Walker.getPlayerPosition();
            dArceuusRCer.currentLoc = "south";
        } else if (Player.tileEquals(dArceuusRCer.playerPos, dArceuusRCer.northDenseRunestone)) {
            Paint.setStatus("Tap north runestone");
            Client.tap(dArceuusRCer.tapNorthRuneStoneNORTH);
            dArceuusRCer.playerPos = Walker.getPlayerPosition();
            dArceuusRCer.currentLoc = "north";
        } else {
            Paint.setStatus("Fetch player position");
            dArceuusRCer.playerPos = Walker.getPlayerPosition();
            Paint.setStatus("Step to south runestone");
            Walker.step(dArceuusRCer.southDenseRunestone);
            dArceuusRCer.currentLoc = "south";
            Paint.setStatus("Tap south runestone");
            Client.tap(dArceuusRCer.tapSouthRuneStoneSOUTH);
            dArceuusRCer.playerPos = dArceuusRCer.southDenseRunestone;
        }
    }
}