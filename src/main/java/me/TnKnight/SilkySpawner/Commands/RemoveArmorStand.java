package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Listeners;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Utils;

public class RemoveArmorStand extends CommandsAbstractClass {

	@Override
	public String getName() {
		return "remove";
	}

	@Override
	public String getDescription() {
		return super.getDes(getName());
	}

	@Override
	public String getUsage() {
		return super.getUsg(getName());
	}

	@Override
	public void executeCommand(Player player, String[] args) {
		if (!super.cConfirm(player, Utils.arrayToString(args, 0), getUsage()))
			return;
		try {
			final int radius = Integer.parseInt(args[0]);
			if (radius < 0)
				return;
			player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).stream()
			    .filter(E -> E.getType().equals(EntityType.ARMOR_STAND)).filter(E -> E.getCustomName() != null)
			    .filter(E -> ((ArmorStand) E).getHealth() == Listeners.Serial).forEach(E -> E.remove());
			player.sendMessage(super.getMsg("RemoveArmorStandsMessage").replace("%radius%", String.valueOf(radius)));
		} catch (NumberFormatException e) {
			player.sendMessage(Storage.getMsg("NotANumber").replace("%input%", args[0]));
		}
	}
}
