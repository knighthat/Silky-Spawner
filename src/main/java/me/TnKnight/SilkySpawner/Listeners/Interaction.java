package me.TnKnight.SilkySpawner.Listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Menus.MenuManager;

public class Interaction extends Storage implements Listener {

	private Map<Player, Long> delay = new HashMap<>();

	public Interaction(SilkySpawner main) {
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void MenuClicked(InventoryClickEvent e) {
		if (e.getView().getTopInventory().getHolder() instanceof MenuManager)
			e.setCancelled(true);
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || e.getClickedInventory().getHolder() instanceof Player)
			return;
		MenuManager menu = (MenuManager) e.getClickedInventory().getHolder();
		menu.itemClicked(e);
	}

	@EventHandler
	public void mobChangin(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack inHand = player.getInventory().getItemInMainHand();
		String mobType = inHand.getType().toString().replace("_SPAWN_EGG", "");
		if (delay.containsKey(player) && delay.get(player) > System.currentTimeMillis())
			return;
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.SPAWNER)
		    && inHand.getType().toString().endsWith("SPAWN_EGG")) {
			e.setCancelled(true);
			if (((CreatureSpawner) e.getClickedBlock().getState()).getSpawnedType().name().equals(mobType)) {
				player.sendMessage(getMsg("SameMob").replace("%mob_type%", MobsList.getMobName(mobType)));
				return;
			}
			if (!permConfirm(player, "changemob." + mobType) && !permConfirm(player, "silkyspawner.changemob.*")) {
				player.sendMessage(noPerm("silkyspawner.changemob." + mobType));
				return;
			}
			if (MobsList.toList().contains(mobType)) {
				delay.put(player, System.currentTimeMillis() + 1000);
				if (inHand.getAmount() > 1) {
					inHand.setAmount(inHand.getAmount() - 1);
				} else
					inHand = null;
				player.getInventory().setItem(player.getInventory().getHeldItemSlot(), inHand);
				player.updateInventory();
				Block newSpawner = e.getClickedBlock();
				CreatureSpawner creature = (CreatureSpawner) newSpawner.getState();
				creature.setSpawnedType(EntityType.valueOf(mobType));
				SilkySpawner.instance.getServer().getScheduler().scheduleSyncDelayedTask(SilkySpawner.instance, new Runnable() {
					public void run() {
						creature.update();
						delay.remove(player);
					}
				}, 5L);
				player.sendMessage(getMsg("MobChanged").replace("%mob_type%", mobType));
			} else
				player.sendMessage(getMsg("CannotChangeMob").replace("%mob_type%", mobType));
		}
	}
}
