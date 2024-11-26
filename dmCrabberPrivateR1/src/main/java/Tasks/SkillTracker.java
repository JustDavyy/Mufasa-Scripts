package Tasks;

import helpers.utils.EquipmentSlot;
import helpers.utils.Skills;
import main.dmCrabberPrivate;
import main.dmCrabberPrivate.TrainingCycleManager;
import utils.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;
import static main.dmCrabberPrivate.*;

public class SkillTracker extends Task {

    private long lastCheckTime = 0;
    private final long MIN_DELAY = 10 * 60 * 1000; // 10 minutes
    private long randomDelay = MIN_DELAY;
    private final Random random = new Random();

    public static int attackLevel = 0;
    public static int strengthLevel = 0;
    public static int defenceLevel = 0;
    public static int rangeLevel = 0;

    private final static Map<String, EquipmentStatus> equipmentStatus = new HashMap<>();
        
    
    private static final Map<String, Integer> equipmentItems = Map.of(
        "Iron", 1115, "Addy", 1123, "Leather", 1129,
        "Snakeskin", 6322, "GreenDhide", 1135,
        "RuneScimitar", 1333, "GraniteHammer", 21742,
        "WillowShortBow", 843, "MagicShortBow", 861
    );

    public enum EquipmentStatus {
        NOT_CHECKED,
        TO_EQUIP,
        EQUIPPED
    }

    public SkillTracker() {
        for (String key : equipmentItems.keySet()) {
            equipmentStatus.put(key, EquipmentStatus.NOT_CHECKED);
        }
    }

    public static Map<String, EquipmentStatus> getEquipmentStatus() {
        return equipmentStatus;
    }

    private long getRandomDelay() {
        // Generate a random delay between 3 and 10 minutes (in milliseconds)
        return (3 + random.nextInt(8)) * 60 * 1000;
    }

    @Override
    public boolean activate() {
        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastCheckTime) >= randomDelay) {
            lastCheckTime = currentTime; // Update the last check time
            randomDelay = getRandomDelay(); // Set a new random delay
            return true; // Trigger the action
        }

        return false; // Not yet time to trigger
    }

    @Override
    public boolean execute() {
        if (!GameTabs.isStatsTabOpen()) {
            GameTabs.openStatsTab();
            Condition.wait(GameTabs::isStatsTabOpen, 100, 20);
        }

        if (GameTabs.isStatsTabOpen()) {
            updateStats();
            determineNextSkillToTrain();

            if ("Melee".equals(dmCrabberPrivate.ChosenBuild)) {
                handleMeleeGear();
            } else if ("Ranging".equals(dmCrabberPrivate.ChosenBuild)) {
                handleRangingGear();
            }

            if (defenceLevel > 70 && strengthLevel > 70 && attackLevel > 70 ||
                "Ranging".equals(dmCrabberPrivate.ChosenBuild)) {
                TrainingCycleManager.nextSkillToTrain = 3;
            }

            GameTabs.openInventoryTab();
            randomDelay = getRandomDelay();
            lastCheckTime = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    private void updateStats() {
        attackLevel = Stats.getRealLevel(Skills.ATTACK);
        strengthLevel = Stats.getRealLevel(Skills.STRENGTH);
        defenceLevel = Stats.getRealLevel(Skills.DEFENCE);
        rangeLevel = Stats.getRealLevel(Skills.RANGED);

        Logger.log("Levels - Attack: " + attackLevel + ", Strength: " + strengthLevel +
                   ", Defence: " + defenceLevel + ", Range: " + rangeLevel);
    }

    private void handleMeleeGear() {
        checkAndEquip("Iron", defenceLevel, 1, 5, EquipmentSlot.BODY);
        checkAndEquip("Addy", defenceLevel, 30, Integer.MAX_VALUE, EquipmentSlot.BODY);
        checkAndEquip("RuneScimitar", attackLevel, 40, 50, EquipmentSlot.WEAPON);
        checkAndEquip("GraniteHammer", attackLevel, 50, Integer.MAX_VALUE, EquipmentSlot.WEAPON);
    }

    private void handleRangingGear() {
        checkAndEquip("Leather", defenceLevel, 1, 30, EquipmentSlot.BODY);
        checkAndEquip("Snakeskin", defenceLevel, 30, 40, EquipmentSlot.BODY);
        checkAndEquip("GreenDhide", defenceLevel, 40, Integer.MAX_VALUE, EquipmentSlot.BODY);
        checkAndEquip("WillowShortBow", rangeLevel, 20, 50, EquipmentSlot.WEAPON);
        checkAndEquip("MagicShortBow", rangeLevel, 50, Integer.MAX_VALUE, EquipmentSlot.WEAPON);
    }

    private void checkAndEquip(String equipmentType, int currentLevel, int minRequired, int maxRequired, EquipmentSlot slot) {
        if (currentLevel >= minRequired && currentLevel < maxRequired) {
            if (equipmentStatus.get(equipmentType) == EquipmentStatus.EQUIPPED) {
                Logger.debugLog(equipmentType + " already equipped.");
                return;
            }

            if (!GameTabs.isEquipTabOpen()) {
                GameTabs.openEquipTab();
                Condition.wait(GameTabs::isEquipTabOpen, 300, 20);
            }

            if (GameTabs.isEquipTabOpen()) {
                Integer itemId = equipmentItems.get(equipmentType);

                if (itemId == null) {
                    Logger.log("Invalid equipment type: " + equipmentType);
                    return;
                }

                if (!Equipment.itemAt(slot, itemId)) {
                    equipmentStatus.put(equipmentType, EquipmentStatus.TO_EQUIP);
                    Logger.debugLog("Flagged " + equipmentType + " for equipping.");
                    walkToBank(equipmentType);
                } else {
                    equipmentStatus.put(equipmentType, EquipmentStatus.EQUIPPED);
                }
            }
        }
    }

    private void walkToBank(String equipmentType) {
        Logger.log("Walking to bank to equip " + equipmentType);
        Walker.webWalk(Eat.bankTile);
    }
}
