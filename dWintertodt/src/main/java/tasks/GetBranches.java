package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.hpToEat;

public class GetBranches extends Task {
    @Override
    public boolean activate() {
        Logger.debugLog("Inside GetBranches activate()");
        return !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside GetBranches execute()");
        if (!Player.atTile(SideManager.getBranchTile(), main.dWintertodt.WTRegion)) {
            Walker.step(SideManager.getBranchTile(), main.dWintertodt.WTRegion);
            return true;
        }

        if (Player.atTile(SideManager.getBranchTile(), main.dWintertodt.WTRegion)) {
            Client.tap(SideManager.getBranchRect());

            Logger.debugLog("Heading to GetBranches conditional wait.");
            Condition.wait(() -> {
                boolean inventoryCheck = Inventory.isFull();
                boolean healthCheck = hpToEat > Player.getHP();
                boolean levelUpCheck = Player.leveledUp();
                XpBar.getXP();
                return inventoryCheck || healthCheck || levelUpCheck;
            }, 200, 150);
            return true;
        }
        return false;
    }
}
