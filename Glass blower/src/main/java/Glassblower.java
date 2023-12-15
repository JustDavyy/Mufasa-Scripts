import helpers.*;
import helpers.utils.OptionType;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

@ScriptManifest(
        name = "Glass blower",
        description = "Blows molten glass into glass objects to train crafting. Supports all options and dynamic banking.",
        version = "1.00",
        category = ScriptCategory.Crafting
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
                        name =  "BankTab",
                        description = "What bank tab are your resources located in?",
                        defaultValue = "0",
                        minMaxIntValues = {0, 9},
                        allowedValues = {
                                @AllowedValue(optionName = "0"),
                                @AllowedValue(optionName = "1"),
                                @AllowedValue(optionName = "2"),
                                @AllowedValue(optionName = "3"),
                                @AllowedValue(optionName = "4"),
                                @AllowedValue(optionName = "5"),
                                @AllowedValue(optionName = "6"),
                                @AllowedValue(optionName = "7"),
                                @AllowedValue(optionName = "8"),
                                @AllowedValue(optionName = "9"),
                        },
                        optionType = OptionType.INTEGER
                )
        }
)

public class Glassblower extends AbstractScript {
    // Creating the strings for later use
    String product;
    String bankloc;
    String itemID;
    int banktab;
    int makeOption;
    int optionInt;
    String optionItemID;
    boolean doneInitialSetup = false;
    String moltenglass = "1775";
    String glassblowingpipe = "1785";
    Map<String, String[]> makeOptions;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");
        banktab = Integer.parseInt(configs.get("BankTab"));

        initializeMakeOptions();

        //Logs for debugging purposes
        logger.log("Thank you for using the Glass blower script!");
        System.out.println("Starting the Glass blower script!");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {
        logger.debugLog("Running the poll() method.");
        System.out.println("Running the poll() method.");

        if (!doneInitialSetup) {
            logger.debugLog("doneInitialSetup is false, running initial setups.");

            // Check if we are logged in, if not, login.
            if (login.findPlayNowOption() != null) {
                logger.debugLog("We are not logged in yet, logging in.");
                System.out.println("We are not logged in yet, logging in.");
                login.presetup();
            }

            // Continue the rest of the setup
            setupMakeOptions();
            setupBanking();
            initialSetup();
        }

        checkInventOpen();
        checkInventGlassblowing();
        executeGlassblowingMethod();
        bank();

    }

    private void initializeMakeOptions() {
        logger.debugLog("Running the initializeMakeOptions() method.");
        System.out.println("Running the initializeMakeOptions() method.");

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

        String[] makeOptionData = makeOptions.get(product);

        if (makeOptionData != null) {
            int makeOption = Integer.parseInt(makeOptionData[0]);
            String itemID = makeOptionData[1];

            System.out.println("The make option for " + product + " is: " + makeOption);
            System.out.println("The itemID for " + product + " is: " + itemID);

            logger.debugLog("The make option for " + product + " is: " + makeOption);
            logger.debugLog("The itemID for " + product + " is: " + itemID);
        } else {
            System.out.println("Product not found in makeOptions map.");
            logger.debugLog("Product not found in makeOptions map.");
        }

        logger.debugLog("Ending the initializeMakeOptions() method.");
        System.out.println("Ending the initializeMakeOptions() method.");
    }

    private void setupMakeOptions() {
        logger.debugLog("Running the setupMakeOptions() method.");
        System.out.println("Running the setupMakeOptions() method.");
        if (makeOption == 0) {
            String[] makeOptionData = makeOptions.get(product);
            int optionInt = Integer.parseInt(makeOptionData[0]);
            String optionItemID = makeOptionData[1];

            logger.debugLog("\nMake option int number: " + optionInt + " \nitemID of final product: " + optionItemID);
        }

        logger.debugLog("Ending the setupMakeOptions() method.");
        System.out.println("Ending the setupMakeOptions() method.");
    }

