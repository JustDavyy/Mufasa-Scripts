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

public class Varrock extends Task {
    private static final Random random = new Random();
    List<Color> obstacleColors = Arrays.asList(
            Color.decode("#1fff25"),
            Color.decode("#20ff25"),
            Color.decode("#29dd2c"),
            Color.decode("#21ff27")
    );
    Color mogColor = new Color(Integer.parseInt("c98718", 16)); // hex code #c98718 for MoG on this course
    Rectangle screenROI = new Rectangle(286, 168, 293, 186);
    Rectangle obstacle1InstantPressArea = new Rectangle(143, 312, 9, 9);
    Area obstacle1Area = new Area(
            new Tile(94, 294),
            new Tile(130, 321)
    );
    Tile obstacle1Start = new Tile(103,305);
    Tile obstacle1End = new Tile(230, 295);
    Rectangle obstacle2InstantPressArea = new Rectangle(336, 268, 13, 11);
    Rectangle obstacle2PressArea = new Rectangle(420, 262, 10, 14);
    Area obstacle2Area = new Area(
            new Tile(222, 286),
            new Tile(232, 303)
    );
    Tile obstacle2Start = new Tile(224, 295);
    Tile obstacle2End = new Tile(216, 295);
    Rectangle obstacle3InstantPressArea = new Rectangle(300, 213, 18, 29);
    Rectangle obstacle3PressArea = new Rectangle(418, 242, 16, 32);
    Area obstacle3Area = new Area(
            new Tile(205, 287),
            new Tile(217, 297)
    );
    Tile obstacle3Start = new Tile(206, 293);
    Tile obstacle3End = new Tile(321, 294);
    Rectangle obstacle4InstantPressArea = new Rectangle(336, 264, 29, 29);
    Rectangle obstacle4PressArea = new Rectangle(385, 262, 31, 32);
    Area obstacle4FailArea = new Area(
            new Tile(50, 296),
            new Tile(95, 348)
    );
    Tile[] obstacle4FailPath = new Tile[] {
            new Tile(79, 312),
            new Tile(85, 312),
            new Tile(91, 311),
            new Tile(99, 312),
            new Tile(104, 305)
    };

    Tile obstacle4Area1 = new Tile(321,294);
    Tile obstacle4Area2 = new Tile(320,294);
    Tile obstacle4Area3 = new Tile(318,294);
    Tile obstacle4Area4 = new Tile(317,294);
    Tile obstacle4Start = new Tile(317,294);
    Tile obstacle4End = new Tile(194,306);
    Rectangle obstacle5InstantPressArea = new Rectangle(445, 351, 70, 18);
    Rectangle obstacle5PressArea = new Rectangle(427, 279, 115, 15);
    Area obstacle5Area = new Area(
            new Tile(192, 304),
            new Tile(205, 314)
    );
    Tile obstacle5Start = new Tile(196, 311);
    Tile obstacle5End = new Tile(196, 317);
    Rectangle obstacle6InstantPressArea = new Rectangle(716, 271, 20, 22);
    Rectangle obstacle6PressArea = new Rectangle(455, 207, 19, 73);
    Area obstacle6Area1 = new Area(
            new Tile(186, 314),
            new Tile(209, 332)
    );
    Area obstacle6Area2 = new Area(
            new Tile(207, 307),
            new Tile(218, 323)
    );
    Tile obstacle6Start = new Tile(216, 318);
    Tile obstacle6End = new Tile(229,315);
    Rectangle obstacle7InstantPressArea = new Rectangle(673, 203, 18, 14);
    Rectangle obstacle7PressArea = new Rectangle(462, 246, 25, 24);
    Area obstacle7Area = new Area(
            new Tile(226, 307),
            new Tile(250, 326)
    );
    Area obstacle7Area2 = new Area(
            new Tile(301, 358),
            new Tile(322, 380)
    );
    Tile obstacle7Start = new Tile(246, 311);
    Tile obstacle7End = new Tile(253, 310);
    Rectangle obstacle8InstantPressArea = new Rectangle(441, 157, 23, 30);
    Rectangle obstacle8PressArea = new Rectangle(435, 211, 88, 30);
    Area obstacle8Area = new Area(
            new Tile(251, 302),
            new Tile(260, 312)
    );
    Tile obstacle8Start = new Tile(253, 305);
    Tile obstacle8End = new Tile(253, 301);
    Rectangle obstacle9InstantPressArea = new Rectangle(438, 187, 17, 10);
    Rectangle obstacle9PressArea = new Rectangle(433, 241, 85, 4);
    Area obstacle9Area = new Area(
            new Tile(251, 291),
            new Tile(260, 301)
    );
    Tile obstacle9Start = new Tile(253, 295);
    Tile obstacle9End = new Tile(123,301);

