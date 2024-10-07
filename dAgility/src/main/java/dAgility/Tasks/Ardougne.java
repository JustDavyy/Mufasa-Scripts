package dAgility.Tasks;

import dAgility.dAgility;
import helpers.utils.Area;
import helpers.utils.Tile;
import dAgility.utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static helpers.Interfaces.*;
import static dAgility.dAgility.*;

public class Ardougne extends Task {
    Color mogColor = new Color(Integer.parseInt("c98718", 16)); // hex code #ca8818 for MoG on this course
    private static final Random random = new Random();
    List<Color> obstacleColors = Arrays.asList(
            Color.decode("#1fff25"),
            Color.decode("#456620"),
            Color.decode("#21ff27"),
            Color.decode("#62c551"),
            Color.decode("#41bf2a"),
            Color.decode("#36931f"),
            Color.decode("#4dcb3a")
    );
    Rectangle screenROI = new Rectangle(61, 41, 600, 485);
    Area obstacle1Area = new Area(
            new Tile(565, 484),
            new Tile(579, 497)
    );
    Tile obstacle1Start = new Tile(570, 488);
    Tile obstacle1End = new Tile(360, 380);
    Tile obstacle1End2 = new Tile(361, 380);
    Rectangle obstacle2InstantPressArea = new Rectangle(433, 108, 11, 29);
    Rectangle obstacle2PressArea = new Rectangle(430, 215, 14, 32);
    Area obstacle2Area = new Area(
            new Tile(357, 363),
            new Tile(364, 382)
    );
    Tile obstacle2Start = new Tile(360, 367);
    Tile obstacle2End = new Tile(352, 355);
    Area failArea = new Area(
            new Tile(530, 450),
            new Tile(577, 481)
    );

    Tile[] failPath = new Tile[] {
            new Tile(556, 473),
            new Tile(559, 481),
            new Tile(562, 485),
            new Tile(567, 489)
    };

    Rectangle obstacle3InstantPressArea = new Rectangle(373, 267, 13, 5);
    Rectangle obstacle3PressArea = new Rectangle(420, 267, 16, 6);
    Area obstacle3Area = new Area(
            new Tile(345, 351),
            new Tile(355, 358)
    );
    Tile obstacle3Start = new Tile(348, 355);
    Tile obstacle3End = new Tile(340, 355);
    Tile obstacle3End2 = new Tile(340, 355);
    Tile obstacle3End3 = new Tile(341, 354);
    Rectangle obstacle4InstantPressArea = new Rectangle(384, 262, 14, 22);
    Rectangle obstacle4PressArea = new Rectangle(420, 261, 14, 22);
    Area obstacle4Area = new Area(
            new Tile(334, 351),
            new Tile(344, 358)
    );
    Tile obstacle4Start = new Tile(338, 355);
    Tile obstacle4End = new Tile(336, 360);
    Tile obstacle4End2 = new Tile(337, 360);
    Rectangle obstacle5InstantPressArea = new Rectangle(436, 350, 17, 38);
    Rectangle obstacle5PressArea = new Rectangle(436, 277, 16, 34);
    Area obstacle5Area = new Area(
            new Tile(333, 358),
            new Tile(342, 363)
    );
    Tile obstacle5Start = new Tile(336, 365);
    Tile obstacle5End = new Tile(334, 367);
    Tile obstacle5End2 = new Tile(334, 366);
    Tile obstacle5End3 = new Tile(334, 367);
    Tile obstacle5End4 = new Tile(333,367);
    Rectangle obstacle6InstantPressArea = new Rectangle(501, 442, 13, 15);
    Rectangle obstacle6PressArea = new Rectangle(460, 284, 12, 10);
    Area obstacle6Area = new Area(
            new Tile(330, 365),
            new Tile(345, 380)
    );
    Tile obstacle6Start = new Tile(336, 377);
    Tile obstacle6End = new Tile(340, 383);
    Tile obstacle6End2 = new Tile(340, 383);
    Rectangle obstacle7InstantPressArea = new Rectangle(449, 270, 17, 22);
    Rectangle obstacle7PressArea = new Rectangle(449, 270, 17, 22);
    Area obstacle7Area = new Area(
            new Tile(337, 381),
            new Tile(347, 392)
    );
    Tile obstacle7Start = new Tile(340, 383);
    Tile obstacle7End = new Tile(564, 487);

    Rectangle ardyMoG1 = new Rectangle(456, 265, 10, 9);

