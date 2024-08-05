package main;

import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.Area;
import helpers.utils.OptionType;
import helpers.utils.RegionBox;
import helpers.utils.Tile;
import tasks.*;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ScriptManifest(
        name = "dCakeThiever",
        description = "Steals from the bakery stall at Ardougne market. Supports world hopping and banking. Detects being caught, runs away if needed.",
        version = "2.0",
        guideLink = "https://wiki.mufasaclient.com/docs/dcake-thiever/",
        categories = {ScriptCategory.Thieving}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Banking",
                        description = "Would you like to bank the stolen cakes?",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "World hopping",
                        description = "Would you like to hop worlds based on your hop profile settings? Keep in mind, this script will ALWAYS hop if a crasher is detected.",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dCakeThiever extends AbstractScript {
    // Creating the strings for later use
    public static Random random = new Random();
    public static String hopProfile;
    public static Boolean hopEnabled;
    Boolean useWDH;
    public static Boolean bankYN;

    public static RegionBox ardyRegion = new RegionBox(
            "ardyRegion",
            5808, 3138,
            6270, 3447
    );

    public static Area scriptArea = new Area(
            new Tile(2015, 1063),
            new Tile(2030, 1080)
    );
    public static int chocSlice = 1901;
    public static  Tile stallTile = new Tile(2023, 1070);
    public static Tile bankBoothTile = new Tile(2004, 1102);
    public static Tile[] pathToStall = new Tile[] {
            new Tile(2004, 1102),
            new Tile(2007, 1097),
            new Tile(2015, 1096),
            new Tile(2019, 1089),
            new Tile(2023, 1085),
            new Tile(2024, 1078),
            new Tile(2023, 1070)
    };

    public static  Rectangle stallTapWindow = new Rectangle(383, 269, 17, 14);
    public static Rectangle bankBooth = new Rectangle(483, 250, 25, 26);
    public static Area bankArea = new Area(
            new Tile(1999, 1099),
            new Tile(2009, 1110)
    );
    public static Tile[] pathToBank = new Tile[] {
            new Tile(2023, 1070),
            new Tile(2024, 1078),
            new Tile(2023, 1085),
            new Tile(2019, 1089),
            new Tile(2015, 1096),
            new Tile(2007, 1097),
            new Tile(2004, 1102)
    };

    public static Tile[] runAwayPath = new Tile[] {
            new Tile(2024, 1073),
            new Tile(2025, 1076),
            new Tile(2029, 1077),
            new Tile(2036, 1078),
            new Tile(2041, 1078),
            new Tile(2049, 1078)
    };

    public static Tile[] runBackPath = new Tile[] {
            new Tile(2049, 1078),
            new Tile(2041, 1078),
            new Tile(2036, 1078),
            new Tile(2029, 1077),
            new Tile(2025, 1076),
            new Tile(2024, 1073)
    };

    public static boolean droppedChocSlice = false;
    public static int inventSpotsFree;
    public static int usedInvent = 0;
    public static int inventUsed;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("World hopping"));
        hopEnabled = Boolean.valueOf((configs.get("World hopping.enabled")));
        useWDH = Boolean.valueOf((configs.get("World hopping.useWDH")));
        bankYN= Boolean.valueOf((configs.get("Banking")));

        Logger.log("Thank you for using the dCakeThiever script!\nSetting up everything for your gains now...");
        Logger.debugLog("Selected hopProfile: " + hopProfile);

        // Set the walker to use ardy region
        Walker.setup(ardyRegion);

        hopActions();
        initialSetup();
    }

    // Task list!
    List<Task> ThievingTasks = Arrays.asList(
            new Hop(),
            new PerformThieving(),
            new Drop(),
            new Bank(),
            new RunBetween()
    );

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {
        for (Task task : ThievingTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    private void initialSetup() {
        Logger.debugLog("Starting initialSetup() method.");

        // Check if we are within the script area, otherwise stop the script.

        if (Player.within(scriptArea) || Player.within(bankArea)) {
            Logger.debugLog("We are located in the Ardougne market area needed for the script to run. Continuing... ");

            // Open inventory if not yet open
            if (!GameTabs.isInventoryTabOpen()) {
                GameTabs.openInventoryTab();
            }

            // Check the amount of inventory spots free (to know when to drop items)
            inventSpotsFree = Inventory.emptySlots();
            usedInvent = Inventory.usedSlots();

            // Close the chat area
            Chatbox.closeChatbox();

            // Enable tap to drop if not yet enabled
            if (!Game.isTapToDropEnabled()) {
                Game.enableTapToDrop();
            }

        } else {
            Logger.log("Could not locate us to the script area in the Ardougne market area. Please move there and start the script again.");
            Logout.logout();
            Script.stop();
        }

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        } else {
            // We do nothing here, as hop is disabled.
        }
    }

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

}