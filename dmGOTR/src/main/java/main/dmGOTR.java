package main;

import Tasks.*;
import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.annotations.ScriptTabConfiguration;
import helpers.utils.*;
import utils.PortalLocation;
import utils.RuneInfo;
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
        categories = {ScriptCategory.Runecrafting, ScriptCategory.Minigames},
        skipClientSetup = true
)
@ScriptTabConfiguration.List({
        @ScriptTabConfiguration(
                name = "Rune setup",
                configurations = {
                        @ScriptConfiguration(
                                name = "Description",
                                description = "Select which runes you would like to to craft",
                                defaultValue = "",
                                optionType = OptionType.DESCRIPTION
                        ),
                        @ScriptConfiguration(
                                name = "Cosmic runes",
                                description = "Would you like to craft Cosmic Runes? This requires having Lost City completed.",
                                defaultValue = "False",
                                optionType = OptionType.BOOLEAN
                        ),
                        @ScriptConfiguration(
                                name = "Law runes",
                                description = "Would you like to craft Law Runes? This requires having Troll Stronghold completed.",
                                defaultValue = "False",
                                optionType = OptionType.BOOLEAN
                        ),
                        @ScriptConfiguration(
                                name = "Death runes",
                                description = "Would you like to craft Death Runes? This requires having Mourning's End Part II completed.",
                                defaultValue = "False",
                                optionType = OptionType.BOOLEAN
                        ),
                        @ScriptConfiguration(
                                name = "Blood runes",
                                description = "Would you like to craft Blood Runes? This requires having Sins of the Father completed.",
                                defaultValue = "False",
                                optionType = OptionType.BOOLEAN
                        )
                }
        )
})
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name = "Fragments",
                        description = "How many fragments would you like to mine at the start?",
                        defaultValue = "165",
                        minMaxIntValues = {50, 200},
                        optionType = OptionType.INTEGER_SLIDER
                ),
                @ScriptConfiguration(
                        name = "Pouches",
                        description = "Do you want to use pouches?",
                        defaultValue = "False",
                        optionType = OptionType.BOOLEAN
                ),
        }
)

public class dmGOTR extends AbstractScript {
    StateUpdater stateUpdater = new StateUpdater();

    // AREAS
    public static Area gameArea = new Area(
            new Tile(14281, 37673, 0),
            new Tile(14639, 37836, 0)
    );
    public static Area gameLobby = new Area(
            new Tile(14398, 37593, 0),
            new Tile(14517, 37672, 0)
    );

    public static Area airAltarArea = new Area(
            new Tile(11274, 19195, 0),
            new Tile(11454, 18951, 0)
    );
    public static Area waterAltarArea = new Area(
            new Tile(10757, 19193, 0),
            new Tile(11003, 18948, 0)
    );
    public static Area earthAltarArea = new Area(
            new Tile(10506, 19195, 0),
            new Tile(10747, 18953, 0)
    );
    public static Area fireAltarArea = new Area(
            new Tile(10217, 19195, 0),
            new Tile(10493, 18950, 0)
    );
    public static Area mindAltarArea = new Area(
            new Tile(11019, 19188, 0),
            new Tile(11237, 19001, 0)
    );
    public static Area bodyAltarArea = new Area(
            new Tile(10005, 19185, 0),
            new Tile(10172, 19027, 0)
    );
    public static Area cosmicAltarArea = new Area(
            new Tile(8457, 19195, 0),
            new Tile(8694, 18963, 0)
    );
    public static Area chaosAltarArea = new Area(
            new Tile(8977, 19195, 0),
            new Tile(9199, 18980, 0)
    );
    public static Area natureAltarArea = new Area(
            new Tile(9488, 19193, 0),
            new Tile(9700, 18972, 0)
    );
    public static Area lawAltarArea = new Area(
            new Tile(9747, 19193, 0),
            new Tile(9966, 18963, 0)
    );
    public static Area deathAltarArea = new Area(
            new Tile(8707, 19195, 0),
            new Tile(8958, 18989, 0)
    );
    public static Area bloodAltarArea = new Area(
            new Tile(12806, 19192, 0),
            new Tile(13046, 18952, 0)
    );
    public static Area cosmicAltarNorth = new Area(
            new Tile(8548, 19186, 0),
            new Tile(8596, 19140, 0)
    );
    public static Area cosmicAltarEast = new Area(
            new Tile(8631, 19104, 0),
            new Tile(8675, 19054, 0)
    );
    public static Area cosmicAltarSouth = new Area(
            new Tile(8547, 19017, 0),
            new Tile(8591, 18973, 0)
    );
    public static Area cosmicAltarWest = new Area(
            new Tile(8464, 19100, 0),
            new Tile(8509, 19058, 0)
    );

