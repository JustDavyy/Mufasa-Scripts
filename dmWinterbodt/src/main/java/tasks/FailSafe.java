package tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class FailSafe extends Task {
    Area topLeftArea = new Area(
            new Tile(6413, 15738, 0),
            new Tile(6520, 15880, 0)
    );

    Area topRightArea = new Area(
            new Tile(6527, 15739, 0),
            new Tile(6609, 15877, 0)
    );

    Tile[] topLeftRecoveryPath = new Tile[] {
            new Tile(6499, 15838, 0),
            new Tile(6479, 15825, 0),
            new Tile(6461, 15812, 0),
            new Tile(6454, 15785, 0),
            new Tile(6457, 15751, 0),
            new Tile(6469, 15724, 0),
            new Tile(6488, 15716, 0),
            new Tile(6510, 15704, 0),
            new Tile(6523, 15685, 0)
    };

    Tile[] topRightRecoveryPath = new Tile[] {
            new Tile(6546, 15837, 0),
            new Tile(6565, 15825, 0),
            new Tile(6584, 15807, 0),
            new Tile(6586, 15777, 0),
            new Tile(6586, 15750, 0),
            new Tile(6574, 15733, 0),
            new Tile(6553, 15716, 0),
            new Tile(6528, 15707, 0),
            new Tile(6521, 15685, 0)
    };

    Area deadArea = new Area(
            new Tile(12858, 12580, 0),
            new Tile(12918, 12656, 0)
    );

    @Override
    public boolean activate() {
        if (foodAmountInInventory == 0) {
            currentLocation = Walker.getPlayerPosition();

            if (shouldEat) {
                return true;
            }

            // Check if we died
            weDied = Player.isTileWithinArea(currentLocation, deadArea);
        }
        Logger.debugLog("Current loc: " + currentLocation);
        return Player.isTileWithinArea(currentLocation, topLeftArea) || Player.isTileWithinArea(currentLocation, topRightArea) || weDied;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("FailSafe triggered");
        Logger.log("FailSafe triggered!");

        // FailSafe for if we are at the top left of the WT area
        if (Player.isTileWithinArea(currentLocation, topLeftArea)) {
            Paint.setStatus("FailSafe topLeft of minigame activated");
            Walker.walkPath(topLeftRecoveryPath);
            Player.waitTillNotMoving(15);
            Walker.step(SideManager.getBranchTile());
            lastActivity = System.currentTimeMillis();
            currentLocation = Walker.getPlayerPosition();
            return false;
        }

        // FailSafe for if we are at the top right of the WT area
        if (Player.isTileWithinArea(currentLocation, topRightArea)) {
            Paint.setStatus("FailSafe topRight of minigame activated");
            Walker.walkPath(topRightRecoveryPath);
            Player.waitTillNotMoving(15);
            Walker.step(SideManager.getBranchTile());
            lastActivity = System.currentTimeMillis();
            currentLocation = Walker.getPlayerPosition();
            return false;
        }

        // FailSafe for if we've died?
        if (weDied) {
            Paint.setStatus("Oh dear, you're dead!");
            Logger.log("Oh dear, you're dead!");
            Logger.log("Logging out and stopping script!");

            Logout.logout();
            lastActivity = System.currentTimeMillis();
            Condition.sleep(generateRandomDelay(10000, 12000));

            // Additional check due to possible in combat
            if (!Logout.isLoggedOut()) {
                Logout.logout();
            }

            // Finally, stop the script
            Script.stop();
        }

        GameTabs.openInventoryTab();
        return false;
    }
}