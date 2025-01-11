package Tasks;

import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class HandlePouches extends Task {
    private final StateUpdater stateUpdater;

    public HandlePouches(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public boolean execute() {
        return false;
    }
}
