package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dWinemaker.*;

public class Bank extends Task {

    @Override
    public boolean activate() {
        return !Inventory.contains(ItemList.GRAPES_1987, 0.75) || (!Inventory.contains(ItemList.GRAPES_1987, 0.75) && Inventory.usedSlots() <= 14);
    }

    @Override
    public boolean execute() {
        if (stopScript) {
            doneBanking = true;
            return true;
        }

        currentUsedSlots = 69;
        initialActiondone = false;

        Paint.setStatus("Bank");
        Logger.log("Banking.");

        if (retrycount >= 2) {
            Logger.log("Not all needed items in the inventory after 2 banking attempts. Assuming we ran out of supplies.");
            Logger.log("Logging out and stopping script!");
            if (Bank.isOpen()) {
                Bank.close();
            }

            Logout.logout();
            Script.stop();
        }

        // Open the bank (and this steps to the bank if needed)
        Bank.open(bankloc);
        Condition.wait(() -> Bank.isOpen(), 100, 25);

        if (bankItem1Count <= 15 || bankItem2Count <= 15) {
            Logger.debugLog("Bank item count is 15 or below, using non-cached bank withdraw method instead.");
            performBank(false);
        } else {
            performBank(true);
        }

        return false;
    }

    private void performBank(boolean useCache) {
        if (Bank.isOpen()) {
            // Check if we are in the correct bank tab, else switch
            if (!Bank.isSelectedBankTab(banktab)) {
                Paint.setStatus("Change bank tab");
                Bank.openTab(banktab);
                Logger.log("Opened bank tab " + banktab);
                Condition.sleep(generateDelay(150, 300));
            }

            // Check if we have actual processed items
            if (Inventory.contains(ItemList.JUG_OF_WINE_1993, 0.75)) {
                // Count our processed items
                updateProcessedItems();
            } else {
                retrycount++;
            }

            // Deposit processed items
            depositItems();

            // Withdraw our new items
            Paint.setStatus("Withdraw bank items");
            withdrawItems(useCache);
            Condition.wait(this::checkItems, 100,30);

            if (bankItem1Count <= 15 || bankItem2Count <= 15) {
                Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                prepareScriptStop = true;
            }

            // Close the bank
            Paint.setStatus("Close bank");
            closeBank();

        } else {
            Logger.debugLog("Bank not open while in bank logic, skipping!");
        }
    }

    private boolean checkItems() {
        return Inventory.contains(ItemList.GRAPES_1987, 0.75) && Inventory.contains(ItemList.JUG_OF_WATER_1937, 0.7);
    }

    private void depositItems() {
        // As we have no tools, we can simply deposit our inventory
        Bank.tapDepositInventoryButton();
        Condition.sleep(generateDelay(150, 300));
    }

    private void withdrawItems(boolean useCache) {
        // Withdraw first item and update our current stacksize
        updatePreviousBankItemCount(1);
        bankItem1Count = Bank.stackSize(ItemList.GRAPES_1987);
        Bank.withdrawItem(ItemList.GRAPES_1987, useCache, 0.75);
        printItemStateDebug(1, "Grapes");

        // Wait for a small bit
        Condition.sleep(generateDelay(125, 250));

        // Withdraw second item
        updatePreviousBankItemCount(2);
        bankItem2Count = Bank.stackSize(ItemList.JUG_OF_WATER_1937);
        Bank.withdrawItem(ItemList.JUG_OF_WATER_1937, useCache, 0.7, Color.decode("#6769c8"));
        printItemStateDebug(2, "Jug of Water");
    }

    private void closeBank() {
        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 100, 30);

        if (Bank.isOpen()) {
            Bank.close();
        }
    }

    private void updateProcessedItems() {
        // Update our process count
        PROCESS_COUNT = PROCESS_COUNT + Inventory.count(ItemList.JUG_OF_WINE_1993, 0.75);

        // Update the paint bar with this new count
        updatePaintBar(PROCESS_COUNT);
    }

    private void printItemStateDebug(int itemNr, String itemName) {
        if (itemNr == 1) {
            Logger.debugLog(itemName + " previously left in bank: " + previousBankItem1Count);
            Logger.debugLog(itemName + " currently left in bank: " + bankItem1Count);
        } else {
            Logger.debugLog(itemName + " previously left in bank: " + previousBankItem2Count);
            Logger.debugLog(itemName + " currently left in bank: " + bankItem2Count);
        }
    }

    private void updatePreviousBankItemCount(int updateItem) {
        if (updateItem == 1) {
            if (previousBankItem1Count != -1 & bankItem1Count > 0) {
                previousBankItem1Count = bankItem1Count;
            }
        } else if (updateItem == 2) {
            if (previousBankItem2Count != -1 & bankItem2Count > 0) {
                previousBankItem2Count = bankItem2Count;
            }
        }
    }
}