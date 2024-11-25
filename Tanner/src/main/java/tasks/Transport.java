package tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import main.PrivateTanner;
import utils.Areas;
import utils.Task;
import static helpers.Interfaces.*;

import java.awt.Rectangle;



public class Transport extends Task {
    private Tile CraftingBankTile = new Tile(11745, 12864,0);
    private Rectangle CapeSlot = new Rectangle(639, 273, 27, 26);
    private Rectangle RingSlot = new Rectangle(737, 393, 26, 28);




    public boolean activate() {
        if (Bank.isOpen()) {return false;}

        Logger.log("Walker: Checking..");
        if (Player.within(Areas.GrandExhangeArea)) {
            if (PrivateTanner.BuyHide && (!Player.atTile(Buying.GeTile1) || !Player.atTile(Buying.GeTile2) ||!Player.atTile(Buying.GeTile3))) {
                Logger.log("Walker - Triggered");
                return true;
            } else if (!PrivateTanner.BuyHide) {
                Logger.log("Walker - Triggered");
                return true;
            }
        }

        if (Player.within(Areas.CratingGuildArea)) {
            if (!PrivateTanner.BuyHide && !Player.atTile(CraftingBankTile)) {
                Logger.log("Walker - Triggered");
                return true;
            }
            if (PrivateTanner.BuyHide) {
                Logger.log("Walker - Triggered");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean execute() {
        //Go TO GE

        if (PrivateTanner.BuyHide) {
            if (Bank.isOpen()) {
                Bank.close();
            }
            if (Player.within(Areas.CratingGuildArea)) {
                if (!GameTabs.isEquipTabOpen()) {
                    GameTabs.openEquipTab();
                    Condition.wait(() -> GameTabs.isEquipTabOpen(),200,7);
                }
                if (GameTabs.isEquipTabOpen()) {
                    Client.tap(RingSlot);
                }   
            }

            if (Player.within(Areas.GrandExhangeArea) && (!Player.atTile(Buying.GeTile1) || !Player.atTile(Buying.GeTile2) || !Player.atTile(Buying.GeTile3))) {
                if (Walker.isReachable(Buying.GeTile2)) {
                    Walker.step(Buying.getRandomGeTile());
                    /////////////////////////////add 1-3 tiles to randomly select
                } else {
                    Walker.webWalk(Buying.getRandomGeTile());
                }
            }
        }

        //Go TO Crafting Guild
        if (!PrivateTanner.BuyHide && !Player.atTile(CraftingBankTile)) {
            if (Bank.isOpen()) {
                Bank.close();
            }

            if (Player.within(Areas.GrandExhangeArea)) {
                if (!GameTabs.isEquipTabOpen()) {
                    GameTabs.openEquipTab();
                    Condition.wait(() -> GameTabs.isEquipTabOpen(),200,7);
                }
                if (GameTabs.isEquipTabOpen()) {
                    Client.tap(CapeSlot);
                }
            }

            if (Player.within(Areas.CratingGuildArea)) {
                Walker.step(CraftingBankTile);
            }
        }

        return false;
    }
}
