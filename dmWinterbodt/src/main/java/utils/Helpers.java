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
}
