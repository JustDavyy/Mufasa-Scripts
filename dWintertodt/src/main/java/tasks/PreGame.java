package tasks;

import utils.SideManager;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class PreGame extends Task {
    @Override
    public boolean activate() {
        Logger.debugLog("Inside PreGame activate()");
        return waitingForGameEnded || shouldStartWithBurn;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside PreGame execute()");

        // Check if we are at the burn tile, otherwise move there
        if (!Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
            Walker.step(SideManager.getBurnTile(), WTRegion);
        }

        // If at burn tile, update current location and lock ourselves to this task
        if (Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
            // Set shouldStartWithBurn to true, so we lock ourselves in this task.
            shouldStartWithBurn = true;

            // Update our current location
            currentLocation = SideManager.getBurnTile();
        }

        // Read the timer on the WT bar
        String results = Chatbox.readLastLine(new Rectangle(209, 33, 25, 12));

        // Check if results are not empty
        if (results != null && !results.trim().isEmpty()) {
            try {
                // Extract the seconds from the results (assuming the format is "0:10")
                int seconds = Integer.parseInt(results.split(":")[1].trim());
                Logger.log("Wintertodt starting in: 0:" + seconds);

                // Check if the time is 10 seconds or below
                if (seconds <= 10) {
                    // Wait for 2 seconds less than the number of seconds read
                    int waitTime = Math.max(0, seconds - 2) * 1000; // Convert to milliseconds
                    Condition.sleep(waitTime);

                    // Start a 3-second while loop with an action every 200-300ms
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < 3000) {
                        Client.tap(SideManager.getBurnRect());

                        Condition.sleep(generateRandomDelay(200, 300));
                    }

                    // Move to the branch tile
                    Walker.step(SideManager.getBranchTile(), WTRegion);
                    currentLocation = SideManager.getBranchTile();

                    // Reset our booleans before exiting the task
                    shouldStartWithBurn = false;
                }
            } catch (NumberFormatException e) {
                Logger.debugLog("Failed to parse seconds from OCR result: " + results);
            }
        }

        return false;
    }
}
