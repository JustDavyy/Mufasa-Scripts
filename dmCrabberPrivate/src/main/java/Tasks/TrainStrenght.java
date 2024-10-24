package Tasks;

import utils.Task;

import static helpers.Interfaces.Logger;
import static main.dmCrabberPrivate.*;

public class TrainStrenght extends Task {
    @Override
    public boolean activate() {
        if (strenghtLevel < 5 && attackLevel >= 5 && defenceLevel < 5) {
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
