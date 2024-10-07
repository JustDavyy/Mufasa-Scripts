package dAgility.Tasks;

import dAgility.dAgility;
import helpers.utils.Area;
import helpers.utils.Tile;
import dAgility.utils.Task;

import java.awt.*;
import java.util.List;
import java.util.*;

import static helpers.Interfaces.*;
import static dAgility.dAgility.*;

public class Rellekka extends Task {
    private final Random random = new Random();
    Color mogColor = new Color(Integer.parseInt("d38f1a", 16)); // hex code #ca8818 for MoG on this course
    List<Color> obstacleColors = Arrays.asList(
            Color.decode("#20ff25")
    );
    Rectangle screenROI = new Rectangle(61, 41, 600, 485);
    Area obstacle1Area = new Area(
            new Tile(588, 114),
            new Tile(632, 138)
    );
    Tile obstacle1Start = new Tile(608, 122);
    Tile obstacle1End = new Tile(489, 298);
    Tile obstacle1End2 = new Tile(490, 298);
    Rectangle obstacle2InstantPressArea = new Rectangle(355, 350, 51, 65);
    Rectangle obstacle2PressArea = new Rectangle(403, 296, 50, 46);
    Area obstacle2Area = new Area(
            new Tile(483, 295),
            new Tile(493, 306)
    );
    Tile obstacle2Start = new Tile(487, 302);
    Tile obstacle2End = new Tile(485, 309);
    Area obstacle3FailArea = new Area(
            new Tile(592, 148),
            new Tile(611, 165)
    );

    Tile[] obstacle3FailPath = new Tile[] {
            new Tile(606, 148),
            new Tile(607, 140),
            new Tile(611, 134),
            new Tile(611, 127),
    };
    Rectangle obstacle3InstantPressArea = new Rectangle(457, 466, 18, 26);
    Rectangle obstacle3PressArea = new Rectangle(454, 264, 15, 14);
    Area obstacle3Area = new Area(
            new Tile(474, 306),
            new Tile(488, 325)
    );
    Tile obstacle3Start = new Tile(485, 321);
    Tile obstacle3End = new Tile(492, 327);
    Area obstacle4FailArea = new Area(
            new Tile(612, 148),
            new Tile(629, 166)
    );
    Tile[] obstacle4FailPath = new Tile[] {
            new Tile(610, 153),
            new Tile(607, 147),
            new Tile(608, 140),
            new Tile(612, 132),
            new Tile(611, 126)
    };
    Rectangle obstacle4InstantPressArea = new Rectangle(472, 225, 63, 30);
    Rectangle obstacle4PressArea = new Rectangle(440, 223, 61, 30);
    Area obstacle4Area = new Area(
            new Tile(489, 324),
            new Tile(498, 334)
    );
    Tile obstacle4Start = new Tile(495, 327);
    Tile obstacle4End = new Tile(508, 329);
    Tile obstacle4End2 = new Tile(508, 327);
    Rectangle obstacle5InstantPressArea = new Rectangle(508, 235, 33, 29);
    Rectangle obstacle5PressArea = new Rectangle(443, 225, 26, 27);
    Area obstacle5Area = new Area(
            new Tile(505, 326),
            new Tile(517, 336)
    );
    Tile obstacle5Start = new Tile(513, 330);
    Tile obstacle5End = new Tile(513, 323);
    Rectangle obstacle6InstantPressArea = new Rectangle(500, 183, 15, 9);
    Rectangle obstacle6PressArea = new Rectangle(440, 249, 15, 11);
    Area obstacle6Area = new Area(
            new Tile(510, 314),
            new Tile(525, 325)
    );
    Tile obstacle6Start = new Tile(519, 317);
    Tile obstacle6End = new Tile(529, 306);
    Rectangle obstacle7InstantPressArea = new Rectangle(410, 162, 26, 26);
    Rectangle obstacle7PressArea = new Rectangle(405, 215, 35, 34);
    Area obstacle7Area = new Area(
            new Tile(526, 283),
            new Tile(546, 316)
    );
    Tile obstacle7Start = new Tile(529, 301);
    Tile obstacle7End = new Tile(641, 128);
    Tile obstacle7End2 = new Tile(641, 128);
    Area obstacleEndArea = new Area(
            new Tile(636, 117),
            new Tile(653, 145)
    );
    Tile[] obstacleEndPath = new Tile[] {
            new Tile(631, 125),
            new Tile(621, 124),
            new Tile(613, 123),
            new Tile(610, 121)
    };

    Rectangle roof1MoG1 = new Rectangle(404, 282, 9, 8);
    Rectangle roof3MoG1 = new Rectangle(462, 301, 9, 9);
    Rectangle roof3MoG2 = new Rectangle(478, 286, 9, 7);
    Rectangle roof4MoG1 = new Rectangle(500, 295, 10, 11);
    Rectangle roof4MoG2 = new Rectangle(482, 316, 8, 9);


