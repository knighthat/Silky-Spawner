package me.TnKnight.SilkySpawner.Menus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.InvConfiguration;

public abstract class MenuManager extends Storage implements InventoryHolder {

	protected Inventory inventory;
	protected MenusStorage storage;

	public MenuManager(MenusStorage storage) {
		this.storage = storage;
	}

	public abstract String getMenuName();
	public abstract int getRows();
	public abstract void itemClicked(InventoryClickEvent e);
	public abstract void setMenuItems();

	public void openMenu() {
		inventory = Bukkit.createInventory(this, getRows() * 9, Utils.AddColors(getMenuName()));
		this.setMenuItems();
		storage.getPlayer().openInventory(inventory);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}
	protected String getInv(String path) {
		Validate.notNull(InvConfiguration.getConfig().getString(path), path + nll);
		return InvConfiguration.getConfig().getString(path);
	}
	protected void setInvItem(Material material, int amount, String name, List<String> lore, int slot) {
		ItemStack item = new ItemStack(material);
		item.setAmount(amount);
		ItemMeta iMeta = item.getItemMeta();
		iMeta.setDisplayName(Utils.AddColors(name));
		if (lore != null && lore.size() > 0)
			iMeta.setLore(lore);
		item.setItemMeta(iMeta);
		inventory.setItem(slot, item);
	}
	protected boolean contains(String material) {
		return Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains(material.toUpperCase());
	}
}