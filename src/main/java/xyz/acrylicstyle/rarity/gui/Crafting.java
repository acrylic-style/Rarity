package xyz.acrylicstyle.rarity.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.rarity.Rarity;
import xyz.acrylicstyle.rarity.utils.Utils;

import java.util.*;

public class Crafting implements InventoryHolder, Listener {
    private Inventory inventory;
    private List<Integer> blockedSlots = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 14, 15, 16, 17, 18, 22, 23, 25, 26, 27, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44));
    private ItemStack barrier;

    public Crafting() {
        ItemStack blackGlass = new ItemStack(Material.STAINED_GLASS_PANE);
        blackGlass.setDurability((short) 15);
        ItemMeta meta = blackGlass.getItemMeta();
        meta.setDisplayName(" ");
        blackGlass.setItemMeta(meta);
        barrier = new ItemStack(Material.BARRIER);
        ItemMeta meta2 = barrier.getItemMeta();
        meta2.setDisplayName(" ");
        barrier.setItemMeta(meta2);
        inventory = Bukkit.createInventory(this, 45, "Crafting");
        inventory.setItem(0, blackGlass);
        inventory.setItem(1, blackGlass);
        inventory.setItem(2, blackGlass);
        inventory.setItem(3, blackGlass);
        inventory.setItem(4, blackGlass);
        inventory.setItem(5, blackGlass);
        inventory.setItem(6, blackGlass);
        inventory.setItem(7, blackGlass);
        inventory.setItem(8, blackGlass);
        // ---
        inventory.setItem(9, blackGlass);
        // 10 (Crafting slot #0)
        // 11 (Crafting slot #1)
        // 12 (Crafting slot #2)
        inventory.setItem(13, blackGlass);
        inventory.setItem(14, blackGlass);
        inventory.setItem(15, blackGlass);
        inventory.setItem(16, blackGlass);
        inventory.setItem(17, blackGlass);
        // ---
        inventory.setItem(18, blackGlass);
        // 19 (Crafting slot #3)
        // 20 (Crafting slot #4)
        // 21 (Crafting slot #5)
        inventory.setItem(22, blackGlass);
        inventory.setItem(23, blackGlass);
        inventory.setItem(24, barrier); // (Result item slot)
        inventory.setItem(25, blackGlass);
        inventory.setItem(26, blackGlass);
        // ---
        inventory.setItem(27, blackGlass);
        // 28 (Crafting slot #6)
        // 29 (Crafting slot #7)
        // 30 (Crafting slot #8)
        inventory.setItem(31, blackGlass);
        inventory.setItem(32, blackGlass);
        inventory.setItem(33, blackGlass);
        inventory.setItem(34, blackGlass);
        inventory.setItem(35, blackGlass);
        // ---
        inventory.setItem(36, blackGlass);
        inventory.setItem(37, blackGlass);
        inventory.setItem(38, blackGlass);
        inventory.setItem(39, blackGlass);
        inventory.setItem(40, blackGlass);
        inventory.setItem(41, blackGlass);
        inventory.setItem(42, blackGlass);
        inventory.setItem(43, blackGlass);
        inventory.setItem(44, blackGlass);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() != this) return;
        checkRecipe(e.getInventory());
        e.getInventorySlots().forEach(i -> {
            if (blockedSlots.contains(i)) e.setCancelled(true);
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() == this && e.getClick() == ClickType.DOUBLE_CLICK) {
            e.setCancelled(true);
            return;
        }
        if (e.getClickedInventory() == null || e.getClickedInventory().getHolder() != this) return;
        if (blockedSlots.contains(e.getSlot()) || e.getCurrentItem().getType() == Material.BARRIER) {
            e.setCancelled(true);
            return;
        }
        if (e.getAction() == InventoryAction.PLACE_SOME && e.getSlot() == 24) {
            e.setCancelled(true);
            return;
        }
        if (e.getSlot() == 24 && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (e.getClick() == ClickType.SHIFT_LEFT) {
                        if (!Utils.hasFullInventory(e.getWhoClicked().getInventory())) {
                            e.getWhoClicked().getInventory().addItem(e.getInventory().getItem(24));
                            e.getInventory().setItem(24, barrier);
                        }
                    }
                    e.getInventory().setItem(10, new ItemStack(Material.AIR));
                    e.getInventory().setItem(11, new ItemStack(Material.AIR));
                    e.getInventory().setItem(12, new ItemStack(Material.AIR));
                    e.getInventory().setItem(19, new ItemStack(Material.AIR));
                    e.getInventory().setItem(20, new ItemStack(Material.AIR));
                    e.getInventory().setItem(21, new ItemStack(Material.AIR));
                    e.getInventory().setItem(28, new ItemStack(Material.AIR));
                    e.getInventory().setItem(29, new ItemStack(Material.AIR));
                    e.getInventory().setItem(30, new ItemStack(Material.AIR));
                }
            }.runTask(Rarity.getPlugin(Rarity.class));
            return;
        }
        checkRecipe(e.getInventory());
    }

    private void checkRecipe(Inventory inventory) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack[] matrix = new ItemStack[9];
                matrix[0] = inventory.getItem(10);
                matrix[1] = inventory.getItem(11);
                matrix[2] = inventory.getItem(12);
                matrix[3] = inventory.getItem(19);
                matrix[4] = inventory.getItem(20);
                matrix[5] = inventory.getItem(21);
                matrix[6] = inventory.getItem(28);
                matrix[7] = inventory.getItem(29);
                matrix[8] = inventory.getItem(30);
                ItemStack result = Utils.recipes.get(Arrays.toString(matrix));
                if (result == null) result = Utils.shapelessRecipes.get(Arrays.toString(matrix));
                if (result == null) result = getShapelessRecipe(matrix);
                if (result == null) {
                    inventory.setItem(24, barrier);
                    return;
                }
                inventory.setItem(24, result);
            }
        }.runTask(Rarity.getPlugin(Rarity.class));
    }

    private ItemStack getShapelessRecipe(ItemStack[] matrix) {
        CollectionList<CollectionList<ItemStack>> items = Utils.shapelessRecipesTest.keysList().filter(c -> c.containsAll(ICollectionList.asList(matrix)));
        if (items == null) return null;
        return Utils.shapelessRecipesTest.get(items.first());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() != this) return;
        if (e.getInventory().getItem(10) != null && e.getInventory().getItem(10).getType() != Material.AIR)
            e.getPlayer().getInventory().addItem(e.getInventory().getItem(10));
        if (e.getInventory().getItem(11) != null && e.getInventory().getItem(11).getType() != Material.AIR)
            e.getPlayer().getInventory().addItem(e.getInventory().getItem(11));
        if (e.getInventory().getItem(12) != null && e.getInventory().getItem(12).getType() != Material.AIR)
            e.getPlayer().getInventory().addItem(e.getInventory().getItem(12));
        if (e.getInventory().getItem(19) != null && e.getInventory().getItem(19).getType() != Material.AIR)
            e.getPlayer().getInventory().addItem(e.getInventory().getItem(19));
        if (e.getInventory().getItem(20) != null && e.getInventory().getItem(20).getType() != Material.AIR)
            e.getPlayer().getInventory().addItem(e.getInventory().getItem(20));
        if (e.getInventory().getItem(21) != null && e.getInventory().getItem(21).getType() != Material.AIR)
            e.getPlayer().getInventory().addItem(e.getInventory().getItem(21));
        if (e.getInventory().getItem(28) != null && e.getInventory().getItem(28).getType() != Material.AIR)
            e.getPlayer().getInventory().addItem(e.getInventory().getItem(28));
        if (e.getInventory().getItem(29) != null && e.getInventory().getItem(29).getType() != Material.AIR)
            e.getPlayer().getInventory().addItem(e.getInventory().getItem(29));
        if (e.getInventory().getItem(30) != null && e.getInventory().getItem(30).getType() != Material.AIR)
            e.getPlayer().getInventory().addItem(e.getInventory().getItem(30));
    }
}
