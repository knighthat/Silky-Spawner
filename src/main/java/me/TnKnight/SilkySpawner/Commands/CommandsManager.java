package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.InvConfiguration;
import me.TnKnight.SilkySpawner.Files.Message;
import me.TnKnight.SilkySpawner.Files.Mobs;

public class CommandsManager extends Storage implements CommandExecutor, TabCompleter {

	public static List<CommandsAbstractClass> Argument = new ArrayList<>();
	private List<String> Arg1 = new ArrayList<>(Arrays.asList("reload"));
	private final List<String> nSuggestion = new ArrayList<>(Arrays.asList("0", "1", "2", "5"));

	public CommandsManager() {
		SilkySpawner.instance.getCommand("SilkySpawner").setExecutor(this);
		SilkySpawner.instance.getCommand("SilkySpawner").setTabCompleter(this);
		Argument.add(new HelpCommand());
		Argument.add(new EnchantCommand());
		Argument.add(new SetNameCommand());
		Argument.add(new LoreCommand());
		Argument.add(new RemoveArmorStand());
		Argument.add(new CreateSpawnerCommand());
		Argument.add(new GUICommand());
		Arg1.addAll(Argument.stream().map(Class -> Class.getName()).collect(Collectors.toList()));
	}

	private CommandsAbstractClass get(String Name) {
		Iterator<CommandsAbstractClass> ac = Argument.iterator();
		while (ac.hasNext()) {
			CommandsAbstractClass sCommand = (CommandsAbstractClass) ac.next();
			if (sCommand.getName().equalsIgnoreCase(Name))
				return sCommand;
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("SilkySpawner") || label.equalsIgnoreCase("SS") || label.equalsIgnoreCase("Spawner")) {
			if (args.length == 0 || (args.length >= 1 && !Arg1.contains(args[0].toLowerCase()))) {
				if (sender instanceof Player) {
					sendMes(((Player) sender), "help", "help");
				} else
					sender.sendMessage("/silkyspawner help");
				return true;
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (!permConfirm(sender, "admin"))
					return true;
				Config.reload();
				Message.reload();
				InvConfiguration.reload();
				Mobs.reload();
				sender.sendMessage(getMsg("ReloadMessage"));
			} else if (sender instanceof Player) {
				Player player = (Player) sender;
				String subCmd = args[0].toLowerCase();
				CommandsAbstractClass command = get(subCmd) == null ? get("help") : get(subCmd);
				if (!args[0].equalsIgnoreCase("lore") && !args[0].equalsIgnoreCase("create") && !permConfirm(player, "command." + subCmd))
					return true;
				final List<String> arg1 = new ArrayList<>(
				    Arrays.asList(args).stream().filter(string -> !string.equals(args[0])).collect(Collectors.toList()));
				try {
					command.executeCommand(player, arg1.toArray(new String[arg1.size()]));
				} catch (Exception e) {
					String StackStrace = String.valueOf(e.getStackTrace()[0]);
					StackStrace = StackStrace.substring(StackStrace.lastIndexOf("(")).replace(")", "").replace("(", "").replace(".java", "")
					    .concat(" -> " + e.getCause());
					String errorCode = getMsg("Error").replace("%error%", StackStrace);
					player.sendMessage(errorCode);
					player.sendMessage(getMsg("ErrorMessage"));
					SilkySpawner.sendMes(Utils.AddColors(errorCode), Level.SEVERE, true);
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> results = new ArrayList<String>();
		if (!(sender instanceof Player)) {
			if (args.length == 1) {
				results.add("reload");
			} else
				SilkySpawner.sendMes("&cConsole can only be able to execute \"reload\" command!", Level.SEVERE, true);
		} else {
			switch (args.length) {
				case 1 :
					for (String filter : Arg1)
						if (filter.toLowerCase().startsWith(args[0].toLowerCase()))
							results.add(filter);
					break;
				case 2 :
					if (args[0].equalsIgnoreCase("lore"))
						for (String filter : LoreCommand.command)
							if (filter.toLowerCase().startsWith(args[1].toLowerCase()))
								results.add(filter);
					if (args[0].equalsIgnoreCase("create"))
						for (String filter : MobsList.toList())
							if (filter.toLowerCase().startsWith(args[1].toLowerCase()))
								results.add(filter);
					if (args[0].equalsIgnoreCase("remove"))
						for (String filter : nSuggestion)
							if (filter.toLowerCase().startsWith(args[1].toLowerCase()))
								results.add(filter);
					break;
				case 3 :
					if (args[0].equalsIgnoreCase("create"))
						for (String filter : nSuggestion)
							if (filter.toLowerCase().startsWith(args[2].toLowerCase()))
								results.add(filter);
					if (args[0].equalsIgnoreCase("lore") && !args[1].equalsIgnoreCase("add") && LoreCommand.command.contains(args[1].toLowerCase())) {
						Player player = (Player) sender;
						ItemStack item = player.getInventory().getItemInMainHand();
						if (item.getType().equals(Material.SPAWNER) && item.getItemMeta().hasLore()) {
							List<Integer> lore = new ArrayList<>();
							for (int i = 0; i < item.getItemMeta().getLore().size() - 1; i++)
								lore.add(i + 1);
							for (int i : lore)
								if (String.valueOf(i).startsWith(args[2]))
									results.add(String.valueOf(i));
						}
					}
					break;
			}
		}
		Collections.sort(results);
		return results;
	}
}
abstract class CommandsAbstractClass extends Storage {

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