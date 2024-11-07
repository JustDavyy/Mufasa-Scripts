package Tasks;

import helpers.utils.Tile;
import helpers.utils.ItemList;
import utils.Task;

import static Tasks.PerformCrabbing.startTime;
import static helpers.Interfaces.*;
import static main.dmCrabberPrivate.*;

public class Baank extends Task {
    private final Tile bankTile = new Tile(6875, 13609, 0);
    public static Boolean IronEquippedWithdrawed = false;
    public static Boolean AddyEquippedWithdrawed = false;
    public static Boolean LeatherEquippedWithdrawed = false;
    public static Boolean SnakeSkinEquippedWithdrawed = false;
    public static Boolean GreenDhideEquippedWithdrawed = false;
    public static Boolean RuneScimitarWithdrawed = false;
    public static Boolean GraniteHammerWithdrawed = false;
    public static Boolean WillowShortBowWithdrawed = false;
    public static Boolean MagicShortBowWithdrawed = false;
    String dynamicBank = "Hosidius_crab_bank";

    // I'm guessing we should just withdraw full inv of food?
    @Override
    public boolean activate() {
        if (!usingPots && selectedFood.equals("None")) {
            return false;
        }

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 100, 10);
        }

        return (!Inventory.contains(foodID, 0.80) || outOfPots);
    }

    @Override
    public boolean execute() {
        startTime = 0; // Reset the perform crabbing start time

        navigateToBankArea();
        if (Player.isTileWithinArea(currentLocation, bankArea)) {
            handleBanking();
        }

        outOfPots = false;
        return true;
    }

    private void navigateToBankArea() {
        Logger.debugLog("Navigating to the bank area");
        // Check if player needs to walk to the bank area
        if (!Player.isTileWithinArea(currentLocation, bankArea)) {
            Walker.webWalk(bankTile, true);
            currentLocation = Walker.getPlayerPosition();
        }

        // Check if player needs to step to the bank tile
        if (!Player.tileEquals(currentLocation, bankTile)) {
            Walker.step(bankTile);
            currentLocation = Walker.getPlayerPosition();
        }
    }

    private void handleBanking() {
        Logger.debugLog("Banking");
        if (dynamicBank == null) {
            dynamicBank = Bank.setupDynamicBank();
        } else {
            Bank.stepToBank(dynamicBank);
        }

        if (!Bank.isOpen()) {
            Bank.open(dynamicBank);
            Condition.wait(() -> Bank.isOpen(), 500, 10);
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
    }

    private void selectBankTab() {
        if (!Bank.isSelectedBankTab(selectedBankTab)) {
            Bank.openTab(selectedBankTab);
            Condition.wait(() -> Bank.isSelectedBankTab(selectedBankTab), 250, 12);
            Logger.debugLog("Opened bank tab " + selectedBankTab);
        }
    }

    private void withdrawGear() {

        if (!IronEquippedWithdrawed && SkillTracker.changeToEquipment.getOrDefault("Iron", false)) {
            Bank.withdrawItem(ItemList.IRON_PLATEBODY_1115, 0.8); // iron platebody
            Condition.wait(() -> Inventory.contains(ItemList.IRON_PLATEBODY_1115, 0.8),250,12);

            Bank.withdrawItem(ItemList.IRON_PLATELEGS_1067, 0.8); // iron platelegs
            Condition.wait(() -> Inventory.contains(ItemList.IRON_PLATELEGS_1067, 0.8),250,12);

            Bank.withdrawItem(ItemList.IRON_FULL_HELM_1153, 0.8); // iron full helmet
            Condition.wait(() -> Inventory.contains(ItemList.IRON_FULL_HELM_1153, 0.8),250,12);

            Bank.withdrawItem(ItemList.IRON_KITESHIELD_1191, 0.8); // iron kite shield
            Condition.wait(() -> Inventory.contains(ItemList.IRON_KITESHIELD_1191, 0.8),250,12);

            Bank.withdrawItem(ItemList.IRON_SCIMITAR_1323, 0.8); // iron scimitar
            Condition.wait(() -> Inventory.contains(ItemList.IRON_SCIMITAR_1323, 0.8),250,12);
            IronEquippedWithdrawed = true;
            Logger.debugLog("Done withdrawing Iron gear");
        }

        if (!AddyEquippedWithdrawed && SkillTracker.changeToEquipment.getOrDefault("Addy", false)) {
            Bank.withdrawItem(ItemList.ADAMANT_PLATEBODY_1123, 0.8); // Adamant platebody
            Condition.wait(() -> Inventory.contains(ItemList.ADAMANT_PLATEBODY_1123, 0.8),250,12);
            
            Bank.withdrawItem(ItemList.ADAMANT_PLATELEGS_1073, 0.8); // Adamant platelegs
            Condition.wait(() -> Inventory.contains(ItemList.ADAMANT_PLATELEGS_1073, 0.8),250,12);

            Bank.withdrawItem(ItemList.ADAMANT_FULL_HELM_1161, 0.8); // Adamant full helmet
            Condition.wait(() -> Inventory.contains(ItemList.ADAMANT_FULL_HELM_1161, 0.8),250,12);

            Bank.withdrawItem(ItemList.ADAMANT_KITESHIELD_1199, 0.8); // Adamant kite shield
            Condition.wait(() -> Inventory.contains(ItemList.ADAMANT_KITESHIELD_1199, 0.8),250,12);

            Bank.withdrawItem(ItemList.ADAMANT_SCIMITAR_1331, 0.8); // Adamant Scimitar
            Condition.wait(() -> Inventory.contains(ItemList.ADAMANT_SCIMITAR_1331, 0.8),250,12);
            AddyEquippedWithdrawed = true;
            Logger.debugLog("Done withdrawing Adamant gear");
        } 

        if (!LeatherEquippedWithdrawed && SkillTracker.changeToEquipment.getOrDefault("Leather", false)) {
            Bank.withdrawItem(ItemList.LEATHER_BODY_1129, 0.8); // Leather platebody
            Condition.wait(() -> Inventory.contains(ItemList.LEATHER_BODY_1129, 0.8),250,12);
            
            Bank.withdrawItem(ItemList.LEATHER_CHAPS_1095, 0.8); // leather chaps
            Condition.wait(() -> Inventory.contains(ItemList.LEATHER_CHAPS_1095, 0.8),250,12);

            Bank.withdrawItem(ItemList.LEATHER_COWL_1167, 0.8); // leather cowl
            Condition.wait(() -> Inventory.contains(ItemList.LEATHER_COWL_1167, 0.8),250,12);

            Bank.withdrawItem(ItemList.LEATHER_VAMBRACES_1063, 0.8); // leather vambraces
            Condition.wait(() -> Inventory.contains(ItemList.LEATHER_VAMBRACES_1063, 0.8),250,12);

            Bank.withdrawItem(ItemList.SHORTBOW_841, 0.8); // shortbow
            Condition.wait(() -> Inventory.contains(ItemList.SHORTBOW_841, 0.8),250,12);

            Bank.withdrawItem(ItemList.OAK_SHORTBOW_843, 0.8); // Oak shortbow taking with us since it only 4 levels to use.
            Condition.wait(() -> Inventory.contains(ItemList.OAK_SHORTBOW_843, 0.8),250,12);
            LeatherEquippedWithdrawed = true;
            Logger.debugLog("Done withdrawing Leather gear");
        }

        if (!SnakeSkinEquippedWithdrawed && SkillTracker.changeToEquipment.getOrDefault("Snakeskin", false)) {
            Bank.withdrawItem(ItemList.SNAKESKIN_BODY_6322, 0.8); // Snakeskin body
            Condition.wait(() -> Inventory.contains(ItemList.SNAKESKIN_BODY_6322, 0.8),250,12);
            
            Bank.withdrawItem(ItemList.SNAKESKIN_CHAPS_6324, 0.8); // Snakeskin legs
            Condition.wait(() -> Inventory.contains(ItemList.SNAKESKIN_CHAPS_6324, 0.8),250,12);

            Bank.withdrawItem(ItemList.SNAKESKIN_BANDANA_6326, 0.8); // snakeskin bandana
            Condition.wait(() -> Inventory.contains(ItemList.SNAKESKIN_BANDANA_6326, 0.8),250,12);

            Bank.withdrawItem(ItemList.SNAKESKIN_BOOTS_6328, 0.8); // snakeskin boots
            Condition.wait(() -> Inventory.contains(ItemList.SNAKESKIN_BOOTS_6328, 0.8),250,12);

            Bank.withdrawItem(ItemList.SNAKESKIN_VAMBRACES_6330, 0.8); // snakeskin vambracers
            Condition.wait(() -> Inventory.contains(ItemList.SNAKESKIN_VAMBRACES_6330, 0.8),250,12);
            SnakeSkinEquippedWithdrawed = true;
            Logger.debugLog("Done withdrawing Snakeskin gear");
        }

        if (!GreenDhideEquippedWithdrawed && SkillTracker.changeToEquipment.getOrDefault("GreenDhide", false)) {
            Bank.withdrawItem(ItemList.GREEN_D_HIDE_BODY_1135, 0.8); // Green D'hide body
            Condition.wait(() -> Inventory.contains(ItemList.GREEN_D_HIDE_BODY_1135, 0.8),250,12);
            
            Bank.withdrawItem(ItemList.GREEN_D_HIDE_CHAPS_1099, 0.8); // Green D'hide chaps
            Condition.wait(() -> Inventory.contains(ItemList.GREEN_D_HIDE_CHAPS_1099, 0.8),250,12);

            Bank.withdrawItem(ItemList.GREEN_D_HIDE_VAMBRACES_1065, 0.8); // Green D'hide Vampbraces
            Condition.wait(() -> Inventory.contains(ItemList.GREEN_D_HIDE_VAMBRACES_1065, 0.8),250,12);

            GreenDhideEquippedWithdrawed = true;
            Logger.debugLog("Done withdrawing GreenDhide gear");
        } 
        
        if (!RuneScimitarWithdrawed && SkillTracker.changeToEquipment.getOrDefault("RuneScimitar", false)) {
            Bank.withdrawItem(ItemList.RUNE_SCIMITAR_1333, 0.8); // Rune Scimitar
            RuneScimitarWithdrawed = true;
            Condition.wait(() -> Inventory.contains(ItemList.RUNE_SCIMITAR_1333, 0.8),250,12);
            Logger.debugLog("Done withdrawing Rune scimitar");
        }

        if (!GraniteHammerWithdrawed && SkillTracker.changeToEquipment.getOrDefault("GraniteHammer", false)) {
            Bank.withdrawItem(ItemList.GRANITE_HAMMER_21742, 0.8); // Granite Hammer
            GraniteHammerWithdrawed = true;
            Condition.wait(() -> Inventory.contains(ItemList.GRANITE_HAMMER_21742, 0.8),250,12);
            Logger.debugLog("Done withdrawing Granite hammer");
        }

        if (!WillowShortBowWithdrawed && SkillTracker.changeToEquipment.getOrDefault("WillowShortBow", false)) {
            Bank.withdrawItem(ItemList.WILLOW_SHORTBOW_849, 0.8); // Granite Hammer
            Condition.wait(() -> Inventory.contains(ItemList.WILLOW_SHORTBOW_849, 0.8),250,12);
            Logger.debugLog("Done withdrawing Willow shortbow");

            Bank.withdrawItem(ItemList.MITHRIL_ARROW_1_921, 0.8);
            Condition.wait(() -> Inventory.contains(ItemList.MITHRIL_ARROW_1_921, 0.8),250,12);
            Logger.debugLog("Done withdrawing Mithril arrows");

            WillowShortBowWithdrawed = true;
        }

        if (!MagicShortBowWithdrawed && SkillTracker.changeToEquipment.getOrDefault("MagicShortBow", false)) {
            Bank.withdrawItem(ItemList.MAGIC_SHORTBOW_861, 0.8); // Granite Hammer
            MagicShortBowWithdrawed = true;
            Condition.wait(() -> Inventory.contains(ItemList.MAGIC_SHORTBOW_861, 0.8),250,12);
            Logger.debugLog("Done withdrawing Magic shortbow");
        }

    }


    private void withdrawPotions() {
        if (!potions.equals("None")) {
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(() -> Bank.isSelectedQuantity5Button(), 250, 12);
            }

            for (int i = 0; i < 3; i++) {
                Bank.withdrawItem(potionID, 0.95);
            }

            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }
        }
    }

    private void withdrawFood() {
        if (!Bank.isSelectedQuantityAllButton()) {
            Bank.tapQuantityAllButton();
            Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 250, 12);
        }
        Bank.withdrawItem(foodID, 0.8);
    }

    private void closeBank() {
        Bank.close();
        Condition.wait(() -> Bank.isOpen(), 500, 10);

        if (Bank.isOpen()) {
            Bank.close();
        }
    }
}