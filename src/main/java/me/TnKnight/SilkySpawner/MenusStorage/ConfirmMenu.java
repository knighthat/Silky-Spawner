package me.TnKnight.SilkySpawner.MenusStorage;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;

public class ConfirmMenu extends MenuAbstractClass {

	public ConfirmMenu(Storage storage) {
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
				player.getInventory().addItem(spawner(storage.getSpawner()));
				player.sendMessage(Utils.getMessage("GetSpawner"));
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
		ItemStack yes = new ItemStack(Material.GREEN_WOOL);
		ItemMeta yesMeta = yes.getItemMeta();
		yesMeta.setDisplayName(Utils.AddColors(invContains("ConfirmMenu.YesButton") ? invConfig("ConfirmMenu.YesButton") : empty));
		yes.setItemMeta(yesMeta);
		inventory.setItem(16, yes);
		ItemStack no = new ItemStack(Material.RED_WOOL);
		ItemMeta noMeta = no.getItemMeta();
		noMeta.setDisplayName(Utils.AddColors(invContains("ConfirmMenu.NoButton") ? invConfig("ConfirmMenu.NoButton") : empty));
		no.setItemMeta(noMeta);
		inventory.setItem(10, no);
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
