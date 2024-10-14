package Tasks;

import utils.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class BreakManager extends Task {
    private final Random random = new Random();
    private long lastBreakTime;
    private long breakAfterMillis = 0;

    @Override
    public boolean activate() {
        if (breakAfterMillis == 0) { // Initialize it
            breakAfterMillis = generateRandomBreak(lowerBreak, higherBreak);
            lastBreakTime = System.currentTimeMillis();

            // Calculate and print the time till next break and the exact break time
            long currentTimeMillis = System.currentTimeMillis();
            long nextBreakTimeMillis = currentTimeMillis + breakAfterMillis;
            Date nextBreakTime = new Date(nextBreakTimeMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            Logger.debugLog("Time till next break: " + breakAfterMillis / 60000 + " minutes and " + (breakAfterMillis % 60000) / 1000 + " seconds.");
            Logger.debugLog("Next break will be at: " + sdf.format(nextBreakTime));
        }

        return System.currentTimeMillis() - lastBreakTime >= breakAfterMillis
                && (Player.tileEquals(currentLocation, spot.getSpotTile())
                || Player.isTileWithinArea(currentLocation, bankArea));
    }

    @Override
    public boolean execute() {
        if (Player.isTileWithinArea(currentLocation, bankArea)) {
            takeBreak();
        } else {
            Logger.log("Moving to safe location for breaks!");
            moveToSafeLocation();
            takeBreak();
            moveBackToSpot();
        }
        return true;
    }

    private void takeBreak() {
        int breakMillis = generateRandomBreakTime();
        Logger.log("Taking a break for " + breakMillis / 60000 + " minute(s) and " + (breakMillis % 60000) / 1000 + " second(s).");
        Logout.logout();
        Condition.sleep(breakMillis);
        lastBreakTime = System.currentTimeMillis();
        Logger.log("Break over, resuming script.");
        Login.login();
        breakAfterMillis = 0;
    }

    private void moveToSafeLocation() {
        Walker.walkPath(spot.getResetPath());
        Condition.sleep(generateRandomDelay(1500, 2250));
        currentLocation = Walker.getPlayerPosition();
    }

    private void moveBackToSpot() {
        Condition.sleep(generateRandomDelay(1500, 2250));
        Walker.walkPath(getReversedTiles(spot.getResetPath()));
        Condition.sleep(generateRandomDelay(1500, 2250));
        Walker.step(spot.getSpotTile());
        Condition.sleep(generateRandomDelay(1500, 2250));
        currentLocation = Walker.getPlayerPosition();
    }

    private int generateRandomBreakTime() {
        int breakMinutes = random.nextInt(6) + 1;
        int breakSeconds = random.nextInt(60);
        return breakMinutes * 60000 + breakSeconds * 1000;
    }

    private long generateRandomBreak(int lowerMinutes, int higherMinutes) {
        int higherSeconds = 59;

        // Ensure lower and higher bounds are correct
        if (lowerMinutes > higherMinutes) {
            int temp = lowerMinutes;
            lowerMinutes = higherMinutes;
            higherMinutes = temp;
        }

        // Convert minutes and seconds to milliseconds
        long lowerMillis = lowerMinutes * 60000L; // Changed to long to prevent overflow
        long higherMillis = higherMinutes * 60000L + higherSeconds * 1000L; // Changed to long to prevent overflow

        // Generate a random delay in milliseconds
        return lowerMillis + (long)(random.nextDouble() * (higherMillis - lowerMillis + 1));
    }
}
