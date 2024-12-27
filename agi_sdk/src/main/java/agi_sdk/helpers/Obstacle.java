package agi_sdk.helpers;

import helpers.utils.Area;
import helpers.utils.Tile;

import java.awt.*;
import java.util.List;

public class Obstacle {
    public String name;
    public Area area;
    public Tile startTile;
    public Tile endTile;
    public Rectangle pressArea;
    public Rectangle instantPressArea;
    public Tile prevEndTile;
    public java.util.List<MarkHandling> markHandling; // Change to a list of MarkHandling
    public boolean checkForMark;
    public Area failArea;
    public boolean checkForFail;

    public Obstacle(String name, Area area, Tile startTile, Tile endTile, Rectangle pressArea,
                    Rectangle instantPressArea, Tile prevEndTile, List<MarkHandling> markHandling, boolean checkForMark, Area failArea, boolean checkForFail) {
        this.name = name;
        this.area = area;
        this.startTile = startTile;
        this.endTile = endTile;
        this.pressArea = pressArea;
        this.instantPressArea = instantPressArea;
        this.prevEndTile = prevEndTile;
        this.markHandling = markHandling;
        this.checkForMark = checkForMark;
        this.failArea = failArea;
        this.checkForFail = checkForFail;
    }
}
