package utils;

import helpers.utils.Area;
import helpers.utils.RegionBox;
import helpers.utils.Tile;

import static main.dmGOTR.airAltar;

public class AltarInfo {
    private RegionBox regionBox;
    private Area area1;
    private Area area2;

    // Constructor
    public AltarInfo(RegionBox regionBox, Area altarArea, Area entryArea) {
        this.regionBox = regionBox;
        this.area1 = altarArea;
        this.area2 = entryArea;
    }

    // Getters
    public RegionBox getRegionBox() {
        return regionBox;
    }

    public Area getArea1() {
        return area1;
    }

    public Area getArea2() {
        return area2;
    }

    @Override
    public String toString() {
        return "AltarInfo{" +
                "regionBox=" + regionBox +
                ", area1=" + area1 +
                ", area2=" + area2 +
                '}';
    }

    // The areas are probably not correct, btw.
    public static final AltarInfo AIR_ALTAR = new AltarInfo(
            airAltar,
            new Area(new Tile(302, 114), new Tile(315, 122)), // An around for the "inside" of the altar where the actual altar is
            new Area(new Tile(292, 104), new Tile(323, 132)) // An area for the outside, so we can check that we are WITHIN that altar area?.
    );
}
