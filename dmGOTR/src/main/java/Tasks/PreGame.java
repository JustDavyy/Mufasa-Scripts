package Tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.StateUpdater;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class PreGame extends Task {

    private final StateUpdater stateUpdater;

    public PreGame(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    // AREAS
    private static Area OUTSIDE_AREA= new Area(
            new Tile(14381, 37677, 0),
            new Tile(14539, 37569, 0)
    );

    // TILES
    private static Tile OUTSIDE_BARRIER_TILE = new Tile(14459, 37677, 0);
    private static Tile INSIDE_BARRIER_TILE = new Tile(14459, 37685, 0);

    // RECTANGLES
    private static Rectangle BARRIER_TAP_RECT = new Rectangle(434, 234, 25, 18);
    private static Rectangle UNCHARGED_CELL_FROMBARRIER_TAP_RECT = new Rectangle(492, 201, 16, 10);

    @Override
    public boolean activate() {
        return !stateUpdater.isGameGoing();
    }

    @Override
    public boolean execute() {
        setStatusAndDebugLog("Start PreGame");

        // Logic if we are outside the game area
        if (Player.isTileWithinArea(currentLocation, OUTSIDE_AREA)) {
            setStatusAndDebugLog("Enter GOTR");

            // Go to the outside barrier
            if (Walker.isReachable(OUTSIDE_BARRIER_TILE)) {
                Walker.step(OUTSIDE_BARRIER_TILE);
            } else {
                Walker.webWalk(OUTSIDE_BARRIER_TILE);
            }

            // Tap the barrier if we're there or exit if we aren't
            if (Player.atTile(OUTSIDE_BARRIER_TILE)) {
                Client.tap(BARRIER_TAP_RECT);
                Condition.wait(() -> Player.atTile(INSIDE_BARRIER_TILE), 100, 50);
            } else {return false;}

            // If we reach this we're inside GOTR now, grab uncharged cells
            setStatusAndDebugLog("Take uncharged cells");
            Client.tap(UNCHARGED_CELL_FROMBARRIER_TAP_RECT);
            Condition.wait(() -> Inventory.contains(ItemList.UNCHARGED_CELL_26882, 0.75), 100, 85);

            // Check if we have uncharged cells
            if (Inventory.contains(ItemList.UNCHARGED_CELL_26882, 0.75)) {
                Logger.debugLog("Done grabbing uncharged cells!");
            } else {return false;}
        }

        return false;
    }
}