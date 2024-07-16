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
        } else {
            Walker.step(cleanTile, museumRegion, () -> {
                for (int id : dropList) {
                    if (Inventory.contains(id, 0.80)) {
                        Inventory.tapItem(id, 0.80);
                        Condition.sleep(generateRandomDelay(100, 300));
                    }
                }
            });

            if (!Inventory.containsAll(dropList, 0.80)) {
                shouldDrop = false;
            }
        }
        return false;
    }
}
