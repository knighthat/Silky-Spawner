package me.TnKnight.SilkySpawner.Menus;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;

public class ConfirmMenu extends MenuAbstractClass {

	public ConfirmMenu(MenusStorage storage) {
		super(storage);
	}

	@Override
	public String getMenuName() {
		return Utils.AddColors(invContains("ConfirmMenu.Title") ? invConfig("ConfirmMenu.Title") : empty);
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		switch (e.getCurrentItem().getType()) {
			case GREEN_WOOL :
				ItemStack spawner = spawner(storage.getSpawner());
				ItemMeta sMeta = spawner.getItemMeta();
				sMeta.setDisplayName(null);
				spawner.setItemMeta(sMeta);
				player.getInventory().addItem(spawner);
				player.sendMessage(Storage.getMsg("GetSpawner"));
				player.closeInventory();
				break;
			case RED_WOOL :
				new MobsListMenu(storage).openMenu();;
				break;
			default :
				break;
		}
	}

	@Override
	public void setMenuItems() {
		for (int slot = 0; slot < getRows() * 9; slot++)
			inventory.setItem(slot, new ItemStack(Material.getMaterial(invContains("ConfirmMenu.Fill") ? invConfig("ConfirmMenu.Fill") : "AIR")));
		super.setInvItem(new ItemStack(Material.GREEN_WOOL),
		    Utils.AddColors(invContains("ConfirmMenu.YesButton") ? invConfig("ConfirmMenu.YesButton") : empty), null, 16);
		super.setInvItem(new ItemStack(Material.RED_WOOL),
		    Utils.AddColors(invContains("ConfirmMenu.NoButton") ? invConfig("ConfirmMenu.NoButton") : empty), null, 10);
		inventory.setItem(13, spawner(storage.getSpawner()));
	}

	private ItemStack spawner(ItemStack RawItemStack) {
		ItemMeta iMeta = RawItemStack.getItemMeta();
		String lore = Utils
		    .AddColors(Config.getConfig().getString("TypeOfCreature").replace("%creature_type%", Utils.StripColors(iMeta.getDisplayName())));
		iMeta.setLore(new ArrayList<String>(Arrays.asList(lore)));
		RawItemStack.setItemMeta(iMeta);
		return RawItemStack;
	}

}
