package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class PreGame extends Task {
    @Override
    public boolean activate() {
        Logger.debugLog("Inside PreGame activate()");
        return waitingForGameToStart || shouldStartWithBurn;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside PreGame execute()");

        // Check if we are at the burn tile, otherwise move there
        if (!Player.atTile(SideManager.getBurnTile(), WTRegion)) {
            Walker.step(SideManager.getBurnTile(), WTRegion);
        }

        // If at burn tile, update current location and lock ourselves to this task
        if (Player.atTile(SideManager.getBurnTile(), WTRegion)) {
            // Set shouldStartWithBurn to true, so we lock ourselves in this task.
            shouldStartWithBurn = true;

            // Update our current location
            currentLocation = SideManager.getBurnTile();
        }

        Integer startHP = Player.getHP();

        // Wait for the game to start
        Condition.wait(() -> {
            // Update states during our conditional wait
            SideManager.updateStates();

            // Check if our HP has dropped
            boolean healthCheck = startHP > Player.getHP();

            // Check if we can light
            if (SideManager.getNeedsReburning()) {
                Logger.log("Brazier needs initial lighting!");
                Logger.log("Lighting brazier!");
                Client.tap(SideManager.getBurnRect());
                Condition.sleep(generateRandomDelay(1000, 1500));
                return true;
            } else {
                return healthCheck;
            }
        }, 200, 300);

        // Move to the branch tile
        Walker.step(SideManager.getBranchTile(), WTRegion);

        // Start chopping branches
        Client.tap(SideManager.getBranchRect());

        // Reset our booleans before exiting the task
        shouldStartWithBurn = false;

        return false;
    }
}
