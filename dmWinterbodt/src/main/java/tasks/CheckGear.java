package tasks;

import helpers.utils.EquipmentSlot;
import helpers.utils.ItemList;
import main.dmWinterbodt;
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
            ItemList.BRONZE_AXE_1351,

            // Odd axes, just added to be able to detect and support them, but low in priority.
            ItemList.CRYSTAL_AXE_23673,
            ItemList.CRYSTAL_AXE_INACTIVE_23675,
            ItemList.CRYSTAL_FELLING_AXE_28220,
            ItemList.CRYSTAL_FELLING_AXE_INACTIVE_28223,
            ItemList.INFERNAL_AXE_13241,
            ItemList.INFERNAL_AXE_UNCHARGED_13242,
            ItemList._3RD_AGE_AXE_20011,
            ItemList._3RD_AGE_FELLING_AXE_28226,
            ItemList.DRAGON_FELLING_AXE_28217,
            ItemList.RUNE_FELLING_AXE_28214,
            ItemList.ADAMANT_FELLING_AXE_28211,
            ItemList.MITHRIL_FELLING_AXE_28208,
            ItemList.BLACK_FELLING_AXE_28205,
            ItemList.STEEL_FELLING_AXE_28202,
            ItemList.IRON_FELLING_AXE_28199,
            ItemList.BRONZE_FELLING_AXE_28196
    };
    int[] lightIDs = { //Reversed order to check highest axe first instead of lower ones.
            ItemList.BRUMA_TORCH_20720,
            ItemList.BRUMA_TORCH_OFF_HAND_29777,
            ItemList.TINDERBOX_590
    };

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside CheckGear activate()");
        return !gearChecked;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Checking gear");
        Logger.log("Checking gear..");
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
            Paint.setStatus("Opening inventory");
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 50, 10);
        }
    }

    private void checkForItem() {
        ensureInventoryOpen();
        Paint.setStatus("Checking for a knife");

        if (!checkedForKnife && Inventory.contains(dmWinterbodt.knife, INVENTORY_THRESHOLD)) {
            Logger.debugLog("Knife" + " in inventory, continuing");
            checkedForKnife = true;
        } else if (!checkedForKnife) {
            Logger.log("You don't have a " + "Knife" + " in inventory");
            Script.stop();
        }
    }

    private void checkForAxe() {
        ensureInventoryOpen();
        Paint.setStatus("Checking for an axe");

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
        Paint.setStatus("Checking for tinderbox/bruma");

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
                } else if (Equipment.itemAt(EquipmentSlot.SHIELD, ItemList.BRUMA_TORCH_OFF_HAND_29777)) {
                    checkedForTinderboxOrBruma = true;
                    Logger.debugLog("Bruma Torch equipped in shield slot, continuing");
                }
            }
        }
    }
}