package me.TnKnight.SilkySpawner.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.CustomEnchantment;
import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.Message;
import me.TnKnight.SilkySpawner.Menus.ConfirmMenu;
import me.TnKnight.SilkySpawner.Menus.MenusStorage;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

public class Spawners extends Storage implements Listener {

	public Spawners(SilkySpawner main) {
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	private final Double serialID = 5.85731858674105;
	public static final Double Serial = 5.85731840133667;

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

	@EventHandler(priority = EventPriority.HIGH)
	public void onSpawnerBreak(BlockBreakEvent e) {
		if (!e.getBlock().getType().equals(Material.SPAWNER))
			return;
		if (!e.getPlayer().getInventory().getItemInMainHand()
		    .containsEnchantment(Config.getConfig().getBoolean("RequireEnchantment") ? CustomEnchantment.PICKDASPAWNER : Enchantment.SILK_TOUCH)) {
			e.getPlayer().sendMessage(Utils.AddColors(Prefix() + ValidateCfg("BreakMessage")));;
			e.setCancelled(true);
			return;
		}
		if (Config.getConfig().getBoolean("Require") && !e.getPlayer().getGameMode().equals(GameMode.valueOf(ValidateCfg("GameMode")))) {
			e.getPlayer().sendMessage(Utils.AddColors(Prefix() + ValidateCfg("GameModeMessage")));
			e.setCancelled(true);
			return;
		}
		Block spawner = e.getBlock();
		Location sLocation = spawner.getLocation();
		List<Entity> Entities = new ArrayList<Entity>(
		    spawner.getWorld().getNearbyEntities(sLocation.add(.5, 1D, .5), 0, 2D + Config.getConfig().getDouble("NameAndLore.Distance"), 0).stream()
		        .filter(entity -> entity.getType().equals(EntityType.ARMOR_STAND)).filter(entity -> entity.getCustomName() != null)
		        .filter(entity -> ((ArmorStand) entity).getHealth() == Serial).collect(Collectors.toList()));
		ItemStack iSpawner = new ItemStack(Material.SPAWNER, 1);
		EntityType creature = ((CreatureSpawner) spawner.getState()).getSpawnedType();
		BlockStateMeta bMeta = (BlockStateMeta) iSpawner.getItemMeta();
		CreatureSpawner cSpawner = (CreatureSpawner) bMeta.getBlockState();
		cSpawner.setSpawnedType(creature);
		bMeta.setBlockState(cSpawner);
		List<String> lore = new ArrayList<String>();
		if (Entities.size() > 0) {
			bMeta.setDisplayName(Entities.get(0).getCustomName());
			for (int i = 1; i < Entities.size(); i++)
				lore.add(Entities.get(i).getCustomName());
		}
		lore.add(Utils.AddColors(ValidateCfg("TypeOfCreature").replace("%creature_type%", MobsList.getMobName(creature.name()))));
		bMeta.setLore(lore);
		iSpawner.setItemMeta(bMeta);
		spawner.getWorld().dropItemNaturally(sLocation, iSpawner);
		for (Entity entity : Entities)
			entity.remove();
	}

	@EventHandler
	public void onSpawnerPlace(BlockPlaceEvent e) {
		if (!e.getBlock().getType().equals(Material.SPAWNER))
			return;
		if (!e.getItemInHand().getItemMeta().hasDisplayName()) {
			e.getPlayer().sendMessage(getMsg("NoName"));
			e.setCancelled(true);
			return;
		}
		ItemMeta iMeta = e.getItemInHand().getItemMeta();
		Block bSpawner = e.getBlock();
		Double space = Config.getConfig().getDouble("NameAndLore.Space");
		if (space < 0 || space > 1D)
			space = space < 0 ? 0 : 1D;
		Double distance = Config.getConfig().getDouble("NameAndLore.Distance") + 1D;
		if (distance < 0 || distance > 4D)
			distance = distance < 0 ? 0 : 4D;
		spawnAS(bSpawner, iMeta.getDisplayName(), distance);
		if (iMeta.hasLore())
			for (int i = 0; i < iMeta.getLore().size() - 1; i++)
				spawnAS(bSpawner, iMeta.getLore().get(i), distance - (space * (i + 1)));
		e.getPlayer().sendMessage(getMsg("PlaceSpawner").replace("%name%", iMeta.getDisplayName()));
	}

	@EventHandler
	public void asyncChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String message = e.getMessage();
		MenusStorage storage = SilkySpawner.getStorage(player);
		if (storage.getBolean()) {
			e.setCancelled(true);
			if (message.startsWith(ValidateCfg("CancelRequest"))) {
				player.sendMessage(getMsg("Cancelled"));
				storage.setBolean(false);
				return;
			}
			ItemStack spawner = new ItemStack(Material.SPAWNER);
			spawner.setItemMeta(storage.getSpawner().getItemMeta());
			ItemMeta sMeta = spawner.getItemMeta();
			List<String> lore = sMeta.getLore();
			lore.remove(sMeta.getLore().size() - 1);
			switch (storage.getType()) {
				case NAME :
					if (!charsCount(player, message, "Name"))
						return;
					sMeta.setDisplayName(Utils.AddColors(message));
					break;
				case ADD_LORE :
					if (!charsCount(player, message, "Lore"))
						return;
					lore.add(Utils.AddColors(message));
					break;
				case INSERT_LORE :
					if (!charsCount(player, message, "Lore"))
						return;
					List<String> nLore = new ArrayList<>();
					for (int i = 0; i < lore.size(); i++) {
						nLore.add(lore.get(i));
						if (lore.get(storage.getLine()).equals(lore.get(i)))
							nLore.add(Utils.AddColors(message));
					}
					lore.clear();
					lore = nLore;
					break;
				case SET_LORE :
					if (!charsCount(player, message, "Lore"))
						return;
					lore.set(storage.getLine(), Utils.AddColors(message));
					break;
				default :
					break;
			}
			if (!storage.getType().equals(ConfirmType.CREATE) && !storage.getType().equals(ConfirmType.NAME)) {
				lore.add(sMeta.getLore().get(sMeta.getLore().size() - 1));
				sMeta.setLore(lore);
			}
			storage.setBolean(false);
			spawner.setItemMeta(sMeta);
			storage.setSpawner(spawner);
			Bukkit.getScheduler().scheduleSyncDelayedTask(SilkySpawner.instance, new Runnable() {
				@Override
				public void run() {
					new ConfirmMenu(storage).openMenu();
				}
			}, 5L);
		}
	}

	private boolean charsCount(Player player, String message, String type) {
		if (!Utils.charsCounting(player, message, "Name")) {
			player.sendMessage(getMsg("Set" + type).replace("%min%", ValidateCfg("MinimumChars")).replace("%max%", ValidateCfg("MaximumChars")));
			player.sendMessage(Utils.AddColors(Message.getConfig().getString("RequestCancel").replace("%request%", ValidateCfg("CancelRequest"))));
			return false;
		}
		return true;
	}
}