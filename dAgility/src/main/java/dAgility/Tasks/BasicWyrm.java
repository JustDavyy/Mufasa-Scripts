package dAgility.Tasks;

import dAgility.dAgility;
import dAgility.utils.Task;
import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;

import static dAgility.dAgility.*;
import static helpers.Interfaces.*;

public class BasicWyrm extends Task {

    public BasicWyrm(){
        super();
        super.name = "BasicWyrm";
    }
    @Override
    public boolean activate() {
        return (dAgility.courseChosen.equals("Basic Colossal Wyrm"));
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
                            mark.pickUpMark(mark.checkArea, mark.tapArea, mark.endTile, mark.failArea, mark.checkForFail);
                            markHandled = true;
                            break;
                        }
                    }
                }

                if (!markHandled) {
                    Paint.setStatus("Traverse obstacle " + obstacle.name);
                    proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 6")) {
                        lapCount++;
                        termiteCount = Inventory.stackSize(30038) - initialTermiteCount;
                        Paint.updateBox(termiteIndex, termiteCount);
                        boneShardCount = Inventory.stackSize(ItemList.BLESSED_BONE_SHARDS_29381) - initialBoneShardCount;
                        Paint.updateBox(shardIndex, boneShardCount);
                    }
                }

                return true;
            }
        }
        return false;
    }
}
