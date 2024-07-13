package main;

import Tasks.*;
import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.OptionType;
import helpers.utils.Tile;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dArceuus RCer new",
        description = "Crafts blood or soul runes at Arceuus, supports using blood essence and hopping worlds. DISCLAIMER: This script is NOT fully safe for 10HP accounts, use at own risk!",
        version = "1.00",
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
            new Tile(205, 145),
            new Tile(200, 137),
            new Tile(200, 129),
            new Tile(202, 125),
            new Tile(207, 121)
    };

    public static Tile[] shortcutOutToAltarPath = new Tile[] {
            new Tile(199, 112),
            new Tile(192, 113),
            new Tile(186, 114),
            new Tile(177, 114),
            new Tile(170, 109),
            new Tile(163, 107),
            new Tile(157, 103),
            new Tile(154, 107)
    };

    public static Tile venerateAltarTile = new Tile(152, 104);
    public static Tile obstacleOutsideTile = new Tile(210, 112);
    public static Tile obstacleInsideTile = new Tile(210, 114);
    public static Area BloodArea1 = new Area(
            new Tile(57, 77),
            new Tile(146, 149)
    );
    public static Area BloodArea2 = new Area(
            new Tile(146, 128),
            new Tile(184, 157)
    );
    public static Area BloodArea3 = new Area(
            new Tile(153, 154),
            new Tile(186, 184)
    );
    public static Area veneratePathArea = new Area(
            new Tile(152, 97),
            new Tile(211, 114)
    );
    public static Area mineFailSafeArea1 = new Area(
            new Tile(216, 112),
            new Tile(245, 160)
    );
    public static Area mineFailSafeArea2 = new Area(
            new Tile(197, 121),
            new Tile(209, 155)
    );
    public static Area mineFailSafeArea3 = new Area(
            new Tile(199, 116),
            new Tile(221, 123)
    );
    public static Tile[] mineFailSafe1Path = new Tile[] {
            new Tile(223, 107),
            new Tile(232, 111),
            new Tile(239, 117),
            new Tile(243, 127),
            new Tile(243, 138),
            new Tile(241, 145),
            new Tile(239, 153),
            new Tile(234, 154),
            new Tile(229, 148),
            new Tile(231, 143),
            new Tile(232, 135),
            new Tile(232, 127),
            new Tile(226, 122),
            new Tile(221, 119),
            new Tile(213, 118),
            new Tile(204, 122),
            new Tile(201, 130),
            new Tile(199, 135),
            new Tile(201, 142),
            new Tile(205, 147),
            new Tile(211, 140)
    };
    public static Tile[] mineFailSafe2Path = new Tile[] {
            new Tile(202, 123),
            new Tile(200, 131),
            new Tile(199, 137),
            new Tile(201, 142),
            new Tile(204, 145),
            new Tile(210, 141)
    };
    public static Tile[] mineFailSafe3Path = new Tile[] {
            new Tile(218, 118),
            new Tile(212, 118),
            new Tile(206, 120),
            new Tile(202, 124),
            new Tile(200, 130),
            new Tile(199, 136),
            new Tile(201, 141),
            new Tile(204, 144),
            new Tile(210, 141)
    };
    public static Area SoulArea1 = new Area(
            new Tile(245, 67),
            new Tile(353, 200)
    );
    public static Area SoulArea2 = new Area(
            new Tile(149, 63),
            new Tile(303, 95)
    );
    public static Area soulAltarArea = new Area(
            new Tile(261, 110),
            new Tile(323, 168)
    );
    public static Area bloodAltarArea = new Area(
            new Tile(137, 158),
            new Tile(166, 183)
    );
    public static Area beforeObstacleOutArea = new Area(
            new Tile(201, 115),
            new Tile(208, 122)
    );
    public static Area beforeObstacleInArea = new Area(
            new Tile(197, 101),
            new Tile(221, 114)
    );
    public static Area successObstacleOUTArea = new Area(
            new Tile(196, 102),
            new Tile(221, 113)
    );
    public static Area successObstacleBloodToMineArea = new Area(
            new Tile(192, 132),
            new Tile(206, 150)
    );
    public static Area successObstacleSoulToMineArea = new Area(
            new Tile(215, 103),
            new Tile(239, 117)
    );
    public static Area venerateAlterArea = new Area(
            new Tile(145, 96),
            new Tile(155, 106)
    );
    public static Tile bloodAltarTile = new Tile(152,174);
    public static Tile soulAltarTile = new Tile(289, 132);
    public static Rectangle venerateAltar = new Rectangle(401, 238, 16, 17);
    public static Tile[] venerateToBloodAltarPath = new Tile[] {
            new Tile(147, 103),
            new Tile(140, 103),
            new Tile(133, 103),
            new Tile(127, 103),
            new Tile(119, 103),
            new Tile(112, 105),
            new Tile(105, 104),
            new Tile(99, 105),
            new Tile(90, 104),
            new Tile(83, 104),
            new Tile(77, 105),
            new Tile(74, 112),
            new Tile(75, 119),
            new Tile(76, 124),
            new Tile(80, 127),
            new Tile(85, 128),
            new Tile(93, 130),
            new Tile(101, 131),
            new Tile(109, 132),
            new Tile(115, 133),
            new Tile(125, 133),
            new Tile(133, 133),
            new Tile(141, 133),
            new Tile(148, 134),
            new Tile(154, 134),
            new Tile(162, 136),
            new Tile(167, 141),
            new Tile(169, 147),
            new Tile(172, 153),
            new Tile(174, 161),
            new Tile(176, 166),
            new Tile(176, 170),
            new Tile(172, 173),
            new Tile(167, 175),
            new Tile(163, 176),
            new Tile(157, 176),
            new Tile(154, 174)
    };

    public static Tile[] venerateToSoulAltarPath = new Tile[] {
            new Tile(156, 95),
            new Tile(161, 92),
            new Tile(167, 92),
            new Tile(172, 92),
            new Tile(179, 92),
            new Tile(185, 90),
            new Tile(192, 88),
            new Tile(199, 86),
            new Tile(207, 84),
            new Tile(213, 83),
            new Tile(222, 83),
            new Tile(229, 85),
            new Tile(234, 85),
            new Tile(242, 86),
            new Tile(249, 87),
            new Tile(255, 87),
            new Tile(263, 88),
            new Tile(270, 88),
            new Tile(279, 90),
            new Tile(284, 93),
            new Tile(293, 96),
            new Tile(298, 99),
            new Tile(301, 105),
            new Tile(301, 113),
            new Tile(299, 119),
            new Tile(295, 122),
            new Tile(292, 125),
            new Tile(290, 129)
    };
    public static Tile[] venerateBackToObstaclePath = new Tile[] {
            new Tile(160, 105),
            new Tile(170, 107),
            new Tile(179, 112),
            new Tile(187, 115),
            new Tile(198, 114),
            new Tile(203, 111)
    };
    public static Tile[] bloodBackToObstaclePath = new Tile[] {
            new Tile(158, 175),
            new Tile(166, 174),
            new Tile(174, 173),
            new Tile(176, 163),
            new Tile(177, 153),
            new Tile(178, 147),
            new Tile(182, 143)
    };
    public static Tile[] soulBackToObstaclePath = new Tile[] {
            new Tile(294, 123),
            new Tile(298, 116),
            new Tile(300, 108),
            new Tile(300, 104),
            new Tile(296, 97),
            new Tile(287, 95),
            new Tile(280, 91),
            new Tile(272, 88),
            new Tile(264, 88),
            new Tile(255, 87),
            new Tile(244, 85),
            new Tile(235, 85),
            new Tile(231, 99)
    };
    public static Tile[] soulObstacleBackViaVenerate = new Tile[] {
            new Tile(231, 92),
            new Tile(228, 87),
            new Tile(222, 85),
            new Tile(214, 83),
            new Tile(204, 82),
            new Tile(197, 84),
            new Tile(190, 88),
            new Tile(182, 89),
            new Tile(173, 92),
            new Tile(165, 92),
            new Tile(158, 92),
            new Tile(154, 95),
            new Tile(155, 100),
            new Tile(156, 105),
            new Tile(162, 106),
            new Tile(167, 107),
            new Tile(173, 110),
            new Tile(180, 112),
            new Tile(186, 114),
            new Tile(191, 115),
            new Tile(197, 113),
            new Tile(203, 111)
    };
    public static Tile southDenseRunestone = new Tile(211, 148);
    public static Tile northDenseRunestone = new Tile(211, 140);
    public static Rectangle tapNorthRuneStoneSOUTH = new Rectangle(462, 157, 28, 16);
    public static Rectangle tapSouthRuneStoneSOUTH = new Rectangle(466, 290, 51, 52);
    public static Rectangle tapNorthRuneStoneNORTH = new Rectangle(462, 194, 44, 39);
    public static Rectangle tapSouthRuneStoneNORTH = new Rectangle(464, 424, 63, 73);
    public static Rectangle northRuneStoneROI = new Rectangle(448, 176, 73, 73);
    public static Rectangle southRuneStoneROI = new Rectangle(458, 282, 70, 77);
    public static Rectangle bloodAltarStaticRect = new Rectangle(447, 220, 16, 16);
    public static Rectangle soulAltarStaticRect = new Rectangle(316, 368, 17, 18);
    public static Rectangle essenceCachedLoc;
    public static Integer essenceToProcess = 0;
    public static Integer essenceCount = 0;
    public static Tile obstacleBackToMineFromBloodInTile = new Tile(185, 141);
    public static Tile obstacleNorthBackFromSoulAltarTile = new Tile(231, 100);
    public static Area miningArea = new Area(
            new Tile(208, 134),
            new Tile(216, 147)
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
        Walker.setup("/maps/ZeahRC.png"); // Setup the walker path!

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

        profitIndex = Paint.createBox("Profit", ItemList.COINS_9_1004, 0);

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
        //Run tasks
        for (Task task : RCTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
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
        DecimalFormat runesFormat = new DecimalFormat("#,###", symbols);
        String runesPerHourFormatted = runesFormat.format(runesPerHour);

        // Format profit per hour as 'k' with two decimals
        DecimalFormat profitFormat = new DecimalFormat("#.##0,00k");
        String profitPerHourFormatted = profitFormat.format(profitPerHour / 1000);

        // Update the statistics label
        String statistics = String.format("Runes/hr: %s | Profit/hr: %s", runesPerHourFormatted, profitPerHourFormatted);
        Paint.setStatistic(statistics);
    }
}