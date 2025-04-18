package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.Task;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dNMZ",
        description = "Slays all the nightmare monsters in Gielinor on auto pilot. Automatically restocks on potions, supports all styles.",
        version = "2.03",
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
                                @AllowedValue(optionIcon = "2434", optionName = "Prayer"),
                                @AllowedValue(optionIcon = "2434", optionName = "Prayer - Ultimate Strength"),
                                @AllowedValue(optionIcon = "2434", optionName = "Prayer - Eagle Eye"),
                                @AllowedValue(optionIcon = "2434", optionName = "Prayer - Mystic Might"),
                                @AllowedValue(optionIcon = "2434", optionName = "Prayer - Chivalry"),
                                @AllowedValue(optionIcon = "2434", optionName = "Prayer - Piety"),
                                @AllowedValue(optionIcon = "2434", optionName = "Prayer - Rigour"),
                                @AllowedValue(optionIcon = "2434", optionName = "Prayer - Augury")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "HP Drop Method",
                        description = "Which item would you like to use to drop your HP?",
                        defaultValue = "None",
                        allowedValues = {
                                @AllowedValue(optionName = "None"),
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
                        name =  "World hopping",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name = "Run anti-ban",
                        description = "Would you like to run anti-ban features?",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class dNMZ extends AbstractScript {
    public static Random random = new Random();
    // Creating the strings for later use

    // Strings
    public static String hopProfile;
    public static String potions;
    public static String HPMethod;
    public static String NMZMethod;

    // booleans
    public static boolean hopEnabled;
    public static boolean useWDH;
    public static boolean bankYN;
    public static boolean insideNMZ;
    public static boolean usingVenatorBow = false;
    public static boolean setupDone = false;
    public static boolean restockDone = false;
    public static boolean absorbMESDone = false;
    public static boolean overloadMESDone = false;
    public static boolean dominicMESDone = false;
    public static boolean rockcakeMESDone = false;
    public static boolean dreamStarted = false;
    private boolean antiBan;

    // Lists
    public static  List<Integer> notifiedTimes = new ArrayList<>();

    // Integers
    public static int banktab;
    public static int absorptionPotionInterval;
    public static int offensivePotionInterval = 302000;
    public static int targetHPForAction = 0;
    public static int currentHP;
    public static int currentPrayerPoints;
    public static int timeUntilNextDrinkOffensive;

    // Longs
    public static long lastOffensivePotionTime;
    public static long lastAbsorptionPotionTime;
    public static long nextQuickPrayerFlickTime = 0;
    public static long lastTimeHPWasTwo = 0;
    private static long lastNMZCheckTimestamp = 0;
    public static long currentTime = System.currentTimeMillis();
    public static long timeSinceLastDrink;
    public static long timeSinceLastDrinkOffensive;

    // Tiles
    public static Tile playerPosition;
    public static Tile bankTile = new Tile(10451, 12125, 0);
    public static Tile rewardChestTile = new Tile(10435, 12221, 0);
    public static Tile vialOutsideTile = new Tile(10419, 12213, 0);
    public static Tile absorptionBarrelTile = new Tile(10403, 12217, 0);
    public static Tile overloadBarrelTile = new Tile(10403, 12213, 0);
    public static Tile vialInsideTile = new Tile(9103, 18469, 0);
    public static Tile dominicOnionTile = new Tile(10431, 12209, 0);
    public static Tile respawnTile = new Tile(10431, 12209, 0);

    // Colors
    public static Color absorbPotColor = Color.decode("#aeb7bc");
    public static Color prayerPotColor = Color.decode("#3ecf99");
    public static Color overloadPotColor = Color.decode("#0b0808");
    public static Color supercombatPotColor = Color.decode("#115c06");
    public static Color divinesupercombatPotColor = Color.decode("#135f06");
    public static Color divinerangingPotColor = Color.decode("#50add0");
    public static Color rangingPotColor = Color.decode("#3aa6cf");
    public static Color superstrengthPotColor = Color.decode("#cfcdcd");
    public static Color superattackPotColor = Color.decode("#3e41cf");

    // Areas
    public static Area NMZAreaOutside = new Area(
            new Tile(10384, 12235, 0),
            new Tile(10459, 12175, 0)
    );
    public static Area NMZArena = new Area(
            new Tile(8975, 18625, 0),
            new Tile(9194, 18435, 0)
    );
    public static Area scriptArea = new Area(
            new Tile(10255, 12251, 0),
            new Tile(10567, 11969, 0)
    );
    public static Area bankArea = new Area(
            new Tile(10425, 12144, 0),
            new Tile(10480, 12087, 0)
    );
    // Paths
    public static Tile[] pathToBank = new Tile[] {
            new Tile(10432, 12207, 0),
            new Tile(10451, 12185, 0),
            new Tile(10444, 12153, 0),
            new Tile(10457, 12127, 0)
    };
    public static Tile[] pathToNMZ = new Tile[] {
            new Tile(10451, 12153, 0),
            new Tile(10445, 12190, 0),
            new Tile(10432, 12204, 0)
    };

    // Rectangles
    public static Rectangle tapRewardChestRect = new Rectangle(443, 216, 27, 33);
    public static Rectangle rewardChestBenefitsRect = new Rectangle(293, 218, 63, 12);
    public static Rectangle vialInside = new Rectangle(438, 307, 19, 20);
    public static Rectangle quickPrayers = new Rectangle(687, 85, 16, 15);
    public static Rectangle absorptionCountRect = new Rectangle(74, 5, 70, 107);
    public static Rectangle ovlStockCountRect = new Rectangle(247, 367, 40, 22);
    public static Rectangle absorbStockCountRect = new Rectangle(310, 366, 44, 25);

    // Color find digit / ocr maps
    public static final ConcurrentHashMap<String, int[][]> absorptionDigitPatterns = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, int[][]> potionstockDigitPatterns = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, int[][]> dominicChatPatterns = new ConcurrentHashMap<>();

    // Points
    public static Point lowerHPItem = new Point(69,69);

    public static List<Color> absorbTextColors = Arrays.asList(
            Color.decode("#ff5555"),
            Color.decode("#ffaaaa"),
            Color.decode("#ffffff"),
            Color.decode("#ff5d5d")
    );
    public static List<Color> potionstockTextColors = List.of(
            Color.decode("#ffffff")
    );
    public static List<Color> dominicChatTextColors = List.of(
            Color.decode("#000001")
    );

    // Maps
    public static Map<String, int[]> potionData = new HashMap<>();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("World hopping"));
        hopEnabled = Boolean.parseBoolean((configs.get("World hopping.enabled")));
        useWDH = Boolean.parseBoolean((configs.get("World hopping.useWDH")));
        bankYN= Boolean.parseBoolean((configs.get("Banking")));
        banktab = Integer.parseInt(configs.get("BankTab"));
        potions = (configs.get("Potions"));
        HPMethod = (configs.get("HP Drop Method"));
        NMZMethod = (configs.get("NMZ Method"));
        antiBan = Boolean.valueOf(configs.get("Run anti-ban"));

        Logger.log("Thank you for using the dNMZ script!\nSetting up everything for your gains now...");

        // Set up the map chunks we need
        MapChunk mapChunk = new MapChunk(new String[]{"40-49", "40-48", "41-49", "41-48", "40-47", "41-47", "35-72", "35-73"}, "0");

        // Set up the walker with our chunks
        Walker.setup(mapChunk);

        if (antiBan) {
            Logger.debugLog("Initializing anti-ban timer");
            Game.antiBan();
        }

        currentTime = System.currentTimeMillis();
    }

    // This is the main part of the script, poll gets looped constantly
    List<Task> nmzTasks = Arrays.asList(
            new Setup(),
            new BreakManager(),
            new doFighting(),
            new Bank(),
            new RestockPotions(),
            new StartDream()
    );

    @Override
    public void poll() {

        if (antiBan) {
            Game.antiBan();
        }

        // Check/update insideNMZ only if 5 seconds have passed since the last update
        if (System.currentTimeMillis() - lastNMZCheckTimestamp >= 5_000) {
            insideNMZ = Player.within(NMZArena);
            lastNMZCheckTimestamp = System.currentTimeMillis();
        }

        // Read XP
        readXP();

        // Close chat if open
        Chatbox.closeChatbox();

        // Open inventory tab
        GameTabs.openTab(UITabs.INVENTORY);

        //Run tasks
        for (Task task : nmzTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    private void readXP() {
        XpBar.getXP();
    }

    public static int generateRandom(int lowerEnd, int higherEnd) {
        if (lowerEnd > higherEnd) {
            // Swap lowerEnd and higherEnd if lowerEnd is greater
            int temp = lowerEnd;
            lowerEnd = higherEnd;
            higherEnd = temp;
        }
        return random.nextInt(higherEnd - lowerEnd + 1) + lowerEnd;
    }

    public static void leaveNMZ() {
        // UPDATE THIS TO USE THE NEW WALKER INSTEAD
        if (usingVenatorBow) {
            Client.tap(823,147);
        } else {
            Client.tap(776,99);
        }
        // CHANGE THIS TO CONDITIONAL WAIT
        Condition.sleep(4000, 6000);
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
        Logger.log("Left NMZ arena.");
    }

    public static void toggleQuickPrayer() {
        Client.tap(quickPrayers);
        Condition.sleep(100, 150);
        Client.tap(quickPrayers);
    }

    public static boolean enableMES() {
        Rectangle mesEnabled;
        Rectangle mesDisabled;

        mesEnabled = Objects.getBestMatch("/imgs/enabled-mes.png", 0.8);

        if (mesEnabled != null) {
            Logger.debugLog("Found the MES enabled option, Menu Entry Swapper menu is enabled!");
            return true;
        } else {
            Logger.debugLog("Could not find the MES enabled option, trying to find the disabled option...");
            mesDisabled = Objects.getBestMatch("/imgs/disabled-mes.png", 0.8);
        }

        if (mesDisabled != null) {
            Logger.debugLog("Found the MES disabled option, enabling MES!");
            Client.tap(mesDisabled);
            Condition.sleep(1500);
            return true;
        } else {
            Logger.debugLog("Failed to find the MES disabled option!");
        }

        Logger.log("Both MES options could not be found, stopping script, please manually set up your menu entry swapper!");
        Script.stop();
        return false;
    }

    public static boolean swapMESOptions() {
        Rectangle checkOption;
        Rectangle storeOption;
        Rectangle takeOption;
        Rectangle mesEnabled;

        Logger.debugLog("Finding the check option.");
        checkOption = Objects.getBestMatch("/imgs/check.png", 0.8);

        Logger.debugLog("Finding the store option.");
        storeOption = Objects.getBestMatch("/imgs/store.png", 0.8);

        Logger.debugLog("Finding the take option.");
        takeOption = Objects.getBestMatch("/imgs/take.png", 0.8);

        // Log the results of finding the options
        if (checkOption != null) {
            Logger.debugLog("Check option found: " + checkOption);
        } else {
            Logger.debugLog("Check option was NOT FOUND.");
        }

        if (storeOption != null) {
            Logger.debugLog("Store option found: " + storeOption);
        } else {
            Logger.debugLog("Store option was NOT FOUND.");
        }

        if (takeOption != null) {
            Logger.debugLog("Take option found: " + takeOption);
        } else {
            Logger.debugLog("Take option was NOT FOUND.");
        }

        // Ensure the take option is the topmost
        if (takeOption != null) {
            Logger.debugLog("Ensuring the take option is the topmost.");

            // Determine the current topmost option
            Rectangle topmostOption = null;

            if (checkOption != null && storeOption != null) {
                topmostOption = checkOption.y < storeOption.y ? checkOption : storeOption;
            } else if (checkOption != null) {
                topmostOption = checkOption;
            } else if (storeOption != null) {
                topmostOption = storeOption;
            }

            // Swap if the take option is not the topmost
            if (topmostOption != null && takeOption.y > topmostOption.y) {
                Logger.debugLog("Take option is not the topmost. Swapping with the current topmost option.");
                Logger.debugLog("Topmost option: " + topmostOption);
                Rectangle takeOptionShort = new Rectangle(takeOption.x, takeOption.y, 15, takeOption.height);
                Rectangle topmostOptionShort = new Rectangle(topmostOption.x, topmostOption.y, 15, topmostOption.height);
                Client.drag(takeOptionShort, topmostOptionShort, 500);
                Condition.sleep(1500);
            } else {
                Logger.debugLog("Take option is already the topmost. No action needed.");
            }
        }

        // Check if the MES option menu is enabled and disable it if necessary
        mesEnabled = Objects.getBestMatch("/imgs/enabled-mes.png", 0.8);

        if (mesEnabled != null) {
            Logger.debugLog("Disabling MES option menu.");
            Client.tap(mesEnabled);
            Condition.sleep(500, 1000);
            return true;
        }

        return false;
    }

    public static boolean isTakeTopMost() {
        Logger.debugLog("Checking if the take option is the topmost...");

        // Find the options
        Rectangle checkOption = Objects.getBestMatch("/imgs/checkcheck.png", 0.8);
        Rectangle storeOption = Objects.getBestMatch("/imgs/checkstore.png", 0.8);
        Rectangle takeOption = Objects.getBestMatch("/imgs/checktake.png", 0.8);

        // Log the options found
        if (checkOption != null) {
            Logger.debugLog("Check option found: " + checkOption);
        } else {
            Logger.debugLog("Check option was NOT FOUND.");
        }

        if (storeOption != null) {
            Logger.debugLog("Store option found: " + storeOption);
        } else {
            Logger.debugLog("Store option was NOT FOUND.");
        }

        if (takeOption != null) {
            Logger.debugLog("Take option found: " + takeOption);
        } else {
            Logger.debugLog("Take option was NOT FOUND.");
            return false; // If takeOption is not found, it cannot be the topmost
        }

        // Determine the topmost option
        Rectangle topmostOption = takeOption; // Start assuming take is topmost
        if (checkOption != null && checkOption.y < topmostOption.y) {
            topmostOption = checkOption;
        }
        if (storeOption != null && storeOption.y < topmostOption.y) {
            topmostOption = storeOption;
        }

        // Check if takeOption is the topmost
        boolean isTopMost = topmostOption == takeOption;
        if (isTopMost) {
            Logger.debugLog("The take option is already the topmost.");
        } else {
            Logger.debugLog("The take option is NOT the topmost.");
        }

        return isTopMost;
    }

    public static void tapTakeOption() {
        Logger.debugLog("Attempting to locate and tap the take option...");

        // Locate the take option
        Rectangle takeOption = Objects.getBestMatch("/imgs/checktake.png", 0.8);

        if (takeOption != null) {
            Logger.debugLog("Take option located: " + takeOption);
            Client.tap(takeOption);
            Logger.debugLog("Take option tapped successfully.");
        } else {
            Logger.debugLog("Take option was NOT FOUND.");
        }
    }

    public static void handleRockCakeMES() {
        Rectangle rockCake = Inventory.findItem(ItemList.DWARVEN_ROCK_CAKE_7510, 0.8, null);

        if (rockCake != null) {
            Client.longPress(rockCake);
            Condition.sleep(800, 1200);
        }

        if (!isGuzzleTopMost()) {
            if (enableMES()) {
                if (swapCakeMESOptions()) {
                    rockcakeMESDone = true;
                    tapCancelOption();
                }
            }
        } else {
            rockcakeMESDone = true;
            tapCancelOption();
        }
    }

    private static boolean swapCakeMESOptions() {
        Rectangle guzzleOption;
        Rectangle eatOption;
        Rectangle useOption;
        Rectangle mesEnabled;

        Logger.debugLog("Finding the Guzzle option.");
        guzzleOption = Objects.getBestMatch("/imgs/checkguzzle.png", 0.8);

        Logger.debugLog("Finding the Eat option.");
        eatOption = Objects.getBestMatch("/imgs/checkeat.png", 0.8);

        Logger.debugLog("Finding the Use option.");
        useOption = Objects.getBestMatch("/imgs/checkuse.png", 0.8);

        // Log the results of finding the options
        if (guzzleOption != null) {
            Logger.debugLog("Guzzle option found: " + guzzleOption);
        } else {
            Logger.debugLog("Guzzle option was NOT FOUND.");
        }

        if (eatOption != null) {
            Logger.debugLog("Eat option found: " + eatOption);
        } else {
            Logger.debugLog("Eat option was NOT FOUND.");
        }

        if (useOption != null) {
            Logger.debugLog("Use option found: " + useOption);
        } else {
            Logger.debugLog("Use option was NOT FOUND.");
        }

        // Ensure the Guzzle option is the topmost
        if (guzzleOption != null) {
            Logger.debugLog("Ensuring the Guzzle option is the topmost.");

            // Determine the current topmost option
            Rectangle topmostOption = guzzleOption; // Start by assuming Guzzle is topmost

            if (eatOption != null && eatOption.y < topmostOption.y) {
                topmostOption = eatOption; // Update if Eat is higher on the screen
            }
            if (useOption != null && useOption.y < topmostOption.y) {
                topmostOption = useOption; // Update if Use is higher on the screen
            }

            // Swap options if Guzzle is not the topmost
            if (topmostOption != guzzleOption) {
                Logger.debugLog("Guzzle option is not the topmost. Swapping with the current topmost option.");
                Logger.debugLog("Topmost option: " + topmostOption);
                Rectangle guzzleOptionShort = new Rectangle(guzzleOption.x, guzzleOption.y, 15, guzzleOption.height);
                Rectangle topmostOptionShort = new Rectangle(topmostOption.x, topmostOption.y, 15, topmostOption.height);
                Client.drag(guzzleOptionShort, topmostOptionShort, 500);
                Condition.sleep(1500);
            } else {
                Logger.debugLog("Guzzle option is already the topmost. No action needed.");
            }
        }

        // Check if the MES option menu is enabled and disable it if necessary
        mesEnabled = Objects.getBestMatch("/imgs/enabled-mes.png", 0.8);

        if (mesEnabled != null) {
            Logger.debugLog("Disabling MES option menu.");
            Client.tap(mesEnabled);
            Condition.sleep(500, 1000);
            return true;
        }

        return false;
    }

    private static boolean isGuzzleTopMost() {
        Logger.debugLog("Checking if the Guzzle option is the topmost...");

        // Find the options on screen
        Rectangle guzzleOption = Objects.getBestMatch("/imgs/checkguzzle.png", 0.8);
        Rectangle eatOption = Objects.getBestMatch("/imgs/checkeat.png", 0.8);
        Rectangle useOption = Objects.getBestMatch("/imgs/checkuse.png", 0.8);

        // Log the found options
        if (guzzleOption != null) {
            Logger.debugLog("Guzzle option found: " + guzzleOption);
        } else {
            Logger.debugLog("Guzzle option was NOT FOUND.");
            return false; // If guzzleOption is not found, it cannot be the topmost
        }

        if (eatOption != null) {
            Logger.debugLog("Eat option found: " + eatOption);
        } else {
            Logger.debugLog("Eat option was NOT FOUND.");
        }

        if (useOption != null) {
            Logger.debugLog("Use option found: " + useOption);
        } else {
            Logger.debugLog("Use option was NOT FOUND.");
        }

        // Determine the topmost option
        Rectangle topmostOption = guzzleOption; // Start by assuming Guzzle is topmost

        if (eatOption != null && eatOption.y < topmostOption.y) {
            topmostOption = eatOption; // Update if Eat is higher on the screen
        }
        if (useOption != null && useOption.y < topmostOption.y) {
            topmostOption = useOption; // Update if Use is higher on the screen
        }

        // Check if Guzzle is the topmost option
        boolean isTopMost = topmostOption == guzzleOption;
        if (isTopMost) {
            Logger.debugLog("The Guzzle option is the topmost.");
        } else {
            Logger.debugLog("The Guzzle option is NOT the topmost.");
        }

        return isTopMost;
    }

    private static void tapCancelOption() {
        Logger.debugLog("Attempting to locate and tap the cancel option...");

        // Locate the dream option
        Rectangle cancelOption = Objects.getBestMatch("/imgs/cancel.png", 0.8);

        if (cancelOption != null) {
            Logger.debugLog("Cancel option located: " + cancelOption);
            Client.tap(cancelOption);
            Logger.debugLog("Cancel option tapped successfully.");
        } else {
            Logger.debugLog("Cancel option was NOT FOUND.");
        }
    }

    public static void drinkOffensivePot() {
        // Special handling for super attack and strength combo
        if ("Super att/str combo".equals(potions)) {
            handleSuperAttackStrengthCombo();
            return;
        }

        // Handle regular potions
        int[] ids = potionData.get(potions);
        if (ids != null) {
            if (!consumePotion(ids)) {
                Logger.debugLog("We've run out of " + potions + " potions, leaving NMZ instance.");
                leaveNMZ();
            }
        }

        // Special handling for Overload (reset HP regen if using Absorption method)
        if ("Overload".equals(potions) && "Absorption".equals(NMZMethod)) {
            toggleQuickPrayer();
            Condition.sleep(9000, 11000);
        }
    }

    private static boolean consumePotion(int[] potionIds) {
        for (int id : potionIds) {
            if (Inventory.contains(id, 0.9)) {
                Inventory.eat(id, 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
                return true; // Successfully consumed a potion
            }
        }
        return false; // No potions available
    }

    private static void handleSuperAttackStrengthCombo() {
        int[][] comboPotions = {
                {149, 161}, {147, 159}, {145, 157}, {2436, 2440}
        };

        for (int[] combo : comboPotions) {
            if (Inventory.containsAll(combo, 0.9)) {
                Inventory.eat(combo[0], 0.9);
                Condition.sleep(1750, 2500);
                Inventory.eat(combo[1], 0.9);
                lastOffensivePotionTime = System.currentTimeMillis();
                return; // Successfully consumed a combo
            }
        }

        Logger.debugLog("We've run out of super sets, leaving NMZ instance.");
        leaveNMZ();
    }

    public static boolean needToBank() {
        // Open inventory tab
        GameTabs.openTab(UITabs.INVENTORY);

        // Check if we only need prayer potions when using overloads
        if (NMZMethod.startsWith("Prayer") && potions.equals("Overload")) {
            return Inventory.count(ItemList.PRAYER_POTION_4_2434, 0.75, prayerPotColor) < 22;
        }

        // Checks for when we use prayer potions and bank-able boost potions
        if (NMZMethod.startsWith("Prayer")) {
            switch (potions) {
                case "Divine super combat":
                    return Inventory.count(ItemList.PRAYER_POTION_4_2434, 0.75, prayerPotColor) < 22
                            || Inventory.count(ItemList.DIVINE_SUPER_COMBAT_POTION_4_23685, 0.75, divinesupercombatPotColor) < 6;
                case "Divine ranging":
                    return Inventory.count(ItemList.PRAYER_POTION_4_2434, 0.75, prayerPotColor) < 22
                            || Inventory.count(ItemList.DIVINE_RANGING_POTION_4_23733, 0.75, divinerangingPotColor) < 6;
                case "Ranging":
                    return Inventory.count(ItemList.PRAYER_POTION_4_2434, 0.75, prayerPotColor) < 22
                            || Inventory.count(ItemList.RANGING_POTION_4_2444, 0.75, rangingPotColor) < 6;
                case "Super att/str combo":
                    return Inventory.count(ItemList.PRAYER_POTION_4_2434, 0.75, prayerPotColor) < 22
                            || Inventory.count(ItemList.SUPER_STRENGTH_4_2440, 0.75, superstrengthPotColor) < 6
                            || Inventory.count(ItemList.SUPER_ATTACK_4_2436, 0.75, superattackPotColor) < 6;
                case "Super combat":
                    return Inventory.count(ItemList.PRAYER_POTION_4_2434, 0.75, prayerPotColor) < 22
                            || Inventory.count(ItemList.SUPER_COMBAT_POTION_4_12695, 0.75, supercombatPotColor) < 6;
                case "Super strength only":
                    return Inventory.count(ItemList.PRAYER_POTION_4_2434, 0.75, prayerPotColor) < 22
                            || Inventory.count(ItemList.SUPER_STRENGTH_4_2440, 0.75, superstrengthPotColor) < 6;
                default:
                    Logger.debugLog("Invalid potion type inside needToBank switch, terminating script.");
                    Logout.logout();
                    Script.stop();
            }
        }

        if (NMZMethod.equals("Absorption") && potions.equals("Overload")) {
            if (HPMethod.equals("Rock cake")) {
                return !Inventory.contains(ItemList.DWARVEN_ROCK_CAKE_7510, 0.75);
            }
            if (HPMethod.equals("Locator orb")) {
                return !Inventory.contains(ItemList.LOCATOR_ORB_22081, 0.75);
            }
        }

        // return false if nothing matches before
        return false;
    }

    public static void ensurePlayerAtTile(Tile targetTile) {
        int attempts = 0;

        while (!Player.atTile(targetTile) && attempts < 3) {
            Logger.debugLog("Attempt " + (attempts + 1) + ": Moving to tile " + targetTile.toString());

            if (Walker.isReachable(targetTile)) {
                Logger.debugLog("Tile is reachable. Stepping to the tile.");
                Walker.step(targetTile);
            } else {
                Logger.debugLog("Tile is not directly reachable. Using webWalk to navigate.");
                Walker.webWalk(targetTile);
                Player.waitTillNotMoving(20); // Wait for player movement to stop
                Logger.debugLog("Stepping to the tile after webWalk.");
                Walker.step(targetTile);
            }

            attempts++;
        }

        if (Player.atTile(targetTile)) {
            Logger.debugLog("Player successfully reached the target tile: " + targetTile.toString());
        } else {
            Logger.debugLog("Failed to reach the target tile after 3 attempts.");
        }
    }

}