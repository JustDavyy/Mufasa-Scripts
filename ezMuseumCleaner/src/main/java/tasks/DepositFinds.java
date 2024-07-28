package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Condition;
import static main.ezMuseumCleaner.*;

public class DepositFinds extends Task {
    private final Rectangle depositRect = new Rectangle(443, 311, 17, 13);
    private final Rectangle depositRect2 = new Rectangle(474, 266, 8, 11);
    private final Rectangle instantTap = new Rectangle(554, 321, 14, 6);

    private final Color checkColor = Color.decode("#9c1f1b"); // the color for the red "Finds"
    private final Rectangle checkRect = new Rectangle(106, 16, 49, 26); // a rectangle for the red "Finds"
    private final Rectangle chatboxRectangle = new Rectangle(146, 52, 255, 20); // Chatbox click rectangle

    @Override
    public boolean activate() {
        return shouldDeposit && !hasFinds;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Depositing finds");
        Logger.log("Depositing finds");

        if (Player.tileEquals(currentLocation, cleanTile)) {
            instantTapForDeposit();
        } else if (!Player.tileEquals(currentLocation, depositTile)) {
            stepToDepositBox();
        }

        if (isAtDepositTile()) {
            depositItems();
            return true;
        }
        return false;
    }

    private void instantTapForDeposit() {
        Logger.debugLog("Instant tapping for deposit");
        Client.tap(instantTap);
        Condition.wait(() -> Player.atTile(depositTile2), 100, 50);
        currentLocation = depositTile2;
    }

    private void stepToDepositBox() {
        Logger.debugLog("Stepping to deposit box");
        Walker.step(depositTile);
        Condition.wait(() -> Player.atTile(depositTile), 100, 50);
        currentLocation = depositTile;
    }

    private boolean isAtDepositTile() {
        return Player.tileEquals(currentLocation, depositTile) || Player.tileEquals(currentLocation, depositTile2);
    }

    private void depositItems() {
        Logger.debugLog("Depositing!");
        if (currentLocation.equals(depositTile)) {
            Client.tap(depositRect);
        }
        Condition.wait(() -> Chatbox.findChatboxMenu() != null, 100, 20);
        Client.tap(chatboxRectangle);
        Condition.wait(() -> !Inventory.containsAny(depositItemsList, 0.80) || checkIfPlayersAround() || Script.isTimeForBreak(), 100, 600);

        shouldDeposit = false;

        if (Inventory.contains(ItemList.ANTIQUE_LAMP_4447, 0.80) || !dropAll) {
            shouldDrop = true;
        } else {
            Walker.step(collectTile, this::dropAllItems);
        }
    }


    private void dropAllItems() {
        Logger.log("Dropping items..");
        Inventory.dropInventItems(toolPositionList, false);
    }
}
