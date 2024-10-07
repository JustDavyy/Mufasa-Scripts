package dAgility.Tasks;

import dAgility.dAgility;
import helpers.utils.Area;
import helpers.utils.RegionBox;
import helpers.utils.Tile;

import java.util.*;

import dAgility.utils.Task;

import java.awt.*;
import java.util.List;

import static helpers.Interfaces.*;
import static dAgility.dAgility.*;

public class Seers extends Task {
    Color mogColor = new Color(Integer.parseInt("ca8818", 16)); // hex code #ca8818 for MoG on this course

    RegionBox seersRegion = new RegionBox("SeersAgility", 357, 1167, 1266, 1662); //TEMPORARY TILL CLIENT UPDATE!
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
    private static final Random random = new Random();
    Rectangle screenROI = new Rectangle(184, 75, 485, 446);
    Rectangle obstacle1PressArea = new Rectangle(440, 229, 13, 8);
    Area obstacle1Area = new Area(
            new Tile(186, 415),
            new Tile(216, 444)
    );
    Tile obstacle1Start = new Tile(198, 432);
    Tile obstacle1End = new Tile(310, 445);
    Tile obstacle1End2 = new Tile(311, 446);
    Tile obstacle1End3 = new Tile(310, 446);
    Rectangle obstacle2InstantPressArea = new Rectangle(48, 86, 32, 93);
    Rectangle obstacle2PressArea = new Rectangle(328, 168, 42, 212);
    Area obstacle2Area = new Area(
            new Tile(295, 431),
            new Tile(315, 451)
    );
    Tile obstacle2Start = new Tile(301, 441);
    Tile obstacle2End = new Tile(378, 442);
    Tile obstacle2End2 = new Tile(379,442);
    Area obstacle3FailArea = new Area(
            new Tile(160, 413),
            new Tile(184, 448)
    );
    Rectangle obstacle3InstantPressArea = new Rectangle(284, 500, 24, 26);
    Rectangle obstacle3PressArea = new Rectangle(431, 315, 21, 21);
    Area obstacle3Area = new Area(
            new Tile(360, 430),
            new Tile(393, 456)
    );
    Tile obstacle3Start = new Tile(374, 447);
    Tile obstacle3Start2 = new Tile(375, 448);
    Tile obstacle3End = new Tile(374, 461);
    Tile obstacle3End2 = new Tile(374, 461);
    Rectangle obstacle4InstantPressArea = new Rectangle(415, 434, 202, 60);
    Rectangle obstacle4PressArea = new Rectangle(435, 277, 92, 21);
    Area obstacle4Area = new Area(
            new Tile(367, 453),
            new Tile(386, 469)
    );
    Tile obstacle4Start = new Tile(374, 465);
    Tile obstacle4End = new Tile(285, 471);
    Rectangle obstacle5InstantPressArea = new Rectangle(60, 391, 61, 47);
    Rectangle obstacle5PressArea = new Rectangle(409, 304, 46, 37);
    Area obstacle5Area = new Area(
            new Tile(265, 461),
            new Tile(298, 482)
    );
    Tile obstacle5Start = new Tile(274, 473);
    Tile obstacle5End = new Tile(364, 481);
    Tile obstacle5End2 = new Tile(364, 481);
    Rectangle obstacle6InstantPressArea = new Rectangle(480, 256, 42, 220);
    Rectangle obstacle6PressArea = new Rectangle(480, 256, 42, 220);
    Area obstacle6Area = new Area(
            new Tile(343, 474),
            new Tile(368, 492)
    );
    Tile obstacle6Start = new Tile(364, 482);
    Tile obstacle6End = new Tile(165, 463);
    Tile obstacle6End2 = new Tile(164, 462);
    Area obstacleEndArea = new Area(
            new Tile(157, 454),
            new Tile(174, 477)
    );

    Area walkBackArea = new Area(
            new Tile(170, 440),
            new Tile(214, 484)
    );
    Tile[] obstacleEndPath = new Tile[] {
            new Tile(174, 465),
            new Tile(182, 465),
            new Tile(186, 458),
            new Tile(188, 453),
            new Tile(193, 446),
            new Tile(195, 441),
            new Tile(195, 436)
    };

