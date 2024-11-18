package main;

import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.Task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dCamTorumMiner",
        description = "Mines the calcified rocks in the Cam Torum mine",
        version = "1.02",
        guideLink = "https://wiki.mufasaclient.com/docs/dcamtorumminer/",
        categories = {ScriptCategory.Ironman, ScriptCategory.Mining, ScriptCategory.Prayer}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "World hopping",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dCamTorumMiner extends AbstractScript {
    // Creating the strings for later use
    public static String hopProfile;
    public static Boolean hopEnabled;
    Boolean useWDH;

    public static Area scriptArea = new Area(
            new Tile(6043, 37889, 1),
            new Tile(6098, 37943, 1)
    );

    public static long lastAction = System.currentTimeMillis();
    public static long startTime;
    public static int startShardCount = 0;
    public static int currentStack = 0;
    public static int earnedStack = 0;
    public static int shardIndex;
    public static int prayIndex;


    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("World hopping"));
        hopEnabled = Boolean.valueOf((configs.get("World hopping.enabled")));
        useWDH = Boolean.valueOf((configs.get("World hopping.useWDH")));

        Logger.log("Thank you for using the dCamTorumMiner script!\nSetting up everything for your gains now...");
        Logger.debugLog("Selected hopProfile: " + hopProfile);

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"23-149"}, "1");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        hopActions();

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Set the two top headers of paintUI.
        Paint.setStatus("Initializing...");

        shardIndex = Paint.createBox("Bl. Bone Shard", ItemList.BLESSED_BONE_SHARDS_29381, 0);
        Condition.sleep(500);
        prayIndex = Paint.createBox("Prayer XP", ItemList.SKILL_PRAYER_ICON_99994, 0);
        Condition.sleep(500);

        if (!Player.within(scriptArea)) {
            Logger.log("Not within script area, stopping script.\n Please start the script when standing inside the Cam Torum mine (north-east part)");
            Logout.logout();
            Script.stop();
        }

        // Initialize AFK timer
        Game.antiAFK();

        // Close chatbox
        Chatbox.closeChatbox();

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        XpBar.getXP();

        startShardCount = Inventory.stackSize(ItemList.BLESSED_BONE_SHARDS_29381);

        startTime = System.currentTimeMillis();
    }

    // Task list!
    List<Task> tasks = Arrays.asList(
            new Drop(),
            new Mine(),
            new AFK()
    );

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        hopActions();
        XpBar.getXP();

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        for (Task task : tasks) {
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
}