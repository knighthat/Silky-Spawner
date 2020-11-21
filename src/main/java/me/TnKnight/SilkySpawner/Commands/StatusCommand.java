package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Menus.MenusStorage;
import me.TnKnight.SilkySpawner.Menus.SpawnersStatus;

public class StatusCommand extends CommandsAbstractClass
{
	
	@Override
	public String getName() {
		return "status";
	}
	
	// /silkyspawner status <look/hand>
	
	MenusStorage storage;
	
	@Override
	public void executeCommand(Player player, String label, String[] args) {
		if (!permConfirm(player, new String[] { getNode(), cmdNode }))
			return;
		storage = SilkySpawner.getStorage(player);
		switch (args.length)
		{
			case 0:
				if (player.getTargetBlockExact(4).getType().equals(Material.SPAWNER)) {
					atLook(player);
				} else if (player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER)) {
					onHand(player);
				} else
					return;
				break;
			
			case 1:
				if (!args[0].equalsIgnoreCase("hand") && !args[0].equalsIgnoreCase("look")) {
					player.spigot().sendMessage(misTyped(getUsg(), label));
					return;
				}
				if (args[0].equalsIgnoreCase("hand")) {
					onHand(player);
				} else
					atLook(player);
				break;
			
			default:
				player.spigot().sendMessage(misTyped(getUsg(), label));
				break;
		}
		new SpawnersStatus(storage).openMenu();
	}
	
	private void onHand(final Player player) {
		ItemStack spawner = player.getInventory().getItemInMainHand();
		if (!spawner.getType().equals(Material.SPAWNER)) {
			player.sendMessage(getMsg("NotASpawner"));
			return;
		}
		BlockStateMeta state = (BlockStateMeta) spawner.getItemMeta();
		storage.setCreatureSpawner((CreatureSpawner) state.getBlockState());
		storage.setHand(true);
		storage.setLocation(player.getLocation());
	}
	
	private void atLook(final Player player) {
		Block spawner = player.getTargetBlockExact(4);
		if (!spawner.getType().equals(Material.SPAWNER)) {
			player.sendMessage(getMsg("NotASpawner"));
			return;
		}
		storage.setHand(false);
		storage.setCreatureSpawner((CreatureSpawner) spawner.getState());
	}
}
