package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dCannonballSmelter.*;

public class Setup extends Task {

    @Override
    public boolean activate() {
        return !setupDone;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Initial Setup");
        Logger.debugLog("Starting initialSetup() method.");

        setupZoom();

        GameTabs.openTab(UITabs.INVENTORY);

        checkAndSetMould();

        checkStartCount();

        moveToBank();

        currentLocation = Walker.getPlayerPosition();

        return lastCheck();
    }

    private boolean lastCheck() {
        switch (location) {
            case "Edgeville":
                if (Player.atTile(edgeBankTile)) {
                    currentLocation = edgeBankTile;
                    setupDone = true;
                    return true;
                } return false;
            case "Mount Karuulm":
                if (Player.atTile(karuulmBankTile)) {
                    currentLocation = karuulmBankTile;
                    setupDone = true;
                    return true;
                } return false;
            case "Neitiznot":
                if (Player.atTile(neitBankTile)){
                    currentLocation = neitBankTile;
                    setupDone = true;
                    return true;
                } return false;
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
                return false;
        }
    }

    private void setupZoom() {
        Paint.setStatus("Set zoom");
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

    private void checkAndSetMould() {
        // Check if we have the moulds here, if no log out with error message
        Paint.setStatus("Check for mould");
        if (!Inventory.containsAny(new int[]{4,27012}, 0.9)) {
            Logger.log("We have no ammo mould in our inventory. Stopping script!");
            Logout.logout();
            Script.stop();
        }

        // Update which mould we are using to be sure.
        if (Inventory.contains(27012, 0.9)) {
            mouldUsed = "Double";
        } else {
            mouldUsed = "Single";
        }
    }

    private void checkStartCount() {
        // Check if we already have cballs in invent, and store the count if yes
        Paint.setStatus("Check start amount of balls");
        if (Inventory.contains(ItemList.CANNONBALL_2, 0.8)) {
            startCount = Inventory.stackSize(ItemList.CANNONBALL_2);
        }
    }

    private void moveToBank() {
        switch (location) {
            case "Edgeville":
                Walker.step(edgeBankTile);
                break;
            case "Mount Karuulm":
                Walker.step(karuulmBankTile);
                break;
            case "Neitiznot":
                Walker.step(neitBankTile);
                break;
            default:
                Logger.log("Unknown location: " + location);
        }
    }
}
