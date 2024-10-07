package dAgility;


import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import dAgility.Tasks.*;
import dAgility.utils.Task;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dAgility",
        description = "Trains agility at various courses. World hopping and eating food is supported, as well as picking up Marks of Grace when running a rooftop course.",
        version = "1.00",
        categories = {ScriptCategory.Agility},
        guideLink = ""
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Course",
                        description = "What agility course do you want to train at?",
                        defaultValue = "Seers",
                        allowedValues = {
                                @AllowedValue(optionName = "Gnome"),
                                @AllowedValue(optionName = "Al Kharid"),
                                @AllowedValue(optionName = "Draynor"),
                                @AllowedValue(optionName = "Varrock"),
                                @AllowedValue(optionName = "Canifis"),
                                @AllowedValue(optionName = "Falador"),
                                @AllowedValue(optionName = "Seers"),
                                @AllowedValue(optionName = "Seers - teleport"),
                                @AllowedValue(optionName = "Pollnivneach"),
                                @AllowedValue(optionName = "Rellekka"),
                                @AllowedValue(optionName = "Ardougne")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Food",
                        description = "Which food to use?",
                        defaultValue = "None",
                        allowedValues = {
                                @AllowedValue(optionName = "None"),
                                @AllowedValue(optionIcon = "1891", optionName = "Cake"),
                                @AllowedValue(optionIcon = "333", optionName = "Trout"),
                                @AllowedValue(optionIcon = "329", optionName = "Salmon"),
                                @AllowedValue(optionIcon = "361", optionName = "Tuna"),
                                @AllowedValue(optionIcon = "1993", optionName = "Jug of wine"),
                                @AllowedValue(optionIcon = "379", optionName = "Lobster"),
                                @AllowedValue(optionIcon = "373", optionName = "Swordfish"),
                                @AllowedValue(optionIcon = "6705", optionName = "Potato with cheese"),
                                @AllowedValue(optionIcon = "7946", optionName = "Monkfish"),
                                @AllowedValue(optionIcon = "3144", optionName = "Karambwan"),
                                @AllowedValue(optionIcon = "385", optionName = "Shark"),
                                @AllowedValue(optionIcon = "391", optionName = "Manta ray"),
                                @AllowedValue(optionIcon = "13441", optionName = "Anglerfish")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "EatPercent",
                        description = "What percent to eat at?",
                        defaultValue = "60%",
                        allowedValues = {
                                @AllowedValue(optionName = "20%"),
                                @AllowedValue(optionName = "25%"),
                                @AllowedValue(optionName = "30%"),
                                @AllowedValue(optionName = "35%"),
                                @AllowedValue(optionName = "40%"),
                                @AllowedValue(optionName = "45%"),
                                @AllowedValue(optionName = "50%"),
                                @AllowedValue(optionName = "55%"),
                                @AllowedValue(optionName = "60%"),
                                @AllowedValue(optionName = "65%"),
                                @AllowedValue(optionName = "70%"),
                                @AllowedValue(optionName = "75%"),
                                @AllowedValue(optionName = "80%"),
                                @AllowedValue(optionName = "85%"),
                                @AllowedValue(optionName = "90%"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dAgility extends AbstractScript {
    List<MarkHandling> noMarks = Arrays.asList(
            new MarkHandling(new Rectangle(1, 1, 1, 1), new Color(203, 137, 25), new Rectangle(1, 1, 1, 1), new Tile(1, 1, 0))
    );
    public static List<startTileStorage> startTiles = Arrays.asList();
    public static final List<Obstacle> obstacles = new ArrayList<dAgility.Obstacle>();
    private static final Random random = new Random();
    private final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    private final DecimalFormat MoGsFormat = new DecimalFormat("#,##0.0", symbols);
    private final DecimalFormat LapsFormat = new DecimalFormat("#,##0.0", symbols);

    public static Tile currentLocation;

    public static String courseChosen; // Save agility course name
    public static String foodID; // Save food
    public static int eatHP; // HP level to eat at
    private int eatPercent; // Save % to eat food at e.g. 20% = 20
    private String hopProfile;
    private Boolean hopEnabled;
    private Boolean useWDH;
    public static int lapCount = 0;
    private static int mogCount = 0;
    private static int MoGIndex;
    public static int mogTotal;
    public static long startTime = System.currentTimeMillis();
    public static Boolean useSeersTeleport = false;
    public static int currentHP;

    private final List<Task> agilityTasks = Arrays.asList(
            new Run(),
            new Eat(),
            new Gnome(),
            new Draynor(),
            new Alkharid(),
            new Varrock()
    );

    @Override
    public void onStart() {
        Logger.log("Initialising dAgility...");
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        hopProfile = configs.get("Use world hopper?");
        hopEnabled = Boolean.valueOf(configs.get("Use world hopper?.enabled"));
        useWDH = Boolean.valueOf(configs.get("Use world hopper?.useWDH"));
        courseChosen = configs.get("Course");

        // Setup walker using the correct chunks
        setupWalker();

        // Setup obstacles for the chosen course
        setupObstacles();

        // Save food
        String foodChosen = configs.get("Food");
        eatPercent = Integer.parseInt(configs.get("EatPercent").replaceAll("%", ""));
        initializeItemIDs(foodChosen);

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create a single image box, to show the amount of processed bows
        MoGIndex = Paint.createBox("Marks of Grace", ItemList.MARK_OF_GRACE_11849, 0);

        // Set the two top headers of paintUI.
        Paint.setStatus("Initializing...");

        Paint.setStatus("Close chatbox");
        Chatbox.closeChatbox();

        // Check level reqs
        Paint.setStatus("Check level reqs");
        checkLevelReqs();
        if (!foodID.equals("None")) {
            Paint.setStatus("Check food");
            checkFood();
        }
        Paint.setStatus("Set zoom level");
        courseZoom();
        Paint.setStatus("Init hop timer");
        hopActions();

        // Set teleport boolean if needed
        if (courseChosen.equals("Seers - teleport")) {
            Paint.setStatus("Set teleport to TRUE");
            useSeersTeleport = true;
        }

        GameTabs.closeInventoryTab();

        // Logs for debugging purposes
        Logger.log("Chosen agility course: " + courseChosen);
        Logger.log("Starting dAgility!");
        Paint.setStatus("End of onStart");
    }

    @Override
    public void poll() {
        // Looped tasks go here.
        hopActions();
        updateStatLabel();

        for (Task task : agilityTasks) {
            if (task.activate()) {
                task.execute();
                readXP();
                return;
            }
        }
    }

    public void initializeItemIDs(String logName) {
        Logger.debugLog("Running the initializeItemIDs() method.");

        Map<String, String[]> itemIDs = new HashMap<>();

        // Map of itemIDs for foodID (1)
        itemIDs.put("None", new String[] {"None"});
        itemIDs.put("Cake", new String[] {"Cake"});
        itemIDs.put("Trout", new String[] {"333"});
        itemIDs.put("Salmon", new String[] {"329"});
        itemIDs.put("Tuna", new String[] {"361"});
        itemIDs.put("Jug of wine", new String[] {"1993"});
        itemIDs.put("Lobster", new String[] {"379"});
        itemIDs.put("Swordfish", new String[] {"373"});
        itemIDs.put("Potato with cheese", new String[] {"6705"});
        itemIDs.put("Monkfish", new String[] {"7946"});
        itemIDs.put("Karambwan", new String[] {"3144"});
        itemIDs.put("Shark", new String[] {"385"});
        itemIDs.put("Manta ray", new String[] {"391"});
        itemIDs.put("Anglerfish", new String[] {"13441"});
        String[] itemIds = itemIDs.get(logName);
        foodID = itemIds[0];

        Logger.debugLog("Ending the initializeItemIDs() method.");
    }


    private void checkFood() {
        Logger.debugLog("Checking inventory has food.");
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }
        if (foodID.equals("Cake")) {
            if (Inventory.count("1891", 0.8) == 0 && Inventory.count("1893", 0.8) == 0 && Inventory.count("1895", 0.8) == 0) {
                Logger.log("Not enough food, stopping script.");
                Logout.logout();
                Script.stop();
            }
        } else if (Inventory.count(foodID, 0.8) == 0) {
            Logger.log("Not enough food, stopping script.");
            Logout.logout();
            Script.stop();
        } else {
            Logger.debugLog("Food found, continuing.");
        }
        GameTabs.closeInventoryTab();
        Logger.debugLog("Finished checking inventory for food.");
    }

    private void setupWalker() {
        switch (courseChosen) {
            case "Gnome":
                MapChunk gnomeChunks = new MapChunk(new String[]{"38-53", "39-53"}, "0", "1", "2");
                Walker.setup(gnomeChunks);
                break;
            case "Al Kharid":
                MapChunk alkharidChunks = new MapChunk(new String[]{"51-49"}, "0", "1", "2", "3");
                Walker.setup(alkharidChunks);
                break;
            case "Varrock":
                MapChunk varrockChunks = new MapChunk(new String[]{"50-53"}, "0", "1", "3");
                Walker.setup(varrockChunks);
                break;
            case "Canifis":
                break;
            case "Falador":
                break;
            case "Rellekka":
                break;
            case "Ardougne":
                break;
            case "Draynor":
                MapChunk draynorChunks = new MapChunk(new String[]{"48-51"}, "0", "3");
                Walker.setup(draynorChunks);
                break;
            case "Pollnivneach":
                break;
            case "Seers":
            case "Seers - teleport":
                break;
            default:
                Logger.log("This is a unknown course, no chunks to set up.");
                Script.stop();
        }
    }

    private void setupObstacles() {
        switch (courseChosen) {
            case "Gnome":
                // Mark of Grace ground color
                Color gnomeMogColor = new Color(Integer.parseInt("cb8919", 16));
                // Mark of Graces
                List<MarkHandling> gnomeObstacle5Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(493, 265, 10, 11), gnomeMogColor, new Rectangle(469, 279, 20, 17), new Tile(9947, 13429, 0))
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(9877, 13481, 0), new Tile(9960, 13512, 0)),
                        new Tile(9895, 13493, 0), new Tile(9895, 13465, 0),
                        new Rectangle(437, 294, 11, 20), new Rectangle(151, 313, 10, 22),
                        new Tile(9935, 13497, 0), noMarks, false));
                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(9870, 13439, 0), new Tile(9915, 13472, 0)),
                        new Tile(9895, 13453, 0), new Tile(9891, 13441, 1),
                        new Rectangle(404, 282, 50, 13), new Rectangle(441, 381, 19, 9),
                        new Tile(9895, 13465, 0), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(9874, 13426, 1), new Tile(9914, 13454, 1)),
                        new Tile(9895, 13437, 1), new Tile(9891, 13429, 2),
                        new Rectangle(419, 252, 4, 22), new Rectangle(442, 282, 3, 21),
                        new Tile(9891, 13441, 1), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(9874, 13410, 2), new Tile(9925, 13447, 2)),
                        new Tile(9907, 13429, 2), new Tile(9931, 13429, 2),
                        new Rectangle(465, 276, 25, 2), new Rectangle(573, 275, 24, 3),
                        new Tile(9891, 13429, 2), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(9927, 13408, 2), new Tile(9970, 13446, 2)),
                        new Tile(9931, 13429, 2), new Tile(9947, 13429, 0),
                        new Rectangle(525, 283, 24, 19), new Rectangle(525, 283, 24, 19),
                        new Tile(9931, 13429, 2), gnomeObstacle5Mark, true));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(9925, 13403, 0), new Tile(9963, 13450, 0)),
                        new Tile(9943, 13449, 0), new Tile(9939, 13461, 0),
                        new Rectangle(411, 227, 36, 20), new Rectangle(393, 106, 34, 26),
                        new Tile(9947, 13429, 0), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(9925, 13455, 0), new Tile(9969, 13479, 0)),
                        new Tile(9935, 13469, 0), new Tile(9935, 13497, 0),
                        new Rectangle(432, 211, 22, 29), new Rectangle(406, 159, 19, 30),
                        new Tile(9939, 13461, 0), noMarks, false));
                break;
            case "Al Kharid":
                // Mark of Grace ground color
                Color alkharidMogColor = new Color(Integer.parseInt("c98818", 16));
                // Mark of Graces
                List<MarkHandling> alkharidObstacle2Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(517, 377, 9, 10), alkharidMogColor, new Rectangle(349, 359, 9, 10), new Tile(13087, 12437, 3))
                );
                List<MarkHandling> alkharidObstacle3Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(312, 445, 10, 9), alkharidMogColor, new Rectangle(494, 219, 7, 8), new Tile(13087, 12437, 3))
                );
                List<MarkHandling> alkharidObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(491, 210, 8, 7), alkharidMogColor, new Rectangle(769, 390, 33, 19), new Tile(13087, 12437, 3))
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(13070, 12501, 0), new Tile(13214, 12544, 0)),
                        new Tile(13091, 12533, 0), new Tile(13091, 12517, 3),
                        new Rectangle(444, 289, 9, 6), null,
                        new Tile(13195, 12525, 0), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(13071, 12459, 3), new Tile(13121, 12526, 3)),
                        new Tile(13087, 12477, 3), new Tile(13087, 12437, 3),
                        new Rectangle(441, 285, 12, 9), new Rectangle(416, 497, 12, 14),
                        new Tile(13091, 12517, 3), alkharidObstacle2Mark, true));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(13051, 12384, 3), new Tile(13101, 12449, 3)),
                        new Tile(13067, 12413, 3), new Tile(13135, 12413, 3),
                        new Rectangle(483, 253, 7, 18), new Rectangle(367, 372, 19, 12),
                        new Tile(13087, 12437, 3), alkharidObstacle3Mark, true));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(13127, 12377, 3), new Tile(13227, 12458, 3)),
                        new Tile(13203, 12401, 3), new Tile(13259, 12401, 1),
                        new Rectangle(477, 259, 16, 16), new Rectangle(796, 309, 26, 17),
                        new Tile(13135, 12413, 3), alkharidObstacle4Mark, true));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(13238, 12372, 1), new Tile(13289, 12420, 1)),
                        new Tile(13271, 12409, 1), new Tile(13267, 12445, 2),
                        new Rectangle(441, 233, 15, 21), new Rectangle(490, 204, 15, 24),
                        new Tile(13259, 12401, 1), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(13231, 12430, 2), new Tile(13290, 12508, 2)),
                        new Tile(13263, 12465, 2), new Tile(13263, 12469, 3),
                        new Rectangle(438, 258, 14, 4), new Rectangle(423, 185, 13, 2),
                        new Tile(13267, 12445, 2), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(13237, 12451, 3), new Tile(13296, 12520, 3)),
                        new Tile(13251, 12489, 3), new Tile(13207, 12497, 3),
                        new Rectangle(441, 249, 10, 10), new Rectangle(396, 184, 9, 7),
                        new Tile(13263, 12469, 3), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 8",
                        new Area(new Tile(13177, 12479, 3), new Tile(13231, 12538, 3)),
                        new Tile(13203, 12517, 3), new Tile(13195, 12525, 0),
                        new Rectangle(422, 246, 13, 12), new Rectangle(411, 177, 13, 12),
                        new Tile(13207, 12497, 3), noMarks, false));

                // Start tiles
                startTiles = Arrays.asList(
                        new startTileStorage(new Tile(13095, 12541, 0), new Rectangle(423, 325, 9, 4)),
                        new startTileStorage(new Tile(13091, 12545, 0), new Rectangle(443, 344, 9, 5)),
                        new startTileStorage(new Tile(13095, 12545, 0), new Rectangle(426, 344, 7, 3)),
                        new startTileStorage(new Tile(13099, 12545, 0), new Rectangle(408, 343, 10, 3)),
                        new startTileStorage(new Tile(13099, 12541, 0), new Rectangle(408, 329, 11, 3)),
                        new startTileStorage(new Tile(13099, 12537, 0), new Rectangle(408, 308, 11, 5)),
                        new startTileStorage(new Tile(13095, 12537, 0), new Rectangle(424, 308, 9, 3)),
                        new startTileStorage(new Tile(13091, 12537, 0), new Rectangle(442, 308, 10, 4)),
                        new startTileStorage(new Tile(13087, 12537, 0), new Rectangle(459, 308, 11, 6))
                );
                break;
            case "Varrock":
                // Mark of Grace ground color
                Color varrockMogColor = new Color(Integer.parseInt("c98718", 16));
                // Mark of Graces
                List<MarkHandling> varrockObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(408, 267, 10, 8), varrockMogColor, new Rectangle(368, 262, 31, 31), new Tile(12767, 13373, 3))
                );
                List<MarkHandling> varrockObstacle6Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(316, 319, 8, 8), varrockMogColor, new Rectangle(800, 219, 16, 29), new Tile(12871, 13345, 3))
                );
                List<MarkHandling> varrockObstacle8Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(472, 193, 9, 7), varrockMogColor, new Rectangle(406, 230, 85, 32), new Tile(12943, 13389, 3))
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(12842, 13383, 0), new Tile(12975, 13453, 0)),
                        new Tile(12887, 13405, 0), new Tile(12875, 13405, 3),
                        new Rectangle(419, 262, 3, 10), new Rectangle(149, 312, 11, 9),
                        new Tile(12943, 13417, 0), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(12843, 13378, 3), new Tile(12894, 13440, 3)),
                        new Tile(12855, 13405, 3), new Tile(12831, 13405, 3),
                        new Rectangle(422, 262, 13, 13), new Rectangle(338, 266, 10, 13),
                        new Tile(12875, 13405, 3), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(12795, 13387, 3), new Tile(12846, 13441, 3)),
                        new Tile(12803, 13413, 3), new Tile(12787, 13413, 1),
                        new Rectangle(419, 244, 13, 28), new Rectangle(302, 215, 17, 25),
                        new Tile(12831, 13405, 3), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(12728, 13375, 1), new Tile(12805, 13429, 1)),
                        new Tile(12775, 13413, 1), new Tile(12767, 13373, 3),
                        new Rectangle(388, 266, 28, 24), new Rectangle(339, 265, 27, 27),
                        new Tile(12787, 13413, 1), varrockObstacle4Mark, true));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(12754, 13346, 3), new Tile(12783, 13385, 3)),
                        new Tile(12771, 13357, 3), new Tile(12771, 13341, 3),
                        new Rectangle(443, 278, 18, 14), new Rectangle(460, 351, 22, 16),
                        new Tile(12767, 13373, 3), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(12720, 13258, 3), new Tile(12843, 13347, 3)),
                        new Tile(12831, 13337, 3), new Tile(12871, 13345, 3),
                        new Rectangle(460, 261, 14, 19), new Rectangle(719, 274, 16, 21),
                        new Tile(12771, 13341, 3), varrockObstacle6Mark, true));

                obstacles.add(new Obstacle("Obstacle 6-2",
                        new Area(new Tile(12803, 13310, 3), new Tile(12844, 13370, 3)),
                        new Tile(12831, 13337, 3), new Tile(12871, 13345, 3),
                        new Rectangle(460, 261, 14, 19), new Rectangle(719, 274, 16, 21),
                        new Tile(12771, 13341, 3), varrockObstacle6Mark, true));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(12851, 13306, 3), new Tile(12939, 13372, 3)),
                        new Tile(12923, 13357, 3), new Tile(12943, 13361, 3),
                        new Rectangle(467, 244, 18, 18), new Rectangle(676, 204, 12, 13),
                        new Tile(12871, 13345, 3), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 8",
                        new Area(new Tile(12936, 13350, 3), new Tile(12973, 13379, 3)),
                        new Tile(12943, 13381, 3), new Tile(12943, 13389, 3),
                        new Rectangle(444, 225, 19, 27), new Rectangle(446, 156, 28, 29),
                        new Tile(12943, 13361, 3), varrockObstacle8Mark, true));

                obstacles.add(new Obstacle("Obstacle 9",
                        new Area(new Tile(12936, 13381, 3), new Tile(12969, 13418, 3)),
                        new Tile(12943, 13409, 3), new Tile(12943, 13417, 0),
                        new Rectangle(439, 255, 15, 5), new Rectangle(441, 186, 15, 10),
                        new Tile(12943, 13389, 3), noMarks, false));
                break;
            case "Canifis":
                break;
            case "Falador":
                break;
            case "Rellekka":
                break;
            case "Ardougne":
                break;
            case "Draynor":
                // Mark of Grace ground color
                Color draynorMogColor = new Color(Integer.parseInt("d38f1a", 16));
                // Mark of Graces
                List<MarkHandling> draynorObstacle2Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(247, 174, 19, 16), draynorMogColor, new Rectangle(427, 455, 29, 28), new Tile(12359, 12853, 3))
                );
                List<MarkHandling> draynorObstacle3Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(381, 364, 23, 16), draynorMogColor, new Rectangle(559, 195, 25, 22), new Tile(12367, 12813, 3))
                );
                List<MarkHandling> draynorObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(535, 273, 27, 25), draynorMogColor, new Rectangle(216, 375, 26, 21), new Tile(12351, 12793, 3))
                );
                List<MarkHandling> draynorObstacle7Marks = Arrays.asList(
                        new MarkHandling(new Rectangle(619, 220, 21, 16), draynorMogColor, new Rectangle(497, 41, 97, 59), new Tile(12411, 12793, 0)),
                        new MarkHandling(new Rectangle(485, 142, 20, 16), draynorMogColor, new Rectangle(631, 131, 32, 63), new Tile(12411, 12793, 0))
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(12405, 12848, 0), new Tile(12443, 12879, 0)),
                        new Tile(12415, 12865, 0), new Tile(12407, 12865, 3),
                        new Rectangle(375, 245, 4, 19), null,
                        new Tile(12411, 12793, 0), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(12384, 12848, 3), new Tile(12417, 12879, 3)),
                        new Tile(12395, 12857, 3), new Tile(12359, 12853, 3),
                        new Rectangle(388, 281, 23, 24), new Rectangle(238, 346, 27, 25),
                        new Tile(12407, 12865, 3), draynorObstacle2Mark, true));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(12340, 12829, 3), new Tile(12381, 12868, 3)),
                        new Tile(12363, 12853, 3), new Tile(12367, 12813, 3),
                        new Rectangle(486, 275, 23, 20), new Rectangle(519, 273, 26, 25),
                        new Tile(12359, 12853, 3), draynorObstacle3Mark, true));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(12352, 12801, 3), new Tile(12385, 12824, 3)),
                        new Tile(12355, 12809, 3), new Tile(12351, 12793, 3),
                        new Rectangle(429, 323, 27, 29), new Rectangle(304, 370, 30, 29),
                        new Tile(12367, 12813, 3), draynorObstacle4Mark, true));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(12344, 12770, 3), new Tile(12362, 12796, 3)),
                        new Tile(12351, 12777, 3), new Tile(12351, 12769, 3),
                        new Rectangle(421, 294, 43, 25), new Rectangle(421, 495, 38, 32),
                        new Tile(12351, 12793, 3), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(12345, 12761, 3), new Tile(12383, 12770, 3)),
                        new Tile(12375, 12769, 3), new Tile(12383, 12773, 3),
                        new Rectangle(479, 267, 36, 30), new Rectangle(741, 270, 18, 27),
                        new Tile(12351, 12769, 3), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(12382, 12767, 3), new Tile(12412, 12796, 3)),
                        new Tile(12403, 12793, 3), new Tile(12411, 12793, 0),
                        new Rectangle(485, 193, 71, 74), new Rectangle(668, 3, 24, 20),
                        new Tile(12383, 12773, 3), draynorObstacle7Marks, true));

                // Start tiles
                startTiles = Arrays.asList(
                        new startTileStorage(new Tile(12415, 12873, 0), new Rectangle(371, 332, 2, 20)),
                        new startTileStorage(new Tile(12419, 12873, 0), new Rectangle(331, 338, 3, 18)),
                        new startTileStorage(new Tile(12423, 12873, 0), new Rectangle(280, 336, 7, 23)),
                        new startTileStorage(new Tile(12427, 12873, 0), new Rectangle(233, 340, 8, 26)),
                        new startTileStorage(new Tile(12427, 12869, 0), new Rectangle(239, 311, 7, 16)),
                        new startTileStorage(new Tile(12423, 12869, 0), new Rectangle(274, 303, 6, 21)),
                        new startTileStorage(new Tile(12419, 12869, 0), new Rectangle(321, 299, 6, 23)),
                        new startTileStorage(new Tile(12415, 12869, 0), new Rectangle(373, 302, 4, 19)),
                        new startTileStorage(new Tile(12415, 12865, 0), new Rectangle(374, 253, 5, 20)),
                        new startTileStorage(new Tile(12419, 12865, 0), new Rectangle(338, 257, 5, 17)),
                        new startTileStorage(new Tile(12423, 12865, 0), new Rectangle(290, 262, 8, 19)),
                        new startTileStorage(new Tile(12427, 12865, 0), new Rectangle(244, 265, 8, 18)),
                        new startTileStorage(new Tile(12427, 12861, 0), new Rectangle(249, 225, 9, 20)),
                        new startTileStorage(new Tile(12423, 12861, 0), new Rectangle(295, 221, 7, 17)),
                        new startTileStorage(new Tile(12419, 12861, 0), new Rectangle(329, 216, 6, 20)),
                        new startTileStorage(new Tile(12415, 12861, 0), new Rectangle(376, 213, 3, 20))
                );
                break;
            case "Pollnivneach":
                break;
            case "Seers":
            case "Seers - teleport":
                break;
            default:
                Logger.log("This is a unknown course, no obstacles to set up.");
                Script.stop();
        }
    }

    public void courseZoom() {

        // Map of course names to their zoom levels
        Map<String, String> courseZoomLevels = Map.ofEntries(
                new AbstractMap.SimpleEntry<>("Gnome", "2"),
                new AbstractMap.SimpleEntry<>("Al Kharid", "1"),
                new AbstractMap.SimpleEntry<>("Varrock", "1"),
                new AbstractMap.SimpleEntry<>("Canifis", "1"),
                new AbstractMap.SimpleEntry<>("Falador", "1"),
                new AbstractMap.SimpleEntry<>("Rellekka", "1"),
                new AbstractMap.SimpleEntry<>("Ardougne", "1")
        );

        String zoomLevel = courseZoomLevels.get(courseChosen);

        if (zoomLevel != null) {
            Logger.debugLog(courseChosen + " course - setting zoom level " + zoomLevel + ".");
            Game.setZoom(zoomLevel);
        }

        if (GameTabs.isSettingsTabOpen()) {
            GameTabs.closeSettingsTab();
        }

        Logger.debugLog("Zoom set.");
    }

    public void checkLevelReqs() {
        Logger.debugLog("Running the checkLevelReqs() method.");

        if (!GameTabs.isStatsTabOpen()) {
            GameTabs.openStatsTab();
            Condition.sleep(1500);
        }

        int calc = Stats.getRealLevel("Hitpoints");
        eatHP = (int) (calc * (eatPercent / 100.0));
        Logger.debugLog("HP to eat at: " + eatHP);

        Logger.debugLog("Checking level requirements.");

        // Save agility level
        int agilityLevel = Stats.getRealLevel("Agility");
        Logger.debugLog("Agility level " + agilityLevel);

        // Map of course names to their required agility levels using Map.ofEntries
        Map<String, Integer> courseRequirements = Map.ofEntries(
                new AbstractMap.SimpleEntry<>("Al Kharid", 20),
                new AbstractMap.SimpleEntry<>("Varrock", 30),
                new AbstractMap.SimpleEntry<>("Canifis", 40),
                new AbstractMap.SimpleEntry<>("Falador", 50),
                new AbstractMap.SimpleEntry<>("Seers", 60),
                new AbstractMap.SimpleEntry<>("Seers - Teleport", 60),
                new AbstractMap.SimpleEntry<>("Pollnivneach", 70),
                new AbstractMap.SimpleEntry<>("Rellekka", 80),
                new AbstractMap.SimpleEntry<>("Ardougne", 90)
        );

        Integer requiredLevel = courseRequirements.get(courseChosen);

        if (requiredLevel != null && agilityLevel < requiredLevel) {
            Logger.log("Agility level not high enough for chosen course (" + courseChosen + "), stopping script.");
            Logout.logout();
            Script.stop();
        }

        GameTabs.closeStatsTab();
        Logger.debugLog("Ending level requirements.");
    }

    private void readXP() {
        XpBar.getXP();
    }

    public void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        }
    }

    private void updateStatLabel() {
        // Set separators
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        // Calculations for MoGs and laps per hour
        long currentTime = System.currentTimeMillis();
        double elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);

        // Calculate MoGs per hour and laps per hour
        double MoGsPerHour = mogCount / elapsedTimeInHours;
        double LapsPerHour = lapCount / elapsedTimeInHours;

        // Format MoGs per hour and laps per hour with one decimal place
        String MoGsPerHourFormatted = MoGsFormat.format(MoGsPerHour);
        String LapsPerHourFormatted = LapsFormat.format(LapsPerHour);

        // Update the statistics label
        String statistics = String.format("MoGs/hr: %s | Laps/hr: %s", MoGsPerHourFormatted, LapsPerHourFormatted);
        Paint.setStatistic(statistics);
    }

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

    public static class Obstacle {
        public String name;
        public Area area;
        public Tile startTile;
        public Tile endTile;
        public Rectangle pressArea;
        public Rectangle instantPressArea;
        public Tile prevEndTile;
        public List<MarkHandling> markHandling; // Change to a list of MarkHandling
        public boolean checkForMark;

        Obstacle(String name, Area area, Tile startTile, Tile endTile, Rectangle pressArea,
                 Rectangle instantPressArea, Tile prevEndTile, List<MarkHandling> markHandling, boolean checkForMark) {
            this.name = name;
            this.area = area;
            this.startTile = startTile;
            this.endTile = endTile;
            this.pressArea = pressArea;
            this.instantPressArea = instantPressArea;
            this.prevEndTile = prevEndTile;
            this.markHandling = markHandling;
            this.checkForMark = checkForMark;
        }
    }

    public static class MarkHandling {
        public final Rectangle checkArea;
        public final Color targetColor;
        public final Rectangle tapArea;
        public final Tile endTile;

        MarkHandling(Rectangle checkArea, Color targetColor, Rectangle tapArea, Tile endTile) {
            this.checkArea = checkArea;
            this.targetColor = targetColor;
            this.tapArea = tapArea;
            this.endTile = endTile;
        }

        public boolean isMarkPresent(Rectangle mogRectangle, Color mogColor) {
            if (Client.isColorInRect(mogColor, mogRectangle, 10)) {
                Logger.debugLog("Found MoG on floor");
                return true;
            } else {
                return false;
            }
        }

        public void pickUpMark(Rectangle mogRectangle, Rectangle nextObstacleRectangle, Tile endTile) {
            Paint.setStatus("Pick up MoG");
            Client.tap(mogRectangle);
            Player.waitTillNotMoving(30);
            mogTotal++;
            mogCount = mogTotal;
            Paint.updateBox(MoGIndex, mogCount);
            Logger.log("Total Marks of grace gathered so far: " + mogTotal);
            Client.tap(nextObstacleRectangle);
            Condition.wait(() -> Player.atTile(endTile), 100, 250);
            Condition.sleep(generateRandomDelay(400, 600));
        }
    }

    public static void traverseWithInstantTap(Obstacle obstacle) {
        Logger.log("Traversing obstacle " + obstacle.name);
        Logger.debugLog("Traversing " + obstacle.name + " with instant tap.");
        Paint.setStatus("Traverse " + obstacle.name);
        Client.tap(obstacle.instantPressArea);
        Condition.wait(() -> Player.atTile(obstacle.endTile), 100, 250);
        Condition.sleep(generateRandomDelay(400, 600));
    }

    public static void traverseObstacle(Obstacle obstacle) {
        Paint.setStatus("Traverse " + obstacle.name);
        Logger.log("Traversing obstacle " + obstacle.name);
        if (!Player.atTile(obstacle.startTile)) {
            Logger.debugLog("Moving to start of " + obstacle.name);
            Walker.step(obstacle.startTile);
            Condition.wait(() -> Player.atTile(obstacle.startTile), 100, 250);
            Condition.sleep(generateRandomDelay(550, 700));
        }
        if (Player.atTile(obstacle.startTile)) {
            Logger.debugLog("At start of " + obstacle.name);
            Client.tap(obstacle.pressArea);
            Condition.wait(() -> Player.atTile(obstacle.endTile), 100, 250);
            Condition.sleep(generateRandomDelay(550, 700));
        }
    }

    public static void proceedWithTraversal(Obstacle obstacle, Tile currentLocation) {
        if (Player.tileEquals(obstacle.prevEndTile, currentLocation)) {
            traverseWithInstantTap(obstacle);
        } else {
            traverseObstacle(obstacle);
        }
    }

    public class startTileStorage {
        public final Tile tile;
        public final Rectangle tapRectangle;

        public startTileStorage(Tile tile, Rectangle tapRectangle) {
            this.tile = tile;
            this.tapRectangle = tapRectangle;
        }

        public Tile getTile() {
            return tile;
        }

        public Rectangle getTapRectangle() {
            return tapRectangle;
        }
    }

}