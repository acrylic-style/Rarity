package xyz.acrylicstyle.rarity.items;

import util.Collection;
import util.ICollection;
import util.ReflectionHelper;
import xyz.acrylicstyle.rarity.utils.Stats;

import java.util.Collections;

@SuppressWarnings("unused")
public final class VanillaItemsStats {
    private VanillaItemsStats() {}

    public static final Collection<Stats, Integer> WOOD_SWORD = ICollection.asCollection(Collections.singletonMap(Stats.DAMAGE, 10));
    public static final Collection<Stats, Integer> STONE_SWORD = ICollection.asCollection(Collections.singletonMap(Stats.DAMAGE, 15));
    public static final Collection<Stats, Integer> IRON_SWORD = ICollection.asCollection(Collections.singletonMap(Stats.DAMAGE, 20));
    public static final Collection<Stats, Integer> GOLD_SWORD = ICollection.asCollection(Collections.singletonMap(Stats.DAMAGE, 25));
    public static final Collection<Stats, Integer> DIAMOND_SWORD = ICollection.asCollection(Collections.singletonMap(Stats.DAMAGE, 30));
    public static final Collection<Stats, Integer> DIAMOND_BLOCK = ICollection.asCollection(Collections.singletonMap(Stats.DEFENCE, 5));
    public static final Collection<Stats, Integer> DIAMOND_CHESTPLATE = ICollection.asCollection(Collections.singletonMap(Stats.DEFENCE, 30));
    public static final Collection<Stats, Integer> DIAMOND_HELMET = ICollection.asCollection(Collections.singletonMap(Stats.DEFENCE, 20));
    public static final Collection<Stats, Integer> DIAMOND_LEGGINGS = ICollection.asCollection(Collections.singletonMap(Stats.DEFENCE, 30));
    public static final Collection<Stats, Integer> DIAMOND_BOOTS = ICollection.asCollection(Collections.singletonMap(Stats.DEFENCE, 20));

    @SuppressWarnings("unchecked")
    public static Collection<Stats, Integer> getByName(String name) {
        try {
            return (Collection<Stats, Integer>) ReflectionHelper.getField(VanillaItemsStats.class, null, name);
        } catch (Exception e) {
            return null;
        }
    }
}
