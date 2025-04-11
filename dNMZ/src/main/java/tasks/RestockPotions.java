package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static main.dNMZ.*;
import static helpers.Interfaces.*;

public class RestockPotions extends Task {

    // Integers
    private int absorbDosesNeeded = 0;
    private int overloadDosesNeeded = 0;
    private int absorbDosesTooMany = 0;
    private int overloadDosesTooMany = 0;

    // Rectangles
    private final Rectangle rewardInterfaceCheckRect1 = new Rectangle(87, 174, 16, 16);
    private final Rectangle rewardInterfaceCheckRect2 = new Rectangle(569, 466, 12, 14);
    private final Rectangle rewardInterfaceAbsorbRect = new Rectangle(321, 329, 17, 20);
    private final Rectangle rewardInterfaceOverloadRect = new Rectangle(254, 327, 20, 28);
    private final Rectangle closeRewardInterfaceRect = new Rectangle(555, 189, 14, 14);
    private final Rectangle tapAbsorptionBarrelRect = new Rectangle(383, 243, 19, 19);
    private final Rectangle tapOverloadBarrelRect = new Rectangle(383, 250, 18, 14);

    // Colors
    public static List<Color> lpMenuTextColors = Arrays.asList(
            Color.decode("#ffffff")
    );

    @Override
    public boolean activate() {
        // No need to run this task when we are inside, early return false
        if (insideNMZ) {
            return false;
        }
        // Never need this task if we don't use absorbs or overloads, early return false
        if (!"Absorption".equals(NMZMethod) && !"Overload".equals(potions)) {
            return false;
        }

        // Check if we need to restock.
        return needToRestock();
    }

