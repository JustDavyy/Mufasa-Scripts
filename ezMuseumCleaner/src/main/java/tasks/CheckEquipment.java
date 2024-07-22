package tasks;


import helpers.utils.EquipmentSlot;
import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.toolPositionList;

public class CheckEquipment extends Task {
    int[] museumToolIDs = {
            ItemList.TROWEL_676,
            ItemList.ROCK_PICK_675,
            ItemList.SPECIMEN_BRUSH_670
    };

    private boolean checkedEquipment = false;

    @Override
    public boolean activate() {
        return !checkedEquipment;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Checking equipment");
        Logger.log("Checking for tools");

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(GameTabs::isInventoryTabOpen, 50, 10);
        }

        for (int item : museumToolIDs) {
            if (Inventory.contains(item, 0.80)) {
                toolPositionList.add(Inventory.itemSlotPosition(item, 0.80));
            } else {
                Logger.log("missing item: " + item);
                Script.stop();
            }
        }

        if (!GameTabs.isEquipTabOpen()) {
            GameTabs.openEquipTab();
            Condition.wait(GameTabs::isEquipTabOpen, 50, 10);
        }

        if (!Equipment.itemAt(EquipmentSlot.FEET, ItemList.LEATHER_BOOTS_1061)) {
            Logger.log("No leather boots equipped, stopping script");
            Script.stop();
            return false;
        }

        if (!Equipment.itemAt(EquipmentSlot.HANDS, ItemList.LEATHER_GLOVES_1059)) {
            Logger.log("No leather gloves equipped, stopping script");
            Script.stop();
            return false;
        }

        Logger.log("We have the items we need, continuing");
        checkedEquipment = true;
        return true;
    }
}
