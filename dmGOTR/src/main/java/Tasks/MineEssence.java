package Tasks;

import helpers.utils.Area;
import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.StateUpdater;
import utils.Task;

import static Tasks.PreGame.AGILITY_OUTSIDE_TILE;
import static Tasks.PreGame.INSIDE_AREA;
import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class MineEssence extends Task {
    private final StateUpdater stateUpdater;
    public MineEssence(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    // AREAS
    static final Area LARGE_REMAINS_MINING_AREA = new Area(
            new Tile(14544, 37805, 0),
            new Tile(14590, 37697, 0)
    );
    static final Area HUGE_REMAINS_MINING_AREA = new Area(
            new Tile(14337, 37809, 0),
            new Tile(14376, 37708, 0)
    );

    // Integers
    private static int tempFragmentHolder = 0;

    // BOOLEANS
    private static boolean doneFillingPouches = false;
    private static boolean startedFillingPouches = false;
    private static boolean startedMiningFragments = false;

    // LONGS
    private static long lastFragmentGaintime = 0;

    @Override
    public boolean activate() {
        return Player.isTileWithinArea(currentLocation, LARGE_REMAINS_MINING_AREA) || Player.isTileWithinArea(currentLocation, HUGE_REMAINS_MINING_AREA) || Player.tileEquals(currentLocation, GUARDIAN_PARTS_TILE);
    }

    @Override
    public boolean execute() {

        // Logic when inside the usual lower mining area
        if (Player.isTileWithinArea(currentLocation, LARGE_REMAINS_MINING_AREA)) {
            setStatusAndDebugLog("Large remains logic");

            // Check if we are at the mining tile, if not move there
            if (!startedMiningFragments) {
                if (!Player.tileEquals(currentLocation, LARGE_GUARDIAN_REMAINS_TILE)) {
                    Walker.step(LARGE_GUARDIAN_REMAINS_TILE);
                }
            }

            // Check if game has started yet
            if (!stateUpdater.isGameGoing()) {
                return false; // Early exit, game is not going yet
            }

            // Game should be going when we reach this part, let's mine

            // Check if we're already done mining
            if (startedMiningFragments) {
                if (Inventory.stackSize(ItemList.GUARDIAN_FRAGMENTS_26878) >= fragmentsToMine) {
                    setStatusAndDebugLog("Fragments goal reached");
                    Client.tap(LARGE_GUARDIAN_AGILITYSHORTCUT_FROMREMAINS_TAP_RECT);
                    Condition.wait(() -> Player.atTile(AGILITY_OUTSIDE_TILE), 100, 100);

                    if (Player.atTile(AGILITY_OUTSIDE_TILE)) {
                        startedMiningFragments = false;
                        lastFragmentGaintime = 0;
                        tempFragmentHolder = 0;
                        Walker.walkPath(agilShortcutToWorkbench);
                        Player.waitTillNotMoving(10);
                        Walker.step(WORKBENCH_TILE);
                        readyToCraftEssences = true;
                        return false;
                    }
                }
            }

            // Mining checks
            if (!startedMiningFragments) {
                setStatusAndDebugLog("Start mining");
                Client.tap(LARGE_GUARDIAN_REMAINS_ATREMAINS_TAP_RECT);
                startedMiningFragments = true;
                Condition.wait(() -> Inventory.contains(ItemList.GUARDIAN_FRAGMENTS_26878, 0.7), 100, 100);
                lastFragmentGaintime = System.currentTimeMillis();
                tempFragmentHolder = Inventory.stackSize(ItemList.GUARDIAN_FRAGMENTS_26878);
            } else { // Already mining, check if we need to re-initiate
                setStatusAndDebugLog("Check if mining");
                if (System.currentTimeMillis() - lastFragmentGaintime > 10000) { // Check every 10 seconds
                    if (Inventory.stackSize(ItemList.GUARDIAN_FRAGMENTS_26878) == tempFragmentHolder) {
                        setStatusAndDebugLog("Re-initiate mining");
                        Client.tap(LARGE_GUARDIAN_REMAINS_ATREMAINS_TAP_RECT);
                        Condition.sleep(4000, 6000);
                        lastFragmentGaintime = System.currentTimeMillis();
                        tempFragmentHolder = Inventory.stackSize(ItemList.GUARDIAN_FRAGMENTS_26878);
                    } else {
                        lastFragmentGaintime = System.currentTimeMillis();
                    }
                } else {
                    Condition.sleep(1000, 2000);
                    return false;
                }
            }

        }

        // Logic when using the upper mining area
        if (Player.tileEquals(currentLocation, GUARDIAN_PARTS_TILE)) {
            setStatusAndDebugLog("Guardian parts logic");

            // Check if we are at the mining tile, if not move there
            if (!Player.tileEquals(currentLocation, GUARDIAN_PARTS_TILE)) {
                Walker.step(GUARDIAN_PARTS_TILE);
            }

            // Check if game has started yet
            if (!stateUpdater.isGameGoing()) {
                return false; // Early exit, game is not going yet
            }

            // Game should be going when we reach this part, let's mine

            // Check if we're already done mining
            if (startedMiningFragments) {
                if (Inventory.stackSize(ItemList.GUARDIAN_FRAGMENTS_26878) >= fragmentsToMine) {
                    setStatusAndDebugLog("Fragments goal reached");
                    setStatusAndDebugLog("Go to workbench");
                    Walker.step(WORKBENCH_TILE);
                    startedMiningFragments = false;
                    lastFragmentGaintime = 0;
                    tempFragmentHolder = 0;
                    readyToCraftEssences = true;
                    return false;
                }
            }

            // Mining checks
            if (!startedMiningFragments) {
                setStatusAndDebugLog("Start mining");
                Client.tap(GUARDIAN_PARTS_TAP_RECT);
                startedMiningFragments = true;
                Condition.wait(() -> Inventory.contains(ItemList.GUARDIAN_FRAGMENTS_26878, 0.7), 100, 100);
                lastFragmentGaintime = System.currentTimeMillis();
                tempFragmentHolder = Inventory.stackSize(ItemList.GUARDIAN_FRAGMENTS_26878);
            } else { // Already mining, check if we need to re-initiate
                setStatusAndDebugLog("Check if mining");
                if (System.currentTimeMillis() - lastFragmentGaintime > 10000) { // Check every 10 seconds
                    if (Inventory.stackSize(ItemList.GUARDIAN_FRAGMENTS_26878) == tempFragmentHolder) {
                        setStatusAndDebugLog("Re-initiate mining");
                        Client.tap(GUARDIAN_PARTS_TAP_RECT);
                        Condition.sleep(4000, 6000);
                        lastFragmentGaintime = System.currentTimeMillis();
                        tempFragmentHolder = Inventory.stackSize(ItemList.GUARDIAN_FRAGMENTS_26878);
                    } else {
                        lastFragmentGaintime = System.currentTimeMillis();
                    }
                } else {
                    Condition.sleep(1000, 2000);
                    return false;
                }
            }

        }

        // Logic when inside the portal mining area (HUGE GUARDIAN)
        if (Player.isTileWithinArea(currentLocation, HUGE_REMAINS_MINING_AREA)) {
            setStatusAndDebugLog("Huge remains logic");

            // Check game is still going, if not leave area
            if (!stateUpdater.isGameGoing()) {
                setStatusAndDebugLog("Leave portal mining area");
                if (Player.atTile(HUGE_GUARDIAN_REMAINS_TILE)) {
                    Client.tap(PORTAL_TAP_RECT_FROM_HUGEREMAINS);
                } else if (Player.atTile(HUGE_REMAINS_PORTAL_TILE)) {
                    Client.tap(PORTAL_TAP_RECT_ATPORTAL_HUGEREMAINS);
                } else {
                    Walker.step(HUGE_REMAINS_PORTAL_TILE);
                    Client.tap(PORTAL_TAP_RECT_ATPORTAL_HUGEREMAINS);
                }

                // Wait till we are inside game area again
                Condition.wait(() -> Player.within(INSIDE_AREA), 100, 75);
                return false; // Early exit
            }

            // If we're here, game is still going and we need to mine essence

            // Start mining essence
            if (Player.tileEquals(currentLocation, HUGE_REMAINS_PORTAL_TILE)) {
                Client.tap(HUGE_GUARDIAN_REMAINS_FROMPORTAL_TAP_RECT);
            } else if (Player.tileEquals(currentLocation, HUGE_GUARDIAN_REMAINS_TILE)) {
                Client.tap(HUGE_GUARDIAN_REMAINS_ATREMAINS_TAP_RECT);
            } else {
                Walker.step(HUGE_GUARDIAN_REMAINS_TILE);
                Client.tap(HUGE_GUARDIAN_REMAINS_ATREMAINS_TAP_RECT);
            }

            // Wait till full inventory
            Condition.wait(() -> Inventory.isFull(), 100, 250);

            // Logic here if we're using pouches
            if (usePouches) {
                // Do stuff here
                startedFillingPouches = true;

                return false; // early exit not to user the other logic
            }

            // Logic here if we're not using pouches
            if (Inventory.isFull()) {
                if (Player.atTile(HUGE_GUARDIAN_REMAINS_TILE)) {
                    Client.tap(PORTAL_TAP_RECT_FROM_HUGEREMAINS);
                } else if (Player.atTile(HUGE_REMAINS_PORTAL_TILE)) {
                    Client.tap(PORTAL_TAP_RECT_ATPORTAL_HUGEREMAINS);
                } else {
                    Walker.step(HUGE_REMAINS_PORTAL_TILE);
                    Client.tap(PORTAL_TAP_RECT_ATPORTAL_HUGEREMAINS);
                }
                Condition.wait(() -> Player.within(INSIDE_AREA), 100, 75);
                return false; // early exit as we should now be outside
            }
        }

        return false;
    }
}
