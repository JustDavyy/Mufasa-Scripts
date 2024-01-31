package utils;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public enum VeinColors {
    COPPER_VEIN(
            Arrays.asList(new Color(0x865d3a)),
            Arrays.asList(new Color(0x4c4646)) //not done yet
    ),
    TIN_VEIN(
            Arrays.asList(new Color(0x665d5d)),
            Arrays.asList(new Color(0x585252)) //not done yet
    ),
    IRON_VEIN(
            Arrays.asList(new Color(0x8d8181)),
            Arrays.asList(new Color(0x585252)) //not done yet
    ),
    CLAY(
            Arrays.asList(new Color(0x6f5932)),
            Arrays.asList(new Color(0x6f5932)) //not done yet
    ),
    SILVER(
            Arrays.asList(new Color(0x897e7d)), // Active color
            Arrays.asList(new Color(0x897e7d))  // Inactive color (example, adjust as needed)
    );

    // Enum setup
    private final List<Color> activeColor;
    private final List<Color> inactiveColor;

    VeinColors(List<Color> activeColor, List<Color> inactiveColor) {
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
    }

    public List<Color> getActiveColor() {
        return activeColor;
    }

    public List<Color> getInactiveColor() {
        return inactiveColor;
    }
}
