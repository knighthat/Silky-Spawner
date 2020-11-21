package me.TnKnight.SilkySpawner.Menus;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.TnKnight.SilkySpawner.Utilities.MobsList;

public class SpawnersStatus extends MenusManager
{
	
	public SpawnersStatus(MenusStorage storage) {
		super(storage);
	}
	
	private String getSection(final String path) {
		return validateInv("SpawnerModificationMenu." + path);
	}
	
	private Map<Material, Integer> items = new HashMap<>();
	{
		items.put(Material.REDSTONE_BLOCK, 10);
		items.put(Material.PLAYER_HEAD, 12);
		items.put(Material.EGG, 14);
		items.put(Material.ENDER_EYE, 16);
	}
	
	@Override
	public String getMenuName() {
		return addColors(getSection("Title"));
	}
	
	@Override
	public int getRows() {
		return 4;
	}
	
	@Override
	public void itemClicked(InventoryClickEvent e) {
		final Player player = (Player) e.getWhoClicked();
		switch (e.getSlot())
		{
			case 31:
				player.closeInventory();
				break;
			
			default:
				break;
		}
	}
	
	
	@Override
	public void setMenuItems() {
		CreatureSpawner creature = storage.getCreatureSpawner();
		Location loc = creature.getLocation();
		if (storage.getHand())
			loc = storage.getLocation();
		storage.setHand(false);
		for (int slot = 0; slot < inventory.getSize(); slot++)
			setInvItem(getMaterial("SpawnerModificationMenu.FillItem"), 1, " ", null, slot);
		for (Material material : items.keySet()) {
			String name = null;
			switch (material.toString())
			{
				case "ENDER_EYE":
					DecimalFormat df = new DecimalFormat("0.0");
					final String X = String.valueOf(df.format(loc.getX()));
					final String Y = String.valueOf(df.format(loc.getY()));
					final String Z = String.valueOf(df.format(loc.getZ()));
					name = getSection("Location").replace("%x%", X).replace("%y%", Y).replace("%z%", Z);
					break;
				
				case "PLAYER_HEAD":
					name = getSection("RequiredRange").replace("%block%", String.valueOf(creature.getRequiredPlayerRange()));
					break;
				
				case "REDSTONE_BLOCK":
					final String min = String.valueOf(creature.getMinSpawnDelay() / 20);
					final String max = String.valueOf(creature.getMaxSpawnDelay() / 20);
					name = getSection("SpawnedTime");
					name = name.replace("%min%", min);
					name = name.replace("%max%", max);
					break;
				
				case "EGG":
					final EntityType spawnedType = creature.getSpawnedType();
					name = getSection("SpawnedType").replace("%creature_type%", MobsList.getMobName(spawnedType));
					break;
			}
			setInvItem(material, 1, addColors(name), null, items.get(material));
		}
		setInvItem(Material.BARRIER, 1, validateInv("CloseButton"), null, 31);
	}
}
