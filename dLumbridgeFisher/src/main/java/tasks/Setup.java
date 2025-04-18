package tasks;

import helpers.utils.ItemList;
import helpers.utils.Skills;
import helpers.utils.UITabs;
import utils.Task;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dLumbridgeFisher.*;

public class Setup extends Task {


    @Override
    public boolean activate() {
        return !setupDone;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Initial Setup");
        Logger.log("Initial Setup");

        Paint.setStatus("Check location");
        Logger.log("Check location");
        if (Player.within(lumbridgeArea)) {
            Logger.debugLog("We are located within the lumbridge script area, checking if we have a small fishing net...");
        } else {
            Logger.log("Could not locate us within the script area. Please move there and start the script again.");
            Logout.logout();
            Script.stop();
        }

        Paint.setStatus("Open inventory");
        if(!GameTabs.isTabOpen(UITabs.INVENTORY)) {
            GameTabs.openTab(UITabs.INVENTORY);
        }

        Paint.setStatus("Check for fishing net");
        if(!Inventory.contains(ItemList.SMALL_FISHING_NET_303, 0.90)) {
            Logger.log("No small fishing net was found in the inventory, please grab it and restart the script.");
            Logout.logout();
            Script.stop();
        } else {
            Logger.debugLog("Fishing net is present in the inventory, we're good to go!");
            hopActions();
        }

        Paint.setStatus("Obtain first XP read");
        XpBar.getXP();

        // Get stats
        Paint.setStatus("Get stat levels");
        GameTabs.openTab(UITabs.STATS);
        fishingLevel = Stats.getRealLevel(Skills.FISHING);
        cookingLevel = Stats.getRealLevel(Skills.COOKING);
        Logger.log("Fishing lvl: " + fishingLevel + " | Cooking level: " + cookingLevel);

        // Make sure the chatbox is opened
        Paint.setStatus("Close game chatbox");
        Logger.debugLog("Making sure the chatbox is closed.");
        Chatbox.closeChatbox();

        Paint.setStatus("Get player position");
        playerPosition = Walker.getPlayerPosition();

        if (!Player.isTileWithinArea(playerPosition, fishingArea)) {
            Logger.log("Moving to fishing area");
            Walker.webWalk(southFishingTile);
            Player.waitTillNotMoving(15);
        }


        if (Player.within(fishingArea)) {
            playerPosition = Walker.getPlayerPosition();
            setupDone = true;
            Logger.log("Setup is done!");
        }

        return false;
    }
}