package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.AIOMiner.bankOres;

public class DropOres  extends Task {
    public boolean activate() {
        // Early exit if banking is enabled!
        if (bankOres) {
            return false;
        }

        Logger.debugLog("Checking if we should be dropping ores");
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        if (GameTabs.isInventoryTabOpen()) {
            return Inventory.isFull();
        }
        return false;
    }
    @Override
    public boolean execute() {
        //Execute logic
        Logger.log("Dropping not supported yet, please run with banking enabled!");
        Script.stop();
        return false;
    }
}
