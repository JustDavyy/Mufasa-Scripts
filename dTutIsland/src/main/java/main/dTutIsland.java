package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.MapChunk;
import helpers.utils.OptionType;

import tasks.*;
import utils.Task;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static helpers.Interfaces.*;


@ScriptManifest(
        name = "dTutIsland",
        description = "Completed tutorial island with the option to make ironman accounts",
        version = "1.00",
        guideLink = "",
        categories = {ScriptCategory.Misc, ScriptCategory.Ironman},
        skipClientSetup = true,
        skipZoomSetup = true
)
@ScriptConfiguration.List(
        value = {
                @ScriptConfiguration(
                        name =  "Account type",
                        description = "Which account type would you like to create?",
                        defaultValue = "Hardcore Ironman",
                        allowedValues = {
                                @AllowedValue(optionIcon = "2421", optionName = "Fucking normie"),
                                @AllowedValue(optionIcon = "12810", optionName = "Standard ironman"),
                                @AllowedValue(optionIcon = "20792", optionName = "Hardcore ironman"),
                                @AllowedValue(optionIcon = "12813", optionName = "Ultimate ironman"),
                                @AllowedValue(optionIcon = "26156", optionName = "Group ironman"),
                                @AllowedValue(optionIcon = "26170", optionName = "Hardcore group ironman")
                        },
                        optionType = OptionType.STRING
                )
        }
)

public class dTutIsland extends AbstractScript {
    public static String accountType;
    public static boolean accountCreationDone = false;
    public static boolean gielinorGuideDone = false;
    public static boolean survivalExpertDone = false;
    public static boolean masterChefDone = false;
    public static boolean questGuideDone = false;
    public static boolean miningInstructorDone = false;
    public static boolean combatInstructorDone = false;
    public static boolean bankInstructorDone = false;
    public static boolean accountGuideDone = false;
    public static boolean prayerInstructorDone = false;
    public static boolean magicInstructorDone = false;
    public static boolean setAccountTypeDone = false;
    public static boolean cancelBankPinDone = false;
    public static boolean setSettingsDone = false;
    public static Rectangle NPCHeaderCheckRect = new Rectangle(216, 17, 218, 28);
    public static Color NPCHeaderColor = Color.decode("#800000");
    public static Rectangle clickToContinueRect = new Rectangle(243, 105, 148, 17);
    

    @Override
    public void onStart(){

        Map<String, String> configs = getConfigurations();
        accountType = configs.get("Account type");

        Logger.log("Disable AFK and break handlers.");
        Client.disableAFKHandler();
        Client.disableBreakHandler();

        Logger.log("Set up walker");
        // Set up walker chunks
        MapChunk chunks = new MapChunk(new String[]{"47-49", "48-49", "49-49", "47-48", "48-48", "49-48", "47-47", "48-47", "49-47", "48-46", "49-50", "50-50", "49-49", "50-49", "48-148", "47-148"}, "0", "1", "2", "3");
        Walker.setup(chunks);

    }

    List<Task> tutTasks = Arrays.asList(
            new AccountCreation(),
            new GielinorGuide(),
            new SurvivalExpert(),
            new MasterChef(),
            new QuestGuide(),
            new MiningInstructor(),
            new CombatInstructor(),
            new BankInstructor(),
            new AccountGuide(),
            new PrayerInstructor(),
            new SetAccountType(),
            new MagicInstructor(),
            new CancelBankPin(),
            new SetIngameSettings()
    );

    @Override
    public void poll() {

        if (setSettingsDone) {
            Logger.log("Done with the last task, logging out and stopping script!");
            Logger.log("GREAT SUCCESS!");
            Logout.logout();
            Script.stop();
        }

        //Run tasks
        for (Task task : tutTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }
}
