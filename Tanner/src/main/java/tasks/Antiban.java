package tasks;

import utils.Task;
import java.util.Random;
import static helpers.Interfaces.*;


public class Antiban extends Task {
    private final RandomTimer randomTimer = new RandomTimer();

    @Override
    public boolean activate() {
        // Antiban should not activate if the bank is open
        if (Bank.isOpen()) {
            return false;
        }
        Logger.log("Antiban: checking..");
        // Only activate when the random timer triggers
        return randomTimer.shouldTrigger();
    }

    @Override
    public boolean execute() {
        // Logic to execute when the timer triggers
        Logger.log("Antiban - Triggered");
    
        Random random = new Random();
        int randomSleepTime = 3000 + random.nextInt(5000); // Random time between 3000 and 8000 ms
    
        // Sleep for a random time or perform tab swapping
        if (random.nextBoolean()) {
            // Sleep for a random time
            Condition.sleep(randomSleepTime);
            Logger.log("Antiban: Slept for " + randomSleepTime + " ms.");
        } else {
            // Perform tab swapping
            Tabswapping();
        }
    
        return true; // Task executed successfully
    }









    public static boolean Tabswapping() {
        Random random = new Random();
        int randomSleepTime = 1000 + random.nextInt(4000);
        if (new Random().nextBoolean()) {
            // Open inventory tab
            if (!GameTabs.isInventoryTabOpen()) {
                Logger.log("Antiban: Opening Inventory");
                GameTabs.openInventoryTab();
                Condition.sleep(randomSleepTime);
            }
        } else {
            // Open stats tab
            if (!GameTabs.isStatsTabOpen()) {
                Logger.log("Antiban: Opening Stats tab");
                GameTabs.openStatsTab();
                Condition.sleep(randomSleepTime);
            }
        }
        // Return true if the task was performed successfully
        return true;
    }

    // RandomTimer inner class
    private static class RandomTimer {
        private long nextTriggerTime;

        public RandomTimer() {
            setNextTriggerTime();
        }

        // Sets the next random trigger time between 5-10 minutes
        private void setNextTriggerTime() {
            Random random = new Random();
            long randomInterval = (5 + random.nextInt(6)) * 60 * 1000L; // 5 to 10 minutes in ms
            nextTriggerTime = System.currentTimeMillis() + randomInterval;
        }

        // Checks if the random timer should trigger
        public boolean shouldTrigger() {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextTriggerTime) {
                setNextTriggerTime();
                return true;
            }
            return false;
        }
    }
}
