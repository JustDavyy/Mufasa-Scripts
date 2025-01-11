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
    private static Area INSIDE_AREA = new Area(
            new Tile(14375, 37829, 0),
            new Tile(14543, 37680, 0)
    );
    private static Area LARGE_REMAINS_MINING_AREA = new Area(
            new Tile(14544, 37805, 0),
            new Tile(14590, 37697, 0)
    );
    private static Area HUGE_REMAINS_MINING_AREA = new Area(
            new Tile(14337, 37809, 0),
            new Tile(14376, 37708, 0)
    );

    // TILES
    private static Tile OUTSIDE_BARRIER_TILE = new Tile(14459, 37677, 0);
    private static Tile INSIDE_BARRIER_TILE = new Tile(14459, 37685, 0);
    private static Tile MIDDLE_INSIDE_GAME_TILE = new Tile(14463, 37721, 0);
    private static Tile EAST_INSIDE_GAME_TILE = new Tile(14503, 37745, 0);
    private static Tile AGILITY_OUTSIDE_TILE = new Tile(14531, 37761, 0);
    private static Tile AGILITY_INSIDE_TILE = new Tile(14547, 37761, 0);

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

            // If we reach this we're inside GOTR now, grab uncharged cells
            setStatusAndDebugLog("Take uncharged cells");
            Client.tap(UNCHARGED_CELL_FROMBARRIER_TAP_RECT);
            Condition.wait(() -> Inventory.contains(ItemList.UNCHARGED_CELL_26882, 0.75), 100, 85);

            // Check if we have uncharged cells
            if (Inventory.contains(ItemList.UNCHARGED_CELL_26882, 0.75)) {
                Logger.debugLog("Done grabbing uncharged cells!");
            } else {return false;}
        }

        // Logic if we are inside the game area
        if (Player.isTileWithinArea(currentLocation, INSIDE_AREA)) {
            if (Inventory.stackSize(ItemList.UNCHARGED_CELL_26882) < 6) {
                setStatusAndDebugLog("Grab uncharged cells");
                if (Walker.isReachable(UNCHARGED_CELL_TABLE_TILE)) {
                    Walker.step(UNCHARGED_CELL_TABLE_TILE);
                } else if (Walker.isReachable(MIDDLE_INSIDE_GAME_TILE)) {
                    Walker.walkTo(MIDDLE_INSIDE_GAME_TILE);
                    Player.waitTillNotMoving(15);
                    Walker.step(UNCHARGED_CELL_TABLE_TILE);
                } else {
                    Walker.webWalk(UNCHARGED_CELL_TABLE_TILE);
                    Player.waitTillNotMoving(15);
                    Walker.step(UNCHARGED_CELL_TABLE_TILE);
                }

                // Check if we are at the tile, if not try once more
                if (!Player.atTile(UNCHARGED_CELL_TABLE_TILE)) {
                    Walker.step(UNCHARGED_CELL_TABLE_TILE);
                }

                // Grab new cells if at the table
                if (Player.atTile(UNCHARGED_CELL_TABLE_TILE)) {
                    Client.tap(UNCHARGED_CELL_TABLE_TAP_RECT);
                    Condition.wait(() -> (Inventory.stackSize(ItemList.UNCHARGED_CELL_26882) > 6), 100, 85);
                }
            }

            if (usePreGameMineArea) {
                setStatusAndDebugLog("Go to lower mining area");
                // Check if east side is already reachable
                if (Walker.isReachable(EAST_INSIDE_GAME_TILE)) {
                    Walker.walkTo(EAST_INSIDE_GAME_TILE);
                } else if (Walker.isReachable(MIDDLE_INSIDE_GAME_TILE)) {
                    Walker.walkTo(MIDDLE_INSIDE_GAME_TILE);
                    Walker.walkTo(EAST_INSIDE_GAME_TILE);
                }

                Player.waitTillNotMoving(15);
                Walker.step(AGILITY_OUTSIDE_TILE);

                // Check if we are on the tile, if yes proceed
                if (Player.atTile(AGILITY_OUTSIDE_TILE)) {
                    Client.tap(LARGE_REMAINS_OUTSIDE_AGILITY_TAP_RECT);
                    Condition.wait(() -> Player.atTile(AGILITY_INSIDE_TILE), 100, 75);
                }

                // Check if we are now inside the mining area, if yes step to remains
                if (Player.atTile(AGILITY_INSIDE_TILE)) {
                    Walker.step(LARGE_GUARDIAN_REMAINS_TILE);
                }

                // Check if we're done
                if (Player.atTile(LARGE_GUARDIAN_REMAINS_TILE)) {
                    Logger.debugLog("Done with PreGame, we're now ready to mine essence.");
                }
            } else {
                setStatusAndDebugLog("Go to normal mining area");
                // Check if the tile we mine at is already reachable
                if (Walker.isReachable(GUARDIAN_PARTS_TILE)) {
                    Walker.step(GUARDIAN_PARTS_TILE);
                } else if (Walker.isReachable(MIDDLE_INSIDE_GAME_TILE)) {
                    Walker.walkTo(MIDDLE_INSIDE_GAME_TILE);
                    Player.waitTillNotMoving(15);
                    Walker.step(GUARDIAN_PARTS_TILE);
                }

                // Check if we are now on the guardians part tile
                if (Player.atTile(GUARDIAN_PARTS_TILE)) {
                    Logger.debugLog("Done with PreGame, we're now ready to mine essence.");
                }
            }
        }

        return false;
    }
}