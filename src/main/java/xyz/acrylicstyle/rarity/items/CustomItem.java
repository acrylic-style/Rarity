package xyz.acrylicstyle.rarity.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import util.Collection;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.rarity.utils.RarityList;
import xyz.acrylicstyle.rarity.utils.Stats;
import xyz.acrylicstyle.rarity.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomItem {
    private String id;
    private Material material;
    private CustomRecipe recipe;
    private String displayName;
    private List<String> lore;
    private Collection<Enchantment, Integer> enchantments;
    private RarityList rarity;
    private Collection<Stats, Integer> stats;
    private String category;
    private CollectionList<Map.Entry<String, Object>> recipesRaw;
    private int resultAmount;

    public CustomItem(String id,
                      Material material,
                      CustomRecipe recipe,
                      String displayName,
                      List<String> lore,
                      Collection<Enchantment, Integer> enchantments,
                      RarityList rarity,
                      Collection<Stats, Integer> stats,
                      String category,
                      CollectionList<Map.Entry<String, Object>> recipesRaw,
                      int resultAmount) {
        this.id = id;
        this.material = material;
        this.recipe = recipe;
        this.displayName = displayName;
        this.lore = lore;
        this.enchantments = enchantments;
        this.rarity = rarity;
        this.stats = stats;
        this.category = category;
        this.recipesRaw = recipesRaw;
        this.resultAmount = resultAmount;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setRecipe(CustomRecipe recipe) {
        this.recipe = recipe;
    }

    public CustomRecipe getRecipe() {
        return recipe;
    }

    public List<String> getLore() {
        return lore;
    }

    public Collection<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public RarityList getRarity() {
        return rarity;
    }

    public String getDisplayName() {
        return this.rarity.getColor() + this.displayName;
    }

    public Collection<Stats, Integer> getStats() {
        return stats;
    }

    public CollectionList<Map.Entry<String, Object>> getRecipesRaw() {
        return recipesRaw;
    }

    public String getCategory() {
        return category;
    }

    public int getResultAmount() {
        return resultAmount;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        if (this.enchantments != null) this.enchantments.forEach((ench, level) -> meta.addEnchant(ench, level, true));
        List<String> lore = new ArrayList<>();
        if (this.stats != null) this.stats.forEach((stats, level) -> lore.add(ChatColor.GRAY + stats.getName() + ": " + stats.getColor() + "+" + (Utils.getBonusStats(item, stats) + level) + stats.getSuffix()));
        if (this.enchantments != null) lore.add("");
        if (this.enchantments != null) this.enchantments.forEach((ench, level) -> lore.add(ChatColor.BLUE + Utils.toStringFromEnchantment(ench) + " " + Utils.toStringFromInteger(level)));
        if (this.lore != null) lore.add("");
        if (this.lore != null) lore.addAll(ICollectionList.asList(this.lore).map(l -> ChatColor.translateAlternateColorCodes('&', l)));
        lore.add("");
        lore.add(this.rarity.getName() + " " + category);
        meta.spigot().setUnbreakable(true);
        meta.setDisplayName(this.rarity.getColor() + this.displayName);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
