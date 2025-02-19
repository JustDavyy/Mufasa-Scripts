package tasks;

import static helpers.Interfaces.*;

import helpers.utils.Area;
import helpers.utils.Tile;

import main.dShopBuyer;
import utils.Task;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Buy extends Task {

    private dShopBuyer shopBuyer;
    Tile currentLoc;
    int stackSize1 = -1;
    int stackSize2 = -1;
    int stackSize3 = -1;
    int notFoundCounter = 0;

    // General
    Rectangle chatWindowCheck1Rect = new Rectangle(7, 126, 24, 27);
    Rectangle chatWindowCheck2Rect = new Rectangle(518, 130, 26, 26);

    // Blast Furnace
    Rectangle copperOreCheckRect = new Rectangle(160, 223, 22, 25);
    Rectangle goldOreCheckRect = new Rectangle(399, 218, 15, 26);
    Rectangle shopFromShopTileRect = new Rectangle(442, 248, 4, 6);
    Rectangle copperOreStackRect = new Rectangle(144, 214, 37, 19);
    Rectangle tinOreStackRect = new Rectangle(194, 213, 36, 22);
    Rectangle ironOreStackRect = new Rectangle(242, 213, 32, 21);
    Rectangle mithrilOreStackRect = new Rectangle(287, 213, 32, 18);
    Rectangle silverOreStackRect = new Rectangle(335, 213, 30, 18);
    Rectangle goldOreStackRect = new Rectangle(383, 214, 31, 17);
    Rectangle coalOreStackRect = new Rectangle(428, 212, 34, 21);
    Rectangle copperOreClickRect = new Rectangle(160, 227, 16, 15);
    Rectangle tinOreClickRect = new Rectangle(209, 227, 14, 15);
    Rectangle ironOreClickRect = new Rectangle(256, 227, 15, 14);
    Rectangle mithrilOreClickRect = new Rectangle(303, 225, 16, 16);
    Rectangle silverOreClickRect = new Rectangle(349, 227, 18, 13);
    Rectangle goldOreClickRect = new Rectangle(395, 225, 17, 17);
    Rectangle coalOreClickRect = new Rectangle(443, 226, 15, 18);
    Rectangle quantity50Rect = new Rectangle(508, 447, 21, 19);

    // Fortunato
    Area fortunatoArea = new Area(new Tile(12325, 12739, 0), new Tile(12352, 12769, 0));
    Rectangle fortunatoScanArea = new Rectangle(283, 156, 191, 279);
    public static List<Color> fortunatoColors = Arrays.asList(Color.decode("#390e09"), Color.decode("#330e09"), Color.decode("#260909"));
    Rectangle FWSOpenCheckRect1 = new Rectangle(298, 217, 22, 34);
    Rectangle FWSOpenCheckRect2 = new Rectangle(254, 223, 17, 22);
    Rectangle JugOfWineStackRect = new Rectangle(147, 214, 21, 21);
    Rectangle EmptyJugPackStackRect = new Rectangle(240, 214, 29, 19);
    Rectangle JugOfWineClickRect = new Rectangle(164, 230, 9, 13);
    Rectangle EmptyJugPackClickRect = new Rectangle(254, 225, 18, 19);

    // Khazard Charter
    public static List<Color> khazardCrewColors = Arrays.asList(Color.decode("#7a1152"), Color.decode("#6e1149"), Color.decode("#610e40"), Color.decode("#dbce49"));Rectangle khazardOpenCheckRect1 = new Rectangle(251, 312, 19, 19);
    Rectangle khazardOpenCheckRect2 = new Rectangle(487, 269, 22, 25);
    Area khazardShopArea = new Area(new Tile(10687, 12308, 0), new Tile(10713, 12339, 0));
    Area khazardScriptArea = new Area(new Tile(10633, 12307, 0), new Tile(10715, 12401, 0));
    Tile khazardRecoverTile = new Tile(10648, 12370, 0);
    Rectangle khazardCrewScanArea = new Rectangle(251, 60, 358, 447);
    Rectangle khazardEmptyBucketStackRect = new Rectangle(382, 214, 33, 19);
    Rectangle khazardPineappleStackRect = new Rectangle(476, 261, 23, 18);
    Rectangle khazardBucketofSlimeStackRect = new Rectangle(241, 307, 26, 18);
    Rectangle khazardBucketofSandStackRect = new Rectangle(336, 307, 26, 20);
    Rectangle khazardSeaweedStackRect = new Rectangle(381, 305, 31, 24);
    Rectangle khazardSodaAshStackRect = new Rectangle(429, 304, 24, 23);
    Rectangle khazardEmptyBucketClickRect = new Rectangle(396, 226, 15, 17);
    Rectangle khazardPineappleClickRect = new Rectangle(491, 277, 10, 12);
    Rectangle khazardBucketofSlimeClickRect = new Rectangle(257, 323, 11, 13);
    Rectangle khazardBucketofSandClickRect = new Rectangle(351, 323, 11, 14);
    Rectangle khazardSeaweedClickRect = new Rectangle(400, 323, 6, 11);
    Rectangle khazardSodaAshClickRect = new Rectangle(444, 324, 11, 10);


    public Buy(dShopBuyer shopBuyer) {
        this.shopBuyer = shopBuyer;
    }
    @Override
    public boolean activate() {
        return !Inventory.isFull();
    }

    @Override
    public boolean execute() {

        // Open inventory
        GameTabs.openInventoryTab();

        switch (shopBuyer.shopToUse) {
            case "Blast Furnace":
                blastFurnaceHandleBuy();
                break;
            case "Fortunato Wine Shop":
                fortunatoHandleBuy();
                break;
            case "Khazard Charter":
                // Check if we haven't wandered off for some reason
                khazardCheckWanderedOff();
                // Proceed to buy
                khazardHandleBuy();
                break;
            default:
                Logger.log("Invalid store, stopping script!");
                Logout.logout();
                Script.stop();
        }

        return false;
    }



    // Blast Furnace
    private boolean isBFShopOpen() {
        boolean check1 = Client.isColorInRect(Color.decode("#d76e29"), copperOreCheckRect, 10);
        boolean check2 = Client.isColorInRect(Color.decode("#cda110"), goldOreCheckRect, 10);

        return check1 && check2;
    }

    private boolean isFortunatoShopOpen() {
        boolean check1 = Client.isColorInRect(Color.decode("#b7450e"), FWSOpenCheckRect1, 10);
        boolean check2 = Client.isColorInRect(Color.decode("#c69a64"), FWSOpenCheckRect2, 10);

        return check1 && check2;
    }

    private boolean isKhazardShopOpen() {
        boolean check1 = Client.isColorInRect(Color.decode("#0eb112"), khazardOpenCheckRect1, 10);
        boolean check2 = Client.isColorInRect(Color.decode("#c48b0e"), khazardOpenCheckRect2, 10);

        return check1 && check2;
    }

    private boolean isChatWindowOpen() {
        boolean check1 = Client.isColorInRect(Color.decode("#5b5345"), chatWindowCheck1Rect, 10);
        boolean check2 = Client.isColorInRect(Color.decode("#5b5345"), chatWindowCheck2Rect, 10);

        return check1 && check2;
    }

    private void readShopStack() {
        stackSize1 = -1;
        stackSize2 = -1;
        stackSize3 = -1;

        switch (shopBuyer.itemToBuy) {
            case "Copper + Tin":
                stackSize1 = interfaces.readStackSize(copperOreStackRect);
                stackSize2 = interfaces.readStackSize(tinOreStackRect);
                printStackSizes(2);
                break;
            case "Copper ore":
                stackSize1 = interfaces.readStackSize(copperOreStackRect);
                printStackSizes(1);
                break;
            case "Tin ore":
                stackSize1 = interfaces.readStackSize(tinOreStackRect);
                printStackSizes(1);
                break;
            case "Iron + Coal":
                stackSize1 = interfaces.readStackSize(ironOreStackRect);
                stackSize2 = interfaces.readStackSize(coalOreStackRect);
                printStackSizes(2);
                break;
            case "Iron ore":
                stackSize1 = interfaces.readStackSize(ironOreStackRect);
                printStackSizes(1);
                break;
            case "Mithril + Coal":
                stackSize1 = interfaces.readStackSize(mithrilOreStackRect);
                stackSize2 = interfaces.readStackSize(coalOreStackRect);
                printStackSizes(2);
                break;
            case "Mithril ore":
                stackSize1 = interfaces.readStackSize(mithrilOreStackRect);
                printStackSizes(1);
                break;
            case "Silver ore":
                stackSize1 = interfaces.readStackSize(silverOreStackRect);
                printStackSizes(1);
                break;
            case "Gold ore":
                stackSize1 = interfaces.readStackSize(goldOreStackRect);
                printStackSizes(1);
                break;
            case "Coal":
                stackSize1 = interfaces.readStackSize(coalOreStackRect);
                printStackSizes(1);
                break;
            case "Jug of Wine":
                stackSize1 = interfaces.readStackSize(JugOfWineStackRect);
                printStackSizes(1);
                break;
            case "Empty Jug Pack":
                stackSize1 = interfaces.readStackSize(EmptyJugPackStackRect);
                printStackSizes(1);
                break;
            case "Empty bucket pack":
                stackSize1 = interfaces.readStackSize(khazardEmptyBucketStackRect);
                printStackSizes(1);
                break;
            case "Pineapple":
                stackSize1 = interfaces.readStackSize(khazardPineappleStackRect);
                printStackSizes(1);
                break;
            case "Bucket of slime":
                stackSize1 = interfaces.readStackSize(khazardBucketofSlimeStackRect);
                printStackSizes(1);
                break;
            case "Bucket of sand":
                stackSize1 = interfaces.readStackSize(khazardBucketofSandStackRect);
                printStackSizes(1);
                break;
            case "Seaweed":
                stackSize1 = interfaces.readStackSize(khazardSeaweedStackRect);
                printStackSizes(1);
                break;
            case "Soda ash":
                stackSize1 = interfaces.readStackSize(khazardSodaAshStackRect);
                printStackSizes(1);
                break;
            case "Bucket of sand + Seaweed":
                stackSize1 = interfaces.readStackSize(khazardBucketofSandStackRect);
                stackSize2 = interfaces.readStackSize(khazardSeaweedStackRect);
                printStackSizes(2);
                break;
            case "Bucket of sand + Soda ash":
                stackSize1 = interfaces.readStackSize(khazardBucketofSandStackRect);
                stackSize2 = interfaces.readStackSize(khazardSodaAshStackRect);
                printStackSizes(2);
                break;
            case "Sand + Seaweed + Soda ash":
                stackSize1 = interfaces.readStackSize(khazardBucketofSandStackRect);
                stackSize2 = interfaces.readStackSize(khazardSeaweedStackRect);
                stackSize3 = interfaces.readStackSize(khazardSodaAshStackRect);
                printStackSizes(3);
                break;
            default:
                Logger.log("Invalid item to buy for Blast Furnace, stopping script!");
                Bank.close();
                Logout.logout();
                Script.stop();
        }
    }

    private boolean qty50Selected() {
        return Client.isColorInRect(Color.decode("#7c1d1b"), quantity50Rect, 10);
    }

    private void printStackSizes(int sizes) {
        if (sizes == 1) {
            Logger.debugLog("Stacksize: " + stackSize1);
        } else if (sizes == 2){
            Logger.debugLog("Stacksize 1: " + stackSize1);
            Logger.debugLog("Stacksize 2: " + stackSize2);
        } else {
            Logger.debugLog("Stacksize 1: " + stackSize1);
            Logger.debugLog("Stacksize 2: " + stackSize2);
            Logger.debugLog("Stacksize 3: " + stackSize3);
        }
    }

    private void buyOres() {
        switch (shopBuyer.itemToBuy) {
            case "Copper + Tin":
                buyLogicDouble(copperOreClickRect, tinOreClickRect);
                resetStackSizes();
                break;
            case "Copper ore":
                buyLogicSingle(copperOreClickRect);
                break;
            case "Tin ore":
                buyLogicSingle(tinOreClickRect);
                break;
            case "Iron + Coal":
                buyLogicDouble(ironOreClickRect, coalOreClickRect);
                break;
            case "Iron ore":
                buyLogicSingle(ironOreClickRect);
                break;
            case "Mithril + Coal":
                buyLogicDouble(mithrilOreClickRect, coalOreClickRect);
                break;
            case "Mithril ore":
                buyLogicSingle(mithrilOreClickRect);
                break;
            case "Silver ore":
                buyLogicSingle(silverOreClickRect);
                break;
            case "Gold ore":
                buyLogicSingle(goldOreClickRect);
                break;
            case "Coal":
                buyLogicSingle(coalOreClickRect);
                break;
            default:
                Logger.log("Invalid item to buy for Blast Furnace, stopping script!");
                Bank.close();
                Logout.logout();
                Script.stop();
        }
    }

    private void buyFWSItems() {
        switch (shopBuyer.itemToBuy) {
            case "Jug of Wine":
                buyLogicSingle(JugOfWineClickRect);
                break;
            case "Empty Jug Pack":
                buyLogicSingle(EmptyJugPackClickRect);
                break;
            default:
                Logger.log("Invalid item to buy for Fortunato Wine Shop, stopping script!");
                Bank.close();
                Logout.logout();
                Script.stop();
        }
    }

    private void buyKCSItems() {
        switch (shopBuyer.itemToBuy) {
            case "Empty bucket pack":
                buyLogicSingle(khazardEmptyBucketClickRect);
                break;
            case "Pineapple":
                buyLogicSingle(khazardPineappleClickRect);
                break;
            case "Bucket of slime":
                buyLogicSingle(khazardBucketofSlimeClickRect);
                break;
            case "Bucket of sand":
                buyLogicSingle(khazardBucketofSandClickRect);
                break;
            case "Seaweed":
                buyLogicSingle(khazardSeaweedClickRect);
                break;
            case "Soda ash":
                buyLogicSingle(khazardSodaAshClickRect);
                break;
            case "Bucket of sand + Seaweed":
                buyLogicDouble(khazardBucketofSandClickRect, khazardSeaweedClickRect);
                break;
            case "Bucket of sand + Soda ash":
                buyLogicDouble(khazardBucketofSandClickRect, khazardSodaAshClickRect);
                break;
            case "Sand + Seaweed + Soda ash":
                buyLogicTriple(khazardBucketofSandClickRect, khazardSeaweedClickRect, khazardSodaAshClickRect);
                break;
            default:
                Logger.log("Invalid item to buy for Fortunato Wine Shop, stopping script!");
                Bank.close();
                Logout.logout();
                Script.stop();
        }
    }

    private void buyLogicSingle(Rectangle rect) {
        if (stackSize1 > 0 && shopBuyer.boughtAmount1 < shopBuyer.amountToBuy) {
            Client.tap(rect);
        }

        // Stop the script if item has reached or exceeded their target amounts
        if (shopBuyer.boughtAmount1 >= shopBuyer.amountToBuy) {
            Paint.setStatus("Target reached for item, stopping script.");
            Logger.log("Target reached for item, stopping script.");
            interfaces.closeCraftJewelleryInterface();

            if (shopBuyer.shopToUse.equals("Blast Furnace")) {
                Condition.wait(() -> !isBFShopOpen(), 400, 50);
            } else if (shopBuyer.shopToUse.equals("Fortunato Wine Shop")) {
                Condition.wait(() -> !isFortunatoShopOpen(), 400, 50);
            } else if (shopBuyer.shopToUse.equals("Khazard Charter")) {
                Condition.wait(() -> !isKhazardShopOpen(), 400, 50);
            }

            Logout.logout();
            Script.stop();
        }

        resetStackSizes();
    }

    private void buyLogicDouble(Rectangle rect1, Rectangle rect2) {
        int totalBought = shopBuyer.boughtAmount1 + shopBuyer.boughtAmount2;
        int targetAmount = shopBuyer.amountToBuy * 2; // Total target for both items combined

        // Always attempt to buy item 1 if the target for item 1 hasn't been reached
        if (stackSize1 > 0 && shopBuyer.boughtAmount1 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 1");
            Client.tap(rect1);  // Buy from rect1 regardless of the stack size as long as we're under the target
        }

        // Check if it's safe to buy item 2 without filling the inventory due to item 1
        if (stackSize2 > 0 && shopBuyer.boughtAmount2 < shopBuyer.amountToBuy && totalBought < targetAmount) {
            if (shopBuyer.boughtAmount1 >= shopBuyer.amountToBuy || stackSize1 < 27) {
                // If item 1 is already bought to its target, or stackSize1 hasn't filled the inventory
                Paint.setStatus("Buy item 2");
                Client.tap(rect2);  // Buy from rect2
            }
        }

        // If stackSize1 is 0, only buy from rect2 if below the target
        else if (stackSize1 == 0 && stackSize2 > 0 && shopBuyer.boughtAmount2 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 2");
            Client.tap(rect2);
        }

        // If stackSize2 is 0, only buy from rect1 if below the target
        else if (stackSize2 == 0 && stackSize1 > 0 && shopBuyer.boughtAmount1 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 1");
            Client.tap(rect1);
        }

        // Stop the script if both items have reached or exceeded their target amounts
        if (shopBuyer.boughtAmount1 >= shopBuyer.amountToBuy && shopBuyer.boughtAmount2 >= shopBuyer.amountToBuy) {
            Paint.setStatus("Target reached for both items, stopping script.");
            Logger.log("Target reached for both items, stopping script.");
            interfaces.closeCraftJewelleryInterface();

            if (shopBuyer.shopToUse.equals("Blast Furnace")) {
                Condition.wait(() -> !isBFShopOpen(), 400, 50);
            } else if (shopBuyer.shopToUse.equals("Khazard Charter")) {
                Condition.wait(() -> !isKhazardShopOpen(), 400, 50);
            }

            Logout.logout();
            Script.stop();
        }

        resetStackSizes();
    }

    private void buyLogicTriple(Rectangle rect1, Rectangle rect2, Rectangle rect3) {
        int totalBought = shopBuyer.boughtAmount1 + shopBuyer.boughtAmount2 + shopBuyer.boughtAmount3;
        int targetAmount = shopBuyer.amountToBuy * 3; // Total target for all three items combined

        // Don't buy item 3 if stack1 and stack2 together are 27 or more
        if (stackSize1 + stackSize2 >= 27 && shopBuyer.boughtAmount3 < shopBuyer.amountToBuy) {
            Paint.setStatus("Skipping item 3 due to full inventory with item 1 and 2");
        }
        // Don't buy item 1 if stack2 and stack3 together are 27 or more
        else if (stackSize2 + stackSize3 >= 27 && shopBuyer.boughtAmount1 < shopBuyer.amountToBuy) {
            Paint.setStatus("Skipping item 1 due to full inventory with item 2 and 3");
        }
        // Don't buy item 2 if stack1 and stack3 together are 27 or more
        else if (stackSize1 + stackSize3 >= 27 && shopBuyer.boughtAmount2 < shopBuyer.amountToBuy) {
            Paint.setStatus("Skipping item 2 due to full inventory with item 1 and 3");
        }

        // Logic for buying item 1
        if (shopBuyer.boughtAmount1 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 1");
            Client.tap(rect1);  // Buy from rect1 regardless of the stack size as long as we're under the target
        }

        // Logic for buying item 2
        if (shopBuyer.boughtAmount2 < shopBuyer.amountToBuy && totalBought < targetAmount) {
            if (shopBuyer.boughtAmount1 >= shopBuyer.amountToBuy || stackSize1 < 27) {
                Paint.setStatus("Buy item 2");
                Client.tap(rect2);  // Buy from rect2
            }
        }

        // Allow buying item 3 even if stackSize1 and stackSize2 are >= 27 if their targets are reached
        if (shopBuyer.boughtAmount3 < shopBuyer.amountToBuy && totalBought < targetAmount) {
            // Only check if item 1 and item 2 haven't yet reached their targets
            if ((shopBuyer.boughtAmount1 >= shopBuyer.amountToBuy || stackSize1 < 27) &&
                    (shopBuyer.boughtAmount2 >= shopBuyer.amountToBuy || stackSize2 < 27)) {
                Paint.setStatus("Buy item 3");
                Client.tap(rect3);  // Buy from rect3
            } else if (shopBuyer.boughtAmount1 >= shopBuyer.amountToBuy && shopBuyer.boughtAmount2 >= shopBuyer.amountToBuy) {
                // If both item 1 and item 2 have been fully bought, ignore stack sizes
                Paint.setStatus("Buy item 3 (ignoring stack sizes for item 1 and 2)");
                Client.tap(rect3);
            }
        }

        // If stackSize1 is 0, only buy from rect2 and rect3 if below the targets
        else if (stackSize1 == 0 && stackSize2 > 0 && shopBuyer.boughtAmount2 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 2");
            Client.tap(rect2);
        } else if (stackSize1 == 0 && stackSize3 > 0 && shopBuyer.boughtAmount3 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 3");
            Client.tap(rect3);
        }

        // If stackSize2 is 0, only buy from rect1 and rect3 if below the targets
        else if (stackSize2 == 0 && stackSize1 > 0 && shopBuyer.boughtAmount1 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 1");
            Client.tap(rect1);
        } else if (stackSize2 == 0 && stackSize3 > 0 && shopBuyer.boughtAmount3 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 3");
            Client.tap(rect3);
        }

        // If stackSize3 is 0, only buy from rect1 and rect2 if below the targets
        else if (stackSize3 == 0 && stackSize1 > 0 && shopBuyer.boughtAmount1 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 1");
            Client.tap(rect1);
        } else if (stackSize3 == 0 && stackSize2 > 0 && shopBuyer.boughtAmount2 < shopBuyer.amountToBuy) {
            Paint.setStatus("Buy item 2");
            Client.tap(rect2);
        }

        // Stop the script if all three items have reached or exceeded their target amounts
        if (shopBuyer.boughtAmount1 >= shopBuyer.amountToBuy && shopBuyer.boughtAmount2 >= shopBuyer.amountToBuy &&
                shopBuyer.boughtAmount3 >= shopBuyer.amountToBuy) {
            Paint.setStatus("Target reached for all three items, stopping script.");
            Logger.log("Target reached for all three items, stopping script.");
            interfaces.closeCraftJewelleryInterface();

            if (shopBuyer.shopToUse.equals("Khazard Charter")) {
                Condition.wait(() -> !isKhazardShopOpen(), 400, 50);
            }

            Logout.logout();
            Script.stop();
        }

        resetStackSizes();
    }

    private void blastFurnaceHandleBuy() {
        Paint.setStatus("Get player position");
        currentLoc = Walker.getPlayerPosition();

        // Logic till we have the shop open
        Paint.setStatus("Trade Ordan");
        if (Player.tileEquals(currentLoc, shopBuyer.BFBankTile)) {
            // Open the shop from the bank tile
            Client.tap(shopBuyer.BFShopRect);
            Condition.wait(this::isBFShopOpen, 250, 50);
        } else if (Player.tileEquals(currentLoc, shopBuyer.BFShopTile)) {
            // We are at the shop tile
            Client.tap(shopFromShopTileRect);
            Condition.wait(this::isBFShopOpen, 150, 20);
        } else {
            // We are not at the bank OR shop
            Walker.step(shopBuyer.BFShopTile);
            Condition.wait(() -> Player.atTile(shopBuyer.BFShopTile), 250, 50);
            Client.tap(shopFromShopTileRect);
            Condition.wait(this::isBFShopOpen, 150, 20);
        }

        // Shop should now be open
        if (isBFShopOpen()) {
            Paint.setStatus("Read shop stock");
            // Read the ore stacks
            readShopStack();

            // Check if buy 50 is selected, select if not
            if (!qty50Selected() && (stackSize1 > 0 || stackSize2 > 0)) {
                Client.tap(quantity50Rect);
            }

            // Buy ores
            Paint.setStatus("Buy ores");
            buyOres();
            interfaces.closeCraftJewelleryInterface();
            Condition.wait(() -> !isBFShopOpen(), 150, 35);
            Condition.sleep(generateRandomDelay(400, 650));

            if (Inventory.isFull()) {
                Logger.debugLog("Inventory is full, done buying.");
            } else if (stackSize1 > 0 || stackSize2 > 0) {
                // If there is still stock in the shop, continue buying
                Paint.setStatus("Continue buying, shop still has stock.");
                Logger.debugLog("Shop still has stock, continue buying.");
            } else {
                // If no stock left, hop worlds
                Paint.setStatus("Hop worlds");
                Logger.debugLog("No stock left, hopping to a new world.");
                Game.instantHop(shopBuyer.hopProfile);
            }
        } else {
            if (!shopBuyer.doneMESSetup) {
                if (dShopBuyer.MESSetupTries < 4) {
                    Logger.log("MES Setup is not done yet, verifying if it's set up correctly!");
                    dShopBuyer.MESSetupTries++;
                }

                if (isChatWindowOpen()) {
                    Logger.log("Chat window is open, confirmed talk-to option is the first option.");

                    Paint.setStatus("Longpress Ordan");

                    Client.longPress(shopFromShopTileRect);
                    Condition.sleep(750);

                    // Perform by setting up the MES stuff here
                    if (enableMES()) {
                        swapMESOptions("/imgs/blast-talk-to.png", "/imgs/blast-trade.png");
                    }
                }
            } else {
                Logger.log("Shop not open yet... Retrying");
                Client.tap(new Rectangle(642, 221, 198, 4));
                notFoundCounter++;
            }
        }
    }

    private void fortunatoHandleBuy() {
        Paint.setStatus("Check player position");

        if (!Player.within(fortunatoArea)) {
            Walker.step(shopBuyer.FWSShopTile);
        }

        if (notFoundCounter >= 4) {
            Logger.debugLog("Failed to locate Fortunato 4 times, hopping worlds");
            Paint.setStatus("Hop worlds");
            Game.instantHop(shopBuyer.hopProfile);
            if (!Player.atTile(shopBuyer.FWSShopTile)){
                Walker.step(shopBuyer.FWSShopTile);
            }
            notFoundCounter = 0;
        }

        // Locate Fortunato and open store if found
        Paint.setStatus("Locate Fortunato");
        List<Point> foundPoints = Client.getPointsFromColorsInRect(fortunatoColors, fortunatoScanArea, 2);

        if (!foundPoints.isEmpty()) {
            Point hotspot = findPointHotSpot(foundPoints, 25);

            if (hotspot != null) {
                Logger.debugLog("Located Fortunato hotspot at: " + hotspot);
                // Perform the long press at the hotspot
                Client.tap(hotspot);
                Condition.wait(this::isFortunatoShopOpen, 150, 35);
            } else {
                Logger.debugLog("No valid hotspot found.");
            }
        } else {
            Logger.debugLog("No color points found.");
            Condition.sleep(generateRandomDelay(400, 750));
        }

        // Check if shop is now open
        if (isFortunatoShopOpen()) {
            notFoundCounter = 0;
            Paint.setStatus("Read shop stock");
            readShopStack();

            // Check if buy 50 is selected, select if not
            if (!qty50Selected() && stackSize1 != 0 && stackSize2 != 0) {Client.tap(quantity50Rect);}

            // Buy items
            Paint.setStatus("Buy items");

            buyFWSItems();
            interfaces.closeCraftJewelleryInterface();
            Condition.wait(() -> !isFortunatoShopOpen(), 150, 35);
            Condition.sleep(generateRandomDelay(400, 650));

            if (Inventory.isFull()) {
                Logger.debugLog("Inventory is full, done buying.");
            } else if (stackSize1 > 0) {
                // If there is still stock in the shop, continue buying
                Paint.setStatus("Continue buying, shop still has stock.");
                Logger.debugLog("Shop still has stock, continue buying.");
            } else {
                // If no stock left, hop worlds
                Paint.setStatus("Hop worlds");
                Logger.debugLog("No stock left, hopping to a new world.");
                Game.instantHop(shopBuyer.hopProfile);
            }

        } if (!shopBuyer.doneMESSetup) {
            if (dShopBuyer.MESSetupTries < 4) {
                Logger.log("MES Setup is not done yet, verifying if it's set up correctly!");
                dShopBuyer.MESSetupTries++;
            }

            if (isChatWindowOpen()) {
                Logger.log("Chat window is open, confirmed talk-to option is the first option.");

                // Locate Fortunato and long press to open the menu
                Paint.setStatus("Locate Fortunato");
                List<Point> foundPoints2 = Client.getPointsFromColorsInRect(fortunatoColors, fortunatoScanArea, 2);

                if (!foundPoints2.isEmpty()) {
                    Point hotspot = findPointHotSpot(foundPoints2, 25);

                    if (hotspot != null) {
                        Logger.debugLog("Located Fortunato hotspot at: " + hotspot);
                        // Perform the long press at the hotspot
                        Client.longPress(hotspot.x, hotspot.y);
                        Condition.sleep(750);

                        // Perform by setting up the MES stuff here
                        if (enableMES()) {
                            swapMESOptions("/imgs/fortunato-talk-to.png", "/imgs/fortunato-trade.png");
                        }
                    } else {
                        Logger.debugLog("No valid hotspot found.");
                    }
                } else {
                    Logger.debugLog("No color points found.");
                    Condition.sleep(generateRandomDelay(400, 750));
                }
            }
        } else {
            Logger.log("Shop not open yet... Retrying");
            Client.tap(new Rectangle(642, 221, 198, 4));
            notFoundCounter++;
        }
    }

    private void khazardHandleBuy() {
        Paint.setStatus("Check player position");

        if (!Player.within(khazardShopArea)) {
            if (Walker.isReachable(shopBuyer.khazardCharterCrewStepTile)){
                Walker.step(shopBuyer.khazardCharterCrewStepTile);
            } else {
                Walker.walkTo(shopBuyer.khazardCharterWalkToTile);
                Walker.step(shopBuyer.khazardCharterCrewStepTile);
            }
        }

        if (notFoundCounter >= 4) {
            Logger.debugLog("Failed to locate Khazard Charter Crew 4 times, hopping worlds");
            Paint.setStatus("Hop worlds");
            Game.instantHop(shopBuyer.hopProfile);
            if (!Player.within(khazardShopArea)){
                Walker.step(shopBuyer.khazardCharterCrewStepTile);
            }
            notFoundCounter = 0;
        }

        // Locate Charter Crew and open store if found
        Paint.setStatus("Locate Charter Crew");
        List<Point> foundPoints = Client.getPointsFromColorsInRect(khazardCrewColors, khazardCrewScanArea, 5);

        if (!foundPoints.isEmpty()) {
            Point hotspot = findPointHotSpot(foundPoints, 25);

            if (hotspot != null) {
                Logger.debugLog("Located Charter Crew hotspot at: " + hotspot);
                // Perform the long press at the hotspot
                Client.tap(hotspot);
                Condition.wait(this::isKhazardShopOpen, 150, 35);
            } else {
                Logger.debugLog("No valid hotspot found.");
            }
        } else {
            Logger.debugLog("No color points found.");
            Condition.sleep(generateRandomDelay(400, 750));
        }

        // Check if shop is now open
        if (isKhazardShopOpen()) {
            notFoundCounter = 0;
            Paint.setStatus("Read shop stock");
            readShopStack();

            // Check if buy 50 is selected, select if not
            if (!qty50Selected() && stackSize1 != 0 && stackSize2 != 0) {Client.tap(quantity50Rect);}

            // Buy items
            Paint.setStatus("Buy items");

            buyKCSItems();
            interfaces.closeCraftJewelleryInterface();
            Condition.wait(() -> !isKhazardShopOpen(), 150, 35);
            Condition.sleep(generateRandomDelay(400, 650));

            if (Inventory.isFull()) {
                Logger.debugLog("Inventory is full, done buying.");
            } else if (stackSize1 > 0 || stackSize2 > 0 || stackSize3 > 0) {
                // If there is still stock in the shop, continue buying
                Paint.setStatus("Continue buying, shop still has stock.");
                Logger.debugLog("Shop still has stock, continue buying.");
            } else {
                // If no stock left, hop worlds
                Paint.setStatus("Hop worlds");
                Logger.debugLog("No stock left, hopping to a new world.");
                Game.instantHop(shopBuyer.hopProfile);
            }

        } else {
            if (!shopBuyer.doneMESSetup) {
                if (dShopBuyer.MESSetupTries < 4) {
                    Logger.log("MES Setup is not done yet, verifying if it's set up correctly!");
                    dShopBuyer.MESSetupTries++;
                }

                if (isChatWindowOpen()) {
                    Logger.log("Chat window is open, confirmed talk-to option is the first option.");

                    // Locate Charter Crew and long press it for the menu
                    Paint.setStatus("Locate Charter Crew");
                    List<Point> foundPoints2 = Client.getPointsFromColorsInRect(khazardCrewColors, khazardCrewScanArea, 5);

                    if (!foundPoints2.isEmpty()) {
                        Point hotspot = findPointHotSpot(foundPoints2, 25);

                        if (hotspot != null) {
                            Logger.debugLog("Located Charter Crew hotspot at: " + hotspot);
                            // Perform the long press at the hotspot
                            Client.longPress(hotspot.x, hotspot.y);
                            Condition.sleep(750);

                            // Perform by setting up the MES stuff here
                            if (enableMES()) {
                                swapMESOptions("/imgs/khazard-talk-to.png", "/imgs/khazard-trade.png");
                            }
                        } else {
                            Logger.debugLog("No valid hotspot found.");
                        }
                    } else {
                        Logger.debugLog("No color points found.");
                        Condition.sleep(generateRandomDelay(400, 750));
                    }
                }
            } else {
                Logger.log("Shop not open yet... Retrying");
                Client.tap(new Rectangle(642, 221, 198, 4));
                notFoundCounter++;
            }
        }
    }

    private void khazardCheckWanderedOff() {
        if (!Player.within(khazardScriptArea)) {
            Paint.setStatus("Recover from wander off");
            Logger.debugLog("Seems like we have wandered off, recovering!");
            if (Walker.isReachable(khazardRecoverTile)) {
                Paint.setStatus("Step back to script area");
                Logger.debugLog("Stepping back to script area!");
                Walker.step(khazardRecoverTile);
            } else {
                Logger.debugLog("Webwalking back to script area!");
                Paint.setStatus("Webwalk back to script area");
                Walker.webWalk(khazardRecoverTile);
            }
        }
    }

    private boolean enableMES() {
        Rectangle mesEnabled = null;
        Rectangle mesDisabled = null;

        mesEnabled = Objects.getBestMatch("/imgs/enabled-mes.png", 0.8);

        Logger.log("mesEnabled:" + mesEnabled);
        Logger.debugLog("mesEnabled: " + mesEnabled);

        if (mesEnabled != null) {
            Logger.debugLog("Found the MES enabled option, Menu Entry Swapper menu is enabled!");
            return true;
        } else {
            Logger.debugLog("Could not find the MES enabled option, trying to find the disabled option...");
            mesDisabled = Objects.getBestMatch("/imgs/disabled-mes.png", 0.8);
        }

        if (mesDisabled != null) {
            Logger.debugLog("Found the MES disabled option, enabling MES!");
            Client.tap(mesDisabled);
            Condition.sleep(1500);
            return true;
        } else {
            Logger.debugLog("Failed to find the MES disabled option!");
        }

        if (mesDisabled == null && mesEnabled == null) {
            Logger.log("Both MES options could not be found, stopping script, please manually set up your menu entry swapper!");
            Script.stop();
        }
        return false;
    }

    private boolean swapMESOptions(String talkToImgPath, String tradeImgPath) {
        Rectangle talkOption = null;
        Rectangle tradeOption = null;
        Rectangle mesEnabled = null;

        Logger.debugLog("Finding the talk-to option.");
        talkOption = Objects.getBestMatch(talkToImgPath, 0.8);

        Logger.debugLog("Finding the trade option.");
        tradeOption = Objects.getBestMatch(tradeImgPath, 0.8);

        if (talkOption != null) {
            Logger.debugLog("Talk option was found: " + talkOption.toString());
        } else {
            Logger.debugLog("Talk option was NOT FOUND");
        }

        if (tradeOption != null) {
            Logger.debugLog("Trade option was found: " + tradeOption.toString());
        } else {
            Logger.debugLog("Trade option was NOT FOUND");
        }

        if (talkOption != null && tradeOption != null) {
            Logger.log("Talk and trade options both found, trying to swap the menu entries");

            // Create new rectangles with the same height and only the first 15 pixels in width
            Rectangle talkOptionShort = new Rectangle(talkOption.x, talkOption.y, 15, talkOption.height);
            Rectangle tradeOptionShort = new Rectangle(tradeOption.x, tradeOption.y, 15, tradeOption.height);

            // Check if the Trade option is above the Talk-to option
            if (tradeOption.y < talkOption.y) {
                Logger.debugLog("Trade option is above the Talk-to option.");
                Logger.debugLog("No action needed.");
                shopBuyer.doneMESSetup = true;
            } else {
                Logger.debugLog("Talk-to option is above the Trade option.");
                Logger.debugLog("Perform swap of options, as trade needs to be on top.");
                Client.drag(tradeOptionShort, talkOptionShort, 500);
                Condition.sleep(1500);
                shopBuyer.doneMESSetup = true;
            }
        }

        mesEnabled = Objects.getBestMatch("/imgs/enabled-mes.png", 0.8);

        if (mesEnabled != null) {
            Logger.debugLog("Disabling MES option menu");
            Client.tap(mesEnabled);
            Condition.sleep(generateRandomDelay(500, 1000));
            return true;
        }

        return false;
    }

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        return lowerBound + dShopBuyer.random.nextInt(upperBound - lowerBound + 1);
    }

    private void resetStackSizes() {
        stackSize1 = 0;
        stackSize2 = 0;
        stackSize3 = 0;
    }

    private Point findPointHotSpot(List<Point> points, int proximityThreshold) {
        if (points.isEmpty()) {
            return null; // No points found, return null
        }

        List<List<Point>> clusters = new ArrayList<>();

        // Group points into clusters based on proximity
        for (Point point : points) {
            boolean addedToCluster = false;

            // Try to add the point to an existing cluster
            for (List<Point> cluster : clusters) {
                for (Point clusterPoint : cluster) {
                    if (point.distance(clusterPoint) <= proximityThreshold) {
                        cluster.add(point);
                        addedToCluster = true;
                        break;
                    }
                }
                if (addedToCluster) break; // Stop looking once the point is added to a cluster
            }

            // If the point doesn't fit into any cluster, create a new one
            if (!addedToCluster) {
                List<Point> newCluster = new ArrayList<>();
                newCluster.add(point);
                clusters.add(newCluster);
            }
        }

        // Find the cluster with the most points
        List<Point> largestCluster = clusters.stream()
                .max(Comparator.comparingInt(List::size))
                .orElse(new ArrayList<>()); // In case of no clusters

        if (largestCluster.isEmpty()) {
            return null; // No valid clusters found
        }

        // Find the "center" point of the largest cluster
        // Calculate average x and y values to get the centroid
        int sumX = 0, sumY = 0;
        for (Point p : largestCluster) {
            sumX += p.x;
            sumY += p.y;
        }
        int centerX = sumX / largestCluster.size();
        int centerY = sumY / largestCluster.size();

        // Return the center point (can pick the closest point to the centroid)
        Point centerPoint = new Point(centerX, centerY);

        return centerPoint;
    }
}
