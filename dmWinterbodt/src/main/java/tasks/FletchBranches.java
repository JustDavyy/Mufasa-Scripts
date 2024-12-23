package tasks;

import helpers.utils.UITabs;
import utils.Helpers;
import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class FletchBranches extends Task {
    public static boolean isFletching = false;

    @Override
    public boolean activate() {
        if (Inventory.isFull() && inventoryHasLogs && !shouldBurn && !burnOnly) {
            isFletching = true;
        }

        return isFletching;
    }

    @Override
    public boolean execute() {
        if (isFletching && !inventoryHasLogs || shouldBurn) {
            isFletching = false;
        }

        Logger.log("Initiating fletching action.");
        Paint.setStatus("Initiating fletching action");

        GameTabs.openTab(UITabs.INVENTORY);

        Inventory.tapItem(knife, true, 0.60);
        Condition.sleep(generateRandomDelay(75, 150));
        Inventory.tapItem(brumaRoot, 0.60);
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Chatbox.makeOption(1);
        lastActivity = System.currentTimeMillis();

        Logger.debugLog("Heading to FletchBranches conditional wait.");
        Paint.setStatus("Waiting for fletching to end");

        Condition.wait(() -> {
            SideManager.updateStates();
            XpBar.getXP();

            return !inventoryHasLogs || Helpers.countItemUnchanged(brumaRoot) || shouldEat || Player.leveledUp() || shouldBurn || gameAt15Percent && isGameGoing;
        }, 200, 150);

        return true;
    }
}
