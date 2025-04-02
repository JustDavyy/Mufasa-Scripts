package dOttos;


import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import dOttos.Tasks.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dOttos",
        description = "Barb fishes at Otto's Grotto.",
        version = "1.8",
        categories = {ScriptCategory.Fishing},
        guideLink = "https://wiki.mufasaclient.com/docs/mdottos/"
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings? WDH is disabled for this script, as there's users on every world.",
                        defaultValue = "1",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dOttos extends AbstractScript {
    private final ArrayList<Task> taskList = new ArrayList<>();
    public static int fishingLevel;
    public static int strengthLevel;
    public static int agilityLevel;
    String hopProfile;
    Boolean hopEnabled;
    public static int previousXP;
    public static int newXP;
    public static Tile northSpot = new Tile(10015, 13817, 0);
    public static Tile westSpot = new Tile(9995, 13785, 0);
    public static Instant lastXpGainTime = Instant.now().minusSeconds(15);
    public static Instant lastActionTime = Instant.now();
    public static boolean doneDropping = false;
    public static Area scriptArea = new Area(
            new Tile(9956, 13682, 0),
            new Tile(10111, 13856, 0)
    );
    public static Area fishingArea3 = new Area(
            new Tile(10053, 13803, 0),
            new Tile(10107, 13839, 0)
    );

    // Paint stuff
    public static int troutIndex;
    public static int salmonIndex;
    public static int sturgeonIndex;
    public static int fishxpIndex;
    public static int agilxpIndex;
    public static int strengthxpIndex;
    public static int troutAmount = 0;
    public static int salmonAmount = 0;
    public static int sturgeonAmount = 0;

    @Override
    public void onStart(){
        Logger.log("Initialising dOttos...");

        // Build task list
        taskList.add(new Fish(this));
        taskList.add(new Drop(this));

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"39-54"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Grab script config stuff
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        if (!Player.within(scriptArea)){
            Logger.log("Not at fishing area, stopping script.");
            Logout.logout();
            Script.stop();
        }
        checkLevelReqs(); // check lvl 48 fishing
        checkEquipment(); // Check barb rod & > 50 feathers

        if (!Game.isTapToDropEnabled()) {
            Game.enableTapToDrop();
        }

        Chatbox.closeChatbox();

        // Paint stuff here
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create all image boxes with a 500ms delay between each one
        troutIndex = Paint.createBox("Leaping Trout", ItemList.LEAPING_TROUT_11328, troutAmount);
        Condition.sleep(500);
        salmonIndex = Paint.createBox("Leaping Salmon", ItemList.LEAPING_SALMON_11330, salmonAmount);
        Condition.sleep(500);
        sturgeonIndex = Paint.createBox("Leaping Sturgeon", ItemList.LEAPING_STURGEON_11332, sturgeonAmount);
        Condition.sleep(500);
        fishxpIndex = Paint.createBox("Fishing XP", ItemList.SKILL_FISHING_ICON_99987, 0);
        Condition.sleep(500);
        agilxpIndex = Paint.createBox("Agility XP", ItemList.SKILL_AGILITY_ICON_99983, 0);
        Condition.sleep(500);
        strengthxpIndex = Paint.createBox("Strength XP", ItemList.SKILL_STRENGHT_ICON_99998, 0);

        Paint.setStatus("Initializing...");

        Logger.log("Starting dOttos!");
    }

    @Override
    public void poll() {
        // Looped tasks go here.
        hopActions();
        readXP();
        for (Task task : taskList) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    public static void checkEquipment() {
        Logger.debugLog("Checking inventory has rod & feathers.");
        if(!GameTabs.isTabOpen(UITabs.INVENTORY)) {
            GameTabs.openTab(UITabs.INVENTORY);
        }
        if (Inventory.stackSize(314) < 50){
            Logger.log("Less than 50 feathers, stopping script.");
            Logout.logout();
            Script.stop();
        }
        if (!Inventory.contains(11323, 0.8)){
            Logger.log("No barbarian rod, stopping script.");
            Logout.logout();
            Script.stop();
        }
        Logger.debugLog("Amount of feathers: " + Inventory.stackSize(314));
        Logger.debugLog("Inventory has required equipment, continuing..");
    }
    public void checkLevelReqs(){
        Logger.debugLog("Checking level requirements.");
        if(!GameTabs.isTabOpen(UITabs.STATS)) {
            GameTabs.openTab(UITabs.STATS);
        }

        fishingLevel = Stats.getRealLevel(Skills.FISHING);
        strengthLevel = Stats.getRealLevel(Skills.STRENGTH);
        agilityLevel = Stats.getRealLevel(Skills.AGILITY);
        Logger.debugLog("Fishing level " + fishingLevel);
        Logger.debugLog("Strength level " + strengthLevel);
        Logger.debugLog("Agility level " + agilityLevel);
        if(fishingLevel < 48){
            Logger.log("Fishing level not high enough, stopping script.");
            Logout.logout();
            Script.stop();
        }
        if(strengthLevel < 15) {
            Logger.log("Strength level not high enough, stopping script.");
            Logout.logout();
            Script.stop();
        }
        if(agilityLevel < 15) {
            Logger.log("Agility level not high enough, stopping script.");
            Logout.logout();
            Script.stop();
        }

        GameTabs.closeTab(UITabs.STATS);
        Logger.debugLog("Ending level requirements.");
    }
    public static boolean shouldFish() {
        long timeSinceLastXpGain = Duration.between(lastXpGainTime, Instant.now()).getSeconds();
        return timeSinceLastXpGain >= 18;
    }
    public static void readXP() {
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
    public void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, false, false);
        }
    }
}