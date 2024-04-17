import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import helpers.utils.RegionBox;
import helpers.utils.Tile;
import helpers.utils.Area;

import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.time.Instant;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dMinnows Fisher",
        description = "Fishes Minnows at Kylie Minnow's fishing platform at the Fishing Guild. The angler's outfit is needed to unlock the platform.",
        version = "1.00",
        guideLink = "https://wiki.mufasaclient.com/docs/dminnows-fisher/",
        categories = {ScriptCategory.Fishing}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings? WDH is disabled for this script, as there's users on every world mostly.",
                        defaultValue = "1",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dMinnowsFisher extends AbstractScript {
Area minnowPlatform = new Area(
        new Tile(1935, 883),
        new Tile(1965, 903)
);
RegionBox minnowRegion = new RegionBox(
        "minnows",
        5616, 2490,
        6090, 2928
);
Tile playerPos;
private Instant lastXpGainTime = Instant.now().minusSeconds(5);
private Instant lastSharkAction = Instant.now();
String previousXP = null;
String newXP;
String hopProfile;
Color FishSpotColor = Color.decode("#27ffff");
Rectangle lastLine = new Rectangle(35, 104, 361, 14);
Rectangle eastArea = new Rectangle(5816, 2658, 33, 32);
Rectangle westArea = new Rectangle(5816, 2658, 33, 32);
RegionBox fishingRegion = new RegionBox("FishingRegion", 5573, 2440, 6110, 2952);
Boolean hopEnabled;
Rectangle allButton = new Rectangle(23, 10, 47, 15);
Rectangle gameButton = new Rectangle(89, 9, 44, 13);
Rectangle privateButton = new Rectangle(153, 9, 45, 16);
Rectangle friendsButton = new Rectangle(218, 10, 42, 15);
Rectangle channelButton = new Rectangle(282, 10, 44, 12);
Rectangle clanButton = new Rectangle(346, 9, 43, 16);
Random random = new Random();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));

        Logger.log("Thank you for using the dMinnows Fisher script!");
        Logger.log("Setting up everything for your gains now...");

        if (hopEnabled) {
            Logger.debugLog("Hopping is enabled for this run!");
        } else {
            Logger.debugLog("Hopping is disabled for this run!");
        }

        // Checking if we are at the right location
        playerPos = Walker.getPlayerPosition();
        if (Player.isTileWithinArea(playerPos, minnowPlatform)) {
            Logger.debugLog("We are located at the Minnow platform, checking if we have a small fishing net...");
        } else {
            Logger.log("Could not locate us at the Minnow platform. Please move there and start the script again.");
            Logout.logout();
            Script.stop();
        }

        // Checking if we have a net in our inventory
        if(!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        if(!Inventory.contains("303", 0.90)) {
            Logger.log("No small fishing net was found in the inventory, please grab it and restart the script.");
            Logout.logout();
            Script.stop();
        } else {
            Logger.debugLog("Fishing net is present in the inventory, we're good to go!");
            GameTabs.closeInventoryTab();
        }

        // Make sure the chatbox is opened
        Logger.debugLog("Making sure the chatbox is open.");
        Random random = new Random();
        int actionNumber = random.nextInt(5);
        switch (actionNumber) {
            case 0:
                Client.tap(gameButton);
                Client.tap(allButton);
                break;
            case 1:
                Client.tap(privateButton);
                Client.tap(allButton);
                break;
            case 2:
                Client.tap(friendsButton);
                Client.tap(allButton);
                break;
            case 3:
                Client.tap(channelButton);
                Client.tap(allButton);
                break;
            case 4:
                Client.tap(clanButton);
                Client.tap(allButton);
                break;
        }

    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        if (isSharkPresent()) {
            Instant currentTime = Instant.now();
            long timeSinceLastSharkAction = Duration.between(lastSharkAction, currentTime).getSeconds();

            // Have we already responded to it? Give it time to catch some minnows first!
            if (timeSinceLastSharkAction >= 10) {
                switchSpots();
            }
        }
        readXP();

        if(hopEnabled) {
            hopActions();
        }
        readXP();

        if (shouldFishMinnows()) {
            fishMinnows();
        }
        readXP();
    }

    private void fishMinnows() {
        Polygon fishSquare = Overlay.findNearest(FishSpotColor);
        if (fishSquare != null) {
            Logger.debugLog("Switching to a new Minnow spot.");
            Client.tap(fishSquare);
            lastXpGainTime = Instant.now().plusSeconds(5);
        } else {
            Logger.debugLog("Could not locate the nearest fishing spot...");
        }
    }

    private void switchSpots() {
        Polygon fishSquare = Overlay.findSecondNearest(FishSpotColor);
        if (fishSquare != null) {
            Logger.log("Switching to a different rotating Minnow spot, as a shark was spotted.");
            Client.tap(fishSquare);
            lastXpGainTime = Instant.now().plusSeconds(8);
            lastSharkAction = Instant.now();
        } else {
            Logger.debugLog("Could not locate the nearest fishing spot...");
        }
    }

    private boolean shouldFishMinnows() {
        long timeSinceLastXpGain = Duration.between(lastXpGainTime, Instant.now()).toMillis();
        int randomOffset = random.nextInt(201); // Generates a random number between 0 and 200
        int lowerBound = 2400; // 2.4 seconds in milliseconds

        return timeSinceLastXpGain >= (lowerBound + randomOffset);
    }

    private void readXP() {
        newXP = XpBar.getXP();

        if (previousXP == null) {
            // If previousXP is null, initialize it with the current XP
            previousXP = newXP;
            lastXpGainTime = Instant.now();
        } else if (!previousXP.equals(newXP)) {
            // If previousXP and newXP are different, update lastXpGainTime and set previousXP to newXP
            lastXpGainTime = Instant.now();
            previousXP = newXP;
        }
    }

    private Boolean isSharkPresent() {
        String results = Chatbox.readLastLine(lastLine);

        // Convert the results string to lowercase for case-insensitive comparison
        String lowercaseResults = results.toLowerCase();

        // Check if any of the specified words is present in the lowercase results
        if (lowercaseResults.contains("eats") ||
                lowercaseResults.contains("jumps") ||
                lowercaseResults.contains("flying") ||
                lowercaseResults.contains("fluing")){
            return true;
        } else {
            return false;
        }
    }

    private void hopActions() {
        Game.hop(hopProfile, false, false);
    }

}