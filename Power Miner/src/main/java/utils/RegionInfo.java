package utils;

import helpers.utils.Area;
import helpers.utils.RegionBox;
import helpers.utils.Tile;

public enum RegionInfo {
    VARROCK_EAST(
            new RegionBox("VARROCK_EAST", 8225, 2600, 8699, 3167), //World region for this spot
            new Area(new Tile(8498, 2959), new Tile(8575, 3027)), // the Mine area
            new Area(new Tile(2796, 916), new Tile(2808, 929)) // the Bank area
    ),
    VARROCK_WEST(
            new RegionBox("VARROCK_WEST", 7916, 2548, 8285, 3123),
            new Area(new Tile(8077, 2995), new Tile(8140, 2930)),
            new Area(new Tile(2704, 886), new Tile(2718, 906))
    );

    private final RegionBox worldRegion;
    private final Area mineArea;
    private final Area bankArea;

    RegionInfo(RegionBox worldRegion, Area mineRegion, Area bankRegion) {
        this.worldRegion = worldRegion;
        this.mineArea = mineRegion;
        this.bankArea = bankRegion;
    }

    public RegionBox getWorldRegion() {
        return worldRegion;
    }
    public Area getMineArea() {
        return mineArea;
    }
    public Area getBankArea() {return bankArea;}
}
