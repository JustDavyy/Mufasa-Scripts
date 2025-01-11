package Tasks;

import main.dmGOTR;
import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class BreakManager extends Task {
    private final StateUpdater stateUpdater;

    public BreakManager(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    @Override
    public boolean activate() {
        // Return true if the game is NOT going and it's time to break
        return !stateUpdater.isGameGoing() && Client.isTimeForBreak();
    }

    @Override
    public boolean execute() {

        Logger.debugLog("[BM] It's time for a break, logging out!");
        Logout.logout();

        // I assume that when resuming it instantly goes into a break?
        dmGOTR.setStatusAndDebugLog("Resume breaks");
        Client.resumeBreaks();

        // I assume we return from a break here then and need to postpone again?
        dmGOTR.setStatusAndDebugLog("Postpone breaks");
        Client.postponeBreaks();


        return false;
    }
}
