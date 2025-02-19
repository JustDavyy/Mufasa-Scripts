package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.Task;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dShopBuyer",
        description = "Buys stuff from different stores all around Gielinor, aimed to aid ironman accounts",
        version = "1.11",
        guideLink = "https://wiki.mufasaclient.com/docs/dshopbuyer/",
        categories = {ScriptCategory.Smithing, ScriptCategory.Crafting, ScriptCategory.Moneymaking, ScriptCategory.Ironman},
        skipZoomSetup = true
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "The default for this script is enabled and recommended to do so",
                        defaultValue = "true",
                        wdhEnabled = "false",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name = "Item to buy",
                        description = "Select which item(s) you'd like to buy",
                        defaultValue = "Gold ore",
                        allowedValues = {
                                @AllowedValue(optionIcon = "436", optionName = "Copper ore"),
                                @AllowedValue(optionIcon = "438", optionName = "Tin ore"),
                                @AllowedValue(optionIcon = "440", optionName = "Iron ore"),
                                @AllowedValue(optionIcon = "447", optionName = "Mithril ore"),
                                @AllowedValue(optionIcon = "442", optionName = "Silver ore"),
                                @AllowedValue(optionIcon = "444", optionName = "Gold ore"),
                                @AllowedValue(optionIcon = "453", optionName = "Coal"),
                                @AllowedValue(optionIcon = "2349", optionName = "Copper + Tin"),
                                @AllowedValue(optionIcon = "2353", optionName = "Iron + Coal"),
                                @AllowedValue(optionIcon = "2359", optionName = "Mithril + Coal"),
                                @AllowedValue(optionIcon = "1993", optionName = "Jug of Wine"),
                                @AllowedValue(optionIcon = "20742", optionName = "Empty Jug Pack"),
                                @AllowedValue(optionIcon = "22660", optionName = "Empty bucket pack"),
                                @AllowedValue(optionIcon = "2114", optionName = "Pineapple"),
                                @AllowedValue(optionIcon = "4286", optionName = "Bucket of slime"),
                                @AllowedValue(optionIcon = "1783", optionName = "Bucket of sand"),
                                @AllowedValue(optionIcon = "401", optionName = "Seaweed"),
                                @AllowedValue(optionIcon = "1781", optionName = "Soda ash"),
                                @AllowedValue(optionIcon = "1775", optionName = "Bucket of sand + Seaweed"),
                                @AllowedValue(optionIcon = "1775", optionName = "Bucket of sand + Soda ash"),
                                @AllowedValue(optionIcon = "24260", optionName = "Sand + Seaweed + Soda ash")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Shop",
                        description = "Select which shop you'd like to buy from",
                        defaultValue = "Blast Furnace",
                        allowedValues = {
                                @AllowedValue(optionName = "Blast Furnace"),
                                @AllowedValue(optionName = "Fortunato Wine Shop"),
                                @AllowedValue(optionName = "Khazard Charter")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Amount",
                        description = "Select how many you would like to buy",
                        defaultValue = "5000",
                        minMaxIntValues = {1, 1000000},
                        optionType = OptionType.INTEGER
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

public class dShopBuyer extends AbstractScript {
    public Boolean hasCoins;
    public boolean doneMESSetup = false;
    private boolean antiBan;
    private boolean extendedAntiBan;
    public String hopProfile;
    public String itemToBuy;
    public String shopToUse;
    public static final Random random = new Random();
    public int bankItemID;
    public int amountToBuy;
    public int initialCoins;
    public int paintItem1 = -50;
    public int paintItem2 = -50;
    public int paintItem3 = -50;
    public int paintProfit;
    public int itemCost1 = -50;
    public int itemCost2 = -50;
    public int itemCost3 = -50;
    public int boughtAmount1 = -50;
    public int boughtAmount2 = -50;
    public int boughtAmount3 = -50;
    public static int MESSetupTries = 0;
    public long startTime;
    public long lastUpdateTime = System.currentTimeMillis();


    // Blast Furnace
    public Tile BFShopTile = new Tile(7743, 19609, 0);
    public Tile BFBankTile = new Tile(7791, 19577, 0);
    public Rectangle BFShopRect = new Rectangle(270, 144, 6, 8);
    public Area BFScriptArea = new Area(new Tile(7731, 19557, 0), new Tile(7845, 19657, 0));

    // Fortunato Wine Shop
    public Area FortunatoWineScriptArea = new Area(new Tile(12289, 12690, 0),new Tile(12428, 12821, 0));
    public Tile FWSBankTile = new Tile(12367, 12729, 0);
    public Tile FWSShopTile = new Tile(12339, 12753, 0);
    public Rectangle FWSBankRect = new Rectangle(392, 256, 23, 30);

    // Khazard Charter Shop
    public Area khazardCharterArea = new Area(new Tile(10605, 12300, 0), new Tile(10740, 12450, 0));
    public Tile khazardCharterCrewStepTile = new Tile(10695, 12329, 0);
    public Tile khazardCharterWalkToTile = new Tile(10655, 12353, 0);
    public Tile khazardCharterBankTile = new Tile(10643, 12397, 0);
    public Rectangle khazardCharterBankRect = new Rectangle(433, 215, 17, 25);

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        hopProfile = configs.get("Use world hopper?");
        itemToBuy = configs.get("Item to buy");
        shopToUse = configs.get("Shop");
        amountToBuy = Integer.parseInt(configs.get("Amount"));
        antiBan = Boolean.valueOf(configs.get("Run anti-ban"));
        extendedAntiBan = Boolean.valueOf(configs.get("Run extended anti-ban"));

        Logger.log("Starting dShopBuyer...");
        Paint.Create("/logo/davyy.png");
        Paint.setStatus("Performing startup actions");

        if (antiBan) {
            Logger.debugLog("Initializing anti-ban timer");
            Game.antiBan();
            if (extendedAntiBan) {
                Logger.debugLog("Initializing extended anti-ban timer");
                Game.enableOptionalAntiBan(AntiBan.EXTENDED_AFK);
                Game.antiBan();
            }
        }

        Logger.log("We are buying: " + itemToBuy + " at the " + shopToUse + " shop.");
    }

    // Task list!
    List<Task> buyerTasks = Arrays.asList(
            new InitialSetup(this),
            new Bank(this),
            new Buy(this)
    );

    @Override
    public void poll() {

        if (antiBan) {
            Game.antiBan();
        }

        //Run tasks
        for (Task task : buyerTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }

    }

    public void updatePaintBar() {
        int totalItemsBought = 0;
        int totalCost = 0;

        // Update the state label on the paintBar
        Paint.setStatus("Update paintBar");

        // Update item 1 on the paintBar
        if (paintItem1 != -50 && itemCost1 != -50 && boughtAmount1 != -50) {
            totalItemsBought += boughtAmount1;
            totalCost += boughtAmount1 * itemCost1;
            Paint.updateBox(paintItem1, boughtAmount1);
        }

        // Update item 2 on the paintBar
        if (paintItem2 != -50 && itemCost2 != -50 && boughtAmount2 != -50) {
            totalItemsBought += boughtAmount2;
            totalCost += boughtAmount2 * itemCost2;
            Paint.updateBox(paintItem2, boughtAmount2);
        }

        // Update item 3 on the paintBar
        if (paintItem3 != -50 && itemCost3 != -50 && boughtAmount3 != -50) {
            totalItemsBought += boughtAmount3;
            totalCost += boughtAmount3 * itemCost3;
            Paint.updateBox(paintItem3, boughtAmount3);
        }

        // Update the coins used
        Paint.updateBox(paintProfit, totalCost);

        // Calculate time passed in hours
        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        double elapsedTimeHours = (double) elapsedTimeMillis / (1000 * 60 * 60); // Convert to hours

        // Calculate items per hour
        double itemsPerHour = (elapsedTimeHours > 0) ? (totalItemsBought / elapsedTimeHours) : 0;

        // Update the stat label on the paintBar
        Paint.setStatistic("Items bought p/h: " + String.format("%.2f", itemsPerHour));
    }

}