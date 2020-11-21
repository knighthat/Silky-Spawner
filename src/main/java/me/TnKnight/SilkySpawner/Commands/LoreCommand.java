package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreCommand extends CommandsAbstractClass
{
	public static final List<String> command = new ArrayList<>(Arrays.asList("add", "set", "insert", "remove"));
	
	@Override
	public String getName() {
		return "lore";
	}
	
	private String label = new String();
	
	@Override
	public void executeCommand(Player player, String label, String[] args) {
		this.label = label + " " + getName() + " ";
		if (args.length == 0 || !command.contains(args[0].toLowerCase())) {
			player.sendMessage(getMsg("MistypedCommand"));
			for (String cmd : command)
				if (player.hasPermission("silkyspwaner." + cmd) || player.hasPermission(cmdNode) || player.hasPermission(wildcard))
					this.sendMes(player, cmd);
			return;
		}
		if (!permConfirm(player, new String[] { getNode() + "." + args[0].toLowerCase(), getNode().concat(".*"), cmdNode })) {
			return;
		}
		if ((args.length == 1 && args[0].equalsIgnoreCase("add"))
				|| (args.length == 2 && (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")))) {
			this.sendMes(player, args[0]);
			return;
		}
		if (!args[0].equalsIgnoreCase("add") && !player.getInventory().getItemInMainHand().getItemMeta().hasLore()) {
			player.sendMessage(getMsg("NoLore"));
			return;
		}
		String input = addColors(arrayToString(args, args[0].equalsIgnoreCase("add") ? 1 : 2));
		if (!args[0].equalsIgnoreCase("remove")) {
			if (!holdingItem(player, true, false))
				return;
			if (input == null || input.isEmpty()) {
				this.sendMes(player, args[0].toLowerCase());
				return;
			}
			if (!countingSystem(player, input, false))
				return;
		}
		ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
		ItemMeta sMeta = spawner.getItemMeta();
		List<String> sLore = sMeta.getLore().stream().filter(line -> !line.equals(sMeta.getLore().get(sMeta.getLore().size() - 1)))
				.collect(Collectors.toList());
		int line = 0;
		if (!args[0].equalsIgnoreCase("add"))
			try {
				line = Integer.parseInt(args[1]);
				line -= 1;
				if (line < 0 || line >= sLore.size()) {
					player.sendMessage(getMsg("OutOfLines").replace("%input%", args[1]));
					return;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(getMsg("NotANumber").replace("%input%", args[1]));
			}
		switch (args[0].toLowerCase())
		{
			case "add":
				sLore.add(input);
				break;
			
			case "set":
				sLore.set(line, input);
				break;
			
			case "insert":
				List<String> nLore = new ArrayList<>();
				for (int i = 0; i < sLore.size(); i++) {
					nLore.add(sLore.get(i));
					if (sLore.get(line).equals(sLore.get(i)))
						nLore.add(input);
				}
				sLore.clear();
				sLore = nLore;
				break;
			
			case "remove":
				sLore.remove(line);
				break;
		}
		sLore.add(getMobName(((CreatureSpawner) ((BlockStateMeta) spawner.getItemMeta()).getBlockState()).getSpawnedType()));
		sMeta.setLore(sLore);
		spawner.setItemMeta(sMeta);
		addItem(player, spawner, true);
	}
	
	private void sendMes(Player player, String sCmd) {
		String cmd = label + sCmd + (!sCmd.equalsIgnoreCase("add") ? sCmd.equalsIgnoreCase("remove") ? " [line]" : " [line] <lore>" : " <lore>");
		player.spigot().sendMessage(clickableMessage(cmd, cmd));
	}
}
