package tasks;

import helpers.utils.UITabs;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dTeaYoinker.*;

public class PerformThieving extends Task {
    @Override
    public boolean activate() {
        return !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        if (!Player.atTile(stallTile)) {
            moveToStall();
        } else {
            Logger.log("Stealing.");
            stealFromStall();
        }
        XpBar.getXP();
        return false;
    }

    private void moveToStall() {
        if (Walker.isReachable(stallTile)) {
            Walker.step(stallTile);
        } else {
            Walker.walkTo(middleTile);
            Player.waitTillNotMoving(12);
            Walker.step(stallTile);
        }

        Condition.wait(() -> Player.atTile(stallTile), 250, 20);
    }

    private void stealFromStall() {
        if (Game.isPlayersUnderUs()) {
            Logger.debugLog("There is a player under us, hopping worlds to prevent issues with clicks!");
            Game.instantHop(hopProfile);

            GameTabs.openTab(UITabs.INVENTORY);
            if (Game.isPlayersUnderUs()) {
                Logger.debugLog("A player is still under us in the new world, doing nothing...");
                return;
            }
        }
        if (Inventory.usedSlots() == 27) {
            Client.tap(stallTapWindow);
            Condition.wait(() -> Inventory.usedSlots() == 28, 100, 30);
        } else {
            Client.tap(stallTapWindow);
            Condition.sleep(generateRandomDelay(4000, 4400));
        }
    }
}