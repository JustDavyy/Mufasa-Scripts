package tasks;

import helpers.utils.Tile;
import utils.SideManager;
import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class BurnBranches extends Task {

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside BurnBranches activate()");

        StateUpdater.updateIsGameGoing();
        StateUpdater.updateGameAt13();

        // Regular check condition
        if (inventoryHasKindlings && !inventoryHasLogs) {
            return true;
        } else if (shouldBurn && isGameGoing) {
            isBurning = true;
        }

        // check if we have brumakindlings & we are at the burn tile
        return (inventoryHasKindlings
                && Player.tileEquals(currentLocation, SideManager.getBurnTile()))
                && isGameGoing
                && !SideManager.getMageDead()
                || isBurning
                && Player.isTileWithinArea(currentLocation, insideArea)
                && !SideManager.getMageDead();
    }

    @Override
    public boolean execute() {
        FletchBranches.isFletching = false;
        GetBranches.gettingBranches = false;

        if (!shouldBurn && isBurning) {
            isBurning = false;
        }

        Logger.debugLog("Inside BurnBranches execute()");

        // This is the logic to walk to safety when the game is near end, and we're out of burns.
        if (!inventoryHasLogs & !inventoryHasKindlings && isGameGoing && Player.tileEquals(currentLocation, SideManager.getBurnTile()) && gameAt13Percent && !burnOnly) {
            Logger.log("Out of items to burn, and game near end. Heading to lobby to prevent getting hit.");
            Paint.setStatus("Walking to safety");
            Condition.sleep(generateRandomDelay(1250, 2000));
            if (Player.leveledUp()) {
                Client.sendKeystroke("KEYCODE_SPACE");
                Condition.sleep(generateRandomDelay(1000, 2000));
            }
            Walker.step(new Tile(6519, 15685, 0));
            Condition.wait(() -> Player.within(lobby), 100, 20);
            currentLocation = Walker.getPlayerPosition();
            lastWalkToSafety = System.currentTimeMillis();
            return false;
        } else if (!inventoryHasLogs && !inventoryHasKindlings && isGameGoing && Player.isTileWithinArea(currentLocation, lobby) && gameAt13Percent) {
            Logger.log("Waiting in lobby for game to end to prevent getting hit.");
            Paint.setStatus("Waiting inside the lobby");
            return false;
        }

        if (!Player.atTile(SideManager.getBurnTile()) && isGameGoing) {
            Paint.setStatus("Stepping to burn tile");
            Logger.log("Stepping to burn tile!");
            Walker.step(SideManager.getBurnTile());
            currentLocation = Walker.getPlayerPosition();
        }

        if (Player.atTile(SideManager.getBurnTile()) && isGameGoing) {
            Paint.setStatus("Initiating burn action");
            Logger.log("Initiating burn action!");
            Client.tap(SideManager.getBurnRect());
            lastActivity = System.currentTimeMillis();
            Paint.setStatus("Waiting for burning to end");

            Condition.wait(() -> {
                // Handle reburning/fixing
                SideManager.updateBurnStates();
                if (SideManager.getNeedsFixing() && isGameGoing && !SideManager.getMageDead() || SideManager.getNeedsReburning() && isGameGoing && !SideManager.getMageDead() || Player.isIdle()) {
                    Logger.log("Brazier needs fixing or re-lighting!");
                    if (SideManager.getNeedsFixing()) {
                        Paint.setStatus("Fixing & Relighting brazier");
                        totalRelightCount = totalRelightCount + 1;
                        totalRepairCount = totalRepairCount + 1;
                        Paint.setStatistic("Brazier Repairs: " + totalRepairCount + " | Relights: " + totalRelightCount);
                        Logger.log("Fixing & Relighting!");
                        tapAndSleep(3); // Fixing and relighting requires three repetitions
                    } else {
                        Paint.setStatus("Relighting brazier");
                        totalRelightCount = totalRelightCount + 1;
                        Paint.setStatistic("Brazier Repairs: " + totalRepairCount + " | Relights: " + totalRelightCount);
                        Logger.log("Relighting!");
                        tapAndSleep(2); // Relighting requires two repetitions
                    }
                }

                StateUpdater.updateStates(states);
                XpBar.getXP();

                if (!shouldBurn && isBurning) {
                    isBurning = false;
                }

                return !inventoryHasKindlings && !inventoryHasLogs || shouldEat || Player.leveledUp() || !isGameGoing;
            }, 100, 300);

            return true;
        }

        return false;
    }

    private void tapAndSleep(int repeatCount) {
        for (int i = 0; i < repeatCount; i++) {
            if (shouldEat) {
                break; // Exit the loop if the player's health drops
            }
            Client.tap(SideManager.getBurnRect());
            Condition.sleep(generateRandomDelay(2100, 2700));
        }
    }
}
