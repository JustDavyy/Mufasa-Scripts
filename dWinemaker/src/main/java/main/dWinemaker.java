package main;

import helpers.*;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import tasks.Bank;
import tasks.DoWines;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "main.dWinemaker",
        description = "Creates well fermented wine for those juicy cooking gains. Supports dynamic banking.",
        version = "1.09",
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
    public static String bankloc;
    public static int banktab;
    public static int GRAPES = 1987;
    public static int JUG_OF_WATER = 1937;
    public static int productIndex;
    public static int PROCESS_COUNT = 0;
    public static int INVENT_COUNT = 0;

    public static Color JUG_OF_WINE_COLOR = Color.decode("#bc4139");

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));

        Logger.log("Thank you for using the main.dWinemaker script!\nSetting up everything for your gains now...");

        // Creating the Paint object
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create a single image box, to show the amount of processed bows
        productIndex = Paint.createBox("Jug of Wine", 1993, PROCESS_COUNT);

        // Set the two top headers of paintUI.
        Paint.setStatus("Initializing...");

        hopActions();
        setupBanking();
    }

    // This is the main part of the script, poll gets looped constantly
    List<Task> processTasks = Arrays.asList(
            new DoWines(),
            new Bank()
    );

    @Override
    public void poll() {

        checkInventOpen();
        hopActions();

        //Run tasks
        for (Task task : processTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    private void setupBanking() {
        Logger.debugLog("Starting setupBanking() method.");
        Paint.setStatus("Setting up banking");
        if (bankloc == null) {
            Logger.debugLog("Starting dynamic banking setup...");

            Logger.debugLog("Starting setup for Dynamic Banking.");
            Paint.setStatus("Setting up dynamic bank");
            bankloc = Bank.setupDynamicBank();
            Logger.debugLog("We're located at: " + bankloc + ".");
            if (bankloc == null) {
                Logger.debugLog("Could not find a dynamic bank location we are in, logging out and aborting script.");
                Logout.logout();
                Script.stop();
            }
        }
        Logger.debugLog("Ending the setupBanking() method.");
    }

    private void checkInventOpen() {
        Paint.setStatus("Check inventory open");
        // Check if the inventory is open (needs this check after a break)
        if (!GameTabs.isInventoryTabOpen()) {
            Paint.setStatus("Opening inventory");
            GameTabs.openInventoryTab();
        }
    }

    public static void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        }
    }

    private static final Random random = new Random();
    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }
}