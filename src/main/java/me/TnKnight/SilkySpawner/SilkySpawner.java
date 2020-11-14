package me.TnKnight.SilkySpawner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.Files;

import me.TnKnight.SilkySpawner.Commands.CommandsManager;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.CustomSymbols;
import me.TnKnight.SilkySpawner.Files.InvConfiguration;
import me.TnKnight.SilkySpawner.Files.Message;
import me.TnKnight.SilkySpawner.Files.Mobs;
import me.TnKnight.SilkySpawner.Menus.MenusStorage;
import me.TnKnight.SilkySpawner.Utilities.Storage;

public class SilkySpawner extends JavaPlugin
{
	public static SilkySpawner instance;
	public static String getName;
	
	@Override
	public void onEnable() {
		getName = "[" + getDescription().getName() + "] ";
		instance = this;
		if (getVersion() < 13) {
			Storage.sendLog("Unsupported server version! Disabling...", "SEVERE", true);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		Storage.sendLog("Starting up, please wait...", "INFO", true);
		Config.startup();
		Message.startup();
		InvConfiguration.startup();
		Mobs.startup();
		CustomSymbols.loadSymbols();
		new CommandsManager();
		getServer().getPluginManager().registerEvents(new Listeners(), this);
		if ((boolean) Storage.validateCfg("CustomEnchantment"))
			CustomEnchantment.Register();
		configVersion();
		if ((boolean) Storage.validateCfg("auto-check")) {
			checkNewVersion();
			new BukkitRunnable() {
				@Override
				public void run() {
					checkNewVersion();
				}
			}.runTaskTimer(this, 504000, 504000);
		}
		Storage.sendLog("Plugin has been successfully loaded!", "INFO", true);
	}
	
	@Override
	public void onDisable() {
		if (getVersion() < 13)
			return;
		if ((boolean) Storage.validateCfg("CustomEnchantment"))
			CustomEnchantment.unRegister();
		Storage.sendLog("All data saved, disabling plugin...", "INFO", true);
	}
	
	private static final Map<Player, MenusStorage> storage = new HashMap<>();
	
	public static MenusStorage getStorage(Player player) {
		MenusStorage menusStorage;
		if (!storage.containsKey(player)) {
			menusStorage = new MenusStorage(player);
			storage.put(player, menusStorage);
		} else
			return storage.get(player);
		return menusStorage;
	}
	
	private void configVersion() {
		if (Storage.validateCfg("version").toString().equals(getDescription().getVersion()))
			return;
		File newDest = new File(getDataFolder() + File.separator + "OldFiles");
		newDest.mkdir();
		Storage.sendLog("Defferent version detected! Backing up old files..", "SEVERE", true);
		for (File f : Arrays.asList(Config.file, Message.file, InvConfiguration.file)) {
			Path src = Paths.get(f.getAbsolutePath());
			Path dest = Paths.get(newDest.getAbsolutePath() + File.separator + f.getName().concat(".old"));
			try {
				Files.copy(src.toFile(), dest.toFile());
				f.delete();
				saveResource(f.getName(), false);
			} catch (IOException e) {
				Storage.sendLog("Error occurs when trying to copy " + f.getName() + "! Please, delete or move", "SEVERE", true);
				Storage.sendLog("" + f.getName() + " away and let plugin create again.", "SEVERE", false);
			}
		}
		Config.reload();
		Message.reload();
		InvConfiguration.reload();
	}
	
	private void checkNewVersion() {
		Storage.sendLog("Checking for new version...", "INFO", true);
		try {
			final String url = "https://raw.githubusercontent.com/knighthat/Silky-Spawner/master/plugin.yml";
			BufferedReader file = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			String str;
			while ((str = file.readLine()) != null)
				if (str.startsWith("version: "))
					if (!str.replace("version:", "").trim().equals(getDescription().getVersion())) {
						Storage.sendLog("A new version " + str.replace("version:", "").trim() + " is avaible. Please update!", "WARNING", true);
					} else
						Storage.sendLog("You're on the lastest version!", "INFO", true);
			file.close();
		} catch (IOException e) {
			Storage.sendLog("Failed! Please report to my page ASAP.", "SEVERE", true);
		}
	}
	
	public static final Integer getVersion() {
		final Pattern pattern = Pattern.compile("1.[1-9]{2}");
		String version = instance.getServer().getVersion();
		Matcher matcher = pattern.matcher(version);
		while (matcher.find())
			version = version.substring(matcher.start(), matcher.end()).replace("1.", "");
		return Integer.parseInt(version);
	}
}
