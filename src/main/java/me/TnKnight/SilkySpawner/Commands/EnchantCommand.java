package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.CustomEnchantment;

public class EnchantCommand extends CommandsAbstractClass {

	@Override
	public String getName() {
		return "enchant";
	}

	@Override
	public String getDescription() {
		return ValidateCfg("CommandsAssistant." + getName() + ".Description");
	}

	@Override
	public String getUsage() {
		return ValidateCfg("CommandsAssistant." + getName() + ".Usage");
	}

	@Override
	public void executeCommand(Player player, String[] args) {
		if (!getBoolean("CustomEnchantment")) {
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
