import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import helpers.utils.RegionBox;
import helpers.utils.Tile;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import static helpers.Interfaces.*;
import static helpers.Interfaces.Logout;

import java.util.Random;

@ScriptManifest(
        name = "dCannonball Smelter",
        description = "Smelts steel bars into cannonballs at various locations. Supports hopping worlds.",
        version = "1.01",
        guideLink = "https://wiki.mufasaclient.com/docs/dcannonball-smelter/",
        categories = {ScriptCategory.Smithing, ScriptCategory.Moneymaking}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Location",
                        description = "Which location would you like to create cannonballs at?",
                        defaultValue = "Edgeville",
                        allowedValues = {
                                @AllowedValue(optionName = "Edgeville"),
                                @AllowedValue(optionName = "Mount Karuulm"),
                                @AllowedValue(optionName = "Neitiznot"),
                                @AllowedValue(optionName = "Prifddinas")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Bank Tab",
                        description = "What bank tab are your steel bars located in?",
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

public class dCannonballSmelter extends AbstractScript {
    private Random random = new Random();
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String location;
    String steelbar = "2353";
    String mouldUsed = "Nothing";
    int banktab;
    Boolean runEnabled;

    // Tiles
    Tile edgeBankTile = new Tile(51, 49);
    Tile edgeFurnaceTile = new Tile(68, 43);
    Tile karuulmFurnaceTile = new Tile(69, 58);
    Tile karuulmBankTile = new Tile(69, 38);
    Tile neitFurnaceTile = new Tile(57, 24);
    Tile neitBankTile = new Tile(48, 27);
    Tile priffFurnaceTile = new Tile(37, 38);
    Tile priffBankTile1 = new Tile(48, 28);
    Tile priffBankTile2 = new Tile(48, 30);
    Tile priffBankTile3 = new Tile(48, 31);

    // Furnace Rectangles (always calculated from the bank tile)
    Rectangle edgeFurnaceRect = new Rectangle(657, 191, 13, 13);
    Rectangle neitFurnaceRect = new Rectangle(628, 186, 11, 16);
    Rectangle priffFurnaceRect1 = new Rectangle(260, 425, 13, 8);
    Rectangle priffFurnaceRect2 = new Rectangle(265, 399, 19, 9);
    Rectangle priffFurnaceRect3 = new Rectangle(271, 380, 21, 9);

            // Bank Rectangles (always calculated from the furnace tile)
    Rectangle edgeBankRect = new Rectangle(177, 368, 11, 11);
    Rectangle karuulmBankRect = new Rectangle(442, 82, 9, 7);
    Rectangle neitBankRect = new Rectangle(266, 316, 17, 16);
    Rectangle priffBankRect = new Rectangle(575, 166, 14, 26);

    // Rectangles to open the bank when on the tile (once during startup)
    Rectangle openEdgeBankONCE = new Rectangle(442, 281, 10, 6);
    Rectangle openKaruulmBankONCE = new Rectangle(442, 249, 11, 8);
    Rectangle openNeitBankONCE = new Rectangle(469, 263, 15, 13);
    Rectangle openPriffBankONCE = new Rectangle(461, 257, 10, 10);

    // Paths
    Tile[] pathToKaruulmFurnace = new Tile[] {
            new Tile(68, 45),
            new Tile(68, 53)
    };

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        location = configs.get("Location");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        // Set up the walker based on the area we are located in.
        if (java.util.Objects.equals(location, "Edgeville")) {
            Walker.setup("/maps/Edgeville.png");
        } else if (java.util.Objects.equals(location, "Mount Karuulm")) {
            Walker.setup("/maps/MountKaruulm.png");
        } else if (java.util.Objects.equals(location, "Neitiznot")) {
            Walker.setup("/maps/Neitiznot.png");
        } else if (java.util.Objects.equals(location, "Prifddinas")) {
            Walker.setup("/maps/Prifddinas.png");
        }

        // Initialize hop timer for this run if hopping is enabled
        hopActions();

        // Close chatbox
        Chatbox.closeChatbox();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dCannonball Smelter script!");
        Logger.log("Setting up everything for your gains now...");

        // One-time setup
        initialSetup();

        if (Player.isRunEnabled()) {
            runEnabled = true;
        } else {
            runEnabled = false;
        }
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        doSmelting();
        readXP();
        bank();



    }

    private void initialSetup() {
        Logger.debugLog("Starting initialSetup() method.");

        // Set zoom based on which location was selected
        if (java.util.Objects.equals(location, "Edgeville")) {
            Game.setZoom("1");
        } else if (java.util.Objects.equals(location, "Mount Karuulm")) {
            Game.setZoom("1");
        } else if (java.util.Objects.equals(location, "Neitiznot")) {
            Game.setZoom("2");
        } else if (java.util.Objects.equals(location, "Prifddinas")) {
            Game.setZoom("1");
        }

        // Make sure the inventory is open
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        // Check if we have the moulds here, if no log out with error message
        if (!Inventory.containsAny(new int[]{4,27012}, 0.9)) {
            Logger.log("We have no ammo mould in our inventory. Stopping script!");
            Logout.logout();
            Script.stop();
        }

        // Update which mould we are using to be sure.
        if (Inventory.contains(27012, 0.9)) {
            mouldUsed = "Double";
        } else {
            mouldUsed = "Single";
        }

        // Move to the bank tile we will use, then use Rectangles for the clicks to the furnace and back to the bank
        if (java.util.Objects.equals(location, "Edgeville")) {
            Logger.debugLog("Moving towards the " + location + " bank.");
            Walker.step(edgeBankTile);
            Condition.wait(() -> Player.atTile(edgeBankTile), 250, 45);
            if (!Player.atTile(edgeBankTile)) {
                Walker.step(edgeBankTile);
                Condition.wait(() -> Player.atTile(edgeBankTile), 250, 15);
                if (!Player.atTile(edgeBankTile)) {
                    Logger.debugLog("An error occured walking to the " + location + " bank, stopping script.");
                    Logout.logout();
                    Script.stop();
                }
            }
        } else if (java.util.Objects.equals(location, "Mount Karuulm")) {
            Logger.debugLog("Moving towards the " + location + " bank.");
            Walker.step(karuulmBankTile);
            Condition.wait(() -> Player.atTile(karuulmBankTile), 250, 45);
            if (!Player.atTile(karuulmBankTile)) {
                Walker.step(karuulmBankTile);
                Condition.wait(() -> Player.atTile(karuulmBankTile), 250, 15);
                if (!Player.atTile(karuulmBankTile)) {
                    Logger.debugLog("An error occured walking to the " + location + " bank, stopping script.");
                    Logout.logout();
                    Script.stop();
                }
            }
        } else if (java.util.Objects.equals(location, "Neitiznot")) {
            Logger.debugLog("Moving towards the " + location + " bank.");
            Walker.step(neitBankTile);
            Condition.wait(() -> Player.atTile(neitBankTile), 250, 45);
            if (!Player.atTile(neitBankTile)) {
                Walker.step(neitBankTile);
                Condition.wait(() -> Player.atTile(neitBankTile), 250, 15);
                if (!Player.atTile(neitBankTile)) {
                    Logger.debugLog("An error occured walking to the " + location + " bank, stopping script.");
                    Logout.logout();
                    Script.stop();
                }
            }
        } else if (java.util.Objects.equals(location, "Prifddinas")) {
            Logger.debugLog("Moving towards the " + location + " bank.");
            Walker.step(priffBankTile3);
            Condition.wait(() -> Player.atTile(priffBankTile3), 250, 45);
            if (!Player.atTile(priffBankTile3)) {
                Walker.step(priffBankTile3);
                Condition.wait(() -> Player.atTile(priffBankTile3), 250, 15);
                if (!Player.atTile(priffBankTile3)) {
                    Logger.debugLog("An error occured walking to the " + location + " bank, stopping script.");
                    Logout.logout();
                    Script.stop();
                }
            }
        }

        // Open the bank
        if (java.util.Objects.equals(location, "Edgeville")) {
            Client.tap(openEdgeBankONCE);
        } else if (java.util.Objects.equals(location, "Mount Karuulm")) {
            Client.tap(openKaruulmBankONCE);
        } else if (java.util.Objects.equals(location, "Neitiznot")) {
            Client.tap(openNeitBankONCE);
        } else if (java.util.Objects.equals(location, "Prifddinas")) {
            Client.tap(openPriffBankONCE);
        }

        // Do the rest of the bank setup
        Condition.wait(() -> Bank.isOpen(), 250, 20);
        if (!Bank.isOpen()) {
            Logger.debugLog("Failed to open bank, stopping script.");
            Logout.logout();
            Script.stop();
        }
        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
            Condition.sleep(750);
        }
        if (Bank.isSelectedQuantityAllButton()) {
            Bank.tapQuantityAllButton();
        }
        if (!Bank.isSelectedBankTab(banktab)) {
            Bank.openTab(banktab);
            Condition.sleep(750);
        }

        // Withdraw the steel bars
        Bank.withdrawItem(steelbar, 0.9);

        // Close the bank interface
        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 250, 20);
        if (Bank.isOpen()) {
            Bank.close();
            Condition.wait(() -> !Bank.isOpen(), 250, 20);
        }

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void bank() {
        Logger.log("Banking.");
        Logger.debugLog("Starting bank() method.");

        if (java.util.Objects.equals(location, "Edgeville")) {
            Logger.debugLog("Tapping the " + location + " bank.");
            Client.tap(edgeBankRect);
            Condition.wait(() -> Bank.isOpen(), 250, 45);
        } else if (java.util.Objects.equals(location, "Mount Karuulm")) {
            Logger.debugLog("Tapping the " + location + " bank.");
            Client.tap(karuulmBankRect);
            Condition.wait(() -> Bank.isOpen(), 250, 55);
        } else if (java.util.Objects.equals(location, "Neitiznot")) {
            Logger.debugLog("Tapping the " + location + " bank.");
            Client.tap(neitBankRect);
            Condition.wait(() -> Bank.isOpen(), 250, 55);
        } else if (java.util.Objects.equals(location, "Prifddinas")) {
            Logger.debugLog("Tapping the " + location + " bank.");
            Client.tap(priffBankRect);
            Condition.wait(() -> Bank.isOpen(), 250, 55);
        }

        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
        }
        Bank.withdrawItem(steelbar, 0.9);


        Bank.close();
        Condition.wait(() -> !Bank.isOpen(),250,20);
        if (Bank.isOpen()) {
            Bank.close();
            Condition.sleep(500);
        }

        Condition.sleep(1000);

        Logger.debugLog("Ending the bank() method.");
    }

    private void doSmelting() {
        Logger.log("Proceeding to process the steel bars.");
        if (!runEnabled) {
            Player.toggleRun();
        }

        if (!Inventory.contains(steelbar, 0.9)) {
            Logger.debugLog("No steel bars found in inventory, did we run out?");
            bank();
            if (!Inventory.contains(steelbar, 0.9)) {
                Logger.debugLog("No steel bars found in inventory, did we run out? Doing one more check before stopping the script.");
                bank();
                if (!Inventory.contains(steelbar, 0.9)) {
                    Logger.debugLog("No steel bars found in inventory, we ran out. Stopping script.");
                    Logout.logout();
                    Script.stop();
                }
            }
        }

        if (java.util.Objects.equals(location, "Edgeville")) {
            Logger.debugLog("Tapping the " + location + " furnace.");
            Client.tap(edgeFurnaceRect);
            Condition.wait(() -> Player.atTile(edgeFurnaceTile), 250, 45);
            Chatbox.makeOption(1);
            waitForFinish();
        } else if (java.util.Objects.equals(location, "Mount Karuulm")) {
            Logger.debugLog("Tapping the " + location + " furnace.");
            Walker.walkPath(pathToKaruulmFurnace);
            Condition.sleep(generateDelay(1500,2500));
            Walker.step(karuulmFurnaceTile);
            Condition.wait(() -> Player.atTile(edgeFurnaceTile), 250, 20);
            if (!Player.atTile(karuulmFurnaceTile)) {
                Walker.step(karuulmFurnaceTile);
                Condition.wait(() -> Player.atTile(edgeFurnaceTile), 250, 16);

                if (!Player.atTile(karuulmFurnaceTile)) {
                    Walker.step(karuulmFurnaceTile);
                    Condition.wait(() -> Player.atTile(edgeFurnaceTile), 250, 16);

                    if (!Player.atTile(karuulmFurnaceTile)) {
                        Logger.debugLog("Failed to move to the " + location + " furnace, stopping script.");
                        Logout.logout();
                        Script.stop();
                    }
                }
            }

            Client.tap(new Rectangle(463, 272, 36, 33));
            Condition.wait(() -> Chatbox.isMakeMenuVisible(), 250, 20);
            Chatbox.makeOption(1);
            waitForFinish();
        } else if (java.util.Objects.equals(location, "Neitiznot")) {
            Logger.debugLog("Tapping the " + location + " furnace.");
            Client.tap(neitFurnaceRect);
            Condition.wait(() -> Player.atTile(neitFurnaceTile), 250, 45);
            Chatbox.makeOption(1);
            waitForFinish();
        } else if (java.util.Objects.equals(location, "Prifddinas")) {
            Tile currentTile = Walker.getPlayerPosition();

            // Determine which furnace rectangle to tap based on player's tile
            Rectangle tapRectangle = null;
            if (currentTile.x() == priffBankTile1.x() && currentTile.y() == priffBankTile1.y()) {
                tapRectangle = priffFurnaceRect1;
            } else if (currentTile.x() == priffBankTile2.x() && currentTile.y() == priffBankTile2.y()) {
                tapRectangle = priffFurnaceRect2;
            } else if (currentTile.x() == priffBankTile3.x() && currentTile.y() == priffBankTile3.y()) {
                tapRectangle = priffFurnaceRect3;
            } else {
                Logger.debugLog("Player is not at a recognized tile, stopping script.");
                Logout.logout();
                Script.stop();
                return;  // Exit if not at one of the expected tiles
            }

            Client.tap(tapRectangle);

            // Wait to be at the furnace tile after tapping
            Condition.wait(() -> Player.atTile(priffFurnaceTile), 250, 45);
            Chatbox.makeOption(1);
            waitForFinish();
        }
    }

    private void waitForFinish() {
        Logger.log("Waiting for inventory to finish processing...");
        // Define random generator
        Random random = new Random();

        // Calculate a random delay between 200ms and 400ms
        int randomDelay = 200 + random.nextInt(201); // nextInt(201) gives a random integer from 0 to 200

        // Calculate the number of checks to not exceed 15 seconds total duration
        int maxChecks = 15000 / randomDelay; // 15000ms is 15 seconds

        if (java.util.Objects.equals(mouldUsed, "Double")) {
            runEnabled = Player.isRunEnabled();
            Condition.sleep(70000);
            Condition.wait(() -> !Inventory.contains(steelbar, 0.9), randomDelay, maxChecks);

        } else {
            runEnabled = Player.isRunEnabled();
            Condition.sleep(150000);
            Condition.wait(() -> !Inventory.contains(steelbar, 0.9), randomDelay, maxChecks);
        }

        Logger.log("Processing is completed!");
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