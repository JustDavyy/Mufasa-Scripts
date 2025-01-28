package tasks;

import helpers.utils.Tile;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Client;
import static main.dTutIsland.*;
import static main.dTutIsland.NPCHeaderCheckRect;

public class AccountGuide extends Task {
    public boolean activate() {
        return !accountGuideDone;
    }

    private boolean firstStepDone = false;
    private boolean secondStepDone = false;
    private boolean thirdStepDone = false;
    private boolean fourthStepDone = false;
    private boolean fifthStepDone = false;
    private boolean sixthStepDone = false;
    private boolean seventhStepDone = false;
    private boolean eightStepDone = false;
    private boolean ninethStepDone = false;

    private final static Rectangle searchArea = new Rectangle(439, 249, 140, 70);
    private static final List<Color> accountGuideColors = Arrays.asList(
            Color.decode("#1e1a1a"),
            Color.decode("#181515"),
            Color.decode("#262424")
    );

    @Override
    public boolean execute() {
        Logger.log("Running Account Guide task");

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(accountGuideColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Account Guide could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Account Guide located. Tapping.");
                Client.tap(points);

                Logger.log("Checking if NPC dialogue is open");
                Condition.wait(() -> Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5), 100, 100);

                if (Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5)) {
                    Logger.log("NPC Dialogue is open");
                } else {
                    Logger.log("NPC Dialogue is not open, retrying!");
                    return false;
                }
            }

            Logger.log("Continue dialogues");
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);

            Logger.log("Open account settings tab");
            Client.tap(new Rectangle(845, 422, 26, 26));
            Condition.sleep(800, 1300);

            if (GameTabs.isTabOpen(UITabs.ACCOUNT)) {
                Logger.log("Account settings tab is open, proceed!");
                firstStepDone = true;
            } else {
                return false;
            }
        }

        if (!secondStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(accountGuideColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Account Guide could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Account Guide located. Tapping.");
                Client.tap(points);

                Logger.log("Checking if NPC dialogue is open");
                Condition.wait(() -> Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5), 100, 100);

                if (Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5)) {
                    Logger.log("NPC Dialogue is open");
                } else {
                    Logger.log("NPC Dialogue is not open, retrying!");
                    return false;
                }
            }

            Logger.log("Continue a fuck ton dialogues");
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.sendKeystroke("space");

            secondStepDone = true;
        }

        if (!thirdStepDone) {
            Logger.log("Go through door");
            Walker.step(new Tile(12515, 12245, 0));
            Client.tap(new Rectangle(477, 248, 8, 16));
            Condition.sleep(2500, 3000);

            if (Player.atTile(new Tile(12519, 12245, 0))) {
                Logger.log("We are ourside, proceed!");
                thirdStepDone = true;
            } else {
                return false;
            }
        }

        // SET TO TRUE HERE AS WE ARE DONE
        if (thirdStepDone) {
            accountGuideDone = true;
        }

        return false;
    }
}
