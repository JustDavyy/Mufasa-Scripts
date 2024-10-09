package dAgility.Tasks;

import dAgility.dAgility;
import dAgility.utils.Task;
import helpers.utils.Area;
import helpers.utils.Tile;

import static dAgility.dAgility.*;
import static helpers.Interfaces.*;

public class Pollnivneach extends Task {

    Tile startTile = new Tile(13403, 11593, 0);
    Tile obs1EndTile = new Tile(13403, 11605, 1 );
    Area pollyArea = new Area(new Tile(13310, 11500, 0), new Tile(13552, 11794, 0));
    Area obstacle9EndArea = new Area(new Tile(13441, 11717, 0), new Tile(13477, 11752, 0));
    Tile[] pathToStart = new Tile[] {
            new Tile(13449, 11727, 0),
            new Tile(13450, 11692, 0),
            new Tile(13444, 11664, 0),
            new Tile(13433, 11637, 0),
            new Tile(13426, 11613, 0),
            new Tile(13411, 11601, 0)
    };

    public Pollnivneach(){
        super();
        super.name = "Pollnivneach";
    }
    @Override
    public boolean activate() {
        return (dAgility.courseChosen.equals("Pollnivneach"));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        // Block that assumes we are at the end of the last obstacle
        if (Player.isTileWithinArea(currentLocation, obstacle9EndArea)) {
            Logger.debugLog("Walking back to the start obstacle");
            Paint.setStatus("Walk to start obstacle");
            Walker.walkPath(pathToStart);
            Player.waitTillNotMoving(18);
        }

        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);
        // Handle most of the start tiles without using color finder for speed
        for (dAgility.startTileStorage tileTap : startTiles) {
            if (Player.tileEquals(currentLocation, tileTap.getTile())) {
                Logger.debugLog("Player is on tile: " + tileTap.getTile());
                Paint.setStatus("Tap start obstacle");
                Client.tap(tileTap.getTapRectangle());
                Condition.wait(() -> Player.atTile(obs1EndTile), 200, 40);
                currentLocation = Walker.getPlayerPosition();
                break;
            }
        }

        for (Obstacle obstacle : obstacles) {
            if (Player.isTileWithinArea(currentLocation, obstacle.area)) {
                boolean markHandled = false;

                if (obstacle.checkForMark && obstacle.markHandling != null) {
                    for (MarkHandling mark : obstacle.markHandling) {
                        if (mark.isMarkPresent(mark.checkArea, mark.targetColor)) {
                            if (obstacle.name.equals("Obstacle 8")) {
                                Condition.sleep(generateRandomDelay(850, 1100));
                            }
                            Paint.setStatus("Pick up mark of grace");
                            Logger.log("Mark of grace detected, picking it up!");
                            mark.pickUpMark(mark.checkArea, mark.tapArea, mark.endTile);
                            markHandled = true;
                            break;
                        }
                    }
                }

                if (!markHandled) {
                    if (obstacle.name.equals("Obstacle 8")) {
                        Condition.sleep(generateRandomDelay(850, 1100));
                    }
                    Paint.setStatus("Traverse obstacle " + obstacle.name);
                    proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 9")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, pollyArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            return true;
        }
        return false;
    }
}
