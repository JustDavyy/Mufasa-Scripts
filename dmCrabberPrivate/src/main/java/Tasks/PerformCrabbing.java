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
    private final long noHitDuration = 40000; // 40 seconds in milliseconds as respawn time is 30 seconds
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
                Logger.debugLog("We should reset, no hits detected in 40 seconds.");
                shouldReset = true;
            }
        } else {
            // Reset the last hit time when a hit happens
            lastHitTime = 0;
        }

        // Check if 10 minutes have passed
        if (currentTime - startTime >= resetTime) {
            shouldReset = true;
            Logger.debugLog("We should reset shortly, 10 minutes have passed (aggro timer).");
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
                Logger.debugLog("Player detected, running further checks.");
            } else {
                // Check if 10 seconds have passed since the first detection
                long currentTime = System.currentTimeMillis();
                if (currentTime - playerDetectedTime >= 10000) {
                    Logger.debugLog("Player is still present after 10 seconds, hopping.");
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
        Walker.webWalk(spot.getResetSpot());
        Condition.sleep(generateRandomDelay(4000, 7000));
        Logout.logout();
        Game.switchWorld();
        Login.login();
        Walker.webWalk(spot.getSpotTile(), true);
        Condition.sleep(generateRandomDelay(1500, 2250));
        Walker.step(spot.getSpotTile());
        Condition.sleep(generateRandomDelay(1500, 2250));
        currentLocation = Walker.getPlayerPosition();
    }

    private void performReset() {
        Logger.debugLog("Resetting.");

        Walker.webWalk(spot.getResetSpot());
        Walker.webWalk(spot.getSpotTile(), true);
        Condition.sleep(generateRandomDelay(1500, 2250));
        Walker.step(spot.getSpotTile());
        Condition.sleep(generateRandomDelay(1500, 2250));
        currentLocation = Walker.getPlayerPosition();

        startTime = 0;
        lastHitTime = 0;

        shouldReset = false;
    }
}
