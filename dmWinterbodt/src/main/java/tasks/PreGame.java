package tasks;

import helpers.utils.UITabs;
import utils.SideManager;
import utils.StateUpdater;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;
import static utils.Helpers.countFoodInInventory;

public class PreGame extends Task {


    private final Rectangle startTimerRect = new Rectangle(198, 23, 70, 13);
    private final Rectangle warmthPercentRect = new Rectangle(171, 28, 32, 17);
    private final List<java.awt.Color> blackColor = Arrays.asList(
            java.awt.Color.decode("#000001")
    );

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside PreGame activate()");
        StateUpdater.updateIsGameGoing();
        return waitingForGameEnded && !isGameGoing && Player.isTileWithinArea(currentLocation, insideArea) || shouldStartWithBurn && !isGameGoing && Player.isTileWithinArea(currentLocation, insideArea);
    }

    @Override
    public boolean execute() {
        //Logger.debugLog("Inside PreGame execute()");
        if (Player.leveledUp()) {
            Client.sendKeystroke("KEYCODE_SPACE");
            Condition.sleep(generateRandomDelay(1000, 2000));
        }

        if (preGameFoodCheck) {
            if (!GameTabs.isTabOpen(UITabs.INVENTORY)) {
                Logger.log("Checking inventory for food in pre-game");
                GameTabs.openTab(UITabs.INVENTORY);
                Condition.wait(() -> GameTabs.isTabOpen(UITabs.INVENTORY), 100, 10);
                countFoodInInventory();
                preGameFoodCheck = false;
            }
        }

        // Check if we are at the burn tile, otherwise move there
        if (BreakManager.shouldBreakNow && !Player.isTileWithinArea(currentLocation, lobby)) {
            Paint.setStatus("Moving to burn tile");
            Walker.walkPath(fromEitherSideToGameLobby);
            currentLocation = Walker.getPlayerPosition();
            return true;
        } else if (!Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
            Paint.setStatus("Stepping to burn tile");
            Walker.step(SideManager.getBurnTile());
        }

        // If at burn tile, update current location and lock ourselves to this task
        if (Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
            Paint.setStatus("Updating our location");
            // Set shouldStartWithBurn to true, so we lock ourselves in this task.
            shouldStartWithBurn = true;

            // Update our current location
            currentLocation = SideManager.getBurnTile();
        }

        gameAt15Percent = false;
        gameAt20Percent = false;
        gameAt70Percent = false;
        isGameGoing = false;
        inventoryHasLogs = false;
        inventoryHasKindlings = false;

        // Read the timer on the WT bar
        int results = Chatbox.readDigitsInArea(startTimerRect, blackColor);

        // Check if results are valid
        if (results != -1) {
            try {
                // Calculate minutes and seconds from the results
                int minutes;
                int seconds;

                if (results >= 100) {
                    // If the result is 100 or more, treat it as MMSS format
                    minutes = results / 100;
                    seconds = results % 100;
                } else {
                    // Otherwise, treat it as total seconds
                    minutes = results / 60;
                    seconds = results % 60;
                }

                // Format the time as "minutes:seconds"
                String timeFormatted = String.format("%d:%02d", minutes, seconds);

                if (seconds >= 5) {
                    Paint.setStatus("Wintertodt starting in " + timeFormatted);
                    Logger.log("Wintertodt starting in " + timeFormatted);

                    if (seconds >= 15) {
                        Logger.log("Sleeping for: " + (seconds - 10) + " seconds.");
                        Paint.setStatus("Sleep for " + (seconds - 10) + " seconds");
                        Condition.sleep((seconds - 10) * 1000);
                    }

                    // Check if the time is between 5 and 10 seconds (inclusive)
                    if (seconds >= 5 && seconds <= 10) {
                        // Wait for 1 second less than the number of seconds read
                        int waitTime = Math.max(0, seconds - 1) * 1000; // Convert to milliseconds
                        Condition.sleep(waitTime);

                        // Start a 1.2-second while loop with an action every 200-300 ms
                        long startTime = System.currentTimeMillis();
                        while (System.currentTimeMillis() - startTime < 1200) {
                            Paint.setStatus("Perform initial burn");
                            totalRelightCount++;
                            Paint.setStatistic("Brazier repairs: " + totalRepairCount + " | Relights: " + totalRelightCount);
                            Client.tap(SideManager.getBurnRect());

                            Condition.sleep(generateRandomDelay(200, 300));
                        }

                        Condition.sleep(generateRandomDelay(1800, 2400));

                        // Move to the branch tile
                        Paint.setStatus("Stepping to branch tile");
                        Walker.step(SideManager.getBranchTile());
                        lastActivity = System.currentTimeMillis();
                        currentLocation = SideManager.getBranchTile();

                        BreakManager.currentGameCount++;
                        totalGameCount++;
                        Logger.log("Total Game Count: " + totalGameCount);
                        Paint.updateBox(brazierIndex, totalGameCount - 1);
                        Paint.updateBox(crateIndex, totalGameCount - 1);
                        Logger.debugLog("Current game count since break: " + BreakManager.currentGameCount);
                        Logger.log("Games till next break: " + (BreakManager.shouldBreakAt - BreakManager.currentGameCount));
                        StateUpdater.updateGameAt15();

                        Paint.setStatus("Resetting states");
                        StateUpdater.mageDeadTimestamps.put("Left", -1L);
                        StateUpdater.mageDeadTimestamps.put("Right", -1L);

                        // Reset our booleans before exiting the task
                        shouldStartWithBurn = false;
                    }
                }
            } catch (Exception e) {
                Logger.debugLog("Error processing OCR result: " + e.getMessage());
            }
        } else {
            Logger.debugLog("No valid digits found in the specified area.");
        }

        return false;
    }
}
