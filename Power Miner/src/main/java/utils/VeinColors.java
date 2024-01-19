package utils;

import java.awt.*;

public enum VeinColors {
    COPPER_VEIN(
            new Color(0x865d3a),
            new Color(0x865d3a) //not done yet
    ),
    TIN_VEIN(
            new Color(0x665d5d),
            new Color(0x665d5d) //not done yet
    ),
    IRON_VEIN(
            new Color(0x4b2d23),
            new Color(0x4b2d23) //not done yet
    );

    // Enum setup
    private final Color activeColor;
    private final Color inactiveColor;

    VeinColors(Color activeColor, Color inactiveColor) {
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    public Color getInactiveColor() {
        return inactiveColor;
    }
}
