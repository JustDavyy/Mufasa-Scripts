import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import static helpers.Interfaces.*;
import static helpers.Interfaces.Logout;

@ScriptManifest(
        name = "dAIO Bow Fletcher",
        description = "Cuts or strings bows for you. Supports all log tiers, and both short and longbows with dynamic banking.",
        version = "1.36",
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
                )
        }
)

public class dAIOBowFletcher extends AbstractScript {
    // Creating the strings for later use
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String method;
    String tier;
    String product;
    String bankloc;
    int banktab;
    String logs;
    String shortbowU;
    String longbowU;
    String shortbow;
    String longbow;
    String bowstring = "1777";
    String knife = "946";
    private Map<String, String[]> itemIDs;

    // This is the onStart, and only gets ran once.
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

        // One-time setup
        initializeItemIDs();
        hopActions();
        setupItemIds();
        setupBanking();
        initialSetup();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dAIO Bow Fletcher script!");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        if (method.equals("Cut")) {
            checkInventOpen();
            checkInventCutMethod();
            executeCutMethod();
            bank();
            hopActions();
        }

        else if (method.equals("String")) {
            checkInventOpen();
            checkInventStringMethod();
            executeStringMethod();
            bank();
            hopActions();
        }

    }

    private void initializeItemIDs() {
        Logger.debugLog("Running the initializeItemIDs() method.");

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

    private void setupItemIds() {
        Logger.debugLog("Running the setupItemIds() method.");
        if (longbow == null) {
            String[] itemIds = itemIDs.get(tier);
            logs = itemIds[0];
            shortbowU = itemIds[1];
            longbowU = itemIds[2];
            shortbow = itemIds[3];
            longbow = itemIds[4];

            Logger.debugLog("Stored IDs for " + tier + ":\nLogs: " + logs + "\nUnstrung Shortbow: " + shortbowU + "\nUnstrung Longbow: " + longbowU + "\nShortbow: " + shortbow + "\nLongbow: " + longbow);
        }

        Logger.debugLog("Ending the setupItemIds() method.");
    }

    private void setupBanking() {
        Logger.debugLog("Starting setupBanking() method.");
        if (bankloc == null) {
            Logger.debugLog("Starting dynamic banking setup...");

            // Opening the inventory if not yet opened.
            Logger.log("Opening up the inventory.");
            if (!GameTabs.isInventoryTabOpen()) {
                GameTabs.openInventoryTab();
            }

            Logger.debugLog("Starting setup for Dynamic Banking.");
            bankloc = Bank.setupDynamicBank();
            Logger.log("We're located at: " + bankloc + ".");
            Logger.debugLog("We're located at: " + bankloc + ".");
            if (bankloc == null) {
                Logger.debugLog("Could not find a dynamic bank location we are in, logging out and aborting script.");
                Logout.logout();
                Script.stop();
            }
            Condition.sleep(5000);
            Logger.log("Opening the Bank of Gielinor.");
            Bank.open(bankloc);
            Logger.debugLog("Bank interface detected!");
            if (Bank.isBankPinNeeded()) {
                Logger.debugLog("Bank pin is needed!");
                Bank.enterBankPin();
                Condition.sleep(500);
                Condition.wait(() -> Bank.isOpen(), 200, 12);
                Logger.debugLog("Bank pin entered.");
                Logger.log("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(638);
            } else {
                Logger.debugLog("Bank pin is not needed, bank is open!");
                Logger.log("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(629);
            }
        }
        Logger.debugLog("Ending the setupBanking() method.");
    }

    @SuppressWarnings("IfStatementWithIdenticalBranches")
    private void initialSetup() {
        Logger.debugLog("Starting initialSetup() method.");

        int randomDelay = new Random().nextInt(600) + 600;
        int randomBiggerDelay = new Random().nextInt(1500) + 1500;

        // Part if the Cut method was chosen
        if (method.equals("Cut")) {
            Logger.debugLog("Cut method was selected.");

            // Withdrawing a knife from the bank
            Logger.debugLog("Withdrawing a knife from the bank.");
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
            }
            Bank.tapSearchButton();
            Condition.sleep(randomDelay);

            String textToSend = "knife";
            for (char c : textToSend.toCharArray()) {
                String keycode = "KEYCODE_" + Character.toUpperCase(c);
                Client.sendKeystroke(keycode);
                Logger.debugLog("Sent keystroke: " + keycode);
            }

            Condition.sleep(randomBiggerDelay);
            Bank.withdrawItem(knife, 0.75);
            Condition.sleep(randomDelay);
            Logger.debugLog("Withdrew knife from the bank.");

            Client.sendKeystroke("KEYCODE_ENTER");
            Condition.sleep(randomDelay);
            Logger.debugLog("Closed search interface.");

            // Check if we have the knife in the inventory, otherwise stop script.
            Condition.wait(() -> Inventory.contains(knife, 0.75), 250,10);
            if (!Inventory.contains(knife, 0.75)) {
                Logger.log("No knife found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }

            // Grabbing the first items to process
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
                Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 12);
                Logger.debugLog("Selected Quantity All button.");

                // Selecting the right bank tab again if needed
                if (!Bank.isSelectedBankTab(banktab)) {
                    Bank.openTab(banktab);
                    Logger.log("Selecting bank tab " + banktab);
                }

                // Withdraw first set of items
                Bank.withdrawItem(logs, 0.75);
                Logger.debugLog("Withdrew " + tier +  " from the bank.");

                // Check if we have the logs in the inventory, otherwise stop script.
                Condition.wait(() -> Inventory.contains(logs, 0.75), 250,10);
                if (!Inventory.contains(logs, 0.75)) {
                    Logger.log("No logs found in inventory, assuming we're out of items to process.");
                    Bank.close();
                    if (Bank.isOpen()) {
                        Bank.close();
                    }
                    Logout.logout();
                    Script.stop();
                }
            } else {
                // Withdraw first set of items
                Bank.withdrawItem(logs, 0.75);

                // Check if we have both a knife and the logs in the inventory, otherwise stop script.
                String[] items3 = {knife, logs};
                Condition.wait(() -> Inventory.contains(items3, 0.75), 250,10);
                if (!Inventory.contains(items3, 0.75)) {
                    Logger.log("Not all items found in inventory, assuming we're out of items to process.");
                    Bank.close();
                    if (Bank.isOpen()) {
                        Bank.close();
                    }
                    Logout.logout();
                    Script.stop();
                }

                Logger.debugLog("Withdrew " + tier +  " from the bank.");
            }
        }

        // Part if the String method was chosen
        else if (method.equals("String")) {
            int randomDelay2 = new Random().nextInt(300) + 200;
            int randomBiggerDelay2 = new Random().nextInt(400) + 600;
            Logger.debugLog("String method was selected.");
            if (!Bank.isSelectedQuantityCustomButton()) {
                Rectangle customQty = Bank.findQuantityCustomButton();
                Client.longPress(customQty);
                Condition.sleep(randomDelay2);
                Client.tap(393, 499);
                Condition.sleep(randomBiggerDelay2);
                Client.sendKeystroke("KEYCODE_1");
                Client.sendKeystroke("KEYCODE_4");
                Client.sendKeystroke("KEYCODE_ENTER");
                Logger.debugLog("Set custom quantity 14 for items in the bank.");
                Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);

                // Select the right bank tab if needed.
                if (!Bank.isSelectedBankTab(banktab)) {
                    Bank.openTab(banktab);
                    Logger.log("Selecting bank tab " + banktab);
                }

                // Withdraw first set of items
                if (product.equals("Shortbow")) {
                    Bank.withdrawItem(shortbowU, 0.75);
                } else {
                    Bank.withdrawItem(longbowU, 0.75);
                }
                Condition.sleep(randomDelay2);
                Bank.withdrawItem(bowstring, 0.75);
                Logger.debugLog("Withdrew 14 bowstrings and unstrung bows from the bank.");
            } else {
                // Select the right bank tab if needed.
                if (!Bank.isSelectedBankTab(banktab)) {
                    Bank.openTab(banktab);
                    Logger.log("Selecting bank tab " + banktab);
                }

                // Withdraw first set of items
                if (product.equals("Shortbow")) {
                    Bank.withdrawItem(shortbowU, 0.75);
                } else {
                    Bank.withdrawItem(longbowU, 0.75);
                }
                Condition.sleep(randomDelay2);
                Bank.withdrawItem(bowstring, 0.75);
                Logger.debugLog("Withdrew 14 bowstrings and unstrung bows from the bank.");
            }
        }

        // Finishing off with closing the bank
        Logger.debugLog("Closing bank interface.");
        Bank.close();
        Logger.debugLog("Closed bank interface.");

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeCutMethod() {
        Logger.debugLog("Starting executeCutMethod() method.");

        // Check if we have both a knife and the logs in the inventory.
        if (!Inventory.contains(logs, 0.75) && !Inventory.contains(knife, 0.75)) {
            Logger.log("We don't have a knife and logs in our inventory, going back to banking!");
            return;
        }

        // Starting to process items
        Inventory.tapItem(knife, 0.75);
        int randomDelay2 = new Random().nextInt(150) + 100;
        int randomDelay3 = new Random().nextInt(1500) + 500;
        Condition.sleep(randomDelay2);
        Inventory.tapItem(logs, 0.75);
        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);

        // tap option needed based on choice in config
        if (product.equals("Shortbow")) {
            Chatbox.makeOption(2);
            Logger.debugLog("Selected option 2 in chatbox.");
        } else {
            Chatbox.makeOption(3);
            Logger.debugLog("Selected option 3 in chatbox.");
        }

        // Wait for the inventory to finish (with a timeout)
        long startTime = System.currentTimeMillis();
        long timeout = 60 * 1000; // 60 seconds in milliseconds as a full invent is about 45-50 seconds.
        while (Inventory.contains(logs, 0.75)) {
            readXP();
            Condition.sleep(randomDelay3);
            hopActions();

            // Check if we have passed the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                Logger.debugLog("Timeout reached for inventory.contains() method");
                break;
            }
        }
        readXP();

        Logger.debugLog("Ending the executeCutMethod() method.");
    }

    private void executeStringMethod() {
        Logger.debugLog("Starting executeStringMethod() method.");

        // Check if we have both unstrung bows and bowstrings in the inventory.
        if (product.equals("Shortbow")) {
            if (!Inventory.contains(shortbowU, 0.75) && !Inventory.contains(bowstring, 0.75)) {
                Logger.log("We don't have unstrung bows and bowstring in our inventory, going back to banking!");
                return;
            }
        } else {
            if (!Inventory.contains(longbowU, 0.75) && !Inventory.contains(bowstring, 0.75)) {
                Logger.log("We don't have unstrung bows and bowstring in our inventory, going back to banking!");
                return;
            }
        }

        // tap item needed based on choice in config
        if (product.equals("Shortbow")) {
            Inventory.tapItem(shortbowU, 0.75);
        } else {
            Inventory.tapItem(longbowU, 0.75);
        }

        int randomDelay2 = new Random().nextInt(150) + 100;
        int randomDelay3 = new Random().nextInt(1500) + 500;
        Condition.sleep(randomDelay2);
        Inventory.tapItem(bowstring, 0.75);
        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
        Chatbox.makeOption(1);
        Logger.debugLog("Selected option 1 in chatbox.");

        // Wait for the inventory to finish (with a timeout)
        long startTime = System.currentTimeMillis();
        long timeout = 22 * 1000; // 22 seconds in milliseconds as a full invent is about 15-17 seconds.
        while (Inventory.contains(bowstring, 0.75)) {
            readXP();
            Condition.sleep(randomDelay3);
            hopActions();

            // Check if we have passed the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                Logger.debugLog("Timeout reached for inventory.contains() method");
                break;
            }
        }
        readXP();

        Logger.debugLog("Ending the executeStringMethod() method.");
    }

    private void bank() {
        Logger.debugLog("Starting bank() method.");
        Logger.log("Banking.");
        int randomDelay = new Random().nextInt(250) + 250;

        // Opening the bank based on your location
        Logger.debugLog("Attempting to open the bank.");
        Bank.open(bankloc);
        Logger.debugLog("Bank is open.");

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Bank.openTab(banktab);
            Logger.log("Opened bank tab " + banktab);
        }

        // Depositing items based on your tier/method
        if (method.equals("Cut")){
            if (product.equals("Shortbow")) {
                Logger.debugLog("Depositing unstrung shortbows.");
                Inventory.tapItem(shortbowU, 0.75);
            } else {
                Logger.debugLog("Depositing unstrung longbows.");
                Inventory.tapItem(longbowU, 0.75);
            }
        }
        else if (method.equals("String")) {
            if (product.equals("Shortbow")) {
                Logger.debugLog("Depositing strung shortbows.");
                Inventory.tapItem(shortbow, 0.75);
            } else {
                Logger.debugLog("Depositing strung longbows.");
                Inventory.tapItem(longbow, 0.75);
            }
        }
        Condition.sleep(randomDelay);

        // Withdrawing the items based on your tier/method
        if (method.equals("Cut")) {
            Bank.withdrawItem(logs, 0.75);

            Logger.debugLog("Withdrew " + tier + " from the bank.");
        }
        else if (method.equals("String")) {

            // Withdraw unstrung bow that was picked in the config
            if (product.equals("Shortbow")) {
                Logger.debugLog("Withdrawing unstrung shortbows");
                Bank.withdrawItem(shortbowU, 0.75);

            } else {
                Logger.debugLog("Withdrawing unstrung longbows");
                Bank.withdrawItem(longbowU, 0.75);

            }

            // Withdraw bowstrings
            Logger.debugLog("Withdrawing bowstrings.");
            Bank.withdrawItem(bowstring, 0.75);

            Logger.log("Withdrew all items from the bank.");
        }

        // Closing the bank, as banking should be done now
        Bank.close();
        Logger.debugLog("Closed the bank.");

        Logger.debugLog("Ending the bank() method.");
    }

    private void checkInventOpen() {
        // Check if the inventory is open (needs this check after a break)
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }
    }

    private void checkInventCutMethod() {
        String[] items = {knife, logs};
        // Check if we have both a knife and the logs in the inventory.
        if (!Inventory.contains(items, 0.75)) {
            Logger.debugLog("1st check failed for knife and logs in our inventory, going back to banking!");
            bank();
        }

        // Check if we have both a knife and the logs in the inventory.
        if (!Inventory.contains(items, 0.75)) {
            Logger.log("2nd check failed for knife and logs in our inventory, logging out and aborting script!");
            Logout.logout();
            Script.stop();
        }
    }

    private void checkInventStringMethod() {
        // Check if we have both unstrung bows and bowstrings in the inventory.
        //noinspection IfStatementWithIdenticalBranches
        if (product.equals("Shortbow")) {
            String[] items = {shortbowU, bowstring};
            if (!Inventory.contains(items, 0.75)) {
                Logger.debugLog("1st check failed for unstrung bows and bowstring in our inventory, going back to banking!");
                bank();
            }
        } else {
            String[] items = {longbowU, bowstring};
            if (!Inventory.contains(items, 0.75)) {
                Logger.debugLog("1st check failed for unstrung bows and bowstring in our inventory, going back to banking!");
                bank();
            }
        }

        // Check if we have both unstrung bows and bowstrings in the inventory.
        if (product.equals("Shortbow")) {
            if (!Inventory.contains(shortbowU, 0.75) && !Inventory.contains(bowstring, 0.75)) {
                Logger.log("2nd check failed for unstrung bows and bowstring in our inventory, logging out and aborting script!");
                Logout.logout();
                Script.stop();
            }
        } else {
            if (!Inventory.contains(longbowU, 0.75) && !Inventory.contains(bowstring, 0.75)) {
                Logger.log("2nd check failed for unstrung bows and bowstring in our inventory, logging out and aborting script!");
                Logout.logout();
                Script.stop();
            }
        }
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