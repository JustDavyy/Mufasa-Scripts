package agi_sdk.helpers;

import agi_sdk.agi_sdk;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import helpers.utils.UITabs;

import static helpers.Interfaces.*;

public class TraverseHelpers {

    public static void traverseWithInstantTap(Obstacle obstacle) {
        Logger.log("Traversing obstacle " + obstacle.name);
        Logger.debugLog("Traversing " + obstacle.name + " with instant tap.");
        Paint.setStatus("Traverse " + obstacle.name);
        Client.tap(obstacle.instantPressArea);

        // Update our counters here before we wait for the end tile
        if (agi_sdk.courseChosen.equals("Basic Colossal Wyrm") || agi_sdk.courseChosen.equals("Advanced Colossal Wyrm")) {
            agi_sdk.generateRandomDelay(150, 250);
            // Generate a random number between 0 and 100 to check for 15% activation chance
            int randomChance = agi_sdk.random.nextInt(100);

            if (randomChance < 15 && (obstacle.name.equals("Obstacle 2") ||
                    obstacle.name.equals("Obstacle 4") ||
                    obstacle.name.equals("Obstacle 5") ||
                    obstacle.name.equals("Obstacle 6"))) {

                Logger.debugLog("Randomized activation: Open inventory to read new stack counts");
                GameTabs.openTab(UITabs.INVENTORY);

                agi_sdk.termiteCount = Inventory.stackSize(30038) - agi_sdk.initialTermiteCount;
                Paint.updateBox(agi_sdk.termiteIndex, agi_sdk.termiteCount);

                agi_sdk.boneShardCount = Inventory.stackSize(ItemList.BLESSED_BONE_SHARDS_29381) - agi_sdk.initialBoneShardCount;
                Paint.updateBox(agi_sdk.shardIndex, agi_sdk.boneShardCount);

                Logger.debugLog("Close inventory for instant obstacle tap");
                GameTabs.closeTab(UITabs.INVENTORY);
            }
        }

        if (obstacle.failArea != null && obstacle.checkForFail) {
            Condition.wait(() -> Player.atTile(obstacle.endTile) || Player.within(obstacle.failArea), 100, 250);
        } else {
            Condition.wait(() -> Player.atTile(obstacle.endTile), 100, 250);
        }

        Condition.sleep(agi_sdk.generateRandomDelay(400, 600));
    }

    public static void traverseObstacle(Obstacle obstacle) {
        Paint.setStatus("Traverse " + obstacle.name);
        Logger.log("Traversing obstacle " + obstacle.name);
        if (!Player.atTile(obstacle.startTile)) {
            Logger.debugLog("Moving to start of " + obstacle.name);
            Walker.step(obstacle.startTile);
            Condition.wait(() -> Player.atTile(obstacle.startTile), 100, 250);
            Condition.sleep(agi_sdk.generateRandomDelay(550, 700));
        }
        if (Player.atTile(obstacle.startTile)) {
            Logger.debugLog("At start of " + obstacle.name);
            Client.tap(obstacle.pressArea);

            if (obstacle.failArea != null && obstacle.checkForFail) {
                Condition.wait(() -> Player.atTile(obstacle.endTile) || Player.within(obstacle.failArea), 100, 250);
            } else {
                Condition.wait(() -> Player.atTile(obstacle.endTile), 100, 250);
            }

            Condition.sleep(agi_sdk.generateRandomDelay(550, 700));
        }
    }

    public static void proceedWithTraversal(Obstacle obstacle, Tile currentLocation) {
        if (Player.tileEquals(obstacle.prevEndTile, currentLocation)) {
            traverseWithInstantTap(obstacle);
        } else {
            traverseObstacle(obstacle);
        }
    }
}
