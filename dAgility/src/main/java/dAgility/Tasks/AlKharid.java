package dAgility.Tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import dAgility.utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static helpers.Interfaces.*;
import static dAgility.dAgility.*;

public class AlKharid extends Task {
    private static final Random random = new Random();
    List<Color> obstacleColors = Arrays.asList(
            Color.decode("#20ff25"),
            Color.decode("#7e9c7c"),
            Color.decode("#1fff25"),
            Color.decode("#456620"),
            Color.decode("#21ff27"),
            Color.decode("#21f124"),
            Color.decode("#20f625"),
            Color.decode("#29d325"),
            Color.decode("#22ff28"),
            Color.decode("#b8cd9b"),
            Color.decode("#b2c791"),
            Color.decode("#a1a95a"),
            Color.decode("#a6ac5e"),
            Color.decode("#20ff26"),
            Color.decode("#21ff26"),
            Color.decode("#29dd2c")
    );
    Color mogColor = new Color(Integer.parseInt("c98818", 16)); // hex code #c98818 for MoG on this course
    Rectangle screenROI = new Rectangle(190, 243, 431, 273);
    Area obstacle1Area = new Area(
            new Tile(58, 183),
            new Tile(75, 197)
    );
    Tile obstacle1Start = new Tile(66, 187);
    Tile obstacle1End = new Tile(270,196);
    Rectangle obstacle2InstantPressArea = new Rectangle(416, 495, 12, 16);
    Rectangle obstacle2PressArea = new Rectangle(439, 285, 11, 9);
    Area obstacle2Area = new Area(
            new Tile(262, 193),
            new Tile(281, 217)
    );
    Tile obstacle2Start = new Tile(269, 210);
    Tile obstacle2End = new Tile(269, 223);
    Tile obstacle2End2 = new Tile(269, 223);
    Rectangle obstacle3InstantPressArea = new Rectangle(366, 373, 20, 12);
    Rectangle obstacle3PressArea = new Rectangle(496, 266, 8, 5);
    Area obstacle3Area = new Area(
            new Tile(253, 220),
            new Tile(272, 241)
    );
    Tile obstacle3Start = new Tile(262, 231);
    Tile obstacle3End = new Tile(285, 231);
    Tile[] obstacle4FailPath = new Tile[] {
            new Tile(112, 217),
            new Tile(98, 210),
            new Tile(84, 205),
            new Tile(76, 191),
            new Tile(68, 189)
    };
    Area obstacle4FailArea = new Area(
            new Tile(104, 223),
            new Tile(121, 242)
    );
    Rectangle obstacle4InstantPressArea = new Rectangle(797, 312, 26, 14);
    Rectangle obstacle4PressArea = new Rectangle(478, 260, 13, 14);
    Area obstacle4Area = new Area(
            new Tile(280, 215),
            new Tile(313, 243)
    );
    Tile obstacle4Start = new Tile(307,235);
    Tile obstacle4End = new Tile(227, 230);
    Rectangle obstacle5InstantPressArea = new Rectangle(486, 210, 19, 19);
    Rectangle obstacle5PressArea = new Rectangle(453, 219, 18, 22);
    Area obstacle5Area = new Area(
            new Tile(220, 223),
            new Tile(235, 237)
    );
    Tile obstacle5Start = new Tile(186, 186);
    Tile obstacle5End = new Tile(230, 215);
    Rectangle obstacle6InstantPressArea = new Rectangle(426, 189, 13, 2);
    Rectangle obstacle6PressArea = new Rectangle(436, 239, 14, 3);
    Area obstacle6Area = new Area(
            new Tile(219, 196),
            new Tile(237, 219)
    );
    Tile obstacle6Start = new Tile(228, 210);
    Tile obstacle6End = new Tile(327, 212);
    Rectangle obstacle7InstantPressArea = new Rectangle(396, 185, 10, 7);
    Rectangle obstacle7PressArea = new Rectangle(421, 246, 10, 12);
    Area obstacle7Area = new Area(
            new Tile(315, 197),
            new Tile(333, 217)
    );
    Tile obstacle7Start = new Tile(325, 206);
    Tile obstacle7End = new Tile(309,203);
    Rectangle obstacle8InstantPressArea = new Rectangle(410, 179, 15, 9);
    Rectangle obstacle8PressArea = new Rectangle(440, 242, 13, 15);
    Area obstacle8Area = new Area(
            new Tile(298, 189),
            new Tile(317, 209)
    );
    Tile obstacle8Start = new Tile(306, 196);
    Tile obstacle8End = new Tile(100, 192);
    Area obstacleEndArea = new Area(
            new Tile(95, 185),
            new Tile(112, 203)
    );
    Tile[] obstacleEndPath = new Tile[] {
            new Tile(93, 191),
            new Tile(86, 190),
            new Tile(79, 188),
            new Tile(68, 187)
    };

    Rectangle roof1MoG1 = new Rectangle(517, 377, 9, 10);
    Rectangle roof2MoG1 = new Rectangle(312, 445, 10, 9);
    Rectangle roof3MoG1 = new Rectangle(491, 210, 8, 7);

