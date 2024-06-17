package utils;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.RegionBox;
import helpers.utils.Tile;
import static main.dWintertodt.currentSide;

import java.awt.*;

public class Constants {
    //public static final int brumaRoot = ItemList.BRUMA_ROOT_20695;
    //public static final int brumaKindling = ItemList.BRUMA_KINDLING_20696;
    //public static final int knife = 946;
    //public static int foodAmountInInventory;

    //public static boolean gameNearingEnd;
    //public static RegionBox WTRegion = new RegionBox("WTRegion", 1701, 264, 2157, 846);
    //public static Area lobby = new Area(new Tile(632, 173), new Tile(644, 184));

    // <--
    //public static Area leftWTArea = new Area(new Tile(609, 150), new Tile(630, 172));

    // -->
    //public static Area rightWTArea = new Area(new Tile(645, 150), new Tile(669, 176));

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
