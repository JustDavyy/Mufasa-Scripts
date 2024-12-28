package agi_sdk;

import agi_sdk.Tasks.*;
import agi_sdk.helpers.*;
import agi_sdk.utils.Task;
import helpers.utils.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.*;

import static helpers.Interfaces.*;

public class main {
    public static final List<Obstacle> obstacles = new ArrayList<>();
    public static final Random random = new Random();
    public static List<StartTileStorage> startTiles;
    public static Tile currentLocation;
    public static Course courseChosen; // Save agility course name
    public static String foodID; // Save food
    public static int eatHP; // HP level to eat at
    public static int lapCount = 0;
    public static int mogCount = 0;
    public static int initialTermiteCount = 0;
    public static int initialBoneShardCount = 0;
    public static int termiteCount = 0;
    public static int boneShardCount = 0;
    public static int MoGIndex;
    public static int termiteIndex;
    public static int shardIndex;
    public static int mogTotal;
    public static long startTime = System.currentTimeMillis();
    public static boolean useSeersTeleport = false;
    public static boolean useProgressive = false;
    public static boolean needToMove = false;
    public static int currentHP;
    public static int agilityLevel;
    public static Course initialCourse;
    public static List<MarkHandling> noMarks = List.of(
            new MarkHandling(new Rectangle(1, 1, 1, 1), new Color(203, 137, 25), new Rectangle(1, 1, 1, 1), new Tile(1, 1, 0), null, false)
    );
    private static String foodChosen;
    private final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    private final DecimalFormat MoGsFormat = new DecimalFormat("#,##0.0", symbols);
    private final DecimalFormat LapsFormat = new DecimalFormat("#,##0.0", symbols);
    private final DecimalFormat ShardsFormat = new DecimalFormat("#,##0", symbols);
    private final DecimalFormat TermitesFormat = new DecimalFormat("#,##0", symbols);
    private final List<Task> agilityTasks = Arrays.asList(
            new Run(),
            new Eat(),
            new moveProgressive(),
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

    private static void setupObstacles() {
        // Map SEERS_TELEPORT to SEERS since they share the same course setup
        Course resolvedCourse = courseChosen == Course.SEERS_TELEPORT ? Course.SEERS : courseChosen;

        // Fetch the obstacles for the resolved course
        List<Obstacle> selectedObstacles = CourseSetup.getObstaclesForCourse(resolvedCourse);
        if (selectedObstacles.isEmpty()) {
            Logger.log("This is an unknown course, no obstacles to set up.");
            Script.stop();
            return;
        }

        obstacles.addAll(selectedObstacles);
    }

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

    public static void changeProgressiveCourse(Course newCourse) {
        courseChosen = newCourse;
        Logger.debugLog("Clear old obstacles");
        obstacles.clear();
        Logger.debugLog("Set up new obstacles");
        setupObstacles();
    }

    // This would have to be run prior to calling it!
    public void setSettings(Course course, String food, int eatHitpoints) {
        courseChosen = course;
        foodChosen = food;
        eatHP = eatHitpoints;

        // Can also call preStart() directly here. I prefer not, since you can setSettings in onStart in main script before even needing it.
        // That way it's ready to go and only needs preStart.
    }

    // If you still want an "onStart", you can manually call this once after the setSettings.
    public void preStart() {
        Logger.log("Initialising agility module...");

        if (courseChosen.equals(Course.PROGRESSIVE_TO_50)) {
            Logger.debugLog("Using 1-50 progressive mode");
            Logger.log("1-50 Progressive will train 1-30 at Draynor, followed by 30-50 at Varrock.");
            Logger.log("Only start this mode when at Draynor.");
            Logger.debugLog("Set course to Draynor.");
            useProgressive = true;
            courseChosen = Course.DRAYNOR;
            initialCourse = Course.DRAYNOR;
        } else if (courseChosen.equals(Course.COLOSSAL_WYRM_PROGRESSIVE)) {
            Logger.debugLog("Using Colossal Wyrm course progressive mode");
            Logger.debugLog("Set course to Basic Colossal Wyrm.");
            useProgressive = true;
            courseChosen = Course.BASIC_COLOSSAL_WYRM;
            initialCourse = Course.BASIC_COLOSSAL_WYRM;
        }

        // Setup walker using the correct chunks
        setupWalker();

        // Setup obstacles for the chosen course
        setupObstacles();

        // Save food
        initializeItemIDs(foodChosen);

        Paint.setStatus("Set zoom level");
        courseZoom();

        // Set teleport boolean if needed
        if (courseChosen.equals(Course.SEERS_TELEPORT)) {
            Paint.setStatus("Set teleport to TRUE");
            useSeersTeleport = true;
        }

        // Open inventory again if doing the wyrm course
        if (courseChosen.equals(Course.BASIC_COLOSSAL_WYRM) || courseChosen.equals(Course.ADVANCED_COLOSSAL_WYRM)) {
            GameTabs.openTab(UITabs.INVENTORY);
            Condition.sleep(1500);

            // Read initial stack values
            initialTermiteCount = Inventory.stackSize(30038);
            initialBoneShardCount = Inventory.stackSize(ItemList.BLESSED_BONE_SHARDS_29381);

            Logger.debugLog("Initial Termites count: " + initialTermiteCount);
            Logger.debugLog("Initial Blessed Bone shard count: " + initialBoneShardCount);
        }
    }

    public void runCourse(boolean updatePaint) {
        // Looped tasks go here.
        if (updatePaint) {
            updateStatLabel();
        }
        GameTabs.closeTab(UITabs.INVENTORY);

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
        itemIDs.put("None", new String[]{"None"});
        itemIDs.put("Cake", new String[]{"Cake"});
        itemIDs.put("Trout", new String[]{"333"});
        itemIDs.put("Salmon", new String[]{"329"});
        itemIDs.put("Tuna", new String[]{"361"});
        itemIDs.put("Jug of wine", new String[]{"1993"});
        itemIDs.put("Lobster", new String[]{"379"});
        itemIDs.put("Swordfish", new String[]{"373"});
        itemIDs.put("Potato with cheese", new String[]{"6705"});
        itemIDs.put("Monkfish", new String[]{"7946"});
        itemIDs.put("Karambwan", new String[]{"3144"});
        itemIDs.put("Shark", new String[]{"385"});
        itemIDs.put("Manta ray", new String[]{"391"});
        itemIDs.put("Anglerfish", new String[]{"13441"});
        String[] itemIds = itemIDs.get(logName);
        foodID = itemIds[0];

        Logger.debugLog("Ending the initializeItemIDs() method.");
    }

    private void setupWalker() {
        switch (courseChosen) {
            case GNOME:
                MapChunk gnomeChunks = new MapChunk(new String[]{"38-53", "39-53"}, "0", "1", "2");
                Walker.setup(gnomeChunks);
                break;
            case AL_KHARID:
                MapChunk alkharidChunks = new MapChunk(new String[]{"51-49"}, "0", "1", "2", "3");
                Walker.setup(alkharidChunks);
                break;
            case VARROCK:
                MapChunk varrockChunks = new MapChunk(new String[]{"50-53"}, "0", "1", "3");
                Walker.setup(varrockChunks);
                break;
            case CANIFIS:
                MapChunk canifisChunks = new MapChunk(new String[]{"54-54"}, "0", "2", "3");
                Walker.setup(canifisChunks);
                break;
            case FALADOR:
                MapChunk faladorChunks = new MapChunk(new String[]{"47-52"}, "0", "3");
                Walker.setup(faladorChunks);
                break;
            case RELLEKKA:
                MapChunk rellekkaChunks = new MapChunk(new String[]{"41-57"}, "0", "3");
                Walker.setup(rellekkaChunks);
                break;
            case ARDOUGNE:
                MapChunk ardyChunks = new MapChunk(new String[]{"41-51"}, "0", "3");
                Walker.setup(ardyChunks);
                break;
            case DRAYNOR:
                MapChunk draynorChunks = new MapChunk(new String[]{"48-51"}, "0", "3");
                Walker.setup(draynorChunks);
                break;
            case POLLNIVNEACH:
                MapChunk pollyChunks = new MapChunk(new String[]{"52-46"}, "0", "1", "2");
                Walker.setup(pollyChunks);
                break;
            case SEERS:
            case SEERS_TELEPORT:
                MapChunk seersChunks = new MapChunk(new String[]{"42-54"}, "0", "2", "3");
                Walker.setup(seersChunks);
                break;
            case ADVANCED_COLOSSAL_WYRM:
            case BASIC_COLOSSAL_WYRM:
                MapChunk colossalWyrmChunks = new MapChunk(new String[]{"25-45"}, "0", "1", "2");
                Walker.setup(colossalWyrmChunks);
                break;
            default:
                Logger.log("This is a unknown course, no chunks to set up.");
                Script.stop();
        }
    }

    public void courseZoom() {
        // Map of course names to their zoom levels
        Map<Course, String> courseZoomLevels = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(Course.GNOME, "2"),
                new AbstractMap.SimpleEntry<>(Course.AL_KHARID, "1"),
                new AbstractMap.SimpleEntry<>(Course.VARROCK, "1"),
                new AbstractMap.SimpleEntry<>(Course.CANIFIS, "1"),
                new AbstractMap.SimpleEntry<>(Course.FALADOR, "1"),
                new AbstractMap.SimpleEntry<>(Course.RELLEKKA, "1"),
                new AbstractMap.SimpleEntry<>(Course.ARDOUGNE, "1"),
                new AbstractMap.SimpleEntry<>(Course.BASIC_COLOSSAL_WYRM, "1"),
                new AbstractMap.SimpleEntry<>(Course.ADVANCED_COLOSSAL_WYRM, "1")
        );

        String zoomLevel = courseZoomLevels.get(courseChosen);

        if (zoomLevel != null) {
            Logger.debugLog(courseChosen + " course - setting zoom level " + zoomLevel + ".");
            Game.setZoom(zoomLevel);
        } else {
            Logger.debugLog(courseChosen + " course - setting zoom level 3");
            Game.setZoom("3");
        }

        if (GameTabs.isTabOpen(UITabs.SETTINGS)) {
            GameTabs.closeTab(UITabs.SETTINGS);
        }

        Logger.debugLog("Zoom set.");
    }

    private void readXP() {
        XpBar.getXP();
    }

    private void updateStatLabel() {
        if (courseChosen.equals(Course.BASIC_COLOSSAL_WYRM) || courseChosen.equals(Course.ADVANCED_COLOSSAL_WYRM)) {
            // Set separators
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');

            // Calculations for MoGs and laps per hour
            long currentTime = System.currentTimeMillis();
            double elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);

            // Calculate MoGs per hour and laps per hour
            double TermitesPerHour = termiteCount / elapsedTimeInHours;
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
            double MoGsPerHour = mogTotal / elapsedTimeInHours;
            double LapsPerHour = lapCount / elapsedTimeInHours;

            // Format MoGs per hour and laps per hour with one decimal place
            String MoGsPerHourFormatted = MoGsFormat.format(MoGsPerHour);
            String LapsPerHourFormatted = LapsFormat.format(LapsPerHour);

            // Update the statistics label
            String statistics = String.format("MoGs/hr: %s | Laps/hr: %s", MoGsPerHourFormatted, LapsPerHourFormatted);
            Paint.setStatistic(statistics);
        }
    }

}