    // PATHS
    public static final Tile[] agilShortcutToWorkbench = new Tile[] {
            new Tile(14518, 37745, 0),
            new Tile(14502, 37730, 0),
            new Tile(14487, 37723, 0),
            new Tile(14465, 37715, 0),
            new Tile(14448, 37707, 0)
    };

    public static final Tile[] entranceToShortcut = new Tile[] {
            new Tile(14464, 37695, 0),
            new Tile(14483, 37705, 0),
            new Tile(14495, 37719, 0),
            new Tile(14514, 37728, 0),
            new Tile(14521, 37739, 0),
            new Tile(14531, 37754, 0)
    };

    // GENERAL RECTANGLES
    public static Rectangle ELEMENTAL_RUNE_RECT = new Rectangle(107, 48, 23, 21);
    public static Rectangle CATALYTIC_RUNE_RECT = new Rectangle(188, 49, 24, 18);
    public static Rectangle ELEMENTAL_POINTS_RECT = new Rectangle(127, 88, 39, 29);
    public static Rectangle CATALYTIC_POINTS_RECT = new Rectangle(204, 88, 47, 29);
    public static Rectangle PORTAL_CHECK_RECT = new Rectangle(271, 80, 20, 18);
    public static Rectangle PORTAL_READ_RECT = new Rectangle(276, 104, 39, 26);
    public static Rectangle PORTAL_LOCATION_READ_RECT = new Rectangle(248, 102, 34, 32);
    public static Rectangle GUARDIAN_POWER_READ_RECT = new Rectangle(186, 12, 38, 15);
    public static Rectangle TIMER_READ_RECT = new Rectangle(137, 47, 43, 27);
    public static Rectangle UNCHARGED_CELL_TABLE_TAP_RECT = new Rectangle(463, 261, 10, 9);
    public static Rectangle WORKBENCH_TAP_RECT = new Rectangle(426, 280, 19, 19);
    public static Rectangle DEPOSIT_POOL_TAP_RECT = new Rectangle(441, 276, 7, 11);
    public static Rectangle CELL_PLACE_TAP_RECT = new Rectangle(463, 267, 6, 6);

    // GUARDIANS RECTANGLES
    public static Rectangle MIND_GUARDIAN_TAP_RECT = new Rectangle(428, 275, 12, 18);
    public static Rectangle BODY_GUARDIAN_TAP_RECT = new Rectangle(410, 258, 11, 18);
    public static Rectangle CHAOS_GUARDIAN_TAP_RECT = new Rectangle(411, 246, 13, 15);
    public static Rectangle DEATH_GUARDIAN_TAP_RECT = new Rectangle(411, 246, 13, 15);
    public static Rectangle LAW_GUARDIAN_TAP_RECT = new Rectangle(428, 230, 10, 16);
    public static Rectangle BLOOD_GUARDIAN_TAP_RECT = new Rectangle(453, 229, 10, 19);
    public static Rectangle FIRE_GUARDIAN_TAP_RECT = new Rectangle(435, 230, 10, 15);
    public static Rectangle NATURE_GUARDIAN_TAP_RECT = new Rectangle(451, 234, 12, 17);
    public static Rectangle EARTH_GUARDIAN_TAP_RECT = new Rectangle(470, 250, 10, 19);
    public static Rectangle WATER_GUARDIAN_TAP_RECT = new Rectangle(470, 250, 10, 19);
    public static Rectangle COSMIC_GUARDIAN_TAP_RECT = new Rectangle(468, 267, 10, 15);
    public static Rectangle AIR_GUARDIAN_TAP_RECT = new Rectangle(448, 283, 12, 18);

