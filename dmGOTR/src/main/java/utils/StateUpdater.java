package utils;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;
import static utils.FontGOTR.digitsTimersAndPortal;
import static utils.FontGOTR.lettersPortalLocation;

public class StateUpdater {
    boolean gameGoing;
    boolean shouldDepositRunes;
    boolean shouldRepairPouches;
    RuneInfo elementalRune = RuneInfo.NOELEMENTAL;
    RuneInfo catalyticRune = RuneInfo.NOCATALYTIC;
    PortalLocation portalLocation = PortalLocation.NONE;
    private static int tempPowerHolder;
    private static int tempSwitchTimeHolder;
    private static String tempPortalLocationHolder = "";

    public void updateAllStates() {
        currentLocation = Walker.getPlayerPosition();
        updateGameGoing();
        updateShouldRepairPouches();
        updateElementalRune();
        updateCatalyticRune();
        updateGuardiansPower();
        updatePortalActive();
        updatePortalLocation();
    }

    public void resetAllStates() {
        gameGoing = false;
        shouldDepositRunes = false;
        shouldRepairPouches = false;
        elementalRune = RuneInfo.NOELEMENTAL;
        catalyticRune = RuneInfo.NOCATALYTIC;
        guardiansPower = 0;
        portalActive = false;
        portalTime = 0;
        tempPowerHolder = 0;
        tempSwitchTimeHolder = 0;
        tempPortalLocationHolder = "";
    }

    //Update each state
    public void updateGameGoing() {
        tempPowerHolder = Chatbox.readDigitsInArea(GUARDIAN_POWER_READ_RECT, blackColor);

        if (tempPowerHolder == -1) {
            tempSwitchTimeHolder = timeTillRuneSwitch();

            if (tempSwitchTimeHolder == -1) {
                setGameGoing(false);
            } else {
                setGameGoing(true);
            }
        } else {
            setGameGoing(true);
        }
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
                setElementalRune(rune);
                matchFound = true;
                break; // Exit loop after finding a match
            }
        }

        // Set to NOELEMENTAL if no match was found
        if (!matchFound && getElementalRune() != RuneInfo.NOELEMENTAL) {
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
                    handleLawOrBodyRune();
                } else if (rune == RuneInfo.MIND || rune == RuneInfo.CHAOS) {
                    // Specific block for MIND and CHAOS
                    handleMindOrChaosRune();
                } else {
                    // General handling for other catalytic runes
                    setCatalyticRune(rune);
                }
                matchFound = true;
                break; // Exit loop after finding a match
            }
        }

        // Set to NOCATALYTIC if no match was found
        if (!matchFound && getCatalyticRune() != RuneInfo.NOCATALYTIC) {
            Logger.debugLog("No match found. Setting catalytic rune to NOCATALYTIC.");
            setCatalyticRune(RuneInfo.NOCATALYTIC);
        }
    }

    public void updateGuardiansPower() {
        guardiansPower = Chatbox.readDigitsInArea(GUARDIAN_POWER_READ_RECT, blackColor);
    }

    public void updatePortalActive() {
        portalActive = Client.isColorInRect(Color.decode("#ffbb1a"), PORTAL_CHECK_RECT, 5);

        if (portalActive) {
            updatePortalLocation();
        }
    }

    public void updatePortalLocation() {
        tempPortalLocationHolder = interfaces.readCustomLettersInArea(PORTAL_LOCATION_READ_RECT, whiteColor, lettersPortalLocation);

        switch (tempPortalLocationHolder) {
            case "SW":
                portalLocation = PortalLocation.SOUTHWEST;
                break;
            case "SE":
                portalLocation = PortalLocation.SOUTHEAST;
                break;
            case "E":
                portalLocation = PortalLocation.EAST;
                break;
            case "S":
                portalLocation = PortalLocation.SOUTH;
                break;
            default:
                portalLocation = PortalLocation.NONE;
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

    public int getGuardiansPower() {return guardiansPower;}

    public boolean isPortalActive() {return portalActive;}

    public int getPortalTime() {
        if (portalActive) {
            portalTime = interfaces.readCustomStackSize(PORTAL_READ_RECT, whiteColor, digitsTimersAndPortal);
        } else {
            portalTime = 0;
        }
        return portalTime;
    }

    public PortalLocation getPortalLocation() {return portalLocation;}

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

    public int timeTillRuneSwitch() {
        return interfaces.readCustomStackSize(TIMER_READ_RECT, whiteColor, digitsTimersAndPortal);
    }


    // Specific handling methods for LAW/BODY and MIND/CHAOS
    private void handleLawOrBodyRune() {
        if (Client.getPointsFromColorsInRect(RuneInfo.LAW.getColorAsList(), CATALYTIC_RUNE_RECT, 5).size() > 100) {
            setCatalyticRune(RuneInfo.LAW);
        } else {
            setCatalyticRune(RuneInfo.BODY);
        }
    }

    private void handleMindOrChaosRune() {
        if (Client.isColorInRect(Color.decode("#ffffff"), CATALYTIC_RUNE_RECT, 5)) {
            setCatalyticRune(RuneInfo.CHAOS);
        } else {
            setCatalyticRune(RuneInfo.MIND);
        }
    }
}
