package main;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.SideManager;
import utils.Task;
import utils.WTStates;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dWintertodt",
        description = "Completes the Wintertodt minigame.",
        version = "1.0",
        guideLink = "",
        categories = {ScriptCategory.Firemaking, ScriptCategory.Minigames}
)
@ScriptConfiguration.List(
        {
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
                        name = "BankTab",
                        description = "What bank tab are your food located in?",
                        defaultValue = "0",
                        optionType = OptionType.BANKTABS
                ),
                @ScriptConfiguration(
                        name = "Food amount",
                        description = "Select the amount of food you'd like to bring",
                        defaultValue = "8",
                        minMaxIntValues = {1, 28},
                        optionType = OptionType.INTEGER_SLIDER
                ),
                @ScriptConfiguration(
                        name = "Food amount left to bank at",
                        description = "Select the amount of food required to have for each game",
                        defaultValue = "4",
                        minMaxIntValues = {1, 28},
                        optionType = OptionType.INTEGER_SLIDER
                ),
                @ScriptConfiguration(
                        name = "HP to eat at",
                        description = "Select the HP amount you'd like to eat at",
                        defaultValue = "20",
                        minMaxIntValues = {0, 100},
                        optionType = OptionType.INTEGER_SLIDER
                ),
                @ScriptConfiguration(
                        name = "Side",
                        description = "Which side would you like to start on?",
                        defaultValue = "Right",
                        allowedValues = {
                                @AllowedValue(optionName = "Right"),
                                @AllowedValue(optionName = "Left")
                        },
                        optionType = OptionType.STRING
                )
        }
)

public class dWintertodt extends AbstractScript {

    // EVERYTHING FROM CONSTANTS FILE BELOW HERE
    public static final int brumaRoot = ItemList.BRUMA_ROOT_20695;
    public static final int brumaKindling = ItemList.BRUMA_KINDLING_20696;
    public static final int knife = 946;
    // Variables
    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    public static int hpToEat;
    public static String selectedFood;
    public static int foodID;
    public static int foodAmount;
    public static int foodAmountLeftToBank;
    public static int bankTab;
    public static int foodAmountInInventory;


    public static boolean gameNearingEnd;
    public static boolean gameAt20Percent;
    public static boolean waitingForGameToStart;
    public static RegionBox WTRegion = new RegionBox("WTRegion", 1701, 264, 2157, 846);
    public static Area lobby = new Area(new Tile(632, 173), new Tile(644, 184));
    public static Tile bankTile = new Tile(650, 228);
    public static Rectangle enterDoorRect = new Rectangle(323, 75, 307, 122);
    public static Rectangle exitDoorRect = new Rectangle(138, 248, 459, 173);
    public static Area insideArea = new Area(
            new Tile(605, 152),
            new Tile(666, 199)
    );
    public static Area outsideArea = new Area(
            new Tile(620, 202),
            new Tile(665, 242)
    );
    public static Area atDoor = new Area( //this one is both at door from inside & outside
            new Tile(624, 193),
            new Tile(652, 209)
    );
    // <--
    public static Area leftWTArea = new Area(new Tile(609, 150), new Tile(630, 172));
    // -->
    public static Area rightWTArea = new Area(new Tile(645, 150), new Tile(669, 176));
    // Paths
    public static Tile[] wtDoorToBank = new Tile[]{
            new Tile(637, 204),
            new Tile(639, 217),
            new Tile(645, 228),
            new Tile(650, 228)
    };
    public static Tile[] gameToWTDoor = new Tile[]{
            new Tile(638, 167),
            new Tile(637, 175),
            new Tile(637, 185),
            new Tile(637, 195)
    };
    public static Tile[] wtDoorToRightSide = new Tile[]{
            new Tile(638, 185),
            new Tile(639, 175),
            new Tile(650, 165)
    };
    public static Tile[] wtDoorToLeftSide = new Tile[]{
            new Tile(637, 186),
            new Tile(637, 173),
            new Tile(625, 165)
    };
    public static Tile[] LowerRightToLeft = new Tile[]{
            new Tile(648, 165),
            new Tile(638, 165),
            new Tile(626, 165)
    };
    public static Tile[] fromEitherSideToGameLobby = new Tile[]{
            new Tile(637, 166),
            new Tile(637, 177)
    };
    // Location static
    public static Tile currentLocation;
    // Side static
    public static String currentSide;
    // State creation (we might not need all 4, but just the bottom ones?)
    public static WTStates[] states = {
            new WTStates("Lower Left", new Rectangle(60, 112, 31, 31), false, false, false, false),
            new WTStates("Lower Right", new Rectangle(120, 113, 27, 31), false, false, false, false)
    };

    // EVERYTHING FROM CONSTANTS FILE ABOVE
    private static final Random random = new Random();
    // These tasks are executed in this order
    List<Task> WTTasks = Arrays.asList(
            new CheckGear(),
            //new Bank(),
            new Eat(),
            //new SwitchSide(),
            new BurnBranches(),
            new FletchBranches(),
            new GetBranches(),
            new GoBackToGame()
    );

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        int delay = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        return delay;
    }

    // Just leaving these down here so we can figure out where they belong
    public static Tile[] getReversedTiles(Tile[] array) {
        if (array == null) return null;
        Tile[] reversed = new Tile[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    @Override
    public void onStart() {
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        hpToEat = Integer.parseInt(configs.get("HP to eat at"));
        selectedFood = configs.get("Food");
        foodAmount = Integer.parseInt(configs.get("Food amount"));
        foodAmountLeftToBank = Integer.parseInt(configs.get("Food amount left to bank at"));
        currentSide = configs.get("Side");
        bankTab = Integer.parseInt(configs.get("BankTab"));

        setupFoodIDs();
        initialFoodCount();
    }

    @Override
    public void poll() {
        SideManager.updateStates();

        //Run tasks
        for (Task task : WTTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
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

    // Method to count total food items in the inventory
    private void initialFoodCount() {
        foodAmountInInventory = 0; // Reset before counting

        if (selectedFood.equals("Cakes")) {
            int[] foodIds = {1891, 1893, 1895};
            for (int id : foodIds) {
                int countMultiplier = 1; // Default count multiplier
                if (id == 1891) {
                    countMultiplier = 3; // A full cake counts as 3
                } else if (id == 1893) {
                    countMultiplier = 2; // half cake counts as 2
                }

                // Assume Inventory.count(id, 0.60) returns the number of items that are at least 60% intact
                foodAmountInInventory += Inventory.count(id, 0.60) * countMultiplier;
            }
        } else {
            foodAmountInInventory = Inventory.count(foodID, 0.60);
        }
    }
}