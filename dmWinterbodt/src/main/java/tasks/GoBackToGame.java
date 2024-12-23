package tasks;

import helpers.utils.Tile;
import helpers.utils.UITabs;
import utils.SideManager;
import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class GoBackToGame extends Task {
    private long lastRun = 0;

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside GoBackToGame activate()");

        StateUpdater.updateStatesNoLoc(states);

        // Check if 5 seconds have passed since the last run to prevent position find spam
        if (System.currentTimeMillis() - lastRun >= 5000) {
            // Update location
            currentLocation = Walker.getPlayerPosition();

            if (Player.isTileWithinArea(currentLocation, lobby) && isGameGoing) {
                // Use logging here for user visibility as this is the last task in our task list
                Paint.setStatus("Waiting for game to end");
                Logger.log("Waiting for game to end, sleeping 5 seconds.");
                // Update the last run time
                lastRun = System.currentTimeMillis();
                Condition.sleep(5000);

                // Prevent DCing because of idling
                if (System.currentTimeMillis() - lastActivity > 240000) {
                    Game.antiAFK();
                    GameTabs.openTab(UITabs.INVENTORY);
                    lastActivity = System.currentTimeMillis();
                }

            } else {
                // Update the last run time
                lastRun = System.currentTimeMillis();
            }
        }

        isMoreThan40Seconds = (System.currentTimeMillis() - lastWalkToSafety) > 40000;

        return Player.within(outsideArea) || Player.within(insideArea) && !SideManager.isWithinGameArea() && !waitingForGameEnded && !isGameGoing && isMoreThan40Seconds || Player.within(insideArea) && !SideManager.isWithinGameArea() && Player.isTileWithinArea(currentLocation, lobby) && isMoreThan40Seconds && !gameAt70Percent;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside GoBackToGame execute()");
        if (BreakManager.shouldBreakNow && (Player.within(lobby) || Player.within(outsideArea)) ) {
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

    private boolean walkToDoorFromOutside() {
        if (Player.isTileWithinArea(currentLocation, outsideArea)) {
            Paint.setStatus("Walking to the door from outside");
            Walker.walkPath(getReversedTiles(wtDoorToBank));
            Condition.wait(() -> Player.within(atDoor), 100, 20);
            Walker.step(new Tile(6519, 15601, 0));
            Condition.sleep(generateRandomDelay(700, 1300));
            Client.tap(enterDoorRect);
            Condition.sleep(generateRandomDelay(4250, 5300));
            currentLocation = Walker.getPlayerPosition();
            return true;
        }
        return false;
    }

    private boolean walkToLobbyFromDoor() {
        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Paint.setStatus("Walking to lobby from the door");
            Walker.walkTo(new Tile(6519, 15681, 0));
            lastActivity = System.currentTimeMillis();
            Condition.sleep(generateRandomDelay(2000, 4000));
            currentLocation = Walker.getPlayerPosition();

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
            Walker.step(SideManager.getBranchTile());
        }
    }
}
