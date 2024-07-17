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
        Paint.setStatus("Dropping items");
        Logger.log("We should drop items..");
        if (!Game.isTapToDropEnabled()) {
            Logger.log("Enabling tap to drop");
            Game.enableTapToDrop();
            Condition.wait(() -> Game.isTapToDropEnabled(), 200, 20);
            return true;
        } else {
            Logger.debugLog("Stepping to collect tile while dropping");
            Walker.step(collectTile, this::dropItems);
        }
        return false;
    }

    private void dropItems() {
        Logger.log("Dropping items..");
        for (int id : dropList) {
            if (Inventory.contains(id, 0.80)) {
                Inventory.tapAllItems(id, 0.80);
                Condition.sleep(generateRandomDelay(100, 300));
                shouldDrop = false;
            }
        }
    }
}
