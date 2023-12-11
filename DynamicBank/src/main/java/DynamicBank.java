import helpers.*;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.Map;

@ScriptManifest(
        name = "DynamicBank",
        description = "Tests the Dynamic banking and enters bank pin if needed.",
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
                                @AllowedValue(optionName = "Option1"), // itemID not specified
                                @AllowedValue(optionIcon = "2", optionName = "Option2") // itemID specified, in this case 'Cannonball' icon.
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Another configuration",
                        description = "Choose another configuration",
                        defaultValue = "OptionA",
                        allowedValues = {
                                @AllowedValue(optionName = "Option1"), // itemID not specified
                                @AllowedValue(optionIcon = "2", optionName = "Option2") // itemID specified, in this case 'Cannonball' icon.
                        },
                        optionType = OptionType.STRING
                )
        }
)

public class DynamicBank extends AbstractScript {
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
        logger.log("Starting the poll method for Dynamic Banking.");
        condition.sleep(2000);
		
		// Setting up stuff, storing it and so on...
        if (bankloc == null) {
            logger.log("Starting setup for Dynamic Banking.");
            bankloc = bank.setupDynamicBank();
            logger.log("We're located at: " + bankloc + ".");
            condition.sleep(5000);
            logger.log("Attempting to open the Bank of Gielinor.");
            bank.open(bankloc);
            condition.wait(() -> bank.isOpen(), 250, 12);
            logger.log("Bank interface detected!");
            if (bank.isBankPinNeeded()) {
                logger.log("Bank pin is needed!");
                bank.enterBankPin();
                condition.sleep(1000);
                logger.log("Bank pin entered.");
            } else {
                logger.log("Bank pin is not needed, bank is open!");
            }
            logger.log("Closing the bank interface");
            bank.close();
        }



        // Main logic
        logger.log("Executing the main logic for Dynamic Banking.");
        condition.sleep(2000);
        xpBar.getXP();
    }
}