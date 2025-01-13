package main;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.ResourceMapping;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dMoltenMaestro",
        description = "Creates jewellery, bars, molten glass and cannonballs using furnaces at different locations",
        version = "1.02",
        guideLink = "https://wiki.mufasaclient.com/docs/dmoltenmaestro/",
        categories = {ScriptCategory.Smithing, ScriptCategory.Crafting, ScriptCategory.Ironman},
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
                                @AllowedValue(optionName = "Neitiznot"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Resource",
                        description = "Which resource would you like to process?",
                        defaultValue = "Molten glass",
                        allowedValues = {
                                @AllowedValue(optionIcon = "2349", optionName = "Bronze bar"),
                                @AllowedValue(optionIcon = "2351", optionName = "Iron bar"),
                                @AllowedValue(optionIcon = "2355", optionName = "Silver bar"),
                                @AllowedValue(optionIcon = "2353", optionName = "Steel bar"),
                                @AllowedValue(optionIcon = "2357", optionName = "Gold bar"),
                                @AllowedValue(optionIcon = "2359", optionName = "Mithril bar"),
                                @AllowedValue(optionIcon = "2361", optionName = "Adamantite bar"),
                                @AllowedValue(optionIcon = "2363", optionName = "Runite bar"),
                                @AllowedValue(optionIcon = "1609", optionName = "Opal"),
                                @AllowedValue(optionIcon = "2357", optionName = "Gold"),
                                @AllowedValue(optionIcon = "1611", optionName = "Jade"),
                                @AllowedValue(optionIcon = "1613", optionName = "Topaz"),
                                @AllowedValue(optionIcon = "1607", optionName = "Sapphire"),
                                @AllowedValue(optionIcon = "1605", optionName = "Emerald"),
                                @AllowedValue(optionIcon = "1603", optionName = "Ruby"),
                                @AllowedValue(optionIcon = "1601", optionName = "Diamond"),
                                @AllowedValue(optionIcon = "1615", optionName = "Dragonstone"),
                                @AllowedValue(optionIcon = "6573", optionName = "Onyx"),
                                @AllowedValue(optionIcon = "4155", optionName = "Slayer"),
                                @AllowedValue(optionIcon = "19493", optionName = "Zenyte"),
                                @AllowedValue(optionIcon = "1775", optionName = "Molten glass"),
                                @AllowedValue(optionIcon = "2", optionName = "Cannonball"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Product type",
                        description = "Which product type would you like to create?",
                        defaultValue = "None",
                        allowedValues = {
                                @AllowedValue(optionIcon = "1645", optionName = "Ring"),
                                @AllowedValue(optionIcon = "1664", optionName = "Necklace"),
                                @AllowedValue(optionIcon = "11115", optionName = "Bracelet"),
                                @AllowedValue(optionIcon = "1702", optionName = "Amulet"),
                                @AllowedValue(optionIcon = "26788", optionName = "Tiara"),
                                @AllowedValue(optionName = "None"),
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

public class dMoltenMaestro extends AbstractScript {
    private static Random random = new Random();
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    public static String resource;
    public static String productType;
    public static String location;
    public static int resourceItemID1 = -1;
    public static int resourceItemID2 = -1;
    public static int resultItemID;
    public static int banktab;
    public static int productIndex;
    public static int processCount = 0;
    public static Boolean runEnabled;
    public static boolean setupDone = false;
    public static long lastProcessTime = System.currentTimeMillis();
    public static int retrycount = 0;
    public static long startTime;
    public static int bankItem1Count = 0;
    public static int bankItem2Count = 0;
    public static int previousBankItem1Count = 0;
    public static int previousBankItem2Count = 0;
    public static int delayInMS = 0;
    public static int delayInS = 0;

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
            new Smelt(),
            new Recover()
    );

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        location = configs.get("Location");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = configs.get("Use world hopper?");
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        resource = configs.get("Resource");
        productType = configs.get("Product type");

        MapChunk chunks = null;
        switch (location) {
            case "Edgeville":
                chunks = new MapChunk(new String[]{"48-54"}, "0");
                break;
            case "Mount Karuulm":
                chunks = new MapChunk(new String[]{"20-59"}, "0");
                break;
            case "Neitiznot":
                chunks = new MapChunk(new String[]{"36-59"}, "0");
                break;
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
                break;
        }

        Logger.debugLog("Setup walker.");
        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Set up all our item maps
        Logger.debugLog("Setup item maps");
        Paint.setStatus("Setup item maps");
        setupItemMaps();

        Logger.debugLog("Create paint box");
        Paint.setStatus("Creating paint box");
        // Create a single image box, to show the amount of processed bows
        createPaintBox();

        Logger.debugLog("Initialize hop timer (if enabled)");
        // Initialize hop timer for this run if hopping is enabled
        hopActions();

        Logger.debugLog("Close chatbox");
        // Close chatbox
        Paint.setStatus("Closing chatbox");
        Chatbox.closeChatbox();

        //Logs for debugging purposes
        Logger.log("Thank you for using the dMoltenMaestro script!");
        Logger.log("Setting up everything for your gains now...");

        runEnabled = Player.isRunEnabled();

        startTime = System.currentTimeMillis();
    }

    @Override
    public void poll() {
        // Check if it's time to hop
        hopActions();

        // Read XP
        readXP();

        // Open inventory tab
        GameTabs.openTab(UITabs.INVENTORY);

        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Updated current location: " + currentLocation.toString());

        // Run tasks
        for (Task task : tasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    private static void setupItemMaps() {
        switch (resource) {
            case "Bronze bar":
            case "Iron bar":
            case "Steel bar":
            case "Mithril bar":
            case "Adamantite bar":
            case "Runite bar":
                ResourceMapping mappingBars = ResourceMapping.fromString(resource);

                if (mappingBars != null) {
                    resourceItemID1 = mappingBars.getResourceItemID1();
                    resourceItemID2 = mappingBars.getResourceItemID2();
                    resultItemID = mappingBars.getResultItemID();
                } else {
                    Logger.log("Invalid resource (not in enum): " + resource);
                }
                break;
            case "Silver bar":
            case "Gold bar":
                if (!productType.equals("None")) {
                    String resourceString = resource.replace(" bar", "") + " " + productType;
                    ResourceMapping mappingDual = ResourceMapping.fromString(resourceString);

                    if (mappingDual != null) {
                        resourceItemID1 = mappingDual.getResourceItemID1();
                        resourceItemID2 = mappingDual.getResourceItemID2();
                        resultItemID = mappingDual.getResultItemID();
                    } else {
                        Logger.debugLog("Invalid resource (not in enum): " + resourceString);
                    }
                } else {
                    ResourceMapping mappingBarsDual = ResourceMapping.fromString(resource);

                    if (mappingBarsDual != null) {
                        resourceItemID1 = mappingBarsDual.getResourceItemID1();
                        resourceItemID2 = mappingBarsDual.getResourceItemID2();
                        resultItemID = mappingBarsDual.getResultItemID();
                    } else {
                        Logger.log("Invalid resource (not in enum): " + resource);
                    }
                }
                break;
            case "Opal":
            case "Gold":
            case "Jade":
            case "Topaz":
            case "Sapphire":
            case "Emerald":
            case "Ruby":
            case "Diamond":
            case "Dragonstone":
            case "Onyx":
            case "Zenyte":
                String resourceString = resource + " " + productType;
                ResourceMapping mappingGems = ResourceMapping.fromString(resourceString);

                if (mappingGems != null) {
                    resourceItemID1 = mappingGems.getResourceItemID1();
                    resourceItemID2 = mappingGems.getResourceItemID2();
                    resultItemID = mappingGems.getResultItemID();
                } else {
                    Logger.debugLog("Invalid resource (not in enum): " + resourceString);
                }
                break;
            case "Slayer":
                resourceItemID1 = ItemList.ENCHANTED_GEM_4155;
                resourceItemID2 = ItemList.GOLD_BAR_2357;
                resultItemID = ItemList.SLAYER_RING_8_11866;
                break;
            case "Molten glass":
                resourceItemID1 = ItemList.SODA_ASH_1781;
                resourceItemID2 = ItemList.BUCKET_OF_SAND_1783;
                resultItemID = ItemList.MOLTEN_GLASS_1775;
                break;
            case "Cannonball":
                resourceItemID1 = ItemList.STEEL_BAR_2353;
                resultItemID = ItemList.CANNONBALL_2;
            default:
                Logger.debugLog("Invalid resource (not in switch logic): " + resource);
                break;
        }

        Paint.setStatus("Set up delays");
        if (resource.equals("Cannonball")) {
            delayInS = 10;
            delayInMS = 10000;
        } else {
            delayInS = 7;
            delayInMS = 7000;
        }
    }

    private static void createPaintBox() {
        switch (resource) {
            case "Bronze bar":
            case "Iron bar":
            case "Steel bar":
            case "Mithril bar":
            case "Adamantite bar":
            case "Runite bar":
                ResourceMapping mappingBars = ResourceMapping.fromString(resource);
                if (mappingBars != null) {
                    resultItemID = mappingBars.getResultItemID();
                } else {
                    Logger.debugLog("Invalid resource (not in enum): " + resource);
                }
                productIndex = Paint.createBox(resource, resultItemID, 0);
                break;
            case "Silver bar":
            case "Gold bar":
                if (!productType.equals("None")) {
                    String resourceString = resource.replace(" bar", "") + " " + productType;
                    ResourceMapping mappingDual = ResourceMapping.fromString(resourceString);
                    productIndex = Paint.createBox(resourceString, resultItemID, 0);

                    if (mappingDual != null) {
                        resourceItemID1 = mappingDual.getResourceItemID1();
                        resourceItemID2 = mappingDual.getResourceItemID2();
                        resultItemID = mappingDual.getResultItemID();
                    } else {
                        Logger.debugLog("Invalid resource (not in enum): " + resourceString);
                    }
                } else {
                    ResourceMapping mappingBarsDual = ResourceMapping.fromString(resource);
                    productIndex = Paint.createBox(resource, resultItemID, 0);

                    if (mappingBarsDual != null) {
                        resourceItemID1 = mappingBarsDual.getResourceItemID1();
                        resourceItemID2 = mappingBarsDual.getResourceItemID2();
                        resultItemID = mappingBarsDual.getResultItemID();
                    } else {
                        Logger.log("Invalid resource (not in enum): " + resource);
                    }
                }
                break;
            case "Opal":
            case "Gold":
            case "Jade":
            case "Topaz":
            case "Sapphire":
            case "Emerald":
            case "Ruby":
            case "Diamond":
            case "Dragonstone":
            case "Onyx":
            case "Zenyte":
                String resourceString = resource + " " + productType;
                ResourceMapping mappingGems = ResourceMapping.fromString(resourceString);

                if (mappingGems != null) {
                    resultItemID = mappingGems.getResultItemID();
                } else {
                    Logger.debugLog("Invalid resource (not in enum): " + resourceString);
                }
                productIndex = Paint.createBox(resourceString, resultItemID, 0);
                break;
            case "Slayer":
                resourceItemID1 = ItemList.ENCHANTED_GEM_4155;
                resourceItemID2 = ItemList.GOLD_BAR_2357;
                resultItemID = ItemList.SLAYER_RING_8_11866;
                productIndex = Paint.createBox("Slayer ring (8)", resultItemID, 0);
                break;
            case "Molten glass":
                resourceItemID1 = ItemList.BUCKET_OF_SAND_1783;
                resourceItemID2 = ItemList.SODA_ASH_1781;
                resultItemID = ItemList.MOLTEN_GLASS_1775;
                productIndex = Paint.createBox("Molten glass", resultItemID, 0);
                break;
            case "Cannonball":
                resourceItemID1 = ItemList.STEEL_BAR_2353;
                resultItemID = ItemList.CANNONBALL_2;
                productIndex = Paint.createBox("Cannonball", resultItemID, 0);
            default:
                Logger.debugLog("Invalid resource (not in switch logic): " + resource);
                break;
        }
    }

    public static boolean checkInventColor(int itemId) {
        switch (itemId) {
            case ItemList.COAL_453:
                return Inventory.contains(itemId, 0.8, Color.decode("#2d2d1c"));
            case ItemList.TIN_ORE_438:
                return Inventory.contains(itemId, 0.8, Color.decode("#7b7170"));
            case ItemList.COPPER_ORE_436:
                return Inventory.contains(itemId, 0.8, Color.decode("#d56e29"));
            case ItemList.IRON_ORE_440:
                return Inventory.contains(itemId, 0.8, Color.decode("#402218"));
            case ItemList.MITHRIL_ORE_447:
                return Inventory.contains(itemId, 0.8, Color.decode("#3c3c60"));
            case ItemList.ADAMANTITE_ORE_449:
                return Inventory.contains(itemId, 0.8, Color.decode("#3d513e"));
            case ItemList.RUNITE_ORE_451:
                return Inventory.contains(itemId, 0.8, Color.decode("#415c68"));
            default:
                return Inventory.contains(itemId, 0.8);
        }
    }

    public static void updatePreviousBankItemCount(int updateItem) {
        if (updateItem == 1) {
            if (previousBankItem1Count != -1 & bankItem1Count > 0) {
                previousBankItem1Count = bankItem1Count;
            }
        } else if (updateItem == 2) {
            if (previousBankItem2Count != -1 & bankItem2Count > 0) {
                previousBankItem2Count = bankItem2Count;
            }
        }
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