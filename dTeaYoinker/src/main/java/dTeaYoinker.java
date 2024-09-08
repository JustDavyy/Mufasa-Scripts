import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.Area;
import helpers.utils.MapChunk;
import helpers.utils.OptionType;
import helpers.utils.Tile;

import java.awt.*;
import java.util.Map;

import static helpers.Interfaces.*;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ScriptManifest(
        name = "dTeaYoinker",
        description = "Steals from the tea stall in east Varrock. Supports world hopping and banking the tea.",
        version = "1.05",
        guideLink = "https://wiki.mufasaclient.com/docs/dtea-yoinker/",
        categories = {ScriptCategory.Thieving}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Bank the stolen teas?",
                        description = "Would you like to bank the stolen tea?",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dTeaYoinker extends AbstractScript {
    // Creating the strings for later use
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    Boolean bankYN;
    Tile playerPos;
    int tea = 712;

    Tile stallTile = new Tile(13071, 13747, 0);
    Tile bankTile1 = new Tile(5335, 15243, 0);
    Tile bankTile2 = new Tile(5339, 15243, 0);
    Tile bankTile3 = new Tile(5343, 15243, 0);
    Rectangle boothArea = new Rectangle(432, 307, 19, 24);
    Rectangle stallTapWindow = new Rectangle(493, 212, 56, 75);
    int pollsSinceLastDrop = 0;

    boolean stolen = false;
    int inventSpotsFree;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        bankYN= Boolean.valueOf((configs.get("Bank the stolen teas?")));

        Logger.log("Thank you for using the dTeaYoinker script!\nSetting up everything for your gains now...");

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"51-53"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        hopActions();
        initialSetup();
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        if (bankYN) {

            if (!Inventory.isFull()) {
                stealFromStall();
                hopActions();
                readXP();
            } else {
                movetoBank();
                bank();
                movetoStall();
            }

        } else {

            stolen = stealFromStall();
            hopActions();
            readXP();

            // Check if tea was stolen before considering to drop
            if (stolen) {
                // Randomly drop items with a 1/20 chance or force after max empty invent slots at script start
                if (ThreadLocalRandom.current().nextInt(20) == 0 || pollsSinceLastDrop >= inventSpotsFree - 1) {
                    dropTea();
                    pollsSinceLastDrop = 0; // Reset the counter after tapping
                } else {
                    pollsSinceLastDrop++; // Increment the counter if not tapping
                }
            }
        }
    }

    private void initialSetup() {
        Logger.debugLog("Starting initialSetup() method.");

        Area scriptArea = new Area(
                new Tile(12964, 13645, 0),
                new Tile(13123, 13774, 0)
        );

        // Check if we are within the script area, otherwise stop the script.
        playerPos = Walker.getPlayerPosition();
        Logger.log("Location: " + playerPos.toString());
        if (Player.within(scriptArea)) {
            Logger.debugLog("We are located in the Varrock area needed for the script to run. Continuing... ");

            // Check if inventory is full
            if (Inventory.isFull()) {
                Logger.log("Inventory is full, emptying inventory before proceeding.");

                movetoBank();
                bank();
                movetoStall();

            }

            // Open inventory if not yet open
            if (!GameTabs.isInventoryTabOpen()) {
                GameTabs.openInventoryTab();
            }

            // Check the amount of inventory spots free (to know when to drop tea)
            inventSpotsFree = Inventory.emptySlots();

            // Close the chat area
            Chatbox.closeChatbox();

            // Enable tap to drop if not yet enabled (but only if banking is not enabled)
            if (!bankYN) {
                if (!Game.isTapToDropEnabled()) {
                    Game.enableTapToDrop();
                }
            }

            // Check if we're at stall, if not move there.
            if (!Player.atTile(stallTile)) {
                Logger.debugLog("Not at stall, moving there!");
                Walker.step(stallTile);
                Condition.wait(() -> Player.atTile(stallTile), 250, 20);
            }

        } else {
            Logger.log("Could not locate us to the script area in east Varrock. Please move there and start the script again.");
            Logout.logout();
            Script.stop();
        }

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void bank() {
        Logger.debugLog("Starting bank() method.");

        Client.tap(boothArea);
        Condition.wait(() -> Bank.isOpen(), 200, 12);

        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
            Condition.sleep(500);
        }

        Bank.tapDepositInventoryButton();

        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 200, 12);

        if (Bank.isOpen()) {
            Bank.close();
        }

        Logger.debugLog("Ending the bank() method.");
    }

    private void movetoBank() {
        Logger.debugLog("Starting movetoBank() method.");

        int randomIndex = ThreadLocalRandom.current().nextInt(1, 4); // Generates 1, 2, or 3

        Logger.debugLog("Walking to a randomly chosen bank booth.");

        switch (randomIndex) {
            case 1:
                Walker.step(bankTile1);
                break;
            case 2:
                Walker.step(bankTile2);
                break;
            case 3:
                Walker.step(bankTile3);
                break;
        }

        // Adding a delay to allow the player to finish walking
        Condition.sleep(750);

        Logger.debugLog("Checking if the player has reached the bank.");
        boolean atBank = false;
        playerPos = Walker.getPlayerPosition();

        // Check if we are at one of the bank tiles
        if (Player.tileEquals(playerPos, bankTile1) || Player.tileEquals(playerPos, bankTile2) || Player.tileEquals(playerPos, bankTile3)) {
            atBank = true;
            Logger.debugLog("Player has reached the bank.");
        }

        if (!atBank) {
            Logger.debugLog("Player did not reach the bank.");
            Logger.debugLog("Attempting to walk to a new randomly chosen bank tile.");
            switch (randomIndex) {
                case 1:
                    Walker.step(bankTile1);
                    break;
                case 2:
                    Walker.step(bankTile2);
                    break;
                case 3:
                    Walker.step(bankTile3);
                    break;
            }
            Condition.sleep(750);
        }
        Logger.debugLog("Ending the movetoBank() method.");
    }

    private void movetoStall() {
        Logger.debugLog("Starting movetoStall() method.");

        // Initial attempt to walk to the stall
        Walker.step(stallTile);

        // Adding a delay to allow the player to finish walking
        Random random = new Random();
        int delay = 4000 + random.nextInt(400); // 4000 to 4400 milliseconds
        Condition.sleep(delay);

        // Check if at the stall
        if (!Player.atTile(stallTile)) {
            Logger.debugLog("Player is not at the stall, attempting to move.");
            Walker.step(stallTile);
            // Additional delay after attempting to move
            Condition.sleep(delay);
        } else {
            Logger.debugLog("Player has reached the stall.");
        }

        Logger.debugLog("Ending the movetoStall() method.");
    }

    private boolean stealFromStall() {
        if (Game.isPlayersUnderUs()) {
            Logger.debugLog("There is a player under us, hopping worlds to prevent issues with clicks!");
            Game.instantHop(hopProfile);

            GameTabs.openInventoryTab();
            if (Game.isPlayersUnderUs()) {
                Logger.debugLog("A player is still under us in the new world, doing nothing...");
                return false;
            }
        }
        Client.tap(stallTapWindow);

        // Generate a random number between 4000 and 4400
        Random random = new Random();
        int delay = 4000 + random.nextInt(4400- 4000 + 1);
        Condition.sleep(delay);
        return true;
    }

    private void dropTea() {
        Logger.debugLog("Starting dropTea() method.");

        Inventory.tapAllItems(tea, 0.75);

        Logger.debugLog("Ending the dropTea() method.");
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