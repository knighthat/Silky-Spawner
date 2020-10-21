package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Menus.MainMenu;
import me.TnKnight.SilkySpawner.Menus.MenusStorage;

public class GUICommand extends CommandsAbstractClass {

	@Override
	public String getName() {
		return "gui";
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
		switch (args.length) {
			case 0 :
				new MainMenu(new MenusStorage(player)).openMenu();
				break;
			default :
				player.spigot().sendMessage(super.cBuilder(getUsage()).create());
				break;
		}
	}

}
