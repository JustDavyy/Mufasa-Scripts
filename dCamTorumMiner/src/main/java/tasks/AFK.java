package tasks;

import helpers.utils.UITabs;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dCamTorumMiner.lastAction;

public class AFK extends Task {

    @Override
    public boolean activate() {
        long currentTime = System.currentTimeMillis();
        boolean timeElapsed = (currentTime - lastAction) >= 210_000; // 3.5 minutes in milliseconds
        return !isIdle() && timeElapsed;
    }

    @Override
    public boolean execute() {
        Game.antiAFK();

        GameTabs.openTab(UITabs.INVENTORY);

        return false;
    }

    private boolean isIdle() {
        return Player.isIdle();
    }

}