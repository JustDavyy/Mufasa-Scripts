package tasks;

import helpers.utils.Area;
import helpers.utils.Tile;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static helpers.Interfaces.*;
import static main.dTutIsland.*;
import static main.dTutIsland.NPCHeaderCheckRect;

public class PrayerInstructor extends Task {
    public boolean activate() {
        return !prayerInstructorDone;
    }

    private boolean firstWalkDone = false;
    private boolean firstStepDone = false;
    private boolean secondStepDone = false;
    private boolean thirdStepDone = false;
    private boolean fourthStepDone = false;
    private boolean fifthStepDone = false;
    private boolean sixthStepDone = false;
    private boolean seventhStepDone = false;

    Tile[] pathToInstructor = new Tile[] {
            new Tile(12535, 12240, 0),
            new Tile(12536, 12224, 0),
            new Tile(12533, 12205, 0),
            new Tile(12526, 12188, 0),
            new Tile(12517, 12177, 0),
            new Tile(12500, 12173, 0)
    };

    private final static Rectangle searchArea = new Rectangle(227, 206, 359, 265);
    private static final List<Color> prayerInstructorColors = Arrays.asList(
            Color.decode("#885b31"),
            Color.decode("#704b28")
    );

    @Override
    public boolean execute() {
        Logger.log("Running Brother Brace task");

        if (!firstWalkDone) {
            Logger.log("Walking to chapel");
            Walker.walkPath(pathToInstructor);
            Player.waitTillNotMoving(7);

            if (Player.within(new Area(new Tile(12481, 12186, 0), new Tile(12513, 12158, 0)))) {
                Logger.log("We are now in the chapel.");
                firstWalkDone = true;
            } else {
                return false;
            }
        }

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(prayerInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Brother Brace could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Brother Brace located. Tapping.");
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

            Logger.log("Open prayer tab");
            Client.tap(new Rectangle(806, 304, 26, 28));
            Condition.sleep(800, 1200);

            if (GameTabs.isTabOpen(UITabs.PRAYER)) {
                Logger.log("Prayer tab is now open, proceed!");
                firstStepDone = true;
            } else {
                return false;
            }
        }

        if (!secondStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(prayerInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Brother Brace could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Brother Brace located. Tapping.");
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

            Logger.log("Open friends tab");
            Client.tap(new Rectangle(843, 383, 27, 27));
            Condition.sleep(800, 1200);

            if (GameTabs.isTabOpen(UITabs.FRIENDS)) {
                Logger.log("Friends tab is now open, proceed!");
                secondStepDone = true;
            } else {
                return false;
            }
        }

        if (!thirdStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(prayerInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Brother Brace could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Brother Brace located. Tapping.");
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
            Client.sendKeystroke("space");
            Condition.sleep(600, 800);
            Client.sendKeystroke("space");

            thirdStepDone = true;
        }

        if (!fourthStepDone) {
            Logger.log("Leave chapel");
            Walker.step(new Tile(12487, 12161, 0));
            Client.tap(new Rectangle(427, 284, 32, 26));
            Condition.sleep(2500, 3000);

            if (Player.atTile(new Tile(12487, 12157, 0))) {
                Logger.log("Success! We are now outside the chapel");
                fourthStepDone = true;
            } else {
                return false;
            }
        }

        // SET TO TRUE HERE AS WE ARE DONE
        if (fourthStepDone) {
            prayerInstructorDone = true;
        }

        return false;
    }
}
