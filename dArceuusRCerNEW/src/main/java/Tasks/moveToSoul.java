package Tasks;

import utils.Task;

import static helpers.Interfaces.*;

import main.dArceuusRCerNEW;

public class moveToSoul extends Task {

    @Override
    public boolean activate() {
        // Have your conditions to activate this task here
        Logger.log("Checking if we should execute moveToSoul");
        return false;
    }

    @Override
    public boolean execute() {
        // Have the logic that needs to be executed with the task here
        Logger.log("Executing moveToSoul!");
        return false;
    }
}