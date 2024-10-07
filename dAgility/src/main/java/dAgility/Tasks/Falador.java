package dAgility.Tasks;

import dAgility.dAgility;
import helpers.utils.Area;
import helpers.utils.Tile;
import dAgility.utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static helpers.Interfaces.*;
import static dAgility.dAgility.*;

public class Falador extends Task {
    private final Random random = new Random();

    Color baseMogColor = new Color(203, 137, 25);
    List<Color> obstacleColors = Arrays.asList(
            Color.decode("#22ff28"),
            Color.decode("#b8cd9b"),
            Color.decode("#b2c791"),
            Color.decode("#a1a95a"),
            Color.decode("#a6ac5e")
    );
    Rectangle screenROI = new Rectangle(377, 190, 200, 148);
    Rectangle obstacle1InstantPressArea = new Rectangle(552, 143, 7, 7);
    Area obstacle1Area = new Area(
            new Tile(405, 127),
            new Tile(434, 151)
    );
    Tile obstacle1Start = new Tile(421, 138);
    Tile obstacle1End = new Tile(401, 253);
    Rectangle obstacle2InstantPressArea = new Rectangle(509, 248, 12, 10);
    Rectangle obstacle2PressArea = new Rectangle(459, 266, 12, 11);
    Area obstacle2Area = new Area(
            new Tile(398, 245),
            new Tile(408, 255)
    );
    Tile obstacle2Start = new Tile(405, 252);
    Tile obstacle2End = new Tile(416, 251);
    Rectangle obstacle4MogRect = new Rectangle(421, 267, 15, 11);
    Rectangle obstacle3InstantPressArea = new Rectangle(491, 135, 19, 28);
    Tile[] obstacle3FailPath = new Tile[] {
            new Tile(434, 127),
            new Tile(422, 141)
    };
    Area obstacle3FailArea = new Area(
            new Tile(431, 111),
            new Tile(445, 128)
    );
    Rectangle obstacle3PressArea = new Rectangle(440, 203, 16, 37);
    Area obstacle3Area = new Area(
            new Tile(412, 242),
            new Tile(423, 257)
    );
    Tile obstacle3Start = new Tile(420, 244);
    Rectangle obstacle3MogRect = new Rectangle(404, 249, 14, 15);
    Tile obstacle3End = new Tile(420, 233);
    Rectangle obstacle4InstantPressArea = new Rectangle(405, 230, 14, 16);
    Rectangle obstacle4PressArea = new Rectangle(439, 240, 12, 17);
    Area obstacle4Area = new Area(
            new Tile(415, 230),
            new Tile(423, 236)
    );
    Tile obstacle4Start = new Tile(417, 232);
    Tile obstacle4End = new Tile(417, 228);
    Rectangle obstacle5InstantPressArea = new Rectangle(370, 248, 12, 28);
    Rectangle obstacle5PressArea = new Rectangle(420, 251, 15, 25);
    Area obstacle5Area = new Area(
            new Tile(413, 219),
            new Tile(419, 229)
    );
    Tile obstacle5Start = new Tile(413, 228);
    Tile obstacle5End = new Tile(408, 228);
    Tile[] obstacle6FailPath = new Tile[] {
            new Tile(418, 126),
            new Tile(419, 141)
    };
    Area obstacle6FailArea = new Area(
            new Tile(407, 110),
            new Tile(421, 123)
    );
    Rectangle obstacle6InstantPressArea = new Rectangle(321, 253, 11, 13);
    Rectangle obstacle6PressArea = new Rectangle(422, 255, 12, 12);
    Area obstacle6Area = new Area(
            new Tile(397, 222),
            new Tile(410, 230)
    );
    Tile obstacle6Start = new Tile(400, 228);
    Tile obstacle6End = new Tile(391, 237);
    Rectangle obstacle7InstantPressArea = new Rectangle(405, 283, 13, 12);
    Rectangle obstacle7PressArea = new Rectangle(423, 270, 11, 10);
    Area obstacle7Area = new Area(
            new Tile(387, 235),
            new Tile(393, 241)
    );
    Tile obstacle7Start = new Tile(389, 239);
    Tile obstacle7End = new Tile(380, 239);
    Rectangle obstacle8InstantPressArea = new Rectangle(385, 275, 31, 6);
    Rectangle obstacle8PressArea = new Rectangle(431, 275, 25, 6);
    Area obstacle8Area = new Area(
            new Tile(364, 230),
            new Tile(383, 240)
    );
    Tile obstacle8Start = new Tile(377, 239);
    Tile obstacle8End = new Tile(377, 244);
    Rectangle obstacle9MogRect = new Rectangle(419, 279, 12, 14);
    Color obstacle9MogColor = new Color(201, 135, 24);
    Rectangle obstacle9InstantPressArea = new Rectangle(378, 331, 30, 8);
    Rectangle obstacle9PressArea = new Rectangle(415, 271, 21, 6);
    Area obstacle9Area = new Area(
            new Tile(373, 242),
            new Tile(384, 253)
    );
    Tile obstacle9Start = new Tile(375, 249);
    Tile obstacle9End = new Tile(372, 248);
    Rectangle obstacle10InstantPressArea = new Rectangle(414, 310, 19, 12);
    Rectangle obstacle10PressArea = new Rectangle(427, 278, 27, 14);
    Area obstacle10Area = new Area(
            new Tile(366, 246),
            new Tile(374, 251)
    );
    Tile obstacle10Start = new Tile(372, 248);
    Tile obstacle10End = new Tile(371, 253);
    Rectangle obstacle11InstantPressArea = new Rectangle(438, 414, 16, 21);
    Rectangle obstacle11PressArea = new Rectangle(436, 281, 15, 20);
    Area obstacle11Area = new Area(
            new Tile(363, 252),
            new Tile(370, 263)
    );
    Tile obstacle11Start = new Tile(371, 263);
    Tile obstacle11End = new Tile(371, 265);
    Rectangle obstacle11MogRect = new Rectangle(468, 268, 14, 15);
    Rectangle obstacle12InstantPressArea = new Rectangle(502, 262, 27, 10);
    Rectangle obstacle12PressArea = new Rectangle(437, 261, 22, 12);
    Area obstacle12Area = new Area(
            new Tile(368, 264),
            new Tile(377, 270)
    );
    Tile obstacle12Start = new Tile(376, 265);
    Tile obstacle12End = new Tile(379, 265);
    Rectangle obstacle13InstantPressArea = new Rectangle(513, 257, 18, 16);
    Rectangle obstacle13PressArea = new Rectangle(446, 258, 17, 17);
    Area obstacle13Area = new Area(
            new Tile(378, 260),
            new Tile(390, 270)
    );
    Tile obstacle13Start = new Tile(385, 265);
    Tile obstacle13End = new Tile(411, 149);


