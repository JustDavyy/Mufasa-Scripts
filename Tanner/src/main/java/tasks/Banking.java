package tasks;


import main.PrivateTanner;
import utils.Task;

import static helpers.Interfaces.*;

import interfaces.iInventory;

public class Banking extends Task {
    static int coins1 = 1001;
    static int coins2 = 1002;
    static int coins3 = 1003;
    static int coins4 = 1004;
    String BlueDHidesRaw ="2505";
    String GreenDHidesRaw = "1745";
    String Finishedproducts = "1753";
    String FinishedproductsName = "Green dragonhide";


    String Finishedproducts2 = "1751";
    String Finishedproducts2Name = "Blue dragonhide";

    public boolean activate() {
        Logger.log("Banking: checking..");
        if (Bank.isOpen() && !PrivateTanner.BuyHide && !PrivateTanner.WalkBank && !PrivateTanner.WalkGE) {
            return true;
        }

        if (Bank.isOpen() && PrivateTanner.BuyHide && !Inventory.contains(PrivateTanner.GreenDhideDoneNoted, 0.8) && !Inventory.contains(PrivateTanner.BlueDHideDoneNoted, 0.8)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean execute() {
        Logger.log("Banking - Triggered");
        
        if (!Bank.isSelectedQuantityAllButton()) {
            Bank.tapQuantityAllButton();
            Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 400,7);
        }


        if (Inventory.contains(coins4, 0.8)) {
            Inventory.tapItem(coins4);
            Condition.sleep(400);
        } 
        if (Inventory.contains(coins3, 0.8)) {
            Inventory.tapItem(coins3);
            Condition.sleep(400);
        }
        if (Inventory.contains(coins2, 0.8)) {
            Inventory.tapItem(coins2);
            Condition.sleep(400);
        }
        if (Inventory.contains(coins1, 0.8)) {
            Inventory.tapItem(coins1);
            Condition.sleep(400);
        }

        if (!PrivateTanner.BuyHide) {
            if (Inventory.contains(PrivateTanner.GreenDHideNotedRaw, 0.95)) {
                Inventory.tapItem(PrivateTanner.GreenDHideNotedRaw, 0.95);
            }

            if (Inventory.contains(PrivateTanner.BlueDHideNotedRaw, 0.85)) {
                Inventory.tapItem(PrivateTanner.BlueDHideNotedRaw, 0.85);
            }
        }

        if (Inventory.isFull()) {
            Inventory.tapItem(Finishedproducts, 0.95);
            Inventory.tapItem(Finishedproducts2, 0.85);
            Condition.sleep(400);
        }

        
        if (!Inventory.contains(PrivateTanner.GreenDHideRaw, 0.95) && !Inventory.contains(PrivateTanner.BlueDHideRaw,0.85)) {
            if (Bank.contains(GreenDHidesRaw, 0.9) && !PrivateTanner.BuyHide) {
                if (Bank.isSelectedNoteButton()) {
                    Bank.tapItemButton();
                    Condition.wait(() -> !Bank.isSelectedNoteButton(), 350,7);
                }
                Bank.withdrawItem(PrivateTanner.GreenDHideRaw, 0.85);
                Condition.sleep(350);
                Bank.close();
                PrivateTanner.BuyHide = false;
                Condition.wait(() -> !Bank.isOpen(), 300,10);
                
            } else if (Bank.contains(BlueDHidesRaw, 0.85) && !PrivateTanner.BuyHide) {
                if (Bank.isSelectedNoteButton()) {
                    Bank.tapItemButton();
                    Condition.wait(() -> Bank.isSelectedNoteButton(), 350,7);
                }
                Bank.withdrawItem(PrivateTanner.BlueDHideRaw, 0.85);
                Condition.sleep(350);
                Bank.close();
                PrivateTanner.BuyHide = false;
                Condition.wait(() -> !Bank.isOpen(), 300,10);

            } else if (!Bank.contains(GreenDHidesRaw, 0.95) && !Bank.contains(BlueDHidesRaw, 0.85) && Bank.contains(Finishedproducts, 0.95) && Bank.contains(Finishedproducts2, 0.85)) {
                if (!Bank.isSelectedNoteButton()) {
                    Bank.tapNoteButton();
                    Condition.wait(() -> Bank.isSelectedNoteButton(), 350,7);
                }
                Bank.withdrawItem(PrivateTanner.GreenDhideDone, 0.8);
                Condition.sleep(350);
                Bank.withdrawItem(PrivateTanner.BlueDHideDone, 0.8);
                Condition.sleep(350);
                PrivateTanner.BuyHide = true;
                Bank.close();
                Condition.wait(() -> !Bank.isOpen(), 300,10);
            }
        }
        return false;
    }
}
