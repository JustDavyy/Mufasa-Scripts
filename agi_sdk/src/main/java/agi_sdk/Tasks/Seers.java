package agi_sdk.Tasks;

import agi_sdk.helpers.Course;
import agi_sdk.helpers.MarkHandling;
import agi_sdk.helpers.Obstacle;
import agi_sdk.helpers.TraverseHelpers;
import agi_sdk.utils.Task;
import helpers.utils.Area;
import helpers.utils.Spells;
import helpers.utils.Tile;
import helpers.utils.UITabs;

import java.awt.*;
import java.util.Arrays;

import static agi_sdk.main.*;
import static helpers.Interfaces.*;

public class Seers extends Task {
    Tile startTile = new Tile(10915, 13701, 0);
    Tile obs1EndTile = new Tile(10915, 13713, 3);
    Area seersArea = new Area(new Tile(10692, 13450, 0), new Tile(11020, 13808, 0));
    Area obstacle6EndArea = new Area(new Tile(10791, 13557, 0), new Tile(10854, 13636, 0));
    Area teleportArea = new Area(new Tile(10892, 13669, 0), new Tile(10924, 13700, 0));
    Area failArea = new Area(new Tile(10818, 13668, 0), new Tile(10878, 13744, 0));
    Tile[] pathToStart = new Tile[]{
            new Tile(10831, 13597, 0),
            new Tile(10850, 13596, 0),
            new Tile(10864, 13596, 0),
            new Tile(10876, 13609, 0),
            new Tile(10881, 13621, 0),
            new Tile(10891, 13637, 0),
            new Tile(10899, 13649, 0),
            new Tile(10904, 13662, 0),
            new Tile(10909, 13674, 0),
            new Tile(10913, 13687, 0)
    };

    Rectangle screenROI = new Rectangle(316, 4, 316, 330);
    Area obs1Area = new Area(new Tile(10894, 13668, 0), new Tile(10939, 13715, 0));
    java.util.List<Color> startObstacleColors = Arrays.asList(
            Color.decode("#20ff25"),
            Color.decode("#20ff26"),
            Color.decode("#21ff27")
    );
    Tile[] fallPathToStart = new Tile[]{
            new Tile(10866, 13693, 0),
            new Tile(10880, 13684, 0),
            new Tile(10893, 13684, 0),
            new Tile(10907, 13682, 0),
            new Tile(10916, 13687, 0)
    };

    @Override
    public boolean activate() {
        return (courseChosen.equals(Course.SEERS) || courseChosen.equals(Course.SEERS_TELEPORT));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        // Block that assumes we are at the end of the last obstacle
        if (Player.isTileWithinArea(currentLocation, obstacle6EndArea)) {
            if (useSeersTeleport) {
                Logger.debugLog("Teleporting back to the start obstacle");
                Paint.setStatus("Teleport to start obstacle");
                Magic.castSpell(Spells.CAMELOT_TELEPORT);
                Condition.wait(() -> Player.within(teleportArea), 150, 50);
            } else {
                Logger.debugLog("Walking back to the start obstacle");
                Paint.setStatus("Walk to start obstacle");
                Walker.walkPath(pathToStart);
                Player.waitTillNotMoving(17);
            }
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
                    TraverseHelpers.proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 6")) {
                        if (useSeersTeleport) {
                            GameTabs.openTab(UITabs.MAGIC);
                        }
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are within the fail area of seers
        if (Player.isTileWithinArea(currentLocation, failArea)) {
            Logger.debugLog("Within fail area, walking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.walkPath(fallPathToStart);
            Player.waitTillNotMoving(15);
            return true;
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, seersArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            return true;
        }
        return false;
    }
}
