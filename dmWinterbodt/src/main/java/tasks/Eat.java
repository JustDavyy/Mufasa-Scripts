package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import java.util.Map;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class Eat extends Task {
    @Override
    public boolean activate() {
        //Logger.debugLog("Inside Eat activate()");
        return shouldEat;
    }

    // Map food types to their IDs and tolerance levels
    Map<String, int[]> foodMap = Map.of(
            "Rejuv Potion", new int[] {
                    ItemList.REJUVENATION_POTION_1_20702,
                    ItemList.REJUVENATION_POTION_2_20701,
                    ItemList.REJUVENATION_POTION_3_20700,
                    ItemList.REJUVENATION_POTION_4_20699
            }
    );

    // Map for tolerance values

    @Override
    public boolean execute() {
        Logger.debugLog("Inside Eat execute()");
        Paint.setStatus("Eating");

        GameTabs.openTab(UITabs.INVENTORY);

        // Check if selected food is in the map
        if (foodMap.containsKey("Rejuv Potion")) {
            int[] foodIds = foodMap.get("Rejuv Potion");

            // Try to consume one of the food items
            return consumeFood(foodIds, 0.95);
        }
        return false;
    }

    // Helper method to check and eat food from the inventory
    private boolean consumeFood(int[] foodIds, double tolerance) {
        for (int foodId : foodIds) {
            if (Inventory.contains(foodId, tolerance)) {
                Inventory.eat(foodId, tolerance);
                lastActivity = System.currentTimeMillis();
                foodAmountInInventory--;
                Condition.sleep(generateRandomDelay(800, 1100));
                return true;
            }
        }
        return false;
    }
}
