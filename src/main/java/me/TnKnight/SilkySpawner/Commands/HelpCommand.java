package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

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
			ComponentBuilder builder = new ComponentBuilder(
					ChatColor.valueOf(TextColor) + Config.getConfig().getString(path + "Usage"));
			builder.event(
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, Config.getConfig().getString(path + "Usage")));
			builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(HoverText).color(ChatColor.valueOf(HoverColor)).create()));
			builder.appendLegacy(ChatColor.translateAlternateColorCodes('&',
					"&f - " + Config.getConfig().getString(path + "Description")));
			player.spigot().sendMessage(builder.create());
		});
	}
}
