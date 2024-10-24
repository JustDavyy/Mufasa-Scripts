package Tasks;

import utils.Task;

import static helpers.Interfaces.Logger;
import static main.dmCrabberPrivate.*;

public class TrainDefence extends Task {
    @Override
    public boolean activate() {
        if (defenceLevel < 5 && strenghtLevel >= 5 && attackLevel >= 5) {
            return true;
        }
        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to Defence");
        return false;
    }
}
