package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.*;

public class HandleLamp extends Task {
    private final Rectangle crossRect = new Rectangle(564, 191, 24, 22);
    private final Color crossColor = Color.decode("#ff9100");
    private final Rectangle submitButton = new Rectangle(291, 434, 108, 20);

    private final Rectangle paperRect = new Rectangle(136, 207, 59, 47);
    private final Color paperColor = Color.decode("#524a4a");

    @Override
    public boolean activate() {
        return Inventory.contains(ItemList.ANTIQUE_LAMP_4447, 0.80);
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Using lamp");
        logAndPaintLampBox();
        useLamp();
        waitForColorChange(crossColor, crossRect);
        selectSkillAndSubmit();
        return true;
    }

    private void logAndPaintLampBox() {
        Logger.log("We have a lamp!");
        currentLampCount++;
        Paint.updateBox(paintLampBox, currentLampCount);
    }

    private void useLamp() {
        Inventory.tapItem(ItemList.ANTIQUE_LAMP_4447, 0.80);
    }

    private void waitForColorChange(Color color, Rectangle rect) {
        Condition.wait(() -> Client.isColorInRect(color, rect, 5), 100, 20);
    }

    private void selectSkillAndSubmit() {
        Client.tap(selectedLampSkillRectangle);
        Condition.sleep(generateRandomDelay(500, 1500));
        Client.tap(submitButton);
        Condition.sleep(generateRandomDelay(1500, 2000));
        XpBar.getXP();
    }
}
