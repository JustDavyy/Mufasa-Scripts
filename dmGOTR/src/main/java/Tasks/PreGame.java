package Tasks;

import utils.Task;

import static helpers.Interfaces.Logger;

public class PreGame extends Task {

    @Override
    public boolean activate() {
        // Have your conditions to activate this task here
        Logger.log("Checking if we should execute SampleTask");
        return false;
    }

    @Override
    public boolean execute() {
        // Have the logic that needs to be executed with the task here
        Logger.log("Executing SampleTask!");
        return false;
    }
}