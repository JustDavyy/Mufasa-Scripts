package main;

import Tasks.*;
import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import utils.Task;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dArceuus RCer",
        description = "Crafts blood or soul runes at Arceuus, supports using blood essence and hopping worlds. DISCLAIMER: This script is NOT fully safe for 10HP accounts, use at own risk!",
        version = "2.14",
        guideLink = "https://wiki.mufasaclient.com/docs/darceuus-rcer/",
        categories = {ScriptCategory.Runecrafting, ScriptCategory.Moneymaking}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Runes",
                        description = "Which rune would you like to craft?",
                        defaultValue = "Blood rune",
                        allowedValues = {
                                @AllowedValue(optionIcon = "565", optionName = "Blood rune"),
                                @AllowedValue(optionIcon = "566", optionName = "Soul rune")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings? WDH is disabled for this script, as there's users on every world.",
                        defaultValue = "1",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dArceuusRCer extends AbstractScript {
    public static Tile[] mineToShortcutOutPath = new Tile[] {
            new Tile(7040, 15156, 0),
            new Tile(7025, 15142, 0),
            new Tile(7019, 15165, 0),
            new Tile(7021, 15195, 0),
            new Tile(7033, 15214, 0),
            new Tile(7049, 15225, 0)
    };

    public static Tile[] shortcutOutToAltarPath = new Tile[] {
            new Tile(7030, 15249, 0),
            new Tile(7007, 15235, 0),
            new Tile(6978, 15229, 0),
            new Tile(6946, 15237, 0),
            new Tile(6924, 15254, 0),
            new Tile(6886, 15262, 0),
            new Tile(6877, 15274, 0)
    };

    public static Tile venerateAltarTile = new Tile(6871, 15273, 0);
    public static Tile obstacleOutsideTile = new Tile(7043, 15245, 0);
    public static Tile obstacleInsideTile = new Tile(7043, 15238, 0);
    public static Area BloodArea1 = new Area(
            new Tile(6608, 15322, 0),
            new Tile(6848, 15118, 0)
    );
    public static Area BloodArea2 = new Area(
            new Tile(6828, 15202, 0),
            new Tile(6972, 15086, 0)
    );
    public static Area BloodArea3 = new Area(
            new Tile(6892, 15094, 0),
            new Tile(6960, 15026, 0)
    );
    public static Area veneratePathArea = new Area(
            new Tile(6912, 15270, 0),
            new Tile(7000, 15222, 0)
    );
    public static Area mineFailSafeArea1 = new Area(
            new Tile(7056, 15230, 0),
            new Tile(7100, 15198, 0)
    );
    public static Area mineFailSafeArea2 = new Area(
            new Tile(7088, 15230, 0),
            new Tile(7156, 15086, 0)
    );
    public static Area mineFailSafeArea3 = new Area(
            new Tile(7108, 15266, 0),
            new Tile(7152, 15226, 0)
    );
    public static Area SoulArea1 = new Area(
            new Tile(6880, 15354, 0),
            new Tile(7216, 15274, 0)
    );
    public static Area SoulArea2 = new Area(
            new Tile(7192, 15354, 0),
            new Tile(7356, 15178, 0)
    );
    public static Area soulAltarArea = new Area(
            new Tile(7226, 15137, 0),
            new Tile(7286, 15191, 0)
    );
    public static Area bloodAltarArea = new Area(
            new Tile(6850, 15040, 0),
            new Tile(6884, 15075, 0)
    );
    public static Area beforeObstacleOutArea = new Area(
            new Tile(7020, 15226, 0),
            new Tile(7056, 15206, 0)
    );
    public static Area beforeObstacleInArea = new Area(
            new Tile(7016, 15262, 0),
            new Tile(7056, 15242, 0)
    );
    public static Area venerateAlterArea = new Area(
            new Tile(6851, 15250, 0),
            new Tile(6889, 15291, 0)
    );
    public static Tile bloodAltarTile = new Tile(6875, 15061, 0);
    public static Tile soulAltarTile = new Tile(7267, 15165, 0);
    public static Rectangle venerateAltar = new Rectangle(401, 238, 16, 17);
    public static Tile[] venerateToBloodAltarPath = new Tile[] {
            new Tile(6847, 15273, 0),
            new Tile(6822, 15269, 0),
            new Tile(6806, 15268, 0),
            new Tile(6785, 15268, 0),
            new Tile(6765, 15267, 0),
            new Tile(6743, 15268, 0),
            new Tile(6718, 15268, 0),
            new Tile(6701, 15266, 0),
            new Tile(6681, 15263, 0),
            new Tile(6664, 15253, 0),
            new Tile(6650, 15235, 0),
            new Tile(6649, 15217, 0),
            new Tile(6655, 15202, 0),
            new Tile(6670, 15194, 0),
            new Tile(6688, 15189, 0),
            new Tile(6707, 15189, 0),
            new Tile(6725, 15186, 0),
            new Tile(6747, 15181, 0),
            new Tile(6771, 15179, 0),
            new Tile(6787, 15180, 0),
            new Tile(6817, 15182, 0),
            new Tile(6832, 15179, 0),
            new Tile(6852, 15178, 0),
            new Tile(6872, 15175, 0),
            new Tile(6887, 15167, 0),
            new Tile(6900, 15155, 0),
            new Tile(6914, 15142, 0),
            new Tile(6926, 15132, 0),
            new Tile(6932, 15109, 0),
            new Tile(6934, 15086, 0),
            new Tile(6931, 15063, 0),
            new Tile(6913, 15056, 0),
            new Tile(6891, 15055, 0),
            new Tile(6874, 15061, 0)
    };
    public static Tile[] venerateToSoulAltarPath = new Tile[] {
            new Tile(6886, 15295, 0),
            new Tile(6919, 15304, 0),
            new Tile(6960, 15306, 0),
            new Tile(7004, 15320, 0),
            new Tile(7049, 15330, 0),
            new Tile(7087, 15330, 0),
            new Tile(7136, 15326, 0),
            new Tile(7180, 15324, 0),
            new Tile(7221, 15312, 0),
            new Tile(7256, 15302, 0),
            new Tile(7296, 15292, 0),
            new Tile(7317, 15258, 0),
            new Tile(7307, 15221, 0),
            new Tile(7289, 15202, 0),
            new Tile(7271, 15175, 0)
    };
    public static Tile[] venerateBackToObstaclePath = new Tile[] {
            new Tile(6885, 15262, 0),
            new Tile(6915, 15261, 0),
            new Tile(6937, 15250, 0),
            new Tile(6957, 15237, 0),
            new Tile(6986, 15231, 0),
            new Tile(7006, 15236, 0),
            new Tile(7022, 15247, 0),
            new Tile(7036, 15244, 0)
    };
    public static Tile[] bloodBackToObstaclePath = new Tile[] {
            new Tile(6879, 15059, 0),
            new Tile(6901, 15050, 0),
            new Tile(6915, 15057, 0),
            new Tile(6933, 15061, 0),
            new Tile(6935, 15082, 0),
            new Tile(6944, 15099, 0),
            new Tile(6952, 15116, 0),
            new Tile(6956, 15136, 0),
            new Tile(6965, 15147, 0)
    };
    public static Tile[] soulBackToObstaclePath = new Tile[] {
            new Tile(7272, 15181, 0),
            new Tile(7294, 15199, 0),
            new Tile(7309, 15225, 0),
            new Tile(7318, 15249, 0),
            new Tile(7314, 15271, 0),
            new Tile(7289, 15295, 0),
            new Tile(7258, 15300, 0),
            new Tile(7228, 15310, 0),
            new Tile(7188, 15319, 0),
            new Tile(7156, 15323, 0),
            new Tile(7117, 15322, 0),
            new Tile(7103, 15296, 0)
    };
    public static Tile[] soulObstacleBackViaVenerate = new Tile[] {
            new Tile(7270, 15174, 0),
            new Tile(7285, 15193, 0),
            new Tile(7304, 15219, 0),
            new Tile(7315, 15246, 0),
            new Tile(7315, 15273, 0),
            new Tile(7298, 15287, 0),
            new Tile(7268, 15299, 0),
            new Tile(7217, 15311, 0),
            new Tile(7158, 15322, 0),
            new Tile(7115, 15330, 0),
            new Tile(7079, 15330, 0),
            new Tile(7049, 15331, 0),
            new Tile(7010, 15327, 0),
            new Tile(6979, 15316, 0),
            new Tile(6950, 15305, 0),
            new Tile(6910, 15304, 0),
            new Tile(6884, 15283, 0),
            new Tile(6879, 15265, 0)
    };
    public static Tile southDenseRunestone = new Tile(7047, 15145, 0);
    public static Tile northDenseRunestone = new Tile(7047, 15169, 0);
    public static Rectangle tapNorthRuneStoneSOUTH = new Rectangle(462, 157, 28, 16);
    public static Rectangle tapSouthRuneStoneSOUTH = new Rectangle(466, 290, 51, 52);
    public static Rectangle tapNorthRuneStoneNORTH = new Rectangle(462, 194, 44, 39);
    public static Rectangle tapSouthRuneStoneNORTH = new Rectangle(464, 424, 63, 73);
    public static Rectangle northRuneStoneROI = new Rectangle(448, 176, 73, 73);
    public static Rectangle southRuneStoneROI = new Rectangle(458, 282, 70, 77);
    public static Rectangle bloodAltarStaticRect = new Rectangle(447, 220, 16, 16);
    public static Rectangle soulAltarStaticRect = new Rectangle(379, 275, 41, 27);
    public static Rectangle essenceCachedLoc;
    public static Integer essenceToProcess = 0;
    public static Integer essenceCount = 0;
    public static Tile obstacleBackToMineFromBloodInTile = new Tile(6967, 15165, 0);
    public static Tile obstacleNorthBackFromSoulAltarTile = new Tile(7103, 15281, 0);
    public static Area miningArea = new Area(
            new Tile(7032, 15108, 0),
            new Tile(7084, 15202, 0)
    );

    public static List<Color> obstacleColors = Arrays.asList(
            Color.decode("#20ff26"),
            Color.decode("#1cff22")
    );
    public static List<Color> venerateAltarColors = Arrays.asList(
            Color.decode("#8f51ba"),
            Color.decode("#50266c"),
            Color.decode("#552972")
    );
    public static List<Color> inactiveRunestone = Arrays.asList(
            Color.decode("#bda023"),
            Color.decode("#c6a82b")
    );
    public static List<Color> bloodAltar = Arrays.asList(
            Color.decode("#b14c42")
    );
    public static List<Color> soulAltar = Arrays.asList(
            Color.decode("#232121"),
            Color.decode("#e9f3e5"),
            Color.decode("#f2f9ee"),
            Color.decode("#d4e2cc"),
            Color.decode("#678367"),
            Color.decode("#c0d4b6")
    );
    public static String hopProfile;
    public static String runeType;
    public static String currentLoc;
    public static Boolean hopEnabled;
    public static Boolean usingEssence = false;
    public static Boolean initialCheckDone = false;
    public static Random random = new Random();
    public static Tile playerPos;
    public static long lastItemTime;
    public static int lastEmptySlots;
    public static int runeIndex;
    public static int profitIndex;
    public static int essenceIndex;
    public static int runePrice = 0;
    public static int essencePrice = 0;
    public static int startRunes = 0;
    public static int craftedRunes = 0;
    public static int bloodEssenceStartStack = 0;
    public static int bloodEssenceUsed = 0;
    public static long startTime;

    @Override
    public void onStart(){
        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"25-61", "28-59"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        runeType = (configs.get("Runes"));

        Logger.log("Thank you for using the dArceuusRCer script!");
        Logger.log("Setting up everything for your gains now...");
        Logger.log("We will be crafting " + runeType + "s this run.");

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Set the two top headers of paintUI.
        Paint.setStatus("Initializing...");

        // Set zoom to level 1
        Game.setZoom("1");

        // Open up the inventory
        GameTabs.openInventoryTab();
        Condition.sleep(800);

        // Check if we are using blood essence or not (only if running blood runes)
        if (java.util.Objects.equals(runeType, "Blood rune")) {
            usingEssence = Inventory.containsAny(new int[] {26390, 26392}, 0.95);
            Logger.debugLog("Using bloodEssence is: " + usingEssence);
            runeIndex = Paint.createBox(runeType, ItemList.BLOOD_RUNE_565, 0);
            Paint.setStatus("Fetch rune price");
            runePrice = GrandExchange.getItemPrice(ItemList.BLOOD_RUNE_565);
            Logger.debugLog("Blood rune price is currently: " + runePrice);
            Paint.setStatus("Fetch start rune stack");
            startRunes = Inventory.stackSize(ItemList.BLOOD_RUNE_565);
            Logger.debugLog("Stack of runes at start: " + startRunes);

            if (usingEssence) {
                essenceIndex = Paint.createBox("Blood essence(s)", 26392, 0);
                essencePrice = GrandExchange.getItemPrice(26390);
                Logger.debugLog("Blood essence price is currently: " + essencePrice);
            }
        } else {
            runeIndex = Paint.createBox(runeType, ItemList.SOUL_RUNE_566, 0);
            Paint.setStatus("Fetch rune price");
            runePrice = GrandExchange.getItemPrice(ItemList.SOUL_RUNE_566);
            Logger.debugLog("Soul rune price is currently: " + runePrice);
            Paint.setStatus("Fetch start rune stack");
            startRunes = Inventory.stackSize(ItemList.SOUL_RUNE_566);
            Logger.debugLog("Stack of runes at start: " + startRunes);
        }

        if (usingEssence) {
            bloodEssenceStartStack = Inventory.stackSize(26390);
            Logger.debugLog("We currently have " + bloodEssenceStartStack + " blood essence in our inventory");

            if (!Inventory.contains(26392, 0.95)) {
                if (Inventory.contains(26390, 0.7)) {
                    Paint.setStatus("Activate blood essence");
                    Logger.debugLog("No active blood essence found, activating one!");
                    Inventory.tapItem(26390, false, 0.7);
                    bloodEssenceUsed += 1;
                    Paint.updateBox(essenceIndex, bloodEssenceUsed);
                }
            } else {
                Logger.debugLog("We already have a blood essence active!");
            }
        }

        profitIndex = Paint.createBox("Profit", ItemList.STONKS_99960, 0);

        Paint.setStatus("Check for chisel");
        // Check if we have a chisel
        if (!Inventory.contains(1755, 0.80)) {
            Logger.debugLog("No chisel found in our inventory, stopping script...");
            Logout.logout();
            Script.stop();
        }

        Paint.setStatus("Initiate hop timers");
        // Initialize hop timer
        hopActions();

        Paint.setStatus("Close chatbox");
        // Close the chatbox
        Chatbox.closeChatbox();

        // Filling playerPos with current position
        Paint.setStatus("Fetch player position");
        playerPos = Walker.getPlayerPosition();

        startTime = System.currentTimeMillis();

        // Update the statistics label
        updateStatLabel();

        Paint.setStatus("End of onStart");
    }

    // Task list!
    List<Task> RCTasks = Arrays.asList(
            new mineEssence(),
            new venerateEssence(),
            new moveToVenerate(),
            new moveBackToMine(),
            new craftBloodRunes(),
            new moveToSoul(),
            new craftSoulRunes(),
            new moveToBlood()
    );

    @Override
    public void poll() {
        boolean anyTaskActivated = false; // Track if any task was activated

        // Check invent open
        GameTabs.openInventoryTab();

        // Check if dialogue is open
        if (Chatbox.findChatboxMenu() != null) {
            Client.sendKeystroke("space");
        }

        // Run tasks
        for (Task task : RCTasks) {
            if (task.activate()) {
                anyTaskActivated = true; // Set flag to true if a task activates
                task.execute();
                return; // Execute only one task per poll
            }
        }

        Logger.debugLog("Not a single task was activated for some reason... Failsafe kicking in and webwalking to the mine!");
        Walker.getPlayerPosition();
        Walker.webWalk(southDenseRunestone, true);
    }

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        int delay = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        return delay;
    }

    public static void readXP() {
        XpBar.getXP();
    }

    public static void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, false, false);
        } else {
            // We do nothing here, as hop is disabled.
        }
    }

    public static void updateStatLabel() {
        // Calculations for the statistics label
        long currentTime = System.currentTimeMillis();
        double elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);
        double runesPerHour = craftedRunes / elapsedTimeInHours;

        // Calculate the total profit subtracting the cost of the blood essences used
        double totalProfit = (craftedRunes * runePrice) - (bloodEssenceUsed * essencePrice);
        double profitPerHour = totalProfit / elapsedTimeInHours;

        // Format runes per hour with dot as thousand separator and no decimals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(','); // Set the decimal separator to comma
        DecimalFormat runesFormat = new DecimalFormat("#,###", symbols);
        String runesPerHourFormatted = runesFormat.format(runesPerHour);

        // Format profit per hour as 'k' with two decimals, dot as thousand separator, and comma as decimal separator
        DecimalFormat profitFormat = new DecimalFormat("#,##0.00k", symbols);
        String profitPerHourFormatted = profitFormat.format(profitPerHour / 1000);

        // Update the statistics label
        String statistics = String.format("Runes/hr: %s | Profit/hr: %s", runesPerHourFormatted, profitPerHourFormatted);
        Paint.setStatistic(statistics);
    }
}