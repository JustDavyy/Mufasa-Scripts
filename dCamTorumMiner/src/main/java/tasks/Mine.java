package tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static helpers.Interfaces.*;
import static main.dCamTorumMiner.*;

public class Mine extends Task {

    Area eastArea = new Area(
            new Tile(6078, 37903, 1),
            new Tile(6094, 37926, 1)
    );

    Area northArea = new Area(
            new Tile(6048, 37921, 1),
            new Tile(6077, 37938, 1)
    );

    Area mineArea = new Area(
            new Tile(6043, 37889, 1),
            new Tile(6098, 37943, 1)
    );

    Tile currentPosition;
    Tile northTile = new Tile(6059, 37929, 1);
    Tile eastTile = new Tile(6079, 37917, 1);

    Rectangle checkAreaEast = new Rectangle(477, 102, 68, 276);
    Rectangle checkAreaNorth = new Rectangle(215, 142, 412, 105);
    Point centerPoint = new Point(441, 260);

    java.util.List<Color> veinColors = Arrays.asList(
            Color.decode("#5e5040"),
            Color.decode("#3b3429"),
            Color.decode("#464e55"),
            Color.decode("#525860")
    );

    @Override
    public boolean activate() {
        return isIdle() && !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Pixelshift: " + Player.currentPixelShift());
        mine();
        return false;
    }

    private void mine() {
        List<Point> foundPoints = new ArrayList<>();

        if (Player.within(northArea)) {
            foundPoints = Client.getPointsFromColorsInRect(veinColors, checkAreaNorth, 5);

            if (foundPoints.isEmpty()) {
                Logger.debugLog("No points found at the north area, moving to east area.");
                Walker.step(eastTile);
                lastAction = System.currentTimeMillis();
            }
        } else if (Player.within(eastArea)) {
            foundPoints = Client.getPointsFromColorsInRect(veinColors, checkAreaEast, 5);

            if (foundPoints.isEmpty()) {
                Logger.debugLog("No points found at the east area, moving to north area.");
                Walker.step(eastTile);
                lastAction = System.currentTimeMillis();
            }
        } else {
            Logger.debugLog("Not at one of the two walls at the north east mining spot. Moving there!");
            if (Walker.isReachable(northTile)) {
                Walker.step(northTile);
                lastAction = System.currentTimeMillis();
            } else {
                Walker.webWalk(northTile);
                lastAction = System.currentTimeMillis();
            }
        }

        if (!foundPoints.isEmpty()) {
            Point clusterCenter = findNearestClusterCenter(foundPoints, centerPoint);
            if (clusterCenter != null) {
                Client.tap(clusterCenter);
                lastAction = System.currentTimeMillis();

                // Wait atleast 5 seconds before we continue
                Condition.sleep(5000);
            }
        }
    }

    private boolean isIdle() {
        boolean idleYN = Player.isIdle();
        Logger.debugLog("Player idle is: " + idleYN);

        if (!idleYN) {
            updatePaintBar();
            Condition.sleep(2000);
        } else {
            Logger.log("We're idle, starting new mining action.");
        }

        return idleYN;
    }

    private Point findNearestClusterCenter(List<Point> points, Point center) {
        List<List<Point>> clusters = groupPointsIntoClusters(points);

        // Calculate the center of each cluster
        Point nearestClusterCenter = null;
        double minDistance = Double.MAX_VALUE;

        for (List<Point> cluster : clusters) {
            Point clusterCenter = calculateCentroid(cluster);
            double distance = clusterCenter.distance(center);

            if (distance < minDistance) {
                minDistance = distance;
                nearestClusterCenter = clusterCenter;
            }
        }

        return nearestClusterCenter;
    }

    private List<List<Point>> groupPointsIntoClusters(List<Point> points) {
        List<List<Point>> clusters = new ArrayList<>();
        double clusteringThreshold = 10.0;

        for (Point point : points) {
            boolean addedToCluster = false;

            for (List<Point> cluster : clusters) {
                for (Point clusteredPoint : cluster) {
                    if (point.distance(clusteredPoint) <= clusteringThreshold) {
                        cluster.add(point);
                        addedToCluster = true;
                        break;
                    }
                }
                if (addedToCluster) break;
            }

            if (!addedToCluster) {
                List<Point> newCluster = new ArrayList<>();
                newCluster.add(point);
                clusters.add(newCluster);
            }
        }

        return clusters;
    }

    private Point calculateCentroid(List<Point> points) {
        int sumX = 0, sumY = 0;
        for (Point p : points) {
            sumX += p.x;
            sumY += p.y;
        }
        return new Point(sumX / points.size(), sumY / points.size());
    }

    public static void updatePaintBar() {
        // Calculations for the statistics label
        long currentTime = System.currentTimeMillis();
        currentStack = Inventory.stackSize(ItemList.BLESSED_BONE_SHARDS_29381);
        earnedStack = currentStack - startShardCount;
        double elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);
        double shardsPerHour = earnedStack / elapsedTimeInHours;

        // Calculate the total of prayer XP gained
        double totalXP = earnedStack * 5;
        double prayXPPerHour = totalXP / elapsedTimeInHours;

        // Format shards per hour with dot as thousand separator and no decimals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(','); // Set the decimal separator to comma
        DecimalFormat shardsFormat = new DecimalFormat("#,###", symbols);
        String shardsPerHourFormatted = shardsFormat.format(shardsPerHour);

        // Format prayer xp per hour as 'k' with two decimals, dot as thousand separator, and comma as decimal separator
        DecimalFormat prayerXPFormat = new DecimalFormat("#,##0.00k", symbols);
        String prayerXPPerHourFormatted = prayerXPFormat.format(prayXPPerHour / 1000);

        // Update the statistics label
        String statistics = String.format("Shards/hr: %s | PrayXP/hr: %s", shardsPerHourFormatted, prayerXPPerHourFormatted);
        Paint.setStatistic(statistics);

        Paint.updateBox(shardIndex, earnedStack);
        Condition.sleep(250);
        Paint.updateBox(prayIndex, (int) totalXP);
    }
}