package tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static helpers.Interfaces.*;
import static main.dTutIsland.*;

public class CancelBankPin extends Task {
    public boolean activate() {
        return !cancelBankPinDone;
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
        Logger.log("Running Cancel Bank Pin task");

        if (!firstStepDone) {
            Logger.log("First step, walk to stairs");
            Walker.walkPath(pathToStairs);
            Player.waitTillNotMoving(10);
            firstStepDone = true;
        }

        if (!secondStepDone) {
            Logger.log("Stepping to stairs");
            Walker.step(new Tile(12823, 12585, 0));

            if (Player.atTile(new Tile(12823, 12585, 0))) {
                Logger.log("We are at the stairs");

                Logger.log("Tap stairs");
                Client.tap(new Rectangle(346, 331, 34, 20));
                Condition.wait(() -> Player.within(floor1), 100, 50);

                if (Player.within(floor1)) {
                    Logger.log("We are now on the 1st floor, great SUCCESS!");
                    secondStepDone = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (!thirdStepDone) {
            Logger.log("Go up one more floor");
            Client.tap(new Rectangle(401, 329, 41, 40));
            Condition.sleep(2000, 2500);
            Client.sendKeystroke("1");
            Condition.wait(() -> Player.within(topFloor), 100, 50);

            if (Player.within(topFloor)) {
                Logger.log("We are now on the top floor, great SUCCESS!");
                thirdStepDone = true;
            } else {
                return false;
            }
        }

        if (!fourthStepDone) {
            GameTabs.openTab(UITabs.INVENTORY);
            Logger.log("Move to the bank");
            Walker.step(new Tile(12831, 12629, 2));

            Logger.log("Tap banker");
            Client.tap(new Rectangle(448, 165, 10, 18));

            if (Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5)) {
                Logger.log("NPC Dialogue is open");
            } else {
                Logger.log("NPC Dialogue is not open, retrying!");
                return false;
            }

            Logger.log("Continue dialogues");
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.sendKeystroke("3");
            Condition.sleep(1200, 1750);

            Logger.log("Delete pin");
            Client.tap(new Rectangle(183, 418, 116, 9));
            Condition.sleep(1200, 1750);
            Logger.log("Confirm delete pin");
            Client.tap(new Rectangle(215, 299, 243, 15));
            Condition.sleep(1200, 1750);
            Logger.log("Close bankpin interface");
            Client.tap(new Rectangle(555, 175, 15, 15));
            fourthStepDone = true;
        }


        // SET TO TRUE HERE AS WE ARE DONE
        if (fourthStepDone) {
            cancelBankPinDone = true;
        }

        return false;
    }
}
