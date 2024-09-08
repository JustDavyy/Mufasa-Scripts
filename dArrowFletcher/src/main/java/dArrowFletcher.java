import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dArrowFletcher",
        description = "Fletches any type of arrow in any location in Gielinor, also supports headless arrows.",
        version = "1.2",
        guideLink = "https://wiki.mufasaclient.com/docs/darrow-fletcher/",
        categories = {ScriptCategory.Fletching}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "Which arrows would you like to fletch?",
                        defaultValue = "Broad arrows",
                        allowedValues = {
                                @AllowedValue(optionIcon = "6447", optionName = "Headless arrows"),
                                @AllowedValue(optionIcon = "897", optionName = "Bronze arrows"),
                                @AllowedValue(optionIcon = "905", optionName = "Iron arrows"),
                                @AllowedValue(optionIcon = "913", optionName = "Steel arrows"),
                                @AllowedValue(optionIcon = "921", optionName = "Mithril arrows"),
                                @AllowedValue(optionIcon = "4172", optionName = "Broad arrows"),
                                @AllowedValue(optionIcon = "929", optionName = "Adamant arrows"),
                                @AllowedValue(optionIcon = "937", optionName = "Rune arrows"),
                                @AllowedValue(optionIcon = "4770", optionName = "Amethyst arrows"),
                                @AllowedValue(optionIcon = "11216", optionName = "Dragon arrows")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dArrowFletcher extends AbstractScript {
    // Creating the strings for later use
    String product;
    String hopProfile;
    Boolean hopEnabled;
    Boolean useWDH;
    String feathers = "314";
    String headlessArrows = "6447";
    String arrowType;
    String singleArrow;
    String doubleArrow;
    String tripleArrow;
    String quadArrow;
    String stackArrow;
    String processingItem;
    private Map<String, String[]> itemIDs;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        Logger.log("Thank you for using the dArrowFletcher script!\nSetting up everything for your gains now...");

        hopActions();
        initializeItemIDs();
        setupItemIds();
        initialSetup();
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        checkInventOpen();

        if(java.util.Objects.equals(product, "Headless arrows")) {
            executeCraftHeadlessArrows();
        } else {
            executeCraftArrows();
        }

        hopActions();

    }

    private void initializeItemIDs() {
        Logger.debugLog("Running the initializeItemIDs() method.");

        itemIDs = new HashMap<>();

        // Map of itemIDs for single arrow (1), two arrows (2), three arrows (3), four arrows (4) and a full stack of arrows (5)
        itemIDs.put("Headless arrows", new String[] {"53", "6444", "6445", "6446", "6447", "6443"});
        itemIDs.put("Bronze arrows", new String[] {"882", "896", "895", "894", "897", "39"});
        itemIDs.put("Iron arrows", new String[] {"884", "904", "903", "902", "905", "40"});
        itemIDs.put("Steel arrows", new String[] {"886", "908", "909", "910", "913", "41"});
        itemIDs.put("Mithril arrows", new String[] {"888", "916", "917", "918", "921", "42"});
        itemIDs.put("Broad arrows", new String[] {"4150", "4175", "4174", "4173", "4172", "4455"});
        itemIDs.put("Adamant arrows", new String[] {"890", "928", "927", "926", "929", "43"});
        itemIDs.put("Rune arrows", new String[] {"892", "936", "935", "934", "937", "44"});
        itemIDs.put("Amethyst arrows", new String[] {"21326", "4771", "4772", "4769", "4770", "21350"});
        itemIDs.put("Dragon arrows", new String[] {"11212", "11213", "11214", "11215", "11216", "11237"});

        Logger.debugLog("Ending the initializeItemIDs() method.");
    }

    private void setupItemIds() {
        Logger.debugLog("Running the setupItemIds() method.");
        if (arrowType == null) {
            String[] itemIds = itemIDs.get(product);
            singleArrow = itemIds[0];
            doubleArrow = itemIds[1];
            tripleArrow = itemIds[2];
            quadArrow = itemIds[3];
            stackArrow = itemIds[4];
            processingItem = itemIds[5];

            Logger.debugLog("Stored IDs for " + product + ":\nSingle arrow: " + singleArrow + "\nDouble arrow: " + doubleArrow + "\nTriple arrow: " + tripleArrow + "\nQuadruple arrow: " + quadArrow + "\nStack of arrows: " + stackArrow);
        }

        Logger.debugLog("Ending the setupItemIds() method.");
    }

    private void initialSetup() {
        Logger.debugLog("Starting initialSetup() method.");

        // Open inventory if it is not open yet
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        // Check if we have the items needed

        if(java.util.Objects.equals(product, "Headless arrows")) {
            if(!Inventory.contains(feathers, 0.60)) {
                Logger.log("Feathers were not found in the inventory. Stopping script!");
                Logout.logout();
                Script.stop();
            }

            if(!Inventory.contains(processingItem, 0.60)) {
                Logger.log("A stack of arrow shafts were not found in the inventory. Stopping script!");
                Logout.logout();
                Script.stop();
            }
        } else {

            if(!Inventory.contains(processingItem, 0.60)) {
                Logger.log("Arrowtips were not found in the inventory. Stopping script!");
                Logout.logout();
                Script.stop();
            }

            if(!Inventory.contains(headlessArrows, 0.60)) {
                Logger.log("A stack of headless arrows were not found in the inventory. Stopping script!");
                Logout.logout();
                Script.stop();
            }

        }

        Logger.debugLog("Ending the initialSetup() method.");
    }

    private void executeCraftArrows() {
        Logger.debugLog("Starting executeCraftArrows() method.");

        // Starting to process items
        Inventory.tapItem(headlessArrows, 0.50);
        int randomDelay2 = new Random().nextInt(150) + 100;
        int randomDelay3 = new Random().nextInt(500) + 500;
        Condition.sleep(randomDelay2);
        Inventory.tapItem(processingItem, 0.75);
        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 150, 30);
        Chatbox.makeOption(1);
        Logger.debugLog("Selected option 1 in chatbox.");

        // Build ints
        int int1 = Integer.parseInt(processingItem);
        int int2 = Integer.parseInt(headlessArrows);
        int [] processingIDS = {int1, int2};

        // Wait for the inventory to finish (with a timeout)
        long startTime = System.currentTimeMillis();
        long timeout = 10 * 1000; // 10 seconds in milliseconds as a full invent is about 12 seconds.
        while (Inventory.containsAll(processingIDS, 0.75)) {
            Condition.sleep(randomDelay3);
            hopActions();
            readXP();

            // Check if we have passed the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                Logger.debugLog("Timeout reached for inventory.contains() method");
                break;
            }
        }
        readXP();

        Logger.debugLog("Ending the executeCraftArrows() method.");
    }

    private void executeCraftHeadlessArrows() {
        Logger.debugLog("Starting executeCraftHeadlessArrows() method.");

        // Starting to process items
        Inventory.tapItem(feathers, 0.75);
        int randomDelay2 = new Random().nextInt(250) + 100;
        int randomDelay3 = new Random().nextInt(500) + 500;
        Condition.sleep(randomDelay2);
        Inventory.tapItem(processingItem, 0.50);
        Logger.debugLog("Waiting for the chatbox Make Menu to be visible...");
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 150, 30);
        Chatbox.makeOption(1);
        Logger.debugLog("Selected option 1 in chatbox.");

        // Build ints
        int int1 = Integer.parseInt(processingItem);
        int int2 = Integer.parseInt(feathers);
        int [] processingIDS = {int1, int2};

        // Wait for the inventory to finish (with a timeout)
        long startTime = System.currentTimeMillis();
        long timeout = 10 * 1000; // 10 seconds in milliseconds as a full invent is about 12 seconds.
        while (Inventory.containsAll(processingIDS, 0.75)) {
            Condition.sleep(randomDelay3);
            hopActions();
            readXP();

            // Check if we have passed the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                Logger.debugLog("Timeout reached for inventory.contains() method");
                break;
            }
        }
        readXP();

        Logger.debugLog("Ending the executeCraftHeadlessArrows() method.");
    }

    private void checkInventOpen() {
        // Check if the inventory is open (needs this check after a break)
        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
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