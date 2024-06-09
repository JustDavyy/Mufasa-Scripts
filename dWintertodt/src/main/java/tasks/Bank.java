package tasks;

import helpers.utils.ItemList;
import utils.Constants;
import utils.Task;

import java.util.Objects;

import static helpers.Interfaces.Inventory;
import static main.dWintertodt.*;

public class Bank extends Task {
    private boolean checkFood = true;

    @Override
    public boolean activate() {
        countFoodInInventory(); //Gotta do this after a bank run.

        return Constants.foodAmountInInventory > foodAmountLeftToBank || (Inventory.emptySlots() > 8 && Inventory.contains(ItemList.SUPPLY_CRATE_20703, 0.60));
    }

    @Override
    public boolean execute() {
        return false;
    }

    // Method to count total food items in the inventory
    public void countFoodInInventory() {
        if (checkFood) {
            Constants.foodAmountInInventory = 0; // Reset before counting

            if (Objects.equals(selectedFood, "Cakes")) {
                int[] foodIds = {1891, 1893, 1895};
                for (int id : foodIds) {
                    int countMultiplier = 1; // Default count multiplier
                    if (id == 1891) {
                        countMultiplier = 3; // A full cake counts as 3
                    } else if (id == 1893) {
                        countMultiplier = 2; // half cake counts as 2
                    }

                    // Assume Inventory.count(id, 0.60) returns the number of items that are at least 60% intact
                    Constants.foodAmountInInventory += Inventory.count(id, 0.60) * countMultiplier;
                    checkFood = false;
                }
            } else {
                Constants.foodAmountInInventory = Inventory.count(foodID, 0.60);
                checkFood = false;
            }
        }
    }
}
