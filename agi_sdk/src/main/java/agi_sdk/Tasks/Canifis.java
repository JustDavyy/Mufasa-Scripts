package agi_sdk.Tasks;

import agi_sdk.agi_sdk;
import agi_sdk.helpers.MarkHandling;
import agi_sdk.helpers.Obstacle;
import agi_sdk.helpers.TraverseHelpers;
import agi_sdk.utils.Task;
import helpers.utils.Area;
import helpers.utils.Tile;

import static agi_sdk.agi_sdk.*;
import static helpers.Interfaces.*;

public class Canifis extends Task {

    Tile startTile = new Tile(12887, 13405, 0);
    Area canifisArea = new Area(new Tile(13835, 13588, 0), new Tile(14096, 13822, 0));
    Area obs4FailArea = new Area(new Tile(13906, 13714, 0), new Tile(13960, 13766, 0));
    Tile[] obs4FailPath = new Tile[]{
            new Tile(13937, 13726, 0),
            new Tile(13958, 13710, 0),
            new Tile(13987, 13699, 0),
            new Tile(14008, 13696, 0),
            new Tile(14027, 13700, 0)
    };

    @Override
    public boolean activate() {
        return (agi_sdk.courseChosen.equals("Canifis"));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                boolean markHandled = false;

                if (obstacle.name.equals("Obstacle 4")) {
                    Condition.sleep(generateRandomDelay(600, 850));
                }

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
                    if (obstacle.name.equals("Obstacle 9")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        if (Player.within(obs4FailArea)) {
            Logger.debugLog("Failed obstacle 4, walking back to start.");
            Paint.setStatus("Recover after fall/failure");
            Walker.walkPath(obs4FailPath);
            Player.waitTillNotMoving(10);
            Walker.step(startTile);
            return true;
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, canifisArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            return true;
        }
        return false;
    }
}
