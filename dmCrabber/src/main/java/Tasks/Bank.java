package Tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class Bank extends Task {
    private final Area bankArea = new Area(
            new Tile(748, 864),
            new Tile(761, 874)
    );
    private final Tile bankTile = new Tile(756,867);

    // I'm guessing we should just withdraw full inv of food?
    @Override
    public boolean activate() {
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 100, 10);
        }

        return !Inventory.contains(foodID, 0.80);
    }

    @Override
    public boolean execute() {
        if (!Player.isTileWithinArea(currentLocation, bankArea)) {
            Walker.walkPath(crabRegion, spot.getPathToBank());
            currentLocation = Walker.getPlayerPosition(crabRegion);
        }

        if (!Player.tileEquals(currentLocation, bankTile) && Player.isTileWithinArea(currentLocation, bankArea)) {
            Walker.step(bankTile, crabRegion);
            currentLocation = Walker.getPlayerPosition(crabRegion);
        }


        return false;
    }
}