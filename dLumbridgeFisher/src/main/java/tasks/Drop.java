package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dLumbridgeFisher.*;

public class Drop extends Task {

    @Override
    public boolean activate() {
        return !cookEnabled && Inventory.isFull();
    }

    @Override
    public boolean execute() {

        shrimpGainedCount = shrimpGainedCount + Inventory.count(ItemList.RAW_SHRIMPS_317, 0.7, Color.decode("#957e72"));
        anchoviesGainedCount = anchoviesGainedCount + Inventory.count(ItemList.RAW_ANCHOVIES_321, 0.7, Color.decode("#54546d"));

        Paint.setStatus("Start Drop actions");
        Logger.log("Start Drop actions.");

        if (!Game.isTapToDropEnabled()) {
            Game.enableTapToDrop();
        }

        Inventory.tapAllItems(ItemList.RAW_SHRIMPS_317, 0.7);
        Inventory.tapAllItems(ItemList.RAW_ANCHOVIES_321, 0.7);
        Condition.sleep(500, 800);

        return false;
    }
}