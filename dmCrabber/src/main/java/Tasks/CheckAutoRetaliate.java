package Tasks;

import utils.Task;

import java.awt.*;

import static helpers.Interfaces.*;

public class CheckAutoRetaliate extends Task {
    boolean checkedRetaliate;
    Rectangle autoRetaliateRect = new Rectangle(67, 377, 172, 40);
    Color retaliateActiveColor = Color.decode("#831f1d");


    @Override
    public boolean activate() {
        return !checkedRetaliate;
    }

    @Override
    public boolean execute() {
        Logger.log("Checking Auto retaliate!");
        if (!GameTabs.isCombatTabOpen()) {
            GameTabs.openCombatTab();
            Condition.wait(() -> GameTabs.isCombatTabOpen(), 100, 20);
        }

        if (GameTabs.isCombatTabOpen()) {
            if (Client.isColorInRect(retaliateActiveColor, autoRetaliateRect, 2)) {
                Logger.log("Auto retaliate already enabled, continuing..");
                checkedRetaliate = true;
                return true;
            } else {
                Logger.log("Activating auto retaliate");
                Client.tap(autoRetaliateRect);
                if (Client.isColorInRect(retaliateActiveColor, autoRetaliateRect, 2)) {
                    checkedRetaliate = true;
                    return true;
                } else {
                    Logger.log("We failed to activate auto retaliate, stopping script");
                    Script.stop();
                }
            }
        }

        return false;
    }
}
