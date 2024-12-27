package agi_sdk.Tasks;

import agi_sdk.agi_sdk;
import agi_sdk.helpers.MarkHandling;
import agi_sdk.helpers.Obstacle;
import agi_sdk.helpers.TraverseHelpers;
import agi_sdk.utils.Task;
import helpers.utils.Area;
import helpers.utils.Skills;
import helpers.utils.Tile;
import helpers.utils.UITabs;

import static agi_sdk.agi_sdk.*;
import static helpers.Interfaces.*;

public class BasicWyrm extends Task {
    private final Tile endTile = new Tile(6579, 11481, 0);
    private final Area wyrmCourseArea = new Area(
            new Tile(6423, 11553, 0),
            new Tile(6684, 11316, 0)
    );
    private final Tile startTile = new Tile(6603, 11473, 0);

    @Override
    public boolean activate() {
        return (agi_sdk.courseChosen.equals("Basic Colossal Wyrm"));
    }

    @Override
    public boolean execute() {
        // Progressive mode block
        if (useProgressive) {
            if (Player.leveledUp()) {
                Logger.debugLog("Agility level: " + agilityLevel);
                Logger.debugLog("Leveled up!");
                agilityLevel++;
                Logger.debugLog("Agility level is now: " + agilityLevel);
            }
            if (agilityLevel >= 62 && Player.atTile(endTile)) {
                Logger.debugLog("Agility level is 62 or higher (" + agilityLevel + "), double checking...");
                if (!GameTabs.isTabOpen(UITabs.STATS)) {
                    Logger.debugLog("Open Stats tab...");
                    GameTabs.openTab(UITabs.STATS);
                }

                agilityLevel = Stats.getRealLevel(Skills.AGILITY);

                Logger.debugLog("Agility level after verification: " + agilityLevel);

                if (agilityLevel >= 62) {
                    Logger.debugLog("Agility level verified to be level 62 or higher. Switching to advanced course!");
                    GameTabs.closeTab(UITabs.STATS);
                    Condition.sleep(generateRandomDelay(500, 750));
                    changeProgressiveCourse("Advanced Colossal Wyrm");
                    if (GameTabs.isTabOpen(UITabs.STATS)) {
                        GameTabs.closeTab(UITabs.STATS);
                    }
                    return true;
                }
                GameTabs.closeTab(UITabs.STATS);
            }
        }

        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                boolean markHandled = false;

                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    for (MarkHandling mark : obstacle.markHandling) {
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
                    if (obstacle.name.equals("Obstacle 6")) {
                        Condition.sleep(generateRandomDelay(100, 200));
                    }
                    TraverseHelpers.proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 6")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, wyrmCourseArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            Walker.step(startTile);
            return true;
        }
        return false;
    }
}
