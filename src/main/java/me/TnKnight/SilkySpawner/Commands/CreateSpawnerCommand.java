package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.Utilities.MobsList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class CreateSpawnerCommand extends CommandsAbstractClass
{
	@Override
	public String getName() {
		return "create";
	}
	
	private String label = new String();
	
	@Override
	public void executeCommand(Player player, String label, String[] args) {
		this.label = label + " " + getName() + " ";
		final String[] getPerms = new String[] { getNode() + ".getlist", cmdNode, wildcard };
		if (args.length == 0) {
			if (!permConfirm(player, getPerms))
				return;
			player.spigot().sendMessage(this.getMobsList());
		} else if (args.length <= 2) {
			if (!MobsList.toList().contains(args[0].toUpperCase())) {
				for (String perm : getPerms)
					if (player.hasPermission("silkyspawner." + perm)) {
						player.spigot().sendMessage(this.getMobsList());
						break;
					}
				return;
			}
			EntityType mob = EntityType.valueOf(args[0].toUpperCase());
			if (!permConfirm(player, new String[] { getNode() + "." + mob.name(), cmdNode }))
				return;
			int amount = 0;
			if (args.length == 2) {
				if (args[1].matches("-?\\d+")) {
					amount = Integer.parseInt(args[1]);
					if (amount < 0)
						return;
				} else {
					player.sendMessage(getMsg("NotANumber").replace("%input%", args[1]));
					return;
				}
			} else
				amount = 1;
			final List<String> lore = new ArrayList<>(Arrays.asList(getMobName(mob)));
			ItemStack spawner = setSpawner(null, lore, mob);
			addItem(player, spawner, false);
		} else
			player.spigot().sendMessage(clickableMessage(this.label, this.label));
	}
	
	private BaseComponent[] getMobsList() {
		ComponentBuilder builder = new ComponentBuilder();
		for (int i = 0; i < MobsList.toList().size(); i++) {
			final String comma = ChatColor.GRAY + (i == MobsList.toList().size() - 1 ? "." : ", ");
			final String spawnedType = MobsList.toList().get(i);
			builder.append(clickableMessage(spawnedType.toLowerCase() + comma, label + spawnedType));
		}
		builder.italic(true);
		return builder.create();
	}
}
