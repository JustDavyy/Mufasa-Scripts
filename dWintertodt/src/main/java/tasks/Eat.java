package tasks;

import utils.Constants;
import utils.Task;

import java.util.Objects;

import static helpers.Interfaces.Inventory;
import static helpers.Interfaces.Player;
import static main.dWintertodt.*;

public class Eat extends Task {
    @Override
    public boolean activate() {
        return hpToEat < Player.getHP();
    }

    @Override
    public boolean execute() {
        if (Objects.equals(selectedFood, "Cakes")) {
            // cake IDs; 1891, 1893, 1895
            if (Inventory.contains(1895, 0.60)) { // slice of cake
                Inventory.eat(1895, 0.60);
                Constants.foodAmountInInventory--;
                return true;
            } else if (Inventory.contains(1893, 0.60)) { // 2/3 cake
                Inventory.eat(1893, 0.60);
                Constants.foodAmountInInventory--;
                return true;
            } else if (Inventory.contains(1891, 0.60)) { // full cake
                Inventory.eat(1891, 0.60);
                Constants.foodAmountInInventory--;
                return true;
            }
            return false;
        } else {
            Inventory.eat(foodID, 0.60);
            Constants.foodAmountInInventory--;
            return true;
        }
    }
}
