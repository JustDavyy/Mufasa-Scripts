package main;

import Tasks.*;
import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import utils.StateUpdater;
import utils.Task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dmGOTR",
        description = "Does Guardians of the Rift minigame.",
        version = "1.01",
        guideLink = "",
        categories = {ScriptCategory.Fletching, ScriptCategory.Agility}
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
    public static Area airAltar = new Area(
            new Tile(11322, 19012, 0),
            new Tile(11443, 19133, 0)
    );
    public static Area waterAltar = new Area(
            new Tile(10767, 18958, 0),
            new Tile(10995, 19184, 0)
    );
    public static Area earthAltar = new Area(
            new Tile(10519, 18989, 0),
            new Tile(10730, 19190, 0)
    );
    public static Area fireAltar = new Area(
            new Tile(10230, 18970, 0),
            new Tile(10475, 19192, 0)
    );
    public static Area mindAltar = new Area(
            new Tile(11022, 18991, 0),
            new Tile(11228, 19192, 0)
    );
    public static Area bodyAltar = new Area(
            new Tile(10009, 19014, 0),
            new Tile(10164, 19182, 0)
    );
    public static Area cosmicAltar = new Area(
            new Tile(8471, 18963, 0),
            new Tile(8682, 19179, 0)
    );
    public static Area chaosAltar = new Area(
            new Tile(8984, 19001, 0),
            new Tile(9198, 19192, 0)
    );
    public static Area natureAltar = new Area(
            new Tile(9490, 18976, 0),
            new Tile(9701, 19195, 0)
    );
    public static Area lawAltar = new Area(
            new Tile(9772, 18973, 0),
            new Tile(9944, 19166, 0)
    );
    public static Area deathAltar = new Area(
            new Tile(8729, 18997, 0),
            new Tile(8937, 19187, 0)
    );
    public static Area bloodAltar = new Area(
            new Tile(12811, 18960, 0),
            new Tile(13040, 19189, 0)
    );

    public static Tile currentLocation;

    // SETTINGS
    public static boolean doCosmics;
    public static boolean doLaws;
    public static boolean doDeaths;
    public static boolean doBloods;
    public static boolean usePreGameMineArea;
    public static boolean usePouches;

    // INTS
    public static int agilityLevel;

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

        if (!GameTabs.isStatsTabOpen()) {
            GameTabs.openStatsTab();
            Condition.wait(() -> GameTabs.isStatsTabOpen(), 100, 20);
        }

        if (GameTabs.isStatsTabOpen()) {
            agilityLevel = Stats.getRealLevel(Skills.AGILITY); //Get the agi level
        }

        if (agilityLevel < 56) {
            Logger.log("Agility level below 56, not using east mine in pre-game");
            usePreGameMineArea = false;
        }
    }

    // Task list!
    List<Task> gotrTasks = Arrays.asList(
            new CheckGear(),
            new BreakManager(stateUpdater),
            new PreGame(stateUpdater),
            new HandleAltars(stateUpdater),
            new GoToAltar(stateUpdater),
            new HandlePouches(stateUpdater),
            new ProcessEssence(stateUpdater),
            new MineEssence(stateUpdater),
            new EnterGame(stateUpdater)
    );

    @Override
    public void poll() {
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 100, 20);
        }

        stateUpdater.updateAllStates();

        //Run tasks
        for (Task task : gotrTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
}