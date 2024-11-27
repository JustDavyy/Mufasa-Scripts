package tasks;

import helpers.utils.ItemList;
import helpers.utils.Spells;
import main.dTeleAlcher;
import utils.Task;

import java.util.Random;

import static helpers.Interfaces.*;
import static main.dTeleAlcher.itemID;
import static tasks.CheckForItems.checkedForItems;

public class PerformTeleAlching extends Task {

    private final Random random = new Random();

    public boolean activate() {
        return checkedForItems;
    }

    @Override
    public boolean execute() {
        if (!Inventory.contains(itemID, 0.69) && Inventory.contains(ItemList.LAW_RUNE_563, 0.69) && GameTabs.isInventoryTabOpen()) {
            Logger.log("Ran out of items to alch or law runes, stopping script");
            Logout.logout();
            Script.stop();
            return true;
        }

        // Open the Magic tab if it is not already open
        if (!GameTabs.isMagicTabOpen()) {
            Logger.log("Opening Magic tab");
            GameTabs.openMagicTab();
            Condition.wait(GameTabs::isMagicTabOpen, 100, 25);
        }

        // Tap alchemy spell based on the chosen teleport spell
        switch (dTeleAlcher.teleport) {
            case "Camelot teleport":
            case "Ardougne teleport":
                Logger.log("Pressing High Alchemy spell");
                Magic.castSpell(Spells.HIGH_LEVEL_ALCHEMY);
                Condition.wait(GameTabs::isInventoryTabOpen, 100, 40);
                break;
            case "Camelot teleport - Low Alchemy":
            case "Varrock teleport":
                Logger.log("Pressing Low Alchemy spell");
                Magic.castSpell(Spells.LOW_LEVEL_ALCHEMY);
                Condition.wait(GameTabs::isInventoryTabOpen, 100, 40);
                break;
        }

        // Tap the item in the Inventory
        if (GameTabs.isInventoryTabOpen()) {
            Logger.log("Pressing item in inventory");
            Inventory.tapItem(itemID, true, 0.69);
            // Need to wait for magic tab to open again, repeating the process
            Condition.wait(GameTabs::isMagicTabOpen, 100, 40);
        }

        // Press the chosen teleport spell
        switch (dTeleAlcher.teleport) {
            case "Camelot teleport - Low Alchemy":
            case "Camelot teleport":
                Logger.log("Teleporting");
                Magic.castSpell(Spells.CAMELOT_TELEPORT);
                break;
            case "Ardougne teleport":
                Logger.log("Teleporting");
                Magic.castSpell(Spells.ARDOUGNE_TELEPORT);
                break;
            case "Varrock teleport":
                Logger.log("Teleporting");
                Magic.castSpell(Spells.VARROCK_TELEPORT);
                break;
        }

        XpBar.getXP();
        Condition.sleep(generateDelay(1800,2000));

        return false;
    }

    private int generateDelay(int lowerEnd, int higherEnd) {
        if (lowerEnd > higherEnd) {
            // Swap lowerEnd and higherEnd if lowerEnd is greater
            int temp = lowerEnd;
            lowerEnd = higherEnd;
            higherEnd = temp;
        }
        return random.nextInt(higherEnd - lowerEnd + 1) + lowerEnd;
    }
}
