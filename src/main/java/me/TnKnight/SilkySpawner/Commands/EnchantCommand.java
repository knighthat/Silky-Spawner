package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.CustomEnchantment;
import me.TnKnight.SilkySpawner.Files.Config;

public class EnchantCommand extends CommandsAbstractClass {

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
			player.sendMessage(getMsg("CETurnedOff"));
			return;
		}
		if (args.length > 0) {
			player.spigot().sendMessage(cBuilder(getUsage()).create());
		} else if (player.getInventory().getItemInMainHand().getType().toString().endsWith("_PICKAXE")) {
			ItemStack pickaxe = player.getInventory().getItemInMainHand();
			if (!pickaxe.getEnchantments().containsKey(CustomEnchantment.PICKDASPAWNER)) {
				CustomEnchantment.enchantItem(pickaxe);
				player.sendMessage(getMsg("Success"));
			} else
				player.sendMessage(getMsg("AlreadyAdded"));
		} else
			player.sendMessage(getMsg("NotHoldingPickaxe"));
		return;
	}

}
