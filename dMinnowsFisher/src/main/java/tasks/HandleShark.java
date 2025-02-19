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

public class HandleShark extends Task {
    private final static Rectangle lastLine = new Rectangle(223, 114, 127, 15);
    private final static Rectangle fishingROI = new Rectangle(228, 148, 355, 279);

    private final static List<Color> whiteColor = Arrays.asList(Color.decode("#ffffff"));
    private long timeSinceLastSharkAction = System.currentTimeMillis();

    @Override
    public boolean activate() {
        return isSharkPresent();
    }

    @Override
    public boolean execute() {

        Paint.setStatus("Handle Shark");

        // Have we already responded to it? Give it time to catch some minnows first!
        if (timeSinceLastSharkAction >= 8) {
            switchSpots();
            return false;
        }

        return true;
    }

    private void switchSpots() {
        Rectangle fishRect = Overlay.findSecondNearestFishing(fishingROI);
        if (fishRect != null) {
            Paint.setStatus("Handle shark");
            Logger.log("Switching to a different rotating Minnow spot, as a shark was spotted.");
            Client.tap(fishRect);
            lastXpGainTime = Instant.now().plusSeconds(9);
            lastSharkAction = Instant.now();
            timeSinceLastSharkAction = Duration.between(lastSharkAction, Instant.now()).getSeconds();
        } else {
            Logger.debugLog("Could not locate the nearest fishing spot...");
        }
        sharkPresent = false;
    }

    private Boolean isSharkPresent() {
        String results = OCR.readPlain12Text(lastLine, whiteColor);

        // Check if it contains "eats"

        sharkPresent = results.contains("eats");
        return sharkPresent;
    }
}