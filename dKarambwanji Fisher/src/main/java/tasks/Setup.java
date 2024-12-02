package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dKarambwanjiFisher.*;

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
        if (Player.within(FishingArea)) {
            Logger.debugLog("We are located at the Karambwanji fishing area, checking if we have a small fishing net...");
        } else {
            Logger.log("Could not locate us in the Karambwanji fishing area. Please move there and start the script again.");
            Logger.log("Location: " + Walker.getPlayerPosition().toString());
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

        Paint.setStatus("Check karambwanji count");
        if (Inventory.contains(ItemList.RAW_KARAMBWANJI_3150, 0.7)) {
            karambwanjiStartCount = Inventory.stackSize(ItemList.RAW_KARAMBWANJI_3150);
        }

        Paint.setStatus("Obtain first XP read");
        XpBar.getXP();

        setupDone = true;

        return false;
    }
}