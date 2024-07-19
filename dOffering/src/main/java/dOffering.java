import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.Map;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logout;

import java.util.Random;

@ScriptManifest(
        name = "dOffering",
        description = "Offers bones and ashes using the Arceuus spellbook for quick prayer gains.",
        version = "1.012",
        guideLink = "https://wiki.mufasaclient.com/docs/doffering/",
        categories = {ScriptCategory.Prayer, ScriptCategory.Magic}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What would you like to offer to the gods today?",
                        defaultValue = "Abyssal ashes",
                        allowedValues = {
                                @AllowedValue(optionIcon = "25766", optionName = "Fiendish ashes"),
                                @AllowedValue(optionIcon = "25769", optionName = "Vile ashes"),
                                @AllowedValue(optionIcon = "25772", optionName = "Malicious ashes"),
                                @AllowedValue(optionIcon = "25775", optionName = "Abyssal ashes"),
                                @AllowedValue(optionIcon = "25778", optionName = "Infernal ashes"),
                                @AllowedValue(optionIcon = "526", optionName = "Bones"),
                                @AllowedValue(optionIcon = "532", optionName = "Big bones"),
                                @AllowedValue(optionIcon = "534", optionName = "Babydragon bones"),
                                @AllowedValue(optionIcon = "22780", optionName = "Wyrm bones"),
                                @AllowedValue(optionIcon = "536", optionName = "Dragon bones"),
                                @AllowedValue(optionIcon = "6812", optionName = "Wyvern bones"),
                                @AllowedValue(optionIcon = "22783", optionName = "Drake bones"),
                                @AllowedValue(optionIcon = "11943", optionName = "Lava dragon bones"),
                                @AllowedValue(optionIcon = "22786", optionName = "Hydra bones"),
                                @AllowedValue(optionIcon = "6729", optionName = "Dagannoth bones"),
                                @AllowedValue(optionIcon = "22124", optionName = "Superior dragon bones")
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

public class dOffering extends AbstractScript {
    // Creating the strings for later use
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String product;
    String bankloc;
    int banktab;
    int itemID;
    int productCount;
    int spellCount;
    boolean shouldStop = false;
    Random random = new Random();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        // One-time setup
        setupOptions();
        hopActions();
        setupBanking();
        checkInventOpen();

        // Close the chatbox
        Chatbox.closeChatbox();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dOfferingscript!");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        executeOfferingMethod();

        if (!shouldStop) {
            bank();
            checkInventOpen();
            Condition.sleep(generateRandomDelay(400, 800));
            checkInventOffering();
            hopActions();
        } else {
            Script.stop();
        }

    }

    private void setupOptions() {
        Logger.debugLog("Running the setupOptions() method.");

        if (product.equals("Fiendish ashes")) {
            itemID = 25766;
        } else if (product.equals("Vile ashes")) {
            itemID = 25769;
        } else if (product.equals("Malicious ashes")) {
            itemID = 25772;
        } else if (product.equals("Abyssal ashes")) {
            itemID = 25775;
        } else if (product.equals("Infernal ashes")) {
            itemID = 25778;
        } else if (product.equals("Bones")) {
            itemID = 526;
        } else if (product.equals("Big bones")) {
            itemID = 532;
        } else if (product.equals("Babydragon bones")) {
            itemID = 534;
        } else if (product.equals("Wyrm bones")) {
            itemID = 22780;
        } else if (product.equals("Dragon bones")) {
            itemID = 536;
        } else if (product.equals("Wyvern bones")) {
            itemID = 6812;
        } else if (product.equals("Drake bones")) {
            itemID = 22783;
        } else if (product.equals("Lava dragon bones")) {
            itemID = 11943;
        } else if (product.equals("Hydra bones")) {
            itemID = 22786;
        } else if (product.equals("Dagannoth bones")) {
            itemID = 6729;
        } else if (product.equals("Superior dragon bones")) {
            itemID = 22124;
        } else {
            Logger.debugLog("Product not recognized: " + product);
            Logger.debugLog("Stopping script.");
            Logout.logout();
            Script.stop();
        }

        Logger.debugLog("Ending the setupOptions() method.");
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
                Condition.sleep(769);
            } else {
                Logger.debugLog("Bank pin is not needed, bank is open!");
                Condition.sleep(712);
            }

            int randomDelay2 = new Random().nextInt(300) + 200;
            int randomBiggerDelay2 = new Random().nextInt(400) + 600;
            // Set the custom quantity to 26
            Bank.tapQuantity1Button();

            Rectangle customQty = Bank.findQuantityCustomButton();
            Client.longPress(customQty);
            Condition.sleep(randomDelay2);
            Client.tap(394, 499);
            Condition.sleep(randomBiggerDelay2);
            Client.sendKeystroke("KEYCODE_2");
            Client.sendKeystroke("KEYCODE_4");
            Client.sendKeystroke("KEYCODE_ENTER");
            Logger.debugLog("Set custom quantity 24 for items in the bank.");
            Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);

            // Select the right bank tab if needed.
            if (!Bank.isSelectedBankTab(banktab)) {
                Bank.openTab(banktab);
                Logger.log("Selecting bank tab " + banktab);
            }

            // Withdraw first set of items
            Bank.withdrawItem(String.valueOf(itemID), 0.75);
            Condition.sleep(randomDelay2);
            Logger.debugLog("Withdrew 24 " + product + " from the bank.");

            Bank.close();
            Condition.wait(() -> !Bank.isOpen(), 250, 20);

            if (Bank.isOpen()) {
                Bank.close();
            }

        }
        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void executeOfferingMethod() {
        Logger.debugLog("Starting executeOfferingMethod() method.");

        // Check if we have all items in the inventory.
        productCount = Inventory.count(itemID, 0.75);
        if (productCount <= 1) {
            Logger.log("We don't have any or enough " + product + " in our inventory, going back to banking!");
            return;
        } else {
            // Calculate spellCount by rounding up the division of productCount by 3
            spellCount = (int) Math.ceil(productCount / 3.0);
            Logger.debugLog("We have " + productCount + " " + product + "(s) in inventory. Spell count is set to: " + spellCount);
        }

        if (spellCount >= 1 && spellCount < 8) {
            Logger.log("Processing our last inventory!");
            shouldStop = true;
        }

        // Open the magic tab so we can start casting spells
        if (!GameTabs.isMagicTabOpen()) {
            GameTabs.openMagicTab();
        }

        if (product.endsWith("bones")) {
            Logger.log("Now casting Sinister Offering " + spellCount + " times.");
            for (int i = 0; i < spellCount; i++) {
                Magic.tapSinisterOfferingSpell();
                readXP();
                Condition.sleep(generateRandomDelay(6000, 6300));
            }
        } else if (product.endsWith("ashes")) {
            Logger.log("Now casting Demonic Offering " + spellCount + " times.");
            for (int i = 0; i < spellCount; i++) {
                Magic.tapDemonicOfferingSpell();
                readXP();
                Condition.sleep(generateRandomDelay(6000, 6300));
            }
        } else {
            Logger.debugLog(product + " does not end on bones or ashes, unsure what spell to cast?!");
        }

        // Check if spellCount is at least 1 and lower than 8 (means we are out of supplies)
        if (spellCount >= 1 && spellCount < 8) {
            Logger.log("We just processed our last inventory of offerings as we ran our of offerings, logging out and stopping script.");
            Logout.logout();
            shouldStop = true;
        } else {
            // Open the inventory tab again so we can check if we're out of bones.
            GameTabs.openInventoryTab();
        }

        Logger.debugLog("Ending the executeOfferingMethod() method.");
    }

    private void bank() {
        Logger.debugLog("Starting bank() method.");

        // Opening the bank based on your location
        Logger.debugLog("Attempting to open the bank.");
        Bank.open(bankloc);
        Logger.debugLog("Bank is open.");

        // Check if bankpin is needed
        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
        }

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Withdraw the product we need
        Bank.withdrawItem(String.valueOf(itemID), 0.75);
        Logger.debugLog("Withdrew " + product + " from the bank.");

        // Closing the bank, as banking should be done now
        Bank.close();
        Condition.sleep(generateRandomDelay(300,600));
        if (Bank.isOpen()) {
            Bank.close();
            Condition.sleep(generateRandomDelay(300,600));
            if (Bank.isOpen()) {
                Bank.close();
            }
        }
        Logger.debugLog("Closed the bank.");

        Logger.debugLog("Ending the bank() method.");
    }

    private void checkMagicOpen() {
        // Check if the Magic tab is open
        if (!GameTabs.isMagicTabOpen()) {
            GameTabs.openMagicTab();
        }
    }

    private void checkInventOpen() {
        // Check if the Inventory tab is open
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }
    }

    private void checkInventOffering() {

        // Check if we have all items needed in the inventory.
        if (!Inventory.contains(itemID, 0.75)) {
            Logger.log("1st check failed for all items in our inventory, going back to banking!");
            bank();
        }

        // Check if we have all items needed in the inventory.
        if (!Inventory.contains(itemID, 0.75)) {
            Logger.log("2nd check failed for all items in our inventory, logging out and aborting script!");
            Logout.logout();
            Script.stop();
        }
    }

    public int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        int delay = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        return delay;
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