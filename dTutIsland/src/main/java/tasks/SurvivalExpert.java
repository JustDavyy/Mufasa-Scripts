package tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Client;
import static main.dTutIsland.*;
import static main.dTutIsland.NPCHeaderCheckRect;

public class SurvivalExpert extends Task {
    public boolean activate() {
        return !survivalExpertDone;
    }

    private final static Tile expertAreaTile = new Tile(12411, 12133, 0);
    private boolean firstWalkDone = false;
    private boolean firstStepDone = false;
    private boolean secondStepDone = false;
    private boolean thirdStepDone = false;
    private boolean fourthStepDone = false;
    private boolean fifthStepDone = false;
    private boolean sixthStepDone = false;

    private final static Rectangle searchArea = new Rectangle(169, 180, 407, 284);
    private static List<Color> survivalExpertColors = Arrays.asList(
            Color.decode("#7d4947"),
            Color.decode("#532420"),
            Color.decode("#8a504c"),
            Color.decode("#653a39")
    );
    private static List<Color> fireColors = Arrays.asList(
            Color.decode("#ecc146"),
            Color.decode("#3c3109"),
            Color.decode("#a7872d")
    );

    private static final Rectangle inventoryTab = new Rectangle(806, 227, 26, 26);
    private static final Rectangle statsTab = new Rectangle(844, 227, 28, 27);
    private final static Rectangle fishROIArea1 = new Rectangle(148, 321, 434, 214);
    private final static Tile fishingTile = new Tile(12403, 12121, 0);
    private final static Tile firemakingTile = new Tile(12407, 12149, 0);


    // Tree tiles and rects
    private final static Tile tree1Tile = new Tile(12403, 12141, 0);
    private final static Tile tree2Tile = new Tile(12403, 12133, 0);
    private final static Tile tree3Tile = new Tile(12387, 12141, 0);
    private final static Rectangle tree1Rect = new Rectangle(344, 237, 31, 30);
    private final static Rectangle tree2Rect = new Rectangle(354, 270, 26, 23);
    private final static Rectangle tree3Rect = new Rectangle(366, 257, 34, 25);

    private final static Rectangle fireSearchArea = new Rectangle(386, 203, 171, 157);

