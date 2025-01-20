package main;

import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.Update;
import tasks.Fish;
import tasks.Setup;
import utils.Task;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dKarambwanji Fisher",
        description = "Fishes Karambwanji at Karamja to use as bait for Karambwans. Has a safe option for low HP accounts (keep in mind, this slows down the catch rate).",
        version = "2.01",
        guideLink = "https://wiki.mufasaclient.com/docs/dkarambwanji-fisher/",
        categories = {ScriptCategory.Fishing}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use safe mode?",
                        description = "Would you like to enable to low HP level safe mode? (this results in less karambwanjis an hour). This only uses the N/NE/E spots, and ignores the S and other spots.",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name = "Run anti-ban",
                        description = "Would you like to run anti-ban features?",
                        defaultValue = "true",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name = "Run extended anti-ban",
                        description = "Would you like to run additional, extended anti-ban like some additional AFK patterns, on TOP of the regular anti-ban?",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class dKarambwanjiFisher extends AbstractScript {
    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    private boolean antiBan;
    private boolean extendedAntiBan;
    public static boolean setupDone = false;
    public static Area FishingArea = new Area(
            new Tile(11109, 11740, 0),
            new Tile(11279, 11875, 0)
    );
    public static String FishingSpot;
    public static Boolean SafeModeOn;
    public static  Instant lastXpGainTime = Instant.now().minusSeconds(15);
    public static int previousXP;
    public static int newXP;
    public static int karambwanjiStartCount = 0;
    public static int karambwanjiGainedCount = 0;
    public static Tile SouthSpot = new Tile(11203, 11785, 0);
    public static Tile NorthEastSpot = new Tile(11231, 11833, 0);
    public static Tile NorthWestSpot = new Tile(11163, 11829, 0);
    public static Tile EastSpot = new Tile(11243, 11817, 0);

    public static Tile[] fishingSpots = new Tile[] {NorthEastSpot, EastSpot, NorthWestSpot, SouthSpot};
    public static  Instant lastActionTime = Instant.now();

    // PaintBar stuff we need
    public static long currentTime = System.currentTimeMillis();
    public static long startTime;
    public static double elapsedTimeInHours;
    public static double itemsPerHour;
    public static int productIndex;
    public static int PROCESS_COUNT = 0;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        SafeModeOn = Boolean.valueOf((configs.get("Use safe mode?")));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        antiBan = Boolean.valueOf(configs.get("Run anti-ban"));
        extendedAntiBan = Boolean.valueOf(configs.get("Run extended anti-ban"));

        Logger.log("Thank you for using the dKarambwanjiFisher script!\nSetting up everything for your gains now...");

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"43-47"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create a single image box, to show the amount of processed items
        productIndex = Paint.createBox("Karambwanji", ItemList.RAW_KARAMBWANJI_3150, karambwanjiGainedCount);

        if (antiBan) {
            Logger.debugLog("Initializing anti-ban timer");
            Game.antiBan();
            if (extendedAntiBan) {
                Logger.debugLog("Initializing extended anti-ban timer");
                Game.enableOptionalAntiBan(AntiBan.EXTENDED_AFK);
                Game.antiBan();
            }
        }

        hopActions();
        startTime = System.currentTimeMillis();
    }

    // This is the main part of the script, poll gets looped constantly
    List<Task> fishingTasks = Arrays.asList(
            new Setup(),
            new Update(),
            new Fish()
    );

    @Override
    public void poll() {

        // Check if it's time to hop
        hopActions();

        // Read XP
        readXP();

        if (antiBan) {
            Game.antiBan();
        }

        // Check for inactivity
        if (Duration.between(lastActionTime, Instant.now()).toMinutes() >= 4) {
            Game.antiAFK();
            hopActions();
            lastActionTime = Instant.now();
        }

        // Open inventory tab
        GameTabs.openTab(UITabs.INVENTORY);

        //Run tasks
        for (Task task : fishingTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }


    public static void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        }
    }

    private void readXP() {
        newXP = XpBar.getXP();

        if (previousXP == 0 || previousXP == -1) {
            // If previousXP is null, initialize it with the current XP
            previousXP = newXP;
        } else if (previousXP != newXP) {
            // If previousXP and newXP are different, update lastXpGainTime and set previousXP to newXP
            lastXpGainTime = Instant.now();
            previousXP = newXP;
        }
    }

    public static boolean shouldFish() {
        Paint.setStatus("Check fish state");
        long timeSinceLastXpGain = Duration.between(lastXpGainTime, Instant.now()).getSeconds();
        return timeSinceLastXpGain >= 15;
    }
}