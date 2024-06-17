package tasks;

import utils.Constants;
import utils.Task;

import static helpers.Interfaces.*;

public class FletchBranches extends Task {

    @Override
    public boolean activate() {
        return Inventory.isFull() && Inventory.contains(Constants.brumaRoot, 0.60);
    }

    @Override
    public boolean execute() {
        Integer startHP = Player.getHP();
        Inventory.tapItem(Constants.knife, 0.60);
        Inventory.tapItem(Constants.brumaRoot, 0.60);
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Chatbox.makeOption(1);
        Condition.wait(() -> startHP < Player.getHP() || !Inventory.contains(Constants.brumaRoot, 0.60), 200, 30);
        return true;
    }
}
