package main;

import Tasks.Bank;
import Tasks.Eat;
import Tasks.GoToSpot;
import Tasks.PerformCrabbing;
import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import helpers.utils.RegionBox;
import helpers.utils.Tile;
import utils.Spots;
import utils.Task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dmCrabber",
        description = "Does crab people",
        version = "1.00",
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
                                @AllowedValue(optionName = "East 3 (3 crabs)"),
                                @AllowedValue(optionName = "East 4 (3 crabs)"),
                                @AllowedValue(optionName = "West 1 (2 crabs)"),
                                @AllowedValue(optionName = "West 2 (2 crabs)"),
                        },
                        optionType = OptionType.STRING
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
                        defaultValue = "15",
                        minMaxIntValues = {0, 100},
                        optionType = OptionType.INTEGER_SLIDER
                )
        }
)

public class dmCrabber extends AbstractScript {
    String selectedSpot;
    String selectedBankTab;
    public static int hpToEat;
    public static int currentHP;
    public static String selectedFood;
    public static int foodID;
    public static Tile currentLocation;
    public static Spots spot;
    private static final Random random = new Random();

    public static RegionBox crabRegion = new RegionBox("crabRegion", 2016, 2373, 2748, 2826);

    @Override
    public void onStart(){
        Logger.log("Starting dmCrabber v1.0");
        Logger.log("initializing script..");
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        selectedSpot = configs.get("Spot"); // Example to get value of the first option
        selectedBankTab = configs.get("BankTab"); // Get the bankTab value from the last configuration option
        hpToEat = Integer.parseInt(configs.get("HP to eat at"));
        selectedFood = configs.get("Food");

        setupCrabSpots();
        setupFoodIDs();
        Logger.log("Done with startup, script starting");
    }

    // Task list!
    List<Task> crabTasks = Arrays.asList(
            new Bank(),
            new Eat(),
            new GoToSpot(),
            new PerformCrabbing()
    );

    @Override
    public void poll() {
        XpBar.getXP();
        currentLocation = Walker.getPlayerPosition(crabRegion);

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
            case "West 2 (2 crabs)":
                spot = Spots.WEST2;
                break;
            default:
                Logger.debugLog("Incorrect setup for vein colors.");
                Script.stop();
        }
    }

    private void setupFoodIDs() {
        Logger.debugLog("Setting up food IDs");
        switch (selectedFood) {
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

    public static Tile[] getReversedTiles(Tile[] array) {
        if (array == null) return null;
        Tile[] reversed = new Tile[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }
}