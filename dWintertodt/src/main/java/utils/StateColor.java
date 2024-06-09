package utils;

import java.awt.*;

public enum StateColor {
    FIRE_ALIVE("#dd4f01"),
    NEEDS_REBURNING("#dd4f01"), // Assuming the negation of presence is handled elsewhere, keeping color same for simplicity
    NEEDS_FIXING("#777777"),
    MAGE_DEAD("#fe0200"),
    GAME_RED_COLOR("#cc0000");

    private final Color color;

    // Constructor
    StateColor(String colorCode) {
        this.color = Color.decode(colorCode);
    }

    // Getter
    public Color getColor() {
        return this.color;
    }
}