package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import Utilities.Methods;
import Utilities.Utils;

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
			Methods.clearArea(player.getLocation(), radius);
			player.sendMessage(getMsg("RemoveArmorStandsMessage").replace("%radius%", String.valueOf(radius)));
		} catch (NumberFormatException e) {
			player.sendMessage(getMsg("NotANumber").replace("%input%", args[0]));
		}
	}
}
