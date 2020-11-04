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
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;

import me.TnKnight.SilkySpawner.Commands.CommandsManager;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.InvConfiguration;
import me.TnKnight.SilkySpawner.Files.Message;
import me.TnKnight.SilkySpawner.Files.Mobs;
import me.TnKnight.SilkySpawner.Listeners.Interaction;
import me.TnKnight.SilkySpawner.Listeners.Spawners;
import me.TnKnight.SilkySpawner.Menus.MenusStorage;

public class SilkySpawner extends JavaPlugin {

	public static SilkySpawner instance;
	public static String getName;

	@Override
	public void onEnable() {
		instance = this;
		getName = ChatColor.WHITE + "[" + instance.getDescription().getName() + "] ";
		sendMes("Starting up, please wait...", Level.INFO, true);
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
		sendMes("Plugin has been successfully loaded!", Level.INFO, true);
	}
	@Override
	public void onDisable() {
		if (Config.getConfig().getBoolean("CustomEnchantment"))
			CustomEnchantment.unRegister();
		sendMes("All data saved, disabling plugin...", Level.INFO, true);
	}

	public static String sendMes(String msg, Level level, boolean prefix) {
		instance.getServer().getLogger().log(level, Utils.StripColors((prefix ? getName : "") + msg));
		return null;
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

	private void configChecking() {
		if (Config.getConfig().getString("version").equals(getDescription().getVersion()))
			return;
		File newDest = new File(getDataFolder() + File.separator + "OldFiles");
		newDest.mkdir();
		getServer().getConsoleSender().sendMessage(Utils.AddColors(getName + "Defferent version detected! Backing up old files.."));
		for (File f : Arrays.asList(Config.file, Message.file, InvConfiguration.file)) {
			Path src = Paths.get(f.getAbsolutePath());
			Path dest = Paths.get(newDest.getAbsolutePath() + File.separator + f.getName().concat(".old"));
			try {
				Files.copy(src.toFile(), dest.toFile());
				f.delete();
				saveResource(f.getName(), false);
			} catch (IOException e) {
				sendMes("Error occurs when trying to copy " + f.getName() + "! Please, delete or move", Level.SEVERE, true);
				sendMes("" + f.getName() + " away and let plugin create again.", Level.SEVERE, false);
			}
		}
		Config.reload();
		Message.reload();
		InvConfiguration.reload();
	}

	private void checkNewVersion() {
		sendMes("Checking for new version...", Level.INFO, true);
		try {
			BufferedReader file = new BufferedReader(
			    new InputStreamReader(new URL("https://raw.githubusercontent.com/knighthat/Silky-Spawner/master/plugin.yml").openStream()));
			String str;
			while ((str = file.readLine()) != null)
				if (str.startsWith("version: "))
					if (!str.replace("version:", "").trim().equals(getDescription().getVersion())) {
						sendMes("A new version " + str.replace("version:", "").trim() + " is avaible. Please update!", Level.WARNING, true);
					} else
						sendMes("You're on the lastest version!", Level.INFO, true);

			file.close();
		} catch (IOException e) {
			sendMes("Checking failed! Please report to my page ASAP.", Level.SEVERE, true);
		}
	}
}
