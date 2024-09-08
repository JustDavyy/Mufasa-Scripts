import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dWinemaker",
        description = "Creates well fermented wine for those juicy cooking gains. Supports dynamic banking.",
        version = "1.08",
        guideLink = "https://wiki.mufasaclient.com/docs/dwinemaker/",
        categories = {ScriptCategory.Cooking}
)
@ScriptConfiguration.List(
        {
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

public class dWinemaker extends AbstractScript {
    // Creating the strings for later use
    private static final Random random = new Random();
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String bankloc;
    int banktab;
    String grapes = "1987";
    String jugofwater = "1937";
    int productIndex;
    int processCount = 0;
    int inventCount = 0;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        Logger.log("Thank you for using the dWinemaker script!\nSetting up everything for your gains now...");

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create a single image box, to show the amount of processed bows
        productIndex = Paint.createBox("Jug of Wine", 1993, processCount);

        // Set the two top headers of paintUI.
        Paint.setStatus("Initializing...");

        hopActions();
        setupBanking();
        initialSetup();
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        checkInventOpen();
        checkInventWineMaking();
        executeMakeWineMethod();
        bank();
        hopActions();

    }

    private void setupBanking() {
        Logger.debugLog("Starting setupBanking() method.");
        Paint.setStatus("Setting up banking");
        if (bankloc == null) {
            Logger.debugLog("Starting dynamic banking setup...");

            // Opening the inventory if not yet opened.
            Logger.debugLog("Opening up the inventory.");
            if (!GameTabs.isInventoryTabOpen()) {
                Paint.setStatus("Opening inventory");
                GameTabs.openInventoryTab();
            }

            Logger.debugLog("Starting setup for Dynamic Banking.");
            Paint.setStatus("Setting up dynamic bank");
            bankloc = Bank.setupDynamicBank();
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
                Paint.setStatus("Entering bank pin");
                Logger.debugLog("Bank pin is needed!");
                Bank.enterBankPin();
                Condition.sleep(500);
                Condition.wait(() -> Bank.isOpen(), 200, 12);
                Logger.debugLog("Bank pin entered.");
                Logger.debugLog("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(677);
            } else {
                Paint.setStatus("Deposit inventory");
                Logger.debugLog("Bank pin is not needed, bank is open!");
                Logger.debugLog("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(705);
            }
        }
        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void initialSetup() {
        Paint.setStatus("Performing initial setup");
        Logger.debugLog("Starting initialSetup() method.");

        int randomDelay = new Random().nextInt(600) + 600;
        int randomDelay2 = new Random().nextInt(300) + 200;
        int randomBiggerDelay = new Random().nextInt(400) + 600;

        // Set custom quantity to 14 if needed
        if (!Bank.isSelectedQuantityCustomButton()) {
            Paint.setStatus("Setting custom Qty to 14");
            Rectangle customQty = Bank.findQuantityCustomButton();
            Client.longPress(customQty);
            Condition.sleep(randomDelay2);
            Client.tap(392, 498);
            Condition.sleep(randomBiggerDelay);
            Client.sendKeystroke("KEYCODE_1");
            Client.sendKeystroke("KEYCODE_4");
            Client.sendKeystroke("KEYCODE_ENTER");
            Logger.debugLog("Set custom quantity 14 for items in the bank.");
            Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);
        }

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Selecting bank tab " + banktab);
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Withdrawing initial items from the bank
        Paint.setStatus("Withdrawing jugs of water");
        Bank.withdrawItem(jugofwater, 0.75);
        Condition.sleep(randomDelay2);
        Logger.debugLog("Withdrew jugs of water from the bank.");
        Paint.setStatus("Withdrawing grapes");
        Bank.withdrawItem(grapes, 0.75);
        Condition.sleep(randomDelay);
        Logger.debugLog("Withdrew grapes from the bank.");

        // Check if we have the jugs of water and grapes in the inventory, otherwise stop script.
        if (!Inventory.contains(jugofwater, 0.75) && !Inventory.contains(grapes, 0.75)) {
            Paint.setStatus("Checking invent for jugs/grapes");
            Logger.log("No jugs of water/grapes found in inventory, assuming we're out of items to process.");
            Bank.close();
            if (Bank.isOpen()) {
                Bank.close();
            }
            Logout.logout();
            Script.stop();
        }

        // Finishing off with closing the bank
        Logger.debugLog("Closing bank interface.");
        Paint.setStatus("Closing the bank interface");
        Bank.close();
        Logger.debugLog("Closed bank interface.");

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeMakeWineMethod() {
        Paint.setStatus("Start making wine");
        Logger.debugLog("Starting executeMakeWineMethod() method.");

        // Starting to process items
        Inventory.tapItem(jugofwater, 0.75);
        int randomDelay2 = new Random().nextInt(150) + 100;
        int randomDelay3 = new Random().nextInt(1500) + 500;
        Condition.sleep(randomDelay2);
        Inventory.tapItem(grapes, 0.75);
        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
        Chatbox.makeOption(1);
        Logger.debugLog("Selected option 1 in chatbox.");

        // Wait for the inventory to finish (with a timeout)
        long startTime = System.currentTimeMillis();
        long timeout = 25 * 1000; // 25 seconds in milliseconds as a full invent is about 17-20 seconds.
        Paint.setStatus("Waiting for inventory to finish");
        while (Inventory.contains(grapes, 0.75)) {
            Condition.sleep(randomDelay3);
            hopActions();

            if (Player.leveledUp()) {
                // Starting to process items
                Inventory.tapItem(jugofwater, 0.75);
                randomDelay2 = new Random().nextInt(150) + 100;
                randomDelay3 = new Random().nextInt(1500) + 500;
                Condition.sleep(randomDelay2);
                Inventory.tapItem(grapes, 0.75);
                Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
                Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
                Chatbox.makeOption(1);
                Logger.debugLog("Selected option 1 in chatbox.");
            }

            // Check if we have passed the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                Logger.debugLog("Timeout reached for inventory.contains() method");
                break;
            }
        }
        readXP();

        processCount += 14;
        inventCount += 1;
        Paint.updateBox(productIndex, processCount);
        Paint.setStatistic("Inventories completed: " + inventCount);

        Logger.debugLog("Ending the executeMakeWineMethod() method.");
    }

    private void bank() {
        Paint.setStatus("Banking");
        Logger.debugLog("Starting bank() method.");
        int randomDelay = new Random().nextInt(250) + 250;
        int randomDelay2 = new Random().nextInt(300) + 200;

        // Opening the bank based on your location
        Logger.debugLog("Attempting to open the bank.");
        Bank.open(bankloc);
        Logger.debugLog("Bank is open.");

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Selecting bank tab " + banktab);
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Depositing items based on your product chosen
        Logger.debugLog("Depositing inventory");
        Paint.setStatus("Deposit inventory");
        Bank.tapDepositInventoryButton();
        Condition.sleep(randomDelay);

        Paint.setStatus("Withdrawing jugs of water");
        Bank.withdrawItem(jugofwater, 0.75);
        Condition.sleep(randomDelay2);
        Logger.debugLog("Withdrew jugs of water from the bank.");
        Paint.setStatus("Withdrawing grapes");
        Bank.withdrawItem(grapes, 0.75);
        Condition.sleep(randomDelay);
        Logger.debugLog("Withdrew grapes from the bank.");

        // Closing the bank, as banking should be done now
        Paint.setStatus("Closing bank");
        Bank.close();
        Logger.debugLog("Closed the bank.");

        Logger.debugLog("Ending the bank() method.");
    }

    private void checkInventOpen() {
        Paint.setStatus("Check inventory open");
        // Check if the inventory is open (needs this check after a break)
        if (!GameTabs.isInventoryTabOpen()) {
            Paint.setStatus("Opening inventory");
            GameTabs.openInventoryTab();
        }
    }

    private void checkInventWineMaking() {
        Paint.setStatus("Check invent for jugs and grapes");
        if (!GameTabs.isInventoryTabOpen()) {
            Paint.setStatus("Opening inventory");
            GameTabs.openInventoryTab();
        }
        String[] items = {jugofwater, grapes};
        // Check if we have both a glassblowing pipe and molten glass in the inventory.
        if (!Inventory.contains(items, 0.75)) {
            Logger.log("1st check failed for jugs of water/grapes in our inventory, going back to banking!");
            bank();
        }

        // Check if we have both a glassblowing pipe and molten glass in the inventory.
        if (!Inventory.contains(items, 0.75)) {
            Logger.log("2nd check failed for jugs of water/grapes in our inventory, logging out and aborting script!");
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

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

}