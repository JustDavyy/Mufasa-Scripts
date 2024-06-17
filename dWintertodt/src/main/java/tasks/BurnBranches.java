package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static utils.Constants.*;

public class BurnBranches extends Task {

    @Override
    public boolean activate() {
        return Inventory.contains(brumaKindling, 0.60) && !Inventory.contains(brumaRoot, 0.60);
    }

    @Override
    public boolean execute() {
        Integer startHP = Player.getHP();

        if (!Player.atTile(SideManager.getBurnTile(), WTRegion)) {
            Walker.step(SideManager.getBurnTile(), WTRegion);
            return true;
        }

        if (Player.atTile(SideManager.getBurnTile(), WTRegion)) {
            Client.tap(SideManager.getBurnRect());
            return true;
        }

        Condition.wait(() -> startHP < Player.getHP() || !Inventory.contains(brumaKindling, 0.60), 200, 80);
        return false;
    }
}
