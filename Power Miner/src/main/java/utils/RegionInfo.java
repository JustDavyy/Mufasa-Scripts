package utils;

import helpers.utils.Area;
import helpers.utils.RegionBox;
import helpers.utils.Tile;

public enum RegionInfo {
    VARROCK_EAST(
            new RegionBox("VARROCK_EAST", 8225, 2600, 8699, 3167),
            new Area(new Tile(8498, 2959), new Tile(8575, 3027))
    ),
    VARROCK_WEST(
            new RegionBox("VARROCK_WEST", 7916, 2548, 8285, 3123),
            new Area(new Tile(8077, 2995), new Tile(8140, 2930))
    );

    private final RegionBox worldRegion;
    private final Area mineRegion;

    RegionInfo(RegionBox worldRegion, Area mineRegion) {
        this.worldRegion = worldRegion;
        this.mineRegion = mineRegion;
    }

    public RegionBox getWorldRegion() {
        return worldRegion;
    }
    public Area getMineRegion() {
        return mineRegion;
    }
}
