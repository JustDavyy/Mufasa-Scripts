package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dBankCrafter.*;

public class Setup extends Task {

    @Override
    public boolean activate() {
        return !setupDone;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Initial Setup");
        Logger.log("Initial Setup");

        Paint.setStatus("Open inventory");
        GameTabs.openTab(UITabs.INVENTORY);

        Paint.setStatus("Find dynamic bank");
        findDynamicBank();

        Paint.setStatus("Open dynamic bank");
        openDynamicBank();

        if (Bank.isOpen()) {
            Paint.setStatus("Deposit inventory");
            Bank.tapDepositInventoryButton();

            Paint.setStatus("Setup bank at start");
            setupBanking();

            Paint.setStatus("Withdraw first items");
            withdrawFirstItems();

            Paint.setStatus("Close bank");
            closeBank();

            setupDone = true;
        } else {
            setupDone = false;
        }

        return false;
    }




    private void withdrawFirstItems() {
        switch (product) {
            case "Beer glass":
            case "Empty candle lantern":
            case "Empty oil lamp":
            case "Vial":
            case "Empty fishbowl":
            case "Unpowered orb":
            case "Lantern lens":
            case "Empty light orb":
            case "Amethyst Bolt tips":
            case "Amethyst Arrow tips":
            case "Amethyst Javelin heads":
            case "Amethyst Dart tips":
                bankItem1Count = Bank.stackSize(sourceItem);
                Bank.withdrawItem(sourceItem, 0.75);
                break;
            case "Uncut opal":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_OPAL_1625, Color.decode("#999989"));
                Bank.withdrawItem(ItemList.UNCUT_OPAL_1625, 0.75, Color.decode("#999989"));
                break;
            case "Uncut jade":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_JADE_1627, Color.decode("#8c9986"));
                Bank.withdrawItem(ItemList.UNCUT_JADE_1627, 0.75, Color.decode("#8c9986"));
                break;
            case "Uncut red topaz":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_RED_TOPAZ_1629, Color.decode("#ac0d60"));
                Bank.withdrawItem(ItemList.UNCUT_RED_TOPAZ_1629, 0.75, Color.decode("#ac0d60"));
                break;
            case "Uncut sapphire":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_SAPPHIRE_1623, Color.decode("#0a0d89"));
                Bank.withdrawItem(ItemList.UNCUT_SAPPHIRE_1623, 0.75, Color.decode("#0a0d89"));
                break;
            case "Uncut emerald":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_EMERALD_1621, Color.decode("#08800b"));
                Bank.withdrawItem(ItemList.UNCUT_EMERALD_1621, 0.75, Color.decode("#08800b"));
                break;
            case "Uncut ruby":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_RUBY_1619, Color.decode("#6e1006"));
                Bank.withdrawItem(ItemList.UNCUT_RUBY_1619, 0.75, Color.decode("#6e1006"));
                break;
            case "Uncut diamond":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_DIAMOND_1617, Color.decode("#afaeae"));
                Bank.withdrawItem(ItemList.UNCUT_DIAMOND_1617, 0.75, Color.decode("#afaeae"));
                break;
            case "Uncut dragonstone":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_DRAGONSTONE_1631, Color.decode("#580877"));
                Bank.withdrawItem(ItemList.UNCUT_DRAGONSTONE_1631, 0.75, Color.decode("#580877"));
                break;
            case "Uncut onyx":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_ONYX_6571, Color.decode("#0b0b1f"));
                Bank.withdrawItem(ItemList.UNCUT_ONYX_6571, 0.75, Color.decode("#0b0b1f"));
                break;
            case "Uncut zenyte":
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_ZENYTE_19496, Color.decode("#ac600d"));
                Bank.withdrawItem(ItemList.UNCUT_ZENYTE_19496, 0.75, Color.decode("#ac600d"));
                break;
            case "Air battlestaff":
                bankItem1Count = Bank.stackSize(ItemList.AIR_ORB_573, Color.decode("#c0c0c8"));
                Bank.withdrawItem(ItemList.AIR_ORB_573, 0.75, Color.decode("#c0c0c8"));
                Condition.sleep(generateDelay(150, 300));
                tempBankCountHolder = Bank.stackSize(ItemList.BATTLESTAFF_1391);
                if (tempBankCountHolder != 0 && tempBankCountHolder != -1 && tempBankCountHolder < bankItem1Count) {
                    bankItem1Count = tempBankCountHolder;
                }
                Bank.withdrawItem(ItemList.BATTLESTAFF_1391, 0.75);
                break;
            case "Water battlestaff":
                bankItem1Count = Bank.stackSize(ItemList.WATER_ORB_571, Color.decode("#1319e5"));
                Bank.withdrawItem(ItemList.WATER_ORB_571, 0.75, Color.decode("#1319e5"));
                Condition.sleep(generateDelay(150, 300));
                tempBankCountHolder = Bank.stackSize(ItemList.BATTLESTAFF_1391);
                if (tempBankCountHolder != 0 && tempBankCountHolder != -1 && tempBankCountHolder < bankItem1Count) {
                    bankItem1Count = tempBankCountHolder;
                }
                Bank.withdrawItem(ItemList.BATTLESTAFF_1391, 0.75);
                break;
            case "Earth battlestaff":
                bankItem1Count = Bank.stackSize(ItemList.EARTH_ORB_575, Color.decode("#8c6047"));
                Bank.withdrawItem(ItemList.EARTH_ORB_575, 0.75, Color.decode("#8c6047"));
                Condition.sleep(generateDelay(150, 300));
                tempBankCountHolder = Bank.stackSize(ItemList.BATTLESTAFF_1391);
                if (tempBankCountHolder != 0 && tempBankCountHolder != -1 && tempBankCountHolder < bankItem1Count) {
                    bankItem1Count = tempBankCountHolder;
                }
                Bank.withdrawItem(ItemList.BATTLESTAFF_1391, 0.75);
                break;
            case "Fire battlestaff":
                bankItem1Count = Bank.stackSize(ItemList.FIRE_ORB_569, Color.decode("#ee2713"));
                Bank.withdrawItem(ItemList.FIRE_ORB_569, 0.75, Color.decode("#ee2713"));
                Condition.sleep(generateDelay(150, 300));
                tempBankCountHolder = Bank.stackSize(ItemList.BATTLESTAFF_1391);
                if (tempBankCountHolder != 0 && tempBankCountHolder != -1 && tempBankCountHolder < bankItem1Count) {
                    bankItem1Count = tempBankCountHolder;
                }
                Bank.withdrawItem(ItemList.BATTLESTAFF_1391, 0.75);
                break;
            case "Leather gloves":
            case "Leather boots":
            case "Leather cowl":
            case "Leather vambraces":
            case "Leather body":
            case "Leather chaps":
            case "Coif":
                bankItem1Count = Bank.stackSize(ItemList.LEATHER_1741, Color.decode("#3d3904"));
                Bank.withdrawItem(ItemList.LEATHER_1741, 0.75, Color.decode("#3d3904"));
                break;
            case "Hardleather body":
                bankItem1Count = Bank.stackSize(ItemList.HARD_LEATHER_1743, Color.decode("#2e2717"));
                Bank.withdrawItem(ItemList.HARD_LEATHER_1743, 0.75, Color.decode("#2e2717"));
                break;
            case "Green d'hide vambraces":
            case "Green d'hide chaps":
            case "Green d'hide body":
                bankItem1Count = Bank.stackSize(ItemList.GREEN_DRAGON_LEATHER_1745, Color.decode("#043d04"));
                Bank.withdrawItem(ItemList.GREEN_DRAGON_LEATHER_1745, 0.75, Color.decode("#043d04"));
                break;
            case "Blue d'hide vambraces":
            case "Blue d'hide chaps":
            case "Blue d'hide body":
                bankItem1Count = Bank.stackSize(ItemList.BLUE_DRAGON_LEATHER_2505, Color.decode("#040751"));
                Bank.withdrawItem(ItemList.BLUE_DRAGON_LEATHER_2505, 0.75, Color.decode("#040751"));
                break;
            case "Red d'hide vambraces":
            case "Red d'hide chaps":
            case "Red d'hide body":
                bankItem1Count = Bank.stackSize(ItemList.RED_DRAGON_LEATHER_2507, Color.decode("#3d0704"));
                Bank.withdrawItem(ItemList.RED_DRAGON_LEATHER_2507, 0.75, Color.decode("#3d0704"));
                break;
            case "Black d'hide vambraces":
            case "Black d'hide chaps":
            case "Black d'hide body":
                bankItem1Count = Bank.stackSize(ItemList.BLACK_DRAGON_LEATHER_2509, Color.decode("#201f1f"));
                Bank.withdrawItem(ItemList.BLACK_DRAGON_LEATHER_2509, 0.75, Color.decode("#201f1f"));
                break;
            default:
                Logger.log("Unknown product: " + product + " stopping script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(2000);
                }
                Logout.logout();
                Script.stop();
        }

        // Wait for a small bit
        Condition.sleep(generateDelay(125, 250));
    }

    private void findDynamicBank() {
        if (bankloc == null) {
            bankloc = Bank.setupDynamicBank();

            if (bankloc == null) {
                Logger.debugLog("Could not find a dynamic bank location we are in, logging out and aborting script.");
                Logout.logout();
                Script.stop();
            } else {
                Logger.log("We're located at: " + bankloc + ".");
            }
        }
    }

    private void openDynamicBank() {
        Condition.sleep(generateDelay(1750, 2500));
        Bank.open(bankloc);
    }

    private void setupBanking() {
        // Withdraw out tool first
        withdrawToolSelection();

        // Set quantity
        setQuantity();

        // Select the correct bank tab if needed
        if (!Bank.isSelectedBankTab(banktab)) {
            Logger.log("Opening bank tab " + banktab);
            Bank.openTab(banktab);
            Condition.sleep(generateDelay(1200, 1800));
        }
    }

    private void withdrawToolSelection() {
        switch (activity) {
            case "Glassblowing":
                withdrawTool(ItemList.GLASSBLOWING_PIPE_1785, "Glassblowing", null, "1");
                break;
            case "Gemcutting":
            case "AmethystCutting":
                withdrawTool(ItemList.CHISEL_1755, "Chisel", null, "1");
                break;
            case "HideCrafting":
                withdrawTool(ItemList.NEEDLE_1733, "Needle", null, "1");
                Bank.openTab(0);
                Condition.sleep(generateDelay(1250, 2000));
                withdrawTool(ItemList.THREAD_1734, "Thread", null, "All");
                break;
            case "StaffCrafting":
                Logger.debugLog("No tools needed for creating Battlestaves.");
                break;
            default:
                Logger.debugLog("Unknown activity (inside withdrawToolSelection): " + activity + " aborting script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(generateDelay(1200, 1800));
                }
                Logout.logout();
                Script.stop();
        }
    }

    private void setQuantity() {
        switch (activity) {
            case "Glassblowing":
            case "Gemcutting":
            case "AmethystCutting":
            case "HideCrafting":
                if (!Bank.isSelectedQuantityAllButton()) {
                    Bank.tapQuantityAllButton();
                }
                break;
            case "StaffCrafting":
                Bank.setCustomQuantity(14);
                break;
            default:
                Logger.debugLog("Unknown activity (inside setQuantity): " + activity + " aborting script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(generateDelay(1200, 1800));
                }
                Logout.logout();
                Script.stop();
        }
    }

    private void withdrawTool(int itemId, String searchString, Color searchColor, String quantity) {
        Logger.debugLog("Withdrawing tool " + itemId + " from the bank.");

        // Set quantity based on the given quantity string
        if ("All".equals(quantity)) {
            Paint.setStatus("Set quantity all");
            Logger.debugLog("Set quantity all");
            // Select quantity all first
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
                Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 12);
            }
        } else if ("5".equals(quantity)) {
            Paint.setStatus("Set quantity 5");
            Logger.debugLog("Set quantity 5");
            // Select quantity 5 first
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(() -> Bank.isSelectedQuantity5Button(), 200, 12);
            }
        } else if ("10".equals(quantity)) {
            Paint.setStatus("Set quantity 10");
            Logger.debugLog("Set quantity 10");
            // Select quantity 10 first
            if (!Bank.isSelectedQuantity10Button()) {
                Bank.tapQuantity10Button();
                Condition.wait(() -> Bank.isSelectedQuantity10Button(), 200, 12);
            }
        } else {
            Paint.setStatus("Set quantity 1");
            Logger.debugLog("Set quantity 1");
            // Select quantity 1 first
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
            }
        }

        Paint.setStatus("Enter search mode");
        Logger.debugLog("Entering bank search mode");
        // Enter search mode
        Bank.tapSearchButton();
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Condition.sleep(generateDelay(450, 600));

        // Type our search string
        Paint.setStatus("Type " + searchString);
        Logger.debugLog("Typing: " + searchString);
        for (char c : searchString.toCharArray()) {
            Client.sendKeystroke(String.valueOf(Character.toUpperCase(c)));
        }

        // Wait for a bit for results to be visible
        Condition.sleep(generateDelay(1300, 1700));

        Paint.setStatus("Withdraw tool");
        Logger.debugLog("Withdrawing tool");
        // Withdraw our item
        if (searchColor != null) {
            Bank.withdrawItem(itemId, 0.7, searchColor);
        } else {
            Bank.withdrawItem(itemId, 0.7);
        }
        Condition.wait(() -> Inventory.contains(itemId, 0.7), 100, 35);

        // Close searchbox again
        Paint.setStatus("Close search box");
        Logger.debugLog("Closing search box");
        Client.sendKeystroke("enter");
        Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 30);

        // Check if we actually have the tool
        if (!Inventory.contains(itemId, 0.70)) {
            Logger.log("No tool (" + itemId + ") found in inventory, stopping the script.");
            Bank.close();
            if (Bank.isOpen()) {
                Bank.close();
            }
            Logout.logout();
            Script.stop();
        }
    }

    private void closeBank() {
        // Close the bank
        Bank.close();
        Condition.sleep(generateDelay(1000, 1500));

        if (Bank.isOpen()) {
            Bank.close();
        }
    }
}