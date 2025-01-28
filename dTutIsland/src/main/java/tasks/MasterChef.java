package tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static helpers.Interfaces.*;
import static main.dTutIsland.*;

public class MasterChef extends Task {
    public boolean activate() {
        return !masterChefDone;
    }

    private final static Tile expertAreaTile = new Tile(12411, 12133, 0);
    private boolean firstWalkDone = false;
    private boolean firstStepDone = false;
    private boolean secondStepDone = false;
    private boolean thirdStepDone = false;
    private boolean fourthStepDone = false;
    private boolean fifthStepDone = false;
    private boolean sixthStepDone = false;

    private final static Rectangle searchArea = new Rectangle(174, 157, 299, 261);
    private static List<Color> masterChefColors = Arrays.asList(
            Color.decode("#4b6f4b"),
            Color.decode("#3c583c"),
            Color.decode("#3a3a4b"),
            Color.decode("#454556")
    );

    @Override
    public boolean execute() {
        Logger.log("Running Master Chef task");

        if (!firstWalkDone) {
            Walker.step(new Tile(12315, 12085, 0));
            Client.tap(new Rectangle(415, 256, 9, 40));
            Condition.sleep(2500, 3000);

            if (Player.atTile(new Tile(12311, 12085, 0))) {
                Logger.log("We are now inside the Master Chef building");
                firstWalkDone = true;
            } else {
                return false;
            }
        }

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(masterChefColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Master Chef could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Master Chef located. Tapping.");
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

            Logger.log("Make dough");
            Inventory.tapItem(ItemList.POT_OF_FLOUR_1933, 0.7);
            Condition.sleep(300, 450);
            Inventory.tapItem(ItemList.BUCKET_OF_WATER_1929, 0.7);
            Condition.sleep(350, 700);

            Logger.log("Go to stove/oven/furnace w/e u fucks call it");
            Walker.step(new Tile(12299, 12077, 0));
            Logger.log("Tap dat shit");
            Client.tap(new Rectangle(455, 310, 37, 24));
            Condition.wait(() -> Inventory.contains(ItemList.BREAD_2309, 0.7), 100, 75);

            if (Inventory.contains(ItemList.BREAD_2309, 0.7)) {
                Logger.log("Done baking bread, continue!");
                firstStepDone = true;
            } else {
                return false;
            }
        }

        if (!secondStepDone) {
            Logger.log("Move to exit door");
            Walker.step(new Tile(12291, 12109, 0));
            Client.tap(new Rectangle(410, 235, 4, 29));
            Condition.sleep(2500, 3000);

            if (Player.atTile(new Tile(12287, 12109, 0))) {
                Logger.log("We are now outside the Master Chef's house, on to the next step!");
                secondStepDone = true;
            } else {
                return false;
            }
        }

        // SET TO TRUE HERE AS WE ARE DONE
        if (secondStepDone) {
            masterChefDone = true;
        }

        return false;
    }
}
