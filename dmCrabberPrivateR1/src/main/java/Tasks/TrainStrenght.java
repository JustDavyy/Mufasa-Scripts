package Tasks;

import utils.Task;

import static helpers.Interfaces.GameTabs;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Condition;


import java.awt.Rectangle;

import main.dmCrabberPrivate.TrainingCycleManager;

public class TrainStrenght extends Task {
    private boolean Swapped = false;
    private Rectangle StrengthSwapSpot = new Rectangle(702, 274, 76, 37);

    @Override
    public boolean activate() {
        // Only activate if it is Strength's turn in the cycle and not already swapped
        if (TrainingCycleManager.nextSkillToTrain == 0 && !Swapped) {
            return true;
        }

        // Reset Swapped if it's no longer Strength's turn
        if (TrainingCycleManager.nextSkillToTrain != 0) {
            Swapped = false;
        }
        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Strength");

        // Only proceed if not yet swapped to avoid repeated activations
        if (!Swapped) {
            if (!GameTabs.isCombatTabOpen()) {
                GameTabs.openCombatTab();
                Condition.wait(GameTabs::isCombatTabOpen, 250, 12);
            }
            Client.tap(StrengthSwapSpot);
            Swapped = true;
            Condition.sleep(300);
            return true;
        }
        return false;
    }
}
