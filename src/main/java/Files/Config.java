package Files;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.TnKnight.SilkySpawner.SilkySpawner;

public class Config {
  private static SilkySpawner Main = SilkySpawner.instance;
  public static File file = null;
  private static FileConfiguration config = null;
  
  public static void startup() {
    if (file == null)
      file = new File(Main.getDataFolder(), "config.yml");
    if (!file.exists())
      Main.saveResource("config.yml", false);
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
    }
    catch (UnsupportedEncodingException e) {}
    if (data != null) {
      YamlConfiguration configData = YamlConfiguration.loadConfiguration(data);
      config.setDefaults(configData);
    }
  }
  
  public static FileConfiguration getConfig() {
    if (config == null)
      reload();
    return config;
  }
}
