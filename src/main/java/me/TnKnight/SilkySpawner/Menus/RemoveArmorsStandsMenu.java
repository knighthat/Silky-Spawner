package me.TnKnight.SilkySpawner.Menus;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Listeners.Spawners;

public class RemoveArmorsStandsMenu extends MenuManager {

	public RemoveArmorsStandsMenu(MenusStorage storage) {
		super(storage);
	}

	@Override
	public String getMenuName() {
		return getInv("RemoveMenu.Title");
	}

	@Override
	public int getRows() {
		return 1;
	}

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack armorStand = storage.getSpawner();
		ItemMeta rMeta = armorStand.getItemMeta();
		String radius = Utils
		    .StripColors(rMeta.getDisplayName().replace(Utils.AddColors(getInv("RemoveMenu.RadiusDisplay").replace("%radius%", "")), ""));
		String amount = Utils.StripColors(e.getCurrentItem().getItemMeta().getDisplayName());
		switch (e.getSlot()) {
			case 1 :
			case 2 :
			case 3 :
			case 5 :
			case 6 :
			case 7 :
				String amt = amount.equals("1") || amount.equals("64") ? amount : String.valueOf(Integer.parseInt(radius) + Integer.parseInt(amount));
				rMeta.setDisplayName(Utils.AddColors(getInv("RemoveMenu.RadiusDisplay").replace("%radius%", amt)));
				armorStand.setItemMeta(rMeta);
				storage.setSpawner(armorStand);
				new RemoveArmorsStandsMenu(storage).openMenu();
				break;
			case 8 :
				int rad = Integer.parseInt(radius);
				player.getWorld().getNearbyEntities(player.getLocation(), rad, rad, rad).stream()
				    .filter(E -> E.getType().equals(EntityType.ARMOR_STAND)).filter(E -> E.getCustomName() != null)
				    .filter(E -> ((ArmorStand) E).getHealth() == Spawners.Serial).forEach(E -> E.remove());
				player.sendMessage(getMsg("RemoveArmorStandsMessage").replace("%radius%", String.valueOf(rad)));
				player.closeInventory();
				break;
			case 0 :
				new MainMenu(storage).openMenu();
		}
	}

	@Override
	public void setMenuItems() {
		inventory.setItem(4, storage.getSpawner());
		setInvItem(Material.GREEN_WOOL, 1, getInv("RemoveMenu.YesButton"), null, 8);
		setInvItem(Material.RED_WOOL, 1, getInv("RemoveMenu.CancelButton"), null, 0);
		setInvItem(Material.TORCH, 1, "&c1", null, 1);
		setInvItem(Material.TORCH, 10, "&c-10", null, 2);
		setInvItem(Material.TORCH, 1, "&c-1", null, 3);
		setInvItem(Material.REDSTONE_TORCH, 1, "&a+1", null, 5);
		setInvItem(Material.REDSTONE_TORCH, 10, "&a+10", null, 6);
		setInvItem(Material.REDSTONE_TORCH, 64, "&a64", null, 7);
	}

}
