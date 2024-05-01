import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.Area;
import helpers.utils.OptionType;
import helpers.utils.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;

import java.util.Random;

@ScriptManifest(
        name = "dNMZ",
        description = "Slays all the nightmare monsters in Gielinor on automatic pilot. Automatically restocks on potions, supports all styles.",
        version = "1.054",
        guideLink = "https://wiki.mufasaclient.com/docs/dnmz/",
        categories = {ScriptCategory.Combat, ScriptCategory.Magic}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "NMZ Method",
                        description = "Which NMZ method would you like to use?",
                        defaultValue = "Absorption",
                        allowedValues = {
                                @AllowedValue(optionIcon = "11734", optionName = "Absorption"),
                                @AllowedValue(optionIcon = "2434", optionName = "Prayer")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "HP Drop Method",
                        description = "Which item would you like to use to drop your HP?",
                        defaultValue = "Rock cake",
                        allowedValues = {
                                @AllowedValue(optionIcon = "22081", optionName = "Locator orb"),
                                @AllowedValue(optionIcon = "7510", optionName = "Rock cake")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Potions",
                        description = "Which combat potions would you like to use?",
                        defaultValue = "Overload",
                        allowedValues = {
                                @AllowedValue(optionIcon = "23685", optionName = "Divine super combat"),
                                @AllowedValue(optionIcon = "23733", optionName = "Divine ranging"),
                                @AllowedValue(optionIcon = "11730", optionName = "Overload"),
                                @AllowedValue(optionIcon = "2444", optionName = "Ranging"),
                                @AllowedValue(optionIcon = "2436", optionName = "Super att/str combo"),
                                @AllowedValue(optionIcon = "12695", optionName = "Super combat"),
                                @AllowedValue(optionIcon = "2440", optionName = "Super strength only")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "BankTab",
                        description = "What bank tab are your supplies located in?",
                        defaultValue = "0",
                        optionType = OptionType.BANKTABS
                ),
                @ScriptConfiguration(
                        name =  "Breaking (lower end)",
                        description = "Please provide the lower end of your desired break time (in minutes).\nDISCLAIMER: It is advised to stay under an hour when breaking while doing NMZ.\nValid range is between 15 and 60, it will be randomised between the lower and higher end.",
                        defaultValue = "20",
                        minMaxIntValues = {15, 60},
                        optionType = OptionType.INTEGER
                ),
                @ScriptConfiguration(
                        name =  "Breaking (higher end)",
                        description = "Please provide the higher end of your desired break time (in minutes).\nDISCLAIMER: It is advised to stay under an hour when breaking while doing NMZ.\nValid range is between 30 and 120, it will be randomised between the lower and higher end.",
                        defaultValue = "50",
                        minMaxIntValues = {30, 120},
                        optionType = OptionType.INTEGER
                ),
                @ScriptConfiguration(
                        name =  "World hopping",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dNMZ extends AbstractScript {
    private Random random = new Random();
    // Creating the strings for later use

    // Strings
    String hopProfile;
    String potions;
    String HPMethod;
    String NMZMethod;

    // Booleans
    Boolean hopEnabled;
    Boolean useWDH;
    Boolean bankYN;
    Boolean insideNMZ;
    Boolean justLeftInstance = false;

    // Lists
    private List<Integer> notifiedTimes = new ArrayList<Integer>();

    // Integers
    int banktab;
    int lowerBreak;
    int higherBreak;
    int breakAfterMinutes;
    int absorptionPotionInterval;
    int offensivePotionInterval = 300000;
    int targetHPForAction = 0;

    // Longs
    long lastBreakTime;
    private long lastOffensivePotionTime;
    private long lastAbsorptionPotionTime;
    private long nextQuickPrayerFlickTime = 0;
    private long lastTimeHPWasTwo = 0;

    // Tiles
    Tile bankTile = new Tile(85, 95);
    Tile rewardChestTile = new Tile(80, 63);
    Tile vialOutsideTile = new Tile(74, 66);
    Tile absorptionBarrelTile = new Tile(69, 64);
    Tile overloadBarrelTile = new Tile(69, 66);
    Tile vialInsideTile = new Tile(195, 93);
    Tile dominicOnionTile = new Tile(80, 66);
    Tile respawnTile = new Tile(78, 67);

    // Areas
    Area NMZArea = new Area(
            new Tile(63, 58),
            new Tile(85, 78)
    );

    // Paths
    Tile[] pathToBank = new Tile[] {
            new Tile(81, 70),
            new Tile(85, 76),
            new Tile(80, 83),
            new Tile(77, 90),
            new Tile(82, 95)
    };
    Tile[] pathToNMZ = new Tile[] {
            new Tile(77, 89),
            new Tile(82, 84),
            new Tile(86, 76),
            new Tile(81, 70)
    };
    
    // Rectangles
    Rectangle bankBooth = new Rectangle(479, 250, 29, 31);
    Rectangle vialInside = new Rectangle(438, 307, 19, 20);
    Rectangle rewardChest = new Rectangle(430, 210, 29, 36);
    Rectangle quickPrayers = new Rectangle(699, 87, 20, 17);

    // Points
    Point lowerHPItem = new Point(69,69);

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("World hopping"));
        hopEnabled = Boolean.valueOf((configs.get("World hopping.enabled")));
        useWDH = Boolean.valueOf((configs.get("World hopping.useWDH")));
        bankYN= Boolean.valueOf((configs.get("Banking")));
        banktab = Integer.parseInt(configs.get("BankTab"));
        potions = (configs.get("Potions"));
        HPMethod = (configs.get("HP Drop Method"));
        NMZMethod = (configs.get("NMZ Method"));
        lowerBreak = Integer.parseInt(configs.get("Breaking (lower end)"));
        higherBreak = Integer.parseInt(configs.get("Breaking (higher end)"));

        Logger.log("Thank you for using the dNMZ script!\nSetting up everything for your gains now...");

        // Set the map we'll be using (Custom NMZ)
        Walker.setup("/maps/NMZ.png");

        // Check if auto retaliate is on.
        Player.enableAutoRetaliate();

        // Make sure the inventory is opened
        GameTabs.openInventoryTab();

        // Make sure the chatbox is opened
        Chatbox.openAllChat();

        // Ensure that break times are correctly ordered
        if (lowerBreak > higherBreak) {
            int temp = lowerBreak;
            lowerBreak = higherBreak;
            higherBreak = temp;
        }

        // Initialize the break timer
        breakAfterMinutes = generateDelay(lowerBreak, higherBreak) * 60000; // Convert minutes to milliseconds
        lastBreakTime = System.currentTimeMillis();
        absorptionPotionInterval = generateDelay(240000, 300000);

        // Set inside NMZ to false
        insideNMZ = false;

        // Disable the built-in break/AFK handler, as we use our own break handler and do not want to AFK.
        Client.disableBreakHandler();
        Client.disableAFKHandler();

        if (!Player.within(NMZArea)) {
            Logger.debugLog("We are not within the NMZ area, trying to move there.");
            moveToNMZ();
        }
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        // Check for break time
        if (insideNMZ) {
            if (System.currentTimeMillis() - lastBreakTime >= breakAfterMinutes) {
                leaveNMZ();
                // Wait at least 10 seconds to be out of combat.
                Condition.sleep(generateDelay(11000,13000));
                takeBreak();
                breakAfterMinutes = generateDelay(lowerBreak, higherBreak) * 60000; // Reset the break timer in milliseconds

                // Calculate the next break time in hours, minutes, and seconds
                int hours = breakAfterMinutes / 3600000;
                int minutes = (breakAfterMinutes % 3600000) / 60000;
                int seconds = (breakAfterMinutes % 60000) / 1000;

                // Log how long it will be until the next break
                Logger.log("Next break in: " + hours + " hour(s), " + minutes + " minute(s), and " + seconds + " second(s).");
            }
        }

        // Check for offensive potion time within NMZ
        if (insideNMZ) {
            long currentTime = System.currentTimeMillis();
            long timeSinceLastDrink = currentTime - lastOffensivePotionTime;

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

                if (timeUntilNextDrink <= 20000) { // Check if it's time to drink within the next 20 seconds
                    Condition.sleep(timeUntilNextDrink);  // Sleep until it's time for the next drink
                    drinkOffensivePot();  // Drink the potion and update the time
                    Logger.log("Topped up our offensive stats using: " + potions + " potion(s).");
                    notifiedTimes.clear();  // Reset the notification times list
                }
            }
        }


        // Check for HP level, but only if using absorptions
        if (insideNMZ && java.util.Objects.equals(NMZMethod, "Absorption")) {
            int currentHP = Player.getHP();

            // Immediately lower HP if it's above 3, regardless of the current target
            if (currentHP > 3) {
                Logger.debugLog("Current HP is above 3 (" + currentHP + "). Lowering HP immediately.");
                lowerHP();  // Method to reduce HP to 1
                currentHP = Player.getHP();  // Re-check HP after action
                targetHPForAction = getRandomTargetHP();  // Set new random target after lowering HP
                Logger.debugLog("New targetHPForAction set to: " + targetHPForAction + " after lowering HP.");
                lastTimeHPWasTwo = 0;  // Reset the timer since HP is no longer 2
                return;  // Exit the method to avoid any further action within this cycle
            }

            // Check if HP has been 2 for more than 40 seconds and target is 3
            if (currentHP == 2 && targetHPForAction == 3) {
                if (lastTimeHPWasTwo == 0) {  // If timer not set, start it
                    lastTimeHPWasTwo = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - lastTimeHPWasTwo > 40000) {  // Check if 40 seconds have passed
                    Logger.debugLog("HP has been 2 for over 40 seconds with target 3. Forcing reduction to 1.");
                    lowerHP();  // Force reduce HP to 1
                    currentHP = Player.getHP();  // Re-check HP after action
                    targetHPForAction = getRandomTargetHP();  // Set new random target after lowering HP
                    Logger.debugLog("New targetHPForAction set to: " + targetHPForAction + " after forced action.");
                    lastTimeHPWasTwo = 0;  // Reset the timer
                    return;  // Exit the method to avoid any further action within this cycle
                }
            } else {
                lastTimeHPWasTwo = 0;  // Reset the timer if conditions don't match
            }

            // Set initial target HP for action if not already set and HP is within range
            if (targetHPForAction == 0 && currentHP <= 3 && currentHP > 1) {
                targetHPForAction = getRandomTargetHP();  // Use controlled probability to decide initial target
                Logger.debugLog("Initial targetHPForAction set to: " + targetHPForAction);
            }

            // Check if HP is at the target and needs action
            if (currentHP == targetHPForAction && currentHP > 1) {
                Logger.debugLog("Current HP (" + currentHP + ") is at target (" + targetHPForAction + "), lowering HP.");
                lowerHP();  // Method to reduce HP to 1
                currentHP = Player.getHP();  // Re-check HP after action
                targetHPForAction = getRandomTargetHP();  // Randomly set target to 2 or 3 for next action with specific probabilities
                Logger.debugLog("New targetHPForAction set to: " + targetHPForAction + " after action.");
            }
        }

        // Check if it's time to flick quick prayers
        if (insideNMZ && java.util.Objects.equals(NMZMethod, "Absorption") && System.currentTimeMillis() >= nextQuickPrayerFlickTime) {
            Client.tap(quickPrayers);
            Client.tap(quickPrayers);
            Logger.debugLog("Quick prayers flicked to reset HP timer.");

            // Schedule the next flick time
            nextQuickPrayerFlickTime = System.currentTimeMillis() + generateDelay(30000, 60000); // Set next flick time between 30 to 60 seconds
        }

        // Check for absorption potion time
        if (insideNMZ && java.util.Objects.equals(NMZMethod, "Absorption")) {
            if (System.currentTimeMillis() - lastAbsorptionPotionTime >= absorptionPotionInterval) {
                drinkAbsorptions();
                Logger.debugLog("Topped up the absorption potions.");
                lastAbsorptionPotionTime = System.currentTimeMillis();
                absorptionPotionInterval = generateDelay(30000, 120000); // Reset to a new random time between 4 and 5 minutes
            }
        }

        // Check for prayer points
        if (insideNMZ && java.util.Objects.equals(NMZMethod, "Prayer")) {
            int currentPrayerPoints = Player.getPray();
            if (currentPrayerPoints < 24 && currentPrayerPoints > 5) {
                long currentTime = System.currentTimeMillis();
                long timeSinceLastDrinkOffensive = currentTime - lastOffensivePotionTime;
                int timeUntilNextDrinkOffensive = (int) (offensivePotionInterval - timeSinceLastDrinkOffensive);

                if (currentPrayerPoints < 19) {  // If prayer points are between 6 and 18, inclusive
                    if (timeUntilNextDrinkOffensive > 60000) {  // More than a minute until the next offensive potion
                        int randomSleepTime = new Random().nextInt(8001) + 2000;  // Random time between 2 and 10 seconds
                        Condition.sleep(randomSleepTime);
                    }
                } else {  // If prayer points are between 19 and 23, inclusive
                    if (timeUntilNextDrinkOffensive > 60000) {  // More than a minute until the next offensive potion
                        int randomSleepTime = new Random().nextInt(5001) + 10000;  // Random time between 10 and 15 seconds
                        Condition.sleep(randomSleepTime);
                    }
                }

                drinkPrayer();
                Logger.log("Restored some prayer points.");
            } else if (currentPrayerPoints == 0) {
                drinkPrayer();
                // Check for the tab, if not open, open it
                GameTabs.openPrayerTab();

                Prayer.activateProtectfromMelee();
                Condition.wait(() -> Prayer.isActiveProtectfromMelee(), 250, 20);
                if (!Prayer.isActiveProtectfromMelee()) {
                    Prayer.activateProtectfromMelee();
                    Condition.wait(() -> Prayer.isActiveProtectfromMelee(), 250, 10);
                    if (!Prayer.isActiveProtectfromMelee()) {
                        Prayer.activateProtectfromMelee();
                        Condition.wait(() -> Prayer.isActiveProtectfromMelee(), 250, 10);
                    } if (!Prayer.isActiveProtectfromMelee()) {
                        Logger.debugLog("Failed to activate protect from melee.");
                    }
                }

                GameTabs.openInventoryTab();
                Logger.log("Restored some prayer points and re-enabled prayer.");
            }
        }

        // Logic here for re-starting a NMZ dream, when using overloads AND absorptions
        if (!insideNMZ && java.util.Objects.equals(NMZMethod, "Absorption") && java.util.Objects.equals(potions, "Overload")){
            // Store all the leftover absorbs and overloads we have
            if (Inventory.containsAny(new int[]{11733, 11732, 11731, 11730}, 0.9)) {
                moveToBarrel("Overload");
                storeBarrel();
            }
            if (Inventory.containsAny(new int[]{11737, 11736, 11735, 11734}, 0.9)) {
                moveToBarrel("Absorption");
                storeBarrel();
            }
            // Move to Dominic Onion (start tile)
            moveToDominic();
            // Move towards the chest, and wait for us to arrive
            Client.tap(450,184);
            Condition.wait(() -> Player.atTile(rewardChestTile), 250, 20);
            // Restock (buy) the pots we need
            restockNMZPotionsCHEST();
            // Proceed to restock them in the inventory
            restockNMZPotionsBARREL();
            // Move back to Dominic
            moveToDominic();
            // Start a dream
            startNMZDream();
            // Enter the just started dream
            enterNMZDream();
        }

        if (!insideNMZ && java.util.Objects.equals(NMZMethod, "Absorption") && !java.util.Objects.equals(potions, "Overload")){
            // Store all the leftover absorbs we have
            if (Inventory.containsAny(new int[]{11737, 11736, 11735, 11734}, 0.9)) {
                moveToBarrel("Absorption");
                storeBarrel();
            }
            // Move to the bank
            moveToBank();
            // Do the banking
            bank();
            // Move back to NMZ
            moveToNMZ();
            // Move to Dominic Onion (start tile)
            moveToDominic();
            // Move towards the chest, and wait for us to arrive
            Client.tap(450,184);
            Condition.wait(() -> Player.atTile(rewardChestTile), 250, 20);
            // Restock (buy) the pots we need
            restockNMZPotionsCHEST();
            // Proceed to restock them in the inventory
            restockNMZPotionsBARREL();
            // Move back to Dominic
            moveToDominic();
            // Start a dream
            startNMZDream();
            // Enter the just started dream
            enterNMZDream();
        }

        if (!insideNMZ && java.util.Objects.equals(NMZMethod, "Prayer") && java.util.Objects.equals(potions, "Overload")) {
            // Store all the leftover overloads we have
            if (Inventory.containsAny(new int[]{11733, 11732, 11731, 11730}, 0.9)) {
                moveToBarrel("Overload");
                storeBarrel();
            }
            // Move to the bank
            moveToBank();
            // Do the banking
            bank();
            // Move back to NMZ
            moveToNMZ();
            // Move to Dominic Onion (start tile)
            moveToDominic();
            // Move towards the chest, and wait for us to arrive
            Client.tap(450,184);
            Condition.wait(() -> Player.atTile(rewardChestTile), 250, 20);
            // Restock (buy) the pots we need
            restockNMZPotionsCHEST();
            // Proceed to restock them in the inventory
            restockNMZPotionsBARREL();
            // Move back to Dominic
            moveToDominic();
            // Start a dream
            startNMZDream();
            // Enter the just started dream
            enterNMZDream();
        }

        if (!insideNMZ && java.util.Objects.equals(NMZMethod, "Prayer") && !java.util.Objects.equals(potions, "Overload")) {
            // Move to the bank
            moveToBank();
            // Do the banking
            bank();
            // Move back to NMZ
            moveToNMZ();
            // Move to Dominic Onion (start tile)
            moveToDominic();
            // Move towards the chest, and wait for us to arrive
            Client.tap(450,184);
            Condition.wait(() -> Player.atTile(rewardChestTile), 250, 20);
            // Move back to Dominic
            moveToDominic();
            // Start a dream
            startNMZDream();
            // Enter the just started dream
            enterNMZDream();
        }

        // Update the XP counter but only when inside NMZ
        if (insideNMZ) {
            readXP();
        }
    }


    private void moveToBank() {
        Logger.log("Heading towards the bank!");
        Logger.debugLog("Moving towards the bank!");
        // Walk the path
        Walker.walkPath(pathToBank);
        Condition.sleep(750);

        // Walk to the specific bank tile
        Walker.step(bankTile);
        Condition.wait(() -> Player.atTile(bankTile), 250, 20);

        // Failsafe
        if (!Player.atTile(bankTile)) {
            Logger.debugLog("Failed walking towards the bankTile, retrying...");
            Walker.step(bankTile);
            Condition.wait(() -> Player.atTile(bankTile), 250, 20);

            if (!Player.atTile(bankTile)) {
                Logger.debugLog("Failed walking towards the bankTile for a second time, resetting!");
                moveToNMZ();
                moveToBank();
            } else {
                Logger.debugLog("Arrived at the bank.");
            }
        } else {
            Logger.debugLog("Arrived at the bank.");
        }
    }

    private void moveToNMZ() {
        Logger.log("Heading back to NMZ!");
        Logger.debugLog("Moving back to NMZ!");

        // Walk the path
        Walker.walkPath(pathToNMZ);
        Condition.sleep(1500);
    }

    private void bank() {
        Logger.log("Banking!");
        Logger.debugLog("Running the banking method!");

        // Tap the booth to open bank
        Client.tap(bankBooth);
        Condition.wait(() -> Bank.isOpen(), 250, 20);

        // Failsafe
        if (!Bank.isOpen()) {
            Client.tap(bankBooth);
            Condition.wait(() -> Bank.isOpen(), 250, 20);
            if (!Bank.isOpen()) {
                Logger.debugLog("Failed to open the bank, retrying...");
                Walker.step(bankTile);
                Condition.wait(() -> Player.atTile(bankTile), 250, 20);

                Client.tap(bankBooth);
                Condition.wait(() -> Bank.isOpen(), 250, 20);

                if (!Bank.isOpen()) {
                    Logger.debugLog("Failed to open the bank twice, stopping the script");
                    Logger.log("Banking failed, stopping script.");
                    Logout.logout();
                    Script.stop();
                }
            }
        }

        // Enter pin if pin is needed
        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
        }

        // Deposit everything
        Bank.tapDepositInventoryButton();

        // Go to the right bank tab if needed
        if (!Bank.isSelectedBankTab(banktab)) {
            Bank.openTab(banktab);
            Condition.wait(() -> Bank.isSelectedBankTab(banktab), 250, 12);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // If using absorptions, withdraw our HP method item again
        if (java.util.Objects.equals(NMZMethod, "Absorption")) {
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }

            if (java.util.Objects.equals(HPMethod, "Rock cake")) {
                Bank.withdrawItem("7510", 0.9);
            }
            if (java.util.Objects.equals(HPMethod, "Locator orb")) {
                Bank.withdrawItem("22081", 0.9);
            }
        }

        // Withdraw stuff we need based on configuration choices
        if (java.util.Objects.equals(potions, "Divine super combat")) {
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(() -> Bank.isSelectedQuantity5Button(), 250, 12);
            }
            Bank.withdrawItem("23685", 0.95);
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }
            Bank.withdrawItem("23685", 0.95);

            // This should withdraw 6 Divine super combat potions.
        }

        if (java.util.Objects.equals(potions, "Divine ranging")) {
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(() -> Bank.isSelectedQuantity5Button(), 250, 12);
            }
            Bank.withdrawItem("23733", 0.95);
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }
            Bank.withdrawItem("23733", 0.95);

            // This should withdraw 6 Divine ranging potions.
        }

        if (java.util.Objects.equals(potions, "Ranging")) {
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(() -> Bank.isSelectedQuantity5Button(), 250, 12);
            }
            Bank.withdrawItem("2444", 0.95);
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }
            Bank.withdrawItem("2444", 0.95);

            // This should withdraw 6 ranging potions.
        }

        if (java.util.Objects.equals(potions, "Super att/str combo")) {
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }
            Bank.withdrawItem("2436", 0.95);
            Bank.withdrawItem("2440", 0.95);
            Bank.withdrawItem("2436", 0.95);
            Bank.withdrawItem("2440", 0.95);
            Bank.withdrawItem("2436", 0.95);
            Bank.withdrawItem("2440", 0.95);

            // This should withdraw 3 super sets.
        }

        if (java.util.Objects.equals(potions, "Super combat")) {
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(() -> Bank.isSelectedQuantity5Button(), 250, 12);
            }
            Bank.withdrawItem("12695", 0.8);
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }
            Bank.withdrawItem("12695", 0.8);

            // This should withdraw 6 super combat potions.
        }

        if (java.util.Objects.equals(potions, "Super strength only")) {
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(() -> Bank.isSelectedQuantity5Button(), 250, 12);
            }
            Bank.withdrawItem("2440", 0.95);
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }
            Bank.withdrawItem("2440", 0.95);

            // This should withdraw 6 super strength potions.
        }

        // If using prayer, fill the rest of the inventory with prayer
        if (java.util.Objects.equals(NMZMethod, "Prayer")) {
            if (!Bank.isSelectedQuantity10Button()) {
                Bank.tapQuantity10Button();
                Condition.wait(() -> Bank.isSelectedQuantity10Button(), 250, 12);
            }
            Bank.withdrawItem("2434", 0.95);
            Bank.withdrawItem("2434", 0.95);
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }
            Bank.withdrawItem("2434", 0.95);
            Bank.withdrawItem("2434", 0.95);

            // This should withdraw 22 prayer potions.
        }
    }

    private void restockNMZPotionsCHEST() {
        Logger.log("Restocking NMZ potions.");
        // Check if we are at the reward chest, if not move there.
        if (!Player.atTile(rewardChestTile)) {
            Logger.debugLog("We are not yet at the NMZ Reward chest, moving there!");
            Walker.step(rewardChestTile);
            Condition.wait(() -> Player.atTile(rewardChestTile), 250, 20);

            if (!Player.atTile(rewardChestTile)) {
                Logger.debugLog("We are still not at the NMZ reward chest, retrying...");
                Walker.step(rewardChestTile);
                Condition.wait(() -> Player.atTile(rewardChestTile), 250, 20);

                if (!Player.atTile(rewardChestTile)) {
                    Logger.log("Failed to restock, stopping script!");
                    Logout.logout();
                    Script.stop();
                }
            }
        }

        Client.tap(446,225);
        Condition.sleep(1250);

        // Check if bank pin is needed, if so enter it
        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
            Condition.sleep(750);
        }

        // Go to the benefits tab
        Client.tap(new Rectangle(308, 234, 50, 11));
        Condition.sleep(1000);

        // Restock absorptions
        if (java.util.Objects.equals(NMZMethod, "Absorption")) {
            Client.longPress(340, 361);
            Condition.sleep(generateDelay(500, 750));
            Client.tap(340, 476);
            Condition.sleep(generateDelay(750, 1250));
            Client.sendKeystroke("KEYCODE_8");
            Client.sendKeystroke("KEYCODE_4");
            Client.sendKeystroke("KEYCODE_ENTER");
            Condition.sleep(generateDelay(300, 600));
        }

        // Restock overloads
        if (java.util.Objects.equals(potions, "Overload")) {
            Client.longPress(274, 361);
            Condition.sleep(generateDelay(500, 750));
            Client.tap(274, 476);
            Condition.sleep(generateDelay(750, 1250));
            Client.sendKeystroke("KEYCODE_2");
            Client.sendKeystroke("KEYCODE_4");
            Client.sendKeystroke("KEYCODE_ENTER");
            Condition.sleep(generateDelay(300, 600));
        }

        // Close the interface again
        Client.tap(new Rectangle(565, 205, 14, 13));
        Condition.sleep(generateDelay(500, 750));
    }

    private void restockNMZPotionsBARREL() {

        if (java.util.Objects.equals(potions, "Overload")) {
            if (!Player.atTile(overloadBarrelTile)) {
                Logger.debugLog("Player not at overload barrel, moving there!");
                Walker.step(overloadBarrelTile);
                Condition.wait(() -> Player.atTile(overloadBarrelTile), 250, 20);

                // failsafe
                if (!Player.atTile(overloadBarrelTile)) {
                    Logger.debugLog("Player is still not at the overload barrel, retrying...");
                    Walker.step(overloadBarrelTile);
                    Condition.wait(() -> Player.atTile(overloadBarrelTile), 250, 20);

                    if (!Player.atTile(overloadBarrelTile)) {
                        Logger.debugLog("Both attempts to move to the overload barrel have failed. Moving on!");
                    }
                }
            }

            if (Player.atTile(overloadBarrelTile)) {
                if (Inventory.containsAny(new int[]{11733, 11732, 11731, 11730}, 0.9)) {
                    storeBarrel();
                }
                takeBarrel("Overload");
            }
        }

        if (java.util.Objects.equals(NMZMethod, "Absorption")) {
            Logger.debugLog("Restocking absorptions");

            if (!Player.atTile(absorptionBarrelTile)) {
                Logger.debugLog("Player not at absorption barrel, moving there!");
                Walker.step(absorptionBarrelTile);
                Condition.wait(() -> Player.atTile(absorptionBarrelTile), 250, 20);

                // failsafe
                if (!Player.atTile(absorptionBarrelTile)) {
                    Logger.debugLog("Player is still not at the absorption barrel, retrying...");
                    Walker.step(absorptionBarrelTile);
                    Condition.wait(() -> Player.atTile(absorptionBarrelTile), 250, 20);

                    if (!Player.atTile(absorptionBarrelTile)) {
                        Logger.debugLog("Both attempts to move to the absorption barrel have failed. Moving on!");
                    }
                }
            }

            if (Player.atTile(absorptionBarrelTile)) {
                if (Inventory.containsAny(new int[]{11737, 11736, 11735, 11734}, 0.9)) {
                    storeBarrel();
                }
                takeBarrel("Absorption");
            }
        }
    }

    private void storeBarrel() {
        Client.longPress(397,256);
        Condition.sleep(generateDelay(500,800));
        Client.tap(397,342);
        Condition.sleep(generateDelay(1000,1400));

        // Check if we have a text box option, if not it means we had no overloads/absorptions
        java.awt.Rectangle foundObjects = Objects.getNearest("/images/YesPlease.png");

        // Based on outcome, tap or not
        if (foundObjects != null && !foundObjects.isEmpty()) {
            Client.sendKeystroke("KEYCODE_1");
            Condition.sleep(generateDelay(500,800));
        }

        // Failsafe in case tap hasn't registered
        java.awt.Rectangle foundObjects2 = Objects.getNearest("/images/YesPlease.png");

        // Based on outcome, tap or not
        if (foundObjects2 != null && !foundObjects2.isEmpty()) {
            Client.sendKeystroke("KEYCODE_1");
            Condition.sleep(generateDelay(500,800));
        }
    }

    private void takeBarrel(String potion) {
        if (java.util.Objects.equals(potion, "Absorption")) {
            Client.longPress(397,256);
            Condition.sleep(generateDelay(500,800));
            Client.tap(397,319);
            Condition.sleep(generateDelay(800,1200));
            Client.sendKeystroke("KEYCODE_8");
            Client.sendKeystroke("KEYCODE_4");
            Client.sendKeystroke("KEYCODE_ENTER");
            Condition.sleep(generateDelay(300, 600));
        }

        if (java.util.Objects.equals(potion, "Overload")) {
            Client.longPress(397,256);
            Condition.sleep(generateDelay(500,800));
            Client.tap(397,319);
            Condition.sleep(generateDelay(800,1200));
            Client.sendKeystroke("KEYCODE_2");
            Client.sendKeystroke("KEYCODE_4");
            Client.sendKeystroke("KEYCODE_ENTER");
            Condition.sleep(generateDelay(300, 600));
        }
    }

    private int generateDelay(int lowerEnd, int higherEnd) {
        if (lowerEnd > higherEnd) {
            // Swap lowerEnd and higherEnd if lowerEnd is greater
            int temp = lowerEnd;
            lowerEnd = higherEnd;
            higherEnd = temp;
        }
        return random.nextInt(higherEnd - lowerEnd + 1) + lowerEnd;
    }

    private void moveToBarrel(String barrel) {
        if (java.util.Objects.equals(barrel, "Overload")) {
            if (!Player.atTile(overloadBarrelTile)) {
                Logger.debugLog("Moving towards the overload barrel.");
                Walker.step(overloadBarrelTile);
                Condition.wait(() -> Player.atTile(overloadBarrelTile), 250,20);

                if (!Player.atTile(overloadBarrelTile)) {
                    Logger.debugLog("Failed to move towards the overload barrel, retrying...");
                    Walker.step(overloadBarrelTile);
                    Condition.wait(() -> Player.atTile(overloadBarrelTile), 250,20);

                    if (!Player.atTile(overloadBarrelTile)) {
                        Logger.debugLog("Failed to move towards the overload barrel for the third time, retrying...");
                        Walker.step(overloadBarrelTile);
                        Condition.wait(() -> Player.atTile(overloadBarrelTile), 250,20);

                        if (!Player.atTile(overloadBarrelTile)) {
                            Logger.debugLog("Multiple attempts failed to move towards the overload barrel. Logging out");
                            Logout.logout();
                            Script.stop();
                        }
                    }
                }
            }
        }

        if (java.util.Objects.equals(barrel, "Absorption")) {
            if (!Player.atTile(absorptionBarrelTile)) {
                Logger.debugLog("Moving towards the absorption barrel.");
                Walker.step(absorptionBarrelTile);
                Condition.wait(() -> Player.atTile(absorptionBarrelTile), 250,20);

                if (!Player.atTile(absorptionBarrelTile)) {
                    Logger.debugLog("Failed to move towards the absorption barrel, retrying...");
                    Walker.step(absorptionBarrelTile);
                    Condition.wait(() -> Player.atTile(absorptionBarrelTile), 250,20);

                    if (!Player.atTile(absorptionBarrelTile)) {
                        Logger.debugLog("Failed to move towards the absorption barrel for the third time, retrying...");
                        Walker.step(absorptionBarrelTile);
                        Condition.wait(() -> Player.atTile(absorptionBarrelTile), 250,20);

                        if (!Player.atTile(absorptionBarrelTile)) {
                            Logger.debugLog("Multiple attempts failed to move towards the absorption barrel. Logging out");
                            Logout.logout();
                            Script.stop();
                        }
                    }
                }
            }
        }
    }

    private void moveToDominic() {

        if (!Player.atTile(dominicOnionTile)) {
            Logger.debugLog("Moving towards Dominic Onion.");
            Walker.step(dominicOnionTile);
            Condition.wait(() -> Player.atTile(dominicOnionTile), 250,20);

            if (!Player.atTile(dominicOnionTile)) {
                Logger.debugLog("Failed to move towards Dominic Onion, retrying...");
                Walker.step(dominicOnionTile);
                Condition.wait(() -> Player.atTile(dominicOnionTile), 250,20);

                if (!Player.atTile(dominicOnionTile)) {
                    Logger.debugLog("Failed to move towards Dominic Onion for the fourth time, retrying...");
                    Walker.step(dominicOnionTile);
                    Condition.wait(() -> Player.atTile(dominicOnionTile), 250,20);

                    if (!Player.atTile(dominicOnionTile)) {
                        Logger.debugLog("Failed to move towards Dominic Onion for the fifth time, retrying...");
                        Walker.step(dominicOnionTile);
                        Condition.wait(() -> Player.atTile(dominicOnionTile), 250,20);

                        if (!Player.atTile(dominicOnionTile)) {
                            Logger.debugLog("Multiple attempts failed to move towards Dominic Onion. Logging out");
                            Logout.logout();
                            Script.stop();
                        }
                    }
                }
            }
        }
    }

    private void takeBreak() {
        // Generate random break time between 1 and 6 minutes
        int breakMinutes = new Random().nextInt(6) + 1;  // Generates a number from 1 to 6

        // Randomize the seconds from 0 to 59
        int breakSeconds = new Random().nextInt(60);
        int breakMillis = breakMinutes * 60000 + breakSeconds * 1000;  // Convert minutes and seconds to milliseconds

        // Log the precise break duration
        Logger.log("Taking a break for " + breakMinutes + " minute(s) and " + breakSeconds + " second(s).");

        // Breaking logic
        Logout.logout();
        Condition.sleep(breakMillis);  // Sleep for the calculated duration

        lastBreakTime = System.currentTimeMillis();  // Resetting the last break time after the break
        Logger.log("Break over, resuming script.");
        if (hopEnabled) {
            hopActions();  // Perform world switching if enabled
        }
        Login.login();

        // Ensuring the inventory tab is open
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        insideNMZ = false;
    }

    private void startNMZDream() {
        // Before proceeding, check if we have the necessary items, if not, stop the script and log out.
        Logger.log("Checking our supplies before starting our NMZ dream.");
        if (java.util.Objects.equals(NMZMethod, "Absorption")) {
            if (java.util.Objects.equals(HPMethod, "Rock cake")) {
                if (!Inventory.contains(7510, 0.9)) {
                    Logger.log("We don't have our rock cake, stopping script!");
                    Logout.logout();
                    Script.stop();
                } else {
                    Logger.debugLog("The rock cake is present in our inventory.");
                }
            } else if (java.util.Objects.equals(HPMethod, "Locator orb")) {
                if (!Inventory.contains(22081, 0.9)) {
                    Logger.log("We don't have our Locator orb, stopping script!");
                    Logout.logout();
                    Script.stop();
                } else {
                    Logger.debugLog("The Locator is present in our inventory.");
                }
            }
        }

        if (java.util.Objects.equals(NMZMethod, "Prayer")) {
            if (!Inventory.contains(2434, 0.9)) {
                Logger.log("We don't have any prayer potions, stopping script!");
                Logout.logout();
                Script.stop();
            } else {
                Logger.debugLog("The Prayer potions are present in our inventory.");
            }
        } else if (java.util.Objects.equals(NMZMethod, "Absorption")) {
            if (!Inventory.contains(11734, 0.9)) {
                Logger.log("We don't have any Absorption potions, stopping script!");
                Logout.logout();
                Script.stop();
            } else {
                Logger.debugLog("The Absorption potions are present in our inventory.");
            }
        }
        if (java.util.Objects.equals(potions, "Divine super combat")) {
            if (!Inventory.contains(23685, 0.9)) {
                Logger.log("We don't have any Divine super combat potions, stopping script!");
                Logout.logout();
                Script.stop();
            } else {
                Logger.debugLog("The Divine super combat potions are present in our inventory.");
            }
        } else if (java.util.Objects.equals(potions, "Divine ranging")) {
            if (!Inventory.contains(23733, 0.9)) {
                Logger.log("We don't have any Divine ranging potions, stopping script!");
                Logout.logout();
                Script.stop();
            } else {
                Logger.debugLog("The Divine ranging potions are present in our inventory.");
            }
        } else if (java.util.Objects.equals(potions, "Overload")) {
            if (!Inventory.contains(11730, 0.9)) {
                Logger.log("We don't have any Overload potions, stopping script!");
                Logout.logout();
                Script.stop();
            } else {
                Logger.debugLog("The Overload potions are present in our inventory.");
            }
        } else if (java.util.Objects.equals(potions, "Ranging")) {
            if (!Inventory.contains(2444, 0.9)) {
                Logger.log("We don't have any ranging potions, stopping script!");
                Logout.logout();
                Script.stop();
            } else {
                Logger.debugLog("The Ranging potions are present in our inventory.");
            }
        } else if (java.util.Objects.equals(potions, "Supper att/str combo")) {
            if (!Inventory.containsAll(new int[]{2436, 2440}, 0.9)) {
                Logger.log("We don't have any super sets, stopping script!");
                Logout.logout();
                Script.stop();
            } else {
                Logger.debugLog("The Super sets are present in our inventory.");
            }
        } else if (java.util.Objects.equals(potions, "Super combat")) {
            if (!Inventory.contains(12695, 0.9)) {
                Logger.log("We don't have any Super combat potions, stopping script!");
                Logout.logout();
                Script.stop();
            } else {
                Logger.debugLog("The Super combat potions are present in our inventory.");
            }
        } else if (java.util.Objects.equals(potions, "Super strength only")) {
            if (!Inventory.contains(2440, 0.9)) {
                Logger.log("We don't have any Super strength potions, stopping script!");
                Logout.logout();
                Script.stop();
            } else {
                Logger.debugLog("The Super strength potions are present in our inventory.");
            }
        }

        // Tap the NPC
        Client.tap(404,255);
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_SPACE");
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_4");
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_SPACE");
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_SPACE");
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_3");
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_4");
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_SPACE");
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_SPACE");
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_1");
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("KEYCODE_SPACE");
        Condition.sleep(generateDelay(1000,1300));
    }

    private void unlockCoffer() {
        Logger.debugLog("Unlocking NMZ coffer, as it's needed.");

        if (!Player.atTile(vialOutsideTile)) {
            Walker.step(vialOutsideTile);
            Condition.wait(() -> Player.atTile(vialOutsideTile), 250, 20);

            if (!Player.atTile(vialOutsideTile)) {
                Walker.step(vialOutsideTile);
                Condition.wait(() -> Player.atTile(vialOutsideTile), 250, 20);
            }
        }

        if (Player.atTile(vialOutsideTile)) {
            // tap the coffer
            Client.tap(537,360);
            Condition.sleep(3000);

            // Check if bankpin is needed
            if (Bank.isBankPinNeeded()) {
                Bank.enterBankPin();
            }

            // tap the vial
            Client.tap(408,161);
            Condition.sleep(4000);
        } else {
            Logger.debugLog("We're not located at the vial, something must have gone wrong.");
        }
    }

    private void enterNMZDream() {
        // Check if we are at the right tile
        if (!Player.atTile(vialOutsideTile)) {
            Walker.step(vialOutsideTile);
            Condition.wait(() -> Player.atTile(vialOutsideTile), 250, 20);

            if (!Player.atTile(vialOutsideTile)) {
                Walker.step(vialOutsideTile);
                Condition.wait(() -> Player.atTile(vialOutsideTile), 250, 20);

                if (!Player.atTile(vialOutsideTile)) {
                    Logger.debugLog("Could not path to the NMZ vial. Stopping script");
                    Logout.logout();
                    Script.stop();
                }
            }
        }

        // Tap the vial
        Client.tap(440,229);
        Condition.sleep(1500);
        // Check if the NMZ interface is open
        java.awt.Rectangle foundObjects = Objects.getNearest("/images/Accept.png");

        // Based on outcome, tap or not
        if (foundObjects != null && !foundObjects.isEmpty()) {
            // Join the NMZ Dream
            Client.tap(486,373);
            Condition.sleep(generateDelay(10000, 12000));

            // Move more to the center
            Client.tap(829,68);

            // Lower a bit of HP if using rock cakes
            if (java.util.Objects.equals(HPMethod, "Rock cake") && java.util.Objects.equals(NMZMethod, "Absorption")) {
                int initialHP = Player.getHP();  // Get the initial HP once at the beginning

                if (initialHP > 50) {  // Only operate if HP is above 50 to safely use rock cakes
                    int targetHP = 51;  // Set the minimum HP target just above the safe limit for Overloads
                    int guzzlesNeeded = calculateGuzzlesNeededToTarget(initialHP, targetHP);

                    for (int i = 0; i < guzzlesNeeded; i++) {
                        handleLowerHPItem();
                        Condition.sleep(generateDelay(500, 700)); // Short delay between uses
                    }
                }
            }

            if (java.util.Objects.equals(potions, "Overload")) {
                // Drink the overload potion
                Inventory.eat(11730, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
                // Wait for the effects of the overload to apply
                Condition.sleep(7000);
            }

            // Drink the non-overload potion if needed
            if (java.util.Objects.equals(potions, "Divine super combat")) {
                Inventory.eat(23685, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (java.util.Objects.equals(potions, "Divine ranging")) {
                Inventory.eat(23733, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (java.util.Objects.equals(potions, "Ranging")) {
                Inventory.eat(2444, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (java.util.Objects.equals(potions, "Super combat")) {
                Inventory.eat(12695, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (java.util.Objects.equals(potions, "Super strength only")) {
                Inventory.eat(2440, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (java.util.Objects.equals(potions, "Super att/str combo")) {
                Inventory.eat(2440, 0.9);
                Condition.sleep(generateDelay(1150,1500));
                Inventory.eat(2436, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            }

            // Stock up on absorption level if needed
            if (java.util.Objects.equals(NMZMethod, "Absorption")) {
                // Tap a total of 19 times on an absorption pot

                // 2 times on 4 dosed potions
                Inventory.eat(11734, 0.9);
                Condition.sleep(generateDelay(450,700));
                Inventory.eat(11734, 0.9);
                Condition.sleep(generateDelay(450,700));

                // 2 times on 3 dosed potions
                Inventory.eat(11735, 0.9);
                Condition.sleep(generateDelay(450,700));
                Inventory.eat(11735, 0.9);
                Condition.sleep(generateDelay(450,700));

                // 2 times on 2 dosed potions
                Inventory.eat(11736, 0.9);
                Condition.sleep(generateDelay(450,700));
                Inventory.eat(11736, 0.9);
                Condition.sleep(generateDelay(450,700));

                // 2 times on 1 dosed potions
                Inventory.eat(11737, 0.9);
                Condition.sleep(generateDelay(450,700));
                Inventory.eat(11737, 0.9);
                Condition.sleep(generateDelay(450,700));
            }

            // If using Absorption and Overloads, we need to drink Overloads and then manage HP using either Rock Cake or Locator Orb.
            if (java.util.Objects.equals(potions, "Overload") && java.util.Objects.equals(NMZMethod, "Absorption")) {
                int initialHP = Player.getHP();  // Get the initial HP once at the beginning

                if (java.util.Objects.equals(HPMethod, "Rock cake")) {
                    // Use the rock cake to reduce HP to 1, calculated number of times based on initial HP
                    int tapsNeeded = Math.max(0, initialHP - 1);  // Calculate how many times to tap based on initial HP
                    for (int i = 0; i < tapsNeeded; i++) {
                        Inventory.tapItem(7510, true, 0.9);
                        Condition.sleep(generateDelay(350, 550)); // Short delay between uses
                    }
                } else if (java.util.Objects.equals(HPMethod, "Locator orb")) {
                    // Use the locator orb to reduce HP to 1, calculated number of times based on initial HP
                    int tapsNeeded = (initialHP - 1) / 10;  // Each tap with the locator orb reduces HP by 10
                    if ((initialHP - 1) % 10 != 0) tapsNeeded++;  // Ensure HP reaches 1 if not perfectly divisible by 10
                    for (int i = 0; i < tapsNeeded; i++) {
                        Inventory.tapItem(22081, true, 0.9);
                        Condition.sleep(generateDelay(350, 550)); // Short delay between uses
                    }
                }
            }

            // If Absorption is used without Overloads, manage HP using either Rock Cake or Locator Orb
            if (!java.util.Objects.equals(potions, "Overload") && java.util.Objects.equals(NMZMethod, "Absorption")) {
                if (java.util.Objects.equals(HPMethod, "Rock cake")) {
                    // Use the rock cake to reduce HP to 1, max 100 times
                    for (int i = 0; i < 100; i++) {
                        int HP = Player.getHP();
                        if (HP > 1) {
                            handleLowerHPItem();
                            Condition.sleep(generateDelay(400, 700)); // Short delay between uses
                        } else {
                            break;  // Stop if HP is at 1
                        }
                    }
                } else if (java.util.Objects.equals(HPMethod, "Locator orb")) {
                    // Use the locator orb to reduce HP to 1, max 15 times
                    for (int i = 0; i < 15; i++) {
                        int HP = Player.getHP();
                        if (HP > 1) {
                            Inventory.tapItem(22081, true, 0.9);
                            Condition.sleep(generateDelay(200, 400)); // Short delay between uses
                        } else {
                            break;  // Stop if HP is at 1
                        }
                    }
                }
            }

            // Enable prayer if using the prayer method
            if (java.util.Objects.equals(NMZMethod, "Prayer")) {
                // Check for the tab, if not open, open it
                GameTabs.openPrayerTab();

                Prayer.activateProtectfromMelee();
                Condition.wait(() -> Prayer.isActiveProtectfromMelee(), 250, 20);
                if (!Prayer.isActiveProtectfromMelee()) {
                    Prayer.activateProtectfromMelee();
                    Condition.wait(() -> Prayer.isActiveProtectfromMelee(), 250, 10);
                    if (!Prayer.isActiveProtectfromMelee()) {
                        Prayer.activateProtectfromMelee();
                        Condition.wait(() -> Prayer.isActiveProtectfromMelee(), 250, 10);
                    } if (!Prayer.isActiveProtectfromMelee()) {
                        Logger.debugLog("Failed to activate protect from melee.");
                    }
                }

                GameTabs.openInventoryTab();

                if (!GameTabs.isInventoryTabOpen()) {
                    GameTabs.openInventoryTab();
                }
            }

            insideNMZ = true;
            readXP();

        } else {
            // Assuming the coffer is locked
            unlockCoffer();
            enterNMZDream();
        }
    }

    private void lowerHP() {
        int initialHP = Player.getHP(); // Read HP once at the start
        if (initialHP <= 1) {
            // If current HP is 1 or less, do nothing
            return;
        }

        if (java.util.Objects.equals(HPMethod, "Rock cake")) {
            while (Player.getHP() > 1) {
                handleLowerHPItem(); // Simulate the rock cake guzzle
                Condition.sleep(generateDelay(200, 400)); // Short delay between uses
            }
        } else if (java.util.Objects.equals(HPMethod, "Locator orb")) {
            while (Player.getHP() > 1) {
                Inventory.tapItem(22081, true, 0.9);
                Condition.sleep(generateDelay(200, 400)); // Short delay between uses
            }
        } else {
            Logger.log("Invalid HP reduction method specified.");
        }
    }

    private void drinkOffensivePot() {
        // This is going to be a giant mess of if else statements...

        // Divine super combat
        if (java.util.Objects.equals(potions, "Divine super combat")) {
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
                Logger.debugLog("We've ran out of Divine super combat potions, leaving NMZ instance.");
                leaveNMZ();
            }
        }

        // Divine ranging
        if (java.util.Objects.equals(potions, "Divine ranging")) {
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
                Logger.debugLog("We've ran out of Divine ranging potions, leaving NMZ instance.");
                leaveNMZ();
            }
        }

        // Overload
        if (java.util.Objects.equals(potions, "Overload")) {
            if (Inventory.contains(11733, 0.9)) {
                Inventory.eat(11733, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (Inventory.contains(11732, 0.9)) {
                Inventory.eat(11732, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (Inventory.contains(11731, 0.9)) {
                Inventory.eat(11731, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (Inventory.contains(11730, 0.9)) {
                Inventory.eat(11730, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else {
                Logger.debugLog("We've ran out of Overload potions, leaving NMZ instance.");
                leaveNMZ();
            }

            Condition.sleep(9000);
        }

        // Ranging
        if (java.util.Objects.equals(potions, "Ranging")) {
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
                Logger.debugLog("We've ran out of Ranging potions, leaving NMZ instance.");
                leaveNMZ();
            }
        }

        // Super attack and strength combination
        if (java.util.Objects.equals(potions, "Super att/str combo")) {
            if (Inventory.containsAll(new int[]{149, 161}, 0.9)) {
                Inventory.eat(149, 0.9);
                Condition.sleep(generateDelay(1750,2500));
                Inventory.eat(161, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (Inventory.containsAll(new int[]{147, 159}, 0.9)) {
                Inventory.eat(147, 0.9);
                Condition.sleep(generateDelay(1750,2500));
                Inventory.eat(159, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (Inventory.containsAll(new int[]{145, 157}, 0.9)) {
                Inventory.eat(145, 0.9);
                Condition.sleep(generateDelay(1750,2500));
                Inventory.eat(157, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else if (Inventory.containsAll(new int[]{2436, 2440}, 0.9)) {
                Inventory.eat(2436, 0.9);
                Condition.sleep(generateDelay(1750,2500));
                Inventory.eat(2440, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
            } else {
                Logger.debugLog("We've ran out of super sets, leaving NMZ instance.");
                leaveNMZ();
            }
        }

        // Super combat
        if (java.util.Objects.equals(potions, "Super combat")) {
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
                Logger.debugLog("We've ran out of Super combat potions, leaving NMZ instance.");
                leaveNMZ();
            }
        }

        // Super strength only
        if (java.util.Objects.equals(potions, "Super strength only")) {
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
                Logger.debugLog("We've ran out of Super strength potions, leaving NMZ instance.");
                leaveNMZ();
            }
        }


    }

    private void drinkAbsorptions() {
        boolean keepDrinking = true;
        int drinkCount = 0;  // Counter for the number of drinks

        // Read the last line from the chat box to determine if "any", "more", or "moment" is mentioned
        String currentChat = Chatbox.readLastLine(new Rectangle(35, 36, 490, 86));
        if (currentChat.contains("any") || currentChat.contains("more") || currentChat.contains("moment")) {
            keepDrinking = false;  // Prevent the loop from running if we are already topped up.
        }

        while (keepDrinking && drinkCount < 10) {  // Check both the boolean and if we've drunk less than 10 times
            if (Inventory.contains(11737, 0.9)) {
                Inventory.eat(11737, 0.9);
            } else if (Inventory.contains(11736, 0.9)) {
                Inventory.eat(11736, 0.9);
            } else if (Inventory.contains(11735, 0.9)) {
                Inventory.eat(11735, 0.9);
            } else if (Inventory.contains(11734, 0.9)) {
                Inventory.eat(11734, 0.9);
            } else {
                Logger.debugLog("No absorption potions found in inventory, leaving the instance!");
                leaveNMZ();
                break;  // Exit the while loop if no potions are found
            }

            drinkCount++;  // Increment the drink counter after a drink attempt

            // Read the last line from the chat box again to determine if "any", "more", or "moment" is mentioned
            String OCRresults = Chatbox.readLastLine(new Rectangle(35, 36, 490, 86));
            if (OCRresults.contains("any") || OCRresults.contains("more") || OCRresults.contains("moment")) {
                keepDrinking = false;  // Stop the loop if any specified words are found
            }

            Condition.sleep(generateDelay(200,500));  // Short delay to prevent too rapid consumption checks
        }
    }

    public void handleLowerHPItem() {
        // Create a Random object for generating random numbers
        Random random = new Random();

        // Check if the lowerHPItem point is not equal to the default value (69, 69)
        if (!lowerHPItem.equals(new Point(69, 69))) {
            // Randomize x and y within specified ranges
            int randomX = lowerHPItem.x + random.nextInt(11) - 5;  // Range: -5 to +5
            int randomY = lowerHPItem.y + random.nextInt(5) - 2;   // Range: -2 to +2

            Client.longPress(randomX, randomY);
            generateDelay(800, 1200);
            Client.tap(randomX, randomY + 63);
        } else {
            // Since lowerHPItem is still at the default value, find the new center point for the item with ID 7510
            Point newItemPoint = Inventory.getItemCenterPoint(7510, 0.9);
            if (newItemPoint != null && !newItemPoint.equals(new Point(69, 69))) {
                // Successfully found a new point
                lowerHPItem = newItemPoint;
                Logger.debugLog("New point for lowering HP is set to: " + lowerHPItem.x + ", " + lowerHPItem.y);

                // Randomize x and y within specified ranges for the new point
                int randomX = lowerHPItem.x + random.nextInt(11) - 5;  // Range: -5 to +5
                int randomY = lowerHPItem.y + random.nextInt(5) - 2;   // Range: -2 to +2

                Client.longPress(randomX, randomY);
                generateDelay(800, 1200);
                Client.tap(randomX, randomY + 63);
            } else {
                Logger.debugLog("Failed to locate the item with ID 7510. No update made.");
            }
        }
    }

    private void drinkPrayer() {
        if (Inventory.contains(143, 0.96)) {
            Inventory.eat(143, 0.96);
            Condition.sleep(generateDelay(2000, 3000));
            Logger.debugLog("Drinking from a 1 dosed prayer potion.");
        } else if (Inventory.contains(141, 0.96)) {
            Inventory.eat(141, 0.96);
            Condition.sleep(generateDelay(2000, 3000));
            Logger.debugLog("Drinking from a 2 dosed prayer potion.");
        } else if (Inventory.contains(139, 0.96)) {
            Inventory.eat(139, 0.96);
            Condition.sleep(generateDelay(2000, 3000));
            Logger.debugLog("Drinking from a 1 dosed prayer potion.");
        } else if (Inventory.contains(2434, 0.96)) {
            Inventory.eat(2434, 0.96);
            Condition.sleep(generateDelay(2000, 3000));
            Logger.debugLog("Drinking from a 4 dosed prayer potion.");
        } else {
            Logger.debugLog("No prayer potions found in inventory, leaving the instance!");
            leaveNMZ();
        }
    }

    private void leaveNMZ() {
        Client.tap(776,99);
        Condition.sleep(generateDelay(4000,6000));
        Walker.step(vialInsideTile);
        Condition.wait(() -> Player.atTile(vialInsideTile), 250, 30);

        if (!Player.atTile(vialInsideTile)) {
            Walker.step(vialInsideTile);
            Condition.wait(() -> Player.atTile(vialInsideTile), 250, 30);
        }

        Client.tap(vialInside);
        Condition.wait(() -> Player.atTile(respawnTile), 250, 50);

        insideNMZ = false;
        lowerHPItem = new Point(69,69);
        justLeftInstance = true;
        Logger.log("Left NMZ arena.");
    }

    // Method to get random target HP with controlled probabilities
    private int getRandomTargetHP() {
        int decision = new Random().nextInt(10);  // Generate a random number between 0 and 9
        int target = decision < 8 ? 2 : 3;  // 80% chance for 2, 20% chance for 3
        Logger.debugLog("Randomly chosen targetHPForAction (80-20 rule): " + target);
        return target;
    }

    // Helper method to calculate the number of guzzles needed with a rock cake to reduce HP to 1
    private int calculateGuzzlesNeeded(int hp) {
        int guzzles = 0;
        while (hp > 1) {
            hp -= (int) (hp * 0.1) + 1; // Guzzling depletes 10% of current hitpoints, rounded down, plus one
            guzzles++;
        }
        return guzzles;
    }

    // Helper method to calculate the number of guzzles needed to reach a target HP with a rock cake
    private int calculateGuzzlesNeededToTarget(int currentHP, int targetHP) {
        int guzzles = 0;
        while (currentHP > targetHP) {
            int damage = (int) (currentHP * 0.1) + 1;  // Guzzling depletes 10% of current HP, rounded down, plus one
            if (currentHP - damage < targetHP) {
                break;  // Stop if the next guzzle would reduce HP below the target
            }
            currentHP -= damage;
            guzzles++;
        }
        return guzzles;
    }

    private void hopActions() {
        if (hopEnabled) {
            Logger.debugLog("Switching to a different world.");
            Game.switchWorld(hopProfile);
            Logger.log("Switched to a different world.");
        }
    }

    private void readXP() {
        XpBar.getXP();
    }

}