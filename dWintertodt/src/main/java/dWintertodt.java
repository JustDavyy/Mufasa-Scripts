import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.OptionType;
import helpers.utils.Tile;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

    @Override
    public void onStart(){

    }

    @Override
    public void poll() {

    }
}