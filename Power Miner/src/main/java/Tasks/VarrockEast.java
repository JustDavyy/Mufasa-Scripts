package Tasks;

import helpers.utils.RegionBox;
import main.AIOMiner;
import utils.LocationInfo;
import utils.RegionInfo;
import utils.Task;

import static helpers.Interfaces.Player;

public class VarrockEast extends Task {

    public boolean activate() {
        if (AIOMiner.Location.equals("Varrock East") && Player.within(RegionInfo.VARROCK_EAST.getWorldRegion(), RegionInfo.VARROCK_EAST.getMineRegion())) {
            return true;
        }
        return false;
    }
    @Override
    public boolean execute() {
        //Move to spot
        return false;
    }
}
