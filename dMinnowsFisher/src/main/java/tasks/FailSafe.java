package tasks;

import helpers.utils.Tile;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dMinnowsFisher.*;
import java.util.concurrent.ThreadLocalRandom;

public class FailSafe extends Task {

    private static final Tile westTile = new Tile(10439, 13517, 0);
    private static final Tile eastTile = new Tile(10475, 13517, 0);

    @Override
    public boolean activate() {
        return !Player.isTileWithinArea(playerPosition, minnowPlatform);
    }

    @Override
    public boolean execute() {

        Logger.log("Player not on the minnow platform!");
        Paint.setStatus("Trigger failsafe");

        if (!Player.within(fishingGuild)) {
            Logger.log("Player not within the fishing guild either... stopping script!");
            Logout.logout();
            Script.stop();
        } else {
            Logger.log("Player within the fishing guild, moving back to platform!");

            if (Walker.isReachable(boatTile)) {
                Logger.log("Boat tile is reachable, walking there!");
            } else {
                Logger.log("Boat tile not reachable, web walking there!");
                Walker.webWalk(boatTile);
                Player.waitTillNotMoving(10);

            }
            Walker.step(boatTile);
            Player.waitTillNotMoving(7);
            if (Player.atTile(boatTile)) {
                Client.tap(boatTapRect);
                Condition.sleep(5000, 7500);
            }
        }

        Paint.setStatus("Update player position");
        playerPosition = Walker.getPlayerPosition();

        Paint.setStatus("Go to random side");
        if (ThreadLocalRandom.current().nextBoolean()) {
            Logger.debugLog("Go to west fishing side");
            Walker.walkTo(westTile);
            Player.waitTillNotMoving(7);
        } else {
            Logger.debugLog("Go to east fishing side");
            Walker.walkTo(eastTile);
            Player.waitTillNotMoving(7);
        }

        return false;
    }
}