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

public class Ardougne extends Task {

    Tile startTile = new Tile(10691, 12937, 0);
    Area ardougneArea = new Area(new Tile(10420, 12772, 0), new Tile(10773, 13118, 0));

    @Override
    public boolean activate() {
        return (agi_sdk.courseChosen.equals("Ardougne"));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                boolean markHandled = false;

                // Ardy has a pre-defined mark handler as it only has 1 spot and we don't have to walk for it
                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    for (MarkHandling mark : obstacle.markHandling) {
                        Condition.sleep(generateRandomDelay(200, 400));
                        if (mark.isMarkPresent(mark.checkArea, mark.targetColor)) {
                            Paint.setStatus("Pick up mark of grace");
                            Logger.log("Mark of grace detected, picking it up!");
                            Client.tap(mark.checkArea);
                            Condition.sleep(generateRandomDelay(1750, 2300));
                            Client.tap(mark.tapArea);
                            mogTotal++;
                            Paint.updateBox(0, mogTotal);
                            Logger.log("Total Marks of grace gathered so far: " + mogTotal);
                            Condition.wait(() -> Player.atTile(mark.endTile), 250, 110);
                            markHandled = true;
                            break;
                        }
                    }
                }

                if (!markHandled) {
                    Paint.setStatus("Traverse obstacle " + obstacle.name);
                    Condition.sleep(generateRandomDelay(200, 300));
                    TraverseHelpers.proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 7")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, ardougneArea)) {
            Condition.sleep(generateRandomDelay(450, 600));
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            return true;
        }
        return false;
    }
}
