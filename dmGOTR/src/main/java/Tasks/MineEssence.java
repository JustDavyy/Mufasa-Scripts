package Tasks;

import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.Inventory;

public class MineEssence extends Task {
    private final StateUpdater stateUpdater;

    public MineEssence(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    @Override
    public boolean activate() {
        return !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        return false;
    }
}
