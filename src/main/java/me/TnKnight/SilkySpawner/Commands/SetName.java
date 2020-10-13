package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Config;
import me.TnKnight.SilkySpawner.Utils;

public class SetName extends AbstractClass {

	@Override
	public String getName() {
		return "setname";
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
		String name = Utils.arrayToString(args, 0);
		ItemStack spawner = player.getInventory().getItemInMainHand();
		if (!spawner.getType().equals(Material.SPAWNER)) {
			player.sendMessage(Utils.getConfig("NotHoldingSpawner").replace("%type%", "Name"));
			return;
		}
		if (name.isEmpty()) {
			player.sendMessage(Utils.getConfig("MistypedCommand").replace("%command%", getUsage()));
			return;
		}
		if (!Utils.charsCounting(player,
				(Config.getConfig().getBoolean("CountTheCodes") ? name : Utils.StripColors(name)),
				Config.getConfig().getString("Name")))
			return;
		ItemMeta sMeta = spawner.getItemMeta();
		sMeta.setDisplayName(Utils.AddColors(name));
		spawner.setItemMeta(sMeta);
	}

}
