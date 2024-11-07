package Tasks;

import helpers.utils.EquipmentSlot;
import helpers.utils.Skills;
import main.dmCrabberPrivate;
import utils.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;
import static main.dmCrabberPrivate.*;




public class SkillTracker extends Task {
    private boolean timeToCheckStats = true;
    private long lastCheckTime = 0;
    private final long MIN_DELAY = 10 * 60 * 1000; // 10 minutes
    private long randomDelay = MIN_DELAY;
    private final Random random = new Random();
    
    private final Map<String, Boolean> equipmentChecked = new HashMap<>(); // Track equipment checks
    final static Map<String, Boolean> changeToEquipment = new HashMap<>(); // Track equipment change flags
    private static final Map<String, Integer> equipmentItems = Map.of(
        "Iron", 1115, "Addy", 1123, "Leather", 1129,
        "Snakeskin", 6322, "GreenDhide", 1135,
        "RuneScimitar", 1333, "GraniteHammer", 21742
    );

    public SkillTracker() {
        // Prevents spam by checking once.
        equipmentChecked.put("Iron", false);
        equipmentChecked.put("Addy", false);
        equipmentChecked.put("Leather", false);
        equipmentChecked.put("Snakeskin", false);
        equipmentChecked.put("GreenDhide", false);
        equipmentChecked.put("RuneScimitar", false);
        equipmentChecked.put("GraniteHammer", false);
        equipmentChecked.put("WillowShortBow", false);
        equipmentChecked.put("MagicShortBow", false);

        // Set the flag for handling equipment change elsewhere
        changeToEquipment.put("Iron", false);
        changeToEquipment.put("Addy", false);
        changeToEquipment.put("Leather", false);
        changeToEquipment.put("Snakeskin", false);
        changeToEquipment.put("GreenDhide", false);
        changeToEquipment.put("RuneScimitar", false);
        changeToEquipment.put("GraniteHammer", false);
        changeToEquipment.put("WillowShortBow", false);
        changeToEquipment.put("MagicShortBow", false);
    }


    @Override
    public boolean activate() {
        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastCheckTime) >= randomDelay) {
            timeToCheckStats = true;
        }

        return timeToCheckStats;
    }


    @Override
    public boolean execute() {
        if (!GameTabs.isStatsTabOpen()) {
            GameTabs.openStatsTab();
            Condition.wait(GameTabs::isStatsTabOpen, 100, 20);
        }

        if (GameTabs.isStatsTabOpen()) {
            int attackLevel = Stats.getRealLevel(Skills.ATTACK);
            int strengthLevel = Stats.getRealLevel(Skills.STRENGTH);
            int defenceLevel = Stats.getRealLevel(Skills.DEFENCE);
            int rangeLevel = Stats.getRealLevel(Skills.RANGED);

            Logger.log("Levels - Attack: " + attackLevel + ", Strength: " + strengthLevel + ", Defence: " + defenceLevel + ", Range: " + rangeLevel);

            timeToCheckStats = false;
            lastCheckTime = System.currentTimeMillis();
            randomDelay = MIN_DELAY + (random.nextInt(120) - 60) * 1000;

            if ("Melee".equals(dmCrabberPrivate.ChosenBuild)) {
                checkAndEquip("Iron", defenceLevel, 1, 30, EquipmentSlot.BODY);
                checkAndEquip("Addy", defenceLevel, 30, Integer.MAX_VALUE, EquipmentSlot.BODY);
                checkAndEquip("RuneScimitar", attackLevel, 40, 50, EquipmentSlot.WEAPON);
                checkAndEquip("GraniteHammer", attackLevel, 50, Integer.MAX_VALUE, EquipmentSlot.WEAPON);
            } else if ("Ranging".equals(dmCrabberPrivate.ChosenBuild) || (defenceLevel >= 60 && strengthLevel >= 60 && attackLevel >= 60)) {
                checkAndEquip("Leather", defenceLevel, 1, 30, EquipmentSlot.BODY);
                checkAndEquip("Snakeskin", defenceLevel, 30, 40, EquipmentSlot.BODY);
                checkAndEquip("GreenDhide", defenceLevel, 40, Integer.MAX_VALUE, EquipmentSlot.BODY);
                checkAndEquip("WillowShortBow", rangeLevel, 20, 50, EquipmentSlot.WEAPON);
                checkAndEquip("MagicShortBow", rangeLevel, 50, Integer.MAX_VALUE, EquipmentSlot.WEAPON);
            }

            if (defenceLevel > 60 && strenghtLevel > 60 && attackLevel > 60 || "Ranging".equals(dmCrabberPrivate.ChosenBuild)) {
                TrainingCycleManager.nextSkillToTrain = 3;
            }
            GameTabs.openInventoryTab();
            return true;
        }
        return false;
    }


    private void checkAndEquip(String equipmentType, int currentLevel, int minRequired, int maxRequired, EquipmentSlot slot) {
        if (currentLevel >= minRequired && currentLevel < maxRequired && !equipmentChecked.get(equipmentType)) {
            if (!GameTabs.isEquipTabOpen()) {
                GameTabs.openEquipTab();
                Condition.wait(GameTabs::isEquipTabOpen, 100, 20);
            }
    
            if (GameTabs.isEquipTabOpen()) {
                int itemId = equipmentItems.get(equipmentType);
                if (!Equipment.itemAt(slot, itemId)) {
                    changeToEquipment.put(equipmentType, true);
                }
                equipmentChecked.put(equipmentType, true);
            }
        }
    }
}
