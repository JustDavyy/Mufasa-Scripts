package main;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dCannonball Smelter",
        description = "Smelts steel bars into cannonballs at various locations. Supports hopping worlds.",
        version = "2.01",
        guideLink = "https://wiki.mufasaclient.com/docs/dcannonball-smelter/",
        categories = {ScriptCategory.Smithing, ScriptCategory.Moneymaking, ScriptCategory.Ironman},
        skipZoomSetup = true
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
                                @AllowedValue(optionName = "Neitiznot")
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
    private static Random random = new Random();
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    public static String location;
    public static String mouldUsed = "Nothing";
    public static int banktab;
    public static int productIndex;
    public static int startCount = 0;
    public static Boolean runEnabled;
    public static boolean setupDone = false;
    public static long lastProcessTime = System.currentTimeMillis();
    private long lastLocationUpdateTime = System.currentTimeMillis();
    public static int retrycount = 0;

    // Tiles
    public static Tile edgeBankTile = new Tile(12383, 13725, 0);
    public static Tile edgeFurnaceTile = new Tile(12435, 13745, 0);
    public static Tile karuulmFurnaceTile = new Tile(5295, 14985, 0);
    public static Tile karuulmBankTile = new Tile(5295, 15045, 0);
    public static Tile neitFurnaceTile = new Tile(9375, 14989, 0);
    public static Tile neitBankTile = new Tile(9347, 14981, 0);
    public static Tile currentLocation;

    // Furnace Rectangles (always calculated from the bank tile)
    public static Rectangle edgeFurnaceRect = new Rectangle(665, 188, 9, 11);
    public static Rectangle neitFurnaceRect = new Rectangle(632, 184, 9, 17);

    // Furnace Rectangles when at the furnace already
    public static Rectangle karuulmFurnaceRectAtFurnace = new Rectangle(494, 284, 90, 101);
    public static Rectangle neitFurnaceRectAtFurnace = new Rectangle(444, 232, 12, 19);
    public static Rectangle edgeFurnaceRectAtFurnace = new Rectangle(466, 265, 5, 12);

    // Bank Rectangles (always calculated from the furnace tile)
    public static Rectangle edgeBankRect = new Rectangle(178, 371, 14, 9);
    public static Rectangle karuulmBankRect = new Rectangle(440, 219, 25, 26);
    public static Rectangle neitBankRect = new Rectangle(264, 313, 16, 20);

    // Rectangles to open the bank when on the tile (once during startup)
    public static Rectangle openEdgeBankONCE = new Rectangle(443, 276, 12, 12);
    public static Rectangle openNeitBankONCE = new Rectangle(472, 256, 15, 22);
    // These tasks are executed in this order
    List<Task> tasks = Arrays.asList(
            new Setup(),
            new Bank(),
            new Smelt()
    );

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        location = configs.get("Location");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        // Create the MapChunk with chunks of our location
        MapChunk chunks = new MapChunk(new String[]{"48-54", "20-59", "20-60", "36-59"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        Paint.setStatus("Creating paint box");
        // Create a single image box, to show the amount of processed bows
        productIndex = Paint.createBox("Cannonballs", ItemList.CANNONBALL_2, 0);

        // Initialize hop timer for this run if hopping is enabled
        hopActions();

        // Close chatbox
        Paint.setStatus("Closing chatbox");
        Chatbox.closeChatbox();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dCannonball Smelter script!");
        Logger.log("Setting up everything for your gains now...");

        if (Player.isRunEnabled()) {
            runEnabled = true;
        } else {
            runEnabled = false;
        }
    }

    @Override
    public void poll() {
        // Check if it's time to hop
        hopActions();

        // Read XP
        readXP();

        // Open inventory tab
        GameTabs.openTab(UITabs.INVENTORY);

        // Update currentLocation every 10 seconds
        if (System.currentTimeMillis() - lastLocationUpdateTime >= 10000) { // 10 seconds in milliseconds
            currentLocation = Walker.getPlayerPosition();
            lastLocationUpdateTime = System.currentTimeMillis();
            Logger.debugLog("Updated current location: " + currentLocation.toString());
        }

        // Run tasks
        for (Task task : tasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    public static void updatePaintBar() {
        Paint.setStatus("Update paint count");
        Paint.updateBox(productIndex, Inventory.stackSize(ItemList.CANNONBALL_2) - startCount);
    }

    public static int generateDelay(int lowerEnd, int higherEnd) {
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