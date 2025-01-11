package Tasks;

import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class ProcessEssence extends Task {
    private final StateUpdater stateUpdater;

    public ProcessEssence(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

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
