package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static helpers.Interfaces.*;
import static main.dMoltenMaestro.*;

public class Bank extends Task {

    private static long currentTime = System.currentTimeMillis();
    private static double elapsedTimeInHours;
    private static double itemsPerHour;
    private static String itemStatString = "Empty";

    @Override
    public boolean activate() {
        if (resourceItemID1 != -1 && resourceItemID2 != -1) {
            if ("Molten glass".equals(resource)) {
                return !Inventory.contains(resourceItemID1, 0.92) || !Inventory.contains(resourceItemID2, 0.92);
            } else {
                return !checkInventColor(resourceItemID1) || !checkInventColor(resourceItemID2);
            }
        } else if (resourceItemID1 != -1) {
            return !Inventory.contains(resourceItemID1, 0.8);
        }

        return false;
    }

    @Override
    public boolean execute() {

        Paint.setStatus("Bank");
        Logger.log("Banking.");

        if (retrycount >= 2) {
            Logger.log("Missing resources in the inventory after 2 banking attempts. Assuming we ran out of resources.");
            Logger.log("Logging out and stopping script!");
            if (Bank.isOpen()) {
                Bank.close();
            }

            Logout.logout();
            Script.stop();
        } else if (retrycount >= 1) {
            Logger.log("Missing resources in the inventory after " + retrycount + " attempts. Retrying!");
        }

        if (!Bank.isOpen()) {
            openBank();
        }

        if (Bank.isBankPinNeeded()) {
            Bank.enterBankPin();
        }

        updatePaintBar();

        setBankDepositStatus();
        if ("Molten glass".equals(resource)) {
            Bank.tapDepositInventoryButton();
        } else {
            Inventory.tapItem(resultItemID, 0.8);
        }
        Condition.sleep(generateDelay(150, 300));

        if (!Bank.isSelectedBankTab(banktab)) {
            Paint.setStatus("Open tab " + banktab);
            Bank.openTab(banktab);
            Condition.wait(() -> Bank.isSelectedBankTab(banktab), 100, 30);
            Condition.sleep(generateDelay(150, 300));
        }

        // LOGIC HERE TO CALL THE RIGHT METHOD BASED ON SINGLE ITEM NEEDED OR DOUBLE
        // Also account for the bank stack sizes to use cached positions or not
        if (resourceItemID1 != -1 && resourceItemID2 != -1) {
            if (bankItem1Count <= 30 || bankItem2Count <= 30) {
                Logger.debugLog("Bank item (1 or 2) count is 30 or below, using non-cached bank withdraw method instead.");
                withdrawDoubleItems(false);
            } else {
                withdrawDoubleItems(true);
            }
            Condition.wait(() -> Inventory.contains(resourceItemID1, 0.8) & Inventory.contains(resourceItemID2, 0.8), 200, 30);
        } else if (resourceItemID1 != -1) {
            if (bankItem1Count <= 30) {
                Logger.debugLog("Bank item 1 count is 30 or below, using non-cached bank withdraw method instead.");
                withdrawSingleItem(false);
            } else {
                withdrawSingleItem(true);
            }
            Condition.wait(() -> Inventory.contains(resourceItemID1, 0.8), 200, 30);
        }

        Paint.setStatus("Close bank");
        Bank.close();
        Condition.wait(() -> !Bank.isOpen(),250,20);
        Condition.sleep(generateDelay(250, 400));

        retrycount++;
        Logger.debugLog("Set retry count to: " + retrycount);

        return false;
    }

