package Tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.*;

import java.awt.*;

import static Tasks.GoToAltar.getAltarAreaForRune;
import static Tasks.GoToAltar.targetArea;
import static Tasks.PreGame.INSIDE_AREA;
import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class HandleAltars extends Task {

    private final StateUpdater stateUpdater;
    public HandleAltars(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    @Override
    public boolean activate() {
        return readyToCraftRunes;
    }

    @Override
    public boolean execute() {
        setStatusAndDebugLog("Craft " + runeToMake + " rune");

        if (runeToMake == null) {
            Logger.debugLog("Rune to make is null, cannot execute HandleAltars.");
            return false;
        }

        Logger.debugLog("Executing HandleAltars for rune: " + runeToMake.getName());

        AltarSpawn direction = AltarSpawn.NONE;

        // Special handling for Cosmic rune
        if (runeToMake == RuneInfo.COSMIC) {
            direction = determineCosmicSpawnDirection();
            if (direction == null) {
                Logger.debugLog("Could not determine spawn direction for Cosmic altar. Exiting.");
                return false;
            }
        }

        // Get the rectangle to tap based on the rune and direction
        Rectangle tapRect = getAltarTapRectangleFromPortalForRune(runeToMake, direction);

        if (tapRect == null) {
            Logger.debugLog("No tap rectangle found for rune: " + runeToMake.getName());
            return false;
        }

        // Do the first tap on the altar
        setStatusAndDebugLog("Tap " + runeToMake + " altar");
        Client.tap(tapRect);
        Condition.wait(() -> !Inventory.contains(ItemList.GUARDIAN_ESSENCE_26879, 0.7), 100, 250);

        // Logic when we're using essence here
        if (usePouches) {
            setStatusAndDebugLog("Empty rune pouch");
        }

        // Leave the altar area here
        if (Player.atTile(getAltarTile(runeToMake, direction))) {
            Client.tap(getPortalFromAltarTapRectangle(runeToMake, direction));
        } else if (Player.atTile(getPortalTile(runeToMake, direction))){
            Client.tap(getPortalAtPortalTapRectangle(runeToMake, direction));
        } else {
            if (!Walker.isReachable(getPortalTile(runeToMake, direction))) {
                Walker.webWalk(getPortalTile(runeToMake, direction));
                Player.waitTillNotMoving(13);
            }
            Walker.step(getPortalTile(runeToMake, direction));
            Client.tap(getPortalAtPortalTapRectangle(runeToMake, direction));
        }

        Condition.wait(() -> Player.within(INSIDE_AREA), 100, 250);

        if (Player.within(INSIDE_AREA)) {
            readyToGoToAltar = false;
            readyToCraftRunes = false;
            if (Inventory.contains(ItemList.GUARDIAN_FRAGMENTS_26878, 0.7)) {
                readyToCraftEssences = true;
            }
        }

        return true;
    }

    private Rectangle getAltarTapRectangleAtAltarForRune(RuneInfo rune, AltarSpawn direction) {
        switch (rune) {
            case AIR:
                return AIR_ALTAR_AT_ALTAR_TAP_RECT;
            case FIRE:
                return FIRE_ALTAR_AT_ALTAR_TAP_RECT;
            case MIND:
                return MIND_ALTAR_AT_ALTAR_TAP_RECT;
            case BODY:
                return BODY_ALTAR_AT_ALTAR_TAP_RECT;
            case WATER:
                return WATER_ALTAR_AT_ALTAR_TAP_RECT;
            case COSMIC:
                return getCosmicTapRectangle(direction, true); // Handle direction for Cosmic rune
            case EARTH:
                return EARTH_ALTAR_AT_ALTAR_TAP_RECT;
            default:
                Logger.debugLog("No AT ALTAR rectangle defined for rune: " + rune.getName());
                return null;
        }
    }


    private Rectangle getAltarTapRectangleFromPortalForRune(RuneInfo rune, AltarSpawn direction) {
        switch (rune) {
            case AIR:
                return AIR_ALTAR_FROM_PORTAL_TAP_RECT;
            case FIRE:
                return FIRE_ALTAR_FROM_PORTAL_TAP_RECT;
            case MIND:
                return MIND_ALTAR_FROM_PORTAL_TAP_RECT;
            case BODY:
                return BODY_ALTAR_FROM_PORTAL_TAP_RECT;
            case WATER:
                return WATER_ALTAR_FROM_PORTAL_TAP_RECT;
            case COSMIC:
                return getCosmicTapRectangle(direction, false); // Handle direction for Cosmic rune
            case EARTH:
                return EARTH_ALTAR_FROM_PORTAL_TAP_RECT;
            default:
                Logger.debugLog("No FROM PORTAL rectangle defined for rune: " + rune.getName());
                return null;
        }
    }

    private Rectangle getCosmicTapRectangle(AltarSpawn direction, boolean atPortal) {
        if (atPortal) {
            // At altar rectangles
            switch (direction) {
                case NORTH:
                    return COSMIC_ALTAR_NORTH_TAP_RECT;
                case EAST:
                    return COSMIC_ALTAR_EAST_ATALTAR_TAP_RECT;
                case SOUTH:
                    return COSMIC_ALTAR_SOUTH_TAP_RECT;
                case WEST:
                    return COSMIC_ALTAR_WEST_TAP_RECT;
                default:
                    Logger.debugLog("Invalid direction specified for Cosmic rune: " + direction + ". Defaulting to NORTH.");
                    return COSMIC_ALTAR_NORTH_TAP_RECT;
            }
        } else {
            // From portal rectangles
            switch (direction) {
                case NORTH:
                    Walker.step(COSMIC_NORTH_ALTAR_TILE);
                    return COSMIC_ALTAR_NORTH_TAP_RECT;
                case EAST:
                    return COSMIC_ALTAR_EAST_FROMPORTAL_TAP_RECT;
                case SOUTH:
                    Walker.step(COSMIC_SOUTH_ALTAR_TILE);
                    return COSMIC_ALTAR_SOUTH_TAP_RECT;
                case WEST:
                    Walker.step(COSMIC_WEST_ALTAR_TILE);
                    return COSMIC_ALTAR_WEST_TAP_RECT;
                default:
                    Logger.debugLog("Invalid direction specified for Cosmic rune: " + direction + ". Defaulting to NORTH.");
                    return COSMIC_ALTAR_NORTH_TAP_RECT;
            }
        }
    }

    private AltarSpawn determineCosmicSpawnDirection() {
        Logger.debugLog("Determining Cosmic altar spawn direction...");

        if (Player.isTileWithinArea(currentLocation, cosmicAltarNorth)) {
            Logger.debugLog("Spawned in Cosmic altar NORTH area.");
            return AltarSpawn.NORTH;
        } else if (Player.isTileWithinArea(currentLocation, cosmicAltarEast)) {
            Logger.debugLog("Spawned in Cosmic altar EAST area.");
            return AltarSpawn.EAST;
        } else if (Player.isTileWithinArea(currentLocation, cosmicAltarSouth)) {
            Logger.debugLog("Spawned in Cosmic altar SOUTH area.");
            return AltarSpawn.SOUTH;
        } else if (Player.isTileWithinArea(currentLocation, cosmicAltarWest)) {
            Logger.debugLog("Spawned in Cosmic altar WEST area.");
            return AltarSpawn.WEST;
        } else {
            Logger.debugLog("Could not determine Cosmic altar spawn direction.");
            return null; // Return null if spawn direction cannot be determined
        }
    }

    private Rectangle getPortalFromAltarTapRectangle(RuneInfo rune, AltarSpawn direction) {
        switch (rune) {
            case AIR:
                return AIR_PORTAL_FROMALTAR_TAP_RECT;
            case FIRE:
                return FIRE_PORTAL_FROMALTAR_TAP_RECT;
            case MIND:
                return MIND_PORTAL_FROMALTAR_TAP_RECT;
            case BODY:
                return BODY_PORTAL_FROMALTAR_TAP_RECT;
            case WATER:
                return WATER_PORTAL_FROMALTAR_TAP_RECT;
            case COSMIC:
                return getCosmicPortalTapRectangle(direction, false); // Use direction for Cosmic
            case EARTH:
                return EARTH_PORTAL_FROMALTAR_TAP_RECT;
            default:
                Logger.debugLog("No FROM ALTAR portal rectangle defined for rune: " + rune.getName());
                return null;
        }
    }

    private Rectangle getPortalAtPortalTapRectangle(RuneInfo rune, AltarSpawn direction) {
        switch (rune) {
            case AIR:
                return AIR_PORTAL_ATPORTAL_TAP_RECT;
            case FIRE:
                return FIRE_PORTAL_ATPORTAL_TAP_RECT;
            case MIND:
                return MIND_PORTAL_ATPORTAL_TAP_RECT;
            case BODY:
                return BODY_PORTAL_ATPORTAL_TAP_RECT;
            case WATER:
                return WATER_PORTAL_ATPORTAL_TAP_RECT;
            case COSMIC:
                return getCosmicPortalTapRectangle(direction, true); // Use direction for Cosmic
            case EARTH:
                return EARTH_PORTAL_ATPORTAL_TAP_RECT;
            default:
                Logger.debugLog("No AT PORTAL portal rectangle defined for rune: " + rune.getName());
                return null;
        }
    }

    private Rectangle getCosmicPortalTapRectangle(AltarSpawn direction, boolean atPortal) {
        if (atPortal) {
            switch (direction) {
                case NORTH:
                    return COSMIC_PORTAL_NORTHPORTAL_TAP_RECT;
                case EAST:
                    return COSMIC_PORTAL_EASTPORTAL_TAP_RECT;
                case SOUTH:
                    return COSMIC_PORTAL_SOUTHPORTAL_TAP_RECT;
                case WEST:
                    return COSMIC_PORTAL_WESTPORTAL_ATPORTAL_TAP_RECT;
                default:
                    Logger.debugLog("Invalid direction for Cosmic portal at portal: " + direction + ". Defaulting to NORTH.");
                    return COSMIC_PORTAL_NORTHPORTAL_TAP_RECT;
            }
        } else {
            switch (direction) {
                case NORTH:
                    Walker.step(COSMIC_ALTAR_LEAVE_NORTH_PORTAL_TILE);
                    return COSMIC_PORTAL_NORTHPORTAL_TAP_RECT;
                case EAST:
                    Walker.step(COSMIC_ALTAR_LEAVE_EAST_PORTAL_TILE);
                    return COSMIC_PORTAL_EASTPORTAL_TAP_RECT;
                case SOUTH:
                    Walker.step(COSMIC_ALTAR_LEAVE_SOUTH_PORTAL_TILE);
                    return COSMIC_PORTAL_SOUTHPORTAL_TAP_RECT;
                case WEST:
                    return COSMIC_PORTAL_WESTPORTAL_FROMALTAR_TAP_RECT;
                default:
                    Logger.debugLog("Invalid direction for Cosmic portal from altar: " + direction + ". Defaulting to NORTH.");
                    return COSMIC_PORTAL_NORTHPORTAL_TAP_RECT;
            }
        }
    }

    private Tile getAltarTile(RuneInfo rune, AltarSpawn direction) {
        switch (rune) {
            case AIR:
                return AIR_ALTAR_TILE;
            case FIRE:
                return FIRE_ALTAR_TILE;
            case MIND:
                return MIND_ALTAR_TILE;
            case BODY:
                return BODY_ALTAR_TILE;
            case WATER:
                return WATER_ALTAR_TILE;
            case COSMIC:
                switch (direction) {
                    case NORTH:
                        return COSMIC_NORTH_ALTAR_TILE;
                    case EAST:
                        return COSMIC_EAST_ALTAR_TILE;
                    case SOUTH:
                        return COSMIC_SOUTH_ALTAR_TILE;
                    case WEST:
                        return COSMIC_WEST_ALTAR_TILE;
                    default:
                        Logger.debugLog("Invalid direction for Cosmic altar: " + direction + ". Defaulting to NORTH.");
                        return COSMIC_NORTH_ALTAR_TILE;
                }
            case EARTH:
                return EARTH_ALTAR_TILE;
            default:
                Logger.debugLog("No altar tile defined for rune: " + rune.getName());
                return null;
        }
    }

    private Tile getPortalTile(RuneInfo rune, AltarSpawn direction) {
        switch (rune) {
            case AIR:
                return AIR_ALTAR_LEAVE_PORTAL_TILE;
            case FIRE:
                return FIRE_ALTAR_LEAVE_PORTAL_TILE;
            case MIND:
                return MIND_ALTAR_LEAVE_PORTAL_TILE;
            case BODY:
                return BODY_ALTAR_LEAVE_PORTAL_TILE;
            case WATER:
                return WATER_ALTAR_LEAVE_PORTAL_TILE;
            case COSMIC:
                switch (direction) {
                    case NORTH:
                        return COSMIC_ALTAR_LEAVE_NORTH_PORTAL_TILE;
                    case EAST:
                        return COSMIC_ALTAR_LEAVE_EAST_PORTAL_TILE;
                    case SOUTH:
                        return COSMIC_ALTAR_LEAVE_SOUTH_PORTAL_TILE;
                    case WEST:
                        return COSMIC_ALTAR_LEAVE_WEST_PORTAL_TILE;
                    default:
                        Logger.debugLog("Invalid direction for Cosmic portal tile: " + direction + ". Defaulting to NORTH.");
                        return COSMIC_ALTAR_LEAVE_NORTH_PORTAL_TILE;
                }
            case EARTH:
                return EARTH_ALTAR_LEAVE_PORTAL_TILE;
            default:
                Logger.debugLog("No portal tile defined for rune: " + rune.getName());
                return null;
        }
    }
}
