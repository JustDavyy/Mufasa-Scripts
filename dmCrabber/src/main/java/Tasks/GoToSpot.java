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
        Logger.debugLog("Walking to crab spot.");

        if (!Player.tileEquals(currentLocation, spot.getSpotTile()) && !Walker.isReachable(spot.getSpotTile())) {
            Walker.walkPath(getReversedTiles(spot.getPathToBank()));
            Condition.sleep(generateRandomDelay(2500, 3000));
            currentLocation = Walker.getPlayerPosition();
        }

        if (!Player.tileEquals(currentLocation, spot.getSpotTile())) {
            Walker.step(spot.getSpotTile());
            Condition.sleep(generateRandomDelay(1500, 2250));
            currentLocation = Walker.getPlayerPosition();
            return true;
        }

        return false;
    }
}
