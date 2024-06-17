package utils;

import helpers.utils.Tile;
import main.dWintertodt;

import java.awt.*;

import static main.dWintertodt.currentSide;

public class SideManager {
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

    public static void updateStates() {
        StateUpdater.updateStates(dWintertodt.states);
    }

    // Method to get the burning state for the current side
    public static boolean getBurningState() {
        WTStates state = getState(currentSide.equals("Right") ? "Lower Right" : "Lower Left");
        return state != null && state.isFireAlive();
    }

    // Method to check if the current side needs fixing
    public static boolean getNeedsFixing() {
        WTStates state = getState(currentSide.equals("Right") ? "Lower Right" : "Lower Left");
        return state != null && state.isNeedsFixing();
    }

    // Method to check if the current side needs reburning
    public static boolean getNeedsReburning() {
        WTStates state = getState(currentSide.equals("Right") ? "Lower Right" : "Lower Left");
        return state != null && state.isNeedsReburning();
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
