package tasks;

import utils.SideManager;
import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class FletchBranches extends Task {
    public static boolean isFletching = false;

    @Override
    public boolean activate() {
        if (Inventory.isFull() && inventoryHasBruma && !shouldBurn) {
            isFletching = true;
        }

        return isFletching;
    }

    @Override
    public boolean execute() {
        if (isFletching && !inventoryHasBruma || shouldBurn) {
            isFletching = false;
        }

        Logger.debugLog("Inside FletchBranches execute()");
        Logger.log("Initiating fletching action.");

        GameTabs.openInventoryTab();

        Integer startHP = Player.getHP();
        Inventory.tapItem(knife, true, 0.60);
        Condition.sleep(generateRandomDelay(75, 150));
        Inventory.tapItem(brumaRoot, 0.60);
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Chatbox.makeOption(1);

        Logger.debugLog("Heading to FletchBranches conditional wait.");
        Condition.wait(() -> {
            StateUpdater.updateGameAt20();
            StateUpdater.updateIsGameGoing();
            StateUpdater.updateKindlingState();
            StateUpdater.updateGameAt13();

            XpBar.getXP();
            return !inventoryHasBruma || startHP > currentHp || Player.leveledUp() || shouldBurn || gameAt13Percent && isGameGoing;
        }, 200, 150);

        return true;
    }
}
