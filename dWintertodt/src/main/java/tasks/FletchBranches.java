package tasks;

import main.dWintertodt;
import utils.Task;

import static helpers.Interfaces.*;

public class FletchBranches extends Task {

    @Override
    public boolean activate() {
        Logger.debugLog("Inside FletchBranches activate()");
        return Inventory.isFull() && Inventory.contains(dWintertodt.brumaRoot, 0.60);
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside FletchBranches execute()");
        Integer startHP = Player.getHP();
        Inventory.tapItem(dWintertodt.knife, 0.60);
        Inventory.tapItem(dWintertodt.brumaRoot, 0.60);
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Chatbox.makeOption(1);

        Logger.debugLog("Heading to FletchBranches conditional wait.");
        Condition.wait(() -> {
            boolean inventoryCheck = !Inventory.contains(dWintertodt.brumaRoot, 0.60);
            boolean healthCheck = startHP > Player.getHP();
            boolean levelUpCheck = Player.leveledUp();
            XpBar.getXP();
            return inventoryCheck || healthCheck || levelUpCheck || dWintertodt.shouldBurn;
        }, 200, 150);
        return true;
    }
}
