package dAgility.Tasks;

import dAgility.dAgility;
import helpers.utils.Area;
import helpers.utils.Tile;
import dAgility.utils.Task;

import java.awt.Color;
import java.awt.Rectangle;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static helpers.Interfaces.*;
import static dAgility.dAgility.*;

public class Pollnivneach extends Task {
    Color mogColor = new Color(Integer.parseInt("cb8a19", 16)); // hex code #ca8818 for MoG on this course
    private final Map<Tile, Rectangle> tileRectangleMap = new HashMap<>();

    List<Color> obstacleColors = Arrays.asList(
            Color.decode("#20ff25"),
            Color.decode("#8fa55f")
    );
    Rectangle screenROI = new Rectangle(61, 41, 600, 485);
    Area obstacle1Area = new Area(
            new Tile(501, 139),
            new Tile(527, 160)
    );
    Tile obstacle1Start = new Tile(510, 148);
    Tile obstacle1End = new Tile(465, 248);
    Area obstacle2FailArea = new Area(
            new Tile(500, 130),
            new Tile(516, 141)
    );
    Rectangle obstacle2InstantPressArea = new Rectangle(362, 35, 40, 39);
    Rectangle obstacle2PressArea = new Rectangle(444, 86, 41, 131);
    Area obstacle2Area = new Area(
            new Tile(456, 241),
            new Tile(467, 253)
    );
    Tile obstacle2Start = new Tile(462, 243);
    Tile obstacle2End = new Tile(466, 236);
    Area obstacle3FailArea = new Area(
            new Tile(511, 122),
            new Tile(521, 131)
    );
    Tile[] obstacle3FailPath = new Tile[] {
            new Tile(518, 135),
            new Tile(510, 148)
    };
    Rectangle obstacle3InstantPressArea = new Rectangle(599, 72, 60, 26);
    Rectangle obstacle3PressArea = new Rectangle(460, 212, 27, 29);
    Area obstacle3Area = new Area(
            new Tile(464, 231),
            new Tile(472, 238)
    );
    Tile obstacle3Start = new Tile(470, 232);
    Tile obstacle3End = new Tile(477, 231);
    Rectangle obstacle4InstantPressArea = new Rectangle(582, 244, 103, 74);
    Rectangle obstacle4PressArea = new Rectangle(461, 259, 33, 26);
    Area obstacle4Area = new Area(
            new Tile(475, 227),
            new Tile(481, 233)
    );
    Tile obstacle4Start = new Tile(479, 231);
    Tile obstacle4End = new Tile(485, 233);
    Rectangle obstacle5InstantPressArea = new Rectangle(478, 165, 140, 77);
    Rectangle obstacle5PressArea = new Rectangle(423, 227, 53, 27);
    Area obstacle5Area = new Area(
            new Tile(483, 231),
            new Tile(491, 237)
    );
    Tile obstacle5Start = new Tile(488, 233);
    Tile obstacle5End = new Tile(487, 224);
    Rectangle obstacle6InstantPressArea = new Rectangle(298, 233, 19, 13);
    Rectangle obstacle6PressArea = new Rectangle(422, 250, 12, 11);
    Area obstacle6Area = new Area(
            new Tile(481, 217),
            new Tile(491, 227)
    );
    Tile obstacle6Start = new Tile(485, 224);
    Tile obstacle6End = new Tile(431, 311);
    Rectangle obstacle7InstantPressArea = new Rectangle(57, 101, 93, 98);
    Rectangle obstacle7PressArea = new Rectangle(427, 224, 34, 33);
    Area obstacle7Area = new Area(
            new Tile(415, 305),
            new Tile(434, 319)
    );
    Tile obstacle7Start = new Tile(422, 310);
    Tile obstacle7End = new Tile(422, 300);
    Tile obstacle7End2 = new Tile(422, 301);
    Rectangle obstacle8InstantPressArea = new Rectangle(506, 42, 23, 24);
    Rectangle obstacle8PressArea = new Rectangle(439, 231, 14, 16);
    Area obstacle8Area1 = new Area(
            new Tile(419, 293),
            new Tile(428, 303)
    );
    Tile obstacle8Start = new Tile(425, 295);
    Tile obstacle8End = new Tile(423, 288);
    Rectangle obstacle9InstantPressArea = new Rectangle(600, 210, 97, 84);
    Rectangle obstacle9PressArea = new Rectangle(527, 294, 97, 87);
    Area obstacle9Area = new Area(
            new Tile(416, 280),
            new Tile(430, 291)
    );
    Tile obstacle9Start = new Tile(426, 286);
    Tile obstacle9End = new Tile(525, 100);
    Area obstacleEndArea = new Area(
            new Tile(506, 91),
            new Tile(551, 123)
    );
    Tile[] obstacleEndPath = new Tile[] {
            new Tile(523, 113),
            new Tile(518, 127),
            new Tile(515, 138),
            new Tile(514, 146)
    };

