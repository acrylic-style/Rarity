package xyz.acrylicstyle.rarity;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import util.ICollectionList;
import xyz.acrylicstyle.rarity.commands.AddEnchant;
import xyz.acrylicstyle.rarity.commands.GiveItem;
import xyz.acrylicstyle.rarity.gui.Crafting;
import xyz.acrylicstyle.rarity.packets.ActionBar;
import xyz.acrylicstyle.rarity.utils.Stats;
import xyz.acrylicstyle.rarity.utils.Utils;
import xyz.acrylicstyle.tomeito_core.utils.Log;

public class Rarity extends JavaPlugin implements Listener {

    public void onEnable() {
        Log.info("Registering events...");
        Bukkit.getPluginManager().registerEvents(this, this);
        Log.info("Registering recipes...");
        Utils.initializeRecipes();
        Log.info("Registering commands...");
        Bukkit.getPluginCommand("giveitem").setExecutor(new GiveItem());
        Bukkit.getPluginCommand("addenchant").setExecutor(new AddEnchant());
        Log.info("Enabled Rarity.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        int health = Utils.calculatePlayerMaxStats(e.getPlayer(), Stats.HEALTH)+100;
        e.getPlayer().setHealth(health);
        new BukkitRunnable() {
            @Override
            public void run() {
                ICollectionList.asList(e.getPlayer().getInventory().getContents()).foreach((item, index) ->
                        e.getPlayer().getInventory().setItem(index, Utils.convertVanillaItem(item)));
            }
        }.runTask(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getPlayer(e.getPlayer().getUniqueId()) == null) {
                    this.cancel();
                    return;
                }
                ActionBar.setActionBarWithoutException(e.getPlayer(), Utils.buildStatsBar(e.getPlayer()));
            }
        }.runTaskTimer(this, 20, 20);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        double damage = 25;
        if (e.getDamager().getType() == EntityType.PLAYER) {
            Player damager = (Player) e.getDamager();
            damage = Utils.calculatePlayerMaxStats(damager, Stats.TRUE_DAMAGE)
                    + (Utils.stats.get(damager.getItemInHand()) == null ? 0 : Utils.stats.get(damager.getItemInHand()).get(Stats.DAMAGE))
                    + (Utils.calculatePlayerMaxStats(damager, Stats.STRENGTH) * 3);
        }
        if (e.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) e.getEntity();
            double damageReduction = Utils.getDamageReduction(Utils.calculatePlayerMaxStats(player, Stats.DEFENCE) + Utils.calculatePlayerMaxStats(player, Stats.TRUE_DEFENCE));
            e.setDamage(5*damage-(damage*damageReduction));
        } else {
            e.setDamage(5*e.getDamage());
        }
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent e) {
        if (e.getInventory().getType() == InventoryType.WORKBENCH) {
            Crafting crafting = new Crafting();
            Bukkit.getPluginManager().registerEvents(crafting, this);
            e.setCancelled(true);
            e.getPlayer().openInventory(crafting.getInventory());
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        e.getEntity().setItemStack(Utils.convertVanillaItem(e.getEntity().getItemStack()));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        e.getItemDrop().setItemStack(Utils.convertVanillaItem(e.getItemDrop().getItemStack()));
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        e.getItem().setItemStack(Utils.convertVanillaItem(e.getItem().getItemStack()));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        e.setDamage(5*e.getDamage());
    }
}
