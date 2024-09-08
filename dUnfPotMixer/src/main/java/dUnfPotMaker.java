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
        name = "dUnfPotMixer",
        description = "Mixes all your herbs to unfinished potions, supports dynamic banking and world hops.",
        version = "1.04",
        guideLink = "https://wiki.mufasaclient.com/docs/dunfinished-potion-maker/",
        categories = {ScriptCategory.Herblore, ScriptCategory.Moneymaking}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Herb",
                        description = "What herb would you like to mix with a vial of water?",
                        defaultValue = "Ranarr weed",
                        allowedValues = {
                                @AllowedValue(optionIcon = "249", optionName = "Guam leaf"),
                                @AllowedValue(optionIcon = "251", optionName = "Marrentill"),
                                @AllowedValue(optionIcon = "253", optionName = "Tarromin"),
                                @AllowedValue(optionIcon = "255", optionName = "Harralander"),
                                @AllowedValue(optionIcon = "257", optionName = "Ranarr weed"),
                                @AllowedValue(optionIcon = "2998", optionName = "Toadflax"),
                                @AllowedValue(optionIcon = "259", optionName = "Irit leaf"),
                                @AllowedValue(optionIcon = "261", optionName = "Avantoe"),
                                @AllowedValue(optionIcon = "263", optionName = "Kwuarm"),
                                @AllowedValue(optionIcon = "3000", optionName = "Snapdragon"),
                                @AllowedValue(optionIcon = "265", optionName = "Cadantine"),
                                @AllowedValue(optionIcon = "2481", optionName = "Lantadyme"),
                                @AllowedValue(optionIcon = "267", optionName = "Dwarf weed"),
                                @AllowedValue(optionIcon = "269", optionName = "Torstol"),
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

public class dUnfPotMaker extends AbstractScript {
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String herb;
    int vialOfWater = 227;
    int herbID = 0;
    int[] herbAndVials;
    String bankloc;
    int banktab;
    private Random random = new Random();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        herb = configs.get("Herb");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        // Initialize hop timer for this run if hopping is enabled
        hopActions();

        // One-time setup
        setHerbID();
        setupBanking();
        initialSetup();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dUnfPotMaker script!");
        Logger.log("Setting up everything for your gains now...");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        checkInventOpen();
        checkInventHerbsAndVials();
        executeMixing();
        bank();
        hopActions();

    }

    private void setHerbID() {
        Logger.debugLog("Running the setHerbID() method.");

        switch (herb) {
            case "Guam leaf":
                herbID = 249;
                break;
            case "Marrentill":
                herbID = 251;
                break;
            case "Tarromin":
                herbID = 253;
                break;
            case "Harralander":
                herbID = 255;
                break;
            case "Ranarr weed":
                herbID = 257;
                break;
            case "Toadflax":
                herbID = 2998;
                break;
            case "Irit leaf":
                herbID = 259;
                break;
            case "Avantoe":
                herbID = 261;
                break;
            case "Kwuarm":
                herbID = 263;
                break;
            case "Snapdragon":
                herbID = 3000;
                break;
            case "Cadantine":
                herbID = 265;
                break;
            case "Lantadyme":
                herbID = 2481;
                break;
            case "Dwarf weed":
                herbID = 267;
                break;
            case "Torstol":
                herbID = 269;
                break;
            default:
                Logger.debugLog("Herb not found: " + herb);
                Logout.logout();
                Script.stop();
                break;
        }

        if (herbID != 0) {
            Logger.debugLog("Selected herb: " + herb + ", Item ID: " + herbID);
            herbAndVials = new int[]{herbID, vialOfWater};
        }

        Logger.debugLog("Ending the setHerbID() method.");
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
                Condition.sleep(generateDelay(400, 600));
                Condition.wait(() -> Bank.isOpen(), 200, 12);
                Logger.debugLog("Bank pin entered.");
                Logger.debugLog("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(generateDelay(500,800));
            } else {
                Logger.debugLog("Bank pin is not needed, bank is open!");
                Logger.debugLog("Depositing inventory.");
                Bank.tapDepositInventoryButton();
                Condition.sleep(generateDelay(500,800));
            }
        }
        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void initialSetup() {
        Logger.debugLog("Starting initialSetup() method.");

        Bank.tapQuantity1Button();
        Condition.sleep(generateDelay(500, 900));

        if (!Bank.isSelectedQuantityCustomButton()) {
            Rectangle customQty = Bank.findQuantityCustomButton();
            Client.longPress(customQty);
            Condition.sleep(generateDelay(300, 500));
            Client.tap(393, 499);
            Condition.sleep(generateDelay(600, 1000));
            Client.sendKeystroke("KEYCODE_1");
            Client.sendKeystroke("KEYCODE_4");
            Client.sendKeystroke("KEYCODE_ENTER");
            Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);
            Logger.debugLog("Set custom quantity 14 for items in the bank.");

            // Selecting the right bank tab again if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Bank.openTab(banktab);
                Logger.debugLog("Opened bank tab " + banktab);
            }

            // Withdraw first set of items
            Bank.withdrawItem(Integer.toString(herbID), 0.75);
            Condition.sleep(generateDelay(300, 500));
            Bank.withdrawItem(Integer.toString(vialOfWater), 0.75);

            // Check if we have the herbs and vials in the inventory, otherwise stop script.
            Condition.wait(() -> Inventory.contains(vialOfWater, 0.75), 250,10);
            if (!Inventory.containsAll(herbAndVials, 0.75)) {
                Logger.log("No " + herb + " and/or vials of water found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }

            Logger.debugLog("Withdrew 14 " + herb + " and 14 vials of water from the bank.");
        } else {
            // Selecting the right bank tab again if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Bank.openTab(banktab);
                Logger.debugLog("Opened bank tab " + banktab);
            }

            // Withdraw first set of items
            Bank.withdrawItem(Integer.toString(herbID), 0.75);
            Condition.sleep(generateDelay(300, 500));
            Bank.withdrawItem(Integer.toString(vialOfWater), 0.75);

            // Check if we have the herbs and vials in the inventory, otherwise stop script.
            Condition.wait(() -> Inventory.contains(vialOfWater, 0.75), 250,10);
            if (!Inventory.containsAll(herbAndVials, 0.75)) {
                Logger.log("No " + herb + " and/or vials of water found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }

            Logger.debugLog("Withdrew 14 " + herb + " and 14 vials of water from the bank.");
        }

        // Finishing off with closing the bank
        Logger.debugLog("Closing bank interface.");
        Bank.close();
        Condition.sleep(generateDelay(400,600));

        if (Bank.isOpen()) {
            Bank.close();
            Condition.wait(() -> !Bank.isOpen(), 200, 20);
        }

        Logger.debugLog("Closed bank interface.");

        GameTabs.openInventoryTab();
        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeMixing() {
        Logger.log("Mixing the vials.");

        // Check if we have grimy herbs in the inventory.
        if (!Inventory.containsAll(herbAndVials, 0.75)) {
            Logger.log("No " + herb + " or vials of water found in inventory, re-banking!");
            return;
        }

        // Perform actions
        if (Inventory.emptySlots() == 0) {
            Inventory.tapItem(herbID, true, 0.75);
            Condition.sleep(generateDelay(200, 400));
            Inventory.tapItem(Integer.toString(vialOfWater), true, 0.75);

            Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
            Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
            Chatbox.makeOption(1);
            Logger.debugLog("Selected option 1 in chatbox.");

            Condition.sleep(generateDelay(7000, 8000));
            Condition.wait(() -> !Inventory.containsAll(herbAndVials, 0.75), 200, 30);
        } else { // We don't use a cached tap here, as we don't have 14 of both items.
            Inventory.tapItem(herbID, false, 0.75);
            Condition.sleep(generateDelay(200, 400));
            Inventory.tapItem(Integer.toString(vialOfWater), false, 0.75);

            Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
            Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 12);
            Chatbox.makeOption(1);
            Logger.debugLog("Selected option 1 in chatbox.");

            Condition.sleep(generateDelay(7000, 8000));
            Condition.wait(() -> !Inventory.containsAll(herbAndVials, 0.75), 200, 30);
        }

        Logger.debugLog("Ending the executeMixing() method.");
    }

    private void bank() {
        Logger.log("Banking.");

        // Opening the bank based on your location
        Logger.debugLog("Attempting to open the bank.");
        Bank.open(bankloc);
        Logger.debugLog("Bank is open.");

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Bank.openTab(banktab);
            Logger.debugLog("Opened bank tab " + banktab);
        }

        // Depositing the full inventory
        Bank.tapDepositInventoryButton();
        Condition.sleep(generateDelay(250,500));

        Bank.withdrawItem(Integer.toString(herbID), 0.75);
        Condition.sleep(generateDelay(300, 500));
        Bank.withdrawItem(Integer.toString(vialOfWater), 0.75);
        Logger.debugLog("Withdrew 14 " + herb + " and 14 vials of water from the bank.");

        // Closing the bank, as banking should be done now
        Bank.close();
        Condition.sleep(generateDelay(350, 450));
        if(Bank.isOpen()) {
            Bank.close();
        }
        Logger.debugLog("Closed the bank.");

        Condition.sleep(generateDelay(75,150));

        Logger.debugLog("Ending the bank() method.");
    }

    private void checkInventOpen() {
        // Check if the inventory is open (needs this check after a break)
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }
    }

    private void checkInventHerbsAndVials() {
        // Check if we have herbs and vials of water in our inventory
        if (!Inventory.containsAll(herbAndVials, 0.75)) {
            Logger.log("1st check failed for herbs and vials of water in our inventory, going back to banking!");
            bank();
        }

        // Check if we have herbs and vials of water in our inventory
        if (!Inventory.containsAll(herbAndVials, 0.75)) {
            Logger.log("2nd check failed for herbs and vials of water in our inventory, logging out and aborting script!");
            Logout.logout();
            Script.stop();
        }
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