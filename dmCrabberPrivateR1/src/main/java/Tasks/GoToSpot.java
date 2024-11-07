package Tasks;

import utils.Task;
import static helpers.Interfaces.*;
import static main.dmCrabberPrivate.*;



import main.dmCrabberPrivate;

public class GoToSpot extends Task {


    @Override
    public boolean activate() {
        return !Player.tileEquals(currentLocation, spot.getSpotTile()) && Inventory.contains(foodID, 0.80);
    }
    

    @Override
    public boolean execute() {
        Logger.debugLog("Walking to crab spot.");

        if (!Player.tileEquals(currentLocation, spot.getSpotTile()) && !Walker.isReachable(spot.getSpotTile())) {
            Walker.webWalk(spot.getSpotTile(), () -> {
                if (!GameTabs.isInventoryTabOpen()) {
                    GameTabs.openInventoryTab();            
                }

                if (GameTabs.isInventoryTabOpen()) {
                    dmCrabberPrivate.TakeGearOn();            
                }

            },true);
            Condition.sleep(generateRandomDelay(2500, 3000));
            currentLocation = Walker.getPlayerPosition();
        }

        if (!Player.tileEquals(currentLocation, spot.getSpotTile())) {
            Walker.step(spot.getSpotTile(), () -> {
                if (!GameTabs.isInventoryTabOpen()) {
                    GameTabs.openInventoryTab();            
                }
                
                if (GameTabs.isInventoryTabOpen()) {
                    dmCrabberPrivate.TakeGearOn();            
                }
            });
            Condition.sleep(generateRandomDelay(1500, 2250));
            currentLocation = Walker.getPlayerPosition();
            return true;
        }

        return false;
    }
}
