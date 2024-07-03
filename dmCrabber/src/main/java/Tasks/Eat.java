package Tasks;

import utils.Task;

import java.util.Arrays;
import java.util.List;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class Eat extends Task {
    private final List<Integer> cakeIds = Arrays.asList(1895, 1893, 1891);

    public boolean activate() {
        currentHP = Player.getHP();
        return (currentHP <= hpToEat && currentHP != -1);
    }

    @Override
    public boolean execute() {
        Logger.log("Below HP threshold, eating food.");
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 100, 10);
        }

        Logger.debugLog("Eating food til above threshold.");
        if (selectedFood.equals("Cake")) {
            boolean foodEaten = false;
            for (int cakeId : cakeIds) {
                if (Inventory.contains(cakeId, 0.8)) {
                    eat(cakeId);
                    foodEaten = true;
                    break;
                }
            }
            if (!foodEaten) {
                Logger.log("No more food, walking to bank.");
                Walker.walkPath(spot.getPathToBank());
                return true;
            }
        } else {
            if (Inventory.count(foodID, 0.8) > 0) {
                eat(foodID);
            } else {
                Logger.log("No more food, walking to bank.");
                Walker.walkPath(spot.getPathToBank());
                return true;
            }
        }


        GameTabs.closeInventoryTab();
        Logger.log("Done eating food.");
        return true;
    }

    public void eat(int food){
        Logger.debugLog("Eating food now.");
        Inventory.eat(food, 0.8);
        Condition.sleep(generateRandomDelay(2750, 3500));
        currentHP = Player.getHP();
    }
}
