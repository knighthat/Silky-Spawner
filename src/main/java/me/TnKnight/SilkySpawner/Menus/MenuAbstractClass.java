package me.TnKnight.SilkySpawner.Menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.InventoriesConfiguration;

public abstract class MenuAbstractClass extends Storage implements InventoryHolder {

	protected Inventory inventory;
	protected MenusStorage storage;

	public MenuAbstractClass(MenusStorage storage) {
		this.storage = storage;
	}

	public abstract String getMenuName();
	public abstract int getRows();
	public abstract void itemClicked(InventoryClickEvent e);
	public abstract void setMenuItems();

	public void openMenu() {
		inventory = Bukkit.createInventory(this, getRows() * 9, getMenuName());
		this.setMenuItems();
		storage.getPlayer().openInventory(inventory);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}
	protected void sendMes(Player player, String Display, String Suggestion) {
		player.closeInventory();
		player.sendMessage(" ");
		player.spigot().sendMessage(Utils.hoverNclick("/silkyspawner " + Display, "/silkyspawner " + Suggestion));
		player.sendMessage(" ");
	}
	protected String invConfig(String path) {
		return Utils.AddColors(InventoriesConfiguration.getConfig().getString(path));
	}
	protected boolean invContains(String path) {
		return InventoriesConfiguration.getConfig().contains(path);
	}
	protected void setInvItem(ItemStack item, String name, List<String> lore, int slot) {
		ItemMeta iMeta = item.getItemMeta();
		iMeta.setDisplayName(name);
		if (lore != null && lore.size() > 0)
			iMeta.setLore(lore);
		item.setItemMeta(iMeta);
		inventory.setItem(slot, item);
	}
}
