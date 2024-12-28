package agi_sdk.Tasks;

import agi_sdk.helpers.Course;
import agi_sdk.helpers.MarkHandling;
import agi_sdk.helpers.Obstacle;
import agi_sdk.helpers.TraverseHelpers;
import agi_sdk.runner;
import agi_sdk.utils.Task;
import helpers.utils.Area;
import helpers.utils.Skills;
import helpers.utils.Tile;
import helpers.utils.UITabs;

import java.awt.*;
import java.util.Arrays;

import static agi_sdk.runner.*;
import static helpers.Interfaces.*;

public class Varrock extends Task {

    Tile startTile = new Tile(12895, 13405, 0);
    Area varrockArea = new Area(new Tile(12731, 13275, 0), new Tile(13101, 13571, 0));
    Area obs1Area = new Area(new Tile(12842, 13383, 0), new Tile(12975, 13453, 0));
    Tile obs1EndTile = new Tile(12875, 13405, 3);
    java.util.List<Color> startObstacleColors = Arrays.asList(
            Color.decode("#20ff25"),
            Color.decode("#20ff26")
    );
    Rectangle screenROI = new Rectangle(239, 160, 360, 235);
    Area obs6MoGArea = new Area(new Tile(12724, 13309, 3), new Tile(12761, 13345, 3));
    Tile obs6AfterMoGTile = new Tile(12794, 13331, 3);

    @Override
    public boolean activate() {
        return (courseChosen.equals(Course.VARROCK));
    }

    @Override
    public boolean execute() {
        // Progressive mode block
        if (useProgressive) {
            if (Player.leveledUp()) {
                Logger.debugLog("Agility level: " + agilityLevel);
                Logger.debugLog("Leveled up!");
                agilityLevel++;
                lastLevelCheck = System.currentTimeMillis();
                Logger.debugLog("Agility level is now: " + agilityLevel);
            }
            if (agilityLevel >= 50) {
                Logger.debugLog("Agility level is 50 or higher (" + agilityLevel + "), double checking...");
                if (!GameTabs.isTabOpen(UITabs.STATS)) {
                    Logger.debugLog("Open Stats tab...");
                    GameTabs.openTab(UITabs.STATS);
                }

                agilityLevel = Stats.getRealLevel(Skills.AGILITY);

                Logger.debugLog("Agility level after verification: " + agilityLevel);

                if (agilityLevel >= 50) {
                    Logger.debugLog("Agility level verified to be level 50 or higher. Done progressing to level 50!");
                    Logger.debugLog("Logging out and stopping script!");
                    Logout.logout();
                    Script.stop();
                    return true;
                }
                GameTabs.closeTab(UITabs.STATS);
                Condition.sleep(generateRandomDelay(500, 750));
                if (GameTabs.isTabOpen(UITabs.STATS)) {
                    GameTabs.closeTab(UITabs.STATS);
                }
            }
        }

        runner.updateStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        if (Player.isTileWithinArea(currentLocation, obs1Area)) {
            Logger.debugLog("Colorfinding start obstacle");
            runner.updateStatus("Colorfind obstacle 1");

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
                runner.updateStatus("Fetch player position");
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

        if (Player.isTileWithinArea(currentLocation, obs6MoGArea)) {
            Walker.walkTo(obs6AfterMoGTile);
            Condition.sleep(generateRandomDelay(1250, 2000));
            return true;
        }

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                boolean markHandled = false;

                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    for (MarkHandling mark : obstacle.markHandling) {
                        Condition.sleep(generateRandomDelay(200, 400));
                        if (mark.isMarkPresent(mark.checkArea, mark.targetColor)) {
                            runner.updateStatus("Pick up mark of grace");
                            Logger.log("Mark of grace detected, picking it up!");
                            mark.pickUpMark(mark.checkArea, mark.tapArea, mark.endTile, obstacle.failArea, obstacle.checkForFail);
                            markHandled = true;
                            break;
                        }
                    }
                }

                if (!markHandled) {
                    runner.updateStatus("Traverse obstacle " + obstacle.name);
                    TraverseHelpers.proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 9")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, varrockArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            runner.updateStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            return true;
        }
        return false;
    }
}
