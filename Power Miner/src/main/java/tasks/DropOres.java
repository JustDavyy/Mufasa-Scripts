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
        return Inventory.isFull();
    }
    @Override
    public boolean execute() {
        //Execute logic
        Logger.log("Dropping not supported yet, please run with banking enabled!");
        Script.stop();
        return false;
    }
}