    // MoG spots
    Rectangle roof3MoG1 = new Rectangle(522, 235, 16, 14);
    Rectangle roof1MoG1 = new Rectangle(358, 190, 15, 13);
    Rectangle roof2MoG1 = new Rectangle(253, 315, 18, 15);
    Rectangle roof2MoG2 = new Rectangle(114, 318, 16, 11);
    Rectangle roof5MoG1 = new Rectangle(259, 273, 16, 14);

    public Seers(){
        super();
        super.name = "Seers";
    }

    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        return (dAgility.courseChosen.equals("Seers") || dAgility.courseChosen.equals("Seers - teleport"));
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        // If player within obstacle area
        if(Player.isTileWithinArea(currentLocation, obstacle1Area)) {
            carryOutStartObstacle_tap();
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle2Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof1MoG1, mogColor)) {
                Client.tap(new Rectangle(112, 164, 35, 140));
                Condition.wait(() -> checkTileandArea(obstacle2End, obstacle3FailArea), 100, 75);
            } else if(check3Tiles(obstacle1End, obstacle1End2, obstacle1End3)){
                Paint.setStatus("Traverse obstacle 2");
                Logger.log("Traversing obstacle 2.");
                Client.tap(obstacle2InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle2End, obstacle3FailArea), 100, 75);
            } else {
                carryOutObstacle_tap(obstacle2Start, obstacle2End, obstacle2PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle3Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof2MoG1, mogColor)) {
                Client.tap(new Rectangle(476, 453, 30, 32));
                Condition.wait(() -> checkTileandArea(obstacle3End, obstacle3FailArea), 100, 85);
            } else if (mogPresent(roof2MoG2, mogColor)) {
                Client.tap(new Rectangle(624, 452, 30, 31));
                Condition.sleep(generateRandomDelay(1900, 2300));
                Condition.wait(() -> checkTileandArea(obstacle3End, obstacle3FailArea), 100, 85);
            } else if(check2Tiles(obstacle2End, obstacle2End2)){
                Paint.setStatus("Traverse obstacle 3");
                Logger.log("Traversing obstacle 3.");
                Client.tap(obstacle3InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle3End, obstacle3FailArea), 100, 85);
            } else if (atTile(obstacle3Start2)) {
                carryOutObstacle_tap(obstacle3Start2, obstacle3End, obstacle3PressArea);
            } else {
                carryOutObstacle_tap(obstacle3Start, obstacle3End, obstacle3PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle3FailArea)){
            Paint.setStatus("Walk back from fail area 3");
            Logger.log("Looks like we fell, going back to obstacle 1!");
            Condition.sleep(generateRandomDelay(1200, 1750));
            if (Walker.isReachable(obstacle1Start, seersRegion)) {
                Walker.step(obstacle1Start);
            } else {
                Walker.step(new Tile(185, 435));
                Walker.step(obstacle1Start);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle4Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof3MoG1, mogColor)) {
                Client.tap(new Rectangle(317, 475, 275, 59));
                Condition.wait(() -> atTile(obstacle4End), 100, 110);
            } else if (check2Tiles(obstacle3End, obstacle3End2)) {
                Paint.setStatus("Traverse obstacle 4");
                Logger.log("Traversing obstacle 4.");
                Client.tap(obstacle4InstantPressArea);
                Condition.wait(() -> atTile(obstacle4End), 100, 110);
            } else {
                carryOutObstacle_tap(obstacle4Start, obstacle4End, obstacle4PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle5Area)) {
            if(tileEquals(obstacle4End, currentLocation)){
                Paint.setStatus("Traverse obstacle 5");
                Logger.log("Traversing obstacle 5.");
                Client.tap(obstacle5InstantPressArea);
                Condition.wait(() -> check2Tiles(obstacle5End, obstacle5End2), 100, 70);
            } else if (tileEquals(new Tile(288, 471), currentLocation)) {
                // This is where we end up after picking up a MoG; different tap is needed
                Client.tap(new Rectangle(353, 275, 16, 15));
                Condition.wait(() -> atTile(new Tile(285, 470)), 100, 18);
                if (atTile(new Tile(285, 470))) {
                    Client.tap(obstacle5InstantPressArea);
                    Condition.wait(() -> check2Tiles(obstacle5End, obstacle5End2), 100, 70);
                }
            } else {
                carryOutObstacle_tap(obstacle5Start, obstacle5End, obstacle5PressArea);
                Condition.wait(() -> check2Tiles(obstacle5End, obstacle5End2), 100, 70);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle6Area)) {
            Condition.sleep(generateRandomDelay(450, 900));
            if (mogPresent(roof5MoG1, mogColor)) {
                Client.tap(new Rectangle(670, 249, 27, 139));
                if (useSeersTeleport) {
                    GameTabs.openMagicTab();
                }
                Condition.wait(() -> check2Tiles(obstacle6End, obstacle6End2), 100, 70);
            }else if(check2Tiles(obstacle5End, obstacle5End2)){
                Paint.setStatus("Traverse obstacle 6");
                Logger.log("Traversing obstacle 6.");
                Client.tap(obstacle6InstantPressArea);
                if (useSeersTeleport) {
                    GameTabs.openMagicTab();
                }
                Condition.wait(() -> check2Tiles(obstacle6End, obstacle6End2), 100, 70);
            } else {
                carryOutObstacle_tap(obstacle6Start, obstacle6End, obstacle6PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacleEndArea)){
            lapCount = lapCount + 1;
            if (useSeersTeleport) {
                Paint.setStatus("Use seers teleport");
                Magic.tapCamelotTeleportSpell();
                Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 100, 40);
            } else {
                Paint.setStatus("Walk to start obstacle");
                Walker.walkPath(obstacleEndPath);
                Condition.sleep(generateRandomDelay(2900, 3500));
                return true;
            }

        }
        else if (Player.isTileWithinArea(currentLocation, walkBackArea)) {
            Logger.debugLog("We are located in the area to move back to the start location... Something must have gone wrong.");
            // Logic to make the player walk this path
            Logger.debugLog("Moving back to the start location now!");
            Walker.walkPath(obstacleEndPath);
            Condition.sleep(generateRandomDelay(2900, 3500));
            return true;
        }
        return false;
    }

    public void carryOutObstacle_tap(Tile obstacleStart, Tile obstacleEnd, Rectangle pressArea) {
        moveToAndTap(obstacleStart, obstacleEnd, pressArea);
    }

    public void carryOutStartObstacle_tap() {
        Condition.sleep(generateRandomDelay(700, 1200));
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
            moveToAndTap(obstacle1Start, new Tile[] {obstacle1End, obstacle1End2, obstacle1End3}, obstacle1PressArea);
        }
    }

    private void moveToAndTap(Tile startTile, Tile endTile, Rectangle pressArea) {
        if (!atTile(startTile)) {
            Logger.debugLog("Moving to the start of the obstacle.");
            Walker.step(startTile);
            Condition.wait(() -> atTile(startTile), 100, 50);
        }
        if (atTile(startTile)) {
            Logger.debugLog("Player is at start of obstacle.");
            Client.tap(pressArea);
            Condition.wait(() -> atTile(endTile), 100, 70);
        }
    }

    private void moveToAndTap(Tile startTile, Tile[] endTiles, Rectangle pressArea) {
        if (!atTile(startTile)) {
            Logger.debugLog("Moving to the start of the obstacle.");
            Walker.step(startTile);
            Condition.wait(() -> atTile(startTile), 100, 63);
        }
        if (atTile(startTile)) {
            Client.tap(pressArea);
            closeSeersTeleportMagicTab();
            Condition.wait(() -> checkAnyTile(endTiles), 100, 88);
        } else {
            Logger.log("We did not locate the start obstacle.");
        }
    }

    private Rectangle adjustRectangle(Rectangle rect) {
        int newWidth = Math.max(8, rect.width - 20);
        int newHeight = Math.max(8, rect.height - 20);
        int newX = rect.x + 10;
        int newY = rect.y + 10;
        return new Rectangle(newX, newY, newWidth, newHeight);
    }

    private void closeSeersTeleportMagicTab() {
        if (dAgility.useSeersTeleport) {
            GameTabs.closeMagicTab();
        }
    }

    private boolean checkAnyTile(Tile[] tiles) {
        for (Tile tile : tiles) {
            if (atTile(tile)) {
                return true;
            }
        }
        return false;
    }
}
