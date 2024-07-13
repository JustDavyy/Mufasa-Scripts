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
        name = "dGem Cutter",
        description = "Cuts any uncut into their cut variants, supports dynamic banking and world hops.",
        version = "1.021",
        guideLink = "https://wiki.mufasaclient.com/docs/dgem-cutter/",
        categories = {ScriptCategory.Crafting}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What uncut type would you like to process?",
                        defaultValue = "Uncut ruby",
                        allowedValues = {
                                @AllowedValue(optionIcon = "1625", optionName = "Uncut opal"),
                                @AllowedValue(optionIcon = "1627", optionName = "Uncut jade"),
                                @AllowedValue(optionIcon = "1629", optionName = "Uncut red topaz"),
                                @AllowedValue(optionIcon = "1623", optionName = "Uncut sapphire"),
                                @AllowedValue(optionIcon = "1621", optionName = "Uncut emerald"),
                                @AllowedValue(optionIcon = "1619", optionName = "Uncut ruby"),
                                @AllowedValue(optionIcon = "1617", optionName = "Uncut diamond"),
                                @AllowedValue(optionIcon = "1631", optionName = "Uncut dragonstone"),
                                @AllowedValue(optionIcon = "6571", optionName = "Uncut onyx"),
                                @AllowedValue(optionIcon = "19496", optionName = "Uncut zenyte")
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

public class dGemCutter extends AbstractScript {
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String product;
    String bankloc;
    int banktab;
    String chisel = "1755";
    String crushedgem = "1633";
    String finalProduct;
    String unprocessedItemID;
    String processedItemID;
    int productIndex;
    int crushedIndex;
    int processedItems = 0;
    int crushedItems = 0;
    Map<String, String[]> ItemIDs;
    private final Random random = new Random();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        initializeItemIDs();

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Initialize hop timer for this run if hopping is enabled
        Paint.setStatus("Initialize hop timer");
        hopActions();

        // One-time setup
        setupItemIDs();

        Paint.setStatus("Creating paint box");
        // Create a single image box, to show the amount of processed bows
        finalProduct = product.trim();  // Trim any leading or trailing spaces
        if (finalProduct.startsWith("Uncut ")) {
            finalProduct = finalProduct.substring(6);  // Remove "Uncut "
        }
        finalProduct = Character.toUpperCase(finalProduct.charAt(0)) + finalProduct.substring(1).toLowerCase();  // Capitalize first letter and make others lowercase
        productIndex = Paint.createBox(finalProduct, Integer.parseInt(processedItemID), processedItems);
        if (product.equals("Uncut opal") || product.equals("Uncut jade") || product.equals("Uncut red topaz")) {
            crushedIndex = Paint.createBox("Crushed gems", ItemList.CRUSHED_GEM_1633, crushedItems);
        }

        setupBanking();
        initialSetup();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dGem Cutter script!");
        Logger.log("Setting up everything for your gains now...");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        checkInventOpen();
        checkInventUncuts();
        executeCutting();
        bank();
        hopActions();

    }

    private void initializeItemIDs() {
        Logger.debugLog("Running the initializeItemIDs() method.");

        ItemIDs = new HashMap<>();

        // Map of item names to their IDs, where the first ID is unprocessed, and the second ID is the processed ID
        ItemIDs.put("Uncut opal", new String[]{"1625", "1609"});
        ItemIDs.put("Uncut jade", new String[]{"1627", "1611"});
        ItemIDs.put("Uncut red topaz", new String[]{"1629", "1613"});
        ItemIDs.put("Uncut sapphire", new String[]{"1623", "1607"});
        ItemIDs.put("Uncut emerald", new String[]{"1621", "1605"});
        ItemIDs.put("Uncut ruby", new String[]{"1619", "1603"});
        ItemIDs.put("Uncut diamond", new String[]{"1617", "1601"});
        ItemIDs.put("Uncut dragonstone", new String[]{"1631", "1615"});
        ItemIDs.put("Uncut onyx", new String[]{"6571", "6573"});
        ItemIDs.put("Uncut zenyte", new String[]{"19496", "19493"});

        Logger.debugLog("Ending the initializeItemIDs() method.");
    }

    private void setupItemIDs() {
        Logger.debugLog("Running the setupItemIDs() method.");
        Paint.setStatus("Initialize item IDs");

        if (ItemIDs.containsKey(product)) {
            String[] ids = ItemIDs.get(product);
            if (ids != null && ids.length == 2) {
                unprocessedItemID = ids[0];
                processedItemID = ids[1];

                // Use these IDs as needed
                Logger.debugLog("\nUnprocessed item ID: " + unprocessedItemID + "\nProcessed item ID: " + processedItemID);
            } else {
                Logger.debugLog("IDs not found for product: " + product);
            }
        } else {
            Logger.debugLog("Product not found: " + product);
        }

        Logger.debugLog("Ending the setupItemIDs() method.");
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
            Condition.sleep(5000);
            Logger.debugLog("Attempting to open the Bank of Gielinor.");
            Paint.setStatus("Open bank");
            Bank.open(bankloc);
            Logger.debugLog("Bank interface detected!");
            if (Bank.isBankPinNeeded()) {
                Paint.setStatus("Enter bank pin");
                Logger.debugLog("Bank pin is needed!");
                Bank.enterBankPin();
                Condition.sleep(500);
                Condition.wait(() -> Bank.isOpen(), 200, 12);
                Logger.debugLog("Bank pin entered.");
                Logger.debugLog("Depositing inventory.");
                Paint.setStatus("Deposit inventory");
                Bank.tapDepositInventoryButton();
                Condition.sleep(706);
            } else {
                Logger.debugLog("Bank pin is not needed, bank is open!");
                Logger.debugLog("Depositing inventory.");
                Paint.setStatus("Deposit inventory");
                Bank.tapDepositInventoryButton();
                Condition.sleep(699);
            }
        }
        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void initialSetup() {
        Paint.setStatus("Perform initial setup");
        Logger.debugLog("Starting initialSetup() method.");

        // Withdrawing a chisel from the bank
        Logger.debugLog("Withdrawing a chisel from the bank.");
        if (!Bank.isSelectedQuantity1Button()) {
            Paint.setStatus("Set quantity 1");
            Bank.tapQuantity1Button();
            Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
        }
        Paint.setStatus("Tap search");
        Bank.tapSearchButton();
        Condition.sleep(generateDelay(300, 500));

        Paint.setStatus("Type chisel");
        String textToSend = "chisel";
        for (char c : textToSend.toCharArray()) {
            String keycode = "KEYCODE_" + Character.toUpperCase(c);
            Client.sendKeystroke(keycode);
            Logger.debugLog("Sent keystroke: " + keycode);
        }

        Condition.sleep(generateDelay(1500, 2000));
        Paint.setStatus("Withdraw chisel");
        Bank.withdrawItem(chisel, 0.75);
        Condition.sleep(generateDelay(300, 500));
        Logger.debugLog("Withdrew chisel from the bank.");

        Client.sendKeystroke("KEYCODE_ENTER");
        Condition.sleep(generateDelay(300, 500));
        Logger.debugLog("Closed search interface.");

        // Check if we have the chisel in the inventory, otherwise stop script.
        Condition.wait(() -> Inventory.contains(chisel, 0.75), 250,10);
        if (!Inventory.contains(chisel, 0.75)) {
            Logger.log("No chisel found in inventory, assuming we're out of items to process.");
            Bank.close();
            if (Bank.isOpen()) {
                Bank.close();
            }
            Logout.logout();
            Script.stop();
        }

        // Grabbing the first uncuts to process
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
            Paint.setStatus("Withdraw " + product);
            Bank.withdrawItem(unprocessedItemID, 0.75);
            Logger.debugLog("Withdrew " + product + " from the bank.");

            // Check if we have the uncuts in the inventory, otherwise stop script.
            Condition.wait(() -> Inventory.contains(unprocessedItemID, 0.75), 250,10);
            if (!Inventory.contains(unprocessedItemID, 0.75)) {
                Logger.log("No " + product + " found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }
        } else {
            // Withdraw first set of items
            Paint.setStatus("Withdraw " + product);
            Bank.withdrawItem(unprocessedItemID, 0.75);

            // Check if we have both uncuts and a chisel in the inventory, otherwise stop script.
            String[] items3 = {chisel, unprocessedItemID};
            Condition.wait(() -> Inventory.contains(items3, 0.75), 250,10);
            if (!Inventory.contains(items3, 0.75)) {
                Logger.log("Not all were items found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }

            Logger.debugLog("Withdrew " + product + " from the bank.");
        }

        // Finishing off with closing the bank
        Logger.debugLog("Closing bank interface.");
        Paint.setStatus("Close bank");
        Bank.close();
        Logger.debugLog("Closed bank interface.");

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeCutting() {
        Logger.debugLog("Starting executeCutting() method.");
        Paint.setStatus("Start process item");

        // Check if we have both a chisel and uncuts in the inventory.
        if (!Inventory.contains(chisel, 0.75) && !Inventory.contains(unprocessedItemID, 0.75)) {
            Logger.log("We don't have a chisel and " + product + " in our inventory, going back to banking!");
            return;
        }

        Paint.setStatus("Tap chisel");
        // Starting to process items
        Inventory.tapItem(chisel, 0.75);
        Condition.sleep(generateDelay(150, 300));
        Paint.setStatus("Tap " + product);
        Inventory.tapItem(unprocessedItemID, 0.75);
        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
        Paint.setStatus("Tap make option 1");
        Chatbox.makeOption(1);
        Logger.debugLog("Selected option 1 in chatbox.");

        // Wait for the inventory to finish
        Condition.wait(() -> outOfUncuts(), 250, 160);
        readXP();

        Paint.updateBox(productIndex, processedItems + Inventory.count(processedItemID, 0.8));
        if (product.equals("Uncut opal") || product.equals("Uncut jade") || product.equals("Uncut red topaz")) {
            Paint.updateBox(crushedIndex, crushedItems + Inventory.count(ItemList.CRUSHED_GEM_1633, 0.8));
        }

        Logger.debugLog("Ending the executeCutting() method.");
    }

    private void bank() {
        Paint.setStatus("Bank");
        Logger.debugLog("Starting bank() method.");

        // Opening the bank based on your location
        Logger.debugLog("Attempting to open the bank.");
        Paint.setStatus("Open bank");
        Bank.open(bankloc);
        Logger.debugLog("Bank is open.");

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Open tab " + banktab);
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Depositing items based on your product chosen
        Logger.debugLog("Depositing " + product + ".");
        Paint.setStatus("Deposit " + finalProduct);
        Inventory.tapItem(processedItemID, 0.75);
        Condition.sleep(generateDelay(150, 300));

        // Check if the product is one of the specific uncut gems
        if ("Uncut opal".equals(product) || "Uncut jade".equals(product) || "Uncut red topaz".equals(product)) {
            // Check for and process crushed gems while processing these specific uncuts
            if (Inventory.contains(crushedgem, 0.75)) {
                Paint.setStatus("Deposit crushed gems");
                Inventory.tapItem(crushedgem, 0.75);
                Condition.sleep(generateDelay(150, 300));
            }
        }

        Paint.setStatus("Withdraw " + product);
        Bank.withdrawItem(unprocessedItemID, 0.75);
        Logger.debugLog("Withdrew " + product + " from the bank.");

        // Closing the bank, as banking should be done now
        Paint.setStatus("Close bank");
        Bank.close();
        if(Bank.isOpen()) {
            Bank.close();
        }
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

    private void checkInventUncuts() {
        Paint.setStatus("Checking inventory");
        int[] items = {Integer.parseInt(chisel), Integer.parseInt(unprocessedItemID)};
        // Check if we have both a chisel and uncuts in the inventory.
        if (!Inventory.containsAll(items, 0.75)) {
            Logger.log("1st check failed for a chisel and uncuts in our inventory, going back to banking!");
            Logout.logout();
            Script.stop();
        }

        // Check if we have both a chisel and uncuts in the inventory.
        if (!Inventory.containsAll(items, 0.75)) {
            Logger.log("2nd check failed for a chisel and uncuts in our inventory, logging out and aborting script!");

        }
    }

    private boolean outOfUncuts() {
        readXP();
        hopActions();

        if (Player.leveledUp()) {
            Paint.setStatus("Leveled up!");
            Paint.setStatus("Tap chisel");
            // Starting to process items
            Inventory.tapItem(chisel, 0.75);
            Condition.sleep(generateDelay(200,300));
            Paint.setStatus("Tap " + product);
            Inventory.tapItem(unprocessedItemID, 0.75);
            Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
            Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
            Paint.setStatus("Tap make option 1");
            Chatbox.makeOption(1);
            Logger.debugLog("Selected option 1 in chatbox.");
            Condition.sleep(generateDelay(900, 1200));
        }

        return !Inventory.contains(unprocessedItemID, 0.8);
    }

    private void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);

            if (!GameTabs.isInventoryTabOpen()) {
                GameTabs.openInventoryTab();
            }
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