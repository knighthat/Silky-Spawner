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

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.MessageYAML;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class LoreCommand extends CommandsAbstractClass {

	public static final List<String> command = new ArrayList<>(Arrays.asList("add", "set", "insert", "remove"));

	@Override
	public String getName() {
		return "lore";
	}

	@Override
	public String getDescription() {
		return super.getDes(getName());
	}

	@Override
	public String getUsage() {
		return super.getUsg(getName());
	}

	@Override
	public void executeCommand(Player player, String[] args) {
		if (args.length == 0 || !command.contains(args[0].toLowerCase())) {
			for (String string : command)
				this.sendMes(player, string);
			return;
		}
		if ((args.length == 1 && args[0].equalsIgnoreCase("add"))
		    || (args.length == 2 && (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")))) {
			this.sendMes(player, args[0]);
			return;
		}
		String input = Utils.AddColors(Utils.arrayToString(args, args[0].equalsIgnoreCase("add") ? 1 : 2));
		if (!args[0].equalsIgnoreCase("remove")) {
			if (!super.mConfirm(player, "SPAWNER", "Lore"))
				return;
			if (input == null || input.isEmpty()) {
				this.sendMes(player, args[0].toLowerCase());
				return;
			}
			if (!Utils.charsCounting(player, input, "Lore"))
				return;
		}
		ItemStack spawner = player.getInventory().getItemInMainHand();
		ItemMeta sMeta = spawner.getItemMeta();
		List<String> sLore = sMeta.getLore().stream().filter(line -> !line.equals(sMeta.getLore().get(sMeta.getLore().size() - 1)))
		    .collect(Collectors.toList());
		int line = 0;
		if (!args[0].equalsIgnoreCase("add"))
			try {
				line = Integer.parseInt(args[1]);
				line -= 1;
				if (line < 0 || line >= sLore.size()) {
					player.sendMessage(super.getMsg("OutOfLines").replace("%input%", args[1]));
					return;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(super.getMsg("NotANumber").replace("%input%", args[1]));
			}
		switch (args[0].toLowerCase()) {
			case "add" :
				sLore.add(input);
				break;
			case "set" :
				sLore.set(line, input);
				break;
			case "insert" :
				List<String> nLore = new ArrayList<>();
				for (int i = 0; i < sLore.size(); i++) {
					nLore.add(sLore.get(i));
					if (sLore.get(line).equals(sLore.get(i)))
						nLore.add(input);
				}
				sLore.clear();
				sLore = nLore;
				break;
			case "remove" :
				sLore.remove(line);
				break;
		}
		sLore.add(Utils.AddColors(super.ValidateCfg("TypeOfCreature", true).replace("%creature_type%",
		    ((CreatureSpawner) ((BlockStateMeta) spawner.getItemMeta()).getBlockState()).getSpawnedType().name())));
		sMeta.setLore(sLore);
		spawner.setItemMeta(sMeta);
	}

	private void sendMes(Player player, String sCmd) {
		ComponentBuilder usage = new ComponentBuilder(
		    ChatColor.translateAlternateColorCodes('&', MessageYAML.getConfig().getString("MistypedCommand")));
		String cmd = "/silkyspawner lore " + sCmd
		    + (!sCmd.equalsIgnoreCase("add") ? sCmd.equalsIgnoreCase("remove") ? " [line]" : " [line] <lore>" : " <lore>");
		usage.append(Utils.hoverNclick(cmd, "/silkyspawner lore " + sCmd));
		player.spigot().sendMessage(usage.create());
	}
}
