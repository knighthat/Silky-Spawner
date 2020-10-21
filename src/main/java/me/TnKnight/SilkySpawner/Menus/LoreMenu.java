package me.TnKnight.SilkySpawner.Menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.Utils;

public class LoreMenu extends MenuAbstractClass {

	public LoreMenu(MenusStorage storage) {
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

	private final List<Material> itemslist = new ArrayList<>(
	    Arrays.asList(Material.FEATHER, Material.REDSTONE_TORCH, Material.PISTON, Material.BARRIER, Material.REDSTONE));

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		if (slot == 8) {
			new MainMenu(new MenusStorage(player)).openMenu();
			return;
		}
		String function = "lore "
		    + (slot == 0 ? "add" : slot == 1 ? "set" : slot == 2 ? "insert" : "remove").concat(slot > 0 ? " [line]" : "").concat(" <lore>");
		sendMes(player, function, function.replace("[line] ", "").replace("<lore>", ""));
	}

	private String Path(String button) {
		return invContains("LoreModificatiorMenu." + button + "Button") ? invConfig("LoreModificatiorMenu." + button + "Button") : empty;
	}

	@Override
	public void setMenuItems() {
		for (int slot = 0; slot < itemslist.size(); slot++) {
			String name = Path(
			    (slot == 0 ? "Add" : slot == 1 ? "Set" : slot == 2 ? "Insert" : slot == 3 ? "Remove" : "GoBack").concat(slot < 4 ? "Lore" : ""));
			super.setInvItem(new ItemStack(itemslist.get(slot)), Utils.AddColors(name), null, slot == 4 ? 8 : slot);
		}
	}

}
