package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.*;

public class CleanFinds extends Task {
    private final Rectangle cleaningTable = new Rectangle(427, 290, 26, 15);
    private final Rectangle instantTap = new Rectangle(398, 312, 32, 14);
    @Override
    public boolean activate() {
        return Inventory.isFull() && hasFinds;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Cleaning finds");
        Logger.log("Cleaning finds");

        if (Player.tileEquals(currentLocation, collectTile) || Player.tileEquals(currentLocation, cleanTile)) {
            boolean atCollectTile = Player.tileEquals(currentLocation, collectTile);
            Client.tap(atCollectTile ? instantTap : cleaningTable);
            Condition.wait(() -> !Inventory.contains(ItemList.UNCLEANED_FIND_11175, 0.80) || !checkIfPlayersAround() || Script.isTimeForBreak(), 300, 300);

            if (count(depositItemsList) >= 16) {
                Logger.log("We have enough to deposit, depositing!");
                shouldDeposit = true;
                return true;
            }

            shouldDrop = true;
            return true;
        }

        if (!Player.tileEquals(currentLocation, cleanTile)) {
            Logger.debugLog("stepping to cleaning bench");
            Walker.step(cleanTile, museumRegion);
            currentLocation = cleanTile;
        }

        return false;
    }
}
