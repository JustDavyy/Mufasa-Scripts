package utils;

import helpers.utils.Tile;

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

}
