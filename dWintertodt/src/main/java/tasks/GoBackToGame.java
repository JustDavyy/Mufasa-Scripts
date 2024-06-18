package tasks;

import main.dWintertodt;
import utils.SideManager;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class GoBackToGame extends Task {

    @Override
    public boolean activate() {
        Logger.debugLog("Inside GoBackToGame activate()");
        return !gameNearingEnd && ((Player.within(outsideArea, WTRegion) || Player.within(insideArea, WTRegion)) && !SideManager.isWithinGameArea());
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside GoBackToGame execute()");
        if (walkToDoorFromOutside()) {
            walkToGameFromDoor();
            return true;
        } else if (walkToBranchTileFromLobby()) {
            return true;
        }

        return false;
    }

    private boolean walkToBranchTileFromLobby() {
        if (Player.isTileWithinArea(currentLocation, lobby)) {
            Walker.step(SideManager.getBranchTile());
            return true;
        }
        return false;
    }

    private boolean walkToDoorFromOutside() {
        if (Player.isTileWithinArea(currentLocation, outsideArea)) {
            Walker.walkPath(getReversedTiles(wtDoorToBank));
            Condition.wait(() -> Player.within(atDoor, WTRegion), 100, 20);
            Client.tap(enterDoorRect); //Need a door tap rect (bottom of screen should do?)
            Condition.sleep(generateRandomDelay(1000, 2500));
            return true;
        }
        return false;
    }

    private boolean walkToGameFromDoor() {
        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Walker.walkPath(SideManager.getDoorToGamePath());
            Condition.wait(() -> SideManager.isWithinGameArea(), 100, 20);
            return true;
        }
        return false;
    }
}
