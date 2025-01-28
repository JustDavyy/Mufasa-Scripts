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

public class QuestGuide extends Task {
    public boolean activate() {
        return !questGuideDone;
    }

    private final static Tile outsideDoorTile = new Tile(12343, 12253, 0);
    private final static Tile insideDoorTile = new Tile(12343, 12249, 0);
    private boolean firstWalkDone = false;
    private boolean firstStepDone = false;
    private boolean secondStepDone = false;
    private boolean thirdStepDone = false;
    private boolean fourthStepDone = false;
    private boolean fifthStepDone = false;
    private boolean sixthStepDone = false;

    private final static Tile[] pathToQuestGuide = new Tile[] {
        new Tile(12285, 12122, 0),
                new Tile(12284, 12139, 0),
                new Tile(12291, 12161, 0),
                new Tile(12304, 12184, 0),
                new Tile(12309, 12214, 0),
                new Tile(12311, 12238, 0),
                new Tile(12329, 12252, 0)
    };

    private final static Rectangle searchArea = new Rectangle(169, 180, 407, 284);
    private static List<Color> questGuideColors = Arrays.asList(
            Color.decode("#1a4228"),
            Color.decode("#26653c"),
            Color.decode("#286c40")
    );

    @Override
    public boolean execute() {
        Logger.log("Running Quest Guide task");

        if (!firstWalkDone) {
            Logger.log("Do run part of tutorial");
            Player.toggleRun();
            Condition.sleep(350, 500);
            Player.toggleRun();
            Condition.sleep(1200, 1500);
            if (!Player.isRunEnabled()) {
                Player.toggleRun();
            }

            Logger.log("Run to Quest Guide building");
            Walker.walkPath(pathToQuestGuide);
            Player.waitTillNotMoving(10);
            Walker.step(outsideDoorTile);
            Client.tap(new Rectangle(426, 277, 31, 26));
            Condition.sleep(2500, 3500);

            if (Player.atTile(insideDoorTile)) {
                Logger.log("We are now inside the Quest guide building!");
                firstWalkDone = true;
            } else {
                return false;
            }
        }

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(questGuideColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Quest Guide could not be located using the specified colors. Retrying...");
                Walker.walkTo(new Tile(12343, 12241, 0));
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Quest Guide located. Tapping.");
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

            Logger.log("Continue dialogue");
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Logger.log("Open quests tab");
            Client.tap(new Rectangle(805, 420, 27, 31));
            Condition.sleep(900, 1300);

            if (GameTabs.isTabOpen(UITabs.QUESTS)) {
                firstStepDone = true;
            } else {
                return false;
            }
        }

        if (!secondStepDone) {
            GameTabs.openTab(UITabs.INVENTORY);

            List<Point> points = Client.getPointsFromColorsInRect(questGuideColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Quest Guide could not be located using the specified colors. Retrying...");
                Walker.walkTo(new Tile(12343, 12241, 0));
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Quest Guide located. Tapping.");
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
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(800, 1400);
            secondStepDone = true;
        }

        if (!thirdStepDone) {
            Logger.log("Heading down the ladder");
            Walker.step(new Tile(12351, 12229, 0));
            Client.tap(new Rectangle(434, 319, 34, 27));
            Condition.sleep(5000, 7000);

            if (Player.within(new Area(new Tile(12279, 37868, 0), new Tile(12487, 37720, 0)))) {
                Logger.log("We are now in the dungeon downstairs. Proceeding!");
                thirdStepDone = true;
            } else {
                return false;
            }
        }

        // SET TO TRUE HERE AS WE ARE DONE
        if (thirdStepDone) {
            questGuideDone = true;
        }

        return false;
    }
}
