package me.TnKnight.SilkySpawner.Menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

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
		switch (slot) {
			case 8 :
				new MainMenu(new MenusStorage(player)).openMenu();
				break;
			default :
				if (!permConfirm(player, "menu.lore." + commands.get(slot).toLowerCase()) && !permConfirm(player, "menu.*")
				    && !permConfirm(player, "menu.lore." + "*"))
					return;
				if (slot != 0) {
					ItemStack ItemLine = new ItemStack(Material.PAPER);
					ItemMeta lMeta = ItemLine.getItemMeta();
					lMeta.setDisplayName(Utils.AddColors(getInv("LineListMenu.LineNumber").replace("%line%", "1")));
					ItemLine.setItemMeta(lMeta);
					storage.setSpawner(ItemLine);
					storage.setType(ConfirmType.valueOf(commands.get(slot).toUpperCase().concat("_LORE")));
					new LineListMenu(storage).openMenu();
				} else
					accessModification(player, false, ConfirmType.ADD_LORE);
				break;
		}
	}

	@Override
	public void setMenuItems() {
		for (int slot : items.keySet())
			setInvItem(items.get(slot), 1,
			    getInv("LoreModificatiorMenu." + commands.get(slot) + (!commands.get(slot).equals("GoBack") ? "Lore" : "") + "Button"), null, slot);

	}

}
