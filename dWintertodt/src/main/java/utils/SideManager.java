package utils;

import helpers.utils.Tile;
import main.dWintertodt;

import java.awt.*;
import java.util.Random;

import static helpers.Interfaces.Player;
import static main.dWintertodt.*;

public class SideManager {
    static Random random = new Random();

    public static Tile getBurnTile() {
        if (currentSide.equals("Right")) {
            return BranchDetails.RIGHT_BRANCH.getBurnTile();
        } else {
            return BranchDetails.LEFT_BRANCH.getBurnTile();
        }
    }

    public static Tile getBranchTile() {
        if (currentSide.equals("Right")) {
            return BranchDetails.RIGHT_BRANCH.getBranchTile();
        } else {
            return BranchDetails.LEFT_BRANCH.getBranchTile();
        }
    }

    public static boolean isWithinGameArea() {
        if (currentSide.equals("Right")) {
            return Player.within(rightWTArea, WTRegion);
        } else {
            return Player.within(leftWTArea, WTRegion);
        }
    }

    public static Tile[] getDoorToGamePath() {
        if (currentSide.equals("Right")) {
            return wtDoorToRightSide;
        } else {
            return wtDoorToLeftSide;
        }
    }

    public static Rectangle getBurnRect() {
        if (currentSide.equals("Right")) {
            return BranchDetails.RIGHT_BRANCH.getBurnClickRect();
        } else {
            return BranchDetails.LEFT_BRANCH.getBurnClickRect();
        }
    }

    public static Rectangle getBranchRect() {
        if (currentSide.equals("Right")) {
            return BranchDetails.RIGHT_BRANCH.getBranchClickRect();
        } else {
            return BranchDetails.LEFT_BRANCH.getBranchClickRect();
        }
    }

    public static Rectangle getBurnSwitchSideRect() {
        if (currentSide.equals("Right")) {
            return BranchDetails.RIGHT_BRANCH.getSwitchSideBurnRect();
        } else {
            return BranchDetails.LEFT_BRANCH.getSwitchSideBurnRect();
        }
    }

    public static Rectangle getBranchSwitchSideRect() {
        if (currentSide.equals("Right")) {
            return BranchDetails.RIGHT_BRANCH.getSwitchSideBranchRect();
        } else {
            return BranchDetails.LEFT_BRANCH.getSwitchSideBranchRect();
        }
    }

    public static void updateStates() {
        StateUpdater.updateStates(dWintertodt.states);
    }

    // Method to get the burning state for the current side
    public static boolean getBurningState() {
        WTStates state = getState(currentSide.equals("Right") ? "Right" : "Left");
        return state != null && state.isFireAlive();
    }

    // Method to check if the current side needs fixing
    public static boolean getNeedsFixing() {
        WTStates state = getState(currentSide.equals("Right") ? "Right" : "Left");
        return state != null && state.isNeedsFixing();
    }

    // Method to check if the current side needs reburning
    public static boolean getNeedsReburning() {
        WTStates state = getState(currentSide.equals("Right") ? "Right" : "Left");
        return state != null && state.isNeedsReburning();
    }

    // Method to check if the current side mage is dead
    public static boolean getMageDead() {
        WTStates state = getState(currentSide.equals("Right") ? "Right" : "Left");
        return state != null && state.isMageDead();
    }

    public static boolean isMageDeadForAtLeast(int seconds) {
        if (StateUpdater.mageDeadTimestamp == -1 || !getMageDead()) {
            return false; // Mage is not dead
        }

        long currentTime = System.currentTimeMillis();
        return (currentTime - StateUpdater.mageDeadTimestamp) >= (seconds * 1000L);
    }

    // Method to pick a random side
    public static String pickRandomSide() {
        boolean pick = random.nextBoolean(); // Generate a random boolean

        // Set currentSide based on the random boolean value
        return pick ? "Right" : "Left";
    }

    // Helper method to get a WTStates object by its name
    private static WTStates getState(String name) {
        for (WTStates state : dWintertodt.states) {
            if (state.getName().equals(name)) {
                return state;
            }
        }
        return null; // Return null if no state with the given name is found
    }
}
