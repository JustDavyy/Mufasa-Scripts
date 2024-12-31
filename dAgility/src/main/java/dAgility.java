import agi_sdk.helpers.Course;
import agi_sdk.helpers.Food;
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
    private String hopProfile;
    private boolean hopEnabled;
    private boolean useWDH;
    private static String courseChosen;
    private int lapsDone = 0;
    private String foodChosen;
    private int eatPercent;
    private static int MoGIndex;
    private static int termiteIndex;
    private static int shardIndex;
    private final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    private final DecimalFormat MoGsFormat = new DecimalFormat("#,##0.0", symbols);
    private final DecimalFormat LapsFormat = new DecimalFormat("#,##0.0", symbols);
    private final DecimalFormat ShardsFormat = new DecimalFormat("#,##0", symbols);
    private final DecimalFormat TermitesFormat = new DecimalFormat("#,##0", symbols);
    private static long startTime = System.currentTimeMillis();
    private String currentStatus = null;

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
            Condition.sleep(500);
            shardIndex = Paint.createBox("Bl. Bone Shards", ItemList.BLESSED_BONE_SHARDS_29381, 0);
        } else {
            MoGIndex = Paint.createBox("Marks of Grace", ItemList.MARK_OF_GRACE_11849, 0);
        }

        // Run the 'onStart' of the agility SDK
        agility.preStart();

        // Set the first headers
        Paint.setStatus("Initializing...");
        Paint.setStatistic("Initializing...");

        currentStatus = agility.getStatus();
    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {
        checkStatus();
        agility.runCourse();
        checkStatus();

        hopActions();

        if (agility.getLapCount() > lapsDone) {
            lapsDone = agility.getLapCount();
            updatePaintBoxes();
            updateStatistics();
            checkStatus();
        }
    }

    public void hopActions() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        }
    }

    public void updatePaintBoxes() {
        if (courseChosen.equals("Basic Colossal Wyrm") || courseChosen.equals("Advanced Colossal Wyrm") || courseChosen.equals("Colossal Wyrm Progressive")) {
            Paint.updateBox(termiteIndex, agility.getTermiteCount());
            Condition.sleep(200);
            Paint.updateBox(shardIndex, agility.getBoneShardCount());
        } else {
            Paint.updateBox(MoGIndex, agility.getMoGCount());
        }
    }

    private void checkStatus() {
        String newStatus = agility.getStatus();
        if (!newStatus.equals(currentStatus)) {
            currentStatus = newStatus;
            Paint.setStatus(currentStatus);
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
                agility.setFood(Food.CAKE);
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

    private void updateStatistics() {
        if (courseChosen.equals("Basic Colossal Wyrm") || courseChosen.equals("Advanced Colossal Wyrm")) {
            // Set separators
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');

            // Calculations for MoGs and laps per hour
            long currentTime = System.currentTimeMillis();
            double elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);

            // Calculate MoGs per hour and laps per hour
            double TermitesPerHour = agility.getTermiteCount() / elapsedTimeInHours;
            double ShardsPerHour = agility.getBoneShardCount() / elapsedTimeInHours;
            double LapsPerHour = agility.getLapCount() / elapsedTimeInHours;

            // Format Termites per hour, shards per hour, and laps per hour with one decimal place
            String TermitesPerHourFormatted = TermitesFormat.format(TermitesPerHour);
            String ShardsPerHourFormatted = ShardsFormat.format(ShardsPerHour);
            String LapsPerHourFormatted = LapsFormat.format(LapsPerHour);

            // Update the statistics label with all three stats
            String statistics = String.format("Term %s | Shard %s | Lap %s /hr", TermitesPerHourFormatted, ShardsPerHourFormatted, LapsPerHourFormatted);
            Paint.setStatistic(statistics);
        } else {
            // Set separators
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');

            // Calculations for MoGs and laps per hour
            long currentTime = System.currentTimeMillis();
            double elapsedTimeInHours = (currentTime - startTime) / (1000.0 * 60 * 60);

            // Calculate MoGs per hour and laps per hour
            double MoGsPerHour = agility.getMoGCount() / elapsedTimeInHours;
            double LapsPerHour = agility.getLapCount() / elapsedTimeInHours;

            // Format MoGs per hour and laps per hour with one decimal place
            String MoGsPerHourFormatted = MoGsFormat.format(MoGsPerHour);
            String LapsPerHourFormatted = LapsFormat.format(LapsPerHour);

            // Update the statistics label
            String statistics = String.format("MoGs/hr: %s | Laps/hr: %s", MoGsPerHourFormatted, LapsPerHourFormatted);
            Paint.setStatistic(statistics);
        }
    }
}