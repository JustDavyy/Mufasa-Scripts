package dAnglerFisher.Tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import dAnglerFisher.Task;
import dAnglerFisher.dAnglerFisher;

import java.time.Instant;

import static helpers.Interfaces.*;

public class Bank extends Task {
    dAnglerFisher main;
    Tile[] pathToBank = new Tile[]{
            new Tile(902, 453),
            new Tile(893, 451),
            new Tile(885, 451),
            new Tile(874, 447),
            new Tile(871, 438)
    };

    public Bank(dAnglerFisher main) {
        super();
        super.name = "Bank";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return (Inventory.isFull());
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        Paint.setStatus("Update paint");
        GameTabs.openInventoryTab();
        updatePaint();
        Paint.setStatus("Moving to bank");
        moveToBank();
        Paint.setStatus("Banking");
        bank();
        Paint.setStatus("Move to fishing spots");
        moveToSpots();
        dAnglerFisher.anglerInventCount = 0;
        dAnglerFisher.lastXpGainTime = Instant.now().minusSeconds(90);
        return false;
    }

    private void bank() {
        if (!Player.atTile(dAnglerFisher.bankTile)) {
            Walker.step(dAnglerFisher.bankTile);
        }

        Client.tap(dAnglerFisher.bankClickRect);
        Condition.wait(() -> Bank.isOpen(), 250, 20);

        if (Bank.isOpen()) {
            // bank is open
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
                Condition.sleep(dAnglerFisher.generateRandomDelay(750, 1000));
            }
            Inventory.tapItem(ItemList.RAW_ANGLERFISH_13439, 0.8);

            if (Inventory.contains(ItemList.CLUE_BOTTLE_EASY_13648, 0.8) || Inventory.contains(ItemList.CLUE_BOTTLE_MEDIUM_13649, 0.8) || Inventory.contains(ItemList.CLUE_BOTTLE_HARD_13650, 0.8) || Inventory.contains(ItemList.CLUE_BOTTLE_ELITE_13651, 0.8)) {
                Inventory.tapItem(ItemList.CLUE_BOTTLE_EASY_13648, 0.8);
                Inventory.tapItem(ItemList.CLUE_BOTTLE_MEDIUM_13649, 0.8);
                Inventory.tapItem(ItemList.CLUE_BOTTLE_HARD_13650, 0.8);
                Inventory.tapItem(ItemList.CLUE_BOTTLE_ELITE_13651, 0.8);
            }

            Condition.sleep(dAnglerFisher.generateRandomDelay(750, 1000));

            Bank.close();
            Condition.wait(() -> !Bank.isOpen(), 250, 20);

            if (Bank.isOpen()) {
                Bank.close();
            }
        }
    }

    private void moveToBank() {
        Walker.walkPath(pathToBank);
        Condition.sleep(dAnglerFisher.generateRandomDelay(2000, 3000));
        Walker.step(dAnglerFisher.bankTile);
    }

    private void moveToSpots() {
        Walker.walkPath(dAnglerFisher.pathToSpots);
        Condition.sleep(dAnglerFisher.generateRandomDelay(2000, 3000));
    }

    private void updatePaint() {
        // Current count of anglerfish in inventory
        int currentCount = Inventory.count(ItemList.RAW_ANGLERFISH_13439, 0.8);

        // Calculate the difference between the current count and the last recorded count
        int difference = currentCount - dAnglerFisher.anglerInventCount;

        // Only update if there is a positive difference
        if (difference > 0) {
            Logger.debugLog("Difference in Anglerfish count: " + difference);

            // Update the last count to the new current count
            dAnglerFisher.anglerInventCount = currentCount;
            dAnglerFisher.anglerAmount += difference;
            dAnglerFisher.profitAmount = dAnglerFisher.anglerAmount * dAnglerFisher.anglerPrice;
            Paint.updateBox(dAnglerFisher.anglerIndex, dAnglerFisher.anglerAmount);
            Paint.updateBox(dAnglerFisher.profitIndex, dAnglerFisher.profitAmount);
        } else {
            // Optionally log that there was no update necessary
            Logger.debugLog("No update needed: difference is " + difference);
        }
    }
}
