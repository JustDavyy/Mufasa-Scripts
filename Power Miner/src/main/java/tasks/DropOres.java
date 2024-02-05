package tasks;

import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.AIOMiner.*;

public class DropOres  extends Task {
    Rectangle tapToDropRect = new Rectangle(17, 192, 29, 19);
    Color tapToDropActiveColor = new Color(0xfecb65);

    public boolean activate() {
        // Early exit if banking is enabled!
        if (bankOres) {
            return false;
        }
        return Inventory.isFull();
    }
    @Override
    public boolean execute() {
        boolean isTapToDropEnabled = Client.isColorInRect(tapToDropActiveColor, tapToDropRect, 5);

        if (!isTapToDropEnabled) {
            Client.tap(tapToDropRect);
            return true;
        }
        // Drop the items
        if (Inventory.contains(oreTypeInt, 10)) {
            Inventory.tapAllItems(oreTypeInt, 10);
            return true;
        }

        return false;
    }
}
