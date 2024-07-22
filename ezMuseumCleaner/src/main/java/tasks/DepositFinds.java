package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Condition;
import static main.ezMuseumCleaner.*;

public class DepositFinds extends Task {
    private final Rectangle depositRect = new Rectangle(443, 311, 17, 13);

    private final Color checkColor = Color.decode("#9c1f1b"); // the color for the red "Finds"
    private final Rectangle checkRect = new Rectangle(106, 16, 49, 26); // a rectangle for the red "Finds"
    private final Rectangle chatboxRectangle = new Rectangle(146, 52, 255, 20); // Chatbox click rectangle

    @Override
    public boolean activate() {
        return shouldDeposit;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Depositing finds");
        Logger.log("Depositing finds");
        if (!Player.tileEquals(currentLocation, depositTile)) {
            Logger.debugLog("Stepping to deposit box");
            Walker.step(depositTile);
            Condition.sleep(generateRandomDelay(400, 600));
            currentLocation = depositTile;
        }

        if (Player.tileEquals(currentLocation, depositTile)) {
            Logger.debugLog("Depositing!");
            Client.tap(depositRect);
            Condition.wait(() -> Client.isColorInRect(checkColor, checkRect, 5), 100, 10);
            Client.tap(chatboxRectangle);
            Condition.wait(() -> !Inventory.containsAny(depositItemsList, 0.80) || Game.isPlayersAround() || Script.isTimeForBreak(), 300, 300);
            shouldDeposit = false;

            if (Inventory.contains(ItemList.ANTIQUE_LAMP_4447, 0.80)) {
                shouldDrop = true;
            } else {
                Walker.step(collectTile, this::dropAllItems);
            }
            return true;
        }
        return false;
    }

    private void dropAllItems() {
        Logger.log("Dropping items..");
        Inventory.dropInventItems(toolPositionList, false);
    }
}
