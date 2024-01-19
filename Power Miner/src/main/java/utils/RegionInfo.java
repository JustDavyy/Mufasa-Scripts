package utils;

import helpers.utils.RegionBox;

import java.awt.*;

public enum RegionInfo {
    VARROCK_EAST(
            new RegionBox("WorldRegion", 8225, 2600, 8699, 3167),
            new RegionBox("MineRegion", 8498, 2959, 8575, 3027)
    ),
    VARROCK_WEST(
            new RegionBox("WorldRegion", 7916, 2548, 8285, 3123),
            new RegionBox("MineRegion", 8077, 2995, 8140, 2930)
    );

    private final RegionBox worldRegion;
    private final RegionBox mineRegion;

    RegionInfo(RegionBox worldRegion, RegionBox mineRegion) {
        this.worldRegion = worldRegion;
        this.mineRegion = mineRegion;
    }

    public RegionBox getWorldRegion() {
        return worldRegion;
    }
    public RegionBox getMineRegion() {
        return mineRegion;
    }
}
