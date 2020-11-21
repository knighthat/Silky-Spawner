package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import me.TnKnight.SilkySpawner.Utilities.MobsList;

public class ChangeMobCommand extends CommandsAbstractClass
{
	
	@Override
	public String getName() {
		return "changemob";
	}
	
	@Override
	public void executeCommand(Player player, String label, String[] args) {
		switch (args.length)
		{
			case 1:
				if (args.length == 2 && !args[0].equalsIgnoreCase("look") && !args[0].equalsIgnoreCase("hand")) {
					player.spigot().sendMessage(misTyped(getUsg(), label));
					return;
				}
				if (!MobsList.toList().contains(args[0].toUpperCase())) {
					player.spigot().sendMessage(CreateSpawnerCommand.getMobsList(player, label, getName()));
					return;
				}
				if (!permConfirm(player, new String[] { getNode() + "." + args[0].toUpperCase(), cmdNode }))
					return;
				final Material spawnedEgg = Material.getMaterial(args[0].toUpperCase() + "_SPAWN_EGG");
				if (!player.getInventory().contains(spawnedEgg)) {
					String message = getMsg("HaveNoEggs");
					message = message.replace("%egg%", spawnedEgg.toString());
					message = message.replace("%type%", validateCfg("SpawnedType").toString());
					player.sendMessage(message);
					return;
				}
				player.getInventory().remove(new ItemStack(spawnedEgg, 1));
				final EntityType spawnedType = EntityType.valueOf(args[0].toUpperCase());
				ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
				if (!spawner.getType().equals(Material.SPAWNER)) {
					player.sendMessage(getMsg("NotHoldingSpawner").replace("%type%", validateCfg("SpawnedType").toString()));
					return;
				}
				BlockStateMeta state = (BlockStateMeta) spawner.getItemMeta();
				CreatureSpawner creature = (CreatureSpawner) state.getBlockState();
				if (creature.getSpawnedType().equals(spawnedType)) {
					player.sendMessage(getMsg("SameMob"));
					return;
				} else
					creature.setSpawnedType(spawnedType);
				state.setBlockState(creature);
				List<String> lore = new ArrayList<>();
				if (state.getLore() != null && state.getLore().size() > 0) {
					lore.addAll(state.getLore());
					final String mobName = lore.get(state.getLore().size() - 1);
					String typeOfCreature = stripColors(validateCfg("TypeOfCreature").toString().replace("%creature_type%", ""));
					if (stripColors(mobName).startsWith(typeOfCreature))
						lore.remove(state.getLore().size() - 1);
				}
				lore.add(getMobName(spawnedType));
				state.setLore(lore);
				spawner.setItemMeta(state);
				addItem(player, spawner, true);
				player.sendMessage(getMsg("MobChanged"));
				break;
			
			default:
				player.spigot().sendMessage(misTyped(getUsg(), label));
				break;
		}
	}
}
