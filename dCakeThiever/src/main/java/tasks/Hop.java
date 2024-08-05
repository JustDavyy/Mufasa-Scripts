package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dCakeThiever.*;

public class Hop extends Task {
    @Override
    public boolean activate() {
        return Player.atTile(stallTile, ardyRegion) && Game.isPlayersUnderUs();
    }

    @Override
    public boolean execute() {
        Logger.log("Hop task activated.");

        int checkDuration = 7000; // Total time to check (7 seconds in milliseconds)
        int checkInterval = 1000;  // Check interval (1 second in milliseconds)
        long startTime = System.currentTimeMillis();
        boolean playerPresentContinuously = true;

        Logger.log("Starting to check for players under us every second.");

        while (System.currentTimeMillis() - startTime < checkDuration) {
            // Check if a player is under us
            if (!Game.isPlayersUnderUs()) {
                Logger.log("No player detected under us. Exiting task.");
                playerPresentContinuously = false;
                break;
            }

            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            Logger.log("Player detected under us for " + elapsedTime + "/7 seconds. Continuing to check...");

            // Wait for the check interval before checking again
            Condition.sleep(checkInterval);
        }

        if (playerPresentContinuously) {
            Logger.log("Player has been present under us for the full duration. Executing action block.");
            // Perform the desired actions only if the player was continuously present
            performActions();
        } else {
            Logger.log("Player was not present for the full duration. No actions executed.");
        }

        Logger.log("Finished checking for players under us.");
        return false;
    }

    private void performActions() {

        // Logout first
        Logout.logout();
        Condition.wait(() -> Logout.isLoggedOut(), 250, 50);

        Condition.sleep(generateRandomDelay(1000, 2000));

        if (hopEnabled) {
            // Hops are enabled, so we have a profile selected and will use that
            Game.switchWorld(hopProfile);
        } else {
            // Hops are disabled, so we'll use the default profile to hop away from our crasher
            Game.switchWorldNoProfile();
        }

        Condition.sleep(generateRandomDelay(1000, 2000));

        // Log back in
        Login.login();

        // Open up the inventory again
        GameTabs.openInventoryTab();
    }
}