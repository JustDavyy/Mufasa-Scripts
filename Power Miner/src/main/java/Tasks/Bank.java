package Tasks;

import utils.Task;

import static helpers.Interfaces.Inventory;

public class Bank extends Task {
    public boolean activate() {
        if (Inventory.isFull()) {
            return true;
        }
        return false;
    }
    @Override
    public boolean execute() {
        //Execute logic
        return false;
    }
}
