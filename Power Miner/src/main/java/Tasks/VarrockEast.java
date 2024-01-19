package Tasks;

import main.AIOMiner;
import utils.Task;

public class VarrockEast extends Task {
    public boolean activate() {
        if (AIOMiner.Location.equals("Varrock East")) {
            return true;
        }
        return false;
    }
    @Override
    public boolean execute() {
        //Execute logic
        return false;
    }
}
