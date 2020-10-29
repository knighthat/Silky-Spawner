package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Listeners.Spawners;

public class RemoveArmorStand extends CommandsAbstractClass {

	@Override
	public String getName() {
		return "remove";
	}

	@Override
	public String getDescription() {
		return getDes(getName());
	}

	@Override
	public String getUsage() {
		return getUsg(getName());
	}

	@Override
	public void executeCommand(Player player, String[] args) {
		if (!cConfirm(player, Utils.arrayToString(args, 0), getUsage()))
			return;
		try {
			final int radius = Integer.parseInt(args[0]);
			if (radius < 0)
				return;
			player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).stream()
			    .filter(E -> E.getType().equals(EntityType.ARMOR_STAND)).filter(E -> E.getCustomName() != null)
			    .filter(E -> ((ArmorStand) E).getHealth() == Spawners.Serial).forEach(E -> E.remove());
			player.sendMessage(getMsg("RemoveArmorStandsMessage").replace("%radius%", String.valueOf(radius)));
		} catch (NumberFormatException e) {
			player.sendMessage(getMsg("NotANumber").replace("%input%", args[0]));
		}
	}
}
