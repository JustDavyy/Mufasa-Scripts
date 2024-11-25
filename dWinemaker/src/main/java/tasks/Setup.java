package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dWinemaker.*;

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

            Paint.setStatus("Setup bank at start");
            setupBanking();

            Paint.setStatus("Withdraw first items");
            withdrawFirstItems();

            Paint.setStatus("Close bank");
            closeBank();

            setupDone = true;
        } else {
            setupDone = false;
        }

        return false;
    }




    private void withdrawFirstItems() {
        // Withdraw first item
        bankItem1Count = Bank.stackSize(ItemList.GRAPES_1987);
        Bank.withdrawItem(ItemList.GRAPES_1987, 0.75);

        // Wait for a small bit
        Condition.sleep(generateDelay(125, 250));

        // Withdraw second item
        bankItem2Count = Bank.stackSize(ItemList.JUG_OF_WATER_1937);
        Bank.withdrawItem(ItemList.JUG_OF_WATER_1937, 0.7, Color.decode("#6769c8"));
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

    private void setupBanking() {
        // Set custom quantity
        setCustomQty("14");

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