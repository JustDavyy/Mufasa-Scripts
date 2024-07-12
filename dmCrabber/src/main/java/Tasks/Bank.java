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

    String dynamicBank;

    // I'm guessing we should just withdraw full inv of food?
    @Override
    public boolean activate() {
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 100, 10);
        }

        return !Inventory.contains(foodID, 0.80);
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

        // Set up dynamic bank if not already done
        if (dynamicBank == null) {
            dynamicBank = Bank.setupDynamicBank();
        } else {
            Bank.stepToBank(dynamicBank);
        }

        // Open the bank if not already open
        if (!Bank.isOpen()) {
            Bank.open(dynamicBank);
            Condition.wait(() -> Bank.isOpen(), 100, 20);
        }

        // Perform actual banking logic if the bank is open
        if (Bank.isOpen()) {
            // Perform actual banking logic
        }

        return false; // Return false to continue the loop
    }

}