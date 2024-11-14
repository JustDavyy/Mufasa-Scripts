package tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;
import static utils.Helpers.countFoodInInventory;

public class CreatePotions extends Task {
    Tile herbTile = new Tile(6503, 15661, 0);
    Rectangle herbRect = new Rectangle(388, 260, 18, 26);
    Tile potionTile = new Tile(6507, 15677, 0);
    Tile brewmaTile = new Tile(6535, 15693, 0);
    Rectangle potionRect = new Rectangle(399, 260, 15, 19);
    Rectangle brewmaRect = new Rectangle(499, 244, 8, 25);
    int brumaHerbItem = ItemList.BRUMA_HERB_20698;
    int rejuvPotionUnf = ItemList.REJUVENATION_POTION__UNF__20697;
    Area topLeftArea = new Area(
            new Tile(6413, 15738, 0),
            new Tile(6520, 15880, 0)
    );
    Area topRightArea = new Area(
            new Tile(6527, 15739, 0),
            new Tile(6609, 15877, 0)
    );
    Tile[] topLeftRecoveryPath = new Tile[] {
            new Tile(6499, 15838, 0),
            new Tile(6479, 15825, 0),
            new Tile(6461, 15812, 0),
            new Tile(6454, 15785, 0),
            new Tile(6457, 15751, 0),
            new Tile(6469, 15724, 0),
            new Tile(6488, 15716, 0),
            new Tile(6510, 15704, 0),
            new Tile(6523, 15685, 0)
    };
    Tile[] topRightRecoveryPath = new Tile[] {
            new Tile(6546, 15837, 0),
            new Tile(6565, 15825, 0),
            new Tile(6584, 15807, 0),
            new Tile(6586, 15777, 0),
            new Tile(6586, 15750, 0),
            new Tile(6574, 15733, 0),
            new Tile(6553, 15716, 0),
            new Tile(6528, 15707, 0),
            new Tile(6521, 15685, 0)
    };
    @Override
    public boolean activate() {
        return foodAmountInInventory < foodAmountLeftToBank && !isGameGoing || foodAmountInInventory < foodAmountLeftToBank & isGameGoing && Player.within(lobby) || foodAmountInInventory == 0 && isGameGoing && shouldEat;
    }

    @Override
    public boolean execute() {
        Logger.log("We should create potions");
        updateCurrentLocation();

        boolean hasBrumaHerb = Inventory.contains(brumaHerbItem, 0.75);
        boolean hasRejuvUnf = Inventory.contains(rejuvPotionUnf, 0.96);

        if ((!hasBrumaHerb || !hasRejuvUnf)) {
            Logger.log("Getting herbs and unfinished potions");

            if (!hasBrumaHerb) {
                if (navigateToTile(herbTile, "herb")) {
                    collectHerbs();
                    return true;
                }
            }

            if (!hasRejuvUnf) {
                if (navigateToTile(potionTile, "potion")) {
                    collectRejuvPotions();
                    return true;
                }
            }
        }

        if (hasBrumaHerb && hasRejuvUnf) {
            if (druidicRitualCompleted) {
                processInventoryWithQuest();
            } else {
                Walker.step(brewmaTile);
                processInventoryWithoutQuest();
            }
        }

        return false;
    }

    private void updateCurrentLocation() {
        currentLocation = Walker.getPlayerPosition();
    }

    private boolean navigateToTile(Tile tile, String tileName) {
        if (Player.atTile(tile)) {
            return false;
        }

        if (Walker.isReachable(tile)) {
            Walker.step(tile);
        } else if (Walker.isReachable(new Tile(6519, 15681, 0))) {
            Walker.walkTo(new Tile(6519, 15681, 0));
            Walker.step(tile);
        } else if (Player.within(topLeftArea)) {
            Walker.walkPath(topLeftRecoveryPath);
            Walker.step(tile);
        } else if (Player.within(topRightArea)) {
            Walker.walkPath(topRightRecoveryPath);
            Walker.step(tile);
        } else {
            Logger.log("Webwalking to " + tileName + " spot!");
            Walker.webWalk(tile, true);
        }

        Condition.wait(() -> Player.atTile(tile), 200, 40);
        updateCurrentLocation();
        return true;
    }

