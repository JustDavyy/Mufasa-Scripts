package tasks;

import helpers.utils.ItemList;
import utils.Task;
import static helpers.Interfaces.*;
import static main.dAIOBowFletcher.*;

public class Process extends Task {
    private int lastUsedSlots = 0;

    @Override
    public boolean activate() {
        if (prepareScriptStop) {
            if (System.currentTimeMillis() - lastProcessTime > 15000) {
                Logger.debugLog("Script prepared for stop, and no item processed in 15 seconds!");
                doneBanking = true;
                return false;
            }
        }
        if (method.equals("Cut")) {
            return checkLogs();
        } else {
            if (product.equals("Shortbow")) {
                return Inventory.contains(shortbowU, 0.75) && Inventory.contains(ItemList.BOW_STRING_1777, 0.75);
            } else {
                return Inventory.contains(longbowU, 0.75) && Inventory.contains(ItemList.BOW_STRING_1777, 0.75);
            }
        }
    }

    @Override
    public boolean execute() {

        Paint.setStatus("Process task");
        // Check if we have leveled up
        if (Player.leveledUp()) {
            Logger.debugLog("We leveled up, restart processing.");
            Paint.setStatus("Re-process after level up");
            if (method.equals("Cut")) {
                processLogs(false);
            } else {
                processBows(false);
            }
            lastProcessTime = System.currentTimeMillis(); // Update last process time
            return true;
        }

        if (!initialActiondone) {
            if (method.equals("Cut")) {
                processLogs(true);
            } else {
                processBows(true);
            }
            lastProcessTime = System.currentTimeMillis(); // Update last process time
            initialActiondone = true;
            return true;
        }

        if (method.equals("Cut")) {
            currentUsedSlots = Inventory.count(logs, 0.75);
        } else {
            currentUsedSlots = Inventory.count(ItemList.BOW_STRING_1777, 0.75);
        }

        // Check if an item has been processed in the last 10 seconds
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
            if (method.equals("Cut")) {
                processLogs(false);
            } else {
                processBows(false);
            }
            lastProcessTime = System.currentTimeMillis(); // Update last process time
        } else {
            if (Inventory.usedSlots() > 5) {
                Paint.setStatus("Wait for interrupt or finish");
                Condition.sleep(2000);
            }
            return false;
        }

        return true;
    }

    private void processLogs(boolean cache) {
        Logger.debugLog("Execute cut bows");
        Paint.setStatus("Execute cut bows");

        // Check if we have both a knife and the logs in the inventory.
        if (!checkLogs() && !Inventory.contains(ItemList.KNIFE_946, 0.75)) {
            Logger.log("We don't have a knife and logs in our inventory, going back to banking!");
            retrycount++;
            return;
        }

        // Reset retry count, as we have the items we need in our inventory
        if (retrycount != 0) {
            retrycount = 0;
            Logger.debugLog("Set retry count to: " + retrycount);
        }

        // Use cached tap location for the knife, as this should never move.
        Inventory.tapItem(ItemList.KNIFE_946, true, 0.75);
        Condition.sleep(generateDelay(125, 200));

        // Tap the logs using a cached location
        Inventory.tapItem(logs, cache, 0.75);
        Condition.wait(() -> makeMenuOpen(), 100, 50);
        Condition.sleep(generateDelay(450, 600));

        if (makeMenuOpen()) {
            if (product.equals("Shortbow")) {
                Client.sendKeystroke("2");
                Logger.debugLog("Selected option 2 in chatbox.");
            } else {
                Client.sendKeystroke("3");
                Logger.debugLog("Selected option 3 in chatbox.");
            }
            Condition.wait(() -> !makeMenuOpen(), 100, 35);
        }
    }

    private void processBows(boolean cache) {
        Logger.debugLog("Execute string bows");
        Paint.setStatus("Execute string bows");

        // Check if we have both unstrung bows and bowstrings in the inventory.
        if (product.equals("Shortbow")) {
            if (!Inventory.contains(shortbowU, 0.75) && !Inventory.contains(ItemList.BOW_STRING_1777, 0.75)) {
                Logger.log("We don't have unstrung bows and bowstring in our inventory, going back to banking!");
                retrycount++;
                return;
            }
        } else {
            if (!Inventory.contains(longbowU, 0.75) && !Inventory.contains(ItemList.BOW_STRING_1777, 0.75)) {
                Logger.log("We don't have unstrung bows and bowstring in our inventory, going back to banking!");
                retrycount++;
                return;
            }
        }

        // Reset retry count, as we have the items we need in our inventory
        if (retrycount != 0) {
            retrycount = 0;
            Logger.debugLog("Set retry count to: " + retrycount);
        }

        // tap item needed based on choice in config
        if (product.equals("Shortbow")) {
            Inventory.tapItem(shortbowU, cache, 0.75);
        } else {
            Inventory.tapItem(longbowU, cache, 0.75);
        }
        Condition.sleep(generateDelay(125, 200));

        Inventory.tapItem(ItemList.BOW_STRING_1777, 0.75);
        Condition.wait(() -> makeMenuOpen(), 100, 40);
        Condition.sleep(generateDelay(450, 600));

        if (makeMenuOpen()) {
            Client.sendKeystroke("1");
            Logger.debugLog("Selected option 1 in chatbox.");
        }
    }
}
