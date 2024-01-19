package utils;

import helpers.utils.RegionBox;

import java.awt.*;

public enum LocationInfo {
    varrockEastCopper(new Rectangle(1, 1, 1, 1), new Rectangle(2, 2, 2, 2), new RegionBox("VarrockEastCopper", 5573, 2440, 6110, 2952)),
    varrockEastIron(new Rectangle(1, 1, 1, 1), new Rectangle(2, 2, 2, 2), new RegionBox("VarrockEastIron", 5573, 2440, 6110, 2952)),
    VarrockWestClay(new Rectangle(3, 3, 3, 3), new Rectangle(4, 4, 4, 4), new RegionBox("VarrockWestClay", 5673, 2540, 6210, 3052));
    // ...

    private final Rectangle checkLocation;
    private final Rectangle clickLocation;
    private final RegionBox regionBox;

    LocationInfo(Rectangle checkLocation, Rectangle clickLocation, RegionBox regionBox) {
        this.checkLocation = checkLocation;
        this.clickLocation = clickLocation;
        this.regionBox = regionBox;
    }

    public Rectangle getCheckLocation() {
        return checkLocation;
    }

    public Rectangle getClickLocation() {
        return clickLocation;
    }

    public RegionBox getRegionBox() {
        return regionBox;
    }
}