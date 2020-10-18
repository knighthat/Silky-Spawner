package me.TnKnight.SilkySpawner.MenusStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.InventoriesConfiguration;
import me.TnKnight.SilkySpawner.Files.MessageYAML;

public class MainMenu extends MenuAbstractClass {

	public MainMenu(Storage storage) {
		super(storage);
	}

	@Override
	public String getMenuName() {
		return Utils.AddColors(invContains("MainMenu.Title") ? invConfig("MainMenu.Title") : empty);
	}

	@Override
	public int getRows() {
		return 2;
	}

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack holding = player.getInventory().getItemInMainHand();
		ItemStack item = e.getCurrentItem();
		switch (item.getType()) {
			case SPAWNER :
				if (player.hasPermission(Utils.getPermission("create"))) {
					new MobsListMenu(storage).openMenu();
				} else
					player.sendMessage(Utils.getMessage("NoPerm").replace("%perm%", Utils.getPermission("create")));
				break;
			case NAME_TAG :
				if (player.hasPermission(Utils.getPermission("setname"))) {
					if (holding.getType().equals(Material.SPAWNER)) {
						sendMes(player, "setname <name>", "setname ");
					} else
						player.sendMessage(Utils.getMessage("NotHoldingSpawner").replace("%type%", Config.getConfig().getString("Name")));
				} else
					player.sendMessage(Utils.getMessage("NoPerm").replace("%perm%", Utils.getPermission("setname")));
				break;
			case PAPER :
				if (player.hasPermission(Utils.getPermission("lore"))) {
					if (holding.getType().equals(Material.SPAWNER)) {
						new LoreMenu(new Storage(player)).openMenu();
					} else
						player.sendMessage(Utils.getMessage("NotHoldingSpawner").replace("%type%", Config.getConfig().getString("Lore")));
				} else
					player.sendMessage(Utils.getMessage("NoPerm").replace("%perm%", Utils.getPermission("lore")));
				break;
			case ENCHANTED_BOOK :
				if (player.hasPermission(Utils.getPermission("enchant"))) {
					player.performCommand("/silkyspawner enchant");
				} else
					player.sendMessage(Utils.getMessage("NoPerm").replace("%perm%", Utils.getPermission("enchant")));
				break;
			case ARMOR_STAND :
				if (player.hasPermission(Utils.getPermission("remove"))) {
					sendMes(player, "remove [radius]", "remove ");
				} else
					player.sendMessage(Utils.getMessage("NoPerm").replace("%perm%", Utils.getPermission("remove")));
				break;
			case SLIME_BALL :
				if (player.hasPermission(Utils.getPermission("reload"))) {
					player.performCommand("/silkyspawner reload");
				} else
					player.sendMessage(Utils.getMessage("NoPerm").replace("%perm%", Utils.getPermission("reload")));
				break;
			case BOOK :
				if (player.hasPermission(Utils.getPermission("help"))) {
					player.performCommand("/silkyspawner help");
				} else
					player.sendMessage(Utils.getMessage("NoPerm").replace("%perm%", Utils.getPermission("help")));
				break;
			case BARRIER :
				if (MessageYAML.getConfig().contains("CloseMenu"))
					player.sendMessage(Utils.getMessage("CloseMenu"));
				player.closeInventory();
				break;
			default :
				break;
		}
	}

	@Override
	public void setMenuItems() {
		int slot = 1;
		for (String key : InventoriesConfiguration.getConfig().getConfigurationSection("MainMenu").getKeys(false).stream()
		    .filter(section -> !section.equals("Title")).filter(section -> !section.equals("Close")).collect(Collectors.toList())) {
			String path = "MainMenu." + key + ".";
			String materialType = "";
			switch (key) {
				case "CreateSpawnerItem" :
					materialType = "SPAWNER";
					break;
				case "SetNameItem" :
					materialType = "NAME_TAG";
					break;
				case "LoreItem" :
					materialType = "PAPER";
					break;
				case "EnchantPickaxeItem" :
					materialType = "ENCHANTED_BOOK";
					break;
				case "RemoveItem" :
					materialType = "ARMOR_STAND";
					break;
				case "ReloadItem" :
					materialType = "SLIME_BALL";
					break;
				case "HelpItem" :
					materialType = "BOOK";
					break;
			}
			ItemStack item = new ItemStack(Material.getMaterial(materialType));
			ItemMeta iMeta = item.getItemMeta();
			String name = Utils.AddColors(!invConfig(path + "Name").isEmpty() ? invConfig(path + "Name") : empty);
			List<String> lore = invContains(path + "Lore") ? InventoriesConfiguration.getConfig().getStringList(path + "Lore").stream()
			    .map(string -> Utils.AddColors(string)).collect(Collectors.toList()) : new ArrayList<String>();
			iMeta.setDisplayName(name);
			if (!lore.isEmpty())
				iMeta.setLore(lore);
			item.setItemMeta(iMeta);
			inventory.setItem(item.getType().equals(Material.BARRIER) ? 13 : slot++, item);
		}
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta iMeta = item.getItemMeta();
		iMeta.setDisplayName(Utils.AddColors(!invConfig("MainMenu.Close").isEmpty() ? invConfig("MainMenu.Close") : empty));
		item.setItemMeta(iMeta);
		inventory.setItem(13, item);
	}
}
