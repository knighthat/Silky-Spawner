package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

public class RemoveArmorStandsCommand extends CommandsAbstractClass
{
	@Override
	public String getName() {
		return "remove";
	}
	
	@Override
	public void executeCommand(Player player, String label, String[] args) {
		if (!permConfirm(player, new String[] { getNode(), cmdNode }))
			return;
		if (!cmdConfirm(player, arrayToString(args, 0), getUsg(), label))
			return;
		try {
			final int radius = Integer.parseInt(args[0]);
			if (radius < 0)
				return;
			clearArea(player.getLocation(), radius);
			player.sendMessage(getMsg("RemoveArmorStandsMessage").replace("%radius%", String.valueOf(radius)));
		} catch (NumberFormatException e) {
			player.sendMessage(getMsg("NotANumber").replace("%input%", args[0]));
		}
	}
}