    public Falador(){
        super();
        super.name = "Falador";
    }
    @Override
    public boolean activate() {
        // Criteria that needs to be met for this class to run
        return (dAgility.courseChosen.equals("Falador"));
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        // If player within obstacle area
        Paint.setStatus("Fetch player position");
        currentLocation = Walker.getPlayerPosition();
        Logger.debugLog("Player pos (x:" + currentLocation.x() + "|y:" + currentLocation.y() + ")");
        if(Player.isTileWithinArea(currentLocation, obstacle1Area)) {
            if(tileEquals(obstacle13End, currentLocation)){
                Paint.setStatus("Traverse obstacle 1");
                Logger.log("Traversing obstacle 1.");
                dAgility.lapCount = dAgility.lapCount + 1;
                Client.tap(obstacle1InstantPressArea);
                Condition.wait(() -> atTile(obstacle1End), 100, 70);
            }
            else {
                carryOutStartObstacle_tap();
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle2Area)) {
            if(tileEquals(obstacle1End, currentLocation)){
                Paint.setStatus("Traverse obstacle 2");
                Logger.log("Traversing obstacle 2.");
                Client.tap(obstacle2InstantPressArea);
                Condition.wait(() -> atTile(obstacle2End), 100, 94);
                Condition.sleep(generateRandomDelay(400, 600));
            }
            else {
                carryOutObstacle_tap(obstacle2Start, obstacle2End, obstacle2PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle3Area)) {
            if(tileEquals(obstacle2End, currentLocation)) {
                Condition.sleep(generateRandomDelay(450, 900));
                if(mogPresent(obstacle3MogRect, baseMogColor)) {
                    currentLocation = Walker.getPlayerPosition();
                } else {
                    Paint.setStatus("Traverse obstacle 3");
                    Logger.log("Traversing obstacle 3.");
                    Client.tap(obstacle3InstantPressArea);
                    Condition.wait(() -> checkTileandArea(obstacle3End, obstacle3FailArea), 100, 105);
                }
            }
            else {
                carryOutObstacle_tap(obstacle3Start, obstacle3End, obstacle3PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle3FailArea)){
            Paint.setStatus("Walk back from fail area 3");
            Walker.walkPath(obstacle3FailPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 100, 50);
            Condition.sleep(2500);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle4Area)) {
            if(tileEquals(obstacle3End, currentLocation)) {
                Condition.sleep(generateRandomDelay(450, 900));
                if(mogPresent(obstacle4MogRect, baseMogColor)) {
                    currentLocation = Walker.getPlayerPosition();
                } else {
                    Paint.setStatus("Traverse obstacle 4");
                    Logger.log("Traversing obstacle 4.");
                    Client.tap(obstacle4InstantPressArea);
                    Condition.wait(() -> atTile(obstacle4End), 100, 35);
                }
            }
            else {
                carryOutObstacle_tap(obstacle4Start, obstacle4End, obstacle4PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle5Area)) {
            if(tileEquals(obstacle4End, currentLocation)){
                Paint.setStatus("Traverse obstacle 5");
                Logger.log("Traversing obstacle 5.");
                Client.tap(obstacle5InstantPressArea);
                Condition.wait(() -> atTile(obstacle5End), 100, 40);
            }
            else {
                carryOutObstacle_tap(obstacle5Start, obstacle5End, obstacle5PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle6Area)) {
            if(tileEquals(obstacle5End, currentLocation)){
                Paint.setStatus("Traverse obstacle 6");
                Logger.log("Traversing obstacle 6.");
                Client.tap(obstacle6InstantPressArea);
                Condition.wait(() -> checkTileandArea(obstacle6End, obstacle6FailArea), 100, 90);
            }
            else {
                carryOutObstacle_tap(obstacle6Start, obstacle6End, obstacle6PressArea);
            }
            return true;
        }
        else if (Player.isTileWithinArea(currentLocation, obstacle6FailArea)){
            Paint.setStatus("Walk back from fail area 6");
            Walker.walkPath(obstacle6FailPath);
            Condition.wait(() -> Player.isTileWithinArea(Walker.getPlayerPosition(), obstacle1Area), 100, 50);
            Condition.sleep(2500);
            dAgility.currentHP = Player.getHP();
            Logger.debugLog("Current HP: " + dAgility.currentHP);
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle7Area)) {
            if(tileEquals(currentLocation, obstacle6End)){
                Paint.setStatus("Traverse obstacle 7");
                Logger.log("Traversing obstacle 7.");
                Client.tap(obstacle7InstantPressArea);
                Condition.wait(() -> atTile(obstacle7End), 100, 55);
            }
            else {
                carryOutObstacle_tap(obstacle7Start, obstacle7End, obstacle7PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle8Area)) {
            if(tileEquals(currentLocation, obstacle7End)){
                Paint.setStatus("Traverse obstacle 8");
                Logger.log("Traversing obstacle 8.");
                Client.tap(obstacle8InstantPressArea);
                Condition.wait(() -> atTile(obstacle8End), 100, 35);
            }
            else {
                carryOutObstacle_tap(obstacle8Start, obstacle8End, obstacle8PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle9Area)) {
            if(tileEquals(obstacle8End, currentLocation)) {
                Condition.sleep(generateRandomDelay(450, 900));
                if(mogPresent(obstacle9MogRect, obstacle9MogColor)) {
                    currentLocation = Walker.getPlayerPosition();
                } else {
                    Paint.setStatus("Traverse obstacle 9");
                    Logger.log("Traversing obstacle 9.");
                    Client.tap(obstacle9InstantPressArea);
                    Condition.wait(() -> atTile(obstacle9End), 100, 42);
                }
            }
            else {
                carryOutObstacle_tap(obstacle9Start, obstacle9End, obstacle9PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle10Area)) {
            if(tileEquals(obstacle9End, currentLocation)) {
                Condition.sleep(generateRandomDelay(450, 900));
                Paint.setStatus("Traverse obstacle 10");
                Logger.log("Traversing obstacle 10.");
                Client.tap(obstacle10InstantPressArea);
                Condition.wait(() -> atTile(obstacle10End), 100, 35);
                Condition.sleep(generateRandomDelay(1000, 1300));
            }
            else {
                carryOutObstacle_tap(obstacle10Start, obstacle10End, obstacle10PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle11Area)) {
            if(tileEquals(currentLocation, obstacle10End)){
                Paint.setStatus("Traverse obstacle 11");
                Logger.log("Traversing obstacle 11.");
                if(mogPresent(obstacle11MogRect, baseMogColor)) {
                    currentLocation = Walker.getPlayerPosition();
                } else {
                    Client.tap(obstacle11InstantPressArea);
                    Condition.wait(() -> atTile(obstacle11End), 100, 55);
                    Condition.sleep(generateRandomDelay(750, 1250));
                }
            }
            else {
                carryOutObstacle_tap(obstacle11Start, obstacle11End, obstacle11PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle12Area)) {
            if(tileEquals(obstacle11End, currentLocation)) {
                Condition.sleep(generateRandomDelay(450, 900));
                Paint.setStatus("Traverse obstacle 12");
                    Logger.log("Traversing obstacle 12.");
                    Client.tap(obstacle12InstantPressArea);
                    Condition.wait(() -> atTile(obstacle12End), 100, 50);
                    Condition.sleep(generateRandomDelay(750, 1250));

            }
            else {
                carryOutObstacle_tap(obstacle12Start, obstacle12End, obstacle12PressArea);
            }
            return true;
        }
        else if(Player.isTileWithinArea(currentLocation, obstacle13Area)) {
            if(tileEquals(currentLocation, obstacle12End)){
                Paint.setStatus("Traverse obstacle 13");
                Logger.log("Traversing obstacle 13.");
                Client.tap(obstacle13InstantPressArea);
                Condition.wait(() -> atTile(obstacle13End), 100, 58);
            }
            else {
                carryOutObstacle_tap(obstacle13Start, obstacle13End, obstacle13PressArea);
            }
            return true;
        }
        return false;
    }

    public void carryOutObstacle_tap(Tile obstacleStart, Tile obstacleEnd, Rectangle pressArea){
        Paint.setStatus("Traverse obstacle X");
        if(!atTile(obstacleStart)) {
            Logger.debugLog("Carrying out obstacle...");
            Walker.step(obstacleStart);
            Condition.wait(() -> atTile(obstacleStart), 100, 100);
        }
        if (atTile(obstacleStart)) {
            Logger.debugLog("Player is at start of obstacle.");
            Client.tap(pressArea);
            Condition.wait(() -> atTile(obstacleEnd), 100, 100);
        }
    }

    public void carryOutStartObstacle_tap() {
        Paint.setStatus("Traverse start obstacle");
        List<Point> foundPoints = Client.getPointsFromColorsInRect(obstacleColors, screenROI, 1);

        if (!foundPoints.isEmpty()) {
            int randomIndex = random.nextInt(foundPoints.size());
            Point tapPoint = foundPoints.get(randomIndex);

            Logger.log("Located the first obstacle using the color finder, tapping.");
            Client.tap(tapPoint);
            Condition.wait(() -> atTile(obstacle1End), 100, 100);
        } else {
            Logger.debugLog("Couldn't locate the first obstacle with the color finder, using fallback method.");
            currentLocation = Walker.getPlayerPosition();

            Logger.log("Moving to the start of the course.");
            Walker.step(new Tile(421, 140));
            Condition.wait(() -> atTile(new Tile(421, 140)), 100, 100);
            Client.tap(new Rectangle(444, 237, 8, 9));
            Condition.wait(() -> atTile(obstacle1End), 100, 100);
        }
    }
}
