package utils;

import java.awt.*;
import java.util.Collections;

public enum RuneInfo {
    NOELEMENTAL(Color.decode("#000000"), 1, RuneType.ELEMENTAL),
    NOCATALYTIC(Color.decode("#000000"), 1, RuneType.CATALYTIC),
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

    // Getter for color as a List<Color>
    public java.util.List<Color> getColorAsList() {
        return Collections.singletonList(color);
    }

    // Getter for required level
    public int getRequiredLevel() {
        return requiredLevel;
    }

    // Getter for rune type
    public RuneType getRuneType() {
        return runeType;
    }

    // Getter for rune name
    public String getName() {
        return this.name(); // The name() method is built into Java enums
    }
}