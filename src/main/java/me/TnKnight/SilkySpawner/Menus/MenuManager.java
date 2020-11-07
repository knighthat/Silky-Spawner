package me.TnKnight.SilkySpawner.Menus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Files.InvConfiguration;
import Files.Message;
import Utilities.Utils;
import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

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
		storage = SilkySpawner.getStorage(storage.getPlayer());
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
	protected void accessModification(Player player, boolean type, ConfirmType cType) {
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER)) {
			player.sendMessage(getMsg("NotHoldingSpawner"));
			return;
		}
		player.sendMessage(
		    getMsg(type ? "SetName" : "SetLore").replace("%min%", ValidateCfg("MinimumChars")).replace("%max%", ValidateCfg("MaximumChars")));
		player.sendMessage(Utils.AddColors(Message.getConfig().getString("RequestCancel").replace("%request%", ValidateCfg("CancelRequest"))));
		storage.setType(cType);
		storage.setBolean(true);
		storage.setSpawner(player.getInventory().getItemInMainHand());
		player.closeInventory();
	}
	protected void YesNoButton(int yesSlot, int noSlot) {
		for (int slot : Arrays.asList(yesSlot, noSlot))
			setInvItem(Material.getMaterial(slot == yesSlot ? "LIME_WOOL" : "RED_WOOL"), 1, getInv(slot == yesSlot ? "YesButton" : "NoButton"), null,
			    slot);
	}
	protected void ChangePageButton(int preButton, int nextButton, int goBackButton) {
		for (int slot : Arrays.asList(preButton, nextButton))
			setInvItem(Material.DARK_OAK_BUTTON, 1, getInv(slot == preButton ? "NextPageButton" : "PreviousPageButton"), null, slot);
		GoBackButton(goBackButton);
	}
	protected void GoBackButton(int slot) {
		setInvItem(Material.REDSTONE, 1, getInv("GoBackButton"), null, slot);
	}
}
