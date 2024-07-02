package Tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class Eat extends Task {
    public boolean activate() {
        currentHP = Player.getHP();
        return (currentHP <= hpToEat && currentHP != -1);
    }

    @Override
    public boolean execute() {
        Logger.log("Below HP threshold, eating food.");
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.sleep(generateRandomDelay(1250,1750));
        }

        Logger.debugLog("Eating food til above threshold.");
        if (selectedFood.equals("Cake")) {
            if (Inventory.contains("1895", 0.8)) { // 1/3 cake
                eat(1895);
            }
            else if (Inventory.contains("1893", 0.8)) { // 2/3 cake
                eat(1893);
            }
            else if (Inventory.contains("1891", 0.8)) { // 3/3 cake
                eat(1891);
            }
            else {
                Logger.log("No more food, stopping script.");
                Logout.logout();
                Script.stop();
            }
        } else {
            if (Inventory.count(foodID, 0.8) > 0) {
                eat(foodID);
            } else {
                Logger.log("No more food, stopping script.");

                // Add logic here to walk away or something first?

                //Logout.logout();
                //Script.stop();
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
