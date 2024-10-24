package Tasks;

import utils.Task;

import static helpers.Interfaces.Logger;
import static main.dmCrabberPrivate.*;

public class TrainStrenght extends Task {
    @Override
    public boolean activate() {

        // step 1
        if (strenghtLevel < 5 && attackLevel >= 5 && defenceLevel < 5) {
            return true;
        }

        // step 2
        if (attackLevel >= 10 && strenghtLevel <= 10 && defenceLevel <= 5) {
            return true;
        }

        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Strenght");

        return false;
    }
}
