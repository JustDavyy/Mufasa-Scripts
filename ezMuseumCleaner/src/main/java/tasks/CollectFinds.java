package tasks;

import helpers.utils.Tile;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.*;

public class CollectFinds extends Task {
    private final Rectangle findsPile = new Rectangle(430, 216, 39, 34);

    @Override
    public boolean activate() {
        return !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Collecting finds");
        Logger.log("Collecting uncleaned finds");
        if (!Player.tileEquals(currentLocation, collectTile)) {
            Logger.debugLog("Stepping to collect pile");
            Walker.step(collectTile, museumRegion);
            currentLocation = collectTile;
        }

        while (!Inventory.isFull() && Player.tileEquals(currentLocation, collectTile)) {
            Logger.debugLog("Collecting finds!");
            Client.tap(findsPile);
            Condition.sleep(generateRandomDelay(200, 500));
        }
        return true;
    }
}
