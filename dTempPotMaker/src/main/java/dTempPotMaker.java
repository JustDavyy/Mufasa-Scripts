import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.ItemList;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.Map;
import java.util.Random;
import static helpers.Interfaces.*;
import static helpers.Interfaces.Logout;

@ScriptManifest(
        name = "dTempPotMaker",
        description = "Makes super restores/attacks in any of the dynamic banks.",
        version = "1.31",
        guideLink = "",
        categories = {ScriptCategory.Herblore}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What potion would you like to make?",
                        defaultValue = "Super Restore",
                        allowedValues = {
                                @AllowedValue(optionIcon = "145", optionName = "Super Attack"),
                                @AllowedValue(optionIcon = "3026", optionName = "Super Restore")
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

public class dTempPotMaker extends AbstractScript {
    private final Random random = new Random();
    // Creating the strings for later use
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String bankloc;
    String product;
    int banktab;
    int processCount = 0;
    int productIndex;
    int[] reqItemsRest = new int[]{ItemList.SNAPDRAGON_POTION__UNF__3004, ItemList.RED_SPIDERS__EGGS_223};
    int[] reqItemsAttk = new int[]{ItemList.IRIT_POTION__UNF__101, ItemList.EYE_OF_NEWT_221};

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        //Logs for debugging purposes
        Logger.log("Thank you for using the dTempPotMaker script!");

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create a single image box, to show the amount of processed bows
        if (product.equals("Super Restore")) {
            productIndex = Paint.createBox("Super Restore", ItemList.SUPER_RESTORE_3__3026, processCount);
        } else {
            productIndex = Paint.createBox("Super Attack", ItemList.SUPER_ATTACK_3__145, processCount);
        }


        // Set the two top headers of paintUI.
        Paint.setStatus("Initializing...");

        // One-time setup
        hopActions();
        setupBanking();
        initialSetup();

        GameTabs.openInventoryTab();
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        checkInventOpen();
        checkInventPotMethod();
        executeMakePotMethod();
        bank();
        hopActions();

    }

    private void setupBanking() {
        Logger.debugLog("Starting setupBanking() method.");
        Paint.setStatus("Setting up for banking");
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
            Condition.sleep(generateDelay(4500, 6000));
            Logger.log("Opening the Bank of Gielinor.");
            Bank.open(bankloc);
            Logger.debugLog("Bank interface detected!");
            if (Bank.isBankPinNeeded()) {
                Logger.debugLog("Bank pin is needed!");
                Bank.enterBankPin();
                Condition.sleep(generateDelay(450, 600));
                Condition.wait(() -> Bank.isOpen(), 200, 12);
                Logger.debugLog("Bank pin entered.");
                Logger.log("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(generateDelay(600, 800));
            } else {
                Logger.debugLog("Bank pin is not needed, bank is open!");
                Logger.log("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(generateDelay(600, 800));
            }
        }
        Logger.debugLog("Ending the setupBanking() method.");
    }


    private void initialSetup() {
        Logger.debugLog("Starting initialSetup() method.");
        Paint.setStatus("Running initial setup");

        // Set quantity 1
        if (!Bank.isSelectedQuantity1Button()) {
            Bank.tapQuantity1Button();
            Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
        }

        // Set quantity 14
        Rectangle customQty = Bank.findQuantityCustomButton();
        Client.longPress(customQty);
        Condition.sleep(generateDelay(900, 1200));
        Client.tap(393, 499);
        Condition.sleep(generateDelay(1500, 2250));
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
        if (product.equals("Super Restore")) {
            Bank.withdrawItem(ItemList.SNAPDRAGON_POTION__UNF__3004, 0.7);
            Condition.sleep(generateDelay(250, 400));
            Bank.withdrawItem(ItemList.RED_SPIDERS__EGGS_223, 0.7);
            Condition.sleep(generateDelay(150, 225));
        } else {
            Bank.withdrawItem(ItemList.IRIT_POTION__UNF__101, 0.7);
            Condition.sleep(generateDelay(250, 400));
            Bank.withdrawItem(ItemList.EYE_OF_NEWT_221, 0.7);
            Condition.sleep(generateDelay(150, 225));
        }

        // Finishing off with closing the bank
        Logger.debugLog("Closing bank interface.");
        Bank.close();
        Condition.sleep(generateDelay(1000, 1500));

        if (Bank.isOpen()) {
            Bank.close();
        }
        Logger.debugLog("Closed bank interface.");

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeMakePotMethod() {
        Logger.debugLog("Starting executeMakePotMethod() method.");
        Paint.setStatus("Execute make pots");

        // Check if we have all supplies in the inventory.
        if (product.equals("Super Restore")) {
            if (!Inventory.containsAll(reqItemsRest, 0.75)) {
                Logger.log("We don't have UNFINISHED POTIONS and SPIDER EGGS in our inventory, going back to banking!");
                return;
            }
        } else {
            if (!Inventory.containsAll(reqItemsAttk, 0.75)) {
                Logger.log("We don't have UNFINISHED POTIONS and EYE OF NEWT in our inventory, going back to banking!");
                return;
            }
        }

        // Starting to process items
        if (product.equals("Super Restore")) {
            Inventory.tapItem(ItemList.SNAPDRAGON_POTION__UNF__3004, 0.75);
            Condition.sleep(generateDelay(200, 350));
            Inventory.tapItem(ItemList.RED_SPIDERS__EGGS_223, 0.75);
        } else {
            Inventory.tapItem(ItemList.IRIT_POTION__UNF__101, 0.75);
            Condition.sleep(generateDelay(200, 350));
            Inventory.tapItem(ItemList.EYE_OF_NEWT_221, 0.75);
        }

        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
        Chatbox.makeOption(1);

        // Wait for the inventory to finish (with a timeout)
        long startTime = System.currentTimeMillis();
        long timeout = 18 * 1000; // 18 seconds in milliseconds as a full invent is about 17 seconds.
        Paint.setStatus("Waiting for completion");
        if (product.equals("Super Restore")) {
            while (Inventory.containsAll(reqItemsRest, 0.8)) {
                readXP();
                Condition.sleep(generateDelay(500, 750));
                hopActions();

                if (Player.leveledUp()) {
                    startTime = System.currentTimeMillis();
                    // Starting to process items
                    Inventory.tapItem(ItemList.SNAPDRAGON_POTION__UNF__3004, 0.75);
                    Condition.sleep(generateDelay(200, 350));
                    Inventory.tapItem(ItemList.RED_SPIDERS__EGGS_223, 0.75);
                    Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
                    Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
                    Chatbox.makeOption(1);
                }

                // Check if we have passed the timeout
                if (System.currentTimeMillis() - startTime > timeout) {
                    Logger.debugLog("Timeout reached for inventory.contains() method");
                    break;
                }
            }
        } else {
            while (Inventory.containsAll(reqItemsAttk, 0.8)) {
                readXP();
                Condition.sleep(generateDelay(500, 750));
                hopActions();

                if (Player.leveledUp()) {
                    startTime = System.currentTimeMillis();
                    // Starting to process items
                    Inventory.tapItem(ItemList.IRIT_POTION__UNF__101, 0.75);
                    Condition.sleep(generateDelay(200, 350));
                    Inventory.tapItem(ItemList.EYE_OF_NEWT_221, 0.75);
                    Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
                    Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
                    Chatbox.makeOption(1);
                }

                // Check if we have passed the timeout
                if (System.currentTimeMillis() - startTime > timeout) {
                    Logger.debugLog("Timeout reached for inventory.contains() method");
                    break;
                }
            }
        }
        readXP();
        processCount += 14;
        Paint.updateBox(productIndex, processCount);

        Logger.debugLog("Ending the executeMakePotMethod() method.");
    }

    private void bank() {
        Logger.debugLog("Starting bank() method.");
        Paint.setStatus("Banking");
        Logger.log("Banking.");

        // Opening the bank based on your location
        Logger.debugLog("Attempting to open the bank.");
        Bank.open(bankloc);
        Logger.debugLog("Bank is open.");

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Bank.openTab(banktab);
            Logger.log("Opened bank tab " + banktab);
        }

        Bank.tapDepositInventoryButton();
        Condition.sleep(generateDelay(300, 500));

        if (product.equals("Super Restore")) {
            Bank.withdrawItem(ItemList.SNAPDRAGON_POTION__UNF__3004, 0.7);
            Condition.sleep(generateDelay(250, 400));
            Bank.withdrawItem(ItemList.RED_SPIDERS__EGGS_223, 0.7);
        } else {
            Bank.withdrawItem(ItemList.IRIT_POTION__UNF__101, 0.7);
            Condition.sleep(generateDelay(250, 400));
            Bank.withdrawItem(ItemList.EYE_OF_NEWT_221, 0.7);
        }
        Condition.sleep(generateDelay(150, 225));

        // Closing the bank, as banking should be done now
        Bank.close();
        Condition.sleep(generateDelay(1000, 1500));
        if (Bank.isOpen()) {
            Bank.close();
        }
        Logger.debugLog("Closed the bank.");
        Logger.debugLog("Ending the bank() method.");
    }

    private void checkInventOpen() {
        Paint.setStatus("Check invent open");
        // Check if the inventory is open (needs this check after a break)
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }
    }

    private void checkInventPotMethod() {
        Paint.setStatus("Check invent for items");

        if (product.equals("Super Restore")) {
            if (!Inventory.containsAll(reqItemsRest, 0.75)) {
                Logger.debugLog("1st check failed for required items in our inventory, going back to banking!");
                bank();
            }

            if (!Inventory.containsAll(reqItemsRest, 0.75)) {
                Logger.log("2nd check failed for required items in our inventory, logging out and aborting script!");
                Logout.logout();
                Script.stop();
            }
        } else {
            if (!Inventory.containsAll(reqItemsAttk, 0.75)) {
                Logger.debugLog("1st check failed for required items in our inventory, going back to banking!");
                bank();
            }

            if (!Inventory.containsAll(reqItemsAttk, 0.75)) {
                Logger.log("2nd check failed for required items in our inventory, logging out and aborting script!");
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