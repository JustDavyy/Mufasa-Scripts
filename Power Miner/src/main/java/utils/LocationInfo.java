package utils;

import helpers.utils.RegionBox;

import java.awt.*;

public enum LocationInfo {
    varrockEastCopper(new Rectangle(1, 1, 1, 1), new Rectangle(2, 2, 2, 2), new RegionBox("VarrockEastCopper", 5573, 2440, 6110, 2952), new RegionBox("varrockEastMineRegion", 8733, 3147, 8138, 2615)),
    varrockEastIron(new Rectangle(1, 1, 1, 1), new Rectangle(2, 2, 2, 2), new RegionBox("VarrockEastIron", 5573, 2440, 6110, 2952), new RegionBox("VarrockEastMineRegion", 8733, 3147, 8138, 2615));
    // ...

    private final Rectangle checkLocation;
    private final Rectangle clickLocation;
    private final RegionBox stepLocation;
    private final RegionBox location;

    LocationInfo(Rectangle checkLocation, Rectangle clickLocation, RegionBox stepLocation, RegionBox location) {
        this.checkLocation = checkLocation;
        this.clickLocation = clickLocation;
        this.stepLocation = stepLocation;
        this.location = location;
    }

    public Rectangle getCheckLocation() {
        return checkLocation;
    }

    public Rectangle getClickLocation() {
        return clickLocation;
    }

    public RegionBox getStepLocation() {
        return stepLocation;
    }

    public RegionBox getLocation() {
        return location;
    }
}