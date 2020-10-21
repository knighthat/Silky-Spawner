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

import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.InventoriesConfiguration;
import me.TnKnight.SilkySpawner.Files.MessageYAML;

public class CommandsManager implements CommandExecutor, TabCompleter {

	public static List<CommandsAbstractClass> Argument = new ArrayList<>();
	private List<String> Arg1 = new ArrayList<>(Arrays.asList("reload"));

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
			switch (args.length) {
				case 0 :
					sender.sendMessage("/silkyspawner help");
					break;
				default :
					if (args[0].equalsIgnoreCase("reload")) {
						if (sender.hasPermission(Storage.getPerm("reload"))) {
							Config.reload();
							MessageYAML.reload();
							InventoriesConfiguration.reload();
							sender.sendMessage(Storage.getMsg("ReloadMessage"));
						} else
							sender.sendMessage(Storage.getMsg("NoPerm").replace("%perm%", Storage.getPerm("reload")));
					} else if (sender instanceof Player) {
						Player player = (Player) sender;
						String subCmd = args[0].toLowerCase();
						CommandsAbstractClass command = get(subCmd) == null ? get("help") : get(subCmd);
						if (!args[0].equalsIgnoreCase("create") && !Storage.confirmPerm(player, subCmd)) {
							player.sendMessage(Storage.getMsg("NoPerm").replace("%perm%", Storage.getPerm(subCmd)));
							return true;
						}
						final List<String> arg1 = new ArrayList<>(
						    Arrays.asList(args).stream().filter(string -> !string.equals(args[0])).collect(Collectors.toList()));
						try {
							command.executeCommand(player, arg1.toArray(new String[arg1.size()]));
						} catch (Exception e) {
							String StackStrace = String.valueOf(e.getStackTrace()[0]);
							StackStrace = StackStrace.substring(StackStrace.lastIndexOf("(")).replace(")", "").replace("(", "").replace(".java", "")
							    .concat(" -> " + e.getCause());
							player.sendMessage(Storage.getMsg("Error").replace("%error%", StackStrace));
							player.sendMessage(Storage.getMsg("ErrorMessage"));
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
		if (sender instanceof Player && (args.length == 3 && args[0].equalsIgnoreCase("lore"))
		    && (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("insert") || args[1].equalsIgnoreCase("remove"))) {
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
		Collections.sort(results);
		return results;
	}
}
