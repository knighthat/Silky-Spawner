package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Config;
import me.TnKnight.SilkySpawner.Utils;
import net.md_5.bungee.api.chat.TextComponent;

public class Lore extends AbstractClass {

	@Override
	public String getName() {
		return "lore";
	}

	@Override
	public String getDescription() {
		return Config.getConfig().getString("CommandsAssistant." + getName() + ".Description");
	}

	@Override
	public String getUsage() {
		return Config.getConfig().getString("CommandsAssistant." + getName() + ".Usage");
	}

	public static final List<String> command = new ArrayList<String>(Arrays.asList("add", "set", "insert", "remove"));

	@Override
	public void executeCommand(Player player, String[] args) {
		if (args.length == 0 || !command.contains(args[0].toLowerCase())) {
			for (String i : command)
				player.spigot().sendMessage(subCommand(i));
			return;
		}
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER)) {
			player.sendMessage(
					Utils.getConfig("NotHoldingSpawner").replace("%type%", Config.getConfig().getString("Lore")));
			return;
		}
		ItemStack spawner = player.getInventory().getItemInMainHand();
		ItemMeta sMeta = spawner.getItemMeta();
		List<String> sLore = new ArrayList<String>();
		if (sMeta.hasLore())
			sLore.addAll(sMeta.getLore());
		if (args[0].equalsIgnoreCase("add")) {
			String lore = Utils.arrayToString(args, 1);
			if (lore.isEmpty()) {
				player.spigot().sendMessage(subCommand(args[0]));
				return;
			}
			if (!Utils.charsCounting(player,
					(Config.getConfig().getBoolean("CountTheCodes") ? lore : Utils.StripColors(lore)), "Lore"))
				return;

			sLore.add(Utils.AddColors(lore));
			sMeta.setLore(sLore);
			spawner.setItemMeta(sMeta);
		} else {
			String lore = Utils.arrayToString(args, 2);
			if (args[1].isEmpty() || lore.isEmpty()) {
				for (String i : command)
					if (args[0].equalsIgnoreCase(i))
						player.spigot().sendMessage(subCommand(i));
				return;
			}
			if (!args[1].matches("-?\\d+")) {
				player.sendMessage(Utils.getConfig("NotANumber").replace("%input%", args[1]));
				return;
			}
			int line = Integer.parseInt(args[1]) - 1;
			if (line < 0 || line > sLore.size()) {
				player.sendMessage(Utils.getConfig("OutOfLines").replace("%input%", args[1]));
				return;
			}
			List<String> newLore = new ArrayList<String>();
			for (int i = 0; i < sLore.size(); i++)
				if (i == line) {
					switch (args[0].toLowerCase()) {
					case "set":
						newLore.add(lore);
						break;
					case "insert":
						newLore.add(sLore.get(i));
						newLore.add(lore);
						break;
					case "remove":
						break;
					}
				} else
					newLore.add(sLore.get(i));
			sMeta.setLore(newLore.stream().map(string -> Utils.AddColors(string)).collect(Collectors.toList()));
			spawner.setItemMeta(sMeta);
		}
		if (!sMeta.hasDisplayName())
			player.sendMessage(Utils.getConfig("NoName"));
		return;
	}

	private TextComponent subCommand(String SubString) {
		switch (SubString.toLowerCase()) {
		case "add":
			return Utils.hoverNclick("/silkyspawner lore add <lore>", TextColor, HoverText, HoverColor,
					"/silkyspawner lore add ");
		case "set":
			return Utils.hoverNclick("/silkyspawner lore set [line] <lore>", TextColor, HoverText, HoverColor,
					"/silkyspawner lore set ");
		case "insert":
			return Utils.hoverNclick("/silkyspawner lore insert [line] <lore>", TextColor, HoverText, HoverColor,
					"/silkyspawner lore insert ");
		case "remove":
			return Utils.hoverNclick("/silkyspawner lore remove [line]", TextColor, HoverText, HoverColor,
					"/silkyspawner lore remove ");
		}
		return null;
	}
}
