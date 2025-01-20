package main;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import tasks.Process;
import utils.Task;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dAIO Bow Fletcher",
        description = "Cuts or strings bows for you. Supports all log tiers, and both short and longbows with dynamic banking.",
        version = "2.01",
        guideLink = "https://wiki.mufasaclient.com/docs/daio-bow-fletcher/",
        categories = {ScriptCategory.Fletching}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Method",
                        description = "Which operation would you like to perform?",
                        defaultValue = "Cut",
                        allowedValues = {
                                @AllowedValue(optionName = "Cut"),
                                @AllowedValue(optionName = "String")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Tier",
                        description = "Which tier of logs/bows would you like to use?",
                        defaultValue = "Maple logs",
                        allowedValues = {
                                @AllowedValue(optionIcon = "1511", optionName = "Logs"),
                                @AllowedValue(optionIcon = "1521", optionName = "Oak logs"),
                                @AllowedValue(optionIcon = "1519", optionName = "Willow logs"),
                                @AllowedValue(optionIcon = "1517", optionName = "Maple logs"),
                                @AllowedValue(optionIcon = "1515", optionName = "Yew logs"),
                                @AllowedValue(optionIcon = "1513", optionName = "Magic logs")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Product",
                        description = "Would you like to make short or longbows?",
                        defaultValue = "Longbow",
                        allowedValues = {
                                @AllowedValue(optionName = "Shortbow"),
                                @AllowedValue(optionName = "Longbow")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Bank Tab",
                        description = "What bank tab are your resources located in?",
                        defaultValue = "0",
                        optionType = OptionType.BANKTABS
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
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

public class dAIOBowFletcher extends AbstractScript {
    public static Random random = new Random();
    public static boolean setupDone = false;
    public static boolean prepareScriptStop = false;
    public static boolean stopScript = false;
    public static boolean doneBanking = false;
    private boolean antiBan;
    private boolean extendedAntiBan;
    public static int retrycount = 0;
    public static int totalcount = 0;
    public static int processedcount = 0;
    public static int previousBankItemCount = 0;
    public static int bankItemCount = 0;
    public static int bowstringCount = 0;
    public static long lastProcessTime = System.currentTimeMillis();
    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    public static String method;
    public static String tier;
    public static String product;
    public static String bankloc;
    public static int banktab;
    public static int currentUsedSlots = 0;
    public static boolean initialActiondone = false;
    public static String logs;
    public static String shortbowU;
    public static String longbowU;
    public static String shortbow;
    public static String longbow;
    public static String productName;
    public static int finalProductID = -1;
    public static int processCount = 0;
    public static int productIndex;
    private static long currentTime = System.currentTimeMillis();
    private static long startTime;
    private static double elapsedTimeInHours;
    private static double itemsPerHour;
    private static final Rectangle chatCheckArea1 = new Rectangle(7, 130, 22, 19);
    private static final Rectangle chatCheckArea2 = new Rectangle(512, 124, 30, 29);
    public static Map<String, String[]> itemIDs;
    // These tasks are executed in this order
    List<Task> tasks = Arrays.asList(
            new Setup(),
            new Bank(),
            new Process()
    );

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        method = configs.get("Method");
        tier = configs.get("Tier");
        product = configs.get("Product");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        antiBan = Boolean.valueOf(configs.get("Run anti-ban"));
        extendedAntiBan = Boolean.valueOf(configs.get("Run extended anti-ban"));

        //Logs for debugging purposes
        Logger.log("Thank you for using the dAIO Bow Fletcher script!");

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // One-time setup
        initializeItemIDs();
        setProductNameAndID();

        // Create a single image box, to show the amount of processed bows
        productIndex = Paint.createBox(productName, finalProductID, processCount);

        // Set the two top headers of paintUI.
        Paint.setStatus("Initializing...");

        // One-time setup
        hopActions();

        if (antiBan) {
            Logger.debugLog("Initializing anti-ban timer");
            Game.antiBan();
            if (extendedAntiBan) {
                Logger.debugLog("Initializing extended anti-ban timer");
                Game.enableOptionalAntiBan(AntiBan.EXTENDED_AFK);
                Game.antiBan();
            }
        }

        startTime = System.currentTimeMillis();
    }

    @Override
    public void poll() {

        if (stopScript & doneBanking) {
            Logger.log("Script has been marked to stop by script dev marked situation, stopping!");
            if (Bank.isOpen()) {
                Bank.close();
                Condition.sleep(generateDelay(700, 1000));
            }
            Logout.logout();
            Script.stop();
        }

        if (prepareScriptStop) {
            Logger.debugLog("Script has been marked to stop by script dev set situation after this iteration!");
            stopScript = true;
        }

        // Check if it's time to hop
        hopActions();

        if (antiBan) {
            Game.antiBan();
        }

        // Read XP
        readXP();

        // Open inventory tab
        GameTabs.openTab(UITabs.INVENTORY);

        // Run tasks
        for (Task task : tasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    public static void updatePaintBar(int count) {
        Paint.setStatus("Update paint count");
        Paint.updateBox(productIndex, count);

        // Time calculations
        currentTime = System.currentTimeMillis();
        elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);
        itemsPerHour = totalcount / elapsedTimeInHours;

        // Format runes items per hour with dot as thousand separator and no decimals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(','); // Set the decimal separator to comma
        DecimalFormat formatItems = new DecimalFormat("#,###", symbols);
        String itemsPerHourFormatted = formatItems.format(itemsPerHour);

        // Update the statistics label
        String statistics = String.format("Bows/hr: %s", itemsPerHourFormatted);
        Paint.setStatistic(statistics);
    }

    private void initializeItemIDs() {
        Logger.debugLog("Running the initializeItemIDs() method.");
        Paint.setStatus("Initializing item IDs");

        itemIDs = new HashMap<>();

        // Map of itemIDs for LogID (1), UnstrungShortbowID (2), UnstrungLongbowID (3), StrungShortbowID (4) and StrungLongbowID (5)
        itemIDs.put("Logs", new String[] {"1511", "50", "48", "841", "839"});
        itemIDs.put("Oak logs", new String[] {"1521", "54", "56", "843", "845"});
        itemIDs.put("Willow logs", new String[] {"1519", "60", "58", "849", "847"});
        itemIDs.put("Maple logs", new String[] {"1517", "64", "62", "853", "851"});
        itemIDs.put("Yew logs", new String[] {"1515", "68", "66", "857", "855"});
        itemIDs.put("Magic logs", new String[] {"1513", "72", "70", "861", "859"});

        Logger.debugLog("Ending the initializeItemIDs() method.");
    }

    private void setProductNameAndID() {
        Logger.debugLog("Running the setProductNameAndID() method.");
        Paint.setStatus("Initializing paint variables");
        String[] itemIDArray = itemIDs.get(tier);

        if (itemIDArray == null) {
            Logger.debugLog("Invalid tier: " + tier);
            return;
        }

        String tierName = tier.equals("Logs") ? "" : tier.replace(" logs", "") + " ";

        if (method.equals("String")) {
            if (product.equals("Shortbow")) {
                productName = tierName + "Shortbow";
                finalProductID = Integer.parseInt(itemIDArray[3]);
            } else if (product.equals("Longbow")) {
                productName = tierName + "Longbow";
                finalProductID = Integer.parseInt(itemIDArray[4]);
            }
        } else if (method.equals("Cut")) {
            if (product.equals("Shortbow")) {
                productName = tierName + "Shortbow (u)";
                finalProductID = Integer.parseInt(itemIDArray[1]);
            } else if (product.equals("Longbow")) {
                productName = tierName + "Longbow (u)";
                finalProductID = Integer.parseInt(itemIDArray[2]);
            }
        }

        Logger.debugLog("Product Name: " + productName);
        Logger.debugLog("Final Product ID: " + finalProductID);

        Logger.debugLog("Ending the setProductNameAndID() method.");
    }

    public static void withdrawLogs(boolean useCache) {
        switch (tier) {
            case "Logs":
                Logger.debugLog("Withdrawing Logs");
                Bank.withdrawItem(ItemList.LOGS_1511, useCache, 0.75, Color.decode("#624827"));
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(ItemList.LOGS_1511, Color.decode("#624827"));
                break;
            case "Oak logs":
                Logger.debugLog("Withdrawing Oak logs");
                Bank.withdrawItem(ItemList.OAK_LOGS_1521, useCache, 0.75, Color.decode("#825f34"));
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(ItemList.OAK_LOGS_1521, Color.decode("#825f34"));
                break;
            case "Willow logs":
                Logger.debugLog("Withdrawing Willow logs");
                Bank.withdrawItem(ItemList.WILLOW_LOGS_1519, useCache, 0.75, Color.decode("#473e14"));
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(ItemList.WILLOW_LOGS_1519, Color.decode("#473e14"));
                break;
            case "Maple logs":
                Logger.debugLog("Withdrawing Maple logs");
                Bank.withdrawItem(ItemList.MAPLE_LOGS_1517, useCache, 0.75, Color.decode("#5f3c07"));
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(ItemList.MAPLE_LOGS_1517, Color.decode("#5f3c07"));
                break;
            case "Yew logs":
                Logger.debugLog("Withdrawing Yew logs");
                Bank.withdrawItem(ItemList.YEW_LOGS_1515, useCache, 0.75, Color.decode("#4f3803"));
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(ItemList.YEW_LOGS_1515, Color.decode("#4f3803"));
                break;
            case "Magic logs":
                Logger.debugLog("Withdrawing Magic logs");
                Bank.withdrawItem(ItemList.MAGIC_LOGS_1513, useCache, 0.7, Color.decode("#817851"));
                updatePreviousBankItemCount();
                bankItemCount = Bank.stackSize(ItemList.MAGIC_LOGS_1513, Color.decode("#817851"));
                break;
            default:
                Logger.debugLog("Invalid log tier, stopping script.");
                Script.stop();
                break;
        }
    }

    public static boolean checkLogs() {
        switch (tier) {
            case "Logs":
                return Inventory.contains(ItemList.LOGS_1511, 0.8, Color.decode("#624827"));
            case "Oak logs":
                return Inventory.contains(ItemList.OAK_LOGS_1521, 0.8, Color.decode("#825f34"));
            case "Willow logs":
                return Inventory.contains(ItemList.WILLOW_LOGS_1519, 0.8, Color.decode("#473e14"));
            case "Maple logs":
                return Inventory.contains(ItemList.MAPLE_LOGS_1517, 0.8, Color.decode("#5f3c07"));
            case "Yew logs":
                return Inventory.contains(ItemList.YEW_LOGS_1515, 0.8, Color.decode("#4f3803"));
            case "Magic logs":
                return Inventory.contains(ItemList.MAGIC_LOGS_1513, 0.7, Color.decode("#817851"));
            default:
                Logger.debugLog("Invalid log tier, stopping script.");
                Script.stop();
                return false;
        }
    }

    public static void updatePreviousBankItemCount() {
        if (previousBankItemCount != -1 & bankItemCount > 0) {
            previousBankItemCount = bankItemCount;
        }
    }


    public static boolean makeMenuOpen() {
        boolean check1 = Client.isColorInRect(Color.decode("#5b5345"), chatCheckArea1, 10);
        boolean check2 = Client.isColorInRect(Color.decode("#5b5345"), chatCheckArea2, 10);

        return check1 & check2;
    }

    public static int generateDelay(int lowerEnd, int higherEnd) {
        if (lowerEnd > higherEnd) {
            // Swap lowerEnd and higherEnd if lowerEnd is greater
            int temp = lowerEnd;
            lowerEnd = higherEnd;
            higherEnd = temp;
        }
        return random.nextInt(higherEnd - lowerEnd + 1) + lowerEnd;
    }

    private void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        } else {
            // We do nothing here, as hop is disabled.
        }
    }

    private void readXP() {
        XpBar.getXP();
    }
}