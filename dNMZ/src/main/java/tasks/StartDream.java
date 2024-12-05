package tasks;

import helpers.utils.ItemList;
import helpers.utils.Tile;
import helpers.utils.UITabs;
import utils.Task;

import java.awt.*;

import static main.dNMZ.*;
import static helpers.Interfaces.*;

public class StartDream extends Task {

    // Rectangles
    private final Rectangle dominicOnionRect = new Rectangle(449, 215, 8, 17);
    private final Rectangle dominicHardRumbleCheckRect = new Rectangle(332, 84, 79, 52);
    private final Rectangle vialRect = new Rectangle(436, 219, 8, 11);
    private final Rectangle nmzInterfaceOpenCheckRect1 = new Rectangle(130, 157, 17, 15);
    private final Rectangle nmzInterfaceOpenCheckRect2 = new Rectangle(527, 476, 12, 17);
    private final Rectangle acceptStartRect = new Rectangle(441, 348, 74, 20);
    private final Rectangle walkActionVenBow = new Rectangle(764, 20, 17, 14);
    private final Rectangle walkActionNormal = new Rectangle(818, 60, 8, 8);

    @Override
    public boolean activate() {
        // No need to run this task when we are inside, early return false
        if (insideNMZ) {
            return false;
        }

        return restockDone;
    }

    @Override
    public boolean execute() {

        Logger.log("Starting a new dream.");

        // First move to Dominic
        if (!Player.atTile(dominicOnionTile)) {
            Logger.debugLog("Moving to Dominic Onion");
            if (Walker.isReachable(dominicOnionTile)) {
                Walker.step(dominicOnionTile);
            } else {
                Walker.webWalk(dominicOnionTile);
                Player.waitTillNotMoving(20);
                Walker.step(dominicOnionTile);
            }
        }

        if (Player.atTile(dominicOnionTile)) {
            if (!dominicMESDone) {
                Logger.debugLog("Checking MES setup for Dominic Onion");
                Client.longPress(dominicOnionRect);
                handleDominicMES();
            } else {
                Client.tap(dominicOnionRect);
            }
            Condition.wait(() -> Chatbox.isMakeMenuVisible(), 100, 50);

            if (Chatbox.isMakeMenuVisible()) {
                Condition.wait(this::hardRumbleAvailable, 100, 50);
                if (hardRumbleAvailable()) {
                    Logger.debugLog("Hard rumble option present, starting dream.");
                    startDream();
                } else {
                    Logger.log("Hard rumble option not found, make sure to set up a hard rumble dream once before starting the script.");
                    Logger.log("Stopping script!");
                    Script.stop();
                }
            } else {
                Logger.debugLog("Chatbox does not seem to be open, failed to 'dream' at dominic? Early exit!");
                return false;
            }
        } else {
            Logger.debugLog("Failed to move to Dominic Onion, early exit startDream task.");
            return false;
        }

        // Enter the dream we just started
        if (dreamStarted) {
            // Check if we need to set up rock cake MES done
            if (HPMethod.equals("Rock cake")) {
                if (!rockcakeMESDone) {
                    Logger.debugLog("Checking if MES is setup correctly for Rock cake");
                    handleRockCakeMES();
                }
            }
            enterDream();
        } else {
            Logger.debugLog("Failed to start dream, early exit task.");
            return false;
        }

        // Reset restock boolean (only do this after we are inside NMZ though
        if (insideNMZ) {
            restockDone = false;
            dreamStarted = false;
        }

        return true;
    }

    private void enterDream() {
        Logger.log("Entering new dream.");
        // Go to vial outside
        ensurePlayerAtTile(vialOutsideTile);

        if (Player.atTile(vialOutsideTile)) {
            if (!Player.isRunEnabled()) {
                Player.toggleRun();
                Condition.sleep(generateDelay(500, 1000));
            }
            // Tap the vial
            Client.tap(vialRect);

            // Wait for the interface to be open
            Condition.wait(this::startNMZInterfaceOpen, 100 ,50);

            // Only continue if interface is open
            if (startNMZInterfaceOpen()) {
                Client.tap(acceptStartRect);
                Condition.sleep(generateDelay(8000, 10000));
                // We should now be inside the NMZ Arena
            }
        } else {
            Logger.debugLog("Failed to walk towards vial outside, early exit.");
            return;
        }

        if (areWeInsideNMZ()) {
            insideNMZ = true;
            // Successfully joined the dream, initial quick setup!
            if (usingVenatorBow) {
                Client.tap(walkActionVenBow);
            } else {
                Client.tap(walkActionNormal);
            }
            handleStartOfDream();
        } else {
            Logger.debugLog("Player not within NMZ arena, something went wrong. Early exit!");
        }
    }