    public Rellekka(){
        super();
        super.name = "Rellekka";
    }
    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        return (dAgility.courseChosen.equals("Rellekka"));
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        // If player within obstacle area
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos (x:" + currentLocation.x() + "|y:" + currentLocation.y() + ")");
        if(Player.isTileWithinArea(currentLocation, obstacle1Area)) {
            Paint.setStatus("Traverse start obstacle");
            carryOutStartObstacle_tap();
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle2Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof1MoG1, mogColor)) {
                Client.tap(new Rectangle(400, 333, 52, 49));
                Condition.sleep(generateRandomDelay(1950, 2100));
                Condition.wait(() -> atTile(obstacle2End), 100, 60);
            } else if(check2Tiles(obstacle1End, obstacle1End2)){
                Paint.setStatus("Traverse obstacle 2");
                Logger.debugLog("Traverse obstacle 2.");
                Client.tap(obstacle2InstantPressArea);
                Condition.wait(() -> atTile(obstacle2End), 100, 60);
            } else {
                carryOutObstacle_tap(obstacle2Start, obstacle2End, obstacle2PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle3Area)) {
            if(tileEquals(obstacle2End, currentLocation)){
                Paint.setStatus("Traverse obstacle 3");
                Logger.debugLog("Traverse obstacle 3.");
                Client.tap(obstacle3InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle3End, obstacle3FailArea), 100, 90);
            } else {
                carryOutObstacle_tap(obstacle3Start, obstacle3End, obstacle3PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle3FailArea)){
            Paint.setStatus("Walk back from fail area 3");
            Walker.walkPath(obstacle3FailPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 100, 35);
            Condition.sleep(2500);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle4Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof3MoG1, mogColor)) {
                Client.tap(new Rectangle(459, 198, 55, 28));
                Condition.sleep(generateRandomDelay(1850, 2100));
                Condition.wait(() -> checkTileandArea(obstacle4End, obstacle4FailArea), 100, 120);
            } else if (mogPresent(roof3MoG2, mogColor)) {
                Client.tap(new Rectangle(443, 209, 56, 29));
                Condition.sleep(generateRandomDelay(1950, 2100));
                Condition.wait(() -> checkTileandArea(obstacle4End, obstacle4FailArea), 100, 120);
            } else if(tileEquals(obstacle3End, currentLocation)){
                Paint.setStatus("Traverse obstacle 4");
                Logger.debugLog("Traverse obstacle 4.");
                Client.tap(obstacle4InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle4End, obstacle4FailArea), 100, 120);
            } else {
                carryOutObstacle_tap(obstacle4Start, obstacle4End, obstacle4PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle4FailArea)){
            Paint.setStatus("Walk back from fail area 4");
            Walker.walkPath(obstacle4FailPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 150, 35);
            Condition.sleep(2500);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle5Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
        if (mogPresent(roof4MoG1, mogColor)) {
            Client.tap(new Rectangle(458, 215, 27, 28));
            Condition.sleep(generateRandomDelay(1950, 2100));
            Condition.wait(() -> atTile(obstacle5End), 100, 55);
        }
        if (mogPresent(roof4MoG2, mogColor)) {
            Client.tap(new Rectangle(472, 200, 30, 27));
            Condition.sleep(generateRandomDelay(1950, 2100));
            Condition.wait(() -> atTile(obstacle5End), 100, 55);
        } else if(check2Tiles(obstacle4End, obstacle4End2)){
            Paint.setStatus("Traverse obstacle 5");
                Logger.debugLog("Traverse obstacle 5.");
                Client.tap(obstacle5InstantPressArea);
                Condition.wait(() -> atTile(obstacle5End), 100, 55);
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
                Condition.wait(() -> atTile(obstacle6End), 100, 110);
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
                Condition.sleep(generateRandomDelay(750, 1100));
                Client.tap(obstacle7InstantPressArea);
                Condition.wait(() -> check2Tiles(obstacle7End, obstacle7End2), 100, 65);
                Condition.sleep(250);
            } else {
                carryOutObstacle_tap(obstacle7Start, obstacle7End, obstacle7PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacleEndArea)){
            dAgility.lapCount = dAgility.lapCount + 1;
            Walker.walkPath(obstacleEndPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 150, 35);
            Condition.sleep(3750);
            return true;
        }
        return false;
    }
    public void carryOutObstacle_tap(Tile obstacleStart, Tile obstacleEnd, Rectangle pressArea){
        if(!atTile(obstacleStart)) {
            Paint.setStatus("Traverse obstacle x");
            Logger.debugLog("Carrying out obstacle...");
            Walker.step(obstacleStart);
            Condition.wait(() -> atTile(obstacleStart), 150, 35);
        }
        if (atTile(obstacleStart)) {
            Logger.debugLog("Player is at start of obstacle.");
            Client.tap(pressArea);
            Condition.wait(() -> atTile(obstacleEnd), 150, 75);
        }
    }

    public void carryOutStartObstacle_tap() {
        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, screenROI, 10);

        if (!foundPoints.isEmpty()) {
            int randomIndex = random.nextInt(foundPoints.size());
            Point tapPoint = foundPoints.get(randomIndex);

            Logger.debugLog("Located the first obstacle using the color finder, tapping.");
            Client.tap(tapPoint);
            Condition.wait(() -> check2Tiles(obstacle1End, obstacle1End2), 100, 45);
        } else {
            Logger.debugLog("Couldn't locate the first obstacle with the color finder, using fallback method.");
            currentLocation = Walker.getPlayerPosition();

            Logger.debugLog("Moving to the start of the obstacle.");
            Walker.step(obstacle1Start);
            Condition.wait(() -> atTile(obstacle1Start), 100, 45);
            carryOutStartObstacle_tap();
        }
    }
}
