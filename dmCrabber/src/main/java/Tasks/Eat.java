package Tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class Eat extends Task {
    public boolean activate() {
        return hpToEat > Player.getHP();
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside Eat execute()");

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        if (java.util.Objects.equals(selectedFood, "Cakes")) {
            // cake IDs; 1891, 1893, 1895
            if (Inventory.contains(1895, 0.75)) { // slice of cake
                Inventory.eat(1895, 0.75);
                Condition.wait(() -> Player.getHP() > hpToEat, 200, 20);
                return true;
            } else if (Inventory.contains(1893, 0.75)) { // 2/3 cake
                Inventory.eat(1893, 0.75);
                Condition.wait(() -> Player.getHP() > hpToEat, 200, 20);
                return true;
            } else if (Inventory.contains(1891, 0.75)) { // full cake
                Inventory.eat(1891, 0.75);
                Condition.wait(() -> Player.getHP() > hpToEat, 200, 20);
                return true;
            }
            return false;
        } else {
            Inventory.eat(foodID, 0.75);
            Condition.wait(() -> Player.getHP() > hpToEat, 200, 20);
            return true;
        }
    }
}
