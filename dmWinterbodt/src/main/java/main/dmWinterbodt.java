package main;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.SideManager;
import utils.Task;
import utils.WTStates;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;
import static utils.SideManager.pickRandomSide;

@ScriptManifest(
        name = "dmWinterbodt",
        description = "Completes the Wintertodt minigame.",
        version = "2.0",
        guideLink = "https://wiki.mufasaclient.com/docs/dmwinterbodt/",
        categories = {ScriptCategory.Firemaking, ScriptCategory.Minigames}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name = "BankTab",
                        description = "What bank tab are your food located in?",
                        defaultValue = "0",
                        optionType = OptionType.BANKTABS
                ),
                @ScriptConfiguration(
                        name = "Potion amount",
                        description = "Select the amount of potions you'd like to bring",
                        defaultValue = "8",
                        minMaxIntValues = {1, 28},
                        optionType = OptionType.INTEGER_SLIDER
                ),
                @ScriptConfiguration(
                        name = "Potions amount left to brew at",
                        description = "Select the amount of potion sips required to have for each game",
                        defaultValue = "4",
                        minMaxIntValues = {1, 28},
                        optionType = OptionType.INTEGER_SLIDER
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
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    public static int foodAmount;
    public static int foodAmountLeftToBank;
    public static int bankTab;
    public static boolean burnOnly;
    public static Rectangle bankSearchArea = new Rectangle(410, 144, 429, 358);
    public static List<Color> bankChest = List.of(
            Color.decode("#2c3737"),
            Color.decode("#2a3535")
    );

    public static boolean preGameFoodCheck = true;
    public static boolean gameAt13Percent;
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
    public static boolean alreadyBanked;
    public static int totalGameCount = 0;
    public static int totalCrateCount = 0;
    public static int totalRepairCount = 0;
    public static int totalRelightCount = 0;
    public static Area WTArea = new Area(
            new Tile(6387, 15409, 0),
            new Tile(6637, 15853, 0)
    );
    public static Area lobby = new Area(
            new Tile(6501, 15655, 0),
            new Tile(6544, 15690, 0)
    );
    public static Area LeftTopWTArea = new Area(
            new Tile(6440, 15753, 0),
            new Tile(6473, 15816, 0)
    );
    public static Area RightTopWTArea = new Area(
            new Tile(6570, 15746, 0),
            new Tile(6605, 15808, 0)
    );
    public static Tile[] LeftTopToStart = new Tile[] {
            new Tile(6469, 15813, 0),
            new Tile(6463, 15789, 0),
            new Tile(6462, 15764, 0),
            new Tile(6464, 15741, 0),
            new Tile(6471, 15728, 0),
            new Tile(6484, 15719, 0),
            new Tile(6501, 15706, 0),
            new Tile(6520, 15694, 0)
    };
    public static Tile[] RightTopToStart =  new Tile[] {
            new Tile(6564, 15827, 0),
            new Tile(6578, 15806, 0),
            new Tile(6588, 15789, 0),
            new Tile(6585, 15756, 0),
            new Tile(6581, 15739, 0),
            new Tile(6567, 15723, 0),
            new Tile(6549, 15707, 0),
            new Tile(6533, 15691, 0)
    };
    public static Tile bankTile = new Tile(6559, 15521, 0);
    public static Area bankTentArea = new Area(
            new Tile(6550, 15508, 0),
            new Tile(6570, 15531, 0)
    );
    public static Tile[] outsideToBankPath =  new Tile[] {
            new Tile(6527, 15549, 0),
            new Tile(6558, 15519, 0)
    };
    public static Rectangle enterDoorRect = new Rectangle(323, 75, 307, 122);
    public static Rectangle exitDoorRect = new Rectangle(379, 260, 176, 116);
    public static Area insideArea = new Area(
            new Tile(6401, 15633, 0),
            new Tile(6645, 15873, 0)
    );
    public static Area insideDoorArea = new Area(
            new Tile(6492, 15610, 0),
            new Tile(6557, 15631, 0)
    );
    public static Area outsideArea = new Area(
            new Tile(6468, 15473, 0),
            new Tile(6609, 15598, 0)
    );
    public static Area atDoor = new Area(
            new Tile(6484, 15572, 0),
            new Tile(6566, 15631, 0)
    ); //this one is both at door from inside & outside
    public static Area atDoorInside = new Area(
            new Tile(6492, 15609, 0),
            new Tile(6552, 15626, 0)
    );
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
            new WTStates("Left", new Rectangle(60, 112, 31, 31), false, false, false, false),
            new WTStates("Right", new Rectangle(120, 113, 27, 31), false, false, false, false)
    };
    // These tasks are executed in this order
    List<Task> WTTasks = Arrays.asList(
            new CheckGear(),
            new BreakManager(), // I think it should be here?
            new GoToSafety(),
            //new Bank(), // We dont need to bank anymore at all?
            new Eat(),
            new FailSafe(), // I think it should be here?
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
        foodAmount = Integer.parseInt(configs.get("Food amount"));
        foodAmountLeftToBank = Integer.parseInt(configs.get("Food amount left to bank at"));
        pickedSide = configs.get("Side");
        bankTab = Integer.parseInt(configs.get("BankTab"));
        burnOnly = Boolean.parseBoolean(configs.get("Burn only?"));

        Walker.setup(new MapChunk(new String[]{"24-63", "26-61"}, "0"));

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
        GameTabs.openInventoryTab();

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

        // Disable break and AFK handlers, as we use custom breaks
        Client.disableBreakHandler();
        Client.disableAFKHandler();
    }

    @Override
    public void poll() {
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 100, 10);
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