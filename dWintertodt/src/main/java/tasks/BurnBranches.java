package tasks;

import helpers.utils.Tile;
import utils.Task;

public class BurnBranches extends Task {
    Tile safespotTileLeft = new Tile(1,1);
    Tile safespotTileRight = new Tile(1,1);

    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public boolean execute() {
        return false;
    }
}
