package tasks;

import helpers.utils.EquipmentSlot;
import helpers.utils.ItemList;
import main.dWintertodt;
import utils.Task;

import static helpers.Interfaces.*;

public class CheckGear extends Task {
    private final double INVENTORY_THRESHOLD = 0.60;
    boolean gearChecked = false;
    boolean checkedForAxe = false;
    boolean checkedForTinderboxOrBruma = false;
    boolean checkedForKnife = false;
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
        //Logger.debugLog("Inside CheckGear activate()");
        return !gearChecked;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside CheckGear execute()");
        checkForItem();

        checkForAxe();
        if (!checkedForAxe) {
            Logger.log("You are missing an axe!");
        }

        checkForTinderboxOrBruma();
        if (!checkedForTinderboxOrBruma) {
            Logger.log("You are missing a tinderbox or Bruma torch!");
        }

        if (checkedForKnife && checkedForAxe && checkedForTinderboxOrBruma) {
            gearChecked = true;
        }

        return gearChecked;
    }

    private void ensureInventoryOpen() {
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 50, 10);
        }
    }

    private void checkForItem() {
        ensureInventoryOpen();

        if (!checkedForKnife && Inventory.contains(dWintertodt.knife, INVENTORY_THRESHOLD)) {
            Logger.debugLog("Knife" + " in inventory, continuing");
            checkedForKnife = true;
        } else if (!checkedForKnife) {
            Logger.log("You don't have a " + "Knife" + " in inventory");
            Script.stop();
        }
    }

    private void checkForAxe() {
        ensureInventoryOpen();

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
        ensureInventoryOpen();

        if (!checkedForTinderboxOrBruma) {
            if (Inventory.containsAny(lightIDs, INVENTORY_THRESHOLD)) {
                Logger.log("Tinderbox or Bruma in inventory, continuing");
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