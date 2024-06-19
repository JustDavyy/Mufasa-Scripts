package tasks;

import utils.SideManager;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dWintertodt.*;

public class PreGame extends Task {
    @Override
    public boolean activate() {
        Logger.debugLog("Inside PreGame activate()");
        return waitingForGameEnded || shouldStartWithBurn;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside PreGame execute()");

        // Check if we are at the burn tile, otherwise move there
        if (!Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
            Walker.step(SideManager.getBurnTile(), WTRegion);
        }

        // If at burn tile, update current location and lock ourselves to this task
        if (Player.tileEquals(currentLocation, SideManager.getBurnTile())) {
            // Set shouldStartWithBurn to true, so we lock ourselves in this task.
            shouldStartWithBurn = true;

            // Update our current location
            currentLocation = SideManager.getBurnTile();
        }

        // Wait for the game to start
        Condition.wait(() -> {
            // Update states during our conditional wait
            SideManager.updateStates();

            // Check if we can light
            if (waitingForGameToStart) {
                if (SideManager.getNeedsReburning()) {
                    Logger.log("Tapping Brazier for initial lighting");
                    Client.tap(SideManager.getBurnRect());
                    Condition.sleep(generateRandomDelay(1000, 1500));
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }, 50, 1200);

        // Move to the branch tile
        Walker.step(SideManager.getBranchTile(), WTRegion);
        currentLocation = SideManager.getBranchTile();

        // Reset our booleans before exiting the task
        shouldStartWithBurn = false;

        return false;
    }
}
