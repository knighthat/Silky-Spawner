package me.TnKnight.SilkySpawner.Menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.Utils;

public class ConfirmMenu extends MenuManager {

	public ConfirmMenu(MenusStorage storage) {
		super(storage);
	}

	@Override
	public String getMenuName() {
		return getInv("ConfirmMenu.Title");
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack spawner = storage.getSpawner();
		String amount = Utils.StripColors(e.getCurrentItem().getItemMeta().getDisplayName());
		switch (e.getSlot()) {
			case 19 :
				switch (storage.getType()) {
					case CREATE :
						new MobsListMenu(storage).openMenu();
						break;
					default :
						player.closeInventory();
				}
				break;
			case 25 :
				switch (storage.getType()) {
					case CREATE :
						player.getInventory().addItem(spawner);
						break;
					default :
						player.getInventory().setItem(player.getInventory().getHeldItemSlot(), storage.getSpawner());
						break;
				}
				player.sendMessage(getMsg("GetSpawner"));
				player.closeInventory();
				break;
			default :
				int amt = amount.equals("1") || amount.equals("64") ? Integer.parseInt(amount) : spawner.getAmount() + Integer.parseInt(amount);
				if (amt < 1 || amt > spawner.getMaxStackSize())
					amt = amt < 1 ? 1 : 64;
				spawner.setAmount(amt);
				storage.setSpawner(spawner);
				new ConfirmMenu(storage).openMenu();
				break;
		}
	}

	@Override
	public void setMenuItems() {
		for (int slot = 0; slot < getRows() * 9; slot++)
			inventory.setItem(slot, new ItemStack(Material.getMaterial(contains(getInv("ConfirmMenu.Fill")) ? getInv("ConfirmMenu.Fill") : "AIR")));
		inventory.setItem(13, storage.getSpawner());
		setInvItem(Material.GREEN_WOOL, 1, getInv("ConfirmMenu.YesButton"), null, 25);
		setInvItem(Material.RED_WOOL, 1, getInv("ConfirmMenu.NoButton"), null, 19);
		switch (storage.getType()) {
			case CREATE :
				setInvItem(Material.TORCH, 1, "&c1", null, 9);
				setInvItem(Material.TORCH, 10, "&c-10", null, 10);
				setInvItem(Material.TORCH, 1, "&c-1", null, 11);
				setInvItem(Material.REDSTONE_TORCH, 1, "&a+1", null, 15);
				setInvItem(Material.REDSTONE_TORCH, 10, "&a+10", null, 16);
				setInvItem(Material.REDSTONE_TORCH, 64, "&a64", null, 17);
				break;
			default :
				break;
		}
	}
}
