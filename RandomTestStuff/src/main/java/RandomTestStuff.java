import helpers.*;
import helpers.utils.*;

import java.util.Map;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dCooker",
        description = "The cooker script to cook all your raw fish (or seaweed) at various different places.",
        version = "1.00",
        guideLink = "https://wiki.mufasaclient.com/docs/dcooker/",
        categories = {ScriptCategory.Cooking}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Product",
                        description = "What product would you like to cook?",
                        defaultValue = "Seaweed",
                        allowedValues = {
                                @AllowedValue(optionIcon = "401", optionName = "Seaweed")
                        },
                        optionType = OptionType.STRING
                )
        }
)

public class RandomTestStuff extends AbstractScript {

// Script config variables
String product;


// Script logic variables

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        product = configs.get("Product");

        Logger.log("Thank you for using the RandomTestStuff script!");
        Logger.log("Setting up everything for your gains now...");

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
        }

    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        Logger.debugLog("Temp log statement.");

        Boolean YesOrNo = Inventory.contains(453, 0.60);

        if(YesOrNo) {
            Logger.debugLog("Inventory contains Coal");
        } else {
            Logger.debugLog("Inventory does not contain Coal.");
        }


    }
}

