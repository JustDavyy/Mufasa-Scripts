package Tasks;

import helpers.utils.Tile;
import helpers.utils.ItemList;
import main.dmCrabberPrivate;
import utils.Task;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static helpers.Interfaces.*;
import static main.dmCrabberPrivate.*;

public class Baank extends Task {
    private final Tile bankTile = new Tile(6875, 13609, 0);
    private final Map<String, Boolean> gearWithdrawn = new HashMap<>();
    private String dynamicBank = "Hosidius_crab_bank";
    Color itemColor = null;


    public Baank() {
        // Initialize withdrawal status for all equipment types
        for (String key : SkillTracker.getEquipmentStatus().keySet()) {
            gearWithdrawn.put(key, false);
        }
    }



    @Override
    public boolean activate() {
        // Activate only if food or potions are needed, or gear needs equipping
        boolean needsGear = SkillTracker.getEquipmentStatus().values().stream()
                .anyMatch(status -> status == SkillTracker.EquipmentStatus.TO_EQUIP);
        return (!Inventory.contains(foodID, 0.80) || outOfPots || needsGear);
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Banking started");

        navigateToBankArea();

        if (Player.within(bankArea)) {
            handleBanking();
        }

        outOfPots = false; // Reset pot status
        return true;
    }

    private void navigateToBankArea() {
        Logger.debugLog("Navigating to the bank area");

        // Walk to the bank area if not already there
        if (!Player.within(bankArea)) {
            Walker.webWalk(bankTile, true);
        }
        // Step to the bank tile if needed
        if (!Player.atTile(bankTile)) {
            Walker.step(bankTile);
        }
    }

    private void handleBanking() {
        Logger.debugLog("Banking");
        if (!Bank.isOpen()) {
            if (dynamicBank == null) {
                dynamicBank = Bank.setupDynamicBank();
            } else if (!Player.atTile(bankTile)) {
                Bank.stepToBank(dynamicBank);
            }

            if (Player.atTile(bankTile)) {
                Bank.open(dynamicBank);
                Condition.wait(Bank::isOpen, 500, 10);
            }
        }

        if (Bank.isOpen()) {
            depositItems();
            selectBankTab();
            withdrawGear();
            withdrawPotions();
            withdrawFood();
            closeBank();
        }
    }

    private void depositItems() {
        Bank.tapDepositInventoryButton();
        Logger.debugLog("Deposited inventory");
    }

    private void selectBankTab() {
        if (!Bank.isSelectedBankTab(selectedBankTab)) {
            Bank.openTab(selectedBankTab);
            Condition.wait(() -> Bank.isSelectedBankTab(selectedBankTab), 250, 12);
            Logger.debugLog("Opened bank tab: " + selectedBankTab);
        }
    }

    private void withdrawGear() {
        Map<String, SkillTracker.EquipmentStatus> equipmentStatus = SkillTracker.getEquipmentStatus();

        for (Map.Entry<String, SkillTracker.EquipmentStatus> entry : equipmentStatus.entrySet()) {
            String equipmentType = entry.getKey();
            SkillTracker.EquipmentStatus status = entry.getValue();

            // Skip if gear is already equipped or withdrawn
            if (gearWithdrawn.getOrDefault(equipmentType, false) || status != SkillTracker.EquipmentStatus.TO_EQUIP) {
                continue;
            }

            Logger.debugLog("Withdrawing gear for: " + equipmentType);
            withdrawItemsForGear(equipmentType);

            gearWithdrawn.put(equipmentType, true); // Mark as withdrawn
        }
    }

