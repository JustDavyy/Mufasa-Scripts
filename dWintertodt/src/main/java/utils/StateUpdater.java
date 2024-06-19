package utils;


import java.awt.*;
import java.util.Date;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class StateUpdater {
//    static Rectangle gameCheckRect = new Rectangle(54, 29, 5, 20);
    static Rectangle gameAt13CheckRect = new Rectangle(83, 38, 1, 1);
    static Rectangle gameAt20CheckRect = new Rectangle(93, 37, 1, 1);
    static Rectangle gameAt70CheckRect = new Rectangle(196, 39, 1, 1);
    static Rectangle waitingForGameToStartRect = new Rectangle(253, 41, 1, 1);
    static Rectangle waitingForGameEndedRect = new Rectangle(56, 38, 1, 1);
    static long mageDeadTimestamp = -1;
    static Date date = new Date();

    public static void updateStates(WTStates[] states) {
        //Update our position
        currentLocation = Walker.getPlayerPosition(WTRegion);

        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            for (WTStates state : states) {
                // Update each boolean based on some conditions or actions
                state.setFireAlive(updateFireAlive(state));
                state.setNeedsReburning(updateNeedsReburning(state));
                state.setNeedsFixing(updateNeedsFixing(state));
                state.setMageDead(updateMageDead(state));
            }

            // Update the game state boolean (true if wt game is 15% or less left.
//            updateGameState();
            updateGameAt13();
            updateGameAt20();
            updateGameAt70();
            updateIsGameGoing();
            updateWaitingForGameToStart();
            updateWaitingForGameEnded();
            updateCurrentHP();

            updateKindlingState();
            updateShouldBurn();

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

        boolean isMageDead = Client.isColorInRect(checkColor, checkRect, 5);

        if (isMageDead) {
            if (mageDeadTimestamp == -1) {
                // Set the timestamp when the mage first becomes dead
                mageDeadTimestamp = System.currentTimeMillis();

                // Set the timestamp to a date object
                date = new Date(mageDeadTimestamp);

                // Format the time as HH:MM:SS
                String formattedTime = String.format("%02d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds());

                Logger.debugLog("Mage died at: " + formattedTime);
            }
        } else {
            // Reset the timestamp if the mage is not dead
            mageDeadTimestamp = -1;
        }

        return isMageDead;
    }

    private static void updateCurrentHP() {
        currentHp = Player.getHP();
    }

//    private static void updateGameState() {
//        gameNearingEnd = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameCheckRect, 10);
//    }

    private static void updateGameAt13() {
        gameAt13Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt13CheckRect, 10);
    }

    private static void updateGameAt20() {
        gameAt20Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt20CheckRect, 10);
    }

    private static void updateGameAt70() {
        gameAt70Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt70CheckRect, 10);
    }

    private static void updateShouldBurn() {
        shouldBurn = (gameAt20Percent && (inventoryHasKindlings || inventoryHasBruma) && Inventory.usedSlots() >= 18)
                || isGameGoing && gameAt20Percent && (inventoryHasBruma || inventoryHasKindlings);
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

    private static void updateIsGameGoing() {
        isGameGoing = Client.isColorInRect(StateColor.GAME_GREEN_COLOR.getColor(), waitingForGameEndedRect, 10);
    }

}
