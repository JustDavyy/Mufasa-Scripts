package Tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class GoToSpot extends Task {
    @Override
    public boolean activate() {
        return !Player.tileEquals(currentLocation, spot.getSpotTile()) && Inventory.contains(foodID, 0.80);
    }

    @Override
    public boolean execute() {
        if (!Player.tileEquals(currentLocation, spot.getSpotTile())) {
            Walker.walkPath(crabRegion, getReversedTiles(spot.getPathToBank()));
            currentLocation = Walker.getPlayerPosition(crabRegion);

            if (!Player.tileEquals(currentLocation, spot.getSpotTile())) {
                Walker.step(spot.getSpotTile(), crabRegion);
                currentLocation = Walker.getPlayerPosition(crabRegion);
            }
            return true;
        }
        return false;
    }
}
