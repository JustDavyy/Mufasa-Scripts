package utils;

import helpers.utils.ItemList;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dmWinterbodt.foodAmountInInventory;

public class Helpers {
    public static void countFoodInInventory() {
        Paint.setStatus("Running food count");
        Logger.debugLog("Running food count.");
        foodAmountInInventory = 0; // Reset before counting

        int[] foodIds = {ItemList.REJUVENATION_POTION__1__20702, ItemList.REJUVENATION_POTION__2__20701, ItemList.REJUVENATION_POTION__3__20700, ItemList.REJUVENATION_POTION__4__20699};
        for (int id : foodIds) {
            int countMultiplier = 1; // Default count multiplier
            if (id == ItemList.REJUVENATION_POTION__2__20701) {
                countMultiplier = 2;
            } else if (id == ItemList.REJUVENATION_POTION__3__20700) {
                countMultiplier = 3;
            } else if (id == ItemList.REJUVENATION_POTION__4__20699) {
                countMultiplier = 4;
            }

            int count = Inventory.count(id, 0.95);
            Logger.debugLog("Found " + count + " items with ID " + id + " and multiplier " + countMultiplier);
            foodAmountInInventory += count * countMultiplier;
            Logger.log("Updated food amount in inventory: " + foodAmountInInventory);
        }
    }

    private static int lastCount = 0;
    private static long lastCheckTime = 0;  // To track the time of the last count check
    private static long unchangedStartTime = 0;  // To track the start time when the count was unchanged
    private static final long CHECK_DURATION_MS = 5000;  // Total duration to check (5 seconds)

    /**
     * Checks if the count of an item has remained unchanged over a period of time.
     *
     * @param itemID the ID of the item to count
     * @return true if the count hasn't changed for the duration, false otherwise
     */
    public static boolean countItemUnchanged(int itemID) {
        long currentTime = System.currentTimeMillis();

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
