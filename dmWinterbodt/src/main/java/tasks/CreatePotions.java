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
    Rectangle potionRect = new Rectangle(399, 260, 15, 19);

    Area safeAreaToStep = new Area(
            new Tile(6477, 15640, 0),
            new Tile(6562, 15710, 0)
    );

    int brumaHerbItem = ItemList.BRUMA_HERB_20698;
    int rejuvPotionUnf = ItemList.REJUVENATION_POTION__UNF__20697;

    @Override
    public boolean activate() {
        if (selectedFood != "Rejuv Potion") {
            return false;
        }

        return foodAmountInInventory < foodAmountLeftToBank && !isGameGoing;
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
            processInventory();
        }

        return false;
    }

    /**
     * Updates the current location of the player.
     */
    private void updateCurrentLocation() {
        currentLocation = Walker.getPlayerPosition();
    }

    /**
     * Navigates to a specified tile either by stepping or webwalking.
     *
     * @param tile The destination tile.
     * @param tileName A descriptive name for logging purposes.
     * @return true if navigation was initiated, false if already at the tile.
     */
    private boolean navigateToTile(Tile tile, String tileName) {
        if (Player.atTile(tile)) {
            return false;
        }

        if (Player.isTileWithinArea(currentLocation, safeAreaToStep)) {
            Logger.log("Within stepping distance, stepping to " + tileName + " spot!");
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
        while (Inventory.count(rejuvPotionUnf, 0.96) < foodAmount && !Script.isScriptStopping()) {
            Client.tap(potionRect);
            Condition.sleep(generateRandomDelay(600, 900));
            if (Inventory.count(rejuvPotionUnf, 0.96) >= foodAmount) {
                break;
            }
        }
    }

    /**
     * Processes the inventory by combining herbs and potions.
     */
    private void processInventory() {
        Logger.log("Combining herbs and unfinished potions in inventory.");
        Inventory.tapItem(brumaHerbItem, 0.75);
        Condition.sleep(generateRandomDelay(200, 400));
        Inventory.tapItem(rejuvPotionUnf, 0.96);
        Condition.wait(() -> !Inventory.contains(brumaHerbItem, 0.75), 300, 60);
        countFoodInInventory();
        Logger.log("Food amount in inventory: " + foodAmountInInventory);
    }
}
