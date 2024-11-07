package dAgility.Tasks;

import dAgility.dAgility;
import dAgility.utils.Task;
import helpers.utils.Area;
import helpers.utils.Tile;

import static dAgility.dAgility.*;
import static helpers.Interfaces.*;

public class Seers extends Task {

    Tile startTile = new Tile(10915, 13701, 0);
    Tile obs1EndTile = new Tile(10915, 13713, 3);
    Area seersArea = new Area(new Tile(10692, 13450, 0), new Tile(11020, 13808, 0));
    Area obstacle6EndArea = new Area(new Tile(10791, 13557, 0), new Tile(10854, 13636, 0));
    Area teleportArea = new Area(new Tile(10892, 13669, 0), new Tile(10924, 13700, 0));
    Tile[] pathToStart = new Tile[] {
            new Tile(10838, 13593, 0),
            new Tile(10860, 13599, 0),
            new Tile(10875, 13616, 0),
            new Tile(10886, 13634, 0),
            new Tile(10897, 13655, 0),
            new Tile(10903, 13672, 0),
            new Tile(10911, 13687, 0)
    };

    public Seers(){
        super();
        super.name = "Seers";
    }
    @Override
    public boolean activate() {
        return (dAgility.courseChosen.equals("Seers") || dAgility.courseChosen.equals("Seers - teleport"));
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        // Block that assumes we are at the end of the last obstacle
        if (Player.isTileWithinArea(currentLocation, obstacle6EndArea)) {
            if (useSeersTeleport) {
                Logger.debugLog("Teleporting back to the start obstacle");
                Paint.setStatus("Teleport to start obstacle");
                Magic.tapCamelotTeleportSpell();
                Condition.wait(() -> Player.within(teleportArea), 150, 50);
            } else {
                Logger.debugLog("Walking back to the start obstacle");
                Paint.setStatus("Walk to start obstacle");
                Walker.walkPath(pathToStart);
                Player.waitTillNotMoving(17);
            }
        }

        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);
        // Handle most of the start tiles without using color finder for speed
        for (dAgility.startTileStorage tileTap : startTiles) {
            if (Player.tileEquals(currentLocation, tileTap.getTile())) {
                Logger.debugLog("Player is on known start tile: " + tileTap.getTile());
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
                    proceedWithTraversal(obstacle, currentLocation);
                    if (obstacle.name.equals("Obstacle 6")) {
                        if (useSeersTeleport) {
                            GameTabs.openMagicTab();
                        }
                        lapCount++;
                    }
                }

                return true;
            }
        }

        // Block that assumes we are not within any of those areas, which means we've fallen or wandered off somewhere?
        if (Player.isTileWithinArea(currentLocation, seersArea)) {
            Logger.debugLog("Not within any obstacle area, webwalking back to start obstacle");
            Paint.setStatus("Recover after fall/failure");
            Walker.webWalk(startTile);
            Player.waitTillNotMoving(17);
            return true;
        }
        return false;
    }
}
