package tasks;

import helpers.utils.Tile;
import utils.SideManager;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class SwitchSide extends Task {
    private boolean isMageDead = false;

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside SwitchSide activate()");

        if (SideManager.getMageDead() && Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
            Logger.debugLog("Mage is dead and player is at burn tile. Checking if mage has been dead for at least 5 seconds.");
            return SideManager.isMageDeadForAtLeast(5);
        } else {
            // Mage has not been dead yet for 5 seconds.
            return false;
        }
    }

    @Override
    public boolean execute() {
        Logger.log("Mage is dead, switching side.");
        Tile targetTile = null;
        Rectangle switchSideRect = null;

        if (Player.tileEquals(currentLocation, SideManager.getBranchTile())) {
            targetTile = SideManager.getOtherSideBranchTile();
            switchSideRect = SideManager.getBranchSwitchSideRect();
            Logger.debugLog("Player is at branch tile. Switching to branch side.");
        } else if (Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
            targetTile = SideManager.getOtherSideBurnTile();
            switchSideRect = SideManager.getBurnSwitchSideRect();
            Logger.debugLog("Player is at burn tile. Switching to burn side.");
        }

        if (targetTile != null && switchSideRect != null) {
            performSwitchSide(switchSideRect, targetTile);
        } else {
            Logger.debugLog("Player is not on branch or burn tile. Walking to middle.");
            walkToMiddle();
        }

        return false;
    }

    private void performSwitchSide(Rectangle switchRect, Tile targetTile) {
        Logger.debugLog("Performing switch side action.");
        Logger.debugLog("Current side: " + currentSide);
        if (currentSide.equals("Right")) {
            Paint.setStatus("Switching to left side");
            currentSide = "Left";
        } else if (currentSide.equals("Left")) {
            Paint.setStatus("Switching to right side");
            currentSide = "Right";
        }
        Client.tap(switchRect);
        Condition.sleep(generateRandomDelay(4250, 5250));
        Walker.step(targetTile);
        lastActivity = System.currentTimeMillis();
        currentLocation = targetTile;
        isMageDead = false;

        Logger.debugLog("Switch side action completed. New side: " + currentSide);
    }

    private void walkToMiddle() {
        Paint.setStatus("Walking to the middle");
        Logger.debugLog("Walking to middle tile.");
        Tile middleTile = new Tile(6519, 15685, 0);
        Walker.walkTo(middleTile);
        lastActivity = System.currentTimeMillis();
        currentLocation = Walker.getPlayerPosition();
        isMageDead = false;
        Logger.debugLog("Reached middle tile. New location: " + currentLocation);
    }
}