    /**
     * Handles the collection of herbs.
     */
    private void collectHerbs() {
        Logger.log("Collecting herbs...");
        Client.tap(herbRect);
        Condition.wait(() -> Inventory.count(brumaHerbItem, 0.75) >= foodAmount, 200, 100);
    }

    /**
     * Handles the collection of rejuvenation potions.
     */
    private void collectRejuvPotions() {
        Logger.log("Collecting rejuvenation potions...");
        int herbCount = Inventory.count(brumaHerbItem, 0.75);
        while (Inventory.count(rejuvPotionUnf, 0.96) < herbCount && !Script.isScriptStopping()) {
            Client.tap(potionRect);

            // Check how close we are to the target count and adjust sleep delay accordingly
            int currentCount = Inventory.count(rejuvPotionUnf, 0.96);
            if (herbCount - currentCount <= 1) {
                Condition.sleep(generateRandomDelay(1900, 2700)); // Slower as it approaches target
            } else {
                Condition.sleep(generateRandomDelay(800, 1100)); // Regular speed
            }

            if (currentCount >= herbCount) {
                break;
            }
        }
    }

    /**
     * Processes the inventory by combining herbs and potions using herblore.
     */
    private void processInventoryWithQuest() {
        Logger.log("Combining herbs and unfinished potions in inventory.");
        Inventory.tapItem(brumaHerbItem, 0.75);
        Condition.sleep(generateRandomDelay(200, 400));
        Inventory.tapItem(rejuvPotionUnf, 0.96);
        Condition.wait(() -> !Inventory.contains(brumaHerbItem, 0.75) || Player.leveledUp(), 300, 60);

        if (Player.leveledUp()) {
            Logger.log("We leveled up, restarting!");
            processInventoryWithQuest(); //Start the action again! recursive.. I know..
        }

        countFoodInInventory();
    }

    /**
     * Processes the inventory by combining herbs and potions using herblore.
     */
    private void processInventoryWithoutQuest() {
        Logger.log("Using Brew'ma to combine pots and herbs.");
        if (!Player.atTile(brewmaTile)) {
            Walker.step(brewmaTile);
        }

        if (Player.atTile(brewmaTile)) {
            boolean tappedHerb = Math.random() < 0.5;

            if (tappedHerb) {
                Inventory.tapItem(brumaHerbItem, 0.75);
            } else {
                Inventory.tapItem(rejuvPotionUnf, 0.96);
            }
            Condition.sleep(generateRandomDelay(100, 150));

            Client.longPress(brewmaRect);
            Condition.sleep(generateRandomDelay(100, 200));

            if (tappedHerb) {
                Logger.debugLog("Finding the use bruma herb on Brew'ma menu option...");
                Rectangle brumaHerbOption = Objects.getBestMatch("/imgs/usebrumaherb.png", 0.8);

                if (brumaHerbOption != null) {
                    Client.tap(brumaHerbOption);
                } else {
                    Logger.debugLog("Use bruma herb menu option could not be located.");
                }
            } else {
                Logger.debugLog("Finding the use rejuv pot on Brew'ma menu option...");
                Rectangle rejuvPotOption = Objects.getBestMatch("/imgs/userejuvpot.png", 0.8);

                if (rejuvPotOption != null) {
                    Client.tap(rejuvPotOption);
                } else {
                    Logger.debugLog("Use rejuv pot menu option could not be located.");
                }
            }

            Condition.wait(() -> !Inventory.contains(brumaHerbItem, 0.75), 300, 60);
            // Close the chatbox menu
            Client.sendKeystroke("space");

            countFoodInInventory();
        } else {
            Logger.debugLog("Failed to move to Brew'ma tile.");
        }
    }
}
