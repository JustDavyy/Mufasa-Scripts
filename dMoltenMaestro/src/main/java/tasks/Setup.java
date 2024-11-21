package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dMoltenMaestro.*;

public class Setup extends Task {

    @Override
    public boolean activate() {
        return !setupDone;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Initial Setup");
        Logger.debugLog("Starting initialSetup.");

        setupZoom();

        GameTabs.openTab(UITabs.INVENTORY);

        moveToBank();
        Condition.sleep(generateDelay(1250, 2000));

        currentLocation = Walker.getPlayerPosition();

        openBank();
        Condition.sleep(generateDelay(1250, 2000));

        if (Bank.isOpen()) {
            // Deposit inventory
            Bank.tapDepositInventoryButton();
            Condition.sleep(generateDelay(700, 1000));

            // Set quantity to 1
            Bank.tapQuantity1Button();
            Condition.sleep(generateDelay(300, 500));

            // Grab the mould we need (if any)
            if (resource.equals("Cannonball")) {
                Logger.debugLog("Withdrawing ammo mould from the bank.");
                Paint.setStatus("Withdraw ammo mould");
                searchAndType("ammo mould");
                Bank.withdrawItem(ItemList.DOUBLE_AMMO_MOULD_27012, 0.8);
                Condition.wait(() -> Inventory.contains(ItemList.DOUBLE_AMMO_MOULD_27012, 0.8), 150, 20);
                if (!Inventory.contains(ItemList.DOUBLE_AMMO_MOULD_27012, 0.8)) {
                    Logger.debugLog("Failed to withdraw double ammo mould, trying single instead.");
                    Bank.withdrawItem(ItemList.AMMO_MOULD_4, 0.8);
                    Condition.wait(() -> Inventory.contains(ItemList.AMMO_MOULD_4, 0.8), 150, 20);

                    if (Inventory.contains(ItemList.AMMO_MOULD_4, 0.8)) {
                        Logger.debugLog("Ammo mould found in inventory.");
                        closeSearchInterface();
                    } else {
                        Logger.debugLog("Failed to withdraw any ammo mould, stopping scripts.");
                        Bank.close();
                        Condition.sleep(generateDelay(1250, 2000));
                        Logout.logout();
                        Script.stop();
                    }
                } else {
                    Logger.debugLog("Double ammo mould found in inventory.");
                    closeSearchInterface();
                }
            } else {
                switch (productType) {
                    case "Ring":
                        searchWithdrawAndLog(ItemList.RING_MOULD_1592, "ring mould");
                        break;
                    case "Necklace":
                        searchWithdrawAndLog(ItemList.NECKLACE_MOULD_1597, "necklace mould");
                        break;
                    case "Bracelet":
                        searchWithdrawAndLog(ItemList.BRACELET_MOULD_11065, "bracelet mould");
                        break;
                    case "Amulet":
                        searchWithdrawAndLog(ItemList.AMULET_MOULD_1595, "amulet mould");
                        break;
                    case "Tiara":
                        searchWithdrawAndLog(ItemList.TIARA_MOULD_5523, "tiara mould");
                        break;
                    default:
                        Logger.debugLog("No need to withdraw mould, productType is None.");
                }
            }
        }

        // Set quantity if needed
        switch (resource) {
            case "Bronze bar":
            case "Molten glass":
                setCustomQty("14");
                break;
            case "Steel bar":
                setCustomQty("18");
                break;
            case "Iron bar":
            case "Silver bar":
            case "Gold bar":
            case "Cannonball":
                Bank.tapQuantityAllButton();
                break;
            case "Mithril bar":
                setCustomQty("20");
                break;
            case "Adamantite bar":
            case "Runite bar":
                setCustomQty("24");
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
                setCustomQty("13");
                break;
            default:
                Logger.debugLog("Invalid resource (not in switch logic): " + resource);
                break;
        }

        Bank.close();
        Condition.sleep(generateDelay(1200, 1500));
        if (Bank.isOpen()) {
            Bank.close();
            Condition.sleep(generateDelay(1200, 1500));
        }

        setupDone = true;
        return true;
    }

    private void setupZoom() {
        Paint.setStatus("Set zoom");
        if (!GameTabs.isTabOpen(UITabs.SETTINGS)) {
            GameTabs.openTab(UITabs.SETTINGS);
            Condition.sleep(1000);
        }
        switch (location) {
            case "Edgeville":
                Game.setZoom("1");
                break;
            case "Mount Karuulm":
                Game.setZoom("3");
                break;
            case "Neitiznot":
                Game.setZoom("2");
                break;
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
        }
    }

