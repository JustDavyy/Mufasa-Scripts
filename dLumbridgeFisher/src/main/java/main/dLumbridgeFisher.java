package main;

import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.Task;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dLumbridgeFisher",
        description = "Fishes and optionally cooks in Lumbridge for early starter levels",
        version = "1.00",
        guideLink = "https://wiki.mufasaclient.com/docs/dlumbridgefisher/",
        categories = {ScriptCategory.Fishing, ScriptCategory.Cooking}
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
                        name = "Cook fish",
                        description = "Would you like to cook the fish caught?",
                        defaultValue = "true",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name = "Stop at",
                        description = "What level would you like to stop the script at?",
                        defaultValue = "20",
                        minMaxIntValues = {2, 99},
                        optionType = OptionType.INTEGER_SLIDER
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

public class dLumbridgeFisher extends AbstractScript {
    public static String hopProfile;
    public static Boolean hopEnabled;
    private boolean antiBan;
    private boolean extendedAntiBan;
    public static  Instant lastXpGainTime = Instant.now().minusSeconds(15);
    public static int previousXP;
    public static int newXP;
    public static boolean cookEnabled = false;
    public static int stopAt;
    public static boolean setupDone = false;
    public static final Area lumbridgeArea = new Area(
            new Tile(12796, 12697, 0),
            new Tile(13026, 12282, 0)
    );
    public static final Area fishingArea = new Area(
            new Tile(12939, 12387, 0),
            new Tile(13001, 12305, 0)
    );
    public static int fishingLevel;
    public static int cookingLevel;
    public static boolean countedFood = false;
    public static Tile playerPosition;
    private static long lastRunTime = System.currentTimeMillis();

    // Tiles
    public final static Tile northFishingTile = new Tile(12963, 12345, 0);
    public final static Tile southFishingTile = new Tile(12963, 12321, 0);

    // Check rectangles if we have a spot against us
    static Rectangle rightRect = new Rectangle(458, 279, 56, 18);
    static Rectangle bottomRect = new Rectangle(441, 303, 24, 43);

    // PaintBar stuff we need
    public static long currentTime = System.currentTimeMillis();
    public static long startTime;
    public static double elapsedTimeInHours;
    public static int rawShrimpIndex;
    public static int rawAnchoviesIndex;
    public static int cookedShrimpIndex;
    public static int cookedAnchoviesIndex;
    public static int burnedFishIndex;
    public static int shrimpGainedCount = 0;
    public static int anchoviesGainedCount = 0;
    public static int shrimpCookedCount = 0;
    public static int anchoviesCookedCount = 0;
    public static int burnedFishCount = 0;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        antiBan = Boolean.parseBoolean(configs.get("Run anti-ban"));
        extendedAntiBan = Boolean.parseBoolean(configs.get("Run extended anti-ban"));
        cookEnabled = Boolean.parseBoolean(configs.get("Cook fish"));
        stopAt = Integer.parseInt((configs.get("Stop at")));

        Logger.log("Thank you for using the dLumbridgeFisher script!");
        Logger.log("Setting up everything for your gains now...");

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"50-49"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create a single image box, to show the amount of processed items
        rawShrimpIndex = Paint.createBox("Raw Shrimps", ItemList.RAW_SHRIMPS_317, shrimpGainedCount);
        Condition.sleep(400, 600);
        rawAnchoviesIndex = Paint.createBox("Raw Anchovies", ItemList.RAW_ANCHOVIES_321, anchoviesGainedCount);
        Condition.sleep(400, 600);
        cookedShrimpIndex = Paint.createBox("Cooked Shrimps", ItemList.SHRIMPS_315, shrimpCookedCount);
        Condition.sleep(400, 600);
        cookedAnchoviesIndex = Paint.createBox("Cooked Anchovies", ItemList.ANCHOVIES_319, anchoviesCookedCount);
        Condition.sleep(400, 600);
        burnedFishCount = Paint.createBox("Burned Fish", ItemList.BURNT_FISH_323, burnedFishCount);
        Condition.sleep(400, 600);

        if (antiBan) {
            Logger.debugLog("Initializing anti-ban timer");
            Game.antiBan();
            if (extendedAntiBan) {
                Logger.debugLog("Initializing extended anti-ban timer");
                Game.enableOptionalAntiBan(AntiBan.EXTENDED_AFK);
                Game.antiBan();
            }
        }

        startTime = System.currentTimeMillis();
    }

    // This is the main part of the script, poll gets looped constantly
    List<Task> fishingTasks = Arrays.asList(
            new Setup(),
            new Fish(),
            new Cook(),
            new Drop(),
            new Update()
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
            Game.hop(hopProfile, false, false);
        }
    }

    private void readXP() {
        newXP = XpBar.getXP();

        if (previousXP == 0 || previousXP == -1) {
            // If previousXP is null, initialize it with the current XP
            previousXP = newXP;
            lastXpGainTime = Instant.now();
        } else if (previousXP != newXP) {
            // If previousXP and newXP are different, update lastXpGainTime and set previousXP to newXP
            lastXpGainTime = Instant.now();
            previousXP = newXP;
        }
    }

    public static boolean shouldFish() {
        long timeSinceLastXpGain = Duration.between(lastXpGainTime, Instant.now()).toMillis();

        // Check if we should fish based on time since last XP gain, or if the spot is no longer available
        return timeSinceLastXpGain >= 20000 || !isSpotAgainstUs();
    }

    public static boolean isSpotAgainstUs() {
        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Check if 5 seconds have passed since the last run
        if (currentTime - lastRunTime < 5000) {
            return true;
        }

        // Update the last run time to the current time
        lastRunTime = currentTime;

        return Client.isColorInRect(OverlayColor.FISHING, rightRect, 5) ||
                Client.isColorInRect(OverlayColor.FISHING, bottomRect, 5);
    }
}