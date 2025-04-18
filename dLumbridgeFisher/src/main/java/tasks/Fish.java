package tasks;

import main.dLumbridgeFisher;
import utils.Task;

import java.awt.*;
import java.time.Instant;

import static helpers.Interfaces.*;
import static main.dLumbridgeFisher.*;

public class Fish extends Task {
    private final static Rectangle fishingROI = new Rectangle(162, 145, 423, 366);

    @Override
    public boolean activate() {
        return !Inventory.isFull() && (shouldFish() || Player.leveledUp());
    }

    @Override
    public boolean execute() {

        if (Player.leveledUp()) {
            Logger.debugLog("We leveled up fishing!");
            fishingLevel++;
            Logger.debugLog("Fishing level is now: " + fishingLevel);
        }

        if (countedFood) {
            countedFood = false;
        }

        Paint.setStatus("Start fish action");
        Logger.log("Start fish action.");

        startFishAction();
        return false;
    }

    private void startFishAction() {
        Rectangle fishRect = Overlay.findNearestFishing(fishingROI);

        if (fishRect != null) {
            Paint.setStatus("Switch spot");
            Logger.debugLog("Switching to a new Fishing spot.");
            Client.tap(fishRect);
            Condition.wait(dLumbridgeFisher::isSpotAgainstUs, 100, 50);

            if (isSpotAgainstUs()) {
                lastXpGainTime = Instant.now();
            }
        } else {
            Logger.debugLog("Could not locate the nearest fishing spot...");
            Walker.walkTo(northFishingTile);
            Player.waitTillNotMoving(10);
        }
    }
}