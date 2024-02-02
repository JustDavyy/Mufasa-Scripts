import helpers.*;
import helpers.utils.*;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
                        defaultValue = "Raw karambwan",
                        allowedValues = {
                                @AllowedValue(optionIcon = "401", optionName = "Seaweed"),
                                @AllowedValue(optionIcon = "21504", optionName = "Giant seaweed"),
                                @AllowedValue(optionIcon = "317", optionName = "Raw shrimps"),
                                @AllowedValue(optionIcon = "327", optionName = "Raw sardine"),
                                @AllowedValue(optionIcon = "345", optionName = "Raw herring"),
                                @AllowedValue(optionIcon = "353", optionName = "Raw mackerel"),
                                @AllowedValue(optionIcon = "335", optionName = "Raw trout"),
                                @AllowedValue(optionIcon = "341", optionName = "Raw cod"),
                                @AllowedValue(optionIcon = "349", optionName = "Raw pike"),
                                @AllowedValue(optionIcon = "331", optionName = "Raw salmon"),
                                @AllowedValue(optionIcon = "3142", optionName = "Raw karambwan"),
                                @AllowedValue(optionIcon = "359", optionName = "Raw tuna"),
                                @AllowedValue(optionIcon = "377", optionName = "Raw lobster"),
                                @AllowedValue(optionIcon = "371", optionName = "Raw swordfish"),
                                @AllowedValue(optionIcon = "7944", optionName = "Raw monkfish"),
                                @AllowedValue(optionIcon = "383", optionName = "Raw shark"),
                                @AllowedValue(optionIcon = "395", optionName = "Raw sea turtle"),
                                @AllowedValue(optionIcon = "13439", optionName = "Raw anglerfish"),
                                @AllowedValue(optionIcon = "389", optionName = "Raw manta ray"),
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Location",
                        description = "What location would you like to cook at?",
                        defaultValue = "Hosidius kitchen",
                        allowedValues = {
                                @AllowedValue(optionName = "Cooks' Guild"),
                                @AllowedValue(optionName = "Hosidius kitchen"),
                                @AllowedValue(optionName = "Mor Ul Rek"),
                                @AllowedValue(optionName = "Myths' Guild"),
                                @AllowedValue(optionName = "Nardah oven"),
                                @AllowedValue(optionName = "Rogues' Den")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name =  "Bank Tab",
                        description = "What bank tab are your resources located in?",
                        defaultValue = "0",
                        optionType = OptionType.BANKTABS
                ),
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "Would you like to hop worlds based on your hop profile settings?",
                        defaultValue = "0",
                        optionType = OptionType.WORLDHOPPER
                )
        }
)

public class dCooker extends AbstractScript {

// Script config variables
String hopProfile;
Boolean hopEnabled;
Boolean useWDH;
int banktab;
String product;
String location;

// Area, regions and tiles
Area cookingGuildArea = new Area(
        new Tile(2655, 877),
        new Tile(2664, 889)
);
RegionBox cookingGuildRegion = new RegionBox(
        "CookingGuild",
        7743, 2460,
        8190, 2901
);
Tile cookingGuildTile = new Tile(2660, 883);

Area hosidiusKitchenArea = new Area(
        new Tile(690, 650),
        new Tile(716, 680)
);
Tile hosidiusTile = new Tile(698, 666);

Tile morulrekTile = new Tile(1692, 191);
RegionBox morulrekRegion = new RegionBox(
        "MorUlRek",
        4803, 363,
        5310, 822
);
Area morulrekArea = new Area(
        new Tile(1682, 184),
        new Tile(1697, 201)
);
Tile mythsguildTile = new Tile(3490, 1904);
RegionBox mythsguildRegion = new RegionBox(
        "MythsGuild",
        10092, 5466,
        10554, 5826
);

RegionBox roguesRegion = new RegionBox(
        "RoguesDen",
        10251, 762,
        10551, 1086
);
Area roguesArea = new Area(
        new Tile(3458, 310),
        new Tile(3465, 316)
);
Tile roguesTile = new Tile(3463, 313);

RegionBox nardahRegion = new RegionBox(
        "Nardah",
        8796, 4545,
        9402, 5040
);
Area nardahArea = new Area(
        new Tile(3026, 1621),
        new Tile(3050, 1641)
);
Tile nardahTile = new Tile(3034, 1628);

// Banks and range rectangles
private Map<String, List<RectanglePair>> bankRectangles = new HashMap<>();
private Random random = new Random();
Rectangle nardahSetupRect = new Rectangle(558, 360, 23, 24);


// Script logic variables
Tile playerPos;

