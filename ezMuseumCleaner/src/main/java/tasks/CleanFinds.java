package tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.*;

public class CleanFinds extends Task {
    private final Rectangle cleaningTable = new Rectangle(376, 301, 80, 33);

    @Override
    public boolean activate() {
        return Inventory.isFull() && hasFinds;
    }

    @Override
    public boolean execute() {
        Logger.log("Cleaning finds");
        if (!Player.tileEquals(currentLocation, cleanTile)) {
            Walker.step(cleanTile, museumRegion);
            currentLocation = cleanTile;
        }

        if (Player.tileEquals(currentLocation, cleanTile)) {
            Client.tap(cleaningTable);
            Condition.wait(() -> !Inventory.contains(ItemList.UNCLEANED_FIND_11175, 0.80), 1000, 90);
            shouldDrop = true;
            return true;
        }

        return false;
    }
}
