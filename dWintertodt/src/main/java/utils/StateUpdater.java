package utils;


import main.dWintertodt;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class StateUpdater {
    static Rectangle gameCheckRect = new Rectangle(54, 29, 5, 20);
    static Rectangle gameAt20CheckRect = new Rectangle(0,0,0,0);
    static Rectangle waitingForGameToStart = new Rectangle(0,0,0,0);

    public static void updateStates(WTStates[] states) {
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
        updateGameAt20();
        updateWaitingForGameToStart();

        //Update our position
        currentLocation = Walker.getPlayerPosition(main.dWintertodt.WTRegion);

        // Update which side we are on
        if (Player.isTileWithinArea(currentLocation, main.dWintertodt.leftWTArea)) {
            currentSide = "Left";
        } else if (Player.isTileWithinArea(currentLocation, main.dWintertodt.rightWTArea)) {
            currentSide = "Right";
        }
    }

    private static boolean updateFireAlive(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.FIRE_ALIVE.getColor();
        boolean check = Client.isColorInRect(checkColor, checkRect, 5);
        //Logger.debugLog("updateFireAlive for: " + state.getName() + " : " + check);

        return check;
    }

    private static boolean updateNeedsReburning(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.NEEDS_REBURNING.getColor();
        boolean check = !Client.isColorInRect(checkColor, checkRect, 5);
        //Logger.debugLog("updateNeedsReburning for: " + state.getName() + " : " + check);

        return check;
    }

    private static boolean updateNeedsFixing(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.NEEDS_FIXING.getColor();
        boolean check = Client.isColorInRect(checkColor, checkRect, 2);
        //Logger.debugLog("updateNeedsFixing for: " + state.getName() + " : " + check);

        return check;
    }

    private static boolean updateMageDead(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.MAGE_DEAD.getColor();
        boolean check = Client.isColorInRect(checkColor, checkRect, 5);
        //Logger.debugLog("updateMageDead for: " + state.getName() + " : " + check);

        return check;
    }

    private static void updateGameState() {
        gameNearingEnd = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameCheckRect, 10);
    }

    private static void updateGameAt20() {
        gameAt20Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt20CheckRect, 10);
    }

    private static void updateWaitingForGameToStart() {
        dWintertodt.waitingForGameToStart = Client.isColorInRect(StateColor.GAME_GREEN_COLOR.getColor(), waitingForGameToStart, 10);
    }

}
