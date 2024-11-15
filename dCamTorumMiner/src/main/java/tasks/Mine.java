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
                Walker.step(northTile);
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
            Point targetPoint = selectTargetPoint(foundPoints);
            if (targetPoint != null) {
                Client.tap(targetPoint);
                lastAction = System.currentTimeMillis();

                // Wait at least 5 seconds before we continue
                Condition.sleep(5000);
            }
        }
    }

    /**
     * Selects a target point based on the given requirements:
     * - If more than 15 points are found, pick one of the closest 15 to (447, 270).
     * - If 15 or fewer points are found, pick a random one.
     *
     * @param points List of points found.
     * @return The selected target point, or null if no points are available.
     */
    private Point selectTargetPoint(List<Point> points) {
        Point referencePoint = new Point(447, 270);

        if (points.size() > 15) {
            // Sort points by distance to (447, 270)
            points.sort((p1, p2) -> Double.compare(p1.distance(referencePoint), p2.distance(referencePoint)));

            // Keep only the closest 15 points
            points = points.subList(0, 15);

            // Pick one of these 15 randomly
            return points.get((int) (Math.random() * points.size()));
        } else if (!points.isEmpty()) {
            // Pick a random point from the available points
            return points.get((int) (Math.random() * points.size()));
        }

        return null; // No points available
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