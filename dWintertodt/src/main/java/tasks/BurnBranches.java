package tasks;

import main.dWintertodt;
import utils.SideManager;
import utils.Task;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class BurnBranches extends Task {

    @Override
    public boolean activate() {
        Logger.debugLog("Inside BurnBranches activate()");
        boolean inventoryHasKindlings = Inventory.contains(brumaKindling, 0.8);
        // Regular check condition
        if (inventoryHasKindlings && !Inventory.contains(brumaRoot, 0.8)) {
            return true;
        }

        // check if we have brumakindlings & we are at the burn tile
        return inventoryHasKindlings && Player.tileEquals(currentLocation, SideManager.getBurnTile());
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside BurnBranches execute()");
        Integer startHP = Player.getHP();

        if (!Player.atTile(SideManager.getBurnTile(), WTRegion)) {
            Walker.step(SideManager.getBurnTile(), WTRegion);
            currentLocation = Walker.getPlayerPosition(WTRegion);
        }

        if (Player.atTile(SideManager.getBurnTile(), dWintertodt.WTRegion)) {
            Client.tap(SideManager.getBurnRect());
            Logger.debugLog("Heading to BurnBranches conditional wait.");
            Condition.wait(() -> {
                boolean inventoryCheck = !Inventory.contains(dWintertodt.brumaKindling, 0.8);
                boolean healthCheck = startHP > Player.getHP();
                boolean levelUpCheck = Player.leveledUp();

                // Handle reburning/fixing
                SideManager.updateStates();
                if (SideManager.getNeedsFixing() || SideManager.getNeedsReburning()) {
                    Client.tap(SideManager.getBurnRect());
                }

                XpBar.getXP();

                return inventoryCheck || healthCheck || levelUpCheck;
            }, 200, 150);


            return true;
        }

        return false;
    }
}
