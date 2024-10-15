package utils;


import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;
import static tasks.FletchBranches.isFletching;
import static tasks.GetBranches.gettingBranches;

public class StateUpdater {
    //    static Rectangle gameCheckRect = new Rectangle(54, 29, 5, 20);
    static Rectangle warmthPercentReadRect = new Rectangle(171, 28, 32, 17);
    static Rectangle energyPercentReadRect = new Rectangle(188, 43, 35, 17);
    static List<Color> blackColor = Arrays.asList(
            java.awt.Color.decode("#000001")
    );
    static Rectangle gameAt13CheckRect = new Rectangle(68, 50, 7, 10);
    static Rectangle gameAt20CheckRect = new Rectangle(100, 50, 9, 9);
    static Rectangle gameAt70CheckRect = new Rectangle(217, 50, 6, 9);
    static Rectangle fullWarmthBar = new Rectangle(57, 35, 198, 9);
    static Rectangle warmthAt60 = new Rectangle(174, 35, 7, 8);
    static int currentWarmth = 0;
    static Rectangle warmthCriticalLowRect = new Rectangle(58, 36, 16, 8);
    static Rectangle waitingForGameToStartRect = new Rectangle(245, 50, 9, 11);
    static Rectangle waitingForGameEndedRect = new Rectangle(56, 49, 5, 12);
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
                    // Convert the timestamp to LocalDateTime for formatting
                    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(mageDeadTimestamp), ZoneId.systemDefault());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String formattedTime = dateTime.format(formatter);

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

    private static void updateShouldEat() {
        // Use readDigitsInArea to check the warmth percentage
        int warmthPercentage = Chatbox.readDigitsInArea(warmthPercentReadRect, blackColor);

        // Check if the warmth percentage is valid and above the critical threshold
        if (warmthPercentage != -1 && warmthPercentage >= 30) {
            shouldEat = warmthPercentage <= 60; // Set shouldEat to true if 60 or lower, otherwise false
        } else {
            // Fallback to color check if the OCR result is invalid or below 30
            shouldEat = Client.isColorInRect(Color.decode("#007c69"), warmthAt60, 5);
        }

        // Update warmthCriticalLow based on the color check
        warmthCriticalLow = Client.isColorInRect(Color.decode("#007c69"), warmthCriticalLowRect, 5);
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
        updateShouldEat();
        updateKindlingState();

    }

    public static void updateStates(WTStates[] states) {
        //Update our position
        currentLocation = Walker.getPlayerPosition();

        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            updateShouldEat(); // Update our HP!

            for (WTStates state : states) {
                // Update each boolean based on some conditions or actions
                state.setFireAlive(updateFireAlive(state));
                state.setNeedsReburning(updateNeedsReburning(state));
                state.setNeedsFixing(updateNeedsFixing(state));
                state.setMageDead(updateMageDead(state));
            }

            // Update the game state boolean (true if wt game is 15% or less left.
            updateGameAt13();
            updateGameAt20();
            updateGameAt70();
            updateIsGameGoing();
            updateWaitingForGameToStart();
            updateWaitingForGameEnded();

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

    public static void updateGameAt13() {
        // Use readDigitsInArea to check the energy percentage
        int energyPercentage = Chatbox.readDigitsInArea(energyPercentReadRect, blackColor);

        // Check if the energy percentage is exactly 15 or lower (and valid)
        if (energyPercentage != -1 && energyPercentage <= 15) {
            if (!gameAt13Percent) {
                Logger.debugLog("Game at 15% true with CF OCR");
            }
            gameAt13Percent = true;
        } else if (energyPercentage > 15) {
            gameAt13Percent = false;
        } else {
            // Fallback to color check if the OCR result is invalid
            gameAt13Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt13CheckRect, 10);
        }
    }

    public static void updateGameAt20() {
        // Use readDigitsInArea to check the energy percentage
        int energyPercentage = Chatbox.readDigitsInArea(energyPercentReadRect, blackColor);

        // Check if the energy percentage is exactly 20 or lower (and valid)
        if (energyPercentage != -1 && energyPercentage <= 20) {
            if (!gameAt20Percent) {
                Logger.debugLog("Game at 20% true with CF OCR");
            }
            gameAt20Percent = true;
        } else if (energyPercentage > 20) {
            gameAt20Percent = false;
        } else {
            // Fallback to color check if the OCR result is invalid
            gameAt20Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt20CheckRect, 10);
        }
    }

    public static void updateGameAt70() {
        // Use readDigitsInArea to check the energy percentage
        int energyPercentage = Chatbox.readDigitsInArea(energyPercentReadRect, blackColor);

        // Check if the energy percentage is exactly 70 or lower (and valid)
        if (energyPercentage != -1 && energyPercentage <= 70) {
            if (!gameAt70Percent) {
                Logger.debugLog("Game at 70% true with CF OCR");
            }
            gameAt70Percent = true;
        } else if (energyPercentage > 70) {
            gameAt70Percent = false;
        } else {
            // Fallback to color check if the OCR result is invalid
            gameAt70Percent = Client.isColorInRect(StateColor.GAME_RED_COLOR.getColor(), gameAt70CheckRect, 10);
        }
    }

    private static void updateShouldBurn() {
        shouldBurn = (
                // for regular mode
                gameAt13Percent && (inventoryHasKindlings || inventoryHasLogs) && Inventory.usedSlots() >= 18) && !burnOnly
                || isGameGoing && gameAt13Percent && (inventoryHasLogs || inventoryHasKindlings) && !burnOnly
                // for burn only!
                || burnOnly && isGameGoing && inventoryHasLogs && Inventory.isFull() && Player.tileEquals(currentLocation, SideManager.getBranchTile())
                || burnOnly && isGameGoing && inventoryHasLogs && gameAt13Percent
                || burnOnly && isGameGoing && inventoryHasLogs && Player.tileEquals(currentLocation, SideManager.getBurnTile());
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
    
    public static void resetAllStates() {
        isGameGoing = false;
        waitingForGameEnded = false;
        waitingForGameToStart = false;
        inventoryHasKindlings = false;
        inventoryHasLogs = false;
        gameAt13Percent = false;
        gameAt20Percent = false;
        gameAt70Percent = false;
        shouldBurn = false;
        isBurning = false;
        isFletching = false;
        gettingBranches = false;
    }
}
