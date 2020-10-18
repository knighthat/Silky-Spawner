package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.CustomEnchantment;
import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.Config;
import net.md_5.bungee.api.ChatColor;

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
			player.sendMessage(Utils.getMessage("CETurnedOff"));
			return;
		}
		if (args.length > 0) {
			player.sendMessage(Utils.getMessage("MistypedCommand").replace("%command%", getUsage()));
		} else if (player.getInventory().getItemInMainHand().getType().toString().endsWith("_PICKAXE")) {
			ItemStack pickaxe = player.getInventory().getItemInMainHand();
			if (!pickaxe.getEnchantments().containsKey(CustomEnchantment.PICKDASPAWNER)) {
				ItemMeta meta = pickaxe.getItemMeta();
				List<String> lore = new ArrayList<String>(
						Arrays.asList(ChatColor.GRAY + Config.getConfig().getString("EnchantmentName")));
				if (meta.getLore() != null && meta.getLore().size() > 1)
					lore.addAll(meta.getLore().stream().map(string -> Utils.AddColors(string))
							.collect(Collectors.toList()));
				meta.setLore(lore);
				pickaxe.setItemMeta(meta);
				pickaxe.addUnsafeEnchantment(CustomEnchantment.PICKDASPAWNER, 1);
				player.sendMessage(Utils.getMessage("Success"));
			} else
				player.sendMessage(Utils.getMessage("AlreadyAdded"));
		} else
			player.sendMessage(Utils.getMessage("NotHoldingPickaxe"));
		return;
	}

}
