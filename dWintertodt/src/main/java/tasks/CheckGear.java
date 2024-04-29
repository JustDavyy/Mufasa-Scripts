package tasks;

import helpers.utils.EquipmentSlot;
import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;

public class CheckGear extends Task {
    boolean gearChecked = false;
    boolean checkedForAxe = false;
    boolean checkedForTinderboxOrBruma = false;
    boolean checkedForKnife = false;
    private final double INVENTORY_THRESHOLD = 0.60;

    private final Integer knife = 946;
    int[] axeIDs = { //Reversed order to check highest axe first instead of lower ones.
            ItemList.DRAGON_AXE_6739,
            ItemList.STEEL_AXE_1353, // Steel axe higher in prio, since a lot would be using that
            ItemList.RUNE_AXE_1359,
            ItemList.ADAMANT_AXE_1357,
            ItemList.MITHRIL_AXE_1355,
            ItemList.BLACK_AXE_1361,
            ItemList.IRON_AXE_1349,
            ItemList.BRONZE_AXE_1351
    };
    int[] lightIDs = { //Reversed order to check highest axe first instead of lower ones.
            ItemList.BRUMA_TORCH_20720,
            ItemList.TINDERBOX_590
    };

    @Override
    public boolean activate() {
        return !gearChecked;
    }

    @Override
    public boolean execute() {
        ensureInventoryOpen();
        checkForItem(knife, "Knife");
        checkForAxe();
        ensureInventoryOpen();
        checkForTinderboxOrBruma();
        return gearChecked;
    }

    private void ensureInventoryOpen() {
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 50, 10);
        }
    }

    private void checkForItem(int itemID, String itemName) {
        if (!checkedForKnife && Inventory.contains(itemID, INVENTORY_THRESHOLD)) {
            Logger.debugLog(itemName + " in inventory, continuing");
            checkedForKnife = true;
        } else if (!checkedForKnife) {
            Logger.log("You don't have a " + itemName + " in inventory");
            Script.stop();
        }
    }

    private void checkForAxe() {
        if (!checkedForAxe) {
            if (Inventory.containsAny(axeIDs, INVENTORY_THRESHOLD)) {
                Logger.debugLog("Axe in inventory, continuing");
                checkedForAxe = true;
            } else {
                if (!GameTabs.isEquipTabOpen()) {
                    GameTabs.openEquipTab();
                    Condition.wait(() -> GameTabs.isEquipTabOpen(), 50, 10);
                }
                for (int axe : axeIDs) {
                    if (Equipment.itemAt(EquipmentSlot.WEAPON, axe)) {
                        checkedForAxe = true;
                        Logger.debugLog("Axe equipped, continuing");
                        break;
                    }
                }
            }
        }
    }

    private void checkForTinderboxOrBruma() {
        if (!checkedForTinderboxOrBruma) {
            if (Inventory.containsAny(lightIDs, INVENTORY_THRESHOLD)) {
                checkedForTinderboxOrBruma = true;
            } else {
                if (!GameTabs.isEquipTabOpen()) {
                    GameTabs.openEquipTab();
                    Condition.wait(() -> GameTabs.isEquipTabOpen(), 50, 10);
                }
                if (Equipment.itemAt(EquipmentSlot.WEAPON, ItemList.BRUMA_TORCH_20720)) {
                    checkedForTinderboxOrBruma = true;
                    Logger.debugLog("Bruma Torch equipped, continuing");
                }
            }
        }
    }
}