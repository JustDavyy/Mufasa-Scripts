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
    MarkHandling noMarks = new MarkHandling(new Rectangle(1, 1, 1, 1), new Color(203, 137, 25), new Rectangle(1, 1, 1 ,1));
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
            new AlKharid(),
            new Varrock(),
            new Canifis(),
            new Falador(),
            new Seers(),
            new Pollnivneach(),
            new Rellekka(),
            new Ardougne()
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

        currentLocation = Walker.getPlayerPosition();

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
                MapChunk chunks = new MapChunk(new String[]{"38-53", "39-53"}, "0", "1", "2");
                Walker.setup(chunks);
                break;
            case "Al Kharid":
                break;
            case "Varrock":
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
                // Mark of Graces

                // Obstacles
                obstacles.add(new Obstacle("Obstacle 1",
                        new Area(new Tile(9877, 13481, 0), new Tile(9960, 13512, 0)),
                        new Tile(9895, 13493, 0), new Tile(9895, 13465, 0),
                        new Rectangle(437, 294, 11, 20), new Rectangle(151, 313, 10, 22),
                        new Tile(9935, 13497, 0), noMarks, false));
                obstacles.add(new Obstacle("Obstacle 2",
                        new Area(new Tile(9870, 13439, 0), new Tile(9915, 13472, 0)),
                        new Tile(9895, 13453, 0), new Tile(9891, 13441, 1),
                        new Rectangle(404, 282, 50, 13), new Rectangle(401, 370, 51, 12),
                        new Tile(9895, 13465, 0), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 3",
                        new Area(new Tile(9874, 13426, 1), new Tile(9914, 13454, 1)),
                        new Tile(9895, 13437, 1), new Tile(9891, 13429, 2),
                        new Rectangle(419, 252, 4, 22), new Rectangle(442, 282, 3, 21),
                        new Tile(9891, 13441, 1), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 4",
                        new Area(new Tile(9864, 13450, 2), new Tile(9920, 13406, 2)),
                        new Tile(9907, 13429, 2), new Tile(9931, 13429, 2),
                        new Rectangle(465, 276, 25, 2), new Rectangle(573, 275, 24, 3),
                        new Tile(9891, 13429, 2), noMarks, false));

                obstacles.add(new Obstacle("Obstacle 5",
                        new Area(new Tile(9924, 13446, 2), new Tile(9968, 13402, 2)),
                        new Tile(9931, 13429, 2), new Tile(9947, 13429, 0),
                        new Rectangle(525, 283, 24, 19), new Rectangle(525, 283, 24, 19),
                        new Tile(9931, 13429, 2), noMarks, false));

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
                break;
            case "Varrock":
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
        String name;
        public Area area;
        Tile startTile;
        Tile endTile;
        Rectangle pressArea;
        public Rectangle instantPressArea;
        public Tile prevEndTile;
        public MarkHandling markHandling;
        public boolean checkForMark;

        Obstacle(String name, Area area, Tile startTile, Tile endTile, Rectangle pressArea,
                 Rectangle instantPressArea, Tile prevEndTile, MarkHandling markHandling, boolean checkForMark) {
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
        private final Rectangle tapArea;

        MarkHandling(Rectangle checkArea, Color targetColor, Rectangle tapArea) {
            this.checkArea = checkArea;
            this.targetColor = targetColor;
            this.tapArea = tapArea;
        }

        public boolean isMarkPresent(Rectangle mogRectangle, Color mogColor) {
            if (Client.isColorInRect(mogColor, mogRectangle, 10)) {
                Logger.debugLog("Found MoG on floor");
                return true;
            } else {
                return false;
            }
        }

        public void pickUpMark(Rectangle mogRectangle, Rectangle nextObstacleRectangle) {
            Paint.setStatus("Pick up MoG");
            Client.tap(mogRectangle);
            Player.waitTillNotMoving(10);
            mogTotal++;
            mogCount = mogTotal;
            Paint.updateBox(MoGIndex, mogCount);
            Logger.log("Total Marks of grace gathered so far: " + mogTotal);
            Client.tap(nextObstacleRectangle);
            Player.waitTillNotMoving(10);
        }
    }

    public static void traverseWithInstantTap(Obstacle obstacle) {
        Logger.debugLog("Traversing " + obstacle.name + " with instant tap.");
        Paint.setStatus("Traverse " + obstacle.name);
        Client.tap(obstacle.instantPressArea);
        Condition.wait(() -> Player.atTile(obstacle.endTile), 100, 110);
    }

    public static void traverseObstacle(Obstacle obstacle) {
        Paint.setStatus("Traverse " + obstacle.name);
        if (!Player.atTile(obstacle.startTile)) {
            Logger.debugLog("Moving to start of " + obstacle.name);
            Walker.step(obstacle.startTile);
            Condition.wait(() -> Player.atTile(obstacle.startTile), 100, 110);
        }
        if (Player.atTile(obstacle.startTile)) {
            Logger.debugLog("At start of " + obstacle.name);
            Client.tap(obstacle.pressArea);
            Condition.wait(() -> Player.atTile(obstacle.endTile), 100, 110);
        }
    }

    public static void proceedWithTraversal(Obstacle obstacle, Tile currentLocation) {
        if (Player.tileEquals(obstacle.prevEndTile, currentLocation)) {
            traverseWithInstantTap(obstacle);
        } else {
            traverseObstacle(obstacle);
        }
    }
}