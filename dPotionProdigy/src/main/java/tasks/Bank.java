package tasks;

import helpers.annotations.AllowedValue;
import helpers.utils.ItemList;
import main.dPotionProdigy;
import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static helpers.Interfaces.Logger;
import static main.dPotionProdigy.*;

public class Bank extends Task {

    @Override
    public boolean activate() {
        return !readyToProcess();
    }

    @Override
    public boolean execute() {
        if (stopScript) {
            doneBanking = true;
            return true;
        }

        currentUsedSlots = 69;
        initialActiondone = false;

        Paint.setStatus("Bank");
        Logger.log("Banking.");

        if (retrycount >= 4) {
            Logger.log("Not all needed items in the inventory after 4 banking attempts. Assuming we ran out of supplies.");
            Logger.log("Logging out and stopping script!");
            if (Bank.isOpen()) {
                Bank.close();
            }

            Logout.logout();
            Script.stop();
        }

        // Open the bank (and this steps to the bank if needed)
        Bank.open(bankloc);
        Condition.wait(() -> Bank.isOpen(), 100, 25);
        Condition.sleep(generateDelay(300, 400));

        if (activity.equals("Unfinished Potion") || activity.equals("Barbarian Mixing") || activity.equals("Tar Creation")) {
            if (bankItem1Count <= 15  && bankItem1Count != -1) {
                Logger.debugLog("Bank item count is 15 or below, using non-cached bank withdraw method instead.");
                performBank(false);
            } else {
                performBank(true);
            }
        } else if (activity.equals("Mixing")) {
            switch (product) {
                case "Ancient brew":
                case "Divine super attack potion":
                case "Divine super strength potion":
                case "Divine super defence potion":
                case "Divine super combat potion":
                case "Divine bastion potion":
                case "Divine battlemage potion":
                case "Divine magic potion":
                case "Divine ranging potion":
                case "Extended antifire":
                case "Extended super antifire (SAP)":
                case "Anti-venom":
                case "Forgotten brew":
                case "Extended anti-venom+":
                    if (bankItem1Count <= 28  && bankItem1Count != -1) {
                        Logger.debugLog("Bank item count is 28 or below, using non-cached bank withdraw method instead.");
                        performBank(false);
                    } else {
                        performBank(true);
                    }
                    break;
                default:
                    if (bankItem1Count <= 15  && bankItem1Count != -1) {
                        Logger.debugLog("Bank item count is 15 or below, using non-cached bank withdraw method instead.");
                        performBank(false);
                    } else {
                        performBank(true);
                    }
            }
        } else if (activity.equals("Herb Cleaning")) {
            if (bankItem1Count <= 28  && bankItem1Count != -1) {
                Logger.debugLog("Bank item count is 28 or below, using non-cached bank withdraw method instead.");
                performBank(false);
            } else {
                performBank(true);
            }
        }

        return false;
    }

    private void performBank(boolean useCache) {
        if (Bank.isOpen()) {
            // Check if we are in the correct bank tab, else switch
            if (!Bank.isSelectedBankTab(banktab)) {
                Paint.setStatus("Change bank tab");
                Bank.openTab(banktab);
                Logger.log("Opened bank tab " + banktab);
                Condition.sleep(generateDelay(150, 300));
            }

            // Check if we have actual processed items
            if (Inventory.contains(targetItem, 0.75)) {
                // Count our processed items
                updateProcessedItems();
            } else {
                retrycount++;
            }

            // Deposit processed items
            depositItems();

            // Withdraw our new items
            Paint.setStatus("Withdraw bank items");
            withdrawItems(useCache);
            Condition.wait(dPotionProdigy::readyToProcess, 100,30);

            if (activity.equals("Unfinished Potion") || activity.equals("Barbarian Mixing") || activity.equals("Tar Creation")) {
                if (bankItem1Count <= 15  && bankItem1Count != -1) {
                    Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                    prepareScriptStop = true;
                }
            } else if (activity.equals("Mixing")) {
                switch (product) {
                    case "Ancient brew":
                    case "Divine super attack potion":
                    case "Divine super strength potion":
                    case "Divine super defence potion":
                    case "Divine super combat potion":
                    case "Divine bastion potion":
                    case "Divine battlemage potion":
                    case "Divine magic potion":
                    case "Divine ranging potion":
                    case "Extended antifire":
                    case "Extended super antifire (SAP)":
                    case "Anti-venom":
                    case "Forgotten brew":
                    case "Extended anti-venom+":
                        if (bankItem1Count <= 28  && bankItem1Count != -1) {
                            Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                            prepareScriptStop = true;
                        }
                        break;
                    default:
                        if (bankItem1Count <= 15  && bankItem1Count != -1) {
                            Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                            prepareScriptStop = true;
                        }
                }
            } else if (activity.equals("Herb Cleaning")) {
                if (bankItem1Count <= 28  && bankItem1Count != -1) {
                    Logger.log("We're (almost) out of supplies, marking script to stop after this iteration!");
                    prepareScriptStop = true;
                }
            }

            // Close the bank
            Paint.setStatus("Close bank");
            closeBank();

        } else {
            Logger.debugLog("Bank not open while in bank logic, skipping!");
        }
    }

