package tasks;

import helpers.utils.ItemList;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dPotionProdigy.*;

public class Process extends Task {

    @Override
    public boolean activate() {
        if (prepareScriptStop) {
            if (System.currentTimeMillis() - lastProcessTime > 15000) {
                Logger.debugLog("Script prepared for stop, and no item processed in 15 seconds!");
                doneBanking = true;
                return false;
            }
        }

        return readyToProcess();
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Process task");

        // Check if we have leveled up
        if (Player.leveledUp()) {
            Logger.debugLog("We leveled up, restart processing.");
            Paint.setStatus("Re-process after level up");
            processItems(false);
            lastProcessTime = System.currentTimeMillis(); // Update last process time
            return true;
        }

        if (!initialActiondone) {
            processItems(true);
            lastProcessTime = System.currentTimeMillis(); // Update last process time
            initialActiondone = true;
            return true;
        }

        // Check current used slots
        currentUsedSlots = getUsedSlots();

        // Check if an item has been processed in the last 6 seconds
        if (currentUsedSlots != lastUsedSlots) {
            lastProcessTime = System.currentTimeMillis(); // Update last process time
            lastUsedSlots = currentUsedSlots; // Update last used slots count
        }

        // Check if 6 seconds have passed without processing an item
        if (System.currentTimeMillis() - lastProcessTime > 6000) {
            if (stopScript) {
                doneBanking = true;
                return true;
            }
            Logger.debugLog("No item processed in the last 6 seconds, attempting to re-initiate action");
            processItems(false);
            lastProcessTime = System.currentTimeMillis(); // Update last process time
        } else {
            if (currentUsedSlots != lastUsedSlots) {
                Paint.setStatus("Wait for interrupt or finish");
                Condition.sleep(2000);
            }
            return false;
        }

        return true;
    }

    private int getUsedSlots() {
        switch (product) {
            case "Attack potion":
            case "Antipoison":
            case "Strength potion":
            case "Serum 207":
            case "Guthix rest tea":
            case "Restore potion":
            case "Energy potion":
            case "Defence potion":
            case "Agility potion":
            case "Combat potion":
            case "Prayer potion":
            case "Super attack":
            case "Superantipoison":
            case "Fishing potion":
            case "Super energy":
            case "Hunter potion":
            case "Goading potion":
            case "Super strength":
            case "Prayer regeneration potion":
            case "Weapon poison":
            case "Super restore":
            case "Super defence":
            case "Antidote+":
            case "Antifire potion":
            case "Ranging potion":
            case "Weapon poison+":
            case "Magic potion":
            case "Stamina potion":
            case "Zamorak brew":
            case "Antidote++":
            case "Bastion potion":
            case "Battlemage potion":
            case "Saradomin brew":
            case "Weapon poison++":
            case "Menaphite remedy":
            case "Super combat potion":
            case "Super antifire potion":
            case "Anti-venom+":
            case "Extended super antifire (EAP)":
            case "Attack mix":
            case "Antipoison mix":
            case "Strength mix":
            case "Restore mix":
            case "Energy mix":
            case "Defence mix":
            case "Agility mix":
            case "Combat mix":
            case "Prayer mix":
            case "Superattack mix":
            case "Anti-poison supermix":
            case "Fishing mix":
            case "Super energy mix":
            case "Hunting mix":
            case "Super str. mix":
            case "Super restore mix":
            case "Super def. mix":
            case "Antidote+ mix":
            case "Antifire mix":
            case "Ranging mix":
            case "Magic mix":
            case "Zamorak mix":
            case "Stamina mix":
            case "Extended antifire mix":
            case "Ancient mix":
            case "Super antifire mix":
            case "Extended super antifire mix":
            case "Guam potion (unf)":
            case "Marrentill potion (unf)":
            case "Tarromin potion (unf)":
            case "Harralander potion (unf)":
            case "Ranarr potion (unf)":
            case "Toadflax potion (unf)":
            case "Irit potion (unf)":
            case "Avantoe potion (unf)":
            case "Kwuarm potion (unf)":
            case "Huasca potion (unf)":
            case "Snapdragon potion (unf)":
            case "Cadantine potion (unf)":
            case "Lantadyme potion (unf)":
            case "Dwarf weed potion (unf)":
            case "Torstol potion (unf)":
                return Inventory.usedSlots();
            case "Guam leaf":
            case "Marrentill":
            case "Tarromin":
            case "Harralander":
            case "Ranarr weed":
            case "Toadflax":
            case "Irit leaf":
            case "Avantoe":
            case "Kwuarm":
            case "Huasca":
            case "Snapdragon":
            case "Cadantine":
            case "Lantadyme":
            case "Dwarf weed":
            case "Torstol":
                return Inventory.count(targetItem, 0.9);
            case "Guam tar":
            case "Marrentill tar":
            case "Tarromin tar":
            case "Harralander tar":
            case "Irit tar":
                return Inventory.stackSize(targetItem);
            case "Compost potion":
                return Inventory.stackSize(ItemList.VOLCANIC_ASH_21622);
            case "Ancient brew":
                return Inventory.stackSize(ItemList.NIHIL_DUST_26368);
            case "Divine super attack potion":
            case "Divine super strength potion":
            case "Divine super defence potion":
            case "Divine super combat potion":
            case "Divine bastion potion":
            case "Divine battlemage potion":
            case "Divine magic potion":
            case "Divine ranging potion":
                return Inventory.stackSize(ItemList.CRYSTAL_DUST_23964);
            case "Extended antifire":
            case "Extended super antifire (SAP)":
                return Inventory.stackSize(ItemList.LAVA_SCALE_SHARD_11994);
            case "Anti-venom":
                return Inventory.stackSize(ItemList.ZULRAH_S_SCALES_12934);
            case "Forgotten brew":
                return Inventory.stackSize(ItemList.ANCIENT_ESSENCE_27616);
            case "Extended anti-venom+":
                return Inventory.stackSize(ItemList.ARAXYTE_VENOM_SACK_29784);
            default:
                return Inventory.usedSlots();
        }
    }

