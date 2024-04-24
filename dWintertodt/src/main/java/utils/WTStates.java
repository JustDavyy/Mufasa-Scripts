package utils;

import java.awt.*;

public class WTStates {
    private String name;
    private Rectangle rectangle;
    private boolean fireAlive;
    private boolean needsReburning;
    private boolean needsFixing;
    private boolean mageDead;

    // Constructor
    public WTStates(String name, Rectangle checkRect, boolean fireAlive, boolean needsReburning, boolean needsFixing, boolean mageDead) {
        this.name = name;
        this.rectangle = checkRect;
        this.fireAlive = fireAlive;
        this.needsReburning = needsReburning;
        this.needsFixing = needsFixing;
        this.mageDead = mageDead;
    }

    public String getName() {
        return name;
    }

    // Getters
    public Rectangle getRectangle() {
        return rectangle;
    }

    public boolean isFireAlive() {
        return fireAlive;
    }

    public boolean isNeedsReburning() {
        return needsReburning;
    }

    public boolean isNeedsFixing() {
        return needsFixing;
    }

    public boolean isMageDead() {
        return mageDead;
    }

    // Setters
    public void setFireAlive(boolean fireAlive) {
        this.fireAlive = fireAlive;
    }

    public void setNeedsReburning(boolean needsReburning) {
        this.needsReburning = needsReburning;
    }

    public void setNeedsFixing(boolean needsFixing) {
        this.needsFixing = needsFixing;
    }

    public void setMageDead(boolean mageDead) {
        this.mageDead = mageDead;
    }
}
