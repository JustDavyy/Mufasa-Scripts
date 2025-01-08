package utils;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class StateUpdater {
    boolean gameGoing;
    boolean shouldDepositRunes;
    boolean shouldRepairPouches;
    RuneInfo elementalRune = RuneInfo.NOELEMENTAL;
    RuneInfo catalyticRune = RuneInfo.NOCATALYTIC;

    public void updateAllStates() {
        currentLocation = Walker.getPlayerPosition();
        updateGameGoing();
        updateShouldRepairPouches();
        updateElementalRune();
        updateCatalyticRune();
    }

    public void resetAllStates() {
        gameGoing = false;
        shouldDepositRunes = false;
        shouldRepairPouches = false;
        elementalRune = RuneInfo.NOELEMENTAL;
        catalyticRune = RuneInfo.NOCATALYTIC;
    }

    //Update each state
    public void updateGameGoing() {
        // CF Method I'm guessing to check if game is going.
    }

    public void updateShouldRepairPouches() {
        // Checker to see if our pouches are destroyed? probably need to cache the pouch inventory locations and check for color.
    }

    public void updateElementalRune() {
        boolean matchFound = false;

        for (RuneInfo rune : RuneInfo.values()) {
            if (rune.getRuneType() != RuneType.ELEMENTAL || rune == RuneInfo.NOELEMENTAL) {
                continue;
            }

            // Check if the rune color is in the rectangle
            if (Client.isColorInRect(rune.getColor(), ELEMENTAL_RUNE_RECT, 5)) {
                Logger.debugLog("Current active elemental rune: " + rune.getName());
                setElementalRune(rune);
                matchFound = true;
                break; // Exit loop after finding a match
            }
        }

        // Set to NOELEMENTAL if no match was found
        if (!matchFound) {
            Logger.debugLog("No match found. Setting elemental rune to NOELEMENTAL.");
            setElementalRune(RuneInfo.NOELEMENTAL);
        }
    }

    public void updateCatalyticRune() {
        boolean matchFound = false;

        for (RuneInfo rune : RuneInfo.values()) {
            // Skip non-CATALYTIC runes and NOCATALYTIC
            if (rune.getRuneType() != RuneType.CATALYTIC || rune == RuneInfo.NOCATALYTIC) {
                continue;
            }

            // Check if the rune color is in the rectangle
            if (Client.isColorInRect(rune.getColor(), CATALYTIC_RUNE_RECT, 5)) {
                if (rune == RuneInfo.LAW || rune == RuneInfo.BODY) {
                    // Specific block for LAW and BODY
                    Logger.debugLog("Current active catalytic rune is LAW or BODY: " + rune.getName());
                    handleLawOrBodyRune(rune);
                } else if (rune == RuneInfo.MIND || rune == RuneInfo.CHAOS) {
                    // Specific block for MIND and CHAOS
                    Logger.debugLog("Current active catalytic rune is MIND or CHAOS: " + rune.getName());
                    handleMindOrChaosRune(rune);
                } else {
                    // General handling for other catalytic runes
                    Logger.debugLog("Current active catalytic rune: " + rune.getName());
                    setCatalyticRune(rune);
                }
                matchFound = true;
                break; // Exit loop after finding a match
            }
        }

        // Set to NOCATALYTIC if no match was found
        if (!matchFound) {
            Logger.debugLog("No match found. Setting catalytic rune to NOCATALYTIC.");
            setCatalyticRune(RuneInfo.NOCATALYTIC);
        }
    }

    //Set each state individually
    public void setGameGoing(boolean state) {
        gameGoing = state;
    }

    public void setShouldDepositRunes(boolean state) {
        shouldDepositRunes = state;
    }

    public void setShouldRepairPouches(boolean state) {
        if (usePouches) {
            shouldRepairPouches = state;
        }
    }

    public void setElementalRune(RuneInfo runeInfo) {
        if (runeInfo.getRuneType() == RuneType.ELEMENTAL) {
            elementalRune = runeInfo;
        } else {
            throw new IllegalArgumentException("Invalid rune type. Expected ELEMENTAL.");
        }
    }

    public void setCatalyticRune(RuneInfo runeInfo) {
        if (runeInfo.getRuneType() == RuneType.CATALYTIC) {
            catalyticRune = runeInfo;
        } else {
            throw new IllegalArgumentException("Invalid rune type. Expected CATALYTIC.");
        }
    }

    //Getters for each state
    public RuneInfo getCatalyticRune() {return catalyticRune;}

    public RuneInfo getElementalRune() {return elementalRune;}

    public boolean isGameGoing() {
        return gameGoing;
    }

    public boolean shouldDepositRunes() {
        return shouldDepositRunes;
    }

    public boolean shouldRepairPouches() {
        if (usePouches) {
            return shouldRepairPouches;
        } else {
            return false;
        }
    }


    // Specific handling methods for LAW/BODY and MIND/CHAOS
    private void handleLawOrBodyRune(RuneInfo rune) {
        if (Client.getPointsFromColorsInRect(RuneInfo.LAW.getColorAsList(), CATALYTIC_RUNE_RECT, 5).size() > 100) {
            setCatalyticRune(RuneInfo.LAW);
        } else {
            setCatalyticRune(RuneInfo.BODY);
        }
    }

    private void handleMindOrChaosRune(RuneInfo rune) {
        if (Client.isColorInRect(Color.decode("#ffffff"), CATALYTIC_RUNE_RECT, 5)) {
            setCatalyticRune(RuneInfo.CHAOS);
        } else {
            setCatalyticRune(RuneInfo.MIND);
        }
    }
}
