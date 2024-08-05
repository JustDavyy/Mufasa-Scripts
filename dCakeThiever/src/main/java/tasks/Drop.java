package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static helpers.Interfaces.*;
import static main.dCakeThiever.*;

public class Drop extends Task {
    public static int[] dropItemsList = {
            ItemList.CHOCOLATE_SLICE_1901,
            ItemList.BREAD_2309,
    };

    @Override
    public boolean activate() {
        return Inventory.isFull() && Inventory.containsAny(dropItemsList, 0.9);
    }

    @Override
    public boolean execute() {
        if (bankYN) {
            Logger.log("Dropping choc slices and bread");
        } else {
            Logger.log("Dropping choc slices, bread and cakes");
        }

        List<Integer> itemsToDrop = new ArrayList<>();

        if (!bankYN) {
            // Add items to the list for the non-banking scenario
            itemsToDrop.add(ItemList.CHOCOLATE_SLICE_1901);
            itemsToDrop.add(ItemList.BREAD_2309);
            itemsToDrop.add(ItemList.CAKE_1891);
        } else {
            // Add items to the list for the banking scenario
            for (int id : dropItemsList) {
                itemsToDrop.add(id);
            }
        }

        // Shuffle the list to randomize the order
        Collections.shuffle(itemsToDrop);

        // Tap all items in the randomized order
        for (int id : itemsToDrop) {
            Inventory.tapAllItems(id, 0.9);
        }

        // Additional actions for the banking scenario
        if (bankYN) {
            Condition.sleep(1250);
            usedInvent = Inventory.usedSlots();
            inventUsed = 1337;
        }

        return false;
    }
}
