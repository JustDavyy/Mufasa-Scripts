package dAgility.Tasks;

import helpers.utils.Tile;
import dAgility.*;
import dAgility.utils.Task;

import static dAgility.dAgility.*;
import static helpers.Interfaces.*;

public class Gnome extends Task {

    public Gnome(){
        super();
        super.name = "Gnome";
    }
    @Override
    public boolean activate() {
        return (dAgility.courseChosen.equals("Gnome"));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    if (obstacle.markHandling.isMarkPresent(obstacle.markHandling.checkArea, obstacle.markHandling.targetColor)) {
                        Paint.setStatus("Pick up mark of grace");
                        Logger.log("Mark of grace detected, picking it up!");
                        obstacle.markHandling.pickUpMark(obstacle.markHandling.checkArea, obstacle.markHandling.tapArea, obstacle.markHandling.endTile);
                    } else {
                        Paint.setStatus("Traverse obstacle " + obstacle.name);
                        proceedWithTraversal(obstacle, currentLocation);
                        if (obstacle.name.equals("Obstacle 7")) {
                            lapCount++;
                        }
                    }
                } else {
                    Paint.setStatus("Traverse obstacle " + obstacle.name);
                    proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 7")) {
                        lapCount++;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
