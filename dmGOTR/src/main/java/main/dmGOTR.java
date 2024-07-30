package main;

import Tasks.*;
import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
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
    // REGIONS
    public static RegionBox gameRegion = new RegionBox("mainGameArea", 54, 12, 504, 393);
    public static RegionBox airAltar = new RegionBox("airAltar", 1308, 537, 1512, 774);
    public static RegionBox waterAltar = new RegionBox("waterAltar", 780, 537, 1029, 783);
    public static RegionBox earthAltar = new RegionBox("earthAltar", 531, 540, 774, 747);
    public static RegionBox fireAltar = new RegionBox("fireAltar", 240, 534, 519, 762);
    public static RegionBox mindAltar = new RegionBox("mindAltar", 1035, 540, 1269, 741);
    public static RegionBox bodyAltar = new RegionBox("bodyAltar", 18, 537, 207, 714);
    public static RegionBox cosmicAltar = new RegionBox("cosmicAltar", 549, 15, 801, 258);
    public static RegionBox chaosAltar = new RegionBox("chaosAltar", 1053, 9, 1302, 198);
    public static RegionBox natureAltar = new RegionBox("natureAltar", 792, 270, 1041, 510);
    public static RegionBox lawAltar = new RegionBox("lawAltar", 1074, 276, 1293, 507);
    public static RegionBox deathAltar = new RegionBox("deathAltar", 804, 9, 1047, 237);
    public static RegionBox bloodAltar = new RegionBox("bloodAltar", 1308, 93, 1560, 345);

    // AREAS
    Area preGameMine = new Area(new Tile(118, 32), new Tile(134, 60));
    Area regularMine = new Area(new Tile(70, 59), new Tile(87, 71));
    Area specialMine = new Area(new Tile(53, 33), new Tile(63, 59));
    Area middleOfGameArea = new Area(new Tile(79, 33), new Tile(106, 66));

    // SETTINGS
    public static boolean doCosmics;
    public static boolean doLaws;
    public static boolean doDeaths;
    public static boolean doBloods;
    public static boolean usePreGameMineArea;

    // INTS
    public static int agilityLevel;

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        doCosmics = Boolean.parseBoolean(configs.get("Do Cosmic runes?"));
        doLaws = Boolean.parseBoolean(configs.get("Do Law runes?"));
        doDeaths = Boolean.parseBoolean(configs.get("Do Death runes?"));
        doBloods = Boolean.parseBoolean(configs.get("Do Blood runes?"));

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
            new BreakManager(),
            new PreGame(),
            new HandleAltars(),
            new GoToAltar(),
            new HandlePouches(),
            new ProcessEssence(),
            new MineEssence(),
            new EnterGame()
    );

    @Override
    public void poll() {
        //Run tasks
        for (Task task : gotrTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
}