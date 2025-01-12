package Tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.StateUpdater;
import utils.Task;

import java.awt.*;

import static Tasks.EnterGame.OUTSIDE_AREA;
import static Tasks.MineEssence.HUGE_REMAINS_MINING_AREA;
import static Tasks.MineEssence.LARGE_REMAINS_MINING_AREA;
import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class PreGame extends Task {

    private final StateUpdater stateUpdater;

    public PreGame(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    // AREAS
    public static final Area INSIDE_AREA = new Area(
            new Tile(14375, 37829, 0),
            new Tile(14543, 37680, 0)
    );

    // TILES
    private static final Tile MIDDLE_INSIDE_GAME_TILE = new Tile(14463, 37721, 0);
    private static final Tile EAST_INSIDE_GAME_TILE = new Tile(14503, 37745, 0);
    public static final Tile AGILITY_OUTSIDE_TILE = new Tile(14531, 37761, 0);
    private static final Tile AGILITY_INSIDE_TILE = new Tile(14547, 37761, 0);

    @Override
    public boolean activate() {
        return !stateUpdater.isGameGoing() && !Player.isTileWithinArea(currentLocation, OUTSIDE_AREA) || stateUpdater.isGameGoing() && stateUpdater.timeTillRuneSwitch() > 35 && (!Player.isTileWithinArea(currentLocation, LARGE_REMAINS_MINING_AREA) && !Player.tileEquals(currentLocation, GUARDIAN_PARTS_TILE));
    }

    @Override
    public boolean execute() {
        setStatusAndDebugLog("Reset all states");
        stateUpdater.resetAllStates();

        setStatusAndDebugLog("Start PreGame");

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

                // Verify we have uncharged cells
                if (!Inventory.contains(ItemList.UNCHARGED_CELL_26882, 0.7)) {
                    Logger.debugLog("Failed to obtain uncharged cells");
                    return false;
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

                Player.waitTillNotMoving(7);
                Walker.step(AGILITY_OUTSIDE_TILE);

                // Check if we are on the tile, if yes proceed
                if (Player.atTile(AGILITY_OUTSIDE_TILE)) {
                    Client.tap(LARGE_REMAINS_OUTSIDE_AGILITY_TAP_RECT);
                    Condition.wait(() -> Player.atTile(AGILITY_INSIDE_TILE), 100, 75);
                }

                // Check if we are now inside the mining area, if yes tap remains
                if (Player.atTile(AGILITY_INSIDE_TILE)) {
                    Client.tap(LARGE_GUARDIAN_REMAINS_FROMAGILITY_TAP_RECT);
                }

                Logger.debugLog("Done with PreGame, we're now ready to mine essence.");
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