package tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dGlassblower.*;

public class Process extends Task {

    @Override
    public boolean activate() {
        if (prepareScriptStop) {
            if (System.currentTimeMillis() - lastProcessTime > 15000) {
                Logger.debugLog("Script prepared for stop, and no item processed in 15 seconds!");
                doneBanking = true;
                return false;
            }
        }

        return Inventory.contains(ItemList.MOLTEN_GLASS_1775, 0.75);
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Process task");

        // Check if we have leveled up
        if (Player.leveledUp()) {
            Logger.debugLog("We leveled up, restart processing.");
            Paint.setStatus("Re-process after level up");
            processItems(false);
            lastProcessTime = System.currentTimeMillis(); // Update last process time
            return true;
        }

        if (!initialActiondone) {
            processItems(true);
            lastProcessTime = System.currentTimeMillis(); // Update last process time
            initialActiondone = true;
            return true;
        }

        // Check current used slots
        currentUsedSlots = Inventory.count(targetItem, 0.75);

        // Check if an item has been processed in the last 5 seconds
        if (currentUsedSlots != lastUsedSlots) {
            lastProcessTime = System.currentTimeMillis(); // Update last process time
            lastUsedSlots = currentUsedSlots; // Update last used slots count
        }

        // Check if 5 seconds have passed without processing an item
        if (System.currentTimeMillis() - lastProcessTime > 5000) {
            if (stopScript) {
                doneBanking = true;
                return true;
            }
            Logger.debugLog("No item processed in the last 5 seconds, attempting to re-initiate action");
            processItems(false);
            lastProcessTime = System.currentTimeMillis(); // Update last process time
        } else {
            if (currentUsedSlots != lastUsedSlots) {
                Paint.setStatus("Wait for interrupt or finish");
                Condition.sleep(2000);
            }
            return false;
        }

        return true;
    }

    private void processItems(boolean useCache) {
        Paint.setStatus("Process items");
        Logger.log("Process items");
        // Tap the items with a small random delay in between actions
        Inventory.tapItem(ItemList.GLASSBLOWING_PIPE_1785, useCache, 0.75);
        Condition.sleep(generateDelay(100, 200));
        Inventory.tapItem(ItemList.MOLTEN_GLASS_1775, useCache, 0.75);

        // Wait for the make menu to be visible
        Paint.setStatus("Wait for make menu");
        Condition.wait(Chatbox::isMakeMenuVisible, 100, 40);
        if (Chatbox.isMakeMenuVisible()) {
            Client.sendKeystroke(String.valueOf(makeOption));
        }
    }
}