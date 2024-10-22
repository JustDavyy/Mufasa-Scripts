package Tasks;

import helpers.utils.Tile;
import utils.Task;

import static Tasks.PerformCrabbing.startTime;
import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class Bank extends Task {
    private final Tile bankTile = new Tile(6875, 13609, 0);

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