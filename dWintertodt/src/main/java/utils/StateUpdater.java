package utils;


import java.awt.*;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class StateUpdater {
//    static Rectangle gameCheckRect = new Rectangle(54, 29, 5, 20);
    static Rectangle gameAt20CheckRect = new Rectangle(253, 41, 1, 1);
    static Rectangle waitingForGameToStartRect = new Rectangle(93, 37, 1, 1);
    static Rectangle waitingForGameEndedRect = new Rectangle(56, 38, 1, 1);

    public static void updateStates(WTStates[] states) {
        //Update our position
        currentLocation = Walker.getPlayerPosition(WTRegion);

        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Logger.debugLog("Updating current states..");
            for (WTStates state : states) {
                // Update each boolean based on some conditions or actions
                state.setFireAlive(updateFireAlive(state));
                state.setNeedsReburning(updateNeedsReburning(state));
                state.setNeedsFixing(updateNeedsFixing(state));
                state.setMageDead(updateMageDead(state));
            }

            // Update the game state boolean (true if wt game is 15% or less left.
//            updateGameState();
            updateGameAt20();
            updateWaitingForGameToStart();
            updateWaitingForGameEnded();

            if (GameTabs.isInventoryTabOpen()) {
                updateKindlingState();
                updateShouldBurn();
            }
        }

        // Update which side we are on
        if (Player.isTileWithinArea(currentLocation, leftWTArea)) {
            currentSide = "Left";
        } else if (Player.isTileWithinArea(currentLocation, rightWTArea)) {
            currentSide = "Right";
        }
    }

    private static boolean updateFireAlive(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.FIRE_ALIVE.getColor();
        //Logger.debugLog("updateFireAlive for: " + state.getName() + " : " + check);

        return Client.isColorInRect(checkColor, checkRect, 5);
    }

    private static boolean updateNeedsReburning(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.NEEDS_REBURNING.getColor();
        //Logger.debugLog("updateNeedsReburning for: " + state.getName() + " : " + check);

        return !Client.isColorInRect(checkColor, checkRect, 5);
    }

    private static boolean updateNeedsFixing(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.NEEDS_FIXING.getColor();
        //Logger.debugLog("updateNeedsFixing for: " + state.getName() + " : " + check);

        return Client.isColorInRect(checkColor, checkRect, 2);
    }

    private static boolean updateMageDead(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.MAGE_DEAD.getColor();
        //Logger.debugLog("updateMageDead for: " + state.getName() + " : " + check);

        return Client.isColorInRect(checkColor, checkRect, 5);
    }

//    private static void updateGameState() {
//        gameNearingEnd = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameCheckRect, 10);
//    }

    private static void updateGameAt20() {
        gameAt20Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt20CheckRect, 10);
    }

    private static void updateShouldBurn() {
        shouldBurn = (gameAt20Percent && (inventoryHasKindlings || inventoryHasBruma && Inventory.usedSlots() >= 18));
    }

    private static void updateKindlingState() {
        inventoryHasKindlings = Inventory.contains(brumaKindling, 0.8);
        inventoryHasBruma = Inventory.contains(brumaRoot, 0.8);
    }

    private static void updateWaitingForGameToStart() {
        waitingForGameToStart = Client.isColorInRect(StateColor.GAME_GREEN_COLOR.getColor(), waitingForGameToStartRect, 10);
    }

    private static void updateWaitingForGameEnded() {
        waitingForGameEnded = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), waitingForGameEndedRect, 10);
    }

}
