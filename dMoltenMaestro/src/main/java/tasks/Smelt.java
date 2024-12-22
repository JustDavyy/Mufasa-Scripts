package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dMoltenMaestro.*;
import static main.dMoltenMaestro.location;

public class Smelt extends Task {
    private int lastUsedSlots = 0;
    private int currentUsedSlots = 0;

    @Override
    public boolean activate() {
        if (resourceItemID1 != -1 & resourceItemID2 != -1) {
            return Player.leveledUp() || checkInventColor(resourceItemID1) && checkInventColor(resourceItemID2);
        } else if (resourceItemID1 != -1) {
            return Player.leveledUp() || Inventory.contains(resourceItemID1, 0.8);
        }

        return false;
    }

    @Override
    public boolean execute() {

        if (retrycount != 0) {
            retrycount = 0;
            Logger.debugLog("Set retry count to: " + retrycount);
        }

        // Check if we have leveled up
        if (Player.leveledUp()) {
            // Re-initiate action here
            if ("Opal".equals(resource) || "Gold".equals(resource) || "Jade".equals(resource) ||
                    "Topaz".equals(resource) || "Sapphire".equals(resource) || "Emerald".equals(resource) ||
                    "Ruby".equals(resource) || "Diamond".equals(resource) || "Dragonstone".equals(resource) ||
                    "Onyx".equals(resource) || "Zenyte".equals(resource) ||
                    ("Silver bar".equals(resource) && !"None".equals(productType)) ||
                    ("Gold bar".equals(resource) && !"None".equals(productType))) {
                doCraftOption(true);
            } else {
                doSmeltOption(true);
            }

            lastProcessTime = System.currentTimeMillis(); // Update last process time
            return true;
        }

        // Check if we are already at the furnace otherwise move there
        if (!atFurnace()) {
            moveToFurnace();
        }

        if (atFurnace()) {
            if ("Molten glass".equals(resource)) {
                currentUsedSlots = Inventory.count(resultItemID, 0.8);
            } else {
                currentUsedSlots = Inventory.usedSlots();
            }

            // Check if an item has been processed in the last 10 seconds
            if (currentUsedSlots != lastUsedSlots) {
                lastProcessTime = System.currentTimeMillis(); // Update last process time
                lastUsedSlots = currentUsedSlots; // Update last used slots count
            }


            // Check if X seconds have passed without processing an item
            if (System.currentTimeMillis() - lastProcessTime > delayInMS) {
                Logger.debugLog("No item processed in the last " + delayInS + " seconds, attempting to re-smelt.");
                if ("Opal".equals(resource) || "Gold".equals(resource) || "Jade".equals(resource) ||
                        "Topaz".equals(resource) || "Sapphire".equals(resource) || "Emerald".equals(resource) ||
                        "Ruby".equals(resource) || "Diamond".equals(resource) || "Dragonstone".equals(resource) ||
                        "Onyx".equals(resource) || "Zenyte".equals(resource) ||
                        ("Silver bar".equals(resource) && !"None".equals(productType)) ||
                        ("Gold bar".equals(resource) && !"None".equals(productType))) {
                    doCraftOption(false);
                } else {
                    if ("Cannonball".equals(resource)) {
                        if (System.currentTimeMillis() - lastProcessTime > delayInMS) {
                            doSmeltOption(true);
                        }
                    } else {
                        doSmeltOption(true);
                    }
                }
                lastProcessTime = System.currentTimeMillis(); // Update last process time
            } else if (makeMenuOpen() || craftMenuOpen()) {
                if ("Opal".equals(resource) || "Gold".equals(resource) || "Jade".equals(resource) ||
                        "Topaz".equals(resource) || "Sapphire".equals(resource) || "Emerald".equals(resource) ||
                        "Ruby".equals(resource) || "Diamond".equals(resource) || "Dragonstone".equals(resource) ||
                        "Onyx".equals(resource) || "Zenyte".equals(resource) ||
                        ("Silver bar".equals(resource) && !"None".equals(productType)) ||
                        ("Gold bar".equals(resource) && !"None".equals(productType))) {
                    doCraftOption(false);
                } else {
                    doSmeltOption(false);
                }
                lastProcessTime = System.currentTimeMillis(); // Update last process time
            } else {
                if (Inventory.usedSlots() > 5) {
                    Paint.setStatus("Wait for interrupt");
                    Condition.sleep(2000);
                }
                return false;
            }
        } else {
            return false;
        }

        return true;
    }


