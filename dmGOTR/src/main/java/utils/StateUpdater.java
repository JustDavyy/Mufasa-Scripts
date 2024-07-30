package utils;

public class StateUpdater {
    boolean gameGoing;
    boolean shouldDepositRunes;

    public void updateAllStates() {
        // Update all states
    }

    public void resetAllStates() {
        gameGoing = false;
        shouldDepositRunes = false;
    }

    //Update each state
    public void updateGameGoing() {
        // CF Method I'm guessing to check if game is going.
    }

    //Set each state individually
    public void setGameGoing(boolean state) {
        gameGoing = state;
    }

    public void setShouldDepositRunes(boolean state) {
        shouldDepositRunes = state;
    }

    //Getters for each state
    public boolean isGamineGoing() {
        return gameGoing;
    }

    public boolean shouldDepositRunes() {
        return shouldDepositRunes;
    }
}
