package Tasks;

import utils.Task;

import static helpers.Interfaces.Logger;
import static main.dmCrabberPrivate.*;

public class TrainAttack extends Task {
    @Override
    public boolean activate() {

        // step 1
        if (attackLevel < 5 && strenghtLevel < 5 && defenceLevel < 5) {
            return true;
        }

        // step 2
        if (attackLevel == 5 && strenghtLevel == 5 && defenceLevel == 5) {
            return true;
        }
        
        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Swapping to attack");
        return false;
    }
}
