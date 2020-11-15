package me.TnKnight.SilkySpawner.Menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.CustomEnchantment;
import me.TnKnight.SilkySpawner.Files.InvConfiguration;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

public class MainMenu extends MenusManager
{
	public MainMenu(MenusStorage storage) {
		super(storage);
	}
	
	@Override
	public String getMenuName() {
		return validateInv("MainMenu.Title");
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
		Material clicked = e.getCurrentItem().getType();
		if (clicked.equals(Material.BARRIER)) {
			player.closeInventory();
			return;
		}
		String[] getPerms = new String[] { "menu." + items.get(clicked), "menu." + items.get(clicked) + ".*", "menu.*" };
		if (e.getSlot() != 6 && !permConfirm(player, getPerms))
			return;
		switch (e.getSlot())
		{
			case 1:
				new MobsListMenu(storage).openMenu();
				break;
			
			case 2:
				accessModification(player, true, ConfirmType.NAME);
				break;
			
			case 3:
				new LoreMenu(storage).openMenu();
				break;
			
			case 4:
				if (!(boolean) validateCfg("CustomEnchantment")) {
					player.sendMessage(getMsg("CETurnedOff"));
					return;
				}
				ItemStack inHand = new ItemStack(player.getInventory().getItemInMainHand());
				if (!inHand.getType().name().endsWith("_PICKAXE")) {
					player.sendMessage(getMsg("NotHoldingPickaxe"));
					return;
				}
				if (inHand.containsEnchantment(CustomEnchantment.PICKDASPAWNER)) {
					player.sendMessage(getMsg("AlreadyAdded"));
					return;
				}
				CustomEnchantment.enchantItem(inHand);
				storage.setType(ConfirmType.CONFIRM_ITEM);
				storage.setSpawner(inHand);
				new ConfirmMenu(storage).openMenu();
				break;
			
			case 5:
				storage.setLine(1);
				storage.setType(ConfirmType.REMOVE_ARMORSTANDS);
				new ConfirmMenu(storage).openMenu();
				break;
			
			default:
				player.performCommand("silkyspawner " + items.get(clicked));
				player.closeInventory();
				break;
		}
	}
	
	@Override
	public void setMenuItems() {
		int slot = 1;
		Set<String> sections = InvConfiguration.getConfig().getConfigurationSection("MainMenu").getKeys(false);
		for (String key : sections.stream().filter(section -> !section.equals("Title")).collect(Collectors.toList())) {
			String path = "MainMenu." + key + ".";
			String materialName = new String();
			switch (key.replace("Item", ""))
			{
				case "CreateSpawner":
					materialName = "SPAWNER";
					break;
				
				case "SetName":
					materialName = "NAME_TAG";
					break;
				
				case "Lore":
					materialName = "PAPER";
					break;
				
				case "EnchantPickaxe":
					materialName = "ENCHANTED_BOOK";
					break;
				
				case "Remove":
					materialName = "ARMOR_STAND";
					break;
				
				case "Reload":
					materialName = "SLIME_BALL";
					break;
				
				case "Help":
					materialName = "BOOK";
					break;
			}
			Material material = Material.getMaterial(materialName);
			List<String> lore = new ArrayList<>();
			if (InvConfiguration.getConfig().contains(path + "Lore")) {
				List<String> itemLore = InvConfiguration.getConfig().getStringList(path + "Lore");
				lore.addAll(itemLore.stream().map(line -> addColors(line)).collect(Collectors.toList()));
			}
			setInvItem(material, 1, validateInv(path + "Name"), lore, slot++);
		}
		setInvItem(Material.BARRIER, 1, validateInv("CloseButton"), null, 13);
	}
}
