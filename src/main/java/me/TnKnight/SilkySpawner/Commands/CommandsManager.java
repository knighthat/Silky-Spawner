package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.CustomSymbols;
import me.TnKnight.SilkySpawner.Files.InvConfiguration;
import me.TnKnight.SilkySpawner.Files.Message;
import me.TnKnight.SilkySpawner.Files.Mobs;
import me.TnKnight.SilkySpawner.Utilities.MobsList;
import me.TnKnight.SilkySpawner.Utilities.Storage;

public class CommandsManager extends Storage implements CommandExecutor, TabCompleter
{
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
		Argument.add(new RemoveArmorStandsCommand());
		Argument.add(new CreateSpawnerCommand());
		Argument.add(new GUICommand());
		Argument.add(new ChangeMobCommand());
		Arg1.addAll(Argument.stream().map(Class -> Class.getName()).collect(Collectors.toList()));
	}
	
	private CommandsAbstractClass get(String Name) {
		Iterator<CommandsAbstractClass> ac = Argument.iterator();
		while (ac.hasNext()) {
			CommandsAbstractClass sCommand = ac.next();
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
					final String getCmd = "/" + label + " help";
					sender.spigot().sendMessage(misTyped(getCmd, "/" + label));
				} else
					sender.sendMessage("/silkyspawner help");
				return true;
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (!permConfirm(sender, new String[] { "admin" }))
					return true;
				Config.reload();
				Message.reload();
				InvConfiguration.reload();
				Mobs.reload();
				CustomSymbols.loadSymbols();
				sender.sendMessage(getMsg("ReloadMessage"));
			} else if (sender instanceof Player) {
				Player player = (Player) sender;
				String subCmd = args[0].toLowerCase();
				CommandsAbstractClass command = get(subCmd) == null ? get("help") : get(subCmd);
				final List<String> arg1 = new ArrayList<>(
						Arrays.asList(args).stream().filter(string -> !string.equals(args[0])).collect(Collectors.toList())
				);
				try {
					command.executeCommand(player, "/" + label, arg1.toArray(new String[arg1.size()]));
				} catch (Exception e) {
					String StackStrace = String.valueOf(e.getStackTrace()[0]);
					StackStrace = StackStrace.substring(StackStrace.lastIndexOf("(")).replace(")", "").replace("(", "").replace(".java", "")
							.concat(" -> " + e.getCause());
					String errorCode = getMsg("Error").replace("%error%", StackStrace);
					player.sendMessage(errorCode);
					player.sendMessage(getMsg("ErrorMessage"));
					Storage.sendLog(errorCode, "SEVERE", false);
					e.printStackTrace();
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
				Storage.sendLog("&cConsole can only be able to execute \"reload\" command!", "SEVERE", true);
		} else {
			Player player = (Player) sender;
			switch (args.length)
			{
				case 1:
					for (String filter : Arg1)
						if (filter.toLowerCase().startsWith(args[0].toLowerCase()))
							results.add(filter);
					break;
				
				case 2:
					if (args[0].equalsIgnoreCase("lore"))
						for (String filter : LoreCommand.command)
							if (filter.toLowerCase().startsWith(args[1].toLowerCase()))
								results.add(filter);
					if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("changemob"))
						for (String filter : MobsList.toList())
							if (filter.toLowerCase().startsWith(args[1].toLowerCase())
									&& player.hasPermission("silkyspawner.command.create." + filter))
								results.add(filter);
					if (args[0].equalsIgnoreCase("remove"))
						for (String filter : nSuggestion)
							if (filter.toLowerCase().startsWith(args[1].toLowerCase()))
								results.add(filter);
					break;
				
				case 3:
					if (args[0].equalsIgnoreCase("create"))
						for (String filter : nSuggestion)
							if (filter.toLowerCase().startsWith(args[2].toLowerCase()))
								results.add(filter);
					if (args[0].equalsIgnoreCase("lore") && !args[1].equalsIgnoreCase("add") && LoreCommand.command.contains(args[1].toLowerCase())) {
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

abstract class CommandsAbstractClass extends Storage
{
	
	public abstract String getName();
	
	public abstract void executeCommand(Player player, String label, String[] args);
	
	protected String getDes() {
		return validateCfg("CommandsAssistant." + getName() + ".Description").toString();
	}
	
	protected String getUsg() {
		return validateCfg("CommandsAssistant." + getName() + ".Usage").toString();
	}
	
	protected final String getNode() {
		return "command." + getName();
	}
	
	protected final static String cmdNode = "command.*";
}