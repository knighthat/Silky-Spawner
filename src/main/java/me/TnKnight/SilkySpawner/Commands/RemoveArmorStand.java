package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;

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
			player.spigot().sendMessage(
					Utils.hoverNclick("/silkyspawner remove <radius>", TextColor, HoverText, HoverColor, getUsage()));
			return;
		}
		try {
			final int radius = Integer.parseInt(args[0]);
			if (radius > 0)
				player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).stream()
						.filter(E -> E.getType().equals(EntityType.ARMOR_STAND)).filter(E -> E.getCustomName() != null)
						.forEach(E -> E.remove());
		} catch (NumberFormatException e) {
			player.sendMessage(Utils.getMessage("NotANumber").replace("%input%", args[0]));
		}
	}
}
