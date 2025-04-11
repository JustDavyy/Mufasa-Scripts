package tasks;

import helpers.utils.ItemList;
import utils.Task;

import java.awt.*;

import static main.dNMZ.*;

import static helpers.Interfaces.*;

public class Bank extends Task {

    @Override
    public boolean activate() {
        // No need to run this task when we are inside, early return false
        if (insideNMZ) {
            return false;
        }

        // Check if we need to bank
        return needToBank();
    }

    @Override
    public boolean execute() {
        Logger.debugLog("We need to bank!");

        Logger.log("Banking.");
        Logger.debugLog("Get player position.");
        playerPosition = Walker.getPlayerPosition();

        // We are within the bank already
        if (Player.isTileWithinArea(playerPosition, bankArea)) {
            Logger.debugLog("We are located at the Yanille bank: " + playerPosition.toString());
            if (!Player.atTile(bankTile)) {
                Logger.debugLog("Step to bank.");
                Walker.step(bankTile);
                Condition.wait(() -> Player.atTile(bankTile), 100, 50);
            } else {
                Logger.debugLog("We are already at the bank booth.");
            }
        }

        // We are within the NMZ area
        if (Player.isTileWithinArea(playerPosition, NMZAreaOutside)) {
            Logger.debugLog("We are located at the outside area of NMZ: " + playerPosition.toString());

            Logger.debugLog("Pathing to the bank.");
            Walker.walkPath(pathToBank);
            Player.waitTillNotMoving(20);
            Walker.step(bankTile);
            Condition.wait(() -> Player.atTile(bankTile), 100, 50);
        }

        // We should now be at the bank tile
        if (Player.atTile(bankTile)) {
            Logger.debugLog("At bank booth, open bank.");
            Bank.open("Yanille_bank"); // This also handles bank pin, and waits for the bank to be open
        } else { // Not at the bank tile, web walk there.
            Logger.debugLog("Not at bank tile, web walking there.");
            Walker.webWalk(bankTile);
            Player.waitTillNotMoving(20);
            Walker.step(bankTile);

            if (Player.atTile(bankTile)) {
                Logger.debugLog("At bank booth, open bank.");
                Bank.open("Yanille_bank");
            }
        }

        // The bank should now be open, if not we cancel operations
        if (Bank.isOpen()) {
            Logger.debugLog("Deposit inventory");
            Bank.tapDepositInventoryButton();
            Condition.sleep(800, 1400);
            // Go to the right bank tab if needed
            if (!Bank.isSelectedBankTab(banktab)) {
                Bank.openTab(banktab);
                Condition.wait(() -> Bank.isSelectedBankTab(banktab), 250, 12);
                Logger.debugLog("Opened bank tab " + banktab);
                Condition.sleep(600, 800);
            }
            // Withdraw the HP lower item we need, offensive pots and/or prayer pots based on the config entries
            withdrawItems();

            Bank.close();
            Condition.sleep(600, 900);
            if (Bank.isOpen()) {
                Bank.close();
            }
        } else {
            Logger.debugLog("Bank not open, stopping bank task.");
            return false;
        }

        // We should now be done with banking, check if we have properly banked or not
        return doneBanking();
    }

    private void withdrawItems() {
        // Offensive potions part
        Logger.debugLog("Withdrawing the combat potions.");
        // Offensive potions part
        if (potions != null && !potions.isEmpty()) {
            switch (potions) {
                case "Divine super combat":
                    withdrawPotions(ItemList.DIVINE_SUPER_COMBAT_POTION_4_23685, divinesupercombatPotColor, 6);
                    break;
                case "Divine ranging":
                    withdrawPotions(ItemList.DIVINE_RANGING_POTION_4_23733, divinerangingPotColor, 6);
                    break;
                case "Ranging":
                    withdrawPotions(ItemList.RANGING_POTION_4_2444, rangingPotColor, 6);
                    break;
                case "Super att/str combo":
                    withdrawPotions(ItemList.SUPER_ATTACK_4_2436, superattackPotColor, 3);
                    withdrawPotions(ItemList.SUPER_STRENGTH_4_2440, superstrengthPotColor, 3);
                    break;
                case "Super combat":
                    withdrawPotions(ItemList.SUPER_COMBAT_POTION_4_12695, supercombatPotColor, 6);
                    break;
                case "Super strength only":
                    withdrawPotions(ItemList.SUPER_STRENGTH_4_2440, superstrengthPotColor, 6);
                    break;
                default:
                    Logger.debugLog("No offensive potion needed.");
            }
        }

        // Prayer potions part
        if (NMZMethod.startsWith("Prayer")) {
            Logger.debugLog("Withdrawing prayer potions.");
            withdrawPotions(ItemList.PRAYER_POTION_4_2434, prayerPotColor, 22);
        }

        // HP item part
        if (NMZMethod.equals("Absorption")) {
            Logger.debugLog("Withdrawing HP lowering item.");
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 250, 12);
            }

