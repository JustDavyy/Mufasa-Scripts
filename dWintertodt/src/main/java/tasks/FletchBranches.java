package tasks;

import utils.Task;

import static helpers.Interfaces.*;

public class FletchBranches extends Task {
    Integer knife = 946;
    Integer brumaRoots = 20695;

    @Override
    public boolean activate() {
        return Inventory.isFull() && Inventory.contains(brumaRoots, 0.60);
    }

    @Override
    public boolean execute() {
        Integer startHP = Player.getHP();
        Inventory.tapItem(knife, 0.60);
        Inventory.tapItem(brumaRoots, 0.60);
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Chatbox.makeOption(1);
        Condition.wait(() -> startHP < Player.getHP() || !Inventory.isFull(), 200, 30);
        return true;
    }
}
