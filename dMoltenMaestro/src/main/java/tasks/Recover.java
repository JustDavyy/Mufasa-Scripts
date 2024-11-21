package tasks;

import utils.Task;

import static helpers.Interfaces.*;
import static main.dMoltenMaestro.*;

public class Recover extends Task {

    @Override
    public boolean activate() {
        Logger.debugLog("Checking recovery activation logic. (no other task activated)");
        currentLocation = Walker.getPlayerPosition();

        switch (location) {
            case "Edgeville":
                if (Player.tileEquals(currentLocation, edgeFurnaceTile)) {
                    Walker.step(edgeBankTile);
                } else if (Player.tileEquals(currentLocation, edgeBankTile)) {
                    Client.tap(edgeFurnaceRect);
                    Condition.wait(() -> Player.atTile(edgeFurnaceTile), 100, 75);
                    currentLocation = edgeFurnaceTile;
                } else {
                    if (!Walker.isReachable(edgeFurnaceTile)) {
                        Walker.webWalk(edgeFurnaceTile);
                    }
                    Walker.step(edgeFurnaceTile);
                }
                break;
            case "Mount Karuulm":
                if (Player.tileEquals(currentLocation, karuulmFurnaceTile)) {
                    Walker.step(karuulmBankTile);
                } else {
                    if (!Walker.isReachable(karuulmFurnaceTile)) {
                        Walker.webWalk(karuulmFurnaceTile);
                    }
                    Walker.step(karuulmFurnaceTile);
                    currentLocation = karuulmFurnaceTile;
                }
                break;
            case "Neitiznot":
                if (Player.tileEquals(currentLocation, neitFurnaceTile)) {
                    Walker.step(neitBankTile);
                } else if (Player.tileEquals(currentLocation, neitBankTile)) {
                    Client.tap(neitFurnaceRect);
                    Condition.wait(() -> Player.atTile(neitFurnaceTile), 100, 75);
                    currentLocation = neitFurnaceTile;
                } else {
                    if (!Walker.isReachable(neitFurnaceTile)) {
                        Walker.webWalk(neitFurnaceTile);
                    }
                    Walker.step(neitFurnaceTile);
                }
                break;
            default:
                Logger.log("Unknown location: " + location);
                Script.stop();
                break;
        }

        return false;
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Recovery activated");
        return true;
    }
}
