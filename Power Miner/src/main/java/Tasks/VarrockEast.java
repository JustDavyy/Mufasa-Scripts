package Tasks;

import main.AIOMiner;
import utils.MiningHelper;
import utils.RegionInfo;
import utils.Task;

import static helpers.Interfaces.Player;

public class VarrockEast extends Task {
    MiningHelper miningHelper = new MiningHelper();

    public boolean activate() {
        if (AIOMiner.Location.equals("Varrock East")) {
            if (Player.within(RegionInfo.VARROCK_EAST.getMineRegion(), RegionInfo.VARROCK_EAST.getWorldRegion())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean execute() {
        //Move to spot

        //perform mining
        performMining();
        return false;
    }

    public void performMining() {

    }
}
