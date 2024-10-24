import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;

import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.time.Instant;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dMinnows Fisher",
        description = "Fishes Minnows at Kylie Minnow's fishing platform at the Fishing Guild. The angler's outfit is needed to unlock the platform.",
        version = "1.06",
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
        new Tile(10421, 13497, 0),
        new Tile(10504, 13544, 0)
);
private Instant lastXpGainTime = Instant.now().minusSeconds(5);
private Instant lastSharkAction = Instant.now();
int previousXP;
int newXP;
String hopProfile;
Color FishSpotColor = Color.decode("#27ffff");
Rectangle lastLine = new Rectangle(35, 104, 361, 14);
Boolean hopEnabled;
private long lastRunTime = System.currentTimeMillis();
Random random = new Random();

// Check rectangles if we have a spot against us
Rectangle rightRect = new Rectangle(465, 267, 57, 22);
Rectangle leftRect = new Rectangle(379, 273, 56, 21);
Rectangle topRect = new Rectangle(443, 219, 23, 42);
Rectangle bottomRect = new Rectangle(441, 303, 24, 43);

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

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"40-53"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Checking if we are at the right location
        if (Player.within(minnowPlatform)) {
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
        Chatbox.openAllChat();
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
            // Calculate the approximate center of the polygon
            Rectangle bounds = fishSquare.getBounds();
            int centerX = bounds.x + bounds.width / 2;
            int centerY = bounds.y + bounds.height / 2;

            // Add some random offset to make it less predictable
            int offsetX = (int) (Math.random() * 4) + 1; // Random value between 1 and 4
            int offsetY = (int) (Math.random() * 4) + 1; // Random value between 1 and 4

            // Randomly decide whether to add or subtract the offset
            centerX += Math.random() < 0.5 ? -offsetX : offsetX;
            centerY += Math.random() < 0.5 ? -offsetY : offsetY;

            Logger.debugLog("Switching to a new Minnow spot.");
            Client.tap(new Point(centerX, centerY)); // Tap around the randomized center
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

        // Check if we should fish based on time since last XP gain, or if the spot is no longer available
        return timeSinceLastXpGain >= (lowerBound + randomOffset) || !isSpotAgainstUs(true);
    }

    private void readXP() {
        newXP = XpBar.getXP();

        if (previousXP == 0 || previousXP == -1) {
            // If previousXP is null, initialize it with the current XP
            previousXP = newXP;
            lastXpGainTime = Instant.now();
        } else if (previousXP != newXP) {
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

    private boolean isSpotAgainstUs(boolean log) {
        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Check if 5 seconds have passed since the last run
        if (currentTime - lastRunTime < 5000) {
            // If less than 5 seconds, return true by default
            return true;
        }

        // Update the last run time to the current time
        lastRunTime = currentTime;

        // Check if the color is present in the right or bottom rectangles
        if (Client.isColorInRect(OverlayColor.FISHING, rightRect, 5) ||
                Client.isColorInRect(OverlayColor.FISHING, bottomRect, 5) ||
                Client.isColorInRect(OverlayColor.FISHING, topRect, 5) ||
                Client.isColorInRect(OverlayColor.FISHING, leftRect, 5)) {
            return true;
        }

        // Log that the spot we're using moved, since we couldn't find a spot
        if (log) {
            Logger.debugLog("Moving to a new spot as the current spot has moved.");
        }
        return false;
    }

    private void hopActions() {
        Game.hop(hopProfile, false, false);
    }

}