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

        StateUpdater.updateStates(states);

        // Use logging here for user visibility as this is the last task in our task list
        if (Player.isTileWithinArea(currentLocation, lobby) && isGameGoing) {
            Paint.setStatus("Waiting for game to end");
            Logger.log("Waiting for game to end.");

            // Prevent DCing because of idling
            if (System.currentTimeMillis() - lastActivity > 240000) {
                Game.antiAFK();
                GameTabs.openInventoryTab();
                lastActivity = System.currentTimeMillis();
            }

        }

        isMoreThan40Seconds = (System.currentTimeMillis() - lastWalkToSafety) > 40000;

        return Player.within(outsideArea, WTRegion) || Player.within(insideArea, WTRegion) && !SideManager.isWithinGameArea() && !waitingForGameEnded && !isGameGoing && isMoreThan40Seconds || Player.within(insideArea, WTRegion) && !SideManager.isWithinGameArea() && Player.isTileWithinArea(currentLocation, lobby) && isMoreThan40Seconds && !gameAt70Percent;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside GoBackToGame execute()");
        if (BreakManager.shouldBreakNow && (Player.within(lobby, WTRegion) || Player.within(outsideArea, WTRegion)) ) {
            return true;
        }

        //Choose a new random side if the initial picked side was random.
        if (pickedSide.equals("Random")) {
            Paint.setStatus("Picking random side");
            currentSide = SideManager.pickRandomSide();
            Logger.debugLog("Picked the " + currentSide + " side.");
        }

        if (walkToDoorFromOutside()) {
            return walkToLobbyFromDoor();
        } else return walkToLobbyFromDoor();
    }

    private boolean walkToBranchTileFromLobby() {
        if (Player.isTileWithinArea(currentLocation, lobby)) {
            Paint.setStatus("Walking to branch tile from lobby");
            Walker.step(SideManager.getBranchTile(), WTRegion);
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToDoorFromOutside() {
        if (Player.isTileWithinArea(currentLocation, outsideArea)) {
            Paint.setStatus("Walking to the door from outside");
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
            Paint.setStatus("Walking to game from the door");
            Walker.walkPath(WTRegion, SideManager.getDoorToGamePath());
            Condition.wait(SideManager::isWithinGameArea, 100, 20);
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToLobbyFromDoor() {
        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Paint.setStatus("Walking to lobby from the door");
            Client.tap(new java.awt.Rectangle(800, 63, 16, 19));
            lastActivity = System.currentTimeMillis();
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
            Paint.setStatus("Walking to branch tile");
            Walker.step(SideManager.getBranchTile(), WTRegion);
        }
    }
}
