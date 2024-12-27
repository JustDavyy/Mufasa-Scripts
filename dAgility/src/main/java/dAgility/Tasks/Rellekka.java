package dAgility.Tasks;

import dAgility.dAgility;
import dAgility.utils.Task;
import helpers.utils.Area;
import helpers.utils.Tile;

import java.awt.*;
import java.util.Arrays;

import static dAgility.dAgility.*;
import static helpers.Interfaces.*;

public class Rellekka extends Task {

    Tile startTile = new Tile(10499, 14461, 0);
    Tile obs1EndTile = new Tile(10499, 14453, 3);
    Area obs1Area = new Area(
            new Tile(10477, 14430, 0),
            new Tile(10521, 14481, 0)
    );
    Area rellekkaArea = new Area(new Tile(10421, 14322, 0), new Tile(10779, 14619, 0));
    Area obstacle7EndArea = new Area(new Tile(10597, 14433, 0),new Tile(10630, 14467, 0));
    Tile[] pathToStart = new Tile[] {
            new Tile(10592, 14453, 0),
            new Tile(10570, 14453, 0),
            new Tile(10545, 14457, 0),
            new Tile(10529, 14460, 0),
            new Tile(10503, 14469, 0)
    };
    java.util.List<Color> startObstacleColors = Arrays.asList(
            Color.decode("#20ff25")
    );
    Rectangle screenROI = new Rectangle(312, 197, 220, 219);

    public Rellekka(){
        super();
        super.name = "Rellekka";
    }
    @Override
    public boolean activate() {
        return (dAgility.courseChosen.equals("Rellekka"));
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
            Player.waitTillNotMoving(14);
            Paint.setStatus("Fetch player position");
            currentLocation = Walker.getPlayerPosition();
            Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);
        }

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
                    proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 7")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, rellekkaArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            return true;
        }
        return false;
    }
}
