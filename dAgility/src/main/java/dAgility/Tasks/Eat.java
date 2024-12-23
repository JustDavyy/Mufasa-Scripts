package dAgility.Tasks;

import dAgility.utils.Task;
import helpers.utils.UITabs;

import static helpers.Interfaces.*;
import static dAgility.dAgility.*;

public class Eat extends Task {

    public Eat(){
        super();
        super.name = "Eat";
    }
    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        currentHP = Player.getHP();
        return (currentHP <= eatHP && currentHP != -1);
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        Paint.setStatus("Eating");
        currentHP = Player.getHP();
        Logger.debugLog("HP: " + currentHP);
        if (foodID.equals("None")) {
            Logger.log("Below HP threshold, no food chosen. Stopping script...");
            Logout.logout();
            Script.stop();
        }

        else {
            Logger.log("Below HP threshold, eating food.");
            if (!GameTabs.isTabOpen(UITabs.INVENTORY)) {
                GameTabs.openTab(UITabs.INVENTORY);
                Condition.sleep(1500);
            }
            Logger.debugLog("Eating food til above threshold.");
            if (foodID.equals("Cake")) {
                if (Inventory.contains("1895", 0.8)) { // 1/3 cake
                    eat("1895");
                }
                else if (Inventory.contains("1893", 0.8)) { // 2/3 cake
                    eat("1893");
                }
                else if (Inventory.contains("1891", 0.8)) { // 3/3 cake
                    eat("1891");
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
                    Logout.logout();
                    Script.stop();
                }
            }

        GameTabs.closeTab(UITabs.INVENTORY);
        Logger.log("Done eating food.");
        return true;
        }
        return false;
    }
    public void eat(String food){
        Logger.debugLog("Eating food now.");
        Inventory.eat(food, 0.8);
        if (foodID.equals("1993")) {
            Logger.debugLog("Using wine, we need to drop the jug.");
            if (!Game.isTapToDropEnabled()) {
                Game.enableTapToDrop();
                Condition.sleep(generateRandomDelay(750, 1000));
            }
            Inventory.tapItem(food, 0.8);
            Game.disableTapToDrop();
            Condition.sleep(generateRandomDelay(250, 500));
            Game.closeHotkeymenu();
            Condition.sleep(generateRandomDelay(250, 500));
        } else {
            Condition.sleep(generateRandomDelay(2750, 3250));
        }
        currentHP = Player.getHP();
        GameTabs.closeTab(UITabs.INVENTORY);
        Condition.wait(() -> !GameTabs.isTabOpen(UITabs.INVENTORY), 250, 20);
        if (GameTabs.isTabOpen(UITabs.INVENTORY)) {
            GameTabs.closeTab(UITabs.INVENTORY);
        }
    }
}
