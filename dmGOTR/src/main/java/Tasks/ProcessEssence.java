package Tasks;

import helpers.utils.ItemList;
import utils.StateUpdater;
import utils.Task;

import static Tasks.MineEssence.LARGE_REMAINS_MINING_AREA;
import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class ProcessEssence extends Task {
    private final StateUpdater stateUpdater;

    public ProcessEssence(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    @Override
    public boolean activate() {
        return readyToCraftEssences || stateUpdater.isGameGoing() && Inventory.contains(ItemList.GUARDIAN_FRAGMENTS_26878, 0.8) && !Inventory.contains(ItemList.GUARDIAN_ESSENCE_26879, 0.8) && !Player.isTileWithinArea(currentLocation, LARGE_REMAINS_MINING_AREA) && !Player.tileEquals(currentLocation, GUARDIAN_PARTS_TILE);
    }

    @Override
    public boolean execute() {

        setStatusAndDebugLog("Create essence");

        // Step to workbench if needed
        if (!Player.tileEquals(currentLocation, WORKBENCH_TILE)) {
            Walker.step(WORKBENCH_TILE);
        }

        // Only run code if we're at the workbench
        if (Player.atTile(WORKBENCH_TILE)) {
            Client.tap(WORKBENCH_TAP_RECT);
            Condition.wait(() -> Inventory.isFull() || !Inventory.contains(ItemList.GUARDIAN_FRAGMENTS_26878, 0.75), 100, 200);
        } else {
            return false;
        }

        // Should have a full inventory of essence now, or ran out of fragments

        if (usePouches) {
            craftAndFillPouches();
        }

        // Done crafting pouches, ready to craft runes
        Walker.walkTo(GUARDIANS_MIDDLE_AREA_TILE);
        readyToCraftEssences = false;
        readyToGoToAltar = true;

        return false;
    }

    private void craftAndFillPouches() {
        // Loop here to fill all pouches and craft again till we are done
    }
}
