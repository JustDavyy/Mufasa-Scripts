package scripts;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.ScriptManifest;

@ScriptManifest(
        name = "Bow Stringer",
        description = "A sample script for Fletching demonstration purposes - used by Davy",
        version = "12.3",
        category = ScriptCategory.Fletching
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