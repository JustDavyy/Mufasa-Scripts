package tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dTutIsland.*;

public class SetIngameSettings extends Task {
    public boolean activate() {
        return !setSettingsDone;
    }

    private boolean firstStepDone = false;
    private boolean secondStepDone = false;
    private boolean thirdStepDone = false;
    private boolean fourthStepDone = false;
    private boolean fifthStepDone = false;
    private boolean sixthStepDone = false;
    private boolean seventhStepDone = false;

    private static final Area floor1 = new Area(
            new Tile(12800, 12680, 1),
            new Tile(12882, 12555, 1)
    );

    private static final Area topFloor = new Area(
            new Tile(12802, 12682, 2),
            new Tile(12869, 12559, 2)
    );

    Tile[] pathToStairs = new Tile[] {
            new Tile(12939, 12617, 0),
            new Tile(12914, 12621, 0),
            new Tile(12894, 12618, 0),
            new Tile(12876, 12618, 0),
            new Tile(12865, 12611, 0),
            new Tile(12860, 12593, 0),
            new Tile(12851, 12586, 0),
            new Tile(12834, 12587, 0)
    };

    @Override
    public boolean execute() {
        Logger.log("Running Set In-Game Settings task");

        Logger.log("Open settings tab");
        GameTabs.openTab(UITabs.SETTINGS);
        Condition.sleep(800, 1200);
        Logger.log("Open ALL settings");
        Client.tap(new Rectangle(638, 458, 107, 19));
        Condition.sleep(1600, 2300);

        Logger.log("Disable hide roof");
        Client.tap(new Rectangle(239, 203, 326, 18));
        // Type hide roof
        String textToSend = "hide roof";
        for (char c : textToSend.toCharArray()) {
            String keyToSend = (c == ' ') ? "space" : String.valueOf(c);
            Client.sendKeystroke(keyToSend);
            Condition.sleep(75, 150);
        }
        Condition.sleep(800, 1200);
        Client.tap(new Rectangle(181, 239, 378, 21));

        // Reset search bar
        Logger.log("Reset search bar");
        Client.tap(new Rectangle(638, 458, 107, 19));
        Condition.sleep(1600, 2300);

        Logger.log("Disable level up popups");
        Client.tap(new Rectangle(239, 203, 326, 18));
        // Type level up
        String textToSend2 = "level up";
        for (char c : textToSend2.toCharArray()) {
            String keyToSend = (c == ' ') ? "space" : String.valueOf(c);
            Client.sendKeystroke(keyToSend);
            Condition.sleep(75, 150);
        }
        Condition.sleep(800, 1200);
        Client.tap(new Rectangle(181, 239, 378, 21));

        // Reset search bar
        Logger.log("Reset search bar");
        Client.tap(new Rectangle(638, 458, 107, 19));
        Condition.sleep(1600, 2300);

        Logger.log("Disable world switch confirmations");
        Client.tap(new Rectangle(239, 203, 326, 18));
        // Type level up
        String textToSend3 = "world switch";
        for (char c : textToSend3.toCharArray()) {
            String keyToSend = (c == ' ') ? "space" : String.valueOf(c);
            Client.sendKeystroke(keyToSend);
            Condition.sleep(75, 150);
        }
        Condition.sleep(800, 1200);
        Client.tap(new Rectangle(181, 239, 378, 21));

        Logger.log("WE ARE DONE, GREAT SUCCESS!");
        Logger.log("Close settings tab");
        Client.tap(new Rectangle(567, 170, 13, 14));
        Condition.sleep(1250, 2000);

        // SET TO TRUE HERE AS WE ARE DONE
        setSettingsDone = true;
        
        return false;
    }
}
