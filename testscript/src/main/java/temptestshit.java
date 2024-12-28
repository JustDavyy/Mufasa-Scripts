import agi_sdk.*;
import agi_sdk.helpers.Course;
import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static helpers.Interfaces.*;

import java.util.concurrent.ConcurrentHashMap;

@ScriptManifest(
        name = "temptestshit",
        description = "Just davyy's temporary test shit script.",
        version = "1.00",
        guideLink = "",
        categories = {ScriptCategory.Minigames},
        skipZoomSetup = true,
        skipClientSetup = true
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Am I an idiot?",
                        description = "Check = yes, uncheck = no",
                        defaultValue = "Yes",
                        allowedValues = {
                      @AllowedValue(optionName = "Yes"),
                                @AllowedValue(optionName = "No")
                        },
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class temptestshit extends AbstractScript {
    Boolean idiot;
    agi_sdk.runner agility = new agi_sdk.runner();

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        idiot = Boolean.valueOf((configs.get("Am I an idiot?")));

        Logger.log("Thank you for using the temptestshit script!\nAre you an idiot: " + idiot);

        Logger.debugLog("This is a test script testing the agility SDK");

        agility.setSettings(Course.DRAYNOR, "None", 50, false);
        agility.preStart();
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {
        agility.runCourse();

        agility.getLapCount();
        agility.getMoGCount();
        agility.getBoneShardCount();
        agility.getTermiteCount();
    }

    // We also dont need any additional methods, yay!

    public static boolean readyToProcess() {
        // Instant true if we leveled up
        if (Player.leveledUp()) {
            return true;
        }

        return false;
    }

}