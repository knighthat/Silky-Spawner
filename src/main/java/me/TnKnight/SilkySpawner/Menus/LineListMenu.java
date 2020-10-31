package me.TnKnight.SilkySpawner.Menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

public class LineListMenu extends MenuManager {

	public LineListMenu(MenusStorage storage) {
		super(storage);
	}

	@Override
	public String getMenuName() {
		return getInv("LinesListMenu.Title");
	}

	@Override
	public int getRows() {
		return 1;
	}

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack loreLine = storage.getSpawner();
		ItemMeta lMeta = loreLine.getItemMeta();
		int line = 1;
		String amount = Utils.StripColors(e.getCurrentItem().getItemMeta().getDisplayName());
		switch (e.getSlot()) {
			case 8 :
				if (SilkySpawner.getStorage(player).getType().equals(ConfirmType.REMOVE_LORE)) {
					lMeta.getLore().set(storage.getLine(), null);
					loreLine.setItemMeta(lMeta);
					SilkySpawner.getStorage(player).setBolean(true);
					SilkySpawner.getStorage(player).setSpawner(loreLine);
					new ConfirmMenu(SilkySpawner.getStorage(player)).openMenu();
				} else {
					SilkySpawner.getStorage(player).setLine(line--);
					accessModification(player, false, storage.getType());
				}
				break;
			case 0 :
				player.closeInventory();
			default :
				String amt = amount.equals("1") || amount.equals("64") ? amount : String.valueOf(line + Integer.parseInt(amount));
				int current = Integer.parseInt(amt);
				int max = player.getInventory().getItemInMainHand().getItemMeta().getLore().size() - 1;
				if (current < 1 || current > max)
					amt = current < 1 ? "1" : String.valueOf(max);
				line = current;
				lMeta.setDisplayName(Utils.AddColors(getInv("LinesListMenu.LineNumber").replace("%line%", amt)));
				loreLine.setItemMeta(lMeta);
				storage.setSpawner(loreLine);
				new LineListMenu(storage).openMenu();
				break;
		}
	}

	@Override
	public void setMenuItems() {
		inventory.setItem(4, storage.getSpawner());
		setInvItem(Material.GREEN_WOOL, 1, getInv("LinesListMenu.YesButton"), null, 8);
		setInvItem(Material.RED_WOOL, 1, getInv("LinesListMenu.NoButton"), null, 0);
		setInvItem(Material.TORCH, 1, "&c1", null, 1);
		setInvItem(Material.TORCH, 10, "&c-10", null, 2);
		setInvItem(Material.TORCH, 1, "&c-1", null, 3);
		setInvItem(Material.REDSTONE_TORCH, 1, "&a+1", null, 5);
		setInvItem(Material.REDSTONE_TORCH, 10, "&a+10", null, 6);
		setInvItem(Material.REDSTONE_TORCH, 64, "&a64", null, 7);
	}
}
