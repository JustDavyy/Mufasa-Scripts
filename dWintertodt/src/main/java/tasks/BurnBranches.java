package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.hpToEat;
import static utils.Constants.*;

public class BurnBranches extends Task {

    @Override
    public boolean activate() {
        Logger.debugLog("Inside BurnBranches activate()");
        return Inventory.contains(main.dWintertodt.brumaKindling, 0.60) && !Inventory.contains(main.dWintertodt.brumaRoot, 0.60);
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside BurnBranches execute()");
        Integer startHP = Player.getHP();

        if (!Player.atTile(SideManager.getBurnTile(), main.dWintertodt.WTRegion)) {
            Walker.step(SideManager.getBurnTile(), main.dWintertodt.WTRegion);
            return true;
        }

        if (Player.atTile(SideManager.getBurnTile(), main.dWintertodt.WTRegion)) {
            Client.tap(SideManager.getBurnRect());
            Logger.debugLog("Heading to BurnBranches conditional wait.");
            Condition.wait(() -> {
                boolean inventoryCheck = !Inventory.contains(main.dWintertodt.brumaKindling, 0.60);
                boolean healthCheck = startHP > Player.getHP();
                return inventoryCheck || healthCheck;
            }, 200, 150);
            return true;
        }

        return false;
    }
}
