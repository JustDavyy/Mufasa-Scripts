package tasks;

import static helpers.Interfaces.*;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import main.dShopBuyer;
import utils.Task;

import java.awt.*;

public class Bank extends Task {

    private dShopBuyer shopBuyer;
    Tile currentLoc;
    Rectangle BFBankRectAtBank = new Rectangle(443, 281, 12, 9);

    public Bank(dShopBuyer shopBuyer) {
        this.shopBuyer = shopBuyer;
    }
    @Override
    public boolean activate() {
        return Inventory.isFull();
    }

    @Override
    public boolean execute() {

        switch (shopBuyer.shopToUse) {
            case "Blast Furnace":
                bankBlastFurnace();
                break;
            case "Fortunato Wine Shop":
                bankFortunato();
                break;
            case "Khazard Charter":
                bankKhazardCharter();
                break;
            default:
                Logger.log("Invalid store, stopping script!");
                Logout.logout();
                Script.stop();
        }

        return false;
    }


    public void bankBlastFurnace() {
        Paint.setStatus("Get player position");
        currentLoc = Walker.getPlayerPosition();

        // Open inventory
        GameTabs.openInventoryTab();

        // Logic till we open the bank
        if (Player.tileEquals(currentLoc, shopBuyer.BFBankTile)) {
            // We are already at the bank
            Paint.setStatus("Open bank");
            Client.tap(BFBankRectAtBank);
            Condition.wait(() -> Bank.isOpen(), 250, 35);
        } else if (Player.tileEquals(currentLoc, shopBuyer.BFShopTile)) {
            // We are at the shop
            Paint.setStatus("Walk to bank");
            Walker.step(shopBuyer.BFBankTile, updateBuyCounts());
            Condition.wait(() -> Player.atTile(shopBuyer.BFBankTile), 250, 50);
            Paint.setStatus("Open bank");
            Client.tap(BFBankRectAtBank);
            shopBuyer.updatePaintBar();
            Condition.wait(() -> Bank.isOpen(), 250, 35);
        } else {
            // We are not at the bank OR shop
            Paint.setStatus("Walk to bank");
            Walker.step(shopBuyer.BFBankTile);
            Condition.wait(() -> Player.atTile(shopBuyer.BFBankTile), 250, 50);
            Paint.setStatus("Open bank");
            Client.tap(BFBankRectAtBank);
            Condition.wait(() -> Bank.isOpen(), 250, 35);
        }

        // Logic to actually bank and deposit items
        if (Bank.isOpen()) {
            Paint.setStatus("Check Qty setting");
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
            }
            Paint.setStatus("Deposit bought items");
            switch (shopBuyer.itemToBuy) {
                case "Copper + Tin":
                    Inventory.tapItem(shopBuyer.bankItemID, 0.7);
                    Condition.sleep(generateRandomDelay(200, 400));
                    Inventory.tapItem(ItemList.TIN_ORE_438, 0.7);
                    break;
                case "Iron + Coal":
                case "Mithril + Coal":
                    Inventory.tapItem(shopBuyer.bankItemID, 0.7);
                    Condition.sleep(generateRandomDelay(200, 400));
                    Inventory.tapItem(ItemList.COAL_453, 0.7);
                    break;
                default:
                    Inventory.tapItem(shopBuyer.bankItemID, 0.7);
            }
        }

        Condition.wait(() -> !Inventory.isFull(), 250, 20);

        if (!Inventory.isFull()) {
            Paint.setStatus("Close bank");
            Bank.close();
        }

