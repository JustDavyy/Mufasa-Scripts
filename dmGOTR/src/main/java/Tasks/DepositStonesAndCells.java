package Tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import utils.StateUpdater;
import utils.Task;

import java.awt.*;

import static Tasks.PreGame.INSIDE_AREA;
import static helpers.Interfaces.*;
import static main.dmGOTR.*;

public class DepositStonesAndCells extends Task {
    private final StateUpdater stateUpdater;
    public DepositStonesAndCells(StateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    // TILES
    private static final Tile MIDDLE_GUARDIAN_TILE = new Tile(14459, 37749, 0);

    // RECTANGLES
    private static final Rectangle MIDDLE_GUARDIAN_TAP_RECT = new Rectangle(437, 197, 20, 22);
    private static final Rectangle DEPOSIT_CELL_FROM_GUARDIAN_TAP_RECT = new Rectangle(546, 233, 5, 7);

    @Override
    public boolean activate() {
        return Player.isTileWithinArea(currentLocation, INSIDE_AREA) &&
                (Inventory.contains(ItemList.WEAK_CELL_26883, 0.8)
                || Inventory.contains(ItemList.ELEMENTAL_GUARDIAN_STONE_26881, 0.8)
                || Inventory.contains(ItemList.CATALYTIC_GUARDIAN_STONE_26880, 0.8)
                || Inventory.contains(ItemList.POLYELEMENTAL_GUARDIAN_STONE_26941, 0.8));
    }

    @Override
    public boolean execute() {

        setStatusAndDebugLog("Deposit stone/cell");

        // Step to workbench if needed
        if (!Player.tileEquals(currentLocation, MIDDLE_GUARDIAN_TILE)) {
            Walker.step(MIDDLE_GUARDIAN_TILE);
        }

        // Only run code if we're at the workbench
        if (Player.atTile(MIDDLE_GUARDIAN_TILE)) {
            Client.tap(MIDDLE_GUARDIAN_TAP_RECT);
            Condition.wait(() -> !Inventory.contains(ItemList.ELEMENTAL_GUARDIAN_STONE_26881, 0.8)
                    && !Inventory.contains(ItemList.CATALYTIC_GUARDIAN_STONE_26880, 0.8)
                    && !Inventory.contains(ItemList.POLYELEMENTAL_GUARDIAN_STONE_26941, 0.8), 100, 400);

            // Deposit cells
            Client.tap(DEPOSIT_CELL_FROM_GUARDIAN_TAP_RECT);
            Condition.wait(() -> !Inventory.contains(ItemList.WEAK_CELL_26883, 0.8)
                    && !Inventory.contains(ItemList.MEDIUM_CELL_26884, 0.8)
                    && !Inventory.contains(ItemList.STRONG_CELL_26885, 0.8)
                    && !Inventory.contains(ItemList.OVERCHARGED_CELL_26886, 0.8), 100, 200);
        } else {
            return false;
        }



        return false;
    }

    private void craftAndFillPouches() {
        // Loop here to fill all pouches and craft again till we are done
    }
}
