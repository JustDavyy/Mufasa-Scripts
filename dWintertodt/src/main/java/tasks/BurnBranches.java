package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static utils.Constants.brumaKindling;
import static utils.Constants.brumaRoot;

public class BurnBranches extends Task {

    @Override
    public boolean activate() {
        return Inventory.isFull() && Inventory.contains(brumaKindling, 0.60) && !Inventory.contains(brumaRoot, 0.60);
    }

    @Override
    public boolean execute() {
        Integer startHP = Player.getHP();
        // Need a burn rect to tap(?)

        Condition.wait(() -> startHP < Player.getHP() || !Inventory.contains(brumaKindling, 0.60), 200, 80);
        return false;
    }
}
