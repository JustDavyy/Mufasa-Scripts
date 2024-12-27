package agi_sdk.Tasks;

import agi_sdk.dAgility;
import agi_sdk.utils.Task;
import helpers.utils.Area;
import helpers.utils.Tile;

import java.awt.*;
import java.util.Arrays;

import static agi_sdk.dAgility.*;
import static helpers.Interfaces.*;

public class Pollnivneach extends Task {

    Tile startTile = new Tile(13403, 11593, 0);
    Tile obs1EndTile = new Tile(13403, 11605, 1 );
    Area obs1Area = new Area(new Tile(13390, 11576, 0), new Tile(13425, 11619, 0));
    Area pollyArea = new Area(new Tile(13310, 11500, 0), new Tile(13552, 11794, 0));
    Area obstacle9EndArea = new Area(new Tile(13441, 11717, 0), new Tile(13477, 11752, 0));
    Tile[] pathToStart = new Tile[] {
            new Tile(13449, 11727, 0),
            new Tile(13450, 11692, 0),
            new Tile(13444, 11664, 0),
            new Tile(13433, 11637, 0),
            new Tile(13426, 11613, 0),
            new Tile(13411, 11601, 0)
    };
    Rectangle screenROI = new Rectangle(104, 170, 478, 320);
    java.util.List<Color> startObstacleColors = Arrays.asList(
            Color.decode("#20ff25"),
            Color.decode("#20ff26"),
            Color.decode("#21ff27"),
            Color.decode("#21ff26")
    );

    public Pollnivneach(){
        super();
        super.name = "Pollnivneach";
    }
    @Override
    public boolean activate() {
        return (agi_sdk.courseChosen.equals("Pollnivneach"));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        // Block that assumes we are at the end of the last obstacle
        if (Player.isTileWithinArea(currentLocation, obstacle9EndArea)) {
            Logger.debugLog("Walking back to the start obstacle");
            Paint.setStatus("Walk to start obstacle");
            Walker.walkPath(pathToStart);
            Player.waitTillNotMoving(20);
            Paint.setStatus("Fetch player position");
            currentLocation = Walker.getPlayerPosition();
            Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);
        }

        // Color find start obstacle
        if (Player.isTileWithinArea(currentLocation, obs1Area)) {
            Logger.debugLog("Colorfinding start obstacle");
            Paint.setStatus("Colorfind obstacle 1");

            java.util.List<Point> foundPoints = Client.getPointsFromColorsInRect(startObstacleColors, screenROI, 10);

            if (!foundPoints.isEmpty()) {
                // Calculate the center point of all found points
                int totalX = 0;
                int totalY = 0;

                for (Point p : foundPoints) {
                    totalX += p.x;
                    totalY += p.y;
                }

                // Compute the average (center) point
                Point centerPoint = new Point(totalX / foundPoints.size(), totalY / foundPoints.size());

                Logger.debugLog("Located the first obstacle using the color finder, tapping around the center point.");
                Client.tap(centerPoint);
                Condition.wait(() -> Player.atTile(obs1EndTile), 200, 45);
                Paint.setStatus("Fetch player position");
                currentLocation = Walker.getPlayerPosition();
                Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);
            } else {
                for (Obstacle obstacle : obstacles) {
                    if (obstacle.name.equals("Obstacle 1")) {
                        Walker.step(obstacle.startTile);

                        if (Player.atTile(obstacle.startTile)) {
                            Client.tap(obstacle.pressArea);
                            Condition.wait(() -> Player.atTile(obstacle.endTile), 100, 80);
                        }

                        return true;
                    }
                }
            }
        }

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                boolean markHandled = false;

                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    for (MarkHandling mark : obstacle.markHandling) {
                        Condition.sleep(generateRandomDelay(200, 400));
                        if (mark.isMarkPresent(mark.checkArea, mark.targetColor)) {
                            if (obstacle.name.equals("Obstacle 8") || obstacle.name.equals("Obstacle 9")) {
                                Condition.sleep(generateRandomDelay(850, 1100));
                            }
                            Paint.setStatus("Pick up mark of grace");
                            Logger.log("Mark of grace detected, picking it up!");
                            mark.pickUpMark(mark.checkArea, mark.tapArea, mark.endTile, mark.failArea, mark.checkForFail);
                            markHandled = true;
                            break;
                        }
                    }
                }

                if (!markHandled) {
                    if (obstacle.name.equals("Obstacle 8") || obstacle.name.equals("Obstacle 9")) {
                        Condition.sleep(generateRandomDelay(850, 1100));
                    }
                    Paint.setStatus("Traverse obstacle " + obstacle.name);
                    proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 9")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, pollyArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            return true;
        }
        return false;
    }
}
