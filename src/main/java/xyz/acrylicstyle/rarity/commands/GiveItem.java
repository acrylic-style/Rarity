package xyz.acrylicstyle.rarity.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.rarity.items.CustomItem;
import xyz.acrylicstyle.rarity.utils.Utils;

public class GiveItem implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "You must specify the item ID.");
            sender.sendMessage(ChatColor.RED + "Usage: /giveitem <item ID>");
            return true;
        }
        CustomItem item = Utils.getCustomItemById(args[0]);
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find item.");
            return true;
        }
        player.getInventory().addItem(item.toItemStack());
        return true;
    }
}
