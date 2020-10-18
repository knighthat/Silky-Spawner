package me.TnKnight.SilkySpawner.MenusStorage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Commands.AbstractClass;
import me.TnKnight.SilkySpawner.Files.InventoriesConfiguration;

public abstract class MenuAbstractClass implements InventoryHolder {

	protected Inventory inventory;
	protected Storage storage;

	public MenuAbstractClass(Storage storage) {
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

	protected ItemStack FILLER = new ItemStack(Material.getMaterial(
	    invContains("CreateSpawnerMenu.Fill") ? Utils.ItemsChecking(invConfig("CreateSpawnerMenu.Fill")) ? invConfig("CreateSpawnerMenu.Fill") : "AIR"
	        : "AIR"));
	protected void sendMes(Player player, String Display, String Suggestion) {
		player.closeInventory();
		player.sendMessage(" ");
		player.spigot().sendMessage(Utils.hoverNclick("/silkyspawner " + Display, AbstractClass.TextColor, AbstractClass.HoverText,
		    AbstractClass.HoverColor, "/silkyspawner " + Suggestion));
		player.sendMessage(" ");
	}
	protected final String empty = "&c&lEMPTY!!!";
	protected String invConfig(String path) {
		return Utils.AddColors(InventoriesConfiguration.getConfig().getString(path));
	}
	protected boolean invContains(String path) {
		return InventoriesConfiguration.getConfig().contains(path);
	}
}
