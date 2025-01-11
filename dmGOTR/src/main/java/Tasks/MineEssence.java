package Tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class MineEssence extends Task {
    private final StateUpdater stateUpdater;
    public MineEssence(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    private static Area LARGE_REMAINS_MINING_AREA = new Area(
            new Tile(14544, 37805, 0),
            new Tile(14590, 37697, 0)
    );
    private static Area HUGE_REMAINS_MINING_AREA = new Area(
            new Tile(14337, 37809, 0),
            new Tile(14376, 37708, 0)
    );

    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public boolean execute() {
        return false;
    }
}
