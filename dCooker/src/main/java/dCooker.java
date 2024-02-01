import helpers.*;
import helpers.utils.OptionType;
import helpers.utils.OverlayColor;
import helpers.utils.Tile;
import helpers.utils.Area;
import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.time.Instant;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dCooker",
        description = "The cooker script to cook all your raw fish (or seaweed) at various different places.",
        version = "1.00",
        guideLink = "To be added.",
        categories = {ScriptCategory.Cooking, ScriptCategory.Slayer}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What product would you like to cook?",
                        defaultValue = "Raw karambwan",
                        allowedValues = {
                                @AllowedValue(optionIcon = "401", optionName = "Seaweed"),
                                @AllowedValue(optionIcon = "21504", optionName = "Giant seaweed"),
                                @AllowedValue(optionIcon = "317", optionName = "Raw shrimps"),
                                @AllowedValue(optionIcon = "327", optionName = "Raw sardine"),
                                @AllowedValue(optionIcon = "345", optionName = "Raw herring"),
                                @AllowedValue(optionIcon = "353", optionName = "Raw mackerel"),
                                @AllowedValue(optionIcon = "335", optionName = "Raw trout"),
                                @AllowedValue(optionIcon = "341", optionName = "Raw cod"),
                                @AllowedValue(optionIcon = "349", optionName = "Raw pike"),
                                @AllowedValue(optionIcon = "331", optionName = "Raw salmon"),
                                @AllowedValue(optionIcon = "3142", optionName = "Raw karambwan"),
                                @AllowedValue(optionIcon = "359", optionName = "Raw tuna"),
                                @AllowedValue(optionIcon = "377", optionName = "Raw lobster"),
                                @AllowedValue(optionIcon = "371", optionName = "Raw swordfish"),
                                @AllowedValue(optionIcon = "7944", optionName = "Raw monkfish"),
                                @AllowedValue(optionIcon = "383", optionName = "Raw shark"),
                                @AllowedValue(optionIcon = "395", optionName = "Raw sea turtle"),
                                @AllowedValue(optionIcon = "13439", optionName = "Raw anglerfish"),
                                @AllowedValue(optionIcon = "389", optionName = "Raw manta ray"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Location",
                        description = "What location would you like to cook at?",
                        defaultValue = "Hosidius kitchen",
                        allowedValues = {
                                @AllowedValue(optionName = "Cooks' Guild"),
                                @AllowedValue(optionName = "Hosidius kitchen"),
                                @AllowedValue(optionName = "Mor Ul Rek"),
                                @AllowedValue(optionName = "Myths' Guild"),
                                @AllowedValue(optionName = "Nardah oven"),
                                @AllowedValue(optionName = "Rogues' Den")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Bank Tab",
                        description = "What bank tab are your resources located in?",
                        defaultValue = "5",
                        optionType = OptionType.BANKTABS
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "1",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name = "Use white dot hop (WDH)?",
                        description = "Only recommended to use at remote locations such as the Nardah furnace and Mor Ul Rek.\nWill only work if you have world hopping enabled!",
                        defaultValue = "False",
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class dCooker extends AbstractScript {

// Script config variables
String hopProfile;
Boolean hopEnabled;
Boolean useWDH;
int banktab;
String product;
String location;

// Script logic variables

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use white dot hop (WDH)?")));
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        product = configs.get("Product");
        location = configs.get("Location");

        Boolean usewdh2 = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        Logger.log("Thank you for using the dCooker script!");
        Logger.log("Setting up everything for your gains now...");

        Logger.debugLog("usewdh2 (.useWDH) is: " + usewdh2);

        if (hopEnabled) {
            if(useWDH) {
                Logger.debugLog("Hopping (with WDH) is enabled for this run! Using profile: " + hopProfile);
            } else {
                Logger.debugLog("Hopping (without WDH) is enabled for this run! Using profile: " + hopProfile);
            }
        } else {
            Logger.debugLog("Hopping is disabled for this run!");
        }

        // Debug prints for chosen settings (in case we ever need this)
        Logger.debugLog("We're using bank tab: " + banktab);
        Logger.debugLog("We're cooking " + product + " in this run at " + location + ".");



    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        //Logger.debugLog("Temp log statement.");

    }


    // Methods and stuff here

}