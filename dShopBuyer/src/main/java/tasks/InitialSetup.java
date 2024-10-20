package tasks;

import static helpers.Interfaces.*;

import helpers.annotations.AllowedValue;
import helpers.utils.ItemList;
import helpers.utils.MapChunk;
import utils.Task;
import main.dShopBuyer;

import java.awt.*;

public class InitialSetup extends Task {
    private boolean checkedSetup = false;
    private dShopBuyer shopBuyer;

    public InitialSetup(dShopBuyer shopBuyer) {
        this.shopBuyer = shopBuyer;
    }

    @Override
    public boolean activate() {return !checkedSetup;}

    @Override
    public boolean execute() {

        switch (shopBuyer.shopToUse) {
            case "Blast Furnace":
                setupBlastFurnace();
                checkedSetup = true;
                break;
            case "Fortunato Wine Shop":
                setupFortunatoWine();
                checkedSetup = true;
                break;
            case "Khazard Charter":
                setupKhazardCharter();
                checkedSetup = true;
                break;
            default:
                Logger.log("Invalid store, stopping script!");
                Logout.logout();
                Script.stop();
        }

        shopBuyer.startTime = System.currentTimeMillis();
        return false;
    }

    public boolean setupBlastFurnace() {
        // Set up paint bar
        setupPaintBar();

        // Create the MapChunk with chunk "30-77 and 31-77 (to avoid using 3x3 chunks) and plane "0"
        MapChunk chunks = new MapChunk(new String[]{"30-77", "31-77"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Some interface actions
        Chatbox.closeChatbox();
        GameTabs.openSettingsTab();
        Game.setZoom("1");
        GameTabs.closeSettingsTab();
        GameTabs.openInventoryTab();

        // Check prerequisites
        if (!Inventory.contains(ItemList.COINS_10000_1004, 0.7)) {
            shopBuyer.hasCoins = false;
            Logger.log("We have no coins in our inventory. Stopping script!");
            Logout.logout();
            Script.stop();
        } else {
            shopBuyer.initialCoins = Inventory.stackSize(ItemList.COINS_10000_1004);
            Logger.log("Initial starting coin stack: " + shopBuyer.initialCoins);
            checkedSetup = true;
        }

        // Check if we are in the correct area
        if (!Player.within(shopBuyer.BFScriptArea)) {
            Logger.log("Not within the Blast Furnace area, stopping script!");
            Logout.logout();
            Script.stop();
        }

        // Start at the bank
        if (!Player.atTile(shopBuyer.BFBankTile)) {
            Walker.step(shopBuyer.BFBankTile);
        }

        // Set up the bank how we like it
        Client.tap(new Rectangle(443, 281, 12, 9));
        Condition.wait(() -> Bank.isOpen(), 100, 20);

        if (Bank.isOpen()) {
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
                Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 150, 20);
            }
        }

        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 100, 20);

        // Set up the integer for our ores
        switch (shopBuyer.itemToBuy) {
            case "Copper + Tin":
            case "Copper ore":
                shopBuyer.bankItemID = ItemList.COPPER_ORE_436;
                break;
            case "Tin ore":
                shopBuyer.bankItemID = ItemList.TIN_ORE_438;
                break;
            case "Iron + Coal":
            case "Iron ore":
                shopBuyer.bankItemID = ItemList.IRON_ORE_440;
                break;
            case "Mithril + Coal":
            case "Mithril ore":
                shopBuyer.bankItemID = ItemList.MITHRIL_ORE_447;
                break;
            case "Silver ore":
                shopBuyer.bankItemID = ItemList.SILVER_ORE_442;
                break;
            case "Gold ore":
                shopBuyer.bankItemID = ItemList.GOLD_ORE_444;
                break;
            case "Coal":
                shopBuyer.bankItemID = ItemList.COAL_453;
                break;
            default:
                Logger.log("Invalid item to buy for Blast Furnace, stopping script!");
                Bank.close();
                Logout.logout();
                Script.stop();
        }

