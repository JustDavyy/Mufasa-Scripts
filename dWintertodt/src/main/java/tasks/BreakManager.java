package tasks;

import utils.Task;

import java.util.Random;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class BreakManager extends Task {
    private static Random random = new Random();
    public static int currentGameCount = 0;
    public static int shouldBreakAt = 0;
    public static boolean shouldBreakNow = false;

    @Override
    public boolean activate() {
        if (shouldBreakAt == 0) {
            generateRandomNextBreakCount();
        }

        if (currentGameCount > shouldBreakAt && Player.isTileWithinArea(currentLocation, lobby) || currentGameCount > shouldBreakAt && Player.isTileWithinArea(currentLocation, outsideArea) && !Bank.isOpen()) {
            shouldBreakNow = true;
        }

        return shouldBreakNow;
    }

    @Override
    //I took the execute from ur nmz ;)
    public boolean execute() {
        // Generate random break time between 1 and 6 minutes
        int breakMinutes = new Random().nextInt(6) + 4;  // Generates a number from 4 to 9

        // Randomize the seconds from 0 to 59
        int breakSeconds = new Random().nextInt(60);
        int breakMillis = breakMinutes * 60000 + breakSeconds * 1000;  // Convert minutes and seconds to milliseconds

        // Log the precise break duration
        Logger.log("Taking a break for " + breakMinutes + " minute(s) and " + breakSeconds + " second(s).");

        // Breaking logic
        Logout.logout();
        Condition.sleep(breakMillis);  // Sleep for the calculated duration

        currentGameCount = 0;  // Resetting the last break time after the break
        generateRandomNextBreakCount();

        Logger.log("Break over, resuming script.");
        Login.login();

        shouldBreakNow = false;
        return true;
    }

    private static void generateRandomNextBreakCount() {
        // nextInt(5) generates a value from 0 to 4, so adding 3 results in a range from 3 to 7
        shouldBreakAt = random.nextInt(5) + 3;
    }
}
