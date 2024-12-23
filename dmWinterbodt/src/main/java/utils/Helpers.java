package utils;

import helpers.utils.ItemList;

import java.util.Map;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dmWinterbodt.*;

public class Helpers {

    public static void countFoodInInventory() {
        Paint.setStatus("Running food count");
        Logger.debugLog("Running food count.");
        foodAmountInInventory = 0; // Reset before counting

        Map<Integer, Integer> foodIdsWithMultipliers = Map.of(
                ItemList.REJUVENATION_POTION_1_20702, 1,
                ItemList.REJUVENATION_POTION_2_20701, 2,
                ItemList.REJUVENATION_POTION_3_20700, 3,
                ItemList.REJUVENATION_POTION_4_20699, 4
        );
        countItemsInInventory(foodIdsWithMultipliers, 0.95);
    }

    private static void countItemsInInventory(Map<Integer, Integer> foodIdsWithMultipliers, double accuracy) {
        for (Map.Entry<Integer, Integer> entry : foodIdsWithMultipliers.entrySet()) {
            int id = entry.getKey();
            int countMultiplier = entry.getValue();

            int count = Inventory.count(id, accuracy);
            Logger.debugLog("Found " + count + " items with ID " + id + " and multiplier " + countMultiplier);
            foodAmountInInventory += count * countMultiplier;
            Logger.debugLog("Updated food amount in inventory: " + foodAmountInInventory);
        }
    }

    private static int lastCount = 0;
    private static long lastCheckTime = 0;  // To track the time of the last count check
    private static long unchangedStartTime = 0;  // To track the start time when the count was unchanged
    private static final long CHECK_DURATION_MS = 3000;  // Total duration to check (3 seconds)
    private static final long THROTTLE_DURATION_MS = 400;  // Minimum time between checks (400 milliseconds)
    private static final long RESET_THRESHOLD_MS = CHECK_DURATION_MS + 1000;  // Reset after 4 seconds

    /**
     * Checks if the count of an item has remained unchanged over a period of time.
     *
     * @param itemID the ID of the item to count
     * @return true if the count hasn't changed for the duration, false otherwise
     */
    public static boolean countItemUnchanged(int itemID) {
        long currentTime = System.currentTimeMillis();

        // Throttle: If less than THROTTLE_DURATION_MS has passed since the last check, skip the check
        // This is done so we don't absolutely demolishingly spam the inventory count checks.
        if (currentTime - lastCheckTime < THROTTLE_DURATION_MS) {
            return false;
        }

        // Reset if it's been too long since the last check (to avoid using old data)
        if (currentTime - lastCheckTime > RESET_THRESHOLD_MS) {
            lastCount = 0;  // Reset count tracking
            unchangedStartTime = currentTime;  // Reset the unchanged duration tracking
        }

        // Get the current item count
        int currentCount = Inventory.count(itemID, 0.75);

        // First call or reset: initialize lastCount and start tracking the unchanged duration
        if (lastCheckTime == 0 || lastCount != currentCount) {
            lastCount = currentCount;
            unchangedStartTime = currentTime;  // Reset the unchanged start time
        }

        // Update the last check time to the current time
        lastCheckTime = currentTime;

        // Check if the count has remained unchanged for the required duration
        if (currentTime - unchangedStartTime >= CHECK_DURATION_MS) {
            // If count has not changed for the duration, return true
            return true;
        }

        // Otherwise, return false as the count is still being monitored for changes
        return false;
    }
}
