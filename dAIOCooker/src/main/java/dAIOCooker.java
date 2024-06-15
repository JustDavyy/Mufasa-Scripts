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
        version = "1.00",
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
String product;
String location;

// Area, regions and tiles

RegionBox hosidiusRegion = new RegionBox(
        "Hosidius",
        2016, 1896,
        2205, 2118
);
Area hosidiusKitchenArea = new Area(
        new Tile(695, 656),
        new Tile(712, 670)
);
Tile hosidiusStartTile = new Tile(699, 667);
Rectangle hosidiusStartRangeRect = new Rectangle(462, 102, 13, 19);

RegionBox nardahRegion = new RegionBox(
        "Nardah",
        8796, 4545,
        9402, 5040
);
Area nardahArea = new Area(
        new Tile(3026, 1621),
        new Tile(3050, 1641)
);
Tile nardahStartTile = new Tile(3034, 1628);
Rectangle nardahStartOvenRect = new Rectangle(560, 363, 20, 21);

Area catherbyArea = new Area(
        new Tile(2202, 885),
        new Tile(2225, 904)
);

RegionBox catherbyRegion = new RegionBox(
        "Catherby",
        6549, 2613,
        6726, 2760
);
Tile catherbyStartTile = new Tile(2210, 895);
Rectangle catherbyStartRangeRect = new Rectangle(663, 181, 32, 22);

// Banks and range rectangles
private Map<String, List<RectanglePair>> bankRectangles = new HashMap<>();
private Random random = new Random();


