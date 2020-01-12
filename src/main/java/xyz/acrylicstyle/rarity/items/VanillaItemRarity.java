package xyz.acrylicstyle.rarity.items;

import util.Collection;
import util.ReflectionHelper;
import xyz.acrylicstyle.rarity.utils.RarityList;
import xyz.acrylicstyle.rarity.utils.Stats;

public final class VanillaItemRarity {
    private VanillaItemRarity() {}

    public static final RarityList WOOD_SWORD = RarityList.COMMON;
    public static final RarityList STONE_SWORD = RarityList.COMMON;
    public static final RarityList IRON_SWORD = RarityList.COMMON;
    public static final RarityList GOLD_SWORD = RarityList.COMMON;
    public static final RarityList DIAMOND_SWORD = RarityList.COMMON;

    public static RarityList getByName(String name) {
        try {
            return (RarityList) ReflectionHelper.getField(VanillaItemsStats.class, null, name);
        } catch (Exception e) {
            return null;
        }
    }
}
