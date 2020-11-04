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
import me.TnKnight.SilkySpawner.Methods;
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

	private Double getDistance() {
		Double distance = Config.getConfig().getDouble("NameAndLore.Distance") + 1D;
		if (distance < 0 || distance > 4D)
			distance = distance < 0 ? 0 : 4D;
		return distance;
	}
	private Double getSpace() {
		Double space = Config.getConfig().getDouble("NameAndLore.Space");
		if (space < 0 || space > 1D)
			space = space < 0 ? 0 : 1D;
		return space;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onSpawnerBreak(BlockBreakEvent e) {
		if (!e.getBlock().getType().equals(Material.SPAWNER))
			return;
		if (Config.getConfig().getBoolean("CustomEnchantment") && !e.getPlayer().getInventory().getItemInMainHand()
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
		Player player = e.getPlayer();
		player.sendMessage("HI");
		List<Entity> Entities = spawner.getWorld().getNearbyEntities(sLocation.add(.5, 1D, .5), 0, 2D + getDistance(), 0).stream()
		    .filter(E -> E.getType().equals(EntityType.ARMOR_STAND) && E.getCustomName() != null && ((ArmorStand) E).getHealth() == Serial)
		    .collect(Collectors.toList());
		for (Entity entity : Entities)
			player.sendMessage(entity.getCustomName());
		ItemStack iSpawner = new ItemStack(Material.SPAWNER, 1);
		String displayName = "";
		EntityType creature = ((CreatureSpawner) spawner.getState()).getSpawnedType();
		List<String> lore = new ArrayList<String>();
		if (Entities.size() > 0) {
			displayName = Entities.get(0).getCustomName();
			for (int i = 1; i < Entities.size(); i++)
				lore.add(Entities.get(i).getCustomName());
		}
		lore.add(Utils.AddColors(ValidateCfg("TypeOfCreature").replace("%creature_type%", MobsList.getMobName(creature.name()))));
		Methods.setItem(iSpawner, displayName, lore, creature);
		spawner.getWorld().dropItemNaturally(sLocation, iSpawner);
		Entities.stream().forEach(E -> E.remove());
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
		spawnAS(bSpawner, iMeta.getDisplayName(), getDistance());
		if (iMeta.hasLore())
			for (int i = 0; i < iMeta.getLore().size() - 1; i++)
				spawnAS(bSpawner, iMeta.getLore().get(i), (getDistance() - .3) - (getSpace() * (i + 1)));
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
			if (!charsCount(player, message, storage.getType().equals(ConfirmType.NAME) ? "Name" : "Lore"))
				return;
			ItemStack spawner = new ItemStack(Material.SPAWNER);
			spawner.setItemMeta(storage.getSpawner().getItemMeta());
			ItemMeta sMeta = spawner.getItemMeta();
			List<String> lore = new ArrayList<>();
			if (sMeta.hasLore()) {
				lore = sMeta.getLore();
				lore.remove(sMeta.getLore().size() - 1);
			}
			int line = storage.getLine() - 1;
			switch (storage.getType()) {
				case NAME :
					sMeta.setDisplayName(Utils.AddColors(message));
					break;
				case ADD_LORE :
					lore.add(Utils.AddColors(message));
					break;
				case INSERT_LORE :
					List<String> nLore = new ArrayList<>();
					for (int i = 0; i < lore.size(); i++) {
						nLore.add(lore.get(i));
						if (lore.get(line).equals(lore.get(i)))
							nLore.add(Utils.AddColors(message));
					}
					lore.clear();
					lore = nLore;
					break;
				case SET_LORE :
					lore.set(line, Utils.AddColors(message));
					break;
				default :
					break;
			}
			if (!storage.getType().equals(ConfirmType.CREATE) && !storage.getType().equals(ConfirmType.NAME)) {
				String nLore = sMeta.hasLore() ? sMeta.getLore().get(sMeta.getLore().size() - 1)
				    : Utils.AddColors(ValidateCfg("TypeOfCreature").replace("%creature_type%",
				        ((CreatureSpawner) ((BlockStateMeta) sMeta).getBlockState()).getSpawnedType().name()));
				lore.add(nLore);
				sMeta.setLore(lore);
			}
			spawner.setItemMeta(sMeta);
			storage.setBolean(false);
			storage.setSpawner(spawner);
			storage.setType(ConfirmType.CONFIRM_ITEM);
			Bukkit.getScheduler().scheduleSyncDelayedTask(SilkySpawner.instance, new Runnable() {
				@Override
				public void run() {
					new ConfirmMenu(storage).openMenu();
				}
			}, 5L);
		}
	}

	private boolean charsCount(Player player, String message, String type) {
		if (Utils.charsCounting(player, message, type))
			return true;
		player.sendMessage(getMsg("Set" + type).replace("%min%", ValidateCfg("MinimumChars")).replace("%max%", ValidateCfg("MaximumChars")));
		player.sendMessage(Utils.AddColors(Message.getConfig().getString("RequestCancel").replace("%request%", ValidateCfg("CancelRequest"))));
		return false;
	}
}