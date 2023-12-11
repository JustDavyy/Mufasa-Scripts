import helpers.*;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.Map;

@ScriptManifest(
        name = "AIO Bow Fletcher",
        description = "AIO Bow Fletcher, supports both cutting and strining bows.",
        version = "1.0",
        category = ScriptCategory.Combat
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Method",
                        description = "Which operation would you like to perform?",
                        defaultValue = "Cut",
                        allowedValues = {
                                @AllowedValue(optionName = "Cut"), // itemID not specified
                                @AllowedValue(optionName = "String"), // itemID not specified
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Tier",
                        description = "Which tier of logs/bows would you like to use?",
                        defaultValue = "Maple logs",
                        allowedValues = {
                                @AllowedValue(optionIcon = "1511", optionName = "Logs"),
                                @AllowedValue(optionIcon = "1521", optionName = "Oak logs"),
                                @AllowedValue(optionIcon = "1519", optionName = "Willow logs"),
                                @AllowedValue(optionIcon = "1517", optionName = "Maple logs"),
                                @AllowedValue(optionIcon = "1515", optionName = "Yew logs"),
                                @AllowedValue(optionIcon = "1513", optionName = "Magic logs")
                        },
                        optionType = OptionType.STRING
                )
        }
)

public class AIOBowFletcher extends AbstractScript {
    String chosenTest; //Lets save the 1st config value
    String anotherConfig; //Lets save the 2nd config value
    String bankloc;

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
        logger.log("Starting the poll method for AIO Bow Fletcher.");

        // Main logic
        logger.log("Executing the main logic for AIO Bow Fletcher.");
        condition.sleep(2000);
        xpBar.getXP();
    }
}