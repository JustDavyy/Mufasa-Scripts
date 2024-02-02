package utils;

import helpers.utils.Tile;

import java.awt.*;

public enum LocationInfo {
    VARROCK_EAST_COPPER(
            new Rectangle(393, 279, 10, 10), //Check1
            new Rectangle(443, 226, 14, 22), //Check2
            new Rectangle(1, 1, 1, 1), //Check3 (only 2 ores)
            new Rectangle(383, 272, 28, 31), //Click1
            new Rectangle(428, 229, 30, 21), //Click2
            new Rectangle(1, 1, 1, 1), //Click 3 (only 2 ores)
            new Tile(2847, 998) //Step
    ),
    VARROCK_EAST_IRON(
            new Rectangle(390, 266, 12, 14), //Check1
            new Rectangle(427, 224, 30, 20), //Check2
            new Rectangle(1, 1, 1, 1), //Check3 (only 2 ores)
            new Rectangle(383, 260, 33, 33), //Click1
            new Rectangle(428, 229, 30, 21), //Click2
            new Rectangle(1, 1, 1, 1), //Click 3 (only 2 ores)
            new Tile(2846, 992) //Step
    ),
    VARROCK_EAST_TIN(
            new Rectangle(382, 274, 21, 22), //Check1
            new Rectangle(431, 227, 21, 19), //Check2
            new Rectangle(1, 1, 1, 1), //Check3 (only 2 ores)
            new Rectangle(379, 271, 33, 27), //Click1
            new Rectangle(427, 230, 32, 26), //Click2
            new Rectangle(1, 1, 1, 1), //Click 3 (only 2 ores)
            new Tile(2840, 999) //Step
    ),
    VARROCK_WEST_CLAY(
            new Rectangle(394, 264, 13, 19), //Check1
            new Rectangle(430, 226, 20, 23), //Check2
            new Rectangle(1, 1, 1, 1), //Check3 (only 2 ores)
            new Rectangle(384, 295, 24, 25), //Click1
            new Rectangle(429, 255, 26, 27), //Click2
            new Rectangle(1, 1, 1, 1), //Click 3 (only 2 ores)
            new Tile(2704, 988) //Step
    ),
    VARROCK_WEST_IRON(
            new Rectangle(440, 299, 16, 20), //Check1
            new Rectangle(428, 219, 29, 14), //Check2
            new Rectangle(1, 1, 1, 1), //Check3 (only 2 ores)
            new Rectangle(429, 302, 26, 31), //Click1
            new Rectangle(457, 219, 22, 22), //Click2
            new Rectangle(1, 1, 1, 1), //Click 3 (only 2 ores)
            new Tile(2697, 994) //Step
    ),
    VARROCK_WEST_SILVER(
            new Rectangle(435, 314, 23, 33), //Check1
            new Rectangle(481, 268, 31, 25), //Check2
            new Rectangle(1, 1, 1, 1), //Check3 (only 2 ores)
            new Rectangle(435, 314, 23, 33), //Click1
            new Rectangle(481, 268, 31, 25), //Click2
            new Rectangle(1, 1, 1, 1), //Click 3 (only 2 ores)
            new Tile(2700, 996) //Step
    );
    // ...

    private final Rectangle checkLocation1;
    private final Rectangle checkLocation2;
    private final Rectangle checkLocation3;
    private final Rectangle clickLocation1;
    private final Rectangle clickLocation2;
    private final Rectangle clickLocation3;
    private final Tile stepLocation;

    LocationInfo(Rectangle checkLocation1, Rectangle checkLocation2, Rectangle checkLocation3, Rectangle clickLocation1, Rectangle clickLocation2, Rectangle clickLocation3, Tile stepLocation) {
        this.checkLocation1 = checkLocation1;
        this.checkLocation2 = checkLocation2;
        this.checkLocation3 = checkLocation3;
        this.clickLocation1 = clickLocation1;
        this.clickLocation2 = clickLocation2;
        this.clickLocation3 = clickLocation3;
        this.stepLocation = stepLocation;
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

    public Tile getStepLocation() {
        return stepLocation;
    }
}