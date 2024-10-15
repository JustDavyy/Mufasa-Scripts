package Tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import static helpers.Interfaces.*;

import main.dArceuusRCer;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class moveBackToMine extends Task {

    Tile bloodRockObstacleSuccess = new Tile(7007, 15165, 0);
    Tile soulRockObstacleSuccess = new Tile(7103, 15265, 0);


    @Override
    public boolean activate() {
        return (Player.within(dArceuusRCer.bloodAltarArea) && !Inventory.containsAny(new int[]{ItemList.DARK_ESSENCE_BLOCK_13446, ItemList.DARK_ESSENCE_FRAGMENTS_7938}, 0.8))
                || ((Player.within(dArceuusRCer.soulAltarArea) || Player.tileEquals(dArceuusRCer.playerPos, dArceuusRCer.soulAltarTile)) && !Inventory.containsAny(new int[]{ItemList.DARK_ESSENCE_BLOCK_13446, ItemList.DARK_ESSENCE_FRAGMENTS_7938}, 0.8))
                || (Player.within(dArceuusRCer.venerateAlterArea) || Player.within(dArceuusRCer.beforeObstacleInArea)) && !Inventory.contains(ItemList.DARK_ESSENCE_BLOCK_13446, 0.8)
                || (Player.within(dArceuusRCer.venerateAlterArea) || Player.within(dArceuusRCer.beforeObstacleInArea)) && doneVenerating() && !Inventory.contains(ItemList.DARK_ESSENCE_FRAGMENTS_7938, 0.8)
                || (Player.within(dArceuusRCer.mineFailSafeArea1) || Player.within(dArceuusRCer.mineFailSafeArea2) || Player.within(dArceuusRCer.mineFailSafeArea3));
    }

    @Override
    public boolean execute() {
        // Have the logic that needs to be executed with the task here
        Logger.log("Executing moveBackToMine!");
        GameTabs.openInventoryTab();
        
        if (Player.within(dArceuusRCer.bloodAltarArea)) {
            // Check if we have to hop first
            Paint.setStatus("Check hop timer");
            dArceuusRCer.hopActions();

            Paint.setStatus("Walk back to mine");
            Logger.debugLog("Pathing back to the mine from the Blood altar.");

            walkBloodToShortcut();
            traverseBloodShortcutIn();

            // Wait for a bit
            Condition.sleep(2500);

            // Tap the north runestone to start mining again
            Client.tap(new Rectangle(636, 187, 24, 19));
            dArceuusRCer.lastEmptySlots = Inventory.emptySlots();
            dArceuusRCer.lastItemTime = System.currentTimeMillis();
            dArceuusRCer.currentLoc = "north";
            dArceuusRCer.playerPos = dArceuusRCer.northDenseRunestone;
            waitTillStopped(9);

            dArceuusRCer.currentLoc = "north";
        } else if (Player.within(dArceuusRCer.soulAltarArea) || Player.tileEquals(dArceuusRCer.playerPos, dArceuusRCer.soulAltarTile)) {
            // Check if we have to hop first
            Paint.setStatus("Check hop timer");
            dArceuusRCer.hopActions();

            Paint.setStatus("Walk back to mine");

            Logger.debugLog("Pathing back to the mine from the Soul altar.");
            walkSoulToShortcut();
            // Northern shortcut
            traverseSoulShortcutIn();
            // Sleep to be sure, we finished traveling
            Condition.sleep(dArceuusRCer.generateRandomDelay(2000,2500));
            // Check if we're still at the first obstacle, re-do if needed in case we missclicked.
            if (!Player.atTile(soulRockObstacleSuccess)) {
                traverseSoulShortcutIn();
            }
            // Southern shortcut
            traverseSoulShortcutIn2();
            dArceuusRCer.lastEmptySlots = Inventory.emptySlots();
            dArceuusRCer.lastItemTime = System.currentTimeMillis();
            dArceuusRCer.currentLoc = "north";
            dArceuusRCer.playerPos = dArceuusRCer.northDenseRunestone;
        } else if (Player.within(dArceuusRCer.venerateAlterArea) || Player.within(dArceuusRCer.beforeObstacleInArea)) {
            Paint.setStatus("Walk back to mine");
            Logger.debugLog("Pathing back to the mine from the Venerate altar.");
            moveBackToMineFromVenerate();
            dArceuusRCer.lastEmptySlots = Inventory.emptySlots();
            dArceuusRCer.lastItemTime = System.currentTimeMillis();
            dArceuusRCer.currentLoc = "north";
            dArceuusRCer.playerPos = dArceuusRCer.northDenseRunestone;
        } else if (Player.within(dArceuusRCer.mineFailSafeArea1)) {
            Paint.setStatus("Mine FailSafe1");
            Walker.webWalk(dArceuusRCer.southDenseRunestone);
            Condition.sleep(dArceuusRCer.generateRandomDelay(3000, 5000));
            Walker.step(dArceuusRCer.southDenseRunestone);
            dArceuusRCer.playerPos = Walker.getPlayerPosition();
            dArceuusRCer.lastEmptySlots = Inventory.emptySlots();
            Client.tap(dArceuusRCer.tapSouthRuneStoneSOUTH);
            dArceuusRCer.lastItemTime = System.currentTimeMillis();
            dArceuusRCer.currentLoc = "south";
        } else if (Player.within(dArceuusRCer.mineFailSafeArea2)) {
            Paint.setStatus("Mine FailSafe2");
            Walker.webWalk(dArceuusRCer.southDenseRunestone);
            Condition.sleep(dArceuusRCer.generateRandomDelay(3000, 5000));
            Walker.step(dArceuusRCer.southDenseRunestone);
            dArceuusRCer.playerPos = Walker.getPlayerPosition();
            dArceuusRCer.lastEmptySlots = Inventory.emptySlots();
            Client.tap(dArceuusRCer.tapSouthRuneStoneSOUTH);
            dArceuusRCer.lastItemTime = System.currentTimeMillis();
            dArceuusRCer.currentLoc = "south";
        } else if (Player.within(dArceuusRCer.mineFailSafeArea3)) {
            Paint.setStatus("Mine FailSafe3");
            Walker.webWalk(dArceuusRCer.southDenseRunestone);
            Condition.sleep(dArceuusRCer.generateRandomDelay(3000, 5000));
            Walker.step(dArceuusRCer.southDenseRunestone);
            dArceuusRCer.playerPos = Walker.getPlayerPosition();
            dArceuusRCer.lastEmptySlots = Inventory.emptySlots();
            Client.tap(dArceuusRCer.tapSouthRuneStoneSOUTH);
            dArceuusRCer.lastItemTime = System.currentTimeMillis();
            dArceuusRCer.currentLoc = "south";
        }


        Paint.setStatus("Read XP");
        dArceuusRCer.readXP();

        // Update the statistics label
        dArceuusRCer.updateStatLabel();

        return false;
    }
    
    
    // BLOOD ALTAR BACK TO MINE AREA
    private void walkBloodToShortcut() {
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
            Logger.debugLog("Enabled run!");
        }
        Logger.debugLog("Now walking from Blood Altar to the agility shortcut.");
        Walker.walkPath(dArceuusRCer.bloodBackToObstaclePath);
        waitTillStopped(6);
    }

    private void traverseBloodShortcutIn() {

        Logger.debugLog("Crossing the rocks obstacle.");

        java.util.List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(365, 171, 179, 220), 3);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(foundPoints, true);
            Condition.wait(() -> Player.atTile(bloodRockObstacleSuccess), 250, 48);
            Condition.sleep(dArceuusRCer.generateRandomDelay(1750, 2250));

            if (!Player.atTile(bloodRockObstacleSuccess)) {
                Logger.debugLog("Looks like we failed the obstacle, what a shame... Last attempt now....");
                Walker.step(dArceuusRCer.obstacleBackToMineFromBloodInTile);
                waitTillStopped(6);

                java.util.List<Point> foundPoints2 = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(365, 171, 179, 220), 3);

                if (!foundPoints2.isEmpty()) {
                    Client.tap(foundPoints2, true);
                    Condition.wait(() -> Player.atTile(bloodRockObstacleSuccess), 250, 48);
                    Condition.sleep(dArceuusRCer.generateRandomDelay(1750, 2250));
                }
            }
        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(dArceuusRCer.obstacleBackToMineFromBloodInTile);
            waitTillStopped(6);

            if (!Player.atTile(dArceuusRCer.obstacleBackToMineFromBloodInTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(dArceuusRCer.obstacleBackToMineFromBloodInTile);
                waitTillStopped(6);

                if (!Player.atTile(dArceuusRCer.obstacleBackToMineFromBloodInTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(dArceuusRCer.obstacleBackToMineFromBloodInTile);
                    waitTillStopped(6);
                }
            }

            if (Player.atTile(dArceuusRCer.obstacleBackToMineFromBloodInTile)) {
                List<Point> foundPoints2 = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(365, 171, 179, 220), 3);

                if (!foundPoints2.isEmpty()) {
                    Client.tap(foundPoints2, true);
                    Condition.wait(() -> Player.atTile(bloodRockObstacleSuccess), 250, 48);
                    Condition.sleep(dArceuusRCer.generateRandomDelay(1750, 2250));
                }
            }
        }
    }
    // BLOOD ALTAR BACK TO MINE AREA

    // SOUL ALTAR BACK TO MINE AREA
    private void walkSoulToShortcut() {
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
            Logger.debugLog("Enabled run!");
        }
        Logger.debugLog("Now walking from Soul Altar to the agility shortcut.");
        Walker.walkPath(dArceuusRCer.soulBackToObstaclePath);
        waitTillStopped(8);
    }

    private void traverseSoulShortcutIn() {

        Logger.debugLog("Crossing the northern rocks obstacle.");

        List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(340, 196, 248, 208), 3);

        // Calculate the centroid of the points
        Point centroid = calculateCentroid(foundPoints);

        // Sort points by distance to the centroid
        foundPoints.sort(Comparator.comparingDouble(p -> distance(p, centroid)));

        // Select the top 4-5 most central points
        List<Point> mostCentralPoints = foundPoints.subList(0, Math.min(5, foundPoints.size()));

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(mostCentralPoints, false);
            waitTillStopped(8);

        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(dArceuusRCer.obstacleNorthBackFromSoulAltarTile);
            waitTillStopped(8);

            if (!Player.atTile(dArceuusRCer.obstacleNorthBackFromSoulAltarTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(dArceuusRCer.obstacleNorthBackFromSoulAltarTile);
                waitTillStopped(8);

                if (!Player.atTile(dArceuusRCer.obstacleNorthBackFromSoulAltarTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Client.tap(new Rectangle(442, 284, 13, 10));
                    waitTillStopped(3);

                    if (Player.atTile(soulRockObstacleSuccess)) {
                        Logger.debugLog("Successfully traversed the obstacle this time!");
                        return;
                    }
                }
            }

            if (Player.atTile(dArceuusRCer.obstacleNorthBackFromSoulAltarTile)) {
                Client.tap(new Rectangle(442, 284, 13, 10));
                waitTillStopped(8);

                if (!Player.atTile(soulRockObstacleSuccess)) {
                    Logger.debugLog("Looks like we failed the obstacle, what a shame... Retrying!");
                    Walker.step(dArceuusRCer.obstacleNorthBackFromSoulAltarTile);
                    waitTillStopped(8);

                    Client.tap(new Rectangle(442, 284, 13, 10));
                    waitTillStopped(8);
                    if (!Player.atTile(soulRockObstacleSuccess)) {
                        Logger.debugLog("Looks like we failed the obstacle again, what a shame... Retrying!");
                        Walker.step(dArceuusRCer.obstacleNorthBackFromSoulAltarTile);
                        waitTillStopped(8);

                        Client.tap(new Rectangle(442, 284, 13, 10));
                        waitTillStopped(8);

                        if (!Player.atTile(soulRockObstacleSuccess)) {
                            Logger.debugLog("Seems like we failed to use the soul obstacle multiple times, we'll walk back via the venerate altar instead.");
                            Walker.walkPath(dArceuusRCer.soulObstacleBackViaVenerate);
                            waitTillStopped(4);
                            Walker.step(dArceuusRCer.obstacleOutsideTile);
                            waitTillStopped(4);
                        }
                    }
                }
            } else {
                Logger.debugLog("Seems like we failed to use the soul obstacle, we'll walk back via the venerate altar instead.");
                Walker.walkPath(dArceuusRCer.soulObstacleBackViaVenerate);
                waitTillStopped(4);
                Walker.step(dArceuusRCer.obstacleOutsideTile);
                waitTillStopped(4);
            }
        }
    }

    private void traverseSoulShortcutIn2() {
        Logger.debugLog("Crossing the rocks obstacle.");

        List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(59, 290, 232, 191), 5);
        List<Point> foundPoints2 = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(384, 213, 211, 169), 3);

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(foundPoints, true);
            waitTillStopped(5);
        } else if (!foundPoints2.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(foundPoints2, true);
            waitTillStopped(5);
        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(dArceuusRCer.obstacleOutsideTile);
            waitTillStopped(4);

            if (!Player.atTile(dArceuusRCer.obstacleOutsideTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(dArceuusRCer.obstacleOutsideTile);
                waitTillStopped(4);

                if (!Player.atTile(dArceuusRCer.obstacleOutsideTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(dArceuusRCer.obstacleOutsideTile);
                    waitTillStopped(4);
                }
            }

            if (Player.atTile(dArceuusRCer.obstacleOutsideTile)) {
                List<Point> foundPoints3 = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(337, 122, 275, 241), 3);

                if (!foundPoints3.isEmpty()) {
                    Logger.debugLog("Located the obstacle using the color finder, tapping.");
                    Client.tap(foundPoints3, true);
                    waitTillStopped(5);
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
        dArceuusRCer.currentLoc = "north";
        dArceuusRCer.lastEmptySlots = Inventory.emptySlots();
        waitTillStopped(6);
    }
    // SOUL ALTAR BACK TO MINE AREA

    // MOVE BACK FROM VENERATE ALTAR AREA
    private void moveBackToMineFromVenerate() {

        dArceuusRCer.essenceCount = Inventory.count(13446, 0.95);

        if (dArceuusRCer.essenceCachedLoc == null) {
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            dArceuusRCer.essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + dArceuusRCer.essenceCachedLoc);
        }

        walkVenerateToShortcut();
        traverseShortcutIn();

        // Check if we are still at the outside part of the obstacle (in case we had our chisel selected for example)
        if (Player.atTile(dArceuusRCer.obstacleOutsideTile)) {
            traverseShortcutIn();
        }

        // Instantly tap the north RuneStone
        Client.tap(new Rectangle(497, 515, 33, 18));

        // Check if we have essence blocks left to process
        while (Inventory.contains(13446, 0.95)){
            processEssence();
        }

        Condition.wait(() -> Player.atTile(dArceuusRCer.northDenseRunestone), 200, 40);
        dArceuusRCer.currentLoc = "north";
        dArceuusRCer.lastEmptySlots = Inventory.emptySlots();
        Client.tap(dArceuusRCer.tapNorthRuneStoneNORTH);
    }

    private void walkVenerateToShortcut() {
        Logger.debugLog("Walking towards to agility shortcut, while cutting our essence blocks");
        Walker.walkPath(dArceuusRCer.venerateBackToObstaclePath, this::processEssence);
        waitTillStopped(6);
    }

    private void traverseShortcutIn() {
        Logger.debugLog("Crossing the rocks obstacle.");

        List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(384, 213, 211, 169), 3);

        // Calculate the centroid of the points
        Point centroid = calculateCentroid(foundPoints);

        // Sort points by distance to the centroid
        foundPoints.sort(Comparator.comparingDouble(p -> distance(p, centroid)));

        // Select the top 4-5 most central points
        List<Point> mostCentralPoints = foundPoints.subList(0, Math.min(5, foundPoints.size()));

        if (!foundPoints.isEmpty()) {
            Logger.debugLog("Located the obstacle using the color finder, tapping.");
            Client.tap(mostCentralPoints, false);
            waitTillStopped(8);
            Condition.sleep(dArceuusRCer.generateRandomDelay(1750, 2250));
        } else {
            Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

            Walker.step(dArceuusRCer.obstacleOutsideTile);
            waitTillStopped(6);

            if (!Player.atTile(dArceuusRCer.obstacleOutsideTile)) {
                Logger.debugLog("Failed to move to specific tile, retrying...");
                Walker.step(dArceuusRCer.obstacleOutsideTile);
                waitTillStopped(6);

                if (!Player.atTile(dArceuusRCer.obstacleOutsideTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(dArceuusRCer.obstacleOutsideTile);
                    waitTillStopped(6);
                }
            }

            if (Player.atTile(dArceuusRCer.obstacleOutsideTile)) {
                List<Point> foundPoints2 = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(337, 122, 275, 241), 3);

                if (!foundPoints2.isEmpty()) {
                    Logger.debugLog("Located the obstacle using the color finder, tapping.");
                    Client.tap(foundPoints2, true);
                    waitTillStopped(6);
                    Condition.sleep(dArceuusRCer.generateRandomDelay(1750, 2250));
                }
            }
        }
    }
    // MOVE BACK FROM VENERATE ALTAR AREA
    
    // HELPER UTILS
    private void processEssence() {
        // Update essenceToProcess only if it is currently 0
        if (dArceuusRCer.essenceToProcess == 0) {
            dArceuusRCer.essenceToProcess = Inventory.count(13446, 0.95);
            Logger.debugLog("Essence to process: " + dArceuusRCer.essenceToProcess);
        }

        // Process a block in the inventory if we still have them (and they are dark)
        if (!(dArceuusRCer.essenceToProcess == 0) && doneVenerating()) {
            Inventory.tapItem(1755, true, 0.80);
            dArceuusRCer.generateRandomDelay(100, 150);
            Client.tap(dArceuusRCer.essenceCachedLoc);

            // Decrease the essenceToProcess count by 1 after the actions
            dArceuusRCer.essenceToProcess--;
        }
    }
    private void waitTillStopped(int waitTimes) {
        // Wait till we stop moving
        Tile lastPosition = Walker.getPlayerPosition();
        dArceuusRCer.playerPos = lastPosition;
        int unchangedCount = 0; // Counter for how many times the position has remained the same
        boolean runEnabled = Player.isRunEnabled();

        Logger.debugLog("Waiting for us to stop moving...");
        while (unchangedCount < waitTimes) { // Loop until the position hasn't changed for the specified number of checks
            Tile currentPosition = Walker.getPlayerPosition();
            dArceuusRCer.playerPos = lastPosition;

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

    private boolean doneVenerating() {

        // Check if we have a cached known location for the last essence spot already, if not cache it
        if (dArceuusRCer.essenceCachedLoc == null) {
            Logger.debugLog("Last inventory spot for dark essence has not yet been cached, caching now!");
            dArceuusRCer.essenceCachedLoc = Inventory.lastItemPosition(13446, 0.95);
            Logger.debugLog("Last inventory spot for dark essence is now cached at: " + dArceuusRCer.essenceCachedLoc);
        }

        // Calculate the center of the rectangle
        int centerX = dArceuusRCer.essenceCachedLoc.x + dArceuusRCer.essenceCachedLoc.width / 2;
        int centerY = dArceuusRCer.essenceCachedLoc.y + dArceuusRCer.essenceCachedLoc.height / 2;

        // Define the 5x5 area around the center point
        java.awt.Rectangle smallRect = new java.awt.Rectangle(centerX - 2, centerY - 2, 5, 5);

        // Check if the essence is dark instead of dense to verify if we have venerated correctly
        return Client.isColorInRect(Color.decode("#675b4e"), smallRect, 10);
    }

    private static Point calculateCentroid(List<Point> points) {
        int sumX = 0, sumY = 0;
        for (Point p : points) {
            sumX += p.x;
            sumY += p.y;
        }
        return new Point(sumX / points.size(), sumY / points.size());
    }

    private static double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
}