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
        name = "dMinnowsFisher",
        description = "Fishes Minnows at Kylie Minnow's fishing platform at the Fishing Guild. The angler's outfit is needed to unlock the platform.",
        version = "2.00",
        guideLink = "https://wiki.mufasaclient.com/docs/dminnows-fisher/",
        categories = {ScriptCategory.Fishing, ScriptCategory.Moneymaking}
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

public class dMinnowsFisher extends AbstractScript {
    public static String hopProfile;
    public static Boolean hopEnabled;
    private boolean antiBan;
    private boolean extendedAntiBan;
    public static boolean setupDone = false;
    public static boolean sharkPresent = false;
    public static Instant lastSharkAction = Instant.now();
    public static final Area minnowPlatform = new Area(
            new Tile(10417, 13547, 0),
            new Tile(10506, 13489, 0)
    );
    public static final Area fishingGuild = new Area(
            new Tile(10307, 13463, 0),
            new Tile(10503, 13312, 0)
    );
    public static  Instant lastXpGainTime = Instant.now().minusSeconds(15);
    public static int previousXP;
    public static int newXP;
    public static int minnowStartCount = 0;
    public static int minnowGainedCount = 0;
    public static final Tile boatTile = new Tile(10399, 13449, 0);
    public static final Rectangle boatTapRect = new Rectangle(421, 178, 61, 32);
    private static long lastRunTime = System.currentTimeMillis();
    private static Random random = new Random();
    public static Tile playerPosition;

    // Check rectangles if we have a spot against us
    static Rectangle rightRect = new Rectangle(465, 267, 57, 22);
    static Rectangle leftRect = new Rectangle(379, 273, 56, 21);
    static Rectangle topRect = new Rectangle(443, 219, 23, 42);
    static Rectangle bottomRect = new Rectangle(441, 303, 24, 43);

    // PaintBar stuff we need
    public static long currentTime = System.currentTimeMillis();
    public static long startTime;
    public static double elapsedTimeInHours;
    public static double minnowsPerHour;
    public static double sharksPerHour;
    public static int minnowIndex;
    public static int sharkIndex;
    public static int profitIndex;
    public static int PROCESS_COUNT = 0;
    public static int PROCESS_COUNT2 = 0;
    public static int sharkPrice = 700;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        antiBan = Boolean.valueOf(configs.get("Run anti-ban"));
        extendedAntiBan = Boolean.valueOf(configs.get("Run extended anti-ban"));

        Logger.log("Thank you for using the dMinnows Fisher script!");
        Logger.log("Setting up everything for your gains now...");

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"40-53"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create a single image box, to show the amount of processed items
        minnowIndex = Paint.createBox("Minnows", 10570, minnowGainedCount);
        Condition.sleep(400, 600);
        sharkIndex = Paint.createBox("Raw sharks", ItemList.RAW_SHARK_383, 0);
        Condition.sleep(400, 600);
        profitIndex = Paint.createBox("Profit", ItemList.STONKS_99960, 0);

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
            new FailSafe(),
            new HandleShark(),
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
        return timeSinceLastXpGain >= 3200 || !isSpotAgainstUs();
    }

    public static boolean isSpotAgainstUs() {
        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Check if 5 seconds have passed since the last run
        if (currentTime - lastRunTime < 5000) {
            // If less than 5 seconds, return true by default
            return true;
        }

        // Update the last run time to the current time
        lastRunTime = currentTime;

        // Check if the color is present in the right or bottom rectangles
        return Client.isColorInRect(OverlayColor.FISHING, rightRect, 5) ||
                Client.isColorInRect(OverlayColor.FISHING, bottomRect, 5) ||
                Client.isColorInRect(OverlayColor.FISHING, topRect, 5) ||
                Client.isColorInRect(OverlayColor.FISHING, leftRect, 5);
    }
}