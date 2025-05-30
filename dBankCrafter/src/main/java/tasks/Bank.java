package tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dBankCrafter.*;

public class Bank extends Task {

    @Override
    public boolean activate() {
        if ("StaffCrafting".equals(activity)) {
            return !Inventory.contains(sourceItem, 0.75) && !Inventory.contains(ItemList.BATTLESTAFF_1391, 0.75);
        } else {
            if ("HideCrafting".equals(activity) && product.endsWith("body")) {
                return Inventory.count(sourceItem, 0.75) <= 2;
            } else {
                return !Inventory.contains(sourceItem, 0.75);
            }
        }
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

        if (retrycount >= 4) {
            Logger.log("Not all needed items in the inventory after 4 banking attempts. Assuming we ran out of supplies.");
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
        Condition.sleep(generateDelay(300, 400));

        if ("StaffCrafting".equals(activity)) {
            if (bankItem1Count <= 15  && bankItem1Count != -1) {
                Logger.debugLog("Bank item count is 15 or below, using non-cached bank withdraw method instead.");
                performBank(false);
            } else {
                performBank(true);
            }
        } else {
            if (bankItem1Count <= 28  && bankItem1Count != -1) {
                Logger.debugLog("Bank item count is 28 or below, using non-cached bank withdraw method instead.");
                performBank(false);
            } else {
                performBank(true);
            }
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
            if (Inventory.contains(targetItem, 0.75)) {
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

            if ("StaffCrafting".equals(activity)) {
                if (bankItem1Count <= 15 && bankItem1Count != -1) {
                    Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                    prepareScriptStop = true;
                }
            } else {
                if (bankItem1Count <= 28 && bankItem1Count != -1) {
                    Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                    prepareScriptStop = true;
                }
            }

            // Close the bank
            Paint.setStatus("Close bank");
            closeBank();

        } else {
            Logger.debugLog("Bank not open while in bank logic, skipping!");
        }
    }

    private boolean checkItems() {
        return Inventory.contains(sourceItem, 0.75);
    }

    private void depositItems() {
        Inventory.tapItem(targetItem, 0.75);
        if (activity.equals("Gemcutting")) {
            if (product.equals("Uncut opal") || product.equals("Uncut jade") || product.equals("Uncut red topaz")) {
                if (Inventory.contains(ItemList.CRUSHED_GEM_1633, 0.75)) {
                    Condition.sleep(generateDelay(150, 300));
                    Inventory.tapItem(ItemList.CRUSHED_GEM_1633, 0.75);
                }
            }
        }
        Condition.sleep(generateDelay(150, 300));
    }

    private void withdrawItems(boolean useCache) {
        // Withdraw first item and update our current stacksize
        updatePreviousBankItemCount(1);
        if ("StaffCrafting".equals(activity)) {
            bankItem1Count = Bank.stackSize(sourceItem);
            Bank.withdrawItem(sourceItem, useCache, 0.75);
            Condition.sleep(generateDelay(150, 300));
            tempBankCountHolder = Bank.stackSize(ItemList.BATTLESTAFF_1391);
            if (tempBankCountHolder != 0 && tempBankCountHolder != -1 && tempBankCountHolder < bankItem1Count) {
                bankItem1Count = tempBankCountHolder;
            }
            Bank.withdrawItem(ItemList.BATTLESTAFF_1391, 0.75);
            printItemStateDebug(1);
        } else {
            bankItem1Count = Bank.stackSize(sourceItem);
            Bank.withdrawItem(sourceItem, useCache, 0.75);
            printItemStateDebug(1);
        }

        // Wait for a small bit
        Condition.sleep(generateDelay(125, 250));
    }

    private void closeBank() {
        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 100, 30);
        Condition.sleep(generateDelay(400, 600));

        if (Bank.isOpen()) {
            Bank.close();
            Condition.wait(() -> !Bank.isOpen(), 100, 30);
            Condition.sleep(generateDelay(400, 600));
        }
    }

    private void updateProcessedItems() {
        if ("AmethystCutting".equals(activity)) {
            // Update our process count
            PROCESS_COUNT = PROCESS_COUNT + Inventory.stackSize(targetItem);
        } else {
            // Update our process count
            PROCESS_COUNT = PROCESS_COUNT + Inventory.count(targetItem, 0.85);
        }

        // Update the paint bar with this new count
        updatePaintBar(PROCESS_COUNT);
    }

    private void printItemStateDebug(int itemNr) {
        if (itemNr == 1) {
            Logger.debugLog(getItemName() + " previously left in bank: " + previousBankItem1Count);
            Logger.debugLog(getItemName() + " currently left in bank: " + bankItem1Count);
        }
    }

    private String getItemName() {
        switch (activity) {
            case "Glassblowing":
                return "Molten glass";
            case "Gemcutting":
            case "AmethystCutting":
            case "HideCrafting":
                return product;
            case "StaffCrafting":
                return product.replace(" battlestaff", " orb/battlestaves");
            default:
                Logger.debugLog("Unknown activity (inside getItemName): " + activity + " aborting script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(generateDelay(1200, 1800));
                }
                Logout.logout();
                Script.stop();
                return "Invalid item";
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