        return false;
    }

    public boolean setupFortunatoWine() {
        // Set up paint bar
        setupPaintBar();

        // Create the MapChunk
        MapChunk chunks = new MapChunk(new String[]{"48-50"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Some interface actions
        Chatbox.closeChatbox();
        GameTabs.openInventoryTab();

        // Check prerequisites
        if (!Inventory.contains(ItemList.COINS_10000_1004, 0.7)) {
            shopBuyer.hasCoins = false;
            Logger.log("We have no coins in our inventory. Stopping script!");
            Logout.logout();
            Script.stop();
        } else {
            shopBuyer.initialCoins = Inventory.stackSize(ItemList.COINS_10000_1004);
            Logger.log("Initial starting coin stack: " + shopBuyer.initialCoins);
            checkedSetup = true;
        }

        // Check if we are in the correct area
        if (!Player.within(shopBuyer.FortunatoWineScriptArea)) {
            Logger.log("Not within the Fortunato Wine Shop area, stopping script!");
            Logout.logout();
            Script.stop();
        }

        // Start at the bank
        if (!Player.atTile(shopBuyer.FWSBankTile)) {
            Walker.step(shopBuyer.FWSBankTile);
        }

        // Set up the bank how we like it
        Client.tap(shopBuyer.FWSBankRect);
        Condition.wait(() -> Bank.isOpen(), 100, 20);

        if (Bank.isOpen()) {
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
                Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 150, 20);
            }
        }

        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 100, 20);

        // Set up the integer for our ores
        switch (shopBuyer.itemToBuy) {
            case "Jug of Wine":
                shopBuyer.bankItemID = ItemList.JUG_OF_WINE_1993;
                break;
            case "Empty Jug Pack":
                shopBuyer.bankItemID = ItemList.EMPTY_JUG_PACK_20742;
                break;
            default:
                Logger.log("Invalid item to buy for Fortunato Wine Shop, stopping script!");
                Bank.close();
                Logout.logout();
                Script.stop();
        }

        return false;
    }

    public boolean setupKhazardCharter() {
        // Set up paint bar
        setupPaintBar();

        // Create the MapChunk
        MapChunk chunks = new MapChunk(new String[]{"41-49"}, "0");

        // Set up the walker with the created MapChunk
        Walker.setup(chunks);

        // Some interface actions
        Chatbox.closeChatbox();
        GameTabs.openInventoryTab();

        // Check prerequisites
        if (!Inventory.contains(ItemList.COINS_10000_1004, 0.7)) {
            shopBuyer.hasCoins = false;
            Logger.log("We have no coins in our inventory. Stopping script!");
            Logout.logout();
            Script.stop();
        } else {
            shopBuyer.initialCoins = Inventory.stackSize(ItemList.COINS_10000_1004);
            Logger.log("Initial starting coin stack: " + shopBuyer.initialCoins);
            checkedSetup = true;
        }

        // Check if we are in the correct area
        if (!Player.within(shopBuyer.khazardCharterArea)) {
            Logger.log("Not within the Khazard Charter area, stopping script!");
            Logout.logout();
            Script.stop();
        }

        // Start at the bank
        if (!Player.atTile(shopBuyer.khazardCharterBankTile)) {
            if (Walker.isReachable(shopBuyer.khazardCharterBankTile)) {
                Walker.step(shopBuyer.khazardCharterBankTile);
            } else {
                Walker.walkTo(shopBuyer.khazardCharterWalkToTile);
                Walker.step(shopBuyer.khazardCharterBankTile);

                if (!Player.atTile(shopBuyer.khazardCharterBankTile)) {
                    Walker.step(shopBuyer.khazardCharterBankTile);
                }
            }
        }

        // Set up the bank how we like it
        Client.tap(shopBuyer.khazardCharterBankRect);
        Condition.wait(() -> Bank.isOpen(), 100, 20);

        if (Bank.isOpen()) {
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
                Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 150, 20);
            }
        }

        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 100, 20);

        // Set up the integer for our ores
        switch (shopBuyer.itemToBuy) {
            case "Empty bucket pack":
                shopBuyer.bankItemID = ItemList.EMPTY_BUCKET_PACK_22660;
                break;
            case "Pineapple":
                shopBuyer.bankItemID = ItemList.PINEAPPLE_2114;
                break;
            case "Bucket of slime":
                shopBuyer.bankItemID = ItemList.BUCKET_OF_SLIME_4286;
                break;
            case "Bucket of sand":
            case "Sand + Seaweed + Soda ash":
            case "Bucket of sand + Seaweed":
                shopBuyer.bankItemID = ItemList.BUCKET_OF_SAND_1783;
                break;
            case "Seaweed":
                shopBuyer.bankItemID = ItemList.SEAWEED_401;
                break;
            case "Soda ash":
                shopBuyer.bankItemID = ItemList.SODA_ASH_1781;
                break;
            default:
                Logger.log("Invalid item to buy for Fortunato Wine Shop, stopping script!");
                Bank.close();
                Logout.logout();
                Script.stop();
        }

        return false;
    }

    private void setupPaintBar() {
        Paint.setStatus("Set up paintBar");

        // Count the number of "+" signs in the item name
        int plusCount = 0;
        for (char c : shopBuyer.itemToBuy.toCharArray()) {
            if (c == '+') {
                plusCount++;
            }
        }

        if (plusCount == 2) {
            shopBuyer.boughtAmount1 = 0;
            shopBuyer.boughtAmount2 = 0;
            shopBuyer.boughtAmount3 = 0;
        } else if (plusCount == 1) {
            shopBuyer.boughtAmount1 = 0;
            shopBuyer.boughtAmount2 = 0;
        } else {
            shopBuyer.boughtAmount1 = 0;
        }

        switch (shopBuyer.itemToBuy) {
            case "Copper + Tin":
                shopBuyer.paintItem1 = Paint.createBox("Copper ore", ItemList.COPPER_ORE_436, 0);
                Condition.sleep(250);
                shopBuyer.paintItem2 = Paint.createBox("Tin ore", ItemList.TIN_ORE_438, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 4;
                shopBuyer.itemCost2 = 4;
                break;
            case "Copper ore":
                shopBuyer.paintItem1 = Paint.createBox("Copper ore", ItemList.COPPER_ORE_436, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 4;
                break;
            case "Tin ore":
                shopBuyer.paintItem1 = Paint.createBox("Tin ore", ItemList.TIN_ORE_438, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 4;
                break;
            case "Iron + Coal":
                shopBuyer.paintItem1 = Paint.createBox("Iron ore", ItemList.IRON_ORE_440, 0);
                Condition.sleep(250);
                shopBuyer.paintItem2 = Paint.createBox("Coal", ItemList.COAL_453, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 25;
                shopBuyer.itemCost2 = 67;
                break;
            case "Iron ore":
                shopBuyer.paintItem1 = Paint.createBox("Iron ore", ItemList.IRON_ORE_440, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 25;
                break;
            case "Mithril + Coal":
                shopBuyer.paintItem1 = Paint.createBox("Mithril ore", ItemList.MITHRIL_ORE_447, 0);
                Condition.sleep(250);
                shopBuyer.paintItem2 = Paint.createBox("Coal", ItemList.COAL_453, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 243;
                shopBuyer.itemCost2 = 67;
                break;
            case "Mithril ore":
                shopBuyer.paintItem1 = Paint.createBox("Mithril ore", ItemList.MITHRIL_ORE_447, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 243;
                break;
            case "Silver ore":
                shopBuyer.paintItem1 = Paint.createBox("Silver ore", ItemList.SILVER_ORE_442, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 112;
                break;
            case "Gold ore":
                shopBuyer.paintItem1 = Paint.createBox("Gold ore", ItemList.GOLD_ORE_444, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 225;
                break;
            case "Coal":
                shopBuyer.paintItem1 = Paint.createBox("Coal", ItemList.COAL_453, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 67;
                break;
            case "Jug of Wine":
                shopBuyer.paintItem1 = Paint.createBox("Jug of Wine", ItemList.JUG_OF_WINE_1993, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 1;
                break;
            case "Empty Jug Pack":
                shopBuyer.paintItem1 = Paint.createBox("Empty Jug Pack", ItemList.EMPTY_JUG_PACK_20742, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 145;
                break;
            case "Empty bucket pack":
                shopBuyer.paintItem1 = Paint.createBox("Empty bucket pack", ItemList.EMPTY_BUCKET_PACK_22660, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 1369;
                break;
            case "Pineapple":
                shopBuyer.paintItem1 = Paint.createBox("Pineapple", ItemList.PINEAPPLE_2114, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 6;
                break;
            case "Bucket of slime":
                shopBuyer.paintItem1 = Paint.createBox("Bucket of slime", ItemList.BUCKET_OF_SLIME_4286, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 3;
                break;
            case "Bucket of sand":
                shopBuyer.paintItem1 = Paint.createBox("Bucket of sand", ItemList.BUCKET_OF_SAND_1783, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 6;
                break;
            case "Seaweed":
                shopBuyer.paintItem1 = Paint.createBox("Seaweed", ItemList.SEAWEED_401, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 7;
                break;
            case "Soda ash":
                shopBuyer.paintItem1 = Paint.createBox("Soda ash", ItemList.SODA_ASH_1781, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 6;
                break;
            case "Bucket of sand + Seaweed":
                shopBuyer.paintItem1 = Paint.createBox("Bucket of sand", ItemList.BUCKET_OF_SAND_1783, 0);
                Condition.sleep(250);
                shopBuyer.paintItem2 = Paint.createBox("Seaweed", ItemList.SEAWEED_401, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 6;
                shopBuyer.itemCost2 = 7;
                break;
            case "Sand + Seaweed + Soda ash":
                shopBuyer.paintItem1 = Paint.createBox("Bucket of sand", ItemList.BUCKET_OF_SAND_1783, 0);
                Condition.sleep(250);
                shopBuyer.paintItem2 = Paint.createBox("Seaweed", ItemList.SEAWEED_401, 0);
                Condition.sleep(250);
                shopBuyer.paintItem3 = Paint.createBox("Soda ash", ItemList.SODA_ASH_1781, 0);
                Condition.sleep(250);
                shopBuyer.paintProfit = Paint.createBox("Coins used (est)", ItemList.COINS_10000_1004, 0);
                shopBuyer.itemCost1 = 6;
                shopBuyer.itemCost2 = 7;
                shopBuyer.itemCost3 = 6;
                break;
            default:
                // We do nothing here
        }
    }
}