// Script logic variables
Tile playerPos;

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

        // Debug prints for chosen settings (in case we ever need this)
        Logger.debugLog("We're using bank tab: " + banktab);
        Logger.debugLog("We're cooking " + product + " in this run at " + location + ".");

        // Initialize all the banking locations and other stuff
        initializeBankRects();

        // Initialize itemIDs
        initializeItemIDS();

        // Initialize hop timer for this run
        hopActions();

        // Setting the correct zoom level
        setZoom();

        // Open the inventory if not already the case
        if (!GameTabs.isInventoryTabOpen()) {
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
        Logger.debugLog("Checking which area we are in, and moving to the start tile.");
        switch (location) {
            case "Catherby range":
                checkLocation(catherbyRegion, catherbyArea, catherbyStartTile);
                break;
            case "Hosidius kitchen":
                checkLocation(hosidiusRegion, hosidiusKitchenArea, hosidiusStartTile);
                break;
            case "Nardah oven":
                checkLocation(nardahRegion, nardahArea, nardahStartTile);
                break;
        }
    }

    private void setZoom() {
        Logger.debugLog("Setting correct zoom level based on location.");
        switch (location) {
            case "Catherby range":
            case "Hosidius kitchen":
                Game.setZoom("2");
                break;
            case "Nardah oven":
                Game.setZoom("1");
                break;
        }
    }

    private void initializeBankRects() {
        Logger.debugLog("Initializing all the bank and range/oven areas.");

        // Nardah
        List<RectanglePair> nardahRects = new ArrayList<>();
        nardahRects.add(new RectanglePair(
                new Rectangle(325, 230, 14, 11),    // Bank Rectangle
                new Rectangle(550, 323, 24, 20)     // Range Rectangle
        ));
        nardahRects.add(new RectanglePair(
                new Rectangle(329, 202, 15, 13),    // Bank Rectangle
                new Rectangle(557, 357, 23, 21)     // Range Rectangle
        ));
        nardahRects.add(new RectanglePair(
                new Rectangle(332, 173, 7, 8),      // Bank Rectangle
                new Rectangle(563, 398, 26, 22)     // Range Rectangle
        ));
        nardahRects.add(new RectanglePair(
                new Rectangle(335, 158, 7, 6),      // Bank Rectangle
                new Rectangle(567, 420, 24, 26)     // Range Rectangle
        ));
        bankRectangles.put("Nardah oven", nardahRects);

        // Hosidius Kitchen
        List<RectanglePair> hosidiusRects = new ArrayList<>();
        hosidiusRects.add(new RectanglePair(
                new Rectangle(378, 434, 16, 20),   // Bank Rectangle
                new Rectangle(486, 118, 15, 21)    // Range Rectangle
        ));
        bankRectangles.put("Hosidius kitchen", hosidiusRects);

        // Catherby range
        List<RectanglePair> catherbyRects = new ArrayList<>();
        catherbyRects.add(new RectanglePair(
                new Rectangle(232, 287, 15, 20),   // Bank Rectangle
                new Rectangle(635, 184, 32, 21)    // Range Rectangle
        ));
        catherbyRects.add(new RectanglePair(
                new Rectangle(200, 290, 16, 14),   // Bank Rectangle
                new Rectangle(656, 181, 29, 23)    // Range Rectangle
        ));
        catherbyRects.add(new RectanglePair(
                new Rectangle(141, 290, 21, 17),   // Bank Rectangle
                new Rectangle(704, 188, 16, 14)    // Range Rectangle
        ));
        bankRectangles.put("Catherby range", catherbyRects);

    }

    private void initializeItemIDS() {
        switch (product) {
            case "Seaweed":
                productID = "401";
                break;
            case "Giant seaweed":
                productID = "21504";
                break;
            case "Raw shrimps":
                productID = "317";
                break;
            case "Raw sardine":
                productID = "327";
                break;
            case "Raw herring":
                productID = "345";
                break;
            case "Raw mackerel":
                productID = "353";
                break;
            case "Raw trout":
                productID = "335";
                break;
            case "Raw cod":
                productID = "341";
                break;
            case "Raw pike":
                productID = "349";
                break;
            case "Raw salmon":
                productID = "331";
                break;
            case "Raw karambwan":
                productID = "3142";
                break;
            case "Raw tuna":
                productID = "359";
                break;
            case "Raw lobster":
                productID = "377";
                break;
            case "Raw swordfish":
                productID = "371";
                break;
            case "Raw monkfish":
                productID = "7944";
                break;
            case "Raw shark":
                productID = "383";
                break;
            case "Raw sea turtle":
                productID = "395";
                break;
            case "Raw anglerfish":
                productID = "13439";
                break;
            case "Raw manta ray":
                productID = "389";
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

    private void checkLocation(RegionBox region, Area area, Tile tile) {
        playerPos = Walker.getPlayerPosition(region);
        if (Player.isTileWithinArea(playerPos, area)) {
            if (!Player.atTile(tile, region)) {
                Logger.debugLog("Walking to the " + location + " start tile.");
                Walker.step(tile, region);
                Condition.wait(() -> Player.atTile(tile, region), 200, 20);
                if (!Player.atTile(tile, region)) {
                    Logger.debugLog("Walking to the " + location + " start tile.");
                    Walker.step(tile, region);
                    Condition.wait(() -> Player.atTile(tile, region), 200, 20);
                    if (!Player.atTile(tile, region)) {
                        Logger.debugLog("Walking to the " + location + " start tile.");
                        Walker.step(tile, region);
                        Condition.wait(() -> Player.atTile(tile, region), 200, 20);
                        if (!Player.atTile(tile, region)) {
                            Logger.debugLog("Walking to the " + location + " start tile.");
                            Walker.step(tile, region);
                            Condition.wait(() -> Player.atTile(tile, region), 200, 20);
                            if (!Player.atTile(tile, region)) {
                                Logger.debugLog("Walking to the " + location + " start tile.");
                                Walker.step(tile, region);
                                Condition.wait(() -> Player.atTile(tile, region), 200, 20);
                                if (!Player.atTile(tile, region)) {
                                    Logger.debugLog("Walking to the " + location + " start tile.");
                                    Walker.step(tile, region);
                                    Condition.wait(() -> Player.atTile(tile, region), 200, 20);
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
                Client.tap(new Rectangle(412, 267, 13, 17));
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
        }

        // Wait till bank is open to be double sure
        Condition.wait(() -> Bank.isOpen(), 200, 20);

        // Deposit the entire inventory
        Bank.tapDepositInventoryButton();

        // Set the correct quantities based on config choices

        if (product.equals("Giant seaweed")) {
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
                Bank.tapQuantityAllButton();
                Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 20);
            }
        }

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Bank.openTab(banktab);
            Logger.log("Selecting bank tab " + banktab);
        }

        // Withdraw the first set of items
        Bank.withdrawItem(productID, 0.88);

        // Close the bank after, twice just in case.
        Bank.close();

        if (Bank.isOpen()) {
            Bank.close();
        }

        Condition.wait(() -> !Bank.isOpen(), 200, 20);

        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void bank() {
        Logger.log("Banking.");

        // Select the right bank tab if needed.
        if (!Bank.isSelectedBankTab(banktab)) {
            Bank.openTab(banktab);
            Logger.log("Selecting bank tab " + banktab);
        }

        // Deposit everything
        Bank.tapDepositInventoryButton();
        Condition.sleep(generateRandomDelay(300, 500));

        // Withdraw new items
        Bank.withdrawItem(productID, 0.88);
        Condition.sleep(generateRandomDelay(400, 800));

        // Close the bank after, twice just in case.
        Bank.close();

        if (Bank.isOpen()) {
            Bank.close();
        }

        Condition.wait(() -> !Bank.isOpen(), 200, 20);
    }

    private void cook() {
        Logger.log("Cooking.");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 200, 75);
        Chatbox.makeOption(1);
        Logger.log("Waiting for cooking to finish...");

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        long startTime = System.currentTimeMillis();
        long timeout = 65 * 1000; // 1 minute and 5 seconds in milliseconds

        Condition.wait(() -> {
            boolean inventoryCheck = !Inventory.contains(productID, 0.88);
            boolean levelUpCheck = Player.leveledUp();
            boolean timeCheck = (System.currentTimeMillis() - startTime) >= timeout;
            return inventoryCheck || levelUpCheck || timeCheck;
        }, 200, 325);

        Logger.debugLog("Cooking finished, player leveled up, or timeout reached.");
    }

    public void resetAndRecheck() {
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

}

