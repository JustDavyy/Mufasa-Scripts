import helpers.*;
import helpers.utils.OptionType;

import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.time.Instant;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dKarambwanji Fisher",
        description = "Fishes Karambwanji at Karamja to use as bait for Karambwans. Has a safe option for low HP accounts (keep in mind, this slows down the catch rate).",
        version = "1.02",
        category = ScriptCategory.Fishing
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use safe mode?",
                        description = "Would you like to enable to low HP level safe mode? (this results in less karambwanjis an hour). This only uses the N/NE/E spots, and ignores the S and other spots.",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class dKarambwanjiFisher extends AbstractScript {
Rectangle FishingArea = new Rectangle(75, 60, 147, 119);
Boolean SafeModeOn;
Point playerPos;
private Instant lastXpGainTime = Instant.now().minusSeconds(15);
String previousXP = null;
String newXP;
Color FishSpotColor = Color.decode("#27ffff");
Point SouthSpot = new Point(195, 140);
Point NorthEastSpot = new Point(191, 100);
Point NorthWestSpot = new Point(127, 108);
Point EastSpot = new Point(207, 116);
String mapString = "maps/KarambwanjiArea.png";
Point[] fishingSpots = new Point[] {NorthEastSpot, EastSpot, NorthWestSpot, SouthSpot};
private Instant lastActionTime = Instant.now();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        SafeModeOn = Boolean.valueOf((configs.get("Use safe mode?")));

        Logger.log("Thank you for using the dKarambwanji Fisher script!");
        Logger.log("Setting up everything for your gains now...");

        // Checking if we are at the right location
        Point playerPos = Walker.getPlayerPosition(mapString);
        if (FishingArea.contains(playerPos)) {
            Logger.debugLog("We are located at the Karambwanji fishing area, checking if we have a small fishing net...");
        } else {
            Logger.log("Could not locate us in the Karambwanji fishing area. Please move there and start the script again.");
            Logout.logout();
            Script.forceStop();
        }

        // Checking if we have a net in our inventory
        if(!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        if(!Inventory.contains("303", 0.90)) {
            Logger.log("No small fishing net was found in the inventory, please grab it and restart the script.");
            Logout.logout();
            Script.forceStop();
        } else {
            Logger.debugLog("Fishing net is present in the inventory, we're good to go!");
            GameTabs.closeInventoryTab();
        }

    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        if (shouldFishKarambwanjis()) {
            FishKarambwanjis();
        }

        readXP();

        // Check for inactivity
        if (Duration.between(lastActionTime, Instant.now()).toMinutes() >= 4) {
            performAntiAFKAction();
        }
    }

    private void FishKarambwanjis() {
        Polygon fishSquare = Overlay.findNearest(FishSpotColor);
        if (fishSquare != null) {
            Client.tap(fishSquare);
            lastXpGainTime = Instant.now();
            lastActionTime = Instant.now();
        } else {
            Logger.debugLog("Could not locate the nearest fishing spot, moving around to find different spots.");
            findNewSpot();
        }
    }

    private boolean shouldFishKarambwanjis() {
        long timeSinceLastXpGain = Duration.between(lastXpGainTime, Instant.now()).getSeconds();
        return timeSinceLastXpGain >= 15;
    }

    private void readXP() {
        newXP = XpBar.getXP();

        if (previousXP == null) {
            // If previousXP is null, initialize it with the current XP
            previousXP = newXP;
        } else if (!previousXP.equals(newXP)) {
            // If previousXP and newXP are different, update lastXpGainTime and set previousXP to newXP
            lastXpGainTime = Instant.now();
            previousXP = newXP;
        }
    }

    private void findNewSpot() {
        // Generate a random index to choose a spot
        Random random = new Random();
        int index;
        if (SafeModeOn) {
            // Exclude the South spot if safe mode is on
            index = random.nextInt(fishingSpots.length - 1);
        } else {
            // Include all spots if safe mode is off
            index = random.nextInt(fishingSpots.length);
        }

        Point selectedSpot = fishingSpots[index];
        // Log the selected spot
        Logger.debugLog("Moving to a new fishing spot: " + selectedSpot);

        Walker.stepCustomMap(selectedSpot, mapString);
        lastActionTime = Instant.now();
        Condition.wait(() -> Walker.getPlayerPosition(mapString).equals(selectedSpot), 500, 35);
    }

    private void performAntiAFKAction() {
        Random random = new Random();
        int actionNumber = random.nextInt(5);

        switch (actionNumber) {
            case 0:
                Logger.debugLog("Performing Anti-AFK action 1");
                GameTabs.openStatsTab();
                GameTabs.closeStatsTab();
                break;
            case 1:
                Logger.debugLog("Performing Anti-AFK action 2");
                GameTabs.openSettingsTab();
                GameTabs.closeSettingsTab();
                break;
            case 2:
                Logger.debugLog("Performing Anti-AFK action 3");
                GameTabs.openEquipTab();
                GameTabs.closeEquipTab();
                break;
            case 3:
                Logger.debugLog("Performing Anti-AFK action 4");
                GameTabs.openFriendsTab();
                GameTabs.closeFriendsTab();
                break;
            case 4:
                Logger.debugLog("Performing Anti-AFK action 5");
                GameTabs.openInventoryTab();
                GameTabs.closeInventoryTab();
                break;
        }

        // Reset the last action time
        lastActionTime = Instant.now();
    }
}