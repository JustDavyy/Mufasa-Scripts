package tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class FailSafe extends Task {
    Area goodArea = new Area(
            new Tile(6467, 15487, 0),
            new Tile(6585, 15736, 0)
    ); //This is just the area including the two sides, lobby and bank area.

    Area deadArea = new Area(
            new Tile(12858, 12580, 0),
            new Tile(12918, 12656, 0)
    );
    
    @Override
    public boolean activate() {
        //Logger.debugLog("Inside FailSafe activate()");
        if (foodAmountInInventory == 0) {
            currentLocation = Walker.getPlayerPosition();

            if (shouldEat) {
                return true;
            }

            // Check if we died
            weDied = !Player.isTileWithinArea(currentLocation, WTArea);
        }

        return Player.isTileWithinArea(currentLocation, LeftTopWTArea) || Player.isTileWithinArea(currentLocation, RightTopWTArea) || weDied;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside FailSafe execute()");
        Paint.setStatus("FailSafe triggered");
        Logger.log("FailSafe triggered!");

        // FailSafe for if we are at the top left of the WT area
        if (Player.isTileWithinArea(currentLocation, LeftTopWTArea)) {
            Paint.setStatus("FailSafe top left activated");
            Walker.walkPath(LeftTopToStart);
            Player.waitTillNotMoving(4);
            Walker.step(SideManager.getBranchTile());
            lastActivity = System.currentTimeMillis();
            currentLocation = Walker.getPlayerPosition();
            return false;
        }

        //FailSafe for if we are at the top right of the WT area
        if (Player.isTileWithinArea(currentLocation, RightTopWTArea)) {
            Paint.setStatus("FailSafe top right activated");
            Walker.walkPath(RightTopToStart);
            Player.waitTillNotMoving(4);
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