    private boolean atFurnace() {
        switch (location) {
            case "Edgeville":
                if (Player.tileEquals(currentLocation, edgeFurnaceTile)) {
                    return true;
                } else {
                    currentLocation = Walker.getPlayerPosition();
                    return Player.tileEquals(currentLocation, edgeFurnaceTile);
                }
            case "Mount Karuulm":
                if (Player.tileEquals(currentLocation, karuulmFurnaceTile)) {
                    return true;
                } else {
                    currentLocation = Walker.getPlayerPosition();
                    return Player.tileEquals(currentLocation, karuulmFurnaceTile);
                }
            case "Neitiznot":
                if (Player.tileEquals(currentLocation, neitFurnaceTile)) {
                    return true;
                } else {
                    currentLocation = Walker.getPlayerPosition();
                    return Player.tileEquals(currentLocation, neitFurnaceTile);
                }
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
                return false;
        }
    }

    private void moveToFurnace() {
        switch (location) {
            case "Edgeville":
                if (Player.tileEquals(currentLocation, edgeFurnaceTile)) {
                    // No action needed, already at the furnace
                } else if (Player.tileEquals(currentLocation, edgeBankTile)) {
                    Client.tap(edgeFurnaceRect);
                    Condition.wait(() -> Player.atTile(edgeFurnaceTile), 100, 75);
                    currentLocation = edgeFurnaceTile;
                    Condition.wait(() -> makeMenuOpen() || craftMenuOpen(), 100, 50);
                } else {
                    if (!Walker.isReachable(edgeFurnaceTile)) {
                        Walker.webWalk(edgeFurnaceTile);
                    }
                    Walker.step(edgeFurnaceTile);
                }
                break;
            case "Mount Karuulm":
                if (Player.tileEquals(currentLocation, karuulmFurnaceTile)) {
                    // No action needed, already at the furnace
                } else {
                    if (!Walker.isReachable(karuulmFurnaceTile)) {
                        Walker.webWalk(karuulmFurnaceTile);
                        Condition.sleep(generateDelay(1500, 2500));
                    }
                    Walker.step(karuulmFurnaceTile);
                    Condition.wait(() -> Player.atTile(karuulmFurnaceTile), 100, 75);
                    currentLocation = karuulmFurnaceTile;
                    Client.tap(karuulmFurnaceRectAtFurnace);
                    Condition.wait(() -> makeMenuOpen() || craftMenuOpen(), 100, 50);
                }
                break;
            case "Neitiznot":
                if (Player.tileEquals(currentLocation, neitFurnaceTile)) {
                    // No action needed, already at the furnace
                } else if (Player.tileEquals(currentLocation, neitBankTile)) {
                    Client.tap(neitFurnaceRect);
                    Condition.wait(() -> Player.atTile(neitFurnaceTile), 100, 75);
                    currentLocation = neitFurnaceTile;
                    Condition.wait(() -> makeMenuOpen() || craftMenuOpen(), 100, 50);
                } else {
                    if (!Walker.isReachable(neitFurnaceTile)) {
                        Walker.webWalk(neitFurnaceTile);
                    }
                    Walker.step(neitFurnaceTile);
                }
                break;
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
                break;
        }

        lastProcessTime = System.currentTimeMillis(); // Update last process time
    }

