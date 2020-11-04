package me.TnKnight.SilkySpawner;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import me.TnKnight.SilkySpawner.Listeners.Spawners;

public class Methods {

	public static ItemStack setItem(ItemStack item, String name, List<String> lore, EntityType spawnType) {
		BlockStateMeta bMeta = (BlockStateMeta) item.getItemMeta();
		if (spawnType != null) {
			CreatureSpawner cSpawner = (CreatureSpawner) bMeta.getBlockState();
			cSpawner.setSpawnedType(spawnType);
			bMeta.setBlockState(cSpawner);
		}
		bMeta.setDisplayName(name == null ? null : Utils.AddColors(name));
		if (lore != null)
			if (bMeta.hasLore()) {
				lore.stream().forEach(line -> bMeta.getLore().add(Utils.AddColors(line)));
			} else
				bMeta.setLore(lore.stream().map(line -> Utils.AddColors(line)).collect(Collectors.toList()));
		item.setItemMeta(bMeta);
		return item;
	}

	public static void addItem(Player player, ItemStack item) {
		if (player.getInventory().firstEmpty() < 0) {
			player.getInventory().addItem(item);
		} else
			player.getWorld().dropItemNaturally(player.getLocation(), item);
	}

	public static void clearArea(Player player, int radius) {
		Collection<Entity> entities = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius);
		entities.stream()
		    .filter(E -> E.getType().equals(EntityType.ARMOR_STAND) && E.getCustomName() != null && ((ArmorStand) E).getHealth() == Spawners.Serial)
		    .forEach(E -> E.remove());
	}
}
