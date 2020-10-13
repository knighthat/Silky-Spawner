package me.TnKnight.SilkySpawner;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	private static SilkySpawner Main = SilkySpawner.instance;
	private static File file = null;
	private static FileConfiguration config = null;

	public static void startup() {
		if (file == null)
			file = new File(Main.getDataFolder(), "config.yml");
		if (!file.exists())
			Main.saveResource("config.yml", false);
		configChecking();
	}

	public static void reload() {
		if (file == null)
			file = new File(Main.getDataFolder(), "config.yml");
		if (!file.exists())
			Main.saveResource("config.yml", false);
		config = YamlConfiguration.loadConfiguration(file);
		Reader data = null;
		try {
			data = new InputStreamReader(Main.getResource("config.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
		}
		if (data != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(data);
			config.setDefaults(defConfig);
		}
	}

	public static FileConfiguration getConfig() {
		if (config == null)
			reload();
		return config;
	}

	public static void configChecking() {
		if (!getConfig().getString("version").equals(Main.getDescription().getVersion())) {
			file.renameTo(new File(Main.getDataFolder(), "config.yml.old"));
			file.delete();
		}
	}
}
