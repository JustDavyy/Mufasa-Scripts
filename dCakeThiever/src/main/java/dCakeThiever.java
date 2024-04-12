import helpers.*;
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
        description = "Steals from the cake stall at Kourend castle. Supports world hopping and banking. Detects being caught, runs away if needed.",
        version = "1.02",
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
    Tile stallTile = new Tile(81, 40);
    Tile groundStairs = new Tile(54, 46);
    Tile groundStairsFromUp = new Tile(54, 45);
    Tile topLevelStairs = new Tile(310, 33);
    Tile floor1Stairs = new Tile(198, 29);
    Tile outAttackRange = new Tile(69, 25);
    Rectangle backupStairTap = new Rectangle(313, 248, 80, 60);
    Rectangle stallTapWindow = new Rectangle(435, 296, 83, 54);
    Rectangle floor1InstantStairs = new Rectangle(537, 3, 27, 19);
    Rectangle floor1StairsFromDoor = new Rectangle(438, 45, 59, 56);
    Rectangle floor2InstantDepoBox = new Rectangle(136, 389, 34, 31);
    Rectangle floor2DepoBoxFromDoor = new Rectangle(240, 248, 18, 32);
    Rectangle floor2InstantStairsDown = new Rectangle(633, 113, 28, 17);
    Rectangle floor2StairsDownFromDoor = new Rectangle(561, 84, 28, 18);
    Rectangle floor1StairsDownStep1 = new Rectangle(437, 492, 30, 25);
    Rectangle floor1StairsDownStep2 = new Rectangle(423, 327, 98, 53);
    Rectangle floor1StairsDownFromDoor = new Rectangle(419, 412, 110, 67);
    Rectangle groundFloorStairs = new Rectangle(307, 210, 79, 48);
    Rectangle f1doorToBank = new Rectangle(521, 86, 24, 48);
    Rectangle f2doorToBank = new Rectangle(316, 420, 13, 40);
    Rectangle f2doorFromBank = new Rectangle(556, 292, 16, 31);
    Rectangle f1doorFromBank = new Rectangle(471, 412, 39, 22);
    Tile[] pathToGroundStairs = new Tile[] {
            new Tile(74, 47),
            groundStairs
    };
    Tile[] pathToStall = new Tile[] {
            new Tile(74, 47),
            stallTile
    };

    Tile[] runAwayPath = new Tile[] {
            new Tile(70, 43),
            outAttackRange
    };

    Tile[] runBackPath = new Tile[] {
            new Tile(69, 41),
            new Tile(76, 42)
    };
    int pollsSinceLastDrop = 0;
    boolean stolen = false;
    boolean droppedChocSlice = false;
    int inventSpotsFree;
    int usedInvent = 0;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("World hopping"));
        hopEnabled = Boolean.valueOf((configs.get("World hopping.enabled")));
        useWDH = Boolean.valueOf((configs.get("World hopping.useWDH")));
        bankYN= Boolean.valueOf((configs.get("Banking")));

        Logger.log("Thank you for using the dCakeThiever script!\nSetting up everything for your gains now...");

        // Set the map we'll be using (Custom Kourend)
        Walker.setup("/maps/Kourend.png");

        hopActions();
        initialSetup();
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        if (bankYN) {

            if (!Inventory.isFull()) {
                stealFromStall();
                dropChocSlice();
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
        Area scriptArea = new Area(
                new Tile(57, 35),
                new Tile(107, 81)
        );

        // Check if we are within the script area, otherwise stop the script.

        if (Player.isTileWithinArea(playerPos, scriptArea)) {
            Logger.debugLog("We are located in the Kourend castle outer area needed for the script to run. Continuing... ");

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
            Logger.log("Could not locate us to the script area in the Kourend castle outer area. Please move there and start the script again.");
            Logout.logout();
            Script.stop();
        }

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void bank() {
        Logger.debugLog("Starting bank() method.");

        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
            Condition.sleep(500);
        }

        Rectangle depositAll = DepositBox.findDepositInventory();
        if (depositAll != null) {
            Client.tap(depositAll);
        }
        Condition.sleep(500);
        Rectangle closeDeposit = DepositBox.findCloseDepositBox();
        if (closeDeposit != null) {
            Client.tap(closeDeposit);
        }
        Condition.sleep(500);

        Logger.debugLog("Ending the bank() method.");
    }

    private void movetoBank() {
        Logger.debugLog("Starting movetoBank() method.");

        // Ground area part
        Logger.debugLog("Walking to the ground level stairs");
        Walker.walkPath(pathToGroundStairs);

        // Adding a delay to allow the player to finish walking
        Condition.sleep(5000);

        Logger.debugLog("Checking if the player has reached the ground level stairs.");
        boolean atStairs = false;
        playerPos = Walker.getPlayerPosition();

        if (Player.atTile(groundStairs)) {
            atStairs = true;
            Logger.debugLog("Player has reached the ground stairs.");
        }

        if (!atStairs) {
            Logger.debugLog("Player did not reach the ground stairs.");
            Walker.step(groundStairsFromUp);
            // Adding a delay to allow the player to finish walking
            Random random = new Random();
            int delay = 2200 + random.nextInt(400); // 2200 to 2600 milliseconds
            Condition.sleep(delay);
            Logger.debugLog("Attempting to walk to the ground stairs.");
        }

        // Check if we are now at the stairs
        playerPos = Walker.getPlayerPosition();
        if (Player.atTile(groundStairs)) {
            atStairs = true;
            Logger.debugLog("Player has reached the ground stairs.");
            Logger.debugLog("Proceeding to floor 1.");
            Client.tap(groundFloorStairs);
            Condition.sleep(2500);
        } else if (Player.atTile(groundStairsFromUp)) {
            atStairs = true;
            Logger.debugLog("Player has reached the ground stairs.");
            Logger.debugLog("Proceeding to floor 1.");
            Client.tap(backupStairTap);
            Condition.sleep(2500);
            if (Player.atTile(new Tile (196, 37))) {
                Client.tap(new Rectangle(425, 303, 25, 24));
                Condition.sleep(2500);
            }
        }

        if (!atStairs) {
            Logger.debugLog("We still have not reached the ground stairs. Something must have gone wrong.");
            Logger.log("Something went wrong walking to the bank, stopping script!");
            Logout.logout();
            Script.stop();
        }

        // Proceed to move to and through first floor
        Logger.debugLog("Walking to the floor 1 stairs");

        // Check if the door is open or not.
        Random random = new Random();
        int delay = 2200 + random.nextInt(400); // 2200 to 2600 milliseconds
        java.awt.Rectangle floor1Door = Objects.getNearest("/images/toBank/doorFloor1.png");
        if (floor1Door != null && !floor1Door.isEmpty()) {
            // Door is shut, so we will use the alternate moving here.
            Client.tap(f1doorToBank);
            Condition.sleep(delay); // Use the random delay
            Client.tap(floor1StairsFromDoor);
            Condition.sleep(delay); // Use the random delay
        } else {
            // Door is open, we can instantly move.
            Client.tap(floor1InstantStairs);
            Condition.sleep(4500);
        }

        // Check if we have arrived at the top floor.
        playerPos = Walker.getPlayerPosition();
        if (!Player.atTile(topLevelStairs)) {
            Logger.debugLog("Something went wrong while moving around floor 1.");
            Logger.log("Something went wrong walking to the bank, stopping script!");
            Logout.logout();
            Script.stop();
        } else {
            Logger.debugLog("We have reached the top floor.");
        }

        // Proceed to move through the second floor
        // Check if the door is open or not.
        int delay2 = 2200 + random.nextInt(400); // 2200 to 2600 milliseconds
        java.awt.Rectangle floor2Door = Objects.getNearest("/images/toBank/doorBank.png");
        if (floor2Door != null && !floor2Door.isEmpty()) {
            // Door is shut, so we will use the alternate moving here.
            Client.tap(f2doorToBank);
            Condition.sleep(delay2); // Use the random delay
            Client.tap(floor2DepoBoxFromDoor);
            Condition.sleep(delay2); // Use the random delay
        } else {
            // Door is open, we can instantly move.
            Client.tap(floor2InstantDepoBox);
            Condition.sleep(4500);
        }

        Logger.debugLog("Ending the movetoBank() method.");
    }

    private void movetoStall() {
        Logger.debugLog("Starting movetoStall() method.");

        // Walk through the top floor part
        Logger.debugLog("Heading from the top floor down to the 1st floor.");

        // Check if the door is open or not.
        Random random = new Random();
        int delay = 2000 + random.nextInt(400); // 2000 to 2400 milliseconds
        java.awt.Rectangle topFloorDoor = Objects.getNearest("/images/fromBank/doorBank.png");
        if (topFloorDoor != null && !topFloorDoor.isEmpty()) {
            // Door is shut, so we will use the alternate moving here.
            Client.tap(f2doorFromBank);
            Condition.sleep(delay); // Use the random delay
            Client.tap(floor2StairsDownFromDoor);
            Condition.sleep(delay); // Use the random delay
        } else {
            // Door is open, we can instantly move.
            Client.tap(floor2InstantStairsDown);
            Condition.sleep(4500);
        }

        // Check if we have arrived at the 1st floor.
        playerPos = Walker.getPlayerPosition();
        if (!Player.atTile(floor1Stairs)) {
            Logger.debugLog("Something went wrong while moving around the top floor.");
            Logger.log("Something went wrong walking back from the bank, stopping script!");
            Logout.logout();
            Script.stop();
        } else {
            Logger.debugLog("We have reached the first floor.");
        }

        // Walk through the 1st floor part
        Logger.debugLog("Heading from the 1st floor down to the ground floor.");

        // Check if the door is open or not.
        int delay2 = 2200 + random.nextInt(400); // 2200 to 2600 milliseconds
        java.awt.Rectangle firstFloorDoor = Objects.getNearest("/images/fromBank/doorFloor1.png");
        if (firstFloorDoor != null && !firstFloorDoor.isEmpty()) {
            // Door is shut, so we will use the alternate moving here.
            Client.tap(f1doorFromBank);
            Condition.sleep(delay2); // Use the random delay
            Client.tap(floor1StairsDownFromDoor);
            Condition.sleep(4500); // Use the random delay
        } else {
            // Door is open, we can instantly move.
            Client.tap(floor1StairsDownStep1);
            Condition.sleep(4500);
            Client.tap(floor1StairsDownStep2);
            Condition.sleep(4500);
        }

        // Check if we have arrived at the ground floor.
        playerPos = Walker.getPlayerPosition();
        if (!Player.atTile(groundStairsFromUp)) {
            Logger.debugLog("Something went wrong while moving around the first floor.");
            Logger.log("Something went wrong walking back from the bank, stopping script!");
            Logout.logout();
            Script.stop();
        } else {
            Logger.debugLog("We have reached the ground floor.");
        }

        // Proceed to move towards the stall
        Logger.debugLog("Heading from the ground floor stairs to the bakery stall.");
        Walker.walkPath(pathToStall);

        // Adding a delay to allow the player to finish walking
        Condition.sleep(3000);

        Logger.debugLog("Checking if the player has reached the bakery stall tile.");
        boolean atStall = false;
        playerPos = Walker.getPlayerPosition();

        if (Player.atTile(stallTile)) {
            atStall = true;
            Logger.debugLog("Player has reached the bakery stall tile.");
        }

        if (!atStall) {
            Logger.debugLog("Player did not reach the bakery stall tile.");
            Walker.step(stallTile);
            // Adding a delay to allow the player to finish walking
            Condition.sleep(5500);
            Logger.debugLog("Attempting to walk to the bakery stall tile.");
        }

        // Check if we are now at the stairs
        playerPos = Walker.getPlayerPosition();
        if (Player.atTile(stallTile)) {
            atStall = true;
            Logger.debugLog("Player has reached the bakery stall tile.");
        }

        if (!atStall) {
            Logger.debugLog("We still have not reached the bakery stall tile. Something must have gone wrong.");
            Logger.log("Something went wrong walking to the bakery stall, stopping script!");
            Logout.logout();
            Script.stop();
        }

        Logger.debugLog("Ending the movetoStall() method.");
    }

    private boolean stealFromStall() {
        java.awt.Rectangle foundObjects = Objects.getNearest("/images/cakePresent.png");

        // Generate a random number between 2450 and 2850
        Random random = new Random();
        int delay = 2450 + random.nextInt(2850- 2450 + 1);

        if (foundObjects != null && !foundObjects.isEmpty()) {
            Client.tap(stallTapWindow);
            Condition.sleep(delay); // Use the random delay

            // If not caught, return true (successful theft), else return false (caught)
            return !checkCaught();
        } else {
            Logger.debugLog("No cake was found in the stall. Skipping attempt to steal.");
            return false; // Not stolen
        }
    }

    private void dropChocSlice() {
        Logger.debugLog("Starting dropChocSlice() method.");

        if (Inventory.contains(chocSlice, 0.90)) {
            Inventory.tapAllItems(chocSlice, 0.90);
            Condition.sleep(500);
            droppedChocSlice = true;
        } else {
            droppedChocSlice = false;
        }

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

        int inventUsed = Inventory.usedSlots();

        if (inventUsed == usedInvent) {
            // Logging
            Logger.debugLog("Inventory usage has not changed, assuming we are being caught.");

            // Run away
            Logger.log("Running away from guards!");
            runAway();

            // Run back
            Logger.log("Moving back to the bakery stall.");
            runBack();

            // Disable tap to drop if enabled
            if (Game.isTapToDropEnabled()) {
                Game.disableTapToDrop();
            }

            // Eat a bread if we have it to heal up
            if (Inventory.contains(2309, 0.90)){
                Inventory.tapItem(2309, false, 0.90);

                Condition.sleep(750);

                // Update invent count
                usedInvent = Inventory.usedSlots();
                inventUsed = usedInvent;
            }

            // Enable tap to drop again
            Game.enableTapToDrop();

            // return true, we were caught
            return true;
        } else {
            // Update invent slot use count for next check.
            usedInvent = inventUsed;
            return false;
        }
    }

    private void runAway() {
        // Enable running if it is not enabled
        if (!Player.isRunEnabled()) {
            Player.toggleRun();
        }

        // Generate a random number between 1500 and 2500
        Random random = new Random();
        int delay = 1500 + random.nextInt(2500- 1500 + 1);

        // Walk out of attack range
        Walker.walkPath(runAwayPath);
        Condition.sleep(delay);
    }

    private void runBack() {
        // Walk back
        Walker.walkPath(runBackPath);
        Condition.sleep(2000);

        // Generate a random number between 3500 and 4000
        Random random = new Random();
        int delay = 3500 + random.nextInt(4000- 3500 + 1);

        // Step to the actual stall tile
        Walker.step(stallTile);
        Condition.sleep(delay);

        playerPos = Walker.getPlayerPosition();

        if (!Player.atTile(stallTile)) {
            Logger.debugLog("Player is not yet at the stall tile, trying to move there again.");
            Walker.step(stallTile);
            Condition.sleep(5000);
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