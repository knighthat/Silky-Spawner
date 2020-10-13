package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Config;
import me.TnKnight.SilkySpawner.Utils;
import net.md_5.bungee.api.chat.TextComponent;

public class HelpCommand extends AbstractClass {

	@Override
	public String getName() {
		return "help";
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
		player.sendMessage(Utils.AddColors(Config.getConfig().getString("HelpCommand.Header")));
		Config.getConfig().getConfigurationSection("CommandsAssistant").getKeys(false).forEach(section -> {
			String path = "CommandsAssistant." + section + ".";
			TextComponent message = Utils.hoverNclick(Config.getConfig().getString(path + "Usage"),
					Config.getConfig().getString("HelpCommand.TextColor"),
					Config.getConfig().getString("HelpCommand.HoverText"),
					Config.getConfig().getString("HelpCommand.HoverColor"),
					Config.getConfig().getString(path + "Usage"));
			message.addExtra(Utils.AddColors("&f - " + Config.getConfig().getString(path + "Description")));
			player.spigot().sendMessage(message);
		});
	}
}
