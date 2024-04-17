import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import helpers.utils.OverlayColor;
import helpers.utils.Tile;
import helpers.utils.Area;
import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.time.Instant;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dKarambwanji Fisher",
        description = "Fishes Karambwanji at Karamja to use as bait for Karambwans. Has a safe option for low HP accounts (keep in mind, this slows down the catch rate).",
        version = "1.04",
        guideLink = "https://wiki.mufasaclient.com/docs/dkarambwanji-fisher/",
        categories = {ScriptCategory.Fishing}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use safe mode?",
                        description = "Would you like to enable to low HP level safe mode? (this results in less karambwanjis an hour). This only uses the N/NE/E spots, and ignores the S and other spots.",
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

public class dKarambwanjiFisher extends AbstractScript {
String hopProfile;
Boolean hopEnabled;
Boolean useWDH;
Area FishingArea = new Area(
        new Tile(1, 0),
        new Tile(97, 77)
);
String FishingSpot;
Boolean SafeModeOn;
private Instant lastXpGainTime = Instant.now().minusSeconds(15);
String previousXP = null;
String newXP;
Tile SouthSpot = new Tile(55, 49);
Tile NorthEastSpot = new Tile(63, 32);
Tile NorthWestSpot = new Tile(42, 34);
Tile EastSpot = new Tile(69, 38);
Tile[] fishingSpots = new Tile[] {NorthEastSpot, EastSpot, NorthWestSpot, SouthSpot};
private Instant lastActionTime = Instant.now();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Walker.setup("/maps/KarambwanjiArea.png"); //Set up the walker!

        Map<String, String> configs = getConfigurations();
        SafeModeOn = Boolean.valueOf((configs.get("Use safe mode?")));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        Logger.log("Thank you for using the dKarambwanji Fisher script!");
        Logger.log("Setting up everything for your gains now...");

        // Checking if we are at the right location
        Tile playerPos = Walker.getPlayerPosition();
        if (Player.isTileWithinArea(playerPos, FishingArea)) {
            Logger.debugLog("We are located at the Karambwanji fishing area, checking if we have a small fishing net...");
        } else {
            Logger.log("Could not locate us in the Karambwanji fishing area. Please move there and start the script again.");
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
            hopActions();
        }

    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        if (shouldFishKarambwanjis()) {
            FishKarambwanjis();
        }

        readXP();
        hopActions();

        // Check for inactivity
        if (Duration.between(lastActionTime, Instant.now()).toMinutes() >= 4) {
            performAntiAFKAction();
            hopActions();
        }
    }

    private void FishKarambwanjis() {
        Polygon fishSquare = Overlay.findNearest(OverlayColor.FISHING);
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

        Tile selectedSpot = fishingSpots[index];
        if (index == 0){
            FishingSpot = "North-East";
        } else if (index == 1) {
            FishingSpot = "East";
        } else if (index == 2) {
            FishingSpot = "North-West";
        } else if (index == 3) {
            FishingSpot = "South";
        }
        // Log the selected spot
        Logger.debugLog("Moving to a new fishing spot: " + FishingSpot);

        Walker.step(selectedSpot);
        lastActionTime = Instant.now();
        Condition.wait(() -> Player.atTile(selectedSpot), 500, 35);
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

    private void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        } else {
            // We do nothing here, as hop is disabled.
        }
    }
}