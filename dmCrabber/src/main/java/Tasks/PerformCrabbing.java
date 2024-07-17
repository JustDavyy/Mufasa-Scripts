package Tasks;

import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class PerformCrabbing extends Task {
    public static boolean shouldReset = false;
    public static long startTime = 0;
    private long lastHitTime = 0;
    private final long resetTime = 10 * 60 * 1000; // 10 minutes
    private final long noHitDuration = 15000; // 5 seconds in milliseconds
    private long playerDetectedTime = 0;

    private final Rectangle playerRect = new Rectangle(429, 231, 49, 63);
    private final Color redHit = Color.decode("#ba0000");
    private final Color blueHit = Color.decode("#4040ff");
    private final Color hitbarColor = Color.decode("#00ff00");

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
        boolean redHitHappened = Client.isColorInRect(redHit, playerRect, 5);
        boolean blueHitHappened = Client.isColorInRect(blueHit, playerRect, 5);
        boolean hpBarVisible = Client.isColorInRect(hitbarColor, playerRect, 5);

        long currentTime = System.currentTimeMillis();

        // Initialize startTime if not already set
        if (startTime == 0) {
            startTime = currentTime;
        }

        // If neither redHitHappened, blueHitHappened, nor hpBarVisible is true, track the time
        if (!redHitHappened && !blueHitHappened && !hpBarVisible) {
            if (lastHitTime == 0) {
                lastHitTime = currentTime;
            } else if (currentTime - lastHitTime >= noHitDuration) {
                shouldReset = true;
            }
        } else {
            // Reset the last hit time when a hit happens
            lastHitTime = 0;
        }

        // Check if 10 minutes have passed
        if (currentTime - startTime >= resetTime) {
            shouldReset = true;
        } else if (!shouldReset) {
            Game.antiAFK();
            Condition.sleep(generateRandomDelay(2000, 10000));
            checkForPlayers();
        }
    }

    private void checkForPlayers() {
        if (Game.isPlayersUnderUs()) {
            if (playerDetectedTime == 0) {
                // Record the time when the player is first detected
                playerDetectedTime = System.currentTimeMillis();
            } else {
                // Check if 5 seconds have passed since the first detection
                long currentTime = System.currentTimeMillis();
                if (currentTime - playerDetectedTime >= 10000) {
                    // Perform the action if the player has been under us for 10 seconds
                    performHopAction();
                    // Reset the detection time to prevent repeated actions
                    playerDetectedTime = 0;
                }
            }
        } else {
            // Reset the detection time if no player is detected
            playerDetectedTime = 0;
        }
    }

    private void performHopAction() {
        Walker.walkPath(crabRegion, spot.getResetPath());
        Logout.logout();
        Game.switchWorldNoProfile();
        Login.login();
        Walker.walkPath(crabRegion, getReversedTiles(spot.getResetPath()));
        Condition.sleep(generateRandomDelay(1500, 2250));
        Walker.step(spot.getSpotTile(), crabRegion);
        Condition.sleep(generateRandomDelay(1500, 2250));
        currentLocation = Walker.getPlayerPosition(crabRegion);
    }

    private void performReset() {
        startTime = 0; // Reset the start time
        lastHitTime = 0;

        Walker.walkPath(crabRegion, spot.getResetPath());

        Walker.walkPath(crabRegion, getReversedTiles(spot.getResetPath()));
        Condition.sleep(generateRandomDelay(1500, 2250));
        Walker.step(spot.getSpotTile(), crabRegion);
        Condition.sleep(generateRandomDelay(1500, 2250));
        currentLocation = Walker.getPlayerPosition(crabRegion);

        shouldReset = false;
    }
}
