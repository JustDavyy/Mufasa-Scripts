package Tasks;

import utils.Task;

import static main.dmCrabberPrivate.*;

public class TrainRange extends Task {
    @Override
    public boolean activate() {

        return false;
    }

    @Override
    public boolean execute() {
        if (!potions.equals("none")) {
            potions = "Ranging"; //to swap the potion used?
        }

        return false;
    }
}
