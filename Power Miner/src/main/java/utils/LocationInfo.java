package utils;

import helpers.utils.RegionBox;

import java.awt.*;

public enum LocationInfo {
    varrockEastCopper(
            new Rectangle(393, 279, 10, 10), //Check1
            new Rectangle(443, 226, 14, 22), //Check2
            new Rectangle(1, 1, 1, 1), //Check3 (only 2 ores)
            new Rectangle(383, 272, 28, 31), //Click1
            new Rectangle(428, 229, 30, 21), //Click2
            new Rectangle(1, 1, 1, 1), //Click 3 (only 2 ores)
            new RegionBox("VarrockEastCopper", 8542, 2992, 8542, 2992), //Step
            new RegionBox("VarrockEastMineRegion", 8733, 3147, 8138, 2615) //Location region
    ),
    varrockEastIron(
            new Rectangle(390, 266, 12, 14), //Check1
            new Rectangle(427, 224, 30, 20), //Check2
            new Rectangle(1, 1, 1, 1), //Check3 (only 2 ores)
            new Rectangle(383, 260, 33, 33), //Click1
            new Rectangle(428, 229, 30, 21), //Click2
            new Rectangle(1, 1, 1, 1), //Click 3 (only 2 ores)
            new RegionBox("VarrockEastIron", 8538, 2976, 8538, 2976), //Step
            new RegionBox("VarrockEastMineRegion", 8733, 3147, 8138, 2615) //Location region
    );
    // ...

    private final Rectangle checkLocation1;
    private final Rectangle checkLocation2;
    private final Rectangle checkLocation3;
    private final Rectangle clickLocation1;
    private final Rectangle clickLocation2;
    private final Rectangle clickLocation3;
    private final RegionBox stepLocation;
    private final RegionBox worldRegion;

    LocationInfo(Rectangle checkLocation1, Rectangle checkLocation2, Rectangle checkLocation3, Rectangle clickLocation1, Rectangle clickLocation2, Rectangle clickLocation3, RegionBox stepLocation, RegionBox worldRegion) {
        this.checkLocation1 = checkLocation1;
        this.checkLocation2 = checkLocation2;
        this.checkLocation3 = checkLocation3;
        this.clickLocation1 = clickLocation1;
        this.clickLocation2 = clickLocation2;
        this.clickLocation3 = clickLocation3;
        this.stepLocation = stepLocation;
        this.worldRegion = worldRegion;
    }

    public Rectangle getCheckLocation1() {
        return checkLocation1;
    }
    public Rectangle getCheckLocation2() {
        return checkLocation2;
    }
    public Rectangle getCheckLocation3() {
        return checkLocation3;
    }

    public Rectangle getClickLocation1() {
        return clickLocation1;
    }
    public Rectangle getClickLocation2() {
        return clickLocation2;
    }
    public Rectangle getClickLocation3() {
        return clickLocation3;
    }

    public RegionBox getStepLocation() {
        return stepLocation;
    }

    public RegionBox getWorldRegion() {
        return worldRegion;
    }
}