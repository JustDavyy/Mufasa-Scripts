package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dCannonballSmelter.*;
import static main.dCannonballSmelter.location;

public class Smelt extends Task {
    private static final Rectangle chatCheckArea1 = new Rectangle(7, 130, 22, 19);
    private static final Rectangle chatCheckArea2 = new Rectangle(512, 124, 30, 29);
    private int lastUsedSlots = 0;

    @Override
    public boolean activate() {
        return Player.leveledUp() || Inventory.contains(ItemList.STEEL_BAR_2353, 0.8);
    }

    @Override
    public boolean execute() {

        if (retrycount != 0) {
            retrycount = 0;
            Logger.debugLog("Set retry count to: " + retrycount);
        }

        Paint.setStatus("Smelt cannonballs");
        // Check if we have leveled up
        if (Player.leveledUp()) {
            // Initiate re-smelt here
            doSmeltOption();
            lastProcessTime = System.currentTimeMillis(); // Update last process time
            return true;
        }

        // Check if we are already at the furnace otherwise move there
        if (!atFurnace()) {
            moveToFurnace();
        }

        if (atFurnace()) {
            int currentUsedSlots = Inventory.usedSlots();

            // Check if an item has been processed in the last 10 seconds
            if (currentUsedSlots != lastUsedSlots) {
                lastProcessTime = System.currentTimeMillis(); // Update last process time
                lastUsedSlots = currentUsedSlots; // Update last used slots count
            }

            // Check if 7 seconds have passed without processing an item
            if (System.currentTimeMillis() - lastProcessTime > 7000) {
                Logger.debugLog("No item processed in the last 7 seconds, attempting to re-smelt.");
                doSmeltOption();
                lastProcessTime = System.currentTimeMillis(); // Update last process time
            } else if (makeMenuOpen()) {
                Client.sendKeystroke("space");
                lastProcessTime = System.currentTimeMillis(); // Update last process time
            } else {
                if (Inventory.usedSlots() > 5) {
                    Paint.setStatus("Wait for interrupt");
                    updatePaintBar();
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
                    Condition.wait(() -> Player.atTile(edgeFurnaceTile), 250, 45);
                    currentLocation = edgeFurnaceTile;
                } else {
                    if (!Walker.isReachable(edgeFurnaceTile)) {
                        Walker.webWalk(edgeFurnaceTile);
                    }
                    Walker.step(edgeFurnaceTile);
                    currentLocation = edgeFurnaceTile;
                }
                break;
            case "Mount Karuulm":
                if (Player.tileEquals(currentLocation, karuulmFurnaceTile)) {
                    // No action needed, already at the furnace
                } else {
                    if (!Walker.isReachable(karuulmFurnaceTile)) {
                        Walker.webWalk(karuulmFurnaceTile);
                    }
                    Walker.step(karuulmFurnaceTile);
                    currentLocation = karuulmFurnaceTile;
                }
                break;
            case "Neitiznot":
                if (Player.tileEquals(currentLocation, neitFurnaceTile)) {
                    // No action needed, already at the furnace
                } else if (Player.tileEquals(currentLocation, neitBankTile)) {
                    Client.tap(neitFurnaceRect);
                    Condition.wait(() -> Player.atTile(neitFurnaceTile), 250, 45);
                    currentLocation = neitFurnaceTile;
                } else {
                    if (!Walker.isReachable(neitFurnaceTile)) {
                        Walker.webWalk(neitFurnaceTile);
                    }
                    Walker.step(neitFurnaceTile);
                    currentLocation = neitFurnaceTile;
                }
                break;
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
                break;
        }
    }

    private void doSmeltOption() {
        switch (location) {
            case "Edgeville":
                Client.tap(edgeFurnaceRectAtFurnace);
                Condition.wait(this::makeMenuOpen, 100, 30);
                if (makeMenuOpen()) {
                    Client.sendKeystroke("space");
                }
                break;
            case "Mount Karuulm":
                Client.tap(karuulmFurnaceRectAtFurnace);
                Condition.wait(this::makeMenuOpen, 100, 30);
                if (makeMenuOpen()) {
                    Client.sendKeystroke("space");
                }
                break;
            case "Neitiznot":
                Client.tap(neitFurnaceRectAtFurnace);
                Condition.wait(this::makeMenuOpen, 100, 30);
                if (makeMenuOpen()) {
                    Client.sendKeystroke("space");
                }
                break;
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
                break;
        }
    }

    private boolean makeMenuOpen() {
        boolean check1 = Client.isColorInRect(Color.decode("#5b5345"), chatCheckArea1, 10);
        boolean check2 = Client.isColorInRect(Color.decode("#5b5345"), chatCheckArea2, 10);

        return check1 && check2;
    }

}
