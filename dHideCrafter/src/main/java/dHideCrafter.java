import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.ItemList;
import helpers.utils.OptionType;

import java.util.Map;
import java.util.HashMap;
import static helpers.Interfaces.*;
import static helpers.Interfaces.Logout;

import java.util.Random;

@ScriptManifest(
        name = "dHide Crafter",
        description = "Crafts tanned dragon hides into any product. Supports all options and dynamic banking.",
        version = "1.032",
        guideLink = "https://wiki.mufasaclient.com/docs/dhide-crafter/",
        categories = {ScriptCategory.Crafting}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What end product would you like to craft?",
                        defaultValue = "Body",
                        allowedValues = {
                                @AllowedValue(optionIcon = "2487", optionName = "Vambraces"),
                                @AllowedValue(optionIcon = "2493", optionName = "Chaps"),
                                @AllowedValue(optionIcon = "2499", optionName = "Body")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Tier",
                        description = "What tier of dragon leather would you like to use?",
                        defaultValue = "Blue",
                        allowedValues = {
                                @AllowedValue(optionIcon = "1745", optionName = "Green"),
                                @AllowedValue(optionIcon = "2505", optionName = "Blue"),
                                @AllowedValue(optionIcon = "2507", optionName = "Red"),
                                @AllowedValue(optionIcon = "2509", optionName = "Black")
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

public class dHideCrafter extends AbstractScript {
    // Creating the strings for later use
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String product;
    String tier;
    String bankloc;
    String productName;
    int processedItems = 0;
    int productIndex;
    int banktab;
    int makeOption;
    int needle = 1733;
    int thread = 1734;
    int leather;
    int depositProduct;
    private final Random random = new Random();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");
        tier = configs.get("Tier");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        Paint.setStatus("Setup item IDs / options");
        // One-time setup
        setupOptions();

        // Create paint box
        Paint.setStatus("Create paint box");
        productIndex = Paint.createBox(productName, depositProduct, processedItems);

        Paint.setStatus("Initialize hop timer");
        hopActions();
        setupBanking();
        initialSetup();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dHide Crafter script!");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        checkInventOpen();
        checkInventCrafting();
        executeCraftingMethod();
        bank();
        hopActions();

    }

    private void setupOptions() {
        Logger.debugLog("Running the setupOptions() method.");

        // Set up the chatbox make option we need to make
        if (java.util.Objects.equals(product, "Vambraces")) {
            makeOption = 2;
        } else if (java.util.Objects.equals(product, "Chaps")) {
            makeOption = 3;
        } else if (java.util.Objects.equals(product, "Body")) {
            makeOption = 1;
        }

        // Set up the ID we need for the leather to grab from the bank
        if (java.util.Objects.equals(tier, "Green")) {
            leather = 1745;
        } else if (java.util.Objects.equals(tier, "Blue")) {
            leather = 2505;
        } else if (java.util.Objects.equals(tier, "Red")) {
            leather = 2507;
        } else if (java.util.Objects.equals(tier, "Black")) {
            leather = 2509;
        }

        // Set up the deposit ID we need
        // Green
        if (java.util.Objects.equals(product, "Vambraces") && java.util.Objects.equals(tier, "Green")) {
            depositProduct = 1065;
            productName = "Green d'hide vambraces";
        } else if (java.util.Objects.equals(product, "Chaps") && java.util.Objects.equals(tier, "Green")) {
            depositProduct = 1099;
            productName = "Green d'hide chaps";
        } else if (java.util.Objects.equals(product, "Body") && java.util.Objects.equals(tier, "Green")) {
            depositProduct = 1135;
            productName = "Green d'hide body";
        }
        // Blue
        if (java.util.Objects.equals(product, "Vambraces") && java.util.Objects.equals(tier, "Blue")) {
            depositProduct = 2487;
            productName = "Blue d'hide vambraces";
        } else if (java.util.Objects.equals(product, "Chaps") && java.util.Objects.equals(tier, "Blue")) {
            depositProduct = 2493;
            productName = "Blue d'hide chaps";
        } else if (java.util.Objects.equals(product, "Body") && java.util.Objects.equals(tier, "Blue")) {
            depositProduct = 2499;
            productName = "Blue d'hide body";
        }
        // Red
        if (java.util.Objects.equals(product, "Vambraces") && java.util.Objects.equals(tier, "Red")) {
            productName = "Red d'hide vambraces";
            depositProduct = 2489;
        } else if (java.util.Objects.equals(product, "Chaps") && java.util.Objects.equals(tier, "Red")) {
            depositProduct = 2495;
            productName = "Red d'hide chaps";
        } else if (java.util.Objects.equals(product, "Body") && java.util.Objects.equals(tier, "Red")) {
            depositProduct = 2501;
            productName = "Red d'hide body";
        }
        // Black
        if (java.util.Objects.equals(product, "Vambraces") && java.util.Objects.equals(tier, "Black")) {
            productName = "Black d'hide vambraces";
            depositProduct = 2491;
        } else if (java.util.Objects.equals(product, "Chaps") && java.util.Objects.equals(tier, "Black")) {
            depositProduct = 2497;
            productName = "Black d'hide chaps";
        } else if (java.util.Objects.equals(product, "Body") && java.util.Objects.equals(tier, "Black")) {
            depositProduct = 2503;
            productName = "Black d'hide body";
        }

        Logger.debugLog("Ending the setupOptions() method.");
    }

    private void setupBanking() {
        Paint.setStatus("Setup banking");
        Logger.debugLog("Starting setupBanking() method.");
        if (bankloc == null) {
            Logger.debugLog("Starting dynamic banking setup...");

            // Opening the inventory if not yet opened.
            Logger.debugLog("Opening up the inventory.");
            if (!GameTabs.isInventoryTabOpen()) {
                Paint.setStatus("Open inventory");
                GameTabs.openInventoryTab();
            }

            Logger.debugLog("Starting setup for Dynamic Banking.");
            Paint.setStatus("Setup dynamic bank");
            bankloc = Bank.setupDynamicBank();
            Logger.log("We're located at: " + bankloc + ".");
            Logger.debugLog("We're located at: " + bankloc + ".");
            if (bankloc == null) {
                Logger.debugLog("Could not find a dynamic bank location we are in, logging out and aborting script.");
                Logout.logout();
                Script.stop();
            }
            Condition.sleep(generateDelay(4500, 5500));
            Logger.debugLog("Attempting to open the Bank of Gielinor.");
            Paint.setStatus("Open bank");
            Bank.open(bankloc);
            Logger.debugLog("Bank interface detected!");
            if (Bank.isBankPinNeeded()) {
                Logger.debugLog("Bank pin is needed!");
                Paint.setStatus("Enter bank pin");
                Bank.enterBankPin();
                Condition.sleep(generateDelay(400, 600));
                Condition.wait(() -> Bank.isOpen(), 200, 12);
                Logger.debugLog("Bank pin entered.");
                Logger.debugLog("Depositing inventory.");
                Paint.setStatus("Deposit inventory");
                Bank.tapDepositInventoryButton();
                Condition.sleep(generateDelay(500, 750));
            } else {
                Logger.debugLog("Bank pin is not needed, bank is open!");
                Logger.debugLog("Depositing inventory.");
                Paint.setStatus("Deposit inventory");
                Bank.tapDepositInventoryButton();
                Condition.sleep(generateDelay(500, 750));
            }
        }
        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void initialSetup() {
        Paint.setStatus("Initial setup");
        Logger.debugLog("Starting initialSetup() method.");

        // Withdrawing a needle from the bank
        Logger.debugLog("Withdrawing a needle from the bank.");
        if (!Bank.isSelectedQuantity1Button()) {
            Paint.setStatus("Set quantity 1");
            Bank.tapQuantity1Button();
            Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
        }
        Paint.setStatus("Tap search");
        Bank.tapSearchButton();
        Condition.sleep(generateDelay(400, 1000));

        Paint.setStatus("Type needle");
        String textToSend = "needle";
        for (char c : textToSend.toCharArray()) {
            String keycode = "KEYCODE_" + Character.toUpperCase(c);
            Client.sendKeystroke(keycode);
            Logger.debugLog("Sent keystroke: " + keycode);
        }

        Condition.sleep(generateDelay(1500, 2250));
        Paint.setStatus("Withdraw needle");
        Bank.withdrawItem(String.valueOf(needle), 0.75);
        Condition.sleep(generateDelay(400, 1000));
        Logger.debugLog("Withdrew needle from the bank.");

        Client.sendKeystroke("KEYCODE_ENTER");
        Condition.sleep(generateDelay(900,1100));
        Logger.debugLog("Closed search interface.");

        // Check if we have the needle in the inventory, otherwise stop script.
        Condition.wait(() -> Inventory.contains(needle, 0.75), 250,10);
        if (!Inventory.contains(needle, 0.75)) {
            Logger.log("No needle found in inventory, assuming we don't have one.");
            Bank.close();
            if (Bank.isOpen()) {
                Bank.close();
            }
            Logout.logout();
            Script.stop();
        }

        // Withdrawing thread from the bank
        Logger.debugLog("Withdrawing threads from the bank.");
        if (!Bank.isSelectedQuantityAllButton()) {
            Paint.setStatus("Set quantity all");
            Bank.tapQuantityAllButton();
            Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 12);
        }
        Paint.setStatus("Tap search");
        Bank.tapSearchButton();
        Condition.sleep(generateDelay(400, 1000));
        Bank.tapSearchButton();
        Condition.sleep(generateDelay(400, 1000));

        Paint.setStatus("Type thread");
        String textToSend2 = "thread";
        for (char c : textToSend2.toCharArray()) {
            String keycode = "KEYCODE_" + Character.toUpperCase(c);
            Client.sendKeystroke(keycode);
            Logger.debugLog("Sent keystroke: " + keycode);
        }

        Condition.sleep(generateDelay(1500, 2250));
        Paint.setStatus("Withdraw thread");
        Bank.withdrawItem(String.valueOf(thread), 0.75);
        Condition.sleep(generateDelay(400, 1000));
        Logger.debugLog("Withdrew threads from the bank.");

        Client.sendKeystroke("KEYCODE_ENTER");
        Condition.sleep(generateDelay(400, 1000));
        Paint.setStatus("Tap search");
        Bank.tapSearchButton();
        Condition.sleep(generateDelay(400, 1000));
        Logger.debugLog("Closed search interface.");

        // Check if we have the threads in the inventory, otherwise stop script.
        Condition.wait(() -> Inventory.contains(thread, 0.75), 250,10);
        if (!Inventory.contains(thread, 0.75)) {
            Logger.log("No threads found in inventory, assuming we don't have them banked.");
            Bank.close();
            if (Bank.isOpen()) {
                Bank.close();
            }
            Logout.logout();
            Script.stop();
        }

        // Grabbing the first leathers to process
        if (!Bank.isSelectedQuantityAllButton()) {
            Paint.setStatus("Set quantity all");
            Bank.tapQuantityAllButton();
            Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 12);
            Logger.debugLog("Selected Quantity All button.");

            // Selecting the right bank tab again if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Paint.setStatus("Open tab " + banktab);
                Bank.openTab(banktab);
                Logger.debugLog("Opened bank tab " + banktab);
            }

            // Withdraw first set of items
            Paint.setStatus("Withdraw leather");
            Bank.withdrawItem(String.valueOf(leather), 0.8);
            Logger.debugLog("Withdrew dragon leather from the bank.");

            // Check if we have the leather in the inventory, otherwise stop script.
            Condition.wait(() -> Inventory.contains(leather, 0.8), 250,10);
            if (!Inventory.contains(leather, 0.75)) {
                Logger.log("No dragonhide leather found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }
        } else {
            // Selecting the right bank tab again if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Paint.setStatus("Open tab " + banktab);
                Bank.openTab(banktab);
                Logger.debugLog("Opened bank tab " + banktab);
            }

            // Withdraw first set of items
            Paint.setStatus("Withdraw leather");
            Bank.withdrawItem(String.valueOf(leather), 0.8);

            // Check if we have all the items in the inventory, otherwise stop script.
            int[] items3 = {ItemList.NEEDLE_1733, ItemList.THREAD_1734, leather};
            Condition.wait(() -> Inventory.containsAll(items3, 0.8), 250,10);
            if (!Inventory.containsAll(items3, 0.75)) {
                Logger.log("Not all items found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }

            Logger.debugLog("Withdrew dragonhide leather from the bank.");
        }

        // Finishing off with closing the bank
        Logger.debugLog("Closing bank interface.");
        Paint.setStatus("Close bank");
        Bank.close();
        Logger.debugLog("Closed bank interface.");

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeCraftingMethod() {
        Paint.setStatus("Start crafting");
        Logger.debugLog("Starting executeCraftingMethod() method.");

        // Check if we have all items in the inventory.
        if (!Inventory.containsAll(new int[]{needle, thread, leather}, 0.8)) {
            Logger.log("We don't have all the crafting supplies in our inventory, going back to banking!");
            return;
        }

        // Starting to process items
        Paint.setStatus("Tap needle");
        Inventory.tapItem(needle, true, 0.8);
        Condition.sleep(generateDelay(100, 250));
        Paint.setStatus("Tap leather");
        Inventory.tapItem(leather, 0.8);
        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
        Paint.setStatus("Tap option " + makeOption);
        Chatbox.makeOption(makeOption);
        Logger.debugLog("Selected option " + makeOption + " in chatbox.");

        Condition.wait(() -> waitFinishCrafting(), 250, 200);

        Logger.debugLog("Ending the executeCraftingMethod() method.");
    }

    private void bank() {
        Paint.setStatus("Banking");
        Logger.debugLog("Starting bank() method.");

        // Opening the bank based on your location
        Logger.debugLog("Attempting to open the bank.");
        Paint.setStatus("Open bank");
        Bank.open(bankloc);
        Logger.debugLog("Bank is open.");

        // Check if bankpin is needed
        if (Bank.isBankPinNeeded()) {
            Paint.setStatus("Enter bank pin");
            Bank.enterBankPin();
        }

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Open tab " + banktab);
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Depositing items based on your product chosen
        Logger.debugLog("Depositing " + tier + " " + product + ".");
        Paint.setStatus("Deposit crafted items");
        Inventory.tapItem(depositProduct, 0.8);
        Condition.sleep(generateDelay(200,400));

        Paint.setStatus("Withdraw leather");
        Bank.withdrawItem(String.valueOf(leather), 0.8);
        Logger.debugLog("Withdrew dragonhide leather from the bank.");

        // Closing the bank, as banking should be done now
        Paint.setStatus("Close bank");
        Bank.close();
        Logger.debugLog("Closed the bank.");

        Logger.debugLog("Ending the bank() method.");
    }

    private void checkInventOpen() {
        // Check if the inventory is open (needs this check after a break)
        if (!GameTabs.isInventoryTabOpen()) {
            Paint.setStatus("Open inventory");
            GameTabs.openInventoryTab();
        }
    }

    private void checkInventCrafting() {
        Paint.setStatus("Check inventory");
        int[] items = {needle, thread, leather};
        // Check if we have all items needed in the inventory.
        if (!Inventory.containsAll(items, 0.8)) {
            Logger.log("1st check failed for all items in our inventory, going back to banking!");
            bank();
        }

        // Check if we have all items needed in the inventory.
        if (!Inventory.containsAll(items, 0.8)) {
            Logger.log("2nd check failed for all items in our inventory, logging out and aborting script!");
            Logout.logout();
            Script.stop();
        }
    }

    private boolean waitFinishCrafting() {
        readXP();

        if (Player.leveledUp()) {
            Paint.setStatus("Tap needle");
            Inventory.tapItem(needle, true, 0.8);
            Condition.sleep(generateDelay(100, 250));
            Paint.setStatus("Tap leather");
            Inventory.tapItem(leather, 0.8);
            Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
            Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
            Paint.setStatus("Tap option " + makeOption);
            Chatbox.makeOption(makeOption);
            Logger.debugLog("Selected option " + makeOption + " in chatbox.");
            Condition.sleep(generateDelay(1000, 1400));
        }

        // Wait for the inventory to finish (with a timeout) based on the method
        if (java.util.Objects.equals(product, "Body")) {
            boolean hasLessThan3Leather = Inventory.count(String.valueOf(leather), 0.8) < 3;
            boolean hasThread = Inventory.contains(ItemList.THREAD_1734, 0.8);

            return hasLessThan3Leather || !hasThread;
        } else if (java.util.Objects.equals(product, "Chaps")) {
            boolean hasLessThan2Leather = Inventory.count(String.valueOf(leather), 0.8) < 2;
            boolean hasThread = Inventory.contains(ItemList.THREAD_1734, 0.8);

            return hasLessThan2Leather || !hasThread;
        } else if (java.util.Objects.equals(product, "Vambraces")) {
            boolean hasLeather = Inventory.contains(leather, 0.8);
            boolean hasThread = Inventory.contains(ItemList.THREAD_1734, 0.8);

            return !hasLeather || !hasThread;
        } else {
            return false;
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

    private int generateDelay(int lowerEnd, int higherEnd) {
        if (lowerEnd > higherEnd) {
            // Swap lowerEnd and higherEnd if lowerEnd is greater
            int temp = lowerEnd;
            lowerEnd = higherEnd;
            higherEnd = temp;
        }
        return random.nextInt(higherEnd - lowerEnd + 1) + lowerEnd;
    }
}