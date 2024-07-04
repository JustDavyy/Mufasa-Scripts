import helpers.AbstractScript;
import helpers.Interfaces;
import helpers.ScriptCategory;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.Area;
import helpers.utils.OptionType;
import helpers.utils.Tile;

import java.awt.*;
import java.util.*;
import java.util.List;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dArceuus RCer",
        description = "Crafts blood or soul runes at Arceuus, supports using blood essence and hopping worlds. DISCLAIMER: This script is NOT fully safe for 10HP accounts, use at own risk!",
        version = "1.01",
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

Tile[] mineToShortcutOutPath = new Tile[] {
        new Tile(205, 145),
        new Tile(200, 137),
        new Tile(200, 129),
        new Tile(202, 125),
        new Tile(207, 121)
    };

Tile[] shortcutOutToAltarPath = new Tile[] {
        new Tile(199, 112),
        new Tile(192, 113),
        new Tile(186, 114),
        new Tile(177, 114),
        new Tile(170, 109),
        new Tile(163, 107),
        new Tile(157, 103),
        new Tile(154, 102)
    };

Tile venerateAltarTile = new Tile(152, 104);
Tile obstacleOutsideTile = new Tile(210, 112);
Tile obstacleInsideTile = new Tile(210, 114);

// ON START AREAS BETWEEN THESE LINES

// BLOOD Stuff first
Area BloodArea1 = new Area(
        new Tile(45, 69),
        new Tile(144, 189)
);
Area BloodArea2 = new Area(
        new Tile(48, 118),
        new Tile(186, 194)
);

// SOUL stuff second
Area SoulArea1 = new Area(
        new Tile(245, 67),
        new Tile(353, 200)
);
Area SoulArea2 = new Area(
        new Tile(149, 63),
        new Tile(303, 95)
);

// Mining area
Area miningAreaonStart = new Area(
        new Tile(198, 118),
        new Tile(237, 164)
);

// Venerate area
Area VenerateAltarArea = new Area(
        new Tile(141, 92),
        new Tile(157, 110)
);

// Obstacle area
Area obstaclesArea1 = new Area(
        new Tile(158, 100),
        new Tile(194, 121)
);
Area obstaclesArea2 = new Area(
        new Tile(194, 105),
        new Tile(216, 115)
);
Area obstaclesArea3 = new Area(
        new Tile(218, 100),
        new Tile(244, 116)
);

// ON START AREAS BETWEEN THESE LINES

Area soulAltarArea = new Area(
        new Tile(261, 110),
        new Tile(323, 168)
);
Area bloodAltarArea = new Area(
        new Tile(137, 158),
        new Tile(166, 183)
);
Area beforeObstacleOutArea = new Area(
        new Tile(201, 115),
        new Tile(208, 122)
);
Area beforeObstacleInArea = new Area(
        new Tile(197, 101),
        new Tile(221, 114)
);
Area beforeObstacleInBLOODArea = new Area(
        new Tile(177, 132),
        new Tile(192, 149)
);

