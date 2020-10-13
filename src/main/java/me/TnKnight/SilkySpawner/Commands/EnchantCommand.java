package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Config;
import me.TnKnight.SilkySpawner.CustomEnchantment;
import me.TnKnight.SilkySpawner.Utils;

public class EnchantCommand extends AbstractClass {

	@Override
	public String getName() {
		return "enchant";
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
		if (!Config.getConfig().getBoolean("CustomEnchantment")) {
			player.sendMessage(Utils.getConfig("CETurnedOff"));
			return;
		}
		if (args.length > 0) {
			player.sendMessage(Utils.getConfig("MistypedCommand").replace("%command%", getUsage()));
		} else if (player.getInventory().getItemInMainHand().getType().toString().endsWith("_PICKAXE")) {
			ItemStack pickaxe = player.getInventory().getItemInMainHand();
			if (!pickaxe.getEnchantments().containsKey(CustomEnchantment.PICKDASPAWNER)) {
				ItemMeta meta = pickaxe.getItemMeta();
				List<String> lore = new ArrayList<String>(Arrays.asList(Utils.AddColors("&7Spawner Picker")));
				if (meta.getLore() != null && meta.getLore().size() > 1)
					lore.addAll(meta.getLore().stream().map(string -> Utils.AddColors(string))
							.collect(Collectors.toList()));
				meta.setLore(lore);
				pickaxe.setItemMeta(meta);
				pickaxe.addUnsafeEnchantment(CustomEnchantment.PICKDASPAWNER, 1);
			} else
				player.sendMessage(Utils.getConfig("AlreadyAdded"));
		} else
			player.sendMessage(Utils.getConfig("NotHoldingPickaxe"));
		return;
	}

}
