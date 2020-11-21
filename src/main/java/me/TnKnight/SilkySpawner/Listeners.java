package me.TnKnight.SilkySpawner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.TnKnight.SilkySpawner.Menus.ConfirmMenu;
import me.TnKnight.SilkySpawner.Menus.MenusManager;
import me.TnKnight.SilkySpawner.Menus.MenusStorage;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;
import me.TnKnight.SilkySpawner.Utilities.MobsList;
import me.TnKnight.SilkySpawner.Utilities.Storage;

public class Listeners extends Storage implements Listener
{
	private Map<Player, Long> delay = new HashMap<>();
	
	private final Double getDistance() {
		Double distance = (Double) validateCfg("NameAndLore.Distance") + 1;
		if (distance < 0 || distance > 4)
			distance = (double) (distance < 0 ? 0 : 4);
		return distance;
	}
	
	private final Double getSpace() {
		Double space = (Double) validateCfg("NameAndLore.Space");
		if (space < 0 || space > 1)
			space = (double) (space < 0 ? 0 : 1);
		return space;
	}
	
	private void spawnArmorStand(Block spawner, String name, Double addY) {
		if (name == null)
			return;
		ArmorStand armorStand = spawner.getWorld().spawn(spawner.getLocation().add(.5, addY, .5), ArmorStand.class);
		armorStand.setGravity(false);
		armorStand.setVisible(false);
		armorStand.setCustomName(addColors(name));
		armorStand.setHealth(5.85731858674105);
		armorStand.setInvulnerable(true);
		armorStand.setBasePlate(false);
		armorStand.setCustomNameVisible(true);
	}
	
	private final boolean charsCounting(final Player player, String input, final boolean type) {
		if (countingSystem(player, input, type))
			return true;
		final String minChars = validateCfg("MinimumChars").toString();
		final String maxChars = validateCfg("MaximumChars").toString();
		String message = getMsg("Set" + (type ? "Name" : "Lore"));
		message = message.replace("%min%", minChars).replace("%max%", maxChars);
		player.sendMessage(message);
		return false;
	}
	
