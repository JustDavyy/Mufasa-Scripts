package tasks;

import utils.Task;

import static helpers.Interfaces.Logger;

public class PreGame extends Task {
    @Override
    public boolean activate() {
        Logger.debugLog("Inside PreGame activate()");
        return false;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside PreGame execute()");
        return false;
    }
}
