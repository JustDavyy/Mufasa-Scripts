package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dWinemaker.*;

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

        return Inventory.contains(ItemList.GRAPES_1987, 0.75) && Inventory.contains(ItemList.JUG_OF_WATER_1937, 0.7);
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
        currentUsedSlots = Inventory.usedSlots();

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
            if (Inventory.usedSlots() > 16) { // As we always use 14 slots for wines, we check if above 16.
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
        if (lastWineRect != null) {
            if (useCache) {
                Client.tap(lastWineRect);
            } else {
                lastWineRect = Inventory.lastItemPosition(ItemList.JUG_OF_WATER_1937, 0.75);
                Client.tap(lastWineRect);
            }
        } else {
            lastWineRect = Inventory.lastItemPosition(ItemList.JUG_OF_WATER_1937, 0.75);
            Client.tap(lastWineRect);
        }
        Condition.sleep(generateDelay(100, 200));
        Inventory.tapItem(ItemList.GRAPES_1987, useCache, 0.75);

        // Wait for the make menu to be visible
        Paint.setStatus("Wait for make menu");
        Condition.wait(Chatbox::isMakeMenuVisible, 100, 40);
        if (Chatbox.isMakeMenuVisible()) {
            Client.sendKeystroke("space");
        }
    }
}