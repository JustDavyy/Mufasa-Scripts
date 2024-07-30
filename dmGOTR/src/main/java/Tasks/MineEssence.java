package Tasks;

import utils.Task;

import static helpers.Interfaces.Inventory;

public class MineEssence extends Task {
    @Override
    public boolean activate() {
        return !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        return false;
    }
}
