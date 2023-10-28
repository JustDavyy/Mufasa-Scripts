package scripts;

import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.ScriptManifest;

@ScriptManifest(
        name = "Th!s 1s S0m3 W31rd ScR1pTN4M3",
        description = "A weird scriptname test for a Woodcutting script by Davy",
        version = "0.7",
        category = ScriptCategory.Woodcutting
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