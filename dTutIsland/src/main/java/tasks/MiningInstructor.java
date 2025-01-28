package tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.SmithItems;
import helpers.utils.Tile;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static helpers.Interfaces.*;
import static main.dTutIsland.*;
import static main.dTutIsland.NPCHeaderCheckRect;

public class MiningInstructor extends Task {
    public boolean activate() {
        return !miningInstructorDone;
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
            new Tile(12342, 37829, 0),
            new Tile(12326, 37819, 0),
            new Tile(12318, 37799, 0),
            new Tile(12320, 37778, 0),
            new Tile(12323, 37763, 0)
    };

    private final static Rectangle searchArea = new Rectangle(77, 156, 507, 335);
    private static final List<Color> miningInstructorColors = Arrays.asList(
            Color.decode("#914497"),
            Color.decode("#67316a"),
            Color.decode("#79397e")
    );
    private static final List<Color> tinOreColors = Arrays.asList(
            Color.decode("#938989"),
            Color.decode("#867b7b"),
            Color.decode("#645c5b")
    );
    private static final List<Color> copperOreColors = Arrays.asList(
            Color.decode("#755842"),
            Color.decode("#836549"),
            Color.decode("#503d2d")
    );

    private final static Rectangle tinSearchArea = new Rectangle(303, 224, 116, 154);
    private final static Rectangle copperSearchArea = new Rectangle(450, 172, 139, 227);

    @Override
    public boolean execute() {
        Logger.log("Running Mining Instructor task");

        if (!firstWalkDone) {
            Logger.log("Moving to Mining Instructor");
            Walker.walkPath(pathToInstructor);
            Player.waitTillNotMoving(7);

            if (Player.within(new Area(new Tile(12307, 37777, 0), new Tile(12341, 37742, 0)))) {
                Logger.log("At the instructor area");
                firstWalkDone = true;
            } else {
                return false;
            }
        }

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(miningInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Mining Instructor could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Mining Instructor located. Tapping.");
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

            if (Client.isColorInRect(NPCHeaderColor, NPCHeaderCheckRect, 5)) {
                Logger.log("NPC Dialogue is open");
                return false;
            } else {
                Logger.log("NPC Dialogue is done!");
                firstStepDone = true;
            }
        }

        if (!secondStepDone) {
            Logger.log("Mining tin ore");
            Walker.walkTo(new Tile(12311, 37769, 0));
            Player.waitTillNotMoving(7);

            List<Point> points = Client.getPointsFromColorsInRect(tinOreColors, tinSearchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Tin ore could not be located using the specified colors. Retrying...");
                Walker.walkTo(new Tile(12311, 37769, 0));
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Tin ore located. Tapping.");
                Client.tap(points);

                Logger.log("Waiting till we have tin ore.");
                Condition.wait(() -> Inventory.contains(ItemList.TIN_ORE_438, 0.7), 100, 100);

                if (Inventory.contains(ItemList.TIN_ORE_438, 0.7)) {
                    Logger.log("Tin ore mined!");
                    secondStepDone = true;
                } else {
                    Logger.log("No tin ore mined, retrying!");
                    return false;
                }
            }
        }

        if (!thirdStepDone) {
            Logger.log("Mining copper ore");
            Walker.walkTo(new Tile(12327, 37749, 0));
            Player.waitTillNotMoving(7);

            List<Point> points = Client.getPointsFromColorsInRect(copperOreColors, copperSearchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Copper could not be located using the specified colors. Retrying...");
                Walker.walkTo(new Tile(12327, 37749, 0));
                Condition.sleep(1500, 2250);
                return false;
            } else {
                int slotsUsed = Inventory.usedSlots();
                Logger.log("Copper ore located. Tapping.");
                Client.tap(points);

                Logger.log("Waiting till we have copper ore.");
                Condition.wait(() -> doneMining(slotsUsed), 100, 100);

                if (doneMining(slotsUsed)) {
                    Logger.log("Copper ore mined!");
                    thirdStepDone = true;
                } else {
                    Logger.log("No copper ore mined, retrying!");
                    return false;
                }
            }
        }

        if (!fourthStepDone) {
            Logger.log("Going to furnace");
            Walker.step(new Tile(12315, 37741, 0));
            Logger.log("Smelting bronze bar");
            Client.tap(new Rectangle(398, 349, 85, 70));
            Condition.wait(() -> Inventory.contains(ItemList.BRONZE_BAR_2349, 0.7), 100, 100);

            if (Inventory.contains(ItemList.BRONZE_BAR_2349, 0.7)) {
                Logger.log("Done smelting bronze bar!");
                fourthStepDone = true;
            } else {
                return false;
            }
        }

        if (!fifthStepDone) {
            Walker.walkTo(new Tile(12319, 37761, 0));
            Player.waitTillNotMoving(10);

            List<Point> points = Client.getPointsFromColorsInRect(miningInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Mining Instructor could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Mining Instructor located. Tapping.");
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

            fifthStepDone = true;
        }

        if (!sixthStepDone) {
            if (Inventory.contains(ItemList.BRONZE_BAR_2349, 0.7)) {
                Logger.log("Go to anvil");
                Walker.step(new Tile(12327, 37745, 0));
                Logger.log("Smith bronze dagger");
                Client.tap(new Rectangle(491, 273, 16, 12));
                Condition.sleep(1250, 2000);
                Client.tap(new Rectangle(106, 205, 24, 28));
                Condition.wait(() -> Inventory.contains(ItemList.BRONZE_DAGGER_1205, 0.7), 100, 100);

                if (Inventory.contains(ItemList.BRONZE_DAGGER_1205, 0.7)) {
                    Logger.log("Done smithing bronze dagger");
                    sixthStepDone = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (!seventhStepDone) {
            Logger.log("Exit mining/smithing area");
            Walker.step(new Tile(12375, 37757, 0));
            Client.tap(new Rectangle(469, 263, 9, 20));
            Condition.sleep(2500, 3000);

            if (Player.atTile(new Tile(12379, 37757, 0)) || Player.atTile(new Tile(12379, 37761, 0))) {
                Logger.debugLog("Successfully went through the gate. Proceed to combat tutor!");
                seventhStepDone = true;
            } else {
                return false;
            }
        }

        // SET TO TRUE HERE AS WE ARE DONE
        if (seventhStepDone) {
            miningInstructorDone = true;
        }

        return false;
    }

    private boolean doneMining(int slotsUsed) {
        int currentSlotsUsed = Inventory.usedSlots();

        return currentSlotsUsed == slotsUsed + 1;
    }
}
