import helpers.*;
import helpers.utils.OptionType;

import java.util.Map;

@ScriptManifest(
        name = "Chicken Killer",
        description = "Chicken Killer by JustDavyy, kills chickens in the lumbridge chicken pen and optionally picks up feathers.",
        version = "1.4",
        category = ScriptCategory.Combat
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Test configuration",
                        description = "Which test would you like to run?",
                        defaultValue = "Test1",
                        allowedValues = {
                                "Test1", "Test2", "Test3"
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Another configuration",
                        description = "Choose another configuration",
                        defaultValue = "OptionA",
                        allowedValues = {
                                "OptionA", "OptionB", "OptionC"
                        },
                        optionType = OptionType.STRING
                )
        }
)

public class SampleScript extends AbstractScript {
    String chosenTest; //Lets save the 1st config value
    String anotherConfig; //Lets save the 2nd config value

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        chosenTest = configs.get("Test configuration"); //Set this value to the 'chosenTest' string
        anotherConfig = configs.get("Another configuration"); //Get the value from the 2nd configuration option

        //Logs for debugging purposes
        logger.log("We are starting the sample script and running onStart()");
        logger.log("Test configuration set to: " + chosenTest);
        logger.log("2nd config value set to: " + anotherConfig);
        System.out.println("Starting the SampleScript!");
    }

    @Override
    public void poll() {
        logger.log("We are looping the poll() method!");
        condition.sleep(5000);
        System.out.println("Executing main logic of the SampleScript!");
        // Main logic for the script
    }
}