package tasks;

import utils.SideManager;
import utils.StateUpdater;
import utils.Task;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;
import static utils.Helpers.countFoodInInventory;

public class PreGame extends Task {
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
            if (!GameTabs.isInventoryTabOpen()) {
                Logger.log("Checking inventory for food in pre-game");
                GameTabs.openInventoryTab();
                Condition.wait(() -> GameTabs.isInventoryTabOpen(), 100, 10);
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

        // Read the timer on the WT bar
        String results = Chatbox.readLastLine(new Rectangle(207, 46, 30, 14));

        // Check if results are not empty
        if (results != null && !results.trim().isEmpty()) {
            try {
                // Extract the seconds from the results using a regular expression
                Pattern pattern = Pattern.compile("\\d+:\\d+");
                Matcher matcher = pattern.matcher(results);
                if (matcher.find()) {
                    String time = matcher.group();
                    int seconds = Integer.parseInt(time.split(":")[1].trim());
                    if (seconds >= 5) {
                        Paint.setStatus("Wintertodt starting in " + seconds + "s");
                        Logger.log("Wintertodt starting in " + seconds + " seconds.");
                    }


                    // Check if the time is between 5 and 10 seconds (inclusive)
                    if (seconds >= 5 && seconds <= 10) {
                        // Wait for 1 seconds less than the number of seconds read
                        int waitTime = Math.max(0, seconds - 1) * 1000; // Convert to milliseconds
                        Condition.sleep(waitTime);

                        // Start a 1,5-second while loop with an action every 200-300ms
                        long startTime = System.currentTimeMillis();
                        while (System.currentTimeMillis() - startTime < 1500) {
                            Paint.setStatus("Perform initial burn");
                            totalRelightCount = totalRelightCount + 1;
                            Paint.setStatistic("Brazier repairs: " + totalRepairCount + " | Brazier relights: " + totalRelightCount);
                            Client.tap(SideManager.getBurnRect());

                            Condition.sleep(generateRandomDelay(200, 300));
                        }

                        Condition.sleep(generateRandomDelay(750, 1000));

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
                        Logger.debugLog("Current game count since break:" + BreakManager.currentGameCount);
                        Logger.log("Games till next break: " + (BreakManager.shouldBreakAt - BreakManager.currentGameCount));

                        Paint.setStatus("Resetting states");
                        StateUpdater.mageDeadTimestamps.put("Left", -1L);
                        StateUpdater.mageDeadTimestamps.put("Right", -1L);

                        // Reset our booleans before exiting the task
                        shouldStartWithBurn = false;
                    }
                } else {
                    Logger.debugLog("No valid time format found in OCR result: " + results);
                }
            } catch (NumberFormatException e) {
                Logger.debugLog("Failed to parse seconds from OCR result: " + results);
            }
        }

        return false;
    }
}
