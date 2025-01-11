package Tasks;

import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class HandleAltars extends Task {

    private final StateUpdater stateUpdater;

    public HandleAltars(StateUpdater stateUpdater) {
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
