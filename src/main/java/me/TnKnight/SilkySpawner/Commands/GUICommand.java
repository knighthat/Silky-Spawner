package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Menus.MainMenu;

public class GUICommand extends CommandsAbstractClass {

	@Override
	public String getName() {
		return "gui";
	}

	@Override
	public String getDescription() {
		return ValidateCfg("CommandsAssistant." + getName() + ".Description");
	}

	@Override
	public String getUsage() {
		return ValidateCfg("CommandsAssistant." + getName() + ".Usage");
	}

	@Override
	public void executeCommand(Player player, String[] args) {
		switch (args.length) {
			case 0 :
				new MainMenu(SilkySpawner.getStorage(player)).openMenu();
				break;
			default :
				player.spigot().sendMessage(cBuilder(getUsage()).create());
				break;
		}
	}

}
