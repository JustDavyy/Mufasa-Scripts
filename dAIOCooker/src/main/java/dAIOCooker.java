import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;

import java.awt.*;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dAIOCooker",
        description = "The cooker script to cook all your raw fish (or seaweed) at various different places.",
        version = "1.05",
        guideLink = "https://wiki.mufasaclient.com/docs/dcooker/",
        categories = {ScriptCategory.Cooking}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What product would you like to cook?",
                        defaultValue = "Raw karambwan",
                        allowedValues = {
                                @AllowedValue(optionIcon = "401", optionName = "Seaweed"),
                                @AllowedValue(optionIcon = "21504", optionName = "Giant seaweed"),
                                @AllowedValue(optionIcon = "321", optionName = "Raw anchovies"),
                                @AllowedValue(optionIcon = "317", optionName = "Raw shrimps"),
                                @AllowedValue(optionIcon = "327", optionName = "Raw sardine"),
                                @AllowedValue(optionIcon = "345", optionName = "Raw herring"),
                                @AllowedValue(optionIcon = "353", optionName = "Raw mackerel"),
                                @AllowedValue(optionIcon = "335", optionName = "Raw trout"),
                                @AllowedValue(optionIcon = "341", optionName = "Raw cod"),
                                @AllowedValue(optionIcon = "349", optionName = "Raw pike"),
                                @AllowedValue(optionIcon = "331", optionName = "Raw salmon"),
                                @AllowedValue(optionIcon = "3142", optionName = "Raw karambwan"),
                                @AllowedValue(optionIcon = "359", optionName = "Raw tuna"),
                                @AllowedValue(optionIcon = "377", optionName = "Raw lobster"),
                                @AllowedValue(optionIcon = "363", optionName = "Raw bass"),
                                @AllowedValue(optionIcon = "371", optionName = "Raw swordfish"),
                                @AllowedValue(optionIcon = "7944", optionName = "Raw monkfish"),
                                @AllowedValue(optionIcon = "383", optionName = "Raw shark"),
                                @AllowedValue(optionIcon = "395", optionName = "Raw sea turtle"),
                                @AllowedValue(optionIcon = "13439", optionName = "Raw anglerfish"),
                                @AllowedValue(optionIcon = "389", optionName = "Raw manta ray"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Location",
                        description = "What location would you like to cook at?",
                        defaultValue = "Hosidius kitchen",
                        allowedValues = {
                                @AllowedValue(optionName = "Catherby range"),
                                @AllowedValue(optionName = "Hosidius kitchen"),
                                @AllowedValue(optionName = "Myths guild"),
                                @AllowedValue(optionName = "Nardah oven"),
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

public class dAIOCooker extends AbstractScript {

// Script config variables
String hopProfile;
Boolean hopEnabled;
Boolean useWDH;
int banktab;
String productID;
String productName;
int cookedProductID;
int productIndex;
int processCount = 0;
String product;
String location;

// Area and tiles
Area hosidiusKitchenArea = new Area(
        new Tile(6672, 14168, 0),
        new Tile(6767, 14262, 0)
);
Tile hosidiusStartTile = new Tile(6699, 14213, 0);
Rectangle hosidiusStartRangeRect = new Rectangle(490, 122, 10, 14);
Rectangle hosidiusStartBankRect = new Rectangle(437, 294, 15, 12);
Rectangle hosidiusRecookRect = new Rectangle(444, 229, 13, 15);
Area nardahArea = new Area(
        new Tile(13676, 11259, 0),
        new Tile(13803, 11367, 0)
);
Tile nardahStartTile = new Tile(13707, 11313, 0);
Rectangle nardahStartOvenRect = new Rectangle(561, 363, 18, 19);

Area catherbyArea = new Area(
        new Tile(11199, 13474, 0),
        new Tile(11294, 13552, 0)
);
Tile catherbyStartTile = new Tile(11235, 13513, 0);
Rectangle catherbyStartRangeRect = new Rectangle(657, 185, 20, 16);

// Myths guild stuff
Rectangle mythsBankRect = new Rectangle(433, 215, 29, 32);
Rectangle mythsRangeRect = new Rectangle(484, 250, 25, 34);
Tile mythsTile = new Tile(9859, 11141, 1);
Area mythsArea = new Area(new Tile(9768, 11071, 1), new Tile(9902, 11199, 1));

// Banks and range rectangles
private Map<String, List<RectanglePair>> bankRectangles = new HashMap<>();
private Random random = new Random();


// Script logic variables

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        product = configs.get("Product");
        location = configs.get("Location");

        Logger.log("Thank you for using the dAIOCooker script!");
        Logger.log("Setting up everything for your gains now...");

        if (hopEnabled) {
            if(useWDH) {
                Logger.debugLog("Hopping (with WDH) is enabled for this run! Using profile: " + hopProfile);
            } else {
                Logger.debugLog("Hopping (without WDH) is enabled for this run! Using profile: " + hopProfile);
            }
        } else {
            Logger.debugLog("Hopping is disabled for this run!");
        }

        if (location.equals("Myths guild")) {
            MapChunk chunks = new MapChunk(new String[]{"38-44"}, "1");
            Walker.setup(chunks);
        } else {
            MapChunk chunks = new MapChunk(new String[]{"43-53", "43-54", "44-54", "44-53", "26-56", "25-56", "53-45", "53-44"}, "0");
            Walker.setup(chunks);
        }

        // Debug prints for chosen settings (in case we ever need this)
        Logger.debugLog("We're using bank tab: " + banktab);
        Logger.debugLog("We're cooking " + product + " in this run at " + location + ".");

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Initialize all the banking locations and other stuff
        initializeBankRects();

        // Initialize itemIDs
        initializeItemIDS();

        Paint.setStatus("Creating paint box");
        // Create a single image box, to show the amount of processed bows
        productIndex = Paint.createBox(productName, cookedProductID, processCount);

        // Initialize hop timer for this run
        hopActions();

        // Setting the correct zoom level
        setZoom();

        // Open the inventory if not already the case
        if (!GameTabs.isInventoryTabOpen()) {
            Paint.setStatus("Opening inventory");
            GameTabs.openInventoryTab();
        }

        // Check if we are in the area we need to be in.
        checkArea();

        // Set up the banking process
        setupBanking();

        // Tap the furnace for the first time
        switch (location) {
            case "Catherby range":
                Client.tap(catherbyStartRangeRect);
                break;
            case "Hosidius kitchen":
                Client.tap(hosidiusStartRangeRect);
                break;
            case "Nardah oven":
                Client.tap(nardahStartOvenRect);
                break;
            case "Myths guild":
                Client.tap(mythsRangeRect);
        }

        // Process first inventory
        cook();
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        // Get the rectangle pairs for the chosen location
        List<RectanglePair> rectanglePairs = bankRectangles.get(location);

        // Pick a random rectangle pair from the list
        RectanglePair rectanglePair = rectanglePairs.get(new Random().nextInt(rectanglePairs.size()));

        Rectangle rangeRectangle = rectanglePair.getRange();
        Rectangle bankRectangle = rectanglePair.getBank();

        Logger.debugLog("Tapping the bank");
        Client.tap(bankRectangle);
        Condition.wait(() -> Bank.isOpen(), 200, 75);

        bank();

        // Check if we do have the required items, if not reset and check again.
        resetAndRecheck();

        Logger.debugLog("Tapping the range/oven");
        Client.tap(rangeRectangle);

        cook();

        hopActions();
    }


    // Methods and stuff here
    private void checkArea() {
        Paint.setStatus("Checking areas");
        Logger.debugLog("Checking which area we are in, and moving to the start tile.");
        switch (location) {
            case "Catherby range":
                checkLocation(catherbyArea, catherbyStartTile);
                break;
            case "Hosidius kitchen":
                checkLocation(hosidiusKitchenArea, hosidiusStartTile);
                break;
            case "Nardah oven":
                checkLocation(nardahArea, nardahStartTile);
                break;
            case "Myths guild":
                checkLocation(mythsArea, mythsTile);
                break;
        }
    }

    private void setZoom() {
        Paint.setStatus("Setting zoom level");
        Logger.debugLog("Setting correct zoom level based on location.");
        switch (location) {
            case "Catherby range":
            case "Hosidius kitchen":
                Game.setZoom("2");
                break;
            case "Nardah oven":
                Game.setZoom("1");
                break;
            case "Myths guild":
                Game.setZoom("3");
                break;
        }
    }

    private void initializeBankRects() {
        Logger.debugLog("Initializing all the bank and range/oven areas.");
        Paint.setStatus("Initializing range/oven areas");

        // Nardah
        List<RectanglePair> nardahRects = new ArrayList<>();
        nardahRects.add(new RectanglePair(
                new Rectangle(329, 233, 7, 11),    // Bank Rectangle
                new Rectangle(555, 324, 15, 19)    // Range Rectangle
        ));
        nardahRects.add(new RectanglePair(
                new Rectangle(333, 205, 10, 11),    // Bank Rectangle
                new Rectangle(558, 359, 17, 19)    // Range Rectangle
        ));
        nardahRects.add(new RectanglePair(
                new Rectangle(337, 179, 9, 7),      // Bank Rectangle
                new Rectangle(565, 401, 17, 22)     // Range Rectangle
        ));
        nardahRects.add(new RectanglePair(
                new Rectangle(344, 168, 6, 6),      // Bank Rectangle
                new Rectangle(567, 426, 17, 20)     // Range Rectangle
        ));
        bankRectangles.put("Nardah oven", nardahRects);

        // Hosidius Kitchen
        List<RectanglePair> hosidiusRects = new ArrayList<>();
        hosidiusRects.add(new RectanglePair(
                new Rectangle(373, 440, 20, 18),   // Bank Rectangle
                new Rectangle(490, 122, 10, 16)    // Range Rectangle
        ));
        bankRectangles.put("Hosidius kitchen", hosidiusRects);

        // Catherby range
        List<RectanglePair> catherbyRects = new ArrayList<>();
        catherbyRects.add(new RectanglePair(
                new Rectangle(141, 287, 18, 15),   // Bank Rectangle
                new Rectangle(706, 188, 19, 16)    // Range Rectangle
        ));
        catherbyRects.add(new RectanglePair(
                new Rectangle(201, 289, 12, 17),   // Bank Rectangle
                new Rectangle(655, 182, 19, 20)    // Range Rectangle
        ));
        catherbyRects.add(new RectanglePair(
                new Rectangle(232, 287, 14, 18),   // Bank Rectangle
                new Rectangle(637, 184, 17, 19)    // Range Rectangle
        ));
        bankRectangles.put("Catherby range", catherbyRects);

        // Myths guild
        List<RectanglePair> mythsRects = new ArrayList<>();
        mythsRects.add(new RectanglePair(
                new Rectangle(433, 215, 29, 32),   // Bank Rectangle
                new Rectangle(484, 250, 25, 34)    // Range Rectangle
        ));
        bankRectangles.put("Myths guild", mythsRects);

    }

    private void initializeItemIDS() {
        Paint.setStatus("Initializing itemIDS");
        switch (product) {
            case "Seaweed":
                productID = "401";
                productName = "Soda ash";
                cookedProductID = ItemList.SODA_ASH_1781;
                break;
            case "Giant seaweed":
                productID = "21504";
                productName = "Soda ash";
                cookedProductID = ItemList.SODA_ASH_1781;
                break;
            case "Raw shrimps":
                productID = "317";
                productName = "Shrimps";
                cookedProductID = ItemList.SHRIMPS_315;
                break;
            case "Raw anchovies":
                productID = "321";
                productName = "Anchovies";
                cookedProductID = ItemList.ANCHOVIES_319;
                break;
            case "Raw sardine":
                productID = "327";
                productName = "Sardine";
                cookedProductID = ItemList.SARDINE_325;
                break;
            case "Raw herring":
                productID = "345";
                productName = "Herring";
                cookedProductID = ItemList.HERRING_347;
                break;
            case "Raw mackerel":
                productID = "353";
                productName = "Mackerel";
                cookedProductID = ItemList.MACKEREL_355;
                break;
            case "Raw trout":
                productID = "335";
                productName = "Trout";
                cookedProductID = ItemList.TROUT_333;
                break;
            case "Raw cod":
                productID = "341";
                productName = "Cod";
                cookedProductID = ItemList.COD_339;
                break;
            case "Raw pike":
                productID = "349";
                productName = "Pike";
                cookedProductID = ItemList.PIKE_351;
                break;
            case "Raw salmon":
                productID = "331";
                productName = "Salmon";
                cookedProductID = ItemList.SALMON_329;
                break;
            case "Raw karambwan":
                productID = "3142";
                productName = "Cooked karambwan";
                cookedProductID = ItemList.COOKED_KARAMBWAN_3144;
                break;
            case "Raw tuna":
                productID = "359";
                productName = "Tuna";
                cookedProductID = ItemList.TUNA_361;
                break;
            case "Raw lobster":
                productID = "377";
                productName = "Lobster";
                cookedProductID = ItemList.LOBSTER_379;
                break;
            case "Raw bass":
                productID = "363";
                productName = "Lobster";
                cookedProductID = ItemList.BASS_365;
                break;
            case "Raw swordfish":
                productID = "371";
                productName = "Swordfish";
                cookedProductID = ItemList.SWORDFISH_373;
                break;
            case "Raw monkfish":
                productID = "7944";
                productName = "Monkfish";
                cookedProductID = ItemList.MONKFISH_7946;
                break;
            case "Raw shark":
                productID = "383";
                productName = "Shark";
                cookedProductID = ItemList.SHARK_385;
                break;
            case "Raw sea turtle":
                productID = "395";
                productName = "Sea turtle";
                cookedProductID = ItemList.SEA_TURTLE_397;
                break;
            case "Raw anglerfish":
                productID = "13439";
                productName = "Anglerfish";
                cookedProductID = ItemList.ANGLERFISH_13441;
                break;
            case "Raw manta ray":
                productID = "389";
                productName = "Manta ray";
                cookedProductID = ItemList.MANTA_RAY_391;
                break;
            default:
                throw new IllegalArgumentException("Unknown product: " + product);
        }
    }

    private void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        } else {
            // We do nothing here, as hop is disabled.
        }
    }

    private void checkLocation(Area area, Tile tile) {
        if (Player.within(area)) {
            if (!Player.atTile(tile)) {
                Logger.debugLog("Walking to the " + location + " start tile.");
                Walker.step(tile);
                Condition.wait(() -> Player.atTile(tile), 200, 20);
                if (!Player.atTile(tile)) {
                    Logger.debugLog("Walking to the " + location + " start tile.");
                    Walker.step(tile);
                    Condition.wait(() -> Player.atTile(tile), 200, 20);
                    if (!Player.atTile(tile)) {
                        Logger.debugLog("Walking to the " + location + " start tile.");
                        Walker.step(tile);
                        Condition.wait(() -> Player.atTile(tile), 200, 20);
                        if (!Player.atTile(tile)) {
                            Logger.debugLog("Walking to the " + location + " start tile.");
                            Walker.step(tile);
                            Condition.wait(() -> Player.atTile(tile), 200, 20);
                            if (!Player.atTile(tile)) {
                                Logger.debugLog("Walking to the " + location + " start tile.");
                                Walker.step(tile);
                                Condition.wait(() -> Player.atTile(tile), 200, 20);
                                if (!Player.atTile(tile)) {
                                    Logger.debugLog("Walking to the " + location + " start tile.");
                                    Walker.step(tile);
                                    Condition.wait(() -> Player.atTile(tile), 200, 20);
                                }
                            }
                        }
                    }
                }
            } else {
                Logger.debugLog("We are located at the " + location + " start tile.");
            }
        } else {
            notInArea();
        }
    }

    private void setupBanking() {
        Paint.setStatus("Set up banking");
        Logger.debugLog("Starting setupBanking() method.");

        Logger.debugLog("Opening the bank of Gielinor");
        // Open the bank based on the selected location (we already checked our location and moved to start tile)
        switch (location) {
            case "Catherby range":
                Client.tap(new Rectangle(442, 232, 17, 21));
                Condition.wait(() -> Bank.isOpen(), 200, 20);

                // Check for pin, as we don't use Bank.open here (which already processes the pin)
                if (Bank.isBankPinNeeded()) {
                    Bank.enterBankPin();
                }
                break;
            case "Hosidius kitchen":
                Client.tap(hosidiusStartBankRect);
                Condition.wait(() -> Bank.isOpen(), 200, 20);

                // Check for pin, as we don't use Bank.open here (which already processes the pin)
                if (Bank.isBankPinNeeded()) {
                    Bank.enterBankPin();
                }
                break;
            case "Nardah oven":
                Client.tap(new Rectangle(422, 261, 12, 14));
                Condition.wait(() -> Bank.isOpen(), 200, 20);

                // Check for pin, as we don't use Bank.open here (which already processes the pin)
                if (Bank.isBankPinNeeded()) {
                    Bank.enterBankPin();
                }
                break;
            case "Myths guild":
                Client.tap(mythsBankRect);
                Condition.wait(() -> Bank.isOpen(), 200, 20);

                // Check for pin, as we don't use Bank.open here (which already processes the pin)
                if (Bank.isBankPinNeeded()) {
                    Bank.enterBankPin();
                }
                break;
        }

        // Wait till bank is open to be double sure
        Condition.wait(() -> Bank.isOpen(), 200, 20);

        // Deposit the entire inventory
        Paint.setStatus("Deposit inventory");
        Bank.tapDepositInventoryButton();
        Condition.sleep(generateRandomDelay(500, 700));

        // Set the correct quantities based on config choices

        if (product.equals("Giant seaweed")) {
            Paint.setStatus("Set custom quantity 4");
            // Set to custom quantity 4, as it cooks into 6 soda ashes each
            Bank.tapQuantity1Button();
            Condition.sleep(generateRandomDelay(400, 800));
            Rectangle customQty = Bank.findQuantityCustomButton();
            Client.longPress(customQty);
            Condition.sleep(generateRandomDelay(400, 800));
            Client.tap(393, 499);
            Condition.sleep(generateRandomDelay(800, 1200));
            Client.sendKeystroke("KEYCODE_4");
            Client.sendKeystroke("KEYCODE_ENTER");
            Logger.debugLog("Set custom quantity 4 for items in the bank.");
            Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 20);
        } else {
            // Set to all, as all others is just using 28.
            if (!Bank.isSelectedQuantityAllButton()) {
                Paint.setStatus("Set quantity all");
                Bank.tapQuantityAllButton();
                Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 20);
            }
        }

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Open bank tab " + banktab);
            Bank.openTab(banktab);
            Logger.log("Selecting bank tab " + banktab);
        }

        // Withdraw the first set of items
        Paint.setStatus("Withdrawing " + product);
        Bank.withdrawItem(productID, 0.88);
        Condition.sleep(generateRandomDelay(150, 250));

        // Close the bank after, twice just in case.
        Paint.setStatus("Close bank");
        Bank.close();

        if (Bank.isOpen()) {
            Bank.close();
        }

        Condition.wait(() -> !Bank.isOpen(), 200, 20);

        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void bank() {
        Logger.log("Banking.");
        Paint.setStatus("Banking");

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Open tab " + banktab);
            Bank.openTab(banktab);
            Logger.log("Selecting bank tab " + banktab);
        }

        // Deposit everything
        Paint.setStatus("Deposit inventory");
        Bank.tapDepositInventoryButton();
        Condition.sleep(generateRandomDelay(300, 500));

        // Withdraw new items
        Paint.setStatus("Withdraw " + product);
        Bank.withdrawItem(productID, 0.88);
        Condition.sleep(generateRandomDelay(400, 800));

        // Close the bank after, twice just in case.
        Paint.setStatus("Closing bank");
        Bank.close();

        if (Bank.isOpen()) {
            Bank.close();
        }

        Condition.wait(() -> !Bank.isOpen(), 200, 20);
    }

    private void cook() {
        Logger.log("Cooking.");
        Paint.setStatus("Cooking");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 75);
        Paint.setStatus("Press make option 1");
        Chatbox.makeOption(1);
        Logger.log("Waiting for cooking to finish...");

        if (!GameTabs.isInventoryTabOpen()) {
            Paint.setStatus("Open inventory");
            GameTabs.openInventoryTab();
        }

        long startTime = System.currentTimeMillis();
        long timeout = 65 * 1000; // 1 minute and 5 seconds in milliseconds

        Paint.setStatus("Waiting for cooking to finish");
        Condition.wait(() -> {
            readXP();
            boolean inventoryCheck = !Inventory.contains(productID, 0.88);
            boolean levelUpCheck = Player.leveledUp();
            boolean timeCheck = (System.currentTimeMillis() - startTime) >= timeout;
            return inventoryCheck || levelUpCheck || timeCheck;
        }, 200, 325);

        if (Player.leveledUp()) {
            Logger.log("Cooking.");
            Paint.setStatus("Re-cook after level up");
            switch (location) {
                case "Catherby range":
                    //Client.tap(catherbyRecookRect);
                    break;
                case "Hosidius kitchen":
                    Client.tap(hosidiusRecookRect);
                    break;
                case "Nardah oven":
                    //Client.tap(nardahRecookRect);
                    break;
                case "Myths guild":
                    Client.tap(mythsRangeRect);
                    break;
            }

            Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 75);
            Paint.setStatus("Press make option 1");
            Chatbox.makeOption(1);
            Logger.log("Waiting for cooking to finish...");

            if (!GameTabs.isInventoryTabOpen()) {
                Paint.setStatus("Open inventory");
                GameTabs.openInventoryTab();
            }

            Paint.setStatus("Waiting for cooking to finish");
            long finalStartTime = System.currentTimeMillis();
            long finalTimeout = timeout;
            Condition.wait(() -> {
                readXP();
                boolean inventoryCheck = !Inventory.contains(productID, 0.88);
                boolean levelUpCheck = Player.leveledUp();
                boolean timeCheck = (System.currentTimeMillis() - finalStartTime) >= finalTimeout;
                return inventoryCheck || levelUpCheck || timeCheck;
            }, 200, 325);
        }

        Logger.debugLog("Cooking finished, player leveled up, or timeout reached.");
        Paint.setStatus("Updating paint statistics");
        if (productName.equals("Soda ash")) {
            Integer ashes = Inventory.count(ItemList.SODA_ASH_1781, 0.8);
            processCount = processCount + ashes;
            Paint.updateBox(productIndex, processCount);
        } else {
            processCount = processCount + 28;
            Paint.updateBox(productIndex, processCount);
        }
        readXP();
    }

    public void resetAndRecheck() {
        Paint.setStatus("Reset!");
        if (!Inventory.contains(productID, 0.88)) {
            Logger.debugLog("Our inventory does not contain any " + product + " resetting and retrying... (2nd attempt)");
            switch (location) {
                case "Catherby range":
                    Bank.open("Catherby_bank");
                    break;
                case "Hosidius kitchen":
                    Bank.open("Hosidius_bank");
                    break;
                case "Nardah oven":
                    Client.tap(new Rectangle(422, 261, 12, 14));
                    Condition.wait(() -> Bank.isOpen(), 200, 20);

                    // Check for pin, as we don't use Bank.open here (which already processes the pin)
                    if (Bank.isBankPinNeeded()) {
                        Bank.enterBankPin();
                    }
                    break;
                case "Myths guild":
                    Client.tap(mythsBankRect);
                    Condition.wait(() -> Bank.isOpen(), 200, 20);

                    // Check for pin, as we don't use Bank.open here (which already processes the pin)
                    if (Bank.isBankPinNeeded()) {
                        Bank.enterBankPin();
                    }
                    break;
            }

            bank();
            if (!Inventory.contains(productID, 0.88)) {
                Logger.debugLog("Our inventory does not contain any " + product + " resetting and retrying... (3rd and last attempt)");
                switch (location) {
                    case "Catherby range":
                        Bank.open("Catherby_bank");
                        break;
                    case "Hosidius kitchen":
                        Bank.open("Hosidius_bank");
                        break;
                    case "Nardah oven":
                        Client.tap(new Rectangle(422, 261, 12, 14));
                        Condition.wait(() -> Bank.isOpen(), 200, 20);

                        // Check for pin, as we don't use Bank.open here (which already processes the pin)
                        if (Bank.isBankPinNeeded()) {
                            Bank.enterBankPin();
                        }
                        break;
                    case "Myths guild":
                        Walker.step(mythsTile);
                        Client.tap(mythsBankRect);
                        Condition.wait(() -> Bank.isOpen(), 200, 20);

                        // Check for pin, as we don't use Bank.open here (which already processes the pin)
                        if (Bank.isBankPinNeeded()) {
                            Bank.enterBankPin();
                        }
                        break;
                }

                bank();
                if (!Inventory.contains(productID, 0.88)) {
                    Logger.log("Could not withdraw any " + product + " from the bank after 3 attempts, assuming we are out of products.");
                    Logger.log("Logging out and stopping script!");
                    Logout.logout();
                    Script.stop();
                }
            }
        } else {
            Logger.debugLog("Our inventory contains " + product + ".");
        }
    }

    private void notInArea() {
        Paint.setStatus("Not in area");
        Logger.log("We are not within the " + location + " area. Please move there and restart the script.");
        Logout.logout();
        Script.stop();
    }

    public RectanglePair getRectanglePair(String bankName) {
        List<RectanglePair> pairs = bankRectangles.get(bankName);
        if (pairs != null && !pairs.isEmpty()) {
            return pairs.get(random.nextInt(pairs.size()));
        }
        return null;
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

    private void readXP() {
        XpBar.getXP();
    }

}

