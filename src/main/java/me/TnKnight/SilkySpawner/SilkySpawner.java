package me.TnKnight.SilkySpawner;

import org.bukkit.plugin.java.JavaPlugin;

import me.TnKnight.SilkySpawner.Commands.CommandsManager;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.InventoriesConfiguration;
import me.TnKnight.SilkySpawner.Files.MessageYAML;

public class SilkySpawner extends JavaPlugin {

	public static SilkySpawner instance;

	@Override
	public void onEnable() {
		instance = this;
		Config.startup();
		MessageYAML.startup();
		InventoriesConfiguration.startup();
		getServer().getConsoleSender().sendMessage(Utils.AddColors(Utils.Prefix + "&fLoading commands, message.yml, menus,etc.."));
		new CommandsManager();
		if (Config.getConfig().getBoolean("CustomEnchantment"))
			CustomEnchantment.Register();
		getServer().getPluginManager().registerEvents(new Listeners(), this);
		getServer().getConsoleSender().sendMessage(Utils.AddColors(Utils.Prefix + "&aPlugin has been successfully loaded!"));
	}
	@Override
	public void onDisable() {
		if (Config.getConfig().getBoolean("CustomEnchantment"))
			CustomEnchantment.unRegister();
		getServer().getConsoleSender().sendMessage(Utils.AddColors(Utils.Prefix + "&aDisabled plugin, all data saved!"));
	}
}