    Rectangle roof1MoG1 = new Rectangle(228, 114, 15, 13);
    Rectangle roof2MoG1 = new Rectangle(487, 188, 15, 15);
    Rectangle roof6MoG1 = new Rectangle(148, 253, 21, 19);
    Rectangle roof7MoG1 = new Rectangle(606, 187, 14, 17);
    Rectangle roof8MoG1 = new Rectangle(349, 188, 12, 13);

    public Pollnivneach(){
        super();
        super.name = "Pollnivneach";
        initializeTileRectangleMap();
    }
    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        return (dAgility.courseChosen.equals("Pollnivneach"));
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
                Condition.sleep(generateRandomDelay(3450, 3600));
                Client.tap(new Rectangle(575, 111, 39, 100));
                Condition.sleep(generateRandomDelay(1950, 2100));
                Condition.wait(() -> checkTileandArea(obstacle2End, obstacle2FailArea), 150, 50);
            } else if(tileEquals(obstacle1End, currentLocation)){
                Paint.setStatus("Traverse obstacle 2");
                Logger.debugLog("Traverse obstacle 2.");
                Client.tap(obstacle2InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle2End, obstacle2FailArea), 150, 50);
            } else {
                carryOutObstacle_tap(obstacle2Start, obstacle2End, obstacle2PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle2FailArea)){
            Paint.setStatus("Walk back from fail area 2");
            Client.tap(806,127);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 250, 44);
            Condition.sleep(3500);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle3Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof2MoG1, mogColor)) {
                Client.tap(new Rectangle(569, 95, 68, 67));
                Condition.sleep(generateRandomDelay(1950, 2100));
                Condition.wait(() -> checkTileandArea(obstacle3End, obstacle3FailArea), 150, 45);
            } else if(tileEquals(obstacle2End, currentLocation)){
                Paint.setStatus("Traverse obstacle 3");
                Logger.debugLog("Traverse obstacle 3.");
                Client.tap(obstacle3InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle3End, obstacle3FailArea), 150, 45);
            } else {
                carryOutObstacle_tap(obstacle3Start, obstacle3End, obstacle3PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle3FailArea)){
            Paint.setStatus("Walk back from fail area 3");
            Walker.walkPath(obstacle3FailPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 150, 30);
            Condition.sleep(2500);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle4Area)) {
            if(tileEquals(obstacle3End, currentLocation)){
                Paint.setStatus("Traverse obstacle 4");
                Logger.debugLog("Traverse obstacle 4.");
                Client.tap(obstacle4InstantPressArea);
                Condition.wait(() -> atTile(obstacle4End), 150, 50);

            }
            else {
                carryOutObstacle_tap(obstacle4Start, obstacle4End, obstacle4PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle5Area)) {
            if(tileEquals(obstacle4End, currentLocation)){
                Paint.setStatus("Traverse obstacle 5");
                Logger.debugLog("Traverse obstacle 5.");
                Client.tap(obstacle5InstantPressArea);
                Condition.wait(() -> atTile(obstacle5End), 150, 37);
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
                Condition.wait(() -> atTile(obstacle6End), 150, 50);
            }
            else {
                carryOutObstacle_tap(obstacle6Start, obstacle6End, obstacle6PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle7Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof6MoG1, mogColor)) {
                Condition.sleep(generateRandomDelay(1950, 2100));
                Client.tap(new Rectangle(344, 143, 66, 81));
                Condition.sleep(generateRandomDelay(1950, 2100));
                Condition.wait(() -> check2Tiles(obstacle7End, obstacle7End2), 150, 85);
                Condition.sleep(generateRandomDelay(200, 350));
            } else if(tileEquals(currentLocation, obstacle6End)){
                Paint.setStatus("Traverse obstacle 7");
                Logger.debugLog("Traverse obstacle 7.");
                Client.tap(obstacle7InstantPressArea);
                Condition.wait(() -> check2Tiles(obstacle7End, obstacle7End2), 150, 85);
                Condition.sleep(generateRandomDelay(200, 350));
            } else {
                carryOutObstacle_tap(obstacle7Start, obstacle7End, obstacle7PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle8Area1)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof7MoG1, mogColor)) {
                Client.tap(new Rectangle(354, 109, 21, 29));
                Condition.sleep(generateRandomDelay(1950, 2100));
                Condition.wait(() -> atTile(obstacle8End), 150, 48);
            } else if(check2Tiles(obstacle7End, obstacle7End2)){
                Paint.setStatus("Traverse obstacle 8");
                Logger.debugLog("Traverse obstacle 8.");
                Client.tap(obstacle8InstantPressArea);
                Condition.wait(() -> atTile(obstacle8End), 150, 48);
            } else {
                carryOutObstacle_tap(obstacle8Start, obstacle8End, obstacle8PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle9Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof8MoG1, mogColor)) {
                Client.tap(new Rectangle(705, 293, 95, 94));
                Condition.sleep(generateRandomDelay(1950, 2100));
                Condition.wait(() -> atTile(obstacle9End), 150, 40);
            } else if(tileEquals(currentLocation, obstacle8End)){
                Paint.setStatus("Traverse obstacle 9");
                Logger.debugLog("Traverse obstacle 9.");
                Client.tap(obstacle9InstantPressArea);
                Condition.wait(() -> atTile(obstacle9End), 150, 40);
            } else {
                carryOutObstacle_tap(obstacle9Start, obstacle9End, obstacle9PressArea);
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
        Paint.setStatus("Traverse obstacle X");
        if(!atTile(obstacleStart)) {
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
        Paint.setStatus("Traverse start obstacle");
        List<Rectangle> foundRects = Client.getObjectsFromColorsInRect(obstacleColors, screenROI, 10);

        if (foundRects.size() == 1) {
            Rectangle tapTarget = foundRects.get(0);

            // Trim the rectangle by 5 pixels from each side
            int newWidth = Math.max(8, tapTarget.width - 10); // Ensure the width is at least 8 pixels
            int newHeight = Math.max(8, tapTarget.height - 10); // Ensure the height is at least 8 pixels
            int newX = tapTarget.x + 5; // Move the x coordinate to the right by 5 pixels
            int newY = tapTarget.y + 5; // Move the y coordinate down by 5 pixels

            // Create a new rectangle with the updated dimensions and position
            Rectangle adjustedTapTarget = new Rectangle(newX, newY, newWidth, newHeight);
            Logger.debugLog("Located the first obstacle using the color finder, tapping.");
            Client.tap(adjustedTapTarget);
            Condition.wait(() -> atTile(obstacle1End), 250, 35);
        } else {
            Logger.debugLog("Couldn't locate the first obstacle with the color finder, using fallback method.");
            currentLocation = Walker.getPlayerPosition();

           Rectangle tapArea = findRectangleForTile(currentLocation);
           if (tapArea != null) {
               Logger.debugLog("Tapping at rectangle for player position (x:" + currentLocation.x() + "|y:" + currentLocation.y() +
                    ") -> Rectangle(x:" + tapArea.x + ", y:" + tapArea.y + ", width:" + tapArea.width +
                    ", height:" + tapArea.height + ")");
                Client.tap(tapArea);
                Condition.sleep(2500);
                Condition.wait(() -> atTile(obstacle1End), 250, 35);
           } else {
                Logger.debugLog("No corresponding rectangle found for player position (x:" + currentLocation.x() + "|y:" + currentLocation.y() + "), manually heading there");
                Logger.debugLog("Moving to the start of the obstacle.");
                Walker.step(obstacle1Start);
                Condition.wait(() -> atTile(obstacle1Start), 250, 16);
                carryOutStartObstacle_tap();
            }
        }
    }

    private void initializeTileRectangleMap() {
        tileRectangleMap.put(new Tile(510, 148), new Rectangle(388, 255, 13, 17));
        tileRectangleMap.put(new Tile(511, 149), new Rectangle(388, 255, 13, 17));
        tileRectangleMap.put(new Tile(510, 149), new Rectangle(388, 255, 13, 17));
        tileRectangleMap.put(new Tile(511, 148), new Rectangle(388, 255, 13, 17));
        tileRectangleMap.put(new Tile(510, 147), new Rectangle(386, 300, 13, 15));
        tileRectangleMap.put(new Tile(511, 147), new Rectangle(386, 300, 13, 15));
        tileRectangleMap.put(new Tile(510, 146), new Rectangle(385, 344, 15, 15));
        tileRectangleMap.put(new Tile(510, 145), new Rectangle(385, 344, 15, 15));
        tileRectangleMap.put(new Tile(511, 145), new Rectangle(385, 344, 15, 15));
        tileRectangleMap.put(new Tile(511, 146), new Rectangle(348, 345, 15, 14));
        tileRectangleMap.put(new Tile(512, 146), new Rectangle(348, 345, 15, 14));
        tileRectangleMap.put(new Tile(512, 147), new Rectangle(351, 309, 14, 17));
        tileRectangleMap.put(new Tile(511, 147), new Rectangle(351, 309, 14, 17));
        tileRectangleMap.put(new Tile(512, 148), new Rectangle(352, 267, 15, 15));
        tileRectangleMap.put(new Tile(512, 149), new Rectangle(352, 267, 15, 15));
        tileRectangleMap.put(new Tile(513, 147), new Rectangle(304, 301, 14, 16));
        tileRectangleMap.put(new Tile(512, 147), new Rectangle(304, 301, 14, 16));
        tileRectangleMap.put(new Tile(514, 147), new Rectangle(257, 301, 15, 17));
        tileRectangleMap.put(new Tile(515, 147), new Rectangle(257, 301, 15, 17));
        tileRectangleMap.put(new Tile(515, 149), new Rectangle(261, 271, 13, 15));
        tileRectangleMap.put(new Tile(514, 148), new Rectangle(261, 271, 13, 15));
        tileRectangleMap.put(new Tile(514, 150), new Rectangle(265, 228, 14, 14));
        tileRectangleMap.put(new Tile(514, 149), new Rectangle(265, 228, 14, 14));
        tileRectangleMap.put(new Tile(512, 150), new Rectangle(345, 228, 14, 16));
        tileRectangleMap.put(new Tile(510, 150), new Rectangle(389, 227, 14, 14));
        tileRectangleMap.put(new Tile(509, 150), new Rectangle(434, 225, 13, 14));
        tileRectangleMap.put(new Tile(509, 149), new Rectangle(434, 225, 13, 14));
        tileRectangleMap.put(new Tile(508, 148), new Rectangle(480, 254, 14, 15));
        tileRectangleMap.put(new Tile(507, 148), new Rectangle(480, 254, 14, 15));
        tileRectangleMap.put(new Tile(508, 150), new Rectangle(482, 227, 10, 13));
        tileRectangleMap.put(new Tile(508, 149), new Rectangle(482, 227, 10, 13));
        tileRectangleMap.put(new Tile(509, 151), new Rectangle(446, 189, 13, 14));
        tileRectangleMap.put(new Tile(509, 152), new Rectangle(446, 152, 13, 12));
        tileRectangleMap.put(new Tile(510, 152), new Rectangle(403, 151, 11, 14));
        tileRectangleMap.put(new Tile(510, 151), new Rectangle(401, 180, 13, 12));
        tileRectangleMap.put(new Tile(511, 151), new Rectangle(401, 180, 13, 12));
        tileRectangleMap.put(new Tile(516, 146), new Rectangle(191, 357, 14, 15));
        tileRectangleMap.put(new Tile(516, 144), new Rectangle(185, 391, 16, 16));
        tileRectangleMap.put(new Tile(516, 145), new Rectangle(185, 391, 16, 16));
        tileRectangleMap.put(new Tile(517, 144), new Rectangle(149, 393, 15, 16));
        tileRectangleMap.put(new Tile(517, 146), new Rectangle(144, 357, 12, 14));
        tileRectangleMap.put(new Tile(518, 146), new Rectangle(107, 359, 16, 15));
    }

    private Rectangle findRectangleForTile(Tile tile) {
        for (Map.Entry<Tile, Rectangle> entry : tileRectangleMap.entrySet()) {
            if (entry.getKey().x() == tile.x() && entry.getKey().y() == tile.y()) {
                return entry.getValue();
            }
        }
        return null;
    }
}
