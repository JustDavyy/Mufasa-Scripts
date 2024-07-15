package Tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class PerformCrabbing extends Task {
    public static boolean shouldReset = false;
    public static long startTime = 0;
    private final long resetTime = 10 * 60 * 1000; // 10 minutes

    @Override
    public boolean activate() {
        return Player.tileEquals(currentLocation, spot.getSpotTile());
    }

    @Override
    public boolean execute() {
        if (shouldReset) {
            performReset();
        } else {
            performAFK();
        }
        return false;
    }

    private void performAFK() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        // Check if 15 minutes have passed
        if (System.currentTimeMillis() - startTime >= resetTime) {
            shouldReset = true;
            // Optionally reset startTime if needed for continuous checking
            startTime = System.currentTimeMillis();
        } else {
            Game.antiAFK();
            Condition.sleep(generateRandomDelay(2000, 10000));
            checkIfPeopleUnder();
        }
    }

    private long playerDetectedTime = -1;

    private void checkIfPeopleUnder() {
        if (Game.isPlayersUnderUs()) {
            if (playerDetectedTime == -1) {
                // Record the time when the player is first detected
                playerDetectedTime = System.currentTimeMillis();
            } else {
                // Check if 5 seconds have passed since the first detection
                long currentTime = System.currentTimeMillis();
                if (currentTime - playerDetectedTime >= 5000) {
                    // Perform the action if the player has been under us for 5 seconds
                    performHopAction();
                    // Reset the detection time to prevent repeated actions
                    playerDetectedTime = -1;
                }
            }
        } else {
            // Reset the detection time if no player is detected
            playerDetectedTime = -1;
        }
    }

    private void performHopAction() {
        Walker.walkPath(crabRegion, spot.getResetPath());
        Logout.logout();
        //Game.hop();
        Login.login();
        Walker.walkPath(crabRegion, getReversedTiles(spot.getResetPath()));
    }

    private void performReset() {
        startTime = 0; // Reset the start time

        Walker.walkPath(crabRegion, spot.getResetPath());

        Walker.walkPath(crabRegion, getReversedTiles(spot.getResetPath()));
        Walker.step(spot.getSpotTile(), crabRegion);

        shouldReset = false;
    }
}
