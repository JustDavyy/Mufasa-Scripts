package tasks;

import utils.Task;

import static main.dmWinterbodt.foodAmountInInventory;
import static main.dmWinterbodt.foodAmountLeftToBank;

public class CreatePotions extends Task {
    @Override
    public boolean activate() {
        return foodAmountInInventory < foodAmountLeftToBank;
    }

    @Override
    public boolean execute() {
        return false;
    }
}
