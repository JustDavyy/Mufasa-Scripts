package main;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import helpers.utils.Tile;
import tasks.*;
import utils.StateUpdater;
import utils.Task;
import utils.WTStates;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Script;

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
                        defaultValue = "Cakes",
                        allowedValues = {
                                @AllowedValue(optionIcon = "1891", optionName = "Cakes"),
                                @AllowedValue(optionIcon = "379", optionName = "Lobster"),
                                @AllowedValue(optionIcon = "373", optionName = "Swordfish"),
                                @AllowedValue(optionIcon = "383", optionName = "Sharks"),
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
                        description = "Select the HP amount you'd liek to eat at",
                        defaultValue = "0",
                        minMaxIntValues = {0, 100},
                        optionType = OptionType.INTEGER_SLIDER
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dWintertodt extends AbstractScript {

    // These tasks are executed in this order
    List<Task> WTTasks = Arrays.asList(
            new Eat(),
            new Bank(),
            new SwitchSide(),
            new BurnBranches(),
            new FletchBranches(),
            new GetBranches()
    );

    // Variables
    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    public static int hpToEat;
    public static String selectedFood;
    public static int foodID;
    public static int foodAmount;
    public static int foodAmountLeftToBank;
    StateUpdater stateUpdater = new StateUpdater();

    // State creation (we might not need all 4, but just the bottom ones?)
    public static WTStates[] states = {
            new WTStates("Upper Left", new Rectangle(65, 57, 25, 23), false, false, false, false),
            new WTStates("Upper Right", new Rectangle(122, 56, 23, 23), false, false, false, false),
            new WTStates("Lower Left", new Rectangle(122, 56, 23, 23), false, false, false, false),
            new WTStates("Lower Right", new Rectangle(120, 110, 25, 27), false, false, false, false)
    };

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        hpToEat = Integer.parseInt(configs.get("HP to eat at"));
        selectedFood = configs.get("Food");
        foodAmount = Integer.parseInt(configs.get("Food amount"));
        foodAmountLeftToBank = Integer.parseInt(configs.get("Food amount left to bank at"));

        setupFoodIDs();
    }

    @Override
    public void poll() {

        // Keep track of the states on each loop
        stateUpdater.updateStates(states);

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
            case "Sharks":
                foodID = 383;
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


    // Just leaving these down here so we can figure out where they belong
    Tile[] LowerRightToLeft = new Tile[] {
            new Tile(648, 165),
            new Tile(638, 165),
            new Tile(626, 165)
    };
    Tile[] returnToWTDoor = new Tile[] {
            new Tile(638, 167),
            new Tile(637, 175),
            new Tile(637, 185),
            new Tile(637, 195)
    };
    Tile[] wtDoorToBank = new Tile[] {
            new Tile(637, 204),
            new Tile(639, 217),
            new Tile(645, 228),
            new Tile(650, 228)
    };
    Tile[] wtDoorToRightSide = new Tile[] {
            new Tile(638, 185),
            new Tile(639, 175),
            new Tile(650, 165)
    };
    Tile[] wtDoorToLeftSide = new Tile[] {
            new Tile(637, 186),
            new Tile(637, 173),
            new Tile(625, 165)
    };
    Tile[] fromEitherSideToSafeSpot = new Tile[] {
            new Tile(637, 166),
            new Tile(637, 177)
    };

    // For when we need reverse the paths when going back/forth
    public Tile[] getReversedTiles(Tile[] array) {
        if (array == null) return null;
        Tile[] reversed = new Tile[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }
}