        Bank.isOpen();
    }

    public void bankFortunato() {

        // Logic till we open the bank
        if (!Player.atTile(shopBuyer.FWSBankTile)) {
            Paint.setStatus("Walk to bank");
            Walker.step(shopBuyer.FWSBankTile, updateBuyCounts());
            Condition.wait(() -> Player.atTile(shopBuyer.FWSBankTile), 250, 50);
            Paint.setStatus("Open bank");
            Client.tap(shopBuyer.FWSBankRect);
            Condition.wait(() -> Bank.isOpen(), 250, 35);
        } else {
            Paint.setStatus("Open bank");
            Client.tap(shopBuyer.FWSBankRect);
            Condition.wait(() -> Bank.isOpen(), 250, 35);
        }

        // Logic to actually bank and deposit items
        if (Bank.isOpen()) {
            Paint.setStatus("Check Qty setting");
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
            }
            Paint.setStatus("Deposit bought items");
            Inventory.tapItem(shopBuyer.bankItemID, 0.7);
        }

        Condition.wait(() -> !Inventory.isFull(), 250, 20);

        if (!Inventory.isFull()) {
            Paint.setStatus("Close bank");
            Bank.close();
        }

        shopBuyer.updatePaintBar();
        Bank.isOpen();
    }

    public void bankKhazardCharter() {

        // Logic till we open the bank
        if (!Player.atTile(shopBuyer.khazardCharterBankTile)) {
            Paint.setStatus("Walk to bank");
            if (Walker.isReachable(shopBuyer.khazardCharterBankTile)) {
                Walker.step(shopBuyer.khazardCharterBankTile, updateBuyCounts());
            } else {
                Walker.walkTo(shopBuyer.khazardCharterWalkToTile, updateBuyCounts());
                Walker.step(shopBuyer.khazardCharterBankTile);
            }
            Condition.wait(() -> Player.atTile(shopBuyer.khazardCharterBankTile), 250, 50);

            if (!Player.atTile(shopBuyer.khazardCharterBankTile)) {
                Walker.step(shopBuyer.khazardCharterBankTile);
            }

            Paint.setStatus("Open bank");
            Client.tap(shopBuyer.khazardCharterBankRect);
            Condition.wait(() -> Bank.isOpen(), 250, 35);
        } else {
            Paint.setStatus("Open bank");
            Client.tap(shopBuyer.khazardCharterBankRect);
            Condition.wait(() -> Bank.isOpen(), 250, 35);
        }

        // Logic to actually bank and deposit items
        if (Bank.isOpen()) {
            Paint.setStatus("Check Qty setting");
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
            }
            Paint.setStatus("Deposit bought items");

            switch (shopBuyer.itemToBuy) {
                case "Bucket of sand + Seaweed":
                    Inventory.tapItem(shopBuyer.bankItemID, 0.7);
                    Condition.sleep(generateRandomDelay(200, 400));
                    Inventory.tapItem(ItemList.SEAWEED_401, 0.7);
                    break;
                case "Bucket of sand + Soda ash":
                    Inventory.tapItem(shopBuyer.bankItemID, 0.7);
                    Condition.sleep(generateRandomDelay(200, 400));
                    Inventory.tapItem(ItemList.SODA_ASH_1781, 0.7);
                    break;
                case "Sand + Seaweed + Soda ash":
                    Inventory.tapItem(shopBuyer.bankItemID, 0.7);
                    Condition.sleep(generateRandomDelay(200, 400));
                    Inventory.tapItem(ItemList.SEAWEED_401, 0.7);
                    Condition.sleep(generateRandomDelay(200, 400));
                    Inventory.tapItem(ItemList.SODA_ASH_1781, 0.7);
                    break;
                default:
                    Inventory.tapItem(shopBuyer.bankItemID, 0.7);
                    break;
            }
        }

        Condition.wait(() -> !Inventory.isFull(), 250, 20);

        if (!Inventory.isFull()) {
            Paint.setStatus("Close bank");
            Bank.close();
        }

        shopBuyer.updatePaintBar();

        if (Bank.isOpen()) {
            Bank.close();
        }
    }

    private Runnable updateBuyCounts() {
        long currentTime = System.currentTimeMillis();

        // Check if 10 seconds (10,000 milliseconds) have passed since the last update
        if (currentTime - shopBuyer.lastUpdateTime >= 10000) {
            // Update the last update time
            shopBuyer.lastUpdateTime = currentTime;
            Paint.setStatus("Update buy counts");

            // Update buy amounts here
            switch (shopBuyer.itemToBuy) {
                case "Copper + Tin":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.COPPER_ORE_436, 0.93);
                    shopBuyer.boughtAmount2 = shopBuyer.boughtAmount2 + Inventory.count(ItemList.TIN_ORE_438, 0.93);
                    break;
                case "Copper ore":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.COPPER_ORE_436, 0.85);
                    break;
                case "Tin ore":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.TIN_ORE_438, 0.85);
                    break;
                case "Iron + Coal":
                    int ironAmount = Inventory.count(ItemList.IRON_ORE_440, 0.94);
                    int coalAmount = Inventory.count(ItemList.COAL_453, 0.85) - ironAmount;
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + ironAmount;
                    shopBuyer.boughtAmount2 = shopBuyer.boughtAmount2 + coalAmount;
                    break;
                case "Iron ore":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.IRON_ORE_440, 0.85);
                    break;
                case "Mithril + Coal":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.MITHRIL_ORE_447, 0.9);
                    shopBuyer.boughtAmount2 = shopBuyer.boughtAmount2 + Inventory.count(ItemList.COAL_453, 0.93);
                    break;
                case "Mithril ore":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.MITHRIL_ORE_447, 0.85);
                    break;
                case "Silver ore":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.SILVER_ORE_442, 0.85);
                    break;
                case "Gold ore":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.GOLD_ORE_444, 0.85);
                    break;
                case "Coal":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.COAL_453, 0.85);
                    break;
                case "Jug of Wine":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.JUG_OF_WINE_1993, 0.85);
                    break;
                case "Empty Jug Pack":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.EMPTY_JUG_PACK_20742, 0.85);
                    break;
                case "Empty bucket pack":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.EMPTY_BUCKET_PACK_22660, 0.85);
                    break;
                case "Pineapple":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.PINEAPPLE_2114, 0.85);
                    break;
                case "Bucket of slime":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.BUCKET_OF_SLIME_4286, 0.85);
                    break;
                case "Bucket of sand":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.BUCKET_OF_SAND_1783, 0.85);
                    break;
                case "Seaweed":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.SEAWEED_401, 0.85);
                    break;
                case "Soda ash":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.SODA_ASH_1781, 0.85);
                    break;
                case "Bucket of sand + Seaweed":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.BUCKET_OF_SAND_1783, 0.85);
                    shopBuyer.boughtAmount2 = shopBuyer.boughtAmount2 + Inventory.count(ItemList.SEAWEED_401, 0.85);
                    break;
                case "Bucket of sand + Soda ash":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.BUCKET_OF_SAND_1783, 0.85);
                    shopBuyer.boughtAmount2 = shopBuyer.boughtAmount2 + Inventory.count(ItemList.SODA_ASH_1781, 0.85);
                    break;
                case "Sand + Seaweed + Soda ash":
                    shopBuyer.boughtAmount1 = shopBuyer.boughtAmount1 + Inventory.count(ItemList.BUCKET_OF_SAND_1783, 0.85);
                    shopBuyer.boughtAmount2 = shopBuyer.boughtAmount2 + Inventory.count(ItemList.SEAWEED_401, 0.85);
                    shopBuyer.boughtAmount3 = shopBuyer.boughtAmount3 + Inventory.count(ItemList.SODA_ASH_1781, 0.85);
                    break;
                default:
                    // We do nothing here
            }
        }
        return null;
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
}
