package dAgility.Tasks;

import helpers.utils.Tile;
import dAgility.*;
import dAgility.utils.Task;

import static helpers.Interfaces.*;
import static dAgility.dAgility.Obstacle;
import static dAgility.dAgility.proceedWithTraversal;
import static dAgility.dAgility.obstacles;

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
        Tile currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    if (obstacle.markHandling.isMarkPresent(obstacle.markHandling.checkArea, obstacle.markHandling.targetColor)) {
                        obstacle.markHandling.pickUpMark(obstacle.markHandling.checkArea, obstacle.instantPressArea);
                    } else {
                        proceedWithTraversal(obstacle, currentLocation);
                    }
                } else {
                    proceedWithTraversal(obstacle, currentLocation);
                }
                return true;
            }
        }
        return false;
    }
}
