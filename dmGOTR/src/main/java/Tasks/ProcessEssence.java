package Tasks;

import utils.Task;

import static helpers.Interfaces.Inventory;

public class ProcessEssence extends Task {
    @Override
    public boolean activate() {
        // Inventory is full && we have essence?
        return Inventory.isFull();
    }

    @Override
    public boolean execute() {
        return false;
    }
}
