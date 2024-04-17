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
        version = "1.05",
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
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String bankloc;
    int banktab;
    String grapes = "1987";
    String jugofwater = "1937";

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        Logger.log("Thank you for using the dWinemaker script!\nSetting up everything for your gains now...");

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
        if (bankloc == null) {
            Logger.debugLog("Starting dynamic banking setup...");

            // Opening the inventory if not yet opened.
            Logger.debugLog("Opening up the inventory.");
            if (!GameTabs.isInventoryTabOpen()) {
                GameTabs.openInventoryTab();
            }

            Logger.debugLog("Starting setup for Dynamic Banking.");
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
        int randomDelay2 = new Random().nextInt(300) + 200;
        int randomBiggerDelay = new Random().nextInt(400) + 600;

        // Set custom quantity to 14 if needed
        if (!Bank.isSelectedQuantityCustomButton()) {
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
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Withdrawing initial items from the bank
        Bank.withdrawItem(jugofwater, 0.75);
        Condition.sleep(randomDelay2);
        Logger.debugLog("Withdrew jugs of water from the bank.");
        Bank.withdrawItem(grapes, 0.75);
        Condition.sleep(randomDelay);
        Logger.debugLog("Withdrew grapes from the bank.");

        // Check if we have the jugs of water and grapes in the inventory, otherwise stop script.
        if (!Inventory.contains(jugofwater, 0.75) && !Inventory.contains(grapes, 0.75)) {
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
        Bank.close();
        Logger.debugLog("Closed bank interface.");

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeMakeWineMethod() {
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
        while (Inventory.contains(grapes, 0.75)) {
            Condition.sleep(randomDelay3);
            hopActions();

            // Check if we have passed the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                Logger.debugLog("Timeout reached for inventory.contains() method");
                break;
            }
        }
        readXP();

        Logger.debugLog("Ending the executeMakeWineMethod() method.");
    }

    private void bank() {
        Logger.debugLog("Starting bank() method.");
        int randomDelay = new Random().nextInt(250) + 250;
        int randomDelay2 = new Random().nextInt(300) + 200;

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
        Logger.debugLog("Depositing inventory");
        Bank.tapDepositInventoryButton();
        Condition.sleep(randomDelay);

        Bank.withdrawItem(jugofwater, 0.75);
        Condition.sleep(randomDelay2);
        Logger.debugLog("Withdrew jugs of water from the bank.");
        Bank.withdrawItem(grapes, 0.75);
        Condition.sleep(randomDelay);
        Logger.debugLog("Withdrew grapes from the bank.");

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

    private void checkInventWineMaking() {
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

}