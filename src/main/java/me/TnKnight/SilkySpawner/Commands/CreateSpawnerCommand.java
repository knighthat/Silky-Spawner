package me.TnKnight.SilkySpawner.Commands;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.MobsList;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import net.md_5.bungee.api.chat.TextComponent;

public class CreateSpawnerCommand extends AbstractClass {

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
		EntityType mob = EntityType.valueOf(args[0].toUpperCase());
		switch (args.length) {
		case 0:
			player.spigot().sendMessage(this.getMobsList());
			break;
		case 1:
			if (MobsList.MobChecking(args[0].toUpperCase())) {
				String permission = Config.getConfig().getString("CommandsAssistant.create.Permission") + "."
						+ args[0].toUpperCase();
				if (!player.hasPermission(permission)) {
					player.sendMessage(Utils.getMessage("NoPerm").replace("%perm%", permission));
					return;
				}
				ItemStack iSpawner = new ItemStack(Material.SPAWNER, 1);
				ItemMeta iMeta = iSpawner.getItemMeta();
				iMeta.setLore(Arrays.asList(Utils.AddColors(
						Config.getConfig().getString("TypeOfCreature").replace("%creature_type%", mob.name()))));
				iSpawner.setItemMeta(iMeta);
				iSpawner.setItemMeta(Utils.SpawnCreature(iSpawner, mob));
				if (player.getInventory().firstEmpty() < 0) {
					player.getWorld().dropItem(player.getLocation(), iSpawner);
				} else
					player.getInventory().addItem(iSpawner);
			} else
				player.spigot().sendMessage(this.getMobsList());
			break;
		default:
			player.spigot().sendMessage(Utils.hoverNclick("/silkyspawner create [Mob's name]", TextColor, HoverText,
					HoverColor, "/silkyspawner create "));
		}
	}

	private TextComponent getMobsList() {
		TextComponent text = new TextComponent();
		for (int i = 0; i < MobsList.toList().size(); i++) {
			text.addExtra(Utils.hoverNclick(MobsList.toList().get(i).toLowerCase(), TextColor, HoverText, HoverColor,
					"/silkyspawner create " + MobsList.toList().get(i)));
			text.setItalic(true);
			TextComponent extra = new TextComponent((i == MobsList.toList().size() - 1) ? "." : ", ");
			extra.setHoverEvent(null);
			extra.setClickEvent(null);
			text.addExtra(extra);
		}
		return text;
	}
}
