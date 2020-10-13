package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.Config;
import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Utils;

public class CommandsManager implements CommandExecutor, TabCompleter {

	private static SilkySpawner Main = SilkySpawner.instance;
	private List<AbstractClass> Argument = new ArrayList<AbstractClass>();
	private List<String> Arg1 = new ArrayList<String>(Arrays.asList("reload"));

	public CommandsManager() {
		Main.getCommand("SilkySpawner").setExecutor(this);
		Main.getCommand("SilkySpawner").setTabCompleter(this);
		Argument.add(new HelpCommand());
		Argument.add(new EnchantCommand());
		Argument.add(new SetName());
		Argument.add(new Lore());
		Argument.add(new RemoveArmorStand());
		Arg1.addAll(Argument.stream().map(Class -> Class.getName()).collect(Collectors.toList()));
	}

	private AbstractClass get(String Name) {
		Iterator<AbstractClass> ac = Argument.iterator();
		while (ac.hasNext()) {
			AbstractClass sCommand = (AbstractClass) ac.next();
			if (sCommand.getName().equalsIgnoreCase(Name))
				return sCommand;
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("SilkySpawner") || label.equalsIgnoreCase("SS")
				|| label.equalsIgnoreCase("Spawner")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission(Config.getConfig().getString("CommandsAssistant.reload.Permission"))) {
					Config.reload();
					Utils.Prefix = Config.getConfig().getString("Prefix");
					AbstractClass.HoverColor = Config.getConfig().getString("HelpCommand.TextColor");
					AbstractClass.TextColor = Config.getConfig().getString("HelpCommand.HoverText");
					AbstractClass.HoverText = Config.getConfig().getString("HelpCommand.HoverColor");
					sender.sendMessage(Utils.getConfig("ReloadMessage"));
				} else
					sender.sendMessage(Utils.AddColors(Config.getConfig().getString("NoPerm").replace("%perm%",
							Config.getConfig().getString("CommandsAssistant.reload.Permission"))));
			} else if (args.length >= 1 && !args[0].equalsIgnoreCase("reload")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					AbstractClass command = get(args[0]);
					if (command != null) {
						if (Config.getConfig().contains("CommandsAssistant." + args[0].toLowerCase() + ".Permisson"))
							if (!player.hasPermission(Config.getConfig()
									.getString("CommandsAssistant." + args[0].toLowerCase() + ".Permisson")))
								player.sendMessage(Utils.getConfig("NoPerm").replace("%perm%", Config.getConfig()
										.getString("CommandsAssistant." + args[0].toLowerCase() + ".Permisson")));
						List<String> arg1 = new ArrayList<String>(Arrays.asList(args));
						arg1.remove(0);
						String[] strings = new String[arg1.size()];
						strings = arg1.toArray(strings);
						try {
							command.executeCommand(player, strings);
						} catch (Exception e) {
							String StackStrace = String.valueOf(e.getStackTrace()[0]);
							StackStrace = StackStrace.substring(StackStrace.lastIndexOf("(")).replace(")", "")
									.replace("(", "");
							player.sendMessage(Utils
									.AddColors(Config.getConfig().getString("Error").replace("%error%", StackStrace)));
							player.sendMessage(Utils.AddColors(Config.getConfig().getString("ErrorMessage")));

						}
					} else
						player.sendMessage(Utils.getConfig("InvalidCommand"));
				} else
					sender.sendMessage(Utils.getConfig("NotPlayer"));
			} else
				sender.sendMessage(Utils.AddColors(Utils.Prefix + " &f/" + label + " help"));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> results = new ArrayList<String>();
		if (args.length == 1)
			for (String filter : Arg1)
				if (filter.toLowerCase().startsWith(args[0].toLowerCase()))
					results.add(filter);
		if (args.length == 2 && args[0].equalsIgnoreCase("lore"))
			for (String filter : Lore.command)
				if (filter.toLowerCase().startsWith(args[1].toLowerCase()))
					results.add(filter);
		return results;
	}
}
