package utils;

import helpers.utils.Tile;

import java.awt.*;

public enum BranchDetails {
    LEFT_BRANCH(
            new Tile(627, 170),
            new Rectangle(360, 253, 25, 31),
            new Tile(627, 159),
            new Rectangle(360, 253, 25, 31) // Assuming it uses the same rectangle as leftBranchClickRect
    ),
    RIGHT_BRANCH(
            new Tile(648, 170), //branch tile
            new Rectangle(501, 245, 40, 33), //branch click rect
            new Tile(648, 159), //burn tile
            new Rectangle(501, 245, 40, 33) // Assuming it uses the same rectangle as rightBranchClickRect //burn click rect
    );

    private final Tile branchTile;
    private final Rectangle branchClickRect;
    private final Tile burnTile;
    private final Rectangle burnClickRect;

    BranchDetails(Tile branchTile, Rectangle branchClickRect, Tile burnTile, Rectangle burnClickRect) {
        this.branchTile = branchTile;
        this.branchClickRect = branchClickRect;
        this.burnTile = burnTile;
        this.burnClickRect = burnClickRect;
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
}
