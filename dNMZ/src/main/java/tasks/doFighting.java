package tasks;

import helpers.annotations.AllowedValue;
import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dNMZ.*;

public class doFighting extends Task {

    @Override
    public boolean activate() {
        // Only run this task when we are inside the NMZ arena
        return insideNMZ;
    }

    @Override
    public boolean execute() {

        // Store current time for usage
        currentTime = System.currentTimeMillis();

        // Check / top up offensive potions
        topUpCombat();

        // Check methods when we are using absorption
        if ("Absorption".equals(NMZMethod)) {
            // Calculate time until the next offensive potion
            int timeUntilNextDrink = (int) (offensivePotionInterval - timeSinceLastDrink);

            // Only run checkHP if we're not about to drink an offensive potion
            if (!"Overload".equals(potions) || timeUntilNextDrink > 20000) {
                checkHP();
            }

            if (currentTime >= nextQuickPrayerFlickTime) {
                prayerFlick();
            }
            if (System.currentTimeMillis() - lastAbsorptionPotionTime >= absorptionPotionInterval) {
                topUpAbsorption();
                Logger.debugLog("Topped up our absorption.");
                lastAbsorptionPotionTime = System.currentTimeMillis();
                absorptionPotionInterval = generateDelay(80000, 125000);
            }
        }

        if (NMZMethod.startsWith("Prayer")) {
            currentPrayerPoints = Player.getPray();
            currentTime = System.currentTimeMillis();
            timeSinceLastDrinkOffensive = currentTime - lastOffensivePotionTime;
            timeUntilNextDrinkOffensive = (int) (offensivePotionInterval - timeSinceLastDrinkOffensive);

            topUpPrayer();
        }

        return true;
    }

    private void topUpCombat() {
        timeSinceLastDrink = currentTime - lastOffensivePotionTime;

        if (timeSinceLastDrink >= offensivePotionInterval) {
            drinkOffensivePot();  // This method updates lastOffensivePotionTime
            Logger.log("Topped up our offensive stats using: " + potions + " potion(s).");
            notifiedTimes.clear();  // Reset the notification times list
        } else {
            int timeUntilNextDrink = (int) (offensivePotionInterval - timeSinceLastDrink); // Calculate the time until the next drink
            int minutes = timeUntilNextDrink / 60000;
            int seconds = (timeUntilNextDrink % 60000) / 1000;
            int[] thresholds = {240, 180, 120, 60, 30};  // Time thresholds in seconds for logging

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

            if (timeUntilNextDrink <= 22000) { // Check if it's time to drink within the next 22 seconds
                Condition.sleep(timeUntilNextDrink);  // Sleep until it's time for the next drink
                drinkOffensivePot();  // Drink the potion and update the time
                Logger.log("Topped up our offensive stats using: " + potions + " potion(s).");
                notifiedTimes.clear();  // Reset the notification times list
            }
        }
    }

    private void topUpPrayer() {
        // Custom logic depending on which prayer we are using
        switch (NMZMethod) {
            case "Prayer - Chivalry":
                if (currentPrayerPoints < 37) {
                    if (timeUntilNextDrinkOffensive > 15000) { // More than 15 seconds until the next offensive potion
                        Condition.sleep(generateDelay(250, 1000));
                        consumePrayerPotion(prayerPotColor);
                        Logger.log("Restored some prayer points.");
                    }
                }
                break;
            case "Prayer - Piety":
                if (currentPrayerPoints < 45) {
                    if (timeUntilNextDrinkOffensive > 15000) { // More than 15 seconds until the next offensive potion
                        Condition.sleep(generateDelay(250, 1000));
                        consumePrayerPotion(prayerPotColor);
                        Logger.log("Restored some prayer points.");
                    }
                }
                break;
            case "Prayer - Rigour":
                if (currentPrayerPoints < 48) {
                    if (timeUntilNextDrinkOffensive > 15000) { // More than 15 seconds until the next offensive potion
                        Condition.sleep(generateDelay(250, 1000));
                        consumePrayerPotion(prayerPotColor);
                        Logger.log("Restored some prayer points.");
                    }
                }
                break;
            case "Prayer - Augury":
                if (currentPrayerPoints < 50) {
                    if (timeUntilNextDrinkOffensive > 15000) { // More than 15 seconds until the next offensive potion
                        Condition.sleep(generateDelay(250, 1000));
                        consumePrayerPotion(prayerPotColor);
                        Logger.log("Restored some prayer points.");
                    }
                }
                break;
            default:
                if (currentPrayerPoints < 24) {
                    Condition.sleep(generateDelay(250, 1000));
                    consumePrayerPotion(prayerPotColor);
                    Logger.log("Restored some prayer points.");
                }
        }
    }

