package tasks;

import utils.Task;
import java.util.Random;
import static helpers.Interfaces.*;
import static main.dCakeThiever.*;

public class PerformThieving extends Task {
    private final Random random = new Random();

    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public boolean execute() {
        if (!Player.tileEquals(currentLocation, stallTile)) {
            moveToStall();
        } else {
            performThieving();
        }
        return false;
    }

    private void moveToStall() {
        Walker.step(stallTile);
        Condition.wait(() -> Player.atTile(stallTile), 250, 20);
        currentLocation = stallTile;
    }

    private void performThieving() {
        usedInvent = Inventory.usedSlots();
        Client.tap(stallTapWindow);
        int delay = 2600 + random.nextInt(151);
        Condition.sleep(delay);
        checkCaught();
    }

    private boolean checkCaught() {
        if (droppedChocSlice) {
            usedInvent = Inventory.usedSlots();
            return false;
        }

        int currentInventUsed = Inventory.usedSlots();
        if (currentInventUsed == usedInvent) {
            handleCaughtScenario();
            return true;
        } else {
            usedInvent = currentInventUsed;
            return false;
        }
    }

    private void handleCaughtScenario() {
        Logger.debugLog("Inventory usage has not changed, assuming we are being caught.");
        Logger.log("Running away from guards!");
        runAway();

        Logger.log("Moving back to the bakery stall.");
        runBack();

        if (!healIfNeeded()) {
            usedInvent = Inventory.usedSlots();
        }
    }

    private void runAway() {
        enableRunIfNeeded();
        Walker.walkPath(runAwayPath);
        int delay = 500 + random.nextInt(1001);
        Condition.sleep(delay);
    }

    private void runBack() {
        Walker.walkPath(runBackPath);
        Condition.sleep(1250);
        moveToStall();
    }

    private boolean healIfNeeded() {
        int[] foodItems = {2309, chocSlice};
        for (int food : foodItems) {
            if (Inventory.contains(food, 0.9)) {
                toggleTapToDropIfEnabled(false);
                Inventory.eat(food, 0.9);
                Condition.sleep(1500);
                usedInvent = Inventory.usedSlots();
                toggleTapToDropIfEnabled(true);
                return true;
            }
        }
        return false;
    }

    private void enableRunIfNeeded() {
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }
    }

    private void toggleTapToDropIfEnabled(boolean enable) {
        if (Game.isTapToDropEnabled() != enable) {
            if (enable) {
                Game.enableTapToDrop();
            } else {
                Game.disableTapToDrop();
            }
            Condition.sleep(250);
        }
    }
}