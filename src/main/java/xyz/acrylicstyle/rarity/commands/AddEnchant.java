package xyz.acrylicstyle.rarity.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import util.ICollectionList;
import xyz.acrylicstyle.rarity.utils.Utils;

public class AddEnchant implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length <= 1) {
            sender.sendMessage(ChatColor.RED + "You must specify the enchantment and level.");
            sender.sendMessage(ChatColor.RED + "Usage: /addenchant <enchantment> <level>");
            return true;
        }
        try {
            Integer.parseInt(args[1]);
        } catch (NumberFormatException ignore) {
            sender.sendMessage(ChatColor.RED + "Please specify valid number!");
            return true;
        }
        try {
            if (!ICollectionList.asList(Enchantment.values()).map(Enchantment::getName).contains(args[0]) && !ICollectionList.asList(Enchantment.values()).map(Enchantment::getId).contains(Integer.parseInt(args[0]))) {
                sender.sendMessage(ChatColor.RED + "Invalid enchant!");
                return true;
            }
        } catch (NumberFormatException ignore) {
            sender.sendMessage(ChatColor.RED + "Invalid enchant!");
            return true;
        }
        ItemStack item = player.getInventory().getItemInHand();
        ItemMeta meta = item.getItemMeta();
        Enchantment ench = Enchantment.getByName(args[0]) == null ? Enchantment.getById(Integer.parseInt(args[0])) : Enchantment.getByName(args[0]);
        meta.addEnchant(ench, Integer.parseInt(args[1]), true);
        item.setItemMeta(meta);
        player.getInventory().setItemInHand(Utils.convertVanillaItem(item));
        sender.sendMessage(ChatColor.GREEN + "Successfully enchanted the item.");
        return true;
    }
}
