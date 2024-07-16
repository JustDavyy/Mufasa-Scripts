package tasks;


import helpers.utils.EquipmentSlot;
import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;

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
        Logger.log("Checking for tools");

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(GameTabs::isInventoryTabOpen, 50, 10);
        }

        if (!Inventory.containsAll(museumToolIDs, 0.80)) {
            Logger.log("We did not find some of the tools in inventory, stopping script");
            Script.stop();
            return false;
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