    private boolean consumePrayerPotion(Color potColor) {
        if (Inventory.contains(ItemList.PRAYER_POTION_1_143, 0.94, potColor)) {
            Logger.debugLog("Drinking from a 1 dosed " + "Prayer" + " potion.");
            Inventory.eat(ItemList.PRAYER_POTION_1_143, 0.94, potColor);
            Condition.sleep(generateDelay(2000, 3000));
            return true;
        } else if (Inventory.contains(ItemList.PRAYER_POTION_2_141, 0.94, potColor)) {
            Logger.debugLog("Drinking from a 2 dosed " + "Prayer" + " potion.");
            Inventory.eat(ItemList.PRAYER_POTION_2_141, 0.94, potColor);
            Condition.sleep(generateDelay(2000, 3000));
            return true;
        } if (Inventory.contains(ItemList.PRAYER_POTION_3_139, 0.94, potColor)) {
            Logger.debugLog("Drinking from a 3 dosed " + "Prayer" + " potion.");
            Inventory.eat(ItemList.PRAYER_POTION_3_139, 0.94, potColor);
            Condition.sleep(generateDelay(2000, 3000));
            return true;
        } if (Inventory.contains(ItemList.PRAYER_POTION_4_2434, 0.94, potColor)) {
            Logger.debugLog("Drinking from a 4 dosed " + "Prayer" + " potion.");
            Inventory.eat(ItemList.PRAYER_POTION_4_2434, 0.94, potColor);
            Condition.sleep(generateDelay(2000, 3000));
            return true;
        } else {
            Logger.debugLog("No " + "Prayer" + " potions found in inventory, leaving the instance!");
            leaveNMZ();
            return false;
        }
    }

    private void topUpAbsorption() {
        int currentAbsorption = absorpLeft();

        // If the current absorption count is invalid (-1) or already at/above 950, no need to proceed
        if (currentAbsorption < 0 || currentAbsorption >= 950) {
            Logger.debugLog("No need to top up absorption potions. Current absorption: " + currentAbsorption);
            return;
        }

        while (currentAbsorption < 950) {
            int absorptionAdded = consumeAbsorptionPotion();

            if (absorptionAdded <= 0) {
                Logger.debugLog("Failed to find or consume an absorption potion. Exiting top-up process.");
                leaveNMZ();
                break;
            }

            currentAbsorption += absorptionAdded;

            // Short delay to simulate human-like interaction
            Condition.sleep(generateDelay(200, 500));
        }
        Condition.sleep(generateDelay(750, 1250));
        Logger.log("Topped up absorption count. (now at: " + absorpLeft() + ")");
    }

    private int consumeAbsorptionPotion() {
        Rectangle absorbRect;
        // Tries to consume an absorption potion and returns the absorption value added
        absorbRect = Inventory.findItem(ItemList.ABSORPTION_1_11737, 0.9, absorbPotColor);
        if (absorbRect != null) {
            tapAbsorbPotion(absorbRect, 1);
            return 50;
        }
        absorbRect = Inventory.findItem(ItemList.ABSORPTION_2_11736, 0.94, absorbPotColor);
        if (absorbRect != null) {
            tapAbsorbPotion(absorbRect, 2);
            return 100;
        }
        absorbRect = Inventory.findItem(ItemList.ABSORPTION_3_11735, 0.94, absorbPotColor);
        if (absorbRect != null) {
            tapAbsorbPotion(absorbRect, 3);
            return 150;
        }
        absorbRect = Inventory.findItem(ItemList.ABSORPTION_4_11734, 0.94, absorbPotColor);
        if (absorbRect != null) {
            tapAbsorbPotion(absorbRect, 3);
            return 200;
        }
        return 0; // No potions found, or failed to consume
    }

    private void tapAbsorbPotion(Rectangle potRect, int taps) {
        for (int i = 0; i < taps; i++) {
            Client.tap(potRect);
            Condition.sleep(generateDelay(350, 500)); // Delay between taps
        }
    }