    private void setupBanking() {
        logger.debugLog("Starting setupBanking() method.");
        System.out.println("Running the setupBanking() method.");
        if (bankloc == null) {
            logger.debugLog("Starting dynamic banking setup...");

            // Opening the inventory if not yet opened.
            logger.debugLog("Opening up the inventory.");
            if (!gameTabs.isInventoryTabOpen()) {
                gameTabs.openInventoryTab();
            }

            logger.debugLog("Starting setup for Dynamic Banking.");
            bankloc = bank.setupDynamicBank();
            logger.debugLog("We're located at: " + bankloc + ".");
            if (bankloc == null) {
                logger.debugLog("Could not find a dynamic bank location we are in, logging out and aborting script.");
                System.out.println("Could not find a dynamic bank location we are in, logging out and aborting script.");
                logout.logout();
                script.forceStop();
            }
            condition.sleep(5000);
            logger.debugLog("Attempting to open the Bank of Gielinor.");
            bank.open(bankloc);
            logger.debugLog("Bank interface detected!");
            if (bank.isBankPinNeeded()) {
                logger.debugLog("Bank pin is needed!");
                bank.enterBankPin();
                condition.sleep(500);
                condition.wait(() -> bank.isOpen(), 200, 12);
                logger.debugLog("Bank pin entered.");
                logger.debugLog("Depositing inventory.");
                bank.tapDepositInventoryButton();
                condition.sleep(677);
            } else {
                logger.debugLog("Bank pin is not needed, bank is open!");
                logger.debugLog("Depositing inventory.");
                bank.tapDepositInventoryButton();
                condition.sleep(705);
            }
        }
        logger.debugLog("Ending the setupBanking() method.");
        System.out.println("Ending the setupBanking() method.");
    }

    private void initialSetup() {
        logger.debugLog("Starting initialSetup() method.");
        System.out.println("Running the initialSetup() method.");

        int randomDelay = new Random().nextInt(600) + 600;
        int randomBiggerDelay = new Random().nextInt(1500) + 1500;

        // Withdrawing a glassblowing pipe from the bank
        logger.debugLog("Withdrawing a glassblowing pipe from the bank.");
        if (!bank.isSelectedQuantity1Button()) {
            bank.tapQuantity1Button();
            condition.wait(() -> bank.isSelectedQuantity1Button(), 200, 12);
        }
        bank.tapSearchButton();
        condition.sleep(randomDelay);

        String textToSend = "glassblowing";
        for (char c : textToSend.toCharArray()) {
            String keycode = "KEYCODE_" + Character.toUpperCase(c);
            client.sendKeystroke(keycode);
            logger.debugLog("Sent keystroke: " + keycode);
        }

        condition.sleep(randomBiggerDelay);
        bank.withdrawItem(glassblowingpipe, 0.75);
        condition.sleep(randomDelay);
        logger.debugLog("Withdrew glassblowing pipe from the bank.");

        client.sendKeystroke("KEYCODE_ENTER");
        condition.sleep(randomDelay);
        logger.debugLog("Closed search interface.");

        // Check if we have the glassblowing pipe in the inventory, otherwise stop script.
        condition.wait(() -> inventory.contains(glassblowingpipe, 0.75), 250,10);
        if (!inventory.contains(glassblowingpipe, 0.75)) {
            logger.log("No knife found in inventory, assuming we're out of items to process.");
            System.out.println("No knife found in inventory, assuming we're out of items to process.");
            bank.close();
            if (bank.isOpen()) {
                bank.close();
            }
            logout.logout();
            script.forceStop();
        }

        // Grabbing the first molten glass to process
        if (!bank.isSelectedQuantityAllButton()) {
            bank.tapQuantityAllButton();
            condition.wait(() -> bank.isSelectedQuantityAllButton(), 200, 12);
            logger.debugLog("Selected Quantity All button.");

            // Selecting the right bank tab again if needed
            if (!bank.isSelectedBankTab(banktab)) {
                bank.openTab(banktab);
                logger.debugLog("Opened bank tab " + banktab);
            }

            // Withdraw first set of items
            bank.withdrawItem(moltenglass, 0.75);
            logger.debugLog("Withdrew molten glass from the bank.");

            // Check if we have the molten glass in the inventory, otherwise stop script.
            condition.wait(() -> inventory.contains(moltenglass, 0.75), 250,10);
            if (!inventory.contains(moltenglass, 0.75)) {
                logger.log("No molten glass found in inventory, assuming we're out of items to process.");
                System.out.println("No molten glass found in inventory, assuming we're out of items to process.");
                bank.close();
                if (bank.isOpen()) {
                    bank.close();
                }
                logout.logout();
                script.forceStop();
            }
        } else {
            // Withdraw first set of items
            bank.withdrawItem(moltenglass, 0.75);

            // Check if we have both molten glass and a glassblowing pipe in the inventory, otherwise stop script.
            String[] items3 = {glassblowingpipe, moltenglass};
            condition.wait(() -> inventory.contains(items3, 0.75), 250,10);
            if (!inventory.contains(items3, 0.75)) {
                logger.log("No items found in inventory, assuming we're out of items to process.");
                System.out.println("No items found in inventory, assuming we're out of items to process.");
                bank.close();
                if (bank.isOpen()) {
                    bank.close();
                }
                logout.logout();
                script.forceStop();
            }

            logger.debugLog("Withdrew molten glass from the bank.");
        }

        // Finishing off with closing the bank
        logger.debugLog("Closing bank interface.");
        bank.close();
        logger.debugLog("Closed bank interface.");

        doneInitialSetup = true;
        logger.debugLog("Set the doneInitialSetup value to true.");

        logger.debugLog("Ending the initialSetup() method.");
        System.out.println("Ending the initialSetup() method.");
    }

