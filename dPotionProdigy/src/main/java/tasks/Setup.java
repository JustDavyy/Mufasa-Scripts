package tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dPotionProdigy.*;

public class Setup extends Task {

    private static boolean mesSetupSucceeded = false;

    @Override
    public boolean activate() {
        return !setupDone;
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Initial Setup");
        Logger.log("Initial Setup");

        Paint.setStatus("Open inventory");
        GameTabs.openTab(UITabs.INVENTORY);

        Paint.setStatus("Find dynamic bank");
        findDynamicBank();

        Paint.setStatus("Open dynamic bank");
        openDynamicBank();

        if (Bank.isOpen()) {
            Paint.setStatus("Deposit inventory");
            Bank.tapDepositInventoryButton();

            if (product.endsWith(" tar")) {
                Paint.setStatus("Withdraw pestle and mortar");
                withdrawTool(ItemList.PESTLE_AND_MORTAR_233, "pestle", null, "1");
            }

            Paint.setStatus("Setup bank at start");
            setupBanking();

            Paint.setStatus("Withdraw first items");
            withdrawFirstItems();

            Paint.setStatus("Close bank");
            closeBank();

            if (product.endsWith("mix")) {
                Paint.setStatus("Setup MES");
                Logger.debugLog("Setting up Menu Entry Swapper for Roe/Caviar");
                handleMES();

                Condition.sleep(1500);

                if (!mesSetupSucceeded) {
                    Logger.debugLog("Menu Entry Swapper setup failed, stopping script!");
                    Logout.logout();
                    Script.stop();
                }
            }

            setupDone = true;
        } else {
            setupDone = false;
        }

        return false;
    }