    @Override
    public boolean execute() {
        Logger.log("Running Survival Expert task");

        if (!firstWalkDone) {
            Logger.debugLog("Walking to survival expert area");
            Walker.walkTo(expertAreaTile);
            firstWalkDone = true;
        }

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(survivalExpertColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Survival Expert could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Survival Expert located. Tapping.");
                Client.tap(points);

                Logger.log("Checking if NPC dialogue is open");
                Condition.wait(() -> Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5), 100, 100);

                if (Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5)) {
                    Logger.log("NPC Dialogue is open");
                } else {
                    Logger.log("NPC Dialogue is not open, retrying!");
                    return false;
                }
            }

            Logger.log("Continue dialogues");
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);

            Logger.log("Open inventory");
            Client.tap(inventoryTab);
            Condition.sleep(400, 600);

            firstStepDone = true;
        }

        if (!secondStepDone) {
            Logger.log("Fishing shrimps!");
            Polygon fishPoly = Overlay.findNearest(Color.decode("#27ffff"));

            if (fishPoly != null) {
                Client.tap(fishPoly);
                Condition.wait(() -> Inventory.contains(ItemList.RAW_SHRIMPS_317, 0.7), 100, 100);
            } else {
                Logger.log("Could not find a fishing spot, moving to the fishing area!");
                Walker.walkTo(fishingTile);
                Condition.sleep(4000, 5000);
                return false;
            }

            if (Inventory.contains(ItemList.RAW_SHRIMPS_317, 0.7)) {
                Logger.log("We've caught the shrimp we need.");
                secondStepDone = true;
            }
        }

        if (!thirdStepDone) {
            Logger.log("Open stats tab");
            Client.tap(statsTab);
            Condition.sleep(400, 600);
            Logger.log("Open inventory");
            Client.tap(inventoryTab);
            Condition.sleep(400, 600);

            thirdStepDone = true;
        }

        if (!fourthStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(survivalExpertColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Survival Expert could not be located using the specified colors. Retrying...");
                Walker.walkTo(expertAreaTile);
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Survival Expert located. Tapping.");
                Client.tap(points);

                Logger.log("Checking if NPC dialogue is open");
                Condition.wait(() -> Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5), 100, 100);

                if (Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5)) {
                    Logger.log("NPC Dialogue is open");
                } else {
                    Logger.log("NPC Dialogue is not open, retrying!");
                    return false;
                }
            }

            Logger.log("Continue dialogues");
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            fourthStepDone = true;
        }

        if (!fifthStepDone) {
            Logger.log("Start to chop wood");
            doChopTree();

            if (Inventory.contains(ItemList.LOGS_1511, 0.7)) {
                Logger.log("We have logs in our inventory.");
            } else {
                return false;
            }

            Logger.log("Start fire making");
            Walker.walkTo(firemakingTile);
            Player.waitTillNotMoving(7);

            Inventory.tapItem(ItemList.TINDERBOX_590, 0.7);
            Condition.sleep(250, 400);
            Inventory.tapItem(ItemList.LOGS_1511, false, 0.7);
            Condition.wait(() -> !Inventory.contains(ItemList.LOGS_1511, 0.7), 100, 75);
            Condition.sleep(7000, 8000);

            if (!Inventory.contains(ItemList.LOGS_1511, 0.7)) {
                Logger.log("Successfully created a fire.");
            }

            Logger.log("Locate fire");
            List<Point> points = Client.getPointsFromColorsInRect(fireColors, fireSearchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Fire could not be located using the specified colors. Retrying...");
                return false;
            } else {
                Logger.log("Fire located. Initiating cooking action.");
                Inventory.tapItem(ItemList.RAW_SHRIMPS_317, 0.7);
                Condition.sleep(250, 400);
                Client.tap(points);

                Logger.log("Checking if we cooked a shrimp");
                Condition.wait(() -> Inventory.contains(ItemList.SHRIMPS_315, 0.7, Color.decode("#7c5008")), 100, 100);

                if (Inventory.contains(ItemList.SHRIMPS_315, 0.7, Color.decode("#7c5008"))) {
                    Logger.log("Successfully cooked a shrimp!");
                    fifthStepDone = true;
                } else {
                    Logger.log("No cooked shrimp found, returning!");
                    return false;
                }
            }

        }

        if (!sixthStepDone) {
            Logger.log("Leaving the Survival Expert area");
            handleFence();

            if (Player.atTile(new Tile(12355, 12113, 0)) || Player.atTile(new Tile(12355, 12117, 0))) {
                Logger.log("We successfully crossed the fence. Moving on!");
                sixthStepDone = true;
            } else {
                return false;
            }
        }

        // SET TO TRUE HERE AS WE ARE DONE
        if (sixthStepDone) {
            survivalExpertDone = true;
        }

        return false;
    }

    private void handleFence() {
        // Define the tiles and corresponding rectangles
        Tile[] tiles = {new Tile(12359, 12117, 0), new Tile(12359, 12113, 0)};
        Rectangle[] rectangles = {new Rectangle(411, 240, 7, 18), new Rectangle(410, 282, 6, 15)};

        // Randomly choose one of the sets (0 or 1)
        Random random = new Random();
        int chosenIndex = random.nextInt(2); // Randomly pick 0 or 1

        // Perform the chosen step and tap
        Tile chosenTile = tiles[chosenIndex];
        Rectangle chosenRect = rectangles[chosenIndex];

        Logger.log("Handling fence: stepping to " + chosenTile + " and tapping " + chosenRect);
        Walker.step(chosenTile);
        Client.tap(chosenRect);
        Condition.sleep(2500, 3000);
    }

    private void doChopTree() {
        // Define the tiles and corresponding rectangles
        Tile[] treeTiles = {tree1Tile, tree2Tile, tree3Tile};
        Rectangle[] treeRects = {tree1Rect, tree2Rect, tree3Rect};

        // Randomly choose one of the trees (1, 2, or 3)
        Random random = new Random();
        int chosenIndex = random.nextInt(3); // 0, 1, or 2
        Tile chosenTile = treeTiles[chosenIndex];
        Rectangle chosenRect = treeRects[chosenIndex];

        // Step to the chosen tree tile
        Logger.log("Walking to tree at: " + chosenTile);
        Walker.step(chosenTile);

        // Check if the player has reached the chosen tile
        if (Player.atTile(chosenTile)) {
            Logger.log("Player is at the tree. Tapping the tree rect: " + chosenRect);
            Client.tap(chosenRect);
            Condition.wait(() -> Inventory.contains(ItemList.LOGS_1511, 0.7), 100, 75);
        } else {
            Logger.log("Player is not at the tree. Retrying...");
        }
    }
}
