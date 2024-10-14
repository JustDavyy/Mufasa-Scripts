package dAgility.Tasks;

import dAgility.dAgility;
import dAgility.utils.Task;
import helpers.utils.Area;
import helpers.utils.Tile;

import static dAgility.dAgility.*;
import static helpers.Interfaces.*;

public class Falador extends Task {

    Tile startTile = new Tile(12143, 13109, 0);
    Area faladorArea = new Area(new Tile(11983, 13033, 0), new Tile(12279, 13306, 0));

    public Falador(){
        super();
        super.name = "Falador";
    }
    @Override
    public boolean activate() {
        return (dAgility.courseChosen.equals("Falador"));
    }

    @Override
    public boolean execute() {
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
                            mark.pickUpMark(mark.checkArea, mark.tapArea, mark.endTile);
                            markHandled = true;
                            break;
                        }
                    }
                }

                if (!markHandled) {
                    Paint.setStatus("Traverse obstacle " + obstacle.name);
                    proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 13")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, faladorArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            return true;
        }
        return false;
    }
}