    private void moveToBank() {
        switch (location) {
            case "Edgeville":
                if (Walker.isReachable(edgeBankTile)) {
                    Walker.step(edgeBankTile);
                } else {
                    Walker.webWalk(edgeBankTile);
                    Condition.sleep(1500);
                    Walker.step(edgeBankTile);
                }
                break;
            case "Mount Karuulm":
                if (Walker.isReachable(karuulmBankTile)) {
                    Walker.step(karuulmBankTile);
                } else {
                    Walker.webWalk(karuulmBankTile);
                    Condition.sleep(1500);
                    Walker.step(karuulmBankTile);
                }
                break;
            case "Neitiznot":
                if (Walker.isReachable(neitBankTile)) {
                    Walker.step(neitBankTile);
                } else {
                    Walker.webWalk(neitBankTile);
                    Condition.sleep(1500);
                    Walker.step(neitBankTile);
                }
                break;
            default:
                Logger.log("Unknown location: " + location);
        }
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

    private void searchAndType(String stringToType) {
        Paint.setStatus("Open bank search menu");
        Logger.debugLog("Open bank search menu");
        Bank.tapSearchButton();
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Condition.sleep(generateDelay(450, 600));

        if (Chatbox.isMakeMenuVisible()) {
            Paint.setStatus("Typing: " + stringToType);
            Logger.debugLog("Typing: " + stringToType);
            for (char c : stringToType.toCharArray()) {
                String keycode;
                if (c == ' ') {
                    keycode = "space";
                } else {
                    keycode = String.valueOf(c);
                }
                Client.sendKeystroke(keycode);
                Condition.sleep(generateDelay(20, 40));
            }
        }
    }

    private void setCustomQty(String quantity) {
        // Default to 1
        Bank.tapQuantity1Button();
        Condition.sleep(generateDelay(750, 1000));

        // Set custom quantity
        Rectangle customQtyBtn = Bank.findQuantityCustomButton();
        if (customQtyBtn != null) {
            Paint.setStatus("Set custom quantity " + quantity);
            Logger.debugLog("Setting quantity to x" + quantity);

            // Pick a random point within the Rectangle
            int randomX = customQtyBtn.x + (int) (Math.random() * customQtyBtn.width);
            int randomY = customQtyBtn.y + (int) (Math.random() * customQtyBtn.height);

            // Perform a longPress at the random point
            Client.longPress(randomX, randomY);
            Condition.sleep(generateDelay(350, 600));

            // Randomize the X offset (-25 to +25) and Y offset (25 to 30 pixels below the longPress)
            int offsetX = -25 + (int) (Math.random() * 51); // Random value between -25 and +25
            int offsetY = 25 + (int) (Math.random() * 6);  // Random value between 25 and 30

            // Calculate the tap point with the randomized offsets
            Point tapPoint = new Point(randomX + offsetX, randomY + offsetY);
            Client.tap(tapPoint);

            Condition.sleep(generateDelay(700, 1200));
            // Type our quantity given here
            for (char c : quantity.toCharArray()) {
                String keycode;
                if (c == ' ') {
                    keycode = "space";
                } else {
                    keycode = String.valueOf(c);
                }
                Client.sendKeystroke(keycode);
                Condition.sleep(generateDelay(20, 40));
            }
            Client.sendKeystroke("enter");

            Condition.wait(() -> Bank.isSelectedQuantityCustomButton(), 200, 12);
        } else {
            Logger.debugLog("Could not locate the custom quantity button.");
        }
    }

    private void closeSearchInterface() {
        Paint.setStatus("Close search interface");
        Logger.debugLog("Closing search interface.");
        // Close search box again
        Client.sendKeystroke("enter");
        Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 30);
    }

    private void searchWithdrawAndLog(int itemId, String logString) {
        Logger.debugLog("Withdrawing " + logString + " from the bank.");
        Paint.setStatus("Withdraw " + logString);
        searchAndType(logString);
        Bank.withdrawItem(itemId, 0.8);
        Condition.wait(() -> Inventory.contains(itemId, 0.8), 150, 20);
        if (!Inventory.contains(itemId, 0.8)) {
            Logger.debugLog("Failed to withdraw " + logString + ", stopping script.");
            Bank.close();
            Condition.sleep(generateDelay(1250, 2000));
            Logout.logout();
            Script.stop();
        } else {
            Logger.debugLog(logString + " found in inventory.");
            closeSearchInterface();
        }
    }
}
