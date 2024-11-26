package Tasks;
import utils.Task;
import static helpers.Interfaces.GameTabs;
import static helpers.Interfaces.Client;
import static helpers.Interfaces.Logger;
import static helpers.Interfaces.Condition;
import static main.dmCrabberPrivate.*;
import java.awt.Rectangle;

import main.dmCrabberPrivate;
import main.dmCrabberPrivate.TrainingCycleManager;


public class TrainRange extends Task {
    private boolean Swapped = false;
    private boolean Ranging50DefSwap = false;
    private Rectangle RapidFire = new Rectangle(702, 274, 76, 37); // Adjust as necessary
    private Rectangle LongRange = new Rectangle(610, 325, 75, 36);

    @Override
    public boolean activate() {
        if (TrainingCycleManager.nextSkillToTrain != 3) return false;

        if (!Swapped) {
            return true;
        }
        if (!Ranging50DefSwap && "Ranging".equals(dmCrabberPrivate.ChosenBuild)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Ranging");

        if (!potions.equals("none")) {
            potions = "Ranging"; //to swap the potion used?
        }

        if ("Ranging".equals(dmCrabberPrivate.ChosenBuild) && SkillTracker.defenceLevel < 50 && !Ranging50DefSwap) {
            Client.tap(LongRange);
            Ranging50DefSwap = true;  // Mark swap as done for this threshold
            return true;
        }

        if (!Swapped) {
            // Ensure the combat tab is open
            if (!GameTabs.isCombatTabOpen()) {
                GameTabs.openCombatTab();
                Condition.wait(GameTabs::isCombatTabOpen, 250, 12);
            }
            if ("Melee".equals(dmCrabberPrivate.ChosenBuild)) {
                Client.tap(RapidFire);
                Swapped = true;  // Mark swap as done for this threshold
                return true;
            }
            if ("Ranging".equals(dmCrabberPrivate.ChosenBuild)) {
                if (SkillTracker.defenceLevel >= 50) {
                    Client.tap(RapidFire);
                    Swapped = true;  // Mark swap as done for this threshold
                    return true;
                }
            }
        }
        return false;
    }
}


