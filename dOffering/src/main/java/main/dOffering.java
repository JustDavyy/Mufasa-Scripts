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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dOffering",
        description = "Offers bones and ashes using the Arceuus spellbook for quick prayer gains.",
        version = "2.02",
        guideLink = "https://wiki.mufasaclient.com/docs/doffering/",
        categories = {ScriptCategory.Prayer, ScriptCategory.Magic}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What would you like to offer to the gods today?",
                        defaultValue = "Abyssal ashes",
                        allowedValues = {
                                @AllowedValue(optionIcon = "25766", optionName = "Fiendish ashes"),
                                @AllowedValue(optionIcon = "25769", optionName = "Vile ashes"),
                                @AllowedValue(optionIcon = "25772", optionName = "Malicious ashes"),
                                @AllowedValue(optionIcon = "25775", optionName = "Abyssal ashes"),
                                @AllowedValue(optionIcon = "25778", optionName = "Infernal ashes"),
                                @AllowedValue(optionIcon = "526", optionName = "Bones"),
                                @AllowedValue(optionIcon = "532", optionName = "Big bones"),
                                @AllowedValue(optionIcon = "534", optionName = "Babydragon bones"),
                                @AllowedValue(optionIcon = "22780", optionName = "Wyrm bones"),
                                @AllowedValue(optionIcon = "536", optionName = "Dragon bones"),
                                @AllowedValue(optionIcon = "6812", optionName = "Wyvern bones"),
                                @AllowedValue(optionIcon = "22783", optionName = "Drake bones"),
                                @AllowedValue(optionIcon = "11943", optionName = "Lava dragon bones"),
                                @AllowedValue(optionIcon = "22786", optionName = "Hydra bones"),
                                @AllowedValue(optionIcon = "6729", optionName = "Dagannoth bones"),
                                @AllowedValue(optionIcon = "22124", optionName = "Superior dragon bones")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Runes",
                        description = "Will we be using runes in inventory, or inside a rune pouch?",
                        defaultValue = "Divine Rune Pouch",
                        allowedValues = {
                                @AllowedValue(optionName = "Inventory"),
                                @AllowedValue(optionIcon = "12791", optionName = "Rune Pouch"),
                                @AllowedValue(optionIcon = "27281", optionName = "Divine Rune Pouch")
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

public class dOffering extends AbstractScript {
    public static String product;
    public static String runeStorage;
    static String hopProfile;
    static Boolean hopEnabled;
    static Boolean useWDH;
    private boolean antiBan;
    private boolean extendedAntiBan;
    public static boolean setupDone = false;
    public static String bankloc;
    public static int banktab;
    public static final Random random = new Random();
    public static int offerItem;
    public static boolean doneProcessing = false;
    public static int itemCount = 0;
    public static int totalIterations = 0;
    public static int currentIteration = 0;

    // Process stuff we need to re-initiate actions
    public static long lastProcessTime = System.currentTimeMillis();
    public static long lastCastTime = 0;

    // Banking stuff we need to prevent releasing placeholders
    public static boolean prepareScriptStop = false;
    public static boolean stopScript = false;
    public static boolean doneBanking = false;
    public static int retrycount = 0;
    public static int bankItem1Count = 0;
    public static int previousBankItem1Count = 0;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");
        runeStorage = configs.get("Runes");
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        antiBan = Boolean.valueOf(configs.get("Run anti-ban"));
        extendedAntiBan = Boolean.valueOf(configs.get("Run extended anti-ban"));

        Logger.log("Thank you for using the dOffering script!\nSetting up everything for your gains now...");

        // Initialize what we need to before proceeding
        initializeOptions();

        // Set the top header(s) of paintUI.
        Paint.setStatus("Initializing...");

        // One-time setup
        hopActions();

        if (antiBan) {
            Logger.debugLog("Initializing anti-ban timer");
            Game.antiBan();
            if (extendedAntiBan) {
                Logger.debugLog("Initializing extended anti-ban timer");
                Game.enableOptionalAntiBan(AntiBan.EXTENDED_AFK);
                Game.antiBan();
            }
        }

        // Open inventory tab
        GameTabs.openTab(UITabs.INVENTORY);
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
            stopScript = true;
        }

        // Check if it's time to hop
        hopActions();

        // Read XP
        readXP();

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

    private void initializeOptions() {
        switch (product) {
            case "Fiendish ashes":
                offerItem = ItemList.FIENDISH_ASHES_25766;
                break;
            case "Vile ashes":
                offerItem = ItemList.VILE_ASHES_25769;
                break;
            case "Malicious ashes":
                offerItem = ItemList.MALICIOUS_ASHES_25772;
                break;
            case "Abyssal ashes":
                offerItem = ItemList.ABYSSAL_ASHES_25775;
                break;
            case "Infernal ashes":
                offerItem = ItemList.INFERNAL_ASHES_25778;
                break;
            case "Bones":
                offerItem = ItemList.BONES_526;
                break;
            case "Big bones":
                offerItem = ItemList.BIG_BONES_532;
                break;
            case "Babydragon bones":
                offerItem = ItemList.BABYDRAGON_BONES_534;
                break;
            case "Wyrm bones":
                offerItem = ItemList.WYRM_BONES_22780;
                break;
            case "Dragon bones":
                offerItem = ItemList.DRAGON_BONES_536;
                break;
            case "Wyvern bones":
                offerItem = ItemList.WYVERN_BONES_6812;
                break;
            case "Drake bones":
                offerItem = ItemList.DRAKE_BONES_22783;
                break;
            case "Lava dragon bones":
                offerItem = ItemList.LAVA_DRAGON_BONES_11943;
                break;
            case "Hydra bones":
                offerItem = ItemList.HYDRA_BONES_22786;
                break;
            case "Dagannoth bones":
                offerItem = ItemList.DAGANNOTH_BONES_6729;
                break;
            case "Superior dragon bones":
                offerItem = ItemList.SUPERIOR_DRAGON_BONES_22124;
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
}