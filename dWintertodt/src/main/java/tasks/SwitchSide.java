package tasks;

import utils.Task;

import static helpers.Interfaces.Logger;

public class SwitchSide extends Task {
    @Override
    public boolean activate() {
        Logger.debugLog("Inside SwitchSide activate()");
        return false;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside SwitchSide execute()");
        return false;
    }
}
