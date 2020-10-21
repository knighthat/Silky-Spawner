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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Menus.MenuAbstractClass;

public class Listeners implements Listener {

	private final Double serialID = 5.85731858674105;
	public static final Double Serial = 5.85731840133667;

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
					player.sendMessage(Utils.AddColors(Storage.Prefix() + Config.getConfig().getString("GameModeMessage")));
					e.setCancelled(true);
					return;
				}
				List<Entity> Entities = new ArrayList<Entity>(spawner.getWorld()
				    .getNearbyEntities(sLocation.add(0.5, 1.0, 0.5), 0, 2.0 + Config.getConfig().getDouble("NameAndLore.Distance"), 0).stream()
				    .filter(entity -> entity.getType().equals(EntityType.ARMOR_STAND)).filter(entity -> entity.getCustomName() != null)
				    .filter(entity -> ((ArmorStand) entity).getHealth() == Serial).collect(Collectors.toList()));
				ItemStack iSpawner = new ItemStack(Material.SPAWNER, 1);
				EntityType creature = ((CreatureSpawner) spawner.getState()).getSpawnedType();
				BlockStateMeta bMeta = (BlockStateMeta) iSpawner.getItemMeta();
				CreatureSpawner cSpawner = (CreatureSpawner) bMeta.getBlockState();
				cSpawner.setSpawnedType(creature);
				bMeta.setBlockState(cSpawner);
				if (Entities.size() > 0) {
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
				e.getPlayer().sendMessage(Utils.AddColors(Storage.Prefix() + Config.getConfig().getString("BreakMessage")));
				e.setCancelled(true);
			}
	}

	private void spawnAS(Block block, String name, Double addY) {
		ArmorStand as = block.getWorld().spawn(block.getLocation().add(.5, addY, .5), ArmorStand.class);
		as.setVisible(false);
		as.setHealth(serialID);
		as.setBasePlate(false);
		as.setGravity(false);
		as.setInvulnerable(true);
		as.setCollidable(false);
		as.setCanPickupItems(false);
		as.setCustomName(Utils.AddColors(name));
		as.setCustomNameVisible(true);
	}

	@EventHandler
	public void onSpawnerPlacing(BlockPlaceEvent e) {
		if (e.getBlock().getType().equals(Material.SPAWNER))
			if (e.getItemInHand().getItemMeta().hasDisplayName()) {
				ItemMeta iMeta = e.getItemInHand().getItemMeta();
				Block bSpawner = e.getBlock();
				Double space = Config.getConfig().getDouble("NameAndLore.Spacing");
				if (space < 0.0 || space > 1.0)
					space = 1.0;
				Double distance = Config.getConfig().getDouble("NameAndLore.Distance");
				if (distance < 0.0 || space > 1.0)
					distance = 1.0;
				spawnAS(bSpawner, iMeta.getDisplayName(), 1.0 + distance);
				if (iMeta.hasLore())
					for (int i = 0; i < iMeta.getLore().size() - 1; i++)
						spawnAS(bSpawner, iMeta.getLore().get(i), (1.0 + distance - space) - (space * i));
				e.getPlayer().sendMessage(Storage.getMsg("PlaceSpawner").replace("%name%", iMeta.getDisplayName()));
			} else
				e.setCancelled(true);
		if (e.isCancelled())
			e.getPlayer().sendMessage(Storage.getMsg("NoName"));
	}

	@EventHandler
	public void MenuClicked(InventoryClickEvent e) {
		if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)
		    && e.getClickedInventory().getHolder() instanceof MenuAbstractClass) {
			e.setCancelled(true);
			MenuAbstractClass menu = (MenuAbstractClass) e.getClickedInventory().getHolder();
			menu.itemClicked(e);
		}
	}
}