Area successObstacleINArea = new Area(
        new Tile(205, 113),
        new Tile(213, 119)
);
Area successObstacleOUTArea = new Area(
        new Tile(196, 102),
        new Tile(221, 113)
);
Area successObstacleBloodToMineArea = new Area(
        new Tile(192, 132),
        new Tile(206, 150)
);
Area successObstacleSoulToMineArea = new Area(
        new Tile(215, 103),
        new Tile(239, 117)
);
Area venerateAlterArea = new Area(
        new Tile(145, 96),
        new Tile(155, 106)
);
Tile bloodAltarTile = new Tile(152,174);
Tile soulAltarTile = new Tile(289, 132);
Rectangle venerateAltar = new Rectangle(401, 238, 16, 17);
Tile[] venerateToBloodAltarPath = new Tile[] {
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

Tile[] venerateToSoulAltarPath = new Tile[] {
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

Tile[] venerateBackToObstaclePath = new Tile[] {
        new Tile(160, 105),
        new Tile(170, 107),
        new Tile(179, 112),
        new Tile(187, 115),
        new Tile(198, 114),
        new Tile(203, 111)
    };

Tile[] bloodBackToObstaclePath = new Tile[] {
        new Tile(158, 175),
        new Tile(166, 174),
        new Tile(174, 173),
        new Tile(176, 163),
        new Tile(177, 153),
        new Tile(178, 147),
        new Tile(182, 143)
    };
Tile[] soulBackToObstaclePath = new Tile[] {
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
Tile[] soulObstacleBackViaVenerate = new Tile[] {
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
Tile southDenseRunestone = new Tile(211, 148);
Tile northDenseRunestone = new Tile(211, 140);
Rectangle tapNorthRuneStoneSOUTH = new Rectangle(462, 157, 28, 16);
Rectangle tapSouthRuneStoneSOUTH = new Rectangle(466, 290, 51, 52);
Rectangle tapNorthRuneStoneNORTH = new Rectangle(462, 194, 44, 39);
Rectangle tapSouthRuneStoneNORTH = new Rectangle(464, 424, 63, 73);
Rectangle northRuneStoneROI = new Rectangle(448, 176, 73, 73);
Rectangle southRuneStoneROI = new Rectangle(458, 282, 70, 77);
Rectangle bloodAltarStaticRect = new Rectangle(447, 220, 16, 16);
Rectangle soulAltarStaticRect = new Rectangle(316, 368, 17, 18);
Rectangle essenceCachedLoc;
Integer essenceToProcess = 0;
Integer essenceCount = 0;
Tile obstacleBackToMineFromBloodInTile = new Tile(185, 141);
Tile obstacleNorthBackFromSoulAltarTile = new Tile(231, 100);
Area soulAltarBackObstacleArea = new Area(
        new Tile(225, 95),
        new Tile(235, 103)
);
Area soulAltarBackObstacleSUCCESSArea = new Area(
        new Tile(222, 104),
        new Tile(236, 112)
);
Area miningArea = new Area(
        new Tile(208, 134),
        new Tile(216, 147)
);

List<Color> obstacleColors = Arrays.asList(
        Color.decode("#20ff26")
);
List<Color> venerateAltarColors = Arrays.asList(
        Color.decode("#8f51ba"),
        Color.decode("#50266c"),
        Color.decode("#552972")
);
List<Color> inactiveRunestone = Arrays.asList(
        Color.decode("#bda023"),
        Color.decode("#c6a82b")
);
List<Color> bloodAltar = Arrays.asList(
        Color.decode("#b14c42")
);
List<Color> soulAltar = Arrays.asList(
        Color.decode("#232121"),
        Color.decode("#e9f3e5"),
        Color.decode("#f2f9ee"),
        Color.decode("#d4e2cc"),
        Color.decode("#678367"),
        Color.decode("#c0d4b6")
);
String hopProfile;
String runeType;
String currentLoc;
Boolean hopEnabled;
Boolean usingEssence = false;
Boolean initialCheckDone = false;
Random random = new Random();
Tile playerPos;

    // This is the onStart, and only gets ran once.
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

        // Set zoom to level 1
        Game.setZoom("1");

        // Open up the inventory
        GameTabs.openInventoryTab();

        // Check if we are using blood essence or not (only if running blood runes)
        if (java.util.Objects.equals(runeType, "Blood rune")) {
            Logger.debugLog("We are using blood essences, set usingEssence to TRUE.");
            usingEssence = Inventory.containsAny(new int[] {26390, 26392}, 0.95);
        }

        if (usingEssence) {
            int stackCount = Inventory.stackSize(26390);
            Logger.debugLog("We currently have " + stackCount + " blood essence in our inventory");

            if (!Inventory.contains(26392, 0.95)) {
                Logger.debugLog("No active blood essence found, activating one!");
                Inventory.tapItem(26390, false, 0.7);
            } else {
                Logger.debugLog("We already have a blood essence active!");
            }
        }

        // Check if we have a chisel
        if (!Inventory.contains(1755, 0.80)) {
            Logger.debugLog("No chisel found in our inventory, stopping script...");
            Logout.logout();
            Script.stop();
        }

        // Close the chatbox
        Chatbox.closeChatbox();


        // CHECK EVERYTHING NEEDED HERE BEFORE PROCEEDING TO THE POLL WHICH IS A FULL LAP ALL THE TIME.
        playerPos = Walker.getPlayerPosition();

        if (Player.isTileWithinArea(playerPos, BloodArea1) || Player.isTileWithinArea(playerPos, BloodArea2)) {
            Logger.debugLog("We are located within one of the blood areas.");

            if (Inventory.contains(7938, 0.9)) {
                Logger.debugLog("Inventory contains fragments, proceeding to blood altar to craft them");
                // MOVE TO BLOOD ALTAR
                moveToBloodAltar();

                // Check if we need to hop
                hopActions();

                // Craft blood runes
                processCraftingBloods();

                // Check if we need to hop
                hopActions();

                // Move back to the obstacle and towards the mine
                moveBackToMineFromBlood();
            } else {
                Logger.debugLog("Inventory does not contain fragments, we still need two trips.");

                // MOVE TO BLOOD ALTAR
                moveToBloodAltar();

                // Check if we need to hop
                hopActions();

                // Move back to the obstacle and towards the mine
                moveBackToMineFromBlood();
            }
        }

        if (Player.isTileWithinArea(playerPos, SoulArea1) || Player.isTileWithinArea(playerPos, SoulArea2)) {
            Logger.debugLog("We are located within one of the soul areas.");

            if (Inventory.contains(7938, 0.9)) {
                Logger.debugLog("Inventory contains fragments, proceeding to soul altar to craft them");
                // MOVE TO SOULS ALTAR
                moveToSoulAltar();

                // Check if we need to hop
                hopActions();

                // Craft soul runes
                processCraftingSouls();

                // Check if we need to hop
                hopActions();

                // Move back to the obstacle and towards the mine
                moveBackToMineFromSoul();
            } else {
                Logger.debugLog("Inventory does not contain fragments, we still need two trips.");

                // MOVE TO SOULS ALTAR
                moveToSoulAltar();

                // Check if we need to hop
                hopActions();

                // Move back to the obstacle and towards the mine
                moveBackToMineFromSoul();
            }
        }

        if (Player.isTileWithinArea(playerPos, miningAreaonStart)) {
            Logger.debugLog("We are located within the mining area.");

            if (Inventory.contains(7938, 0.9)) {
                Logger.debugLog("Inventory contains fragments, we only need 1 more trip");

                // MINING STUFF/LOGIC HERE
                mineEssence();

                // Check if we need to hop
                hopActions();

                // FIRST MOVE TO VENERATE ALTAR and interact with it
                moveToVenerateAltar();
                interactWithVenerateAltar();

                // GO TO ALTAR HERE BASED ON WHAT WE CHOSE IN THE CONFIG
                if (java.util.Objects.equals(runeType, "Blood rune")) {
                    // MOVE TO BLOOD ALTAR
                    moveToBloodAltar();

                    // Check if we need to hop
                    hopActions();

                    // Craft blood runes
                    processCraftingBloods();

                    // Check if we need to hop
                    hopActions();

                    // Move back to the obstacle and towards the mine
                    moveBackToMineFromBlood();
                } else {
                    // MOVE TO SOULS ALTAR
                    moveToSoulAltar();

                    // Check if we need to hop
                    hopActions();

                    // Craft soul runes
                    processCraftingSouls();

                    // Check if we need to hop
                    hopActions();

                    // Move back to the obstacle and towards the mine
                    moveBackToMineFromSoul();
                }
            } else {
                Logger.debugLog("Inventory does not contain fragments, we can just run the normal poll as we still need two trips.");
            }
        }

        if (Player.isTileWithinArea(playerPos, obstaclesArea1) || Player.isTileWithinArea(playerPos, obstaclesArea2) || Player.isTileWithinArea(playerPos, obstaclesArea3)) {
            Logger.debugLog("We are located within the obstacles area (between venerate altar and soul return shortcut.");

            if (doneVenerating()) {
                walkVenerateToShortcut();
                traverseShortcutIn();

                // Instantly tap the north RuneStone
                Client.tap(new Rectangle(497, 515, 33, 18));
                currentLoc = "north";
            } else {
                walkShortcutOutToAltar();
                interactWithVenerateAltar();

                walkVenerateToShortcut();
                traverseShortcutIn();

                // Instantly tap the north RuneStone
                Client.tap(new Rectangle(497, 515, 33, 18));
                currentLoc = "north";
            }

            if (Inventory.contains(7938, 0.9)) {
                Logger.debugLog("Inventory contains fragments, we only need 1 more trip");
                // MINING STUFF/LOGIC HERE
                mineEssence();

                // Check if we need to hop
                hopActions();

                // FIRST MOVE TO VENERATE ALTAR and interact with it
                moveToVenerateAltar();
                interactWithVenerateAltar();

                // GO TO ALTAR HERE BASED ON WHAT WE CHOSE IN THE CONFIG
                if (java.util.Objects.equals(runeType, "Blood rune")) {
                    // MOVE TO BLOOD ALTAR
                    moveToBloodAltar();

                    // Check if we need to hop
                    hopActions();

                    // Craft blood runes
                    processCraftingBloods();

                    // Check if we need to hop
                    hopActions();

                    // Move back to the obstacle and towards the mine
                    moveBackToMineFromBlood();
                } else {
                    // MOVE TO SOULS ALTAR
                    moveToSoulAltar();

                    // Check if we need to hop
                    hopActions();

                    // Craft soul runes
                    processCraftingSouls();

                    // Check if we need to hop
                    hopActions();

                    // Move back to the obstacle and towards the mine
                    moveBackToMineFromSoul();
                }
            } else {
                Logger.debugLog("Inventory does not contain fragments, we can just run the normal poll as we still need two trips.");
            }
        }
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        // MINING STUFF/LOGIC HERE
        mineEssence();

        // Check if we need to hop
        hopActions();

        // FIRST MOVE TO VENERATE ALTAR and interact with it
        moveToVenerateAltar();
        interactWithVenerateAltar();

        // MOVE BACK TO THE MINE FROM THE VENERATE ALTAR
        moveBackToMineFromVenerate();

        // MINE A SECOND INVENTORY
        mineEssence();

        // Check if we need to hop
        hopActions();

        // MOVE BACK TO VENERATE ALTAR
        moveToVenerateAltar();
        interactWithVenerateAltar();

        if (java.util.Objects.equals(runeType, "Blood rune")) {
            // MOVE TO BLOOD ALTAR
            moveToBloodAltar();

            // Check if we need to hop
            hopActions();

            // Craft blood runes
            processCraftingBloods();

            // Check if we need to hop
            hopActions();

            // Move back to the obstacle and towards the mine
            moveBackToMineFromBlood();
        } else {
            // MOVE TO SOULS ALTAR
            moveToSoulAltar();

            // Check if we need to hop
            hopActions();

            // Craft soul runes
            processCraftingSouls();

            // Check if we need to hop
            hopActions();

            // Move back to the obstacle and towards the mine
            moveBackToMineFromSoul();
        }

    }

    // ACTIONS AND SO ON
    private void interactWithVenerateAltar(){
        List<Point> foundPoints = Client.getPointsFromColorsInRect(venerateAltarColors, new Rectangle(312, 164, 205, 163), 5);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the venerate altar using the color finder, tapping.");
            Client.tap(foundPoints, true);
            Condition.wait(this::doneVenerating, 100, 50);
        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(venerateAltarTile);
            waitTillStopped(6);

            if (!Player.atTile(venerateAltarTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(venerateAltarTile);
                waitTillStopped(6);

                if (!Player.atTile(venerateAltarTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(venerateAltarTile);
                    waitTillStopped(6);
                }
            }

            if (Player.atTile(venerateAltarTile)) {
                Client.tap(venerateAltar);
                Condition.wait(this::doneVenerating, 100, 50);
            }
        }

        if (!doneVenerating()) {
            Logger.debugLog("Seems like venerating has failed, retrying!");
            interactWithVenerateAltar();
        } else {
            Logger.debugLog("Essences have been successfully venerated.");
        }
    }

    private void processCraftingBloods() {
        // Process the fragments first
        List<Point> foundPoints = Client.getPointsFromColorsInRect(bloodAltar, new Rectangle(329, 163, 277, 201), 5);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the blood altar using the color finder, tapping.");
            Client.tap(foundPoints, true);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 20);
        } else {
            Logger.debugLog("Couldn't locate the blood altar using the color finder, moving to altar tile and proceeding...");
            Walker.step(bloodAltarTile);
            Condition.wait(() -> Player.atTile(bloodAltarTile), 250, 20);
            Client.tap(bloodAltarStaticRect);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 20);
        }

        // Process dark essence blocks
        int count = Inventory.count(13446, 0.95);
        for (int i = 0; i < count; i++) {
            Inventory.tapItem(1755, true, 0.80);
            generateRandomDelay(75, 150);
            Client.tap(essenceCachedLoc);
            generateRandomDelay(50, 100);
        }

        // Process the leftovers
        while (Inventory.contains(13446, 0.95)){
            Inventory.tapItem(1755, true, 0.80);
            generateRandomDelay(75, 150);
            Client.tap(essenceCachedLoc);
            generateRandomDelay(50, 100);
        }

        // Check if we need to re-activate blood essence
        if (usingEssence) {
            if (!Inventory.contains(26392, 0.95)) {
                Logger.debugLog("No active blood essence found, activating one!");
                Inventory.tapItem(26390, false, 0.7);
            }
        }

        // Craft runes a second time
        List<Point> foundPoints2 = Client.getPointsFromColorsInRect(bloodAltar, new Rectangle(329, 163, 277, 201), 5);

        if (!foundPoints2.isEmpty()) {
            Logger.debugLog("Located the blood altar using the color finder, tapping.");
            Client.tap(foundPoints2, true);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 20);
        } else {
            Logger.debugLog("Couldn't locate the blood altar using the color finder, moving to altar tile and proceeding...");
            Walker.step(bloodAltarTile);
            Condition.wait(() -> Player.atTile(bloodAltarTile), 250, 20);
            Client.tap(bloodAltarStaticRect);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 20);
        }

        Condition.sleep(generateRandomDelay(750, 1250));

        // Check if we need to re-activate blood essence
        if (usingEssence) {
            if (!Inventory.contains(26392, 0.95)) {
                Logger.debugLog("No active blood essence found, activating one!");
                Inventory.tapItem(26390, false, 0.7);
            }
        }

        // Set process count to 0 to reset it
        essenceToProcess = 0;

        // Read XP now that we're done crafting again
        readXP();
    }

    private void processCraftingSouls() {
        // Process the fragments first
        List<Rectangle> foundRectangles = Client.getObjectsFromColorsInRect(soulAltar, new Rectangle(188, 199, 350, 298), 3);

        if (!foundRectangles.isEmpty()) {
            Rectangle randomRect = foundRectangles.get(random.nextInt(foundRectangles.size()));
            Logger.debugLog("Located the soul altar using the color finder, tapping.");
            Client.tap(randomRect);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
        } else {
            Logger.debugLog("Couldn't locate the soul altar using the color finder, moving to altar tile and proceeding...");
            Walker.step(soulAltarTile);
            waitTillStopped(7);
            Client.tap(soulAltarStaticRect);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
        }

        // Process dark essence blocks
        int count = Inventory.count(13446, 0.95);
        for (int i = 0; i < count; i++) {
            Inventory.tapItem(1755, true, 0.80);
            generateRandomDelay(75, 150);
            Client.tap(essenceCachedLoc);
            generateRandomDelay(50, 100);
        }

        // Process the leftovers
        while (Inventory.contains(13446, 0.95)){
            Inventory.tapItem(1755, true, 0.80);
            generateRandomDelay(75, 150);
            Client.tap(essenceCachedLoc);
            generateRandomDelay(50, 100);
        }

        // Craft runes a second time
        List<Rectangle> foundRectangles2 = Client.getObjectsFromColorsInRect(soulAltar, new Rectangle(350, 244, 137, 113), 3);

        if (!foundRectangles2.isEmpty()) {
            Rectangle randomRect2 = foundRectangles2.get(random.nextInt(foundRectangles2.size()));
            Logger.debugLog("Located the soul altar using the color finder, tapping.");
            Client.tap(randomRect2);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
        } else {
            Logger.debugLog("Couldn't locate the soul altar using the color finder, moving to altar tile and proceeding...");
            Walker.step(soulAltarTile);
            Condition.wait(() -> Player.atTile(soulAltarTile), 250, 20);
            Client.tap(soulAltarStaticRect);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
        }

        // Start a move away from the portal
        Client.tap(new Rectangle(821, 28, 16, 13));
        Condition.sleep(generateRandomDelay(750,1250));

        // Set process count to 0 to reset it
        essenceToProcess = 0;

        // Read XP now that we're done crafting again
        readXP();
    }

    // ALL WALKING / PATHING (1 CALL PER PATH) CAN BE FOUND BELOW
    private void moveToVenerateAltar() {
        Logger.debugLog("Pathing to the Venerate altar.");
        walkMineToShortcutOut();
        traverseShortcutOut();
        walkShortcutOutToAltar();
    }

    private void moveToBloodAltar() {
        Logger.debugLog("Pathing to the Blood Altar.");
        walkAltartoBloodAltar();
    }

    private void moveToSoulAltar() {
        Logger.debugLog("Pathing to the Soul Altar.");
        walkAltartoSoulAltar();
    }

    private void moveBackToMineFromVenerate() {

        essenceCount = Inventory.count(13446, 0.95);

        if (essenceCachedLoc == null) {
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + essenceCachedLoc);
        }

        Logger.debugLog("Pathing back to the mine from the Venerate altar.");
        walkVenerateToShortcut();
        traverseShortcutIn();

        // Instantly tap the north RuneStone
        Client.tap(new Rectangle(497, 515, 33, 18));

        // Check if we have essence blocks left to process
        while (Inventory.contains(13446, 0.95)){
            processEssence();
        }

        waitTillStopped(8);
        currentLoc = "north";
        Client.tap(tapNorthRuneStoneNORTH);
    }

    private void moveBackToMineFromBlood() {
        Logger.debugLog("Pathing back to the mine from the Blood altar.");
        walkBloodToShortcut();
        traverseBloodShortcutIn();

        // Tap the north runestone to start mining again
        Client.tap(new Rectangle(636, 187, 24, 19));
        waitTillStopped(8);

        currentLoc = "north";
    }

    private void moveBackToMineFromSoul() {
        Logger.debugLog("Pathing back to the mine from the Soul altar.");
        walkSoulToShortcut();
        // Northern shortcut
        traverseSoulShortcutIn();
        // Sleep to be sure, we finished traveling
        Condition.sleep(generateRandomDelay(2000,2500));
        // Southern shortcut
        traverseSoulShortcutIn2();
    }

    // ALL WALKING / PATHING SMALLER METHODS CAN BE FOUND BELOW

    private void walkVenerateToShortcut() {
        Logger.debugLog("Walking towards to agility shortcut, while cutting our essence blocks");
        Walker.walkPath(venerateBackToObstaclePath, this::processEssence);
        waitTillStopped(6);
    }
    private void walkMineToShortcutOut() {
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
            Logger.debugLog("Enabled run!");
        }
        Logger.debugLog("Walking towards the agility shortcut.");
        Walker.walkPath(mineToShortcutOutPath);
        waitTillStopped(6);
    }

    private void traverseShortcutOut() {
        Logger.debugLog("Crossing the rocks obstacle.");

        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(369, 140, 261, 218), 5);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(foundPoints, true);
            Condition.wait(() -> Player.within(successObstacleOUTArea), 250, 25);
            waitTillStopped(7);
        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(obstacleInsideTile);
            waitTillStopped(7);

            if (!Player.atTile(obstacleInsideTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(obstacleInsideTile);
                waitTillStopped(7);

                if (!Player.atTile(obstacleInsideTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(obstacleInsideTile);
                    waitTillStopped(7);
                }
            }

            if (Player.atTile(obstacleInsideTile)) {
                List<Point> foundPoints2 = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(337, 122, 275, 241), 5);
                Condition.wait(() -> Player.within(successObstacleOUTArea), 250, 25);
                waitTillStopped(7);
                if (!foundPoints2.isEmpty()) {
                    Client.tap(foundPoints2, true);
                    Condition.wait(() -> Player.within(successObstacleOUTArea), 250, 25);
                    waitTillStopped(7);
                }
            }
        }
    }

    private void traverseShortcutIn() {
        Logger.debugLog("Crossing the rocks obstacle.");

        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(384, 213, 211, 169), 3);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(foundPoints, true);
            waitTillStopped(9);
        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(obstacleOutsideTile);
            waitTillStopped(9);

            if (!Player.atTile(obstacleOutsideTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(obstacleOutsideTile);
                waitTillStopped(9);

                if (!Player.atTile(obstacleOutsideTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(obstacleOutsideTile);
                    waitTillStopped(9);
                }
            }

            if (Player.atTile(obstacleOutsideTile)) {
                List<Point> foundPoints2 = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(337, 122, 275, 241), 3);

                if (!foundPoints2.isEmpty()) {
                    Logger.debugLog("Located the obstacle using the color finder, tapping.");
                    Client.tap(foundPoints2, true);
                    waitTillStopped(9);
                }
            }
        }
    }

    private void traverseBloodShortcutIn() {

        Logger.debugLog("Crossing the rocks obstacle.");

        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(365, 171, 179, 220), 3);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(foundPoints, true);
            Condition.wait(() -> Player.within(successObstacleBloodToMineArea), 250, 48);

            if (!Player.within(successObstacleBloodToMineArea)) {
                Logger.debugLog("Looks like we failed the obstacle, what a shame... Last attempt now....");
                Walker.step(obstacleBackToMineFromBloodInTile);
                waitTillStopped(9);

                List<Point> foundPoints2 = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(365, 171, 179, 220), 3);

                if (!foundPoints2.isEmpty()) {
                    Client.tap(foundPoints2, true);
                    Condition.wait(() -> Player.within(successObstacleBloodToMineArea), 250, 48);
                }
            }
        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(obstacleBackToMineFromBloodInTile);
            waitTillStopped(9);

            if (!Player.atTile(obstacleBackToMineFromBloodInTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(obstacleBackToMineFromBloodInTile);
                waitTillStopped(9);

                if (!Player.atTile(obstacleBackToMineFromBloodInTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(obstacleBackToMineFromBloodInTile);
                    waitTillStopped(9);
                }
            }

            if (Player.atTile(obstacleBackToMineFromBloodInTile)) {
                List<Point> foundPoints2 = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(365, 171, 179, 220), 3);

                if (!foundPoints2.isEmpty()) {
                    Client.tap(foundPoints2, true);
                    Condition.wait(() -> Player.within(successObstacleBloodToMineArea), 250, 48);
                }
            }
        }
    }

    private void traverseSoulShortcutIn() {

        Logger.debugLog("Crossing the northern rocks obstacle.");

        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(340, 196, 248, 208), 3);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(foundPoints, true);
            waitTillStopped(11);

        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(obstacleNorthBackFromSoulAltarTile);
            waitTillStopped(11);

            if (!Player.atTile(obstacleNorthBackFromSoulAltarTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(obstacleNorthBackFromSoulAltarTile);
                waitTillStopped(11);

                if (!Player.atTile(obstacleNorthBackFromSoulAltarTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Client.tap(new Rectangle(442, 284, 13, 10));
                    waitTillStopped(6);

                    if (Player.within(successObstacleSoulToMineArea)) {
                        Logger.debugLog("Successfully traversed the obstacle this time!");
                        return;
                    }
                }
            }

            if (Player.atTile(obstacleNorthBackFromSoulAltarTile)) {
                Client.tap(new Rectangle(442, 284, 13, 10));
                waitTillStopped(11);

                if (!Player.within(successObstacleSoulToMineArea)) {
                    Logger.debugLog("Looks like we failed the obstacle, what a shame... Retrying!");
                    Walker.step(obstacleNorthBackFromSoulAltarTile);
                    waitTillStopped(11);

                    Client.tap(new Rectangle(442, 284, 13, 10));
                    waitTillStopped(11);
                    if (!Player.within(successObstacleSoulToMineArea)) {
                        Logger.debugLog("Looks like we failed the obstacle again, what a shame... Retrying!");
                        Walker.step(obstacleNorthBackFromSoulAltarTile);
                        waitTillStopped(11);

                        Client.tap(new Rectangle(442, 284, 13, 10));
                        waitTillStopped(11);

                        if (!Player.within(successObstacleSoulToMineArea)) {
                            Logger.debugLog("Seems like we failed to use the soul obstacle multiple times, we'll walk back via the venerate altar instead.");
                            Walker.walkPath(soulObstacleBackViaVenerate);
                            waitTillStopped(7);
                            Walker.step(obstacleOutsideTile);
                            waitTillStopped(7);
                        }
                    }
                }
            } else {
                Logger.debugLog("Seems like we failed to use the soul obstacle, we'll walk back via the venerate altar instead.");
                Walker.walkPath(soulObstacleBackViaVenerate);
                waitTillStopped(7);
                Walker.step(obstacleOutsideTile);
                waitTillStopped(7);
            }
        }
    }

    private void traverseSoulShortcutIn2() {
        Logger.debugLog("Crossing the rocks obstacle.");

        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(59, 290, 232, 191), 5);
        List<Point> foundPoints2 = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(384, 213, 211, 169), 3);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(foundPoints, true);
            waitTillStopped(8);
        } else if (!foundPoints2.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(foundPoints2, true);
            waitTillStopped(8);
        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(obstacleOutsideTile);
            waitTillStopped(7);

            if (!Player.atTile(obstacleOutsideTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(obstacleOutsideTile);
                waitTillStopped(7);

                if (!Player.atTile(obstacleOutsideTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(obstacleOutsideTile);
                    waitTillStopped(7);
                }
            }

            if (Player.atTile(obstacleOutsideTile)) {
                List<Point> foundPoints3 = Client.getPointsFromColorsInRect(obstacleColors, new Rectangle(337, 122, 275, 241), 3);

                if (!foundPoints3.isEmpty()) {
                    Logger.debugLog("Located the obstacle using the color finder, tapping.");
                    Client.tap(foundPoints3, true);
                    waitTillStopped(8);
                }
            }
        }

        // Enable run if needed again
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
            Logger.debugLog("Enabled run!");
        }

        // Instantly tap the north RuneStone
        Client.tap(new Rectangle(497, 515, 33, 18));
        currentLoc = "north";
        waitTillStopped(9);
    }

    private void walkShortcutOutToAltar() {
        Logger.debugLog("Moving from shortcut to Venerate Altar.");
        Walker.walkPath(shortcutOutToAltarPath);
        waitTillStopped(6);
        Logger.debugLog("Player is now located at the Venerate Altar.");
    }

    private void walkAltartoSoulAltar() {
        Logger.debugLog("Now walking from Venerate altar to Soul Altar.");
        Walker.walkPath(venerateToSoulAltarPath);
        waitTillStopped(9);

        if (essenceCachedLoc == null) {
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + essenceCachedLoc);
        }

        // Verify if we are in the area multiple times, if not, try walking there again and in the end reset if failed.
        if (!Player.within(soulAltarArea)) {
            Logger.debugLog("Seems like we failed walking to the soul altar, retrying...");
            Walker.walkPath(venerateToSoulAltarPath);
            waitTillStopped(9);

            if (!Player.within(soulAltarArea)) {
                Logger.debugLog("Seems like we failed walking to the soul altar again, retrying...");
                Walker.walkPath(venerateToSoulAltarPath);
                waitTillStopped(9);

                if (!Player.within(soulAltarArea)) {
                    Logger.debugLog("Seems like we failed walking to the soul altar, retrying but first moving away from our current spot randomly.");
                    Client.tap(new Rectangle(755, 64, 99, 43));
                    Condition.sleep(1500);
                    Walker.walkPath(venerateToSoulAltarPath);
                    waitTillStopped(9);

                    if (!Player.within(soulAltarArea)) {
                        Logger.debugLog("Seems like we failed walking to the soul altar, retrying but first moving away from our current spot randomly.");
                        Client.tap(new Rectangle(755, 64, 99, 43));
                        Condition.sleep(1500);
                        Walker.walkPath(venerateToSoulAltarPath);
                        waitTillStopped(9);
                    }
                }
            }
        }

        if (Player.within(soulAltarArea)) {
            Logger.debugLog("Player is now at the Soul Altar.");
        }
    }

    private void walkAltartoBloodAltar() {
        Logger.debugLog("Now walking from Venerate altar to Blood Altar.");
        Walker.walkPath(venerateToBloodAltarPath);
        waitTillStopped(6);

        if (essenceCachedLoc == null) {
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + essenceCachedLoc);
        }

        // Verify if we are in the area multiple times, if not, try walking there again and in the end log out if failed.
        if (!Player.within(bloodAltarArea)) {
            Logger.debugLog("Seems like we failed walking to the blood altar, retrying...");
            Walker.walkPath(venerateToBloodAltarPath);
            waitTillStopped(6);

            if (!Player.within(bloodAltarArea)) {
                Logger.debugLog("Seems like we failed walking to the blood altar again, retrying...");
                Walker.walkPath(venerateToBloodAltarPath);
                waitTillStopped(6);

                if (!Player.within(bloodAltarArea)) {
                    Logger.debugLog("Seems like we failed walking to the blood altar, retrying but first moving away from our current spot randomly.");
                    Client.tap(new Rectangle(755, 64, 99, 43));
                    Condition.sleep(1500);
                    Walker.walkPath(venerateToBloodAltarPath);
                    waitTillStopped(6);

                    if (!Player.within(bloodAltarArea)) {
                        Logger.debugLog("Seems like we failed walking to the blood altar, retrying but first moving away from our current spot randomly.");
                        Client.tap(new Rectangle(755, 64, 99, 43));
                        Condition.sleep(1500);
                        Walker.walkPath(venerateToBloodAltarPath);
                        waitTillStopped(6);
                    }
                }
            }
        }

        if (Player.within(bloodAltarArea)) {
            Logger.debugLog("Player is now at the Blood Altar.");
        }
    }

    private void walkBloodToShortcut() {
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
            Logger.debugLog("Enabled run!");
        }
        Logger.debugLog("Now walking from Blood Altar to the agility shortcut.");
        Walker.walkPath(bloodBackToObstaclePath);
        waitTillStopped(6);
    }

    private void walkSoulToShortcut() {
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
            Logger.debugLog("Enabled run!");
        }
        Logger.debugLog("Now walking from Soul Altar to the agility shortcut.");
        Walker.walkPath(soulBackToObstaclePath);
        waitTillStopped(6);
    }

    // METHODS TO DO STUFF HERE
    private void processEssence() {
        // Update essenceToProcess only if it is currently 0
        if (essenceToProcess == 0) {
            essenceToProcess = Inventory.count(13446, 0.95);
            Logger.debugLog("Essence to process: " + essenceToProcess);
        }

        // Process a block in the inventory if we still have them
        if (!(essenceToProcess == 0)) {
            Inventory.tapItem(1755, true, 0.80);
            generateRandomDelay(75, 150);
            Client.tap(essenceCachedLoc);
            generateRandomDelay(50, 100);

            // Decrease the essenceToProcess count by 1 after the actions
            essenceToProcess--;
        }
    }

    private void mineEssence() {
        // Sleep for a bit here to make sure our tap will register correctly.
        Condition.sleep(generateRandomDelay(750, 1500));
        // Check if we are on one of the two mining tiles already
        if (!initialCheckDone) {
            resetToSouth();
            initialCheckDone = true;
        }

        long lastItemTime = System.currentTimeMillis(); // Track the last time an item was gained
        int lastEmptySlots = Inventory.emptySlots(); // Current number of empty slots

        // Mine repeatedly and switch runestones when necessary until the inventory is full
        while (!Inventory.isFull()) {
            if (System.currentTimeMillis() - lastItemTime > 30000) {
                // Reset if no new item has been gained in the last 45 seconds
                Logger.debugLog("No new essence blocks gained in 30 seconds, resetting to south runestone as we might be stuck.");
                resetToSouth();
                lastItemTime = System.currentTimeMillis(); // Reset the timer after resetting
            }

            if (Player.leveledUp()) {
                if (Player.leveledUp()) {
                    Client.sendKeystroke("KEYCODE_SPACE");
                    Condition.sleep(generateRandomDelay(1000, 2000));

                    if (java.util.Objects.equals(currentLoc, "south")) {
                        Client.tap(tapSouthRuneStoneSOUTH);
                    } else {
                        Client.tap(tapNorthRuneStoneNORTH);
                    }
                }
            }

            if (java.util.Objects.equals(currentLoc, "south")) {
                // Check if the runestone is inactive
                if (isRunestoneInactive(southRuneStoneROI)) {
                    Client.tap(tapNorthRuneStoneSOUTH);  // Switch and start mining at the north runestone
                    Logger.debugLog("Switching to north runestone, as south is inactive.");
                    currentLoc = "north";
                }
            } else if (java.util.Objects.equals(currentLoc, "north")) {
                // Check if the runestone is inactive
                if (isRunestoneInactive(northRuneStoneROI)) {
                    Client.tap(tapSouthRuneStoneNORTH);  // Switch and start mining at the south runestone
                    Logger.debugLog("Switching to south runestone, as north is inactive.");
                    currentLoc = "south";
                }
            }

            int currentEmptySlots = Inventory.emptySlots();
            if (currentEmptySlots < lastEmptySlots) {
                lastItemTime = System.currentTimeMillis(); // Update the last item time when a new item is gained
                lastEmptySlots = currentEmptySlots; // Update the last empty slot count
            }

            Condition.sleep(750);
        }

        Logger.debugLog("Inventory is now full!");
        readXP();
    }

    private boolean doneVenerating() {

        // Check if we have a cached known location for the last essence spot already, if not cache it
        if (essenceCachedLoc == null) {
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + essenceCachedLoc);
        }

        // Calculate the center of the rectangle
        int centerX = essenceCachedLoc.x + essenceCachedLoc.width / 2;
        int centerY = essenceCachedLoc.y + essenceCachedLoc.height / 2;

        // Define the 5x5 area around the center point
        java.awt.Rectangle smallRect = new java.awt.Rectangle(centerX - 2, centerY - 2, 5, 5);

        // Check if the essence is dark instead of dense to verify if we have venerated correctly
        return Client.isColorInRect(Color.decode("#675b4e"), smallRect, 10);
    }

    private boolean isRunestoneInactive(Rectangle roi) {
        // Check if the runestone is inactive by searching for specific colors within the region of interest
        List<Point> foundPoints = Client.getPointsFromColorsInRect(inactiveRunestone, roi, 5);
        return !foundPoints.isEmpty();  // Returns true if any points are found, indicating inactivity
    }

    public int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        int delay = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        return delay;
    }

    private void resetToSouth() {
        playerPos = Walker.getPlayerPosition();
        Walker.step(southDenseRunestone);
        Condition.wait(() -> Player.atTile(southDenseRunestone), 250, 10);
        currentLoc = "south";
        Client.tap(tapSouthRuneStoneSOUTH); // Start mining at the south runestone
    }

    private void waitTillStopped(int waitTimes) {
        // Wait till we stop moving
        Tile lastPosition = Walker.getPlayerPosition();
        int unchangedCount = 0; // Counter for how many times the position has remained the same
        boolean runEnabled = Player.isRunEnabled();

        Logger.debugLog("Waiting for us to stop moving...");
        while (unchangedCount < waitTimes) { // Loop until the position hasn't changed for the specified number of checks
            Tile currentPosition = Walker.getPlayerPosition();

            // Compare currentPosition and lastPosition by coordinates
            if (java.util.Objects.equals(currentPosition.toString(), lastPosition.toString())) {
                // If the current position is the same as the last position, increment the unchanged counter
                unchangedCount++;
            } else {
                // If the position has changed, reset the counter
                unchangedCount = 0;
            }

            lastPosition = currentPosition; // Update lastPosition for the next check

            // Adjust the sleep time based on whether the player is running or not
            if (runEnabled) {
                Condition.sleep(generateRandomDelay(100, 200));
            } else {
                Condition.sleep(generateRandomDelay(200, 300));
            }
        }
        Logger.debugLog("We have stopped moving!");
    }

    private void readXP() {
        XpBar.getXP();
    }

    private void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, false, false);
        } else {
            // We do nothing here, as hop is disabled.
        }
    }

}