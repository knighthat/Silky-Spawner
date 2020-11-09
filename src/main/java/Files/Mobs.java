package Files;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import Utilities.MobsList;
import me.TnKnight.SilkySpawner.SilkySpawner;

public class Mobs {
  private static SilkySpawner Main = SilkySpawner.instance;
  private static FileConfiguration config = null;
  public static File file = null;
  
  public static void startup() {
    if (file == null)
      file = new File(Main.getDataFolder(), "mobs.yml");
    if (!file.exists())
      Main.saveResource("mobs.yml", false);
    MobsList.setCustomName();
  }
  
  public static void reload() {
    if (file == null)
      file = new File(Main.getDataFolder(), "mobs.yml");
    if (!file.exists())
      Main.saveResource("mobs.yml", false);
    config = YamlConfiguration.loadConfiguration(file);
    Reader data = null;
    try {
      data = new InputStreamReader(Main.getResource("mobs.yml"), "UTF8");
    }
    catch (Exception e) {}
    if (data != null) {
      YamlConfiguration def = YamlConfiguration.loadConfiguration(data);
      config.setDefaults(def);
    }
    MobsList.setCustomName();
  }
  
  public static FileConfiguration getConfig() {
    if (config == null)
      reload();
    return config;
  }
}
