package main;

import Tasks.*;
import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import utils.Spots;
import utils.Task;

import java.util.*;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dmCrabberPrivate",
        description = "Does crab people",
        version = "0.1",
        guideLink = "",
        categories = {ScriptCategory.Combat}
)
@ScriptConfiguration.List(
        {
                // Example config with a selection dropdown
                @ScriptConfiguration(
                        name =  "Spot",
                        description = "which spot would you like to use?",
                        defaultValue = "East 1 (2 crabs)",
                        allowedValues = {
                                @AllowedValue(optionName = "East 1 (2 crabs)"),
                                @AllowedValue(optionName = "East 2 (3 crabs)"),
                                @AllowedValue(optionName = "East 3 (4 crabs)"),
                                @AllowedValue(optionName = "East 4 (3 crabs)"),
                                @AllowedValue(optionName = "West 1 (2 crabs)"),
                                @AllowedValue(optionName = "West 2 (3 crabs)"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Breaking (lower end)",
                        description = "Please provide the lower end of your desired break time (in minutes).\nValid range is between 15 and 60, it will be randomised between the lower and higher end.",
                        defaultValue = "30",
                        minMaxIntValues = {15, 60},
                        optionType = OptionType.INTEGER
                ),
                @ScriptConfiguration(
                        name =  "Breaking (higher end)",
                        description = "Please provide the higher end of your desired break time (in minutes).\nValid range is between 30 and 120, it will be randomised between the lower and higher end.",
                        defaultValue = "60",
                        minMaxIntValues = {30, 120},
                        optionType = OptionType.INTEGER
                ),
                @ScriptConfiguration(
                        name =  "BankTab",
                        description = "What bank tab is your resources located in?",
                        defaultValue = "0",
                        optionType = OptionType.BANKTABS
                ),
                
                @ScriptConfiguration(
                        name = "Food",
                        description = "Select which food to use",
                        defaultValue = "Shark",
                        allowedValues = {
                                @AllowedValue(optionName = "None"),
                                @AllowedValue(optionIcon = "1891", optionName = "Cakes"),
                                @AllowedValue(optionIcon = "379", optionName = "Lobster"),
                                @AllowedValue(optionIcon = "373", optionName = "Swordfish"),
                                @AllowedValue(optionIcon = "385", optionName = "Shark"),
                                @AllowedValue(optionIcon = "359", optionName = "Tuna"),
                                @AllowedValue(optionIcon = "333", optionName = "Trout"),
                                @AllowedValue(optionIcon = "329", optionName = "Salmon"),
                                @AllowedValue(optionIcon = "365", optionName = "Bass"),
                                @AllowedValue(optionIcon = "3144", optionName = "Cooked karambwan"),
                                @AllowedValue(optionIcon = "391", optionName = "Manta ray"),
                                @AllowedValue(optionIcon = "13441", optionName = "Anglerfish")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "HP to eat at",
                        description = "Select the HP amount you'd like to eat at",
                        defaultValue = "6",
                        minMaxIntValues = {0, 100},
                        optionType = OptionType.INTEGER_SLIDER
                ),
                @ScriptConfiguration(
                    name = "Build",
                    description = "Choose which build you want to make",
                    defaultValue = "Melee",
                    allowedValues = {
                            @AllowedValue(optionName = "Melee"),
                            @AllowedValue(optionName = "Ranging"),
                    },
                    optionType = OptionType.STRING
            ),
                @ScriptConfiguration(
                        name = "Potions",
                        description = "Which combat potions would you like to use?",
                        defaultValue = "None",
                        allowedValues = {
                                @AllowedValue(optionName = "None"),
                                @AllowedValue(optionIcon = "9739", optionName = "Combat potion"),
                                @AllowedValue(optionIcon = "23685", optionName = "Divine super combat"),
                                @AllowedValue(optionIcon = "23733", optionName = "Divine ranging"),
                                @AllowedValue(optionIcon = "2444", optionName = "Ranging"),
                                @AllowedValue(optionIcon = "12695", optionName = "Super combat"),
                                @AllowedValue(optionIcon = "2440", optionName = "Super strength")
                        },
                        optionType = OptionType.STRING
                ),
        }
)

public class dmCrabberPrivate extends AbstractScript {
    String selectedSpot;
    public static int selectedBankTab;
    public static String potions;
    public static boolean usingPots;
    public static boolean outOfPots;
    public static int hpToEat;
    public static int currentHP;
    public static String selectedFood;
    public static int foodID;
    public static int potionID;
    public static Tile currentLocation;
    public static Spots spot;
    public static int lowerBreak;
    public static int higherBreak;
    public static String ChosenBuild;

    public static int attackLevel;
    public static int strenghtLevel;
    public static int defenceLevel;
    public static int rangeLevel;
    private static boolean IronPutOn = false;
    private static boolean AddyPutOn = false;
    private static boolean LeatherPutOn = false;
    private static boolean SnakeSkinPutOn = false;
    private static boolean GreenDhidePutOn = false;
    private static boolean RuneScimitarPutOn = false;
    private static boolean GraniteHammerOn = false;
    private static boolean WillowShortBowPutOn = false;
    private static boolean MagicShortBowPutOn = false;
    
    public static Area bankArea = new Area(
            new Tile(6841, 13577, 0),
            new Tile(6904, 13624, 0)
    );

    private static final Random random = new Random();

    public static class TrainingCycleManager {
        public static int nextSkillToTrain = 0; // 0 = Strength, 1 = Attack, 2 = Defence
    }
    
    
    @Override
    public void onStart(){
        Logger.log("Starting dmCrabber Private v0.1");
        Logger.log("initializing script..");
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        selectedSpot = configs.get("Spot"); // Example to get value of the first option
        selectedBankTab = Integer.parseInt(configs.get("BankTab")); // Get the bankTab value from the last configuration option
        hpToEat = Integer.parseInt(configs.get("HP to eat at"));
        selectedFood = configs.get("Food");
        potions = (configs.get("Potions"));
        usingPots = !java.util.Objects.equals(potions, "None");
        lowerBreak = Integer.parseInt(configs.get("Breaking (lower end)"));
        higherBreak = Integer.parseInt(configs.get("Breaking (higher end)"));
        ChosenBuild = (configs.get("Build"));

        setupCrabSpots();
        setupFoodIDs();
        setupPotIDs();

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"27-54"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        Client.disableBreakHandler();
        Client.disableAFKHandler();

        Chatbox.closeChatbox();

        Logger.log("Done with startup, script starting");

        currentLocation = Walker.getPlayerPosition();
    }

    // Task list!
    List<Task> crabTasks = Arrays.asList(
            new CheckAutoRetaliate(),
            new SkillTracker(),
            new Baank(),
            new BreakManager(),
            new Eat(),
            new TrainStrenght(),
            new TrainAttack(),
            new TrainDefence(),
            new TrainRange(),
            new GoToSpot(),
            new UsePotions(),
            new PerformCrabbing()
    );


    @Override
    public void poll() {
        XpBar.getXP();

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        //Run tasks
        for (Task task : crabTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
    

    public static void TakeGearOn() {
        Map<String, int[]> gearItems = Map.of(
        "Iron", new int[]{ItemList.IRON_PLATEBODY_1115, ItemList.IRON_PLATELEGS_1067, ItemList.IRON_FULL_HELM_1153, ItemList.IRON_KITESHIELD_1191, ItemList.IRON_SCIMITAR_1323},
        "Addy", new int[]{ItemList.ADAMANT_PLATEBODY_1123, ItemList.ADAMANT_PLATELEGS_1073, ItemList.ADAMANT_FULL_HELM_1161, ItemList.ADAMANT_KITESHIELD_1199, ItemList.ADAMANT_SCIMITAR_1331},
        "Leather", new int[]{ItemList.LEATHER_BODY_1129, ItemList.LEATHER_CHAPS_1095, ItemList.LEATHER_COWL_1167, ItemList.LEATHER_VAMBRACES_1063, ItemList.SHORTBOW_841, ItemList.OAK_SHORTBOW_843},
        "Snakeskin", new int[]{ItemList.SNAKESKIN_BODY_6322, ItemList.SNAKESKIN_CHAPS_6324, ItemList.SNAKESKIN_BANDANA_6326, ItemList.SNAKESKIN_BOOTS_6328, ItemList.SNAKESKIN_VAMBRACES_6330},
        "GreenDhide", new int[]{ItemList.GREEN_D_HIDE_BODY_1135, ItemList.GREEN_D_HIDE_CHAPS_1099, ItemList.GREEN_D_HIDE_VAMBRACES_1065},
        "RuneScimitar", new int[]{ItemList.RUNE_SCIMITAR_1333},
        "GraniteHammer", new int[]{ItemList.GRANITE_HAMMER_21742},
        "WillowShortBow", new int[]{ItemList.WILLOW_SHORTBOW_849, ItemList.MITHRIL_ARROW_1_921},
        "MagicShortBow", new int[]{ItemList.MAGIC_SHORTBOW_861}
        );

        // Check each type of gear and equip if it's been withdrawn
        if (Baank.IronEquippedWithdrawed && !IronPutOn) equipGear("Iron", gearItems.get("Iron"));
        if (Baank.AddyEquippedWithdrawed && !AddyPutOn) equipGear("Addy", gearItems.get("Addy"));
        if (Baank.LeatherEquippedWithdrawed && !LeatherPutOn) equipGear("Leather", gearItems.get("Leather"));
        if (Baank.SnakeSkinEquippedWithdrawed && !SnakeSkinPutOn) equipGear("Snakeskin", gearItems.get("Snakeskin"));
        if (Baank.GreenDhideEquippedWithdrawed && !GreenDhidePutOn) equipGear("GreenDhide", gearItems.get("GreenDhide"));
        if (Baank.RuneScimitarWithdrawed && !RuneScimitarPutOn) equipGear("RuneScimitar", gearItems.get("RuneScimitar"));
        if (Baank.GraniteHammerWithdrawed && !GraniteHammerOn) equipGear("GraniteHammer", gearItems.get("GraniteHammer"));
        if (Baank.WillowShortBowWithdrawed && !WillowShortBowPutOn) equipGear("WillowShortBow", gearItems.get("WillowShortBow"));
        if (Baank.MagicShortBowWithdrawed && !MagicShortBowPutOn) equipGear("MagicShortBow", gearItems.get("MagicShortBow"));
    }
    

    public static void equipGear(String gearType, int[] itemIds) {
        for (int itemId : itemIds) {
            Inventory.tapItem(itemId, 0.8);
            Condition.wait(() -> !Inventory.contains(itemId, 0.8), 250, 12);
        }
        Logger.debugLog("Equipped " + gearType + " gear.");
    }


    public void setupCrabSpots() {
        Logger.debugLog("Setting up spot info");
        switch (selectedSpot) {
            case "East 1 (2 crabs)":
                spot = Spots.EAST1;
                break;
            case "East 2 (3 crabs)":
                spot = Spots.EAST2;
                break;
            case "East 3 (3 crabs)":
                spot = Spots.EAST3;
                break;
            case "East 4 (3 crabs)":
                spot = Spots.EAST4;
                break;
            case "West 1 (2 crabs)":
                spot = Spots.WEST1;
                break;
            case "West 2 (3 crabs)":
                spot = Spots.WEST2;
                break;
            default:
                Logger.debugLog("Incorrect spot setup.");
                Script.stop();
        }
    }


    private void setupPotIDs() {
        Logger.debugLog("Setting up potion IDs");
        switch (potions) {
            case "Divine super combat":
                potionID = 23685;
                break;
            case "Divine ranging":
                potionID = 23733;
                break;
            case "Ranging":
                potionID = 2444;
                break;
            case "Super combat":
                potionID = 12695;
                break;
            case "Super strength":
                potionID = 2440;
                break;
            case "Combat potion":
                potionID = 9739;
                break;
            case "None":
                potionID = 0;
                usingPots = false;
                break;
            default:
                Logger.log("Invalid potion configuration, please restart script");
                Script.stop();
                break;
        }
    }


    private void setupFoodIDs() {
        Logger.debugLog("Setting up food IDs");
        switch (selectedFood) {
            case "None":
                foodID = 0;
                break;
            case "Cakes":
                foodID = 1891;
                break;
            case "Lobster":
                foodID = 379;
                break;
            case "Swordfish":
                foodID = 373;
                break;
            case "Shark":
                foodID = 385;
                break;
            case "Tuna":
                foodID = 359;
                break;
            case "Trout":
                foodID = 333;
                break;
            case "Salmon":
                foodID = 329;
                break;
            case "Bass":
                foodID = 365;
                break;
            case "Cooked karambwan":
                foodID = 3144;
                break;
            case "Manta ray":
                foodID = 391;
                break;
            case "Anglerfish":
                foodID = 13441;
                break;
            default:
                Logger.log("Invalid food configuration, please restart script");
                Script.stop();
                break;
        }
    }


    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }
}