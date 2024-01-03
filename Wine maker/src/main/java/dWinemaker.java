import helpers.*;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dWine maker",
        description = "Creates well fermented wine for those juicy cooking gains. Supports dynamic banking.",
        version = "1.00",
        category = ScriptCategory.Cooking
)
@ScriptConfiguration.List(
        {
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

public class dWinemaker extends AbstractScript {
    // Creating the strings for later use
    String bankloc;
    int banktab;
    boolean doneInitialSetup = false;
    String grapes = "1987";
    String jugofwater = "1937";

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        banktab = Integer.parseInt(configs.get("BankTab"));

        //Logs for debugging purposes
        logger.log("Thank you for using the dWine maker script!");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {
        logger.debugLog("Running the poll() method.");

        if (!doneInitialSetup) {
            logger.debugLog("doneInitialSetup is false, running initial setups.");

            // Check if we are logged in, if not, login.
            if (login.findPlayNowOption() != null) {
                logger.debugLog("We are not logged in yet, logging in.");
                login.preSetup();
            }

            // Continue the rest of the setup
            setupBanking();
            initialSetup();
        }

        checkInventOpen();
        checkInventWineMaking();
        executeMakeWineMethod();
        bank();

    }

    private void setupBanking() {
        logger.debugLog("Starting setupBanking() method.");
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
    }

    private void initialSetup() {
        logger.debugLog("Starting initialSetup() method.");

        int randomDelay = new Random().nextInt(600) + 600;
        int randomDelay2 = new Random().nextInt(300) + 200;
        int randomBiggerDelay = new Random().nextInt(400) + 600;

        // Set custom quantity to 14 if needed
        if (!bank.isSelectedQuantityCustomButton()) {
            Rectangle customQty = bank.findQuantityCustomButton();
            client.longPress(customQty);
            condition.sleep(randomDelay2);
            client.tap(392, 498);
            condition.sleep(randomBiggerDelay);
            client.sendKeystroke("KEYCODE_1");
            client.sendKeystroke("KEYCODE_4");
            client.sendKeystroke("KEYCODE_ENTER");
            logger.debugLog("Set custom quantity 14 for items in the bank.");
            condition.wait(() -> bank.isSelectedQuantityCustomButton(), 200, 12);
        }

        // Select the right bank tab if needed.
        if (!bank.isSelectedBankTab(banktab)) {
            bank.openTab(banktab);
            logger.debugLog("Opened bank tab " + banktab);
        }

        // Withdrawing initial items from the bank
        bank.withdrawItem(jugofwater, 0.75);
        condition.sleep(randomDelay2);
        logger.debugLog("Withdrew jugs of water from the bank.");
        bank.withdrawItem(grapes, 0.75);
        condition.sleep(randomDelay);
        logger.debugLog("Withdrew grapes from the bank.");

        // Check if we have the jugs of water and grapes in the inventory, otherwise stop script.
        if (!inventory.contains(jugofwater, 0.75) && !inventory.contains(grapes, 0.75)) {
            logger.log("No jugs of water/grapes found in inventory, assuming we're out of items to process.");
            bank.close();
            if (bank.isOpen()) {
                bank.close();
            }
            logout.logout();
            script.forceStop();
        }

        // Finishing off with closing the bank
        logger.debugLog("Closing bank interface.");
        bank.close();
        logger.debugLog("Closed bank interface.");

        doneInitialSetup = true;
        logger.debugLog("Set the doneInitialSetup value to true.");

        logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeMakeWineMethod() {
        logger.debugLog("Starting executeMakeWineMethod() method.");

        // Check if we have the jugs of water and grapes in the inventory.
        if (!inventory.contains(jugofwater, 0.75) && !inventory.contains(grapes, 0.75)) {
            logger.log("We don't have jugs of water/grapes in our inventory, going back to banking!");
            return;
        }

        // Starting to process items
        inventory.tapItem(jugofwater, 0.75);
        int randomDelay2 = new Random().nextInt(150) + 100;
        int randomDelay3 = new Random().nextInt(1500) + 500;
        condition.sleep(randomDelay2);
        inventory.tapItem(grapes, 0.75);
        logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        condition.wait(() -> chatbox.isMakeMenuVisible(), 200, 12);
        chatbox.makeOption(1);
        logger.debugLog("Selected option 1 in chatbox.");

        // Wait for the inventory to finish (with a timeout)
        long startTime = System.currentTimeMillis();
        long timeout = 25 * 1000; // 25 seconds in milliseconds as a full invent is about 17-20 seconds.
        while (inventory.contains(grapes, 0.75)) {
            condition.sleep(randomDelay3);

            // Check if we have passed the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                logger.debugLog("Timeout reached for inventory.contains() method");
                break;
            }
        }
        readXP();

        logger.debugLog("Ending the executeMakeWineMethod() method.");
    }

    private void bank() {
        logger.debugLog("Starting bank() method.");
        int randomDelay = new Random().nextInt(250) + 250;
        int randomDelay2 = new Random().nextInt(300) + 200;

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
        logger.debugLog("Depositing inventory");
        bank.tapDepositInventoryButton();
        condition.sleep(randomDelay);

        bank.withdrawItem(jugofwater, 0.75);
        condition.sleep(randomDelay2);
        logger.debugLog("Withdrew jugs of water from the bank.");
        bank.withdrawItem(grapes, 0.75);
        condition.sleep(randomDelay);
        logger.debugLog("Withdrew grapes from the bank.");

        // Closing the bank, as banking should be done now
        bank.close();
        logger.debugLog("Closed the bank.");

        logger.debugLog("Ending the bank() method.");
    }

    private void checkInventOpen() {
        // Check if the inventory is open (needs this check after a break)
        if (!gameTabs.isInventoryTabOpen()) {
            gameTabs.openInventoryTab();
        }
    }

    private void checkInventWineMaking() {
        String[] items = {jugofwater, grapes};
        // Check if we have both a glassblowing pipe and molten glass in the inventory.
        if (!inventory.contains(items, 0.75)) {
            logger.log("1st check failed for jugs of water/grapes in our inventory, going back to banking!");
            bank();
        }

        // Check if we have both a glassblowing pipe and molten glass in the inventory.
        if (!inventory.contains(items, 0.75)) {
            logger.log("2nd check failed for jugs of water/grapes in our inventory, logging out and aborting script!");
            logout.logout();
            script.forceStop();
        }
    }

    private void readXP() {
        xpBar.getXP();
    }

}