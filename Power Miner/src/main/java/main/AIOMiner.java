package main;

import Tasks.Bank;
import Tasks.CheckPickaxe;
import Tasks.VarrockEast;
import helpers.*;
import helpers.utils.OptionType;
import utils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "AIO Miner",
        description = "Mines ores in different places",
        version = "1.0",
        category = ScriptCategory.Mining
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings? WDH is disabled for this script, as there's users on every world mostly.",
                        defaultValue = "1",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name =  "Location",
                        description = "Which location would you like to use? be sure to read the script guide for which ores are supported in specific locations",
                        defaultValue = "Soul isles",
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
                        description = "Make sure you read the script guides if banking is supported in your location!",
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
            new VarrockEast()
    );

    public static String Location;
    public static String oreType;
    public static Boolean bankOres;
    public static int miningLevel;
    public String hopProfile;
    public Boolean hopEnabled;

    LocationInfo locationInfo;
    RegionInfo regionInfo;
    VeinColors veinColors;
    PathsToBanks pathsToBanks;

    @Override
    public void onStart(){
        //Setup configs
        Map<String, String> configs = getConfigurations();
        Location = configs.get("Location");
        oreType = configs.get("Ore type");
        bankOres = Boolean.valueOf((configs.get("Bank ores")));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));

        //Setup enum values
        setupRegionInfo();
        setupLocationInfo();
        setupVeinColors();
        setupPathsToBank();

        //Check and cache STARTING mining level (just to make sure people dont fuck up)
        if (!GameTabs.isStatsTabOpen()) {
            GameTabs.openStatsTab();
        }
        if (GameTabs.isStatsTabOpen()) {
            miningLevel = Stats.getRealLevel("MINING");
        }
    }

    @Override
    public void poll() {
        //Check if we should do hopping
        if(hopEnabled) {
            hopActions();
        }

        //Run tasks
            for (Task task : miningTasks) {
                if (task.activate()) {
                    task.execute();
                    return;
                }
            }
        }

    private void hopActions() {
        Game.hop(hopProfile, true, false);
    }

    private void setupRegionInfo() {
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
        switch (Location) {
            case "Varrock East":
                pathsToBanks = PathsToBanks.VARROCK_EAST_BANKPATHS;
                break;
            case "Varrock West":
                pathsToBanks = PathsToBanks.VARROCK_WEST_BANKPATHS;
                break;
        }
    }
}