package tasks;

import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class Eat extends Task {
    @Override
    public boolean activate() {
        //Logger.debugLog("Inside Eat activate()");
        currentHp = Player.getHP();
        return hpToEat > currentHp;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside Eat execute()");

        GameTabs.openInventoryTab();

        if (java.util.Objects.equals(selectedFood, "Cakes")) {
            // cake IDs; 1891, 1893, 1895
            if (Inventory.contains(1895, 0.75)) { // slice of cake
                Inventory.eat(1895, 0.75);
                foodAmountInInventory--;
                Condition.wait(() -> Player.getHP() > hpToEat, 200, 20);
                return true;
            } else if (Inventory.contains(1893, 0.75)) { // 2/3 cake
                Inventory.eat(1893, 0.75);
                foodAmountInInventory--;
                Condition.wait(() -> Player.getHP() > hpToEat, 200, 20);
                return true;
            } else if (Inventory.contains(1891, 0.75)) { // full cake
                Inventory.eat(1891, 0.75);
                foodAmountInInventory--;
                Condition.wait(() -> Player.getHP() > hpToEat, 200, 20);
                return true;
            }
            return false;
        } else {
            Inventory.eat(foodID, 0.75);
            foodAmountInInventory--;
            Condition.wait(() -> Player.getHP() > hpToEat, 200, 20);
            return true;
        }
    }

}
