package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static utils.Constants.*;

public class GetBranches extends Task {
    @Override
    public boolean activate() {
        return !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        if (!Player.atTile(SideManager.getBranchTile(), WTRegion)) {
            Walker.step(SideManager.getBranchTile(), WTRegion);
            return true;
        }

        if (Player.atTile(SideManager.getBranchTile(), WTRegion)) {
            Client.tap(SideManager.getBranchRect());
            return true;
        }
        Game.antiAFK();
        return false;
    }
}
