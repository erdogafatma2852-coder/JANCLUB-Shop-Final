package me.janclub.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ShopPlugin extends JavaPlugin implements Listener {
    private Economy economy;
    private final Map<Integer, ShopItem> items = new HashMap<>();
    private final String title = "§5ѕʜᴏᴘ";

    private record ShopItem(Material material, String name, double price) {}

    @Override
    public void onEnable() {
        RegisteredServiceProvider<Economy> registration =
                getServer().getServicesManager().getRegistration(Economy.class);

        if (registration == null) {
            getLogger().severe("Vault ekonomi bulunamadi!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        economy = registration.getProvider();
        Bukkit.getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getCommand("shop")).setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player player) openMain(player);
            return true;
        });
    }

    private ItemStack make(Material material, String name, double price) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d" + name);
        meta.setLore(Arrays.asList(
                "§7Fiyat: §a" + price + "$",
                "§eSatın almak için tıkla"
        ));
        item.setItemMeta(meta);
        return item;
    }

    private void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, title);
        inv.setItem(11, category(Material.ENDER_CHEST, "§dEnd", "end"));
        inv.setItem(13, category(Material.BLAZE_ROD, "§6Nether", "nether"));
        inv.setItem(15, category(Material.TOTEM_OF_UNDYING, "§bGear", "gear"));
        inv.setItem(22, category(Material.COOKED_BEEF, "§aFood", "food"));
        player.openInventory(inv);
    }

    private ItemStack category(Material material, String name, String key) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList("§7Menüyü açmak için tıkla"));
        meta.setLocalizedName(key);
        item.setItemMeta(meta);
        return item;
    }

    private void openCategory(Player player, String category) {
        Inventory inv = Bukkit.createInventory(null, 54, title + " - " + category.toUpperCase(Locale.ROOT));
        items.clear();

        switch (category) {
            case "end" -> {
                add(inv, 10, Material.ENDER_CHEST, "Ender Sandığı", 2500);
                add(inv, 11, Material.ENDER_PEARL, "Ender İncisi", 75);
                add(inv, 12, Material.END_STONE, "End Taşı", 8);
                add(inv, 13, Material.DRAGON_BREATH, "Ejderha Nefesi", 1000);
                add(inv, 14, Material.END_ROD, "End Çubuğu", 100);
                add(inv, 15, Material.CHORUS_FRUIT, "Chorus Meyvesi", 10);
                add(inv, 16, Material.POPPED_CHORUS_FRUIT, "Patlamış Chorus Meyvesi", 15);
                add(inv, 19, Material.SHULKER_SHELL, "Shulker Kabuğu", 350);
                add(inv, 20, Material.SHULKER_BOX, "Shulker Kutusu", 800);
            }
            case "nether" -> {
                add(inv, 10, Material.BLAZE_ROD, "Blaze Çubuğu", 150);
                add(inv, 11, Material.NETHER_WART, "Nether Wart", 15);
                add(inv, 12, Material.GLOWSTONE_DUST, "Glowstone Tozu", 15);
                add(inv, 13, Material.MAGMA_CREAM, "Magma Kremi", 15);
                add(inv, 14, Material.GHAST_TEAR, "Ghast Gözyaşı", 350);
                add(inv, 15, Material.QUARTZ, "Kuvars", 15);
                add(inv, 16, Material.SOUL_SAND, "Ruh Kumu", 50);
                add(inv, 19, Material.MAGMA_BLOCK, "Magma Bloğu", 35);
                add(inv, 20, Material.CRYING_OBSIDIAN, "Ağlayan Obsidyen", 150);
            }
            case "gear" -> {
                add(inv, 10, Material.OBSIDIAN, "Obsidyen", 100);
                add(inv, 11, Material.GLOWSTONE, "Glowstone Bloğu", 100);
                add(inv, 12, Material.TOTEM_OF_UNDYING, "Ölümsüzlük Totemi", 1250);
                add(inv, 13, Material.ENDER_PEARL, "Ender İncisi", 75);
                add(inv, 14, Material.GOLDEN_APPLE, "Altın Elma", 250);
                add(inv, 15, Material.EXPERIENCE_BOTTLE, "XP Şişesi", 100);
                add(inv, 16, Material.ARROW, "Ok", 250);
            }
            case "food" -> {
                add(inv, 10, Material.POTATO, "Patates", 75);
                add(inv, 11, Material.SWEET_BERRIES, "Tatlı Meyveler", 50);
                add(inv, 12, Material.MELON_SLICE, "Karpuz Dilimi", 10);
                add(inv, 13, Material.CARROT, "Havuç", 65);
                add(inv, 14, Material.APPLE, "Elma", 25);
                add(inv, 15, Material.COOKED_CHICKEN, "Pişmiş Tavuk", 30);
                add(inv, 16, Material.COOKED_BEEF, "Biftek", 35);
                add(inv, 19, Material.GOLDEN_CARROT, "Altın Havuç", 50);
                add(inv, 20, Material.GOLDEN_APPLE, "Altın Elma", 250);
            }
        }

        inv.setItem(49, category(Material.ARROW, "§cGeri", "back"));
        player.openInventory(inv);
    }

    private void add(Inventory inv, int slot, Material material, String name, double price) {
        inv.setItem(slot, make(material, name, price));
        items.put(slot, new ShopItem(material, name, price));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String viewTitle = event.getView().getTitle();
        if (!viewTitle.startsWith(title)) return;

        event.setCancelled(true);
        if (event.getRawSlot() < 0 || event.getRawSlot() >= event.getView().getTopInventory().getSize()) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        if (viewTitle.equals(title)) {
            String key = clicked.getItemMeta().getLocalizedName();
            if (key != null && Set.of("end", "nether", "gear", "food").contains(key)) {
                openCategory(player, key);
            }
            return;
        }

        if (clicked.getItemMeta().hasLocalizedName()
                && clicked.getItemMeta().getLocalizedName().equals("back")) {
            openMain(player);
            return;
        }

        ShopItem shopItem = items.get(event.getRawSlot());
        if (shopItem == null) return;

        if (economy.getBalance(player) < shopItem.price()) {
            player.sendMessage("§cParan yetersiz!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1f);
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cEnvanterin dolu!");
            return;
        }

        if (!economy.withdrawPlayer(player, shopItem.price()).transactionSuccess()) {
            player.sendMessage("§cÖdeme başarısız!");
            return;
        }

        Map<Integer, ItemStack> leftovers =
                player.getInventory().addItem(new ItemStack(shopItem.material()));

        if (!leftovers.isEmpty()) {
            economy.depositPlayer(player, shopItem.price());
            player.sendMessage("§cEnvanterin dolu, paran iade edildi!");
            return;
        }

        player.sendMessage("§aSatın aldın: §f" + shopItem.name()
                + " §7(-" + shopItem.price() + "$)");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f);
    }
}
