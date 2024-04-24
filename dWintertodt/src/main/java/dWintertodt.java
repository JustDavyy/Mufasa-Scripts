import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import tasks.StateUpdater;
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
            new StateUpdater()
    );

    public static String hopProfile;
    public static Boolean hopEnabled;
    public static Boolean useWDH;


    // STATES
    WTStates[] states = {
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

        //Run tasks
        for (Task task : WTTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
}