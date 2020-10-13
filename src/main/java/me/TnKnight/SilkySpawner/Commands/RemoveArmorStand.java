package me.TnKnight.SilkySpawner.Commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Config;
import me.TnKnight.SilkySpawner.Utils;

public class RemoveArmorStand extends AbstractClass {

	@Override
	public String getName() {
		return "remove";
	}

	@Override
	public String getDescription() {
		return Config.getConfig().getString("CommandsAssistant." + getName() + ".Description");
	}

	@Override
	public String getUsage() {
		return Config.getConfig().getString("CommandsAssistant." + getName() + ".Usage");
	}

	@Override
	public void executeCommand(Player player, String[] args) {
		if (args.length == 0 || args.length > 1) {
			player.spigot().sendMessage(Utils.hoverNclick("/silkyspawner remove <radius>", TextColor, HoverText,
					HoverColor, "/silkyspawner remove "));
			return;
		}
		if (!args[0].matches("-?\\d+")) {
			player.sendMessage(Utils.getConfig("NotANumber").replace("%input%", args[1]));
			return;
		}
		int radius = Integer.parseInt(args[0]);
		if (radius < 0)
			return;
		List<Entity> Entities = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)
				.stream().filter(en -> en.getType().equals(EntityType.ARMOR_STAND))
				.filter(en -> en.getCustomName() != null).collect(Collectors.toList());
		for (int i = 0; i < Entities.size(); i++)
			Entities.get(i).remove();
	}

}
