package tasks;

import utils.BranchDetails;
import utils.Constants;
import utils.Task;

import static helpers.Interfaces.*;
import static utils.Constants.brumaKindling;
import static utils.Constants.brumaRoot;
import static main.dWintertodt.currentSide;
import static main.dWintertodt.currentLocation;

public class BurnBranches extends Task {

    @Override
    public boolean activate() {
        return Inventory.contains(brumaKindling, 0.60) && !Inventory.contains(brumaRoot, 0.60);
    }

    @Override
    public boolean execute() {
        Integer startHP = Player.getHP();

        if (currentSide.equals("Right")) {
            if (Player.atTile(BranchDetails.RIGHT_BRANCH.getBurnTile(), Constants.WTRegion)) {
                Client.tap(BranchDetails.RIGHT_BRANCH.getBurnClickRect());
            } else {
                Walker.step(BranchDetails.RIGHT_BRANCH.getBurnTile(), Constants.WTRegion);
            }
        } else if (currentSide.equals("Left")) {
            if (Player.atTile(BranchDetails.LEFT_BRANCH.getBurnTile(), Constants.WTRegion)) {
                Client.tap(BranchDetails.LEFT_BRANCH.getBurnClickRect());
            } else {
                Walker.step(BranchDetails.LEFT_BRANCH.getBurnTile(), Constants.WTRegion);
            }
        }

        Condition.wait(() -> startHP < Player.getHP() || !Inventory.contains(brumaKindling, 0.60), 200, 80);
        return false;
    }
}
