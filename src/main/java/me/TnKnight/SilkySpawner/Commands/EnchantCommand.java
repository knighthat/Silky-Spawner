package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.CustomEnchantment;

public class EnchantCommand extends CommandsAbstractClass
{
	@Override
	public String getName() {
		return "enchant";
	}
	
	@Override
	public void executeCommand(Player player, String label, String[] args) {
		if (!permConfirm(player, new String[] { getNode(), cmdNode }))
			return;
		if (!(boolean) validateCfg("CustomEnchantment")) {
			player.sendMessage(getMsg("CETurnedOff"));
			return;
		}
		if (args.length > 0) {
			player.spigot().sendMessage(misTyped(getUsg(), label));
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
