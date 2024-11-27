package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import helpers.utils.Skills;

import java.awt.Color;
import java.awt.Rectangle;

import tasks.CheckForItems;
import tasks.PerformTeleAlching;
import utils.Task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;


@ScriptManifest(
        name = "dTeleportAlcher",
        description = "Uses alchemy in combination with camelot or ardougne teleport for high XP rates.",
        version = "1.04",
        guideLink = "",
        categories = {ScriptCategory.Magic}
)
@ScriptConfiguration.List(
        value = {
                @ScriptConfiguration(
                        name = "Item ID",
                        description = "Enter the item ID of what you'd like to alch, you can find the IDs in our discord using the /id command.",
                        defaultValue = "0",
                        minMaxIntValues = {0, 30000},
                        optionType = OptionType.INTEGER
                ),
                @ScriptConfiguration(
                        name =  "Teleport",
                        description = "Which teleport would you like to use? Camelot/Ardougne will use High Level Alchemy, varrock & falador & lumbridge will be used with Low Level Alchemy for the lower levels.",
                        defaultValue = "Camelot teleport",
                        allowedValues = {
                                @AllowedValue(optionName = "Varrock teleport"),
                                @AllowedValue(optionName = "Lumbridge teleport"),
                                @AllowedValue(optionName = "Falador teleport"),
                                @AllowedValue(optionName = "Camelot teleport"),
                                @AllowedValue(optionName = "Camelot teleport - Low Alchemy"),
                                @AllowedValue(optionName = "Ardougne teleport")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "1",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dTeleAlcher extends AbstractScript {
    private Rectangle MagicInfoTab = new Rectangle(636, 472, 35, 11);
    private Color MagicInfoTabColor = Color.decode("#932320");
    public static int itemID;
    public static String hopProfile;
    public static String teleport;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    public static int magicLevel = 0;
    

    @Override
    public void onStart(){

        Map<String, String> configs = getConfigurations();
        itemID = Integer.parseInt(configs.get("Item ID"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        teleport = configs.get("Teleport");

        switch (dTeleAlcher.teleport) {
            case "Camelot teleport":
                Logger.log("Using Camelot teleport in combination with High Level Alchemy for this run.");
                break;
            case "Camelot teleport - Low Alchemy":
                Logger.log("Using Camelot teleport in combination with Low Level Alchemy for this run.");
                break;
            case "Ardougne teleport":
                Logger.log("Using Ardougne teleport in combination with High Level Alchemy for this run.");
                break;
            case "Varrock teleport":
                Logger.log("Using Varrock teleport in combination with Low Level Alchemy for this run.");
                break;
            case "Falador teleport":
                Logger.log("Using Falador teleport in combination with Low Level Alchemy for this run.");
                break;
            case "Lumbridge teleport":
                Logger.log("Using Lumbridge teleport in combination with Low Level Alchemy for this run.");
                break;
        }

        // Open the magic tab
        GameTabs.openMagicTab();
        Condition.wait(() -> GameTabs.isMagicTabOpen(),200,10);
        if (Client.isColorInRect(MagicInfoTabColor, MagicInfoTab, 5)) {
            Logger.log("You have Info enabled, disabling now.");
            Client.tap(MagicInfoTab);
            Condition.wait(() -> !Client.isColorInRect(MagicInfoTabColor, MagicInfoTab, 5),200,10);
        }

    }

    List<Task> alchTasks = Arrays.asList(
            new CheckForItems(),
            new PerformTeleAlching()
    );

    @Override
    public void poll() {
        // Check if we should WH
        if (hopEnabled) {
            Game.hop(hopProfile, useWDH, false); // Check if we should worldhop
        }

        //Run tasks
        for (Task task : alchTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
}
