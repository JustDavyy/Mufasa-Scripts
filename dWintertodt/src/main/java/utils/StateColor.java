package utils;

public enum StateColor {
    FIRE_ALIVE("#bb3b03"),
    NEEDS_REBURNING("#bb3b03"), // Assuming the negation of presence is handled elsewhere, keeping color same for simplicity
    NEEDS_FIXING("#777777"),
    MAGE_DEAD("#fe0200"),
    GAME_RED_COLOR("#cc0000");

    private final String colorCode;

    // Constructor
    StateColor(String colorCode) {
        this.colorCode = colorCode;
    }

    // Getter
    public String getColorCode() {
        return this.colorCode;
    }
}
