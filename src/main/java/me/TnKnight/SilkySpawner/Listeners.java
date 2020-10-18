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
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.MenusStorage.MenuAbstractClass;

public class Listeners implements Listener {

	@EventHandler
	public void onSpawnerBreaking(BlockBreakEvent e) {
		if (e.getBlock().getType().equals(Material.SPAWNER))
			if (e.getPlayer().getInventory().getItemInMainHand()
			    .containsEnchantment(Config.getConfig().getBoolean("CustomEnchantment") ? CustomEnchantment.PICKDASPAWNER : Enchantment.SILK_TOUCH)) {
				Player player = e.getPlayer();
				Block spawner = e.getBlock();
				Location sLocation = spawner.getLocation();
				if (Config.getConfig().getBoolean("Require")
				    && !player.getGameMode().equals(GameMode.valueOf(Config.getConfig().getString("GameMode").toUpperCase()))) {
					player.sendMessage(Utils.AddColors(Utils.Prefix + Config.getConfig().getString("GameModeMessage")));
					e.setCancelled(true);
					return;
				}
				List<Entity> Entities = new ArrayList<Entity>(spawner.getWorld()
				    .getNearbyEntities(sLocation.add(0.5, 1.0, 0.5), 0, 2.0 + Config.getConfig().getDouble("NameAndLore.Distance"), 0).stream()
				    .filter(entity -> entity.getType().equals(EntityType.ARMOR_STAND)).filter(entity -> entity.getCustomName() != null)
				    .collect(Collectors.toList()));
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
					lore.add(Utils.AddColors(Config.getConfig().getString("TypeOfCreature").replace("%creature_type%", creature.name())));
					bMeta.setLore(lore);
				}
				iSpawner.setItemMeta(bMeta);
				spawner.getWorld().dropItemNaturally(sLocation, iSpawner);
				for (Entity entity : Entities)
					entity.remove();
			} else {
				e.getPlayer().sendMessage(Utils.AddColors(Utils.Prefix + Config.getConfig().getString("BreakMessage")));
				e.setCancelled(true);
			}
	}

	@EventHandler
	public void onSpawnerPlacing(BlockPlaceEvent e) {
		if (e.getBlock().getType().equals(Material.SPAWNER))
			if (e.getItemInHand().getItemMeta().hasDisplayName()) {
				Player player = e.getPlayer();
				ItemStack iSpawner = e.getItemInHand();
				ItemMeta iMeta = iSpawner.getItemMeta();
				Block bSpawner = e.getBlock();
				Double Spacing = Config.getConfig().getDouble("NameAndLore.Spacing") < 0.0 ? 1.0
				    : (Config.getConfig().getDouble("NameAndLore.Spacing") > 1.0 ? 1.0 : Config.getConfig().getDouble("NameAndLore.Spacing"));
				Double Distance = Config.getConfig().getDouble("NameAndLore.Distance") < 0.0 ? 1.0
				    : (Config.getConfig().getDouble("NameAndLore.Distance") > 1.0 ? 1.0 : Config.getConfig().getDouble("NameAndLore.Distance"));
				ArmorStand asName = (ArmorStand) bSpawner.getWorld().spawnEntity(bSpawner.getLocation().add(0.5, 1.0 + Distance, 0.5),
				    EntityType.ARMOR_STAND);
				asName.setVisible(false);
				asName.setRemoveWhenFarAway(true);
				asName.setCustomNameVisible(true);
				asName.setGravity(false);
				asName.setInvulnerable(true);
				asName.setCustomName(Utils.AddColors(iMeta.getDisplayName()));
				if (iMeta.hasLore())
					for (int i = 0; i < iMeta.getLore().size() - 1; i++) {
						ArmorStand asLore = (ArmorStand) bSpawner.getWorld()
						    .spawnEntity(bSpawner.getLocation().add(0.5, (1.0 + Distance - Spacing) - (Spacing * i), 0.5), EntityType.ARMOR_STAND);
						asLore.setVisible(false);
						asLore.setRemoveWhenFarAway(true);
						asLore.setCustomNameVisible(true);
						asLore.setGravity(false);
						asLore.setInvulnerable(true);
						asLore.setCustomName(Utils.AddColors(iMeta.getLore().get(i)));
					}
				player.sendMessage(Utils.getMessage("PlaceSpawner").replace("%name%", iSpawner.getItemMeta().getDisplayName()));
			} else
				e.getPlayer().sendMessage(Utils.getMessage("NoName"));
	}

	@EventHandler
	public void MenuClicked(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
			return;
		InventoryHolder holder = e.getClickedInventory().getHolder();
		if (holder instanceof MenuAbstractClass) {
			e.setCancelled(true);
			MenuAbstractClass menu = (MenuAbstractClass) holder;
			menu.itemClicked(e);
		}
	}
}
