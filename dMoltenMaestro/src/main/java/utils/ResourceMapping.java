package utils;

import helpers.utils.ItemList;

import static helpers.Interfaces.Logger;

public enum ResourceMapping {
    // Bars
    BRONZE_BAR(ItemList.COPPER_ORE_436, ItemList.TIN_ORE_438, ItemList.BRONZE_BAR_2349),
    IRON_BAR(ItemList.IRON_ORE_440, null, ItemList.IRON_BAR_2351),
    SILVER_BAR(ItemList.SILVER_ORE_442, null, ItemList.SILVER_BAR_2355),
    STEEL_BAR(ItemList.IRON_ORE_440, ItemList.COAL_453, ItemList.STEEL_BAR_2353),
    GOLD_BAR(ItemList.GOLD_ORE_444, null, ItemList.GOLD_BAR_2357),
    MITHRIL_BAR(ItemList.MITHRIL_ORE_447, ItemList.COAL_453, ItemList.MITHRIL_BAR_2359),
    ADAMANTITE_BAR(ItemList.ADAMANTITE_ORE_449, ItemList.COAL_453, ItemList.ADAMANTITE_BAR_2361),
    RUNITE_BAR(ItemList.RUNITE_ORE_451, ItemList.COAL_453, ItemList.RUNITE_BAR_2363),

    // Rings
    OPAL_RING(ItemList.OPAL_1609, ItemList.SILVER_BAR_2355, ItemList.OPAL_RING_21081),
    JADE_RING(ItemList.JADE_1611, ItemList.SILVER_BAR_2355, ItemList.JADE_RING_21084),
    TOPAZ_RING(ItemList.RED_TOPAZ_1613, ItemList.SILVER_BAR_2355, ItemList.TOPAZ_RING_21087),
    SAPPHIRE_RING(ItemList.SAPPHIRE_1607, ItemList.GOLD_BAR_2357, ItemList.SAPPHIRE_RING_1637),
    EMERALD_RING(ItemList.EMERALD_1605, ItemList.GOLD_BAR_2357, ItemList.EMERALD_RING_1639),
    RUBY_RING(ItemList.RUBY_1603, ItemList.GOLD_BAR_2357, ItemList.RUBY_RING_1641),
    DIAMOND_RING(ItemList.DIAMOND_1601, ItemList.GOLD_BAR_2357, ItemList.DIAMOND_RING_1643),
    DRAGONSTONE_RING(ItemList.DRAGONSTONE_1615, ItemList.GOLD_BAR_2357, ItemList.DRAGONSTONE_RING_1645),
    ONYX_RING(ItemList.ONYX_6573, ItemList.GOLD_BAR_2357, ItemList.ONYX_RING_6575),
    ZENYTE_RING(ItemList.ZENYTE_19493, ItemList.GOLD_BAR_2357, ItemList.ZENYTE_RING_19538),

    // Necklaces
    OPAL_NECKLACE(ItemList.OPAL_1609, ItemList.SILVER_BAR_2355, ItemList.OPAL_NECKLACE_21090),
    JADE_NECKLACE(ItemList.JADE_1611, ItemList.SILVER_BAR_2355, ItemList.JADE_NECKLACE_21093),
    TOPAZ_NECKLACE(ItemList.RED_TOPAZ_1613, ItemList.SILVER_BAR_2355, ItemList.TOPAZ_NECKLACE_21096),
    SAPPHIRE_NECKLACE(ItemList.SAPPHIRE_1607, ItemList.GOLD_BAR_2357, ItemList.SAPPHIRE_NECKLACE_1656),
    EMERALD_NECKLACE(ItemList.EMERALD_1605, ItemList.GOLD_BAR_2357, ItemList.EMERALD_NECKLACE_1658),
    RUBY_NECKLACE(ItemList.RUBY_1603, ItemList.GOLD_BAR_2357, ItemList.RUBY_NECKLACE_1660),
    DIAMOND_NECKLACE(ItemList.DIAMOND_1601, ItemList.GOLD_BAR_2357, ItemList.DIAMOND_NECKLACE_1662),
    DRAGONSTONE_NECKLACE(ItemList.DRAGONSTONE_1615, ItemList.GOLD_BAR_2357, ItemList.DRAGON_NECKLACE_1664),
    ONYX_NECKLACE(ItemList.ONYX_6573, ItemList.GOLD_BAR_2357, ItemList.ONYX_NECKLACE_6577),
    ZENYTE_NECKLACE(ItemList.ZENYTE_19493, ItemList.GOLD_BAR_2357, ItemList.ZENYTE_NECKLACE_19535),

