package utils;

import helpers.utils.Area;
import helpers.utils.RegionBox;
import helpers.utils.Tile;

import java.awt.*;

public class Constants {
    public static boolean gameNearingEnd;
    public static RegionBox WTRegion = new RegionBox("WTRegion", 1701, 264, 2157, 846);
    public static Area lobby = new Area(new Tile(632, 173), new Tile(644, 184));

    // <--
    public static Tile leftBranchTile = new Tile(627,170);
    public static Rectangle leftBranchClickRect = new Rectangle(360, 253, 25, 31);
    public static Tile leftBurnTile = new Tile(627,159);

    // -->
    public static Tile rightBranchTile = new Tile(648,170);
    public static Rectangle rightBranchClickRect = new Rectangle(501, 245, 40, 33);
    public static Tile rightBurnTile = new Tile(648,159);

    // Paths
    public static Tile[] wtDoorToBank = new Tile[] {
            new Tile(637, 204),
            new Tile(639, 217),
            new Tile(645, 228),
            new Tile(650, 228)
    };
    public static Tile[] gameToWTDoor = new Tile[] {
            new Tile(638, 167),
            new Tile(637, 175),
            new Tile(637, 185),
            new Tile(637, 195)
    };

    public static Tile[] wtDoorToRightSide = new Tile[] {
            new Tile(638, 185),
            new Tile(639, 175),
            new Tile(650, 165)
    };
    public static Tile[] wtDoorToLeftSide = new Tile[] {
            new Tile(637, 186),
            new Tile(637, 173),
            new Tile(625, 165)
    };
    public static Tile[] LowerRightToLeft = new Tile[] {
            new Tile(648, 165),
            new Tile(638, 165),
            new Tile(626, 165)
    };
    public static Tile[] fromEitherSideToGameLobby = new Tile[] {
            new Tile(637, 166),
            new Tile(637, 177)
    };

    // Just leaving these down here so we can figure out where they belong
    public Tile[] getReversedTiles(Tile[] array) {
        if (array == null) return null;
        Tile[] reversed = new Tile[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }
}
