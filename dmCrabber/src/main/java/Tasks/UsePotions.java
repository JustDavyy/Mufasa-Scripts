package Tasks;

import utils.Spots;
import utils.Task;

import java.util.ArrayList;
import java.util.List;

import static helpers.Interfaces.*;
import static main.dmCrabber.*;

public class UsePotions extends Task {
    private long lastOffensivePotionTime = 0;
    private int offensivePotionInterval = 422000; // A bit over 7 seconds
    private List<Integer> notifiedTimes = new ArrayList<Integer>();
    @Override
    public boolean activate() {

        if (!usingPots) {
            return false;
        }

        if (lastOffensivePotionTime == 0) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastDrink = currentTime - lastOffensivePotionTime;

        if (timeSinceLastDrink >= offensivePotionInterval) {
            notifiedTimes.clear();  // Reset the notification times list
            return true;
        } else {
            int timeUntilNextDrink = (int) (offensivePotionInterval - timeSinceLastDrink); // Calculate the time until the next drink
            int minutes = timeUntilNextDrink / 60000;
            int seconds = (timeUntilNextDrink % 60000) / 1000;
            int[] thresholds = {360, 300, 240, 180, 120, 60, 30};  // Time thresholds in seconds for logging

            boolean shouldLog = false;
            for (int threshold : thresholds) {
                if (timeUntilNextDrink <= threshold * 1000 && !notifiedTimes.contains(threshold)) {
                    notifiedTimes.add(threshold);  // Add this threshold to the list to avoid repeated logs
                    shouldLog = true;
                    break;  // Only log once for the smallest applicable threshold
                }
            }

            if (shouldLog) {
                Logger.debugLog("Next offensive potion in: " + minutes + " minute(s) and " + seconds + " second(s).");
            }

            return false;
        }
    }

    @Override
    public boolean execute() {

        // Make sure the inventory is open
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        drinkPotion();
        Logger.log("Topped up our offensive stats using: " + potions + " potion(s).");

        return false;
    }


    private void drinkPotion() {
        // Giant load of shit statements to find the correct potion, starting at 1 dose towards 4.
        switch (potions) {
            case "Combat potion":
                if (Inventory.contains(9745, 0.9)) {
                    Inventory.eat(9745, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(9743, 0.9)) {
                    Inventory.eat(9743, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(9741, 0.9)) {
                    Inventory.eat(9741, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(9739, 0.9)) {
                    Inventory.eat(9739, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else {
                    Logger.debugLog("We've ran out of Combat potions, we should bank soon.");
                    outOfPots = true;
                }
                break;
            case "Divine super combat":
                if (Inventory.contains(23694, 0.9)) {
                    Inventory.eat(23694, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(23691, 0.9)) {
                    Inventory.eat(23691, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(23688, 0.9)) {
                    Inventory.eat(23688, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(23685, 0.9)) {
                    Inventory.eat(23685, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else {
                    Logger.debugLog("We've ran out of Divine super combat potions, we should bank soon.");
                    outOfPots = true;
                }
                break;
            case "Divine ranging":
                if (Inventory.contains(23742, 0.9)) {
                    Inventory.eat(23742, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(23739, 0.9)) {
                    Inventory.eat(23739, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(23736, 0.9)) {
                    Inventory.eat(23736, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(23733, 0.9)) {
                    Inventory.eat(23733, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else {
                    Logger.debugLog("We've ran out of Divine ranging potions, we should bank soon.");
                    outOfPots = true;
                }
                break;
            case "Ranging":
                if (Inventory.contains(173, 0.97)) {
                    Inventory.eat(173, 0.97);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(171, 0.97)) {
                    Inventory.eat(171, 0.97);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(169, 0.97)) {
                    Inventory.eat(169, 0.97);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(2444, 0.97)) {
                    Inventory.eat(2444, 0.97);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else {
                    Logger.debugLog("We've ran out of Ranging potions, we should bank soon.");
                    outOfPots = true;
                }
                break;
            case "Super combat":
                if (Inventory.contains(12701, 0.9)) {
                    Inventory.eat(12701, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(12699, 0.9)) {
                    Inventory.eat(12699, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(12697, 0.9)) {
                    Inventory.eat(12697, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(12695, 0.9)) {
                    Inventory.eat(12695, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else {
                    Logger.debugLog("We've ran out of Super combat potions, we should bank soon.");
                    outOfPots = true;
                }
                break;
            case "Super strength":
                if (Inventory.contains(161, 0.9)) {
                    Inventory.eat(161, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(159, 0.9)) {
                    Inventory.eat(159, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(157, 0.9)) {
                    Inventory.eat(157, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else if (Inventory.contains(2440, 0.9)) {
                    Inventory.eat(2440, 0.9);
                    lastOffensivePotionTime = System.currentTimeMillis();
                } else {
                    Logger.debugLog("We've ran out of Super strength potions, we should bank soon.");
                    outOfPots = true;
                }
                break;
            default:
                Logger.debugLog("Incorrect setup drinking offensive potions. Stopping script.");
                Script.stop();
        }
    }
}
