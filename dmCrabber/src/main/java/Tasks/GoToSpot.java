package Tasks;

import utils.Task;

import static helpers.Interfaces.Player;
import static helpers.Interfaces.Walker;
import static main.dmCrabber.*;

public class GoToSpot extends Task {
    @Override
    public boolean activate() {
        return !Player.tileEquals(currentLocation, spot.getSpotTile());
    }

    @Override
    public boolean execute() {
        Walker.walkPath(crabRegion, spot.getPathToBank());

        // If within area -> step to tile if we didnt land on it(?)
        return true;
    }
}
