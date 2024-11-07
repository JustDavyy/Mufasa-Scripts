package dAgility.Tasks;

import dAgility.dAgility;
import dAgility.utils.Task;
import helpers.utils.Area;
import helpers.utils.Tile;

import java.awt.*;
import java.util.Arrays;

import static dAgility.dAgility.*;
import static helpers.Interfaces.*;
import static dAgility.dAgility.startTileStorage.*;

public class Draynor extends Task {

    Tile startTile = new Tile(12415, 12865, 0);
    Tile obs1EndTile = new Tile(12407, 12865, 3);
    Area draynorArea = new Area(new Tile(12275, 12663, 0), new Tile(12540, 12981, 0));
    Area obstacle7EndArea = new Area(new Tile(12400, 12765, 0), new Tile(12432, 12846, 0));
    Area obs1Area = new Area(new Tile(12405, 12848, 0), new Tile(12443, 12879, 0));
    java.util.List<Color> startObstacleColors = Arrays.asList(
            Color.decode("#20ff25"),
            Color.decode("#20ff26")
    );
    Rectangle screenROI = new Rectangle(102, 85, 408, 372);
    Tile[] pathToStart = new Tile[] {
            new Tile(12417, 12802, 0),
            new Tile(12419, 12822, 0),
            new Tile(12419, 12838, 0),
            new Tile(12422, 12858, 0)
    };

    public Draynor(){
        super();
        super.name = "Draynor";
    }
    @Override
    public boolean activate() {
        return (dAgility.courseChosen.equals("Draynor"));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        // Block that assumes we are at the end of the last obstacle
        if (Player.isTileWithinArea(currentLocation, obstacle7EndArea)) {
            Logger.debugLog("Walking back to the start obstacle");
            Paint.setStatus("Walk to start obstacle");
            Walker.walkPath(pathToStart);
            Player.waitTillNotMoving(17);
        }

        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);
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
                Condition.wait(() -> Player.atTile(obs1EndTile), 200, 35);
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
                            Paint.setStatus("Pick up mark of grace");
                            Logger.log("Mark of grace detected, picking it up!");
                            mark.pickUpMark(mark.checkArea, mark.tapArea, mark.endTile, mark.failArea, mark.checkForFail);
                            markHandled = true;
                            break;
                        }
                    }
                }

                if (!markHandled) {
                    Paint.setStatus("Traverse obstacle " + obstacle.name);
                    if (obstacle.name.equals("Obstacle 5")) {
                        Condition.sleep(generateRandomDelay(500, 700));
                    }
                    proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 7")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, draynorArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            return true;
        }
        return false;
    }
}
