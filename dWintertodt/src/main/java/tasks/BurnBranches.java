package tasks;

import main.dWintertodt;
import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;

public class BurnBranches extends Task {

    @Override
    public boolean activate() {
        Logger.debugLog("Inside BurnBranches activate()");
        boolean inventoryHasKindlings = Inventory.contains(dWintertodt.brumaKindling, 0.8);
        // Regular check condition
        if (inventoryHasKindlings && !Inventory.contains(dWintertodt.brumaRoot, 0.8)) {
            return true;
        }

        // check if we have brumakindlings & we are at the burn tile
        return inventoryHasKindlings && Player.tileEquals(dWintertodt.currentLocation, SideManager.getBurnTile());
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
                boolean inventoryCheck = !Inventory.contains(main.dWintertodt.brumaKindling, 0.8);
                boolean healthCheck = startHP > Player.getHP();
                boolean levelUpCheck = Player.leveledUp();
                XpBar.getXP();
                return inventoryCheck || healthCheck || levelUpCheck;
            }, 200, 150);
            return true;
        }

        return false;
    }
}
