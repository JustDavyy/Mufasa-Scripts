package main;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.Helpers;
import utils.SideManager;
import utils.Task;
import utils.WTStates;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;
import static utils.Helpers.countFoodInInventory;
import static utils.SideManager.pickRandomSide;

@ScriptManifest(
        name = "dmWinterbodt",
        description = "Completes the Wintertodt minigame. Start inside the Wintertodt minigame area",
        version = "2.27",
        guideLink = "https://wiki.mufasaclient.com/docs/dmwinterbodt/",
        categories = {ScriptCategory.Firemaking, ScriptCategory.Minigames}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name = "Potion amount",
                        description = "Select the amount of potions or food you'd like to bring",
                        defaultValue = "5",
                        minMaxIntValues = {1, 10},
                        optionType = OptionType.INTEGER_SLIDER
                ),
                @ScriptConfiguration(
                        name = "Potion sips left before obtaining more",
                        description = "Select the amount of potion sips or food bites required to have for each game",
                        defaultValue = "8",
                        minMaxIntValues = {4, 36},
                        optionType = OptionType.INTEGER_SLIDER
                ),
                @ScriptConfiguration(
                        name = "Druidic ritual completed?",
                        description = "Does this account have the Druidic Ritual quest completed?",
                        defaultValue = "True",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name = "Side",
                        description = "Which side would you like to start on? Random will also randomize it each time it goes back from bank.",
                        defaultValue = "Random",
                        allowedValues = {
                                @AllowedValue(optionName = "Right"),
                                @AllowedValue(optionName = "Left"),
                                @AllowedValue(optionName = "Random")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Burn only?",
                        description = "Would you like to only burn and not fletch? This gives increased FM xp, but no fletching xp and less points per game.",
                        defaultValue = "False",
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class dmWinterbodt extends AbstractScript {

    // EVERYTHING FROM CONSTANTS FILE BELOW HERE
    public static final int brumaRoot = ItemList.BRUMA_ROOT_20695;
    public static final int brumaKindling = ItemList.BRUMA_KINDLING_20696;
    public static final int knife = 946;
    // EVERYTHING FROM CONSTANTS FILE ABOVE
    private static final Random random = new Random();
    // Variables
    public static int crateIndex;
    public static int brazierIndex;
    public static String hopProfile;
    public static boolean hopEnabled;
    public static boolean useWDH;
    public static boolean druidicRitualCompleted;
    public static int foodAmount;
    public static int foodAmountLeftToBank;
    public static boolean burnOnly;
    public static boolean preGameFoodCheck = true;
    public static boolean gameAt15Percent;
    public static boolean gameAt20Percent;
    public static boolean gameAt70Percent;
    public static boolean shouldBurn;
    public static boolean inventoryHasKindlings;
    public static boolean inventoryHasLogs;
    public static boolean waitingForGameToStart;
    public static boolean waitingForGameEnded;
    public static boolean isGameGoing;
    public static boolean isBurning = false;
    public static boolean shouldStartWithBurn;
    public static int foodAmountInInventory;
    public static boolean shouldEat;
    public static boolean warmthCriticalLow;
    public static long lastWalkToSafety = System.currentTimeMillis();
    public static long lastActivity = System.currentTimeMillis();
    public static boolean isMoreThan40Seconds;
    public static boolean weDied;
    public static int totalGameCount = 0;
    public static int totalCrateCount = 0;
    public static int totalRepairCount = 0;
    public static int totalRelightCount = 0;
    public static Area lobby = new Area(
            new Tile(6492, 15609, 0),
            new Tile(6551, 15695, 0)
    );
    public static Rectangle enterDoorRect = new Rectangle(323, 75, 307, 122);
    public static Area insideArea = new Area(
            new Tile(6401, 15633, 0),
            new Tile(6645, 15873, 0)
    );
    public static Area insideDoorArea = new Area(
            new Tile(6492, 15610, 0),
            new Tile(6557, 15631, 0)
    );
    public static Area outsideArea = new Area(
            new Tile(6457, 15436, 0),
            new Tile(6617, 15608, 0)
    );
    public static Area atDoor = new Area(
            new Tile(6484, 15572, 0),
            new Tile(6566, 15631, 0)
    ); //this one is both at door from inside & outside
    public static Area leftWTArea = new Area(
            new Tile(6440, 15695, 0),
            new Tile(6498, 15751, 0)
    );
    public static Area rightWTArea = new Area(
            new Tile(6545, 15695, 0),
            new Tile(6583, 15748, 0)
    );
    // Paths
    public static Tile[] wtDoorToBank = new Tile[] {
            new Tile(6523, 15563, 0),
            new Tile(6556, 15521, 0)
    };
    public static Tile[] gameToWTDoor = new Tile[] {
            new Tile(6522, 15701, 0),
            new Tile(6523, 15666, 0),
            new Tile(6525, 15620, 0)
    };
    public static Tile[] wtDoorToRightSide = new Tile[] {
            new Tile(6525, 15648, 0),
            new Tile(6523, 15691, 0),
            new Tile(6562, 15714, 0)
    };
    public static Tile[] wtDoorToLeftSide =  new Tile[] {
            new Tile(6519, 15657, 0),
            new Tile(6518, 15711, 0),
            new Tile(6477, 15718, 0)
    };
    public static Tile[] fromEitherSideToGameLobby = new Tile[] {
            new Tile(6521, 15709, 0),
            new Tile(6522, 15671, 0)
    };
    // Location static
    public static Tile currentLocation;
    // Side static
    public static String currentSide;
    public static String pickedSide;
    // State creation (we might not need all 4, but just the bottom ones?)
    public static WTStates[] states = {
            new WTStates("Left", new Rectangle(88, 112, 16, 13), false, false, false, false),
            new WTStates("Right", new Rectangle(143, 107, 17, 14), false, false, false, false)
    };
    // These tasks are executed in this order
    List<Task> WTTasks = Arrays.asList(
            new CheckGear(),
            new BreakManager(), // I think it should be here?
            new GoToSafety(),
            new Eat(),
            new FailSafe(), // I think it should be here?
            new CreatePotions(),
            new SwitchSide(),
            new BurnBranches(),
            new FletchBranches(),
            new PreGame(),
            new GetBranches(),
            new GoBackToGame()
    );

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

    // Just leaving these down here so we can figure out where they belong
    public static Tile[] getReversedTiles(Tile[] array) {
        if (array == null) return null;
        Tile[] reversed = new Tile[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    @Override
    public void onStart() {
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        foodAmount = Integer.parseInt(configs.get("Potion amount"));
        foodAmountLeftToBank = Integer.parseInt(configs.get("Potion sips left before obtaining more"));
        pickedSide = configs.get("Side");
        burnOnly = Boolean.parseBoolean(configs.get("Burn only?"));
        druidicRitualCompleted = Boolean.parseBoolean(configs.get("Druidic ritual completed?"));

        // 24-63, 24-62, 24-61, 25-63, 25-62, 25-61, 26-63, 26-62, 26-61, 24-60, 25-60, 26-60, 50-50
        Walker.setup(new MapChunk(new String[]{"24-63", "24-62", "24-61", "25-63", "25-62", "25-61", "26-63", "26-62", "26-61", "24-60", "50-50"}, "0"));

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/dm.png");

        // Create two image boxes
        crateIndex = Paint.createBox("Supply crate (s)", ItemList.SUPPLY_CRATE_20703, totalCrateCount);
        brazierIndex = Paint.createBox("Games completed", ItemList.WINTERTODT_BRAZIER_99999, totalGameCount);

        // Set the two top headers of paintUI.
        Paint.setStatus("Initializing...");
        Paint.setStatistic("Brazier Repairs: " + totalRepairCount + " | Relights: " + totalRelightCount);

        // Make sure the inventory is open
        Paint.setStatus("Opening inventory");
        GameTabs.openTab(UITabs.INVENTORY);

        if (pickedSide.equals("Random")) {
            Paint.setStatus("Picking random side");
            currentSide = pickRandomSide();
            Logger.debugLog("Picked the " + currentSide + " side.");
        } else {
            currentSide = pickedSide;
        }

        // Make sure our Chatbox is closed
        Paint.setStatus("Closing chatbox");
        Chatbox.closeChatbox();

        if (!GameTabs.isTabOpen(UITabs.INVENTORY)) {
            GameTabs.openTab(UITabs.INVENTORY);
            Condition.wait(() -> GameTabs.isTabOpen(UITabs.INVENTORY), 100, 20);
        }
        countFoodInInventory(); // Initial food count!

        // Disable break and AFK handlers, as we use custom breaks
        Client.disableBreakHandler();
        Client.disableAFKHandler();
    }

    @Override
    public void poll() {
        if (!GameTabs.isTabOpen(UITabs.INVENTORY)) {
            GameTabs.openTab(UITabs.INVENTORY);
            Condition.wait(() -> GameTabs.isTabOpen(UITabs.INVENTORY), 100, 10);
        }

        SideManager.updateStates();

        //Run tasks
        for (Task task : WTTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
}