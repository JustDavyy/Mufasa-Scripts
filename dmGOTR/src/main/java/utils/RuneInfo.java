package utils;

import java.awt.*;

public enum RuneInfo {
    AIR(Color.decode("#e4e1e1"), 1, RuneType.ELEMENTAL),
    WATER(Color.decode("#151aa8"), 5, RuneType.ELEMENTAL),
    EARTH(Color.decode("6d490d"), 9, RuneType.ELEMENTAL),
    FIRE(Color.decode("#a82215"), 14, RuneType.ELEMENTAL),
    MIND(Color.decode("#484609"), 2, RuneType.CATALYTIC),
    BODY(Color.decode("#1820d0"), 20, RuneType.CATALYTIC),
    COSMIC(Color.decode("#e0de1a"), 27, RuneType.CATALYTIC),
    CHAOS(Color.decode("#e0a81a"), 35, RuneType.CATALYTIC),
    NATURE(Color.decode("#118b15"), 44, RuneType.CATALYTIC),
    LAW(Color.decode("#1a22e0"), 54, RuneType.CATALYTIC),
    DEATH(Color.decode("e4e1e1"), 65, RuneType.CATALYTIC),
    BLOOD(Color.decode("#8b1d11"), 77, RuneType.CATALYTIC);

    private final Color color;
    private final int requiredLevel;
    private final RuneType runeType;

    // Constructor for the enum
    RuneInfo(Color color, int requiredLevel, RuneType runeType) {
        this.color = color;
        this.requiredLevel = requiredLevel;
        this.runeType = runeType;
    }

    // Getter for color
    public Color getColor() {
        return color;
    }

    // Getter for required level
    public int getRequiredLevel() {
        return requiredLevel;
    }

    public RuneType getRuneType() {
        return runeType;
    }
}
