package xyz.acrylicstyle.rarity.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import util.*;
import util.Collection;
import xyz.acrylicstyle.rarity.items.CustomItem;
import xyz.acrylicstyle.rarity.items.CustomRecipe;
import xyz.acrylicstyle.rarity.items.VanillaItemRarity;
import xyz.acrylicstyle.rarity.items.VanillaItemsStats;
import xyz.acrylicstyle.tomeito_core.providers.ConfigProvider;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class Utils {
    private Utils() {}

    public static CollectionList<String> getItemDefinitionStringFiles() {
        File itemsDir = new File("./plugins/Rarity/items/");
        String[] itemsArray = itemsDir.list();
        if (itemsArray == null) throw new NullPointerException("Couldn't find any items!");
        return ICollectionList.asList(itemsArray);
    }

    private static CollectionList<CustomItem> customItems;

    public static CollectionList<CustomItem> getCustomItems() {
        ICollectionList<String> files = getItemDefinitionStringFiles();
        return files.map(file -> {
            ConfigProvider config;
            try {
                config = new ConfigProvider("./plugins/Rarity/items/" + file);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            String id = config.getString("id");
            if (id == null) throw new NullPointerException("Item ID must be specified.");
            Material material = Material.valueOf(config.getString("material", "STONE"));
            String displayName = config.getString("displayName", "Unknown Item");
            List<String> lore = config.getStringList("lore");
            String rarityStr = config.getString("rarity", "COMMON");
            if (!ICollectionList.asList(RarityList.values()).map(Enum::name).contains(rarityStr)) rarityStr = "COMMON";
            RarityList rarity = RarityList.valueOf(rarityStr);
            Collection<Enchantment, Integer> enchantments = config.get("enchantments") == null ? null : ICollection.asCollection(config.getConfigSectionValue("enchantments", true))
                    .map((k, v) -> Enchantment.getByName(k), (k, v) -> (int) v);
            Collection<Stats, Integer> stats = config.get("stats") == null ? null : ICollection.asCollection(config.getConfigSectionValue("stats", true))
                    .map((k, v) -> Stats.valueOf(k), (k, v) -> (int) v);
            String category = config.getString("category", "");
            List<Map<?, ?>> recipesRawRaw = config.getMapList("recipe.recipe");
            CollectionList<Map.Entry<String, Object>> recipesRaw = ICollectionList.asList(recipesRawRaw)
                    .map(map -> new HashMap.SimpleEntry<>((String) map.keySet().toArray()[0], map.values().toArray()[0]));
            int amount = config.getInt("recipe.result.amount", 1);
            return new CustomItem(id, material, null, displayName, lore, enchantments, rarity, stats, category, recipesRaw, amount);
        });
    }

    public static CollectionList<CustomItem> getCustomItemsCached() {
        if (Utils.customItems == null) Utils.customItems = Utils.getCustomItems();
        return Utils.customItems;
    }

    /**
     * Gets custom item by ID.
     * @param id Custom item ID. Case-sensitive.
     * @return Custom item if found, null otherwise
     */
    public static CustomItem getCustomItemById(String id) {
        try {
            return Utils.getCustomItemsCached().filter(custom -> custom.getId().equals(id)).first();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static CustomItem getCustomItemByDisplayName(String displayName) {
        try {
            return Utils.getCustomItemsCached().filter(custom -> custom.getDisplayName().equals(ChatColor.stripColor(displayName))).first();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static StringCollection<ItemStack> recipes = new StringCollection<>();
    public static StringCollection<ItemStack> shapelessRecipes = new StringCollection<>();
    public static Collection<CollectionList<ItemStack>, ItemStack> shapelessRecipesTest = new Collection<>();
    public static StringCollection<Collection<Stats, Integer>> stats = new StringCollection<>();

    public static void initializeRecipes() {
        Utils.getCustomItems().forEach(item -> {
            stats.add(item.getDisplayName(), item.getStats());
            ItemStack[] matrix = new ItemStack[9];
            item.getRecipesRaw().foreach((map, index) -> {
                String id = map.getKey();
                CustomItem recipeItem = getCustomItemById(id);
                int amount = (int) map.getValue();
                if (recipeItem == null) {
                    if (id.equalsIgnoreCase("null")) {
                        matrix[index] = null;
                    } else {
                        ItemStack recipeVanillaItem = convertVanillaItem(Material.valueOf(id));
                        recipeVanillaItem.setAmount(amount);
                        matrix[index] = recipeVanillaItem;
                    }
                } else {
                    ItemStack recipeItem2 = recipeItem.toItemStack().clone();
                    recipeItem2.setAmount(amount);
                    matrix[index] = recipeItem2;
                }
            });
            int resultAmount = item.getResultAmount();
            ItemStack resultItem2 = item.toItemStack().clone();
            resultItem2.setAmount(resultAmount);
            item.setRecipe(new CustomRecipe(matrix, resultItem2));
            recipes.add(Arrays.toString(matrix), resultItem2);
        });
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
                String[] shape = shapedRecipe.getShape();
                List<ItemStack> items = new ArrayList<>();
                ICollectionList
                        .asList(shape)
                        .map((str, index) -> str.toCharArray())
                        .forEach(character -> {
                            for (char c : character) {
                                items.add(shapedRecipe.getIngredientMap().get(c));
                            }
                });
                recipes.add(Arrays.toString(expandItemStackArray(items.toArray(new ItemStack[0]), 9)), shapedRecipe.getResult());
            } else if (recipe instanceof ShapelessRecipe) {
                ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
                shapelessRecipes.add(Arrays.toString(shapelessRecipe.getIngredientList().toArray(new ItemStack[0])), shapelessRecipe.getResult());
                shapelessRecipesTest.add(ICollectionList.asList(shapelessRecipe.getIngredientList().toArray(new ItemStack[0])), shapelessRecipe.getResult());
            }
        });
    }

    public static boolean hasFullInventory(Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }

    public static String toStringFromInteger(int number) {
        if (number < 1) return "TOO_LOW_NUMBER";
        if (number == 1000) return "L";
        if (number > 10) return "TOO_HIGH_NUMBER";
        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            default: return "UNKNOWN";
        }
    }

    public static String toStringFromEnchantment(Enchantment ench) {
        if (Enchantment.DAMAGE_ALL.equals(ench)) {
            return "Sharpness";
        } else if (Enchantment.ARROW_FIRE.equals(ench)) {
            return "Flame";
        } else if (Enchantment.ARROW_INFINITE.equals(ench)) {
            return "Infinite";
        } else if (Enchantment.DAMAGE_ARTHROPODS.equals(ench)) {
            return "Bane of Arthropods";
        } else if (Enchantment.DAMAGE_UNDEAD.equals(ench)) {
            return "Smite";
        } else if (Enchantment.SILK_TOUCH.equals(ench)) {
            return "Silk Touch";
        } else if (Enchantment.THORNS.equals(ench)) {
            return "Thorns";
        } else if (Enchantment.ARROW_DAMAGE.equals(ench)) {
            return "Power";
        } else if (Enchantment.ARROW_KNOCKBACK.equals(ench)) {
            return "Punch";
        } else if (Enchantment.DIG_SPEED.equals(ench)) {
            return "Efficiency";
        } else if (Enchantment.LOOT_BONUS_BLOCKS.equals(ench)) {
            return "Fortune";
        } else if (Enchantment.LOOT_BONUS_MOBS.equals(ench)) {
            return "Looting";
        } else if (Enchantment.LURE.equals(ench)) {
            return "Lure";
        } else if (Enchantment.LUCK.equals(ench)) {
            return "Luck of the Sea";
        } else if (Enchantment.DEPTH_STRIDER.equals(ench)) {
            return "Depth Strider";
        } else if (Enchantment.OXYGEN.equals(ench)) {
            return "Respiration";
        } else if (Enchantment.WATER_WORKER.equals(ench)) {
            return "Aqua Affinity";
        } else if (Enchantment.PROTECTION_PROJECTILE.equals(ench)) {
            return "Projectile Protection";
        } else if (Enchantment.PROTECTION_ENVIRONMENTAL.equals(ench)) {
            return "Protection";
        } else if (Enchantment.PROTECTION_EXPLOSIONS.equals(ench)) {
            return "Blast Protection";
        } else if (Enchantment.KNOCKBACK.equals(ench)) {
            return "Knockback";
        } else if (Enchantment.PROTECTION_FIRE.equals(ench)) {
            return "Fire Protection";
        } else if (Enchantment.PROTECTION_FALL.equals(ench)) {
            return "Feather Falling";
        } else if (Enchantment.DURABILITY.equals(ench)) {
            return "Unbreaking";
        } else if (Enchantment.FIRE_ASPECT.equals(ench)) {
            return "Fire Aspect";
        } else return "Unknown Enchantment";
    }

    public static String getFriendlyName(ItemStack itemStack) {
        String name = itemStack.getType().toString().replaceAll("_", " ").toLowerCase();
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        name = name.replaceFirst("Tnt", "TNT");
        name = name.replaceFirst("Clay brick", "Brick");
        return name;
    }

    public static int getBonusStats(ItemStack item, Stats stats) {
        if (stats == Stats.DEFENCE) return getEnchantmentNotNull(item, Enchantment.PROTECTION_ENVIRONMENTAL)*5;
        return 0;
    }

    public static ItemStack convertVanillaItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return new ItemStack(Material.AIR);
        ItemMeta meta = item.getItemMeta();
        String displayName = meta.getDisplayName();
        CustomItem customItem = Utils.getCustomItemByDisplayName(displayName);
        if (customItem == null) {
            List<String> lore = new ArrayList<>();
            Collection<Stats, Integer> statsCollection = VanillaItemsStats.getByName(item.getType().name());
            if (statsCollection != null)
                statsCollection.forEach((stats, level) -> lore.add(ChatColor.GRAY + stats.getName() + ": " + stats.getColor() + "+" + (getBonusStats(item, stats) + level) + stats.getSuffix()));
            if (meta.hasEnchants()) lore.add("");
            if (meta.hasEnchants())
                meta.getEnchants().forEach((ench, level) -> lore.add(ChatColor.BLUE + Utils.toStringFromEnchantment(ench) + " " + Utils.toStringFromInteger(level)));
            if (meta.hasEnchants() || statsCollection != null) lore.add("");
            RarityList rarity = VanillaItemRarity.getByName(item.getType().name());
            if (rarity == null) rarity = RarityList.COMMON;
            lore.add(rarity.getName());
            meta.spigot().setUnbreakable(true);
            meta.setDisplayName(rarity.getColor() + Utils.getFriendlyName(item));
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
            if (meta.hasEnchants()) meta.getEnchants().forEach((ench, level) -> meta.addEnchant(ench, level, true));
        } else {
            if (meta.hasEnchants()) meta.getEnchants().forEach((ench, level) -> meta.addEnchant(ench, level, true));
            List<String> lore = new ArrayList<>();
            if (customItem.getStats() != null) customItem.getStats().forEach((stats, level) -> lore.add(ChatColor.GRAY + stats.getName() + ": " + stats.getColor() + "+" + (Utils.getBonusStats(item, stats) + level) + stats.getSuffix()));
            if (meta.hasEnchants()) lore.add("");
            if (meta.hasEnchants()) meta.getEnchants().forEach((ench, level) -> lore.add(ChatColor.BLUE + Utils.toStringFromEnchantment(ench) + " " + Utils.toStringFromInteger(level)));
            if (customItem.getLore() != null) lore.add("");
            if (customItem.getLore() != null) lore.addAll(ICollectionList.asList(customItem.getLore()).map(l -> ChatColor.translateAlternateColorCodes('&', l)));
            lore.add("");
            lore.add(customItem.getRarity().getName() + " " + customItem.getCategory());
            meta.spigot().setUnbreakable(true);
            meta.setDisplayName(customItem.getRarity().getColor() + customItem.getDisplayName());
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack convertVanillaItem(Material material) {
        if (material == Material.AIR) return new ItemStack(Material.AIR);
        return convertVanillaItem(new ItemStack(material));
    }

    public static Collection<UUID, Integer> playerMaxHealth = new Collection<>();
    public static Collection<UUID, Integer> playerHealth = new Collection<>();
    public static Collection<UUID, Integer> playerMaxDefence = new Collection<>();
    public static Collection<UUID, Integer> playerMaxIntelligence = new Collection<>();
    public static Collection<UUID, Integer> playerIntelligence = new Collection<>();

    public static int calculatePlayerMaxStats(Player player, Stats stat) {
        AtomicInteger sum = new AtomicInteger();
        player.getInventory().forEach(item -> statsCheck(stat, sum, item));
        for (ItemStack item : player.getInventory().getArmorContents()) {
            statsCheck(stat, sum, item);
        }
        return sum.get();
    }

    private static void statsCheck(Stats stat, AtomicInteger sum, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        Collection<Stats, Integer> stats = Utils.stats.get(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
        if (stats == null) stats = VanillaItemsStats.getByName(item.getType().name());
        if (stats != null && stats.get(stat) != null) sum.addAndGet(stats.get(stat));
        Integer protection = getEnchantment(item, Enchantment.PROTECTION_ENVIRONMENTAL);
        if (stat == Stats.DEFENCE && protection != null) sum.addAndGet(protection*5);
    }

    public static Integer getEnchantment(ItemStack item, Enchantment ench) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        if (!meta.hasEnchant(ench)) return null;
        return meta.getEnchantLevel(ench);
    }

    public static int getEnchantmentNotNull(ItemStack item, Enchantment ench) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasEnchant(ench)) return 0;
        return meta.getEnchantLevel(ench);
    }

    public static String buildStatsBar(Player player) {
        int health = calculatePlayerMaxStats(player, Stats.HEALTH)+100;
        int defence = calculatePlayerMaxStats(player, Stats.DEFENCE);
        int intelligence = calculatePlayerMaxStats(player, Stats.INTELLIGENCE)+100;
        playerMaxHealth.add(player.getUniqueId(), health);
        if (!playerHealth.containsKey(player.getUniqueId())) playerHealth.add(player.getUniqueId(), health);
        playerMaxDefence.add(player.getUniqueId(), defence);
        playerMaxIntelligence.add(player.getUniqueId(), intelligence);
        if (playerIntelligence.get(player.getUniqueId()) == null) playerIntelligence.add(player.getUniqueId(), intelligence);
        return "" + ChatColor.RED + playerHealth.get(player.getUniqueId()) + "/" + playerMaxHealth.get(player.getUniqueId()) + "❤     " + ChatColor.GREEN + playerMaxDefence.get(player.getUniqueId()) + " Defence     " + ChatColor.AQUA + playerIntelligence.get(player.getUniqueId()) + "/" + playerMaxIntelligence.get(player.getUniqueId()) + "✎ Mana";
    }

    public static double getDamageReduction(int defence) {
        return ((double)defence)/(defence+100D);
    }

    public static ItemStack[] expandItemStackArray(ItemStack[] array, int length) {
        ItemStack[] t = new ItemStack[length];
        for (int i = 0; i < length; i++) {
            if (array.length > i) {
                t[i] = array[i];
            } else {
                t[i] = null;
            }
        }
        return t;
    }

    public static void damagePlayer(Player player, int damage) {
        playerHealth.add(player.getUniqueId(), playerHealth.get(player.getUniqueId())-damage);
        player.setHealth(40 * ((float) playerHealth.get(player.getUniqueId()) / (float) playerMaxHealth.get(player.getUniqueId())));
    }
}
