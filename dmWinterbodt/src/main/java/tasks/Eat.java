package tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class Eat extends Task {
    @Override
    public boolean activate() {
        //Logger.debugLog("Inside Eat activate()");
        return shouldEat;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside Eat execute()");
        Paint.setStatus("Eating");

        GameTabs.openInventoryTab();

        if (Inventory.contains(ItemList.REJUVENATION_POTION__1__20702, 0.95)) {
            Inventory.eat(ItemList.REJUVENATION_POTION__1__20702, 0.95);
            lastActivity = System.currentTimeMillis();
            foodAmountInInventory--;
            Condition.sleep(500);
            return true;
        } else if (Inventory.contains(ItemList.REJUVENATION_POTION__2__20701, 0.95)) {
            Inventory.eat(ItemList.REJUVENATION_POTION__2__20701, 0.95);
            lastActivity = System.currentTimeMillis();
            Condition.sleep(500);
            foodAmountInInventory--;
            return true;
        } else if (Inventory.contains(ItemList.REJUVENATION_POTION__3__20700, 0.95)) {
            Inventory.eat(ItemList.REJUVENATION_POTION__3__20700, 0.95);
            lastActivity = System.currentTimeMillis();
            Condition.sleep(500);
            foodAmountInInventory--;
            return true;
        } else if (Inventory.contains(ItemList.REJUVENATION_POTION__4__20699, 0.95)) {
            Inventory.eat(ItemList.REJUVENATION_POTION__4__20699, 0.95);
            lastActivity = System.currentTimeMillis();
            Condition.sleep(500);
            foodAmountInInventory--;
            return true;
        }
    return false;
    }
}
