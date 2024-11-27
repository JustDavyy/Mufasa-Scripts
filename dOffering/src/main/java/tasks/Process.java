package tasks;

import helpers.utils.ItemList;
import helpers.utils.Spells;
import helpers.utils.UITabs;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dOffering.*;

public class Process extends Task {
    private long lastLoggedSecond = -1;

    @Override
    public boolean activate() {
        if (prepareScriptStop) {
            if (System.currentTimeMillis() - lastProcessTime > 15000) {
                Logger.debugLog("Script prepared for stop, and no item processed in 15 seconds!");
                doneBanking = true;
                return false;
            }
        }

        return !doneProcessing;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Process task");

        if (doneProcessing) {
            Paint.setStatus("Done processing spells");
            Logger.log("All iterations completed. No further spells to cast.");
            return false; // Indicating no further actions are needed
        }

        if (GameTabs.isTabOpen(UITabs.INVENTORY)) {
            itemCount = Inventory.count(offerItem, 0.75);
            totalIterations = itemCount / 3;
            Logger.log("Total iterations to cast: " + totalIterations);
        }

        GameTabs.openTab(UITabs.MAGIC);

        if (currentIteration < totalIterations) {
            long timeSinceLastCast = System.currentTimeMillis() - lastCastTime;
            long timeRemaining = 5700 - timeSinceLastCast;

            if (currentIteration == totalIterations - 1 && timeSinceLastCast >= 5700) {
                // Directly process done logic if this is the last iteration
                castSpell();
                currentIteration++;
                doneProcessing = true; // Mark as done immediately
                Paint.setStatus("Done processing");
                Logger.debugLog("Final spell cast. Done processing spells.");
                GameTabs.openTab(UITabs.INVENTORY);
                Condition.sleep(generateDelay(1250, 1800));
            } else if (timeSinceLastCast >= 5700) { // Regular cooldown logic
                castSpell();
                currentIteration++;
                lastCastTime = System.currentTimeMillis(); // Update the last cast time
                Logger.log("Iteration " + currentIteration + " of " + totalIterations + " completed.");
            } else {
                long currentSecond = timeRemaining / 1000; // Calculate the remaining time in seconds
                if (currentSecond != lastLoggedSecond) { // Log only if a new second has passed
                    lastLoggedSecond = currentSecond;
                    Paint.setStatus("Waiting for cooldown");
                    Logger.debugLog("Cooldown in progress. Time remaining: " + timeRemaining + "ms");
                }
            }
        } else {
            // Immediately handle done logic without waiting for cooldown
            doneProcessing = true;
            Paint.setStatus("Done processing");
            Logger.debugLog("Done processing spells. All iterations completed.");
            GameTabs.openTab(UITabs.INVENTORY);
            Condition.sleep(generateDelay(1250, 1800));
        }

        return true;
    }

    private void castSpell() {
        if (product.endsWith("ashes")) {
            Paint.setStatus("Cast Demonic offering");
            Logger.log("Casting Demonic offering");
            Magic.castSpell(Spells.DEMONIC_OFFERING);
        } else if (product.endsWith("bones")) {
            Paint.setStatus("Cast Sinister offering");
            Logger.log("Casting Sinister offering");
            Magic.castSpell(Spells.SINISTER_OFFERING);
        } else {
            Logger.debugLog("Invalid product, stopping script.");
            Logout.logout();
            Script.stop();
        }
    }
}