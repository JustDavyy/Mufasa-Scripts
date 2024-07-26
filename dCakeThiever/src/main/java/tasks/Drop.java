package tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dCakeThiever.*;

public class Drop extends Task {
    public static int[] dropItemsList = {
            ItemList.CHOCOLATE_SLICE_1901,
            ItemList.BREAD_2309
    };

    @Override
    public boolean activate() {
        return Inventory.isFull() && Inventory.containsAny(dropItemsList, 0.80);
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Dropping items..");

        for (int id : dropItemsList) {
            Inventory.tapAllItems(id, 0.80);
        }
        Condition.sleep(1250);

        usedInvent = Inventory.usedSlots();
        inventUsed = 1337;
        return true;
    }
}
