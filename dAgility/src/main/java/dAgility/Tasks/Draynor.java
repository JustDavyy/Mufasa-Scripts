package dAgility.Tasks;

import dAgility.dAgility;
import helpers.utils.Area;
import helpers.utils.RegionBox;
import helpers.utils.Tile;
import dAgility.utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static helpers.Interfaces.*;
import static dAgility.dAgility.*;

public class Draynor extends Task {
    private static final Random random = new Random();
    List<Color> obstacleColors = Arrays.asList(
            Color.decode("#1fff25")
    );
    Color mogColor = new Color(Integer.parseInt("d38f1a", 16)); // hex code #d38f1a for MoG on this course
    Rectangle screenROI = new Rectangle(236, 121, 293, 229);
    Area obstacle1Area = new Area(
            new Tile(303, 95),
            new Tile(316, 106)
    );
    Tile obstacle1Start = new Tile(306,100);
    Tile obstacle1End = new Tile(218, 97);
    Tile obstacle1End2 = new Tile(218, 97);
    Rectangle obstacle2InstantPressArea = new Rectangle(229, 351, 28, 25);
    Rectangle obstacle2PressArea = new Rectangle(384, 284, 26, 21);
    Area obstacle2Area = new Area(
            new Tile(209, 92),
            new Tile(219, 102)
    );
    RegionBox draynorRB = new RegionBox("DraynorAgility", 507, 156, 1038, 576);
    Area obstacle2FailArea = new Area(
            new Tile(287, 96),
            new Tile(297, 110)
    );
    Tile obstacle2Start = new Tile(214, 100);
    Tile obstacle2End = new Tile(202,101);
    Rectangle obstacle3InstantPressArea = new Rectangle(522, 275, 27, 21);
    Rectangle obstacle3PressArea = new Rectangle(486, 274, 26, 23);
    Area obstacle3Area = new Area(
            new Tile(196, 98),
            new Tile(206, 107)
    );
    Area obstacle3FailArea = new Area(
            new Tile(283, 117),
            new Tile(289, 125)
    );

    Tile[] obstacle3FailPath = new Tile[] {
            new Tile(291, 121),
            new Tile(297, 121),
            new Tile(302, 120),
            new Tile(307, 117),
            new Tile(307, 112),
            new Tile(307, 107),
            new Tile(308, 101)
    };
    Tile obstacle3Start = new Tile(203, 101);
    Tile obstacle3End = new Tile(204, 115);
    Tile[] obstacle4FailPath = new Tile[] {
            new Tile(299, 122),
            new Tile(307, 102)
    };
    Area obstacle4FailArea = new Area(
            new Tile(284, 119),
            new Tile(296, 132)
    );
    Rectangle obstacle4InstantPressArea = new Rectangle(297, 376, 32, 28);
    Rectangle obstacle4PressArea = new Rectangle(425, 327, 32, 28);
    Area obstacle4Area = new Area(
            new Tile(198, 110),
            new Tile(209, 118)
    );
    Tile obstacle4Start = new Tile(200, 116);
    Tile obstacle4End = new Tile(199, 121);
    Tile obstacle4End2 = new Tile(200, 121);
    Rectangle obstacle5InstantPressArea = new Rectangle(414, 500, 55, 32);
    Rectangle obstacle5PressArea = new Rectangle(416, 297, 52, 29);
    Area obstacle5Area = new Area(
            new Tile(190, 120),
            new Tile(199, 127)
    );
    Tile obstacle5Start = new Tile(199, 127);
    Tile obstacle5End = new Tile(199, 129);
    Tile obstacle5End2 = new Tile(199,130);
    Rectangle obstacle6InstantPressArea = new Rectangle(750, 252, 13, 47);
    Rectangle obstacle6PressArea = new Rectangle(477, 268, 40, 36);
    Area obstacle6Area = new Area(
            new Tile(196, 129),
            new Tile(209, 131)
    );
    Tile obstacle6Start = new Tile(207, 129);
    Tile obstacle6End = new Tile(210, 128);
    Rectangle obstacle7InstantPressArea = new Rectangle(672, 3, 20, 20);
    Rectangle obstacle7PressArea = new Rectangle(457, 183, 108, 60);
    Area obstacle7Area = new Area(
            new Tile(209, 120),
            new Tile(218, 129)
    );
    Tile obstacle7Start = new Tile(216, 121);
    Tile obstacle7End = new Tile(306, 124);

