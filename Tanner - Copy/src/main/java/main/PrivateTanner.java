package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.MapChunk;
import helpers.utils.OptionType;
import helpers.utils.Skills;
import tasks.Antiban;
import tasks.Banking;
import tasks.Buying;
import tasks.PerformTanning;
import tasks.Transport;
import utils.Task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;


@ScriptManifest(
        name = "Tanner",
        description = "Buys and Tans Green and Blue d hide.",
        version = "1.0",
        guideLink = "",
        categories = {ScriptCategory.Beta}
)

@ScriptConfiguration.List(
    value = {
        @ScriptConfiguration(
                name = "Use world hopper?",
                description = "Hops world based on worldhopper settings.",
                defaultValue = "1",
                optionType = OptionType.WORLDHOPPER
        )
    }
)

public class PrivateTanner extends AbstractScript {
    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    public static int CraftingLevel = 0;
    
    public static int GreenDHideRaw = 1745;
    public static int GreenDHideNotedRaw = 1746;

    public static int BlueDHideRaw = 2505;
    public static int BlueDHideNotedRaw = 2506;

    public static int GreenDhideDone = 1753;
    public static int GreenDhideDoneNoted = 1754;

    public static int BlueDHideDone = 1751;
    public static int BlueDHideDoneNoted = 1752;


    public static boolean BuyHide = false;
    public static boolean WalkBank = false;
    public static boolean WalkGE = false;
    public static MapChunk mapChunk;
    


    @Override
    public void onStart(){
        Logger.log("Starting Private Tanner");
        Condition.sleep(500);
        Logger.log("Product Owner let me know if there's any bugs! and i will get them fixed");
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        mapChunk =  new MapChunk(new String[]{
            "45-51",
            "46-51",
            "47-51",
            "48-51",
            "49-51",
            "49-52",
            "49-53",
            "49-54"
        }, "0");
        
        // Check our levels here to decide which spell to use.
        GameTabs.openStatsTab();
        Condition.sleep(600);
        CraftingLevel = Stats.getRealLevel(Skills.CRAFTING);
        Walker.setup(mapChunk);
        Logger.log("Crafting level is: " + CraftingLevel);
        Condition.sleep(100);
        // Open the inventory again.
        GameTabs.openInventoryTab();
    }

    List<Task> alchTasks = Arrays.asList(
        new Antiban(),
        new Banking(),
        new Buying(),
        new Buying(),
        new PerformTanning(),
        new Transport()
    );

    @Override
    public void poll() {

        XpBar.getXP();
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
