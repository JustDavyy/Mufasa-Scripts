package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.ItemList;
import helpers.utils.OptionType;
import helpers.utils.RegionBox;
import helpers.utils.Tile;
import tasks.*;
import utils.Task;

import java.awt.*;
import java.util.*;
import java.util.List;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "ezMuseumCleaner",
        description = "Does Museum Cleaning for all your off-skill needs",
        version = "1.2",
        guideLink = "https://wiki.mufasaclient.com/docs/ezmuseumcleaner/",
        categories = {ScriptCategory.Minigames}
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "The default for this script is enabled and recommended to do so",
                        defaultValue = "true",
                        wdhEnabled = "true",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name = "Lamp skill",
                        description = "Select which skill you would like to spend lamps on",
                        defaultValue = "Slayer",
                        allowedValues = {
                                @AllowedValue(optionIcon = "99997", optionName = "Attack"),
                                @AllowedValue(optionIcon = "99998", optionName = "Strenght"),
                                @AllowedValue(optionIcon = "99995", optionName = "Ranged"),
                                @AllowedValue(optionIcon = "99993", optionName = "Magic"),
                                @AllowedValue(optionIcon = "99996", optionName = "Defence"),
                                @AllowedValue(optionIcon = "99991", optionName = "HP"),
                                @AllowedValue(optionIcon = "99994", optionName = "Prayer"),
                                @AllowedValue(optionIcon = "99983", optionName = "Agility"),
                                @AllowedValue(optionIcon = "99982", optionName = "Herblore"),
                                @AllowedValue(optionIcon = "99981", optionName = "Thieving"),
                                @AllowedValue(optionIcon = "99990", optionName = "Crafting"),
                                @AllowedValue(optionIcon = "99992", optionName = "Runecrafting"),
                                @AllowedValue(optionIcon = "99979", optionName = "Slayer"),
                                @AllowedValue(optionIcon = "99978", optionName = "Farming"),
                                @AllowedValue(optionIcon = "99989", optionName = "Mining"),
                                @AllowedValue(optionIcon = "99988", optionName = "Smithing"),
                                @AllowedValue(optionIcon = "99987", optionName = "Fishing"),
                                @AllowedValue(optionIcon = "99986", optionName = "Cooking"),
                                @AllowedValue(optionIcon = "99985", optionName = "Firemaking"),
                                @AllowedValue(optionIcon = "99984", optionName = "Woodcutting"),
                                @AllowedValue(optionIcon = "99980", optionName = "Fletching"),
                                @AllowedValue(optionIcon = "99977", optionName = "Construction"),
                                @AllowedValue(optionIcon = "99976", optionName = "Hunter"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Drop All",
                        description = "Toggle this to drop everything, darts, knives, bolts etc. it will NOT drop the coins.",
                        defaultValue = "false",
                        optionType = OptionType.BOOLEAN
                )
        }
)

public class ezMuseumCleaner extends AbstractScript {
    Boolean hopEnabled;
    Boolean useWDH;
    String hopProfile;
    private static final Random random = new Random();

    public static boolean hasFinds;
    public static boolean shouldDrop = false;
    public static boolean shouldDeposit = false;
    public static String selectedLampSkill;
    public static Rectangle selectedLampSkillRectangle;
    public static boolean dropAll;

    public static Tile depositTile = new Tile(186, 117);
    public static Tile depositTile2 = new Tile(185, 118);
    public static Tile cleanTile = new Tile(181,116);
    public static Tile collectTile = new Tile(182, 114);
    public static RegionBox museumRegion = new RegionBox(
	"museumRegion",
            414, 237,
            636, 438
    );

    public static int[] depositItemsList = {
            ItemList.POTTERY_11178,
            ItemList.JEWELLERY_11177,
            ItemList.OLD_CHIPPED_VASE_11183,
            ItemList.ARROWHEADS_11176,
    };

    public static List<Integer> toolPositionList = new ArrayList<>();

    public static int[] dropList;

    public static Tile currentLocation;

    public static int paintLampBox;
    public static int currentLampCount = 0;

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        hopProfile = configs.get("Use world hopper?");
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf(configs.get("Use world hopper?.useWDH"));
        selectedLampSkill = configs.get("Lamp skill");
        dropAll = Boolean.parseBoolean(configs.get("Drop All"));

        Walker.setup("maps/Varrock.png", museumRegion);

        Chatbox.closeChatbox();
        Game.setZoom("2");

        Logger.log("Starting ezMuseumCleaner v1.0");
        Paint.Create(null);
        Paint.setStatus("Performing startup actions");
        paintLampBox = Paint.createBox("Lamps", ItemList.ANTIQUE_LAMP_4447, currentLampCount);
        setupDropList();
        updateSelectedLampSkillRectangle();