    private void depositItems() {
        switch (product) {
            case "Ancient brew":
            case "Divine super attack potion":
            case "Divine super strength potion":
            case "Divine super defence potion":
            case "Divine super combat potion":
            case "Divine bastion potion":
            case "Divine battlemage potion":
            case "Divine magic potion":
            case "Divine ranging potion":
            case "Extended antifire":
            case "Extended super antifire (SAP)":
            case "Anti-venom":
            case "Forgotten brew":
            case "Extended anti-venom+":
            case "Guam tar":
            case "Marrentill tar":
            case "Tarromin tar":
            case "Harralander tar":
            case "Irit tar":
                Inventory.tapItem(targetItem, 0.7);
                break;
            default:
                Bank.tapDepositInventoryButton();
        }
        Condition.sleep(generateDelay(150, 300));
    }

    private void withdrawItems(boolean useCache) {
        // Withdraw first item and update our current stacksize
        usingCache = useCache;
        updatePreviousBankItemCount(1);
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
                withdrawHelper(ItemList.GRIMY_MARRENTILL_201, 0.7, "#076c0a", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Tarromin":
                withdrawHelper(ItemList.GRIMY_TARROMIN_203, 0.7, "#076c2f", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Harralander":
                withdrawHelper(ItemList.GRIMY_HARRALANDER_205, 0.7, "#456807", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Ranarr weed":
                withdrawHelper(ItemList.GRIMY_RANARR_WEED_207, 0.7, "#335904", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Toadflax":
                withdrawHelper(ItemList.GRIMY_TOADFLAX_3049, 0.7, "#002100", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Irit leaf":
                withdrawHelper(ItemList.GRIMY_IRIT_LEAF_209, 0.7, "#47702c", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Avantoe":
                withdrawHelper(ItemList.GRIMY_AVANTOE_211, 0.7, "#0c4c1e", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
                break;
            case "Kwuarm":
                withdrawHelper(ItemList.GRIMY_KWUARM_213, 0.7, "#455004", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
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
                Logger.log("Unknown product in withdrawItems (bank): " + product + " stopping script.");
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
        withdrawItem(item1Id, item1Threshold, item1Color);
        withdrawItem(item2Id, item2Threshold, item2Color);
        withdrawItem(item3Id, item3Threshold, item3Color);
        withdrawItem(item4Id, item4Threshold, item4Color);
        withdrawItem(item5Id, item5Threshold, item5Color);
    }

    private void withdrawItem(int itemId, double threshold, String color) {
        if (itemId != -1 && itemId != 21622 && itemId != 26368 && itemId != 23964 && itemId != 11994 && itemId != 12934 && itemId != 27616 && itemId != 29784 && itemId != 1939) {
            if (color != null) {
                Bank.withdrawItem(itemId, usingCache, threshold, Color.decode(color));
                Condition.sleep(generateDelay(150, 300));
                bankCountHelper(Bank.stackSize(itemId, Color.decode(color)));
            } else {
                Bank.withdrawItem(itemId, usingCache, threshold);
                Condition.sleep(generateDelay(150, 300));
                bankCountHelper(Bank.stackSize(itemId));
            }
            printItemStateDebug(1);
        }
    }

    private void bankCountHelper(int count) {
        if (count != -1) {
            if (count < bankItem1Count) {
                bankItem1Count = count;
            }
        }
    }

    private void closeBank() {
        Bank.close();
        Condition.wait(() -> !Bank.isOpen(), 100, 30);
        Condition.sleep(generateDelay(400, 600));

        if (Bank.isOpen()) {
            Bank.close();
            Condition.wait(() -> !Bank.isOpen(), 100, 30);
            Condition.sleep(generateDelay(400, 600));
        }
    }

    private void updateProcessedItems() {
        if (activity.equals("Tar Creation")) {
            // Update our process count
            PROCESS_COUNT = PROCESS_COUNT + Inventory.stackSize(targetItem);
        } else {
            // Update our process count
            PROCESS_COUNT = PROCESS_COUNT + Inventory.count(targetItem, 0.75);
        }

        // Update the paint bar with this new count
        updatePaintBar(PROCESS_COUNT);
    }

    private void printItemStateDebug(int itemNr) {
        if (itemNr == 1) {
            Logger.debugLog("Source item previously left in bank: " + previousBankItem1Count);
            Logger.debugLog("Source item currently left in bank: " + bankItem1Count);
        }
    }

    private void updatePreviousBankItemCount(int updateItem) {
        if (updateItem == 1) {
            if (previousBankItem1Count != -1 & bankItem1Count > 0) {
                previousBankItem1Count = bankItem1Count;
            }
        }
    }
}