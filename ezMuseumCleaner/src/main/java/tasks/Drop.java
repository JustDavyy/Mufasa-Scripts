package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.*;

public class Drop extends Task {

    @Override
    public boolean activate() {
        return shouldDrop;
    }

    @Override
    public boolean execute() {
        if (!Game.isTapToDropEnabled()) {
            Game.enableTapToDrop();
            Condition.wait(() -> Game.isTapToDropEnabled(), 200, 20);
            return true;
        } else {
            if (Player.tileEquals(currentLocation, depositTile)) {
                Walker.step(cleanTile, museumRegion, () -> {
                    dropItems();
                    if (!Inventory.containsAny(dropList, 0.80)) {
                        shouldDrop = false;
                    }
                });
            } else {
                dropItems();
            }
        }
        return false;
    }

    private void dropItems() {
        for (int id : dropList) {
            if (Inventory.contains(id, 0.80)) {
                Inventory.tapAllItems(id, 0.80);
                Condition.sleep(generateRandomDelay(100, 300));
                shouldDrop = false;
            }
        }
    }
}