    // Bracelets
    OPAL_BRACELET(ItemList.OPAL_1609, ItemList.SILVER_BAR_2355, ItemList.OPAL_BRACELET_21117),
    JADE_BRACELET(ItemList.JADE_1611, ItemList.SILVER_BAR_2355, ItemList.JADE_BRACELET_21120),
    TOPAZ_BRACELET(ItemList.RED_TOPAZ_1613, ItemList.SILVER_BAR_2355, ItemList.TOPAZ_BRACELET_21123),
    SAPPHIRE_BRACELET(ItemList.SAPPHIRE_1607, ItemList.GOLD_BAR_2357, ItemList.SAPPHIRE_BRACELET_11071),
    EMERALD_BRACELET(ItemList.EMERALD_1605, ItemList.GOLD_BAR_2357, ItemList.EMERALD_BRACELET_11076),
    RUBY_BRACELET(ItemList.RUBY_1603, ItemList.GOLD_BAR_2357, ItemList.RUBY_BRACELET_11085),
    DIAMOND_BRACELET(ItemList.DIAMOND_1601, ItemList.GOLD_BAR_2357, ItemList.DIAMOND_BRACELET_11092),
    DRAGONSTONE_BRACELET(ItemList.DRAGONSTONE_1615, ItemList.GOLD_BAR_2357, ItemList.DRAGONSTONE_BRACELET_11115),
    ONYX_BRACELET(ItemList.ONYX_6573, ItemList.GOLD_BAR_2357, ItemList.ONYX_BRACELET_11130),
    ZENYTE_BRACELET(ItemList.ZENYTE_19493, ItemList.GOLD_BAR_2357, ItemList.ZENYTE_BRACELET_19492),

    // Amulets
    OPAL_AMULET(ItemList.OPAL_1609, ItemList.SILVER_BAR_2355, ItemList.OPAL_AMULET_U_21099),
    JADE_AMULET(ItemList.JADE_1611, ItemList.SILVER_BAR_2355, ItemList.JADE_AMULET_U_21102),
    TOPAZ_AMULET(ItemList.RED_TOPAZ_1613, ItemList.SILVER_BAR_2355, ItemList.TOPAZ_AMULET_U_21105),
    SAPPHIRE_AMULET(ItemList.SAPPHIRE_1607, ItemList.GOLD_BAR_2357, ItemList.SAPPHIRE_AMULET_U_1675),
    EMERALD_AMULET(ItemList.EMERALD_1605, ItemList.GOLD_BAR_2357, ItemList.EMERALD_AMULET_U_1677),
    RUBY_AMULET(ItemList.RUBY_1603, ItemList.GOLD_BAR_2357, ItemList.RUBY_AMULET_U_1679),
    DIAMOND_AMULET(ItemList.DIAMOND_1601, ItemList.GOLD_BAR_2357, ItemList.DIAMOND_AMULET_U_1681),
    DRAGONSTONE_AMULET(ItemList.DRAGONSTONE_1615, ItemList.GOLD_BAR_2357, ItemList.DRAGONSTONE_AMULET_U_1683),
    ONYX_AMULET(ItemList.ONYX_6573, ItemList.GOLD_BAR_2357, ItemList.ONYX_AMULET_U_6579),
    ZENYTE_AMULET(ItemList.ZENYTE_19493, ItemList.GOLD_BAR_2357, ItemList.ZENYTE_AMULET_U_19501),

    // Tiaras
    SILVER_TIARA(ItemList.SILVER_BAR_2355, null, ItemList.TIARA_5525),
    GOLD_TIARA(ItemList.GOLD_BAR_2357, null, ItemList.GOLD_TIARA_26788);

    private final Integer resourceItemID1;
    private final Integer resourceItemID2;
    private final Integer resultItemID;

    ResourceMapping(Integer resourceItemID1, Integer resourceItemID2, Integer resultItemID) {
        this.resourceItemID1 = resourceItemID1;
        this.resourceItemID2 = resourceItemID2;
        this.resultItemID = resultItemID;
    }

    public Integer getResourceItemID1() {
        if (resourceItemID1 != null) {
            Logger.debugLog("ResourceItemID1 set to: " + resourceItemID1);
            return resourceItemID1;
        } else {
            Logger.debugLog("ResourceItemID1 has not been set.");
            return -1;
        }
    }

    public Integer getResourceItemID2() {
        if (resourceItemID2 != null) {
            Logger.debugLog("ResourceItemID2 set to: " + resourceItemID2);
            return resourceItemID2;
        } else {
            Logger.debugLog("ResourceItemID2 has not been set.");
            return -1;
        }
    }

    public Integer getResultItemID() {
        if (resultItemID != null) {
            Logger.debugLog("ResultItemID set to: " + resultItemID);
            return resultItemID;
        } else {
            Logger.debugLog("ResultItemID has not been set.");
            return -1;
        }
    }

    public static ResourceMapping fromString(String resourceName) {
        try {
            ResourceMapping mapping = ResourceMapping.valueOf(resourceName.replace(" ", "_").toUpperCase());
            Logger.debugLog("Successfully mapped resourceName: " + resourceName + " to enum: " + mapping.name());
            return mapping;
        } catch (IllegalArgumentException e) {
            Logger.debugLog("Failed to map resourceName: " + resourceName + " to a valid ResourceMapping enum.");
            return null; // Handle invalid resource names gracefully
        }
    }
}
