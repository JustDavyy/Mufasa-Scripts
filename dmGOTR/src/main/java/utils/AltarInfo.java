package utils;

import helpers.utils.Area;
import helpers.utils.RegionBox;
import helpers.utils.Tile;

public class AltarInfo {
    private RegionBox regionBox;
    private Area area1;
    private Area area2;

    // Constructor
    public AltarInfo(RegionBox regionBox, Area area1, Area area2) {
        this.regionBox = regionBox;
        this.area1 = area1;
        this.area2 = area2;
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

    public static final AltarInfo AIR_ALTAR = new AltarInfo(
            new RegionBox(" ", 1, 1, 1, 1),
            new Area(new Tile(1,1), new Tile(1,1)),
            new Area(new Tile(1,1), new Tile(1,1))
    );
}
