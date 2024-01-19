import Tasks.Bank;
import Tasks.CheckPickaxe;
import Tasks.VarrockEast;
import helpers.*;
import helpers.utils.OptionType;
import utils.Task;

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
                                //@AllowedValue(optionName = "Varrock west"),
                                //@AllowedValue(optionName = "Soul isles"),
                                //@AllowedValue(optionName = "Al kharid"),
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
                                //@AllowedValue(optionIcon = "434", optionName = "Clay"),
                                @AllowedValue(optionIcon = "440", optionName = "Iron ore"),
                                //@AllowedValue(optionIcon = "453", optionName = "Coal"),

                        },
                        optionType = OptionType.STRING
                )
        }
)

public class AIOMiner extends AbstractScript {
    public static String Location;
    public static String oreType;
    public static Boolean bankOres;
    public static int miningLevel;
    public String hopProfile;
    public Boolean hopEnabled;

    @Override
    public void onStart(){
        //Setup configs
        Map<String, String> configs = getConfigurations();
        Location = configs.get("Location");
        oreType = configs.get("Ore type");
        bankOres = Boolean.valueOf((configs.get("Bank ores")));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));

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
            List<Task> miningTasks = Arrays.asList(
                    new CheckPickaxe(),
                    new Bank(),
                    new VarrockEast()
            );

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
}