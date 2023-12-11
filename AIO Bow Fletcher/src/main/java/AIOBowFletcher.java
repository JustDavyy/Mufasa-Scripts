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
        version = "1.0",
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
    String initialsetup;
    String logs;
    String shortbowU;
    String longbowU;
    String shortbow;
    String longbow;
    String bowstring = "1777";
    String knife = "946";
    private Map<String, String[]> itemIDs;

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        method = configs.get("Method");
        tier = configs.get("Tier");
        product = configs.get("Product");
        banktab = Integer.parseInt(configs.get("BankTab"));

        initializeItemIDs();

        //Logs for debugging purposes
        logger.log("Thank you for using the AIO Bow Fletcher script!");
        System.out.println("Starting the AIO Bow Fletcher script!");
    }

    private void initializeItemIDs() {
        itemIDs = new HashMap<>();

        // Map of itemIDs for LogID (1), UnstrungShortbowID (2), UnstrungLongbowID (3), StrungShortbowID (4) and StrungLongbowID (5)
        itemIDs.put("Logs", new String[] {"1511", "50", "48", "841", "839"});
        itemIDs.put("Oak logs", new String[] {"1521", "54", "56", "843", "845"});
        itemIDs.put("Willow logs", new String[] {"1519", "60", "58", "849", "847"});
        itemIDs.put("Maple logs", new String[] {"1517", "64", "62", "853", "851"});
        itemIDs.put("Yew logs", new String[] {"1515", "68", "66", "857", "855"});
        itemIDs.put("Magic logs", new String[] {"1513", "72", "70", "861", "859"});
    }

    @Override
    public void poll() {
        // Setting the correct IDs to run the script
        if (longbow == null) {
            String[] itemIds = itemIDs.get(tier);
            logs = itemIds[0];
            shortbowU = itemIds[1];
            longbowU = itemIds[2];
            shortbow = itemIds[3];
            longbow = itemIds[4];

            System.out.println("Stored IDs for " + tier + ":\nLogs: " + logs + "\nUnstrung Shortbow: " + shortbowU + "\nUnstrung Longbow: " + longbowU + "\nShortbow: " + shortbow + "\nLongbow: " + longbow);
        }

        // Dynamic banking (and initially open the bank)
        if (bankloc == null) {

            // Opening the inventory if not yet opened.
            logger.debugLog("Opening up the inventory.");
            if (!gameTabs.isInventoryTabOpen()) {
                gameTabs.openInventoryTab();
            }

            logger.debugLog("Starting setup for Dynamic Banking.");
            bankloc = bank.setupDynamicBank();
            logger.log("We're located at: " + bankloc + ".");
            condition.sleep(5000);
            logger.debugLog("Attempting to open the Bank of Gielinor.");
            bank.open(bankloc);
            condition.wait(() -> bank.isOpen(), 250, 12);
            logger.debugLog("Bank interface detected!");
            if (bank.isBankPinNeeded()) {
                logger.log("Bank pin is needed!");
                bank.enterBankPin();
                condition.sleep(2000);
                logger.log("Bank pin entered.");
                bank.openTab(banktab);
                condition.wait(() -> bank.isSelectedBankTab(banktab), 250, 12);
            } else {
                logger.log("Bank pin is not needed, bank is open!");
            }
        }

        // Check if knife is present in Inventory, otherwise withdraw it from the bank.
        if (!inventory.contains(knife, 0.90)) {
            logger.debugLog("Inventory doesn't contain a knife, withdrawing it from the bank.");
            if (!bank.isSelectedQuantity1Button()) {
                bank.tapQuantity1Button();
                condition.wait(() -> bank.isSelectedQuantity1Button(), 500, 10);
            }
            bank.tapSearchButton();
            condition.sleep(1000);

            // Send keystroke for each character
            String textToSend = "knife";
            for (char c : textToSend.toCharArray()) {
                String keycode = "KEYCODE_" + Character.toUpperCase(c);
                client.sendKeystroke(keycode);
            }

            condition.sleep(3000);
            bank.withdrawItem(knife, 0.90);
            condition.sleep(500);

            // Send keystroke to close the search interface
            client.sendKeystroke("KEYCODE_ENTER");
            condition.sleep(1000);

            if (!bank.isSelectedBankTab(banktab)) {
                bank.openTab(banktab);
                condition.wait(() -> bank.isSelectedBankTab(banktab), 250, 12);
            }
        }

        // Specific setup for "cut" method
        if (Objects.equals(method, "Cut")) {
            logger.log("Cut method was selected.");
            if (!bank.isSelectedQuantityAllButton()) {
                bank.tapQuantityAllButton();
                condition.wait(() -> bank.isSelectedQuantityAllButton(), 1000, 5);

                // Withdraw first set of items
                bank.withdrawItem(logs, 0.90);
            }
        }

        // Specific setup for "string" method
        if (Objects.equals(method, "String")){
            logger.log("String method was selected.");
            if (!bank.isSelectedQuantityCustomButton()) {
                Rectangle customQty = bank.findQuantityCustomButton();
                client.longPressWithinRectangle(customQty);
                condition.sleep(500);
                client.tap(393, 499);
                condition.sleep(1000);
                client.sendKeystroke("KEYCODE_1");
                client.sendKeystroke("KEYCODE_4");
                client.sendKeystroke("KEYCODE_ENTER");

                condition.wait(() -> bank.isSelectedQuantityCustomButton(), 500, 10);

                // Withdraw first set of items
                if (Objects.equals(product, "Shortbow")) {
                    bank.withdrawItem(shortbowU, 0.90);
                } else {
                    bank.withdrawItem(longbowU, 0.90);
                }
                bank.withdrawItem(bowstring, 0.90);
            }
        }

        // End the initial setup
        if (!Objects.equals(initialsetup, "done")) {
            logger.debugLog("Closing bank interface.");
            bank.close();
            condition.sleep(1500);
            initialsetup = "done";
        }

        // ---------------------------------------------------------------- //

        // Main script logic for cut method
        if (Objects.equals(method, "Cut")) {
            inventory.tapItem(knife, 0.90);
            int randomDelay = new Random().nextInt(251) + 50;
            int randomBiggerDelay = new Random().nextInt(200) + 400;
            condition.sleep(randomDelay);
            inventory.tapItem(logs, 0.90);
            condition.wait(() -> chatbox.isMakeMenuVisible(), 250, 12);

            // tap option needed based on choice in config
            if (Objects.equals(product, "Shortbow")) {
                chatbox.makeOption(2);
            } else {
                chatbox.makeOption(3);
            }

            // Wait for the inventory to finish
            while (inventory.contains(logs, 0.90)) {
                xpBar.getXP();
                int randomDelay2 = new Random().nextInt(2000) + 1000;
                condition.sleep(randomDelay2);
            }
            xpBar.getXP();

            bank.open(bankloc);
            condition.wait(() -> bank.isOpen(), 250, 12);
            if (!bank.isSelectedBankTab(banktab)) {
                bank.openTab(banktab);
                condition.wait(() -> bank.isSelectedBankTab(banktab), 250, 12);
            }
            // bank item needed based on choice in config
            if (Objects.equals(product, "Shortbow")) {
                inventory.tapItem(shortbowU, 0.90);
            } else {
                inventory.tapItem(longbowU, 0.90);
            }
            condition.sleep(randomBiggerDelay);

            bank.withdrawItem(logs, 0.90);
            condition.sleep(randomDelay);
            bank.close();
            condition.sleep(randomBiggerDelay);
        }

        // Main script logic for string method
        if (Objects.equals(method, "String")){
            if (Objects.equals(product, "Shortbow")) {
                inventory.tapItem(shortbowU, 0.90);
            } else {
                inventory.tapItem(longbowU, 0.90);
            }
            int randomDelay = new Random().nextInt(251) + 50;
            int randomBiggerDelay = new Random().nextInt(200) + 400;
            condition.sleep(randomDelay);
            inventory.tapItem(bowstring, 0.90);
            condition.wait(() -> chatbox.isMakeMenuVisible(), 250, 12);
            chatbox.makeOption(1);

            // Wait for the inventory to finish
            while (inventory.contains(bowstring, 0.90)) {
                xpBar.getXP();
                int randomDelay2 = new Random().nextInt(500) + 500;
                condition.sleep(randomDelay2);
            }
            xpBar.getXP();

            bank.open(bankloc);
            condition.wait(() -> bank.isOpen(), 250, 12);
            if (!bank.isSelectedBankTab(banktab)) {
                bank.openTab(banktab);
                condition.wait(() -> bank.isSelectedBankTab(banktab), 250, 12);
            }
            // bank item needed based on choice in config
            if (Objects.equals(product, "Shortbow")) {
                inventory.tapItem(shortbow, 0.90);
            } else {
                inventory.tapItem(longbow, 0.90);
            }
            condition.sleep(randomBiggerDelay);

            if (Objects.equals(product, "Shortbow")) {
                bank.withdrawItem(shortbowU, 0.90);
            } else {
                bank.withdrawItem(longbowU, 0.90);
            }
            condition.sleep(randomDelay);
            bank.withdrawItem(bowstring, 0.90);
            condition.sleep(randomDelay);
            bank.close();
            condition.sleep(randomBiggerDelay);
        }

        // ---------------------------------------------------------------- //
    }
}