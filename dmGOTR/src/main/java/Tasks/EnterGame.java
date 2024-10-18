package Tasks;

import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.Player;
import static helpers.Interfaces.Walker;
import static main.dmGOTR.currentLocation;
import static main.dmGOTR.gameLobby;

public class EnterGame extends Task {
    private final StateUpdater stateUpdater;

    public EnterGame(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    // Color finder parameters to check the portal color!
    @Override
    public boolean activate() {
        return Player.isTileWithinArea(currentLocation, gameLobby);
    }

    @Override
    public boolean execute() {
        return false;
    }
}
