package Tasks;

import utils.Task;

import static helpers.Interfaces.GameTabs;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Condition;


import java.awt.Rectangle;

import main.dmCrabberPrivate.TrainingCycleManager;

public class TrainDefence extends Task {
    private boolean Swapped = false;
    private Rectangle DefenceSwapSpot = new Rectangle(703, 326, 72, 35);

    @Override
    public boolean activate() {
        // Only activate if it is Defence's turn in the cycle and not already swapped
        if (TrainingCycleManager.nextSkillToTrain == 2 && !Swapped) {
            return true;
        }

        // Reset Swapped if it's no longer Defence's turn
        if (TrainingCycleManager.nextSkillToTrain != 2) {
            Swapped = false;
        }
        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Defence");

        // Only proceed if not yet swapped to avoid repeated activations
        if (!Swapped) {
            if (!GameTabs.isCombatTabOpen()) {
                GameTabs.openCombatTab();
                Condition.wait(GameTabs::isCombatTabOpen, 250, 12);
            }
            Client.tap(DefenceSwapSpot);
            Swapped = true;
            return true;
        }
        return false;
    }
}
