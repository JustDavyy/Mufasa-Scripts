package Tasks;

import helpers.utils.Tile;
import utils.Task;

import static helpers.Interfaces.*;

import main.dArceuusRCer;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class moveToVenerate extends Task {

    Tile obstacleOutSuccess = new Tile(7043, 15245, 0);

    @Override
    public boolean activate() {
        return (Player.within(dArceuusRCer.miningArea) || Player.within(dArceuusRCer.beforeObstacleOutArea) || Player.within(dArceuusRCer.veneratePathArea)) && Inventory.isFull() && !doneVenerating();
    }

    @Override
    public boolean execute() {
        // Check if we have to hop first
        Paint.setStatus("Check hop timer");
        dArceuusRCer.hopActions();

        Paint.setStatus("Move to venerate altar");
        Logger.log("Executing moveToVenerate!");
        Logger.debugLog("Pathing to the Venerate altar.");
        if (!Player.within(dArceuusRCer.veneratePathArea)) {
            if (!Player.isRunEnabled()) {
                Player.toggleRun();
                Logger.debugLog("Enabled run!");
            }
            Logger.debugLog("Walking towards the agility shortcut.");
            Walker.walkPath(dArceuusRCer.mineToShortcutOutPath);
            waitTillStopped(6);

            Logger.debugLog("Crossing the rocks obstacle.");

            java.util.List<Point> foundPoints = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(382, 179, 139, 116), 5);

            if (!foundPoints.isEmpty()) {
                // Calculate the centroid only if there are points
                Point centroid = calculateCentroid(foundPoints);

                // Sort points by distance to the centroid
                foundPoints.sort(Comparator.comparingDouble(p -> distance(p, centroid)));

                // Select the top 4-5 most central points
                List<Point> mostCentralPoints = foundPoints.subList(0, Math.min(5, foundPoints.size()));

                Logger.debugLog("Located the obstacle using the color finder, tapping.");
                Client.tap(mostCentralPoints, false);
                Condition.wait(() -> Player.atTile(obstacleOutSuccess), 250, 25);
                waitTillStopped(7);
            } else {
                Logger.debugLog("Couldn't locate the obstacle with the color finder, using fallback method.");

                Walker.step(dArceuusRCer.obstacleInsideTile);
                waitTillStopped(4);

                if (!Player.atTile(dArceuusRCer.obstacleInsideTile)) {
                    Logger.debugLog("Failed to move to specific tile, retrying...");
                    Walker.step(dArceuusRCer.obstacleInsideTile);
                    waitTillStopped(4);

                    if (!Player.atTile(dArceuusRCer.obstacleInsideTile)) {
                        Logger.debugLog("Failed to move to specific tile, retrying...");
                        Walker.step(dArceuusRCer.obstacleInsideTile);
                        waitTillStopped(4);
                    }
                }

                if (Player.atTile(dArceuusRCer.obstacleInsideTile)) {
                    List<Point> foundPoints2 = Client.getPointsFromColorsInRect(dArceuusRCer.obstacleColors, new Rectangle(382, 179, 139, 116), 5);

                    if (!foundPoints2.isEmpty()) {
                        // Calculate the centroid only if there are points
                        Point centroid2 = calculateCentroid(foundPoints2);

                        // Sort points by distance to the centroid
                        foundPoints2.sort(Comparator.comparingDouble(p -> distance(p, centroid2)));

                        // Select the top 4-5 most central points
                        List<Point> mostCentralPoints2 = foundPoints2.subList(0, Math.min(5, foundPoints2.size()));

                        Client.tap(mostCentralPoints2, false);
                        Condition.wait(() -> Player.atTile(obstacleOutSuccess), 250, 25);
                        waitTillStopped(7);
                    } else {
                        Logger.debugLog("Couldn't locate the obstacle with the color finder, fallback also failed.");
                    }
                }
            }
        }

        Logger.debugLog("Moving from shortcut to Venerate Altar.");
        Walker.walkPath(dArceuusRCer.shortcutOutToAltarPath);
        waitTillStopped(6);
        Logger.debugLog("Player is now located at the Venerate Altar.");


        Paint.setStatus("Read XP");
        dArceuusRCer.readXP();

        // Update the statistics label
        dArceuusRCer.updateStatLabel();

        return false;
    }


    private void waitTillStopped(int waitTimes) {
        Condition.sleep(400);
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