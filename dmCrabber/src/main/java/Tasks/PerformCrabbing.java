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
        }
    }

    private void performReset() {
        startTime = 0; // Reset the start time

        Walker.walkPath(crabRegion, spot.getResetPath());

        Walker.walkPath(crabRegion, getReversedTiles(spot.getResetPath()));
        Walker.step(spot.getSpotTile(), crabRegion);

        shouldReset = false;
    }
}
