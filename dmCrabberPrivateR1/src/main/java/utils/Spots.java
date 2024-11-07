package utils;

import helpers.utils.Tile;

public enum Spots {
    EAST1(
            new Tile(6935, 13629, 0), //crab tile
            new Tile(6956, 13747, 0) // reset path
    ),
    EAST2(
            new Tile(6996, 13626, 0), //crab tile
            new Tile(6949, 13765, 0) // reset path
    ),
    EAST3(
            new Tile(7059, 13617, 0), //crab tile
            new Tile(7049, 13786, 0) // reset path
    ),
    EAST4(
            new Tile(7101, 13621, 0), //crab tile
            new Tile(7164, 13773, 0) // reset path
    ),
    WEST1(
            new Tile(6775, 13633, 0), //crab tile
            new Tile(6883, 13741, 0) // reset path
    ),
    WEST2(
            new Tile(6699, 13661, 0), //crab tile
            new Tile(6877, 13725, 0) // reset path
    );
    private final Tile spot;
    private final Tile resetSpot;

    Spots(Tile spot, Tile resetSpot) {
        this.spot = spot;
        this.resetSpot = resetSpot;
    }

    public Tile getSpotTile() {
        return spot;
    }

    public Tile getResetSpot() {
        return resetSpot;
    }

}
