package utils;

import helpers.utils.Tile;

import java.awt.*;

public enum BranchDetails {
    LEFT_BRANCH(
            new Tile(6487, 15701, 0), //branch tile
            new Rectangle(350, 258, 62, 32), //branch click rect
            new Tile(6487, 15733, 0), // burn tile
            new Rectangle(397, 179, 43, 33), //burn click rect
            new Rectangle(820, 95, 21, 17), // burn switch side click rect
            new Rectangle(819, 61, 23, 18) // branch switch side click rect
    ),
    RIGHT_BRANCH(
            new Tile(6551, 15701, 0), //branch tile
            new Rectangle(509, 263, 43, 29), //branch click rect
            new Tile(6551, 15733, 0), //burn tile
            new Rectangle(465, 162, 63, 48), //burn click rect
            new Rectangle(757, 94, 21, 19), // burn switch side click rect
            new Rectangle(755, 62, 22, 19) // branch switch side click rect
    );

    private final Tile branchTile;
    private final Rectangle branchClickRect;
    private final Tile burnTile;
    private final Rectangle burnClickRect;
    private final Rectangle switchSideBurnRect;
    private final Rectangle switchSideBranchRect;

    BranchDetails(Tile branchTile, Rectangle branchClickRect, Tile burnTile, Rectangle burnClickRect, Rectangle switchSideBurnRect, Rectangle switchSideBranchRect) {
        this.branchTile = branchTile;
        this.branchClickRect = branchClickRect;
        this.burnTile = burnTile;
        this.burnClickRect = burnClickRect;
        this.switchSideBurnRect = switchSideBurnRect;
        this.switchSideBranchRect = switchSideBranchRect;
    }

    public Tile getBranchTile() {
        return branchTile;
    }

    public Rectangle getBranchClickRect() {
        return branchClickRect;
    }

    public Tile getBurnTile() {
        return burnTile;
    }

    public Rectangle getBurnClickRect() {
        return burnClickRect;
    }

    public Rectangle getSwitchSideBurnRect() {
        return switchSideBurnRect;
    }

    public Rectangle getSwitchSideBranchRect() {
        return switchSideBranchRect;
    }

}
