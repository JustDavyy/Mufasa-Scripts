package dOttos.Tasks;

import helpers.utils.ItemList;
import helpers.utils.UITabs;
import dOttos.Task;
import dOttos.dOttos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static helpers.Interfaces.*;

public class Drop extends Task {
    dOttos main;
    private static final List<Integer> ITEMS_TO_DROP = Arrays.asList(ItemList.LEAPING_TROUT_11328, ItemList.LEAPING_SALMON_11330, ItemList.LEAPING_STURGEON_11332, ItemList.ROE_11324, ItemList.FISH_OFFCUTS_11334, ItemList.CAVIAR_11326);
    private static int fishingXP = 0;
    private static int agilityXP = 0;
    private static int strengthXP = 0;
    public Drop(dOttos main) {
        super();
        super.name = "Drop";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return (Inventory.isFull());
    }

    @Override //the code to execute if criteria met
    public boolean execute() {
        Paint.setStatus("Update paint");
        GameTabs.openTab(UITabs.INVENTORY);
        updatePaint();
        Paint.setStatus("Dropping");
        Logger.debugLog("We should start dropping fish.");
        dropFish();
        dOttos.doneDropping = true;
        return false;
    }

    private void dropFish() {
        Logger.log("Dropping fish");

        if (!Game.isTapToDropEnabled()) {
            if (Chatbox.findChatboxMenu() != null) {
                Logger.log("Chatbox message is open, clicking it away first!");
                Client.sendKeystroke("KEYCODE_SPACE");
                Condition.sleep(500);
            }
            Logger.log("Enabling tap to drop");
            Game.enableTapToDrop();
            Condition.wait(() -> Game.isTapToDropEnabled(), 100, 20);
        }

        // Shuffle the list to randomize the drop order
        Collections.shuffle(ITEMS_TO_DROP);

        // Drop the items in the randomized order
        for (Integer itemId : ITEMS_TO_DROP) {
            dropItems(itemId);
        }
    }

    private void dropItems(int itemId) {
        Inventory.tapAllItems(itemId, 0.80);
    }

    private void updatePaint() {
        // Update the number of fish caught
        dOttos.troutAmount += Inventory.count(ItemList.LEAPING_TROUT_11328, 0.8);
        dOttos.salmonAmount += Inventory.count(ItemList.LEAPING_SALMON_11330, 0.8);
        dOttos.sturgeonAmount += Inventory.count(ItemList.LEAPING_STURGEON_11332, 0.8);

        // Update caught fish amounts in the paint
        Paint.updateBox(dOttos.troutIndex, dOttos.troutAmount);
        Paint.updateBox(dOttos.salmonIndex, dOttos.salmonAmount);
        Paint.updateBox(dOttos.sturgeonIndex, dOttos.sturgeonAmount);

        // Calculate XP for fishing, agility, and strength
        fishingXP = dOttos.troutAmount * 50 + dOttos.salmonAmount * 70 + dOttos.sturgeonAmount * 80;
        agilityXP = dOttos.troutAmount * 5 + dOttos.salmonAmount * 6 + dOttos.sturgeonAmount * 7;
        strengthXP = dOttos.troutAmount * 5 + dOttos.salmonAmount * 6 + dOttos.sturgeonAmount * 7;

        // Update XP distribution in the paint
        Paint.updateBox(dOttos.fishxpIndex, fishingXP);
        Paint.updateBox(dOttos.agilxpIndex, agilityXP);
        Paint.updateBox(dOttos.strengthxpIndex, strengthXP);
    }
}
