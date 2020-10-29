package me.TnKnight.SilkySpawner.Menus;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.InvConfiguration;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

public class MobsListMenu extends AbstractMobsListMenu {

	public MobsListMenu(MenusStorage storage) {
		super(storage);
	}

	@Override
	public String getMenuName() {
		return getInv("CreateSpawnerMenu.Title").replace("%page%", String.valueOf(page + 1));
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
			String perm = "menu.create.";
			if (!permConfirm(player, perm + MobsList.toList().get(index).toLowerCase()) && !permConfirm(player, "menu.*")
			    && !permConfirm(player, perm + "*"))
				return;
			storage.setType(ConfirmType.CREATE);
			ItemMeta sMeta = e.getCurrentItem().getItemMeta();
			String lore = Utils.AddColors(Config.getConfig().getString("TypeOfCreature").replace("%creature_type%",
			    MobsList.getMobName(((CreatureSpawner) ((BlockStateMeta) sMeta).getBlockState()).getSpawnedType().name())));
			sMeta.setDisplayName(null);
			sMeta.setLore(new ArrayList<String>(Arrays.asList(lore)));
			e.getCurrentItem().setItemMeta(sMeta);
			storage.setSpawner(e.getCurrentItem());
			new ConfirmMenu(storage).openMenu();
		} else if (item.equals(Material.REDSTONE)) {
			new MainMenu(storage).openMenu();
		} else if (item.equals(Material.DARK_OAK_BUTTON))
			switch (e.getSlot()) {
				case 48 :
					if (page > 0) {
						page -= 1;
						openMenu();
					} else
						player.sendMessage(getMsg("OnFirstPage"));
					break;
				case 50 :
					if (index++ < MobsList.toList().size()) {
						page += 1;
						openMenu();
					} else
						player.sendMessage(getMsg("OnLastPage"));
					break;
			}
	}

	@Override
	public void setMenuItems() {
		fillItem();
		for (int slot = 0; slot < itemsPerPage; slot++) {
			index = itemsPerPage * page + slot;
			if (index >= MobsList.toList().size())
				break;
			final String name = ValidateCfg("TypeOfCreature").replace("%creature_type%", MobsList.getMobName(MobsList.toList().get(index)));
			inventory.addItem(setItem(new ItemStack(Material.SPAWNER), name, null, EntityType.valueOf(MobsList.toList().get(index))));
		}
	}
}

abstract class AbstractMobsListMenu extends MenuManager {

	public AbstractMobsListMenu(MenusStorage storage) {
		super(storage);
	}
	protected int page = 0;
	protected int itemsPerPage = 28;
	protected int index = 0;
	public void fillItem() {
		ItemStack filler = new ItemStack(Material.getMaterial(contains(getInv("CreateSpawnerMenu.Fill")) ? getInv("CreateSpawnerMenu.Fill") : "AIR"));
		ItemMeta fMeta = filler.getItemMeta();
		fMeta.setDisplayName(" ");
		filler.setItemMeta(fMeta);
		for (int rows = 0; rows < 6; rows++)
			for (int slot = rows * 9; slot < rows * 9 + 9; slot++)
				if (rows == 0 || rows == 5 || ((rows > 0 && rows < 5) && (rows * 9 == slot || slot == rows * 9 + 8)))
					inventory.setItem(slot, filler);
		addItem(Material.DARK_OAK_BUTTON, "CreateSpawnerMenu.PreviousPage", 48);
		addItem(Material.REDSTONE, "CreateSpawnerMenu.GoBackButton", 49);
		addItem(Material.DARK_OAK_BUTTON, "CreateSpawnerMenu.NextPage", 50);
	}
	private void addItem(Material material, String path, int slot) {
		ItemStack button = new ItemStack(material);
		ItemMeta meta = button.getItemMeta();
		meta.setDisplayName(Utils.AddColors(InvConfiguration.getConfig().getString(path)));
		button.setItemMeta(meta);
		inventory.setItem(slot, button);
	}
}
