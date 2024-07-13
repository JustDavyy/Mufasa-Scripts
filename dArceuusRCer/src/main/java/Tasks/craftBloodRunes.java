package Tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import main.dArceuusRCer;

import java.awt.*;
import java.util.List;

public class craftBloodRunes extends Task {

    @Override
    public boolean activate() {
        return Player.within(dArceuusRCer.bloodAltarArea) && Inventory.containsAny(new int[]{ItemList.DARK_ESSENCE_BLOCK_13446, ItemList.DARK_ESSENCE_FRAGMENTS_7938}, 0.8) && dArceuusRCer.runeType.equals("Blood rune");
    }

    @Override
    public boolean execute() {
        // Check if we have to hop first
        Paint.setStatus("Check for hop");
        dArceuusRCer.hopActions();

        // Have the logic that needs to be executed with the task here
        Logger.log("Executing craftBloodRunes!");

        if (dArceuusRCer.essenceCachedLoc == null) {
            Paint.setStatus("Cache essence loc");
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            dArceuusRCer.essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + dArceuusRCer.essenceCachedLoc);
        }

        // Process dark essence blocks
        if (!Inventory.contains(ItemList.DARK_ESSENCE_FRAGMENTS_7938, 0.8) && Inventory.contains(ItemList.DARK_ESSENCE_BLOCK_13446, 0.8)) {
            Paint.setStatus("Process dark essence");
            int count = Inventory.count(13446, 0.95);
            for (int i = 0; i < count + 2; i++) {
                Inventory.tapItem(1755, true, 0.80);
                Client.tap(dArceuusRCer.essenceCachedLoc);
                dArceuusRCer.generateRandomDelay(100, 150);
            }
        }

        List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCer.bloodAltar, new Rectangle(329, 163, 277, 201), 5);

        if (!foundPoints.isEmpty()) {
            Paint.setStatus("Tap blood altar");
            Logger.debugLog("Located the blood altar using the color finder, tapping.");
            Client.tap(foundPoints, true);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 20);
        } else {
            Logger.debugLog("Couldn't locate the blood altar using the color finder, moving to altar tile and proceeding...");
            Paint.setStatus("Step to blood altar");
            Walker.step(dArceuusRCer.bloodAltarTile);
            Condition.wait(() -> Player.atTile(dArceuusRCer.bloodAltarTile), 250, 20);
            Paint.setStatus("Tap blood altar");
            Client.tap(dArceuusRCer.bloodAltarStaticRect);
            Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 20);
        }

        Paint.setStatus("Read XP");
        dArceuusRCer.readXP();
        if (dArceuusRCer.usingEssence) {
            if (!Inventory.contains(26392, 0.95)) {
                if (Inventory.contains(26390, 0.7)){
                    Paint.setStatus("Activate blood essence");
                    Logger.debugLog("No active blood essence found, activating one!");
                    Inventory.tapItem(26390, false, 0.7);
                    dArceuusRCer.bloodEssenceUsed += 1;
                    Paint.updateBox(dArceuusRCer.essenceIndex, dArceuusRCer.bloodEssenceUsed);
                }
            }
        }

        Paint.setStatus("Update rune/profit count");
        dArceuusRCer.craftedRunes = Inventory.stackSize(ItemList.BLOOD_RUNE_565) - dArceuusRCer.startRunes;
        Paint.updateBox(dArceuusRCer.runeIndex, dArceuusRCer.craftedRunes);
        Paint.updateBox(dArceuusRCer.profitIndex, (dArceuusRCer.craftedRunes * dArceuusRCer.runePrice) - (dArceuusRCer.bloodEssenceUsed * dArceuusRCer.essencePrice));

        // Update the statistics label
        dArceuusRCer.updateStatLabel();

        return false;
    }
}