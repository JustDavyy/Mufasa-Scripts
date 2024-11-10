package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dWinemaker.*;

public class DoWines extends Task {

    @Override
    public boolean activate() {
        return Inventory.contains(JUG_OF_WATER, 0.75) && Inventory.contains(GRAPES, 0.75);
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Start making wine");
        Logger.log("Making wines");

        // Start the initial wine-making process
        makeWine();

        // Set the timeout for a full inventory (25 seconds)
        long startTime = System.currentTimeMillis();
        long timeout = 25 * 1000;

        Paint.setStatus("Waiting for inventory to finish");
        while (Inventory.contains(GRAPES, 0.75) && !Script.isScriptStopping() && !Script.isTimeForBreak()) {
            Condition.sleep(generateRandomDelay(1000, 1500)); // Sleep between actions
            hopActions();

            // Restart wine-making if player leveled up
            if (Player.leveledUp()) {
                makeWine();
            }

            // Check for timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                Logger.debugLog("Timeout reached for wine-making process");
                break;
            }
        }

        // Update XP and statistics
        XpBar.getXP();
        PROCESS_COUNT += 14;
        INVENT_COUNT += 1;
        Paint.updateBox(productIndex, PROCESS_COUNT);
        Paint.setStatistic("Inventories completed: " + INVENT_COUNT);

        Logger.debugLog("Ending the executeMakeWineMethod() method.");

        return false;
    }

    private void makeWine() {
        int initialDelay = generateRandomDelay(100, 250);

        Inventory.tapItem(JUG_OF_WATER, 0.75);
        Condition.sleep(initialDelay);
        Inventory.tapItem(GRAPES, 0.75);

        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(Chatbox::isMakeMenuVisible, 100, 20);
        Chatbox.makeOption(1);
        Logger.debugLog("Selected option 1 in chatbox.");
    }
}