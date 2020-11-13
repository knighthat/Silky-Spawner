package me.TnKnight.SilkySpawner.Menus;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Files.Message;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;
import me.TnKnight.SilkySpawner.Utilities.Storage;

public abstract class MenusManager extends Storage implements InventoryHolder
{
	protected Inventory inventory;
	protected MenusStorage storage;
	
	public MenusManager(MenusStorage storage) {
		this.storage = storage;
	}
	
	public abstract String getMenuName();
	
	public abstract int getRows();
	
	public abstract void itemClicked(InventoryClickEvent e);
	
	public abstract void setMenuItems();
	
	public void openMenu() {
		storage = SilkySpawner.getStorage(storage.getPlayer());
		inventory = Bukkit.createInventory(this, getRows() * 9, addColors(getMenuName()));
		this.setMenuItems();
		storage.getPlayer().openInventory(inventory);
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	protected void setInvItem(Material material, int amount, String name, List<String> lore, int slot) {
		ItemStack item = new ItemStack(material);
		item.setAmount(amount);
		ItemMeta iMeta = item.getItemMeta();
		iMeta.setDisplayName(addColors(name));
		if (lore != null && lore.size() > 0)
			iMeta.setLore(lore);
		item.setItemMeta(iMeta);
		inventory.setItem(slot, item);
	}
	
	protected void accessModification(Player player, boolean type, ConfirmType cType) {
		player.closeInventory();
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER)) {
			final String replacement = validateCfg(type ? "Name" : "Lore").toString();
			player.sendMessage(getMsg("NotHoldingSpawner").replace("%type%", replacement));
			return;
		}
		final String minChars = validateCfg("MinimumChars").toString();
		final String maxChars = validateCfg("MaximumChars").toString();
		final String cRequest = validateCfg("CancelRequest").toString();
		player.sendMessage(getMsg(type ? "SetName" : "SetLore").replace("%min%", minChars).replace("%max%", maxChars));
		player.sendMessage(addColors(Message.getConfig().getString("RequestCancel").replace("%request%", cRequest)));
		storage.setType(cType);
		storage.setBolean(true);
		storage.setSpawner(player.getInventory().getItemInMainHand());
	}
	
	protected void YesNoButton(int yesSlot, int noSlot) {
		setInvItem(Material.GREEN_WOOL, 1, validateInv("YesButton"), null, yesSlot);
		setInvItem(Material.RED_WOOL, 1, validateInv("NoButton"), null, noSlot);
	}
	
	protected void ChangePageButton(int preButton, int nextButton, int goBackButton) {
		for (int slot : Arrays.asList(preButton, nextButton))
			setInvItem(Material.DARK_OAK_BUTTON, 1, validateInv(slot == preButton ? "NextPageButton" : "PreviousPageButton"), null, slot);
		GoBackButton(goBackButton);
	}
	
	protected void GoBackButton(int slot) {
		setInvItem(Material.REDSTONE, 1, validateInv("GoBackButton"), null, slot);
	}
}
