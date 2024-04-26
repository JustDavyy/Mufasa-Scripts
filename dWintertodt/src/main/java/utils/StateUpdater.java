package utils;


import main.dWintertodt;

import java.awt.*;

import static helpers.Interfaces.Client;
import static helpers.Interfaces.Logger;

public class StateUpdater {
    Rectangle gameCheckRect = new Rectangle(54, 29, 5, 20);

    public void updateStates(WTStates[] states) {
        for (WTStates state : states) {
            // Update each boolean based on some conditions or actions
            state.setFireAlive(updateFireAlive(state));
            state.setNeedsReburning(updateNeedsReburning(state));
            state.setNeedsFixing(updateNeedsFixing(state));
            state.setMageDead(updateMageDead(state));
        }

        // Update the game state boolean (true if wt game is 15% or less left.
        updateGameState();
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
        boolean check = Client.isColorInRect(checkColor, checkRect, 10);
        Logger.debugLog("updateNeedsReburning for: " + state.getName() + " : " + check);

        return check;
    }

    private boolean updateNeedsFixing(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.NEEDS_FIXING.getColor();
        boolean check = Client.isColorInRect(checkColor, checkRect, 5);
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
        dWintertodt.gameNearingEnd = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameCheckRect, 10);
    }
}
