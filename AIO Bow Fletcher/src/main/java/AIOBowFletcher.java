import helpers.*;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

@ScriptManifest(
        name = "AIO Bow Fletcher",
        description = "AIO Bow Fletcher, supports both cutting and stringing bows.",
        version = "1.31",
        category = ScriptCategory.Fletching
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

public class AIOBowFletcher extends AbstractScript {
    // Creating the strings for later use
    String method;
    String tier;
    String product;
    String bankloc;
    int banktab;
    boolean doneInitialSetup = false;
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
        banktab = Integer.parseInt(configs.get("BankTab"));

        initializeItemIDs();

        //Logs for debugging purposes
        logger.log("Thank you for using the AIO Bow Fletcher script!");
        System.out.println("Starting the AIO Bow Fletcher script!");
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
            setupItemIds();
            setupBanking();
            initialSetup();
        }

        if (Objects.equals(method, "Cut")) {
            checkInventOpen();
            checkInventCutMethod();
            executeCutMethod();
            bank();
        }

        else if (Objects.equals(method, "String")) {
            checkInventOpen();
            checkInventStringMethod();
            executeStringMethod();
            bank();
        }

    }

    private void initializeItemIDs() {
        logger.debugLog("Running the initializeItemIDs() method.");
        System.out.println("Running the initializeItemIDs() method.");

        itemIDs = new HashMap<>();

        // Map of itemIDs for LogID (1), UnstrungShortbowID (2), UnstrungLongbowID (3), StrungShortbowID (4) and StrungLongbowID (5)
        itemIDs.put("Logs", new String[] {"1511", "50", "48", "841", "839"});
        itemIDs.put("Oak logs", new String[] {"1521", "54", "56", "843", "845"});
        itemIDs.put("Willow logs", new String[] {"1519", "60", "58", "849", "847"});
        itemIDs.put("Maple logs", new String[] {"1517", "64", "62", "853", "851"});
        itemIDs.put("Yew logs", new String[] {"1515", "68", "66", "857", "855"});
        itemIDs.put("Magic logs", new String[] {"1513", "72", "70", "861", "859"});

        logger.debugLog("Ending the initializeItemIDs() method.");
        System.out.println("Ending the initializeItemIDs() method.");
    }

    private void setupItemIds() {
        logger.debugLog("Running the setupItemIds() method.");
        System.out.println("Running the setupItemIds() method.");
        if (longbow == null) {
            String[] itemIds = itemIDs.get(tier);
            logs = itemIds[0];
            shortbowU = itemIds[1];
            longbowU = itemIds[2];
            shortbow = itemIds[3];
            longbow = itemIds[4];

            logger.debugLog("Stored IDs for " + tier + ":\nLogs: " + logs + "\nUnstrung Shortbow: " + shortbowU + "\nUnstrung Longbow: " + longbowU + "\nShortbow: " + shortbow + "\nLongbow: " + longbow);
        }

        logger.debugLog("Ending the setupItemIds() method.");
        System.out.println("Ending the setupItemIds() method.");
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
                condition.sleep(1076);
            } else {
                logger.debugLog("Bank pin is not needed, bank is open!");
                logger.debugLog("Depositing inventory.");
                bank.tapDepositInventoryButton();
                condition.sleep(1076);
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

        // Selecting the right bank tab again if needed
        if (!bank.isSelectedBankTab(banktab)) {
            bank.openTab(banktab);
            logger.debugLog("Opened bank tab " + banktab);
        }

        // Part if the Cut method was chosen
        if (Objects.equals(method, "Cut")) {
            logger.debugLog("Cut method was selected.");

            // Withdrawing a knife from the bank
            logger.debugLog("Withdrawing a knife from the bank.");
            if (!bank.isSelectedQuantity1Button()) {
                bank.tapQuantity1Button();
                condition.wait(() -> bank.isSelectedQuantity1Button(), 200, 12);
            }
            bank.tapSearchButton();
            condition.sleep(randomDelay);

            String textToSend = "knife";
            for (char c : textToSend.toCharArray()) {
                String keycode = "KEYCODE_" + Character.toUpperCase(c);
                client.sendKeystroke(keycode);
                logger.debugLog("Sent keystroke: " + keycode);
            }

            condition.sleep(randomBiggerDelay);
            bank.withdrawItem(knife, 0.75);
            condition.sleep(randomDelay);
            logger.debugLog("Withdrew knife from the bank.");

            client.sendKeystroke("KEYCODE_ENTER");
            condition.sleep(randomDelay);
            logger.debugLog("Closed search interface.");

            // Grabbing the first items to process
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
                bank.withdrawItem(logs, 0.75);
                logger.debugLog("Withdrew " + tier +  " from the bank.");
            } else {
                // Withdraw first set of items
                bank.withdrawItem(logs, 0.75);
                logger.debugLog("Withdrew " + tier +  " from the bank.");
            }
        }

        // Part if the String method was chosen
        else if (Objects.equals(method, "String")) {
            int randomDelay2 = new Random().nextInt(300) + 200;
            int randomBiggerDelay2 = new Random().nextInt(400) + 600;
            logger.debugLog("String method was selected.");
            if (!bank.isSelectedQuantityCustomButton()) {
                Rectangle customQty = bank.findQuantityCustomButton();
                client.longPressWithinRectangle(customQty);
                condition.sleep(randomDelay2);
                client.tap(393, 499);
                condition.sleep(randomBiggerDelay2);
                client.sendKeystroke("KEYCODE_1");
                client.sendKeystroke("KEYCODE_4");
                client.sendKeystroke("KEYCODE_ENTER");
                logger.debugLog("Set custom quantity 14 for items in the bank.");
                condition.wait(() -> bank.isSelectedQuantityCustomButton(), 200, 12);

                // Withdraw first set of items
                if (Objects.equals(product, "Shortbow")) {
                    bank.withdrawItem(shortbowU, 0.75);
                } else {
                    bank.withdrawItem(longbowU, 0.75);
                }
                condition.sleep(randomDelay2);
                bank.withdrawItem(bowstring, 0.75);
                logger.debugLog("Withdrew 14 bowstrings and unstrung bows from the bank.");
            } else {
                // Withdraw first set of items
                if (Objects.equals(product, "Shortbow")) {
                    bank.withdrawItem(shortbowU, 0.75);
                } else {
                    bank.withdrawItem(longbowU, 0.75);
                }
                condition.sleep(randomDelay2);
                bank.withdrawItem(bowstring, 0.75);
                logger.debugLog("Withdrew 14 bowstrings and unstrung bows from the bank.");
            }
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

    private void executeCutMethod() {
        logger.debugLog("Starting executeCutMethod() method.");
        System.out.println("Running the executeCutMethod() method.");

        // Check if we have both a knife and the logs in the inventory.
        if (!inventory.contains(knife, 0.75) && !inventory.contains(logs, 0.75)) {
            logger.log("We don't have a knife and logs in our inventory, going back to banking!");
            System.out.println("We don't have a knife and logs in our inventory, going back to banking!");
            return;
        }

        // Starting to process items
        inventory.tapItem(knife, 0.75);
        int randomDelay = new Random().nextInt(500) + 500;
        int randomDelay2 = new Random().nextInt(300) + 200;
        System.out.println("Sleeping for randomDelay: " + randomDelay);
        logger.debugLog("Sleeping for randomDelay: " + randomDelay);
        condition.sleep(randomDelay2);
        inventory.tapItem(logs, 0.75);
        System.out.print("Waiting for the chatbox Make Menu to be visible...");
        logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        condition.wait(() -> chatbox.isMakeMenuVisible(), 200, 12);

        // tap option needed based on choice in config
        if (Objects.equals(product, "Shortbow")) {
            chatbox.makeOption(2);
            System.out.println("Selected option 2 in chatbox.");
            logger.debugLog("Selected option 2 in chatbox.");
        } else {
            chatbox.makeOption(3);
            System.out.println("Selected option 3 in chatbox.");
            logger.debugLog("Selected option 3 in chatbox.");
        }

        // Wait for the inventory to finish
        while (inventory.contains(logs, 0.75)) {
            readXP();
            int randomDelay3 = new Random().nextInt(2000) + 1000;
            System.out.println("Sleeping for randomDelay2: " + randomDelay2);
            logger.debugLog("Sleeping for randomDelay2: " + randomDelay2);
            condition.sleep(randomDelay3);
        }
        readXP();

        logger.debugLog("Ending the executeCutMethod() method.");
        System.out.println("Ending the executeCutMethod() method.");
    }

    private void executeStringMethod() {
        logger.debugLog("Starting executeStringMethod() method.");
        System.out.println("Running the executeStringMethod() method.");

        // Check if we have both unstrung bows and bowstrings in the inventory.
        if (Objects.equals(product, "Shortbow")) {
            if (!inventory.contains(shortbowU, 0.75) &  !inventory.contains(bowstring, 0.75)) {
                logger.log("We don't have unstrung bows and bowstring in our inventory, going back to banking!");
                System.out.println("We don't have unstrung bows and bowstring in our inventory, going back to banking!");
                return;
            }
        } else {
            if (!inventory.contains(longbowU, 0.75) &  !inventory.contains(bowstring, 0.75)) {
                logger.log("We don't have unstrung bows and bowstring in our inventory, going back to banking!");
                System.out.println("We don't have unstrung bows and bowstring in our inventory, going back to banking!");
                return;
            }
        }

        // tap item needed based on choice in config
        if (Objects.equals(product, "Shortbow")) {
            inventory.tapItem(shortbowU, 0.75);
        } else {
            inventory.tapItem(longbowU, 0.75);
        }

        int randomDelay = new Random().nextInt(500) + 500;
        int randomDelay2 = new Random().nextInt(300) + 200;
        int randomDelay3 = new Random().nextInt(2000) + 2000;
        System.out.println("Sleeping for randomDelay: " + randomDelay);
        logger.debugLog("Sleeping for randomDelay: " + randomDelay);
        condition.sleep(randomDelay2);
        inventory.tapItem(bowstring, 0.75);
        System.out.println("Waiting for the chatbox Make Menu to be visible...");
        logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        condition.wait(() -> chatbox.isMakeMenuVisible(), 200, 12);
        chatbox.makeOption(1);
        System.out.println("Selected option 1 in chatbox.");
        logger.debugLog("Selected option 1 in chatbox.");

        // Wait for the inventory to finish
        while (inventory.contains(bowstring, 0.75)) {
            readXP();
            System.out.println("Sleeping for: " + randomDelay3);
            logger.debugLog("Sleeping for: " + randomDelay3);
            condition.sleep(randomDelay3);
        }
        readXP();

        logger.debugLog("Ending the executeStringMethod() method.");
        System.out.println("Ending the executeStringMethod() method.");
    }

    private void bank() {
        logger.debugLog("Starting bank() method.");
        System.out.println("Running the bank() method.");
        int randomDelay = new Random().nextInt(500) + 500;
        int randomBiggerDelay = new Random().nextInt(500) + 1000;

        // Opening the bank based on your location
        logger.debugLog("Attempting to open the bank.");
        bank.open(bankloc);
        logger.debugLog("Bank is open.");

        // Select the right bank tab if needed.
        if (!bank.isSelectedBankTab(banktab)) {
            bank.openTab(banktab);
            logger.debugLog("Opened bank tab " + banktab);
        }

        // Depositing items based on your tier/method
        if (Objects.equals(method, "Cut")){
            if (Objects.equals(product, "Shortbow")) {
                System.out.println("Depositing unstrung shortbows.");
                logger.debugLog("Depositing unstrung shortbows.");
                inventory.tapItem(shortbowU, 0.75);
            } else {
                System.out.println("Depositing unstrung longbows.");
                logger.debugLog("Depositing unstrung longbows.");
                inventory.tapItem(longbowU, 0.75);
            }
        }
        else if (Objects.equals(method, "String")) {
            if (Objects.equals(product, "Shortbow")) {
                System.out.println("Depositing strung shortbows.");
                logger.debugLog("Depositing strung shortbows.");
                inventory.tapItem(shortbow, 0.75);
            } else {
                System.out.println("Depositing strung longbows.");
                logger.debugLog("Depositing strung longbows.");
                inventory.tapItem(longbow, 0.75);
            }
        }
        condition.sleep(randomBiggerDelay);

        // Withdrawing the items based on your tier/method
        if (Objects.equals(method, "Cut")) {
            bank.withdrawItem(logs, 0.75);
            System.out.println("Withdrew " + tier + " from the bank.");
            logger.debugLog("Withdrew " + tier + " from the bank.");
            condition.sleep(randomDelay);
        }
        else if (Objects.equals(method, "String")) {

            // Withdraw unstrung bow that was picked in the config
            if (Objects.equals(product, "Shortbow")) {
                System.out.println("Withdrawing unstrung shortbows");
                logger.debugLog("Withdrawing unstrung shortbows.");
                bank.withdrawItem(shortbowU, 0.75);
            } else {
                System.out.println("Withdrawing unstrung longbows");
                logger.debugLog("Withdrawing unstrung longbows.");
                bank.withdrawItem(longbowU, 0.75);
            }
            condition.sleep(randomDelay);

            // Withdraw bowstrings
            System.out.println("Withdrawing bowstrings.");
            logger.debugLog("Withdrawing bowstrings.");
            bank.withdrawItem(bowstring, 0.75);
            condition.sleep(randomDelay);
            System.out.println("Withdrew items from the bank.");
            logger.debugLog("Withdrew items from the bank.");
        }

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

    private void checkInventCutMethod() {
        // Check if we have both a knife and the logs in the inventory.
        if (!inventory.contains(knife, 0.75) && !inventory.contains(logs, 0.75)) {
            logger.log("1st check failed for knife and logs in our inventory, going back to banking!");
            System.out.println("1st check failed for a knife and logs in our inventory, going back to banking!");
            bank();
        }

        // Check if we have both a knife and the logs in the inventory.
        if (!inventory.contains(knife, 0.75) && !inventory.contains(logs, 0.75)) {
            logger.log("2nd check failed for knife and logs in our inventory, logging out and aborting script!");
            System.out.println("2nd check failed for a knife and logs in our inventory, logging out and aborting script!");
            logout.logout();
            script.forceStop();
        }
    }

    private void checkInventStringMethod() {
        // Check if we have both unstrung bows and bowstrings in the inventory.
        if (Objects.equals(product, "Shortbow")) {
            if (!inventory.contains(shortbowU, 0.75) &  !inventory.contains(bowstring, 0.75)) {
                logger.log("1st check failed for unstrung bows and bowstring in our inventory, going back to banking!");
                System.out.println("1st check failed for unstrung bows and bowstring in our inventory, going back to banking!");
                bank();
            }
        } else {
            if (!inventory.contains(longbowU, 0.75) &  !inventory.contains(bowstring, 0.75)) {
                logger.log("1st check failed for unstrung bows and bowstring in our inventory, going back to banking!");
                System.out.println("1st check failed for unstrung bows and bowstring in our inventory, going back to banking!");
                bank();
            }
        }

        // Check if we have both unstrung bows and bowstrings in the inventory.
        if (Objects.equals(product, "Shortbow")) {
            if (!inventory.contains(shortbowU, 0.75) &  !inventory.contains(bowstring, 0.75)) {
                logger.log("2nd check failed for unstrung bows and bowstring in our inventory, logging out and aborting script!");
                System.out.println("2nd check failed for unstrung bows and bowstring in our inventory, logging out and aborting script!");
                logout.logout();
                script.forceStop();
            }
        } else {
            if (!inventory.contains(longbowU, 0.75) &  !inventory.contains(bowstring, 0.75)) {
                logger.log("2nd check failed for unstrung bows and bowstring in our inventory, logging out and aborting script!");
                System.out.println("2nd check failed for unstrung bows and bowstring in our inventory, logging out and aborting script!");
                logout.logout();
                script.forceStop();
            }
        }
    }

    private void readXP() {
        xpBar.getXP();
    }

}