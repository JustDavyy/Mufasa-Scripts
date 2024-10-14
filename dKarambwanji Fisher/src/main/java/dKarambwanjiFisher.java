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
        name = "dKarambwanji Fisher",
        description = "Fishes Karambwanji at Karamja to use as bait for Karambwans. Has a safe option for low HP accounts (keep in mind, this slows down the catch rate).",
        version = "1.07",
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
        new Tile(11109, 11740, 0),
        new Tile(11279, 11875, 0)
);
String FishingSpot;
Boolean SafeModeOn;
private Instant lastXpGainTime = Instant.now().minusSeconds(15);
int previousXP;
int newXP;
int karambwanjiStartCount = 0;
int karambwanjiGainedCount = 0;
Tile SouthSpot = new Tile(11203, 11785, 0);
Tile NorthEastSpot = new Tile(11231, 11833, 0);
Tile NorthWestSpot = new Tile(11163, 11829, 0);
Tile EastSpot = new Tile(11243, 11817, 0);

Tile[] fishingSpots = new Tile[] {NorthEastSpot, EastSpot, NorthWestSpot, SouthSpot};
private Instant lastActionTime = Instant.now();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"43-47"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        Map<String, String> configs = getConfigurations();
        SafeModeOn = Boolean.valueOf((configs.get("Use safe mode?")));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        Logger.log("Thank you for using the dKarambwanji Fisher script!");
        Logger.log("Setting up everything for your gains now...");

        // Checking if we are at the right location
        if (Player.within(FishingArea)) {
            Logger.debugLog("We are located at the Karambwanji fishing area, checking if we have a small fishing net...");
        } else {
            Logger.log("Could not locate us in the Karambwanji fishing area. Please move there and start the script again.");
            Logger.log("Location: " + Walker.getPlayerPosition().toString());
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

        // Gather start amount of karambwanji
        if (Inventory.contains(ItemList.RAW_KARAMBWANJI_3150, 0.7)) {
            karambwanjiStartCount = Inventory.stackSize(ItemList.RAW_KARAMBWANJI_3150);
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

        // Read the gained karambwanji count
        Logger.log("Karambwanji's gained: " + (Inventory.stackSize(ItemList.RAW_KARAMBWANJI_3150) - karambwanjiStartCount));
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

        if (previousXP == 0 || previousXP == -1) {
            // If previousXP is null, initialize it with the current XP
            previousXP = newXP;
        } else if (previousXP != newXP) {
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

        Game.antiAFK();
        GameTabs.openInventoryTab();

        // Reset the last action time
        lastActionTime = Instant.now();
    }

    private void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
            GameTabs.openInventoryTab();
        } else {
            // We do nothing here, as hop is disabled.
        }
    }
}