            if (HPMethod.equals("Rock cake")) {
                Bank.withdrawItem(ItemList.DWARVEN_ROCK_CAKE_7510, 0.8);
            } else {
                Bank.withdrawItem(ItemList.LOCATOR_ORB_22081, 0.8);
            }
        }
    }

    private void withdrawPotions(int itemId, Color potionColor, int quantity) {
        int batchesOf10 = quantity / 10;
        int remainingAfter10 = quantity % 10;
        int batchesOf5 = remainingAfter10 / 5;
        int remainder = remainingAfter10 % 5;

        // Withdraw in batches of 10
        if (batchesOf10 > 0) {
            if (!Bank.isSelectedQuantity10Button()) {
                Bank.tapQuantity10Button();
                Condition.wait(Bank::isSelectedQuantity10Button, 250, 12);
                Condition.sleep(250, 500);
            }
            for (int i = 0; i < batchesOf10; i++) {
                Bank.withdrawItem(itemId, 0.75, potionColor);
                Condition.sleep(200, 400);
            }
        }

        // Withdraw in batches of 5
        if (batchesOf5 > 0) {
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(Bank::isSelectedQuantity5Button, 250, 12);
                Condition.sleep(250, 500);
            }
            for (int i = 0; i < batchesOf5; i++) {
                Bank.withdrawItem(itemId, 0.75, potionColor);
                Condition.sleep(200, 400);
            }
        }

        // Withdraw the remainder individually
        if (remainder > 0) {
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(Bank::isSelectedQuantity1Button, 250, 12);
                Condition.sleep(250, 500);
            }
            for (int i = 0; i < remainder; i++) {
                Bank.withdrawItem(itemId, 0.75, potionColor);
                Condition.sleep(200, 400);
            }
        }
    }

    private boolean doneBanking() {
        Logger.debugLog("Checking if banking was successful...");

        // Check HP item if using absorptions
        if ("Absorption".equals(NMZMethod)) {
            if (HPMethod.equals("Rock cake") && !Inventory.contains(ItemList.DWARVEN_ROCK_CAKE_7510, 0.75)) {
                Logger.debugLog("Rock cake is missing from the inventory.");
                return false;
            } else if (!HPMethod.equals("Rock cake") && !Inventory.contains(ItemList.LOCATOR_ORB_22081, 0.75)) {
                Logger.debugLog("Locator orb is missing from the inventory.");
                return false;
            }
            Logger.debugLog("HP lowering item is present in the inventory.");
        }

        // Check prayer potions if using prayer
        if (NMZMethod.startsWith("Prayer")) {
            int prayerPotionCount = Inventory.count(ItemList.PRAYER_POTION_4_2434, 0.75, prayerPotColor);
            if (prayerPotionCount < 22) {
                Logger.debugLog("Prayer potions are insufficient. Expected: 22, Found: " + prayerPotionCount);
                return false;
            }
            Logger.debugLog("Sufficient prayer potions are present in the inventory.");
        }

        // Check offensive potions
        if (potions != null && !potions.isEmpty() && !potions.equals("Overload")) {
            switch (potions) {
                case "Divine super combat":
                    if (Inventory.count(ItemList.DIVINE_SUPER_COMBAT_POTION_4_23685, 0.75, divinesupercombatPotColor) < 6) {
                        Logger.debugLog("Divine super combat potions are insufficient.");
                        return false;
                    }
                    break;
                case "Divine ranging":
                    if (Inventory.count(ItemList.DIVINE_RANGING_POTION_4_23733, 0.75, divinerangingPotColor) < 6) {
                        Logger.debugLog("Divine ranging potions are insufficient.");
                        return false;
                    }
                    break;
                case "Ranging":
                    if (Inventory.count(ItemList.RANGING_POTION_4_2444, 0.75, rangingPotColor) < 6) {
                        Logger.debugLog("Ranging potions are insufficient.");
                        return false;
                    }
                    break;
                case "Super att/str combo":
                    if (Inventory.count(ItemList.SUPER_ATTACK_4_2436, 0.75, superattackPotColor) < 3 ||
                            Inventory.count(ItemList.SUPER_STRENGTH_4_2440, 0.75, superstrengthPotColor) < 3) {
                        Logger.debugLog("Super attack or super strength potions are insufficient.");
                        return false;
                    }
                    break;
                case "Super combat":
                    if (Inventory.count(ItemList.SUPER_COMBAT_POTION_4_12695, 0.75, supercombatPotColor) < 6) {
                        Logger.debugLog("Super combat potions are insufficient.");
                        return false;
                    }
                    break;
                case "Super strength only":
                    if (Inventory.count(ItemList.SUPER_STRENGTH_4_2440, 0.75, superstrengthPotColor) < 6) {
                        Logger.debugLog("Super strength potions are insufficient.");
                        return false;
                    }
                    break;
                default:
                    Logger.debugLog("No offensive potions needed or unrecognized potion type.");
            }
        }

        Logger.debugLog("All required items are present in the inventory.");
        restockDone = true;
        return true;
    }
}