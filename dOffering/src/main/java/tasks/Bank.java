package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dOffering.*;

public class Bank extends Task {

    @Override
    public boolean activate() {
        return doneProcessing;
    }

    @Override
    public boolean execute() {
        if (stopScript) {
            doneBanking = true;
            return true;
        }

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

        GameTabs.openTab(UITabs.INVENTORY);

        // Open the bank (and this steps to the bank if needed)
        Bank.open(bankloc);
        Condition.wait(() -> Bank.isOpen(), 100, 25);

        if (bankItem1Count <= 28) {
            Logger.debugLog("Bank item count is 28 or below, using non-cached bank withdraw method instead.");
            performBank(false);
        } else {
            performBank(true);
        }

        doneProcessing = false;
        currentIteration = 0;

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

            // Withdraw our new items
            Paint.setStatus("Withdraw bank items");
            withdrawItems(useCache);
            Condition.wait(this::checkItems, 100,30);

            if (bankItem1Count <= 28) {
                Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                prepareScriptStop = true;
            }

            // Close the bank
            Paint.setStatus("Close bank");
            closeBank();
            Condition.sleep(generateDelay(800, 1200));

            GameTabs.openTab(UITabs.INVENTORY);

        } else {
            Logger.debugLog("Bank not open while in bank logic, skipping!");
        }
    }

    private boolean checkItems() {
        return Inventory.contains(offerItem, 0.75);
    }

    private void withdrawItems(boolean useCache) {
        // Withdraw first item and update our current stacksize
        updatePreviousBankItemCount(1);
        int itemcount = Bank.stackSize(offerItem);
        if (itemcount != -1) {bankItem1Count = itemcount;}
        Bank.withdrawItem(offerItem, useCache, 0.75);
        printItemStateDebug(1, product);

        // Wait for a small bit
        Condition.sleep(generateDelay(125, 250));
    }

    private void closeBank() {
        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 100, 30);

        if (Bank.isOpen()) {
            Bank.close();
        }
    }

    private void printItemStateDebug(int itemNr, String itemName) {
        if (itemNr == 1) {
            Logger.debugLog(itemName + " previously left in bank: " + previousBankItem1Count);
            Logger.debugLog(itemName + " currently left in bank: " + bankItem1Count);
        }
    }

    private void updatePreviousBankItemCount(int updateItem) {
        if (updateItem == 1) {
            if (previousBankItem1Count != -1 & bankItem1Count > 0) {
                previousBankItem1Count = bankItem1Count;
            }
        }
    }
}