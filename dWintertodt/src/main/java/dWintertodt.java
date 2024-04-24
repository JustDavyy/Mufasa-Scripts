import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import helpers.utils.Tile;
import tasks.*;
import utils.StateUpdater;
import utils.Task;
import utils.WTStates;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ScriptManifest(
        name = "dTeaYoinker",
        description = "Steals from the tea stall in east Varrock. Supports world hopping and banking the tea.",
        version = "1.02",
        guideLink = "https://wiki.mufasaclient.com/docs/dtea-yoinker/",
        categories = {ScriptCategory.Firemaking}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dWintertodt extends AbstractScript {
    List<Task> WTTasks = Arrays.asList(
            new Eat(),
            new Bank(),
            new SwitchSide(),
            new BurnBranches(),
            new FletchBranches(),
            new GetBranches()
    );

    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;
    StateUpdater stateUpdater = new StateUpdater();

    // STATES
    public static WTStates[] states = {
            new WTStates("Upper Left", new Rectangle(65, 57, 25, 23), false, false, false, false),
            new WTStates("Upper Right", new Rectangle(122, 56, 23, 23), false, false, false, false),
            new WTStates("Lower Left", new Rectangle(122, 56, 23, 23), false, false, false, false),
            new WTStates("Lower Right", new Rectangle(120, 110, 25, 27), false, false, false, false)
    };

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
    }

    @Override
    public void poll() {

        // Keep track of the states on each loop
        stateUpdater.updateStates(states);

        //Run tasks
        for (Task task : WTTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }


    // Just leaving these down here so we can figure out where they belong
    Tile[] LowerRightToLeft = new Tile[] {
            new Tile(648, 165),
            new Tile(638, 165),
            new Tile(626, 165)
    };
    Tile[] returnToWTDoor = new Tile[] {
            new Tile(638, 167),
            new Tile(637, 175),
            new Tile(637, 185),
            new Tile(637, 195)
    };
    Tile[] wtDoorToBank = new Tile[] {
            new Tile(637, 204),
            new Tile(639, 217),
            new Tile(645, 228),
            new Tile(650, 228)
    };
    Tile[] wtDoorToRightSide = new Tile[] {
            new Tile(638, 185),
            new Tile(639, 175),
            new Tile(650, 165)
    };
    Tile[] wtDoorToLeftSide = new Tile[] {
            new Tile(637, 186),
            new Tile(637, 173),
            new Tile(625, 165)
    };
    Tile[] fromEitherSideToSafeSpot = new Tile[] {
            new Tile(637, 166),
            new Tile(637, 177)
    };

    // For when we need reverse the paths when going back/forth
    public Tile[] getReversedTiles(Tile[] array) {
        if (array == null) return null;
        Tile[] reversed = new Tile[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }
}