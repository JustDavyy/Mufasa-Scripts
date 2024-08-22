import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.ItemList;
import helpers.utils.OptionType;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dNestOpener",
        description = "Opens bird nests (both seeds and rings). Supports dynamic banking.",
        version = "1.2",
        guideLink = "https://wiki.mufasaclient.com/docs/dnestopener/",
        categories = {ScriptCategory.Herblore}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Bank Tab",
                        description = "What bank tab are your bird nests located in?",
                        defaultValue = "0",
                        optionType = OptionType.BANKTABS
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dNestOpener extends AbstractScript {
    // Creating the strings for later use
    private static final Random random = new Random();
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String bankloc;
    int banktab;
    private static final int birdnestRing = ItemList.BIRD_NEST_5074;
    private static final int birdnestSeed = ItemList.BIRD_NEST_5073;
    private static final int birdnestEmpty = ItemList.BIRD_NEST_5075;
    private static int seedNestIndex;
    private static int ringNestIndex;
    private static int profitIndex;
    private static int profitHourlyIndex;
    private static int processSeedNestCount = 0;
    private static int processRingNestCount = 0;
    private static int inventCount = 0;
    private static int profit = 0;
    private static int clawPrice = 0;
    private static int skinPrice = 0;
    private static long startTime;
    private static boolean firstTimeSeedNest = false;
    private static boolean firstTimeRingNest = false;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        Logger.log("Thank you for using the dNestOpener script!\nSetting up everything for your gains now...");

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Set the header of paintUI.
        Paint.setStatus("Initializing...");

        // Create a single image box, to show the amount of processed bows
        Condition.sleep(500);
        seedNestIndex = Paint.createBox("Seed nest", birdnestSeed, processSeedNestCount);
        Condition.sleep(500);
        ringNestIndex = Paint.createBox("Ring nest", birdnestRing, processRingNestCount);
        Condition.sleep(500);
        profitIndex = Paint.createBox("Est. Profit GP", ItemList.STONKS_99960, profit);
        Condition.sleep(500);
        profitHourlyIndex = Paint.createBox("Est. Profit HR", ItemList.STONKS_99960, profit);

        hopActions();
        setupBanking();
        initialSetup();

        startTime = System.currentTimeMillis();

        // Update the statistics label
        updateStatLabel();

        // Get claw and skin prices
        clawPrice = GrandExchange.getItemPrice(7416);
        skinPrice = GrandExchange.getItemPrice(7418);

        Paint.setStatus("End of onStart");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        checkInventOpen();
        checkInventNests();
        executeEmptyNests();
        bank();
        hopActions();

    }

    private void setupBanking() {
        Logger.debugLog("Starting setupBanking() method.");
        Paint.setStatus("Setting up banking");
        if (bankloc == null) {
            Logger.debugLog("Starting dynamic banking setup...");

            // Opening the inventory if not yet opened.
            Logger.debugLog("Opening up the inventory.");
            if (!GameTabs.isInventoryTabOpen()) {
                Paint.setStatus("Opening inventory");
                GameTabs.openInventoryTab();
            }

            Logger.debugLog("Starting setup for Dynamic Banking.");
            Paint.setStatus("Setting up dynamic bank");
            bankloc = Bank.setupDynamicBank();
            Logger.debugLog("We're located at: " + bankloc + ".");
            if (bankloc == null) {
                Logger.debugLog("Could not find a dynamic bank location we are in, logging out and aborting script.");
                Logout.logout();
                Script.stop();
            }
            Condition.sleep(generateRandomDelay(4500, 6000));
            Logger.debugLog("Attempting to open the Bank of Gielinor.");
            Bank.open(bankloc);
            Logger.debugLog("Bank interface detected!");
            if (Bank.isBankPinNeeded()) {
                Paint.setStatus("Entering bank pin");
                Logger.debugLog("Bank pin is needed!");
                Bank.enterBankPin();
                Condition.sleep(500);
                Condition.wait(() -> Bank.isOpen(), 200, 12);
                Logger.debugLog("Bank pin entered.");
                Logger.debugLog("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(generateRandomDelay(400, 600));
            } else {
                Paint.setStatus("Deposit inventory");
                Logger.debugLog("Bank pin is not needed, bank is open!");
                Logger.debugLog("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(generateRandomDelay(400, 600));
            }
        }
        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void initialSetup() {
        Paint.setStatus("Performing initial setup");
        Logger.debugLog("Starting initialSetup() method.");

        int randomDelay2 = new Random().nextInt(300) + 200;
        int randomBiggerDelay = new Random().nextInt(400) + 600;

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Selecting bank tab " + banktab);
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Set custom quantity based on found nests
        if (Bank.contains(String.valueOf(birdnestSeed), 0.6)) {
            Bank.isSelectedQuantity1Button();
            Condition.sleep(generateRandomDelay(800, 1100));

            Paint.setStatus("Setting custom Qty to 16");
            Rectangle customQty = Bank.findQuantityCustomButton();
            Client.longPress(customQty);
            Condition.sleep(randomDelay2);
            Client.tap(392, 498);
            Condition.sleep(randomBiggerDelay);
            Client.sendKeystroke("KEYCODE_1");
            Client.sendKeystroke("KEYCODE_6");
            Client.sendKeystroke("KEYCODE_ENTER");
            Logger.debugLog("Set custom quantity 16 for items in the bank.");
            Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);
            firstTimeSeedNest = true;
        } else if (Bank.contains(String.valueOf(birdnestRing), 0.6)) {
            Bank.isSelectedQuantity1Button();
            Condition.sleep(generateRandomDelay(800, 1100));

            Paint.setStatus("Setting custom Qty to 14");
            Rectangle customQty = Bank.findQuantityCustomButton();
            Client.longPress(customQty);
            Condition.sleep(randomDelay2);
            Client.tap(392, 498);
            Condition.sleep(randomBiggerDelay);
            Client.sendKeystroke("KEYCODE_1");
            Client.sendKeystroke("KEYCODE_4");
            Client.sendKeystroke("KEYCODE_ENTER");
            Logger.debugLog("Set custom quantity 14 for items in the bank.");
            Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);
            firstTimeRingNest = true;
        }

        // Withdrawing initial items from the bank
        Paint.setStatus("Withdrawing Birds nests");

        if (firstTimeSeedNest) {
            Bank.withdrawItem(birdnestSeed, 0.6);
        } else if (firstTimeRingNest) {
            Bank.withdrawItem(birdnestRing, 0.6);
        }
        Condition.sleep(randomDelay2);
        Logger.debugLog("Withdrew Birds nests from the bank.");

        // Finishing off with closing the bank
        Logger.debugLog("Closing bank interface.");
        Paint.setStatus("Closing the bank interface");
        Bank.close();
        Logger.debugLog("Closed bank interface.");

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeEmptyNests() {
        Paint.setStatus("Empty nests");
        Logger.debugLog("Starting executeEmptyNests() method.");

        if (Inventory.contains(birdnestSeed, 0.75)) {
            Inventory.tapAllItems(birdnestSeed, 0.75);
            processSeedNestCount += 16;
            inventCount += 1;

            Paint.updateBox(seedNestIndex, processSeedNestCount);
        } else if (Inventory.contains(birdnestRing, 0.75)) {
            Inventory.tapAllItems(birdnestRing, 0.75);

            processRingNestCount += 14;
            inventCount += 1;

            Paint.updateBox(ringNestIndex, processRingNestCount);
        }

        updateStatLabel();

        Logger.debugLog("Ending the executeEmptyNests() method.");
    }

    private void bank() {
        Paint.setStatus("Banking");
        Logger.debugLog("Starting bank() method.");
        int randomDelay = new Random().nextInt(250) + 250;
        int randomDelay2 = new Random().nextInt(300) + 200;

        // Opening the bank based on your location
        Logger.debugLog("Attempting to open the bank.");
        Bank.open(bankloc);
        Logger.debugLog("Bank is open.");

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Selecting bank tab " + banktab);
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Depositing items based on your product chosen
        Logger.debugLog("Depositing inventory");
        Paint.setStatus("Deposit inventory");
        Bank.tapDepositInventoryButton();
        Condition.sleep(randomDelay);

        Paint.setStatus("Withdrawing Birds nests");
        if (Bank.contains(String.valueOf(birdnestSeed), 0.6)) {
            if (!firstTimeSeedNest) {
                Bank.isSelectedQuantity1Button();
                Condition.sleep(generateRandomDelay(800, 1100));

                Paint.setStatus("Setting custom Qty to 16");
                Rectangle customQty = Bank.findQuantityCustomButton();
                Client.longPress(customQty);
                Condition.sleep(randomDelay2);
                Client.tap(392, 498);
                Condition.sleep(generateRandomDelay(1600, 2200));
                Client.sendKeystroke("KEYCODE_1");
                Client.sendKeystroke("KEYCODE_6");
                Client.sendKeystroke("KEYCODE_ENTER");
                Logger.debugLog("Set custom quantity 16 for items in the bank.");
                Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);
                firstTimeSeedNest = true;
                Bank.withdrawItem(birdnestSeed, 0.6);
            } else {
                Bank.withdrawItem(birdnestSeed, 0.6);
            }
        } else if (Bank.contains(String.valueOf(birdnestRing), 0.6)) {
            if (!firstTimeRingNest) {
                Bank.isSelectedQuantity1Button();
                Condition.sleep(generateRandomDelay(800, 1100));

                Paint.setStatus("Setting custom Qty to 14");
                Rectangle customQty = Bank.findQuantityCustomButton();
                Client.longPress(customQty);
                Condition.sleep(randomDelay2);
                Client.tap(392, 498);
                Condition.sleep(generateRandomDelay(1600, 2200));
                Client.sendKeystroke("KEYCODE_1");
                Client.sendKeystroke("KEYCODE_4");
                Client.sendKeystroke("KEYCODE_ENTER");
                Logger.debugLog("Set custom quantity 14 for items in the bank.");
                Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);
                firstTimeRingNest = true;
                Bank.withdrawItem(birdnestRing, 0.6);
            } else {
                Bank.withdrawItem(birdnestRing, 0.6);
            }
        }
        Condition.sleep(randomDelay2);
        Logger.debugLog("Withdrew Birds nests from the bank.");

        // Closing the bank, as banking should be done now
        Paint.setStatus("Closing bank");
        Bank.close();
        Logger.debugLog("Closed the bank.");

        Logger.debugLog("Ending the bank() method.");
    }

    private void checkInventOpen() {
        Paint.setStatus("Check inventory open");
        // Check if the inventory is open (needs this check after a break)
        if (!GameTabs.isInventoryTabOpen()) {
            Paint.setStatus("Opening inventory");
            GameTabs.openInventoryTab();
        }
    }

    private void checkInventNests() {
        Paint.setStatus("Check invent for nests");
        if (!GameTabs.isInventoryTabOpen()) {
            Paint.setStatus("Opening inventory");
            GameTabs.openInventoryTab();
        }
        int[] items = {birdnestSeed, birdnestRing};
        if (!Inventory.containsAny(items, 0.75)) {
            Logger.log("1st check failed for Birds nests in our inventory, going back to banking!");
            bank();
        }

        if (!Inventory.containsAny(items, 0.75)) {
            Logger.log("2nd check failed for Birds nests in our inventory, logging out and aborting script!");
            Logout.logout();
            Script.stop();
        }
    }

    private void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        } else {
            // We do nothing here, as hop is disabled.
        }
    }

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

    public static void updateStatLabel() {
        // Calculations for the statistics label
        long currentTime = System.currentTimeMillis();
        double elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);
        int totalProfit = (processRingNestCount + processSeedNestCount) * 1333;

        // Calculate per hour rates and round to the nearest whole number
        long inventsPerHour = (elapsedTimeInHours > 0) ? Math.round(inventCount / elapsedTimeInHours) : 0;
        long nestsPerHour = (elapsedTimeInHours > 0) ? Math.round((processRingNestCount + processSeedNestCount) / elapsedTimeInHours) : 0;
        int profitPerHour = (elapsedTimeInHours > 0) ? (int) Math.round(totalProfit / elapsedTimeInHours) : 0;

        // Create the statistics string with proper formatting
        String statistics = String.format(
                "Invents %d (%dp/h) | Nests %d (%dp/h)",
                inventCount, inventsPerHour,
                processRingNestCount + processSeedNestCount, nestsPerHour
        );

        // Update the boxes with the profit
        Paint.updateBox(profitIndex, totalProfit);
        Paint.updateBox(profitHourlyIndex, profitPerHour);

        // Update the statistics label
        Paint.setStatistic(statistics);
    }
}