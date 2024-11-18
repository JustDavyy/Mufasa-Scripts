package tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dCamTorumMiner.*;

public class Drop extends Task {

    @Override
    public boolean activate() {
        return Inventory.emptySlots() <= 2;
    }

    @Override
    public boolean execute() {

        if (!Game.isTapToDropEnabled()) {
            Game.enableTapToDrop();
            Condition.sleep(500);
        }

        Inventory.tapAllItems(ItemList.CALCIFIED_DEPOSIT_29088, 0.8);

        // Uncuts
        if (Inventory.contains(ItemList.UNCUT_SAPPHIRE_1623, 0.8)) {
            Inventory.tapAllItems(ItemList.UNCUT_SAPPHIRE_1623, 0.8);
        }
        if (Inventory.contains(ItemList.UNCUT_EMERALD_1621, 0.8)) {
            Inventory.tapAllItems(ItemList.UNCUT_EMERALD_1621, 0.8);
        }
        if (Inventory.contains(ItemList.UNCUT_RUBY_1619, 0.8)) {
            Inventory.tapAllItems(ItemList.UNCUT_RUBY_1619, 0.8);
        }
        if (Inventory.contains(ItemList.UNCUT_DIAMOND_1617, 0.8)) {
            Inventory.tapAllItems(ItemList.UNCUT_DIAMOND_1617, 0.8);
        }

        lastAction = System.currentTimeMillis();

        return false;
    }
}
