package Tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.RuneInfo;
import utils.RuneType;
import utils.StateUpdater;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class GoToAltar extends Task {
    private final StateUpdater stateUpdater;
    public GoToAltar(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    public static Area targetArea;

    // RUNE INFO


    @Override
    public boolean activate() {
        return readyToGoToAltar;
    }

    @Override
    public boolean execute() {

        setStatusAndDebugLog("Go to altar");

        // Check the point distribution to see what altar we should do
        elementalPoints = Chatbox.readDigitsInArea(ELEMENTAL_POINTS_RECT, blackColor);
        catalyticPoints = Chatbox.readDigitsInArea(CATALYTIC_POINTS_RECT, blackColor);

        // Decide which rune to make based on point distribution and which runes can be crafted by the user
        runeToMake = decideWhichRuneToMake();

        if (runeToMake == null) {
            Logger.debugLog("No rune can be crafted at this time. Exiting execution.");
            return false; // No rune to make, exit
        }

        if (!Inventory.contains(ItemList.GUARDIAN_ESSENCE_26879, 0.8)) {
            readyToGoToAltar = false;
            return false;
        }

        // Get the guardian tile for the chosen rune
        Tile guardianTile = getGuardianTileForRune(runeToMake);

        if (guardianTile == null) {
            Logger.debugLog("No guardian tile found for rune: " + runeToMake.getName() + ". Exiting execution.");
            return false; // Fallback in case of invalid mapping
        }

        Logger.debugLog("Stepping to guardian tile: " + guardianTile + " for rune: " + runeToMake.getName());
        Walker.step(guardianTile);

        // Retry stepping to the tile as long as we have time
        int timeTillSwitch = stateUpdater.timeTillRuneSwitch();
        while (!Player.atTile(guardianTile) && timeTillSwitch > 5) {
            Walker.step(guardianTile);
            Condition.sleep(1000); // Wait for 1 second before retrying
            timeTillSwitch = stateUpdater.timeTillRuneSwitch();
            Logger.debugLog("Retrying step to guardian tile. Time till switch: " + timeTillSwitch + " ms");
        }

        // Check if we successfully reached the guardian tile
        if (Player.atTile(guardianTile)) {
            Logger.debugLog("Reached guardian tile. Tapping guardian rectangle for rune: " + runeToMake.getName());
            // Get the altar area based on the rune type
            targetArea = getAltarAreaForRune(runeToMake);
            tapGuardianRectangle(runeToMake);

            Logger.debugLog("RUNE TO MAKE: " + runeToMake);
            Logger.debugLog("TARGET AREA: " + targetArea + " | " + runeToMake);

            if (targetArea == null) {
                Logger.debugLog("No area defined for rune: " + runeToMake.getName() + ". Exiting execution.");
                return false; // Fallback in case of an undefined area
            }

            Logger.debugLog("Waiting until we are within the altar area");
            Condition.sleep(2800, 3200);
            Condition.wait(() -> Player.within(targetArea) || Chatbox.isMakeMenuVisible(), 100, 100);
            if (Player.within(targetArea)) {
                Logger.debugLog("Player is within the altar target area");
                readyToCraftRunes = true;
                return false;
            } else if (Chatbox.isMakeMenuVisible()) {
                setStatusAndDebugLog("Dismiss popup");
                Client.sendKeystroke("space");
                Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 35);
                return false;
            } else {
                Logger.debugLog("Could not detect us in the altar area for " + runeToMake);
                return false;
            }
        } else {
            Logger.debugLog("Failed to reach guardian tile in time. Exiting execution.");
            return false; // Could not reach the tile in time
        }
    }

    private RuneInfo decideWhichRuneToMake() {
        Logger.debugLog("Starting decision-making for which rune to make...");

        // Retrieve active runes
        RuneInfo elementalRune = stateUpdater.getElementalRune();
        RuneInfo catalyticRune = stateUpdater.getCatalyticRune();

        int timeTillSwitch = stateUpdater.timeTillRuneSwitch();

        Logger.debugLog("Active runes: Elemental: " + elementalRune.getName() + ", Catalytic: " + catalyticRune.getName());
        Logger.debugLog("Time till rune switch: " + timeTillSwitch + " ms");

        // If there's less than 8 seconds, wait for the next cycle
        if (timeTillSwitch < 8) {
            Logger.debugLog("Less than 8 seconds remaining, waiting for next cycle...");
            Condition.sleep((timeTillSwitch * 1000) + 1500);
            return null;
        }

        RuneInfo chosenRune = null;

        setStatusAndDebugLog("Compare points");
        // Compare points
        if (elementalPoints < catalyticPoints) {
            Logger.debugLog("Elemental points are lower. Preference: ELEMENTAL.");
            chosenRune = getBestRune(elementalRune, RuneType.ELEMENTAL);
        } else if (catalyticPoints < elementalPoints) {
            Logger.debugLog("Catalytic points are lower. Preference: CATALYTIC.");
            chosenRune = getBestRune(catalyticRune, RuneType.CATALYTIC);
        } else {
            Logger.debugLog("Points are equal or within 20. Preference: HIGHEST TIER.");
            if (Math.abs(elementalPoints - catalyticPoints) <= 20) {
                chosenRune = getBestRuneByTier(elementalRune, catalyticRune);
            } else {
                chosenRune = elementalPoints < catalyticPoints
                        ? getBestRune(elementalRune, RuneType.ELEMENTAL)
                        : getBestRune(catalyticRune, RuneType.CATALYTIC);
            }
        }

        if (chosenRune == null) {
            Logger.debugLog("No valid rune found based on level or configuration.");
            return null;
        }

        Logger.debugLog("Chosen rune: " + chosenRune.getName());
        return chosenRune;
    }

    private RuneInfo getBestRune(RuneInfo activeRune, RuneType runeType) {
        setStatusAndDebugLog("Find best rune");

        // Skip level checks for elemental runes
        if (runeType == RuneType.ELEMENTAL) {
            Logger.debugLog("Rune " + activeRune.getName() + " is an elemental rune and always valid.");
            return activeRune;
        }

        // Validate catalytic runes
        if (activeRune == null || runecraftingLevel < activeRune.getRequiredLevel()) {
            Logger.debugLog("Cannot craft rune: " + (activeRune == null ? "None active" : activeRune.getName()) +
                    " due to insufficient level.");
            return null;
        }

        // Additional restrictions for catalytic runes
        switch (activeRune) {
            case COSMIC:
                if (!doCosmics) return null;
                break;
            case LAW:
                if (!doLaws) return null;
                break;
            case DEATH:
                if (!doDeaths) return null;
                break;
            case BLOOD:
                if (!doBloods) return null;
                break;
        }

        Logger.debugLog("Rune " + activeRune.getName() + " can be crafted.");
        return activeRune;
    }

    private RuneInfo getBestRuneByTier(RuneInfo elementalRune, RuneInfo catalyticRune) {
        setStatusAndDebugLog("Determine highest tier");

        // No level checks for elemental runes
        Logger.debugLog("Elemental rune does not require level validation: " +
                (elementalRune != null ? elementalRune.getName() : "None"));

        // Validate catalytic rune
        if (catalyticRune != null) {
            boolean validCatalytic = true;
            switch (catalyticRune) {
                case COSMIC:
                    validCatalytic = doCosmics && runecraftingLevel >= catalyticRune.getRequiredLevel();
                    break;
                case LAW:
                    validCatalytic = doLaws && runecraftingLevel >= catalyticRune.getRequiredLevel();
                    break;
                case DEATH:
                    validCatalytic = doDeaths && runecraftingLevel >= catalyticRune.getRequiredLevel();
                    break;
                case BLOOD:
                    validCatalytic = doBloods && runecraftingLevel >= catalyticRune.getRequiredLevel();
                    break;
                default:
                    validCatalytic = runecraftingLevel >= catalyticRune.getRequiredLevel();
                    break;
            }
            if (!validCatalytic) {
                Logger.debugLog("Cannot craft catalytic rune: " + catalyticRune.getName() + " due to insufficient level or boolean restriction.");
                catalyticRune = null;
            }
        }

        // Assign tier priorities
        int elementalTier = getTier(elementalRune);
        int catalyticTier = getTier(catalyticRune);

        Logger.debugLog("Validated tiers -> Elemental: " + elementalTier + ", Catalytic: " + catalyticTier);

        // Compare tiers
        if (elementalRune != null && catalyticRune != null) {
            return (elementalTier >= catalyticTier) ? elementalRune : catalyticRune;
        }

        // Return the valid rune, if any
        return (elementalRune != null) ? elementalRune : catalyticRune;
    }

    private int getTier(RuneInfo rune) {
        if (rune == null) return 0;

        setStatusAndDebugLog("Check rune tier");

        switch (rune) {
            case AIR:
            case BODY:
            case MIND:
                return 1; // Weak tier
            case WATER:
            case COSMIC:
            case CHAOS:
                return 2; // Medium tier
            case EARTH:
            case NATURE:
            case LAW:
                return 3; // Strong tier
            case FIRE:
            case DEATH:
            case BLOOD:
                return 4; // Overcharged tier
            default:
                return 0;
        }
    }

    private void tapGuardianRectangle(RuneInfo rune) {
        setStatusAndDebugLog("Tap guardian");

        Rectangle tapRect = getGuardianTapRectangleForRune(rune);

        if (tapRect == null) {
            Logger.debugLog("No rectangle found for rune: " + rune.getName());
            return;
        }

        Client.tap(tapRect);
    }

    private Tile getGuardianTileForRune(RuneInfo rune) {
        setStatusAndDebugLog("Get guardian tile");

        switch (rune) {
            case AIR:
                return AIR_GUARDIAN_TILE;
            case BODY:
                return BODY_GUARDIAN_TILE;
            case MIND:
                return MIND_GUARDIAN_TILE;
            case WATER:
                return WATER_GUARDIAN_TILE;
            case COSMIC:
                return COSMIC_GUARDIAN_TILE;
            case CHAOS:
                return CHAOS_GUARDIAN_TILE;
            case EARTH:
                return EARTH_GUARDIAN_TILE;
            case NATURE:
                return NATURE_GUARDIAN_TILE;
            case LAW:
                return LAW_GUARDIAN_TILE;
            case FIRE:
                return FIRE_GUARDIAN_TILE;
            case DEATH:
                return DEATH_GUARDIAN_TILE;
            case BLOOD:
                return BLOOD_GUARDIAN_TILE;
            default:
                Logger.debugLog("Unknown rune: " + rune.getName());
                return null;
        }
    }

    private Rectangle getGuardianTapRectangleForRune(RuneInfo rune) {
        switch (rune) {
            case AIR:
                return AIR_GUARDIAN_TAP_RECT;
            case BODY:
                return BODY_GUARDIAN_TAP_RECT;
            case MIND:
                return MIND_GUARDIAN_TAP_RECT;
            case WATER:
                return WATER_GUARDIAN_TAP_RECT;
            case COSMIC:
                return COSMIC_GUARDIAN_TAP_RECT;
            case CHAOS:
                return CHAOS_GUARDIAN_TAP_RECT;
            case EARTH:
                return EARTH_GUARDIAN_TAP_RECT;
            case NATURE:
                return NATURE_GUARDIAN_TAP_RECT;
            case LAW:
                return LAW_GUARDIAN_TAP_RECT;
            case FIRE:
                return FIRE_GUARDIAN_TAP_RECT;
            case DEATH:
                return DEATH_GUARDIAN_TAP_RECT;
            case BLOOD:
                return BLOOD_GUARDIAN_TAP_RECT;
            default:
                return null; // Fallback for unknown runes
        }
    }

    public static Area getAltarAreaForRune(RuneInfo rune) {
        switch (rune) {
            case AIR:
                return airAltarArea;
            case WATER:
                return waterAltarArea;
            case EARTH:
                return earthAltarArea;
            case FIRE:
                return fireAltarArea;
            case MIND:
                return mindAltarArea;
            case BODY:
                return bodyAltarArea;
            case COSMIC:
                return cosmicAltarArea;
            case CHAOS:
                return chaosAltarArea;
            case NATURE:
                return natureAltarArea;
            case LAW:
                return lawAltarArea;
            case DEATH:
                return deathAltarArea;
            case BLOOD:
                return bloodAltarArea;
            default:
                Logger.debugLog("[AltarArea] Unknown rune: " + rune.getName());
                return null; // Return null for unknown runes
        }
    }
}
