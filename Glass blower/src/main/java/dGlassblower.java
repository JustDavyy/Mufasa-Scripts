import helpers.*;
import helpers.utils.OptionType;

import java.util.Map;
import java.util.HashMap;
import static helpers.Interfaces.*;
import static helpers.Interfaces.Logout;

import java.util.Random;

@ScriptManifest(
        name = "dGlass blower",
        description = "Blows molten glass into glass objects to train crafting. Supports all options and dynamic banking.",
        version = "1.03",
        guideLink = "https://wiki.mufasaclient.com/docs/dglass-blower/",
        categories = {ScriptCategory.Crafting}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What glass product would you like to make?",
                        defaultValue = "Lantern lens",
                        allowedValues = {
                                @AllowedValue(optionIcon = "1919", optionName = "Beer glass"),
                                @AllowedValue(optionIcon = "4527", optionName = "Empty candle lantern"),
                                @AllowedValue(optionIcon = "4525", optionName = "Empty oil lamp"),
                                @AllowedValue(optionIcon = "229", optionName = "Vial"),
                                @AllowedValue(optionIcon = "6667", optionName = "Empty fishbowl"),
                                @AllowedValue(optionIcon = "567", optionName = "Unpowered orb"),
                                @AllowedValue(optionIcon = "4542", optionName = "Lantern lens"),
                                @AllowedValue(optionIcon = "10980", optionName = "Empty light orb")
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

public class dGlassblower extends AbstractScript {
    // Creating the strings for later use
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String product;
    String bankloc;
    int banktab;
    int makeOption;
    int optionInt;
    String optionItemID;
    String moltenglass = "1775";
    String glassblowingpipe = "1785";
    Map<String, String[]> makeOptions;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        initializeMakeOptions();

        // One-time setup
        setupMakeOptions();
        hopActions();
        setupBanking();
        initialSetup();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dGlass blower script!");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        checkInventOpen();
        checkInventGlassblowing();
        executeGlassblowingMethod();
        bank();
        hopActions();

    }

    private void initializeMakeOptions() {
        Logger.debugLog("Running the initializeMakeOptions() method.");

        makeOptions = new HashMap<>();

        // Map of make options, where the key is the item name, and the value is an array of [make option, itemID]
        makeOptions.put("Beer glass", new String[]{"1", "1919"});
        makeOptions.put("Empty candle lantern", new String[]{"2", "4527"});
        makeOptions.put("Empty oil lamp", new String[]{"3", "4525"});
        makeOptions.put("Vial", new String[]{"4", "229"});
        makeOptions.put("Empty fishbowl", new String[]{"5", "6667"});
        makeOptions.put("Unpowered orb", new String[]{"6", "567"});
        makeOptions.put("Lantern lens", new String[]{"7", "4542"});
        makeOptions.put("Empty light orb", new String[]{"8", "10980"});

        Logger.debugLog("Ending the initializeMakeOptions() method.");
    }

    private void setupMakeOptions() {
        Logger.debugLog("Running the setupMakeOptions() method.");
        if (makeOption == 0) {
            String[] makeOptionData = makeOptions.get(product);
            optionInt = Integer.parseInt(makeOptionData[0]);
            optionItemID = makeOptionData[1];

            Logger.debugLog("\nMake option int number: " + optionInt + " \nitemID of final product: " + optionItemID);
        }

        Logger.debugLog("Ending the setupMakeOptions() method.");
    }

    private void setupBanking() {
        Logger.debugLog("Starting setupBanking() method.");
        if (bankloc == null) {
            Logger.debugLog("Starting dynamic banking setup...");

            // Opening the inventory if not yet opened.
            Logger.debugLog("Opening up the inventory.");
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
            Logger.debugLog("Attempting to open the Bank of Gielinor.");
            Bank.open(bankloc);
            Logger.debugLog("Bank interface detected!");
            if (Bank.isBankPinNeeded()) {
                Logger.debugLog("Bank pin is needed!");
                Bank.enterBankPin();
                Condition.sleep(500);
                Condition.wait(() -> Bank.isOpen(), 200, 12);
                Logger.debugLog("Bank pin entered.");
                Logger.debugLog("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(677);
            } else {
                Logger.debugLog("Bank pin is not needed, bank is open!");
                Logger.debugLog("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(705);
            }
        }
        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void initialSetup() {
        Logger.debugLog("Starting initialSetup() method.");

        int randomDelay = new Random().nextInt(600) + 600;
        int randomBiggerDelay = new Random().nextInt(1500) + 1500;

        // Withdrawing a glassblowing pipe from the bank
        Logger.debugLog("Withdrawing a glassblowing pipe from the bank.");
        if (!Bank.isSelectedQuantity1Button()) {
            Bank.tapQuantity1Button();
            Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
        }
        Bank.tapSearchButton();
        Condition.sleep(randomDelay);

        String textToSend = "glassblowing";
        for (char c : textToSend.toCharArray()) {
            String keycode = "KEYCODE_" + Character.toUpperCase(c);
            Client.sendKeystroke(keycode);
            Logger.debugLog("Sent keystroke: " + keycode);
        }

        Condition.sleep(randomBiggerDelay);
        Bank.withdrawItem(glassblowingpipe, 0.75);
        Condition.sleep(randomDelay);
        Logger.debugLog("Withdrew glassblowing pipe from the bank.");

        Client.sendKeystroke("KEYCODE_ENTER");
        Condition.sleep(randomDelay);
        Logger.debugLog("Closed search interface.");

        // Check if we have the glassblowing pipe in the inventory, otherwise stop script.
        Condition.wait(() -> Inventory.contains(glassblowingpipe, 0.75), 250,10);
        if (!Inventory.contains(glassblowingpipe, 0.75)) {
            Logger.log("No glassblowing pipe found in inventory, assuming we're out of items to process.");
            Bank.close();
            if (Bank.isOpen()) {
                Bank.close();
            }
            Logout.logout();
            Script.stop();
        }

        // Grabbing the first molten glass to process
        if (!Bank.isSelectedQuantityAllButton()) {
            Bank.tapQuantityAllButton();
            Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 12);
            Logger.debugLog("Selected Quantity All button.");

            // Selecting the right bank tab again if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Bank.openTab(banktab);
                Logger.debugLog("Opened bank tab " + banktab);
            }

            // Withdraw first set of items
            Bank.withdrawItem(moltenglass, 0.75);
            Logger.debugLog("Withdrew molten glass from the bank.");

            // Check if we have the molten glass in the inventory, otherwise stop script.
            Condition.wait(() -> Inventory.contains(moltenglass, 0.75), 250,10);
            if (!Inventory.contains(moltenglass, 0.75)) {
                Logger.log("No molten glass found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }
        } else {
            // Withdraw first set of items
            Bank.withdrawItem(moltenglass, 0.75);

            // Check if we have both molten glass and a glassblowing pipe in the inventory, otherwise stop script.
            String[] items3 = {glassblowingpipe, moltenglass};
            Condition.wait(() -> Inventory.contains(items3, 0.75), 250,10);
            if (!Inventory.contains(items3, 0.75)) {
                Logger.log("No items found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }

            Logger.debugLog("Withdrew molten glass from the bank.");
        }

        // Finishing off with closing the bank
        Logger.debugLog("Closing bank interface.");
        Bank.close();
        Logger.debugLog("Closed bank interface.");

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeGlassblowingMethod() {
        Logger.debugLog("Starting executeGlassblowingMethod() method.");

        // Check if we have both a glassblowing pipe and molten glass in the inventory.
        if (!Inventory.contains(glassblowingpipe, 0.75) && !Inventory.contains(moltenglass, 0.75)) {
            Logger.log("We don't have a glassblowing pipe and molten glass in our inventory, going back to banking!");
            return;
        }

        // Starting to process items
        Inventory.tapItem(glassblowingpipe, 0.75);
        int randomDelay2 = new Random().nextInt(150) + 100;
        int randomDelay3 = new Random().nextInt(1500) + 500;
        Condition.sleep(randomDelay2);
        Inventory.tapItem(moltenglass, 0.75);
        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
        Chatbox.makeOption(optionInt);
        Logger.debugLog("Selected option " + optionInt + " in chatbox.");

        // Wait for the inventory to finish (with a timeout)
        long startTime = System.currentTimeMillis();
        long timeout = 60 * 1000; // 60 seconds in milliseconds as a full invent is about 45-50 seconds.
        while (Inventory.contains(moltenglass, 0.75)) {
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

        Logger.debugLog("Ending the executeGlassblowingMethod() method.");
    }

    private void bank() {
        Logger.debugLog("Starting bank() method.");
        int randomDelay = new Random().nextInt(250) + 250;

        // Opening the bank based on your location
        Logger.debugLog("Attempting to open the bank.");
        Bank.open(bankloc);
        Logger.debugLog("Bank is open.");

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Depositing items based on your product chosen
        Logger.debugLog("Depositing " + product + ".");
        Inventory.tapItem(optionItemID, 0.75);
        Condition.sleep(randomDelay);

        Bank.withdrawItem(moltenglass, 0.75);
        Logger.debugLog("Withdrew molten glass from the bank.");

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

    private void checkInventGlassblowing() {
        String[] items = {glassblowingpipe, moltenglass};
        // Check if we have both a glassblowing pipe and molten glass in the inventory.
        if (!Inventory.contains(items, 0.75)) {
            Logger.log("1st check failed for a glassblowing pipe and molten glass in our inventory, going back to banking!");
            bank();
        }

        // Check if we have both a glassblowing pipe and molten glass in the inventory.
        if (!Inventory.contains(items, 0.75)) {
            Logger.log("2nd check failed for a glassblowing pipe and molten glass in our inventory, logging out and aborting script!");
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

    private void readXP() {
        XpBar.getXP();
    }

}