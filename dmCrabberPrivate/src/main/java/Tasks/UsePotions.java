package Tasks;

import utils.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;
import static main.dmCrabberPrivate.*;

public class UsePotions extends Task {
    private long lastOffensivePotionTime = 0;
    private final int offensivePotionInterval = 422000; // A bit over 7 seconds
    private final List<Integer> notifiedTimes = new ArrayList<>();
    private final Map<String, int[]> potionIDsMap = new HashMap<>();

    public UsePotions() {
        potionIDsMap.put("Combat potion", new int[]{9745, 9743, 9741, 9739});
        potionIDsMap.put("Divine super combat", new int[]{23694, 23691, 23688, 23685});
        potionIDsMap.put("Divine ranging", new int[]{23742, 23739, 23736, 23733});
        potionIDsMap.put("Ranging", new int[]{173, 171, 169, 2444});
        potionIDsMap.put("Super combat", new int[]{12701, 12699, 12697, 12695});
        potionIDsMap.put("Super strength", new int[]{161, 159, 157, 2440});
    }

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
            logTimeUntilNextDrink(timeSinceLastDrink);
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
        int[] potionIDs = potionIDsMap.getOrDefault(potions, new int[]{});
        if (potionIDs.length == 0) {
            Logger.debugLog("Incorrect setup drinking offensive potions. Stopping script.");
            Script.stop();
            return;
        }

        for (int id : potionIDs) {
            if (Inventory.contains(id, 0.9)) {
                Inventory.eat(id, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
                return;
            }
        }

        Logger.debugLog("We've ran out of " + potions + " potions, we should bank soon.");
        outOfPots = true;
    }

    private void logTimeUntilNextDrink(long timeSinceLastDrink) {
        int timeUntilNextDrink = (int) (offensivePotionInterval - timeSinceLastDrink);
        int minutes = timeUntilNextDrink / 60000;
        int seconds = (timeUntilNextDrink % 60000) / 1000;
        int[] thresholds = {360, 300, 240, 180, 120, 60, 30};  // Time thresholds in seconds for logging

        for (int threshold : thresholds) {
            if (timeUntilNextDrink <= threshold * 1000 && !notifiedTimes.contains(threshold)) {
                notifiedTimes.add(threshold);  // Add this threshold to the list to avoid repeated logs
                Logger.debugLog("Next offensive potion in: " + minutes + " minute(s) and " + seconds + " second(s).");
                break;  // Only log once for the smallest applicable threshold
            }
        }
    }
}