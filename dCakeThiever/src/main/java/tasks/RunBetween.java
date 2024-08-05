package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dCakeThiever.*;

public class RunBetween extends Task {
    @Override
    public boolean activate() {
        return (!Player.within(bankArea) && Inventory.isFull()) || (!Player.atTile(stallTile) && !Inventory.isFull());
    }

    @Override
    public boolean execute() {
        if (!Player.within(bankArea, ardyRegion) && Inventory.isFull()) {
            movetoBank();
        } else {
            movetoStall();
        }
        return false;
    }

    private void movetoBank() {
        Logger.log("Moving to the bank!");

        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        Walker.walkPath(pathToBank);
        Condition.wait(() -> Player.within(bankArea), 1000, 15);
        Condition.sleep(generateRandomDelay(3000, 5000));
    }

    private void movetoStall() {
        Logger.log("Moving back to the stalls!");

        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        Walker.walkPath(pathToStall);
        Condition.wait(() -> Player.within(scriptArea), 1000, 15);
        Player.waitTillNotMoving(13, ardyRegion);
        Walker.step(stallTile);

        if (!Player.atTile(stallTile, ardyRegion)) {
            Walker.step(stallTile);
        }

    }
}
