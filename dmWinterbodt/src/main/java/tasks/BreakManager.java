package tasks;

import utils.StateUpdater;
import utils.Task;

import java.util.Random;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class BreakManager extends Task {
    public static int currentGameCount = 0;
    public static int shouldBreakAt = 0;
    public static boolean shouldBreakNow = false;
    private static final Random random = new Random(System.nanoTime());

    private static void generateRandomNextBreakCount() {
        shouldBreakAt = random.nextInt(4) + 2;  // Generates a number from 2 to 5
    }

    @Override
    public boolean activate() {
        if (shouldBreakAt == 0) {
            generateRandomNextBreakCount();
        }

        StateUpdater.updateIsGameGoing();

        if (currentGameCount >= shouldBreakAt && Player.isTileWithinArea(currentLocation, lobby) && !isGameGoing || currentGameCount > shouldBreakAt && Player.isTileWithinArea(currentLocation, outsideArea) && !Bank.isOpen()) {
            shouldBreakNow = true;
        }

        return shouldBreakNow;
    }

    @Override
    public boolean execute() {
        int breakMinutes = random.nextInt(4) + 1;  // Generates a number from 1 to 4
        int breakSeconds = random.nextInt(60);     // Use the same random instance here as well
        int breakMillis = breakMinutes * 60000 + breakSeconds * 1000;  // Convert minutes and seconds to milliseconds

        // Add a delay just to be safe, only when inside.
        if (Player.isTileWithinArea(currentLocation, insideArea)){
            Condition.sleep(generateRandomDelay(4000, 8000));
        }

        // Log the precise break duration
        Logger.log("Taking a break for " + breakMinutes + " minute(s) and " + breakSeconds + " second(s).");

        // Breaking logic
        Logout.logout();
        Condition.sleep(breakMillis);  // Sleep for the calculated duration

        Logger.log("Break over, resuming script.");
        Login.login();

        currentGameCount = 0;  // Resetting the last break time after the break
        generateRandomNextBreakCount();
        shouldBreakNow = false;
        return true;
    }
}
