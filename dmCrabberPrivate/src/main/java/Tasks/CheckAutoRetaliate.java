package Tasks;

import utils.Task;

import static helpers.Interfaces.*;

public class CheckAutoRetaliate extends Task {
    boolean checkedRetaliate;

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
            if (Player.isAutoRetaliateOn()) {
                Logger.log("Auto retaliate already enabled, continuing..");
                checkedRetaliate = true;
                return true;
            } else {
                Logger.log("Activating auto retaliate");
                Player.enableAutoRetaliate();
                Condition.wait(() -> Player.isAutoRetaliateOn(), 200, 10);
                if (Player.isAutoRetaliateOn()) {
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
