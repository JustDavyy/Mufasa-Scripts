package tasks;

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
        if (!Player.atTile(leftBranchTile, WTRegion)) {
            Walker.step(leftBranchTile, WTRegion);
            return true;
        }

        if (Player.atTile(leftBranchTile, WTRegion)) {
            Client.tap(leftBranchClickRect);
            return true;
        }
        Game.antiAFK();
        return false;
    }
}
