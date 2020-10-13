package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Config;
import me.TnKnight.SilkySpawner.SilkySpawner;

public abstract class AbstractClass {

	public SilkySpawner Main = SilkySpawner.instance;

	public static String TextColor = Config.getConfig().getString("HelpCommand.TextColor");

	public static String HoverText = Config.getConfig().getString("HelpCommand.HoverText");

	public static String HoverColor = Config.getConfig().getString("HelpCommand.HoverColor");

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getUsage();

	public abstract void executeCommand(Player player, String[] args);
}
