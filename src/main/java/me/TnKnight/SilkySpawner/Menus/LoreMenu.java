package me.TnKnight.SilkySpawner.Menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class LoreMenu extends MenuManager {

	public LoreMenu(MenusStorage storage) {
		super(storage);
	}

	@Override
	public String getMenuName() {
		return getInv("LoreModificatiorMenu.Title");
	}

	@Override
	public int getRows() {
		return 1;
	}

	private Map<Integer, Material> items = new HashMap<>();
	private Map<Integer, String> commands = new HashMap<>();
	{
		items.put(0, Material.FEATHER);
		items.put(1, Material.REDSTONE_TORCH);
		items.put(2, Material.PISTON);
		items.put(3, Material.BARRIER);
		items.put(8, Material.REDSTONE);
		commands.put(0, "Add");
		commands.put(1, "Set");
		commands.put(2, "Insert");
		commands.put(3, "Remove");
		commands.put(8, "GoBack");

	}

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		if (slot == 8) {
			new MainMenu(new MenusStorage(player)).openMenu();
			return;
		}
		String perm = "menu.lore.";
		if (!permConfirm(player, perm + commands.get(slot).toLowerCase()) && !permConfirm(player, "menu.*") && !permConfirm(player, perm + "*"))
			return;
		String function = "lore " + commands.get(slot).concat(slot > 0 ? " [line]" : "").concat(" <lore>");
		sendMes(player, function, function.replace("[line] ", "").replace("<lore>", ""));
	}

	@Override
	public void setMenuItems() {
		for (int slot : items.keySet())
			setInvItem(items.get(slot), 1, getInv("LoreModificatiorMenu." + commands.get(slot) + ".Button"), null, slot);

	}

}