    private void handleStartOfDream() {
        // Drink offensive potions if configured
        if (potions != null && !potions.isEmpty()) {
            Logger.debugLog("Drinking offensive potion: " + potions);
            drinkOffensivePot();
        }

        // Stock up on absorption levels if Absorption is the chosen method
        if ("Absorption".equals(NMZMethod)) {
            Logger.debugLog("Stocking up on absorption levels.");
            stockAbsorption(); // Consume 3 absorption potions
        }

        // Enable prayers if using Prayer method
        if (NMZMethod.startsWith("Prayer")) {
            Logger.debugLog("Enabling prayers.");
            enablePrayer();
        }

        // Manage HP reduction if using Rock Cake or Locator Orb
        if ("Absorption".equals(NMZMethod)) {
            int initialHP = Player.getHP();

            if (initialHP > 50 && !HPMethod.equals("None")) {
                Logger.debugLog("Lowering HP.");
                reduceHP();
            }
        }
    }

    private void stockAbsorption() {
        for (int i = 0; i < 3; i++) {
            Rectangle itemRect = Inventory.findItem(ItemList.ABSORPTION_4_11734, 0.75, absorbPotColor);
            if (itemRect != null) {
                for (int taps = 0; taps < 4; taps++) { // Each potion provides 4 taps worth of absorption
                    Client.tap(itemRect);
                    Condition.sleep(generateDelay(400, 600));
                }
            }
        }
    }

    private void enablePrayer() {
        GameTabs.openTab(UITabs.PRAYER);
        Condition.sleep(generateDelay(1250, 2000));

        // Activate Protect from Melee
        Prayer.activateProtectfromMelee(); // Quick prayers
        Condition.sleep(generateDelay(750, 1250));
        if (!Prayer.isActiveProtectfromMelee()) {
            Prayer.activateProtectfromMelee();
            Condition.sleep(generateDelay(750, 1250));
        }

        // Activate offensive prayers
        switch (NMZMethod) {
            case "Prayer - Ultimate Strength":
                Prayer.activateUltimateStrength();
                break;
            case "Prayer - Eagle Eye":
                Prayer.activateEagleEye();
                break;
            case "Prayer - Mystic Might":
                Prayer.activateMysticMight();
                break;
            case "Prayer - Chivalry":
                Prayer.activateChivalry();
                break;
            case "Prayer - Piety":
                Prayer.activatePiety();
                break;
            case "Prayer - Rigour":
                Prayer.activateRigour();
                break;
            case "Prayer - Augury":
                Prayer.activateAugury();
                break;
            default:
                Logger.debugLog("No offensive prayer was selected.");
        }
    }

    private boolean areWeInsideNMZ() {
        return Player.within(NMZArena);
    }

    private void startDream() {
        Client.sendKeystroke("4"); // Previous: Customisable Rumble (hard) line
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("space"); // Tap here to continue line
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("1"); // Yes option line
        Condition.sleep(generateDelay(1000,1300));
        Client.sendKeystroke("space"); // Tap here to continue line
        Condition.wait(() -> !Chatbox.isMakeMenuVisible(), 100, 50);
        dreamStarted = true;
    }

    private boolean hardRumbleAvailable() {
        return Chatbox.isTextVisible(dominicHardRumbleCheckRect, dominicChatTextColors, dominicChatPatterns, "hard");
    }

    private boolean startNMZInterfaceOpen() {
        boolean check1 = Client.isColorInRect(Color.decode("#0e0e0c"), nmzInterfaceOpenCheckRect1, 10);
        boolean check2 = Client.isColorInRect(Color.decode("#0e0e0c"), nmzInterfaceOpenCheckRect2, 10);

        return check1 && check2;
    }

    private void reduceHP() {
        while (Player.getHP() > 1) {
            switch (HPMethod) {
                case "Locator orb":
                    Inventory.tapItem(ItemList.LOCATOR_ORB_22081, true, 0.8);
                    break;
                case "Rock cake":
                    if (!rockcakeMESDone) {
                        handleRockCakeMES();
                    }
                    Inventory.tapItem(ItemList.DWARVEN_ROCK_CAKE_7510, true, 0.8);
                    break;
                default:
                    Logger.debugLog("No HP method selected.");
            }
            Condition.sleep(generateDelay(200, 400)); // Short delay between uses
        }
    }

