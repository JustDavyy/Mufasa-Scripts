import helpers.*;
import helpers.utils.OptionType;
import helpers.utils.OverlayColor;
import helpers.utils.Tile;
import helpers.utils.Area;
import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.time.Instant;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dZeah RCer",
        description = "Crafts blood or soul runes in Zeah.",
        version = "1.00",
        guideLink = "To be added.",
        category = ScriptCategory.Fishing
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings? WDH is disabled for this script, as there's users on every world.",
                        defaultValue = "1",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dZeahRCer extends AbstractScript {

Tile[] mineToShortcutOutPath = new Tile[] {
        new Tile(199, 132),
        new Tile(209, 117)
    };
Tile[] shortcutOutToAltarPath = new Tile[] {
        new Tile(198, 111),
        new Tile(189, 116),
        new Tile(180, 114),
        new Tile(171, 108),
        new Tile(164, 105),
        new Tile(153, 106)
    };
Tile altarTile = new Tile(150,97);
Tile obstacleOutsideTile = new Tile(209, 112);
Area beforeObstacleOutArea = new Area(
        new Tile(201, 115),
        new Tile(208, 122)
);
Area beforeObstacleInArea = new Area(
        new Tile(204, 107),
        new Tile(209, 112)
);
Tile bloodAltarTile = new Tile(152,174);
Rectangle bloodAltar = new Rectangle(361, 139, 48, 41);
Rectangle venerateAltar = new Rectangle(430, 320, 28, 40);
Tile[] altarToAltarPath = new Tile[] {
        new Tile(136, 100),
        new Tile(120, 102),
        new Tile(106, 103),
        new Tile(92, 105),
        new Tile(82, 104),
        new Tile(73, 114),
        new Tile(76, 127),
        new Tile(86, 129),
        new Tile(101, 132),
        new Tile(116, 133),
        new Tile(130, 131),
        new Tile(146, 134),
        new Tile(162, 137),
        new Tile(171, 146),
        new Tile(174, 158),
        new Tile(177, 170),
        new Tile(167, 175),
        new Tile(159, 175)
    };
Tile[] venerateBackToObstaclePath = new Tile[] {
        new Tile(155, 105),
        new Tile(168, 105),
        new Tile(174, 111),
        new Tile(186, 115),
        new Tile(196, 113),
        new Tile(207, 109)
    };

Tile[] bloodBackToObstaclePath = new Tile[] {
        new Tile(159, 175),
        new Tile(171, 174),
        new Tile(174, 162),
        new Tile(174, 149),
        new Tile(182, 141)
    };

Tile[] obstacleInToMinePath = new Tile[] {
        new Tile(200, 125),
        new Tile(212, 138)
    };
Tile southDenseRunestone = new Tile(210,145);
Tile northDenseRunestone = new Tile(210,137);
Tile obstacleBackToMineFromBloodInTile = new Tile(197,138);
Tile obstacleBackToMineFromVenerateInTile = new Tile(209,114);
Area miningArea = new Area(
        new Tile(208, 134),
        new Tile(216, 147)
);
String hopProfile;
Boolean hopEnabled;
String mapString = "/maps/ZeahRC.png";

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));

        Logger.log("Thank you for using the dZeahRCer script!");
        Logger.log("Setting up everything for your gains now...");

        if (hopEnabled) {
            Logger.debugLog("Hopping is enabled for this run!");
        } else {
            Logger.debugLog("Hopping is disabled for this run!");
        }

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        if (GameTabs.isInventoryTabOpen()) {
            GameTabs.closeInventoryTab();
        }

        // TESTING STUFF HERE

        // FIRST MOVE TO VENERATE ALTAR and interact with it
        moveToVenerateAltar();
        interactWithVenerateAltar();

        // MOVE BACK TO THE MINE AS WE NEED TO START OUR 2ND ITERATION
        moveBackToMineFromVenerate();

        // ONCE WE ARE BACK AT THE MINE, WAIT FOR A BIT AND MOVE TO venerate again (2nd iteration)
        Condition.sleep(2000);
        moveToVenerateAltar();
        interactWithVenerateAltar();

        // NOW MOVE TO THE BLOOD ALTAR
        moveToBloodAltar();
        // do some interaction here as well.

        // MOVE BACK TO MINE
        moveBackToMineFromBlood();

    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        //Logger.debugLog("Temp log statement.");

    }






    // ACTIONS AND SO ON
    private void interactWithVenerateAltar(){
        Client.tap(venerateAltar);
    }

    private void interactWithBloodAltar() {
        Client.tap(bloodAltar);
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

    private void moveBackToMineFromVenerate() {
        Logger.debugLog("Pathing back to the mine from the Venerate altar.");
        walkVenerateToShortcut();
        traverseShortcutIn();
        walkShortcutIntoMine();
    }

    private void moveBackToMineFromBlood() {
        Logger.debugLog("Pathing back to the mine from the Blood altar.");
        walkBloodToShortcut();
        traverseBloodShortcutIn();
        stepToSouthRunestone();
    }

    private void moveBackToMineFromSoul() {
        Logger.debugLog("Pathing back to the mine from the Soul altar.");
    }

    // ALL WALKING / PATHING SMALLER METHODS CAN BE FOUND BELOW

    private void walkVenerateToShortcut() {
        Logger.debugLog("Walking towards to agility shortcut.");
        Walker.walkPathOnCustomMap(mapString, venerateBackToObstaclePath);
        Condition.wait(() -> Player.withinCustom(mapString,beforeObstacleInArea), 750, 20);
        Condition.sleep(2000);
    }
    private void walkMineToShortcutOut() {
        Logger.debugLog("Walking towards the agility shortcut.");
        Walker.walkPathOnCustomMap(mapString, mineToShortcutOutPath);
        Condition.wait(() -> Player.withinCustom(mapString, beforeObstacleOutArea), 750, 20);
        Condition.sleep(2000);
    }

    private void walkShortcutIntoMine() {
        Logger.debugLog("Walking back to the mining area.");
        Walker.walkPathOnCustomMap(mapString, obstacleInToMinePath);
        Condition.wait(() -> Player.withinCustom(mapString, miningArea), 750, 20);
        Condition.sleep(1500);
    }

    private void traverseShortcutOut() {
        Logger.debugLog("Crossing the rocks obstacle.");
        Polygon obstacleOut = Overlay.findNearest(OverlayColor.AGILITY);
        if (obstacleOut != null) {
            Client.tap(obstacleOut);
        } else {
            Logger.debugLog("Could not locate the agility shortcut, stopping script.");
            Script.forceStop();
        }
        Condition.wait(() -> Player.atTileCustom(mapString, obstacleOutsideTile), 500, 16);
        Condition.sleep(1000);
    }

    private void traverseShortcutIn() {
        Logger.debugLog("Crossing the rocks obstacle.");
        Polygon obstacleOut = Overlay.findNearest(OverlayColor.AGILITY);
        if (obstacleOut != null) {
            Client.tap(obstacleOut);
        } else {
            Logger.debugLog("Could not locate the agility shortcut, stopping script.");
            Script.forceStop();
        }
        Condition.wait(() -> Player.atTileCustom(mapString, obstacleBackToMineFromVenerateInTile), 500, 16);
        Condition.sleep(1000);
    }

    private void traverseBloodShortcutIn() {
        Logger.debugLog("Traversing down the rocks obstacles.");
        Polygon obstacleOut = Overlay.findNearest(OverlayColor.AGILITY);
        if (obstacleOut != null) {
            Client.tap(obstacleOut);
        } else {
            Logger.debugLog("Could not locate the agility shortcut, stopping script.");
            Script.forceStop();
        }
        Condition.wait(() -> Player.atTileCustom(mapString, obstacleBackToMineFromBloodInTile), 500, 16);
        Condition.sleep(2000);
    }

    private void walkShortcutOutToAltar() {
        Logger.debugLog("Moving from shortcut to Venerate Altar.");
        Walker.walkPathOnCustomMap(mapString, shortcutOutToAltarPath);
        Condition.sleep(1500);
        Walker.stepCustomMap(altarTile, mapString);
        Condition.wait(() -> Player.atTileCustom(mapString, altarTile), 250, 12);
        Logger.debugLog("Player is now located at the Venerate Altar.");
        Condition.sleep(1500);
    }

    private void walkAltartoBloodAltar() {
        Logger.debugLog("Now walking from Venerate altar to Blood Altar.");
        Walker.walkPathOnCustomMap(mapString, altarToAltarPath);
        Condition.sleep(1750);
        Walker.stepCustomMap(bloodAltarTile, mapString);
        Condition.wait(() -> Player.atTileCustom(mapString, bloodAltarTile), 250, 20);
        Logger.debugLog("Player is now at the Blood Altar.");
    }

    private void walkBloodToShortcut() {
        Logger.debugLog("Now walking from Blood Altar to the agility shortcut.");
        Walker.walkPathOnCustomMap(mapString, bloodBackToObstaclePath);
        Condition.sleep(2000);
    }

    private void stepToSouthRunestone(){
        Logger.debugLog("Moving to south dense runestone.");
        Walker.stepCustomMap(southDenseRunestone, mapString);
        Condition.wait(() -> Player.atTileCustom(mapString, southDenseRunestone), 250, 20);
    }

    private void stepToNorthRunestone(){
        Logger.debugLog("Moving to north dense runestone.");
        Walker.stepCustomMap(northDenseRunestone, mapString);
        Condition.wait(() -> Player.atTileCustom(mapString, northDenseRunestone), 250, 20);
    }

}