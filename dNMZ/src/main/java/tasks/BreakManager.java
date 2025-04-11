package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dNMZ.*;

public class BreakManager extends Task {

    @Override
    public boolean activate() {
        return Client.isTimeForBreak();
    }

    @Override
    public boolean execute() {

        if (insideNMZ) {
            leaveNMZ();
            // Wait at least 10 seconds to be out of combat.
            Condition.sleep(11000, 13000);
            takeBreak();
        } else {
            if (!Logout.isLoggedOut()) {
                Logout.logout();
                Condition.sleep(1500, 3000);
            }

            if (Logout.isLoggedOut()) {
                takeBreak();
            } else {
                Logger.debugLog("Logout procedure seemed to have failed? We are still logged in...");
            }
        }

        return true;
    }

    private void takeBreak() {

        // Perform logout and wait for the break duration
        Logger.debugLog("Logging out");
        Logout.logout();
        Condition.sleep(1500, 3000);

        // Perform optional actions after the break
        if (hopEnabled) {
            hopActions();
        }

        if (Client.isTimeForSleep()) {
            Logger.log("It is time to sleep!");
            Client.resumeSleeps();
            Client.resumeBreaks();
            insideNMZ = false;
            return;
        }

        if (Client.isTimeForBreak()) {
            Logger.log("It is time to take a break!");
            Client.resumeBreaks();
            insideNMZ = false;
        }
    }

    private void hopActions() {
        Logger.debugLog("Switching to a different world.");
        Game.switchWorld(hopProfile);
        Logger.log("Switched to a different world.");
    }

}