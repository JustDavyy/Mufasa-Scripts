package main;

import Tasks.*;
import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import utils.FontGOTR;
import utils.PortalLocation;
import utils.StateUpdater;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dmGOTR",
        description = "Does Guardians of the Rift minigame.",
        version = "1.00",
        guideLink = "",
        categories = {ScriptCategory.Fletching, ScriptCategory.Agility},
        skipClientSetup = true
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name = "Use pouches?",
                        description = "Do you want to use pouches?",
                        defaultValue = "False",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name = "Do Cosmic runes?",
                        description = "This requires having Lost City completed.",
                        defaultValue = "False",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name = "Do Law runes?",
                        description = "This requires having Troll Stronghold completed.",
                        defaultValue = "False",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name = "Do Death runes?",
                        description = "This requires having Mourning's End Part II completed.",
                        defaultValue = "False",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name = "Do Blood runes?",
                        description = "This requires having Sins of the Father completed.",
                        defaultValue = "False",
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class dmGOTR extends AbstractScript {
    StateUpdater stateUpdater = new StateUpdater();

    // REGIONS
    public static Area gameArea = new Area(
            new Tile(14281, 37673, 0),
            new Tile(14639, 37836, 0)
    );
    public static Area gameLobby = new Area(
            new Tile(14398, 37593, 0),
            new Tile(14517, 37672, 0)
    );

    // RECTANGLES
    public static Rectangle ELEMENTAL_RUNE_RECT = new Rectangle(107, 48, 23, 21);
    public static Rectangle CATALYTIC_RUNE_RECT = new Rectangle(188, 49, 24, 18);
    public static Rectangle PORTAL_CHECK_RECT = new Rectangle(271, 80, 20, 18);
    public static Rectangle PORTAL_READ_RECT = new Rectangle(276, 104, 39, 26);
    public static Rectangle PORTAL_LOCATION_READ_RECT = new Rectangle(248, 102, 34, 32);
    public static Rectangle GUARDIAN_POWER_READ_RECT = new Rectangle(186, 12, 38, 15);
    public static Rectangle TIMER_READ_RECT = new Rectangle(137, 47, 43, 27);

    public static Tile currentLocation;

    // SETTINGS
    public static boolean doCosmics;
    public static boolean doLaws;
    public static boolean doDeaths;
    public static boolean doBloods;
    public static boolean usePreGameMineArea;
    public static boolean usePouches;
    public static boolean portalActive;
    public static PortalLocation portalLocation = PortalLocation.NONE;

    // INTS
    public static int agilityLevel;
    public static int guardiansPower;
    public static int portalTime;

    // COLORS
    public static List<Color> blackColor = Arrays.asList(
        java.awt.Color.decode("#000001")
    );

    public static List<Color> whiteColor = Arrays.asList(
            java.awt.Color.decode("#ffffff")
    );

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        doCosmics = Boolean.parseBoolean(configs.get("Do Cosmic runes?"));
        doLaws = Boolean.parseBoolean(configs.get("Do Law runes?"));
        doDeaths = Boolean.parseBoolean(configs.get("Do Death runes?"));
        doBloods = Boolean.parseBoolean(configs.get("Do Blood runes?"));
        usePouches = Boolean.parseBoolean(configs.get("Use pouches?"));

        // 55-148, 55-147, 56-147, 56-148, 56-149, 57-149, 57-148, 57-147, 44-75, 42-75, 41-75, 40-75, 39-75, 38-75, 37-75, 36-75, 35-75, 34-75, 33-75, 32-75, 43-75, 50-75
        Walker.setup(new MapChunk(new String[]{"55-148", "55-147", "56-147", "56-148", "56-149", "57-149", "57-148", "57-147", "44-75", "42-75", "41-75", "41-75", "40-75", "39-75", "38-75", "37-75", "36-75", "35-75", "34-75", "33-75", "32-75", "43-75", "50-75"}, "0"));

        // Open stats tab to check stats
        if (!GameTabs.isTabOpen(UITabs.STATS)) {
            GameTabs.openTab(UITabs.STATS);
            Condition.wait(() -> GameTabs.isTabOpen(UITabs.STATS), 100, 20);
        }

        if (GameTabs.isTabOpen(UITabs.STATS)) {
            agilityLevel = Stats.getRealLevel(Skills.AGILITY); //Get the agi level
        }

        if (agilityLevel < 56) {
            Logger.log("Agility level below 56, not using east mine in pre-game");
            usePreGameMineArea = false;
        }

        // Make sure chatbox is closed
        Chatbox.closeChatbox();

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/dm.png");
    }

    // Task list!
    List<Task> gotrTasks = Arrays.asList(
            new CheckGear(),
            new BreakManager(stateUpdater),
            new PreGame(stateUpdater),
            new HandleAltars(stateUpdater),
            new GoToAltar(stateUpdater),
            new DepositRunes(stateUpdater),
            new HandlePouches(stateUpdater),
            new ProcessEssence(stateUpdater),
            new MineEssence(stateUpdater),
            new EnterGame(stateUpdater)
    );

    @Override
    public void poll() {
        if (!GameTabs.isTabOpen(UITabs.INVENTORY)) {
            GameTabs.openTab(UITabs.INVENTORY);
            Condition.wait(() -> GameTabs.isTabOpen(UITabs.INVENTORY), 100, 20);
        }

        stateUpdater.updateAllStates();

        if (stateUpdater.isGameGoing()) {
            Logger.debugLog("Active elemental rune: " + stateUpdater.getElementalRune().getName());
            Logger.debugLog("Active catalytic rune: " + stateUpdater.getCatalyticRune().getName());
            Logger.debugLog("Guardians power: " + stateUpdater.getGuardiansPower());
            Logger.debugLog("Portal active: " + stateUpdater.isPortalActive());
            if (stateUpdater.isPortalActive()) {
                Logger.debugLog("Portal time left: " + stateUpdater.getPortalTime());
                Logger.debugLog("Portal location: " + stateUpdater.getPortalLocation());
            }
            Logger.debugLog("Time till next rune switch: " + stateUpdater.timeTillRuneSwitch());
        } else {
            Logger.debugLog("Game is currently NOT going.");
        }


        Condition.sleep(1500, 2500);

        //Run tasks
        //for (Task task : gotrTasks) {
        //    if (task.activate()) {
        //        task.execute();
        //        return;
        //    }
        //}
    }
}