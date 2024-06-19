package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class GetBranches extends Task {
    @Override
    public boolean activate() {
        Logger.debugLog("Inside GetBranches activate()");
        return !Inventory.isFull() && SideManager.isWithinGameArea() && !waitingForGameEnded && isGameGoing;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside GetBranches execute()");
        if (!Player.atTile(SideManager.getBranchTile(), WTRegion)) {
            Logger.log("Stepping to branch tile!");
            Walker.step(SideManager.getBranchTile(), WTRegion);
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }

        if (Player.atTile(SideManager.getBranchTile(), WTRegion)) {
            Logger.log("Tapping branch rect!");
            Client.tap(SideManager.getBranchRect());

            Logger.debugLog("Heading to GetBranches conditional wait.");
            Condition.wait(() -> {
                XpBar.getXP();
                return Inventory.isFull() || hpToEat > currentHp || Player.leveledUp() || shouldBurn || gameAt13Percent && isGameGoing;
            }, 200, 150);
            return true;
        }
        return false;
    }
}
