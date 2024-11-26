package Tasks;

import utils.Task;

import static helpers.Interfaces.GameTabs;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Condition;


import java.awt.Rectangle;

import main.dmCrabberPrivate.TrainingCycleManager;

public class TrainAttack extends Task {
    private Rectangle AttackSwapSpot = new Rectangle(610, 273, 74, 38);
    private boolean Swapped = false;

    @Override
    public boolean activate() {
        // Only activate if it is Attack's turn in the cycle and not already swapped
        if (TrainingCycleManager.nextSkillToTrain == 1 && !Swapped) {
            return true;
        }

        // Reset Swapped if it's no longer Attack's turn
        if (TrainingCycleManager.nextSkillToTrain != 1) {
            Swapped = false;
        }
        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Attack");

        // Only proceed if not yet swapped to avoid repeated activations
        if (!Swapped) {
            if (!GameTabs.isCombatTabOpen()) {
                GameTabs.openCombatTab();
                Condition.wait(GameTabs::isCombatTabOpen, 250, 12);
            }
            Client.tap(AttackSwapSpot);
            Swapped = true;
            return true;
        }
        return false;
    }
}
