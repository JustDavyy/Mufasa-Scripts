package main;

import Tasks.*;
import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import java.awt.Color;
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

    Map<String, SkillTracker.EquipmentStatus> equipmentStatus = SkillTracker.getEquipmentStatus();
    public static Color IronScimitarColor =  Color.decode("#403a3a");
    public static Color AdamantScimitarColor =  Color.decode("#2b3a2b");
    public static Color RuneScimitarColor =  Color.decode("#3a525d");
    public static Color AdamantPlateBodyColor =  Color.decode("#2b3a2b");
    public static Color IronPlatebodyColor =  Color.decode("#2f2b2b");
    
    public static Area bankArea = new Area(
            new Tile(6841, 13577, 0),
            new Tile(6904, 13624, 0)
    );

    private static final Random random = new Random();

    public static class TrainingCycleManager {
        public static int nextSkillToTrain = -1; // 0 = Strength, 1 = Attack, 2 = Defence 3 = Ranging
    }

    public static int determineNextSkillToTrain() {
        Logger.debugLog("Figuring out what to train");
    
        // Final Goal: Once all skills reach 70, switch to Ranging
        if (SkillTracker.strengthLevel >= 70 && SkillTracker.attackLevel >= 70 && SkillTracker.defenceLevel >= 70 || "Ranging".equals(dmCrabberPrivate.ChosenBuild)) {
            Logger.debugLog("All melee skills reached 70 or Ranging build selected. Switching to Ranging.");
            TrainingCycleManager.nextSkillToTrain = 3; // Switch to Ranging
            return 3;
        }
    
        // Initial Goal: Prioritize reaching level 30 for each skill in the order Strength -> Attack -> Defence
        if (SkillTracker.strengthLevel < 30 || SkillTracker.attackLevel < 30 || SkillTracker.defenceLevel < 30) {
            Logger.debugLog("Prioritizing reaching level 30 in all skills.");
    
            // 1. Train Strength first if it’s below 30 and within 5 levels of the others
            if (SkillTracker.strengthLevel < 30 && 
                SkillTracker.strengthLevel <= SkillTracker.attackLevel + 5 && SkillTracker.strengthLevel <= SkillTracker.defenceLevel + 5) {
                Logger.debugLog("Training Strength to reach level 30.");
                TrainingCycleManager.nextSkillToTrain = 0; // Train Strength
                return 0;
            }
    
            // 2. Train Attack if it’s below 30 within 5 levels of Strength, and Strength has reached or exceeded Attack
            if (SkillTracker.attackLevel < 30 && 
                SkillTracker.attackLevel <= SkillTracker.strengthLevel + 5 && SkillTracker.attackLevel <= SkillTracker.defenceLevel + 5) {
                Logger.debugLog("Training Attack to reach level 30.");
                TrainingCycleManager.nextSkillToTrain = 1; // Train Attack
                return 1;
            }
    
            // 3. Train Defence last if it’s below 30 and within 5 levels of Attack and Strength
            if (SkillTracker.defenceLevel < 30 && 
                SkillTracker.defenceLevel <= SkillTracker.attackLevel + 5 && SkillTracker.defenceLevel <= SkillTracker.strengthLevel + 5) {
                Logger.debugLog("Training Defence to reach level 30.");
                TrainingCycleManager.nextSkillToTrain = 2; // Train Defence
                return 2;
            }
        }
    
        // Intermediate Goal: Maintain a 5-level difference up to level 70
        if (SkillTracker.defenceLevel < 70 && SkillTracker.defenceLevel <= SkillTracker.attackLevel - 5 && SkillTracker.defenceLevel <= SkillTracker.strengthLevel - 5) {
            Logger.debugLog("Training Defence in intermediate goal (30 to 70).");
            TrainingCycleManager.nextSkillToTrain = 2;
            return 2; // Train Defence
        }
        if (SkillTracker.attackLevel < 70 && SkillTracker.attackLevel <= SkillTracker.strengthLevel - 5 && SkillTracker.attackLevel >= SkillTracker.defenceLevel) {
            Logger.debugLog("Training Attack in intermediate goal (30 to 70).");
            TrainingCycleManager.nextSkillToTrain = 1;
            return 1; // Train Attack
        }
        if (SkillTracker.strengthLevel < 70 && SkillTracker.strengthLevel >= SkillTracker.attackLevel && SkillTracker.strengthLevel >= SkillTracker.defenceLevel) {
            Logger.debugLog("Training Strength in intermediate goal (30 to 70).");
            TrainingCycleManager.nextSkillToTrain = 0;
            return 0; // Train Strength
        }
    
        // Default to Strength if no other conditions are met
        Logger.debugLog("Defaulting to Strength.");
        return 0;
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
            new TrainDefence(),
            new TrainStrenght(),
            new TrainAttack(),
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

        TakeGearOn();

        //Run tasks
        for (Task task : crabTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
    

    public static final Set<Integer> tappedItems = new HashSet<>(); // Keep track of items tapped for equipping

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
    
        Map<String, SkillTracker.EquipmentStatus> equipmentStatus = SkillTracker.getEquipmentStatus();
    
        for (Map.Entry<String, SkillTracker.EquipmentStatus> entry : equipmentStatus.entrySet()) {
            String gearType = entry.getKey();
            SkillTracker.EquipmentStatus status = entry.getValue();
    
            if (status == SkillTracker.EquipmentStatus.TO_EQUIP) {
                int[] itemsToEquip = gearItems.get(gearType);
                boolean allTapped = true;
    
                for (int itemId : itemsToEquip) {
                    Color itemColor = getItemColor(itemId);
    
                    // Check if the item has already been tapped
                    if (!tappedItems.contains(itemId)) {
                        if (!Inventory.contains(itemId, 0.8, itemColor)) {
                            Logger.debugLog("Missing item: " + itemId + " for gear type: " + gearType);
                            allTapped = false; // Mark as not fully tapped
                        } else {
                            // Tap to equip the item
                            equipItem(itemId, itemColor);
                            tappedItems.add(itemId); // Mark this item as tapped
                        }
                    }
                }
    
                // Mark as EQUIPPED only if all items have been tapped
                if (allTapped) {
                    SkillTracker.getEquipmentStatus().put(gearType, SkillTracker.EquipmentStatus.EQUIPPED);
                    Logger.debugLog("All items tapped for gear type: " + gearType);
                }
            }
        }
    }

    

    public static void equipItem(int itemId, Color itemColor) {
        Inventory.tapItem(itemId, 0.8, itemColor);
    
        // Wait until the item is no longer in inventory, indicating it's equipped
        Condition.wait(() -> !Inventory.contains(itemId, 0.8, itemColor), 250, 12);
    
        Logger.debugLog("Tapped item with ID: " + itemId);
    }
    
    private static Color getItemColor(int itemId) {
        // Return the correct color for specific items, or null if no color is needed
        if (itemId == ItemList.IRON_PLATEBODY_1115 || 
            itemId == ItemList.IRON_PLATELEGS_1067 || 
            itemId == ItemList.IRON_FULL_HELM_1153 || 
            itemId == ItemList.IRON_KITESHIELD_1191) {
            return IronPlatebodyColor; // Iron gear color
        }
        if (itemId == ItemList.IRON_SCIMITAR_1323) {
            return IronScimitarColor; // Iron scimitar color
        }
        if (itemId == ItemList.RUNE_SCIMITAR_1333) {
            return RuneScimitarColor; // Rune scimitar color
        }
        if (itemId == ItemList.ADAMANT_PLATEBODY_1123 || 
            itemId == ItemList.ADAMANT_PLATELEGS_1073 || 
            itemId == ItemList.ADAMANT_FULL_HELM_1161 || 
            itemId == ItemList.ADAMANT_KITESHIELD_1199) {
            return AdamantPlateBodyColor; // Adamant gear color
        }
        if (itemId == ItemList.ADAMANT_SCIMITAR_1331) {
            return AdamantScimitarColor; // Adamant scimitar color
        }
    
        return null; // Default for items without specific colors
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