    private void doSmeltOption(boolean tapFurnace) {
        setFurnActionStatus();
        if (tapFurnace) {
            switch (location) {
                case "Edgeville":
                    Client.tap(edgeFurnaceRectAtFurnace);
                    Condition.wait(this::makeMenuOpen, 100, 75);
                    break;
                case "Mount Karuulm":
                    Client.tap(karuulmFurnaceRectAtFurnace);
                    Condition.wait(this::makeMenuOpen, 100, 75);
                    break;
                case "Neitiznot":
                    Client.tap(neitFurnaceRectAtFurnace);
                    Condition.wait(this::makeMenuOpen, 100, 75);
                    break;
                default:
                    Logger.log("Unknown location: " + location);
                    Script.stop();
                    break;
            }
        }
        if (makeMenuOpen()) {
            switch (resource) {
                case "Bronze bar":
                    Client.sendKeystroke("1");
                    break;
                case "Iron bar":
                    Client.sendKeystroke("3");
                    break;
                case "Silver bar":
                    if (productType.equals("Tiara")) {
                        Client.sendKeystroke("space");
                    } else {
                        Client.sendKeystroke("4");
                    }
                    break;
                case "Steel bar":
                    Client.sendKeystroke("5");
                    break;
                case "Gold bar":
                    if (productType.equals("Tiara")) {
                        Client.sendKeystroke("space");
                    } else {
                        Client.sendKeystroke("6");
                    }
                    break;
                case "Mithril bar":
                    Client.sendKeystroke("7");
                    break;
                case "Adamantite bar":
                    Client.sendKeystroke("8");
                    break;
                case "Runite bar":
                    Client.sendKeystroke("9");
                    break;
                case "Slayer":
                case "Molten glass":
                case "Cannonball":
                    Client.sendKeystroke("space");
                    break;
                default:
                    Logger.debugLog("Invalid resource (not in switch logic): " + resource);
                    break;
            }
        }

        Condition.wait(() -> !makeMenuOpen(), 100, 30);
    }

    private void doCraftOption(boolean tapFurnace) {
        setFurnActionStatus();
        if (tapFurnace) {
            switch (location) {
                case "Edgeville":
                    Client.tap(edgeFurnaceRectAtFurnace);
                    Condition.wait(this::craftMenuOpen, 100, 30);
                    break;
                case "Mount Karuulm":
                    Client.tap(karuulmFurnaceRectAtFurnace);
                    Condition.wait(this::craftMenuOpen, 100, 30);
                    break;
                case "Neitiznot":
                    Client.tap(neitFurnaceRectAtFurnace);
                    Condition.wait(this::craftMenuOpen, 100, 30);
                    break;
                default:
                    Logger.log("Unknown location: " + location);
                    Script.stop();
                    break;
            }
        }
        if (craftMenuOpen()) {
            switch (resource) {
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
                case "Silver bar":
                case "Gold bar":
                    interfaces.craftJewellery(resultItemID);
                    break;
                default:
                    Logger.debugLog("Invalid resource (not in switch logic): " + resource);
                    break;
            }
        }

        Condition.wait(() -> !craftMenuOpen(), 100, 30);
    }

    private void setFurnActionStatus() {
        switch (resource) {
            case "Bronze bar":
            case "Iron bar":
            case "Silver bar":
            case "Steel bar":
            case "Gold bar":
            case "Mithril bar":
            case "Adamantite bar":
            case "Runite bar":
            case "Cannonball":
            case "Molten glass":
                Paint.setStatus("Smelt " + resource);
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
                Paint.setStatus("Craft " + resourceString);
                break;
            default:
                Logger.debugLog("Invalid resource (not in switch logic): " + resource);
                break;
        }
    }

    private boolean makeMenuOpen() {
        return Chatbox.isMakeMenuVisible();
    }

    private boolean craftMenuOpen() {
        return interfaces.craftJewelleryIsOpen();
    }

}
