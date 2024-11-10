package tasks;

import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dWinemaker.*;

public class Bank extends Task {
    private boolean doneInitialSetup = false;

    @Override
    public boolean activate() {
        return !Inventory.contains(GRAPES, 0.75);
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Banking");
        Logger.debugLog("Starting bank() method.");

        Bank.open(bankloc);
        Condition.wait(Bank::isOpen, 100, 20);
        Bank.tapDepositInventoryButton();

        if (!doneInitialSetup) {
            initialSetup();
        } else if (Bank.isOpen()) {
            handleBanking();
        }

        Logger.debugLog("Ending the bank() method.");
        return false;
    }

    private void initialSetup() {
        Paint.setStatus("Performing initial setup");
        Logger.debugLog("Starting initialSetup() method.");

        setQuantity(14);
        selectBankTab();
        withdrawItem(JUG_OF_WATER);
        withdrawItem(GRAPES);

        Bank.close();
        Condition.sleep(generateRandomDelay(1000, 1500));
        if (!Inventory.contains(JUG_OF_WATER, 0.75) || !Inventory.contains(GRAPES, 0.75)) {
            Logger.log("No jugs of water/grapes found in inventory. Stopping script.");
            Logout.logout();
            Script.stop();
            return;
        }

        doneInitialSetup = true;
        Logger.debugLog("Initial setup completed.");
    }

    private void handleBanking() {
        if (!Bank.isSelectedBankTab(banktab)) {
            selectBankTab();
        }

        Condition.sleep(generateRandomDelay(100, 250));

        withdrawItem(JUG_OF_WATER);
        withdrawItem(GRAPES);

        Bank.close();
        Condition.sleep(generateRandomDelay(1000, 1500));
    }

    private void setQuantity(int quantity) {
        if (!Bank.isSelectedQuantityCustomButton()) {
            Paint.setStatus("Setting custom Qty to " + quantity);
            Logger.debugLog("Setting custom quantity " + quantity);

            Rectangle customQty = Bank.findQuantityCustomButton();
            Client.longPress(customQty);
            Condition.sleep(generateRandomDelay(300, 500));
            Client.tap(392, 498);
            Condition.sleep(generateRandomDelay(400, 1000));
            Client.sendKeystroke("KEYCODE_" + (quantity / 10));
            Client.sendKeystroke("KEYCODE_" + (quantity % 10));
            Client.sendKeystroke("KEYCODE_ENTER");
            Condition.wait(Bank::isSelectedQuantityCustomButton, 200, 12);
        }
    }

    private void selectBankTab() {
        Paint.setStatus("Selecting bank tab " + banktab);
        Bank.openTab(banktab);
        Logger.debugLog("Opened bank tab " + banktab);
    }

    private void withdrawItem(int item) {
        Bank.withdrawItem(item, 0.75);
        Condition.sleep(generateRandomDelay(200, 350));
        Logger.debugLog("Withdrew " + item + " from the bank.");
    }
}