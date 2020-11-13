package me.TnKnight.SilkySpawner.Commands;

import java.util.Iterator;

import org.bukkit.entity.Player;

public class HelpCommand extends CommandsAbstractClass
{
	@Override
	public String getName() {
		return "help";
	}
	
	@Override
	public void executeCommand(Player player, String label, String[] args) {
		if (!permConfirm(player, new String[] { getNode(), wildcard, cmdNode }))
			return;
		player.sendMessage(addColors(validateCfg("HelpCommand.Header").toString()));
		Iterator<CommandsAbstractClass> cmdAb = CommandsManager.Argument.iterator();
		while (cmdAb.hasNext()) {
			CommandsAbstractClass sCommand = cmdAb.next();
			if (player.hasPermission(sCommand.getNode()) || player.hasPermission(wildcard) || player.hasPermission(cmdNode)) {
				final String usg = sCommand.getUsg().replace(getUsg().split(" ")[0], label);
				final String des = addColors("&f - " + sCommand.getDes());
				player.spigot().sendMessage(clickableMessage(usg + des, usg));
			}
		}
	}
}
