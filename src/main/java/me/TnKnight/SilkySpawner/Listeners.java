package me.TnKnight.SilkySpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Listeners implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void breakSpawner(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Block spawner = e.getBlock();
		Location sLocation = spawner.getLocation();
		if (e.getBlock().getType().equals(Material.SPAWNER))
			if (Config.getConfig().getBoolean("CustomEnchantment")
					? player.getInventory().getItemInMainHand().containsEnchantment(CustomEnchantment.PICKDASPAWNER)
					: player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
				if (Config.getConfig().getBoolean("Require") && !player.getGameMode()
						.equals(GameMode.valueOf(Config.getConfig().getString("GameMode").toUpperCase()))) {
					player.sendMessage(Utils.getConfig("GameModeMessage"));
					e.setCancelled(true);
					return;
				}
				List<Entity> Entities = new ArrayList<Entity>(spawner.getWorld()
						.getNearbyEntities(sLocation.add(0.5, 1.0, 0.5), 0,
								1.0 + Config.getConfig().getDouble("NameAndLore.Distance"), 0)
						.stream().filter(entity -> entity.getType().equals(EntityType.ARMOR_STAND))
						.filter(entity -> entity.getCustomName() != null).collect(Collectors.toList()));
				ItemStack iSpawner = new ItemStack(Material.SPAWNER, 1);
				EntityType creature = ((CreatureSpawner) spawner.getState()).getSpawnedType();
				BlockStateMeta bMeta = (BlockStateMeta) iSpawner.getItemMeta();
				CreatureSpawner cSpawner = (CreatureSpawner) bMeta.getBlockState();
				cSpawner.setSpawnedType(creature);
				bMeta.setBlockState(cSpawner);
				if (!Entities.isEmpty()) {
					List<String> lore = new ArrayList<String>();
					bMeta.setDisplayName(Entities.get(0).getCustomName());
					for (int i = 1; i < Entities.size(); i++)
						lore.add(Entities.get(i).getCustomName());
					bMeta.setLore(lore);
				}
				iSpawner.setItemMeta(bMeta);
				spawner.getWorld().dropItemNaturally(sLocation, iSpawner);
				for (Entity entity : Entities)
					entity.remove();
			} else {
				player.sendMessage(Utils.getConfig("BreakMessage"));
				e.setCancelled(true);
				return;
			}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void placeSpawner(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		ItemStack iSpawner = e.getItemInHand();
		Block bSpawner = e.getBlock();
		Double Spacing = Config.getConfig().getDouble("NameAndLore.Spacing");
		Double Distance = Config.getConfig().getDouble("NameAndLore.Distance");
		if (bSpawner.getType().equals(Material.SPAWNER)) {
			if (!iSpawner.getItemMeta().hasDisplayName()) {
				player.sendMessage(Utils.getConfig("NoName"));
				e.setCancelled(true);
				return;
			}
			ItemMeta iMeta = iSpawner.getItemMeta();
			if (iMeta.hasDisplayName()) {
				ArmorStand as = (ArmorStand) bSpawner.getWorld()
						.spawnEntity(bSpawner.getLocation().add(0.5, 1.0 + Distance, 0.5), EntityType.ARMOR_STAND);
				as.setVisible(false);
				as.setCustomNameVisible(true);
				as.setGravity(false);
				as.setInvulnerable(true);
				as.setCustomName(Utils.AddColors(iMeta.getDisplayName()));
			}
			if (iMeta.hasLore())
				for (int i = 0; i < iMeta.getLore().size(); i++) {
					ArmorStand as = (ArmorStand) bSpawner.getWorld().spawnEntity(
							bSpawner.getLocation().add(0.5, (1.0 + Distance - Spacing) - (Spacing * i), 0.5),
							EntityType.ARMOR_STAND);
					as.setVisible(false);
					as.setCustomNameVisible(true);
					as.setGravity(false);
					as.setInvulnerable(true);
					as.setCustomName(Utils.AddColors(iMeta.getLore().get(i)));
				}
			player.sendMessage(
					Utils.getConfig("PlaceSpawner").replace("%name%", iSpawner.getItemMeta().getDisplayName()));
		}
	}
}
