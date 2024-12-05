package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static helpers.Interfaces.GameTabs;
import static main.dNMZ.*;

public class BreakManager extends Task {

    private int hours;
    private int minutes;
    private int seconds;
    public int breakMinutes;
    public int breakSeconds;
    public int breakMillis;
    private final int maxBreakMinutes = 350;

    @Override
    public boolean activate() {
        return (System.currentTimeMillis() - lastBreakTime >= breakAfterMinutes);
    }

    @Override
    public boolean execute() {

        if (insideNMZ) {
            leaveNMZ();
            // Wait at least 10 seconds to be out of combat.
            Condition.sleep(generateDelay(11000,13000));
            takeBreak();
            breakAfterMinutes = generateDelay(lowerBreak, higherBreak) * 60000; // Reset the break timer in milliseconds

            // Cap the break time at 5 hours and 50 minutes (350 minutes)
            breakAfterMinutes = Math.min(breakAfterMinutes, maxBreakMinutes * 60000);

            // Calculate the next break time in hours, minutes, and seconds
            hours = breakAfterMinutes / 3600000;
            minutes = (breakAfterMinutes % 3600000) / 60000;
            seconds = (breakAfterMinutes % 60000) / 1000;


            // Log how long it will be until the next break
            Logger.log("Next break in: " + hours + " hour(s), " + minutes + " minute(s), and " + seconds + " second(s).");
        } else {
            if (!Logout.isLoggedOut()) {
                Logout.logout();
                Condition.sleep(generateDelay(1500, 3000));
            }

            if (Logout.isLoggedOut()) {
                takeBreak();
                breakAfterMinutes = generateDelay(lowerBreak, higherBreak) * 60000; // Reset the break timer in milliseconds

                // Cap the break time at 5 hours and 50 minutes (350 minutes)
                breakAfterMinutes = Math.min(breakAfterMinutes, maxBreakMinutes * 60000);

                // Calculate the next break time in hours, minutes, and seconds
                hours = breakAfterMinutes / 3600000;
                minutes = (breakAfterMinutes % 3600000) / 60000;
                seconds = (breakAfterMinutes % 60000) / 1000;


                // Log how long it will be until the next break
                Logger.log("Next break in: " + hours + " hour(s), " + minutes + " minute(s), and " + seconds + " second(s).");
            } else {
                Logger.debugLog("Logout procedure seemed to have failed? We are still logged in...");
            }
        }

        return true;
    }

    private void takeBreak() {
        // Generate a random break duration (1 to 6 minutes) and randomize seconds (0 to 59)
        breakMinutes = generateDelay(1, 6);
        breakSeconds = generateDelay(0, 59);
        breakMillis = (breakMinutes * 60 + breakSeconds) * 1000; // Convert to milliseconds

        // Log the break duration
        Logger.log(String.format("Taking a break for %d minute(s) and %d second(s).", breakMinutes, breakSeconds));

        // Perform logout and wait for the break duration
        Logger.debugLog("Logging out");
        Logout.logout();
        Condition.sleep(breakMillis);

        Logger.log("Break over, resuming script.");

        // Perform optional actions after the break
        if (hopEnabled) {
            hopActions();
        }
        Logger.debugLog("Logging back in");
        Login.login();

        // Ensure the inventory tab is open and chatbox is closed
        if (!GameTabs.isInventoryTabOpen()) {
            Logger.debugLog("Open inventory tab");
            GameTabs.openInventoryTab();
        }
        Logger.debugLog("Closing chatbox");
        Chatbox.closeChatbox();

        // Update the last break time
        Logger.debugLog("Reset the break timer");
        lastBreakTime = System.currentTimeMillis();

        insideNMZ = false;
    }

    private void hopActions() {
        Logger.debugLog("Switching to a different world.");
        Game.switchWorld(hopProfile);
        Logger.log("Switched to a different world.");
    }

}