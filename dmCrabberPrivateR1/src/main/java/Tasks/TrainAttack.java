package Tasks;

import utils.Task;

import static helpers.Interfaces.GameTabs;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Condition;
import static main.dmCrabberPrivate.*;

import java.awt.Rectangle;

import main.dmCrabberPrivate.TrainingCycleManager;

public class TrainAttack extends Task {
    private int lastSwappedThreshold = 0;
    private Rectangle AttackSwapSpot = new Rectangle(610, 273, 74, 38);
    private boolean Swapped = false;
    private static final int[] THRESHOLDS = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};

    @Override
    public boolean activate() {
        // Check if it's Attack's turn in the cycle
        if (TrainingCycleManager.nextSkillToTrain != 1) {
            Swapped = false;
            return false;
        }

        // Exit if Swapped is true to avoid reactivation
        if (Swapped) {
            return false;
        }

        // Loop through thresholds and check if Attack needs training
        for (int threshold : THRESHOLDS) {
            if (attackLevel < threshold && lastSwappedThreshold < threshold && strenghtLevel > attackLevel && defenceLevel < attackLevel) {
                lastSwappedThreshold = threshold;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Attack");

        if (!Swapped) {
            if (!GameTabs.isCombatTabOpen()) {
                GameTabs.openCombatTab();
                Condition.wait(GameTabs::isCombatTabOpen, 250, 12);
            }
            Client.tap(AttackSwapSpot);
            Swapped = true;

            // Rotate to the next skill after swapping
            TrainingCycleManager.nextSkillToTrain = 2; // Set to Defence
            return true;
        }
        return false;
    }
}
