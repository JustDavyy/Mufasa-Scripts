package tasks;

import helpers.utils.ItemList;
import helpers.utils.MapChunk;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dAIOBowFletcher.*;

public class Setup extends Task {

    @Override
    public boolean activate() {
        return !setupDone;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Initial Setup");

        Paint.setStatus("Open inventory");
        GameTabs.openTab(UITabs.INVENTORY);

        Paint.setStatus("Setup item IDs cache");
        setupItemIDs();

        Paint.setStatus("Find dynamic bank");
        findDynamicBank();

        Paint.setStatus("Open dynamic bank");
        openDynamicBank();

        Paint.setStatus("Setup bank at start");
        if (method.equals("Cut")) {
            setupBankingCUT();
        } else if (method.equals("String")) {
            setupBankingSTRING();
        } else {
            Logger.debugLog("Unknown process method, stopping script.");
            Bank.close();
            Logout.logout();
            Script.stop();
        }

        lastBankTime = System.currentTimeMillis();
        Condition.sleep(generateDelay(450, 600));
        setupDone = true;
        return true;
    }


    private void findDynamicBank() {
        if (bankloc == null) {
            bankloc = Bank.setupDynamicBank();

            if (bankloc == null) {
                Logger.debugLog("Could not find a dynamic bank location we are in, logging out and aborting script.");
                Logout.logout();
                Script.stop();
            } else {
                Logger.log("We're located at: " + bankloc + ".");
            }
        }
    }

    // Specific method for the bow fletcher, as we need a bunch of IDs for this.
    private void setupItemIDs() {
        if (longbow == null) {
            String[] itemIds = itemIDs.get(tier);
            logs = itemIds[0];
            shortbowU = itemIds[1];
            longbowU = itemIds[2];
            shortbow = itemIds[3];
            longbow = itemIds[4];

            Logger.debugLog("Stored IDs for " + tier + ":\nLogs: " + logs + "\nUnstrung Shortbow: " + shortbowU + "\nUnstrung Longbow: " + longbowU + "\nShortbow: " + shortbow + "\nLongbow: " + longbow);
        }
    }

    private void openDynamicBank() {
        Condition.sleep(generateDelay(1750, 2500));
        Bank.open(bankloc);
    }

    private void setupBankingCUT() {
        if (Bank.isOpen()) {
            Bank.tapDepositInventoryButton();
            Condition.sleep(generateDelay(300, 500));

            // Withdrawing a knife from the bank
            Logger.debugLog("Withdrawing a knife from the bank.");
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
            }
            Bank.tapSearchButton();
            Condition.wait(() -> makeMenuOpen(), 100, 30);
            Condition.sleep(generateDelay(450, 600));

            String textToSend = "knife";
            for (char c : textToSend.toCharArray()) {
                String keycode = "KEYCODE_" + Character.toUpperCase(c);
                Client.sendKeystroke(keycode);
                Logger.debugLog("Sent keystroke: " + keycode);
            }

            Condition.sleep(generateDelay(1300, 1700));
            Bank.withdrawItem(ItemList.KNIFE_946, 0.75);
            Condition.wait(() -> Inventory.contains(ItemList.KNIFE_946, 0.75), 100,35);

            // Close search box again
            Client.sendKeystroke("enter");
            Condition.wait(() -> !makeMenuOpen(), 100, 30);

            // Check if we actually have a knife
            if (!Inventory.contains(ItemList.KNIFE_946, 0.75)) {
                Logger.log("No knife found in inventory, stopping the script.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }

            // Set quantity if needed
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
                Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 12);
                Logger.debugLog("Setting quantity to all");
            }

            // Select the correct bank tab if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Logger.log("Opening bank tab " + banktab);
                Bank.openTab(banktab);
                Condition.sleep(generateDelay(1200, 1800));
            }

            // Withdraw the first logs here
            withdrawLogs(false);
            Condition.wait(() -> Inventory.contains(logs, 0.75), 100,30);

            if (Inventory.contains(logs, 0.75)) {
                Logger.debugLog("Withdrew " + tier +  " from the bank.");
                Bank.close();
                Condition.sleep(generateDelay(200, 400));
                if (Bank.isOpen()) {
                    Bank.close();
                }
            }
        } else {
            Logger.debugLog("Bank is not open, cannot set up the bank.");
        }
    }

    private void setupBankingSTRING() {
        if (Bank.isOpen()) {
            Bank.tapDepositInventoryButton();
            Condition.sleep(generateDelay(300, 500));

            Bank.tapQuantityAllButton();
            Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 100, 30);
            Condition.sleep(generateDelay(800, 1000));

            // Set quantity if needed
            if (!Bank.isSelectedQuantityCustomButton()) {
                Rectangle customQty = Bank.findQuantityCustomButton();

                if (customQty != null) {
                    Logger.debugLog("Setting quantity to x14");

                    // Pick a random point within the Rectangle
                    int randomX = customQty.x + (int) (Math.random() * customQty.width);
                    int randomY = customQty.y + (int) (Math.random() * customQty.height);

                    // Perform a longPress at the random point
                    Client.longPress(randomX, randomY);
                    Condition.sleep(generateDelay(350, 600));

                    // Randomize the X offset (-25 to +25) and Y offset (25 to 30 pixels below the longPress)
                    int offsetX = -25 + (int) (Math.random() * 51); // Random value between -25 and +25
                    int offsetY = 25 + (int) (Math.random() * 6);  // Random value between 25 and 30

                    // Calculate the tap point with the randomized offsets
                    Point tapPoint = new Point(randomX + offsetX, randomY + offsetY);
                    Client.tap(tapPoint);

                    Condition.sleep(generateDelay(700, 1200));
                    Client.sendKeystroke("1");
                    Condition.sleep(generateDelay(150, 200));
                    Client.sendKeystroke("4");
                    Condition.sleep(generateDelay(150, 200));
                    Client.sendKeystroke("enter");

                    Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);
                } else {
                    Logger.debugLog("Could not locate the custom quantity button.");
                }
            }

            // Select the correct bank tab if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Logger.log("Opening bank tab " + banktab);
                Bank.openTab(banktab);
                Condition.sleep(generateDelay(1200, 1800));
            }

            // Withdraw the first set of items
            if (product.equals("Shortbow")) {
                Bank.withdrawItem(shortbowU, 0.75);
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(Integer.parseInt(shortbowU));
            } else {
                Bank.withdrawItem(longbowU, 0.75);
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(Integer.parseInt(longbowU));
            }
            Condition.sleep(generateDelay(150, 300));

            Bank.withdrawItem(ItemList.BOW_STRING_1777, 0.75);
            Condition.wait(() -> Inventory.contains(ItemList.BOW_STRING_1777, 0.75), 200, 35);

            Bank.close();
            Condition.sleep(generateDelay(200, 400));
            if (Bank.isOpen()) {
                Bank.close();
            }
        } else {
            Logger.debugLog("Bank is not open, cannot set up the bank.");
        }
    }
}
