package Tasks;

import helpers.utils.EquipmentSlot;
import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Script;

public class CheckGear extends Task {
    boolean equipmentChecked = false;

    int[] pickaxeIDs = { //Reversed order to check highest pickaxes first instead of lower ones.
            ItemList._3RD_AGE_PICKAXE_20014,
            ItemList.INFERNAL_PICKAXE_13243,
            ItemList.INFERNAL_PICKAXE_UNCHARGED_13244,
            ItemList.CRYSTAL_PICKAXE_23680,
            ItemList.CRYSTAL_PICKAXE_UNCHARGED_23682,
            ItemList.DRAGON_PICKAXE_11920,
            ItemList.RUNE_PICKAXE_1275,
            ItemList.GILDED_PICKAXE_23276,
            ItemList.ADAMANT_PICKAXE_1271,
            ItemList.MITHRIL_PICKAXE_1273,
            ItemList.BLACK_PICKAXE_12297,
            ItemList.STEEL_PICKAXE_1269,
            ItemList.IRON_PICKAXE_1267,
            ItemList.BRONZE_PICKAXE_1265
    };


    @Override
    public boolean activate() {
        return !equipmentChecked;
    }

    @Override
    public boolean execute() {
        Logger.log("Checking for pickaxe");
        if (!equipmentChecked) {
            if (!GameTabs.isInventoryTabOpen()) {
                GameTabs.openInventoryTab();
                Condition.wait(() -> GameTabs.isInventoryTabOpen(), 50, 10);
            }

            if (GameTabs.isInventoryTabOpen()) {
                if (Inventory.containsAny(pickaxeIDs, 0.75)) {
                    equipmentChecked = true;
                    return true;
                }
            }
        }

        if (!equipmentChecked) {
            if (!GameTabs.isEquipTabOpen()) {
                GameTabs.openEquipTab();
                Condition.wait(() -> GameTabs.isEquipTabOpen(), 50, 10);
            }

            if (GameTabs.isEquipTabOpen()) {
                for (int pickaxeID : pickaxeIDs) {
                    if (Equipment.itemAt(EquipmentSlot.WEAPON, pickaxeID)) {
                        Logger.log("Pickaxe equipped, continuing");
                        equipmentChecked = true;
                        return true;
                    }
                }
            }
        }

        if (!equipmentChecked) {
            Logger.log("Pickaxe not found, stopping script");
            Script.stop();
            return false;
        }
    return false;
    }
}
