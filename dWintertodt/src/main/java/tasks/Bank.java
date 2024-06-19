package tasks;

import helpers.utils.ItemList;
import main.dWintertodt;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class Bank extends Task {
    private boolean checkFood = true;
    private String dynamicBank;

    @Override
    public boolean activate() {
        Logger.debugLog("Inside Bank activate()");
        return foodAmountInInventory < foodAmountLeftToBank && !isGameGoing || foodAmountInInventory < foodAmountLeftToBank && Player.isTileWithinArea(currentLocation, outsideArea);
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside Bank execute()");
        checkFood = true;

        if (walkToBankFromDoorInside()) {
            currentLocation = Walker.getPlayerPosition(WTRegion);
        } else if (walkToBankFromGame()) {
            currentLocation = Walker.getPlayerPosition(WTRegion);
        } else if (walkToBankFromOutsideArea()) {
            currentLocation = Walker.getPlayerPosition(WTRegion);
        }

        if (!Player.tileEquals(currentLocation, bankTile)) {
            Walker.step(bankTile, WTRegion);
            currentLocation = Walker.getPlayerPosition(WTRegion);
        }
        if (Player.tileEquals(currentLocation, bankTile)) {
            handleBanking();
            return true;
        }

        countFoodInInventory();
        return false;
    }

    private void handleBanking() {
        setupOrStepToBank();
        if (ensureBankIsOpen()) {
            ensureCorrectBankTab();

            int foodNeeded = CalculateAmountOfFoodNeeded();
            withdrawFoodIfNeeded(foodNeeded);
            depositExcessSupplyCrates();
        }
    }

    private void setupOrStepToBank() {
        if (dynamicBank == null) {
            dynamicBank = Bank.setupDynamicBank();
        } else {
            Bank.stepToBank(dynamicBank);
        }
    }

    private boolean ensureBankIsOpen() {
        if (!Bank.isOpen()) {
            Bank.open(dynamicBank);
            Condition.wait(() -> Bank.isOpen(), 100, 10);
            return true;
        }
        return false;
    }

    private void withdrawFoodIfNeeded(int foodNeeded) {
        if (foodNeeded > 0) {

            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
            }
            for (int i = 0; i < foodNeeded; i++) {
                Bank.withdrawItem(foodID, 1);
            }
        }
    }

    private void ensureCorrectBankTab() {
        if (Bank.getCurrentTab(true) != bankTab) {
            Bank.openTab(bankTab);
            Condition.wait(() -> Bank.getCurrentTab(true) == bankTab, 100, 20);
        }
    }

    private void depositExcessSupplyCrates() {
        if (Inventory.contains(ItemList.SUPPLY_CRATE_20703, 0.80)) {
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
            }
            Inventory.tapItem(ItemList.SUPPLY_CRATE_20703, 0.80);
            Condition.wait(() -> !Inventory.contains(ItemList.SUPPLY_CRATE_20703, 0.80), 100, 20);
        }
    }

    private int CalculateAmountOfFoodNeeded() {
        // Calculate the amount of food needed to withdraw from the bank
        int foodNeeded = dWintertodt.foodAmountLeftToBank - dWintertodt.foodAmountInInventory;

        // If more food is needed, return that amount; otherwise, return 0
        return Math.max(foodNeeded, 0);
    }

    // Method to count total food items in the inventory
    private void countFoodInInventory() {
        if (checkFood) {
            dWintertodt.foodAmountInInventory = 0; // Reset before counting

            if (selectedFood.equals("Cakes")) {
                int[] foodIds = {1891, 1893, 1895};
                for (int id : foodIds) {
                    int countMultiplier = 1; // Default count multiplier
                    if (id == 1891) {
                        countMultiplier = 3; // A full cake counts as 3
                    } else if (id == 1893) {
                        countMultiplier = 2; // half cake counts as 2
                    }

                    // Assume Inventory.count(id, 0.60) returns the number of items that are at least 60% intact
                    dWintertodt.foodAmountInInventory += Inventory.count(id, 0.60) * countMultiplier;
                    checkFood = false;
                }
            } else {
                dWintertodt.foodAmountInInventory = Inventory.count(foodID, 0.60);
                checkFood = false;
            }
        }
    }

    private boolean walkToBankFromGame() {
        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Walker.walkPath(WTRegion, gameToWTDoor);
            Condition.wait(() -> Player.within(atDoor, WTRegion), 100, 20);
            Client.tap(exitDoorRect);
            Condition.sleep(generateRandomDelay(3500, 5000));
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToBankFromDoorInside() {
        if (Player.isTileWithinArea(currentLocation, atDoor)) {
            Client.tap(exitDoorRect);
            Condition.sleep(generateRandomDelay(3500, 5000));
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToBankFromOutsideArea() {
        if (Player.isTileWithinArea(currentLocation, outsideArea)) {
            Walker.step(bankTile, WTRegion); //Step to bank tile.
            Condition.wait(() -> Player.atTile(bankTile, WTRegion), 100, 20);
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }
}
