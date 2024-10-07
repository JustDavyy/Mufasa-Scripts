package dAgility.Tasks;

import dAgility.utils.Task;

import static helpers.Interfaces.*;

public class Run extends Task {
    private long lastRunCheckTime; // Last time we checked run percentage
    private int delayInSeconds; // Delay for how long we don't need to check run based on last percentage

    public Run(){
        super();
        super.name = "Run";
    }

    @Override
    public boolean activate() {
        long currentTime = System.currentTimeMillis();
        // Check if it's time for the next run check based on the previously set delay
        if (currentTime - lastRunCheckTime < delayInSeconds * 1000L) {
            return false;
        }

        // Always update the last checked time when the method runs
        lastRunCheckTime = currentTime;

        // Retrieve run energy to calculate delay
        int runEnergy = Player.getRun();
        Logger.debugLog("Run energy: " + runEnergy);

        // Calculate delay based on run energy
        if (runEnergy >= 30) {
            delayInSeconds = (runEnergy - 30) / 10 * 30;
        } else {
            delayInSeconds = 0; // Set to 0 or a minimum default delay if desired
        }

        // Return true to allow running only if running is currently disabled and energy is sufficient
        return !Player.isRunEnabled() && runEnergy >= 30;
    }

    @Override // The code to execute if criteria met
    public boolean execute() {
        Paint.setStatus("Turn on run");
        Logger.debugLog("Turning on run.");
        Player.toggleRun();
        Condition.wait(() -> Player.isRunEnabled(), 100, 100);
        return true;
    }
}