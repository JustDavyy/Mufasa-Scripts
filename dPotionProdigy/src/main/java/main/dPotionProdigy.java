package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.AntiBan;
import helpers.utils.ItemList;
import helpers.utils.OptionType;
import helpers.utils.UITabs;
import tasks.Bank;
import tasks.Process;
import tasks.Setup;
import utils.Task;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dPotionProdigy",
        description = "Bank stander script for the Herblore skill, uses dynamic banking to support a variety of banks around Gielinor. Current supported operations: Cleaning herbs, making tars and mixing unfinished, finished and barbarian potions.",
        version = "1.03",
        guideLink = "https://wiki.mufasaclient.com/docs/dpotionprodigy/",
        categories = {ScriptCategory.Herblore, ScriptCategory.Moneymaking}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What product would you like to make?",
                        defaultValue = "Lantern lens",
                        allowedValues = {
                                @AllowedValue(optionName = "FULL MIX OPTIONS"),
                                @AllowedValue(optionIcon = "121", optionName = "Attack potion"),
                                @AllowedValue(optionIcon = "175", optionName = "Antipoison"),
                                @AllowedValue(optionIcon = "115", optionName = "Strength potion"),
                                @AllowedValue(optionIcon = "3410", optionName = "Serum 207"),
                                @AllowedValue(optionIcon = "4417", optionName = "Guthix rest tea"),
                                @AllowedValue(optionIcon = "6472", optionName = "Compost potion"),
                                @AllowedValue(optionIcon = "127", optionName = "Restore potion"),
                                @AllowedValue(optionIcon = "3010", optionName = "Energy potion"),
                                @AllowedValue(optionIcon = "133", optionName = "Defence potion"),
                                @AllowedValue(optionIcon = "3034", optionName = "Agility potion"),
                                @AllowedValue(optionIcon = "23688", optionName = "Combat potion"),
                                @AllowedValue(optionIcon = "139", optionName = "Prayer potion"),
                                @AllowedValue(optionIcon = "145", optionName = "Super attack"),
                                @AllowedValue(optionIcon = "181", optionName = "Superantipoison"),
                                @AllowedValue(optionIcon = "151", optionName = "Fishing potion"),
                                @AllowedValue(optionIcon = "3018", optionName = "Super energy"),
                                @AllowedValue(optionIcon = "10000", optionName = "Hunter potion"),
                                @AllowedValue(optionIcon = "30140", optionName = "Goading potion"),
                                @AllowedValue(optionIcon = "157", optionName = "Super strength"),
                                @AllowedValue(optionIcon = "30128", optionName = "Prayer regeneration potion"),
                                @AllowedValue(optionIcon = "187", optionName = "Weapon poison"),
                                @AllowedValue(optionIcon = "24601", optionName = "Super restore"),
                                @AllowedValue(optionIcon = "163", optionName = "Super defence"),
                                @AllowedValue(optionIcon = "5945", optionName = "Antidote+"),
                                @AllowedValue(optionIcon = "2454", optionName = "Antifire potion"),
                                @AllowedValue(optionIcon = "23700", optionName = "Divine super attack potion"),
                                @AllowedValue(optionIcon = "23712", optionName = "Divine super strength potion"),
                                @AllowedValue(optionIcon = "23724", optionName = "Divine super defence potion"),
                                @AllowedValue(optionIcon = "23736", optionName = "Ranging potion"),
                                @AllowedValue(optionIcon = "5937", optionName = "Weapon poison+"),
                                @AllowedValue(optionIcon = "23736", optionName = "Divine ranging potion"),
                                @AllowedValue(optionIcon = "3042", optionName = "Magic potion"),
                                @AllowedValue(optionIcon = "12625", optionName = "Stamina potion"),
                                @AllowedValue(optionIcon = "189", optionName = "Zamorak brew"),
                                @AllowedValue(optionIcon = "23748", optionName = "Divine magic potion"),
                                @AllowedValue(optionIcon = "5952", optionName = "Antidote++"),
                                @AllowedValue(optionIcon = "22464", optionName = "Bastion potion"),
                                @AllowedValue(optionIcon = "22452", optionName = "Battlemage potion"),
                                @AllowedValue(optionIcon = "6687", optionName = "Saradomin brew"),
                                @AllowedValue(optionIcon = "5940", optionName = "Weapon poison++"),
                                @AllowedValue(optionIcon = "11953", optionName = "Extended antifire"),
                                @AllowedValue(optionIcon = "26342", optionName = "Ancient brew"),
                                @AllowedValue(optionIcon = "24638", optionName = "Divine bastion potion"),
                                @AllowedValue(optionIcon = "24626", optionName = "Divine battlemage potion"),
                                @AllowedValue(optionIcon = "12905", optionName = "Anti-venom"),
                                @AllowedValue(optionIcon = "27205", optionName = "Menaphite remedy"),
                                @AllowedValue(optionIcon = "23688", optionName = "Super combat potion"),
                                @AllowedValue(optionIcon = "27632", optionName = "Forgotten brew"),
                                @AllowedValue(optionIcon = "21981", optionName = "Super antifire potion"),
                                @AllowedValue(optionIcon = "12913", optionName = "Anti-venom+"),
                                @AllowedValue(optionIcon = "29824", optionName = "Extended anti-venom+"),
                                @AllowedValue(optionIcon = "23688", optionName = "Divine super combat potion"),
                                @AllowedValue(optionIcon = "22212", optionName = "Extended super antifire (SAP)"),
                                @AllowedValue(optionIcon = "22212", optionName = "Extended super antifire (EAP)"),
                                @AllowedValue(optionName = "BARBARIAN MIX OPTIONS"),
                                @AllowedValue(optionIcon = "11429", optionName = "Attack mix"),
                                @AllowedValue(optionIcon = "11433", optionName = "Antipoison mix"),
                                @AllowedValue(optionIcon = "11443", optionName = "Strength mix"),
                                @AllowedValue(optionIcon = "11449", optionName = "Restore mix"),
                                @AllowedValue(optionIcon = "11453", optionName = "Energy mix"),
                                @AllowedValue(optionIcon = "11457", optionName = "Defence mix"),
                                @AllowedValue(optionIcon = "11461", optionName = "Agility mix"),
                                @AllowedValue(optionIcon = "11445", optionName = "Combat mix"),
                                @AllowedValue(optionIcon = "11465", optionName = "Prayer mix"),
                                @AllowedValue(optionIcon = "11469", optionName = "Superattack mix"),
                                @AllowedValue(optionIcon = "11473", optionName = "Anti-poison supermix"),
                                @AllowedValue(optionIcon = "11477", optionName = "Fishing mix"),
                                @AllowedValue(optionIcon = "11481", optionName = "Super energy mix"),
                                @AllowedValue(optionIcon = "11517", optionName = "Hunting mix"),
                                @AllowedValue(optionIcon = "11485", optionName = "Super str. mix"),
                                @AllowedValue(optionIcon = "11493", optionName = "Super restore mix"),
                                @AllowedValue(optionIcon = "11497", optionName = "Super def. mix"),
                                @AllowedValue(optionIcon = "11501", optionName = "Antidote+ mix"),
                                @AllowedValue(optionIcon = "11505", optionName = "Antifire mix"),
                                @AllowedValue(optionIcon = "11509", optionName = "Ranging mix"),
                                @AllowedValue(optionIcon = "11513", optionName = "Magic mix"),
                                @AllowedValue(optionIcon = "11521", optionName = "Zamorak mix"),
                                @AllowedValue(optionIcon = "12633", optionName = "Stamina mix"),
                                @AllowedValue(optionIcon = "11960", optionName = "Extended antifire mix"),
                                @AllowedValue(optionIcon = "26350", optionName = "Ancient mix"),
                                @AllowedValue(optionIcon = "21994", optionName = "Super antifire mix"),
                                @AllowedValue(optionIcon = "22221", optionName = "Extended super antifire mix"),
                                @AllowedValue(optionName = "TAR CREATION OPTIONS"),
                                @AllowedValue(optionIcon = "10142", optionName = "Guam tar"),
                                @AllowedValue(optionIcon = "10143", optionName = "Marrentill tar"),
                                @AllowedValue(optionIcon = "10144", optionName = "Tarromin tar"),
                                @AllowedValue(optionIcon = "10145", optionName = "Harralander tar"),
                                @AllowedValue(optionIcon = "28837", optionName = "Irit tar"),
                                @AllowedValue(optionName = "MIXING UNFINISHED POTION OPTIONS"),
                                @AllowedValue(optionIcon = "91", optionName = "Guam potion (unf)"),
                                @AllowedValue(optionIcon = "93", optionName = "Marrentill potion (unf)"),
                                @AllowedValue(optionIcon = "95", optionName = "Tarromin potion (unf)"),
                                @AllowedValue(optionIcon = "97", optionName = "Harralander potion (unf)"),
                                @AllowedValue(optionIcon = "99", optionName = "Ranarr potion (unf)"),
                                @AllowedValue(optionIcon = "3002", optionName = "Toadflax potion (unf)"),
                                @AllowedValue(optionIcon = "101", optionName = "Irit potion (unf)"),
                                @AllowedValue(optionIcon = "103", optionName = "Avantoe potion (unf)"),
                                @AllowedValue(optionIcon = "105", optionName = "Kwuarm potion (unf)"),
                                @AllowedValue(optionIcon = "30100", optionName = "Huasca potion (unf)"),
                                @AllowedValue(optionIcon = "3004", optionName = "Snapdragon potion (unf)"),
                                @AllowedValue(optionIcon = "107", optionName = "Cadantine potion (unf)"),
                                @AllowedValue(optionIcon = "2483", optionName = "Lantadyme potion (unf)"),
                                @AllowedValue(optionIcon = "109", optionName = "Dwarf weed potion (unf)"),
                                @AllowedValue(optionIcon = "111", optionName = "Torstol potion (unf)"),
                                @AllowedValue(optionName = "CLEANING HERB OPTIONS"),
                                @AllowedValue(optionIcon = "249", optionName = "Guam leaf"),
                                @AllowedValue(optionIcon = "251", optionName = "Marrentill"),
                                @AllowedValue(optionIcon = "253", optionName = "Tarromin"),
                                @AllowedValue(optionIcon = "255", optionName = "Harralander"),
                                @AllowedValue(optionIcon = "257", optionName = "Ranarr weed"),
                                @AllowedValue(optionIcon = "2998", optionName = "Toadflax"),
                                @AllowedValue(optionIcon = "259", optionName = "Irit leaf"),
                                @AllowedValue(optionIcon = "261", optionName = "Avantoe"),
                                @AllowedValue(optionIcon = "263", optionName = "Kwuarm"),
                                @AllowedValue(optionIcon = "30097", optionName = "Huasca"),
                                @AllowedValue(optionIcon = "3000", optionName = "Snapdragon"),
                                @AllowedValue(optionIcon = "265", optionName = "Cadantine"),
                                @AllowedValue(optionIcon = "2481", optionName = "Lantadyme"),
                                @AllowedValue(optionIcon = "267", optionName = "Dwarf weed"),
                                @AllowedValue(optionIcon = "269", optionName = "Torstol")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Bank Tab",
                        description = "What bank tab are your resources located in?",
                        defaultValue = "0",
                        optionType = OptionType.BANKTABS
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name = "Run anti-ban",
                        description = "Would you like to run anti-ban features?",
                        defaultValue = "true",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name = "Run extended anti-ban",
                        description = "Would you like to run additional, extended anti-ban like some additional AFK patterns, on TOP of the regular anti-ban?",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class dPotionProdigy extends AbstractScript {
    public static String product;
    static String hopProfile;
    static Boolean hopEnabled;
    static Boolean useWDH;
    public static boolean setupDone = false;
    public static String bankloc;
    public static int banktab;
    public static final Random random = new Random();
    public static int targetItem;
    public static int sourceItem;
    public static String activity;
    public static boolean usingCache = false;
    private boolean antiBan;
    private boolean extendedAntiBan;

    // Process stuff we need to re-initiate actions
    public static long lastProcessTime = System.currentTimeMillis();
    public static int currentUsedSlots = 0;
    public static boolean initialActiondone = false;
    public static int lastUsedSlots = 0;

    // Banking stuff we need to prevent releasing placeholders
    public static boolean prepareScriptStop = false;
    public static boolean stopScript = false;
    public static boolean doneBanking = false;
    public static int retrycount = 0;
    public static int bankItem1Count = 0;
    public static int previousBankItem1Count = 0;
    public static int tempBankCountHolder = 0;

    // PaintBar stuff we need
    private static long currentTime = System.currentTimeMillis();
    private static long startTime;
    private static double elapsedTimeInHours;
    private static double itemsPerHour;
    public static int productIndex;
    public static int PROCESS_COUNT = 0;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        antiBan = Boolean.valueOf(configs.get("Run anti-ban"));
        extendedAntiBan = Boolean.valueOf(configs.get("Run extended anti-ban"));

        Logger.log("Thank you for using the dPotionProdigy script!\nSetting up everything for your gains now...");

        // Initialize what we need to before proceeding
        initializeOptions();

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create a single image box, to show the amount of processed items
        productIndex = Paint.createBox(product, targetItem, PROCESS_COUNT);

        // Set the top header(s) of paintUI.
        Paint.setStatus("Initializing...");
        Paint.setStatistic("Statistics will update soon.");

        if (antiBan) {
            Logger.debugLog("Initializing anti-ban timer");
            Game.antiBan();
            if (extendedAntiBan) {
                Logger.debugLog("Initializing extended anti-ban timer");
                Game.enableOptionalAntiBan(AntiBan.EXTENDED_AFK);
                Game.antiBan();
            }
        }

        // One-time setup
        hopActions();

        startTime = System.currentTimeMillis();
    }

    // This is the main part of the script, poll gets looped constantly
    List<Task> processTasks = Arrays.asList(
            new Setup(),
            new Process(),
            new Bank()
    );

    @Override
    public void poll() {

        if (antiBan) {
            Game.antiBan();
        }

        if (stopScript & doneBanking) {
            Logger.log("Script has been marked to stop by script dev marked situation, stopping!");
            if (Bank.isOpen()) {
                Bank.close();
                Condition.sleep(generateDelay(700, 1000));
            }
            Logout.logout();
            Script.stop();
        }

        if (prepareScriptStop) {
            Logger.debugLog("Script has been marked to stop by script dev set situation after this iteration!");
            Paint.setStatus("Stop after iteration");
            Condition.sleep(2000);
            stopScript = true;
        }

        // Check if it's time to hop
        hopActions();

        // Read XP
        readXP();

        // Open inventory tab
        GameTabs.openTab(UITabs.INVENTORY);

        //Run tasks
        for (Task task : processTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }


    public static void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        }
    }


    public static int generateDelay(int lowerEnd, int higherEnd) {
        if (lowerEnd > higherEnd) {
            int temp = lowerEnd;
            lowerEnd = higherEnd;
            higherEnd = temp;
        }
        return random.nextInt(higherEnd - lowerEnd + 1) + lowerEnd;
    }

    private void readXP() {
        XpBar.getXP();
    }

    public static void updatePaintBar(int count) {
        Paint.setStatus("Update paint count");
        Paint.updateBox(productIndex, count);

        // Time calculations
        currentTime = System.currentTimeMillis();
        elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);
        itemsPerHour = PROCESS_COUNT / elapsedTimeInHours;

        // Format items per hour with dot as thousand separator and no decimals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(','); // Set the decimal separator to comma
        DecimalFormat formatItems = new DecimalFormat("#,###", symbols);
        String itemsPerHourFormatted = formatItems.format(itemsPerHour);

        // Update the statistics label
        String statistics = String.format("%s/hr: %s", product, itemsPerHourFormatted);
        Paint.setStatistic(statistics);
    }

    private void initializeOptions() {
        switch (product) {
            // Mixing Options
            case "Attack potion":
                sourceItem = ItemList.GUAM_POTION_UNF_91;
                targetItem = ItemList.ATTACK_POTION_3_121;
                activity = "Mixing";
                break;
            case "Antipoison":
                sourceItem = ItemList.MARRENTILL_POTION_UNF_93;
                targetItem = ItemList.ANTIPOISON_3_175;
                activity = "Mixing";
                break;
            case "Strength potion":
                sourceItem = ItemList.TARROMIN_POTION_UNF_95;
                targetItem = ItemList.STRENGTH_POTION_3_115;
                activity = "Mixing";
                break;
            case "Serum 207":
                sourceItem = ItemList.TARROMIN_POTION_UNF_95;
                targetItem = ItemList.SERUM_207_3_3410;
                activity = "Mixing";
                break;
            case "Guthix rest tea":
                sourceItem = ItemList.CUP_OF_HOT_WATER_4460;
                targetItem = ItemList.GUTHIX_REST_3_4419;
                activity = "Mixing";
                break;
            case "Compost potion":
                sourceItem = ItemList.HARRALANDER_POTION_UNF_97;
                targetItem = ItemList.COMPOST_POTION_3_6472;
                activity = "Mixing";
                break;
            case "Restore potion":
                sourceItem = ItemList.HARRALANDER_POTION_UNF_97;
                targetItem = ItemList.RESTORE_POTION_3_127;
                activity = "Mixing";
                break;
            case "Energy potion":
                sourceItem = ItemList.HARRALANDER_POTION_UNF_97;
                targetItem = ItemList.ENERGY_POTION_3_3010;
                activity = "Mixing";
                break;
            case "Defence potion":
                sourceItem = ItemList.RANARR_POTION_UNF_99;
                targetItem = ItemList.DEFENCE_POTION_3_133;
                activity = "Mixing";
                break;
            case "Agility potion":
                sourceItem = ItemList.TOADFLAX_POTION_UNF_3002;
                targetItem = ItemList.AGILITY_POTION_3_3034;
                activity = "Mixing";
                break;
            case "Combat potion":
                sourceItem = ItemList.HARRALANDER_POTION_UNF_97;
                targetItem = ItemList.COMBAT_POTION_3_9741;
                activity = "Mixing";
                break;
            case "Prayer potion":
                sourceItem = ItemList.RANARR_POTION_UNF_99;
                targetItem = ItemList.PRAYER_POTION_3_139;
                activity = "Mixing";
                break;
            case "Super attack":
                sourceItem = ItemList.IRIT_POTION_UNF_101;
                targetItem = ItemList.SUPER_ATTACK_3_145;
                activity = "Mixing";
                break;
            case "Superantipoison":
                sourceItem = ItemList.IRIT_POTION_UNF_101;
                targetItem = ItemList.SUPERANTIPOISON_3_181;
                activity = "Mixing";
                break;
            case "Fishing potion":
                sourceItem = ItemList.AVANTOE_POTION_UNF_103;
                targetItem = ItemList.FISHING_POTION_3_151;
                activity = "Mixing";
                break;
            case "Super energy":
                sourceItem = ItemList.AVANTOE_POTION_UNF_103;
                targetItem = ItemList.SUPER_ENERGY_3_3018;
                activity = "Mixing";
                break;
            case "Hunter potion":
                sourceItem = ItemList.AVANTOE_POTION_UNF_103;
                targetItem = ItemList.HUNTER_POTION_3_10000;
                activity = "Mixing";
                break;
            case "Goading potion":
                sourceItem = ItemList.HARRALANDER_POTION_UNF_97;
                targetItem = ItemList.GOADING_POTION_3_30140;
                activity = "Mixing";
                break;
            case "Super strength":
                sourceItem = ItemList.KWUARM_POTION_UNF_105;
                targetItem = ItemList.SUPER_STRENGTH_3_157;
                activity = "Mixing";
                break;
            case "Prayer regeneration potion":
                sourceItem = ItemList.HUASCA_POTION_UNF_30100;
                targetItem = ItemList.PRAYER_REGENERATION_POTION_3_30128;
                activity = "Mixing";
                break;
            case "Weapon poison":
                sourceItem = ItemList.KWUARM_POTION_UNF_105;
                targetItem = ItemList.WEAPON_POISON_187;
                activity = "Mixing";
                break;
            case "Super restore":
                sourceItem = ItemList.SNAPDRAGON_POTION_UNF_3004;
                targetItem = ItemList.SUPER_RESTORE_3_3026;
                activity = "Mixing";
                break;
            case "Super defence":
                sourceItem = ItemList.CADANTINE_POTION_UNF_107;
                targetItem = ItemList.SUPER_DEFENCE_3_163;
                activity = "Mixing";
                break;
            case "Antidote+":
                sourceItem = ItemList.ANTIDOTE_UNF_5942;
                targetItem = ItemList.ANTIDOTE_3_5945;
                activity = "Mixing";
                break;
            case "Antifire potion":
                sourceItem = ItemList.LANTADYME_POTION_UNF_2483;
                targetItem = ItemList.ANTIFIRE_POTION_3_2454;
                activity = "Mixing";
                break;
            case "Divine super attack potion":
                sourceItem = ItemList.SUPER_ATTACK_4_2436;
                targetItem = ItemList.DIVINE_SUPER_ATTACK_POTION_4_23697;
                activity = "Mixing";
                break;
            case "Divine super strength potion":
                sourceItem = ItemList.SUPER_STRENGTH_4_2440;
                targetItem = ItemList.DIVINE_SUPER_STRENGTH_POTION_4_23709;
                activity = "Mixing";
                break;
            case "Divine super defence potion":
                sourceItem = ItemList.SUPER_DEFENCE_4_2442;
                targetItem = ItemList.DIVINE_SUPER_DEFENCE_POTION_4_23721;
                activity = "Mixing";
                break;
            case "Ranging potion":
                sourceItem = ItemList.DWARF_WEED_POTION_UNF_109;
                targetItem = ItemList.RANGING_POTION_3_169;
                activity = "Mixing";
                break;
            case "Weapon poison+":
                sourceItem = ItemList.WEAPON_POISON_UNF_5936;
                targetItem = ItemList.WEAPON_POISON_5937;
                activity = "Mixing";
                break;
            case "Divine ranging potion":
                sourceItem = ItemList.RANGING_POTION_4_2444;
                targetItem = ItemList.DIVINE_RANGING_POTION_4_23733;
                activity = "Mixing";
                break;
            case "Magic potion":
                sourceItem = ItemList.LANTADYME_POTION_UNF_2483;
                targetItem = ItemList.MAGIC_POTION_3_3042;
                activity = "Mixing";
                break;
            case "Stamina potion":
                sourceItem = ItemList.SUPER_ENERGY_4_3016;
                targetItem = ItemList.STAMINA_POTION_4_12625;
                activity = "Mixing";
                break;
            case "Zamorak brew":
                sourceItem = ItemList.TORSTOL_POTION_UNF_111;
                targetItem = ItemList.ZAMORAK_BREW_3_189;
                activity = "Mixing";
                break;
            case "Divine magic potion":
                sourceItem = ItemList.MAGIC_POTION_4_3040;
                targetItem = ItemList.DIVINE_MAGIC_POTION_4_23745;
                activity = "Mixing";
                break;
            case "Antidote++":
                sourceItem = ItemList.ANTIDOTE_UNF_5951;
                targetItem = ItemList.ANTIDOTE_4_5952;
                activity = "Mixing";
                break;
            case "Bastion potion":
                sourceItem = ItemList.CADANTINE_BLOOD_POTION_UNF_22443;
                targetItem = ItemList.BASTION_POTION_3_22464;
                activity = "Mixing";
                break;
            case "Battlemage potion":
                sourceItem = ItemList.CADANTINE_BLOOD_POTION_UNF_22443;
                targetItem = ItemList.BATTLEMAGE_POTION_3_22452;
                activity = "Mixing";
                break;
            case "Saradomin brew":
                sourceItem = ItemList.TOADFLAX_POTION_UNF_3002;
                targetItem = ItemList.SARADOMIN_BREW_3_6687;
                activity = "Mixing";
                break;
            case "Weapon poison++":
                sourceItem = ItemList.WEAPON_POISON_UNF_5939;
                targetItem = ItemList.WEAPON_POISON_5940;
                activity = "Mixing";
                break;
            case "Extended antifire":
                sourceItem = ItemList.ANTIFIRE_POTION_4_2452;
                targetItem = ItemList.EXTENDED_ANTIFIRE_4_11951;
                activity = "Mixing";
                break;
            case "Ancient brew":
                sourceItem = ItemList.DWARF_WEED_POTION_UNF_109;
                targetItem = ItemList.ANCIENT_BREW_3_26342;
                activity = "Mixing";
                break;
            case "Divine bastion potion":
                sourceItem = ItemList.BASTION_POTION_4_22461;
                targetItem = ItemList.DIVINE_BASTION_POTION_4_24635;
                activity = "Mixing";
                break;
            case "Divine battlemage potion":
                sourceItem = ItemList.BATTLEMAGE_POTION_4_22449;
                targetItem = ItemList.DIVINE_BATTLEMAGE_POTION_4_24623;
                activity = "Mixing";
                break;
            case "Anti-venom":
                sourceItem = ItemList.ANTIDOTE_4_5952;
                targetItem = ItemList.ANTI_VENOM_4_12905;
                activity = "Mixing";
                break;
            case "Menaphite remedy":
                sourceItem = ItemList.DWARF_WEED_POTION_UNF_109;
                targetItem = ItemList.MENAPHITE_REMEDY_3_27205;
                activity = "Mixing";
                break;
            case "Super combat potion":
                sourceItem = ItemList.TORSTOL_POTION_UNF_111;
                targetItem = ItemList.SUPER_COMBAT_POTION_4_12695;
                activity = "Mixing";
                break;
            case "Forgotten brew":
                sourceItem = ItemList.ANCIENT_BREW_4_26340;
                targetItem = ItemList.FORGOTTEN_BREW_4_27629;
                activity = "Mixing";
                break;
            case "Super antifire potion":
                sourceItem = ItemList.ANTIFIRE_POTION_4_2452;
                targetItem = ItemList.SUPER_ANTIFIRE_POTION_4_21978;
                activity = "Mixing";
                break;
            case "Anti-venom+":
                sourceItem = ItemList.ANTI_VENOM_4_12905;
                targetItem = ItemList.ANTI_VENOM_4_12913;
                activity = "Mixing";
                break;
            case "Extended anti-venom+":
                sourceItem = ItemList.ANTI_VENOM_4_12913;
                targetItem = ItemList.EXTENDED_ANTI_VENOM_4_29824;
                activity = "Mixing";
                break;
            case "Divine super combat potion":
                sourceItem = ItemList.SUPER_COMBAT_POTION_4_12695;
                targetItem = ItemList.DIVINE_SUPER_COMBAT_POTION_4_23685;
                activity = "Mixing";
                break;
            case "Extended super antifire (SAP)":
                sourceItem = ItemList.SUPER_ANTIFIRE_POTION_4_21978;
                targetItem = ItemList.EXTENDED_SUPER_ANTIFIRE_4_22209;
                activity = "Mixing";
                break;
            case "Extended super antifire (EAP)":
                sourceItem = ItemList.EXTENDED_ANTIFIRE_4_11951;
                targetItem = ItemList.EXTENDED_SUPER_ANTIFIRE_4_22209;
                activity = "Mixing";
                break;

            // Barbarian Mix Options
            case "Attack mix":
                sourceItem = ItemList.SUPER_ATTACK_2_147;
                targetItem = ItemList.ATTACK_MIX_2_11429;
                activity = "Barbarian Mixing";
                break;
            case "Antipoison mix":
                sourceItem = ItemList.ANTIPOISON_2_177;
                targetItem = ItemList.ANTIPOISON_MIX_2_11433;
                activity = "Barbarian Mixing";
                break;
            case "Strength mix":
                sourceItem = ItemList.STRENGTH_POTION_2_117;
                targetItem = ItemList.STRENGTH_MIX_2_11443;
                activity = "Barbarian Mixing";
                break;
            case "Restore mix":
                sourceItem = ItemList.RESTORE_POTION_2_129;
                targetItem = ItemList.RESTORE_MIX_2_11449;
                activity = "Barbarian Mixing";
                break;
            case "Energy mix":
                sourceItem = ItemList.ENERGY_POTION_2_3012;
                targetItem = ItemList.ENERGY_MIX_2_11453;
                activity = "Barbarian Mixing";
                break;
            case "Defence mix":
                sourceItem = ItemList.DEFENCE_POTION_2_135;
                targetItem = ItemList.DEFENCE_MIX_2_11457;
                activity = "Barbarian Mixing";
                break;
            case "Agility mix":
                sourceItem = ItemList.AGILITY_POTION_2_3036;
                targetItem = ItemList.AGILITY_MIX_2_11461;
                activity = "Barbarian Mixing";
                break;
            case "Combat mix":
                sourceItem = ItemList.COMBAT_POTION_2_9743;
                targetItem = ItemList.COMBAT_MIX_2_11445;
                activity = "Barbarian Mixing";
                break;
            case "Prayer mix":
                sourceItem = ItemList.PRAYER_POTION_2_141;
                targetItem = ItemList.PRAYER_MIX_2_11465;
                activity = "Barbarian Mixing";
                break;
            case "Superattack mix":
                sourceItem = ItemList.SUPER_ATTACK_2_147;
                targetItem = ItemList.SUPERATTACK_MIX_2_11469;
                activity = "Barbarian Mixing";
                break;
            case "Anti-poison supermix":
                sourceItem = ItemList.SUPERANTIPOISON_2_183;
                targetItem = ItemList.ANTI_POISON_SUPERMIX_2_11473;
                activity = "Barbarian Mixing";
                break;
            case "Fishing mix":
                sourceItem = ItemList.FISHING_POTION_2_153;
                targetItem = ItemList.FISHING_MIX_2_11477;
                activity = "Barbarian Mixing";
                break;
            case "Super energy mix":
                sourceItem = ItemList.SUPER_ENERGY_2_3020;
                targetItem = ItemList.SUPER_ENERGY_MIX_2_11481;
                activity = "Barbarian Mixing";
                break;
            case "Hunting mix":
                sourceItem = ItemList.HUNTER_POTION_2_10002;
                targetItem = ItemList.HUNTING_MIX_2_11517;
                activity = "Barbarian Mixing";
                break;
            case "Super str. mix":
                sourceItem = ItemList.SUPER_STRENGTH_2_159;
                targetItem = ItemList.SUPER_STR_MIX_2_11485;
                activity = "Barbarian Mixing";
                break;
            case "Super restore mix":
                sourceItem = ItemList.SUPER_RESTORE_2_3028;
                targetItem = ItemList.SUPER_RESTORE_MIX_2_11493;
                activity = "Barbarian Mixing";
                break;
            case "Super def. mix":
                sourceItem = ItemList.SUPER_DEFENCE_2_165;
                targetItem = ItemList.SUPER_DEF_MIX_2_11497;
                activity = "Barbarian Mixing";
                break;
            case "Antidote+ mix":
                sourceItem = ItemList.ANTIDOTE_2_5947;
                targetItem = ItemList.ANTIDOTE_MIX_2_11501;
                activity = "Barbarian Mixing";
                break;
            case "Antifire mix":
                sourceItem = ItemList.ANTIFIRE_POTION_2_2456;
                targetItem = ItemList.ANTIFIRE_MIX_2_11505;
                activity = "Barbarian Mixing";
                break;
            case "Ranging mix":
                sourceItem = ItemList.RANGING_POTION_2_171;
                targetItem = ItemList.RANGING_MIX_2_11509;
                activity = "Barbarian Mixing";
                break;
            case "Magic mix":
                sourceItem = ItemList.MAGIC_POTION_2_3044;
                targetItem = ItemList.MAGIC_MIX_2_11513;
                activity = "Barbarian Mixing";
                break;
            case "Zamorak mix":
                sourceItem = ItemList.ZAMORAK_BREW_2_191;
                targetItem = ItemList.ZAMORAK_MIX_2_11521;
                activity = "Barbarian Mixing";
                break;
            case "Stamina mix":
                sourceItem = ItemList.STAMINA_POTION_2_12629;
                targetItem = ItemList.STAMINA_MIX_2_12633;
                activity = "Barbarian Mixing";
                break;
            case "Extended antifire mix":
                sourceItem = ItemList.EXTENDED_ANTIFIRE_2_11955;
                targetItem = ItemList.EXTENDED_ANTIFIRE_MIX_2_11960;
                activity = "Barbarian Mixing";
                break;
            case "Ancient mix":
                sourceItem = ItemList.ANCIENT_BREW_2_26344;
                targetItem = ItemList.ANCIENT_MIX_2_26350;
                activity = "Barbarian Mixing";
                break;
            case "Super antifire mix":
                sourceItem = ItemList.SUPER_ANTIFIRE_POTION_2_21984;
                targetItem = ItemList.SUPER_ANTIFIRE_MIX_2_21994;
                activity = "Barbarian Mixing";
                break;
            case "Extended super antifire mix":
                sourceItem = ItemList.EXTENDED_SUPER_ANTIFIRE_2_22215;
                targetItem = ItemList.EXTENDED_SUPER_ANTIFIRE_MIX_2_22221;
                activity = "Barbarian Mixing";
                break;

            // Tar Creation Options
            case "Guam tar":
                sourceItem = ItemList.GUAM_LEAF_249;
                targetItem = ItemList.GUAM_TAR_10142;
                activity = "Tar Creation";
                break;
            case "Marrentill tar":
                sourceItem = ItemList.MARRENTILL_251;
                targetItem = ItemList.MARRENTILL_TAR_10143;
                activity = "Tar Creation";
                break;
            case "Tarromin tar":
                sourceItem = ItemList.TARROMIN_253;
                targetItem = ItemList.TARROMIN_TAR_10144;
                activity = "Tar Creation";
                break;
            case "Harralander tar":
                sourceItem = ItemList.HARRALANDER_255;
                targetItem = ItemList.HARRALANDER_TAR_10145;
                activity = "Tar Creation";
                break;
            case "Irit tar":
                sourceItem = ItemList.IRIT_LEAF_259;
                targetItem = ItemList.IRIT_TAR_28837;
                activity = "Tar Creation";
                break;

            // Mixing Unfinished Potion Options
            case "Guam potion (unf)":
                sourceItem = ItemList.GUAM_LEAF_249;
                targetItem = ItemList.GUAM_POTION_UNF_91;
                activity = "Unfinished Potion";
                break;
            case "Marrentill potion (unf)":
                sourceItem = ItemList.MARRENTILL_251;
                targetItem = ItemList.MARRENTILL_POTION_UNF_93;
                activity = "Unfinished Potion";
                break;
            case "Tarromin potion (unf)":
                sourceItem = ItemList.TARROMIN_253;
                targetItem = ItemList.TARROMIN_POTION_UNF_95;
                activity = "Unfinished Potion";
                break;
            case "Harralander potion (unf)":
                sourceItem = ItemList.HARRALANDER_255;
                targetItem = ItemList.HARRALANDER_POTION_UNF_97;
                activity = "Unfinished Potion";
                break;
            case "Ranarr potion (unf)":
                sourceItem = ItemList.RANARR_WEED_257;
                targetItem = ItemList.RANARR_POTION_UNF_99;
                activity = "Unfinished Potion";
                break;
            case "Toadflax potion (unf)":
                sourceItem = ItemList.TOADFLAX_2998;
                targetItem = ItemList.TOADFLAX_POTION_UNF_3002;
                activity = "Unfinished Potion";
                break;
            case "Irit potion (unf)":
                sourceItem = ItemList.IRIT_LEAF_259;
                targetItem = ItemList.IRIT_POTION_UNF_101;
                activity = "Unfinished Potion";
                break;
            case "Avantoe potion (unf)":
                sourceItem = ItemList.AVANTOE_261;
                targetItem = ItemList.AVANTOE_POTION_UNF_103;
                activity = "Unfinished Potion";
                break;
            case "Kwuarm potion (unf)":
                sourceItem = ItemList.KWUARM_263;
                targetItem = ItemList.KWUARM_POTION_UNF_105;
                activity = "Unfinished Potion";
                break;
            case "Huasca potion (unf)":
                sourceItem = ItemList.HUASCA_30097;
                targetItem = ItemList.HUASCA_POTION_UNF_30100;
                activity = "Unfinished Potion";
                break;
            case "Snapdragon potion (unf)":
                sourceItem = ItemList.SNAPDRAGON_3000;
                targetItem = ItemList.SNAPDRAGON_POTION_UNF_3004;
                activity = "Unfinished Potion";
                break;
            case "Cadantine potion (unf)":
                sourceItem = ItemList.CADANTINE_265;
                targetItem = ItemList.CADANTINE_POTION_UNF_107;
                activity = "Unfinished Potion";
                break;
            case "Lantadyme potion (unf)":
                sourceItem = ItemList.LANTADYME_2481;
                targetItem = ItemList.LANTADYME_POTION_UNF_2483;
                activity = "Unfinished Potion";
                break;
            case "Dwarf weed potion (unf)":
                sourceItem = ItemList.DWARF_WEED_267;
                targetItem = ItemList.DWARF_WEED_POTION_UNF_109;
                activity = "Unfinished Potion";
                break;
            case "Torstol potion (unf)":
                sourceItem = ItemList.TORSTOL_269;
                targetItem = ItemList.TORSTOL_POTION_UNF_111;
                activity = "Unfinished Potion";
                break;

            // Cleaning Herb Options
            case "Guam leaf":
                sourceItem = ItemList.GRIMY_GUAM_LEAF_199;
                targetItem = ItemList.GUAM_LEAF_249;
                activity = "Herb Cleaning";
                break;
            case "Marrentill":
                sourceItem = ItemList.GRIMY_MARRENTILL_201;
                targetItem = ItemList.MARRENTILL_251;
                activity = "Herb Cleaning";
                break;
            case "Tarromin":
                sourceItem = ItemList.GRIMY_TARROMIN_203;
                targetItem = ItemList.TARROMIN_253;
                activity = "Herb Cleaning";
                break;
            case "Harralander":
                sourceItem = ItemList.GRIMY_HARRALANDER_205;
                targetItem = ItemList.HARRALANDER_255;
                activity = "Herb Cleaning";
                break;
            case "Ranarr weed":
                sourceItem = ItemList.GRIMY_RANARR_WEED_207;
                targetItem = ItemList.RANARR_WEED_257;
                activity = "Herb Cleaning";
                break;
            case "Toadflax":
                sourceItem = ItemList.GRIMY_TOADFLAX_3049;
                targetItem = ItemList.TOADFLAX_2998;
                activity = "Herb Cleaning";
                break;
            case "Irit leaf":
                sourceItem = ItemList.GRIMY_IRIT_LEAF_209;
                targetItem = ItemList.IRIT_LEAF_259;
                activity = "Herb Cleaning";
                break;
            case "Avantoe":
                sourceItem = ItemList.GRIMY_AVANTOE_211;
                targetItem = ItemList.AVANTOE_261;
                activity = "Herb Cleaning";
                break;
            case "Kwuarm":
                sourceItem = ItemList.GRIMY_KWUARM_213;
                targetItem = ItemList.KWUARM_263;
                activity = "Herb Cleaning";
                break;
            case "Huasca":
                sourceItem = ItemList.GRIMY_HUASCA_30094;
                targetItem = ItemList.HUASCA_30097;
                activity = "Herb Cleaning";
                break;
            case "Snapdragon":
                sourceItem = ItemList.GRIMY_SNAPDRAGON_3051;
                targetItem = ItemList.SNAPDRAGON_3000;
                activity = "Herb Cleaning";
                break;
            case "Cadantine":
                sourceItem = ItemList.GRIMY_CADANTINE_215;
                targetItem = ItemList.CADANTINE_265;
                activity = "Herb Cleaning";
                break;
            case "Lantadyme":
                sourceItem = ItemList.GRIMY_LANTADYME_2485;
                targetItem = ItemList.LANTADYME_2481;
                activity = "Herb Cleaning";
                break;
            case "Dwarf weed":
                sourceItem = ItemList.GRIMY_DWARF_WEED_217;
                targetItem = ItemList.DWARF_WEED_267;
                activity = "Herb Cleaning";
                break;
            case "Torstol":
                sourceItem = ItemList.GRIMY_TORSTOL_219;
                targetItem = ItemList.TORSTOL_269;
                activity = "Herb Cleaning";
                break;
            default:
                Logger.log("Unknown product: " + product + " stopping script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(2000);
                }
                Logout.logout();
                Script.stop();
        }
    }

    public static boolean readyToProcess() {
        // Instant true if we leveled up
        if (Player.leveledUp()) {
            return true;
        }

        switch (product) {
            case "Attack potion":
                return containsHelper(ItemList.GUAM_POTION_UNF_91, 0.7, "#92aaad", ItemList.EYE_OF_NEWT_221, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Antipoison":
                return containsHelper(ItemList.MARRENTILL_POTION_UNF_93, 0.7, "#ad92ad", ItemList.UNICORN_HORN_DUST_235, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Strength potion":
                return containsHelper(ItemList.TARROMIN_POTION_UNF_95, 0.7, "#abaa91", ItemList.LIMPWURT_ROOT_225, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Serum 207":
                return containsHelper(ItemList.TARROMIN_POTION_UNF_95, 0.7, "#abaa91", ItemList.ASHES_592, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Guthix rest tea":
                return containsHelper(ItemList.CUP_OF_HOT_WATER_4460, 0.7, "#8482a9", ItemList.HARRALANDER_255, 0.7, "#4a7007", ItemList.GUAM_LEAF_249, 0.7, "#003304", ItemList.GUAM_LEAF_249, 0.7, "#003304", ItemList.MARRENTILL_251, 0.7, "#076c0a");
            case "Compost potion":
                return containsHelper(ItemList.VOLCANIC_ASH_21622, 0.7, null, ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Restore potion":
                return containsHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.RED_SPIDERS_EGGS_223, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Energy potion":
                return containsHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.CHOCOLATE_DUST_1975, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Defence potion":
                return containsHelper(ItemList.RANARR_POTION_UNF_99, 0.7, "#92ad92", ItemList.WHITE_BERRIES_239, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Agility potion":
                return containsHelper(ItemList.TOADFLAX_POTION_UNF_3002, 0.7, "#6d6c53", ItemList.TOAD_S_LEGS_2152, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Combat potion":
                return containsHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.GOAT_HORN_DUST_9736, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Prayer potion":
                return containsHelper(ItemList.RANARR_POTION_UNF_99, 0.7, "#92ad92", ItemList.SNAPE_GRASS_231, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super attack":
                return containsHelper(ItemList.IRIT_POTION_UNF_101, 0.7, "#aeaeb4", ItemList.EYE_OF_NEWT_221, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Superantipoison":
                return containsHelper(ItemList.IRIT_POTION_UNF_101, 0.7, "#aeaeb4", ItemList.UNICORN_HORN_DUST_235, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Fishing potion":
                return containsHelper(ItemList.AVANTOE_POTION_UNF_103, 0.7, "#9b9191", ItemList.SNAPE_GRASS_231, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super energy":
                return containsHelper(ItemList.AVANTOE_POTION_UNF_103, 0.7, "#9b9191", ItemList.MORT_MYRE_FUNGUS_2970, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Hunter potion":
                return containsHelper(ItemList.AVANTOE_POTION_UNF_103, 0.7, "#9b9191", ItemList.KEBBIT_TEETH_DUST_10111, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Goading potion":
                return containsHelper(ItemList.HARRALANDER_POTION_UNF_97, 0.7, "#ab9492", ItemList.ALDARIUM_29993, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super strength":
                return containsWithExcludeHelper(ItemList.KWUARM_POTION_UNF_105, 0.7, "#ada6a5", "#cecccc", ItemList.LIMPWURT_ROOT_225, 0.7, null, null, -1, -1, null, null, -1, -1, null, null, -1, -1, null, null);
            case "Prayer regeneration potion":
                return containsHelper(ItemList.HUASCA_POTION_UNF_30100, 0.7, "#927187", ItemList.ALDARIUM_29995, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Weapon poison":
                return containsHelper(ItemList.KWUARM_POTION_UNF_105, 0.7, "#ada6a5", ItemList.DRAGON_SCALE_DUST_241, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super restore":
                return containsHelper(ItemList.SNAPDRAGON_POTION_UNF_3004, 0.7, "#a17b30", ItemList.RED_SPIDERS_EGGS_223, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super defence":
                return containsHelper(ItemList.CADANTINE_POTION_UNF_107, 0.7, "#b5a8a0", ItemList.WHITE_BERRIES_239, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Antidote+":
                return containsHelper(ItemList.ANTIDOTE_UNF_5942, 0.7, null, ItemList.YEW_ROOTS_6049, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Antifire potion":
                return containsHelper(ItemList.LANTADYME_POTION_UNF_2483, 0.7, "#b5a8a0", ItemList.DRAGON_SCALE_DUST_241, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Divine super attack potion":
                return containsHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_ATTACK_4_2436, 0.7, "#4547d0", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Divine super strength potion":
                return containsHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_STRENGTH_4_2440, 0.7, "#d2d0d0", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Divine super defence potion":
                return containsHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_DEFENCE_4_2442, 0.7, "#d0ad48", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Ranging potion":
                return containsHelper(ItemList.DWARF_WEED_POTION_UNF_109, 0.7, "#9fa6b5", ItemList.WINE_OF_ZAMORAK_245, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Weapon poison+":
                return containsHelper(ItemList.WEAPON_POISON_UNF_5936, 0.7, null, ItemList.RED_SPIDERS_EGGS_223, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Divine ranging potion":
                return containsHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.RANGING_POTION_4_2444, 0.7, "#48a8d0", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Magic potion":
                return containsHelper(ItemList.LANTADYME_POTION_UNF_2483, 0.7, "#b5a8a0", ItemList.POTATO_CACTUS_3138, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Stamina potion":
                return containsHelper(ItemList.AMYLASE_CRYSTAL_12640, 0.7, null, ItemList.SUPER_ENERGY_4_3016, 0.7, "#ba4e93", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Zamorak brew":
                return containsHelper(ItemList.TORSTOL_POTION_UNF_111, 0.7, "#b9aea6", ItemList.JANGERBERRIES_247, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Divine magic potion":
                return containsHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.MAGIC_POTION_4_3040, 0.7, "#c39f94", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Antidote++":
                return containsHelper(ItemList.ANTIDOTE_UNF_5951, 0.7, null, ItemList.MAGIC_ROOTS_6051, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Bastion potion":
                return containsHelper(ItemList.CADANTINE_BLOOD_POTION_UNF_22443, 0.7, "#9e5c4f", ItemList.WINE_OF_ZAMORAK_245, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Battlemage potion":
                return containsHelper(ItemList.CADANTINE_BLOOD_POTION_UNF_22443, 0.7, "#9e5c4f", ItemList.POTATO_CACTUS_3138, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Saradomin brew":
                return containsHelper(ItemList.TOADFLAX_POTION_UNF_3002, 0.7, "#6d6c53", ItemList.CRUSHED_NEST_6693, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Weapon poison++":
                return containsHelper(ItemList.WEAPON_POISON_UNF_5939, 0.7, null, ItemList.POISON_IVY_BERRIES_6018, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Extended antifire":
                return containsHelper(ItemList.LAVA_SCALE_SHARD_11994, 0.7, null, ItemList.ANTIFIRE_POTION_4_2452, 0.7, "#690a90", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Ancient brew":
                return containsHelper(ItemList.NIHIL_DUST_26368, 0.7, null, ItemList.DWARF_WEED_POTION_UNF_109, 0.7, "#9fa6b5", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Divine bastion potion":
                return containsHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.BASTION_POTION_4_22461, 0.7, "#b5550e", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Divine battlemage potion":
                return containsHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.BATTLEMAGE_POTION_4_22449, 0.7, "#d89f27", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Anti-venom":
                return containsHelper(ItemList.ZULRAH_S_SCALES_12934, 0.7, null, ItemList.ANTIDOTE_4_5952, 0.7, "#72752f", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Menaphite remedy":
                return containsHelper(ItemList.DWARF_WEED_POTION_UNF_109, 0.7, "#9fa6b5", ItemList.LILY_OF_THE_SANDS_27272, 0.7, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super combat potion":
                return containsHelper(ItemList.TORSTOL_POTION_UNF_111, 0.7, "#b9aea6", ItemList.SUPER_ATTACK_4_2436, 0.7, "#4547d0", ItemList.SUPER_STRENGTH_4_2440, 0.7, "#d2d0d0",  ItemList.SUPER_DEFENCE_4_2442, 0.7, "#d0ad48", -1, -1, null);
            case "Forgotten brew":
                return containsHelper(ItemList.ANCIENT_ESSENCE_27616, 0.7, null, ItemList.ANCIENT_BREW_4_26340, 0.7, "#9f63c9", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super antifire potion":
                return containsHelper(ItemList.CRUSHED_SUPERIOR_DRAGON_BONES_21975, 0.7, null, ItemList.ANTIFIRE_POTION_4_2452, 0.7, "#690a90", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Anti-venom+":
                return containsHelper(ItemList.ANTI_VENOM_4_12905, 0.7, "#304037", ItemList.TORSTOL_269, 0.7, "#045407", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Extended anti-venom+":
                return containsHelper(ItemList.ARAXYTE_VENOM_SACK_29784, 0.7, null, ItemList.ANTI_VENOM_4_12913, 0.7, "#4e3a41", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Divine super combat potion":
                return containsHelper(ItemList.CRYSTAL_DUST_23964, 0.7, null, ItemList.SUPER_COMBAT_POTION_4_12695, 0.7, "#126106", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Extended super antifire (SAP)":
                return containsHelper(ItemList.LAVA_SCALE_SHARD_11994, 0.7, null, ItemList.SUPER_ANTIFIRE_POTION_4_21978, 0.7, "#8253a2", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Extended super antifire (EAP)":
                return containsHelper(ItemList.CRUSHED_SUPERIOR_DRAGON_BONES_21975, 0.7, null, ItemList.EXTENDED_ANTIFIRE_4_11951, 0.7, "#622bce", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Attack mix":
                return containsHelper(ItemList.ROE_11324, 0.7, null, ItemList.ATTACK_POTION_2_123, 0.7, "#40c9d0", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Antipoison mix":
                return containsHelper(ItemList.ROE_11324, 0.7, null, ItemList.ANTIPOISON_2_177, 0.7, "#6ed816", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Strength mix":
                return containsHelper(ItemList.ROE_11324, 0.7, null, ItemList.STRENGTH_POTION_2_117, 0.7, "#cfcd3d", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Restore mix":
                return containsHelper(ItemList.ROE_11324, 0.7, null, ItemList.RESTORE_POTION_2_129, 0.7, "#d04840", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Energy mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ENERGY_POTION_2_3012, 0.7, "#9e4f5c", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Defence mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.DEFENCE_POTION_2_135, 0.7, "#40d043", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Agility mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.AGILITY_POTION_2_3036, 0.7, "#6d870a", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Combat mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.COMBAT_POTION_2_9743, 0.7, "#7db467", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Prayer mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.PRAYER_POTION_2_141, 0.7, "#3dcf98", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Superattack mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_ATTACK_2_147, 0.7, "#4043d0", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Anti-poison supermix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPERANTIPOISON_2_183, 0.7, "#d81c6b", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Fishing mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.FISHING_POTION_2_153, 0.7, "#413c3c", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super energy mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_ENERGY_2_3020, 0.7, "#b74991", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Hunting mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.HUNTER_POTION_2_10002, 0.7, "#065053", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super str. mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_STRENGTH_2_159, 0.7, "#cecbcb", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super restore mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_RESTORE_2_3028, 0.7, "#ae3262", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super def. mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_DEFENCE_2_165, 0.7, "#d0ab40", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Antidote+ mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ANTIDOTE_2_5947, 0.7, "#717156", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Antifire mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ANTIFIRE_POTION_2_2456, 0.7, "#690a90", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Ranging mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.RANGING_POTION_2_171, 0.7, "#3da6cf", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Magic mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.MAGIC_POTION_2_3044, 0.7, "#bf9a8f", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Zamorak mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ZAMORAK_BREW_2_191, 0.7, "#c78d10", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Stamina mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.STAMINA_POTION_2_12629, 0.7, "#916a3a", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Extended antifire mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.EXTENDED_ANTIFIRE_2_11955, 0.7, "#5e27cb", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Ancient mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.ANCIENT_BREW_2_26344, 0.7, "#9e61c9", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Super antifire mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.SUPER_ANTIFIRE_POTION_2_21984, 0.7, "#7d529f", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Extended super antifire mix":
                return containsHelper(ItemList.CAVIAR_11326, 0.7, null, ItemList.EXTENDED_SUPER_ANTIFIRE_2_22215, 0.7, "#a680c4", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Guam tar":
                return containsHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.GUAM_LEAF_249, 0.7, "#044104", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Marrentill tar":
                return containsHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.MARRENTILL_251, 0.7, "#076c0a", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Tarromin tar":
                return containsHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.TARROMIN_253, 0.7, "#076c2f", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Harralander tar":
                return containsHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.HARRALANDER_255, 0.7, "#456807", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Irit tar":
                return containsHelper(ItemList.SWAMP_TAR_1939, 0.7, null, ItemList.IRIT_LEAF_259, 0.7, "#47702c", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Guam potion (unf)":
                return containsHelper(ItemList.GUAM_LEAF_249, 0.7, "#044104", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Marrentill potion (unf)":
                return containsHelper(ItemList.MARRENTILL_251, 0.7, "#076c0a", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Tarromin potion (unf)":
                return containsHelper(ItemList.TARROMIN_253, 0.7, "#076c2f", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Harralander potion (unf)":
                return containsHelper(ItemList.HARRALANDER_255, 0.7, "#456807", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Ranarr potion (unf)":
                return containsHelper(ItemList.RANARR_WEED_257, 0.7, "#335904", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Toadflax potion (unf)":
                return containsHelper(ItemList.TOADFLAX_2998, 0.7, "#002100", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Irit potion (unf)":
                return containsHelper(ItemList.IRIT_LEAF_259, 0.7, "#47702c", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Avantoe potion (unf)":
                return containsHelper(ItemList.AVANTOE_261, 0.7, "#0c4c1e", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Kwuarm potion (unf)":
                return containsHelper(ItemList.KWUARM_263, 0.7, "#455004", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Huasca potion (unf)":
                return containsHelper(ItemList.HUASCA_30097, 0.7, "#4c2f41", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Snapdragon potion (unf)":
                return containsHelper(ItemList.SNAPDRAGON_3000, 0.7, "#344104", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Cadantine potion (unf)":
                return containsHelper(ItemList.CADANTINE_265, 0.7, "#3c4715", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Lantadyme potion (unf)":
                return containsHelper(ItemList.LANTADYME_2481, 0.7, "#04463a", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Dwarf weed potion (unf)":
                return containsHelper(ItemList.DWARF_WEED_267, 0.7, "#043c04", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Torstol potion (unf)":
                return containsHelper(ItemList.TORSTOL_269, 0.7, "#045007", ItemList.VIAL_OF_WATER_227, 0.7, "#9b9fbc", -1, -1, null, -1, -1, null, -1, -1, null);
            case "Guam leaf":
                return containsHelper(ItemList.GRIMY_GUAM_LEAF_199, 0.7, "#044104", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Marrentill":
                return containsHelper(ItemList.GRIMY_MARRENTILL_201, 0.8, "#076c0a", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Tarromin":
                return containsHelper(ItemList.GRIMY_TARROMIN_203, 0.8, "#076c2f", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Harralander":
                return containsHelper(ItemList.GRIMY_HARRALANDER_205, 0.92, "#456807", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Ranarr weed":
                return containsHelper(ItemList.GRIMY_RANARR_WEED_207, 0.87, "#335904", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Toadflax":
                return containsHelper(ItemList.GRIMY_TOADFLAX_3049, 0.7, "#002100", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Irit leaf":
                return containsHelper(ItemList.GRIMY_IRIT_LEAF_209, 0.92, "#47702c", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Avantoe":
                return containsHelper(ItemList.GRIMY_AVANTOE_211, 0.7, "#0c4c1e", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Kwuarm":
                return containsHelper(ItemList.GRIMY_KWUARM_213, 0.89, "#455004", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Huasca":
                return containsHelper(ItemList.GRIMY_HUASCA_30094, 0.88, "#4c2f41", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Snapdragon":
                return containsHelper(ItemList.GRIMY_SNAPDRAGON_3051, 0.89, "#344104", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Cadantine":
                return containsHelper(ItemList.GRIMY_CADANTINE_215, 0.87, "#3c4715", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Lantadyme":
                return containsHelper(ItemList.GRIMY_LANTADYME_2485, 0.7, "#04463a", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Dwarf weed":
                return containsHelper(ItemList.GRIMY_DWARF_WEED_217, 0.7, "#043c04", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            case "Torstol":
                return containsHelper(ItemList.GRIMY_TORSTOL_219, 0.7, "#045007", -1, -1, null, -1, -1, null, -1, -1, null, -1, -1, null);
            default:
                Logger.log("Unknown product in readyToProcess (main): " + product + " stopping script.");
                if (Bank.isOpen()) {
                    Bank.close();
                    Condition.sleep(2000);
                }
                Logout.logout();
                Script.stop();
        }

        return false;
    }

    private static boolean containsHelper(
            int item1Id, double item1Threshold, String item1Color,
            int item2Id, double item2Threshold, String item2Color,
            int item3Id, double item3Threshold, String item3Color,
            int item4Id, double item4Threshold, String item4Color,
            int item5Id, double item5Threshold, String item5Color
    ) {
        return checkItem(item1Id, item1Threshold, item1Color) &&
                checkItem(item2Id, item2Threshold, item2Color) &&
                checkItem(item3Id, item3Threshold, item3Color) &&
                checkItem(item4Id, item4Threshold, item4Color) &&
                checkItem(item5Id, item5Threshold, item5Color);
    }

    private static boolean checkItem(int itemId, double threshold, String color) {
        if (itemId == -1) return true;
        return color != null ? Inventory.contains(itemId, threshold, Color.decode(color))
                : Inventory.contains(itemId, threshold);
    }

    private static boolean containsWithExcludeHelper(
            int item1Id, double item1Threshold, String item1Color, String item1ExcludeColor,
            int item2Id, double item2Threshold, String item2Color, String item2ExcludeColor,
            int item3Id, double item3Threshold, String item3Color, String item3ExcludeColor,
            int item4Id, double item4Threshold, String item4Color, String item4ExcludeColor,
            int item5Id, double item5Threshold, String item5Color, String item5ExcludeColor
    ) {
        return checkItemWithExclude(item1Id, item1Threshold, item1Color, item1ExcludeColor) &&
                checkItemWithExclude(item2Id, item2Threshold, item2Color, item2ExcludeColor) &&
                checkItemWithExclude(item3Id, item3Threshold, item3Color, item3ExcludeColor) &&
                checkItemWithExclude(item4Id, item4Threshold, item4Color, item4ExcludeColor) &&
                checkItemWithExclude(item5Id, item5Threshold, item5Color, item5ExcludeColor);
    }

    private static boolean checkItemWithExclude(int itemId, double threshold, String color, String excludeColor) {
        if (itemId == -1) return true;
        return color != null ? Inventory.contains(itemId, threshold, Color.decode(color), Color.decode(excludeColor))
                : Inventory.contains(itemId, threshold);
    }
}