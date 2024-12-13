package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static helpers.Interfaces.*;
import static main.dTeaYoinker.*;

public class Drop extends Task {
    public static int[] dropItemsList = {
            ItemList.CHOCOLATE_SLICE_1901,
            ItemList.BREAD_2309,
    };

    @Override
    public boolean activate() {
        if (bankYN) {
            return false;
        }

        return Inventory.isFull();
    }

    @Override
    public boolean execute() {
        if (!Game.isTapToDropEnabled()) {
            Logger.debugLog("Enable tap to drop.");
            Game.enableTapToDrop();
            Condition.sleep(generateRandomDelay(800, 1200));
        }

        if (Game.isTapToDropEnabled()) {
            Logger.log("Dropping all cup of teas.");
            Inventory.tapAllItems(ItemList.CUP_OF_TEA_712, 0.8);
        }

        return false;
    }
}
