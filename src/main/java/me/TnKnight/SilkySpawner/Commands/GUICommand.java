package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Menus.MainMenu;

public class GUICommand extends CommandsAbstractClass
{
	@Override
	public String getName() {
		return "gui";
	}
	
	@Override
	public void executeCommand(Player player, String label, String[] args) {
		if (!permConfirm(player, new String[] { getNode(), wildcard, cmdNode }))
			return;
		switch (args.length)
		{
			case 0:
				new MainMenu(SilkySpawner.getStorage(player)).openMenu();
				break;
			
			default:
				final String usg = getUsg().replace(getUsg().split(" ")[0], label);
				player.spigot().sendMessage(misTyped(usg));
				break;
		}
	}
}
