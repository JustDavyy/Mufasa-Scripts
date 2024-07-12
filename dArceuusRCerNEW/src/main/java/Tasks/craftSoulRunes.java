package Tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dArceuusRCerNEW.random;

import main.dArceuusRCerNEW;

import java.awt.*;
import java.util.List;

public class craftSoulRunes extends Task {

    @Override
    public boolean activate() {
        Logger.log("Checking if we should execute craftSoulRunes");
        return (Player.within(dArceuusRCerNEW.SoulArea1) || Player.within(dArceuusRCerNEW.SoulArea2) || Player.tileEquals(dArceuusRCerNEW.playerPos, dArceuusRCerNEW.soulAltarTile)) && Inventory.containsAny(new int[]{ItemList.DARK_ESSENCE_BLOCK_13446, ItemList.DARK_ESSENCE_FRAGMENTS_7938}, 0.8);
    }

    @Override
    public boolean execute() {
        Logger.log("Executing craftSoulRunes!");

        // Process dark essence blocks
        if (!Inventory.contains(ItemList.DARK_ESSENCE_FRAGMENTS_7938, 0.8) && Inventory.contains(ItemList.DARK_ESSENCE_BLOCK_13446, 0.8)) {
            int count = Inventory.count(13446, 0.95);
            for (int i = 0; i < count + 10; i++) {
                Inventory.tapItem(1755, true, 0.80);
                Client.tap(dArceuusRCerNEW.essenceCachedLoc);
                dArceuusRCerNEW.generateRandomDelay(100, 150);
            }
        }

        if (Player.tileEquals(dArceuusRCerNEW.playerPos, dArceuusRCerNEW.soulAltarTile)) {
            List<Rectangle> foundRectangles2 = Client.getObjectsFromColorsInRect(dArceuusRCerNEW.soulAltar, new Rectangle(350, 244, 137, 113), 3);

            if (!foundRectangles2.isEmpty()) {
                Rectangle randomRect2 = foundRectangles2.get(random.nextInt(foundRectangles2.size()));
                Logger.debugLog("Located the soul altar using the color finder, tapping.");
                Client.tap(randomRect2);
                Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
            } else {
                Logger.debugLog("Couldn't locate the soul altar using the color finder, moving to altar tile and proceeding...");
                Walker.step(dArceuusRCerNEW.soulAltarTile);
                Condition.wait(() -> Player.atTile(dArceuusRCerNEW.soulAltarTile), 250, 20);
                Client.tap(dArceuusRCerNEW.soulAltarStaticRect);
                Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
            }
        } else {
            List<Rectangle> foundRectangles = Client.getObjectsFromColorsInRect(dArceuusRCerNEW.soulAltar, new Rectangle(188, 199, 350, 298), 3);

            if (!foundRectangles.isEmpty()) {
                Rectangle randomRect = foundRectangles.get(random.nextInt(foundRectangles.size()));
                Logger.debugLog("Located the soul altar using the color finder, tapping.");
                Client.tap(randomRect);
                Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
            } else {
                Logger.debugLog("Couldn't locate the soul altar using the color finder, moving to altar tile and proceeding...");
                Walker.step(dArceuusRCerNEW.soulAltarTile);
                waitTillStopped(7);
                Client.tap(dArceuusRCerNEW.soulAltarStaticRect);
                Condition.wait(() -> !Inventory.contains(7938, 0.85), 250, 35);
                dArceuusRCerNEW.playerPos = dArceuusRCerNEW.soulAltarTile;
            }
        }

        if (!Inventory.containsAny(new int[]{ItemList.DARK_ESSENCE_BLOCK_13446, ItemList.DARK_ESSENCE_FRAGMENTS_7938}, 0.8)) {
            // Start a move away from the portal
            Client.tap(new Rectangle(821, 28, 16, 13));
            Condition.sleep(dArceuusRCerNEW.generateRandomDelay(750,1250));

            // Set process count to 0 to reset it
            dArceuusRCerNEW.essenceToProcess = 0;
        }

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
                Condition.sleep(dArceuusRCerNEW.generateRandomDelay(100, 200));
            } else {
                Condition.sleep(dArceuusRCerNEW.generateRandomDelay(200, 300));
            }
        }
        Logger.debugLog("We have stopped moving!");
    }
}