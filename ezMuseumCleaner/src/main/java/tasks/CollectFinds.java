package tasks;

import helpers.utils.Tile;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.*;

public class CollectFinds extends Task {
    private final Tile collectTile = new Tile(182, 114);
    private final Rectangle findsPile = new Rectangle(430, 216, 39, 34);

    @Override
    public boolean activate() {
        return !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        Logger.log("Collecting uncleaned finds");
        if (!Player.tileEquals(currentLocation, collectTile)) {
            Walker.step(collectTile, museumRegion);
            return true;
        }

        while (!Inventory.isFull() && Player.tileEquals(currentLocation, collectTile)) {
            Client.tap(findsPile);
            Condition.sleep(generateRandomDelay(200, 500));
        }
        return true;
    }
}
