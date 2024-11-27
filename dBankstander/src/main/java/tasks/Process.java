package tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dBankstander.*;

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

        return Inventory.contains(sourceItem, 0.75);
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
        currentUsedSlots = getUsedSlots();

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

    private int getUsedSlots() {
        if ("AmethystCutting".equals(activity)) {
            return Inventory.stackSize(targetItem);
        } else {
            return Inventory.count(targetItem, 0.75);
        }
    }

    private void sendMakeOption() {
        if (!Chatbox.isMakeMenuVisible()) {
            return; // Exit early if the make menu is not visible
        }

        switch (activity) {
            case "Glassblowing":
            case "AmethystCutting":
                Client.sendKeystroke(String.valueOf(makeOption));
                break;
            case "Gemcutting":
                Client.sendKeystroke("space");
                break;
            default:
                Logger.debugLog("Unknown activity (not sending keystroke): " + activity);
        }
    }

    private void tapActivityItems(boolean useCache) {
        switch (activity) {
            case "Glassblowing":
                Inventory.tapItem(ItemList.GLASSBLOWING_PIPE_1785, useCache, 0.75);
                Condition.sleep(generateDelay(100, 200));
                Inventory.tapItem(ItemList.MOLTEN_GLASS_1775, useCache, 0.75);
                break;
            case "Gemcutting":
            case "AmethystCutting":
                Inventory.tapItem(ItemList.CHISEL_1755, useCache, 0.75);
                Condition.sleep(generateDelay(100, 200));
                Inventory.tapItem(sourceItem, useCache, 0.75);
                break;
            default:
                Logger.debugLog("Unknown activity (not sending keystroke): " + activity);
        }
    }

    private void processItems(boolean useCache) {
        Paint.setStatus("Process items");
        Logger.log("Process items");
        // Tap the items with a small random delay in between actions
        tapActivityItems(useCache);

        // Wait for the make menu to be visible
        Paint.setStatus("Wait for make menu");
        Condition.wait(Chatbox::isMakeMenuVisible, 100, 40);
        sendMakeOption();
        Condition.sleep(generateDelay(750, 1000));
    }
}