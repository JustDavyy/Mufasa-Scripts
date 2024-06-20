package tasks;

import helpers.utils.Tile;
import utils.SideManager;
import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class BurnBranches extends Task {
    public static boolean isBurning = false;

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside BurnBranches activate()");

        StateUpdater.updateIsGameGoing();
        StateUpdater.updateGameAt13();

        // Regular check condition
        if (inventoryHasKindlings && !inventoryHasBruma) {
            return true;
        } else if (shouldBurn && isGameGoing) {
            isBurning = true;
        }

        // check if we have brumakindlings & we are at the burn tile
        return (inventoryHasKindlings && Player.tileEquals(currentLocation, SideManager.getBurnTile())) && isGameGoing && !SideManager.getMageDead() || isBurning && Player.isTileWithinArea(currentLocation, insideArea) && !SideManager.getMageDead();
    }

    @Override
    public boolean execute() {
        FletchBranches.isFletching = false;
        GetBranches.gettingBranches = false;

        if (!shouldBurn && isBurning) {
            isBurning = false;
        }

        Logger.debugLog("Inside BurnBranches execute()");
        Integer startHP = Player.getHP();

        // This is the logic to walk to safety when the game is near end, and we're out of burns.
        if (!inventoryHasBruma & !inventoryHasKindlings && isGameGoing && Player.tileEquals(currentLocation, SideManager.getBurnTile()) && gameAt13Percent) {
            Logger.log("Out of items to burn, and game near end. Heading to lobby to prevent getting hit.");
            Condition.sleep(generateRandomDelay(1250, 2000));
            if (Player.leveledUp()) {
                Client.sendKeystroke("KEYCODE_SPACE");
                Condition.sleep(generateRandomDelay(1000, 2000));
            }
            Walker.step(new Tile(638, 174), WTRegion);
            Condition.wait(() -> Player.within(lobby, WTRegion), 100, 20);
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return false;
        } else if (!inventoryHasBruma && !inventoryHasKindlings && isGameGoing && Player.isTileWithinArea(currentLocation, lobby) && gameAt13Percent) {
            Logger.log("Waiting in lobby for game to end to prevent getting hit.");
            return false;
        }

        if (!Player.atTile(SideManager.getBurnTile(), WTRegion) && isGameGoing) {
            Logger.log("Stepping to burn tile!");
            Walker.step(SideManager.getBurnTile(), WTRegion);
            currentLocation = Walker.getPlayerPosition(WTRegion);
        }

        if (Player.atTile(SideManager.getBurnTile(), WTRegion) && isGameGoing) {
            Logger.log("Initiating burn action!");
            Client.tap(SideManager.getBurnRect());
            Condition.wait(() -> {

                // Handle reburning/fixing
                SideManager.updateBurnStates();
                if (SideManager.getNeedsFixing() && isGameGoing && !SideManager.getMageDead() || SideManager.getNeedsReburning() && isGameGoing && !SideManager.getMageDead()) {
                    Logger.log("Brazier needs fixing or re-lighting!");
                    if (SideManager.getNeedsFixing()) {
                        Logger.log("Fixing & Relighting!");
                        tapAndSleep(3, startHP); // Fixing and relighting requires three repetitions
                    } else {
                        Logger.log("Relighting!");
                        tapAndSleep(2, startHP); // Relighting requires two repetitions
                    }
                }

                StateUpdater.updateIsGameGoing();

                XpBar.getXP();
                return !inventoryHasKindlings && !inventoryHasBruma || startHP > currentHp || Player.leveledUp() || !isGameGoing;
            }, 100, 300);

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
            Condition.sleep(generateRandomDelay(1750, 2500));
        }
    }
}
