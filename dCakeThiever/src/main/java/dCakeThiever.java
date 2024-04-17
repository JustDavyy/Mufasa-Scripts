import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.Area;
import helpers.utils.OptionType;
import helpers.utils.Tile;

import java.awt.*;
import java.util.Map;

import static helpers.Interfaces.*;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ScriptManifest(
        name = "dCakeThiever",
        description = "Steals from the bakery stall at Ardougne market. Supports world hopping and banking. Detects being caught, runs away if needed.",
        version = "1.08",
        guideLink = "https://wiki.mufasaclient.com/docs/dcake-thiever/",
        categories = {ScriptCategory.Thieving}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Banking",
                        description = "Would you like to bank the stolen cakes and breads?",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "World hopping",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dCakeThiever extends AbstractScript {
    // Creating the strings for later use
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    Boolean bankYN;
    Tile playerPos;
    int chocSlice = 1901;
    Tile stallTile = new Tile(84, 45);
    Tile outAttackRange = new Tile(92, 72);
    Tile bankBoothTile = new Tile(65, 77);
    Tile[] pathToStall = new Tile[] {
            new Tile(60, 80),
            new Tile(51, 80),
            new Tile(50, 70),
            new Tile(50, 59),
            new Tile(53, 50),
            new Tile(61, 49),
            new Tile(70, 49),
            new Tile(79, 50),
            new Tile(84, 48)
    };
    Rectangle stallTapWindow = new Rectangle(381, 260, 15, 15);
    Rectangle bankBooth = new Rectangle(486, 246, 28, 30);
    Tile[] pathToBank = new Tile[] {
            new Tile(86, 54),
            new Tile(81, 59),
            new Tile(77, 68),
            new Tile(68, 73),
            new Tile(57, 73),
            new Tile(54, 79),
            new Tile(63, 77)
    };

    Tile[] runAwayPath = new Tile[] {
            new Tile(87, 55),
            new Tile(87, 61),
            new Tile(88, 67),
            outAttackRange
    };

    Tile[] runBackPath = new Tile[] {
            new Tile(88, 67),
            new Tile(86, 60),
            new Tile(87, 54),
            new Tile(87, 47)
    };

    int pollsSinceLastDrop = 0;
    boolean stolen = false;
    boolean droppedChocSlice = false;
    int inventSpotsFree;
    int usedInvent = 0;
    int inventUsed;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("World hopping"));
        hopEnabled = Boolean.valueOf((configs.get("World hopping.enabled")));
        useWDH = Boolean.valueOf((configs.get("World hopping.useWDH")));
        bankYN= Boolean.valueOf((configs.get("Banking")));

        Logger.log("Thank you for using the dCakeThiever script!\nSetting up everything for your gains now...");
        Logger.debugLog("Selected hopProfile: " + hopProfile);

        // Set the map we'll be using (Custom Ardougne)
        Walker.setup("/maps/ArdougneMarket2.png");

        hopActions();
        initialSetup();
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        if (bankYN) {

            if (!Inventory.isFull()) {
                droppedChocSlice = false;
                stealFromStall();
                hopActions();
                readXP();
            } else {
                if (Inventory.contains(chocSlice, 0.9)) {
                    dropChocSlice();
                } else {
                    movetoBank();
                    bank();
                    movetoStall();
                }
            }

        } else {

            stolen = stealFromStall();
            hopActions();
            readXP();

            // Check if tea was stolen before considering to drop
            if (stolen) {
                // Randomly drop items with a 1/20 chance or force after max empty invent slots at script start
                if (ThreadLocalRandom.current().nextInt(20) == 0 || pollsSinceLastDrop >= inventSpotsFree - 1) {
                    dropAll();
                    pollsSinceLastDrop = 0; // Reset the counter after tapping
                } else {
                    pollsSinceLastDrop++; // Increment the counter if not tapping
                }
            }
        }
    }

    private void initialSetup() {
        Logger.debugLog("Starting initialSetup() method.");

        // Get the script area and current player position
        playerPos = Walker.getPlayerPosition();
        Logger.debugLog(String.valueOf(playerPos));
        Area scriptArea = new Area(
                new Tile(36, 20),
                new Tile(100, 99)
        );

        // Check if we are within the script area, otherwise stop the script.

        if (Player.isTileWithinArea(playerPos, scriptArea)) {
            Logger.debugLog("We are located in the Ardougne market area needed for the script to run. Continuing... ");

            // Open inventory if not yet open
            if (!GameTabs.isInventoryTabOpen()) {
                GameTabs.openInventoryTab();
            }

            // Check if inventory is full
            if (Inventory.isFull()) {
                Logger.log("Inventory is full, emptying inventory before proceeding.");

                movetoBank();
                bank();
                movetoStall();

            }

            if (!Player.atTile(stallTile)) {
                movetoStall();
            }

            // Check the amount of inventory spots free (to know when to drop items)
            inventSpotsFree = Inventory.emptySlots();
            usedInvent = Inventory.usedSlots();

            // Close the chat area
            Chatbox.closeChatbox();

            // Enable tap to drop if not yet enabled
            if (!Game.isTapToDropEnabled()) {
                Game.enableTapToDrop();
            }

        } else {
            Logger.log("Could not locate us to the script area in the Ardougne market area. Please move there and start the script again.");
            Logout.logout();
            Script.stop();
        }

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void bank() {
        Logger.debugLog("Starting bank() method.");

        Client.tap(bankBooth);
        Condition.wait(() -> Bank.isOpen(), 250, 30);

        if (Bank.isOpen()) {
            if (Bank.isBankPinNeeded()) {
                Bank.enterBankPin();
                Condition.sleep(750);
            }
            Bank.tapDepositInventoryButton();
            Bank.tapDepositInventoryButton();
            Condition.sleep(500);
            Bank.close();
            Condition.sleep(500);

            if (Bank.isOpen()) {
                Bank.close();
                Condition.sleep(300);
            }
        }

        Logger.debugLog("Ending the bank() method.");
    }

    private void movetoBank() {
        Logger.debugLog("Starting movetoBank() method.");

        Walker.walkPath(pathToBank);

        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        Walker.step(bankBoothTile);
        Condition.wait(() -> Player.atTile(bankBoothTile), 250, 20);

        if (Player.atTile(bankBoothTile)) {
            Logger.debugLog("We are now at the bank booth.");
        } else {
            Walker.step(bankBoothTile);
            Condition.wait(() -> Player.atTile(bankBoothTile), 250, 20);

            if (Player.atTile(bankBoothTile)) {
                Logger.debugLog("We are now at the bank booth.");
            } else {
                Logger.log("Both attempts failed to move towards the bank booth., ending script.");
                Logout.logout();
                Script.stop();
            }
        }

        Logger.debugLog("Ending the movetoBank() method.");
    }

    private void movetoStall() {
        Logger.debugLog("Starting movetoStall() method.");

        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        Walker.walkPath(pathToStall);

        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        Walker.step(stallTile);
        Condition.wait(() -> Player.atTile(stallTile), 250, 20);

        if (Player.atTile(stallTile)) {
            Logger.debugLog("We are now at the bakery stall tile.");
        } else {
            Walker.step(stallTile);
            Condition.wait(() -> Player.atTile(stallTile), 250, 20);

            if (Player.atTile(stallTile)) {
                Logger.debugLog("We are now at the bakery stall tile");
            } else {
                Logger.log("Both attempts failed to move towards the bakery stall, ending script.");
                Logout.logout();
                Script.stop();
            }
        }

        Logger.debugLog("Ending the movetoStall() method.");
    }

    private boolean stealFromStall() {
        if (Game.isPlayersUnderUs()) {
            Logger.debugLog("A player is detected under us, hopping!");
            Game.instantHop(hopProfile);
            Condition.sleep(2500);
            if (!Game.isPlayersUnderUs()) {
                usedInvent = Inventory.usedSlots();
                Client.tap(stallTapWindow);
                // Generate a random number between 2600 and 2750
                Random random = new Random();
                int delay = 2600 + random.nextInt(2750- 2600 + 1);
                Condition.sleep(delay);
                return !checkCaught();
            } else {
                Logger.debugLog("A player is still under us after hopping, proceeding in poll logic.");
                return false;
            }
        } else {
            usedInvent = Inventory.usedSlots();
            Client.tap(stallTapWindow);
            // Generate a random number between 2600 and 2750
            Random random = new Random();
            int delay = 2600 + random.nextInt(2750- 2600 + 1);
            Condition.sleep(delay);
            return !checkCaught();
        }
    }

    private void dropChocSlice() {
        Logger.debugLog("Starting dropChocSlice() method.");

        Inventory.tapAllItems(chocSlice, 0.90);
        Condition.sleep(1250);
        droppedChocSlice = true;

        usedInvent = Inventory.usedSlots();
        inventUsed = 1337;

        Logger.debugLog("Ending the dropChocSlice() method.");
    }

    private void dropAll() {
        Logger.debugLog("Starting dropAll() method.");

        Inventory.tapAllItems(1891, 0.90);
        Inventory.tapAllItems(2309, 0.90);
        Inventory.tapAllItems(chocSlice, 0.90);
        Condition.sleep(500);

        usedInvent = Inventory.usedSlots();

        Logger.debugLog("Ending the dropAll() method.");
    }

    private boolean checkCaught() {

        if (droppedChocSlice) {
            usedInvent = Inventory.usedSlots();
            return false;
        }

        inventUsed = Inventory.usedSlots();

        if (inventUsed == usedInvent) {
            // Logging
            Logger.debugLog("Inventory usage has not changed, assuming we are being caught.");

            // Run away
            Logger.log("Running away from guards!");
            runAway();

            // Run back
            Logger.log("Moving back to the bakery stall.");
            runBack();

            // Eat a bread or choc slice if we have it to heal up
            if (Inventory.contains(2309, 0.9)){
                // Disable tap to drop if enabled
                if (Game.isTapToDropEnabled()) {
                    Game.disableTapToDrop();
                    Condition.sleep(250);
                }

                Inventory.eat(2309, 0.9);
                Condition.sleep(1500);

                // Update invent count
                usedInvent = Inventory.usedSlots();
                inventUsed = 1337;

                // Enable tap to drop again
                Game.enableTapToDrop();
            } else if (Inventory.contains(chocSlice, 0.9)) {
                // Disable tap to drop if enabled
                if (Game.isTapToDropEnabled()) {
                    Game.disableTapToDrop();
                    Condition.sleep(250);
                }

                Inventory.eat(chocSlice, 0.9);
                Condition.sleep(1500);

                // Update invent count
                usedInvent = Inventory.usedSlots();
                inventUsed = 1337;
            } else {
                // Update invent count
                usedInvent = Inventory.usedSlots();
                inventUsed = usedInvent;
            }

            // return true, we were caught
            return true;
        } else {
            // Update invent slot use count for next check as we were not caught
            usedInvent = inventUsed;
            return false;
        }
    }

    private void runAway() {
        // Enable running if it is not enabled
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        // Walk out of attack range
        Walker.walkPath(runAwayPath);

        // Generate a random number between 500 and 1500
        Random random = new Random();
        int delay = 500 + random.nextInt(1500- 500 + 1);
        Condition.sleep(delay);
    }

    private void runBack() {
        // Walk back
        Walker.walkPath(runBackPath);
        Condition.sleep(1250);

        // Step to the actual stall tile
        Walker.step(stallTile);
        Condition.wait(() -> Player.atTile(stallTile), 250, 20);

        playerPos = Walker.getPlayerPosition();

        if (!Player.atTile(stallTile)) {
            Logger.debugLog("Player is not yet at the stall tile, trying to move there again.");
            Walker.step(stallTile);
            Condition.wait(() -> Player.atTile(stallTile), 250, 20);
        }

        playerPos = Walker.getPlayerPosition();

        if (!Player.atTile(stallTile)) {
            Logger.debugLog("Player is still not at stall tile, stopping script!");
            Logout.logout();
            Script.stop();
        }

        // Enable run if not yet enabled for when we need to run/bank again.
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
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