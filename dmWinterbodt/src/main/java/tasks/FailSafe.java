package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class FailSafe extends Task {
    @Override
    public boolean activate() {
        //Logger.debugLog("Inside FailSafe activate()");
        if (foodAmountInInventory == 0) {
            Logger.debugLog("Food 0?");
            currentLocation = Walker.getPlayerPosition();

            // Check if we died
            weDied = !Player.isTileWithinRegionbox(currentLocation, WTRegion);
        }

        return Player.isTileWithinArea(currentLocation, LeftTopWTArea) || Player.isTileWithinArea(currentLocation, RightTopWTArea) || weDied;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside FailSafe execute()");
        Logger.log("FailSafe triggered!");

        // FailSafe for if we are at the top left of the WT area
        if (Player.isTileWithinArea(currentLocation, LeftTopWTArea)) {
            Walker.walkPath(WTRegion, LeftTopToStart);
            Player.waitTillNotMoving(4, WTRegion);
            Walker.step(SideManager.getBranchTile());
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return false;
        }

        //FailSafe for if we are at the top left of the WT area
        if (Player.isTileWithinArea(currentLocation, RightTopWTArea)) {
            Walker.walkPath(WTRegion, RightTopToStart);
            Player.waitTillNotMoving(4, WTRegion);
            Walker.step(SideManager.getBranchTile());
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return false;
        }

        // FailSafe for if we've died?
        if (weDied) {
            Logger.log("Oh dear, you're dead!");
            Logger.log("Logging out and stopping script!");

            Logout.logout();
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
