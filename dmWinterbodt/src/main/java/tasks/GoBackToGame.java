package tasks;

import utils.SideManager;
import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class GoBackToGame extends Task {

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside GoBackToGame activate()");

        // Use logging here for user visibility as this is the last task in our task list
        if (Player.isTileWithinArea(currentLocation, lobby) && isGameGoing) {
            Logger.log("Waiting for game to end.");
        }

        return Player.within(outsideArea, WTRegion) || Player.within(insideArea, WTRegion) && !SideManager.isWithinGameArea() && !waitingForGameEnded && !isGameGoing;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside GoBackToGame execute()");
        if (BreakManager.shouldBreakNow && (Player.within(lobby, WTRegion) || Player.within(outsideArea, WTRegion)) ) {
            return true;
        }

        //Choose a new random side if the initial picked side was random.
        if (pickedSide.equals("Random")) {
            currentSide = SideManager.pickRandomSide();
            Logger.debugLog("Picked the " + currentSide + " side.");
        }

        if (walkToDoorFromOutside()) {
            return walkToLobbyFromDoor();
        } else return walkToLobbyFromDoor();
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
            Condition.sleep(generateRandomDelay(700, 1300));
            Client.tap(enterDoorRect);
            Condition.sleep(generateRandomDelay(4250, 5300));
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToGameFromDoor() {
        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Walker.walkPath(WTRegion, SideManager.getDoorToGamePath());
            Condition.wait(SideManager::isWithinGameArea, 100, 20);
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToLobbyFromDoor() {
        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Client.tap(new java.awt.Rectangle(795, 63, 16, 19));
            Condition.sleep(generateRandomDelay(2000, 4000));
            currentLocation = Walker.getPlayerPosition(WTRegion);

            // Check if we can start a game or not
            StateUpdater.updateGameAt70();
            if (!gameAt70Percent) {
                walkToBranchesTile();
            }
            return true;
        }
        return false;
    }

    private void walkToBranchesTile() {
        if (Player.isTileWithinArea(currentLocation, lobby)) {
            Walker.step(SideManager.getBranchTile(), WTRegion);
        }
    }
}
