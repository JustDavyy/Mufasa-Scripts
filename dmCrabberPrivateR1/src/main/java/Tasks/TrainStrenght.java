package Tasks;

import utils.Task;

import static helpers.Interfaces.GameTabs;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Condition;
import static main.dmCrabberPrivate.*;

import java.awt.Rectangle;

import main.dmCrabberPrivate.TrainingCycleManager;

public class TrainStrenght extends Task {
    private boolean Swapped = false;
    private int lastSwappedThreshold = 0;
    private Rectangle StrengthSwapSpot = new Rectangle(702, 274, 76, 37);
    private static final int[] THRESHOLDS = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};

    @Override
    public boolean activate() {
        // Check if it's Strength's turn in the cycle
        if (TrainingCycleManager.nextSkillToTrain != 0) {
            return false;
        }

        // Exit if Swapped is true to avoid reactivation
        if (Swapped) {
            return false;
        }

        // Loop through thresholds and check if Strength needs training
        for (int threshold : THRESHOLDS) {
            if (strenghtLevel < threshold && lastSwappedThreshold < threshold) {
                lastSwappedThreshold = threshold;
                Swapped = false;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Strength");

        if (!Swapped) {
            if (!GameTabs.isCombatTabOpen()) {
                GameTabs.openCombatTab();
                Condition.wait(GameTabs::isCombatTabOpen, 250, 12);
            }
            Client.tap(StrengthSwapSpot);
            Swapped = true;

            // Rotate to the next skill after swapping
            TrainingCycleManager.nextSkillToTrain = 1; // Set to Attack
            return true;
        }
        return false;
    }
}
