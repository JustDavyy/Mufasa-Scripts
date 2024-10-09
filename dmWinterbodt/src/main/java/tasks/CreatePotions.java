package tasks;

import utils.Task;

import static main.dmWinterbodt.foodAmountInInventory;
import static main.dmWinterbodt.foodAmountLeftToBank;
import static helpers.Interfaces.*;

public class CreatePotions extends Task {
    @Override
    public boolean activate() {
        return foodAmountInInventory < foodAmountLeftToBank;
    }

    @Override
    public boolean execute() {
        Logger.log("We need to create more potions!");
        return false;
    }
}
