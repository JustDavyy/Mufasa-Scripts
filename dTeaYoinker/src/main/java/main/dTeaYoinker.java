package main;

import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;

import java.util.Random;

@ScriptManifest(
        name = "dTeaYoinker",
        description = "Steals from the tea stall in east Varrock. Supports world hopping and banking the tea.",
        version = "2.00",
        guideLink = "https://wiki.mufasaclient.com/docs/dtea-yoinker/",
        categories = {ScriptCategory.Thieving}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Bank the stolen teas?",
                        description = "Would you like to bank the stolen tea?",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dTeaYoinker extends AbstractScript {
    public static Random random = new Random();
    // Creating the strings for later use
    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    public static Boolean bankYN;
    public static Tile playerPos;

    public static Tile stallTile = new Tile(13071, 13389, 0);
    public static Tile bankTile1 = new Tile(13007, 13429, 0);
    public static Tile bankTile2 = new Tile(13011, 13429, 0);
    public static Tile bankTile3 = new Tile(13015, 13429, 0);
    public static Tile middleTile = new Tile(13051, 13425, 0);
    public static Rectangle boothArea = new Rectangle(432, 307, 19, 24);
    public static Rectangle stallTapWindow = new Rectangle(493, 212, 56, 75);

    Area scriptArea = new Area(
            new Tile(12955, 13374, 0),
            new Tile(13098, 13490, 0)
    );

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        bankYN= Boolean.valueOf((configs.get("Bank the stolen teas?")));

        Logger.log("Thank you for using the dTeaYoinker script!\nSetting up everything for your gains now...");

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"51-53"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        if (Player.within(scriptArea)) {
            Logger.debugLog("We are located in the Varrock area needed for the script to run. Continuing... ");
        } else {
            Logger.log("Player not within the varrock tea stall script area, stopping script!");
            Logout.logout();
            Script.stop();
        }
    }

    // Task list!
    List<Task> ThievingTasks = Arrays.asList(
            new PerformThieving(),
            new Drop(),
            new Bank()
    );

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        GameTabs.openTab(UITabs.INVENTORY);
        hopActions();
        readXP();

        for (Task task : ThievingTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
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

    private void readXP() {
        XpBar.getXP();
    }

}