    private void tapItemHelper(
            int item1Id, double item1Threshold, String item1Color,
            int item2Id, double item2Threshold, String item2Color,
            int item3Id, double item3Threshold, String item3Color,
            int item4Id, double item4Threshold, String item4Color,
            int item5Id, double item5Threshold, String item5Color
    ) {
        tapItem(item1Id, item1Threshold);
        tapItem(item2Id, item2Threshold);
        tapItem(item3Id, item3Threshold);
        tapItem(item4Id, item4Threshold);
        tapItem(item5Id, item5Threshold);
    }

    private void tapItem(int itemId, double threshold) {
        if (itemId != -1) {
            Inventory.tapItem(itemId, usingCache, threshold);
            Condition.sleep(generateDelay(150, 300));
        }
    }

    private void tapAllItemHelper(
            int item1Id, double item1Threshold, String item1Color,
            int item2Id, double item2Threshold, String item2Color,
            int item3Id, double item3Threshold, String item3Color,
            int item4Id, double item4Threshold, String item4Color,
            int item5Id, double item5Threshold, String item5Color
    ) {
        tapAllItem(item1Id, item1Threshold);
        tapAllItem(item2Id, item2Threshold);
        tapAllItem(item3Id, item3Threshold);
        tapAllItem(item4Id, item4Threshold);
        tapAllItem(item5Id, item5Threshold);
    }

    private void tapAllItem(int itemId, double threshold) {
        if (itemId != -1) {
            Inventory.tapAllItems(itemId, threshold);
            Condition.sleep(generateDelay(150, 300));
        }
    }

