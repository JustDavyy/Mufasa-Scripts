package tasks;

import helpers.utils.Tile;
import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class SwitchSide extends Task {
    @Override
    public boolean activate() {
        Logger.debugLog("Inside SwitchSide activate()");

        if (SideManager.getMageDead() && Player.tileEquals(currentLocation, SideManager.getBurnTile())) {

            // Check for 5 seconds if the mage is dead for at least 5 seconds
            Condition.wait(() -> SideManager.isMageDeadForAtLeast(5), 250, 20);

            return SideManager.isMageDeadForAtLeast(5);
        } else {
            return false;
        }
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside SwitchSide execute()");

        // Logic if we're currently on the left side
        if (currentSide.equals("Left")) {
            // Check if we are on the branch tile
            if (Player.tileEquals(currentLocation, SideManager.getBranchTile())) {
                Client.tap(SideManager.getBranchSwitchSideRect());
                Condition.sleep(generateRandomDelay(4250, 5250));

                // switch the side parameter
                currentSide = "Right";

                // Step to the branch tile
                Walker.step(SideManager.getBranchTile(), WTRegion);
                currentLocation = SideManager.getBranchTile();
            }
            // Check if we are on the burn tile
            else if (Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
                Client.tap(SideManager.getBurnSwitchSideRect());
                Condition.sleep(generateRandomDelay(4250, 5250));

                // switch the side parameter
                currentSide = "Right";

                // Step to the burn tile
                Walker.step(SideManager.getBurnTile(), WTRegion);
                currentLocation = SideManager.getBurnTile();
            }
            // Else walk to the middle if we are not found on any of the two tiles
            else {
                currentSide = "Right";
                Walker.walkTo(new Tile(638, 167), WTRegion);
                currentLocation = Walker.getPlayerPosition(WTRegion);
            }
        // Logic if we're currently on the right side
        } else if (currentSide.equals("Right")) {
            // Check if we are on the branch tile
            if (Player.tileEquals(currentLocation, SideManager.getBranchTile())) {
                Client.tap(SideManager.getBranchSwitchSideRect());
                Condition.sleep(generateRandomDelay(4250, 5250));

                // switch the side parameter
                currentSide = "Left";

                // Step to the branch tile
                Walker.step(SideManager.getBranchTile(), WTRegion);
                currentLocation = SideManager.getBranchTile();
            }
            // Check if we are on the burn tile
            else if (Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
                Client.tap(SideManager.getBurnSwitchSideRect());
                Condition.sleep(generateRandomDelay(4250, 5250));

                // switch the side parameter
                currentSide = "Left";

                // Step to the burn tile
                Walker.step(SideManager.getBurnTile(), WTRegion);
                currentLocation = SideManager.getBurnTile();
            }
            // Else walk to the middle if we are not found on any of the two tiles
            else {
                currentSide = "Left";
                Walker.walkTo(new Tile(638, 167), WTRegion);
                currentLocation = Walker.getPlayerPosition(WTRegion);
            }
        } else {
            // For some dumb fucking reason we're bot not on the left, and not on the right?
            Walker.walkTo(new Tile(638, 167), WTRegion);
            currentLocation = Walker.getPlayerPosition(WTRegion);
        }

        return false;
    }
}
