package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Files.Config;

public abstract class CommandsAbstractClass extends Storage {

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getUsage();

	public abstract void executeCommand(Player player, String[] args);

	protected String getDes(String cmd) {
		return Config.getConfig().getString("CommandsAssistant." + cmd + ".Description");
	}
	protected String getUsg(String cmd) {
		return Config.getConfig().getString("CommandsAssistant." + cmd + ".Usage");
	}
}
