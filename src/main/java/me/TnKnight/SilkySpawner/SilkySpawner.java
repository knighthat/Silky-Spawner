package me.TnKnight.SilkySpawner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;

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
		getServer().getConsoleSender().sendMessage(Utils.AddColors(Storage.Prefix() + "&fLoading commands, message.yml, menus,etc.."));
		new CommandsManager();
		if (Config.getConfig().getBoolean("CustomEnchantment"))
			CustomEnchantment.Register();
		getServer().getPluginManager().registerEvents(new Listeners(), this);
		configChecking();
		getServer().getConsoleSender().sendMessage(Utils.AddColors(Storage.Prefix() + "&fPlugin has been successfully loaded!"));
	}
	@Override
	public void onDisable() {
		if (Config.getConfig().getBoolean("CustomEnchantment"))
			CustomEnchantment.unRegister();
		getServer().getConsoleSender().sendMessage(Utils.AddColors(Storage.Prefix() + "&fAll data saved, disabling plugin..."));
	}

	private void configChecking() {
		if (Config.getConfig().getString("version").equals(getDescription().getVersion()))
			return;
		File newDest = new File(getDataFolder() + File.separator + "OldFiles");
		newDest.mkdir();
		getServer().getConsoleSender().sendMessage(Utils.AddColors(Storage.Prefix() + "&cDefferent version detected! Started copying a new files.."));
		for (File f : Arrays.asList(Config.file, MessageYAML.file, InventoriesConfiguration.file)) {
			Path src = Paths.get(f.getAbsolutePath());
			Path dest = Paths.get(newDest.getAbsolutePath() + File.separator + f.getName().concat(".old"));
			try {
				Files.copy(src.toFile(), dest.toFile());
				f.delete();
				saveResource(f.getName(), false);
			} catch (IOException e) {
				getServer().getConsoleSender()
				    .sendMessage(Utils.AddColors(Storage.Prefix() + "&4Error occurs when copying &f" + f.getName() + "&4. Please, delete or moving"));
				getServer().getConsoleSender().sendMessage(Utils.AddColors("&4this file to other folder and let plugin create again"));
			}
		}
	}
}
