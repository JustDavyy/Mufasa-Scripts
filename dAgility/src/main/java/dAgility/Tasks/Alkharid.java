package dAgility.Tasks;

import dAgility.dAgility;
import dAgility.utils.Task;
import helpers.utils.Area;
import helpers.utils.Tile;

import static dAgility.dAgility.*;
import static helpers.Interfaces.*;

public class Alkharid extends Task {

    Tile startTile = new Tile(13091, 12533, 0);
    Tile obs1EndTile = new Tile(13091, 12517, 3);
    Area alkharidArea = new Area(new Tile(13041, 12317, 0), new Tile(13324, 12606, 0));
    Area obstacle8EndArea = new Area(new Tile(13179, 12499, 0), new Tile(13213, 12534, 0));
    Tile[] pathToStart = new Tile[] {
            new Tile(13183, 12523, 0),
            new Tile(13160, 12522, 0),
            new Tile(13139, 12531, 0),
            new Tile(13117, 12532, 0),
            new Tile(13100, 12537, 0)
    };

    public Alkharid(){
        super();
        super.name = "Al Kharid";
    }
    @Override
    public boolean activate() {
        return (dAgility.courseChosen.equals("Al Kharid"));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        // Block that assumes we are at the end of the last obstacle
        if (Player.isTileWithinArea(currentLocation, obstacle8EndArea)) {
            Logger.debugLog("Walking back to the start obstacle");
            Paint.setStatus("Walk to start obstacle");
            Walker.walkPath(pathToStart);
            Player.waitTillNotMoving(20);
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
                    if (obstacle.name.equals("Obstacle 8")) {
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, alkharidArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            return true;
        }
        return false;
    }
}
