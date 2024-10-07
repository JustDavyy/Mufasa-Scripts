package dAgility.Tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import dAgility.dAgility;
import dAgility.utils.Task;

import java.awt.*;
import java.util.Random;

import static helpers.Interfaces.*;
import static dAgility.dAgility.*;

public class Canifis extends Task {

    Color mogColor = new Color(203,138,25);
    Rectangle obstacle1InstantPressArea = new Rectangle(406, 195, 13, 18);
    Rectangle obstacle1PressArea = new Rectangle(438, 244, 11, 14);
    Area obstacle1Area = new Area(
            new Tile(74, 392),
            new Tile(106, 433)
    );
    Tile obstacle1Start = new Tile(100,413);
    Tile obstacle1End = new Tile(283, 371);
    Tile obstacle1BuggedEnd = new Tile(282, 375);
    Point obstacle2MogPoint = new Point(447,227);
    Rectangle obstacle2InstantPressArea = new Rectangle(428, 187, 11, 8);
    Rectangle obstacle2PressArea = new Rectangle(441, 255, 13, 10);
    Area obstacle2Area = new Area(
            new Tile(279, 361),
            new Tile(292, 373)
    );
    Tile obstacle2Start = new Tile(282,364);
    Tile obstacle2End = new Tile(278, 355);
    Point obstacle3MogPoint = new Point(431,258);
    Rectangle obstacle3InstantPressArea = new Rectangle(346, 258, 10, 25);
    Rectangle obstacle3PressArea = new Rectangle(412, 256, 14, 28);
    Area obstacle3Area = new Area(
            new Tile(268, 349),
            new Tile(282, 358)
    );
    Tile obstacle3Start = new Tile(273,355);
    Tile obstacle3End = new Tile(265,355);
    Point obstacle4MogPoint = new Point(408,327);
    Area obstacle4FailArea = new Area(
            new Tile(60, 394),
            new Tile(71, 414)
    );
    Tile[] obstacle4FailPath = new Tile[] {
            new Tile(80, 409),
            new Tile(97, 414)
    };
    Rectangle obstacle4InstantPressArea = new Rectangle(311, 342, 13, 34);
    Rectangle obstacle4PressArea = new Rectangle(412, 258, 14, 28);
    Area obstacle4Area = new Area(
            new Tile(251, 351),
            new Tile(268, 365)
    );
    Tile obstacle4Start = new Tile(258,362);
    Tile obstacle4End = new Tile(439,462);
    Point obstacle5MogPoint = new Point(427,323);
    Rectangle obstacle5InstantPressArea = new Rectangle(414, 390, 19, 43);
    Rectangle obstacle5PressArea = new Rectangle(437, 277, 14, 34);
    Area obstacle5Area = new Area(
            new Tile(431, 460),
            new Tile(442, 475)
    );
    Tile obstacle5Start = new Tile(438,470);
    Tile obstacle5End = new Tile(246,379);
    Point obstacle6MogPoint = new Point(446,309);
    Point[] obstacle6InstantPressAreas = new Point[] {new Point(490,322), new Point(482,322), new Point(476,323), new Point(492,321), new Point(480,322)};
    Rectangle obstacle6PressArea = new Rectangle(437, 287, 20, 5);
    Area obstacle6Area = new Area(
            new Tile(243, 376),
            new Tile(255, 387)
    );
    Tile obstacle6Start = new Tile(249,382);
    Tile obstacle6End = new Tile(452,493);
    Rectangle obstacle7InstantPressArea = new Rectangle(689, 255, 18, 24);
    Rectangle obstacle7PressArea = new Rectangle(466, 255, 14, 32);
    Area obstacle7Area = new Area(
            new Tile(449, 488),
            new Tile(474, 505)
    );
    Tile obstacle7Start = new Tile(470,493);
    Tile obstacle7End = new Tile(289,392);
    Rectangle obstacle8InstantPressArea = new Rectangle(440, 168, 12, 13);
    Rectangle obstacle8PressArea = new Rectangle(439, 243, 15, 17);
    Area obstacle8Area = new Area(
            new Tile(284, 382),
            new Tile(298, 396)
    );
    Tile obstacle8Start = new Tile(289,384);
    Tile obstacle8End = new Tile(103,417);


