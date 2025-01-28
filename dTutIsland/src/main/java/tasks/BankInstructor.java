package tasks;

import helpers.utils.Tile;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.dTutIsland.*;
import static main.dTutIsland.clickToContinueRect;

public class BankInstructor extends Task {
    public boolean activate() {
        return !bankInstructorDone;
    }

    @Override
    public boolean execute() {
        Logger.log("Running Bank Instructor task");

        Logger.log("Walk to bank booth");
        Walker.step(new Tile(12487, 12241, 0));

        Logger.log("Open bank");
        Client.tap(new Rectangle(439, 208, 27, 32));
        Condition.sleep(1750, 2250);

        Logger.log("Handle more bank space pop-up");
        Client.tap(new Rectangle(223, 386, 50, 19));
        Condition.sleep(600, 1000);

        Logger.log("Close bank");
        Client.tap(new Rectangle(556, 172, 11, 12));
        Condition.sleep(1000, 1400);

        Logger.log("Tap poll booth");
        Client.tap(new Rectangle(288, 346, 21, 21));
        Condition.sleep(2500, 3250);

        Logger.log("Continue dialogues");
        Client.tap(clickToContinueRect);
        Condition.sleep(800, 1400);
        Client.tap(clickToContinueRect);
        Condition.sleep(800, 1400);
        Client.tap(clickToContinueRect);
        Condition.sleep(800, 1400);

        Logger.log("Proceed through door");
        Client.tap(new Rectangle(644, 140, 8, 15));
        Condition.wait(() -> Player.atTile(new Tile(12499, 12245, 0)), 100, 75);
        Condition.sleep(2000, 2500);

        if (Player.atTile(new Tile(12499, 12245, 0))) {
            Logger.log("Went through the door with success.");
            bankInstructorDone = true;
        } else {
            return false;
        }

        return false;
    }
}
