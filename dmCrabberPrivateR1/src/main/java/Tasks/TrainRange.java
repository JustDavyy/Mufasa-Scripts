package Tasks;
import utils.Task;
import static helpers.Interfaces.GameTabs;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Condition;
import static main.dmCrabberPrivate.*;
import java.awt.Rectangle;
import main.dmCrabberPrivate.TrainingCycleManager;


public class TrainRange extends Task {
    private boolean Swapped = false;
    private Rectangle RangingSwapSpot = new Rectangle(702, 274, 76, 37); // Adjust as necessary

    @Override
    public boolean activate() {
        if (TrainingCycleManager.nextSkillToTrain != 3) return false;
        // Check each target threshold individually, ensuring lastSwappedThreshold <= each target
        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Ranging");

        if (!potions.equals("none")) {
            potions = "Ranging"; //to swap the potion used?
        }

        if (!Swapped) {
            // Ensure the combat tab is open
            if (!GameTabs.isCombatTabOpen()) {
                GameTabs.openCombatTab();
                Condition.wait(GameTabs::isCombatTabOpen, 250, 12);
            }
            Client.tap(RangingSwapSpot);
            Swapped = true;  // Mark swap as done for this threshold
            return true;
        }
        return false;
    }
}


