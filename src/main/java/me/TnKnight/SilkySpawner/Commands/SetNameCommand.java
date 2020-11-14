package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetNameCommand extends CommandsAbstractClass
{
	@Override
	public String getName() {
		return "setname";
	}
	
	@Override
	public void executeCommand(Player player, String label, String[] args) {
		String argument = arrayToString(args, 0);
		if (!permConfirm(player, new String[] { getNode(), cmdNode }))
			return;
		if (!holdingItem(player, true, true))
			return;
		if (!cmdConfirm(player, argument, getUsg(), label))
			return;
		if (!countingSystem(player, argument, true))
			return;
		ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
		ItemMeta sMeta = spawner.getItemMeta();
		sMeta.setDisplayName(addColors(argument));
		spawner.setItemMeta(sMeta);
		addItem(player, spawner, true);
	}
}
