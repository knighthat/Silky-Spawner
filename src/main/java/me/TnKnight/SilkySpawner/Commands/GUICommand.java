package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.MenusStorage.MainMenu;
import me.TnKnight.SilkySpawner.MenusStorage.Storage;

public class GUICommand extends AbstractClass {

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
				new MainMenu(new Storage(player)).openMenu();
				break;
			default :
				Utils.hoverNclick(getUsage(), TextColor, HoverText, HoverColor, getUsage());
				break;
		}
	}

}
