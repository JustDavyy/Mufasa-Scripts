package tasks;

import utils.SideManager;
import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class GetBranches extends Task {
    public static boolean gettingBranches = false;

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside GetBranches activate()");
        StateUpdater.updateStates(states);

        if (!Inventory.isFull() && SideManager.isWithinGameArea() && !waitingForGameEnded && isGameGoing && !gameAt15Percent) {
            Logger.debugLog("Getting branches: TRUE");
            gettingBranches = true;
        }

        return gettingBranches || !Inventory.isFull() && SideManager.isWithinGameArea() && !waitingForGameEnded && isGameGoing && !gameAt15Percent || !Inventory.isFull() && SideManager.isWithinGameArea() && !waitingForGameEnded && isGameGoing && gameAt20Percent && burnOnly && !isBurning && Inventory.count(brumaRoot, 0.8) < 7;
    }

    @Override
    public boolean execute() {
        preGameFoodCheck = true; //set this back to true now that a game is going?

        if (gettingBranches && Inventory.isFull() || shouldBurn) {
            gettingBranches = false;
        }

        Logger.debugLog("Inside GetBranches execute()");
        if (!Player.tileEquals(SideManager.getBranchTile(), currentLocation)) {
            Paint.setStatus("Stepping to branch tile");
            Logger.log("Stepping to branch tile!");
            Walker.step(SideManager.getBranchTile());
            lastActivity = System.currentTimeMillis();
            currentLocation = SideManager.getBranchTile();
            return true;
        }

        if (Player.tileEquals(SideManager.getBranchTile(), currentLocation)) {
            Paint.setStatus("Initiating chop action");
            Logger.log("Initiating chop action!");
            Client.tap(SideManager.getBranchRect());
            lastActivity = System.currentTimeMillis();

            Logger.debugLog("Heading to GetBranches conditional wait.");
            Paint.setStatus("Waiting for chop action to end");
            Condition.wait(() -> {
                SideManager.updateStates();
                XpBar.getXP();

                if (gameAt15Percent) {
                    if (Inventory.count(brumaRoot, 0.8) >= 5) {
                        shouldBurn = true;
                    } else {
                        shouldBurn = false;
                    }
                }

                return Inventory.isFull() || shouldEat || Player.leveledUp() || shouldBurn;
            }, 200, 150);
            return true;
        }
        return false;
    }
}
