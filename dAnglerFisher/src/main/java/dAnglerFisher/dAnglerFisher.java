package dAnglerFisher;


import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import dAnglerFisher.Tasks.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dAnglerFisher",
        description = "Fishes Anglerfish at Port Piscarilius",
        version = "1.43",
        categories = {ScriptCategory.Fishing},
        guideLink = "https://wiki.mufasaclient.com/docs/danglerfisher/"
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings? WDH is disabled for this script, as there's users on every world.",
                        defaultValue = "1",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name = "Run anti-ban",
                        description = "Would you like to run anti-ban features?",
                        defaultValue = "true",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name = "Run extended anti-ban",
                        description = "Would you like to run additional, extended anti-ban like some additional AFK patterns, on TOP of the regular anti-ban?",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class dAnglerFisher extends AbstractScript {
    private final ArrayList<Task> taskList = new ArrayList<>();
    String hopProfile;
    Boolean hopEnabled;
    private boolean antiBan;
    private boolean extendedAntiBan;
    public static int previousXP;
    public static int newXP;
    public static long startTime;
    public static Instant lastXpGainTime = Instant.now().minusSeconds(90);
    public static Instant lastActionTime = Instant.now();
    public static Random random = new Random();
    public static Tile bankTile = new Tile(7215, 14909, 0);
    public static Rectangle bankClickRect = new Rectangle(437, 249, 13, 10);
    public static Area fishingArea = new Area(
            new Tile(7264, 14807, 0),
            new Tile(7363, 14868, 0)
    );
    public static Area upperFishSpotArea = new Area(
            new Tile(7337, 14830, 0),
            new Tile(7366, 14880, 0)
    );
    public static Area portPiscArea = new Area(
            new Tile(7149, 14809, 0),
            new Tile(7419, 14967, 0)
    );

    // Walker stuff
    public static Tile[] pathToSpots = new Tile[]{
            new Tile(7223, 14874, 0),
            new Tile(7242, 14857, 0),
            new Tile(7272, 14852, 0),
            new Tile(7298, 14849, 0),
            new Tile(7320, 14835, 0)
    };

    // Paint stuff
    public static int anglerIndex;
    public static int profitIndex;
    public static int anglerAmount = 0;
    public static int profitAmount = 0;
    public static int anglerPrice = 0;
    public static int anglerInventCount = 0;

    @Override
    public void onStart(){
        Logger.log("Initialising dAnglerFisher...");

        // Build task list
        taskList.add(new Fish(this));
        taskList.add(new Bank(this));
        taskList.add(new AntiAFK(this));

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"28-59"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Grab script config stuff
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        antiBan = Boolean.valueOf(configs.get("Run anti-ban"));
        extendedAntiBan = Boolean.valueOf(configs.get("Run extended anti-ban"));

        if (!Player.within(portPiscArea)){
            Logger.log("Not at fishing area, stopping script.");
            Logout.logout();
            Script.stop();
        }

        if (antiBan) {
            Logger.debugLog("Initializing anti-ban timer");
            Game.antiBan();
            if (extendedAntiBan) {
                Logger.debugLog("Initializing extended anti-ban timer");
                Game.enableOptionalAntiBan(AntiBan.EXTENDED_AFK);
                Game.antiBan();
            }
        }

        checkEquipment();

        // Set zoom
        GameTabs.openSettingsTab();
        Condition.sleep(generateRandomDelay(500, 750));
        Game.setZoom("1");

        Chatbox.closeChatbox();
        GameTabs.openInventoryTab();

        // Paint stuff here
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create all image boxes with a 500ms delay between each one
        anglerIndex = Paint.createBox("Raw Anglerfish", ItemList.RAW_ANGLERFISH_13439, anglerAmount);
        Condition.sleep(500);
        profitIndex = Paint.createBox("Profit", ItemList.MOUNTED_COINS_20631, profitAmount);
        Condition.sleep(500);

        Paint.setStatus("Initializing...");

        anglerInventCount = Inventory.count(ItemList.RAW_ANGLERFISH_13439, 0.8);
        anglerPrice = GrandExchange.getItemPrice(ItemList.RAW_ANGLERFISH_13439);

        if (!Player.within(fishingArea) && !Inventory.isFull()) {
            Paint.setStatus("Move to the fishing spots");
            moveToSpots();
        }

        startTime = System.currentTimeMillis();
        updateStatLabel();
        Logger.log("Starting dAnglerFisher!");
    }

    @Override
    public void poll() {
        // Looped tasks go here.
        hopActions();
        readXP();
        if (antiBan) {
            Game.antiBan();
        }
        for (Task task : taskList) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    public static void checkEquipment() {
        Logger.debugLog("Checking inventory has rod & worms.");
        if(!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }
        if (Inventory.stackSize(ItemList.SANDWORMS_13431) < 50){
            Logger.log("Less than 50 sandworms, stopping script.");
            Logout.logout();
            Script.stop();
        }
        if (!Inventory.contains(ItemList.FISHING_ROD_307, 0.8)){
            Logger.log("No fishing rod, stopping script.");
            Logout.logout();
            Script.stop();
        }
        Logger.debugLog("Amount of sandworms: " + Inventory.stackSize(ItemList.SANDWORMS_13431));
        Logger.debugLog("Inventory has required equipment, continuing..");
    }
    public static boolean shouldFish() {
        long timeSinceLastXpGain = Duration.between(lastXpGainTime, Instant.now()).getSeconds();
        return timeSinceLastXpGain >= 90;
    }
    public static void readXP() {
        newXP = XpBar.getXP();

        if (previousXP == 0 || previousXP == -1) {
            // If previousXP is null, initialize it with the current XP
            previousXP = newXP;
        } else if (previousXP != newXP) {
            // If previousXP and newXP are different, update lastXpGainTime and set previousXP to newXP
            lastXpGainTime = Instant.now();
            previousXP = newXP;
        }
    }
    public void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, false, false);
        }
    }

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        int delay = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        return delay;
    }

    private void moveToSpots() {
        Walker.walkPath(pathToSpots);
        Condition.sleep(dAnglerFisher.generateRandomDelay(2000, 3000));
    }

    public static void updatePaint() {
        // Current count of anglerfish in inventory
        int currentCount = Inventory.count(ItemList.RAW_ANGLERFISH_13439, 0.8);

        // Calculate the difference between the current count and the last recorded count
        int difference = currentCount - dAnglerFisher.anglerInventCount;

        // Only update if there is a positive difference
        if (difference > 0) {
            Logger.debugLog("Difference in Anglerfish count: " + difference);

            // Update the last count to the new current count
            dAnglerFisher.anglerInventCount = currentCount;
            dAnglerFisher.anglerAmount += difference;
            dAnglerFisher.profitAmount = dAnglerFisher.anglerAmount * dAnglerFisher.anglerPrice;
            Paint.updateBox(dAnglerFisher.anglerIndex, dAnglerFisher.anglerAmount);
            Paint.updateBox(dAnglerFisher.profitIndex, dAnglerFisher.profitAmount);
        }
    }

    public static void updateStatLabel() {
        // Calculations for the statistics label
        long currentTime = System.currentTimeMillis();
        double elapsedTimeInHours = (currentTime - dAnglerFisher.startTime) / (1000.0 * 60 * 60);
        double anglersPerHour = anglerAmount / elapsedTimeInHours;

        // Calculate the total profit subtracting the cost of the blood essences used
        double totalProfit = (anglerAmount * anglerPrice);
        double profitPerHour = totalProfit / elapsedTimeInHours;

        // Format runes per hour with dot as thousand separator and no decimals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(','); // Set the decimal separator to comma
        DecimalFormat runesFormat = new DecimalFormat("#,###", symbols);
        String runesPerHourFormatted = runesFormat.format(anglersPerHour);

        // Format profit per hour as 'k' with two decimals, dot as thousand separator, and comma as decimal separator
        DecimalFormat profitFormat = new DecimalFormat("#,##0.00k", symbols);
        String profitPerHourFormatted = profitFormat.format(profitPerHour / 1000);

        // Update the statistics label
        String statistics = String.format("Anglers/hr: %s | Profit/hr: %s", runesPerHourFormatted, profitPerHourFormatted);
        Paint.setStatistic(statistics);
    }
}