    public void withdrawSingleItem(boolean useCache) {
        if (!Bank.isSelectedQuantityAllButton()) {
            Bank.tapQuantityAllButton();
        }
        setBankWithdrawStatus(1);
        if (resourceItemID1 == ItemList.STEEL_BAR_2353) {
            Bank.withdrawItem(ItemList.STEEL_BAR_2353, useCache, 0.8, Color.decode("#675e5d"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.STEEL_BAR_2353, Color.decode("#675e5d"));
        } else if (resourceItemID1 == ItemList.SILVER_BAR_2355) {
            Bank.withdrawItem(ItemList.SILVER_BAR_2355, useCache,0.8, Color.decode("#7a7b86"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.SILVER_BAR_2355, Color.decode("#7a7b86"));
        } else if (resourceItemID1 == ItemList.GOLD_BAR_2357) {
            Bank.withdrawItem(ItemList.GOLD_BAR_2357, useCache,0.8, Color.decode("#a5810c"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.GOLD_BAR_2357, Color.decode("#a5810c"));
        } else if (resourceItemID1 == ItemList.IRON_ORE_440) {
            Bank.withdrawItem(ItemList.IRON_ORE_440, useCache,0.8, Color.decode("#3c2116"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.IRON_ORE_440, Color.decode("#3c2116"));
        } else if (resourceItemID1 == ItemList.SILVER_ORE_442) {
            Bank.withdrawItem(ItemList.SILVER_ORE_442, useCache,0.8, Color.decode("#9696a3"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.SILVER_ORE_442, Color.decode("#9696a3"));
        } else if (resourceItemID1 == ItemList.GOLD_ORE_444) {
            Bank.withdrawItem(ItemList.GOLD_ORE_444, useCache,0.8, Color.decode("#cda110"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.GOLD_ORE_444, Color.decode("#cda110"));
        } else {
            Bank.withdrawItem(resourceItemID1, useCache,0.8);
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(resourceItemID1);
        }
        Condition.sleep(generateDelay(150, 300));
        Logger.debugLog("Previous bank items of " + resourceItemID1 + " left in bank: " + previousBankItem1Count);
        Logger.debugLog("Items left of " + resourceItemID1 + " in bank: " + bankItem1Count);
    }

    public void withdrawDoubleItems(boolean useCache) {
        if (!Bank.isSelectedQuantityCustomButton()) {
            Bank.tapQuantityCustomButton();
            Condition.sleep(generateDelay(150, 250));
        }
        // Withdraw items using our custom set quantities first
        setBankWithdrawStatus(2);
        if (resourceItemID2 == ItemList.SILVER_BAR_2355) {
            Bank.withdrawItem(ItemList.SILVER_BAR_2355, useCache, 0.8, Color.decode("#7a7b86"));
            updatePreviousBankItemCount(2);
            bankItem2Count = Bank.stackSize(ItemList.SILVER_BAR_2355, Color.decode("#7a7b86"));
        } else if (resourceItemID2 == ItemList.GOLD_BAR_2357) {
            Bank.withdrawItem(ItemList.GOLD_BAR_2357, useCache, 0.8, Color.decode("#a5810c"));
            updatePreviousBankItemCount(2);
            bankItem2Count = Bank.stackSize(ItemList.GOLD_BAR_2357, Color.decode("#a5810c"));
        } else if (resourceItemID2 == ItemList.COAL_453) {
            Bank.withdrawItem(ItemList.COAL_453, useCache, 0.8, Color.decode("#2d2d1c"));
            updatePreviousBankItemCount(2);
            bankItem2Count = Bank.stackSize(ItemList.COAL_453, Color.decode("#2d2d1c"));
        } else if (resourceItemID2 == ItemList.TIN_ORE_438) {
            Bank.withdrawItem(ItemList.TIN_ORE_438, useCache, 0.8, Color.decode("#7b7170"));
            updatePreviousBankItemCount(2);
            bankItem2Count = Bank.stackSize(ItemList.TIN_ORE_438, Color.decode("#7b7170"));
        } else {
            Bank.withdrawItem(resourceItemID2, useCache, 0.8);
            updatePreviousBankItemCount(2);
            bankItem2Count = Bank.stackSize(resourceItemID2);
        }
        Condition.sleep(generateDelay(150, 300));
        Logger.debugLog("Previous bank items of " + resourceItemID2 + " left in bank: " + previousBankItem2Count);
        Logger.debugLog("Items left of " + resourceItemID2 + " in bank: " + bankItem2Count);

        // Then withdraw the rest (this fills up the inventory)
        setBankWithdrawStatus(1);
        if (resourceItemID1 == ItemList.SILVER_BAR_2355) {
            Bank.withdrawItem(ItemList.SILVER_BAR_2355, useCache, 0.8, Color.decode("#7a7b86"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.SILVER_BAR_2355, Color.decode("#7a7b86"));
        } else if (resourceItemID1 == ItemList.GOLD_BAR_2357) {
            Bank.withdrawItem(ItemList.GOLD_BAR_2357, useCache, 0.8, Color.decode("#a5810c"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.GOLD_BAR_2357, Color.decode("#a5810c"));
        } else if (resourceItemID1 == ItemList.COPPER_ORE_436) {
            Bank.withdrawItem(ItemList.COPPER_ORE_436, useCache, 0.8, Color.decode("#d56e29"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.COPPER_ORE_436, Color.decode("#d56e29"));
        } else if (resourceItemID1 == ItemList.IRON_ORE_440) {
            Bank.withdrawItem(ItemList.IRON_ORE_440, useCache, 0.8, Color.decode("#402218"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.IRON_ORE_440, Color.decode("#402218"));
        } else if (resourceItemID1 == ItemList.MITHRIL_ORE_447) {
            Bank.withdrawItem(ItemList.MITHRIL_ORE_447, useCache, 0.8, Color.decode("#3c3c60"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.MITHRIL_ORE_447, Color.decode("#3c3c60"));
        } else if (resourceItemID1 == ItemList.ADAMANTITE_ORE_449) {
            Bank.withdrawItem(ItemList.ADAMANTITE_ORE_449, useCache, 0.8, Color.decode("#3d513e"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.ADAMANTITE_ORE_449, Color.decode("#3d513e"));
        } else if (resourceItemID1 == ItemList.RUNITE_ORE_451) {
            Bank.withdrawItem(ItemList.RUNITE_ORE_451, useCache, 0.8, Color.decode("#415c68"));
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(ItemList.RUNITE_ORE_451, Color.decode("#415c68"));
        } else {
            Bank.withdrawItem(resourceItemID1, useCache, 0.8);
            updatePreviousBankItemCount(1);
            bankItem1Count = Bank.stackSize(resourceItemID1);
        }
        Condition.sleep(generateDelay(150, 300));
        Logger.debugLog("Previous bank items of " + resourceItemID1 + " left in bank: " + previousBankItem1Count);
        Logger.debugLog("Items left of " + resourceItemID1 + " in bank: " + bankItem1Count);
    }

    public void openBank() {
        Paint.setStatus("Open bank");
        switch (location) {
            case "Edgeville":
                if (Player.tileEquals(currentLocation, edgeFurnaceTile)) {
                    Client.tap(edgeBankRect);
                    currentLocation = edgeBankTile;
                } else if (Player.tileEquals(currentLocation, edgeBankTile)) {
                    Client.tap(openEdgeBankONCE);
                    currentLocation = edgeBankTile;
                } else {
                    if (!Walker.isReachable(edgeBankTile)) {
                        Walker.webWalk(edgeBankTile);
                    }
                    Walker.step(edgeBankTile);
                    Client.tap(openEdgeBankONCE);
                    currentLocation = edgeBankTile;
                }
                Condition.wait(() -> Bank.isOpen(), 100, 175);
                break;
            case "Mount Karuulm":
                if (Player.tileEquals(currentLocation, karuulmFurnaceTile)) {
                    Walker.step(karuulmBankTile);
                    Client.tap(karuulmBankRect);
                    currentLocation = karuulmBankTile;
                } else if (Player.tileEquals(currentLocation, karuulmBankTile)) {
                    Client.tap(karuulmBankRect);
                    currentLocation = karuulmBankTile;
                } else {
                    if (!Walker.isReachable(karuulmBankTile)) {
                        Walker.webWalk(karuulmBankTile);
                    }
                    Walker.step(karuulmBankTile);
                    Client.tap(karuulmBankRect);
                    currentLocation = karuulmBankTile;
                }
                Condition.wait(() -> Bank.isOpen(), 100, 175);
                break;
            case "Neitiznot":
                if (Player.tileEquals(currentLocation, neitFurnaceTile)) {
                    Client.tap(neitBankRect);
                    currentLocation = neitBankTile;
                } else if (Player.tileEquals(currentLocation, neitBankTile)) {
                    Client.tap(openNeitBankONCE);
                    currentLocation = neitBankTile;
                } else {
                    if (!Walker.isReachable(neitBankTile)) {
                        Walker.webWalk(neitBankTile);
                    }
                    Walker.step(neitBankTile);
                    Client.tap(openNeitBankONCE);
                    currentLocation = neitBankTile;
                }
                Condition.wait(() -> Bank.isOpen(), 100, 175);
                break;
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
        }
    }

    public static void updatePaintBar() {
        Paint.setStatus("Update paint count");
        switch (resource) {
            case "Bronze bar":
            case "Iron bar":
            case "Silver bar":
            case "Steel bar":
            case "Gold bar":
            case "Mithril bar":
            case "Adamantite bar":
            case "Runite bar":
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
            case "Slayer":
            case "Molten glass":
                processCount = processCount + Inventory.count(resultItemID, 0.8);
                Paint.updateBox(productIndex, processCount);
                break;
            case "Cannonball":
                processCount = processCount + Inventory.stackSize(ItemList.CANNONBALL_2);
                Paint.updateBox(productIndex, processCount);
                break;
            default:
                Logger.debugLog("Invalid resource (not in switch logic): " + resource);
                break;
        }

        // Time calculations
        currentTime = System.currentTimeMillis();
        elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);
        itemsPerHour = processCount/ elapsedTimeInHours;

        // Format items per hour with dot as thousand separator and no decimals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(','); // Set the decimal separator to comma
        DecimalFormat formatItems = new DecimalFormat("#,###", symbols);
        String itemsPerHourFormatted = formatItems.format(itemsPerHour);

        // Update the statistics label
        String statistics = String.format("%s/hr: %s", itemStatString, itemsPerHourFormatted);
        Paint.setStatistic(statistics);
    }

    private void setBankDepositStatus() {
        switch (resource) {
            case "Bronze bar":
            case "Iron bar":
            case "Steel bar":
            case "Mithril bar":
            case "Adamantite bar":
            case "Runite bar":
            case "Cannonball":
            case "Molten glass":
                Paint.setStatus("Deposit " + resource);
                if (itemStatString.equals("Empty")) {
                    itemStatString = resource;
                }
                break;
            case "Gold bar":
            case "Silver bar":
                if (!productType.equals("None")) {
                    String resourceString = resource.replace(" bar", "") + " " + productType;
                    if (itemStatString.equals("Empty")) {
                        itemStatString = resourceString;
                    }
                    Paint.setStatus("Deposit " + resourceString);
                } else {
                    if (itemStatString.equals("Empty")) {
                        itemStatString = resource;
                    }
                    Paint.setStatus("Deposit " + resource);
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
            case "Slayer":
                String resourceString = resource + " " + productType;
                if (itemStatString.equals("Empty")) {
                    itemStatString = resourceString;
                }
                Paint.setStatus("Deposit " + resourceString);
                break;
            default:
                Logger.debugLog("Invalid resource (not in switch logic): " + resource);
                break;
        }
    }

    private void setBankWithdrawStatus(int resourceNumber) {
        switch (resource) {
            case "Bronze bar":
                if (resourceNumber == 1) {
                    Paint.setStatus("Withdraw Copper ore");
                } else {
                    Paint.setStatus("Withdraw Tin ore");
                }
                break;
            case "Iron bar":
                Paint.setStatus("Withdraw Iron ore");
                break;
            case "Silver bar":
                Paint.setStatus("Withdraw Silver ore");
                break;
            case "Steel bar":
                if (resourceNumber == 1) {
                    Paint.setStatus("Withdraw Iron ore");
                } else {
                    Paint.setStatus("Withdraw Coal");
                }
                break;
            case "Gold bar":
                Paint.setStatus("Withdraw Gold ore");
                break;
            case "Mithril bar":
                if (resourceNumber == 1) {
                    Paint.setStatus("Withdraw Mithril ore");
                } else {
                    Paint.setStatus("Withdraw Coal");
                }
                break;
            case "Adamantite bar":
                if (resourceNumber == 1) {
                    Paint.setStatus("Withdraw Adamantite ore");
                } else {
                    Paint.setStatus("Withdraw Coal");
                }
                break;
            case "Runite bar":
                if (resourceNumber == 1) {
                    Paint.setStatus("Withdraw Runite ore");
                } else {
                    Paint.setStatus("Withdraw Coal");
                }
                break;
            case "Cannonball":
                Paint.setStatus("Withdraw Steel bar");
                break;
            case "Molten glass":
                if (resourceNumber == 1) {
                    Paint.setStatus("Withdraw Bucket of sand");
                } else {
                    Paint.setStatus("Withdraw Soda ash");
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
                if (resourceNumber == 1) {
                    Paint.setStatus("Withdraw " + resource);
                } else {
                    Paint.setStatus("Withdraw Gold bar");
                }
                break;
            case "Slayer":
                if (resourceNumber == 1) {
                    Paint.setStatus("Withdraw Enchanted gem");
                } else {
                    Paint.setStatus("Withdraw Gold bar");
                }
                break;
            default:
                Logger.debugLog("Invalid resource (not in switch logic): " + resource);
                break;
        }
    }
}