    // ALTAR TAP RECTANGLES
    public static Rectangle AIR_ALTAR_FROM_PORTAL_TAP_RECT = new Rectangle(481, 188, 21, 18);
    public static Rectangle AIR_ALTAR_AT_ALTAR_TAP_RECT = new Rectangle(453, 223, 21, 24);
    public static Rectangle FIRE_ALTAR_AT_ALTAR_TAP_RECT = new Rectangle(453, 294, 25, 22);
    public static Rectangle FIRE_ALTAR_FROM_PORTAL_TAP_RECT = new Rectangle(764, 510, 47, 23);
    public static Rectangle MIND_ALTAR_AT_ALTAR_TAP_RECT = new Rectangle(416, 224, 25, 21);
    public static Rectangle MIND_ALTAR_FROM_PORTAL_TAP_RECT = new Rectangle(341, 101, 22, 15);
    public static Rectangle BODY_ALTAR_FROM_PORTAL_TAP_RECT = new Rectangle(470, 173, 17, 18);
    public static Rectangle BODY_ALTAR_AT_ALTAR_TAP_RECT = new Rectangle(454, 222, 22, 23);
    public static Rectangle WATER_ALTAR_AT_ALTAR_TAP_RECT = new Rectangle(396, 236, 26, 25);
    public static Rectangle WATER_ALTAR_FROM_PORTAL_TAP_RECT = new Rectangle(254, 175, 27, 23);
    public static Rectangle COSMIC_ALTAR_NORTH_TAP_RECT = new Rectangle(432, 292, 21, 22);
    public static Rectangle COSMIC_ALTAR_EAST_ATALTAR_TAP_RECT = new Rectangle(397, 254, 23, 21);
    public static Rectangle COSMIC_ALTAR_EAST_FROMPORTAL_TAP_RECT = new Rectangle(85, 257, 25, 19);
    public static Rectangle COSMIC_ALTAR_SOUTH_TAP_RECT = new Rectangle(436, 224, 23, 21);
    public static Rectangle COSMIC_ALTAR_WEST_TAP_RECT = new Rectangle(471, 259, 22, 20);
    public static Rectangle EARTH_ALTAR_AT_ALTAR_TAP_RECT = new Rectangle(454, 225, 22, 21);
    public static Rectangle EARTH_ALTAR_FROM_PORTAL_TAP_RECT = new Rectangle(465, 64, 24, 21);

    // PORTAL TAP RECTANGLES
    public static Rectangle PORTAL_TAP_RECT_FROM_HUGEREMAINS = new Rectangle(475, 221, 5, 8);
    public static Rectangle PORTAL_TAP_RECT_ATPORTAL_HUGEREMAINS = new Rectangle(463, 263, 7, 6);
    public static Rectangle AIR_PORTAL_ATPORTAL_TAP_RECT = new Rectangle(442, 282, 10, 7);
    public static Rectangle AIR_PORTAL_FROMALTAR_TAP_RECT = new Rectangle(408, 332, 8, 8);
    public static Rectangle FIRE_PORTAL_ATPORTAL_TAP_RECT = new Rectangle(444, 253, 8, 7);
    public static Rectangle FIRE_PORTAL_FROMALTAR_TAP_RECT = new Rectangle(327, 178, 6, 5);
    public static Rectangle MIND_PORTAL_ATPORTAL_TAP_RECT = new Rectangle(443, 284, 8, 8);
    public static Rectangle MIND_PORTAL_FROMALTAR_TAP_RECT = new Rectangle(577, 517, 10, 11);
    public static Rectangle BODY_PORTAL_FROMALTAR_TAP_RECT = new Rectangle(427, 353, 7, 9);
    public static Rectangle BODY_PORTAL_ATPORTAL_TAP_RECT = new Rectangle(442, 284, 9, 6);
    public static Rectangle WATER_PORTAL_ATPORTAL_TAP_RECT = new Rectangle(460, 267, 8, 7);
    public static Rectangle WATER_PORTAL_FROMALTAR_TAP_RECT = new Rectangle(587, 321, 4, 7);
    public static Rectangle COSMIC_PORTAL_NORTHPORTAL_TAP_RECT = new Rectangle(442, 251, 7, 6);
    public static Rectangle COSMIC_PORTAL_EASTPORTAL_TAP_RECT = new Rectangle(462, 265, 8, 8);
    public static Rectangle COSMIC_PORTAL_SOUTHPORTAL_TAP_RECT = new Rectangle(443, 283, 7, 8);
    public static Rectangle COSMIC_PORTAL_WESTPORTAL_ATPORTAL_TAP_RECT = new Rectangle(424, 269, 8, 8);
    public static Rectangle COSMIC_PORTAL_WESTPORTAL_FROMALTAR_TAP_RECT = new Rectangle(121, 268, 10, 7);
    public static Rectangle EARTH_PORTAL_ATPORTAL_TAP_RECT = new Rectangle(426, 268, 8, 7);
    public static Rectangle EARTH_PORTAL_FROMALTAR_TAP_RECT = new Rectangle(409, 452, 9, 9);

