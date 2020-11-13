package me.TnKnight.SilkySpawner.Menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

public class LoreMenu extends MenusManager
{
	public LoreMenu(MenusStorage storage) {
		super(storage);
	}
	
	@Override
	public String getMenuName() {
		return validateInv("LoreModificatiorMenu.Title");
	}
	
	@Override
	public int getRows() {
		return 1;
	}
	
	private Map<Integer, Material> items = new HashMap<>();
	private List<String> commands = new ArrayList<>(Arrays.asList("Add", "Set", "Insert", "Remove"));
	{
		items.put(0, Material.FEATHER);
		items.put(1, Material.REDSTONE_TORCH);
		items.put(2, Material.PISTON);
		items.put(3, Material.BARRIER);
	}
	
	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		switch (slot)
		{
			case 8:
				new MainMenu(new MenusStorage(player)).openMenu();
				break;
			
			default:
				final String[] perms = new String[] { "menu.lore." + commands.get(slot).toLowerCase(), "menu.*", "menu.lore.*" };
				if (!permConfirm(player, perms))
					return;
				if (slot != 0) {
					if (!player.getInventory().getItemInMainHand().getItemMeta().hasLore()) {
						player.sendMessage(getMsg("NoLore"));
						return;
					}
					storage.setLine(1);
					storage.setSpawner(player.getInventory().getItemInMainHand());
					storage.setType(ConfirmType.valueOf(commands.get(slot).toUpperCase().concat("_LORE")));
					new ConfirmMenu(storage).openMenu();
				} else
					accessModification(player, false, ConfirmType.ADD_LORE);
				break;
		}
	}
	
	@Override
	public void setMenuItems() {
		for (int slot : items.keySet())
			setInvItem(items.get(slot), 1, validateInv("LoreModificatiorMenu." + commands.get(slot) + "LoreButton"), null, slot);
		GoBackButton(8);
	}
}
