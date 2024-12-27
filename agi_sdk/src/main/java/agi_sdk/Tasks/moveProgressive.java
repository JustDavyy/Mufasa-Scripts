package agi_sdk.Tasks;

import agi_sdk.utils.Task;
import helpers.utils.Area;
import helpers.utils.MapChunk;
import helpers.utils.Tile;

import static agi_sdk.agi_sdk.*;
import static helpers.Interfaces.*;

public class moveProgressive extends Task {
    private static final Area draynorObstacle7EndArea = new Area(new Tile(12400, 12765, 0), new Tile(12432, 12846, 0));
    private static final Tile[] pathToVarrock = new Tile[]{
            new Tile(12412, 12795, 0),
            new Tile(12418, 12810, 0),
            new Tile(12413, 12833, 0),
            new Tile(12392, 12836, 0),
            new Tile(12378, 12857, 0),
            new Tile(12367, 12877, 0),
            new Tile(12347, 12880, 0),
            new Tile(12324, 12876, 0),
            new Tile(12312, 12890, 0),
            new Tile(12304, 12915, 0),
            new Tile(12294, 12936, 0),
            new Tile(12291, 12959, 0),
            new Tile(12291, 12980, 0),
            new Tile(12291, 13003, 0),
            new Tile(12290, 13026, 0),
            new Tile(12290, 13045, 0),
            new Tile(12292, 13067, 0),
            new Tile(12288, 13085, 0),
            new Tile(12286, 13110, 0),
            new Tile(12286, 13134, 0),
            new Tile(12281, 13159, 0),
            new Tile(12282, 13177, 0),
            new Tile(12288, 13197, 0),
            new Tile(12283, 13218, 0),
            new Tile(12282, 13240, 0),
            new Tile(12295, 13258, 0),
            new Tile(12305, 13284, 0),
            new Tile(12316, 13295, 0),
            new Tile(12327, 13312, 0),
            new Tile(12347, 13319, 0),
            new Tile(12365, 13331, 0),
            new Tile(12379, 13350, 0),
            new Tile(12389, 13365, 0),
            new Tile(12398, 13383, 0),
            new Tile(12399, 13401, 0),
            new Tile(12403, 13421, 0),
            new Tile(12417, 13428, 0),
            new Tile(12435, 13427, 0),
            new Tile(12450, 13426, 0),
            new Tile(12467, 13424, 0),
            new Tile(12481, 13413, 0),
            new Tile(12497, 13404, 0),
            new Tile(12516, 13405, 0),
            new Tile(12531, 13409, 0),
            new Tile(12550, 13411, 0),
            new Tile(12571, 13411, 0),
            new Tile(12590, 13411, 0),
            new Tile(12610, 13420, 0),
            new Tile(12626, 13432, 0),
            new Tile(12646, 13438, 0),
            new Tile(12663, 13444, 0),
            new Tile(12680, 13456, 0),
            new Tile(12699, 13457, 0),
            new Tile(12717, 13457, 0),
            new Tile(12734, 13461, 0),
            new Tile(12753, 13465, 0),
            new Tile(12768, 13462, 0),
            new Tile(12787, 13459, 0),
            new Tile(12802, 13457, 0),
            new Tile(12818, 13457, 0),
            new Tile(12830, 13448, 0),
            new Tile(12845, 13434, 0),
            new Tile(12857, 13433, 0),
            new Tile(12880, 13433, 0),
            new Tile(12889, 13419, 0),
            new Tile(12895, 13411, 0)
    };
    Area draynorArea = new Area(new Tile(12275, 12663, 0), new Tile(12540, 12981, 0));
    Area draynorTileArea = new Area(new Tile(12310, 12918, 0), new Tile(12352, 12874, 0));
    Tile varrockStartTile = new Tile(12895, 13405, 0);
    Area varrockArea = new Area(new Tile(12731, 13275, 0), new Tile(13101, 13571, 0));

    @Override
    public boolean activate() {
        return needToMove;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos: " + currentLocation.x + ", " + currentLocation.y + ", " + currentLocation.z);

        if (initialCourse.equals("Draynor")) {
            Logger.debugLog("Initial course was Draynor");
            moveToVarrock();
        }

        return false;
    }


    public boolean moveToVarrock() {
        Logger.debugLog("Moving to Varrock course");

        if (Player.isTileWithinArea(currentLocation, draynorObstacle7EndArea)) {
            Logger.debugLog("We're at the end obstacle of the Draynor course");

            Logger.debugLog("Set up walker chunks from Draynor to Varrock");
            MapChunk walkToVarrockChunks = new MapChunk(new String[]{"47-53", "50-50"}, "0");
            Walker.setup(walkToVarrockChunks);
            Condition.sleep(generateRandomDelay(1000, 1500));

            Logger.debugLog("Moving to Varrock using a pre-defined route");
            Walker.walkPath(pathToVarrock);
        } else if (Player.isTileWithinArea(currentLocation, draynorArea)) {
            Logger.debugLog("Looks like we aren't around the end obstacle, but within draynor.");
            Logger.debugLog("Web walk to a pre-defined tile");
            Walker.webWalk(new Tile(12329, 12898, 0));
            Condition.sleep(generateRandomDelay(1500, 2500));

            if (Player.within(draynorTileArea)) {
                Logger.debugLog("Around the pre-defined tile, walking to Varrock.");
                Walker.walkPath(pathToVarrock);
                Condition.sleep(generateRandomDelay(1500, 2500));
                Walker.step(varrockStartTile);
            }
        } else {
            Logger.debugLog("Not around draynor, web walking to varrock.");
            Walker.webWalk(varrockStartTile);
            Condition.sleep(generateRandomDelay(1500, 2500));
            Walker.step(varrockStartTile);
        }

        Logger.debugLog("Check if we are now at the Varrock course area.");
        Paint.setStatus("Check reached destination");
        if (Player.within(varrockArea)) {
            Logger.log("Reached Varrock.");
            Logger.debugLog("Set course to Varrock.");
            changeProgressiveCourse("Varrock");
            Logger.debugLog("Set needToMove state to false.");
            needToMove = false;
            Logger.debugLog("Set up walker chunks to Varrock Course");
            MapChunk varrockChunks = new MapChunk(new String[]{"50-53"}, "0", "1", "3");
            Walker.setup(varrockChunks);
            Condition.sleep(generateRandomDelay(1000, 1500));

            return true;
        } else {
            Logger.log("Failed to reach Varrock.");
            return false;
        }
    }
}
