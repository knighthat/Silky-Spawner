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

import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.InventoriesConfiguration;
import me.TnKnight.SilkySpawner.Files.MessageYAML;
import me.TnKnight.SilkySpawner.MenusStorage.MenuAbstractClass;

public class CommandsManager implements CommandExecutor, TabCompleter {

	private static SilkySpawner Main = SilkySpawner.instance;
	private List<AbstractClass> Argument = new ArrayList<AbstractClass>();
	private List<String> Arg1 = new ArrayList<String>(Arrays.asList("reload"));
	MenuAbstractClass menuAbstractClass;

	public CommandsManager() {
		Main.getCommand("SilkySpawner").setExecutor(this);
		Main.getCommand("SilkySpawner").setTabCompleter(this);
		Argument.add(new HelpCommand());
		Argument.add(new EnchantCommand());
		Argument.add(new SetNameCommand());
		Argument.add(new LoreCommand());
		Argument.add(new RemoveArmorStand());
		Argument.add(new CreateSpawnerCommand());
		Argument.add(new GUICommand());
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
		if (label.equalsIgnoreCase("SilkySpawner") || label.equalsIgnoreCase("SS") || label.equalsIgnoreCase("Spawner")) {
			switch (args.length) {
				case 0 :
					sender.sendMessage("/silkyspawner help");
					break;
				default :
					switch (args[0].toLowerCase()) {
						case "reload" :
							if (sender.hasPermission(Config.getConfig().getString("CommandsAssistant.reload.Permission"))) {
								Config.reload();
								MessageYAML.reload();
								InventoriesConfiguration.reload();
								Utils.Prefix = Config.getConfig().getString("Prefix");
								AbstractClass.HoverColor = Config.getConfig().getString("HelpCommand.TextColor");
								AbstractClass.TextColor = Config.getConfig().getString("HelpCommand.HoverText");
								AbstractClass.HoverText = Config.getConfig().getString("HelpCommand.HoverColor");
								sender.sendMessage(Utils.getMessage("ReloadMessage"));
							} else
								sender.sendMessage(Utils.getMessage("NoPerm").replace("%perm%",
								    Config.getConfig().getString("CommandsAssistant.reload.Permission")));
							break;
						default :
							if (!(sender instanceof Player))
								return true;
							Player player = (Player) sender;
							AbstractClass command = get(args[0]) == null ? get("help") : get(args[0]);
							String path = "CommandsAssistant." + args[0].toLowerCase() + ".Permisson";
							if (!args[0].equalsIgnoreCase("create") && Config.getConfig().contains(path)
							    && !player.hasPermission(Config.getConfig().getString(path)))
								player.sendMessage(Utils.getMessage("NoPerm").replace("%perm%", Config.getConfig().getString(path)));
							final List<String> arg1 = new ArrayList<String>(
							    Arrays.asList(args).stream().filter(string -> !string.equals(args[0])).collect(Collectors.toList()));
							try {
								command.executeCommand(player, arg1.toArray(new String[arg1.size()]));
							} catch (Exception e) {
								String StackStrace = String.valueOf(e.getStackTrace()[0]);
								StackStrace = StackStrace.substring(StackStrace.lastIndexOf("(")).replace(")", "").replace("(", "").replace(".java",
								    "") + " -> " + e.getCause();
								player.sendMessage(Utils.getMessage("Error").replace("%error%", StackStrace));
								player.sendMessage(Utils.AddColors(MessageYAML.getConfig().getString("ErrorMessage")));
								e.printStackTrace();
							}
					}

			}
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
		if (args.length == 2)
			switch (args[0].toLowerCase()) {
				case "lore" :
					for (String filter : LoreCommand.command)
						if (filter.toLowerCase().startsWith(args[1].toLowerCase()))
							results.add(filter);
					break;
				case "create" :
					for (String filter : MobsList.toList())
						if (filter.toLowerCase().startsWith(args[1].toLowerCase()))
							results.add(filter);
					break;
			}
		return results;
	}
}
