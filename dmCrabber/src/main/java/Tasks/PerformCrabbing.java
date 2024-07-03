package Tasks;

import utils.Task;

import static helpers.Interfaces.Player;
import static helpers.Interfaces.Walker;
import static main.dmCrabber.*;

public class PerformCrabbing extends Task {
    @Override
    public boolean activate() {
        return Player.tileEquals(currentLocation, spot.getSpotTile());
    }

    @Override
    public boolean execute() {
        // We need some way for a timer here, and also anti afk in-between? And a method to reset?
        return false;
    }
}
