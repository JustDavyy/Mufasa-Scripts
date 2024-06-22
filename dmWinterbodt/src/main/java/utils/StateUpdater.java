package utils;


import java.awt.*;
import java.util.Date;
import java.util.HashMap;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class StateUpdater {
    //    static Rectangle gameCheckRect = new Rectangle(54, 29, 5, 20);
    static Rectangle gameAt13CheckRect = new Rectangle(83, 38, 1, 1);
    static Rectangle gameAt20CheckRect = new Rectangle(93, 37, 1, 1);
    static Rectangle gameAt70CheckRect = new Rectangle(196, 39, 1, 1);
    static Rectangle waitingForGameToStartRect = new Rectangle(253, 41, 1, 1);
    static Rectangle waitingForGameEndedRect = new Rectangle(56, 38, 1, 1);
    public static final HashMap<String, Long> mageDeadTimestamps = new HashMap<>();

    static {
        mageDeadTimestamps.put("Left", -1L);
        mageDeadTimestamps.put("Right", -1L);
    }
    static long mageDeadTimestamp = -1;
    static Date date = new Date();

    public static void updateMageDead(WTStates[] states) {
        for (WTStates state : states) {
            if (state.getName().equals(currentSide)) {

                boolean isMageDead = updateMageDeadState(state);

                if (isMageDead && mageDeadTimestamps.get(currentSide) == -1L) {
                    mageDeadTimestamp = System.currentTimeMillis();
                    date = new Date(mageDeadTimestamp);
                    String formattedTime = String.format("%02d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds());

                    Logger.debugLog("Mage has died at " + formattedTime + " on side " + currentSide);
                    mageDeadTimestamps.put(currentSide, mageDeadTimestamp);
                    Logger.debugLog("Set the mageDeadTimeStamp for " + currentSide + " to: " + mageDeadTimestamp);
                } else if (!isMageDead && mageDeadTimestamps.get(currentSide) != -1L) {
                    Logger.debugLog("Mage is no longer dead on side " + currentSide);
                    mageDeadTimestamps.put(currentSide, -1L);
                    Logger.debugLog("Set the mageDeadTimeStamp for " + currentSide + " to: " + -1L);
                }

                state.setMageDead(isMageDead);
            }
        }
    }

    private static boolean updateMageDeadState(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.MAGE_DEAD.getColor();

        boolean isMageDead = Client.isColorInRect(checkColor, checkRect, 5);

        Logger.debugLog("Result of mage dead state check: " + isMageDead);

        return isMageDead;
    }

    public static void updateBurnStates(WTStates[] states) {
        for (WTStates state : states) {
            // Update each boolean based on some conditions or actions
            state.setNeedsReburning(updateNeedsReburning(state));
            state.setNeedsFixing(updateNeedsFixing(state));
            state.setMageDead(updateMageDead(state));
        }
        updateIsGameGoing();
        updateCurrentHP();
        updateKindlingState();

    }

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

    public static void updateGameAt13() {
        gameAt13Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt13CheckRect, 10);
    }

    public static void updateGameAt20() {
        gameAt20Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt20CheckRect, 10);
    }

    public static void updateGameAt70() {
        gameAt70Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt70CheckRect, 10);
    }

    private static void updateShouldBurn() {
        shouldBurn = (
                // for regular mode
                gameAt13Percent && (inventoryHasKindlings || inventoryHasLogs) && Inventory.usedSlots() >= 18) && !burnOnly
                || isGameGoing && gameAt13Percent && (inventoryHasLogs || inventoryHasKindlings) && !burnOnly
                // for burn only!
                || burnOnly && isGameGoing && inventoryHasLogs && Inventory.isFull()
                || burnOnly && isGameGoing && inventoryHasLogs && gameAt13Percent;
    }

    private static void updateKindlingState() {
        inventoryHasKindlings = Inventory.contains(brumaKindling, 0.8);
        inventoryHasLogs = Inventory.contains(brumaRoot, 0.8);
    }

    private static void updateWaitingForGameToStart() {
        waitingForGameToStart = Client.isColorInRect(StateColor.GAME_GREEN_COLOR.getColor(), waitingForGameToStartRect, 10);
    }

    private static void updateWaitingForGameEnded() {
        waitingForGameEnded = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), waitingForGameEndedRect, 10);
    }

    public static void updateIsGameGoing() {
        isGameGoing = Client.isColorInRect(StateColor.GAME_GREEN_COLOR.getColor(), waitingForGameEndedRect, 10);
    }

}