    private void withdrawFirstItems() {
        switch (product) {
            case "Attack potion":
                withdrawHelper(ItemList.GUAM_POTION_UNF_91, 0.7, "#92aaad", ItemList.EYE_OF_NEWT_221, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antipoison":
                withdrawHelper(ItemList.MARRENTILL_POTION_UNF_93, 0.7, "#ad92ad", ItemList.UNICORN_HORN_DUST_235, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Strength potion":
                withdrawHelper(ItemList.TARROMIN_POTION_UNF_95, 0.7, "#abaa91", ItemList.LIMPWURT_ROOT_225, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Serum 207":
                withdrawHelper(ItemList.TARROMIN_POTION_UNF_95, 0.7, "#abaa91", ItemList.ASHES_592, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Guthix rest tea":
                withdrawHelper(ItemList.CUP_OF_HOT_WATER_4460, 0.7, "#8482a9", ItemList.HARRALANDER_255, 0.7, "#4a7007", ItemList.GUAM_LEAF_249, 0.7, "#003304", ItemList.GUAM_LEAF_249, 0.7, "#003304", ItemList.MARRENTILL_251, 0.7, "#076c0a");
                break;
            case "Compost potion":
                withdrawHelper(ItemList.VOLCANIC_ASH_21622, 0.7, null, ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Restore potion":
                withdrawHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.RED_SPIDERS_EGGS_223, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Energy potion":
                withdrawHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.CHOCOLATE_DUST_1975, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Defence potion":
                withdrawHelper(ItemList.RANARR_POTION_UNF_99, 0.7, "#92ad92", ItemList.WHITE_BERRIES_239, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Agility potion":
                withdrawHelper(ItemList.TOADFLAX_POTION_UNF_3002, 0.7, "#6d6c53", ItemList.TOAD_S_LEGS_2152, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Combat potion":
                withdrawHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.GOAT_HORN_DUST_9736, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Prayer potion":
                withdrawHelper(ItemList.RANARR_POTION_UNF_99, 0.7, "#92ad92", ItemList.SNAPE_GRASS_231, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super attack":
                withdrawHelper(ItemList.IRIT_POTION_UNF_101, 0.7, "#aeaeb4", ItemList.EYE_OF_NEWT_221, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Superantipoison":
                withdrawHelper(ItemList.IRIT_POTION_UNF_101, 0.7, "#aeaeb4", ItemList.UNICORN_HORN_DUST_235, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Fishing potion":
                withdrawHelper(ItemList.AVANTOE_POTION_UNF_103, 0.7, "#9b9191", ItemList.SNAPE_GRASS_231, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super energy":
                withdrawHelper(ItemList.AVANTOE_POTION_UNF_103, 0.7, "#9b9191", ItemList.MORT_MYRE_FUNGUS_2970, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Hunter potion":
                withdrawHelper(ItemList.AVANTOE_POTION_UNF_103, 0.7, "#9b9191", ItemList.KEBBIT_TEETH_DUST_10111, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Goading potion":
                withdrawHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.ALDARIUM_29993, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super strength":
                withdrawHelper(ItemList.KWUARM_POTION_UNF_105, 0.7, "#ada6a5", ItemList.LIMPWURT_ROOT_225, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Prayer regeneration potion":
                withdrawHelper(ItemList.HUASCA_POTION_UNF_30100, 0.7, "#927187", ItemList.ALDARIUM_29995, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Weapon poison":
                withdrawHelper(ItemList.KWUARM_POTION_UNF_105, 0.7, "#ada6a5", ItemList.DRAGON_SCALE_DUST_241, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super restore":
                withdrawHelper(ItemList.SNAPDRAGON_POTION_UNF_3004, 0.7, "#a17b30", ItemList.RED_SPIDERS_EGGS_223, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super defence":
                withdrawHelper(ItemList.CADANTINE_POTION_UNF_107, 0.7, "#b5a8a0", ItemList.WHITE_BERRIES_239, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antidote+":
                withdrawHelper(ItemList.ANTIDOTE_UNF_5942, 0.7, null, ItemList.YEW_ROOTS_6049, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antifire potion":
                withdrawHelper(ItemList.LANTADYME_POTION_UNF_2483, 0.7, "#b5a8a0", ItemList.DRAGON_SCALE_DUST_241, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine super attack potion":
                withdrawHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_ATTACK_4_2436, 0.7, "#4547d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine super strength potion":
                withdrawHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_STRENGTH_4_2440, 0.7, "#d2d0d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine super defence potion":
                withdrawHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_DEFENCE_4_2442, 0.7, "#d0ad48", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ranging potion":
                withdrawHelper(ItemList.DWARF_WEED_POTION_UNF_109, 0.7, "#9fa6b5", ItemList.WINE_OF_ZAMORAK_245, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Weapon poison+":
                withdrawHelper(ItemList.WEAPON_POISON_UNF_5936, 0.7, null, ItemList.RED_SPIDERS_EGGS_223, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine ranging potion":
                withdrawHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.RANGING_POTION_4_2444, 0.7, "#48a8d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Magic potion":
                withdrawHelper(ItemList.LANTADYME_POTION_UNF_2483, 0.7, "#b5a8a0", ItemList.POTATO_CACTUS_3138, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Stamina potion":
                withdrawHelper(ItemList.AMYLASE_CRYSTAL_12640, 0.7, null, ItemList.SUPER_ENERGY_4_3016, 0.7, "#ba4e93", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Zamorak brew":
                withdrawHelper(ItemList.TORSTOL_POTION_UNF_111, 0.7, "#b9aea6", ItemList.JANGERBERRIES_247, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine magic potion":
                withdrawHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.MAGIC_POTION_4_3040, 0.7, "#c39f94", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antidote++":
                withdrawHelper(ItemList.ANTIDOTE_UNF_5951, 0.7, null, ItemList.MAGIC_ROOTS_6051, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Bastion potion":
                withdrawHelper(ItemList.CADANTINE_BLOOD_POTION_UNF_22443, 0.7, "#9e5c4f", ItemList.WINE_OF_ZAMORAK_245, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Battlemage potion":
                withdrawHelper(ItemList.CADANTINE_BLOOD_POTION_UNF_22443, 0.7, "#9e5c4f", ItemList.POTATO_CACTUS_3138, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Saradomin brew":
                withdrawHelper(ItemList.TOADFLAX_POTION_UNF_3002, 0.7, "#6d6c53", ItemList.CRUSHED_NEST_6693, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Weapon poison++":
                withdrawHelper(ItemList.WEAPON_POISON_UNF_5939, 0.7, null, ItemList.POISON_IVY_BERRIES_6018, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended antifire":
                withdrawHelper(ItemList.LAVA_SCALE_SHARD_11994, 0.7, null, ItemList.ANTIFIRE_POTION_4_2452, 0.7, "#690a90", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ancient brew":
                withdrawHelper(ItemList.NIHIL_DUST_26368, 0.7, null, ItemList.DWARF_WEED_POTION_UNF_109, 0.7, "#9fa6b5", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine bastion potion":
                withdrawHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.BASTION_POTION_4_22461, 0.7, "#b5550e", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine battlemage potion":
                withdrawHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.BATTLEMAGE_POTION_4_22449, 0.7, "#d89f27", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Anti-venom":
                withdrawHelper(ItemList.ZULRAH_S_SCALES_12934, 0.7, null, ItemList.ANTIDOTE_4_5952, 0.7, "#72752f", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Menaphite remedy":
                withdrawHelper(ItemList.DWARF_WEED_POTION_UNF_109, 0.7, "#9fa6b5", ItemList.LILY_OF_THE_SANDS_27272, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super combat potion":
                withdrawHelper(ItemList.TORSTOL_POTION_UNF_111, 0.7, "#b9aea6", ItemList.SUPER_ATTACK_4_2436, 0.7, "#4547d0", ItemList.SUPER_STRENGTH_4_2440, 0.7, "#d2d0d0",  ItemList.SUPER_DEFENCE_4_2442, 0.7, "#d0ad48", -1, -1, null);
                break;
            case "Forgotten brew":
                withdrawHelper(ItemList.ANCIENT_ESSENCE_27616, 0.7, null, ItemList.ANCIENT_BREW_4_26340, 0.7, "#9f63c9", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super antifire potion":
                withdrawHelper(ItemList.CRUSHED_SUPERIOR_DRAGON_BONES_21975, 0.7, null, ItemList.ANTIFIRE_POTION_4_2452, 0.7, "#690a90", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Anti-venom+":
                withdrawHelper(ItemList.ANTI_VENOM_4_12905, 0.7, "#304037", ItemList.TORSTOL_269, 0.7, "#045407", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended anti-venom+":
                withdrawHelper(ItemList.ARAXYTE_VENOM_SACK_29784, 0.7, null, ItemList.ANTI_VENOM_4_12913, 0.7, "#4e3a41", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Divine super combat potion":
                withdrawHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_COMBAT_POTION_4_12695, 0.7, "#126106", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended super antifire (SAP)":
                withdrawHelper(ItemList.LAVA_SCALE_SHARD_11994, 0.7, null, ItemList.SUPER_ANTIFIRE_POTION_4_21978, 0.7, "#8253a2", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended super antifire (EAP)":
                withdrawHelper(ItemList.CRUSHED_SUPERIOR_DRAGON_BONES_21975, 0.7, null, ItemList.EXTENDED_ANTIFIRE_4_11951, 0.7, "#622bce", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Attack mix":
                withdrawHelper(ItemList.ROE_11324, 0.7, null, ItemList.ATTACK_POTION_2_123, 0.7, "#40c9d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antipoison mix":
                withdrawHelper(ItemList.ROE_11324, 0.7, null, ItemList.ANTIPOISON_2_177, 0.7, "#6ed816", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Strength mix":
                withdrawHelper(ItemList.ROE_11324, 0.7, null, ItemList.STRENGTH_POTION_2_117, 0.7, "#cfcd3d", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Restore mix":
                withdrawHelper(ItemList.ROE_11324, 0.7, null, ItemList.RESTORE_POTION_2_129, 0.7, "#d04840", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Energy mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ENERGY_POTION_2_3012, 0.7, "#9e4f5c", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Defence mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.DEFENCE_POTION_2_135, 0.7, "#40d043", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Agility mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.AGILITY_POTION_2_3036, 0.7, "#6d870a", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Combat mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.COMBAT_POTION_2_9743, 0.7, "#7db467", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Prayer mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.PRAYER_POTION_2_141, 0.7, "#3dcf98", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Superattack mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_ATTACK_2_147, 0.7, "#4043d0", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Anti-poison supermix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPERANTIPOISON_2_183, 0.7, "#d81c6b", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Fishing mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.FISHING_POTION_2_153, 0.7, "#413c3c", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super energy mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_ENERGY_2_3020, 0.7, "#b74991", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Hunting mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.HUNTER_POTION_2_10002, 0.7, "#065053", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super str. mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_STRENGTH_2_159, 0.7, "#cecbcb", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super restore mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_RESTORE_2_3028, 0.7, "#ae3262", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super def. mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_DEFENCE_2_165, 0.7, "#d0ab40", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antidote+ mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ANTIDOTE_2_5947, 0.7, "#717156", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Antifire mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ANTIFIRE_POTION_2_2456, 0.7, "#690a90", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ranging mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.RANGING_POTION_2_171, 0.7, "#3da6cf", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Magic mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.MAGIC_POTION_2_3044, 0.7, "#bf9a8f", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Zamorak mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ZAMORAK_BREW_2_191, 0.7, "#c78d10", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Stamina mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.STAMINA_POTION_2_12629, 0.7, "#916a3a", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended antifire mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.EXTENDED_ANTIFIRE_2_11955, 0.7, "#5e27cb", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ancient mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ANCIENT_BREW_2_26344, 0.7, "#9e61c9", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Super antifire mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_ANTIFIRE_POTION_2_21984, 0.7, "#7d529f", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Extended super antifire mix":
                withdrawHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.EXTENDED_SUPER_ANTIFIRE_2_22215, 0.7, "#a680c4", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Guam tar":
                withdrawHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.GUAM_LEAF_249, 0.7, "#044104", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Marrentill tar":
                withdrawHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.MARRENTILL_251, 0.7, "#076c0a", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Tarromin tar":
                withdrawHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.TARROMIN_253, 0.7, "#076c2f", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Harralander tar":
                withdrawHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.HARRALANDER_255, 0.7, "#456807", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Irit tar":
                withdrawHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.IRIT_LEAF_259, 0.7, "#47702c", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Guam potion (unf)":
                withdrawHelper(ItemList.GUAM_LEAF_249, 0.7, "#044104", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Marrentill potion (unf)":
                withdrawHelper(ItemList.MARRENTILL_251, 0.7, "#076c0a", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Tarromin potion (unf)":
                withdrawHelper(ItemList.TARROMIN_253, 0.7, "#076c2f", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Harralander potion (unf)":
                withdrawHelper(ItemList.HARRALANDER_255, 0.7, "#456807", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ranarr potion (unf)":
                withdrawHelper(ItemList.RANARR_WEED_257, 0.7, "#335904", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Toadflax potion (unf)":
                withdrawHelper(ItemList.TOADFLAX_2998, 0.7, "#002100", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Irit potion (unf)":
                withdrawHelper(ItemList.IRIT_LEAF_259, 0.7, "#47702c", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Avantoe potion (unf)":
                withdrawHelper(ItemList.AVANTOE_261, 0.7, "#0c4c1e", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Kwuarm potion (unf)":
                withdrawHelper(ItemList.KWUARM_263, 0.7, "#455004", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Huasca potion (unf)":
                withdrawHelper(ItemList.HUASCA_30097, 0.7, "#4c2f41", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Snapdragon potion (unf)":
                withdrawHelper(ItemList.SNAPDRAGON_3000, 0.7, "#344104", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Cadantine potion (unf)":
                withdrawHelper(ItemList.CADANTINE_265, 0.7, "#3c4715", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Lantadyme potion (unf)":
                withdrawHelper(ItemList.LANTADYME_2481, 0.7, "#04463a", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Dwarf weed potion (unf)":
                withdrawHelper(ItemList.DWARF_WEED_267, 0.7, "#043c04", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Torstol potion (unf)":
                withdrawHelper(ItemList.TORSTOL_269, 0.7, "#045007", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Guam leaf":
                withdrawHelper(ItemList.GRIMY_GUAM_LEAF_199, 0.7, "#044104", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Marrentill":
                withdrawHelper(ItemList.GRIMY_MARRENTILL_201, 0.8, "#076c0a", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Tarromin":
                withdrawHelper(ItemList.GRIMY_TARROMIN_203, 0.8, "#076c2f", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Harralander":
                withdrawHelper(ItemList.GRIMY_HARRALANDER_205, 0.92, "#456807", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ranarr weed":
                withdrawHelper(ItemList.GRIMY_RANARR_WEED_207, 0.87, "#335904", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Toadflax":
                withdrawHelper(ItemList.GRIMY_TOADFLAX_3049, 0.7, "#002100", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Irit leaf":
                withdrawHelper(ItemList.GRIMY_IRIT_LEAF_209, 0.92, "#47702c", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Avantoe":
                withdrawHelper(ItemList.GRIMY_AVANTOE_211, 0.7, "#0c4c1e", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Kwuarm":
                withdrawHelper(ItemList.GRIMY_KWUARM_213, 0.89, "#455004", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Huasca":
                withdrawHelper(ItemList.GRIMY_HUASCA_30094, 0.7, "#4c2f41", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Snapdragon":
                withdrawHelper(ItemList.GRIMY_SNAPDRAGON_3051, 0.7, "#344104", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Cadantine":
                withdrawHelper(ItemList.GRIMY_CADANTINE_215, 0.7, "#3c4715", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Lantadyme":
                withdrawHelper(ItemList.GRIMY_LANTADYME_2485, 0.7, "#04463a", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Dwarf weed":
                withdrawHelper(ItemList.GRIMY_DWARF_WEED_217, 0.7, "#043c04", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Torstol":
                withdrawHelper(ItemList.GRIMY_TORSTOL_219, 0.7, "#045007", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            default:
                Logger.log("Unknown product in withdrawFirstItems (setup): " + product + " stopping script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(2000);
                }
                Logout.logout();
                Script.stop();
        }

        // Wait for a small bit
        Condition.sleep(generateDelay(125, 250));
    }

    private void withdrawHelper(
            int item1Id, double item1Threshold, String item1Color,
            int item2Id, double item2Threshold, String item2Color,
            int item3Id, double item3Threshold, String item3Color,
            int item4Id, double item4Threshold, String item4Color,
            int item5Id, double item5Threshold, String item5Color
    ) {
        bankItem1Count = withdrawItem(item1Id, item1Threshold, item1Color, bankItem1Count);
        bankItem1Count = withdrawItem(item2Id, item2Threshold, item2Color, bankItem1Count);
        bankItem1Count = withdrawItem(item3Id, item3Threshold, item3Color, bankItem1Count);
        bankItem1Count = withdrawItem(item4Id, item4Threshold, item4Color, bankItem1Count);
        bankItem1Count = withdrawItem(item5Id, item5Threshold, item5Color, bankItem1Count);
    }

    private int withdrawItem(int itemId, double threshold, String color, int currentCount) {
        if (itemId == -1) return currentCount;
        int stackSize = (color != null)
                ? Bank.stackSize(itemId, Color.decode(color))
                : Bank.stackSize(itemId);
        Logger.debugLog("Stack size during setup: " + stackSize);
        if (color != null) {
            Logger.debugLog("Withdraw with color");
            Bank.withdrawItem(itemId, threshold, Color.decode(color));
            Condition.sleep(generateDelay(150, 300));
        } else {
            Logger.debugLog("Withdraw without color");
            Bank.withdrawItem(itemId, threshold);
            Condition.sleep(generateDelay(150, 300));
        }

        if (currentCount == 0 && stackSize > 0) {return stackSize;}
        if (stackSize < currentCount) {return stackSize;}

        return currentCount;
    }

    private void findDynamicBank() {
        if (bankloc == null) {
            bankloc = Bank.setupDynamicBank();

            if (bankloc == null) {
                Logger.debugLog("Could not find a dynamic bank location we are in, logging out and aborting script.");
                Logout.logout();
                Script.stop();
            } else {
                Logger.log("We're located at: " + bankloc + ".");
            }
        }
    }

    private void openDynamicBank() {
        Condition.sleep(generateDelay(1750, 2500));
        Bank.open(bankloc);
    }

    private void setupBanking() {
        // Set quantity
        setQuantity();

        // Select the correct bank tab if needed
        if (!Bank.isSelectedBankTab(banktab)) {
            Logger.log("Opening bank tab " + banktab);
            Bank.openTab(banktab);
            Condition.sleep(generateDelay(1200, 1800));
        }
    }

    private void setQuantity() {
        switch (activity) {
            case "Mixing":
                switch (product) {
                    case "Guthix rest tea":
                        Logger.debugLog("Setting quantity to 5.");
                        Paint.setStatus("Set bank withdraw 5");
                        if (!Bank.isSelectedQuantity5Button()) {
                            Bank.tapQuantity5Button();
                        }
                        break;
                    case "Super combat potion":
                        Logger.debugLog("Setting custom quantity: 7.");
                        Paint.setStatus("Set custom qty 7");
                        Bank.setCustomQuantity(7);
                        break;
                    case "Compost potion":
                    case "Divine super attack potion":
                    case "Divine super strength potion":
                    case "Divine super defence potion":
                    case "Divine ranging potion":
                    case "Divine magic potion":
                    case "Extended antifire":
                    case "Ancient brew":
                    case "Divine bastion potion":
                    case "Divine battlemage potion":
                    case "Anti-venom":
                    case "Forgotten brew":
                    case "Extended anti-venom+":
                    case "Divine super combat potion":
                    case "Extended super antifire (SAP)":
                        Logger.debugLog("Setting quantity to all.");
                        Paint.setStatus("Set bank withdraw all");
                        if (!Bank.isSelectedQuantityAllButton()) {
                            Bank.tapQuantityAllButton();
                        }
                        break;
                    default:
                        Logger.debugLog("Setting custom quantity: 14.");
                        Paint.setStatus("Set custom qty 14");
                        Bank.setCustomQuantity(14);
                }
                break;
            case "Herb Cleaning":
            case "Tar Creation":
                Logger.debugLog("Setting quantity to all.");
                Paint.setStatus("Set bank withdraw all");
                if (!Bank.isSelectedQuantityAllButton()) {
                    Bank.tapQuantityAllButton();
                }
                break;
            case "Barbarian Mixing":
            case "Unfinished Potion":
                Logger.debugLog("Setting custom quantity: 14.");
                Paint.setStatus("Set custom qty 14");
                Bank.setCustomQuantity(14);
                break;
            default:
                Logger.debugLog("Unknown activity (inside setQuantity): " + activity + " aborting script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(generateDelay(1200, 1800));
                }
                Logout.logout();
                Script.stop();
        }
    }

    private void withdrawTool(int itemId, String searchString, Color searchColor, String quantity) {
        Logger.debugLog("Withdrawing tool " + itemId + " from the bank.");

        // Set quantity based on the given quantity string
        if ("All".equals(quantity)) {
            Paint.setStatus("Set quantity all");
            Logger.debugLog("Set quantity all");
            // Select quantity all first
            if (!Bank.isSelectedQuantityAllButton()) {
                Bank.tapQuantityAllButton();
                Condition.wait(() -> Bank.isSelectedQuantityAllButton(), 200, 12);
            }
        } else if ("5".equals(quantity)) {
            Paint.setStatus("Set quantity 5");
            Logger.debugLog("Set quantity 5");
            // Select quantity 5 first
            if (!Bank.isSelectedQuantity5Button()) {
                Bank.tapQuantity5Button();
                Condition.wait(() -> Bank.isSelectedQuantity5Button(), 200, 12);
            }
        } else if ("10".equals(quantity)) {
            Paint.setStatus("Set quantity 10");
            Logger.debugLog("Set quantity 10");
            // Select quantity 10 first
            if (!Bank.isSelectedQuantity10Button()) {
                Bank.tapQuantity10Button();
                Condition.wait(() -> Bank.isSelectedQuantity10Button(), 200, 12);
            }
        } else {
            Paint.setStatus("Set quantity 1");
            Logger.debugLog("Set quantity 1");
            // Select quantity 1 first
            if (!Bank.isSelectedQuantity1Button()) {
                Bank.tapQuantity1Button();
                Condition.wait(() -> Bank.isSelectedQuantity1Button(), 200, 12);
            }
        }

        Paint.setStatus("Enter search mode");
        Logger.debugLog("Entering bank search mode");
        // Enter search mode
        Bank.tapSearchButton();
        Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 30);
        Condition.sleep(generateDelay(450, 600));

        // Type our search string
        Paint.setStatus("Type " + searchString);
        Logger.debugLog("Typing: " + searchString);
        for (char c : searchString.toCharArray()) {
            Client.sendKeystroke(String.valueOf(Character.toUpperCase(c)));
        }

        // Wait for a bit for results to be visible
        Condition.sleep(generateDelay(1300, 1700));

        Paint.setStatus("Withdraw tool");
        Logger.debugLog("Withdrawing tool");
        // Withdraw our item
        if (searchColor != null) {
            Bank.withdrawItem(itemId, 0.7, searchColor);
        } else {
            Bank.withdrawItem(itemId, 0.7);
        }
        Condition.wait(() -> Inventory.contains(itemId, 0.7), 100, 35);

        // Close searchbox again
        Paint.setStatus("Close search box");
        Logger.debugLog("Closing search box");
        Client.sendKeystroke("enter");
        Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 30);

        // Check if we actually have the tool
        if (!Inventory.contains(itemId, 0.70)) {
            Logger.log("No tool (" + itemId + ") found in inventory, stopping the script.");
            Bank.close();
            if (Bank.isOpen()) {
                Bank.close();
            }
            Logout.logout();
            Script.stop();
        }
    }

    private void closeBank() {
        // Close the bank
        Bank.close();
        Condition.sleep(generateDelay(1000, 1500));

        if (Bank.isOpen()) {
            Bank.close();
        }
    }

    public static void handleMES() {
        Rectangle img = null;
        if (Inventory.contains(ItemList.ROE_11324, 0.7)) {
            img = Inventory.findItem(ItemList.ROE_11324, 0.7, null);
        } else if (Inventory.contains(ItemList.CAVIAR_11326, 0.7)) {
            img = Inventory.findItem(ItemList.CAVIAR_11326, 0.7, null);
        }

        if (img != null) {
            Client.longPress(img);
            Condition.sleep(generateDelay( 800, 1200));
        }

        if (!isUseTopMost()) {
            if (enableMES()) {
                if (swapMESOptions()) {
                     mesSetupSucceeded = true;
                    tapCancelOption();
                }
            }
        } else {
            mesSetupSucceeded = true;
            tapCancelOption();
        }
    }

    private static void tapCancelOption() {
        Logger.debugLog("Attempting to locate and tap the cancel option...");

        // Locate the dream option
        Rectangle cancelOption = Objects.getBestMatch("/imgs/cancel.png", 0.8);

        if (cancelOption != null) {
            Logger.debugLog("Cancel option located: " + cancelOption);
            Client.tap(cancelOption);
            Logger.debugLog("Cancel option tapped successfully.");
        } else {
            Logger.debugLog("Cancel option was NOT FOUND.");
        }
    }

    private static boolean swapMESOptions() {
        Rectangle dropOption;
        Rectangle eatOption;
        Rectangle useOption;
        Rectangle mesEnabled;

        Logger.debugLog("Finding the Drop option.");
        dropOption = Objects.getBestMatch("/imgs/checkdrop.png", 0.8);

        Logger.debugLog("Finding the Eat option.");
        eatOption = Objects.getBestMatch("/imgs/checkeat.png", 0.8);

        Logger.debugLog("Finding the Use option.");
        useOption = Objects.getBestMatch("/imgs/checkuse.png", 0.8);

        // Log the results of finding the options
        if (dropOption != null) {
            Logger.debugLog("Drop option found: " + dropOption);
        } else {
            Logger.debugLog("Drop option was NOT FOUND.");
        }

        if (eatOption != null) {
            Logger.debugLog("Eat option found: " + eatOption);
        } else {
            Logger.debugLog("Eat option was NOT FOUND.");
        }

        if (useOption != null) {
            Logger.debugLog("Use option found: " + useOption);
        } else {
            Logger.debugLog("Use option was NOT FOUND.");
        }

        // Ensure the Use option is the topmost
        if (useOption != null) {
            Logger.debugLog("Ensuring the Use option is the topmost.");

            // Determine the current topmost option
            Rectangle topmostOption = useOption; // Start by assuming Guzzle is topmost

            if (eatOption != null && eatOption.y < topmostOption.y) {
                topmostOption = eatOption; // Update if Eat is higher on the screen
            }
            if (dropOption != null && dropOption.y < topmostOption.y) {
                topmostOption = dropOption; // Update if Drop is higher on the screen
            }

            // Swap options if Use is not the topmost
            if (topmostOption != useOption) {
                Logger.debugLog("Use option is not the topmost. Swapping with the current topmost option.");
                Logger.debugLog("Topmost option: " + topmostOption);
                Rectangle useOptionShort = new Rectangle(useOption.x, useOption.y, 15, useOption.height);
                Rectangle topmostOptionShort = new Rectangle(topmostOption.x, topmostOption.y, 15, topmostOption.height);
                Client.drag(useOptionShort, topmostOptionShort, 500);
                Condition.sleep(1500);
            } else {
                Logger.debugLog("Use option is already the topmost. No action needed.");
            }
        }

        // Check if the MES option menu is enabled and disable it if necessary
        mesEnabled = Objects.getBestMatch("/imgs/enabled-mes.png", 0.8);

        if (mesEnabled != null) {
            Logger.debugLog("Disabling MES option menu.");
            Client.tap(mesEnabled);
            Condition.sleep(generateDelay(500, 1000));
            return true;
        }

        return false;
    }

    public static boolean enableMES() {
        Rectangle mesEnabled;
        Rectangle mesDisabled;

        mesEnabled = Objects.getBestMatch("/imgs/enabled-mes.png", 0.8);

        if (mesEnabled != null) {
            Logger.debugLog("Found the MES enabled option, Menu Entry Swapper menu is enabled!");
            return true;
        } else {
            Logger.debugLog("Could not find the MES enabled option, trying to find the disabled option...");
            mesDisabled = Objects.getBestMatch("/imgs/disabled-mes.png", 0.8);
        }

        if (mesDisabled != null) {
            Logger.debugLog("Found the MES disabled option, enabling MES!");
            Client.tap(mesDisabled);
            Condition.sleep(1500);
            return true;
        } else {
            Logger.debugLog("Failed to find the MES disabled option!");
        }

        Logger.log("Both MES options could not be found, stopping script, please manually set up your menu entry swapper!");
        Script.stop();
        return false;
    }

    public static boolean isUseTopMost() {
        Logger.debugLog("Checking if the use option is the topmost...");

        // Find the options
        Rectangle eatOption = Objects.getBestMatch("/imgs/checkeat.png", 0.8);
        Rectangle dropOption = Objects.getBestMatch("/imgs/checkdrop.png", 0.8);
        Rectangle useOption = Objects.getBestMatch("/imgs/checkuse.png", 0.8);

        // Log the options found
        if (eatOption != null) {
            Logger.debugLog("Eat option found: " + eatOption);
        } else {
            Logger.debugLog("Eat option was NOT FOUND.");
        }

        if (dropOption != null) {
            Logger.debugLog("Drop option found: " + dropOption);
        } else {
            Logger.debugLog("Drop option was NOT FOUND.");
        }

        if (useOption != null) {
            Logger.debugLog("Use option found: " + useOption);
        } else {
            Logger.debugLog("Use option was NOT FOUND.");
            return false; // If useOption is not found, it cannot be the topmost
        }

        // Determine the topmost option
        Rectangle topmostOption = useOption; // Start assuming use is topmost
        if (eatOption != null && eatOption.y < topmostOption.y) {
            topmostOption = eatOption;
        }
        if (dropOption != null && dropOption.y < topmostOption.y) {
            topmostOption = dropOption;
        }

        // Check if useOption is the topmost
        boolean isTopMost = topmostOption == useOption;
        if (isTopMost) {
            Logger.debugLog("The use option is already the topmost.");
        } else {
            Logger.debugLog("The use option is NOT the topmost.");
        }

        return isTopMost;
    }
}