    Rectangle obstacle4MoG1 = new Rectangle(408, 267, 10, 8);
    Rectangle obstacle6MoG1 = new Rectangle(316, 319, 8, 8);
    Rectangle obstacle8MoG1 = new Rectangle(472, 193, 9, 7);

    public Varrock(){
        super();
        super.name = "Varrock";
    }
    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        return (dAgility.courseChosen.equals("Varrock"));
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        // If player within obstacle area
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        if(Player.isTileWithinArea(currentLocation, obstacle1Area)) {
            dAgility.lapCount = dAgility.lapCount + 1;
            if(tileEquals(obstacle9End, currentLocation)){
                Logger.debugLog("Traverse obstacle 1.");
                Paint.setStatus("Traverse obstacle 1");
                Client.tap(obstacle1InstantPressArea);
                Condition.wait(() -> atTile(obstacle1End), 100, 150);
            }
            else {
                carryOutStartObstacle_tap();
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle2Area)) {
            if(tileEquals(obstacle1End, currentLocation)){
                Logger.debugLog("Traverse obstacle 2.");
                Paint.setStatus("Traverse obstacle 2");
                Client.tap(obstacle2InstantPressArea);
                Condition.wait(() -> atTile(obstacle2End), 100, 95);
            }
            else {
                carryOutObstacle_tap(obstacle2Start, obstacle2End, obstacle2PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle3Area)) {
            if(tileEquals(obstacle2End, currentLocation)){
                Condition.sleep(generateRandomDelay(400, 650));
                Logger.debugLog("Traverse obstacle 3.");
                Paint.setStatus("Traverse obstacle 3");
                Client.tap(obstacle3InstantPressArea);
                Condition.wait(() -> atTile(obstacle3End), 100, 65);
            }
            else {
                carryOutObstacle_tap(obstacle3Start, obstacle3End, obstacle3PressArea);
            }
            return true;
        }
        else if(tileEquals(currentLocation, obstacle4Area1) || tileEquals(currentLocation, obstacle4Area2) || tileEquals(currentLocation, obstacle4Area3) || tileEquals(currentLocation, obstacle4Area4)) {
            if(tileEquals(obstacle3End, currentLocation)){
                Condition.sleep(generateRandomDelay(450, 900));
                if (mogPresent(obstacle4MoG1, mogColor)) {
                    Client.tap(new Rectangle(368, 262, 31, 31));
                    Condition.wait(() -> checkTileandArea(obstacle4End, obstacle4FailArea), 100, 140);
                } else {
                    Logger.debugLog("Traverse obstacle 4.");
                    Paint.setStatus("Traverse obstacle 4");
                    Client.tap(obstacle4InstantPressArea);
                    Condition.wait(() -> checkTileandArea(obstacle4End, obstacle4FailArea), 100, 140);
                }
            }
            else {
                carryOutObstacle_tap(obstacle4Start, obstacle4End, obstacle4PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle4FailArea)){
            Paint.setStatus("Walk back fail area 4");
            Walker.walkPath(obstacle4FailPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 150, 30);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            Condition.sleep(generateRandomDelay(2500,4000));
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle5Area)) {
            if(tileEquals(obstacle4End, currentLocation)){
                Paint.setStatus("Traverse obstacle 5");
                Logger.debugLog("Traverse obstacle 5.");
                Client.tap(obstacle5InstantPressArea);
                Condition.wait(() -> atTile(obstacle5End), 100, 50);
            }
            else {
                carryOutObstacle_tap(obstacle5Start, obstacle5End, obstacle5PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle6Area1) || Player.isTileWithinArea(currentLocation, obstacle6Area2)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(obstacle6MoG1, mogColor)) {
                Condition.sleep(generateRandomDelay(2400,2600));
                Client.tap(new Rectangle(800, 219, 16, 29));
                Condition.wait(() -> atTile(obstacle6End), 100, 150);
            }
            if(tileEquals(obstacle5End, currentLocation)){
                Logger.debugLog("Traverse obstacle 6.");
                Paint.setStatus("Traverse obstacle 6");
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
                Logger.debugLog("Traverse obstacle 7.");
                Paint.setStatus("Traverse obstacle 7");
                Client.tap(obstacle7InstantPressArea);
                Condition.wait(() -> atTile(obstacle7End), 100, 80);
            }
            else {
                carryOutObstacle_tap(obstacle7Start, obstacle7End, obstacle7PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle7Area2)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(obstacle8MoG1, mogColor)) {
                Condition.sleep(generateRandomDelay(1400,1600));
                Paint.setStatus("Traverse obstacle 8");
                Client.tap(new Rectangle(406, 230, 85, 32));
                Condition.sleep(generateRandomDelay(4800, 5200));
                Paint.setStatus("Traverse obstacle 9");
                Logger.debugLog("Traverse obstacle 9 after MOG.");
                Client.tap(new Rectangle(427, 185, 25, 11));
                Condition.wait(() -> atTile(obstacle9End), 100, 55);
            } else {
                Paint.setStatus("Traverse obstacle 8");
                Logger.debugLog("Traverse obstacle 8.");
                Client.tap(obstacle8InstantPressArea);
                Condition.sleep(generateRandomDelay(4800, 5200));
                Paint.setStatus("Traverse obstacle 9");
                Logger.debugLog("Traverse obstacle 9.");
                Client.tap(obstacle9InstantPressArea);
                Condition.wait(() -> atTile(obstacle9End), 100, 55);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle8Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(obstacle8MoG1, mogColor)) {
                Condition.sleep(generateRandomDelay(1400,1600));
                Paint.setStatus("Traverse obstacle 8");
                Client.tap(new Rectangle(406, 230, 85, 32));
                Condition.sleep(generateRandomDelay(4800, 5200));
                Paint.setStatus("Traverse obstacle 9");
                Logger.debugLog("Traverse obstacle 9 after MOG.");
                Client.tap(new Rectangle(427, 185, 25, 11));
                Condition.wait(() -> atTile(obstacle9End), 100, 55);
            } else if(tileEquals(currentLocation, obstacle7End)){
                Paint.setStatus("Traverse obstacle 8");
                Logger.debugLog("Traverse obstacle 8.");
                Client.tap(obstacle8InstantPressArea);
                Condition.sleep(generateRandomDelay(4800, 5200));
                Paint.setStatus("Traverse obstacle 9");
                Logger.debugLog("Traverse obstacle 9.");
                Client.tap(obstacle9InstantPressArea);
                Condition.wait(() -> atTile(obstacle9End), 100, 55);
            }
            else {
                carryOutObstacle_tap(obstacle8Start, obstacle8End, obstacle8PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle9Area)) {
            if(tileEquals(currentLocation, obstacle8End)){
                Paint.setStatus("Traverse obstacle 9");
                Logger.debugLog("Traverse obstacle 9.");
                Client.tap(obstacle9InstantPressArea);
                Condition.wait(() -> atTile(obstacle9End), 100, 55);
            }
            else {
                carryOutObstacle_tap(obstacle9Start, obstacle9End, obstacle9PressArea);
            }
            return true;
        }
        return false;
    }

    public void carryOutObstacle_tap(Tile obstacleStart, Tile obstacleEnd, Rectangle pressArea){
        if(!atTile(obstacleStart)) {
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
        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, screenROI, 1);

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