    private void withdrawItemsForGear(String equipmentType) {
        switch (equipmentType) {
            case "Iron":
                itemColor = dmCrabberPrivate.IronPlatebodyColor;
                withdrawItem(ItemList.IRON_PLATEBODY_1115, itemColor);
                withdrawItem(ItemList.IRON_PLATELEGS_1067, itemColor);
                withdrawItem(ItemList.IRON_FULL_HELM_1153, itemColor);
                withdrawItem(ItemList.IRON_KITESHIELD_1191, itemColor);
                withdrawItem(ItemList.IRON_SCIMITAR_1323, dmCrabberPrivate.IronScimitarColor);
                break;

            case "Addy":
                itemColor = dmCrabberPrivate.AdamantPlateBodyColor;
                withdrawItem(ItemList.ADAMANT_PLATEBODY_1123, itemColor);
                withdrawItem(ItemList.ADAMANT_PLATELEGS_1073, itemColor);
                withdrawItem(ItemList.ADAMANT_FULL_HELM_1161, itemColor);
                withdrawItem(ItemList.ADAMANT_KITESHIELD_1199, itemColor);
                withdrawItem(ItemList.ADAMANT_SCIMITAR_1331, dmCrabberPrivate.AdamantScimitarColor);
                break;

            case "Leather":
                itemColor = null;
                withdrawItem(ItemList.LEATHER_BODY_1129, itemColor);
                withdrawItem(ItemList.LEATHER_CHAPS_1095, itemColor);
                withdrawItem(ItemList.LEATHER_COWL_1167, itemColor);
                withdrawItem(ItemList.LEATHER_VAMBRACES_1063, itemColor);
                withdrawItem(ItemList.SHORTBOW_841, itemColor);
                withdrawItem(ItemList.OAK_SHORTBOW_843, itemColor);
                break;

            case "Snakeskin":
                itemColor = null;
                withdrawItem(ItemList.SNAKESKIN_BODY_6322, itemColor);
                withdrawItem(ItemList.SNAKESKIN_CHAPS_6324, itemColor);
                withdrawItem(ItemList.SNAKESKIN_BANDANA_6326, itemColor);
                withdrawItem(ItemList.SNAKESKIN_BOOTS_6328, itemColor);
                withdrawItem(ItemList.SNAKESKIN_VAMBRACES_6330, itemColor);
                break;

            case "GreenDhide":
                itemColor = null;
                withdrawItem(ItemList.GREEN_D_HIDE_BODY_1135, itemColor);
                withdrawItem(ItemList.GREEN_D_HIDE_CHAPS_1099, itemColor);
                withdrawItem(ItemList.GREEN_D_HIDE_VAMBRACES_1065, itemColor);
                break;

            case "RuneScimitar":
                itemColor = dmCrabberPrivate.RuneScimitarColor;
                withdrawItem(ItemList.RUNE_SCIMITAR_1333, dmCrabberPrivate.RuneScimitarColor);
                break;

            case "GraniteHammer":
                itemColor = null;
                withdrawItem(ItemList.GRANITE_HAMMER_21742, itemColor);
                break;

            case "WillowShortBow":
                itemColor = null;
                withdrawItem(ItemList.WILLOW_SHORTBOW_849, itemColor);
                withdrawItem(ItemList.MITHRIL_ARROW_1_921, itemColor);
                break;

            case "MagicShortBow":
                itemColor = null;
                withdrawItem(ItemList.MAGIC_SHORTBOW_861, itemColor);
                break;

            default:
                Logger.debugLog("No items defined for: " + equipmentType);
        }
    }

    private void withdrawItem(int itemId, Color itemColor) {
        Bank.withdrawItem(itemId, 0.8, itemColor);
        Condition.wait(() -> Inventory.contains(itemId, 0.8, itemColor), 350, 12);
        Logger.debugLog("Withdrawn item with ID: " + itemId);
    }

    private void withdrawPotions() {
        if (!potions.equals("None")) {
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(Bank::isSelectedQuantity5Button, 350, 12);
            }

            for (int i = 0; i < 3; i++) {
                Bank.withdrawItem(potionID, 0.95);
            }

            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(Bank::isSelectedQuantity1Button, 350, 12);
            }
        }
    }

    private void withdrawFood() {
        if (!Bank.isSelectedQuantityAllButton()) {
            Bank.tapQuantityAllButton();
            Condition.wait(Bank::isSelectedQuantityAllButton, 350, 12);
        }
        Bank.withdrawItem(foodID, 0.8);
    }

    private void closeBank() {
        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 500, 10);
    }
    
}
