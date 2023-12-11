import helpers.AbstractScript;
import helpers.ScriptCategory;
import helpers.ScriptConfiguration;
import helpers.ScriptManifest;
import helpers.utils.OptionType;

import java.awt.*;
import java.util.Map;

@ScriptManifest(
        name = "Method Tester Walker",
        description = "Simple test script to test out certain methods as actual script execution.",
        version = "0.2",
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

public class MethodTesterWalker extends AbstractScript {
    String chosenTest; //Lets save the 1st config value
    String anotherConfig; //Lets save the 2nd config value

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        chosenTest = configs.get("Test configuration"); //Set this value to the 'chosenTest' string
        anotherConfig = configs.get("Another configuration"); //Get the value from the 2nd configuration option
        System.out.println("Starting the Method Tester walking script!");
    }

    @Override
    public void poll() {
        String image = "/agility.png";
        Point position1 = new Point(228, 701);
        Point position2 = new Point(216, 701);
        walker.walkTo(position1, image);
        condition.wait((walker.getPlayerPosition(image).getKey().equals(position1)), 200, 20);
        walker.walkTo(position2, image);
        condition.wait((walker.getPlayerPosition(image).getKey().equals(position2)), 200, 20);
    }
}