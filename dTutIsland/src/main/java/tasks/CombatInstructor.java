package tasks;

import helpers.utils.ItemList;
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

public class CombatInstructor extends Task {
    public boolean activate() {
        return !combatInstructorDone;
    }

    private boolean firstWalkDone = false;
    private boolean firstStepDone = false;
    private boolean secondStepDone = false;
    private boolean thirdStepDone = false;
    private boolean fourthStepDone = false;
    private boolean fifthStepDone = false;
    private boolean sixthStepDone = false;
    private boolean seventhStepDone = false;
    private boolean eightStepDone = false;
    private boolean ninethStepDone = false;

    private final static Rectangle fightUIBox = new Rectangle(77, 155, 141, 33);
    private final static Rectangle rangeSearchArea = new Rectangle(203, 186, 221, 130);
    private final static Rectangle searchArea = new Rectangle(77, 156, 507, 335);
    private static final List<Color> combatInstructorColors = Arrays.asList(
            Color.decode("#56150f"),
            Color.decode("#9a7455"),
            Color.decode("#a22415"),
            Color.decode("#9a8f8e")
    );
    private static final List<Color> ratColors = Arrays.asList(
            Color.decode("#8f8f84"),
            Color.decode("#717169"),
            Color.decode("#9b9b8f")
    );

