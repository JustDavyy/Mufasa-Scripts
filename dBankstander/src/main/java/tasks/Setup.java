package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dBankstander.*;

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
                Bank.withdrawItem(ItemList.MOLTEN_GLASS_1775, 0.75);
                bankItem1Count = Bank.stackSize(ItemList.MOLTEN_GLASS_1775);
                break;
            case "Uncut opal":
                Bank.withdrawItem(ItemList.UNCUT_OPAL_1625, 0.75, Color.decode("#999989"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_OPAL_1625, Color.decode("#999989"));
                break;
            case "Uncut jade":
                Bank.withdrawItem(ItemList.UNCUT_JADE_1627, 0.75, Color.decode("#8c9986"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_JADE_1627, Color.decode("#8c9986"));
                break;
            case "Uncut red topaz":
                Bank.withdrawItem(ItemList.UNCUT_RED_TOPAZ_1629, 0.75, Color.decode("#ac0d60"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_RED_TOPAZ_1629, Color.decode("#ac0d60"));
                break;
            case "Uncut sapphire":
                Bank.withdrawItem(ItemList.UNCUT_SAPPHIRE_1623, 0.75, Color.decode("#0a0d89"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_SAPPHIRE_1623, Color.decode("#0a0d89"));
                break;
            case "Uncut emerald":
                Bank.withdrawItem(ItemList.UNCUT_EMERALD_1621, 0.75, Color.decode("#08800b"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_EMERALD_1621, Color.decode("#08800b"));
                break;
            case "Uncut ruby":
                Bank.withdrawItem(ItemList.UNCUT_RUBY_1619, 0.75, Color.decode("#6e1006"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_RUBY_1619, Color.decode("#6e1006"));
                break;
            case "Uncut diamond":
                Bank.withdrawItem(ItemList.UNCUT_DIAMOND_1617, 0.75, Color.decode("#afaeae"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_DIAMOND_1617, Color.decode("#afaeae"));
                break;
            case "Uncut dragonstone":
                Bank.withdrawItem(ItemList.UNCUT_DRAGONSTONE_1631, 0.75, Color.decode("#580877"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_DRAGONSTONE_1631, Color.decode("#580877"));
                break;
            case "Uncut onyx":
                Bank.withdrawItem(ItemList.UNCUT_ONYX_6571, 0.75, Color.decode("#0b0b1f"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_ONYX_6571, Color.decode("#0b0b1f"));
                break;
            case "Uncut zenyte":
                Bank.withdrawItem(ItemList.UNCUT_ZENYTE_19496, 0.75, Color.decode("#ac600d"));
                bankItem1Count = Bank.stackSize(ItemList.UNCUT_ZENYTE_19496, Color.decode("#ac600d"));
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
                withdrawTool(ItemList.GLASSBLOWING_PIPE_1785, "Glassblowing", null);
                break;
            case "Gemcutting":
                withdrawTool(ItemList.CHISEL_1755, "Chisel", null);
                break;
            default:
                Logger.debugLog("Unknown activity: " + activity + " aborting script.");
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
                Bank.tapQuantityAllButton();
                break;
            default:
                Logger.debugLog("Unknown activity: " + activity + " aborting script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(generateDelay(1200, 1800));
                }
                Logout.logout();
                Script.stop();
        }
    }

    private void withdrawTool(int itemId, String searchString, Color searchColor) {
        Logger.debugLog("Withdrawing tool " + itemId + " from the bank.");

        Paint.setStatus("Set quantity 1");
        Logger.debugLog("Set quantity 1");
        // Select quantity 1 first
        if (!Bank.isSelectedQuantity1Button()) {
            Bank.tapQuantity1Button();
            Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
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