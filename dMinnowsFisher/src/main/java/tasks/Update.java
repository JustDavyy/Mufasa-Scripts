package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static helpers.Interfaces.*;
import static main.dMinnowsFisher.*;

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

        PROCESS_COUNT = Inventory.stackSize(10570) - minnowStartCount;
        PROCESS_COUNT2 = PROCESS_COUNT / 40;

        Paint.updateBox(minnowIndex, PROCESS_COUNT);
        Condition.sleep(125, 250);
        Paint.updateBox(sharkIndex, PROCESS_COUNT2);
        Condition.sleep(125, 250);
        Paint.updateBox(profitIndex, (PROCESS_COUNT2 * sharkPrice));

        // Time calculations
        currentTime = System.currentTimeMillis();
        elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);
        minnowsPerHour = PROCESS_COUNT / elapsedTimeInHours;
        sharksPerHour = PROCESS_COUNT2 / elapsedTimeInHours;


        // Format items per hour with dot as a thousand separator and no decimals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(','); // Set the decimal separator to comma
        DecimalFormat formatItems = new DecimalFormat("#,###", symbols);
        String minnowsPerHourFormatted = formatItems.format(minnowsPerHour);
        String sharksPerHourFormatted = formatItems.format(sharksPerHour);

        // Update the statistics label
        String statistics = String.format("Minnows/hr: %s | Sharks/hr: %s", minnowsPerHourFormatted, sharksPerHourFormatted);
        Paint.setStatistic(statistics);
    }

}