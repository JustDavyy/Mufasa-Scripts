package tasks;

import utils.Task;
import static helpers.Interfaces.*;
import static main.dTeaYoinker.*;

public class Bank extends Task {

    @Override
    public boolean activate() {
        if (!bankYN) {
            return false;
        }
        return Inventory.isFull();
    }

    @Override
    public boolean execute() {

        Logger.log("Banking");
        moveToBankBooth();
        handleBanking();

        return false;
    }

    private void moveToBankBooth() {
        int randomIndex = generateRandomDelay(1, 3);

        Logger.debugLog("Walking to a randomly chosen bank booth.");

        switch (randomIndex) {
            case 1:
                if (Walker.isReachable(bankTile1)) {
                    Walker.step(bankTile1);
                } else {
                    Walker.walkTo(middleTile);
                    Player.waitTillNotMoving(7);
                    Walker.step(bankTile1);
                }
                break;
            case 2:
                if (Walker.isReachable(bankTile2)) {
                    Walker.step(bankTile2);
                } else {
                    Walker.walkTo(middleTile);
                    Player.waitTillNotMoving(7);
                    Walker.step(bankTile2);
                }
                break;
            case 3:
                if (Walker.isReachable(bankTile3)) {
                    Walker.step(bankTile3);
                } else {
                    Walker.walkTo(middleTile);
                    Player.waitTillNotMoving(7);
                    Walker.step(bankTile3);
                }
                break;
        }

        // Adding a delay to allow the player to finish walking
        Condition.sleep(750);

        Logger.debugLog("Checking if the player has reached the bank.");
        boolean atBank = false;
        playerPos = Walker.getPlayerPosition();

        // Check if we are at one of the bank tiles
        if (Player.tileEquals(playerPos, bankTile1) || Player.tileEquals(playerPos, bankTile2) || Player.tileEquals(playerPos, bankTile3)) {
            atBank = true;
            Logger.debugLog("Player has reached the bank.");
        }

        if (!atBank) {
            Logger.debugLog("Player did not reach the bank.");
            Logger.debugLog("Attempting to walk to a new randomly chosen bank tile.");
            switch (randomIndex) {
                case 1:
                    Walker.step(bankTile1);
                    break;
                case 2:
                    Walker.step(bankTile2);
                    break;
                case 3:
                    Walker.step(bankTile3);
                    break;
            }
            Condition.sleep(750);
        }
    }

    private void handleBanking() {
        Client.tap(boothArea);
        Condition.wait(() -> Bank.isOpen(), 200, 12);

        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
            Condition.sleep(500);
        }

        Bank.tapDepositInventoryButton();

        closeBankIfOpen();

        Condition.sleep(generateRandomDelay(600, 800));
    }

    private void closeBankIfOpen() {
        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 200, 12);
        Condition.sleep(300);

        if (Bank.isOpen()) {
            Bank.close();
            Condition.sleep(300);
        }
    }
}