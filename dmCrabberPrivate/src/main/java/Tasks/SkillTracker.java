package Tasks;

import helpers.utils.Skills;
import utils.Task;

import java.util.Random;

import static helpers.Interfaces.*;
import static main.dmCrabberPrivate.*;

public class SkillTracker extends Task {
    public boolean timeToCheckStats = true;

    private long lastCheckTime = 0;
    private final long MIN_DELAY = 10 * 60 * 1000; // 10 minutes in milliseconds
    private long randomDelay = MIN_DELAY;
    private Random random = new Random();

    @Override
    public boolean activate() {
        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastCheckTime) >= randomDelay) {
            timeToCheckStats = true;
        }

        return timeToCheckStats;
    }

    @Override
    public boolean execute() {
        if (!GameTabs.isStatsTabOpen()) {
            GameTabs.openStatsTab();
            Condition.wait(() -> GameTabs.isStatsTabOpen(), 100, 20);
        }

        if (GameTabs.isStatsTabOpen()) {
            // Once the stats tab is open, retrieve the skill levels
            attackLevel = Stats.getRealLevel(Skills.ATTACK);
            strenghtLevel = Stats.getRealLevel(Skills.STRENGTH);
            defenceLevel = Stats.getRealLevel(Skills.DEFENCE);
            rangeLevel = Stats.getRealLevel(Skills.RANGED);

            // Log or use the skill levels as needed
            Logger.log("Attack Level: " + attackLevel);
            Logger.log("Strength Level: " + strenghtLevel);
            Logger.log("Defence Level: " + defenceLevel);
            Logger.log("Range Level: " + rangeLevel);

            //Reset it
            lastCheckTime = System.currentTimeMillis();
            randomDelay = MIN_DELAY + (random.nextInt(120) - 60) * 1000; // +/- 1 minute
            timeToCheckStats = false;

            // re-open inv
            GameTabs.openInventoryTab();
            return true;
        }

        return false;
    }
}
