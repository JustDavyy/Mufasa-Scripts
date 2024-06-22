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
        StateUpdater.updateIsGameGoing();

        if (!Inventory.isFull() && SideManager.isWithinGameArea() && !waitingForGameEnded && isGameGoing && !gameAt13Percent) {
            gettingBranches = true;
        }

        return gettingBranches || !Inventory.isFull() && SideManager.isWithinGameArea() && !waitingForGameEnded && isGameGoing && !gameAt13Percent;
    }

    @Override
    public boolean execute() {
        if (gettingBranches && Inventory.isFull() || shouldBurn) {
            gettingBranches = false;
        }

        Logger.debugLog("Inside GetBranches execute()");
        if (!Player.atTile(SideManager.getBranchTile(), WTRegion)) {
            Logger.log("Stepping to branch tile!");
            Walker.step(SideManager.getBranchTile(), WTRegion);
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }

        if (Player.atTile(SideManager.getBranchTile(), WTRegion)) {
            Logger.log("Initiating chop action!");
            Client.tap(SideManager.getBranchRect());

            Logger.debugLog("Heading to GetBranches conditional wait.");
            Condition.wait(() -> {
                SideManager.updateStates();
                Logger.debugLog("Reading XP");
                XpBar.getXP();
                Logger.debugLog("XP Read");
                return Inventory.isFull() || hpToEat > currentHp || Player.leveledUp() || shouldBurn || gameAt13Percent && isGameGoing;
            }, 200, 150);
            return true;
        }
        return false;
    }
}
