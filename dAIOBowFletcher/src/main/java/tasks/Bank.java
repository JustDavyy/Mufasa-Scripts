package tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dAIOBowFletcher.*;

public class Bank extends Task {

    @Override
    public boolean activate() {
        if (method.equals("Cut")) {
            return !checkLogs();
        } else {
            if (product.equals("Shortbow")) {
                return (Inventory.usedSlots() <= 14 || !Inventory.contains(shortbowU, 0.95))
                        && !Inventory.contains(ItemList.BOW_STRING_1777, 0.75);
            } else {
                return (Inventory.usedSlots() <= 14 || !Inventory.contains(longbowU, 0.915))
                        && !Inventory.contains(ItemList.BOW_STRING_1777, 0.75);
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

        if (retrycount >= 2) {
            Logger.log("Not all needed items in the inventory after 2 banking attempts. Assuming we ran out of supplies.");
            Logger.log("Logging out and stopping script!");
            if (Bank.isOpen()) {
                Bank.close();
            }

            Logout.logout();
            Script.stop();
        } else if (retrycount >= 1) {
            Logger.log("Not all needed items in the inventory after " + retrycount + " attempts. Retrying!");
        }

        // Check if we are at the bank
        Bank.open(bankloc);
        Condition.wait(() -> Bank.isOpen(), 100, 25);

        if (method.equals("Cut")) {
            if (bankItemCount <= 28) {
                Logger.debugLog("Bank item count is 28 or below, using non-cached bank withdraw method instead.");
                bankCut(false);
            } else {
                bankCut(true);
            }
        } else {
            if (bankItemCount <= 15) {
                Logger.log("Bank item count is 15 or below, using non-cached bank withdraw method instead.");
                bankString(false);
            } else {
                bankString(true);
            }
        }
        return false;
    }

    private void bankCut(boolean useCache) {
        if (Bank.isOpen()) {

            // Select the right bank tab if needed.
            if (!Bank.isSelectedBankTab(banktab)) {
                Bank.openTab(banktab);
                Logger.log("Opened bank tab " + banktab);
            }

            // Bank processed items
            if (product.equals("Shortbow")) {
                if (Inventory.contains(shortbowU, 0.75)) {
                    Logger.debugLog("Depositing unstrung shortbows.");
                    // Update paintBar
                    processedcount = Inventory.count(shortbowU, 0.75);
                    totalcount = totalcount + processedcount;
                    updatePaintBar(totalcount);
                    Inventory.tapItem(shortbowU, true, 0.75);
                } else {
                    retrycount++;
                }
            } else {
                if (Inventory.contains(longbowU, 0.75)) {
                    Logger.debugLog("Depositing unstrung longbows.");
                    // Update paintBar
                    processedcount = Inventory.count(longbowU, 0.75);
                    totalcount = totalcount + processedcount;
                    updatePaintBar(totalcount);
                    Inventory.tapItem(longbowU, true, 0.75);
                } else {
                    retrycount++;
                }
            }
            Condition.sleep(generateDelay(150, 300));

            // Withdraw new logs
            withdrawLogs(useCache);
            Condition.wait(() -> checkLogs(), 100,30);
            Logger.debugLog("Previous bank items left in bank: " + previousBankItemCount);
            Logger.debugLog("Items left in bank: " + bankItemCount);

            if (bankItemCount <= 29 & bankItemCount != -1) {
                Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                prepareScriptStop = true;
            }

            // Close the bank
            Bank.close();
        }
    }

    private void bankString(boolean useCache) {
        if (Bank.isOpen()) {

            // Select the right bank tab if needed.
            if (!Bank.isSelectedBankTab(banktab)) {
                Bank.openTab(banktab);
                Logger.log("Opened bank tab " + banktab);
            }

            // Bank processed items
            if (product.equals("Shortbow")) {
                if (Inventory.contains(shortbow, 0.75)) {
                    Logger.debugLog("Depositing strung shortbows.");
                    // Update paintBar
                    processedcount = Inventory.count(shortbow, 0.75);
                    totalcount = totalcount + processedcount;
                    updatePaintBar(totalcount);
                    Inventory.tapItem(shortbow, true, 0.75);
                } else {
                    retrycount++;
                }
            } else {
                if (Inventory.contains(longbow, 0.75)) {
                    Logger.debugLog("Depositing strung longbows.");
                    // Update paintBar
                    processedcount = Inventory.count(longbow, 0.75);
                    totalcount = totalcount + processedcount;
                    updatePaintBar(totalcount);
                    Inventory.tapItem(longbow, true, 0.75);
                } else {
                    retrycount++;
                }
            }
            Condition.sleep(generateDelay(150, 300));

            // Withdraw new unstrung bows
            if (product.equals("Shortbow")) {
                Logger.debugLog("Withdrawing unstrung shortbows");
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(Integer.parseInt(shortbowU));
                Bank.withdrawItem(Integer.parseInt(shortbowU), useCache, 0.75);
            } else {
                Logger.debugLog("Withdrawing unstrung longbows");
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(Integer.parseInt(longbowU));
                Bank.withdrawItem(Integer.parseInt(longbowU), useCache, 0.75);
            }

            // Withdraw new bow strings
            bowstringCount = Bank.stackSize(ItemList.BOW_STRING_1777);
            Bank.withdrawItem(ItemList.BOW_STRING_1777, useCache, 0.75);
            Condition.wait(() -> Inventory.contains(ItemList.BOW_STRING_1777, 0.75), 100,30);
            if (bowstringCount < bankItemCount & bowstringCount != -1) {
                updatePreviousBankItemCount();
                bankItemCount = bowstringCount;
            }
            Logger.debugLog("Previous bank items left in bank: " + previousBankItemCount);
            Logger.debugLog("Items left in bank: " + bankItemCount);

            if (bankItemCount <= 15 & bankItemCount != -1) {
                Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                prepareScriptStop = true;
            }

            // Close the bank
            Bank.close();
            Condition.sleep(generateDelay(450, 600));
        }
    }
}