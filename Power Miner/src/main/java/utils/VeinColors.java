package utils;

import java.awt.*;

public enum VeinColors {
    COPPER_VEIN(
            new Color(0x865d3a),
            new Color(0x4c4646) //not done yet
    ),
    TIN_VEIN(
            new Color(0x665d5d),
            new Color(0x585252) //not done yet
    ),
    IRON_VEIN(
            new Color(0x8d8181),
            new Color(0x585252) //not done yet
    ),
    CLAY(
            new Color(0x6f5932),
            new Color(0x6f5932) //not done yet
    ),
    SILVER(
            new Color(0x897e7d),
            new Color(0x897e7d) //not done yet
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