    public AlKharid(){
        super();
        super.name = "AlKharid";
    }
    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        return (courseChosen.equals("Al Kharid"));
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        // If player within obstacle area
        Paint.setStatus("Fetch player position");
        Logger.debugLog("Player pos (x:" + currentLocation.x() + "|y:" + currentLocation.y() + ")");
        if(Player.isTileWithinArea(currentLocation, obstacle1Area)) {
            carryOutStartObstacle_tap();
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle2Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof1MoG1, mogColor)) {
                Condition.sleep(generateRandomDelay(1700, 1900));
                Client.tap(new Rectangle(349, 359, 9, 10));
                Condition.wait(() -> check2Tiles(obstacle2End, obstacle2End2), 100, 100);
            } else if(tileEquals(obstacle1End, currentLocation)){
                Paint.setStatus("Traverse obstacle 2");
                Logger.debugLog("Traverse obstacle 2.");
                Client.tap(obstacle2InstantPressArea);
                Condition.wait(() -> check2Tiles(obstacle2End, obstacle2End2), 100, 100);
            }
            else {
                carryOutObstacle_tap(obstacle2Start, obstacle2End, obstacle2PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle3Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof2MoG1, mogColor)) {
                Condition.sleep(generateRandomDelay(3450, 3600));
                Client.tap(new Rectangle(494, 219, 7, 8));
                Condition.wait(() -> atTile(obstacle3End), 100, 70);
            } else if(check2Tiles(obstacle2End, obstacle2End2)){
                Paint.setStatus("Traverse obstacle 3");
                Logger.debugLog("Traverse obstacle 3.");
                Client.tap(obstacle3InstantPressArea);
                Condition.wait(() -> atTile(obstacle3End), 100, 70);
            }
            else {
                carryOutObstacle_tap(obstacle3Start, obstacle3End, obstacle3PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle4Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof3MoG1, mogColor)) {
                Condition.sleep(generateRandomDelay(1950, 2100));
                Client.tap(new Rectangle(769, 390, 33, 19));
                Condition.wait(() -> atTile(obstacle4End), 100, 215);
            } else if(tileEquals(obstacle3End, currentLocation)) {
                Paint.setStatus("Traverse obstacle 4");
                Logger.debugLog("Traverse obstacle 4.");
                Client.tap(obstacle4InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle4End, obstacle4FailArea), 100, 200);
            }
            else{
                carryOutObstacle_tap(obstacle4Start, obstacle4End, obstacle4PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle4FailArea)){
            Paint.setStatus("Walk back from fail area 4");
            Walker.walkPath(obstacle4FailPath);
            Condition.wait(() -> Player.isTileWithinArea(currentLocation, obstacle1Area), 100, 40);
            currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + currentHP);
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle5Area)) {
            if(tileEquals(obstacle4End, currentLocation)){
                Paint.setStatus("Traverse obstacle 5");
                Logger.debugLog("Traverse obstacle 5.");
                Client.tap(obstacle5InstantPressArea);
                Condition.wait(() -> atTile(obstacle5End), 100, 65);
            }
            else {
                carryOutObstacle_tap(obstacle5Start, obstacle5End, obstacle5PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle6Area)) {
            if(tileEquals(obstacle5End, currentLocation)){
                Paint.setStatus("Traverse obstacle 6");
                Logger.debugLog("Traverse obstacle 6.");
                Client.tap(obstacle6InstantPressArea);
                Condition.wait(() -> atTile(obstacle6End), 100, 50);
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
                Condition.wait(() -> atTile(obstacle7End), 100, 130);
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
                Condition.wait(() -> atTile(obstacle8End), 100, 50);
            }
            else {
                carryOutObstacle_tap(obstacle8Start, obstacle8End, obstacle8PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacleEndArea)) {
            lapCount = lapCount + 1;
            Walker.walkPath(obstacleEndPath);
            Condition.wait(() -> Player.isTileWithinArea(currentLocation, obstacle1Area), 100, 40);
            return true;
        }
        return false;
    }

    public void carryOutObstacle_tap(Tile obstacleStart, Tile obstacleEnd, Rectangle pressArea){
        if(!atTile(obstacleStart)) {
            Paint.setStatus("Traverse obstacle X");
            Logger.debugLog("Carrying out obstacle...");
            Walker.step(obstacleStart);
            Condition.wait(() -> atTile(obstacleStart), 150, 16);
        }
        if (atTile(obstacleStart)) {
            Logger.debugLog("Player is at start of obstacle.");
            Client.tap(pressArea);
            Condition.wait(() -> atTile(obstacleEnd), 150, 16);
        }
    }

    public void carryOutStartObstacle_tap() {
        Paint.setStatus("Traverse start obstacle");
        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, screenROI, 5);

        if (!foundPoints.isEmpty()) {
            int randomIndex = random.nextInt(foundPoints.size());
            Point tapPoint = foundPoints.get(randomIndex);

            Logger.debugLog("Located the first obstacle using the color finder, tapping.");
            Client.tap(tapPoint);
            Condition.wait(() -> atTile(obstacle1End), 100, 100);
        } else {
            Logger.debugLog("Couldn't locate the first obstacle with the color finder, using fallback method.");
            currentLocation = Walker.getPlayerPosition();

            Logger.debugLog("Moving to the start of the obstacle.");
            Walker.step(obstacle1Start);
            Condition.wait(() -> atTile(obstacle1Start), 100, 100);
            carryOutStartObstacle_tap();
        }
    }
}
