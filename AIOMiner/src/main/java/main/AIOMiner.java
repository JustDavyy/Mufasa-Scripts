package main;

import helpers.*;
import helpers.utils.OptionType;
import tasks.Bank;
import tasks.CheckPickaxe;
import tasks.DropOres;
import tasks.performMining;
import utils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "AIO Miner",
        description = "Mines ores in different places",
        version = "1.0",
        guideLink = "",
        categories = {ScriptCategory.Mining}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name =  "Location",
                        description = "Which location would you like to use? be sure to read the script guide for which ores are supported in specific locations",
                        defaultValue = "Varrock East",
                        allowedValues = {
                                @AllowedValue(optionName = "Varrock East"),
                                @AllowedValue(optionName = "Varrock West"),
                                //@AllowedValue(optionName = "Soul isles"),
                                //@AllowedValue(optionName = "Al kharid South"),
                                //@AllowedValue(optionName = "Mining Guild"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Bank ores",
                        description = "Make sure you read the script guide if banking is supported in your location!",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "Ore type",
                        description = "Which ore would you like to mine?",
                        defaultValue = "Iron ore",
                        allowedValues = {
                                @AllowedValue(optionIcon = "436", optionName = "Copper ore"),
                                @AllowedValue(optionIcon = "438", optionName = "Tin ore"),
                                @AllowedValue(optionIcon = "434", optionName = "Clay"),
                                @AllowedValue(optionIcon = "442", optionName = "Silver ore"),
                                @AllowedValue(optionIcon = "440", optionName = "Iron ore"),
                                //@AllowedValue(optionIcon = "453", optionName = "Coal"),

                        },
                        optionType = OptionType.STRING
                )
        }
)

public class AIOMiner extends AbstractScript {
    List<Task> miningTasks = Arrays.asList(
            new CheckPickaxe(),
            new Bank(),
            new DropOres(),
            new performMining()
    );

    public static String Location;
    public static String oreType;
    public static int oreTypeInt;
    public static Boolean bankOres;
    //public static int miningLevel;
    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;

    public static LocationInfo locationInfo;
    public static RegionInfo regionInfo;
    public static VeinColors veinColors;
    public static PathsToBanks pathsToBanks;

    @Override
    public void onStart(){
        //Setup configs
        Map<String, String> configs = getConfigurations();
        Location = configs.get("Location");
        oreType = configs.get("Ore type");
        bankOres = Boolean.valueOf((configs.get("Bank ores")));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        //Setup enum values
        setupRegionInfo();
        setupLocationInfo();
        setupVeinColors();
        setupPathsToBank();
        setupOreTypeInts();

        //Check and cache STARTING mining level (just to make sure people dont fuck up)
        //if (!GameTabs.isStatsTabOpen()) {
        //    GameTabs.openStatsTab();
        //}
        //if (GameTabs.isStatsTabOpen()) {
        //    miningLevel = Stats.getRealLevel("Mining");
        //}
    }

    @Override
    public void poll() {
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        //Run tasks
            for (Task task : miningTasks) {
                if (task.activate()) {
                    task.execute();
                    return;
                }
            }
        }

    private void setupRegionInfo() {
        Logger.debugLog("Setting up region info");
        switch (Location) {
            case "Varrock East":
                regionInfo = RegionInfo.VARROCK_EAST;
                break;
            case "Varrock West":
                regionInfo = RegionInfo.VARROCK_WEST;
                break;
        }
    }

    private void setupLocationInfo() {
        Logger.debugLog("Setting up location info");
        if (regionInfo.equals(RegionInfo.VARROCK_EAST)) {
            switch (oreType) {
                case "Tin ore":
                    locationInfo = LocationInfo.VARROCK_EAST_TIN;
                    break;
                case "Copper ore":
                    locationInfo = LocationInfo.VARROCK_EAST_COPPER;
                    break;
                case "Iron ore":
                    locationInfo = LocationInfo.VARROCK_EAST_IRON;
                    break;
                default:
                    Logger.log("Incorrect setup configuration");
                    break;
            }
        } else if (regionInfo.equals(RegionInfo.VARROCK_WEST)) {
            switch (oreType) {
                case "Clay":
                    locationInfo = LocationInfo.VARROCK_WEST_CLAY;
                    break;
                case "Silver ore":
                    locationInfo = LocationInfo.VARROCK_WEST_SILVER;
                    break;
                case "Iron ore":
                    locationInfo = LocationInfo.VARROCK_WEST_IRON;
                    break;
                default:
                    Logger.log("Incorrect setup configuration");
                    break;
            }
        }
    }

    private void setupVeinColors() {
        Logger.debugLog("Setting up vein info");
        switch (oreType) {
            case "Copper ore":
                veinColors = VeinColors.COPPER_VEIN;
                break;
            case "Tin ore":
                veinColors = VeinColors.TIN_VEIN;
                break;
            case "Iron ore":
                veinColors = VeinColors.IRON_VEIN;
                break;
            case "Clay":
                veinColors = VeinColors.CLAY;
                break;
            case "Silver ore":
                veinColors = VeinColors.SILVER;
                break;
        }
    }

    private void setupPathsToBank() {
        Logger.debugLog("Setting up bank pathing");
        switch (Location) {
            case "Varrock East":
                pathsToBanks = PathsToBanks.VARROCK_EAST_BANKPATHS;
                break;
            case "Varrock West":
                pathsToBanks = PathsToBanks.VARROCK_WEST_BANKPATHS;
                break;
        }
    }

    private void setupOreTypeInts() {
        Logger.debugLog("Setting up ore type info");
        switch (oreType) {
            case "Copper ore":
                oreTypeInt = 436;
                break;
            case "Tin ore":
                oreTypeInt = 438;
                break;
            case "Clay":
                oreTypeInt = 434;
                break;
            case "Silver ore":
                oreTypeInt = 442;
                break;
            case "Iron ore":
                oreTypeInt = 440;
                break;
        }
    }
}