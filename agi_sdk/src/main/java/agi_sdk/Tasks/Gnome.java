package agi_sdk.Tasks;

import agi_sdk.main;
import agi_sdk.helpers.MarkHandling;
import agi_sdk.helpers.Obstacle;
import agi_sdk.helpers.TraverseHelpers;
import agi_sdk.utils.Task;

import static agi_sdk.main.*;
import static helpers.Interfaces.*;

public class Gnome extends Task {

    @Override
    public boolean activate() {
        return (main.courseChosen.equals("Gnome"));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                boolean markHandled = false;

                // Check for marks if applicable
                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    for (MarkHandling mark : obstacle.markHandling) {
                        if (mark.isMarkPresent(mark.checkArea, mark.targetColor)) {
                            Paint.setStatus("Pick up mark of grace");
                            Logger.log("Mark of grace detected, picking it up!");
                            mark.pickUpMark(mark.checkArea, mark.tapArea, mark.endTile, mark.failArea, mark.checkForFail);
                            markHandled = true;
                            break; // Exit loop after handling the first detected mark
                        }
                    }
                }

                // If no marks were handled, proceed with obstacle traversal
                if (!markHandled) {
                    Paint.setStatus("Traverse obstacle " + obstacle.name);
                    TraverseHelpers.proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 7")) {
                        lapCount++;
                    }
                }

                return true; // Obstacle found, exit loop and return
            }
        }

        return false;
    }
}
