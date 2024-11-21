package main;

import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
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
        name = "dWinemaker",
        description = "Creates well fermented wine for those juicy cooking gains. Supports dynamic banking.",
        version = "2.00",
        guideLink = "https://wiki.mufasaclient.com/docs/dwinemaker/",
        categories = {ScriptCategory.Cooking}
)
@ScriptConfiguration.List(
        {
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
                )
        }
)

public class dWinemaker extends AbstractScript {
    static String hopProfile;
    static Boolean hopEnabled;
    static Boolean useWDH;
    public static boolean setupDone = false;
    public static String bankloc;
    public static int banktab;
    public static final Random random = new Random();

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
    public static int bankItem2Count = 0;
    public static int previousBankItem1Count = 0;
    public static int previousBankItem2Count = 0;

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
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        Logger.log("Thank you for using the dWinemaker script!\nSetting up everything for your gains now...");

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create a single image box, to show the amount of processed items
        productIndex = Paint.createBox("Jug of Wine", ItemList.JUG_OF_WINE_1993, PROCESS_COUNT);

        // Set the top header(s) of paintUI.
        Paint.setStatus("Initializing...");
        Paint.setStatistic("Statistics will update soon.");

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
        String statistics = String.format("Wines/hr: %s", itemsPerHourFormatted);
        Paint.setStatistic(statistics);
    }
}