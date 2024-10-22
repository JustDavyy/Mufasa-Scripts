package Tasks;

import helpers.utils.Skills;
import utils.Task;

import java.util.Random;

import static helpers.Interfaces.*;

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
            int attackLevel = Stats.getRealLevel(Skills.ATTACK);
            int strengthLevel = Stats.getRealLevel(Skills.STRENGTH);
            int defenceLevel = Stats.getRealLevel(Skills.DEFENCE);
            int rangeLevel = Stats.getRealLevel(Skills.RANGED);

            // Log or use the skill levels as needed
            Logger.log("Attack Level: " + attackLevel);
            Logger.log("Strength Level: " + strengthLevel);
            Logger.log("Defence Level: " + defenceLevel);
            Logger.log("Range Level: " + rangeLevel);

            //Reset it
            lastCheckTime = System.currentTimeMillis();
            randomDelay = MIN_DELAY + (random.nextInt(120) - 60) * 1000; // +/- 1 minute
            timeToCheckStats = false;

            return true;
        }

        return false;
    }
}
