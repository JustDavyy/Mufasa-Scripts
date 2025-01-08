import agi_sdk.helpers.Course;
import agi_sdk.helpers.
import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dAgility",
        description = "Trains agility at various courses. World hopping and eating food is supported, as well as picking up Marks of Grace when running a rooftop course.",
        version = "1.23",
        categories = {ScriptCategory.Agility},
        guideLink = "https://wiki.mufasaclient.com/docs/dagility/",
        skipZoomSetup = true
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Course",
                        description = "What agility course do you want to train at?",
                        defaultValue = "Advanced Colossal Wyrm",
                        allowedValues = {
                                @AllowedValue(optionName = "1-50 Progressive"),
                                @AllowedValue(optionName = "Gnome"),
                                @AllowedValue(optionName = "Draynor"),
                                @AllowedValue(optionName = "Al Kharid"),
                                @AllowedValue(optionName = "Varrock"),
                                @AllowedValue(optionName = "Canifis"),
                                @AllowedValue(optionName = "Colossal Wyrm Progressive"),
                                @AllowedValue(optionName = "Basic Colossal Wyrm"),
                                @AllowedValue(optionName = "Falador"),
                                @AllowedValue(optionName = "Seers"),
                                @AllowedValue(optionName = "Seers - teleport"),
                                @AllowedValue(optionName = "Advanced Colossal Wyrm"),
                                @AllowedValue(optionName = "Pollnivneach"),
                                @AllowedValue(optionName = "Rellekka"),
                                @AllowedValue(optionName = "Ardougne")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Food",
                        description = "Which food to use?",
                        defaultValue = "None",
                        allowedValues = {
                                @AllowedValue(optionName = "None"),
                                @AllowedValue(optionIcon = "1891", optionName = "Cake"),
                                @AllowedValue(optionIcon = "333", optionName = "Trout"),
                                @AllowedValue(optionIcon = "329", optionName = "Salmon"),
                                @AllowedValue(optionIcon = "361", optionName = "Tuna"),
                                @AllowedValue(optionIcon = "1993", optionName = "Jug of wine"),
                                @AllowedValue(optionIcon = "379", optionName = "Lobster"),
                                @AllowedValue(optionIcon = "373", optionName = "Swordfish"),
                                @AllowedValue(optionIcon = "6705", optionName = "Potato with cheese"),
                                @AllowedValue(optionIcon = "7946", optionName = "Monkfish"),
                                @AllowedValue(optionIcon = "3144", optionName = "Karambwan"),
                                @AllowedValue(optionIcon = "385", optionName = "Shark"),
                                @AllowedValue(optionIcon = "391", optionName = "Manta ray"),
                                @AllowedValue(optionIcon = "13441", optionName = "Anglerfish")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "EatPercent",
                        description = "What percent to eat at?",
                        defaultValue = "60%",
                        allowedValues = {
                                @AllowedValue(optionName = "20%"),
                                @AllowedValue(optionName = "25%"),
                                @AllowedValue(optionName = "30%"),
                                @AllowedValue(optionName = "35%"),
                                @AllowedValue(optionName = "40%"),
                                @AllowedValue(optionName = "45%"),
                                @AllowedValue(optionName = "50%"),
                                @AllowedValue(optionName = "55%"),
                                @AllowedValue(optionName = "60%"),
                                @AllowedValue(optionName = "65%"),
                                @AllowedValue(optionName = "70%"),
                                @AllowedValue(optionName = "75%"),
                                @AllowedValue(optionName = "80%"),
                                @AllowedValue(optionName = "85%"),
                                @AllowedValue(optionName = "90%"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dAgility extends AbstractScript {
    agi_sdk.runner agility = new agi_sdk.runner();
    PaintUpdater paintUpdater = new PaintUpdater();
    private String hopProfile;
    private boolean hopEnabled;
    private boolean useWDH;
    private static String courseChosen;
    private String foodChosen;
    private int eatPercent;
    private static int MoGIndex = 99;
    private static int termiteIndex = 99;
    private static int shardIndex = 99;
    private final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    private final DecimalFormat MoGsFormat = new DecimalFormat("#,##0.0", symbols);
    private final DecimalFormat LapsFormat = new DecimalFormat("#,##0.0", symbols);
    private final DecimalFormat ShardsFormat = new DecimalFormat("#,##0", symbols);
    private final DecimalFormat TermitesFormat = new DecimalFormat("#,##0", symbols);
    private static long startTime = System.currentTimeMillis();

    @Override
    public void onStart(){
        Logger.log("Initialising dAgility...");
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        hopProfile = configs.get("Use world hopper?");
        hopEnabled = Boolean.valueOf(configs.get("Use world hopper?.enabled"));
        useWDH = Boolean.valueOf(configs.get("Use world hopper?.useWDH"));
        courseChosen = configs.get("Course");
        foodChosen = configs.get("Food");
        eatPercent = Integer.parseInt(configs.get("EatPercent").replaceAll("%", ""));

        // Set up the Agility SDK settings
        setCourse();
        setFood();
        agility.setEatPercent(eatPercent);
        agility.setUsePaintbar(true);

        // Create the paintBar
        Logger.debugLog("Creating paint object.");
        Paint.Create("/logo/davyy.png");

        // Create image box(es), to show the amount of obtained marks
        if (courseChosen.equals("Basic Colossal Wyrm") || courseChosen.equals("Advanced Colossal Wyrm") || courseChosen.equals("Colossal Wyrm Progressive")) {
            termiteIndex = Paint.createBox("Termites", 30038, 0);
            Condition.sleep(400, 600);
            shardIndex = Paint.createBox("Bl. Bone Shards", ItemList.BLESSED_BONE_SHARDS_29381, 0);
        } else {
            MoGIndex = Paint.createBox("Marks of Grace", ItemList.MARK_OF_GRACE_11849, 0);
        }

        Logger.debugLog("Pass on the paint indexes to SDK");
        agility.setPaintHandler(paintUpdater, MoGIndex, termiteIndex, shardIndex);

        // Run the 'onStart' of the agility SDK
        agility.preStart();

        // Set the first headers
        Paint.setStatus("Initializing...");
        Paint.setStatistic("Initializing...");
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {
        agility.runCourse();
        hopActions();
    }

    public void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        }
    }

    private void setCourse() {
        switch (courseChosen) {
            case "1-50 Progressive":
                agility.setCourse(Course.PROGRESSIVE_TO_50);
                break;
            case "Gnome":
                agility.setCourse(Course.GNOME);
                break;
            case "Draynor":
                agility.setCourse(Course.DRAYNOR);
                break;
            case "Al Kharid":
                agility.setCourse(Course.AL_KHARID);
                break;
            case "Varrock":
                agility.setCourse(Course.VARROCK);
                break;
            case "Canifis":
                agility.setCourse(Course.CANIFIS);
                break;
            case "Colossal Wyrm Progressive":
                agility.setCourse(Course.COLOSSAL_WYRM_PROGRESSIVE);
                break;
            case "Basic Colossal Wyrm":
                agility.setCourse(Course.BASIC_COLOSSAL_WYRM);
                break;
            case "Falador":
                agility.setCourse(Course.FALADOR);
                break;
            case "Seers":
                agility.setCourse(Course.SEERS);
                break;
            case "Seers - teleport":
                agility.setCourse(Course.SEERS_TELEPORT);
                break;
            case "Advanced Colossal Wyrm":
                agility.setCourse(Course.ADVANCED_COLOSSAL_WYRM);
                break;
            case "Pollnivneach":
                agility.setCourse(Course.POLLNIVNEACH);
                break;
            case "Rellekka":
                agility.setCourse(Course.RELLEKKA);
                break;
            case "Ardougne":
                agility.setCourse(Course.ARDOUGNE);
                break;
            default:
                Logger.debugLog("Invalid course, stopping script. COURSE: " + courseChosen);
                Logout.logout();
                Script.stop();
        }
    }

    private void setFood() {
        switch (foodChosen) {
            case "None":
                agility.setFood(Food.NONE);
                break;
            case "Cake":
                agility.setFood();
                break;
            case "Trout":
                agility.setFood(Food.TROUT);
                break;
            case "Salmon":
                agility.setFood(Food.SALMON);
                break;
            case "Tuna":
                agility.setFood(Food.TUNA);
                break;
            case "Jug of wine":
                agility.setFood(Food.JUG_OF_WINE);
                break;
            case "Lobster":
                agility.setFood(Food.LOBSTER);
                break;
            case "Swordfish":
                agility.setFood(Food.SWORDFISH);
                break;
            case "Potato with cheese":
                agility.setFood(Food.POTATO_WITH_CHEESE);
                break;
            case "Monkfish":
                agility.setFood(Food.MONKFISH);
                break;
            case "Karambwan":
                agility.setFood(Food.KARAMBWAN);
                break;
            case "Shark":
                agility.setFood(Food.SHARK);
                break;
            case "Manta ray":
                agility.setFood(Food.MANTA_RAY);
                break;
            case "Anglerfish":
                agility.setFood(Food.ANGLERFISH);
                break;
            default:
                Logger.debugLog("Invalid food, stopping script. FOOD: " + courseChosen);
                Logout.logout();
                Script.stop();
        }
    }
}