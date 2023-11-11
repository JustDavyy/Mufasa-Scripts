import helpers.*;
import helpers.utils.OptionType;

import java.util.Map;

@ScriptManifest(
        name = "ReadXP",
        description = "Reads your total XP on a loop.",
        version = "1.0",
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

public class ReadXP extends AbstractScript {
    String chosenTest; //Lets save the 1st config value
    String anotherConfig; //Lets save the 2nd config value

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        chosenTest = configs.get("Test configuration"); //Set this value to the 'chosenTest' string
        anotherConfig = configs.get("Another configuration"); //Get the value from the 2nd configuration option

        //Logs for debugging purposes
        logger.log("We are starting the Method Tester script and running onStart()");
        logger.log("Test configuration set to: " + chosenTest);
        logger.log("2nd config value set to: " + anotherConfig);
        System.out.println("Starting the Method Tester script!");
    }

    @Override
    public void poll() {
        client.readxp();
        condition.sleep(2000);
    }
}