package tasks;

import helpers.utils.Tile;
import utils.MiningHelper;
import utils.Task;

import static helpers.Interfaces.*;
import static main.AIOMiner.*;

public class performMining extends Task {
    MiningHelper miningHelper = new MiningHelper();
    Tile location;

    public boolean activate() {
        Logger.debugLog("Checking if we should do mining");
        location = Walker.getPlayerPosition(regionInfo.getWorldRegion()); // Cache our position so we only need to check once per loop
        Logger.debugLog("Are we within mine area? " + Player.isTileWithinArea(location, regionInfo.getMineArea()));
        return Player.isTileWithinArea(location, regionInfo.getMineArea());
    }
    @Override
    public boolean execute() {
        Logger.debugLog("Running mining sequence");
        if (hopEnabled) {
            hopActions();
        }

        //Move to spot
        if (!location.equals(locationInfo.getStepLocation())) {
            Logger.log("Stepping to vein spot");
            Walker.step(locationInfo.getStepLocation(), regionInfo.getWorldRegion());
            Condition.wait(() -> Player.atTile(locationInfo.getStepLocation(), regionInfo.getWorldRegion()), 100, 20);
        }

        //perform mining
        if (location.equals(locationInfo.getStepLocation())) {
            return doMining();
        }
        return false;
    }

    private boolean doMining() {
        return miningHelper.checkPositionsAndPerformActions(locationInfo, veinColors);
    }

    private void hopActions() {
        Game.hop(hopProfile, true, false);
    }
}
