package tasks;

import helpers.utils.Tile;
import main.PrivateTanner;
import utils.Task;

import static helpers.Interfaces.*;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;

public class Buying extends Task {
    static final Tile GeTile1 = new Tile(12655, 13697, 0);
    static final Tile GeTile2 = new Tile(12659, 13697, 0);
    static final Tile GeTile3 = new Tile(12663, 13697, 0);
    static final Rectangle GeTileC1 = new Rectangle(422, 238, 40, 15);
    static final Rectangle GeTileC2 = new Rectangle(406, 239, 58, 11);
    static final Rectangle GeTileC3 = new Rectangle(381, 238, 45, 12);

    static final Rectangle CloseGe = new Rectangle(553, 184, 15, 16);



    private static final int SHORT_DELAY_MIN = 350;
    private static final int SHORT_DELAY_MAX = 450;

    private static final int MEDIUM_DELAY_MIN = 550;
    private static final int MEDIUM_DELAY_MAX = 750;

    private static final int LONG_DELAY_MIN = 5000;
    private static final int LONG_DELAY_MAX = 16000;

    public Boolean GreenDHideHandling = false;
    public Boolean BlueDHideHandling = false;

    public static int BuyGreenDhideat = 0;
    public static int BuyBlueDhideat = 0;

    public static int SellGreenDhideat = 0;
    public static int SellBlueDhideat = 0;

    private static boolean FinishedBuySellGreenDHide = false;
    private static boolean FinishedBuySellBlueDHide = false;


    private int getShortDelay() {
        return SHORT_DELAY_MIN + (int)(Math.random() * ((SHORT_DELAY_MAX - SHORT_DELAY_MIN) + 1));
    }
    
    private int getMediumDelay() {
        return MEDIUM_DELAY_MIN + (int)(Math.random() * ((MEDIUM_DELAY_MAX - MEDIUM_DELAY_MIN) + 1));
    }
    
    private int getLongDelay() {
        return LONG_DELAY_MIN + (int)(Math.random() * ((LONG_DELAY_MAX - LONG_DELAY_MIN) + 1));
    }

    public boolean activate() {
        Logger.log("Buyinge: Checking..");
        return PrivateTanner.BuyHide && (!GreenDHideHandling || !BlueDHideHandling);
    }




    @Override
    public boolean execute() {
        if (!GrandExchange.isOpen() && PrivateTanner.BuyHide) {
            openGEInterface();
        }
        
        if (FinishedBuySellGreenDHide && FinishedBuySellBlueDHide) {
            if (GrandExchange.getCompleted() < 2) {
                Condition.sleep(getLongDelay());
                return true;
            } else if (GrandExchange.getCompleted() == 2) {
                GrandExchange.collectAllItems();
                Condition.sleep(getMediumDelay());

                Client.tap(CloseGe);
                Condition.sleep(getMediumDelay());
                PrivateTanner.BuyHide = false;
                return false;
            } 
        }

        // Process Green Dragon Hide
        if (!GreenDHideHandling) {
            Logger.log("Buying Green hides in progress");
            if (Inventory.contains(PrivateTanner.GreenDhideDoneNoted, 0.9)) {
                GrandExchange.sellItem(PrivateTanner.GreenDhideDoneNoted, 13000, SellGreenDhideat);
                Condition.sleep(452);
                return true;
            }
            if (!Inventory.contains(PrivateTanner.GreenDhideDoneNoted, 0.9)) {
                GrandExchange.buyItem("Green dragon leather", PrivateTanner.GreenDHideRaw, 13000, BuyGreenDhideat);
                FinishedBuySellGreenDHide = true;
                return true;
            }
            return true;
        }
    
        // Process Blue Dragon Hide
        if (!BlueDHideHandling) {
            Logger.log("Buying Blue hides in progress");
            if (Inventory.contains(PrivateTanner.BlueDHideDoneNoted, 0.9)) {
                GrandExchange.sellItem(PrivateTanner.BlueDHideDoneNoted, 13000, SellBlueDhideat);
                Condition.sleep(452);
                return true;
            }
            if (!Inventory.contains(PrivateTanner.BlueDHideDoneNoted, 0.9)) {
                GrandExchange.buyItem("Blue dragon leather", PrivateTanner.BlueDHideRaw, 13000, BuyBlueDhideat);
                FinishedBuySellBlueDHide = true;
            }
            return true;
        }
        return false;
    }




    public static Tile getRandomGeTile() {
        Tile[] tiles = {GeTile1, GeTile2, GeTile3};
        Random random = new Random();
        int randomIndex = random.nextInt(tiles.length); // Random index between 0 and 2
        return tiles[randomIndex];
    }

    private void openGEInterface() {
        if (Player.atTile(GeTile1)) {
            Client.tap(GeTileC1);
        } else if (Player.atTile(GeTile2)) {
            Client.tap(GeTileC2);
        } else if (Player.atTile(GeTile3)) {
            Client.tap(GeTileC3);
        }
        Condition.sleep(getMediumDelay());
    }

    public void spellOut(String text) {
        for (char c : text.toCharArray()) {
            Client.sendKeystroke(c == ' ' ? "KEYCODE_SPACE" : "KEYCODE_" + Character.toUpperCase(c));
            Condition.sleep(getShortDelay());
        }
        Logger.log("Spelled out: " + text);
    }
}
