package Tasks;

import utils.Task;

import java.util.Random;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class BreakManager extends Task {
    private final Random random = new Random();
    private long lastBreakTime;
    int breakAfterMinutes = 0;

    @Override
    public boolean activate() {
        if (breakAfterMinutes == 0) { // Initialize it
            breakAfterMinutes = generateDelay(lowerBreak, higherBreak) * 60000; // Convert minutes to milliseconds
            lastBreakTime = System.currentTimeMillis();
        }

        return System.currentTimeMillis() - lastBreakTime >= breakAfterMinutes && (Player.tileEquals(currentLocation, spot.getSpotTile()) || Player.isTileWithinArea(currentLocation, bankArea));
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
    }

    private void moveToSafeLocation() {
        Walker.walkPath(crabRegion, spot.getResetPath());
        Condition.sleep(generateRandomDelay(1500, 2250));
    }

    private void moveBackToSpot() {
        Condition.sleep(generateRandomDelay(1500, 2250));
        Walker.walkPath(crabRegion, getReversedTiles(spot.getResetPath()));
        Condition.sleep(generateRandomDelay(1500, 2250));
        Walker.step(spot.getSpotTile(), crabRegion);
        Condition.sleep(generateRandomDelay(1500, 2250));
    }

    private int generateRandomBreakTime() {
        int breakMinutes = random.nextInt(6) + 1;
        int breakSeconds = random.nextInt(60);
        return breakMinutes * 60000 + breakSeconds * 1000;
    }

    private int generateDelay(int lowerEnd, int higherEnd) {
        if (lowerEnd > higherEnd) {
            // Swap lowerEnd and higherEnd if lowerEnd is greater
            int temp = lowerEnd;
            lowerEnd = higherEnd;
            higherEnd = temp;
        }
        return random.nextInt(higherEnd - lowerEnd + 1) + lowerEnd;
    }
}
