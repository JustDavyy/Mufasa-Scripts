package Tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import utils.Task;

import static Tasks.PerformCrabbing.startTime;
import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class Bank extends Task {
    private final Area bankArea = new Area(
            new Tile(748, 864),
            new Tile(761, 874)
    );
    private final Tile bankTile = new Tile(756,867);

    String dynamicBank = "Hosidius_crab_bank";

    // I'm guessing we should just withdraw full inv of food?
    @Override
    public boolean activate() {
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 100, 10);
        }

        return (!Inventory.contains(foodID, 0.80) || outOfPots);
    }

    @Override
    public boolean execute() {
        startTime = 0; // Reset the perform crabbing start time

        // Check if player needs to walk to the bank area
        if (!Player.isTileWithinArea(currentLocation, bankArea)) {
            Walker.walkPath(crabRegion, spot.getPathToBank());
            currentLocation = Walker.getPlayerPosition(crabRegion);
        }

        // Check if player needs to step to the bank tile
        if (!Player.tileEquals(currentLocation, bankTile)) {
            Walker.step(bankTile, crabRegion);
            currentLocation = Walker.getPlayerPosition(crabRegion);
        }

        //If we are IN the bank area.
        if (Player.isTileWithinArea(currentLocation, bankArea)) {
            if (dynamicBank == null) {
                dynamicBank = Bank.setupDynamicBank();
            } else {
                Bank.stepToBank(dynamicBank);
            }

            if (!Bank.isOpen()) {
                Bank.open(dynamicBank);
                Condition.wait(() -> Bank.isOpen(), 100, 20);
            }

            if (Bank.isOpen()) {
                // Deposit everything
                Bank.tapDepositInventoryButton();

                // Go to the right bank tab if needed
                if (!Bank.isSelectedBankTab(selectedBankTab)) {
                    Bank.openTab(selectedBankTab);
                    Condition.wait(() -> Bank.isSelectedBankTab(selectedBankTab), 250, 12);
                    Logger.debugLog("Opened bank tab " + selectedBankTab);
                }

                // If using potions, withdraw these first.
                if (!java.util.Objects.equals(potions, "None")) {
                    if (!Bank.isSelectedQuantity5Button()) {
                        Bank.tapQuantity5Button();
                        Condition.wait(() -> Bank.isSelectedQuantity5Button(), 250, 12);
                    }

                    Bank.withdrawItem(potionID, 0.95);
                    if (!Bank.isSelectedQuantity1Button()) {
                        Bank.tapQuantity1Button();
                        Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
                    }
                    Bank.withdrawItem(potionID, 0.95);
                    Bank.withdrawItem(potionID, 0.95);
                    // This should have withdrawn 7 of the chosen potions.
                }

                // Now fill the rest of the inventory with food
                if (!Bank.isSelectedQuantityAllButton()) {
                    Bank.tapQuantityAllButton();
                    Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 250, 12);
                }
                Bank.withdrawItem(foodID, 0.8);

                // Finally, close the bank.
                Bank.close();
                Condition.sleep(generateRandomDelay( 400, 750));

                if (Bank.isOpen()) {
                    Bank.close();
                }
            }
        }

        outOfPots = false;
        return false; // Return false to continue the loop
    }

}