    private void tapActivityItems(boolean useCache) {
        usingCache = useCache;
        switch (product) {
            case "Attack potion":
                tapItemHelper(ItemList.GUAM_POTION_UNF_91, 0.7, "#92aaad", ItemList.EYE_OF_NEWT_221, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antipoison":
                tapItemHelper(ItemList.MARRENTILL_POTION_UNF_93, 0.7, "#ad92ad", ItemList.UNICORN_HORN_DUST_235, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Strength potion":
                tapItemHelper(ItemList.TARROMIN_POTION_UNF_95, 0.7, "#abaa91", ItemList.LIMPWURT_ROOT_225, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Serum 207":
                tapItemHelper(ItemList.TARROMIN_POTION_UNF_95, 0.7, "#abaa91", ItemList.ASHES_592, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Guthix rest tea":
                tapItemHelper(ItemList.CUP_OF_HOT_WATER_4460, 0.7, "#8482a9", ItemList.HARRALANDER_255, 0.7, "#4a7007", ItemList.GUAM_LEAF_249, 0.7, "#003304", ItemList.GUAM_LEAF_249, 0.7, "#003304", ItemList.MARRENTILL_251, 0.7, "#076c0a");
                break;
            case "Compost potion":
                tapItemHelper(ItemList.VOLCANIC_ASH_21622, 0.7, null, ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Restore potion":
                tapItemHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.RED_SPIDERS_EGGS_223, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Energy potion":
                tapItemHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.CHOCOLATE_DUST_1975, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Defence potion":
                tapItemHelper(ItemList.RANARR_POTION_UNF_99, 0.7, "#92ad92", ItemList.WHITE_BERRIES_239, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Agility potion":
                tapItemHelper(ItemList.TOADFLAX_POTION_UNF_3002, 0.7, "#6d6c53", ItemList.TOAD_S_LEGS_2152, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Combat potion":
                tapItemHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.GOAT_HORN_DUST_9736, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Prayer potion":
                tapItemHelper(ItemList.RANARR_POTION_UNF_99, 0.7, "#92ad92", ItemList.SNAPE_GRASS_231, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super attack":
                tapItemHelper(ItemList.IRIT_POTION_UNF_101, 0.7, "#aeaeb4", ItemList.EYE_OF_NEWT_221, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Superantipoison":
                tapItemHelper(ItemList.IRIT_POTION_UNF_101, 0.7, "#aeaeb4", ItemList.UNICORN_HORN_DUST_235, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Fishing potion":
                tapItemHelper(ItemList.AVANTOE_POTION_UNF_103, 0.7, "#9b9191", ItemList.SNAPE_GRASS_231, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super energy":
                tapItemHelper(ItemList.AVANTOE_POTION_UNF_103, 0.7, "#9b9191", ItemList.MORT_MYRE_FUNGUS_2970, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Hunter potion":
                tapItemHelper(ItemList.AVANTOE_POTION_UNF_103, 0.7, "#9b9191", ItemList.KEBBIT_TEETH_DUST_10111, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Goading potion":
                tapItemHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.ALDARIUM_29993, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super strength":
                tapItemHelper(ItemList.KWUARM_POTION_UNF_105, 0.7, "#ada6a5", ItemList.LIMPWURT_ROOT_225, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Prayer regeneration potion":
                tapItemHelper(ItemList.HUASCA_POTION_UNF_30100, 0.7, "#927187", ItemList.ALDARIUM_29995, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Weapon poison":
                tapItemHelper(ItemList.KWUARM_POTION_UNF_105, 0.7, "#ada6a5", ItemList.DRAGON_SCALE_DUST_241, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super restore":
                tapItemHelper(ItemList.SNAPDRAGON_POTION_UNF_3004, 0.7, "#a17b30", ItemList.RED_SPIDERS_EGGS_223, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super defence":
                tapItemHelper(ItemList.CADANTINE_POTION_UNF_107, 0.7, "#b5a8a0", ItemList.WHITE_BERRIES_239, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antidote+":
                tapItemHelper(ItemList.ANTIDOTE_UNF_5942, 0.7, null, ItemList.YEW_ROOTS_6049, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antifire potion":
                tapItemHelper(ItemList.LANTADYME_POTION_UNF_2483, 0.7, "#b5a8a0", ItemList.DRAGON_SCALE_DUST_241, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine super attack potion":
                tapItemHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_ATTACK_4_2436, 0.7, "#4547d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine super strength potion":
                tapItemHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_STRENGTH_4_2440, 0.7, "#d2d0d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine super defence potion":
                tapItemHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_DEFENCE_4_2442, 0.7, "#d0ad48", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ranging potion":
                tapItemHelper(ItemList.DWARF_WEED_POTION_UNF_109, 0.7, "#9fa6b5", ItemList.WINE_OF_ZAMORAK_245, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Weapon poison+":
                tapItemHelper(ItemList.WEAPON_POISON_UNF_5936, 0.7, null, ItemList.RED_SPIDERS_EGGS_223, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine ranging potion":
                tapItemHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.RANGING_POTION_4_2444, 0.7, "#48a8d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Magic potion":
                tapItemHelper(ItemList.LANTADYME_POTION_UNF_2483, 0.7, "#b5a8a0", ItemList.POTATO_CACTUS_3138, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Stamina potion":
                tapItemHelper(ItemList.AMYLASE_CRYSTAL_12640, 0.7, null, ItemList.SUPER_ENERGY_4_3016, 0.7, "#ba4e93", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Zamorak brew":
                tapItemHelper(ItemList.TORSTOL_POTION_UNF_111, 0.7, "#b9aea6", ItemList.JANGERBERRIES_247, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine magic potion":
                tapItemHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.MAGIC_POTION_4_3040, 0.7, "#c39f94", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antidote++":
                tapItemHelper(ItemList.ANTIDOTE_UNF_5951, 0.7, null, ItemList.MAGIC_ROOTS_6051, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Bastion potion":
                tapItemHelper(ItemList.CADANTINE_BLOOD_POTION_UNF_22443, 0.7, "#9e5c4f", ItemList.WINE_OF_ZAMORAK_245, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Battlemage potion":
                tapItemHelper(ItemList.CADANTINE_BLOOD_POTION_UNF_22443, 0.7, "#9e5c4f", ItemList.POTATO_CACTUS_3138, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Saradomin brew":
                tapItemHelper(ItemList.TOADFLAX_POTION_UNF_3002, 0.7, "#6d6c53", ItemList.CRUSHED_NEST_6693, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Weapon poison++":
                tapItemHelper(ItemList.WEAPON_POISON_UNF_5939, 0.7, null, ItemList.POISON_IVY_BERRIES_6018, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended antifire":
                tapItemHelper(ItemList.LAVA_SCALE_SHARD_11994, 0.7, null, ItemList.ANTIFIRE_POTION_4_2452, 0.7, "#690a90", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ancient brew":
                tapItemHelper(ItemList.NIHIL_DUST_26368, 0.7, null, ItemList.DWARF_WEED_POTION_UNF_109, 0.7, "#9fa6b5", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine bastion potion":
                tapItemHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.BASTION_POTION_4_22461, 0.7, "#b5550e", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine battlemage potion":
                tapItemHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.BATTLEMAGE_POTION_4_22449, 0.7, "#d89f27", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Anti-venom":
                tapItemHelper(ItemList.ZULRAH_S_SCALES_12934, 0.7, null, ItemList.ANTIDOTE_4_5952, 0.7, "#72752f", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Menaphite remedy":
                tapItemHelper(ItemList.DWARF_WEED_POTION_UNF_109, 0.7, "#9fa6b5", ItemList.LILY_OF_THE_SANDS_27272, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super combat potion":
                tapItemHelper(ItemList.TORSTOL_POTION_UNF_111, 0.7, "#b9aea6", ItemList.SUPER_ATTACK_4_2436, 0.7, "#4547d0", ItemList.SUPER_STRENGTH_4_2440, 0.7, "#d2d0d0",  ItemList.SUPER_DEFENCE_4_2442, 0.7, "#d0ad48", -1, -1, null);
                break;
            case "Forgotten brew":
                tapItemHelper(ItemList.ANCIENT_ESSENCE_27616, 0.7, null, ItemList.ANCIENT_BREW_4_26340, 0.7, "#9f63c9", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super antifire potion":
                tapItemHelper(ItemList.CRUSHED_SUPERIOR_DRAGON_BONES_21975, 0.7, null, ItemList.ANTIFIRE_POTION_4_2452, 0.7, "#690a90", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Anti-venom+":
                tapItemHelper(ItemList.ANTI_VENOM_4_12905, 0.7, "#304037", ItemList.TORSTOL_269, 0.7, "#045407", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended anti-venom+":
                tapItemHelper(ItemList.ARAXYTE_VENOM_SACK_29784, 0.7, null, ItemList.ANTI_VENOM_4_12913, 0.7, "#4e3a41", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine super combat potion":
                tapItemHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_COMBAT_POTION_4_12695, 0.7, "#126106", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended super antifire (SAP)":
                tapItemHelper(ItemList.LAVA_SCALE_SHARD_11994, 0.7, null, ItemList.SUPER_ANTIFIRE_POTION_4_21978, 0.7, "#8253a2", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended super antifire (EAP)":
                tapItemHelper(ItemList.CRUSHED_SUPERIOR_DRAGON_BONES_21975, 0.7, null, ItemList.EXTENDED_ANTIFIRE_4_11951, 0.7, "#622bce", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Attack mix":
                tapItemHelper(ItemList.ROE_11324, 0.7, null, ItemList.ATTACK_POTION_2_123, 0.7, "#40c9d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antipoison mix":
                tapItemHelper(ItemList.ROE_11324, 0.7, null, ItemList.ANTIPOISON_2_177, 0.7, "#6ed816", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Strength mix":
                tapItemHelper(ItemList.ROE_11324, 0.7, null, ItemList.STRENGTH_POTION_2_117, 0.7, "#cfcd3d", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Restore mix":
                tapItemHelper(ItemList.ROE_11324, 0.7, null, ItemList.RESTORE_POTION_2_129, 0.7, "#d04840", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Energy mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ENERGY_POTION_2_3012, 0.7, "#9e4f5c", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Defence mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.DEFENCE_POTION_2_135, 0.7, "#40d043", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Agility mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.AGILITY_POTION_2_3036, 0.7, "#6d870a", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Combat mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.COMBAT_POTION_2_9743, 0.7, "#7db467", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Prayer mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.PRAYER_POTION_2_141, 0.7, "#3dcf98", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Superattack mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_ATTACK_2_147, 0.7, "#4043d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Anti-poison supermix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPERANTIPOISON_2_183, 0.7, "#d81c6b", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Fishing mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.FISHING_POTION_2_153, 0.7, "#413c3c", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super energy mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_ENERGY_2_3020, 0.7, "#b74991", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Hunting mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.HUNTER_POTION_2_10002, 0.7, "#065053", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super str. mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_STRENGTH_2_159, 0.7, "#cecbcb", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super restore mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_RESTORE_2_3028, 0.7, "#ae3262", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super def. mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_DEFENCE_2_165, 0.7, "#d0ab40", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antidote+ mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ANTIDOTE_2_5947, 0.7, "#717156", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antifire mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ANTIFIRE_POTION_2_2456, 0.7, "#690a90", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ranging mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.RANGING_POTION_2_171, 0.7, "#3da6cf", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Magic mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.MAGIC_POTION_2_3044, 0.7, "#bf9a8f", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Zamorak mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ZAMORAK_BREW_2_191, 0.7, "#c78d10", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Stamina mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.STAMINA_POTION_2_12629, 0.7, "#916a3a", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended antifire mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.EXTENDED_ANTIFIRE_2_11955, 0.7, "#5e27cb", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ancient mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ANCIENT_BREW_2_26344, 0.7, "#9e61c9", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super antifire mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_ANTIFIRE_POTION_2_21984, 0.7, "#7d529f", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended super antifire mix":
                tapItemHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.EXTENDED_SUPER_ANTIFIRE_2_22215, 0.7, "#a680c4", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Guam tar":
                tapItemHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.GUAM_LEAF_249, 0.7, "#044104", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Marrentill tar":
                tapItemHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.MARRENTILL_251, 0.7, "#076c0a", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Tarromin tar":
                tapItemHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.TARROMIN_253, 0.7, "#076c2f", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Harralander tar":
                tapItemHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.HARRALANDER_255, 0.7, "#456807", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Irit tar":
                tapItemHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.IRIT_LEAF_259, 0.7, "#47702c", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Guam potion (unf)":
                tapItemHelper(ItemList.GUAM_LEAF_249, 0.7, "#044104", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Marrentill potion (unf)":
                tapItemHelper(ItemList.MARRENTILL_251, 0.7, "#076c0a", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Tarromin potion (unf)":
                tapItemHelper(ItemList.TARROMIN_253, 0.7, "#076c2f", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Harralander potion (unf)":
                tapItemHelper(ItemList.HARRALANDER_255, 0.7, "#456807", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ranarr potion (unf)":
                tapItemHelper(ItemList.RANARR_WEED_257, 0.7, "#335904", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Toadflax potion (unf)":
                tapItemHelper(ItemList.TOADFLAX_2998, 0.7, "#002100", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Irit potion (unf)":
                tapItemHelper(ItemList.IRIT_LEAF_259, 0.7, "#47702c", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Avantoe potion (unf)":
                tapItemHelper(ItemList.AVANTOE_261, 0.7, "#0c4c1e", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Kwuarm potion (unf)":
                tapItemHelper(ItemList.KWUARM_263, 0.7, "#455004", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Huasca potion (unf)":
                tapItemHelper(ItemList.HUASCA_30097, 0.7, "#4c2f41", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Snapdragon potion (unf)":
                tapItemHelper(ItemList.SNAPDRAGON_3000, 0.7, "#344104", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Cadantine potion (unf)":
                tapItemHelper(ItemList.CADANTINE_265, 0.7, "#3c4715", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Lantadyme potion (unf)":
                tapItemHelper(ItemList.LANTADYME_2481, 0.7, "#04463a", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Dwarf weed potion (unf)":
                tapItemHelper(ItemList.DWARF_WEED_267, 0.7, "#043c04", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Torstol potion (unf)":
                tapItemHelper(ItemList.TORSTOL_269, 0.7, "#045007", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Guam leaf":
                tapAllItemHelper(ItemList.GRIMY_GUAM_LEAF_199, 0.7, "#044104", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Marrentill":
                tapAllItemHelper(ItemList.GRIMY_MARRENTILL_201, 0.8, "#076c0a", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Tarromin":
                tapAllItemHelper(ItemList.GRIMY_TARROMIN_203, 0.8, "#076c2f", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Harralander":
                tapAllItemHelper(ItemList.GRIMY_HARRALANDER_205, 0.92, "#456807", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ranarr weed":
                tapAllItemHelper(ItemList.GRIMY_RANARR_WEED_207, 0.87, "#335904", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Toadflax":
                tapAllItemHelper(ItemList.GRIMY_TOADFLAX_3049, 0.7, "#002100", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Irit leaf":
                tapAllItemHelper(ItemList.GRIMY_IRIT_LEAF_209, 0.92, "#47702c", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Avantoe":
                tapAllItemHelper(ItemList.GRIMY_AVANTOE_211, 0.7, "#0c4c1e", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Kwuarm":
                tapAllItemHelper(ItemList.GRIMY_KWUARM_213, 0.89, "#455004", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Huasca":
                tapAllItemHelper(ItemList.GRIMY_HUASCA_30094, 0.88, "#4c2f41", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Snapdragon":
                tapAllItemHelper(ItemList.GRIMY_SNAPDRAGON_3051, 0.89, "#344104", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Cadantine":
                tapAllItemHelper(ItemList.GRIMY_CADANTINE_215, 0.87, "#3c4715", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Lantadyme":
                tapAllItemHelper(ItemList.GRIMY_LANTADYME_2485, 0.7, "#04463a", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Dwarf weed":
                tapAllItemHelper(ItemList.GRIMY_DWARF_WEED_217, 0.7, "#043c04", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Torstol":
                tapAllItemHelper(ItemList.GRIMY_TORSTOL_219, 0.7, "#045007", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            default:
                Logger.log("Unknown product in tapActivityItems (process): " + product + " stopping script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(2000);
                }
                Logout.logout();
                Script.stop();
        }
    }

    private void processItems(boolean useCache) {
        Paint.setStatus("Process items");
        Logger.log("Process items");
        boolean fromLevelup = Player.leveledUp();
        // Tap the items with a small random delay in between actions
        tapActivityItems(useCache);

        // Wait for the make menu to be visible
        Paint.setStatus("Wait for make menu");
        if (fromLevelup) {
            Condition.sleep(generateDelay(900, 1200));
        }
        Condition.wait(Chatbox::isMakeMenuVisible, 100, 40);
        Client.sendKeystroke("space");
        Condition.sleep(generateDelay(750, 1000));
    }
}