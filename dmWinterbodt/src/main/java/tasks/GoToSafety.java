package tasks;

import utils.StateUpdater;
import utils.Task;

import static helpers.Interfaces.*;
import static main.dmWinterbodt.*;

public class GoToSafety extends Task {
    @Override
    public boolean activate() {
        StateUpdater.updateIsGameGoing();

        Logger.debugLog("food in invent: " + foodAmountInInventory);

        //Logger.debugLog("Inside GoToSafety activate()");
        return foodAmountInInventory == 0 && isGameGoing && Player.isTileWithinArea(currentLocation, insideArea) && !Player.isTileWithinArea(currentLocation, lobby) && !Player.isTileWithinArea(currentLocation, insideDoorArea);
    }

    @Override
    public boolean execute() {
        Logger.debugLog("Inside GoToSafety execute()");
        Logger.log("We're out of food, heading to the lobby for safety and waiting till the game ends.");

        // Walk towards the door
        Walker.walkPath(WTRegion, gameToWTDoor);
        Condition.wait(() -> Player.within(atDoor, WTRegion), 100, 20);
        currentLocation = Walker.getPlayerPosition(WTRegion);
        return false;
    }

}
