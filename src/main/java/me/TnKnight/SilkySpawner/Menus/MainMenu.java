package me.TnKnight.SilkySpawner.Menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.InventoriesConfiguration;
import me.TnKnight.SilkySpawner.Files.MessageYAML;

public class MainMenu extends MenuAbstractClass {

	public MainMenu(MenusStorage storage) {
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

	private final Map<Material, String> items = new HashMap<Material, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(Material.SPAWNER, "create");
			put(Material.NAME_TAG, "setname");
			put(Material.PAPER, "lore");
			put(Material.ENCHANTED_BOOK, "enchant");
			put(Material.ARMOR_STAND, "remove");
			put(Material.SLIME_BALL, "reload");
			put(Material.BOOK, "help");
			put(Material.BARRIER, "CloseMenu");
		}
	};

	@Override
	public void itemClicked(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		for (Material material : items.keySet())
			if (item.getType().equals(material)) {
				String key = items.get(material);
				if (material.equals(Material.BARRIER)) {
					if (MessageYAML.getConfig().contains("CloseMenu"))
						player.sendMessage(Storage.getMsg("CloseMenu"));
					player.closeInventory();
					return;
				}
				if (super.confirmPerm(player, key)) {
					switch (material) {
						case ENCHANTED_BOOK :
						case SLIME_BALL :
						case BOOK :
							player.performCommand("silkyspawner " + key);
							break;
						case SPAWNER :
							new MobsListMenu(storage).openMenu();
							break;
						case PAPER :
							new LoreMenu(storage).openMenu();
							break;
						default :
							super.sendMes(player, key + (key.equals("setname") ? " <name>" : " [radius]"), key);
							break;
					}
				} else
					player.sendMessage(super.getMsg("NoPerm").replace("%perm%", super.getPerm(key)));
			}
	}

	@Override
	public void setMenuItems() {
		int slot = 1;
		for (String key : InventoriesConfiguration.getConfig().getConfigurationSection("MainMenu").getKeys(false).stream()
		    .filter(section -> !section.equals("Title")).filter(section -> !section.equals("Close")).collect(Collectors.toList())) {
			String path = "MainMenu." + key + ".";
			ItemStack item = new ItemStack(Material.getMaterial(key.startsWith("CreateSpawner") ? "SPAWNER"
			    : key.startsWith("SetName") ? "NAME_TAG"
			        : key.startsWith("Lore") ? "PAPER"
			            : key.startsWith("EnchantPickaxe") ? "ENCHANTED_BOOK"
			                : key.startsWith("Remove") ? "ARMOR_STAND" : key.startsWith("Reload") ? "SLIME_BALL" : "BOOK"));
			List<String> lore = invContains(path + "Lore") ? InventoriesConfiguration.getConfig().getStringList(path + "Lore").stream()
			    .map(string -> Utils.AddColors(string)).collect(Collectors.toList()) : new ArrayList<String>();
			super.setInvItem(item, Utils.AddColors(!invConfig(path + "Name").isEmpty() ? invConfig(path + "Name") : empty), lore, slot++);
		}
		super.setInvItem(new ItemStack(Material.BARRIER),
		    Utils.AddColors(!invConfig("MainMenu.Close").isEmpty() ? invConfig("MainMenu.Close") : empty), null, 13);
	}
}
