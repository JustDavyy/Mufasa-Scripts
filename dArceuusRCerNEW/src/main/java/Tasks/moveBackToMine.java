package Tasks;

import utils.Task;

import static helpers.Interfaces.*;

import main.dArceuusRCerNEW;

public class moveBackToMine extends Task {

    @Override
    public boolean activate() {
        // Have your conditions to activate this task here
        Logger.log("Checking if we should execute moveBackToMine");
        return false;
    }

    @Override
    public boolean execute() {
        // Have the logic that needs to be executed with the task here
        Logger.log("Executing moveBackToMine!");
        return false;
    }
}