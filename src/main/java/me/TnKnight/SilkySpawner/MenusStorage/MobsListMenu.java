package me.TnKnight.SilkySpawner.MenusStorage;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;

public class MobsListMenu extends AbstractMobsListMenu {

	public MobsListMenu(Storage storage) {
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
		ItemStack item = e.getCurrentItem();
		switch (item.getType()) {
			case SPAWNER :
				storage.setSpawner(e.getCurrentItem());
				new ConfirmMenu(storage).openMenu();
				break;
			case REDSTONE :
				new MainMenu(new Storage(player)).openMenu();
				break;
			case DARK_OAK_BUTTON :
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(invConfig("CreateSpawnerMenu.PreviousPage"))) {
					if (page > 0) {
						page = page - 1;
						super.openMenu();
					} else
						player.sendMessage(Utils.getMessage("OnFirstPage"));
				} else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(invConfig("CreateSpawnerMenu.NextPage"))) {
					if (!((index + 1) >= MobsList.toList().size())) {
						page = page + 1;
						super.openMenu();
					} else
						player.sendMessage(Utils.getMessage("OnLastPage"));
				}
				break;
			default :
				break;
		}
	}

	@Override
	public void setMenuItems() {
		fillItem();
		for (int page = 0; page < super.itemsPerPage; page++) {
			index = super.itemsPerPage * super.page + page;
			if (index >= MobsList.toList().size())
				break;
			ItemStack item = new ItemStack(Material.SPAWNER);
			ItemMeta iMeta = item.getItemMeta();
			String creature = Config.getConfig().getString("TypeOfCreature")
			    .replace(Utils.StripColors(Config.getConfig().getString("TypeOfCreature").replace("%creature_type%", "")), "");
			iMeta.setDisplayName(Utils.AddColors(creature.replace("%creature_type%", MobsList.toList().get(index))));
			item.setItemMeta(iMeta);
			item.setItemMeta(Utils.SpawnCreature(item, EntityType.valueOf(MobsList.toList().get(index))));
			inventory.addItem(item);
		}
	}

}
