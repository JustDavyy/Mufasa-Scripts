package tasks;

import helpers.utils.Tile;
import utils.SideManager;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class SwitchSide extends Task {
    private boolean isMageDead = false;
    @Override
    public boolean activate() {
        //Logger.debugLog("Inside SwitchSide activate()");

        if (SideManager.getMageDead() && Player.tileEquals(currentLocation, SideManager.getBurnTile())) {

            // Check for 5 seconds if the mage is dead for at least 5 seconds
            Condition.wait(() -> {
                if (SideManager.isMageDeadForAtLeast(5)) {
                    isMageDead = true;
                    return true;
                }
            return isMageDead;}, 250, 20);

        }
        return isMageDead;
    }

    @Override
    public boolean execute() {
        Logger.log("Mage is dead, switching side.");
        Tile targetTile = null;
        Rectangle switchSideRect = null;

        if (Player.tileEquals(currentLocation, SideManager.getBranchTile())) {
            targetTile = SideManager.getBranchTile();
            switchSideRect = SideManager.getBranchSwitchSideRect();
        } else if (Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
            targetTile = SideManager.getBurnTile();
            switchSideRect = SideManager.getBurnSwitchSideRect();
        }

        if (targetTile != null && switchSideRect != null) {
            performSwitchSide(switchSideRect, targetTile);
        } else {
            // Default action if not on branch or burn tile
            walkToMiddle();
        }

        return false;
    }

    private void performSwitchSide(Rectangle switchRect, Tile targetTile) {
        Client.tap(switchRect);
        Condition.sleep(generateRandomDelay(4250, 5250));
        Walker.step(targetTile, WTRegion);
        currentLocation = targetTile;
        isMageDead = false;
    }

    private void walkToMiddle() {
        Tile middleTile = new Tile(638, 167);
        Walker.walkTo(middleTile, WTRegion);
        currentLocation = Walker.getPlayerPosition(WTRegion);
        isMageDead = false;
    }
}