	@EventHandler
	public void onMenuClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
			return;
		if (e.getView().getTopInventory().getHolder() instanceof MenusManager) {
			e.setCancelled(true);
			if (!(e.getInventory().getHolder() instanceof MenusManager))
				return;
			((MenusManager) e.getInventory().getHolder()).itemClicked(e);
		}
	}
	
	@EventHandler
	public void mobChanging(PlayerInteractEvent e) {
		if (!e.getMaterial().toString().endsWith("_SPAWN_EGG") || !e.getClickedBlock().getType().equals(Material.SPAWNER)
				|| !e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;
		e.setCancelled(true);
		final Player player = e.getPlayer();
		final String mobType = e.getMaterial().toString().replace("_SPAWN_EGG", "");
		boolean success = true;
		if (delay.containsKey(player) && delay.get(player) > System.currentTimeMillis())
			return;
		if (!permConfirm(player, new String[] { "changemob." + mobType, "changemob.*" }))
			return;
		if (!MobsList.toList().contains(mobType))
			success = false;
		CreatureSpawner creature = (CreatureSpawner) e.getClickedBlock().getState();
		EntityType spawnedType = EntityType.valueOf(mobType);
		String message = "CannotChangeMob";
		if (creature.getSpawnedType().equals(spawnedType)) {
			message = "SameMob";
			return;
		}
		if (success) {
			delay.put(player, System.currentTimeMillis() + 1000);
			takeItem(player);
			message = "MobChanged";
			new BukkitRunnable() {
				
				@Override
				public void run() {
					creature.setSpawnedType(spawnedType);
					creature.update();
				}
			}.runTaskLater(SilkySpawner.instance, 5);
		}
		player.sendMessage(getMsg(message).replace("%mob_type%", MobsList.getMobName(spawnedType)));
	}
	
	@EventHandler
	public void onSpawnerPlace(BlockPlaceEvent e) {
		if (!e.getBlock().getType().equals(Material.SPAWNER))
			return;
		Block spawner = e.getBlock();
		ItemStack sItem = e.getPlayer().getInventory().getItemInMainHand();
		ItemMeta sMeta = sItem.getItemMeta();
		if (sMeta.hasDisplayName()) {
			spawnArmorStand(spawner, sMeta.getDisplayName(), getDistance());
			e.getPlayer().sendMessage(getMsg("PlaceSpawner").replace("%name%", sMeta.getDisplayName()));
		}
		if (sMeta.hasLore())
			for (int line = 0; line < sMeta.getLore().size() - 1; line++) {
				final Double loca = (getDistance() - .3) - (getSpace() * (line + 1));
				spawnArmorStand(spawner, sMeta.getLore().get(line), loca);
			}
		new BukkitRunnable() {
			final EntityType spawnedType = ((CreatureSpawner) ((BlockStateMeta) sItem.getItemMeta()).getBlockState()).getSpawnedType();
			CreatureSpawner creature = (CreatureSpawner) spawner.getState();
			
			@Override
			public void run() {
				creature.setSpawnedType(spawnedType);
				creature.update();
			}
		}.runTaskLater(SilkySpawner.instance, 5);
	}
	
	@EventHandler
	public void onSpwanerBreak(BlockBreakEvent e) {
		final Player player = e.getPlayer();
		if (!e.getBlock().getType().equals(Material.SPAWNER))
			return;
		ItemStack inHand = player.getInventory().getItemInMainHand();
		if (((boolean) validateCfg("CustomEnchantment") && !inHand.containsEnchantment(CustomEnchantment.PICKDASPAWNER))
				|| !((boolean) validateCfg("CustomEnchantment")) && !inHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
			player.sendMessage(getMsg("NotEnchanted"));
			e.setCancelled(true);
			return;
		}
		final GameMode gamemode = GameMode.valueOf(validateCfg("GameMode").toString());
		if ((boolean) validateCfg("RequireGameMode") && !player.getGameMode().equals(gamemode)) {
			player.sendMessage(getMsg("WrongGameMode"));
			e.setCancelled(true);
			return;
		}
		Block spawner = e.getBlock();
		final EntityType spawnedType = ((CreatureSpawner) spawner.getState()).getSpawnedType();
		List<ArmorStand> armor_stands = new ArrayList<>();
		Collection<Entity> entities = spawner.getWorld().getNearbyEntities(spawner.getLocation(), 1, getDistance() + 1, 1);
		entities.stream().forEach(E ->
		{
			if (E.getType().equals(EntityType.ARMOR_STAND) && E.getCustomName() != null && ((ArmorStand) E).getHealth() == SERIAL)
				armor_stands.add((ArmorStand) E);
		});
		String display_name = null;
		List<String> lore = new ArrayList<>();
		if (armor_stands != null && armor_stands.size() > 0) {
			final boolean hasName = (spawner.getLocation().getY() + getDistance()) == armor_stands.get(0).getLocation().getY();
			lore.addAll(armor_stands.stream().map(A -> A.getCustomName()).collect(Collectors.toList()));
			if (hasName) {
				display_name = armor_stands.get(0).getCustomName();
				lore.remove(0);
			}
		}
		lore.add(getMobName(spawnedType));
		spawner.getWorld().dropItemNaturally(spawner.getLocation(), setSpawner(display_name, lore, spawnedType));
		armor_stands.stream().forEach(A -> A.remove());
	}
	
	@EventHandler
	public void stopCmdsProcessing(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		MenusStorage storage = SilkySpawner.getStorage(player);
		if (storage.getBolean()) {
			e.setCancelled(true);
			player.sendMessage(getMsg("CannotExecuteCommand").replace("%cancel_request%", validateCfg("CancelRequest").toString()));
		}
	}
	
	@EventHandler
	public void asyncPlayerChat(AsyncPlayerChatEvent e) {
		MenusStorage storage = SilkySpawner.getStorage(e.getPlayer());
		if (!storage.getBolean())
			return;
		e.setCancelled(true);
		final Player player = e.getPlayer();
		final String input = addColors(e.getMessage());
		final ConfirmType type = storage.getType();
		if (input.equalsIgnoreCase(validateCfg("CancelRequest").toString())) {
			player.sendMessage(getMsg("Cancelled"));
			storage.setBolean(false);
			return;
		}
		if (!charsCounting(player, input, type.equals(ConfirmType.NAME) ? true : false))
			return;
		ItemMeta sMeta = new ItemStack(storage.getSpawner()).getItemMeta();
		List<String> lore = new ArrayList<>();
		if (sMeta.hasLore()) {
			lore.addAll(sMeta.getLore());
			lore.remove(sMeta.getLore().size() - 1);
		}
		final int line = storage.getLine() - 1;
		switch (type)
		{
			case NAME:
				sMeta.setDisplayName(input);
				break;
			
			case ADD_LORE:
				lore.add(input);
				break;
			
			case SET_LORE:
				lore.set(line, input);
				break;
			
			case INSERT_LORE:
				List<String> nLore = new ArrayList<>();
				for (int i = 0; i < lore.size(); i++) {
					if (i == line)
						nLore.add(input);
					nLore.add(lore.get(i));
				}
				lore.clear();
				lore.addAll(nLore);
				break;
			
			default:
				break;
		}
		EntityType spawnedType = ((CreatureSpawner) ((BlockStateMeta) sMeta).getBlockState()).getSpawnedType();
		lore.add(getMobName(spawnedType));
		storage.setSpawner(setSpawner(sMeta.getDisplayName(), lore, spawnedType));
		storage.setBolean(false);
		storage.setType(ConfirmType.CONFIRM_ITEM);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				new ConfirmMenu(storage).openMenu();
			}
		}.runTaskLater(SilkySpawner.instance, 5);
	}
}
