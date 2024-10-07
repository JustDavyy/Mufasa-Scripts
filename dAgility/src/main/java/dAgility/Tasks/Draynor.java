package dAgility.Tasks;

import dAgility.dAgility;
import dAgility.utils.Task;
import helpers.utils.Area;
import helpers.utils.Tile;

import static dAgility.dAgility.*;
import static helpers.Interfaces.*;

public class Draynor extends Task {

    Tile startTile = new Tile(12415, 12865, 0);
    Area draynorArea = new Area(new Tile(12275, 12663, 0), new Tile(12540, 12981, 0));
    Area obstacle7EndArea = new Area(new Tile(12400, 12765, 0), new Tile(12432, 12846, 0));
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
            Player.waitTillNotMoving(20);
        }

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                boolean markHandled = false;

                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    for (MarkHandling mark : obstacle.markHandling) {
                        if (mark.isMarkPresent(mark.checkArea, mark.targetColor)) {
                            Paint.setStatus("Pick up mark of grace");
                            Logger.log("Mark of grace detected, picking it up!");
                            mark.pickUpMark(mark.checkArea, mark.tapArea, mark.endTile);
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
        if (Player.isTileWithinArea(currentLocation, draynorArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            return true;
        }
        return false;
    }
}
