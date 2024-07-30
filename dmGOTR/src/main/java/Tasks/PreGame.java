package Tasks;

import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.Logger;

public class PreGame extends Task {

    private final StateUpdater stateUpdater;

    public PreGame(StateUpdater stateUpdater) {
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