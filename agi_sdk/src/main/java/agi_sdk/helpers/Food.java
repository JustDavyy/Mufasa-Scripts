package agi_sdk.helpers;

import helpers.utils.ItemList;

public enum Food {
    NONE(0),
    CAKE(ItemList.CAKE_1891),
    TROUT(ItemList.TROUT_333),
    SALMON(ItemList.SALMON_329),
    TUNA(ItemList.TUNA_361),
    JUG_OF_WINE(ItemList.JUG_OF_WINE_1993),
    LOBSTER(ItemList.LOBSTER_379),
    SWORDFISH(ItemList.SWORDFISH_373),
    POTATO_WITH_CHEESE(ItemList.POTATO_WITH_CHEESE_6705),
    MONKFISH(ItemList.MONKFISH_7946),
    KARAMBWAN(ItemList.COOKED_KARAMBWAN_3144),
    SHARK(ItemList.SHARK_385),
    MANTA_RAY(ItemList.MANTA_RAY_391),
    ANGLERFISH(ItemList.ANGLERFISH_13441);

    private final int itemId;

    Food(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

    public static Food getFoodById(int id) {
        for (Food food : Food.values()) {
            if (food.itemId == id) {
                return food;
            }
        }
        return NONE; // Default if no match found
    }
}
