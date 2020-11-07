package me.TnKnight.SilkySpawner.Commands;

import java.util.Iterator;

import org.bukkit.entity.Player;

import Utilities.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class HelpCommand extends CommandsAbstractClass {
	@Override
	public String getName() {
		return "help";
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
		player.sendMessage(Utils.AddColors(ValidateCfg("HelpCommand.Header")));
		Iterator<CommandsAbstractClass> cmdAb = CommandsManager.Argument.iterator();
		while (cmdAb.hasNext()) {
			CommandsAbstractClass sCommand = (CommandsAbstractClass) cmdAb.next();
			ComponentBuilder builder = new ComponentBuilder(sCommand.getUsage());
			builder.color(TextColor()).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, sCommand.getUsage()))
			    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(HoverText()).color(HoverColor()).create()))
			    .append(ChatColor.translateAlternateColorCodes('&', "&f - " + sCommand.getDescription()));
			player.spigot().sendMessage(builder.create());
		}
	}
}
