package tasks;

import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;
import static main.ezMuseumCleaner.*;

public class CollectFinds extends Task {
    private final Rectangle findsPile = new Rectangle(441, 239, 13, 15);

    private final Rectangle instantTapFromDeposit2 = new Rectangle(391, 179, 14, 12);
    private final Rectangle instantTapFromDeposit1 = new Rectangle(366, 195, 12, 13);

    @Override
    public boolean activate() {
        return !Inventory.isFull();
    }

    @Override
    public boolean execute() {
        Paint.setStatus("Collecting finds");
        Logger.log("Collecting uncleaned finds");

        if (Player.tileEquals(currentLocation, depositTile)) {
            instantTapFromDeposit(instantTapFromDeposit1);
        } else if (Player.tileEquals(currentLocation, depositTile2)) {
            instantTapFromDeposit(instantTapFromDeposit2);
        } else if (!Player.tileEquals(currentLocation, collectTile)) {
            stepToCollectPile();
        }

        collectFinds();
        return true;
    }

    private void instantTapFromDeposit(Rectangle tapRect) {
        Client.tap(instantTapFromDeposit1);
        Condition.sleep(generateRandomDelay(600, 900));
        currentLocation = collectTile;
    }

    private void instantTapFromDeposit2() {
        Client.tap(instantTapFromDeposit2);
        Condition.sleep(generateRandomDelay(600, 900));
        currentLocation = collectTile;
    }

    private void stepToCollectPile() {
        Logger.debugLog("Stepping to collect pile");
        Walker.step(collectTile, museumRegion);
        currentLocation = collectTile;
    }

    private void collectFinds() {
        while (!Inventory.isFull() && Player.atTile(collectTile) && (hopEnabled && useWDH && !Game.isPlayersAround()) && !Script.isScriptStopping() && !Script.isTimeForBreak()) {
            Logger.debugLog("Collecting finds!");
            Client.tap(findsPile);
            Condition.sleep(generateRandomDelay(200, 500));
        }
    }
}
