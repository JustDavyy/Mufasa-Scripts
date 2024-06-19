package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class BurnBranches extends Task {

    @Override
    public boolean activate() {
        Logger.debugLog("Inside BurnBranches activate()");
        boolean inventoryHasKindlings = Inventory.contains(brumaKindling, 0.8);
        boolean inventoryHasBruma = Inventory.contains(brumaRoot, 0.8);

        // Regular check condition
        if (inventoryHasKindlings && !inventoryHasBruma) {
            return true;
        }

        // check if we have brumakindlings & we are at the burn tile
        return (inventoryHasKindlings && Player.tileEquals(currentLocation, SideManager.getBurnTile())) || shouldBurn;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside BurnBranches execute()");
        Integer startHP = Player.getHP();

        if (!Player.atTile(SideManager.getBurnTile(), WTRegion)) {
            Logger.log("Stepping to burn tile!");
            Walker.step(SideManager.getBurnTile(), WTRegion);
            currentLocation = Walker.getPlayerPosition(WTRegion);
        }

        if (Player.atTile(SideManager.getBurnTile(), WTRegion)) {
            Logger.log("Tapping burn rect!");
            Client.tap(SideManager.getBurnRect());
            Condition.wait(() -> {
                Logger.debugLog("Waiting for next actions..");

                // Handle reburning/fixing
                SideManager.updateStates();
                if (SideManager.getNeedsFixing() || SideManager.getNeedsReburning()) {
                    Logger.log("Brazier needs fixing or re-lighting!");
                    if (SideManager.getNeedsFixing()) {
                        Logger.log("Fixing & Relighting!");
                        tapAndSleep(3, startHP); // Fixing and relighting requires three repetitions
                    } else {
                        Logger.log("Relighting!");
                        tapAndSleep(2, startHP); // Relighting requires two repetitions
                    }
                }

                XpBar.getXP();

                return !inventoryHasKindlings || startHP > currentHp || Player.leveledUp();
            }, 200, 150);

            return true;
        }

        return false;
    }

    private void tapAndSleep(int repeatCount, int startHP) {
        for (int i = 0; i < repeatCount; i++) {
            if (startHP > currentHp) {
                break; // Exit the loop if the player's health drops
            }
            Client.tap(SideManager.getBurnRect());
            Condition.sleep(generateRandomDelay(1000, 1500));
        }
    }
}
