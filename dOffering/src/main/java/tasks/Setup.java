package tasks;

import helpers.annotations.AllowedValue;
import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dOffering.*;

public class Setup extends Task {

    @Override
    public boolean activate() {
        return !setupDone;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Initial Setup");
        Logger.log("Initial Setup");

        Paint.setStatus("Open inventory");
        GameTabs.openTab(UITabs.INVENTORY);

        Paint.setStatus("Find dynamic bank");
        findDynamicBank();

        Paint.setStatus("Open dynamic bank");
        openDynamicBank();

        if (Bank.isOpen()) {
            Paint.setStatus("Deposit inventory");
            Bank.tapDepositInventoryButton();

            Paint.setStatus("Withdraw runes");
            withdrawRunes();

            Paint.setStatus("Setup bank at start");
            setupBanking();

            Paint.setStatus("Withdraw first offer items");
            withdrawFirstItems();

            Paint.setStatus("Close bank");
            closeBank();

            setupDone = true;
        } else {
            setupDone = false;
        }

        GameTabs.openTab(UITabs.INVENTORY);
        Condition.sleep(generateDelay(800, 1200));

        return false;
    }

    private void withdrawFirstItems() {
        // Withdraw first item
        bankItem1Count = Bank.stackSize(offerItem);
        Bank.withdrawItem(offerItem, 0.75);

        // Wait for a small bit
        Condition.sleep(generateDelay(125, 250));
    }

    private void withdrawRunes() {
        if (runeStorage.equals("Rune Pouch")) {
            withdrawPouch(ItemList.RUNE_POUCH_12791, "Rune pouch", null);
        } else if (runeStorage.equals("Divine Rune Pouch")) {
            withdrawPouch(ItemList.DIVINE_RUNE_POUCH_27281, "Rune pouch", null);
        } else {
            Logger.debugLog("Not using any rune pouch, withdrawing runes!");
            Bank.tapQuantityAllButton();

            // Select the correct bank tab if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Logger.log("Opening bank tab " + banktab);
                Bank.openTab(banktab);
                Condition.sleep(generateDelay(1200, 1800));
            }

            if (product.endsWith("ashes")) {
                Logger.debugLog("Using demonic offering, need: soul and wrath");
                Bank.withdrawItem(ItemList.WRATH_RUNE_21880, 0.75);

                // Wait for a small bit
                Condition.sleep(generateDelay(125, 250));

                Bank.withdrawItem(ItemList.SOUL_RUNE_566, 0.75);
            } else {
                Logger.debugLog("Using sinister offering, need: blood and wrath");
                Bank.withdrawItem(ItemList.WRATH_RUNE_21880, 0.75);

                // Wait for a small bit
                Condition.sleep(generateDelay(125, 250));

                Bank.withdrawItem(ItemList.BLOOD_RUNE_565, 0.75);
                Condition.sleep(generateDelay(125, 250));
            }
        }

        // Wait for a small bit
        Condition.sleep(generateDelay(125, 250));
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

    private void openDynamicBank() {
        Condition.sleep(generateDelay(1750, 2500));
        Bank.open(bankloc);
    }

    private void withdrawPouch(int itemId, String searchString, Color searchColor) {
        Logger.debugLog("Withdrawing pouch " + itemId + " from the bank.");

        Paint.setStatus("Set quantity 1");
        Logger.debugLog("Set quantity 1");
        // Select quantity 1 first
        if (!Bank.isSelectedQuantity1Button()) {
            Bank.tapQuantity1Button();
            Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
        }

        Paint.setStatus("Enter search mode");
        Logger.debugLog("Entering bank search mode");
        // Enter search mode
        Bank.tapSearchButton();
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Condition.sleep(generateDelay(450, 600));

        // Type our search string
        Paint.setStatus("Type " + searchString);
        Logger.debugLog("Typing: " + searchString);

        for (char c : searchString.toCharArray()) {
            if (c == ' ') {
                Client.sendKeystroke("space"); // Handle spaces
            } else {
                Client.sendKeystroke(String.valueOf(Character.toUpperCase(c))); // Send uppercase characters
            }
        }

        // Wait for a bit for results to be visible
        Condition.sleep(generateDelay(1300, 1700));

        Paint.setStatus("Withdraw pouch");
        Logger.debugLog("Withdrawing pouch");
        // Withdraw our item
        if (searchColor != null) {
            Bank.withdrawItem(itemId, 0.4, searchColor);
        } else {
            Bank.withdrawItem(itemId, 0.4);
        }
        Condition.wait(() -> Inventory.contains(itemId, 0.4), 100, 35);

        // Close searchbox again
        Paint.setStatus("Close search box");
        Logger.debugLog("Closing search box");
        Client.sendKeystroke("enter");
        Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 30);

        // Check if we actually have the pouch
        if (!Inventory.contains(itemId, 0.40)) {
            Logger.log("No pouch (" + itemId + ") found in inventory, stopping the script.");
            Bank.close();
            if (Bank.isOpen()) {
                Bank.close();
            }
            Logout.logout();
            Script.stop();
        }

        Condition.sleep(generateDelay(1500, 2000));
    }

    private void setupBanking() {
        // Set quantity
        if (!runeStorage.equals("Inventory")) {
            Bank.tapQuantityAllButton();
            Condition.sleep(generateDelay(800, 1200));
        } else {
            setCustomQty("24");
        }

        // Select the correct bank tab if needed
        if (!Bank.isSelectedBankTab(banktab)) {
            Logger.log("Opening bank tab " + banktab);
            Bank.openTab(banktab);
            Condition.sleep(generateDelay(1200, 1800));
        }
    }

    private void setCustomQty(String quantity) {
        // Set a different quantity by default randomly
        int choice = random.nextInt(4);
        switch (choice) {
            case 0:
                Bank.tapQuantity1Button();
                break;
            case 1:
                Bank.tapQuantity5Button();
                break;
            case 2:
                Bank.tapQuantity10Button();
                break;
            case 3:
                Bank.tapQuantityAllButton();
                break;
        }
        Condition.sleep(generateDelay(750, 1000));

        // Set custom quantity
        Rectangle customQtyBtn = Bank.findQuantityCustomButton();
        if (customQtyBtn != null) {
            Paint.setStatus("Set custom quantity " + quantity);
            Logger.debugLog("Setting quantity to x" + quantity);

            // Pick a random point within the Rectangle
            int randomX = customQtyBtn.x + (int) (Math.random() * customQtyBtn.width);
            int randomY = customQtyBtn.y + (int) (Math.random() * customQtyBtn.height);

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
            // Type our quantity given here
            for (char c : quantity.toCharArray()) {
                String keycode;
                if (c == ' ') {
                    keycode = "space";
                } else {
                    keycode = String.valueOf(c);
                }
                Client.sendKeystroke(keycode);
                Condition.sleep(generateDelay(20, 40));
            }
            Client.sendKeystroke("enter");

            Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);
        } else {
            Logger.debugLog("Could not locate the custom quantity button.");
        }
    }

    private void closeBank() {
        // Close the bank
        Bank.close();
        Condition.sleep(generateDelay(1000, 1500));

        if (Bank.isOpen()) {
            Bank.close();
        }
    }
}