import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;

import java.util.Map;
import java.util.HashMap;
import static helpers.Interfaces.*;
import static helpers.Interfaces.Logout;

import java.util.Random;

@ScriptManifest(
        name = "dHerbCleaner",
        description = "Cleans all your grimy herbs with customisable speeds, supports dynamic banking and world hops.",
        version = "1.00",
        guideLink = "https://wiki.mufasaclient.com/docs/dherbcleaner/",
        categories = {ScriptCategory.Herblore}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Herb",
                        description = "What herb would you like to clean?",
                        defaultValue = "Ranarr weed",
                        allowedValues = {
                                @AllowedValue(optionIcon = "199", optionName = "Guam leaf"),
                                @AllowedValue(optionIcon = "201", optionName = "Marrentill"),
                                @AllowedValue(optionIcon = "203", optionName = "Tarromin"),
                                @AllowedValue(optionIcon = "205", optionName = "Harralander"),
                                @AllowedValue(optionIcon = "207", optionName = "Ranarr weed"),
                                @AllowedValue(optionIcon = "3049", optionName = "Toadflax"),
                                @AllowedValue(optionIcon = "209", optionName = "Irit leaf"),
                                @AllowedValue(optionIcon = "211", optionName = "Avantoe"),
                                @AllowedValue(optionIcon = "213", optionName = "Kwuarm"),
                                @AllowedValue(optionIcon = "3051", optionName = "Snapdragon"),
                                @AllowedValue(optionIcon = "215", optionName = "Cadantine"),
                                @AllowedValue(optionIcon = "2485", optionName = "Lantadyme"),
                                @AllowedValue(optionIcon = "217", optionName = "Dwarf weed"),
                                @AllowedValue(optionIcon = "219", optionName = "Torstol"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Method",
                        description = "What cleaning method would you like to use?",
                        defaultValue = "Active",
                        allowedValues = {
                                @AllowedValue(optionName = "AFK"),
                                @AllowedValue(optionName = "Active"),
                                @AllowedValue(optionName = "Active with 2% AFK"),
                                @AllowedValue(optionName = "Active with 5% AFK"),
                                @AllowedValue(optionName = "Active with 7% AFK"),
                                @AllowedValue(optionName = "Active with 10% AFK"),
                                @AllowedValue(optionName = "Active with 12% AFK"),
                                @AllowedValue(optionName = "Active with 15% AFK"),
                                @AllowedValue(optionName = "Active with 25% AFK"),
                                @AllowedValue(optionName = "Active with 40% AFK")
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

public class dHerbCleaner extends AbstractScript {
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String herb;
    String method;
    int herbGrimyID = 0;
    String bankloc;
    int banktab;
    private Random random = new Random();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        herb = configs.get("Herb");
        method = configs.get("Method");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        // Initialize hop timer for this run if hopping is enabled
        hopActions();

        // One-time setup
        setHerbGrimyID();
        setupBanking();
        initialSetup();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dHerbCleaner script!");
        Logger.log("Setting up everything for your gains now...");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        checkInventOpen();
        checkInventGrimyHerbs();
        executeCleaning();
        bank();
        hopActions();

    }

    private void setHerbGrimyID() {
        Logger.debugLog("Running the setHerbGrimyID() method.");

        switch (herb) {
            case "Guam leaf":
                herbGrimyID = 199;
                break;
            case "Marrentill":
                herbGrimyID = 201;
                break;
            case "Tarromin":
                herbGrimyID = 203;
                break;
            case "Harralander":
                herbGrimyID = 205;
                break;
            case "Ranarr weed":
                herbGrimyID = 207;
                break;
            case "Toadflax":
                herbGrimyID = 3049;
                break;
            case "Irit leaf":
                herbGrimyID = 209;
                break;
            case "Avantoe":
                herbGrimyID = 211;
                break;
            case "Kwuarm":
                herbGrimyID = 213;
                break;
            case "Snapdragon":
                herbGrimyID = 3051;
                break;
            case "Cadantine":
                herbGrimyID = 215;
                break;
            case "Lantadyme":
                herbGrimyID = 2485;
                break;
            case "Dwarf weed":
                herbGrimyID = 217;
                break;
            case "Torstol":
                herbGrimyID = 219;
                break;
            default:
                Logger.debugLog("Herb not found: " + herb);
                Logout.logout();
                Script.stop();
                break;
        }

        if (herbGrimyID != 0) {
            Logger.debugLog("Selected herb: " + herb + ", Item ID: " + herbGrimyID);
        }

        Logger.debugLog("Ending the setHerbGrimyID() method.");
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

        // Grabbing the first amethyst to process
        Logger.debugLog("Withdrawing " + herb + " from the bank.");
        if (!Bank.isSelectedQuantityAllButton()) {
            Bank.tapQuantityAllButton();
            Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 12);
            Logger.debugLog("Selected Quantity All button.");

            // Selecting the right bank tab again if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Bank.openTab(banktab);
                Logger.debugLog("Opened bank tab " + banktab);
            }

            // Withdraw first set of items
            Bank.withdrawItem(Integer.toString(herbGrimyID), 0.75);

            // Check if we have the grimy herbs in the inventory, otherwise stop script.
            Condition.wait(() -> Inventory.contains(herbGrimyID, 0.75), 250,10);
            if (!Inventory.contains(herbGrimyID, 0.75)) {
                Logger.log("No " + herb + " found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }

            Logger.debugLog("Withdrew " + herb + " from the bank.");
        } else {
            // Selecting the right bank tab again if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Bank.openTab(banktab);
                Logger.debugLog("Opened bank tab " + banktab);
            }

            // Withdraw first set of items
            Bank.withdrawItem(Integer.toString(herbGrimyID), 0.75);

            // Check if we have the grimy herbs in the inventory, otherwise stop script.
            Condition.wait(() -> Inventory.contains(herbGrimyID, 0.75), 250,10);
            if (!Inventory.contains(herbGrimyID, 0.75)) {
                Logger.log("No " + herb + " found in inventory, assuming we're out of items to process.");
                Bank.close();
                if (Bank.isOpen()) {
                    Bank.close();
                }
                Logout.logout();
                Script.stop();
            }

            Logger.debugLog("Withdrew " + herb + " from the bank.");
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

    private void executeCleaning() {
        Logger.log("Cleaning the inventory of herbs.");

        // Check if we have grimy herbs in the inventory.
        if (!Inventory.contains(herbGrimyID, 0.75)) {
            Logger.log("No " + herb + " found in inventory, re-banking!");
            return;
        }

        // Perform actions based on the selected method
        switch (method) {
            case "AFK":
                Logger.debugLog("Using AFK method.");
                afkAction();
                break;

            case "Active":
                Logger.debugLog("Using Active method.");
                activeAction();
                break;

            case "Active with 2% AFK":
                Logger.debugLog("Using Active with 2% AFK method.");
                if (random.nextInt(100) < 2) {
                    afkAction();
                } else {
                    activeAction();
                }
                break;

            case "Active with 5% AFK":
                Logger.debugLog("Using Active with 5% AFK method.");
                if (random.nextInt(100) < 5) {
                    afkAction();
                } else {
                    activeAction();
                }
                break;

            case "Active with 7% AFK":
                Logger.debugLog("Using Active with 7% AFK method.");
                if (random.nextInt(100) < 7) {
                    afkAction();
                } else {
                    activeAction();
                }
                break;

            case "Active with 10% AFK":
                Logger.debugLog("Using Active with 10% AFK method.");
                if (random.nextInt(100) < 10) {
                    afkAction();
                } else {
                    activeAction();
                }
                break;

            case "Active with 12% AFK":
                Logger.debugLog("Using Active with 12% AFK method.");
                if (random.nextInt(100) < 12) {
                    afkAction();
                } else {
                    activeAction();
                }
                break;

            case "Active with 15% AFK":
                Logger.debugLog("Using Active with 15% AFK method.");
                if (random.nextInt(100) < 15) {
                    afkAction();
                } else {
                    activeAction();
                }
                break;

            case "Active with 25% AFK":
                Logger.debugLog("Using Active with 25% AFK method.");
                if (random.nextInt(100) < 25) {
                    afkAction();
                } else {
                    activeAction();
                }
                break;

            case "Active with 40% AFK":
                Logger.debugLog("Using Active with 40% AFK method.");
                if (random.nextInt(100) < 40) {
                    afkAction();
                } else {
                    activeAction();
                }
                break;

            default:
                Logger.debugLog("Unknown method: " + method);
                return;
        }

        readXP();

        Logger.debugLog("Ending the executeCleaning() method.");
    }

    private void afkAction() {
        Inventory.tapItem(herbGrimyID, 0.75);
        Condition.sleep(30000);
        Condition.wait(() -> !Inventory.contains(herbGrimyID, 0.75), 200, 50);
    }

    private void activeAction() {
        Inventory.dropInventItems(0, false);
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

        Bank.withdrawItem(Integer.toString(herbGrimyID), 0.75);
        Logger.debugLog("Withdrew " + herb + " from the bank.");

        // Closing the bank, as banking should be done now
        Bank.close();
        Condition.sleep(generateDelay(200, 400));
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

    private void checkInventGrimyHerbs() {
        // Check if we have grimy herbs in our inventory
        if (!Inventory.contains(herbGrimyID, 0.75)) {
            Logger.log("1st check failed for grimy herbs in our inventory, going back to banking!");
            bank();
        }

        // Check if we have grimy herbs in our inventory
        if (!Inventory.contains(herbGrimyID, 0.75)) {
            Logger.log("2nd check failed for grimy herbs in our inventory, logging out and aborting script!");
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