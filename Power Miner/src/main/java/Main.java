import Tasks.VarrockEast;
import helpers.*;
import helpers.utils.OptionType;
import utils.Task;

import java.util.Arrays;
import java.util.List;

@ScriptManifest(
        name = "AIO Miner",
        description = "Mines ores in different places",
        version = "1.0",
        category = ScriptCategory.Mining
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Location",
                        description = "Which location would you like to use? be sure to read the script guide for which ores are supported in specific locations",
                        defaultValue = "Soul isles",
                        allowedValues = {
                                @AllowedValue(optionName = "Varrock East"),
                                //@AllowedValue(optionName = "Varrock west"),
                                //@AllowedValue(optionName = "Soul isles"),
                                //@AllowedValue(optionName = "Al kharid"),
                                //@AllowedValue(optionName = "Mining Guild"),

                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Bank ores",
                        description = "Make sure you read the script guides if banking is supported in your location!",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "Ore type",
                        description = "Which ore would you like to mine?",
                        defaultValue = "Iron ore",
                        allowedValues = {
                                @AllowedValue(optionIcon = "436", optionName = "Copper ore"),
                                @AllowedValue(optionIcon = "442", optionName = "Silver ore"),
                                @AllowedValue(optionIcon = "434", optionName = "Clay"),
                                @AllowedValue(optionIcon = "440", optionName = "Iron ore"),
                                @AllowedValue(optionIcon = "453", optionName = "Coal"),

                        },
                        optionType = OptionType.STRING
                )
        }
)

public class Main extends AbstractScript {

    @Override
    public void onStart(){
        // Things you want to do before the script itself starts running
    }

    @Override
    public void poll() {
            List<Task> agilityTasks = Arrays.asList(
                    new VarrockEast()
            );

            for (Task task : agilityTasks) {
                if (task.activate()) {
                    task.execute();
                    return;
                }
            }
        }
}