    // MENU ENTRY SWAPPER STUFF
    private void handleDominicMES() {
        if (!isDreamTopMost()) {
            if (enableMES()) {
                if (swapDreamMESOptions()) {
                    dominicMESDone = true;
                    tapDreamOption();
                }
            }
        } else {
            dominicMESDone = true;
            tapDreamOption();
        }
    }

    private boolean swapDreamMESOptions() {
        Rectangle talkToOption;
        Rectangle dreamOption;
        Rectangle mesEnabled;

        Logger.debugLog("Finding the talk-to option.");
        talkToOption = Objects.getBestMatch("/imgs/talk-to.png", 0.8);

        Logger.debugLog("Finding the dream option.");
        dreamOption = Objects.getBestMatch("/imgs/dream.png", 0.8);

        // Log the results of finding the options
        if (talkToOption != null) {
            Logger.debugLog("Talk-to option found: " + talkToOption);
        } else {
            Logger.debugLog("Talk-to option was NOT FOUND.");
        }

        if (dreamOption != null) {
            Logger.debugLog("Dream option found: " + dreamOption);
        } else {
            Logger.debugLog("Dream option was NOT FOUND.");
        }

        // Ensure the dream option is the topmost
        if (dreamOption != null) {
            Logger.debugLog("Ensuring the dream option is the topmost.");

            // Determine the current topmost option

            // Swap if the dream option is not the topmost
            if (talkToOption != null && dreamOption.y > talkToOption.y) {
                Logger.debugLog("Dream option is not the topmost. Swapping with the current topmost option.");
                Logger.debugLog("Topmost option: " + talkToOption);
                Rectangle dreamOptionShort = new Rectangle(dreamOption.x, dreamOption.y, 15, dreamOption.height);
                Rectangle topmostOptionShort = new Rectangle(talkToOption.x, talkToOption.y, 15, talkToOption.height);
                Client.drag(dreamOptionShort, topmostOptionShort, 500);
                Condition.sleep(1500);
            } else {
                Logger.debugLog("Dream option is already the topmost. No action needed.");
            }
        }

        // Check if the MES option menu is enabled and disable it if necessary
        mesEnabled = Objects.getBestMatch("/imgs/enabled-mes.png", 0.8);

        if (mesEnabled != null) {
            Logger.debugLog("Disabling MES option menu.");
            Client.tap(mesEnabled);
            Condition.sleep(generateDelay(500, 1000));
            return true;
        }

        return false;
    }

    private boolean isDreamTopMost() {
        Logger.debugLog("Checking if the dream option is the topmost...");

        // Find the options
        Rectangle talkToOption = Objects.getBestMatch("/imgs/checktalk-to.png", 0.8);
        Rectangle dreamOption = Objects.getBestMatch("/imgs/checkdream.png", 0.8);

        // Log the options found
        if (talkToOption != null) {
            Logger.debugLog("Talk-to option found: " + talkToOption);
        } else {
            Logger.debugLog("Talk-to option was NOT FOUND.");
        }

        if (dreamOption != null) {
            Logger.debugLog("Dream option found: " + dreamOption);
        } else {
            Logger.debugLog("Dream option was NOT FOUND.");
            return false; // If dreamOption is not found, it cannot be the topmost
        }

        // Determine the topmost option
        Rectangle topmostOption = dreamOption; // Start assuming dream is topmost
        if (talkToOption != null && talkToOption.y < topmostOption.y) {
            topmostOption = talkToOption;
        }

        // Check if dreamOption is the topmost
        boolean isTopMost = topmostOption == dreamOption;
        if (isTopMost) {
            Logger.debugLog("The dream option is already the topmost.");
        } else {
            Logger.debugLog("The dream option is NOT the topmost.");
        }

        return isTopMost;
    }

    private void tapDreamOption() {
        Logger.debugLog("Attempting to locate and tap the dream option...");

        // Locate the dream option
        Rectangle dreamOption = Objects.getBestMatch("/imgs/checkdream.png", 0.8);

        if (dreamOption != null) {
            Logger.debugLog("Dream option located: " + dreamOption);
            Client.tap(dreamOption);
            Logger.debugLog("Dream option tapped successfully.");
        } else {
            Logger.debugLog("Dream option was NOT FOUND.");
        }
    }
}