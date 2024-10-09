package tasks;

import helpers.utils.Tile;
import utils.Task;

import java.awt.*;

import static main.dmWinterbodt.foodAmountInInventory;
import static main.dmWinterbodt.foodAmountLeftToBank;
import static helpers.Interfaces.*;

public class CreatePotions extends Task {
    Tile brumaHerb = new Tile(6503, 15661, 0);
    Rectangle herbRect = new Rectangle(388, 260, 18, 26);

    Tile potionTile = new Tile(6507, 15677, 0);
    Rectangle potionRect = new Rectangle(388, 260, 18, 26);
    
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
