package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class GoBackToGame extends Task {

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside GoBackToGame activate()");
        return (Player.within(outsideArea, WTRegion) || Player.within(insideArea, WTRegion)) && !SideManager.isWithinGameArea() && !waitingForGameEnded && !isGameGoing;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside GoBackToGame execute()");

        //Choose a new random side if the initial picked side was random.
        if (pickedSide.equals("Random")) {
            currentSide = SideManager.pickRandomSide();
            Logger.debugLog("Picked the " + currentSide + " side.");
        }

        if (walkToDoorFromOutside()) {
            return walkToGameFromDoor();
        } else return walkToBranchTileFromLobby();
    }

    private boolean walkToBranchTileFromLobby() {
        if (Player.isTileWithinArea(currentLocation, lobby)) {
            Walker.step(SideManager.getBranchTile(), WTRegion);
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToDoorFromOutside() {
        if (Player.isTileWithinArea(currentLocation, outsideArea)) {
            Walker.walkPath(WTRegion, getReversedTiles(wtDoorToBank));
            Condition.wait(() -> Player.within(atDoor, WTRegion), 100, 20);
            Client.tap(enterDoorRect); //Need a door tap rect (bottom of screen should do?)
            Condition.sleep(generateRandomDelay(1000, 2500));
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToGameFromDoor() {
        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Walker.walkPath(WTRegion, SideManager.getDoorToGamePath());
            Condition.wait(SideManager::isWithinGameArea, 100, 20);
            return true;
        }
        return false;
    }
}
