package Tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import utils.StateUpdater;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class EnterGame extends Task {
    private final StateUpdater stateUpdater;
    public EnterGame(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    // AREAS
    public static Area OUTSIDE_AREA= new Area(
            new Tile(14381, 37677, 0),
            new Tile(14539, 37569, 0)
    );

    // TILES
    private static final Tile OUTSIDE_BARRIER_TILE = new Tile(14459, 37677, 0);
    private static final Tile INSIDE_BARRIER_TILE = new Tile(14459, 37685, 0);

    // RECTANGLES
    private static final Rectangle BARRIER_TAP_RECT = new Rectangle(434, 234, 25, 18);

    // Color finder parameters to check the portal color!
    @Override
    public boolean activate() {
        return !stateUpdater.isGameGoing() && Player.isTileWithinArea(currentLocation, OUTSIDE_AREA);
    }

    @Override
    public boolean execute() {

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
                Condition.sleep(750, 900);
                // Handle if we cannot join due to ending game
                if (Chatbox.isMakeMenuVisible()) {
                    setStatusAndDebugLog("Dismiss pop-up");
                    Client.sendKeystroke("space");
                    Condition.sleep(1500, 2000);
                    return false;
                }
                Condition.wait(() -> Player.atTile(INSIDE_BARRIER_TILE), 100, 50);
            } else {return false;}
        }

        return false;
    }
}
