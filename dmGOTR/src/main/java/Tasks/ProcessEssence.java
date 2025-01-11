package Tasks;

import helpers.utils.ItemList;
import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class ProcessEssence extends Task {
    private final StateUpdater stateUpdater;

    public ProcessEssence(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    @Override
    public boolean activate() {
        return readyToCraftEssences;
    }

    @Override
    public boolean execute() {

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

        return false;
    }

    private void craftAndFillPouches() {
        // Loop here to fill all pouches and craft again till we are done
    }
}
