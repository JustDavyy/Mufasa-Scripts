package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class FletchBranches extends Task {

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside FletchBranches activate()");
        return Inventory.isFull() && Inventory.contains(brumaRoot, 0.60);
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside FletchBranches execute()");
        Logger.log("Starting to fletch.");
        Integer startHP = Player.getHP();
        Inventory.tapItem(knife, true,0.60);
        Condition.sleep(generateRandomDelay(75, 150));
        Inventory.tapItem(brumaRoot, 0.60);
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Chatbox.makeOption(1);

        Logger.debugLog("Heading to FletchBranches conditional wait.");
        Condition.wait(() -> {
            SideManager.updateStates();
            XpBar.getXP();
            return !inventoryHasBruma || startHP > currentHp || Player.leveledUp() || shouldBurn || gameAt13Percent && isGameGoing;
        }, 200, 150);
        return true;
    }
}
