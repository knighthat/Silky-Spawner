package me.TnKnight.SilkySpawner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;

import me.TnKnight.SilkySpawner.Commands.CommandsManager;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.InvConfiguration;
import me.TnKnight.SilkySpawner.Files.Message;
import me.TnKnight.SilkySpawner.Files.Mobs;
import me.TnKnight.SilkySpawner.Listeners.Interaction;
import me.TnKnight.SilkySpawner.Listeners.Spawners;

public class SilkySpawner extends JavaPlugin {

	public static SilkySpawner instance;
	public static String getName;

	@Override
	public void onEnable() {
		instance = this;
		getName = ChatColor.WHITE + "&7[" + instance.getDescription().getName() + "]&a ";
		getServer().getConsoleSender().sendMessage(Utils.AddColors(getName + "Starting up, please wait..."));
		Config.startup();
		Message.startup();
		InvConfiguration.startup();
		Mobs.startup();
		new CommandsManager();
		new Spawners(this);
		new Interaction(this);
		if (Config.getConfig().getBoolean("CustomEnchantment"))
			CustomEnchantment.Register();
		configChecking();
		if (Config.getConfig().getBoolean("auto-check"))
			checkNewVersion();
		getServer().getConsoleSender().sendMessage(Utils.AddColors(getName + "Plugin has been successfully loaded!"));
	}
	@Override
	public void onDisable() {
		if (Config.getConfig().getBoolean("CustomEnchantment"))
			CustomEnchantment.unRegister();
		getServer().getConsoleSender().sendMessage(Utils.AddColors(getName + "All data saved, disabling plugin..."));
	}

	private void configChecking() {
		if (Config.getConfig().getString("version").equals(getDescription().getVersion()))
			return;
		File newDest = new File(getDataFolder() + File.separator + "OldFiles");
		newDest.mkdir();
		getServer().getConsoleSender().sendMessage(Utils.AddColors(getName + "&cDefferent version detected! Started copying a new files.."));
		for (File f : Arrays.asList(Config.file, Message.file, InvConfiguration.file)) {
			Path src = Paths.get(f.getAbsolutePath());
			Path dest = Paths.get(newDest.getAbsolutePath() + File.separator + f.getName().concat(".old"));
			try {
				Files.copy(src.toFile(), dest.toFile());
				f.delete();
				saveResource(f.getName(), false);
			} catch (IOException e) {
				getServer().getConsoleSender()
				    .sendMessage(Utils.AddColors(getName + "&4Error occurs when copying &f" + f.getName() + "&4. Please, delete or moving"));
				getServer().getConsoleSender().sendMessage(Utils.AddColors("&4this file to other folder and let plugin create again"));
			}
		}
		Config.reload();
		Message.reload();
		InvConfiguration.reload();
	}

	private void checkNewVersion() {
		getServer().getConsoleSender().sendMessage(Utils.AddColors(getName + "Checking for new version..."));
		try {
			BufferedReader file = new BufferedReader(
			    new InputStreamReader(new URL("https://raw.githubusercontent.com/knighthat/Silky-Spawner/master/plugin.yml").openStream()));
			String str;
			while ((str = file.readLine()) != null) {
				if (str.startsWith("version: ") && !str.replace("version:", "").trim().equals(getDescription().getVersion()))
					getServer().getConsoleSender().sendMessage(
					    Utils.AddColors(getName + "A new version " + str.replace("version:", "").trim() + " is avaible. Please update!"));
			}
			file.close();
		} catch (IOException e) {
			getServer().getConsoleSender().sendMessage(Utils.AddColors(getName + "&cChecking failed! Please report to my page ASAP."));
		}
	}
}
