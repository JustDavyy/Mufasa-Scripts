package utils;

import helpers.utils.Area;
import helpers.utils.Tile;

public enum AltarInfo {
    AIR(new Area(
            new Tile(11322, 19012, 0),
            new Tile(11443, 19133, 0))),
    WATER(new Area(
            new Tile(10767, 18958, 0),
            new Tile(10995, 19184, 0))),
    EARTH(new Area(
            new Tile(10519, 18989, 0),
            new Tile(10730, 19190, 0))),
    FIRE(new Area(
            new Tile(10230, 18970, 0),
            new Tile(10475, 19192, 0))),
    MIND(new Area(
            new Tile(11022, 18991, 0),
            new Tile(11228, 19192, 0))),
    BODY(new Area(
            new Tile(10009, 19014, 0),
            new Tile(10164, 19182, 0))),
    COSMIC(new Area(
            new Tile(8471, 18963, 0),
            new Tile(8682, 19179, 0))),
    CHAOS(new Area(
            new Tile(8984, 19001, 0),
            new Tile(9198, 19192, 0))),
    NATURE(new Area(
            new Tile(9490, 18976, 0),
            new Tile(9701, 19195, 0))),
    LAW(new Area(
            new Tile(9772, 18973, 0),
            new Tile(9944, 19166, 0))),
    DEATH(new Area(
            new Tile(8729, 18997, 0),
            new Tile(8937, 19187, 0))),
    BLOOD(new Area(
            new Tile(12811, 18960, 0),
            new Tile(13040, 19189, 0)));

    private final Area area;

    // Enum constructor
    AltarInfo(Area area) {
        this.area = area;
    }

    // Getter for the area
    public Area getArea() {
        return area;
    }

    @Override
    public String toString() {
        return name() + ": " + area;
    }
}