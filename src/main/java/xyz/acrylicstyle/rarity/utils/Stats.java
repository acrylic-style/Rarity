package xyz.acrylicstyle.rarity.utils;

import org.bukkit.ChatColor;

@SuppressWarnings("unused")
public enum Stats {
    DAMAGE("Damage"),
    STRENGTH("Strength", ChatColor.RED, '\u2741'),
    CRIT_DAMAGE("Crit Damage", "%", ChatColor.RED, '\u2623'),
    CRIT_CHANCE("Crit Chance", "%", ChatColor.RED, '\u2620'),
    HEALTH("Health", ChatColor.GREEN, '\u2764'),
    DEFENCE("Defence", ChatColor.GREEN, '\u2748'),
    INTELLIGENCE("Intelligence", ChatColor.GREEN, '\u270E'),
    TRUE_DEFENCE("True Defence", ChatColor.WHITE, '\u2742'),
    TRUE_DAMAGE("True Damage", ChatColor.WHITE),
    ATTACK_SPEED("Attack Speed", "%"),
    SPEED("Speed", ChatColor.GREEN, '\u2726');

    private String name;
    private String suffix = "";
    private ChatColor color = ChatColor.RED;
    private Character symbol = null;

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

    Stats(String name, ChatColor color, Character symbol) {
        this.name = name;
        this.color = color;
        this.symbol = symbol;
    }

    Stats(String name, String suffix, ChatColor color) {
        this.name = name;
        this.suffix = suffix;
        this.color = color;
    }

    Stats(String name, String suffix, ChatColor color, Character symbol) {
        this.name = name;
        this.suffix = suffix;
        this.color = color;
        this.symbol = symbol;
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

    public Character getSymbol() {
        return symbol;
    }
}
