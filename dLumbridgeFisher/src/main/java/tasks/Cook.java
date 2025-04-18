package tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dLumbridgeFisher.*;

public class Cook extends Task {

    private static final Tile[] pathToRange = new Tile[] {
            new Tile(12958, 12329, 0),
            new Tile(12963, 12343, 0),
            new Tile(12974, 12353, 0),
            new Tile(12978, 12379, 0),
            new Tile(12980, 12395, 0),
            new Tile(12980, 12416, 0),
            new Tile(12981, 12430, 0),
            new Tile(12981, 12451, 0),
            new Tile(12982, 12472, 0),
            new Tile(12977, 12493, 0),
            new Tile(12977, 12512, 0),
            new Tile(12968, 12528, 0),
            new Tile(12957, 12543, 0),
            new Tile(12948, 12551, 0),
            new Tile(12944, 12572, 0),
            new Tile(12939, 12586, 0),
            new Tile(12929, 12604, 0),
            new Tile(12924, 12619, 0),
            new Tile(12901, 12619, 0),
            new Tile(12884, 12618, 0),
            new Tile(12871, 12618, 0),
            new Tile(12859, 12610, 0),
            new Tile(12859, 12588, 0),
            new Tile(12846, 12585, 0),
            new Tile(12836, 12601, 0)
    };

    private static final Tile[] pathToFishing = new Tile[] {
            new Tile(12839, 12589, 0),
            new Tile(12860, 12603, 0),
            new Tile(12871, 12621, 0),
            new Tile(12885, 12621, 0),
            new Tile(12902, 12619, 0),
            new Tile(12923, 12619, 0),
            new Tile(12932, 12601, 0),
            new Tile(12943, 12581, 0),
            new Tile(12946, 12561, 0),
            new Tile(12956, 12551, 0),
            new Tile(12969, 12534, 0),
            new Tile(12978, 12517, 0),
            new Tile(12984, 12498, 0),
            new Tile(12991, 12479, 0),
            new Tile(12986, 12461, 0),
            new Tile(12983, 12445, 0),
            new Tile(12983, 12431, 0),
            new Tile(12979, 12412, 0),
            new Tile(12979, 12397, 0),
            new Tile(12976, 12379, 0),
            new Tile(12975, 12365, 0),
            new Tile(12973, 12351, 0),
            new Tile(12965, 12342, 0),
            new Tile(12958, 12329, 0),
            new Tile(12961, 12321, 0)
    };

    private static final Area rangeArea = new Area(
            new Tile(12816, 12620, 0),
            new Tile(12855, 12588, 0)
    );

    private static final Tile rangeTile = new Tile(12843, 12609, 0);
    private static final Rectangle rangeRect = new Rectangle(489, 237, 26, 40);

    @Override
    public boolean activate() {
        return cookEnabled && Inventory.isFull();
    }

    @Override
    public boolean execute() {

        if (Player.leveledUp()) {
            Logger.debugLog("We leveled up cooking!");
            cookingLevel++;
            Logger.debugLog("Cooking level is now: " + cookingLevel);
        }

        if (!countedFood) {
            shrimpGainedCount = Inventory.count(ItemList.RAW_SHRIMPS_317, 0.7, Color.decode("#957e72"));
            anchoviesGainedCount = Inventory.count(ItemList.RAW_ANCHOVIES_321, 0.7, Color.decode("#54546d"));
            countedFood = true;
        }

        Paint.setStatus("Start Cooking action");
        Logger.log("Start Cooking action.");

        if (!Player.within(rangeArea)) {
            Walker.walkPath(pathToRange);
            Player.waitTillNotMoving(18);
            Walker.step(rangeTile);
        }

        if (Player.atTile(rangeTile)) {
            if (hasFishToCook()) {
                if (Inventory.contains(ItemList.RAW_SHRIMPS_317, 0.7, Color.decode("#957e72"))) {
                    Paint.setStatus("Cook shrimps");
                    Logger.log("Cooking shrimps");
                    Inventory.tapItem(ItemList.RAW_SHRIMPS_317, 0.7, Color.decode("#957e72"));
                    Condition.sleep(150, 300);
                    Client.tap(rangeRect);
                    Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 50);
                    Client.sendKeystroke("space");
                    Condition.wait(() -> Player.leveledUp() || !Inventory.contains(ItemList.RAW_SHRIMPS_317, 0.7, Color.decode("#957e72")), 100, 600);
                } else {
                    Paint.setStatus("Cook anchovies");
                    Logger.log("Cook anchovies");
                    Client.tap(rangeRect);
                    Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 50);
                    Client.sendKeystroke("space");
                    Condition.wait(() -> Player.leveledUp() || !hasFishToCook(), 250, 280);
                }
            }
        } else {
            return false;
        }

        if (!hasFishToCook()) {

            Logger.log("Done cooking food.");
            Paint.setStatus("Dropping cooked food");
            Logger.log("Start drop actions.");

            shrimpCookedCount = shrimpCookedCount + Inventory.count(ItemList.SHRIMPS_315, 0.7, Color.decode("#9a620d"));
            anchoviesCookedCount = anchoviesCookedCount + Inventory.count(ItemList.ANCHOVIES_319, 0.7, Color.decode("#363c68"));
            burnedFishCount = burnedFishCount + Inventory.count(ItemList.BURNT_FISH_323, 0.7, Color.decode("#484242"));

            if (!Game.isTapToDropEnabled()) {
                Game.enableTapToDrop();
            }

            Inventory.tapAllItems(ItemList.SHRIMPS_315, 0.7);
            Inventory.tapAllItems(ItemList.ANCHOVIES_319, 0.7);

            Game.disableTapToDrop();

            Walker.walkPath(pathToFishing);
            Player.waitTillNotMoving(15);
        }

        return false;
    }

    private boolean hasFishToCook() {
        return Inventory.contains(ItemList.RAW_SHRIMPS_317, 0.7, Color.decode("#957e72")) ||
                Inventory.contains(ItemList.RAW_ANCHOVIES_321, 0.7, Color.decode("#54546d"));
    }
}