    private void executeGlassblowingMethod() {
        logger.debugLog("Starting executeGlassblowingMethod() method.");
        System.out.println("Running the executeGlassblowingMethod() method.");

        // Check if we have both a knife and the logs in the inventory.
        if (!inventory.contains(glassblowingpipe, 0.75) && !inventory.contains(moltenglass, 0.75)) {
            logger.log("We don't have a glassblowing pipe and molten glass in our inventory, going back to banking!");
            System.out.println("We don't have a glassblowing pipe and molten glass in our inventory, going back to banking!");
            return;
        }

        // Starting to process items
        inventory.tapItem(glassblowingpipe, 0.75);
        int randomDelay2 = new Random().nextInt(150) + 100;
        int randomDelay3 = new Random().nextInt(1500) + 500;
        condition.sleep(randomDelay2);
        inventory.tapItem(moltenglass, 0.75);
        System.out.print("Waiting for the chatbox Make Menu to be visible...");
        logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        condition.wait(() -> chatbox.isMakeMenuVisible(), 200, 12);
        chatbox.makeOption(optionInt);
        logger.debugLog("Selected option " + optionInt + " in chatbox.");

        // Wait for the inventory to finish (with a timeout)
        long startTime = System.currentTimeMillis();
        long timeout = 60 * 1000; // 60 seconds in milliseconds as a full invent is about 45-50 seconds.
        while (inventory.contains(moltenglass, 0.75)) {
            readXP();
            condition.sleep(randomDelay3);

            // Check if we have passed the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                System.out.println("Timeout reached for inventory.contains() method");
                logger.debugLog("Timeout reached for inventory.contains() method");
                break;
            }
        }
        readXP();

        logger.debugLog("Ending the executeGlassblowingMethod() method.");
        System.out.println("Ending the executeGlassblowingMethod() method.");
    }

    private void bank() {
        logger.debugLog("Starting bank() method.");
        System.out.println("Running the bank() method.");
        int randomDelay = new Random().nextInt(250) + 250;

        // Opening the bank based on your location
        logger.debugLog("Attempting to open the bank.");
        bank.open(bankloc);
        logger.debugLog("Bank is open.");

        // Select the right bank tab if needed.
        if (!bank.isSelectedBankTab(banktab)) {
            bank.openTab(banktab);
            logger.debugLog("Opened bank tab " + banktab);
        }

        // Depositing items based on your product chosen
        System.out.println("Depositing " + product + ".");
        logger.debugLog("Depositing " + product + ".");
        inventory.tapItem(optionItemID, 0.75);
        condition.sleep(randomDelay);

        bank.withdrawItem(moltenglass, 0.75);
        System.out.println("Withdrew molten glass from the bank.");
        logger.debugLog("Withdrew molten glass from the bank.");

        // Closing the bank, as banking should be done now
        bank.close();
        System.out.println("Closed the bank.");
        logger.debugLog("Closed the bank.");

        logger.debugLog("Ending the bank() method.");
        System.out.println("Ending the bank() method.");
    }

    private void checkInventOpen() {
        // Check if the inventory is open (needs this check after a break)
        if (!gameTabs.isInventoryTabOpen()) {
            gameTabs.openInventoryTab();
        }
    }

    private void checkInventGlassblowing() {
        String[] items = {glassblowingpipe, moltenglass};
        // Check if we have both a glassblowing pipe and molten glass in the inventory.
        if (!inventory.contains(items, 0.75)) {
            logger.log("1st check failed for a glassblowing pipe and molten glass in our inventory, going back to banking!");
            System.out.println("1st check failed for a glassblowing pipe and molten glass in our inventory, going back to banking!");
            bank();
        }

        // Check if we have both a glassblowing pipe and molten glass in the inventory.
        if (!inventory.contains(items, 0.75)) {
            logger.log("2nd check failed for a glassblowing pipe and molten glass in our inventory, logging out and aborting script!");
            System.out.println("2nd check failed for a a glassblowing pipe and molten glass in our inventory, logging out and aborting script!");
            logout.logout();
            script.forceStop();
        }
    }

    private void readXP() {
        xpBar.getXP();
    }

}