    @Override
    public boolean execute() {

        // Check how many potions of each we still need
        Logger.debugLog("Check how many doses we still need of each potion.");
        if ("Absorption".equals(NMZMethod)) {
            absorbDosesNeeded = calculateDosesNeeded(ItemList.ABSORPTION_1_11737, ItemList.ABSORPTION_2_11736, ItemList.ABSORPTION_3_11735, ItemList.ABSORPTION_4_11734, absorbPotColor, 84);
            absorbDosesTooMany = calculateExcessDoses(ItemList.ABSORPTION_1_11737, ItemList.ABSORPTION_2_11736, ItemList.ABSORPTION_3_11735, ItemList.ABSORPTION_4_11734, absorbPotColor, 84);
        }
        if ("Overload".equals(potions)) {
            overloadDosesNeeded = calculateDosesNeeded(ItemList.OVERLOAD_1_11733, ItemList.OVERLOAD_2_11732, ItemList.OVERLOAD_3_11731, ItemList.OVERLOAD_4_11730, overloadPotColor, 24);
            overloadDosesTooMany = calculateExcessDoses(ItemList.OVERLOAD_1_11733, ItemList.OVERLOAD_2_11732, ItemList.OVERLOAD_3_11731, ItemList.OVERLOAD_4_11730, overloadPotColor, 24);
        }

        playerPosition = Walker.getPlayerPosition();
        Logger.log("Restocking NMZ potions");

        // We are within the NMZ area
        if (Player.isTileWithinArea(playerPosition, NMZAreaOutside)) {
            Logger.debugLog("We are located at the outside area of NMZ: " + playerPosition.toString());

            Logger.debugLog("Moving towards the reward chest");
            if (Walker.isReachable(rewardChestTile)) {
                Walker.step(rewardChestTile);
                Condition.sleep(1200, 1600);
            } else {
                Walker.webWalk(rewardChestTile);
                Player.waitTillNotMoving(20);
                Walker.step(rewardChestTile);
            }
        }

        // We are at the bank, move to NMZ area
        if (Player.isTileWithinArea(playerPosition, bankArea)) {
            Logger.debugLog("We are located at the Yanille bank: " + playerPosition.toString());
            Logger.debugLog("Walking towards the NMZ area");
            Walker.walkPath(pathToNMZ);
            Player.waitTillNotMoving(20);
            Walker.step(rewardChestTile);
        }

        // Handle any excess we might have
        if (absorbDosesTooMany > 0) {
            Logger.debugLog("We have an excess of Absorption potions, storing them!");
            ensurePlayerAtTile(absorptionBarrelTile);
            Condition.sleep(750, 1500);
            if (Player.atTile(absorptionBarrelTile)) {
                Client.longPress(tapAbsorptionBarrelRect);
                Condition.sleep(600, 1000);
                tapStoreOption();
                Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 50);

                if (Chatbox.isMakeMenuVisible()) {
                    Client.sendKeystroke("1");
                    Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 50);
                    Condition.sleep(500, 750);
                    Logger.debugLog("Recalculate needed absorb doses...");
                    absorbDosesNeeded = calculateDosesNeeded(ItemList.ABSORPTION_1_11737, ItemList.ABSORPTION_2_11736, ItemList.ABSORPTION_3_11735, ItemList.ABSORPTION_4_11734, absorbPotColor, 84);
                }
            }
        }

        if (overloadDosesTooMany > 0) {
            Logger.debugLog("We have an excess of Overload potions, storing them!");
            ensurePlayerAtTile(overloadBarrelTile);
            Condition.sleep(750, 1500);
            if (Player.atTile(overloadBarrelTile)) {
                Client.longPress(tapOverloadBarrelRect);
                Condition.sleep(600, 1000);
                tapStoreOption();
                Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 50);

                if (Chatbox.isMakeMenuVisible()) {
                    Client.sendKeystroke("1");
                    Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 50);
                    Condition.sleep(500, 750);
                    Logger.debugLog("Recalculate needed overload doses...");
                    overloadDosesNeeded = calculateDosesNeeded(ItemList.OVERLOAD_1_11733, ItemList.OVERLOAD_2_11732, ItemList.OVERLOAD_3_11731, ItemList.OVERLOAD_4_11730, overloadPotColor, 24);
                }
            }
        }

        if (absorbDosesTooMany > 0 || overloadDosesTooMany > 0) {
            Logger.debugLog("We had to store excess potions, moving back to reward chest.");
            ensurePlayerAtTile(rewardChestTile);
        }

        // We should be at the reward chest here
        if (Player.atTile(rewardChestTile)) {
            // Open the reward chest
            Client.tap(tapRewardChestRect);
            Condition.wait(this::rewardInterfaceOpen, 100, 50);

            // Check if a bank pin is needed (is needed for reward chest)
            if (Bank.isBankPinNeeded()) {
                Bank.enterBankPin();
                Condition.sleep(750);
            }

            // Open the benefits tab (only if interface is open)
            if (rewardInterfaceOpen()) {
                Client.tap(rewardChestBenefitsRect);
                Condition.wait(() -> Client.isColorInRect(Color.decode("#811f1d"), rewardChestBenefitsRect, 10), 100, 50);
                Condition.sleep(500, 750);
            }

            // Check current potion stock, buy more if needed
            if (rewardInterfaceOpen()) {
                // Read the current stock we have
                int absorbStockCount = interfaces.readCustomStackSize(absorbStockCountRect, potionstockTextColors, potionstockDigitPatterns);
                int overloadStockCount = interfaces.readCustomStackSize(ovlStockCountRect, potionstockTextColors, potionstockDigitPatterns);

                // Compare this to how many doses we need, buy more if needed (based on config)
                if ("Absorption".equals(NMZMethod)) {
                    if (absorbStockCount < absorbDosesNeeded) {
                        int additionalAbsorbDosesNeeded = absorbDosesNeeded - absorbStockCount; // Total required: 21 full potions (84 doses)
                        Logger.debugLog("We need to buy more absorption doses. Needed: " + absorbDosesNeeded + " We have: " + absorbStockCount);
                        Logger.log("Buying " + additionalAbsorbDosesNeeded + " more absorption doses.");

                        // Long press and tap Buy-X
                        Client.longPressWithMenuAction(rewardInterfaceAbsorbRect, 158, 224, lpMenuTextColors, "Buy-X");
                        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 50);
                        Condition.sleep(1000, 2000);

                        // Convert the integer quantity to a string for keystroke simulation
                        String quantityStr = Integer.toString(additionalAbsorbDosesNeeded);
                        // Type the quantity digit by digit
                        for (char c : quantityStr.toCharArray()) {
                            String keycode;
                            if (c == ' ') {
                                keycode = "space";
                            } else {
                                keycode = String.valueOf(c);
                            }
                            Client.sendKeystroke(keycode);
                            Condition.sleep(20, 40);
                        }
                        Client.sendKeystroke("enter");
                        Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 50);
                        Condition.sleep(500, 750);
                    }
                }

                if ("Overload".equals(potions)) {
                    if (overloadStockCount < overloadDosesNeeded) {
                        int additionalOverloadDosesNeeded = overloadDosesNeeded - overloadStockCount; // Total required: 6 full potions (24 doses)
                        Logger.debugLog("We need to buy more overload doses. Needed: " + overloadDosesNeeded + " We have: " + overloadStockCount);
                        Logger.log("Buying " + additionalOverloadDosesNeeded + " more overload doses.");

                        // Long press and tap Buy-X
                        Client.longPressWithMenuAction(rewardInterfaceOverloadRect, 158, 224, lpMenuTextColors, "Buy-X");
                        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 50);
                        Condition.sleep(1000, 2000);

                        // Convert the integer quantity to a string for keystroke simulation
                        String quantityStr = Integer.toString(additionalOverloadDosesNeeded);
                        // Type the quantity digit by digit
                        for (char c : quantityStr.toCharArray()) {
                            String keycode;
                            if (c == ' ') {
                                keycode = "space";
                            } else {
                                keycode = String.valueOf(c);
                            }
                            Client.sendKeystroke(keycode);
                            Condition.sleep(20, 40);
                        }
                        Client.sendKeystroke("enter");
                        Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 50);
                        Condition.sleep(500, 750);
                    }
                }

                if (rewardInterfaceOpen()) {
                    Logger.debugLog("Closing reward shop");
                    Client.tap(closeRewardInterfaceRect);
                    Condition.wait(() -> !rewardInterfaceOpen(), 100, 50);
                }

                // Now withdraw the amount of doses we need from the barrels.
                if ("Overload".equals(potions)) {
                    if (overloadDosesNeeded > 0) {
                        Logger.debugLog("Taking out " + overloadDosesNeeded + " overload doses from the barrel");

                        if (!Player.atTile(overloadBarrelTile)) {
                            if (Walker.isReachable(overloadBarrelTile)) {
                                Walker.step(overloadBarrelTile);
                                Condition.sleep(500, 750);
                            } else {
                                Walker.webWalk(overloadBarrelTile);
                                Player.waitTillNotMoving(20);
                                Walker.step(overloadBarrelTile);
                                Condition.sleep(500, 750);
                            }
                        }

                        // We should be at the barrel now
                        if (Player.atTile(overloadBarrelTile)) {
                            if (!overloadMESDone) {
                                Client.longPress(tapOverloadBarrelRect);
                                handleBarrelMES("Overload");
                            } else {
                                Client.tap(tapOverloadBarrelRect);
                            }

                            Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 50);
                            if (Chatbox.isMakeMenuVisible()) {
                                // Convert the integer quantity to a string for keystroke simulation
                                String quantityStr = Integer.toString(overloadDosesNeeded);
                                // Type the quantity digit by digit
                                for (char c : quantityStr.toCharArray()) {
                                    String keycode;
                                    if (c == ' ') {
                                        keycode = "space";
                                    } else {
                                        keycode = String.valueOf(c);
                                    }
                                    Client.sendKeystroke(keycode);
                                    Condition.sleep(20, 40);
                                }
                                Client.sendKeystroke("enter");
                                Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 50);
                                Condition.sleep(500, 750);
                            }
                        } else {
                            Logger.debugLog("Failed to path to the overload barrel!");
                            restockDone = false;
                            return false;
                        }
                    }
                }

                if ("Absorption".equals(NMZMethod)) {
                    if (absorbDosesNeeded > 0) {
                        Logger.debugLog("Taking out " + absorbDosesNeeded + " absorption doses from the barrel");

                        if (!Player.atTile(absorptionBarrelTile)) {
                            if (Walker.isReachable(absorptionBarrelTile)) {
                                Walker.step(absorptionBarrelTile);
                                Condition.sleep(500, 750);
                            } else {
                                Walker.webWalk(absorptionBarrelTile);
                                Player.waitTillNotMoving(20);
                                Walker.step(absorptionBarrelTile);
                                Condition.sleep(500, 750);
                            }
                        }

                        // We should be at the barrel now
                        if (Player.atTile(absorptionBarrelTile)) {
                            if (!absorbMESDone) {
                                Client.longPress(tapAbsorptionBarrelRect);
                                handleBarrelMES("Absorption");
                            } else {
                                Client.tap(tapAbsorptionBarrelRect);
                            }

                            Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 50);
                            if (Chatbox.isMakeMenuVisible()) {
                                // Convert the integer quantity to a string for keystroke simulation
                                String quantityStr = Integer.toString(absorbDosesNeeded);
                                // Type the quantity digit by digit
                                for (char c : quantityStr.toCharArray()) {
                                    String keycode;
                                    if (c == ' ') {
                                        keycode = "space";
                                    } else {
                                        keycode = String.valueOf(c);
                                    }
                                    Client.sendKeystroke(keycode);
                                    Condition.sleep(20, 40);
                                }
                                Client.sendKeystroke("enter");
                                Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 50);
                                Condition.sleep(500, 750);
                            }
                        } else {
                            Logger.debugLog("Failed to path to the absorption barrel!");
                            restockDone = false;
                            return false;
                        }
                    }
                }
            }
        } else {
            Logger.debugLog("Not at the reward chest yet, web walking.");
            Walker.webWalk(rewardChestTile);
            Player.waitTillNotMoving(20);
            Walker.step(rewardChestTile);
        }

        if (!needToRestock()) {
            Logger.log("Done stocking up on NMZ potions");
            restockDone = true;
        } else {
            Logger.debugLog("Restock might have failed? Early return from the task.");
            return false;
        }

        return true;
    }

    private void handleBarrelMES(String potion) {
        if (!isTakeTopMost()) {
            if (enableMES()) {
                if (swapMESOptions()) {
                    if (potion.equals("Overload")) {
                        overloadMESDone = true;
                    } else if (potion.equals("Absorption")) {
                        absorbMESDone = true;
                    }
                    tapTakeOption();
                }
            }
        } else {
            if (potion.equals("Overload")) {
                overloadMESDone = true;
            } else if (potion.equals("Absorption")) {
                absorbMESDone = true;
            }
            tapTakeOption();
        }
    }

    private boolean needToRestock() {
        Logger.debugLog("Checking if we need to restock NMZ potions...");

        // Check for overload potions
        int overloadCount = Inventory.count(ItemList.OVERLOAD_4_11730, 0.94, overloadPotColor);
        if (overloadCount < 6) {
            Logger.debugLog("Need to restock overload potions. Found: " + overloadCount + ", Required: 6.");
            return true;
        }
        Logger.debugLog("Sufficient overload potions available: " + overloadCount);

        // Check for absorption potions
        int absorptionCount = Inventory.count(ItemList.ABSORPTION_4_11734, 0.94, absorbPotColor);
        if (absorptionCount < 21) {
            Logger.debugLog("Need to restock absorption potions. Found: " + absorptionCount + ", Required: 21.");
            return true;
        }
        Logger.debugLog("Sufficient absorption potions available: " + absorptionCount);

        Logger.debugLog("No need to restock potions.");
        restockDone = true;
        return false;
    }

    private int calculateDosesNeeded(int dose1Id, int dose2Id, int dose3Id, int dose4Id, Color potionColor, int totalRequiredDoses) {
        Logger.debugLog("Calculating total doses of potions in inventory...");

        // Count doses in inventory
        int dose1Count = Inventory.count(dose1Id, 0.94, potionColor);
        int dose2Count = Inventory.count(dose2Id, 0.94, potionColor);
        int dose3Count = Inventory.count(dose3Id, 0.94, potionColor);
        int dose4Count = Inventory.count(dose4Id, 0.94, potionColor);

        // Calculate total doses available
        int totalDosesAvailable = dose1Count + dose2Count * 2 + dose3Count * 3 + dose4Count * 4;
        Logger.debugLog("Doses available: " + totalDosesAvailable + " (1-dose: " + dose1Count + ", 2-dose: " + dose2Count +
                ", 3-dose: " + dose3Count + ", 4-dose: " + dose4Count + ")");

        // Calculate doses still needed
        int dosesNeeded = Math.max(0, totalRequiredDoses - totalDosesAvailable);
        Logger.debugLog("Doses still needed: " + dosesNeeded);

        return dosesNeeded;
    }

    private int calculateExcessDoses(int dose1Id, int dose2Id, int dose3Id, int dose4Id, Color potionColor, int totalRequiredDoses) {
        Logger.debugLog("Calculating excess doses of potions in inventory...");

        // Count doses in inventory
        int dose1Count = Inventory.count(dose1Id, 0.94, potionColor);
        int dose2Count = Inventory.count(dose2Id, 0.94, potionColor);
        int dose3Count = Inventory.count(dose3Id, 0.94, potionColor);
        int dose4Count = Inventory.count(dose4Id, 0.94, potionColor);

        // Calculate total doses available
        int totalDosesAvailable = dose1Count + dose2Count * 2 + dose3Count * 3 + dose4Count * 4;
        Logger.debugLog("Doses available: " + totalDosesAvailable + " (1-dose: " + dose1Count + ", 2-dose: " + dose2Count +
                ", 3-dose: " + dose3Count + ", 4-dose: " + dose4Count + ")");

        // Calculate excess doses
        int excessDoses = Math.max(0, totalDosesAvailable - totalRequiredDoses);
        Logger.debugLog("Excess doses: " + excessDoses);

        return excessDoses;
    }

    private boolean rewardInterfaceOpen() {
        boolean check1 = Client.isColorInRect(Color.decode("#0e0e0c"), rewardInterfaceCheckRect1, 10);
        boolean check2 = Client.isColorInRect(Color.decode("#0e0e0c"), rewardInterfaceCheckRect2, 10);

        return check1 && check2;
    }

    private void tapStoreOption() {
        Logger.debugLog("Attempting to locate and tap the store option...");

        // Locate the dream option
        Rectangle storeOption = Objects.getBestMatch("/imgs/checkstore.png", 0.8);

        if (storeOption != null) {
            Logger.debugLog("Store option located: " + storeOption);
            Client.tap(storeOption);
            Logger.debugLog("Store option tapped successfully.");
        } else {
            Logger.debugLog("Store option was NOT FOUND.");
        }
    }
}