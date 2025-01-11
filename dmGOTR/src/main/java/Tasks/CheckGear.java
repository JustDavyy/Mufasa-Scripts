package Tasks;

import helpers.utils.EquipmentSlot;
import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class CheckGear extends Task {
    boolean equipmentChecked = false;
    boolean hasPickaxe = false;
    boolean hasChisel = true;

    int[] pickaxeIDs = { //Reversed order to check highest pickaxes first instead of lower ones.

            ItemList.DRAGON_PICKAXE_11920,
            ItemList.RUNE_PICKAXE_1275,
            ItemList.ADAMANT_PICKAXE_1271,
            ItemList.MITHRIL_PICKAXE_1273,
            ItemList.BLACK_PICKAXE_12297,
            ItemList.STEEL_PICKAXE_1269,
            ItemList.IRON_PICKAXE_1267,
            ItemList.BRONZE_PICKAXE_1265,

            // More odd pickaxes later
            ItemList._3RD_AGE_PICKAXE_20014,
            ItemList.INFERNAL_PICKAXE_13243,
            ItemList.INFERNAL_PICKAXE_UNCHARGED_13244,
            ItemList.CRYSTAL_PICKAXE_23680,
            ItemList.CRYSTAL_PICKAXE_INACTIVE_23682,
            ItemList.GILDED_PICKAXE_23276
    };


    @Override
    public boolean activate() {
        return !equipmentChecked;
    }

    @Override
    public boolean execute() {
        Logger.log("Checking for pickaxe");
        if (!equipmentChecked) {
            if (!GameTabs.isTabOpen(UITabs.INVENTORY)) {
                GameTabs.openTab(UITabs.INVENTORY);
                Condition.wait(() -> GameTabs.isTabOpen(UITabs.INVENTORY), 50, 10);
            }

            if (GameTabs.isTabOpen(UITabs.INVENTORY)) {
                if (Inventory.containsAny(pickaxeIDs, 0.75)) {
                    hasPickaxe = true;
                    return true;
                }

                if (Inventory.contains(ItemList.CHISEL_1755, 0.80)) {
                    hasChisel = true;
                }
            }
        }

        if (!hasPickaxe) {
            if (!GameTabs.isTabOpen(UITabs.EQUIP)) {
                GameTabs.openTab(UITabs.EQUIP);
                Condition.wait(() -> GameTabs.isTabOpen(UITabs.EQUIP), 50, 10);
            }

            if (GameTabs.isTabOpen(UITabs.EQUIP)) {
                for (int pickaxeID : pickaxeIDs) {
                    if (Equipment.itemAt(EquipmentSlot.WEAPON, pickaxeID)) {
                        Logger.log("Pickaxe equipped, continuing");
                        hasPickaxe = true;
                        return true;
                    }
                }
            }
        }
        equipmentChecked = true;

        if (!hasPickaxe || !hasChisel) {
            Logger.log("Pickaxe or Chisel not found, stopping script");
            Script.stop();
            return false;
        }

    return false;
    }
}
