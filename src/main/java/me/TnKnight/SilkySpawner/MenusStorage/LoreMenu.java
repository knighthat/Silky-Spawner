package me.TnKnight.SilkySpawner.MenusStorage;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreMenu extends MenuAbstractClass {

	public LoreMenu(Storage storage) {
		super(storage);
	}

	@Override
	public String getMenuName() {
		return invContains("LoreModificatiorMenu.Title") ? invConfig("LoreModificatiorMenu.Title") : empty;
	}

	@Override
	public int getRows() {
		return 1;
	}

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		switch (e.getCurrentItem().getType()) {
			case REDSTONE :
				new MainMenu(new Storage(player)).openMenu();
				break;
			case FEATHER :
				sendMes(player, "lore add <lore>", "lore add ");
				break;
			case REDSTONE_TORCH :
				sendMes(player, "lore set [line] <lore>", "lore set ");
				break;
			case PISTON :
				sendMes(player, "lore insert [line] <lore>", "lore insert ");
				break;
			case BARRIER :
				sendMes(player, "lore remove [line]", "lore remove ");
				break;
			default :
				break;
		}
	}

	@Override
	public void setMenuItems() {
		String back = "LoreModificatiorMenu.GoBackButton";
		String add = "LoreModificatiorMenu.AddLoreButton";
		String set = "LoreModificatiorMenu.SetLoreButton";
		String insert = "LoreModificatiorMenu.InsertLoreButton";
		String remove = "LoreModificatiorMenu.RemoveLoreButton";

		ItemStack goBack = new ItemStack(Material.REDSTONE);
		ItemMeta backMeta = goBack.getItemMeta();
		backMeta.setDisplayName(invContains(back) ? invConfig(back) : empty);
		goBack.setItemMeta(backMeta);
		inventory.setItem(8, goBack);

		ItemStack Add = new ItemStack(Material.FEATHER);
		ItemMeta addMeta = Add.getItemMeta();
		addMeta.setDisplayName(invContains(add) ? invConfig(add) : empty);
		Add.setItemMeta(addMeta);
		inventory.setItem(0, Add);

		ItemStack Set = new ItemStack(Material.REDSTONE_TORCH);
		ItemMeta setMeta = Set.getItemMeta();
		setMeta.setDisplayName(invContains(set) ? invConfig(set) : empty);
		Set.setItemMeta(setMeta);
		inventory.setItem(1, Set);

		ItemStack Insert = new ItemStack(Material.PISTON);
		ItemMeta insetMeta = Insert.getItemMeta();
		insetMeta.setDisplayName(invContains(insert) ? invConfig(insert) : empty);
		Insert.setItemMeta(insetMeta);
		inventory.setItem(2, Insert);

		ItemStack Remove = new ItemStack(Material.BARRIER);
		ItemMeta removeMeta = Remove.getItemMeta();
		removeMeta.setDisplayName(invContains(remove) ? invConfig(remove) : empty);
		Remove.setItemMeta(removeMeta);
		inventory.setItem(3, Remove);
	}

}
