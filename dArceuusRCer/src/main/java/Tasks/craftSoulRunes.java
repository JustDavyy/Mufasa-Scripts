package Tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dArceuusRCer.random;
import static main.dArceuusRCer.soulAltarArea;

import main.dArceuusRCer;

import java.awt.*;
import java.util.List;

public class craftSoulRunes extends Task {

    @Override
    public boolean activate() {
        return (Player.within(dArceuusRCer.SoulArea1) || Player.within(dArceuusRCer.SoulArea2) || Player.atTile(dArceuusRCer.soulAltarTile) || Player.within(soulAltarArea)) && Inventory.containsAny(new int[]{ItemList.DARK_ESSENCE_BLOCK_13446, ItemList.DARK_ESSENCE_FRAGMENTS_7938}, 0.8) && dArceuusRCer.runeType.equals("Soul rune");
    }

    @Override
    public boolean execute() {
        GameTabs.openInventoryTab();
        // Check if we have to hop first
        Paint.setStatus("Check hop timer");
        dArceuusRCer.hopActions();

        Logger.log("Executing craftSoulRunes!");

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

        if (Player.tileEquals(dArceuusRCer.playerPos, dArceuusRCer.soulAltarTile)) {
            List<Rectangle> foundRectangles2 = Client.getObjectsFromColorsInRect(dArceuusRCer.soulAltar, new Rectangle(350, 244, 137, 113), 3);

            if (!foundRectangles2.isEmpty()) {
                Rectangle randomRect2 = foundRectangles2.get(random.nextInt(foundRectangles2.size()));
                Logger.debugLog("Located the soul altar using the color finder, tapping.");
                Paint.setStatus("Tap soul altar");
                Client.tap(randomRect2);
                Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
            } else {
                Logger.debugLog("Couldn't locate the soul altar using the color finder, moving to altar tile and proceeding...");
                Paint.setStatus("Step to soul altar");
                Walker.step(dArceuusRCer.soulAltarTile);
                Condition.wait(() -> Player.atTile(dArceuusRCer.soulAltarTile), 250, 20);
                Paint.setStatus("Tap soul altar");
                Client.tap(dArceuusRCer.soulAltarStaticRect);
                Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
            }
        } else {
            List<Rectangle> foundRectangles = Client.getObjectsFromColorsInRect(dArceuusRCer.soulAltar, new Rectangle(188, 199, 350, 298), 3);

            if (!foundRectangles.isEmpty()) {
                Paint.setStatus("Locate soul altar");
                Rectangle randomRect = foundRectangles.get(random.nextInt(foundRectangles.size()));
                Logger.debugLog("Located the soul altar using the color finder, tapping.");
                Paint.setStatus("Tap soul altar");
                Client.tap(randomRect);
                Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
            } else {
                Logger.debugLog("Couldn't locate the soul altar using the color finder, moving to altar tile and proceeding...");
                Paint.setStatus("Step to soul altar");
                Walker.step(dArceuusRCer.soulAltarTile);
                waitTillStopped(7);
                Paint.setStatus("Tap soul altar");
                Client.tap(dArceuusRCer.soulAltarStaticRect);
                Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
                dArceuusRCer.playerPos = dArceuusRCer.soulAltarTile;
            }
        }

        if (!Inventory.containsAny(new int[]{ItemList.DARK_ESSENCE_BLOCK_13446, ItemList.DARK_ESSENCE_FRAGMENTS_7938}, 0.8)) {
            // Start a move away from the portal
            Paint.setStatus("Step away from soul altar");
            Client.tap(new Rectangle(821, 28, 16, 13));
            Condition.sleep(dArceuusRCer.generateRandomDelay(750,1250));

            // Set process count to 0 to reset it
            Paint.setStatus("Reset essence to process");
            dArceuusRCer.essenceToProcess = 0;
        }

        Paint.setStatus("Read XP");
        dArceuusRCer.readXP();

        Paint.setStatus("Update profit/rune index");
        dArceuusRCer.craftedRunes = Inventory.stackSize(ItemList.SOUL_RUNE_566) - dArceuusRCer.startRunes;
        Paint.updateBox(dArceuusRCer.runeIndex, dArceuusRCer.craftedRunes);
        Paint.updateBox(dArceuusRCer.profitIndex, dArceuusRCer.craftedRunes * dArceuusRCer.runePrice);

        // Update the statistics label
        dArceuusRCer.updateStatLabel();

        return false;
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
                Condition.sleep(dArceuusRCer.generateRandomDelay(100, 200));
            } else {
                Condition.sleep(dArceuusRCer.generateRandomDelay(200, 300));
            }
        }
        Logger.debugLog("We have stopped moving!");
    }
}