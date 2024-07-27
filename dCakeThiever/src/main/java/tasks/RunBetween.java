package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dCakeThiever.*;

public class RunBetween extends Task {
    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public boolean execute() {
        return false;
    }

    private void movetoBank() {
        Logger.debugLog("Moving to the bank!");

        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        Walker.walkPath(pathToBank);
    }

    private void movetoStall() {
        Logger.debugLog("Moving back to the stalls!");

        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        Walker.walkPath(pathToStall);
    }
}
