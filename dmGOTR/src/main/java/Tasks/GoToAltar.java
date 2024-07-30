package Tasks;

import utils.StateUpdater;
import utils.Task;

public class GoToAltar extends Task {

    private final StateUpdater stateUpdater;

    public GoToAltar(StateUpdater stateUpdater) {
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