        if (count(depositItemsList) >= 16) {
            Logger.log("We have enough to deposit, depositing!");
            shouldDeposit = true;
        }
    }

    // Task list!
    List<Task> museumTasks = Arrays.asList(
            new CheckEquipment(),
            new HandleLamp(),
            new Drop(),
            new DepositFinds(),
            new CleanFinds(),
            new CollectFinds()
    );

    @Override
    public void poll() {
        if(hopEnabled) {
            Game.hop(hopProfile, useWDH, false);
        }

        if (!GameTabs.isInventoryTabOpen()) {
            GameTabs.openInventoryTab();
            Condition.wait(() -> GameTabs.isInventoryTabOpen(), 200, 20);
        }
        hasFinds = Inventory.contains(ItemList.UNCLEANED_FIND_11175, 0.80);

        currentLocation = Walker.getPlayerPosition();
        //Run tasks
        for (Task task : museumTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }
    }

    public static int generateRandomDelay(int lowerBound, int upperBound) {
        // Swap if lowerBound is greater than upperBound
        if (lowerBound > upperBound) {
            int temp = lowerBound;
            lowerBound = upperBound;
            upperBound = temp;
        }
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

    private void setupDropList() {
        // Base list of items, shared between both conditions
        List<Integer> baseList = new ArrayList<>(Arrays.asList(
                ItemList.BROKEN_ARROW_687,
                ItemList.IRON_DAGGER_1203,
                ItemList.UNCUT_JADE_1627,
                ItemList.BONES_526,
                ItemList.BOWL_1923,
                ItemList.POT_1931,
                ItemList.BRONZE_LIMBS_9420,
                ItemList.WOODEN_STOCK_9440,
                ItemList.TIN_ORE_438,
                ItemList.COAL_453,
                ItemList.COPPER_ORE_436,
                ItemList.BIG_BONES_532,
                ItemList.IRON_ORE_440,
                ItemList.MITHRIL_ORE_447,
                ItemList.UNCUT_OPAL_1625,
                ItemList.BROKEN_GLASS_1469
        ));

        // Items exclusive to dropAll condition
        if (dropAll) {
            baseList.addAll(Arrays.asList(
                    ItemList.IRON_ARROWTIPS_40,
                    ItemList.IRON_BOLTS_2_9150,
                    ItemList.IRON_DART_807,
                    ItemList.IRON_KNIFE_863
            ));
        }

        // Convert List to array for dropList assignment
        dropList = baseList.stream().mapToInt(Integer::intValue).toArray();
    }

    private static void updateSelectedLampSkillRectangle() {
        switch (selectedLampSkill) {
            case "Attack":
                selectedLampSkillRectangle = new Rectangle(265, 293, 18, 19);
                break;
            case "Strength":
                selectedLampSkillRectangle = new Rectangle(302, 295, 16, 17);
                break;
            case "Ranged":
                selectedLampSkillRectangle = new Rectangle(338, 297, 15, 15);
                break;
            case "Magic":
                selectedLampSkillRectangle = new Rectangle(375, 295, 14, 16);
                break;
            case "Defence":
                selectedLampSkillRectangle = new Rectangle(412, 297, 11, 14);
                break;
            case "HP":
                selectedLampSkillRectangle = new Rectangle(248, 332, 13, 14);
                break;
            case "Prayer":
                selectedLampSkillRectangle = new Rectangle(284, 334, 14, 14);
                break;
            case "Agility":
                selectedLampSkillRectangle = new Rectangle(320, 332, 14, 15);
                break;
            case "Herblore":
                selectedLampSkillRectangle = new Rectangle(355, 332, 16, 14);
                break;
            case "Thieving":
                selectedLampSkillRectangle = new Rectangle(391, 331, 20, 20);
                break;
            case "Crafting":
                selectedLampSkillRectangle = new Rectangle(425, 331, 19, 18);
                break;
            case "Runecrafting":
                selectedLampSkillRectangle = new Rectangle(246, 367, 17, 17);
                break;
            case "Slayer":
                selectedLampSkillRectangle = new Rectangle(283, 366, 18, 18);
                break;
            case "Farming":
                selectedLampSkillRectangle = new Rectangle(319, 365, 17, 21);
                break;
            case "Mining":
                selectedLampSkillRectangle = new Rectangle(355, 367, 16, 18);
                break;
            case "Smithing":
                selectedLampSkillRectangle = new Rectangle(390, 367, 19, 19);
                break;
            case "Fishing":
                selectedLampSkillRectangle = new Rectangle(426, 366, 18, 19);
                break;
            case "Cooking":
                selectedLampSkillRectangle = new Rectangle(247, 402, 18, 18);
                break;
            case "Firemaking":
                selectedLampSkillRectangle = new Rectangle(282, 402, 19, 18);
                break;
            case "Woodcutting":
                selectedLampSkillRectangle = new Rectangle(319, 403, 16, 18);
                break;
            case "Fletching":
                selectedLampSkillRectangle = new Rectangle(355, 402, 17, 18);
                break;
            case "Construction":
                selectedLampSkillRectangle = new Rectangle(390, 401, 19, 19);
                break;
            case "Hunter":
                selectedLampSkillRectangle = new Rectangle(426, 404, 17, 17);
                break;
            default:
                Logger.log("Incorrect lamp skill setup, stopping script");
                Script.stop();
                break;
        }
    }

    public int count(int[] itemsToCount) {
        int totalCount = 0;
        for (int item : itemsToCount) {
            totalCount += Inventory.count(item, 0.80);
        }
        Logger.debugLog("deposit inventory count: " + totalCount);
        return totalCount;
    }
}