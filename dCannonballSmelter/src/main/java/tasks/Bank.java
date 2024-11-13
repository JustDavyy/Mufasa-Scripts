package tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dCannonballSmelter.*;

public class Bank extends Task {

    @Override
    public boolean activate() {
        return !Inventory.contains(ItemList.STEEL_BAR_2353, 0.8);
    }

    @Override
    public boolean execute() {

        Paint.setStatus("Bank");
        Logger.log("Banking.");

        if (!Bank.isOpen()) {
            openBank();
        }

        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
        }

        if (!Bank.isSelectedQuantityAllButton()) {
            Paint.setStatus("Set quantity all");
            Bank.tapQuantityAllButton();
        }

        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Open tab " + banktab);
            Bank.openTab(banktab);
            Condition.wait(() -> Bank.isSelectedBankTab(banktab), 100, 30);
            Condition.sleep(200);
        }

        Paint.setStatus("Withdraw steel bar");
        Bank.withdrawItem(ItemList.STEEL_BAR_2353, 0.8);
        Condition.wait(() -> Inventory.contains(ItemList.STEEL_BAR_2353, 0.8), 200, 30);

        Paint.setStatus("Close bank");
        Bank.close();
        Condition.wait(() -> !Bank.isOpen(),250,20);
        Condition.sleep(generateDelay(250, 400));

        return false;
    }


    public void openBank() {
        switch (location) {
            case "Edgeville":
                if (Player.tileEquals(currentLocation, edgeFurnaceTile)) {
                    Client.tap(edgeBankRect);
                    currentLocation = edgeBankTile;
                } else if (Player.tileEquals(currentLocation, edgeBankTile)) {
                    Client.tap(openEdgeBankONCE);
                    currentLocation = edgeBankTile;
                } else {
                    if (!Walker.isReachable(edgeBankTile)) {
                        Walker.webWalk(edgeBankTile);
                    }
                    Walker.step(edgeBankTile);
                    Client.tap(openEdgeBankONCE);
                    currentLocation = edgeBankTile;
                }
                Condition.wait(() -> Bank.isOpen(), 100, 175);
                break;
            case "Mount Karuulm":
                if (Player.tileEquals(currentLocation, karuulmFurnaceTile)) {
                    Walker.step(karuulmBankTile);
                    Client.tap(karuulmBankRect);
                    currentLocation = karuulmBankTile;
                } else if (Player.tileEquals(currentLocation, karuulmBankTile)) {
                    Client.tap(karuulmBankRect);
                    currentLocation = karuulmBankTile;
                } else {
                    if (!Walker.isReachable(karuulmBankTile)) {
                        Walker.webWalk(karuulmBankTile);
                    }
                    Walker.step(karuulmBankTile);
                    Client.tap(karuulmBankRect);
                    currentLocation = karuulmBankTile;
                }
                Condition.wait(() -> Bank.isOpen(), 100, 175);
                break;
            case "Neitiznot":
                if (Player.tileEquals(currentLocation, neitFurnaceTile)) {
                    Client.tap(neitBankRect);
                    currentLocation = neitBankTile;
                } else if (Player.tileEquals(currentLocation, neitBankTile)) {
                    Client.tap(openNeitBankONCE);
                    currentLocation = neitBankTile;
                } else {
                    if (!Walker.isReachable(neitBankTile)) {
                        Walker.webWalk(neitBankTile);
                    }
                    Walker.step(neitBankTile);
                    Client.tap(openNeitBankONCE);
                    currentLocation = neitBankTile;
                }
                Condition.wait(() -> Bank.isOpen(), 100, 175);
                break;
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
        }
    }
}