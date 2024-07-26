package tasks;

import utils.Task;

import java.util.Random;

import static helpers.Interfaces.*;
import static main.dCakeThiever.*;

public class PerformThieving extends Task {
    Random random = new Random();

    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public boolean execute() {
        if (!Player.tileEquals(currentLocation, stallTile)) {
            Walker.step(stallTile);
            Condition.wait(() -> Player.atTile(stallTile), 250, 20);
            currentLocation = stallTile;
        } else {
            usedInvent = Inventory.usedSlots();
            Client.tap(stallTapWindow);
            // Generate a random number between 2600 and 2750
            int delay = 3100 + random.nextInt(2750 - 2600 + 1);
            Condition.sleep(delay);
            return !checkCaught();
        }
        return false;
    }

    private boolean checkCaught() {
        if (droppedChocSlice) {
            usedInvent = Inventory.usedSlots();
            return false;
        }

        inventUsed = Inventory.usedSlots();

        if (inventUsed == usedInvent) {
            // Logging
            Logger.debugLog("Inventory usage has not changed, assuming we are being caught.");

            // Run away
            Logger.log("Running away from guards!");
            runAway();

            // Run back
            Logger.log("Moving back to the bakery stall.");
            runBack();

            // Eat a bread or choc slice if we have it to heal up
            int[] foodItems = {2309, chocSlice};
            for (int food : foodItems) {
                if (Inventory.contains(food, 0.9)) {
                    // Disable tap to drop if enabled
                    if (Game.isTapToDropEnabled()) {
                        Game.disableTapToDrop();
                        Condition.sleep(250);
                    }

                    Inventory.eat(food, 0.9);
                    Condition.sleep(1500);

                    // Update invent count
                    usedInvent = Inventory.usedSlots();
                    inventUsed = 1337;

                    // Enable tap to drop again
                    Game.enableTapToDrop();
                    return true;
                }
            }

            // Update invent count if no food was found
            usedInvent = Inventory.usedSlots();
            inventUsed = usedInvent;

            // return true, we were caught
            return true;
        } else {
            // Update invent slot use count for next check as we were not caught
            usedInvent = inventUsed;
            return false;
        }
    }

    private void runAway() {
        // Enable running if it is not enabled
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        // Walk out of attack range
        Walker.walkPath(runAwayPath);

        // Generate a random number between 500 and 1500
        int delay = 500 + random.nextInt(1500 - 500 + 1);
        Condition.sleep(delay);
    }

    private void runBack() {
        // Walk back
        Walker.walkPath(runBackPath);
        Condition.sleep(1250);

        // Step to the actual stall tile
        Walker.step(stallTile);
        Condition.wait(() -> Player.atTile(stallTile), 250, 20);

        currentLocation = Walker.getPlayerPosition();

        if (!Player.atTile(stallTile)) {
            Logger.debugLog("Player is not yet at the stall tile, trying to move there again.");
            Walker.step(stallTile);
            Condition.wait(() -> Player.atTile(stallTile), 250, 20);
        }

        currentLocation = Walker.getPlayerPosition();

        if (!Player.atTile(stallTile)) {
            Logger.debugLog("Player is still not at stall tile, stopping script!");
            Logout.logout();
            Script.stop();
        }

        // Enable run if not yet enabled for when we need to run/bank again.
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }
    }
}
