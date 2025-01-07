package utils;

import static helpers.Interfaces.Walker;
import static main.dmGOTR.currentLocation;
import static main.dmGOTR.usePouches;

public class StateUpdater {
    boolean gameGoing;
    boolean shouldDepositRunes;
    boolean shouldRepairPouches;

    public void updateAllStates() {
        currentLocation = Walker.getPlayerPosition();
        updateGameGoing();
        updateShouldRepairPouches();
    }

    public void resetAllStates() {
        gameGoing = false;
        shouldDepositRunes = false;
        shouldRepairPouches = false;
    }

    //Update each state
    public void updateGameGoing() {
        // CF Method I'm guessing to check if game is going.
    }

    public void updateShouldRepairPouches() {
        // Checker to see if our pouches are destroyed? probably need to cache the pouch inventory locations and check for color.
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

    //Getters for each state
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
}
