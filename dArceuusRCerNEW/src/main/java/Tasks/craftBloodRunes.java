package Tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import main.dArceuusRCerNEW;

import java.awt.*;
import java.util.List;

public class craftBloodRunes extends Task {

    @Override
    public boolean activate() {
        Logger.log("Checking if we should execute craftBloodRunes");
        return Player.within(dArceuusRCerNEW.bloodAltarArea) && Inventory.containsAny(new int[]{ItemList.DARK_ESSENCE_BLOCK_13446, ItemList.DARK_ESSENCE_FRAGMENTS_7938}, 0.8);
    }

    @Override
    public boolean execute() {
        // Have the logic that needs to be executed with the task here
        Logger.log("Executing craftBloodRunes!");

        // Process dark essence blocks
        if (!Inventory.contains(ItemList.DARK_ESSENCE_FRAGMENTS_7938, 0.8) && Inventory.contains(ItemList.DARK_ESSENCE_BLOCK_13446, 0.8)) {
            int count = Inventory.count(13446, 0.95);
            for (int i = 0; i < count + 10; i++) {
                Inventory.tapItem(1755, true, 0.80);
                Client.tap(dArceuusRCerNEW.essenceCachedLoc);
                dArceuusRCerNEW.generateRandomDelay(100, 150);
            }
        }

        List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCerNEW.bloodAltar, new Rectangle(329, 163, 277, 201), 5);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the blood altar using the color finder, tapping.");
            Client.tap(foundPoints, true);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 20);
        } else {
            Logger.debugLog("Couldn't locate the blood altar using the color finder, moving to altar tile and proceeding...");
            Walker.step(dArceuusRCerNEW.bloodAltarTile);
            Condition.wait(() -> Player.atTile(dArceuusRCerNEW.bloodAltarTile), 250, 20);
            Client.tap(dArceuusRCerNEW.bloodAltarStaticRect);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 20);
        }

        return false;
    }
}