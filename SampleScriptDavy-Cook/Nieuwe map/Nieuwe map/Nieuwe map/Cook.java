package scripts;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.ScriptManifest;

@ScriptManifest(
        name = "AIO Cooker",
        description = "A sample script for cooking demonstration purposes - used by Davy",
        version = "2.7",
        category = ScriptCategory.Cooking
)
public class SampleScript extends AbstractScript {
    @Override
    public void onStart(){
        logger.log("We are starting the sample script and running onStart()");
        System.out.println("Starting the SampleScriptDavy!");
    }

    @Override
    public void poll() {
        logger.log("We are looping the poll() method!");
        condition.sleep(5000);
        System.out.println("Executing main logic of the SampleScript!");
        // Main logic for the script
    }
}