package tasks;

import main.dMinnowsFisher;
import utils.Task;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static helpers.Interfaces.*;
import static main.dMinnowsFisher.*;

public class Fish extends Task {
    private final static Rectangle lastLine = new Rectangle(223, 114, 127, 15);
    private final static Rectangle fishingROI = new Rectangle(228, 148, 355, 279);

    private final static List<Color> whiteColor = Arrays.asList(java.awt.Color.decode("#ffffff"));

    @Override
    public boolean activate() {
        return shouldFish() || Player.leveledUp();
    }

    @Override
    public boolean execute() {

        Paint.setStatus("Start fish action");
        Logger.log("Start fish action.");

        fishMinnows();
        return false;
    }

    private void fishMinnows() {
        Rectangle fishRect = Overlay.findNearestFishing(fishingROI);

        if (fishRect != null) {
            Paint.setStatus("Switch spot");
            Logger.debugLog("Switching to a new Minnow spot.");
            Client.tap(fishRect);
            lastXpGainTime = Instant.now().plusSeconds(8);
            Condition.wait(dMinnowsFisher::isSpotAgainstUs, 100, 50);
        } else {
            Logger.debugLog("Could not locate the nearest fishing spot...");
        }
    }
}