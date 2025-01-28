package tasks;

import helpers.annotations.AllowedValue;
import helpers.utils.Tile;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static helpers.Interfaces.*;
import static main.dTutIsland.*;

public class SetAccountType extends Task {
    public boolean activate() {
        return !setAccountTypeDone;
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
            new Tile(12481, 12138, 0),
            new Tile(12479, 12114, 0),
            new Tile(12492, 12095, 0),
            new Tile(12512, 12089, 0),
            new Tile(12528, 12094, 0)
    };

    private final static Rectangle searchArea = new Rectangle(87, 165, 466, 311);
    private static final List<Color> ironmanTutorColors = Arrays.asList(
            Color.decode("#c1b7b7"),
            Color.decode("#a19595"),
            Color.decode("#685f5f"),
            Color.decode("#9a8f8e"),
            Color.decode("#b9adac")
    );

    @Override
    public boolean execute() {
        Logger.log("Running Set Account Type task");

        if (!firstWalkDone) {
            Logger.log("Walking to instructor");
            Walker.walkPath(pathToInstructor);
            Player.waitTillNotMoving(7);
            Condition.sleep(1000, 1500);
            firstWalkDone = true;
        }

        if (accountType.equals("Fucking normie")) {
            Logger.log("REEE you selected fucking normie accounts, no account type to set!");
            setAccountTypeDone = true;
            return false;
        }

        if (!firstStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(ironmanTutorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Ironman Tutor could not be located using the specified colors. Retrying...");
                Walker.walkTo(new Tile(12523, 12097, 0));
                Player.waitTillNotMoving(7);
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Ironman Tutor located. Tapping.");
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
            Client.tap(new Rectangle(194, 57, 155, 11));
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

            firstStepDone = true;
        }

        if (!secondStepDone) {
            List<Point> points = Client.getPointsFromColorsInRect(ironmanTutorColors, searchArea, 5);

            if (points.isEmpty()) {
                // If no points are found, log a retry message
                Logger.log("Ironman Tutor could not be located using the specified colors. Retrying...");
                Walker.walkTo(new Tile(12523, 12097, 0));
                Player.waitTillNotMoving(7);
                Condition.sleep(1500, 2250);
                return false;
            } else {
                Logger.log("Ironman Tutor located. Tapping.");
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
            Client.tap(new Rectangle(148, 75, 250, 13));
            Condition.sleep(800, 1400);
            Client.tap(clickToContinueRect);
            Condition.sleep(1800, 2400);

            Logger.log("Check if Ironman Setup is open");
            Rectangle setup = null;
            setup = Objects.getBestMatch("/imgs/ironmansetup.png", 0.8);

            if (setup != null) {
                Logger.log("Ironman setup tab is open!");
                secondStepDone = true;
            } else {
                Logger.log("Ironman setup tab is not open!");
                Logger.log("Use image recog as fallback");

                Rectangle changemode = null;
                changemode = Objects.getBestMatch("/imgs/changemode.png", 0.8);

                if (changemode != null) {
                    Client.tap(changemode);
                    Condition.sleep(1800, 2400);
                    Client.tap(clickToContinueRect);
                    Condition.sleep(1800, 2400);
                    secondStepDone = true;
                    return false;
                } else {
                    Logger.log("We ran into an error, SORRY!");
                    return false;
                }
            }
        }

        if (!thirdStepDone) {
            Logger.log("Set ironman type! LETS FUCKING GOOO");
            Logger.log("Type chosen: " + accountType);

            GameTabs.openTab(UITabs.INVENTORY);

            switch (accountType) {
                case "Standard ironman":
                    Client.tap(new Rectangle(139, 405, 144, 20));
                    Condition.sleep(500, 1000);
                    break;
                case "Hardcore ironman":
                    Logger.debugLog("Drag menu down");
                    Client.drag(new Rectangle(563, 240, 9, 95), new Rectangle(561, 497, 21, 35), 600 + new Random().nextInt(901));
                    Condition.sleep(800, 1200);
                    Client.tap(new Rectangle(140, 274, 140, 16));
                    Condition.sleep(400, 600);
                    break;
                case "Ultimate ironman":
                    Logger.debugLog("Drag menu down");
                    Client.drag(new Rectangle(563, 240, 9, 95), new Rectangle(561, 497, 21, 35), 600 + new Random().nextInt(901));
                    Condition.sleep(800, 1200);
                    Client.tap(new Rectangle(141, 389, 138, 19));
                    Condition.sleep(400, 600);
                    break;
                case "Group ironman":
                    Client.tap(new Rectangle(371, 326, 145, 22));
                    Condition.sleep(500, 1000);
                    break;
                case "Hardcore group ironman":
                    Logger.debugLog("Drag menu down");
                    Client.drag(new Rectangle(563, 240, 9, 95), new Rectangle(561, 497, 21, 35), 600 + new Random().nextInt(901));
                    Condition.sleep(800, 1200);
                    Client.tap(new Rectangle(375, 236, 139, 17));
                    Condition.sleep(400, 600);
                    break;
                default:
                    Logger.log("Un fucking known account type, we fucked up, stopping script!");
                    Script.stop();
            }

            Logger.log("Yes I know we need to set a pin... fucking degens!");
            Logger.log("Tap proceed");
            Client.tap(new Rectangle(378, 377, 52, 19));
            Condition.sleep(700, 1000);

            String bankPin = generateBankPin();
            Logger.log("Setting bank pin: " + bankPin);
            Bank.setBankPin(bankPin);
            Condition.sleep(1000, 1200);
            Bank.setBankPin(bankPin);
            Condition.sleep(1500, 2000);

            thirdStepDone = true;
        }

        // SET TO TRUE HERE AS WE ARE DONE
        if (thirdStepDone) {
            setAccountTypeDone = true;
        }

        return false;
    }

    private String generateBankPin() {
        Random random = new Random();
        StringBuilder pin = new StringBuilder();

        // Generate 4 random digits (0-9)
        for (int i = 0; i < 4; i++) {
            int digit = random.nextInt(10); // Random number between 0 and 9
            pin.append(digit);
        }

        return pin.toString();
    }
}
