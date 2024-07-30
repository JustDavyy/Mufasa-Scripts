package Tasks;

import utils.StateUpdater;
import utils.Task;

public class EnterGame extends Task {
    private final StateUpdater stateUpdater;

    public EnterGame(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    // Color finder parameters to check the portal color!
    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public boolean execute() {
        return false;
    }
}