    Area endObstacleArea = new Area(
            new Tile(301, 120),
            new Tile(311, 135)
    );

    Tile[] endToStartPath = new Tile[] {
            new Tile(306, 124),
            new Tile(308, 118),
            new Tile(308, 113),
            new Tile(308, 109),
            new Tile(308, 103)
    };

    Rectangle roof1MoG1 = new Rectangle(247, 174, 19, 16);
    Rectangle roof2MoG1 = new Rectangle(381, 364, 23, 16);
    Rectangle roof3MoG1 = new Rectangle(535, 273, 27, 25);
    Rectangle roof5MoG1 = new Rectangle(619, 220, 21, 16);
    Rectangle roof5MoG2 = new Rectangle(485, 142, 20, 16);


    public Draynor(){
        super();
        super.name = "Draynor";
    }

    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        return (dAgility.courseChosen.equals("Draynor"));
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        // If player within obstacle area
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos (x:" + currentLocation.x() + "|y:" + currentLocation.y() + ")");
        if(Player.isTileWithinArea(currentLocation, obstacle1Area)) {
            carryOutStartObstacle_tap();
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle2Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof1MoG1, mogColor)) {
                Client.tap(new Rectangle(427, 455, 29, 28));
                Condition.wait(() -> atTile(obstacle2End), 100, 105);
            } else if(check2Tiles(obstacle1End, obstacle1End2)){
                Paint.setStatus("Traverse obstacle 2");
                Logger.debugLog("Traverse obstacle 2.");
                Client.tap(obstacle2InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle2End, obstacle2FailArea), 100, 100);
            }
            else {
                carryOutObstacle_tap(obstacle2Start, obstacle2End, obstacle2PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle2FailArea)) {
            Paint.setStatus("Walk back from fail area 2");
            Logger.debugLog("Fell from obstacle 2, walking back to the start location...");
            Client.tap(new Rectangle(837, 69, 10, 11));
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 150, 16);
            int randomDelay = new Random().nextInt(1900) + 250;
            Condition.sleep(randomDelay);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle3Area)) {
            Condition.sleep(generateRandomDelay(1750, 2000));
            if (mogPresent(roof2MoG1, mogColor)) {
                Client.tap(new Rectangle(559, 195, 25, 22));
                Condition.wait(() -> checkTileandArea(obstacle3End, obstacle3FailArea), 100, 105);
            }
            if(tileEquals(obstacle2End, currentLocation)){
                Paint.setStatus("Traverse obstacle 3");
                Logger.debugLog("Traverse obstacle 3.");
                Client.tap(obstacle3InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle3End, obstacle3FailArea), 100, 95);
            }
            else {
                carryOutObstacle_tap(obstacle3Start, obstacle3End, obstacle3PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle3FailArea)) {
            Paint.setStatus("Walk back from fail area 3");
            Logger.debugLog("Fell from obstacle 3, walking back to the start location...");
            Walker.walkPath(obstacle3FailPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 150, 16);
            int randomDelay = new Random().nextInt(900) + 250;
            Condition.sleep(randomDelay);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle4Area)) {
            Condition.sleep(generateRandomDelay(1250, 1500));
            if (mogPresent(roof3MoG1, mogColor)) {
                Condition.sleep(generateRandomDelay(1000, 1500));
                Client.tap(new Rectangle(216, 375, 26, 21));
                Condition.wait(() -> check2Tiles(obstacle4End, obstacle4End2), 100, 40);
            }
            if(tileEquals(obstacle3End, currentLocation)){
                Paint.setStatus("Traverse obstacle 4");
                Logger.debugLog("Traverse obstacle 4.");
                    Client.tap(obstacle4InstantPressArea);
                    Condition.wait(() -> check2Tiles(obstacle4End, obstacle4End2), 100, 40);
            }
            else {
                carryOutObstacle_tap(obstacle4Start, obstacle4End, obstacle4PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle4FailArea)){
            Paint.setStatus("Walk back from fail area 4");
            Logger.debugLog("Fell from obstacle 4, walking back to the start location...");
            Walker.walkPath(obstacle4FailPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 150, 16);
            int randomDelay = new Random().nextInt(900) + 250;
            Condition.sleep(randomDelay);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle5Area)) {
            if(check2Tiles(obstacle4End, obstacle4End2)){
                Condition.sleep(generateRandomDelay(600, 900));
                Paint.setStatus("Traverse obstacle 5");
                Logger.debugLog("Traverse obstacle 5.");
                Client.tap(obstacle5InstantPressArea);
                Condition.sleep(generateRandomDelay(4800, 5500));
                Condition.wait(() -> check2Tiles(obstacle5End, obstacle5End2), 100, 55);
            }
            else {
                carryOutObstacle_tap(obstacle5Start, obstacle5End, obstacle5PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle6Area)) {
            if(tileEquals(obstacle5End, currentLocation)) {
                Paint.setStatus("Traverse obstacle 6");
                Logger.debugLog("Traverse obstacle 6.");

                Client.tap(obstacle6InstantPressArea);
                Condition.wait(() -> atTile(obstacle6End), 100, 62);
                Condition.sleep(generateRandomDelay(600, 900));
                if (mogPresent(roof5MoG1, mogColor)) {
                    Client.tap(new Rectangle(497, 41, 97, 59));
                    Condition.wait(() -> atTile(obstacle7End), 100, 70);

                    Condition.sleep(generateRandomDelay(250, 500));
                    // Move directly to the start again with randomization
                    Client.tap(new Rectangle(799, 12, 10, 9));
                    Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 100, 70);

                    Condition.sleep(generateRandomDelay(1250, 3500));
                } else if (mogPresent(roof5MoG2, mogColor)) {
                    Client.tap(new Rectangle(631, 131, 32, 63));
                    Condition.wait(() -> atTile(obstacle7End), 100, 70);

                    Condition.sleep(generateRandomDelay(250, 500));
                    // Move directly to the start again with randomization
                    Client.tap(new Rectangle(799, 12, 10, 9));
                    Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 100, 70);

                    Condition.sleep(generateRandomDelay(2500, 3250));
                } else{
                    Paint.setStatus("Traverse obstacle 7");
                    Logger.debugLog("Traverse obstacle 7.");
                    Client.tap(obstacle7InstantPressArea);
                    Condition.wait(() -> atTile(obstacle7End), 100, 70);

                    Condition.sleep(generateRandomDelay(600, 900));
                    // Move directly to the start again with randomization
                    Client.tap(new Rectangle(799, 12, 10, 9));
                    Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 100, 70);
                    Player.waitTillNotMoving(10, draynorRB);
                }
            }
            else {
                carryOutObstacle_tap(obstacle6Start, obstacle6End, obstacle6PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, endObstacleArea)) {
            dAgility.lapCount = dAgility.lapCount + 1;
            Walker.walkPath(endToStartPath);
            Player.waitTillNotMoving(7, draynorRB);
        } else {
            carryOutStartObstacle_tap();
            return true;
        }
        return false;
    }

    public void carryOutObstacle_tap(Tile obstacleStart, Tile obstacleEnd, Rectangle pressArea){
        Paint.setStatus("Traverse obstacle X");
        if(!atTile(obstacleStart)) {
            Logger.debugLog("Carrying out obstacle...");
            Walker.step(obstacleStart);
            Condition.wait(() -> atTile(obstacleStart), 100, 100);
        }
        if (atTile(obstacleStart)) {
            Logger.debugLog("Player is at start of obstacle.");
            Client.tap(pressArea);
            Condition.wait(() -> atTile(obstacleEnd), 100, 100);
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
            Condition.wait(() -> atTile(obstacle1Start), 100, 100);
            Condition.sleep(generateRandomDelay(200, 400));
            Client.tap(new Rectangle(422, 240, 9, 40));
            Condition.wait(() -> check2Tiles(obstacle1End, obstacle1End2), 100, 100);
        }
    }
}