    public Canifis(){
        super();
        super.name = "Canifis";
    }
    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        return (dAgility.courseChosen.equals("Canifis"));
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        // If player within obstacle area
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos (x:" + currentLocation.x() + "|y:" + currentLocation.y() + ")");
        if(Player.isTileWithinArea(currentLocation, obstacle1Area)) {
            if (Chatbox.findChatboxMenu() != null) {
                Logger.debugLog("A chat window is open for some reason... closing it!");
                Client.sendKeystroke("KEYCODE_SPACE");
            }
            if(tileEquals(obstacle8End, currentLocation)){
                dAgility.lapCount = dAgility.lapCount + 1;
                Paint.setStatus("Traverse obstacle 1");
                Logger.debugLog("Traverse obstacle 1.");
                Client.tap(obstacle1InstantPressArea);
                Condition.wait(() -> check2Tiles(obstacle1End, obstacle1BuggedEnd), 100, 70);
            }
            else {
                carryOutObstacle_tap(obstacle1Start, obstacle1End, obstacle1PressArea);
            }
            return true;
        }
        else if (tileEquals(currentLocation, obstacle1BuggedEnd)) {
            Logger.debugLog("Seems like we are on the bugged tree tile, thanks jamflex...");
            Logger.debugLog("Getting us out of this shit spot.");
            Paint.setStatus("Traverse obstacle 2");
            carryOutObstacle_tap(obstacle2Start, obstacle2End, obstacle2PressArea);
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle2Area)) {
            if(tileEquals(obstacle1End, currentLocation)) {
                Condition.sleep(generateRandomDelay(450, 900));
                if(mogged(obstacle2MogPoint, mogColor)) {
                    currentLocation = Walker.getPlayerPosition();
                }
            }
            if(tileEquals(obstacle1End, currentLocation)){
                Paint.setStatus("Traverse obstacle 2");
                Logger.debugLog("Traverse obstacle 2.");
                Client.tap(obstacle2InstantPressArea);
                Condition.wait(() -> atTile(obstacle2End), 100, 60);
            }
            else {
                carryOutObstacle_tap(obstacle2Start, obstacle2End, obstacle2PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle3Area)) {
            if(tileEquals(obstacle2End, currentLocation)) {
                Condition.sleep(generateRandomDelay(450, 900));
                if(mogged(obstacle3MogPoint, mogColor)) {
                    currentLocation = Walker.getPlayerPosition();
                }
            }
            if(tileEquals(obstacle2End, currentLocation)){
                Paint.setStatus("Traverse obstacle 3");
                Logger.debugLog("Traverse obstacle 3.");
                Client.tap(obstacle3InstantPressArea);
                Condition.wait(() -> atTile(obstacle3End), 100, 52);
            }
            else {
                carryOutObstacle_tap(obstacle3Start, obstacle3End, obstacle3PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle4Area)) {
            if(tileEquals(obstacle3End, currentLocation)) {
                Condition.sleep(generateRandomDelay(1750, 2000));
                if(mogged(obstacle4MogPoint, mogColor)) {
                    currentLocation = Walker.getPlayerPosition();
                }
            }
            if(tileEquals(obstacle3End, currentLocation)){
                Paint.setStatus("Traverse obstacle 4");
                Logger.debugLog("Traverse obstacle 4.");
                Client.tap(obstacle4InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle4End, obstacle4FailArea), 100, 65);

            }
            else {
                carryOutObstacle_tap(obstacle4Start, obstacle4End, obstacle4PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle4FailArea)){
            Paint.setStatus("Walk back from fail area 4");
            Walker.walkPath(obstacle4FailPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 100, 60);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle5Area)) {
            if(tileEquals(obstacle4End, currentLocation)) {
                Condition.sleep(generateRandomDelay(450, 900));
                if(mogged(obstacle5MogPoint, mogColor)) {
                    currentLocation = Walker.getPlayerPosition();
                }
            }
            if(tileEquals(obstacle4End, currentLocation)){
                Paint.setStatus("Traverse obstacle 5");
                Logger.debugLog("Traverse obstacle 5.");
                Client.tap(obstacle5InstantPressArea);
                Condition.wait(() -> atTile(obstacle5End), 100, 80);
            }
            else {
                carryOutObstacle_tap(obstacle5Start, obstacle5End, obstacle5PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle6Area)) {
            if(tileEquals(obstacle5End, currentLocation)) {
                Condition.sleep(generateRandomDelay(450, 900));
                if(mogged(obstacle6MogPoint, mogColor)){
                    currentLocation = Walker.getPlayerPosition();
                }
            }
            if(tileEquals(obstacle5End, currentLocation)){
                Paint.setStatus("Traverse obstacle 6");
                Logger.debugLog("Traverse obstacle 6.");
                int randNum = new Random().nextInt(obstacle6InstantPressAreas.length);
                Client.tap(obstacle6InstantPressAreas[randNum].x, obstacle6InstantPressAreas[randNum].y);
                Condition.wait(() -> atTile(obstacle6End), 100, 80);
            }
            else {
                carryOutObstacle_tap(obstacle6Start, obstacle6End, obstacle6PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle7Area)) {
            if(tileEquals(currentLocation, obstacle6End)){
                Paint.setStatus("Traverse obstacle 7");
                Logger.debugLog("Traverse obstacle 7.");
                Client.tap(obstacle7InstantPressArea);
                Condition.wait(() -> atTile(obstacle7End), 100, 60);
            }
            else {
                carryOutObstacle_tap(obstacle7Start, obstacle7End, obstacle7PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle8Area)) {
            if(tileEquals(currentLocation, obstacle7End)){
                Paint.setStatus("Traverse obstacle 8");
                Logger.debugLog("Traverse obstacle 8.");
                Client.tap(obstacle8InstantPressArea);
                Condition.wait(() -> atTile(obstacle8End), 100, 60);
            }
            else {
                carryOutObstacle_tap(obstacle8Start, obstacle8End, obstacle8PressArea);
            }
            return true;
        }
        return false;
    }

    public void carryOutObstacle_tap(Tile obstacleStart, Tile obstacleEnd, Rectangle pressArea) {
        Paint.setStatus("Traverse obstacle X");
        if (!atTile(obstacleStart)) {
            Logger.debugLog("Carrying out obstacle...");
            Walker.step(obstacleStart);
            Condition.wait(() -> atTile(obstacleStart), 100, 100);
        }
        if (atTile(obstacleStart)) {
            Logger.debugLog("Player is at start of obstacle.");
            Client.tap(pressArea);
        }
    }
}