    // MINING TAP RECTANGLES
    public static Rectangle HUGE_GUARDIAN_REMAINS_FROMPORTAL_TAP_RECT = new Rectangle(392, 347, 29, 30);
    public static Rectangle HUGE_GUARDIAN_REMAINS_ATREMAINS_TAP_RECT = new Rectangle(413, 296, 28, 24);
    public static Rectangle LARGE_REMAINS_OUTSIDE_AGILITY_TAP_RECT = new Rectangle(460, 267, 11, 10);
    public static Rectangle LARGE_REMAINS_INSIDE_AGILITY_TAP_RECT = new Rectangle(427, 264, 9, 10);
    public static Rectangle LARGE_GUARDIAN_REMAINS_FROMAGILITY_TAP_RECT = new Rectangle(490, 341, 32, 31);
    public static Rectangle LARGE_GUARDIAN_REMAINS_ATREMAINS_TAP_RECT = new Rectangle(453, 292, 25, 26);
    public static Rectangle LARGE_GUARDIAN_AGILITYSHORTCUT_FROMREMAINS_TAP_RECT = new Rectangle(395, 224, 11, 9);
    public static Rectangle GUARDIAN_PARTS_TAP_RECT = new Rectangle(427, 271, 10, 7);

    // GENERAL TILES
    public static Tile currentLocation;
    public static Tile UNCHARGED_CELL_TABLE_TILE = new Tile(14467, 37701, 0);
    public static Tile WORKBENCH_TILE = new Tile(14447, 37701, 0);
    public static Tile DEPOSIT_POOL_TILE = new Tile(14435, 37701, 0);
    public static Tile CELL_PLACE_TILE = new Tile(14483, 37761, 0);

    // GUARDIAN TILES (PORTALS TO THE ALTARS)
    public static Tile GUARDIANS_MIDDLE_AREA_TILE = new Tile(14459, 37741, 0);
    public static Tile MIND_GUARDIAN_TILE = new Tile(14451, 37733, 0);
    public static Tile BODY_GUARDIAN_TILE = new Tile(14439, 37737, 0);
    public static Tile CHAOS_GUARDIAN_TILE = new Tile(14431, 37749, 0);
    public static Tile DEATH_GUARDIAN_TILE = new Tile(14431, 37769, 0);
    public static Tile LAW_GUARDIAN_TILE = new Tile(14435, 37781, 0);
    public static Tile BLOOD_GUARDIAN_TILE = new Tile(14447, 37789, 0);
    public static Tile FIRE_GUARDIAN_TILE = new Tile(14471, 37789, 0);
    public static Tile NATURE_GUARDIAN_TILE = new Tile(14483, 37781, 0);
    public static Tile EARTH_GUARDIAN_TILE = new Tile(14487, 37769, 0);
    public static Tile WATER_GUARDIAN_TILE = new Tile(14487, 37749, 0);
    public static Tile COSMIC_GUARDIAN_TILE = new Tile(14479, 37737, 0);
    public static Tile AIR_GUARDIAN_TILE = new Tile(14467, 37733, 0);


    // PORTAL TILES
    public static Tile HUGE_REMAINS_PORTAL_TILE = new Tile(14367, 37757, 0);
    public static Tile AIR_ALTAR_LEAVE_PORTAL_TILE = new Tile(11363, 19061, 0);
    public static Tile FIRE_ALTAR_LEAVE_PORTAL_TILE = new Tile(10295, 19145, 0);
    public static Tile MIND_ALTAR_LEAVE_PORTAL_TILE = new Tile(11171, 19061, 0);
    public static Tile BODY_ALTAR_LEAVE_PORTAL_TILE = new Tile(10083, 19085, 0);
    public static Tile WATER_ALTAR_LEAVE_PORTAL_TILE = new Tile(10903, 19077, 0);
    public static Tile COSMIC_ALTAR_LEAVE_NORTH_PORTAL_TILE = new Tile(8567, 19161, 0);
    public static Tile COSMIC_ALTAR_LEAVE_EAST_PORTAL_TILE = new Tile(10871, 19089, 0);
    public static Tile COSMIC_ALTAR_LEAVE_SOUTH_PORTAL_TILE = new Tile(8567, 19001, 0);
    public static Tile COSMIC_ALTAR_LEAVE_WEST_PORTAL_TILE = new Tile(8487, 19081, 0);
    public static Tile EARTH_ALTAR_LEAVE_PORTAL_TILE = new Tile(10623, 19065, 0);


