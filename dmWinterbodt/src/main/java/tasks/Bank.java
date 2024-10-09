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

    @Override
    public boolean activate() {
        StateUpdater.updateIsGameGoing();
        return !isGameGoing && !Player.leveledUp() || !isGameGoing && (Inventory.count(ItemList.SUPPLY_CRATE_20703, 0.8) >= 8) || Player.isTileWithinArea(currentLocation, outsideArea) || Player.isTileWithinArea(currentLocation, lobby) && (!isGameGoing || totalGameCount == 0);
    }

    @Override
    public boolean execute() {
        Logger.log("Banking!");
        currentLocation = Walker.getPlayerPosition();

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

        if (walkToBankFromDoorInside()) {
            Logger.log("Walk to bank from door inside");
            currentLocation = Walker.getPlayerPosition();
        } else if (walkToBankFromGame()) {
            Logger.log("Walk to bank from game");
            currentLocation = Walker.getPlayerPosition();
        } else if (walkToBankFromOutsideArea()) {
            Logger.log("walk to bank from outside");
            currentLocation = Walker.getPlayerPosition();
        }

        StateUpdater.resetAllStates();
        if (!Player.isTileWithinArea(currentLocation, bankTentArea) && Player.isTileWithinArea(currentLocation, outsideArea)) {
            if (Walker.isReachable(bankTile)) {
                Walker.step(bankTile);
            } else {
                Walker.walkTo(new Tile(6527, 15541, 0));
                Condition.sleep(generateRandomDelay(900, 1350));
                Walker.step(bankTile);
            }
            Condition.wait(() -> Player.within(bankTentArea), 250, 15);
            Condition.sleep(generateRandomDelay(500, 1000));
            currentLocation = Walker.getPlayerPosition();
        }
        if (Player.isTileWithinArea(currentLocation, bankTentArea)) {
            handleBanking();
            isBurning = false;
            FletchBranches.isFletching = false;
            GetBranches.gettingBranches = false;
            StateUpdater.mageDeadTimestamps.put("Left", -1L);
            StateUpdater.mageDeadTimestamps.put("Right", -1L);
            return true;
        }
        return false;
    }

    private void handleBanking() {
        Paint.setStatus("Banking");
        setupOrStepToBank();
        if (ensureBankIsOpen()) {
            ensureCorrectBankTab();

            Bank.close();
            Condition.sleep(generateRandomDelay(400, 700));
            if (Bank.isOpen()) {
                Bank.close();
            }

            GameTabs.openInventoryTab();

            Condition.sleep(generateRandomDelay(1250, 2000));

            isBurning = false;
            FletchBranches.isFletching = false;
            GetBranches.gettingBranches = false;
            isGameGoing = false;
            lastActivity = System.currentTimeMillis();

            // Final check to see if we have enough food, otherwise terminate script.
            if (foodAmountInInventory < foodAmountLeftToBank) { // We at least need to have the amount needed before banking, if not we might have run out of food
                if (!alreadyBanked) {
                    Paint.setStatus("Reattempting bank sequence");
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
        if (!Player.within(bankTentArea)) {
            Walker.step(bankTile);
            Condition.wait(() -> Player.within(bankTentArea), 250, 15);
            Condition.sleep(generateRandomDelay(500, 1000));
            currentLocation = Walker.getPlayerPosition();
        }
    }

    private boolean ensureBankIsOpen() {
        Paint.setStatus("Checking if bank is open");
        if (!Bank.isOpen()) {
            Logger.debugLog("Bank is not open yet, opening!");

            // use color finder here
            List<Rectangle> foundRectangles = Client.getObjectsFromColorsInRect(bankChest, bankSearchArea, 1);

            if (!foundRectangles.isEmpty()) {
                Rectangle randomRect = foundRectangles.get(random.nextInt(foundRectangles.size()));
                Rectangle centerRect = new Rectangle(
                        randomRect.x + randomRect.width / 2 - 2,
                        randomRect.y + randomRect.height / 2 - 2,
                        5,
                        5
                );
                Logger.debugLog("Located the wintertodt bank chest using the color finder, tapping.");
                Client.tap(centerRect);
                Condition.wait(() -> Bank.isOpen(), 250, 15);

                // re-try if bank is not open.
                if (!Bank.isOpen()) {
                    Logger.debugLog("Bank is not open yet, opening!");

                    // use color finder here
                    List<Rectangle> foundRectangles2 = Client.getObjectsFromColorsInRect(bankChest, bankSearchArea, 1);

                    if (!foundRectangles2.isEmpty()) {
                        Rectangle randomRect2 = foundRectangles2.get(random.nextInt(foundRectangles2.size()));
                        Rectangle centerRect2 = new Rectangle(
                                randomRect2.x + randomRect2.width / 2 - 2,
                                randomRect2.y + randomRect2.height / 2 - 2,
                                5,
                                5
                        );
                        Logger.debugLog("Located the wintertodt bank chest using the color finder, tapping.");
                        Client.tap(centerRect2);
                        Condition.wait(() -> Bank.isOpen(), 250, 15);

                        // re-try if bank is not open.
                        if (!Bank.isOpen()) {
                            Logger.debugLog("Bank is not open yet, opening!");

                            // use color finder here
                            List<Rectangle> foundRectangles3 = Client.getObjectsFromColorsInRect(bankChest, bankSearchArea, 1);

                            if (!foundRectangles3.isEmpty()) {
                                Rectangle randomRect3 = foundRectangles3.get(random.nextInt(foundRectangles3.size()));
                                Rectangle centerRect3 = new Rectangle(
                                        randomRect3.x + randomRect3.width / 2 - 2,
                                        randomRect3.y + randomRect3.height / 2 - 2,
                                        5,
                                        5
                                );
                                Logger.debugLog("Located the wintertodt bank chest using the color finder, tapping.");
                                Client.tap(centerRect3);
                                Condition.wait(() -> Bank.isOpen(), 250, 15);

                                if (!Bank.isOpen()) {
                                    Logger.debugLog("Failed to bank three times, resetting position!");
                                    Walker.step(bankTile);
                                    return false;
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

    private void ensureCorrectBankTab() {
        if (Bank.getCurrentTab(true) != bankTab) {
            Bank.openTab(bankTab);
            Condition.wait(() -> Bank.getCurrentTab(true) == bankTab, 100, 20);
        }
    }

    private void depositExcessSupplyCrates() {
        if (Inventory.contains(ItemList.SUPPLY_CRATE_20703, 0.80)) {
            int createAmount = Inventory.count(ItemList.SUPPLY_CRATE_20703, 0.80);
            totalCrateCount += createAmount;
            Paint.updateBox(crateIndex, totalCrateCount);
            Logger.log("Depositing supply crates.");
            Paint.setStatus("Depositing supply crates");
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
            }
            Inventory.tapItem(ItemList.SUPPLY_CRATE_20703, 0.80);
            Condition.wait(() -> !Inventory.contains(ItemList.SUPPLY_CRATE_20703, 0.80), 100, 20);
        }
    }

    private boolean walkToBankFromGame() {
        if (Player.isTileWithinArea(currentLocation, insideArea)) {
            Paint.setStatus("Walking to the bank from game area");
            Walker.walkPath(gameToWTDoor);
            currentLocation = Walker.getPlayerPosition();
            Condition.wait(() -> Player.within(atDoorInside), 100, 20);

            if (Player.isTileWithinArea(currentLocation, atDoorInside)) {
                Logger.debugLog("We are at the door, exiting!");
                Client.tap(exitDoorRect);
                Condition.sleep(generateRandomDelay(500, 1000));

                if (Chatbox.findChatboxMenu() != null) {
                    Client.sendKeystroke("KEYCODE_SPACE");
                    Condition.wait(() -> Chatbox.findChatboxMenu() != null, 100, 10);
                }
            }

            if (Player.within(outsideArea)) {
                Logger.debugLog("We are outside, moving to bank");
                if (Walker.isReachable(bankTile)) {
                    Walker.step(bankTile);
                } else {
                    Walker.walkTo(new Tile(6527, 15541, 0));
                    Condition.sleep(generateRandomDelay(900, 1350));
                    Walker.step(bankTile);
                }
                Condition.wait(() -> Player.within(bankTentArea), 250, 15);
                Condition.sleep(generateRandomDelay(500, 1000));
                currentLocation = Walker.getPlayerPosition();
                return true;
            }
        }
        return false;
    }

    private boolean walkToBankFromDoorInside() {
        if (Player.isTileWithinArea(currentLocation, atDoorInside)) {
            Paint.setStatus("Walking to the bank from inside");
            Client.tap(exitDoorRect);
            Condition.sleep(generateRandomDelay(4250, 5300));
            Walker.walkTo(new Tile(6527, 15541, 0));
            Condition.sleep(generateRandomDelay(300, 450));
            Walker.step(bankTile);
            Condition.wait(() -> Player.within(bankTentArea), 250, 15);
            Condition.sleep(generateRandomDelay(500, 1000));
            currentLocation = Walker.getPlayerPosition();
            return true;
        }
        return false;
    }

    private boolean walkToBankFromOutsideArea() {
        if (Player.isTileWithinArea(currentLocation, outsideArea)) {
            Paint.setStatus("Walking to the bank from outside");
            if (Walker.isReachable(bankTile)) {
                Walker.step(bankTile);
            } else {
                Walker.walkPath(outsideToBankPath);
                Condition.sleep(generateRandomDelay(1000, 1500));
                Walker.step(bankTile);
            }
            Condition.wait(() -> Player.within(bankTentArea), 250, 15);
            Condition.sleep(generateRandomDelay(500, 1000));
            currentLocation = Walker.getPlayerPosition();
            return true;
        }
        return false;
    }
}
