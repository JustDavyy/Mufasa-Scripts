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

public class MagicInstructor extends Task {
    public boolean activate() {
        return !magicInstructorDone;
    }

    private boolean firstWalkDone = false;
    private boolean firstStepDone = false;
    private boolean secondStepDone = false;
    private boolean thirdStepDone = false;
    private boolean fourthStepDone = false;
    private boolean fifthStepDone = false;
    private boolean sixthStepDone = false;
    private boolean seventhStepDone = false;

    private final static Rectangle fightUIBox = new Rectangle(77, 155, 141, 33);
    private final static Rectangle searchArea = new Rectangle(73, 154, 514, 328);
    private static final List<Color> magicInstructorColors = Arrays.asList(
            Color.decode("#155f94"),
            Color.decode("#124b71"),
            Color.decode("#156ca6"),
            Color.decode("#12588a")
    );

    private final static Rectangle chickenSearchArea = new Rectangle(339, 159, 214, 89);
    private static final List<Color> chickenColors = Arrays.asList(
            Color.decode("#8f7c5e"),
            Color.decode("#bfa87e"),
            Color.decode("#cdb387")
    );

    @Override
    public boolean execute() {
        Logger.log("Running Magic Instructor task");

        if (!firstWalkDone) {
            Walker.walkTo(new Tile(12563, 12101, 0));
            Player.waitTillNotMoving(7);
            firstWalkDone = true;
        }

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(magicInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Magic Instructor could not be located using the specified colors. Retrying...");
                Walker.walkTo(new Tile(12563, 12101, 0));
                Player.waitTillNotMoving(7);
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Magic Instructor located. Tapping.");
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

            Logger.log("Open spellbook tab");
            Client.tap(new Rectangle(805, 343, 26, 27));
            Condition.sleep(800, 1200);

            if (GameTabs.isTabOpen(UITabs.MAGIC)) {
                Logger.log("Magic tab is open, success and proceed!");
                firstStepDone = true;
            } else {
                return false;
            }
        }

        if (!secondStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(magicInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Magic Instructor could not be located using the specified colors. Retrying...");
                Walker.walkTo(new Tile(12563, 12101, 0));
                Player.waitTillNotMoving(7);
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Magic Instructor located. Tapping.");
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

            Walker.step(new Tile(12555, 12113, 0));

            secondStepDone = true;
        }

        if (!thirdStepDone) {
            Logger.log("Casting spell on chicken");
            GameTabs.openTab(UITabs.MAGIC);

            Logger.log("Tap Air Strike");
            Client.tap(new Rectangle(689, 340, 13, 11));
            Condition.sleep(400, 600);

            List<Point> points = Client.getPointsFromColorsInRect(chickenColors, chickenSearchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Chicken(s) could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Chicken(s) located. Tapping.");
                Client.tap(points);

                Logger.log("Checking if Fight UI box is visible");
                Condition.wait(() -> fightBoxVisible(), 100, 100);

                Condition.sleep(1000, 1500);

                if (fightBoxVisible()) {
                    Logger.log("Fight UI Box is open, cast spell with success!");
                    thirdStepDone = true;
                } else {
                    Logger.log("Fight UI box is NOT open, Failed and retrying!");
                    return false;
                }
            }
        }

        if (!fourthStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(magicInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Magic Instructor could not be located using the specified colors. Retrying...");
                Walker.walkTo(new Tile(12563, 12101, 0));
                Player.waitTillNotMoving(7);
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Magic Instructor located. Tapping.");
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
            Client.sendKeystroke("1");
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(7500, 10000);

            Logger.log("Check if we are within Lumbridge");

            if (Player.within(new Area(new Tile(12864, 12676, 0), new Tile(12994, 12555, 0)))) {
                Logger.log("We are now in Lumbridge, tutorial island is finished!");
                magicInstructorDone = true;
            } else {
                return false;
            }
        }

        return false;
    }


    private boolean fightBoxVisible() {
        return Client.isColorInRect(Color.decode("#c80000"), fightUIBox, 5) || Client.isColorInRect(Color.decode("#00c800"), fightUIBox, 5);
    }
}
