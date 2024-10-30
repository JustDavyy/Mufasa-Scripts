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
        version = "1.07",
        categories = {ScriptCategory.Agility},
        guideLink = "https://wiki.mufasaclient.com/docs/dagility/"
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Course",
                        description = "What agility course do you want to train at?",
                        defaultValue = "Advanced Colossal Wyrm",
                        allowedValues = {
                                @AllowedValue(optionName = "Gnome"),
                                @AllowedValue(optionName = "Al Kharid"),
                                @AllowedValue(optionName = "Draynor"),
                                @AllowedValue(optionName = "Varrock"),
                                @AllowedValue(optionName = "Canifis"),
                                @AllowedValue(optionName = "Basic Colossal Wyrm"),
                                @AllowedValue(optionName = "Falador"),
                                @AllowedValue(optionName = "Seers"),
                                @AllowedValue(optionName = "Seers - teleport"),
                                @AllowedValue(optionName = "Advanced Colossal Wyrm"),
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
            new MarkHandling(new Rectangle(1, 1, 1, 1), new Color(203, 137, 25), new Rectangle(1, 1, 1, 1), new Tile(1, 1, 0), null, false)
    );
    public static List<startTileStorage> startTiles = Arrays.asList();
    public static final List<Obstacle> obstacles = new ArrayList<dAgility.Obstacle>();
    public static final Random random = new Random();
    private final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    private final DecimalFormat MoGsFormat = new DecimalFormat("#,##0.0", symbols);
    private final DecimalFormat LapsFormat = new DecimalFormat("#,##0.0", symbols);
    private final DecimalFormat ShardsFormat = new DecimalFormat("#,##0", symbols);
    private final DecimalFormat TermitesFormat = new DecimalFormat("#,##0", symbols);

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
    public static int initialTermiteCount = 0;
    public static int initialBoneShardCount = 0;
    public static int termiteCount = 0;
    public static int boneShardCount = 0;
    private static int MoGIndex;
    public static int termiteIndex;
    public static int shardIndex;
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
            new Varrock(),
            new Canifis(),
            new Falador(),
            new Seers(),
            new Pollnivneach(),
            new Rellekka(),
            new Ardougne(),
            new BasicWyrm(),
            new AdvancedWyrm()
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

        // Create image box(es), to show the amount of obtained marks
        if (courseChosen.equals("Basic Colossal Wyrm") || courseChosen.equals("Advanced Colossal Wyrm")) {
            termiteIndex = Paint.createBox("Termites", 30038, 0);
            Condition.sleep(500);
            shardIndex = Paint.createBox("Bl. Bone Shards", ItemList.BLESSED_BONE_SHARDS_29381, 0);
        } else {
            MoGIndex = Paint.createBox("Marks of Grace", ItemList.MARK_OF_GRACE_11849, 0);
        }

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

        // Open inventory again if doing the wyrm course
        if (courseChosen.equals("Basic Colossal Wyrm") || courseChosen.equals("Advanced Colossal Wyrm")) {
            GameTabs.openInventoryTab();
            Condition.sleep(1500);

            // Read initial stack values
            initialTermiteCount = Inventory.stackSize(30038);
            initialBoneShardCount = Inventory.stackSize(ItemList.BLESSED_BONE_SHARDS_29381);

            Logger.debugLog("Initial Termites count: " + initialTermiteCount);
            Logger.debugLog("Initial Blessed Bone shard count: " + initialBoneShardCount);
        }

        // Logs for debugging purposes
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
                MapChunk canifisChunks = new MapChunk(new String[]{"54-54"}, "0", "2", "3");
                Walker.setup(canifisChunks);
                break;
            case "Falador":
                MapChunk faladorChunks = new MapChunk(new String[]{"47-52"}, "0", "3");
                Walker.setup(faladorChunks);
                break;
            case "Rellekka":
                MapChunk rellekkaChunks = new MapChunk(new String[]{"41-57"}, "0", "3");
                Walker.setup(rellekkaChunks);
                break;
            case "Ardougne":
                MapChunk ardyChunks = new MapChunk(new String[]{"41-51"}, "0", "3");
                Walker.setup(ardyChunks);
                break;
            case "Draynor":
                MapChunk draynorChunks = new MapChunk(new String[]{"48-51"}, "0", "3");
                Walker.setup(draynorChunks);
                break;
            case "Pollnivneach":
                MapChunk pollyChunks = new MapChunk(new String[]{"52-46"}, "0", "1", "2");
                Walker.setup(pollyChunks);
                break;
            case "Seers":
            case "Seers - teleport":
                MapChunk seersChunks = new MapChunk(new String[]{"42-54"}, "0", "2", "3");
                Walker.setup(seersChunks);
                break;
            case "Advanced Colossal Wyrm":
            case "Basic Colossal Wyrm":
                MapChunk colossalWyrmChunks = new MapChunk(new String[]{"25-45"}, "0", "1", "2");
                Walker.setup(colossalWyrmChunks);
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
                        new MarkHandling(new Rectangle(493, 265, 10, 11), gnomeMogColor, new Rectangle(469, 279, 20, 17), new Tile(9947, 13429, 0), null, false)
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(9877, 13481, 0), new Tile(9960, 13512, 0)),
                        new Tile(9895, 13493, 0), new Tile(9895, 13465, 0),
                        new Rectangle(437, 294, 11, 20), new Rectangle(151, 313, 10, 22),
                        new Tile(9935, 13497, 0), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(9870, 13439, 0), new Tile(9915, 13472, 0)),
                        new Tile(9895, 13453, 0), new Tile(9891, 13441, 1),
                        new Rectangle(404, 282, 50, 13), new Rectangle(441, 381, 19, 9),
                        new Tile(9895, 13465, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(9874, 13426, 1), new Tile(9914, 13454, 1)),
                        new Tile(9895, 13437, 1), new Tile(9891, 13429, 2),
                        new Rectangle(419, 252, 4, 22), new Rectangle(442, 282, 3, 21),
                        new Tile(9891, 13441, 1), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(9874, 13410, 2), new Tile(9925, 13447, 2)),
                        new Tile(9907, 13429, 2), new Tile(9931, 13429, 2),
                        new Rectangle(465, 276, 25, 2), new Rectangle(573, 275, 24, 3),
                        new Tile(9891, 13429, 2), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(9927, 13408, 2), new Tile(9970, 13446, 2)),
                        new Tile(9931, 13429, 2), new Tile(9947, 13429, 0),
                        new Rectangle(525, 283, 24, 19), new Rectangle(525, 283, 24, 19),
                        new Tile(9931, 13429, 2), gnomeObstacle5Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(9925, 13403, 0), new Tile(9963, 13450, 0)),
                        new Tile(9943, 13449, 0), new Tile(9939, 13461, 0),
                        new Rectangle(411, 227, 36, 20), new Rectangle(393, 106, 34, 26),
                        new Tile(9947, 13429, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(9925, 13455, 0), new Tile(9969, 13479, 0)),
                        new Tile(9935, 13469, 0), new Tile(9935, 13497, 0),
                        new Rectangle(432, 211, 22, 29), new Rectangle(406, 159, 19, 30),
                        new Tile(9939, 13461, 0), noMarks, false, null, false));
                break;
            case "Al Kharid":
                // Mark of Grace ground color
                Color alkharidMogColor = new Color(Integer.parseInt("c98818", 16));
                // Mark of Graces
                List<MarkHandling> alkharidObstacle2Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(517, 377, 9, 10), alkharidMogColor, new Rectangle(349, 359, 9, 10), new Tile(13087, 12437, 3), null, false)
                );
                List<MarkHandling> alkharidObstacle3Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(312, 445, 10, 9), alkharidMogColor, new Rectangle(494, 219, 7, 8), new Tile(13087, 12437, 3), null, false)
                );
                List<MarkHandling> alkharidObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(491, 210, 8, 7), alkharidMogColor, new Rectangle(769, 390, 33, 19), new Tile(13087, 12437, 3), null, false)
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(13070, 12501, 0), new Tile(13214, 12544, 0)),
                        new Tile(13091, 12533, 0), new Tile(13091, 12517, 3),
                        new Rectangle(444, 289, 9, 6), null,
                        new Tile(13195, 12525, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(13071, 12459, 3), new Tile(13121, 12526, 3)),
                        new Tile(13087, 12477, 3), new Tile(13087, 12437, 3),
                        new Rectangle(441, 285, 12, 9), new Rectangle(416, 497, 12, 14),
                        new Tile(13091, 12517, 3), alkharidObstacle2Mark, true,
                        new Area(new Tile(13053, 12427, 0), new Tile(13115, 12473, 0)), true));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(13051, 12384, 3), new Tile(13101, 12449, 3)),
                        new Tile(13067, 12413, 3), new Tile(13135, 12413, 3),
                        new Rectangle(483, 253, 7, 18), new Rectangle(367, 372, 19, 12),
                        new Tile(13087, 12437, 3), alkharidObstacle3Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(13127, 12377, 3), new Tile(13227, 12458, 3)),
                        new Tile(13203, 12401, 3), new Tile(13259, 12401, 1),
                        new Rectangle(477, 259, 16, 16), new Rectangle(796, 309, 26, 17),
                        new Tile(13135, 12413, 3), alkharidObstacle4Mark, true,
                        new Area(new Tile(13206, 12367, 0), new Tile(13262, 12429, 0)), true));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(13238, 12372, 1), new Tile(13289, 12420, 1)),
                        new Tile(13271, 12409, 1), new Tile(13267, 12445, 2),
                        new Rectangle(441, 233, 15, 21), new Rectangle(490, 204, 15, 24),
                        new Tile(13259, 12401, 1), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(13231, 12430, 2), new Tile(13290, 12508, 2)),
                        new Tile(13263, 12465, 2), new Tile(13263, 12469, 3),
                        new Rectangle(438, 258, 14, 4), new Rectangle(423, 185, 13, 2),
                        new Tile(13267, 12445, 2), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(13237, 12451, 3), new Tile(13296, 12520, 3)),
                        new Tile(13251, 12489, 3), new Tile(13207, 12497, 3),
                        new Rectangle(441, 249, 10, 10), new Rectangle(396, 184, 9, 7),
                        new Tile(13263, 12469, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 8",
                        new Area(new Tile(13177, 12479, 3), new Tile(13231, 12538, 3)),
                        new Tile(13203, 12517, 3), new Tile(13195, 12525, 0),
                        new Rectangle(422, 246, 13, 12), new Rectangle(411, 177, 13, 12),
                        new Tile(13207, 12497, 3), noMarks, false, null, false));

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
                        new MarkHandling(new Rectangle(408, 267, 10, 8), varrockMogColor, new Rectangle(368, 262, 31, 31), new Tile(12767, 13373, 3), null, false)
                );
                List<MarkHandling> varrockObstacle6Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(316, 319, 8, 8), varrockMogColor, new Rectangle(800, 219, 16, 29), new Tile(12871, 13345, 3), null, false)
                );
                List<MarkHandling> varrockObstacle8Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(472, 193, 9, 7), varrockMogColor, new Rectangle(406, 230, 85, 32), new Tile(12943, 13389, 3), null, false)
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(12842, 13383, 0), new Tile(12975, 13453, 0)),
                        new Tile(12887, 13405, 0), new Tile(12875, 13405, 3),
                        new Rectangle(419, 262, 3, 10), new Rectangle(149, 312, 11, 9),
                        new Tile(12943, 13417, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(12843, 13378, 3), new Tile(12894, 13440, 3)),
                        new Tile(12855, 13405, 3), new Tile(12831, 13405, 3),
                        new Rectangle(422, 262, 13, 13), new Rectangle(338, 266, 10, 13),
                        new Tile(12875, 13405, 3), noMarks, false,
                        new Area(new Tile(12823, 13380, 0), new Tile(12866, 13434, 0)), true));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(12795, 13387, 3), new Tile(12846, 13441, 3)),
                        new Tile(12803, 13413, 3), new Tile(12787, 13413, 1),
                        new Rectangle(419, 244, 13, 28), new Rectangle(302, 215, 17, 25),
                        new Tile(12831, 13405, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(12728, 13375, 1), new Tile(12805, 13429, 1)),
                        new Tile(12775, 13413, 1), new Tile(12767, 13373, 3),
                        new Rectangle(388, 266, 28, 24), new Rectangle(339, 265, 27, 27),
                        new Tile(12787, 13413, 1), varrockObstacle4Mark, true,
                        new Area(new Tile(12745, 13358, 0), new Tile(12793, 13425, 0)), true));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(12754, 13346, 3), new Tile(12783, 13385, 3)),
                        new Tile(12771, 13357, 3), new Tile(12771, 13341, 3),
                        new Rectangle(443, 278, 18, 14), new Rectangle(460, 351, 22, 16),
                        new Tile(12767, 13373, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(12720, 13258, 3), new Tile(12843, 13347, 3)),
                        new Tile(12831, 13337, 3), new Tile(12871, 13345, 3),
                        new Rectangle(460, 261, 14, 19), new Rectangle(719, 274, 16, 21),
                        new Tile(12771, 13341, 3), varrockObstacle6Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 6-2",
                        new Area(new Tile(12803, 13310, 3), new Tile(12844, 13370, 3)),
                        new Tile(12831, 13337, 3), new Tile(12871, 13345, 3),
                        new Rectangle(460, 261, 14, 19), new Rectangle(719, 274, 16, 21),
                        new Tile(12771, 13341, 3), varrockObstacle6Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(12851, 13306, 3), new Tile(12939, 13372, 3)),
                        new Tile(12923, 13357, 3), new Tile(12943, 13361, 3),
                        new Rectangle(467, 244, 18, 18), new Rectangle(676, 204, 12, 13),
                        new Tile(12871, 13345, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 8",
                        new Area(new Tile(12936, 13350, 3), new Tile(12973, 13379, 3)),
                        new Tile(12943, 13381, 3), new Tile(12943, 13389, 3),
                        new Rectangle(444, 225, 19, 27), new Rectangle(446, 156, 28, 29),
                        new Tile(12943, 13361, 3), varrockObstacle8Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 9",
                        new Area(new Tile(12936, 13381, 3), new Tile(12969, 13418, 3)),
                        new Tile(12943, 13409, 3), new Tile(12943, 13417, 0),
                        new Rectangle(439, 255, 15, 5), new Rectangle(441, 186, 15, 10),
                        new Tile(12943, 13389, 3), noMarks, false, null, false));
                break;
            case "Canifis":
                // Mark of Grace ground color
                Color canifisMogColor = new Color(Integer.parseInt("cb8a19", 16));
                // Mark of Graces
                List<MarkHandling> canifisObstacle2Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(445, 225, 5, 5), canifisMogColor, new Rectangle(415, 218, 15, 11), new Tile(14007, 13765, 2), null, false)
                );
                List<MarkHandling> canifisObstacle3Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(428, 253, 8, 7), canifisMogColor, new Rectangle(359, 269, 17, 20), new Tile(13968, 13765, 2), null, false)
                );
                List<MarkHandling> canifisObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(405, 319, 7, 6), canifisMogColor, new Rectangle(359, 293, 13, 15), new Tile(13915, 13745, 3), null, false)
                );
                List<MarkHandling> canifisObstacle5Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(425, 321, 5, 5), canifisMogColor, new Rectangle(436, 334, 17, 31), new Tile(13911, 13693, 2), null, false)
                );
                List<MarkHandling> canifisObstacle6Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(444, 307, 5, 5), canifisMogColor, new Rectangle(480, 289, 5, 2), new Tile(13955, 13653, 3), null, false)
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(14009, 13674, 0), new Tile(14054, 13717, 0)),
                        new Tile(14031, 13697, 0), new Tile(14023, 13717, 2),
                        new Rectangle(384, 205, 6, 10), new Rectangle(370, 171, 17, 7),
                        new Tile(14039, 13689, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(14008, 13702, 2), new Tile(14056, 13749, 2)),
                        new Tile(14023, 13733, 2), new Tile(14007, 13765, 2),
                        new Rectangle(432, 231, 12, 13), new Rectangle(433, 180, 11, 12),
                        new Tile(14023, 13717, 2), canifisObstacle2Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(13979, 13749, 2), new Tile(14027, 13789, 2)),
                        new Tile(13991, 13765, 2), new Tile(13968, 13765, 2),
                        new Rectangle(414, 255, 14, 18), new Rectangle(346, 259, 15, 22),
                        new Tile(14007, 13765, 2), canifisObstacle3Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(13927, 13730, 2), new Tile(13982, 13786, 2)),
                        new Tile(13947, 13745, 2), new Tile(13915, 13745, 3),
                        new Rectangle(413, 260, 14, 17), new Rectangle(311, 346, 18, 21),
                        new Tile(13968, 13765, 2), canifisObstacle4Mark, true,
                        new Area(new Tile(13906, 13719, 0), new Tile(13952, 13764, 0)), true));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(13885, 13702, 3), new Tile(13933, 13756, 3)),
                        new Tile(13911, 13721, 3), new Tile(13911, 13693, 2),
                        new Rectangle(440, 277, 12, 31), new Rectangle(414, 391, 18, 41),
                        new Tile(13915, 13745, 3), canifisObstacle5Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(13894, 13658, 2), new Tile(13950, 13716, 2)),
                        new Tile(13915, 13685, 2), new Tile(13955, 13653, 3),
                        new Rectangle(462, 288, 5, 4), new Rectangle(476, 321, 7, 3),
                        new Tile(13911, 13693, 2), canifisObstacle6Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(13939, 13606, 3), new Tile(14038, 13680, 3)),
                        new Tile(14007, 13653, 3), new Tile(14039, 13653, 2),
                        new Rectangle(468, 255, 13, 23), new Rectangle(685, 256, 24, 22),
                        new Tile(13955, 13653, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 8",
                        new Area(new Tile(14020, 13633, 2), new Tile(14078, 13694, 2)),
                        new Tile(14039, 13677, 2), new Tile(14039, 13689, 0),
                        new Rectangle(441, 243, 13, 14), new Rectangle(441, 169, 13, 10),
                        new Tile(14039, 13653, 2), noMarks, false, null, false));
                break;
            case "Falador":
                // Mark of Grace ground color
                Color faladorMogColor = new Color(Integer.parseInt("cb8a19", 16));
                // Mark of Graces
                List<MarkHandling> faladorObstacle3Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(404, 249, 14, 15), faladorMogColor, new Rectangle(531, 171, 4, 11), new Tile(12199, 13177, 3), null, false)
                );
                List<MarkHandling> faladorObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(421, 267, 15, 11), faladorMogColor, new Rectangle(426, 232, 6, 7), new Tile(12191, 13193, 3), null, false)
                );
                List<MarkHandling> faladorObstacle9Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(419, 279, 12, 14), faladorMogColor, new Rectangle(389, 313, 5, 6), new Tile(12055, 13133, 3), null, false)
                );
                List<MarkHandling> faladorObstacle12Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(475, 268, 8, 7), faladorMogColor, new Rectangle(475, 263, 16, 10), new Tile(12075, 13081, 3), null, false)
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(12096, 13066, 0), new Tile(12177, 13130, 0)),
                        new Tile(12143, 13109, 0), new Tile(12143, 13117, 3),
                        new Rectangle(444, 240, 8, 8), new Rectangle(552, 142, 5, 9),
                        new Tile(12115, 13081, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(12131, 13105, 3), new Tile(12170, 13145, 3)),
                        new Tile(12155, 13121, 3), new Tile(12187, 13125, 3),
                        new Rectangle(462, 266, 8, 7), new Rectangle(512, 247, 10, 7),
                        new Tile(12143, 13117, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(12168, 13099, 3), new Tile(12223, 13156, 3)),
                        new Tile(12199, 13141, 3), new Tile(12199, 13177, 3),
                        new Rectangle(444, 196, 14, 23), new Rectangle(494, 144, 13, 18),
                        new Tile(12187, 13125, 3), faladorObstacle3Mark, true,
                        new Area(new Tile(12173, 13104, 0), new Tile(12222, 13226, 0)), true));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(12187, 13167, 3), new Tile(12208, 13185, 3)),
                        new Tile(12195, 13181, 3), new Tile(12191, 13193, 3),
                        new Rectangle(421, 244, 16, 13), new Rectangle(405, 229, 11, 15),
                        new Tile(12199, 13177, 3), faladorObstacle4Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(12173, 13184, 3), new Tile(12201, 13226, 3)),
                        new Tile(12179, 13193, 3), new Tile(12163, 13193, 3),
                        new Rectangle(421, 261, 12, 19), new Rectangle(373, 245, 13, 28),
                        new Tile(12191, 13193, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(12128, 13181, 3), new Tile(12172, 13212, 3)),
                        new Tile(12139, 13193, 3), new Tile(12111, 13165, 3),
                        new Rectangle(423, 257, 10, 11), new Rectangle(321, 259, 11, 12),
                        new Tile(12163, 13193, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(12098, 13148, 3), new Tile(12128, 13174, 3)),
                        new Tile(12107, 13161, 3), new Tile(12079, 13161, 3),
                        new Rectangle(424, 269, 8, 10), new Rectangle(407, 285, 9, 8),
                        new Tile(12111, 13165, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 8",
                        new Area(new Tile(12029, 13149, 3), new Tile(12096, 13189, 3)),
                        new Tile(12071, 13161, 3), new Tile(12071, 13145, 3),
                        new Rectangle(441, 274, 13, 4), new Rectangle(401, 274, 17, 5),
                        new Tile(12079, 13161, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 9",
                        new Area(new Tile(12062, 13114, 3), new Tile(12095, 13146, 3)),
                        new Tile(12067, 13133, 3), new Tile(12055, 13133, 3),
                        new Rectangle(402, 284, 16, 10), new Rectangle(396, 322, 12, 8),
                        new Tile(12071, 13145, 3), faladorObstacle9Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 10",
                        new Area(new Tile(12035, 13120, 3), new Tile(12063, 13145, 3)),
                        new Tile(12051, 13125, 3), new Tile(12051, 13117, 3),
                        new Rectangle(433, 278, 21, 9), new Rectangle(406, 311, 25, 13),
                        new Tile(12055, 13133, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 11",
                        new Area(new Tile(12028, 13083, 3), new Tile(12060, 13121, 3)),
                        new Tile(12051, 13089, 3), new Tile(12051, 13081, 3),
                        new Rectangle(438, 282, 15, 18), new Rectangle(436, 416, 17, 17),
                        new Tile(12051, 13117, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 12",
                        new Area(new Tile(12045, 13065, 3), new Tile(12073, 13083, 3)),
                        new Tile(12063, 13081, 3), new Tile(12075, 13081, 3),
                        new Rectangle(458, 260, 16, 14), new Rectangle(506, 260, 17, 12),
                        new Tile(12051, 13081, 3), faladorObstacle12Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 13",
                        new Area(new Tile(12074, 13070, 3), new Tile(12118, 13091, 3)),
                        new Tile(12087, 13081, 3), new Tile(12115, 13081, 0),
                        new Rectangle(482, 261, 14, 16), new Rectangle(536, 259, 15, 16),
                        new Tile(12075, 13081, 3), noMarks, false, null, false));
                break;
            case "Rellekka":
                // Mark of Grace ground color
                Color rellekkaMogColor = new Color(Integer.parseInt("d38f1a", 16));
                // Mark of Graces
                List<MarkHandling> rellekkaObstacle2Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(404, 282, 9, 8), rellekkaMogColor, new Rectangle(400, 333, 52, 49), new Tile(10487, 14421, 3), null, false)
                );
                List<MarkHandling> rellekkaObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(462, 301, 9, 9), rellekkaMogColor, new Rectangle(459, 198, 55, 28), new Tile(10555, 14361, 3), null, false),
                        new MarkHandling(new Rectangle(478, 286, 9, 7), rellekkaMogColor, new Rectangle(443, 209, 56, 29), new Tile(10555, 14361, 3), null, false)
                );
                List<MarkHandling> rellekkaObstacle5Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(500, 295, 10, 11), rellekkaMogColor, new Rectangle(458, 215, 27, 28), new Tile(10571, 14377, 3), null, false),
                        new MarkHandling(new Rectangle(482, 316, 8, 9), rellekkaMogColor, new Rectangle(472, 200, 30, 27), new Tile(10571, 14377, 3), null, false)
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(10477, 14438, 0), new Tile(10522, 14478, 0)),
                        new Tile(10499, 14461, 0), new Tile(10499, 14453, 3),
                        new Rectangle(448, 282, 8, 5), null,
                        new Tile(10607, 14453, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(10480, 14428, 3), new Tile(10516, 14462, 3)),
                        new Tile(10491, 14441, 3), new Tile(10487, 14421, 3),
                        new Rectangle(404, 295, 48, 41), new Rectangle(361, 355, 43, 43),
                        new Tile(10499, 14453, 3), rellekkaObstacle2Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(10453, 14373, 3), new Tile(10500, 14426, 3)),
                        new Tile(10487, 14385, 3), new Tile(10507, 14365, 3),
                        new Rectangle(456, 281, 14, 13), new Rectangle(458, 466, 17, 25),
                        new Tile(10487, 14421, 3), noMarks, false,
                        new Area(new Tile(10478, 14348, 0), new Tile(10566, 14393, 0)), true));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(10500, 14338, 3), new Tile(10532, 14374, 3)),
                        new Tile(10515, 14369, 3), new Tile(10555, 14361, 3),
                        new Rectangle(445, 234, 55, 28), new Rectangle(477, 228, 56, 24),
                        new Tile(10507, 14365, 3), rellekkaObstacle4Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(10551, 14336, 3), new Tile(10586, 14367, 3)),
                        new Tile(10571, 14361, 3), new Tile(10571, 14377, 3),
                        new Rectangle(444, 234, 24, 29), new Rectangle(513, 237, 23, 25),
                        new Tile(10555, 14361, 3), rellekkaObstacle5Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(10563, 14365, 3), new Tile(10610, 14406, 3)),
                        new Tile(10587, 14397, 3), new Tile(10619, 14429, 3),
                        new Rectangle(442, 250, 13, 11), new Rectangle(504, 184, 10, 9),
                        new Tile(10571, 14377, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(10603, 14401, 3), new Tile(10681, 14506, 3)),
                        new Tile(10619, 14453, 3), new Tile(10607, 14453, 0),
                        new Rectangle(404, 248, 22, 22), new Rectangle(413, 165, 21, 23),
                        new Tile(10619, 14429, 3), noMarks, false, null, false));

                // Start tiles
                startTiles = Arrays.asList(
                        new startTileStorage(new Tile(10503, 14461, 0), new Rectangle(428, 288, 10, 4)),
                        new startTileStorage(new Tile(10495, 14465, 0), new Rectangle(461, 300, 8, 4)),
                        new startTileStorage(new Tile(10495, 14461, 0), new Rectangle(461, 284, 8, 5)),
                        new startTileStorage(new Tile(10495, 14457, 0), new Rectangle(460, 267, 9, 7)),
                        new startTileStorage(new Tile(10499, 14457, 0), new Rectangle(447, 267, 10, 6)),
                        new startTileStorage(new Tile(10499, 14461, 0), new Rectangle(448, 282, 8, 5)),
                        new startTileStorage(new Tile(10499, 14465, 0), new Rectangle(448, 301, 9, 5)),
                        new startTileStorage(new Tile(10503, 14469, 0), new Rectangle(431, 322, 6, 5)),
                        new startTileStorage(new Tile(10503, 14465, 0), new Rectangle(433, 307, 7, 5)),
                        new startTileStorage(new Tile(10503, 14461, 0), new Rectangle(428, 286, 10, 6)),
                        new startTileStorage(new Tile(10503, 14457, 0), new Rectangle(428, 266, 11, 7)),
                        new startTileStorage(new Tile(10507, 14457, 0), new Rectangle(410, 266, 10, 5)),
                        new startTileStorage(new Tile(10507, 14461, 0), new Rectangle(411, 284, 9, 7)),
                        new startTileStorage(new Tile(10507, 14465, 0), new Rectangle(410, 303, 10, 5)),
                        new startTileStorage(new Tile(10507, 14469, 0), new Rectangle(410, 322, 9, 6)),
                        new startTileStorage(new Tile(10479, 14465, 0), new Rectangle(543, 299, 6, 6)),
                        new startTileStorage(new Tile(10479, 14461, 0), new Rectangle(537, 281, 5, 7)),
                        new startTileStorage(new Tile(10483, 14465, 0), new Rectangle(526, 294, 6, 8)),
                        new startTileStorage(new Tile(10483, 14461, 0), new Rectangle(523, 281, 7, 8)),
                        new startTileStorage(new Tile(10487, 14465, 0), new Rectangle(506, 292, 7, 10)),
                        new startTileStorage(new Tile(10491, 14465, 0), new Rectangle(487, 297, 8, 7)),
                        new startTileStorage(new Tile(10491, 14461, 0), new Rectangle(485, 285, 8, 8)),
                        new startTileStorage(new Tile(10491, 14457, 0), new Rectangle(484, 265, 7, 9)),
                        new startTileStorage(new Tile(10519, 14457, 0), new Rectangle(355, 266, 9, 7)),
                        new startTileStorage(new Tile(10519, 14461, 0), new Rectangle(354, 280, 10, 8)),
                        new startTileStorage(new Tile(10519, 14465, 0), new Rectangle(353, 300, 9, 6)),
                        new startTileStorage(new Tile(10515, 14465, 0), new Rectangle(369, 301, 9, 6)),
                        new startTileStorage(new Tile(10515, 14461, 0), new Rectangle(370, 285, 8, 7)),
                        new startTileStorage(new Tile(10515, 14457, 0), new Rectangle(368, 265, 9, 7)),
                        new startTileStorage(new Tile(10511, 14457, 0), new Rectangle(389, 265, 7, 8)),
                        new startTileStorage(new Tile(10511, 14461, 0), new Rectangle(387, 281, 7, 6)),
                        new startTileStorage(new Tile(10511, 14465, 0), new Rectangle(388, 303, 8, 6))
                );
                break;
            case "Ardougne":
                // Mark of Grace ground color
                Color ardyMogColor = new Color(Integer.parseInt("c98718", 16));
                // Mark of Graces
                List<MarkHandling> ardyObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(456, 265, 10, 9), ardyMogColor, new Rectangle(384, 262, 14, 22), new Tile(10611, 13005, 3), null, false)
                );

                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(10664, 12900, 0), new Tile(10711, 12950, 0)),
                        new Tile(10691, 12937, 0), new Tile(10683, 12945, 3),
                        new Rectangle(443, 249, 11, 9), new Rectangle(527, 248, 14, 9),
                        new Tile(10671, 12937, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(10676, 12937, 3), new Tile(10694, 12996, 3)),
                        new Tile(10683, 12981, 3), new Tile(10659, 13021, 3),
                        new Rectangle(430, 221, 12, 28), new Rectangle(433, 111, 10, 22),
                        new Tile(10683, 12945, 3), noMarks, false,
                        new Area(new Tile(10581, 12945, 0), new Tile(10708, 13051, 0)), true));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(10639, 13011, 3), new Tile(10670, 13027, 3)),
                        new Tile(10647, 13021, 3), new Tile(10623, 13021, 3),
                        new Rectangle(422, 268, 12, 5), new Rectangle(374, 267, 13, 6),
                        new Tile(10659, 13021, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(10609, 13011, 3), new Tile(10637, 13028, 3)),
                        new Tile(10615, 13021, 3), new Tile(10611, 13005, 3),
                        new Rectangle(419, 264, 14, 17), new Rectangle(383, 266, 14, 16),
                        new Tile(10623, 13021, 3), ardyObstacle4Mark, true,
                        new Area(new Tile(10581, 12945, 0), new Tile(10708, 13051, 0)), true));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(10609, 12982, 3), new Tile(10621, 13007, 3)),
                        new Tile(10611, 12989, 3), new Tile(10603, 12985, 3),
                        new Rectangle(436, 279, 15, 31), new Rectangle(438, 356, 13, 31),
                        new Tile(10611, 13005, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(10600, 12957, 3), new Tile(10610, 12987, 3)),
                        new Tile(10611, 12953, 3), new Tile(10623, 12937, 3),
                        new Rectangle(462, 282, 8, 12), new Rectangle(502, 443, 12, 14),
                        new Tile(10603, 12985, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 6-2",
                        new Area(new Tile(10601, 12944, 3), new Tile(10622, 12967, 3)),
                        new Tile(10611, 12953, 3), new Tile(10623, 12937, 3),
                        new Rectangle(462, 282, 8, 12), new Rectangle(502, 443, 12, 14),
                        new Tile(10603, 12985, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(10620, 12904, 3), new Tile(10636, 12944, 3)),
                        new Tile(10623, 12937, 3), new Tile(10671, 12937, 0),
                        new Rectangle(450, 273, 13, 19), new Rectangle(450, 273, 13, 19),
                        new Tile(10623, 12937, 3), noMarks, false, null, false));

                break;
            case "Draynor":
                // Mark of Grace ground color
                Color draynorMogColor = new Color(Integer.parseInt("d38f1a", 16));
                // Mark of Graces
                List<MarkHandling> draynorObstacle2Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(247, 174, 19, 16), draynorMogColor, new Rectangle(427, 455, 29, 28), new Tile(12359, 12853, 3), null, false)
                );
                List<MarkHandling> draynorObstacle3Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(381, 364, 23, 16), draynorMogColor, new Rectangle(559, 195, 25, 22), new Tile(12367, 12813, 3), null, false)
                );
                List<MarkHandling> draynorObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(535, 273, 27, 25), draynorMogColor, new Rectangle(216, 375, 26, 21), new Tile(12351, 12793, 3), null, false)
                );
                List<MarkHandling> draynorObstacle7Marks = Arrays.asList(
                        new MarkHandling(new Rectangle(619, 220, 21, 16), draynorMogColor, new Rectangle(497, 41, 97, 59), new Tile(12411, 12793, 0), null, false),
                        new MarkHandling(new Rectangle(485, 142, 20, 16), draynorMogColor, new Rectangle(631, 131, 32, 63), new Tile(12411, 12793, 0), null, false)
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(12405, 12848, 0), new Tile(12443, 12879, 0)),
                        new Tile(12415, 12865, 0), new Tile(12407, 12865, 3),
                        new Rectangle(375, 245, 4, 19), null,
                        new Tile(12411, 12793, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(12384, 12848, 3), new Tile(12417, 12879, 3)),
                        new Tile(12395, 12857, 3), new Tile(12359, 12853, 3),
                        new Rectangle(388, 281, 23, 24), new Rectangle(238, 346, 27, 25),
                        new Tile(12407, 12865, 3), draynorObstacle2Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(12340, 12829, 3), new Tile(12381, 12868, 3)),
                        new Tile(12363, 12853, 3), new Tile(12367, 12813, 3),
                        new Rectangle(486, 275, 23, 20), new Rectangle(519, 273, 26, 25),
                        new Tile(12359, 12853, 3), draynorObstacle3Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(12352, 12801, 3), new Tile(12385, 12824, 3)),
                        new Tile(12355, 12809, 3), new Tile(12351, 12793, 3),
                        new Rectangle(429, 323, 27, 29), new Rectangle(304, 370, 30, 29),
                        new Tile(12367, 12813, 3), draynorObstacle4Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(12344, 12770, 3), new Tile(12362, 12796, 3)),
                        new Tile(12351, 12777, 3), new Tile(12351, 12769, 3),
                        new Rectangle(421, 294, 43, 25), new Rectangle(421, 495, 38, 32),
                        new Tile(12351, 12793, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(12345, 12761, 3), new Tile(12383, 12770, 3)),
                        new Tile(12375, 12769, 3), new Tile(12383, 12773, 3),
                        new Rectangle(479, 267, 36, 30), new Rectangle(741, 270, 18, 27),
                        new Tile(12351, 12769, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(12382, 12767, 3), new Tile(12412, 12796, 3)),
                        new Tile(12403, 12793, 3), new Tile(12411, 12793, 0),
                        new Rectangle(485, 193, 71, 74), new Rectangle(668, 3, 24, 20),
                        new Tile(12383, 12773, 3), draynorObstacle7Marks, true, null, false));

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
                // Mark of Grace ground color
                Color pollyMogColor = new Color(Integer.parseInt("cb8a19", 16));
                // Mark of Graces
                List<MarkHandling> pollyObstacle2Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(228, 114, 15, 13), pollyMogColor, new Rectangle(575, 111, 39, 100), new Tile(13407, 11641, 1), null, false)
                );
                List<MarkHandling> pollyObstacle3Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(487, 188, 15, 15), pollyMogColor, new Rectangle(569, 95, 68, 67), new Tile(13439, 11657, 1), null, false)
                );
                List<MarkHandling> pollyObstacle7Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(148, 253, 21, 19), pollyMogColor, new Rectangle(344, 143, 66, 81), new Tile(13431, 11713, 2), null, false)
                );
                List<MarkHandling> pollyObstacle8Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(606, 187, 14, 17), pollyMogColor, new Rectangle(354, 109, 21, 29), new Tile(13435, 11749, 2), null, false)
                );
                List<MarkHandling> pollyObstacle9Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(349, 188, 12, 13), pollyMogColor, new Rectangle(705, 293, 95, 94), new Tile(13451, 11741, 0), null, false)
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(13390, 11576, 0), new Tile(13425, 11619, 0)),
                        new Tile(13403, 11593, 0), new Tile(13403, 11605, 1),
                        new Rectangle(435, 225, 13, 15), null, // No instantPressArea
                        new Tile(13451, 11741, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(13374, 11586, 1), new Tile(13416, 11629, 1)),
                        new Tile(13395, 11621, 1), new Tile(13407, 11641, 1),
                        new Rectangle(452, 159, 29, 56), new Rectangle(371, 43, 29, 31),
                        new Tile(13403, 11605, 1), pollyObstacle2Mark, true,
                        new Area(new Tile(13387, 11616, 0), new Tile(13418, 11648, 0)), true));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(13401, 11631, 1), new Tile(13430, 11660, 1)),
                        new Tile(13419, 11653, 1), new Tile(13439, 11657, 1),
                        new Rectangle(494, 146, 44, 43), new Rectangle(604, 72, 46, 22),
                        new Tile(13407, 11641, 1), pollyObstacle3Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(13434, 11649, 1), new Tile(13456, 11668, 1)),
                        new Tile(13447, 11661, 1), new Tile(13463, 11653, 1),
                        new Rectangle(509, 307, 66, 49), new Rectangle(600, 255, 59, 56),
                        new Tile(13439, 11657, 1), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(13458, 11635, 1), new Tile(13489, 11658, 1)),
                        new Tile(13471, 11653, 1), new Tile(13471, 11677, 1),
                        new Rectangle(407, 174, 100, 58), new Rectangle(495, 174, 101, 52),
                        new Tile(13463, 11653, 1), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(13455, 11666, 1), new Tile(13489, 11696, 1)),
                        new Tile(13463, 11677, 1), new Tile(13459, 11681, 2),
                        new Rectangle(384, 232, 16, 15), new Rectangle(298, 232, 17, 15),
                        new Tile(13471, 11677, 1), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 7",
                        new Area(new Tile(13414, 11659, 2), new Tile(13470, 11695, 2)),
                        new Tile(13431, 11685, 2), new Tile(13431, 11713, 2),
                        new Rectangle(410, 158, 69, 72), new Rectangle(73, 132, 61, 57),
                        new Tile(13459, 11681, 2), pollyObstacle7Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 8",
                        new Area(new Tile(13419, 11698, 2), new Tile(13494, 11736, 2)),
                        new Tile(13439, 11729, 2), new Tile(13435, 11749, 2),
                        new Rectangle(434, 178, 24, 33), new Rectangle(506, 42, 19, 25),
                        new Tile(13431, 11713, 2), pollyObstacle8Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 9",
                        new Area(new Tile(13415, 11738, 2), new Tile(13460, 11775, 2)),
                        new Tile(13443, 11757, 2), new Tile(13451, 11741, 0),
                        new Rectangle(547, 302, 65, 64), new Rectangle(616, 223, 64, 61),
                        new Tile(13435, 11749, 2), pollyObstacle9Mark, true, null, false));

                // Start tiles
                startTiles = Arrays.asList(
                        new startTileStorage(new Tile(13411, 11597, 0), new Rectangle(341, 259, 15, 16)),
                        new startTileStorage(new Tile(13407, 11601, 0), new Rectangle(386, 300, 14, 15)),
                        new startTileStorage(new Tile(13407, 11597, 0), new Rectangle(389, 266, 11, 16)),
                        new startTileStorage(new Tile(13407, 11593, 0), new Rectangle(390, 228, 13, 14)),
                        new startTileStorage(new Tile(13403, 11593, 0), new Rectangle(434, 225, 14, 16)),
                        new startTileStorage(new Tile(13411, 11593, 0), new Rectangle(356, 228, 11, 14)),
                        new startTileStorage(new Tile(13415, 11593, 0), new Rectangle(311, 227, 13, 18)),
                        new startTileStorage(new Tile(13419, 11593, 0), new Rectangle(265, 228, 14, 17)),
                        new startTileStorage(new Tile(13419, 11597, 0), new Rectangle(264, 260, 13, 17)),
                        new startTileStorage(new Tile(13419, 11601, 0), new Rectangle(258, 302, 14, 17)),
                        new startTileStorage(new Tile(13423, 11601, 0), new Rectangle(211, 303, 13, 17)),
                        new startTileStorage(new Tile(13423, 11597, 0), new Rectangle(216, 270, 15, 15)),
                        new startTileStorage(new Tile(13423, 11593, 0), new Rectangle(222, 228, 13, 16)),
                        new startTileStorage(new Tile(13423, 11605, 0), new Rectangle(204, 345, 14, 17)),
                        new startTileStorage(new Tile(13427, 11605, 0), new Rectangle(156, 346, 13, 16)),
                        new startTileStorage(new Tile(13427, 11609, 0), new Rectangle(148, 392, 16, 17)),
                        new startTileStorage(new Tile(13423, 11609, 0), new Rectangle(184, 391, 15, 18)),
                        new startTileStorage(new Tile(13407, 11605, 0), new Rectangle(385, 343, 15, 17)),
                        new startTileStorage(new Tile(13407, 11609, 0), new Rectangle(383, 392, 16, 18)),
                        new startTileStorage(new Tile(13411, 11605, 0), new Rectangle(348, 355, 13, 17)),
                        new startTileStorage(new Tile(13407, 11613, 0), new Rectangle(383, 442, 14, 17)),
                        new startTileStorage(new Tile(13411, 11613, 0), new Rectangle(344, 440, 14, 19))
                );
                break;
            case "Seers":
            case "Seers - teleport":
                // Mark of Grace ground color
                Color seersMogColor = new Color(Integer.parseInt("ca8818", 16));
                // Mark of Graces
                List<MarkHandling> seersObstacle2Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(358, 190, 15, 13), seersMogColor, new Rectangle(112, 164, 35, 140), new Tile(10851, 13725, 2), null, false)
                );
                List<MarkHandling> seersObstacle3Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(253, 315, 18, 15), seersMogColor, new Rectangle(476, 453, 30, 32), new Tile(10839, 13669, 2), null, false),
                        new MarkHandling(new Rectangle(114, 318, 16, 11), seersMogColor, new Rectangle(624, 452, 30, 31), new Tile(10839, 13669, 2), null, false)
                );
                List<MarkHandling> seersObstacle4Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(522, 235, 16, 14), seersMogColor, new Rectangle(317, 475, 275, 59), new Tile(10847, 13637, 3), null, false)
                );
                List<MarkHandling> seersObstacle6Mark = Arrays.asList(
                        new MarkHandling(new Rectangle(259, 273, 16, 14), seersMogColor, new Rectangle(670, 249, 27, 139), new Tile(10815, 13605, 0), null, false)
                );
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(10888, 13663, 0), new Tile(10953, 13731, 0)),
                        new Tile(10915, 13701, 0), new Tile(10915, 13713, 3),
                        new Rectangle(433, 203, 35, 24), null, // No instantPressArea
                        new Tile(10815, 13605, 0), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(10867, 13691, 3), new Tile(10938, 13757, 3)),
                        new Tile(10887, 13725, 3), new Tile(10851, 13725, 2),
                        new Rectangle(328, 238, 37, 53), new Rectangle(46, 127, 37, 49),
                        new Tile(10915, 13713, 3), seersObstacle2Mark, true,
                        new Area(new Tile(10810, 13667, 0), new Tile(10884, 13758, 0)), true));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(10806, 13682, 2), new Tile(10896, 13750, 2)),
                        new Tile(10839, 13709, 2), new Tile(10839, 13669, 2),
                        new Rectangle(432, 314, 24, 27), new Rectangle(286, 499, 25, 29),
                        new Tile(10851, 13725, 2), seersObstacle3Mark, true,
                        new Area(new Tile(10810, 13667, 0), new Tile(10884, 13758, 0)), true));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(10826, 13641, 2), new Tile(10880, 13680, 2)),
                        new Tile(10839, 13657, 2), new Tile(10839, 13637, 3),
                        new Rectangle(425, 292, 33, 54), new Rectangle(429, 440, 38, 44),
                        new Tile(10839, 13669, 2), seersObstacle4Mark, true, null, false));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(10784, 13607, 3), new Tile(10889, 13670, 3)),
                        new Tile(10811, 13629, 3), new Tile(10807, 13609, 2),
                        new Rectangle(432, 301, 32, 39), new Rectangle(73, 395, 46, 39),
                        new Tile(10839, 13637, 3), noMarks, false, null, false));

                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(10748, 13574, 2), new Tile(10829, 13627, 2)),
                        new Tile(10807, 13605, 2), new Tile(10815, 13605, 0),
                        new Rectangle(485, 268, 35, 36), new Rectangle(486, 301, 33, 38),
                        new Tile(10807, 13609, 2), seersObstacle6Mark, true, null, false));

                // Start tiles
                startTiles = Arrays.asList(
                        new startTileStorage(new Tile(10911, 13693, 0), new Rectangle(471, 129, 29, 24)),
                        new startTileStorage(new Tile(10907, 13693, 0), new Rectangle(521, 130, 20, 24)),
                        new startTileStorage(new Tile(10907, 13689, 0), new Rectangle(520, 95, 15, 23)),
                        new startTileStorage(new Tile(10907, 13685, 0), new Rectangle(514, 67, 24, 20)),
                        new startTileStorage(new Tile(10911, 13685, 0), new Rectangle(479, 69, 26, 18)),
                        new startTileStorage(new Tile(10915, 13685, 0), new Rectangle(434, 68, 28, 21)),
                        new startTileStorage(new Tile(10915, 13689, 0), new Rectangle(443, 97, 26, 12)),
                        new startTileStorage(new Tile(10915, 13693, 0), new Rectangle(436, 125, 31, 24)),
                        new startTileStorage(new Tile(10919, 13693, 0), new Rectangle(391, 135, 33, 24)),
                        new startTileStorage(new Tile(10919, 13689, 0), new Rectangle(394, 106, 28, 24)),
                        new startTileStorage(new Tile(10919, 13685, 0), new Rectangle(392, 72, 32, 27)),
                        new startTileStorage(new Tile(10915, 13697, 0), new Rectangle(423, 164, 34, 24)),
                        new startTileStorage(new Tile(10911, 13697, 0), new Rectangle(472, 160, 27, 26)),
                        new startTileStorage(new Tile(10911, 13701, 0), new Rectangle(474, 200, 30, 24)),
                        new startTileStorage(new Tile(10903, 13701, 0), new Rectangle(565, 201, 28, 22)),
                        new startTileStorage(new Tile(10903, 13697, 0), new Rectangle(563, 170, 25, 24)),
                        new startTileStorage(new Tile(10899, 13693, 0), new Rectangle(602, 125, 30, 23)),
                        new startTileStorage(new Tile(10899, 13689, 0), new Rectangle(603, 93, 20, 23)),
                        new startTileStorage(new Tile(10899, 13685, 0), new Rectangle(596, 64, 26, 20)),
                        new startTileStorage(new Tile(10895, 13693, 0), new Rectangle(645, 124, 17, 25)),
                        new startTileStorage(new Tile(10895, 13689, 0), new Rectangle(641, 94, 16, 24)),
                        new startTileStorage(new Tile(10895, 13685, 0), new Rectangle(634, 65, 21, 19)),
                        new startTileStorage(new Tile(10903, 13685, 0), new Rectangle(564, 65, 28, 20)),
                        new startTileStorage(new Tile(10899, 13697, 0), new Rectangle(607, 158, 26, 29)),
                        new startTileStorage(new Tile(10899, 13701, 0), new Rectangle(609, 200, 23, 20))
                );
                break;
            case "Basic Colossal Wyrm":
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(6554, 11453, 0), new Tile(6618, 11502, 0)),
                        new Tile(6603, 11473, 0), new Tile(6611, 11473, 1),
                        new Rectangle(467, 252, 6, 10), new Rectangle(609, 275, 12, 6),
                        new Tile(6579, 11481, 0), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(6601, 11441, 1), new Tile(6643, 11493, 1)),
                        new Tile(6619, 11453, 1), new Tile(6595, 11389, 1),
                        new Rectangle(442, 284, 13, 12), new Rectangle(485, 366, 15, 19),
                        new Tile(6611, 11473, 1), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(6581, 11361, 1), new Tile(6613, 11401, 1)),
                        new Tile(6587, 11389, 1), new Tile(6539, 11389, 1),
                        new Rectangle(421, 269, 14, 10), new Rectangle(388, 268, 14, 12),
                        new Tile(6595, 11389, 1), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(6524, 11358, 1), new Tile(6555, 11402, 1)),
                        new Tile(6527, 11389, 1), new Tile(6507, 11473, 1),
                        new Rectangle(420, 253, 12, 21), new Rectangle(364, 255, 13, 18),
                        new Tile(6539, 11389, 1), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(6483, 11459, 1), new Tile(6525, 11492, 1)),
                        new Tile(6503, 11477, 1), new Tile(6499, 11477, 2),
                        new Rectangle(435, 254, 5, 8), new Rectangle(417, 245, 5, 9),
                        new Tile(6507, 11473, 1), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(6482, 11456, 2), new Tile(6515, 11492, 2)),
                        new Tile(6499, 11481, 2), new Tile(6579, 11481, 0),
                        new Rectangle(459, 251, 9, 21), new Rectangle(459, 236, 9, 22),
                        new Tile(6499, 11477, 2), noMarks, false, null, false));
                break;
            case "Advanced Colossal Wyrm":
                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(6554, 11453, 0), new Tile(6618, 11502, 0)),
                        new Tile(6603, 11473, 0), new Tile(6611, 11473, 1),
                        new Rectangle(467, 252, 6, 10), new Rectangle(609, 275, 12, 6),
                        new Tile(6579, 11481, 0), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(6601, 11441, 1), new Tile(6643, 11493, 1)),
                        new Tile(6619, 11453, 1), new Tile(6595, 11389, 1),
                        new Rectangle(442, 284, 13, 12), new Rectangle(485, 366, 15, 19),
                        new Tile(6611, 11473, 1), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(6581, 11361, 1), new Tile(6613, 11401, 1)),
                        new Tile(6591, 11385, 1), new Tile(6591, 11381, 2),
                        new Rectangle(442, 267, 6, 9), new Rectangle(423, 284, 8, 10),
                        new Tile(6595, 11389, 1), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(6573, 11355, 2), new Tile(6620, 11395, 2)),
                        new Tile(6587, 11377, 2), new Tile(6539, 11377, 2),
                        new Rectangle(419, 253, 15, 25), new Rectangle(400, 274, 16, 18),
                        new Tile(6591, 11381, 2), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(6523, 11357, 2), new Tile(6564, 11395, 2)),
                        new Tile(6531, 11377, 2), new Tile(6495, 11473, 2),
                        new Rectangle(440, 253, 10, 12), new Rectangle(408, 253, 10, 11),
                        new Tile(6539, 11377, 2), noMarks, false, null, false));
                obstacles.add(new Obstacle("Obstacle 6",
                        new Area(new Tile(6482, 11456, 2), new Tile(6515, 11492, 2)),
                        new Tile(6499, 11481, 2), new Tile(6579, 11481, 0),
                        new Rectangle(459, 251, 9, 21), new Rectangle(474, 218, 11, 19),
                        new Tile(6495, 11473, 2), noMarks, false, null, false));
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
                new AbstractMap.SimpleEntry<>("Ardougne", "1"),
                new AbstractMap.SimpleEntry<>("Basic Colossal Wyrm", "1"),
                new AbstractMap.SimpleEntry<>("Advanced Colossal Wyrm", "1")
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
                new AbstractMap.SimpleEntry<>("Basic Colossal Wyrm", 50),
                new AbstractMap.SimpleEntry<>("Seers", 60),
                new AbstractMap.SimpleEntry<>("Seers - Teleport", 60),
                new AbstractMap.SimpleEntry<>("Advanced Colossal Wyrm", 62),
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
        if (courseChosen.equals("Basic Colossal Wyrm") || courseChosen.equals("Advanced Colossal Wyrm")) {
            // Set separators
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');

            // Calculations for MoGs and laps per hour
            long currentTime = System.currentTimeMillis();
            double elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);

            // Calculate MoGs per hour and laps per hour
            double TermitesPerHour = termiteCount/ elapsedTimeInHours;
            double ShardsPerHour = boneShardCount / elapsedTimeInHours;
            double LapsPerHour = lapCount / elapsedTimeInHours;

            // Format Termites per hour, shards per hour, and laps per hour with one decimal place
            String TermitesPerHourFormatted = TermitesFormat.format(TermitesPerHour);
            String ShardsPerHourFormatted = ShardsFormat.format(ShardsPerHour);
            String LapsPerHourFormatted = LapsFormat.format(LapsPerHour);

            // Update the statistics label with all three stats
            String statistics = String.format("Term %s | Shard %s | Lap %s /hr", TermitesPerHourFormatted, ShardsPerHourFormatted, LapsPerHourFormatted);
            Paint.setStatistic(statistics);
        } else {
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
        public Area failArea;
        public boolean checkForFail;

        Obstacle(String name, Area area, Tile startTile, Tile endTile, Rectangle pressArea,
                 Rectangle instantPressArea, Tile prevEndTile, List<MarkHandling> markHandling, boolean checkForMark, Area failArea, boolean checkForFail) {
            this.name = name;
            this.area = area;
            this.startTile = startTile;
            this.endTile = endTile;
            this.pressArea = pressArea;
            this.instantPressArea = instantPressArea;
            this.prevEndTile = prevEndTile;
            this.markHandling = markHandling;
            this.checkForMark = checkForMark;
            this.failArea = failArea;
            this.checkForFail = checkForFail;
        }
    }

    public static class MarkHandling {
        public final Rectangle checkArea;
        public final Color targetColor;
        public final Rectangle tapArea;
        public final Tile endTile;
        public final Area failArea;
        public final boolean checkForFail;

        MarkHandling(Rectangle checkArea, Color targetColor, Rectangle tapArea, Tile endTile, Area failArea, boolean checkForFail) {
            this.checkArea = checkArea;
            this.targetColor = targetColor;
            this.tapArea = tapArea;
            this.endTile = endTile;
            this.failArea = failArea;
            this.checkForFail = checkForFail;
        }

        public boolean isMarkPresent(Rectangle mogRectangle, Color mogColor) {
            if (Client.isColorInRect(mogColor, mogRectangle, 10)) {
                Logger.debugLog("Found MoG on floor");
                return true;
            } else {
                return false;
            }
        }

        public void pickUpMark(Rectangle mogRectangle, Rectangle nextObstacleRectangle, Tile endTile, Area failArea, boolean checkForFail) {
            Paint.setStatus("Pick up MoG");
            Client.tap(mogRectangle);
            Player.waitTillNotMoving(30);
            mogTotal++;
            mogCount = mogTotal;
            Paint.updateBox(MoGIndex, mogCount);
            Logger.log("Total Marks of grace gathered so far: " + mogTotal);
            Client.tap(nextObstacleRectangle);

            if (failArea != null && checkForFail) {
                Condition.wait(() -> Player.atTile(endTile) || Player.within(failArea), 100, 250);
            } else {
                Condition.wait(() -> Player.atTile(endTile), 100, 250);
            }

            Condition.sleep(generateRandomDelay(400, 600));
        }
    }

    public static void traverseWithInstantTap(Obstacle obstacle) {
        Logger.log("Traversing obstacle " + obstacle.name);
        Logger.debugLog("Traversing " + obstacle.name + " with instant tap.");
        Paint.setStatus("Traverse " + obstacle.name);
        Client.tap(obstacle.instantPressArea);

        if (obstacle.failArea != null && obstacle.checkForFail) {
            Condition.wait(() -> Player.atTile(obstacle.endTile) || Player.within(obstacle.failArea), 100, 250);
        } else {
            Condition.wait(() -> Player.atTile(obstacle.endTile), 100, 250);
        }

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

            if (obstacle.failArea != null && obstacle.checkForFail) {
                Condition.wait(() -> Player.atTile(obstacle.endTile) || Player.within(obstacle.failArea), 100, 250);
            } else {
                Condition.wait(() -> Player.atTile(obstacle.endTile), 100, 250);
            }

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