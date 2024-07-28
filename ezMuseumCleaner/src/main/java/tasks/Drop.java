package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.*;

public class Drop extends Task {
    boolean checkedTapToDrop = false;

    @Override
    public boolean activate() {
        return shouldDrop;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Dropping items");
        Logger.log("We should drop items..");
        if (!checkedTapToDrop) {
            if (!Game.isTapToDropEnabled()) {
                Logger.log("Enabling tap to drop");
                if (Chatbox.findChatboxMenu() != null) {
                    Client.sendKeystroke("KEYCODE_SPACE");
                    Condition.wait(() -> Chatbox.findChatboxMenu() == null, 100, 30);
                }

                Game.enableTapToDrop();
                Condition.wait(() -> Game.isTapToDropEnabled(), 200, 20);
                return true;
            }
            checkedTapToDrop = true;
        } else {
            Logger.debugLog("Stepping to collect tile while dropping");
            Walker.step(collectTile, this::dropItems);
            return true;
        }
        return false;
    }

    private void dropItems() {
        Logger.log("Dropping items..");
        for (int id : dropList) {
            Inventory.tapAllItems(id, 0.80);
            shouldDrop = false;
        }
    }
}
