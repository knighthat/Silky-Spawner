package me.TnKnight.SilkySpawner.Menus;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Utils;

public class MobsListMenu extends AbstractMobsListMenu {

	public MobsListMenu(MenusStorage storage) {
		super(storage);
	}

	@Override
	public String getMenuName() {
		return Utils.AddColors(
		    invContains("CreateSpawnerMenu.Title") ? invConfig("CreateSpawnerMenu.Title").replace("%page%", String.valueOf(page + 1)) : empty);
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Material item = e.getCurrentItem().getType();
		if (item.equals(Material.SPAWNER)) {
			storage.setSpawner(e.getCurrentItem());
			new ConfirmMenu(storage).openMenu();
		} else if (item.equals(Material.REDSTONE)) {
			new MainMenu(storage).openMenu();
		} else if (item.equals(Material.DARK_OAK_BUTTON))
			switch (e.getSlot()) {
				case 48 :
					if (page > 0) {
						page -= 1;
						super.openMenu();
					} else
						player.sendMessage(Storage.getMsg("OnFirstPage"));
					break;
				case 50 :
					if (index++ < MobsList.toList().size()) {
						page += 1;
						super.openMenu();
					} else
						player.sendMessage(Storage.getMsg("OnLastPage"));
					break;
			}
	}

	@Override
	public void setMenuItems() {
		fillItem();
		for (int slot = 0; slot < super.itemsPerPage; slot++) {
			index = super.itemsPerPage * super.page + slot;
			if (index >= MobsList.toList().size())
				break;
			ItemStack item = new ItemStack(Material.SPAWNER);
			ItemMeta iMeta = item.getItemMeta();
			iMeta.setDisplayName(Utils.AddColors("&6&l" + MobsList.toList().get(index)));
			item.setItemMeta(iMeta);
			item.setItemMeta(Utils.SpawnCreature(item, EntityType.valueOf(MobsList.toList().get(index))));
			inventory.addItem(item);
		}
	}

}
