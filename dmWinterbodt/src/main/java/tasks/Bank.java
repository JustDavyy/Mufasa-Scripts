package tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.StateUpdater;
import utils.Task;

import java.awt.*;
import java.util.List;
import java.util.Random;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class Bank extends Task {
    Random random = new Random();
    private boolean checkFood = true;

    @Override
    public boolean activate() {
        //Logger.debugLog("Inside Bank activate()");
        StateUpdater.updateIsGameGoing();
        return foodAmountInInventory < foodAmountLeftToBank && !isGameGoing && !Player.leveledUp() || !isGameGoing && (Inventory.count(ItemList.SUPPLY_CRATE_20703, 0.8) >= 8) || foodAmountInInventory < foodAmountLeftToBank && Player.isTileWithinArea(currentLocation, outsideArea) || foodAmountInInventory < foodAmountLeftToBank && Player.isTileWithinArea(currentLocation, lobby) && !isGameGoing;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside Bank execute()");
        Logger.log("Banking!");
        checkFood = true;

        GameTabs.openInventoryTab();

        if (walkToBankFromDoorInside()) {
            currentLocation = Walker.getPlayerPosition(WTRegion);
        } else if (walkToBankFromGame()) {
            currentLocation = Walker.getPlayerPosition(WTRegion);
        } else if (walkToBankFromOutsideArea()) {
            currentLocation = Walker.getPlayerPosition(WTRegion);
        }

        if (!Player.isTileWithinArea(currentLocation, bankTentArea)) {
            Walker.step(bankTile, WTRegion);
            Condition.wait(() -> Player.within(bankTentArea, WTRegion), 250, 15);
            Condition.sleep(generateRandomDelay(500, 1000));
            currentLocation = Walker.getPlayerPosition(WTRegion);
        }
        if (Player.isTileWithinArea(currentLocation, bankTentArea)) {
            handleBanking();
            BurnBranches.isBurning = false;
            FletchBranches.isFletching = false;
            GetBranches.gettingBranches = false;
            StateUpdater.mageDeadTimestamps.put("Left", -1L);
            StateUpdater.mageDeadTimestamps.put("Right", -1L);
            return true;
        }
        return false;
    }

    private void handleBanking() {
        setupOrStepToBank();
        if (ensureBankIsOpen()) {
            ensureCorrectBankTab();

            depositExcessSupplyCrates();

            int foodNeeded = CalculateAmountOfFoodNeeded();
            withdrawFoodIfNeeded(foodNeeded);

            Bank.close();
            Condition.sleep(generateRandomDelay(400, 700));
            if (Bank.isOpen()) {
                Bank.close();
            }

            GameTabs.openInventoryTab();

            checkFood = true;
            Condition.sleep(generateRandomDelay(1250, 2000));
            countFoodInInventory();

            BurnBranches.isBurning = false;
            FletchBranches.isFletching = false;
            GetBranches.gettingBranches = false;
            isGameGoing = false;

            // Final check to see if we have enough food, otherwise terminate script.
            if (foodAmountInInventory < foodAmountLeftToBank) { // We at least need to have the amount needed before banking, if not we might have ran out of food
                if (!alreadyBanked) {
                    Logger.log("We only have " + foodAmountInInventory + " food in our inventory, reattempting a banking sequence.");
                    alreadyBanked = true;
                    handleBanking();
                } else {
                    Logger.log("We only have " + foodAmountInInventory + " food in our inventory, and already reattempted banking. Assuming we have no more food in our bank.");
                    Logger.log("Terminating script.");

                    if (Bank.isOpen()) {
                        Bank.close();
                    }

                    Logout.logout();
                    Script.stop();
                }
            } else {
                alreadyBanked = false;
            }
        }
    }

    private void setupOrStepToBank() {
        if (!Player.within(bankTentArea, WTRegion)) {
            Walker.step(bankTile, WTRegion);
            Condition.wait(() -> Player.within(bankTentArea, WTRegion), 250, 15);
            Condition.sleep(generateRandomDelay(500, 1000));
            currentLocation = Walker.getPlayerPosition(WTRegion);
        }
    }

    private boolean ensureBankIsOpen() {
        if (!Bank.isOpen()) {
            Logger.debugLog("Bank is not open yet, opening!");

            // use color finder here
            List<Rectangle> foundRectangles = Client.getObjectsFromColorsInRect(bankChest, bankSearchArea, 1);

            if (!foundRectangles.isEmpty()) {
                Rectangle randomRect = foundRectangles.get(random.nextInt(foundRectangles.size()));
                Logger.debugLog("Located the wintertodt bank chest using the color finder, tapping.");
                Client.tap(randomRect);
                Condition.wait(() -> Bank.isOpen(), 250, 15);

                // re-try if bank is not open.
                if (!Bank.isOpen()) {
                    Logger.debugLog("Bank is not open yet, opening!");

                    // use color finder here
                    List<Rectangle> foundRectangles2 = Client.getObjectsFromColorsInRect(bankChest, bankSearchArea, 1);

                    if (!foundRectangles2.isEmpty()) {
                        Rectangle randomRect2 = foundRectangles2.get(random.nextInt(foundRectangles2.size()));
                        Logger.debugLog("Located the wintertodt bank chest using the color finder, tapping.");
                        Client.tap(randomRect2);
                        Condition.wait(() -> Bank.isOpen(), 250, 15);

                        // re-try if bank is not open.
                        if (!Bank.isOpen()) {
                            Logger.debugLog("Bank is not open yet, opening!");

                            // use color finder here
                            List<Rectangle> foundRectangles3 = Client.getObjectsFromColorsInRect(bankChest, bankSearchArea, 1);

                            if (!foundRectangles3.isEmpty()) {
                                Rectangle randomRect3 = foundRectangles3.get(random.nextInt(foundRectangles3.size()));
                                Logger.debugLog("Located the wintertodt bank chest using the color finder, tapping.");
                                Client.tap(randomRect3);
                                Condition.wait(() -> Bank.isOpen(), 250, 15);

                                if (!Bank.isOpen()) {
                                    Logger.debugLog("Failed to bank three times, resetting position!");
                                    Walker.step(bankTile, WTRegion);
                                }
                                return true;
                            } else {
                                Logger.debugLog("Couldn't locate the wintertodt bank chest using the color finder.");
                                return false;
                            }
                        } else {
                            Logger.debugLog("Bank is open!");
                            return true;
                        }
                    } else {
                        Logger.debugLog("Couldn't locate the wintertodt bank chest using the color finder.");
                        return false;
                    }
                } else {
                    Logger.debugLog("Bank is open!");
                    return true;
                }
            } else {
                Logger.debugLog("Couldn't locate the wintertodt bank chest using the color finder.");
                return false;
            }
        } else {
            Logger.debugLog("Bank is open!");
            return true;
        }
    }

    private void withdrawFoodIfNeeded(int foodNeeded) {
        if (foodNeeded > 0) {
            Logger.log("Withdrawing " + foodNeeded + " " + selectedFood + " from the bank.");
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
            }
            for (int i = 0; i < foodNeeded; i++) {
                Bank.withdrawItem(foodID, 0.7);
            }
        }
    }

    private void ensureCorrectBankTab() {
        if (Bank.getCurrentTab(true) != bankTab) {
            Bank.openTab(bankTab);
            Condition.wait(() -> Bank.getCurrentTab(true) == bankTab, 100, 20);
        }
    }

    private void depositExcessSupplyCrates() {
        if (Inventory.contains(ItemList.SUPPLY_CRATE_20703, 0.80)) {
            Logger.log("Depositing supply crates.");
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
            }
            Inventory.tapItem(ItemList.SUPPLY_CRATE_20703, 0.80);
            Condition.wait(() -> !Inventory.contains(ItemList.SUPPLY_CRATE_20703, 0.80), 100, 20);
        }
    }

    private int CalculateAmountOfFoodNeeded() {
        // Calculate the amount of food needed to withdraw from the bank
        int foodNeeded = foodAmount - foodAmountInInventory;

        // If more food is needed, return that amount; otherwise, return 0
        return Math.max(foodNeeded, 0);
    }

    // Method to count total food items in the inventory
    private void countFoodInInventory() {
        if (checkFood) {
            Logger.debugLog("Running food count.");
            foodAmountInInventory = 0; // Reset before counting

            if (selectedFood.equals("Cakes")) {
                int[] foodIds = {1891, 1893, 1895};
                for (int id : foodIds) {
                    int countMultiplier = 1; // Default count multiplier
                    if (id == 1891) {
                        countMultiplier = 3; // A full cake counts as 3
                    } else if (id == 1893) {
                        countMultiplier = 2; // half cake counts as 2
                    }

                    // Assume Inventory.count(id, 0.60) returns the number of items that are at least 60% intact
                    foodAmountInInventory += Inventory.count(id, 0.75) * countMultiplier;
                    Logger.debugLog("Food in inventory: " + foodAmountInInventory);
                    checkFood = false;
                }
            } else {
                foodAmountInInventory = Inventory.count(foodID, 0.75);
                Logger.debugLog("Food in inventory: " + foodAmountInInventory);
                checkFood = false;
            }
        }
    }

    private boolean walkToBankFromGame() {
        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Walker.walkPath(WTRegion, gameToWTDoor);
            Condition.wait(() -> Player.within(atDoor, WTRegion), 100, 20);
            Client.tap(exitDoorRect);
            Condition.sleep(generateRandomDelay(4250, 5300));
            Walker.walkTo(new Tile(640, 221), WTRegion);
            Condition.sleep(generateRandomDelay(300, 450));
            Walker.step(bankTile, WTRegion);
            Condition.wait(() -> Player.within(bankTentArea, WTRegion), 250, 15);
            Condition.sleep(generateRandomDelay(500, 1000));
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToBankFromDoorInside() {
        if (Player.isTileWithinArea(currentLocation, atDoor)) {
            Client.tap(exitDoorRect);
            Condition.sleep(generateRandomDelay(4250, 5300));
            Walker.walkTo(new Tile(640, 221), WTRegion);
            Condition.sleep(generateRandomDelay(300, 450));
            Walker.step(bankTile, WTRegion);
            Condition.wait(() -> Player.within(bankTentArea, WTRegion), 250, 15);
            Condition.sleep(generateRandomDelay(500, 1000));
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }

    private boolean walkToBankFromOutsideArea() {
        if (Player.isTileWithinArea(currentLocation, outsideArea)) {
            Walker.walkPath(WTRegion, outsideToBankPath);
            Condition.sleep(generateRandomDelay(1000, 1500));
            Walker.step(bankTile, WTRegion); //Step to bank tile.
            Condition.wait(() -> Player.within(bankTentArea, WTRegion), 250, 15);
            Condition.sleep(generateRandomDelay(500, 1000));
            currentLocation = Walker.getPlayerPosition(WTRegion);
            return true;
        }
        return false;
    }
}