    // ALTAR TILES
    public static Tile AIR_ALTAR_TILE = new Tile(11371, 19077, 0);
    public static Tile FIRE_ALTAR_TILE = new Tile(10335, 19109, 0);
    public static Tile MIND_ALTAR_TILE = new Tile(11147, 19105, 0);
    public static Tile BODY_ALTAR_TILE = new Tile(10087, 19097, 0);
    public static Tile WATER_ALTAR_TILE = new Tile(10871, 19089, 0);
    public static Tile COSMIC_NORTH_ALTAR_TILE = new Tile(8567, 19089, 0);
    public static Tile COSMIC_EAST_ALTAR_TILE = new Tile(8575, 19081, 0);
    public static Tile COSMIC_SOUTH_ALTAR_TILE = new Tile(8567, 19073, 0);
    public static Tile COSMIC_WEST_ALTAR_TILE = new Tile(8559, 19081, 0);
    public static Tile EARTH_ALTAR_TILE = new Tile(10627, 19105, 0);

    // MINING TILES
    public static Tile HUGE_GUARDIAN_REMAINS_TILE = new Tile(14363, 37745, 0);
    public static Tile LARGE_GUARDIAN_REMAINS_TILE = new Tile(14555, 37749, 0);
    public static Tile GUARDIAN_PARTS_TILE = new Tile(14423, 37701, 0);

    // SETTINGS
    public static boolean doCosmics;
    public static boolean doLaws;
    public static boolean doDeaths;
    public static boolean doBloods;
    public static boolean usePreGameMineArea;
    public static boolean usePouches;
    public static boolean portalActive;
    public static boolean readyToCraftEssences = false;
    public static boolean readyToGoToAltar = false;
    public static boolean readyToCraftRunes = false;
    public static boolean isGameAt90Percent = false;
    public static PortalLocation portalLocation = PortalLocation.NONE;
    public static RuneInfo runeToMake = RuneInfo.NOELEMENTAL;


    // INTS
    public static int agilityLevel;
    public static int runecraftingLevel;
    public static int guardiansPower;
    public static int portalTime;
    public static int fragmentsToMine;
    public static int elementalPoints = 0;
    public static int catalyticPoints = 0;

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
        doCosmics = Boolean.parseBoolean(configs.get("Cosmic runes"));
        doLaws = Boolean.parseBoolean(configs.get("Law runes"));
        doDeaths = Boolean.parseBoolean(configs.get("Death runes"));
        doBloods = Boolean.parseBoolean(configs.get("Blood runes"));
        usePouches = Boolean.parseBoolean(configs.get("Pouches"));
        fragmentsToMine = Integer.parseInt(configs.get("Fragments"));

        // 55-148, 55-147, 56-147, 56-148, 56-149, 57-149, 57-148, 57-147, 44-75, 42-75, 41-75, 40-75, 39-75, 38-75, 37-75, 36-75, 35-75, 34-75, 33-75, 32-75, 43-75, 50-75
        Walker.setup(new MapChunk(new String[]{"55-148", "55-147", "56-147", "56-148", "56-149", "57-149", "57-148", "57-147", "44-75", "42-75", "41-75", "41-75", "40-75", "39-75", "38-75", "37-75", "36-75", "35-75", "34-75", "33-75", "32-75", "43-75", "50-75"}, "0"));

        // Open stats tab to check stats
        if (!GameTabs.isTabOpen(UITabs.STATS)) {
            GameTabs.openTab(UITabs.STATS);
            Condition.wait(() -> GameTabs.isTabOpen(UITabs.STATS), 100, 20);
        }

        if (GameTabs.isTabOpen(UITabs.STATS)) {
            agilityLevel = Stats.getRealLevel(Skills.AGILITY); //Get the agility level
            runecraftingLevel = Stats.getRealLevel(Skills.RUNECRAFTING); // Get the runecrafting level
        }

        if (agilityLevel < 56) {
            Logger.log("Agility level below 56, not using east mine in pre-game");
            usePreGameMineArea = false;
        } else {
            Logger.log("Agility level above 56, using east lower mine in pre-game");
            usePreGameMineArea = true;
        }

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/dm.png");

        setStatusAndDebugLog("Initializing...");
        Paint.setStatistic("Initializing...");

        // Make sure chatbox is closed
        setStatusAndDebugLog("Close chatbox");
        Chatbox.closeChatbox();

        // Zoom all the way out, zoom level 1
        setStatusAndDebugLog("Set zoom 1");
        Game.setZoom("1");

        // Disable AFK handler, we don't do that shit here
        setStatusAndDebugLog("Disable AFK Handler");
        Client.disableAFKHandler();

        setStatusAndDebugLog("Set postpone breaks TRUE");
        Client.postponeBreaks();
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

        if (!readyToCraftRunes) {
            stateUpdater.updateAllStates();
        }

        //Run tasks
        for (Task task : gotrTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    public static void setStatusAndDebugLog(String stringToLog) {
        Paint.setStatus(stringToLog);
        Logger.debugLog(stringToLog);
    }
}