package agi_sdk.helpers;

import agi_sdk.agi_sdk;
import helpers.utils.Area;
import helpers.utils.Tile;

import java.awt.*;

import static helpers.Interfaces.*;

public class MarkHandling {
    public final Rectangle checkArea;
    public final Color targetColor;
    public final Rectangle tapArea;
    public final Tile endTile;
    public final Area failArea;
    public final boolean checkForFail;

    public MarkHandling(Rectangle checkArea, Color targetColor, Rectangle tapArea, Tile endTile, Area failArea, boolean checkForFail) {
        this.checkArea = checkArea;
        this.targetColor = targetColor;
        this.tapArea = tapArea;
        this.endTile = endTile;
        this.failArea = failArea;
        this.checkForFail = checkForFail;
    }

    public boolean isMarkPresent(Rectangle mogRectangle, Color mogColor) {
        if (Client.isColorInRect(mogColor, mogRectangle, 10)) {
            Logger.debugLog("Found MoG on floor");
            return true;
        } else {
            return false;
        }
    }

    public void pickUpMark(Rectangle mogRectangle, Rectangle nextObstacleRectangle, Tile endTile, Area failArea, boolean checkForFail) {
        Paint.setStatus("Pick up MoG");
        Client.tap(mogRectangle);
        Player.waitTillNotMoving(30);
        agi_sdk.mogTotal++;
        agi_sdk.mogCount = agi_sdk.mogTotal;
        Paint.updateBox(agi_sdk.MoGIndex, agi_sdk.mogCount);
        Logger.log("Total Marks of grace gathered so far: " + agi_sdk.mogTotal);
        Client.tap(nextObstacleRectangle);

        if (failArea != null && checkForFail) {
            Condition.wait(() -> Player.atTile(endTile) || Player.within(failArea), 100, 250);
        } else {
            Condition.wait(() -> Player.atTile(endTile), 100, 250);
        }

        Condition.sleep(agi_sdk.generateRandomDelay(400, 600));
    }
}
