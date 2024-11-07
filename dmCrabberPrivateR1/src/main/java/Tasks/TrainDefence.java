package Tasks;

import utils.Task;

import static helpers.Interfaces.GameTabs;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Condition;
import static main.dmCrabberPrivate.*;

import java.awt.Rectangle;

import main.dmCrabberPrivate.TrainingCycleManager;

public class TrainDefence extends Task {
    private boolean Swapped = false;
    private int lastSwappedThreshold = 0;
    private Rectangle DefenceSwapSpot = new Rectangle(703, 326, 72, 35);
    private static final int[] THRESHOLDS = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};

    @Override
    public boolean activate() {
        // Check if it's Defence's turn in the cycle
        if (TrainingCycleManager.nextSkillToTrain != 2) {
            Swapped = false;
            return false;
        }

        // Exit if Swapped is true to avoid reactivation
        if (Swapped) {
            return false;
        }

        // Loop through thresholds and check if Defence needs training
        for (int threshold : THRESHOLDS) {
            if (defenceLevel < threshold && lastSwappedThreshold < threshold && attackLevel > defenceLevel && strenghtLevel > defenceLevel) {
                lastSwappedThreshold = threshold;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Defence");

        if (!Swapped) {
            if (!GameTabs.isCombatTabOpen()) {
                GameTabs.openCombatTab();
                Condition.wait(GameTabs::isCombatTabOpen, 250, 12);
            }
            Client.tap(DefenceSwapSpot);
            Swapped = true;

            // Rotate to the next skill after swapping
            TrainingCycleManager.nextSkillToTrain = 0; // Set back to Strength
            return true;
        }
        return false;
    }
}
