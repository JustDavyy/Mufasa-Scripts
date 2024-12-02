package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static helpers.Interfaces.*;
import static main.dKarambwanjiFisher.*;

public class Update extends Task {

    private static long lastUpdateTimestamp = 0;

    @Override
    public boolean activate() {
        return !shouldFish();
    }

    @Override
    public boolean execute() {

        updatePaintBar();

        return false;
    }


    private void updatePaintBar() {
        long currentTimeMillis = System.currentTimeMillis();

        // Check if 10 seconds (10,000 milliseconds) have passed since the last update
        if (currentTimeMillis - lastUpdateTimestamp < 10_000) {
            return; // Skip updating if 10 seconds haven't passed
        }

        Paint.setStatus("Update paint count");
        Logger.debugLog("Updating paint counts");

        // Update the last update time
        lastUpdateTimestamp = currentTimeMillis;

        PROCESS_COUNT = Inventory.stackSize(ItemList.RAW_KARAMBWANJI_3150) - karambwanjiStartCount;

        Paint.updateBox(productIndex, PROCESS_COUNT);

        // Time calculations
        currentTime = System.currentTimeMillis();
        elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);
        itemsPerHour = PROCESS_COUNT / elapsedTimeInHours;

        // Format items per hour with dot as a thousand separator and no decimals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(','); // Set the decimal separator to comma
        DecimalFormat formatItems = new DecimalFormat("#,###", symbols);
        String itemsPerHourFormatted = formatItems.format(itemsPerHour);

        // Update the statistics label
        String statistics = String.format("Karambwanji/hr: %s", itemsPerHourFormatted);
        Paint.setStatistic(statistics);
    }

}