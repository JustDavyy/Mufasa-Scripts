package Tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.Condition;
import static helpers.Interfaces.Inventory;
import static main.dmGOTR.shouldDepositRunes;

public class DepositRunes extends Task {
    int[] runeIDs = {
            ItemList.AIR_RUNE_556,
            ItemList.WATER_RUNE_555,
            ItemList.EARTH_RUNE_557,
            ItemList.FIRE_RUNE_554,
            ItemList.MIND_RUNE_558,
            ItemList.BODY_RUNE_559,
            ItemList.COSMIC_RUNE_564,
            ItemList.CHAOS_RUNE_562,
            ItemList.NATURE_RUNE_561,
            ItemList.LAW_RUNE_563,
            ItemList.DEATH_RUNE_560,
            ItemList.BLOOD_RUNE_565
    };

    @Override
    public boolean activate() {
        return shouldDepositRunes;
    }

    @Override
    public boolean execute() {
        // Go to that rune fountain
        // Click it
        Condition.wait(() -> !Inventory.containsAll(runeIDs, 0.80), 100, 10);

        if (!Inventory.containsAll(runeIDs, 0.80)) {
            shouldDepositRunes = false;
            return true;
        }
        return false;
    }
}
