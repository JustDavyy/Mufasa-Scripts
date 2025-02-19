package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dMinnowsFisher.*;

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
        if (Player.within(minnowPlatform) || Player.within(fishingGuild)) {
            Logger.debugLog("We are located within the script area, checking if we have a small fishing net...");
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

        Paint.setStatus("Check minnow count");
        if (Inventory.contains(10570, 0.7)) {
            minnowStartCount = Inventory.stackSize(10570);
        }

        Paint.setStatus("Obtain first XP read");
        XpBar.getXP();

        Paint.setStatus("Obtain Raw Shark price");
        sharkPrice = GrandExchange.getItemPrice(ItemList.RAW_SHARK_383);

        // Make sure the chatbox is opened
        Paint.setStatus("Open game chatbox");
        Logger.debugLog("Making sure the chatbox is open.");
        Chatbox.openGameChat();

        Paint.setStatus("Get player position");
        playerPosition = Walker.getPlayerPosition();

        setupDone = true;

        return false;
    }
}