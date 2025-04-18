package tasks;

import utils.Task;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static helpers.Interfaces.*;
import static main.dLumbridgeFisher.*;

public class Update extends Task {

    private static long lastUpdateTimestamp = 0;

    @Override
    public boolean activate() {
        return !shouldFish() && !Inventory.isFull();
    }

    @Override
    public boolean execute() {

        updatePaintBar();

        return false;
    }


    private void updatePaintBar() {
        long currentTimeMillis = System.currentTimeMillis();

        // Check if 30 seconds (10,000 milliseconds) have passed since the last update
        if (currentTimeMillis - lastUpdateTimestamp < 30_000) {
            return; // Skip updating if 30 seconds haven't passed
        }

        Paint.setStatus("Update paint count");
        Logger.debugLog("Updating paint counts");

        Paint.updateBox(rawShrimpIndex, shrimpGainedCount);
        Condition.sleep(125, 250);
        Paint.updateBox(rawAnchoviesIndex, anchoviesGainedCount);
        Condition.sleep(125, 250);
        Paint.updateBox(cookedShrimpIndex, shrimpCookedCount);
        Condition.sleep(125, 250);
        Paint.updateBox(cookedAnchoviesIndex, anchoviesCookedCount);
        Condition.sleep(125, 250);
        Paint.updateBox(burnedFishIndex, burnedFishCount);
        Condition.sleep(125, 250);

        // Time calculations
        currentTime = System.currentTimeMillis();
        elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);
        double fishPerHour = (shrimpGainedCount + anchoviesGainedCount) / elapsedTimeInHours;
        double cookPerHour = (shrimpCookedCount + anchoviesCookedCount + burnedFishCount) / elapsedTimeInHours;


        // Format items per hour with dot as a thousand separator and no decimals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(','); // Set the decimal separator to comma
        DecimalFormat formatItems = new DecimalFormat("#,###", symbols);
        String fishedPerHourFormatted = formatItems.format(fishPerHour);
        String cookedPerHourFormatted = formatItems.format(cookPerHour);

        // Update the statistics label
        String statistics = String.format("Fished/hr: %s | Cooked/hr: %s", fishedPerHourFormatted, cookedPerHourFormatted);
        Paint.setStatistic(statistics);

        // Update the last update time
        lastUpdateTimestamp = System.currentTimeMillis();
    }

}