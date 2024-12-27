package agi_sdk.helpers;

import helpers.utils.Tile;

import java.awt.*;

public class StartTileStorage {
    public final Tile tile;
    public final Rectangle tapRectangle;

    public StartTileStorage(Tile tile, Rectangle tapRectangle) {
        this.tile = tile;
        this.tapRectangle = tapRectangle;
    }

    public Tile getTile() {
        return tile;
    }

    public Rectangle getTapRectangle() {
        return tapRectangle;
    }
}
