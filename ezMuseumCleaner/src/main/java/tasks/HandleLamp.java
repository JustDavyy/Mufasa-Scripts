package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.*;

public class HandleLamp extends Task {
    private Rectangle crossRect = new Rectangle(564, 191, 24, 22);
    private Color crossColor = Color.decode("#ff9100");
    private Rectangle submitButton = new Rectangle(291, 434, 108, 20);

    private Rectangle paperRect = new Rectangle(136, 207, 59, 47);
    private Color paperColor = Color.decode("#524a4a");

    @Override
    public boolean activate() {
        return Inventory.contains(ItemList.ANTIQUE_LAMP_4447, 0.80);
    }

    @Override
    public boolean execute() {
        Logger.log("We have a lamp!");
        Paint.updateBox(paintLampBox, currentLampCount++);

        Inventory.tapItem(ItemList.ANTIQUE_LAMP_4447, 0.80);
        Condition.wait(() -> Client.isColorInRect(crossColor, crossRect, 5), 100, 20);
        Client.tap(selectedLampSkillRectangle);
        Condition.sleep(generateRandomDelay(500, 1500));
        Client.tap(submitButton);
        Condition.sleep(generateRandomDelay(1500, 2000));
        return true;
    }
}