    public Ardougne(){
        super();
        super.name = "Ardougne";
    }
    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        return (dAgility.courseChosen.equals("Ardougne"));
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        // If player within obstacle area
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos (x:" + currentLocation.x() + "|y:" + currentLocation.y() + ")");
        if (Player.isTileWithinArea(currentLocation, failArea)) {
            Paint.setStatus("Walk back from fail area");
            Walker.walkPath(failPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 150, 30);
            Condition.sleep(2500);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
        }
        else if (tileEquals(currentLocation, obstacle7End)) {
            // We are at the end obstacle tile, so can instantly press the start obstacle again.
            dAgility.lapCount = dAgility.lapCount + 1;
            Paint.setStatus("Traverse obstacle 1");
            Condition.sleep(generateRandomDelay(200,350));
            Client.tap(new Rectangle(525, 247, 14, 11));
            Condition.sleep(2000);
            Condition.wait(() -> check2Tiles(obstacle1End, obstacle1End2), 100, 65);
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle1Area)) {
            carryOutStartObstacle_tap();
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle2Area)) {
            if(check2Tiles(obstacle1End, obstacle1End2)){
                Paint.setStatus("Traverse obstacle 2");
                Logger.debugLog("Traverse obstacle 2.");
                Client.tap(obstacle2InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle2End,failArea), 100, 95);
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
                Condition.wait(() -> check3TilesandArea(obstacle3End, obstacle3End2, obstacle3End3, failArea), 100, 80);
            } else {
                carryOutObstacle_tap(obstacle3Start, obstacle3End, obstacle3PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle4Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(ardyMoG1, mogColor)) {
                Client.tap(obstacle4InstantPressArea);
                Condition.wait(() -> check2Tiles(obstacle4End, obstacle4End2), 100, 45);
            } else if(check3Tiles(obstacle3End, obstacle3End2, obstacle3End3)){
                Paint.setStatus("Traverse obstacle 4");
                Logger.debugLog("Traverse obstacle 4.");
                Client.tap(obstacle4InstantPressArea);
                Condition.wait(() -> check2Tiles(obstacle4End, obstacle4End2), 100, 45);
            } else {
                carryOutObstacle_tap(obstacle4Start, obstacle4End, obstacle4PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle5Area)) {
        if(check2Tiles(obstacle4End, obstacle4End2)){
            Paint.setStatus("Traverse obstacle 5");
                Logger.debugLog("Traverse obstacle 5.");
                Client.tap(obstacle5InstantPressArea);
                Condition.wait(() -> check4TilesandArea(obstacle5End, obstacle5End2, obstacle5End3, obstacle5End4, failArea), 100, 55);
                Condition.sleep(350);
            }
            else {
                carryOutObstacle_tap(obstacle5Start, obstacle5End, obstacle5PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle6Area)) {
            if(check4Tiles(obstacle5End, obstacle5End2, obstacle5End3, obstacle5End4)){
                Paint.setStatus("Traverse obstacle 6");
                Logger.debugLog("Traverse obstacle 6.");
                Client.tap(obstacle6InstantPressArea);
                Condition.wait(() -> check2Tiles(obstacle6End, obstacle6End2), 100, 85);
            }
            else {
                carryOutObstacle_tap(obstacle6Start, obstacle6End, obstacle6PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle7Area)) {
            if(check2Tiles(obstacle6End, obstacle6End2)){
                Paint.setStatus("Traverse obstacle 7");
                Logger.debugLog("Traverse obstacle 7.");
                Client.tap(obstacle7InstantPressArea);
                Condition.wait(() -> Player.atTile(obstacle7End), 100, 130);
            } else {
                carryOutObstacle_tap(obstacle7Start, obstacle7End, obstacle7PressArea);
            }
            return true;
        }
        return false;
    }
    public void carryOutObstacle_tap(Tile obstacleStart, Tile obstacleEnd, Rectangle pressArea){
        Paint.setStatus("Traverse obstacle X");
        if(!Player.atTile(obstacleStart)) {
            Logger.debugLog("Carrying out obstacle...");
            Walker.step(obstacleStart);
            Condition.wait(() -> Player.atTile(obstacleStart), 150, 35);
        }
        if (Player.atTile(obstacleStart)) {
            Logger.debugLog("Player is at start of obstacle.");
            Client.tap(pressArea);
            Condition.wait(() -> Player.atTile(obstacleEnd), 150, 75);
        }
    }

    public void carryOutStartObstacle_tap() {
        Paint.setStatus("Traverse start obstacle");
        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, screenROI, 1);

        if (!foundPoints.isEmpty()) {
            // Calculate the middle point (centroid)
            int totalX = 0;
            int totalY = 0;

            for (Point point : foundPoints) {
                totalX += point.x;
                totalY += point.y;
            }

            int middleX = totalX / foundPoints.size();
            int middleY = totalY / foundPoints.size();

            // Randomize the middle point with ±2 offset
            int randomOffsetX = random.nextInt(5) - 2; // Random number between -2 and 2
            int randomOffsetY = random.nextInt(5) - 2; // Random number between -2 and 2
            Point tapPoint = new Point(middleX + randomOffsetX, middleY + randomOffsetY);

            Logger.debugLog("Located the first obstacle using the color finder, tapping.");
            Client.tap(tapPoint);
            Condition.wait(() -> check2Tiles(obstacle1End, obstacle1End2), 100, 100);
        } else {
            Logger.debugLog("Couldn't locate the first obstacle with the color finder, using fallback method.");
            currentLocation = Walker.getPlayerPosition();

            Logger.debugLog("Moving to the start of the obstacle.");
            Walker.step(obstacle1Start);
            Condition.wait(() -> tileEquals(obstacle1Start, currentLocation), 100, 45);
            Client.tap(new Rectangle(441, 245, 14, 10));
            Condition.wait(() -> Player.atTile(obstacle1End), 100, 60);
        }
    }
}
