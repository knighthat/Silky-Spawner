package Utilities;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Listeners.Spawners;

public class Methods extends Storage {

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

	public static void clearArea(Location loc, int radius) {
		List<Entity> entities = loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream()
		    .filter(E -> E.getType().equals(EntityType.ARMOR_STAND)).collect(Collectors.toList());
		entities.stream().map(A -> (ArmorStand) A).forEach(A -> {
			if (A.getCustomName() != null && A.getHealth() == Spawners.Serial)
				A.remove();
		});
	}

	public static void sendLog(String msg, Level level, boolean prefix) {
		Bukkit.getLogger().log(level, (prefix ? SilkySpawner.getName : "") + msg);
	}
}
