package xyz.acrylicstyle.rarity.utils;

import org.bukkit.ChatColor;

@SuppressWarnings("unused")
public enum Stats {
    DAMAGE("Damage"),
    STRENGTH("Strength"),
    CRIT_DAMAGE("Crit Damage", "%"),
    CRIT_CHANCE("Crit Chance", "%"),
    HEALTH("Health", ChatColor.GREEN),
    DEFENCE("Defence", ChatColor.GREEN),
    INTELLIGENCE("Intelligence", ChatColor.GREEN),
    TRUE_DEFENCE("True Defence", ChatColor.WHITE),
    TRUE_DAMAGE("True Damage", ChatColor.WHITE),
    ATTACK_SPEED("Attack Speed", "%"),
    SPEED("Speed", ChatColor.GREEN);

    private String name;
    private String suffix = "";
    private ChatColor color = ChatColor.RED;

    Stats(String name) {
        this.name = name;
    }

    Stats(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    Stats(String name, String suffix) {
        this.name = name;
        this.suffix = suffix;
    }

    Stats(String name, String suffix, ChatColor color) {
        this.name = name;
        this.suffix = suffix;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getSuffix() {
        return suffix;
    }

    public ChatColor getColor() {
        return color;
    }
}
