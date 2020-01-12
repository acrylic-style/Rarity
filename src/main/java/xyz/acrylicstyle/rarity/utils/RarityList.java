package xyz.acrylicstyle.rarity.utils;

import org.bukkit.ChatColor;

public enum RarityList {
    SPECIAL("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "SPECIAL", ChatColor.LIGHT_PURPLE),
    LEGENDARY("" + ChatColor.GOLD + ChatColor.BOLD + "LEGENDARY", ChatColor.GOLD),
    EPIC("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "EPIC", ChatColor.DARK_PURPLE),
    RARE("" + ChatColor.BLUE + ChatColor.BOLD + "RARE", ChatColor.BLUE),
    UNCOMMON("" + ChatColor.GREEN + ChatColor.BOLD + "UNCOMMON", ChatColor.GREEN),
    COMMON("" + ChatColor.WHITE + ChatColor.BOLD + "COMMON", ChatColor.WHITE);

    private String name;
    private ChatColor color;

    RarityList(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public ChatColor getColor() { return color; }
}