    // This is the onStart, and only gets ran once.
    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations();
        hopProfile = (configs.get("Use world hopper?"));
        hopEnabled = Boolean.valueOf((configs.get("Use world hopper?.enabled")));
        useWDH = Boolean.valueOf((configs.get("Use world hopper?.useWDH")));
        banktab = Integer.parseInt(configs.get("Bank Tab"));
        product = configs.get("Product");
        location = configs.get("Location");

        Logger.log("Thank you for using the dCooker script!");
        Logger.log("Setting up everything for your gains now...");

        if (hopEnabled) {
            if(useWDH) {
                Logger.debugLog("Hopping (with WDH) is enabled for this run! Using profile: " + hopProfile);
            } else {
                Logger.debugLog("Hopping (without WDH) is enabled for this run! Using profile: " + hopProfile);
            }
        } else {
            Logger.debugLog("Hopping is disabled for this run!");
        }

        // Debug prints for chosen settings (in case we ever need this)
        Logger.debugLog("We're using bank tab: " + banktab);
        Logger.debugLog("We're cooking " + product + " in this run at " + location + ".");

        // Initialize all the banking locations and other stuff
        initializeBankRects();

        // Initialize hop timer for this run
        hopActions();

        // Setting the correct zoom level
        setZoom();

        // Check if we are in the area we need to be in.
        checkArea();

    }

    // This is the main part of the script, poll gets looped constantly
    @Override
    public void poll() {

        //Logger.debugLog("Temp log statement.");

    }


    // Methods and stuff here
    private void checkArea() {
        Logger.debugLog("Checking which area we are in.");
        switch (location) {
            case "Cooks' Guild":
                checkLocation(cookingGuildRegion, cookingGuildArea, cookingGuildTile);
                break;
            case "Hosidius kitchen":
                playerPos = Walker.getPlayerPosition("maps/map.png");
                break;
            case "Mor Ul Rek":
                checkLocation(morulrekRegion, morulrekArea, morulrekTile);
                break;
            case "Myths' Guild":
                checkLocation(mythsguildRegion, myt, mythsguildTile);
                break;
            case "Nardah oven":
                playerPos = Walker.getPlayerPosition(nardahRegion);
                break;
            case "Rogues' Den":
                playerPos = Walker.getPlayerPosition(roguesRegion);
                break;
        }
    }

    private void setZoom() {
        Logger.debugLog("Setting correct zoom level based on location.");
        switch (location) {
            case "Cooks' Guild":
                Game.setZoom("4");
                break;
            case "Hosidius kitchen":
                Game.setZoom("2");
                break;
            case "Nardah oven":
                Game.setZoom("1");
                break;
            case "Myths' Guild":
            case "Rogues' Den":
            case "Mor Ul Rek":
                Game.setZoom("5");
                break;
        }
    }

    private void initializeBankRects() {
        Logger.debugLog("Initializing all the bank and furnace/oven areas.");
        // Myths Guild
        List<RectanglePair> mythsGuildRects = new ArrayList<>();
        mythsGuildRects.add(new RectanglePair(
                new Rectangle(409, 101, 104, 124),   // Bank Rectangle
                new Rectangle(550, 200, 84, 127)    // Range Rectangle
        ));
        bankRectangles.put("Myths' Guild", mythsGuildRects);

        // Nardah
        List<RectanglePair> nardahRects = new ArrayList<>();
        nardahRects.add(new RectanglePair(
                new Rectangle(325, 230, 14, 11),    // Bank Rectangle
                new Rectangle(550, 323, 24, 20)     // Range Rectangle
        ));
        nardahRects.add(new RectanglePair(
                new Rectangle(329, 202, 15, 13),    // Bank Rectangle
                new Rectangle(557, 357, 23, 21)     // Range Rectangle
        ));
        nardahRects.add(new RectanglePair(
                new Rectangle(332, 173, 7, 8),      // Bank Rectangle
                new Rectangle(563, 398, 26, 22)     // Range Rectangle
        ));
        nardahRects.add(new RectanglePair(
                new Rectangle(335, 158, 7, 6),      // Bank Rectangle
                new Rectangle(567, 420, 24, 26)     // Range Rectangle
        ));
        bankRectangles.put("Nardah", nardahRects);

        // Cooking Guild
        List<RectanglePair> cookGuildRects = new ArrayList<>();
        cookGuildRects.add(new RectanglePair(
                new Rectangle(484, 466, 72, 67),   // Bank Rectangle
                new Rectangle(359, 58, 46, 54)   // Range Rectangle
        ));
        cookGuildRects.add(new RectanglePair(
                new Rectangle(578, 464, 51, 64),   // Bank Rectangle
                new Rectangle(284, 56, 46, 56)   // Range Rectangle
        ));
        bankRectangles.put("Cooks' Guild", cookGuildRects);

        // Hosidius Kitchen
        List<RectanglePair> hosidiusRects = new ArrayList<>();
        hosidiusRects.add(new RectanglePair(
                new Rectangle(378, 434, 16, 20),   // Bank Rectangle
                new Rectangle(486, 118, 15, 21)    // Range Rectangle
        ));
        bankRectangles.put("Hosidius kitchen", hosidiusRects);

        // Mor Ul Rek
        List<RectanglePair> morulrekRects = new ArrayList<>();
        morulrekRects.add(new RectanglePair(
                new Rectangle(535, 264, 74, 103),   // Bank Rectangle
                new Rectangle(438, 164, 45, 14)    // Range Rectangle
        ));
        bankRectangles.put("Mor Ul Rek", morulrekRects);

        // Rogues' Den
        List<RectanglePair> roguesRects = new ArrayList<>();
        roguesRects.add(new RectanglePair(
                new Rectangle(284, 271, 24, 40),   // Bank Rectangle
                new Rectangle(405, 187, 49, 37)    // Range Rectangle
        ));
        bankRectangles.put("Rogues' Den", roguesRects);

    }

    private void hopActions() {
        Game.hop(hopProfile, useWDH, false);
    }

    private void checkLocation(RegionBox region, Area area, Tile tile) {
        playerPos = Walker.getPlayerPosition(region);
        if (Player.isTileWithinArea(playerPos, area)) {
            if (!Player.atTile(tile, region)) {
                Logger.debugLog("Walking to the " + location + " start tile.");
                Walker.step(tile, region);
                Condition.wait(() -> Player.atTile(tile, region), 200, 20);
            } else {
                Logger.debugLog("We are located at the " + location + " start tile.");
            }
        } else {
            notInArea();
        }
    }

    private void notInArea() {
        Logger.log("We are not within the " + location + " area. Please move there and restart the script.");
        Logout.logout();
        Script.forceStop();
    }

    public RectanglePair getRectanglePair(String bankName) {
        List<RectanglePair> pairs = bankRectangles.get(bankName);
        if (pairs != null && !pairs.isEmpty()) {
            return pairs.get(random.nextInt(pairs.size()));
        }
        return null;
    }

}

