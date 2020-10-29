package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class CreateSpawnerCommand extends CommandsAbstractClass {

	@Override
	public String getName() {
		return "create";
	}

	@Override
	public String getDescription() {
		return Config.getConfig().getString("CommandsAssistant." + getName() + ".Description");
	}

	@Override
	public String getUsage() {
		return Config.getConfig().getString("CommandsAssistant." + getName() + ".Usage");
	}

	@Override
	public void executeCommand(Player player, String[] args) {
		if (args.length == 0) {
			if (!permConfirm(player, "create.getlist") && !permConfirm(player, "create.*"))
				return;
			player.spigot().sendMessage(this.getMobsList());
		} else if (args.length == 1 || args.length == 2) {
			if (MobsList.toList().contains(args[0].toUpperCase())) {
				player.spigot().sendMessage(this.getMobsList());
				return;
			}
			EntityType mob = EntityType.valueOf(args[0].toUpperCase());
			if (!permConfirm(player, mob.name().toLowerCase()) && !permConfirm(player, "create.*"))
				return;
			int amount = 1;
			if (args.length == 2)
				if (args[1].matches("-?\\d+")) {
					if (Integer.parseInt(args[1]) > 0)
						amount = Integer.parseInt(args[1]);
				} else
					player.sendMessage(getMsg("NotANumber").replace("%input%", args[1]));
			final List<String> lore = new ArrayList<>(
			    Arrays.asList(Config.getConfig().getString("TypeOfCreature").replace("%creature_type%", MobsList.getMobName(mob.name()))));
			addItem(player, setItem(new ItemStack(Material.SPAWNER, amount), null, lore, mob));
		} else
			player.spigot().sendMessage(cBuilder(getUsage()).create());
	}

	private TextComponent getMobsList() {
		TextComponent text = new TextComponent();
		for (int i = 0; i < MobsList.toList().size(); i++)
			text.addExtra(
			    Utils.hoverNclick(MobsList.toList().get(i).toLowerCase() + ChatColor.GRAY + ((i == MobsList.toList().size() - 1) ? "." : ", "),
			        "/silkyspawner create " + MobsList.toList().get(i)));
		text.setItalic(true);
		return text;
	}
}
