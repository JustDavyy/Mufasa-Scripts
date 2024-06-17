package utils;


import java.awt.*;

import static helpers.Interfaces.*;
import static main.dWintertodt.currentLocation;
import static main.dWintertodt.currentSide;

import static utils.Constants.*;

public class StateUpdater {
    Rectangle gameCheckRect = new Rectangle(54, 29, 5, 20);

    public void updateStates(WTStates[] states) {
        Logger.debugLog("Updating current states..");
        for (WTStates state : states) {
            // Update each boolean based on some conditions or actions
            state.setFireAlive(updateFireAlive(state));
            state.setNeedsReburning(updateNeedsReburning(state));
            state.setNeedsFixing(updateNeedsFixing(state));
            state.setMageDead(updateMageDead(state));
        }

        // Update the game state boolean (true if wt game is 15% or less left.
        updateGameState();

        //Update our position
        currentLocation = Walker.getPlayerPosition(Constants.WTRegion);

        // Update which side we are on
        if (Player.isTileWithinArea(currentLocation, Constants.leftWTArea)) {
            currentSide = "Left";
        } else if (Player.isTileWithinArea(currentLocation, Constants.rightWTArea)) {
            currentSide = "Right";
        }
    }

    private boolean updateFireAlive(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.FIRE_ALIVE.getColor();
        boolean check = Client.isColorInRect(checkColor, checkRect, 5);
        Logger.debugLog("updateFireAlive for: " + state.getName() + " : " + check);

        return check;
    }

    private boolean updateNeedsReburning(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.NEEDS_REBURNING.getColor();
        boolean check = !Client.isColorInRect(checkColor, checkRect, 5);
        Logger.debugLog("updateNeedsReburning for: " + state.getName() + " : " + check);

        return check;
    }

    private boolean updateNeedsFixing(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.NEEDS_FIXING.getColor();
        boolean check = Client.isColorInRect(checkColor, checkRect, 2);
        Logger.debugLog("updateNeedsFixing for: " + state.getName() + " : " + check);

        return check;
    }

    private boolean updateMageDead(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.MAGE_DEAD.getColor();
        boolean check = Client.isColorInRect(checkColor, checkRect, 5);
        Logger.debugLog("updateMageDead for: " + state.getName() + " : " + check);

        return check;
    }

    private void updateGameState() {
        gameNearingEnd = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameCheckRect, 10);
    }
}
