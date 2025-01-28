package tasks;

import helpers.utils.Tile;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static helpers.Interfaces.*;
import static main.dTutIsland.*;

public class GielinorGuide extends Task {
    public boolean activate() {
        return !gielinorGuideDone;
    }

    private static List<Color> gielinorGuideColors = Arrays.asList(
            Color.decode("#711a12"),
            Color.decode("#942015"),
            Color.decode("#65180f")
    );
    private static final Rectangle searchAreaROI = new Rectangle(162, 186, 589, 283);
    private static final Rectangle settingsTab = new Rectangle(846, 461, 24, 25);
    private static final Rectangle compassRect = new Rectangle(705, 11, 21, 19);
    private static final Tile doorInsideTile = new Tile(12387, 12177, 0);
    private static final Rectangle tapDoorRect = new Rectangle(465, 252, 4, 14);
    private static final Tile doorOutsideTile = new Tile(12391, 12177, 0);

    private boolean firstStepDone = false;
    private boolean secondStepDone = false;

    @Override
    public boolean execute() {
        Logger.log("Running Gielinor Guide task");

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(gielinorGuideColors, searchAreaROI, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Gielinor Guide could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Gielinor Guide located. Tapping.");
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

            Logger.log("Continue Gielinor guide dialogue");
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

            Logger.log("Select experienced player");
            Rectangle experiencedOption = null;
            experiencedOption = Objects.getBestMatch("/imgs/experienced.png", 0.8);

            if (experiencedOption != null) {
                Logger.log("Experienced player option was found: ");
                Client.tap(experiencedOption);
            } else {
                Logger.log("Experienced player option was NOT FOUND");
                Logger.log("We ran into an error, stopping script... SORRY!");
                Script.stop();
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

            Logger.log("Open settings menu");
            Client.tap(settingsTab);

            firstStepDone = true;
        }

        if (!secondStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(gielinorGuideColors, searchAreaROI, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Gielinor Guide could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Gielinor Guide located. Tapping.");
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

            Logger.log("Change camera angle");
            Client.tap(compassRect);
            Condition.sleep(500, 900);
            Client.moveCameraUp();

            Logger.log("Move to door");
            Walker.step(doorInsideTile);
            Condition.sleep(400, 600);

            Logger.log("Open door");
            Client.tap(tapDoorRect);
            Condition.sleep(3500, 4000);

            if (Player.atTile(doorOutsideTile)) {
                Logger.log("We are now outside of the door");
                secondStepDone = true;
            }
        }

        // SET TO TRUE HERE AS WE ARE DONE
        if (secondStepDone) {
            GameTabs.openTab(UITabs.SETTINGS);
            Client.tap(new Rectangle(734, 233, 46, 18));
            Condition.sleep(1200, 1500);
            Game.setZoom("3");
            Condition.sleep(500, 800);
            GameTabs.openTab(UITabs.INVENTORY);
            gielinorGuideDone = true;
        }

        return false;
    }
}
