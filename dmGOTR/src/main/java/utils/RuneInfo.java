package utils;

import java.awt.*;

public enum RuneInfo {
    AIR(Color.decode("#ffffff"), 1, RuneType.ELEMENTAL),
    WATER(Color.decode("#1e55e8"), 5, RuneType.ELEMENTAL),
    EARTH(Color.decode("#9d643a"), 9, RuneType.ELEMENTAL),
    FIRE(Color.decode("#e90e1d"), 14, RuneType.ELEMENTAL),
    MIND(Color.decode("#da7614"), 2, RuneType.CATALYTIC),
    BODY(Color.decode("#1e55e8"), 20, RuneType.CATALYTIC),
    COSMIC(Color.decode("#ffff00"), 27, RuneType.CATALYTIC),
    CHAOS(Color.decode("#da7614"), 35, RuneType.CATALYTIC),
    NATURE(Color.decode("#3ece27"), 44, RuneType.CATALYTIC),
    LAW(Color.decode("#1e55e8"), 54, RuneType.CATALYTIC),
    DEATH(Color.decode("#ffffff"), 65, RuneType.CATALYTIC),
    BLOOD(Color.decode("#be2633"), 77, RuneType.CATALYTIC);

    // LAW AND BODY HAS SAME COLOR
    // LAW: Found and drawn points: 172
    // BODY: Found and drawn points: 70
    // DETERMINE IF BODY/LAW based on found points
    // MIND AND CHAOS HAS SAME COLOR  - CHECK CHAOS WHITE COLOR #ffffff

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
