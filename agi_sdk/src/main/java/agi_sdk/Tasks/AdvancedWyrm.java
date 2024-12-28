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

import static agi_sdk.runner.*;
import static helpers.Interfaces.*;

public class AdvancedWyrm extends Task {
    private final Area wyrmCourseArea = new Area(
            new Tile(6423, 11553, 0),
            new Tile(6684, 11316, 0)
    );
    private final Tile startTile = new Tile(6603, 11473, 0);

    @Override
    public boolean activate() {
        return (courseChosen.equals(Course.ADVANCED_COLOSSAL_WYRM));
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
            } else {
                if (lastLevelCheck > 0 && System.currentTimeMillis() - lastLevelCheck > 1800000) {
                    lastLevelCheck = System.currentTimeMillis();
                }
            }
        }

        runner.updateStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                boolean markHandled = false;

                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    for (MarkHandling mark : obstacle.markHandling) {
                        if (mark.isMarkPresent(mark.checkArea, mark.targetColor)) {
                            runner.updateStatus("Pick up mark of grace");
                            Logger.log("Mark of grace detected, picking it up!");
                            mark.pickUpMark(mark.checkArea, mark.tapArea, mark.endTile, mark.failArea, mark.checkForFail);
                            markHandled = true;
                            break;
                        }
                    }
                }

                if (!markHandled) {
                    runner.updateStatus("Traverse obstacle " + obstacle.name);
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
            runner.updateStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            Walker.step(startTile);
            return true;
        }
        return false;
    }
}
