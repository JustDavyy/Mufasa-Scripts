package tasks;

import utils.Task;
import static helpers.Interfaces.*;
import static main.dCakeThiever.*;

public class Bank extends Task {

    @Override
    public boolean activate() {
        return Player.isTileWithinArea(currentLocation, bankArea);
    }

    @Override
    public boolean execute() {
        bank();
        return false;
    }

    private void bank() {
        Logger.debugLog("Banking...");
        moveToBankBooth();

        if (Player.tileEquals(currentLocation, bankBoothTile)) {
            interactWithBank();
        }
    }

    private void moveToBankBooth() {
        if (!Player.tileEquals(currentLocation, bankBoothTile)) {
            Walker.step(bankBoothTile);
            Condition.wait(() -> Player.atTile(bankBoothTile), 250, 20);
            currentLocation = bankBoothTile;
        }
    }

    private void interactWithBank() {
        Client.tap(bankBooth);
        Condition.wait(() -> Bank.isOpen(), 250, 30);

        if (Bank.isOpen()) {
            handleBanking();
        }
    }

    private void handleBanking() {
        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
            Condition.sleep(750);
        }

        depositInventory();
        closeBankIfOpen();
    }

    private void depositInventory() {
        Bank.tapDepositInventoryButton();
        Bank.tapDepositInventoryButton();
        Condition.sleep(500);
    }

    private void closeBankIfOpen() {
        Bank.close();
        Condition.sleep(500);

        if (Bank.isOpen()) {
            Bank.close();
            Condition.sleep(300);
        }
    }
}