    @Override
    public boolean execute() {
        Logger.log("Running Combat Instructor task");

        if (!firstWalkDone) {
            Logger.log("Walk to Combat Instructor");
            Walker.walkTo(new Tile(12423, 37781, 0));
            Player.waitTillNotMoving(7);
            firstWalkDone = true;
        }

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(combatInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Combat Instructor could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Combat Instructor located. Tapping.");
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

            Logger.log("Open equipment tab");
            Client.tap(new Rectangle(805, 265, 27, 28));
            Condition.sleep(600, 1000);

            if (GameTabs.isTabOpen(UITabs.EQUIP)) {
                Logger.log("Equipment tab is open, proceed!");
                firstStepDone = true;
            } else {
                return false;
            }
        }

        if (!secondStepDone) {
            boolean daggerFound = false;
            Logger.log("Locate bronze dagger");
            if (!GameTabs.isTabOpen(UITabs.INVENTORY)) {
                GameTabs.openTab(UITabs.INVENTORY);
            }
            Rectangle bronzeDagger = Inventory.findItem(ItemList.BRONZE_DAGGER_1205, 0.7, null);

            if (bronzeDagger != null) {
                Logger.log("Bronze dagger found: " + bronzeDagger.toString());
                daggerFound = true;
            }

            Logger.log("Open worn equipment");
            if (!GameTabs.isTabOpen(UITabs.EQUIP)) {
                GameTabs.openTab(UITabs.EQUIP);
            }

            Client.tap(new Rectangle(612, 441, 28, 26));
            Condition.sleep(1200, 1600);

            if (daggerFound) {
                Client.tap(bronzeDagger);
                Condition.sleep(500, 1000);
                // Close interface
                Client.tap(new Rectangle(562, 175, 13, 13));
                Condition.sleep(500, 1000);
            } else {
                // Close interface
                Client.tap(new Rectangle(562, 175, 13, 13));
                Condition.sleep(500, 1000);

                if (!GameTabs.isTabOpen(UITabs.INVENTORY)) {
                    GameTabs.openTab(UITabs.INVENTORY);
                }

                Inventory.tapItem(ItemList.BRONZE_DAGGER_1205, 0.7);
            }
            secondStepDone = true;
        }

        if (!thirdStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(combatInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Combat Instructor could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Combat Instructor located. Tapping.");
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

            Logger.log("Equip new gear");
            Inventory.tapItem(ItemList.BRONZE_SWORD_1277, 0.7);
            Condition.sleep(300, 400);
            Inventory.tapItem(ItemList.WOODEN_SHIELD_1171, 0.7);
            Condition.sleep(300, 400);

            thirdStepDone = true;
        }

        if (!fourthStepDone) {
            Logger.log("Open combat tab");
            Client.tap(new Rectangle(806, 382, 26, 29));
            Condition.sleep(700, 1000);

            if (GameTabs.isTabOpen(UITabs.COMBAT)) {
                Logger.log("Combat tab is open, proceed!");
            } else {
                return false;
            }

            Logger.log("Move to gate (outside)");
            Walker.walkTo(new Tile(12443, 37821, 0));
            Player.waitTillNotMoving(7);
            Logger.log("Go through gate");
            Client.tap(new Rectangle(415, 263, 10, 23));
            Condition.sleep(2500, 3000);

            if (Player.atTile(new Tile(12439, 37821, 0)) || Player.atTile(new Tile(12439, 37825, 0))) {
                Logger.log("We are now inside the fence");
                fourthStepDone = true;
            } else {
                return false;
            }
        }

        if (!fifthStepDone) {
            Logger.log("Fight rat with melee");
            List<Point> points = Client.getPointsFromColorsInRect(ratColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Rat(s) could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Rat(s) located. Tapping.");
                Client.tap(points);
                Condition.sleep(5000);

                Logger.log("Checking if NPC dialogue is open");
                Condition.wait(() -> fightBoxVisible(), 100, 100);

                if (fightBoxVisible()) {
                    Logger.log("Fight box UI is open");
                    Logger.log("Wait till done with fighting");
                    Condition.wait(() -> doneFighting(), 100, 750);

                    if (doneFighting()) {
                        Logger.log("Done fighting!");
                        fifthStepDone = true;
                    } else {
                        return false;
                    }
                } else {
                    Logger.log("Fight box UI is not open, retrying!");
                    return false;
                }
            }
        }

        if (!sixthStepDone) {
            Logger.log("Exit fight cave");
            Walker.step(new Tile(12439, 37825, 0));
            Client.tap(new Rectangle(477, 226, 8, 21));
            Condition.sleep(2500, 3000);

            if (Player.atTile(new Tile(12443, 37825, 0)) || Player.atTile(new Tile(12443, 37821, 0))) {
                Logger.log("We are now outside of the fight cave");
                sixthStepDone = true;
            } else {
                return false;
            }
        }

        if (!seventhStepDone) {
            GameTabs.openTab(UITabs.INVENTORY);
            Logger.log("Walk to Combat Instructor");
            Walker.walkTo(new Tile(12423, 37781, 0));
            Player.waitTillNotMoving(7);

            List<Point> points = Client.getPointsFromColorsInRect(combatInstructorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Combat Instructor could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Combat Instructor located. Tapping.");
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

            if (Inventory.contains(ItemList.SHORTBOW_841, 0.7)) {
                seventhStepDone = true;
            } else {
                return false;
            }
        }

        if (!eightStepDone) {
            Logger.log("Equip ranged equipment");
            Inventory.tapItem(ItemList.SHORTBOW_841, 0.7);
            Condition.sleep(300, 400);
            Inventory.tapItem(ItemList.BRONZE_ARROW_1_897, 0.7);

            Logger.log("Walk to ranged tile");
            Walker.step(new Tile(12427, 37797, 0));

            Logger.log("Fight rat with ranged");
            List<Point> points = Client.getPointsFromColorsInRect(ratColors, rangeSearchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Rat(s) could not be located using the specified colors. Retrying...");
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Rat(s) located. Tapping.");
                Client.tap(points);
                Condition.sleep(5000);

                Logger.log("Checking if NPC dialogue is open");
                Condition.wait(() -> fightBoxVisible(), 100, 100);

                if (fightBoxVisible()) {
                    Logger.log("Fight box UI is open");
                    Logger.log("Wait till done with fighting");
                    Condition.wait(() -> doneFighting(), 100, 750);

                    if (doneFighting()) {
                        Logger.log("Done fighting!");
                        eightStepDone = true;
                    } else {
                        return false;
                    }
                } else {
                    Logger.log("Fight box UI is not open, retrying!");
                    return false;
                }
            }
        }

        if (!ninethStepDone) {
            Logger.log("Done with fighting instructions, exiting cave!");
            Walker.step(new Tile(12443, 37849, 0));
            Client.tap(new Rectangle(430, 191, 21, 28));
            Condition.wait(() -> Player.atTile(new Tile(12443, 12249, 0)), 100, 75);

            if (Player.atTile(new Tile(12443, 12249, 0))) {
                Logger.log("We are now at the next stage, back on the tut island!");
                ninethStepDone = true;
            } else {
                return false;
            }
        }

        // SET TO TRUE HERE AS WE ARE DONE
        if (ninethStepDone) {
            combatInstructorDone = true;
        }

        return false;
    }

    private boolean fightBoxVisible() {
        return Client.isColorInRect(Color.decode("#c80000"), fightUIBox, 5) || Client.isColorInRect(Color.decode("#00c800"), fightUIBox, 5);
    }

    private boolean doneFighting() {
        if (!fightBoxVisible()) {
            return false;
        } else {
            return !Client.isColorInRect(Color.decode("#c80000"), fightUIBox, 5) && !Client.isColorInRect(Color.decode("#00c800"), fightUIBox, 5);
        }
    }
}