    private int absorpLeft() {
        return interfaces.readCustomStackSize(absorptionCountRect, absorbTextColors, absorptionDigitPatterns);
    }

    private void prayerFlick() {
        toggleQuickPrayer();
        Logger.debugLog("Quick prayers flicked to reset HP timer.");

        // Generate delay for the next flick time
        long delayMillis = generateDelay(15000, 45000); // Delay between 15 to 45 seconds
        nextQuickPrayerFlickTime = System.currentTimeMillis() + delayMillis;

        // Log the next flick time in seconds
        Logger.debugLog(String.format("Next quick prayer flick scheduled in %.2f seconds.", delayMillis / 1000.0));
    }

    private void checkHP() {
        currentHP = Player.getHP();

        // Immediately lower HP if above 3
        if (currentHP > 3) {
            Logger.debugLog("Current HP is above 3 (" + currentHP + "). Lowering HP immediately.");
            lowerHP(); // Reduce HP to 1
            currentHP = Player.getHP(); // Update current HP
            targetHPForAction = getRandomTargetHP(); // Set a new random target
            Logger.debugLog("New targetHPForAction set to: " + targetHPForAction + " after lowering HP.");
            lastTimeHPWasTwo = 0; // Reset the 2 HP timer
            return; // Exit early to avoid further checks
        }

        // Handle prolonged HP of 2 with a target of 3
        if (currentHP == 2 && targetHPForAction == 3) {
            if (lastTimeHPWasTwo == 0) {
                lastTimeHPWasTwo = System.currentTimeMillis(); // Start the timer
            } else if (System.currentTimeMillis() - lastTimeHPWasTwo > 40_000) {
                Logger.debugLog("HP has been 2 for over 40 seconds with target 3. Forcing reduction to 1.");
                lowerHP(); // Force reduction to 1
                currentHP = Player.getHP(); // Update current HP
                targetHPForAction = getRandomTargetHP(); // Set a new random target
                Logger.debugLog("New targetHPForAction set to: " + targetHPForAction + " after forced action.");
                lastTimeHPWasTwo = 0; // Reset the timer
                return; // Exit early
            }
        } else {
            lastTimeHPWasTwo = 0; // Reset timer if conditions don't match
        }

        // Set an initial target if not already set
        if (targetHPForAction == 0 && currentHP <= 3 && currentHP > 1) {
            targetHPForAction = getRandomTargetHP(); // Set initial target with controlled probability
            Logger.debugLog("Initial targetHPForAction set to: " + targetHPForAction);
        }

        // Check if HP matches the target and take action
        if (currentHP == targetHPForAction && currentHP > 1) {
            Logger.debugLog("Current HP (" + currentHP + ") matches target (" + targetHPForAction + "). Lowering HP.");
            lowerHP(); // Reduce HP to 1
            currentHP = Player.getHP(); // Update current HP
            targetHPForAction = getRandomTargetHP(); // Set a new random target
            Logger.debugLog("New targetHPForAction set to: " + targetHPForAction + " after action.");
        }
    }

    private void lowerHP() {
        int initialHP = Player.getHP();

        // Exit early if HP is already 1 or less
        if (initialHP <= 1) {
            return;
        }

        // Lower HP
        reduceHP();

        // Reset the HP regeneration timer if using Absorption method
        if ("Absorption".equals(NMZMethod)) {
            toggleQuickPrayer();
        }
    }

    private void reduceHP() {
        while (Player.getHP() > 1) {
            switch (HPMethod) {
                case "Locator orb":
                    Inventory.tapItem(ItemList.LOCATOR_ORB_22081, true, 0.8);
                    break;
                case "Rock cake":
                    if (!rockcakeMESDone) {
                        handleRockCakeMES();
                    }
                    Inventory.tapItem(ItemList.DWARVEN_ROCK_CAKE_7510, true, 0.8);
                    break;
                default:
                    Logger.debugLog("No HP method selected.");
            }
            Condition.sleep(generateDelay(200, 400)); // Short delay between uses
        }
    }

    private int getRandomTargetHP() {
        int decision = random.nextInt(10); // Generate a random number between 0 and 9

        // Determine the target HP based on probability
        int target = (decision < 8) ? 2 : 3; // 80% chance for 2, 20% chance for 3

        Logger.debugLog(String.format("Randomly chosen targetHPForAction: %d (Decision: %d)", target, decision));
        return target;
    }
}