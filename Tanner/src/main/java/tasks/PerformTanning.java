package tasks;

import utils.Task;
import java.awt.*;

import helpers.utils.Tile;
import main.PrivateTanner;

import static helpers.Interfaces.*;


public class PerformTanning extends Task {
    private Tile CraftingBankTile = new Tile(11745, 12864,0);
    private Rectangle CraftingBankClickSpot = new Rectangle(400,400,400,200);

    public boolean activate() {
        Logger.log("Tanning: Checking..");
        if (!Player.atTile(CraftingBankTile)) {return false;}
        if (Bank.isOpen()) {return false;}
        
        if (Inventory.contains(PrivateTanner.GreenDHideRaw, 0.8) || Inventory.contains(PrivateTanner.BlueDHideRaw, 0.8)) {
            Logger.log("Tanning - Triggered");
            return true;
        } else if (Player.atTile(CraftingBankTile)) {
            Logger.log("Tanning - Triggered");
            Client.tap(CraftingBankClickSpot);
            Condition.wait(()-> Bank.isOpen(), 200,10);
        }
        return false;
    }


    @Override
    public boolean execute() {
        if (!GameTabs.isMagicTabOpen()) {
            GameTabs.openMagicTab();
            Condition.wait(()-> GameTabs.isMagicTabOpen(), 200,10);
        }
        if (GameTabs.isMagicTabOpen()) {
            Magic.tapTanLeatherSpell();
            Condition.sleep(400);
        }
        return false;
    }
}
