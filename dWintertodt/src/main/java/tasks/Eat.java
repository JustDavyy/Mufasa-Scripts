package tasks;

import utils.Task;

import java.util.Objects;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class Eat extends Task {
    @Override
    public boolean activate() {
        Logger.debugLog("Inside Eat activate()");
        return hpToEat > Player.getHP();
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside Eat execute()");
        if (java.util.Objects.equals(selectedFood, "Cakes")) {
            // cake IDs; 1891, 1893, 1895
            if (Inventory.contains(1895, 0.60)) { // slice of cake
                Inventory.eat(1895, 0.60);
                main.dWintertodt.foodAmountInInventory--;
                Condition.wait(() -> hpToEat < Player.getHP(), 200, 20);
                return true;
            } else if (Inventory.contains(1893, 0.60)) { // 2/3 cake
                Inventory.eat(1893, 0.60);
                main.dWintertodt.foodAmountInInventory--;
                Condition.wait(() -> hpToEat < Player.getHP(), 200, 20);
                return true;
            } else if (Inventory.contains(1891, 0.60)) { // full cake
                Inventory.eat(1891, 0.60);
                main.dWintertodt.foodAmountInInventory--;
                Condition.wait(() -> hpToEat < Player.getHP(), 200, 20);
                return true;
            }
            return false;
        } else {
            Inventory.eat(foodID, 0.60);
            main.dWintertodt.foodAmountInInventory--;
            Condition.wait(() -> hpToEat < Player.getHP(), 200, 20);
            return true;
        }
    }

}
