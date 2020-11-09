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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.Files;

import Files.Config;
import Files.CustomSymbols;
import Files.InvConfiguration;
import Files.Message;
import Files.Mobs;
import Utilities.Methods;
import me.TnKnight.SilkySpawner.Commands.CommandsManager;
import me.TnKnight.SilkySpawner.Listeners.Interactions;
import me.TnKnight.SilkySpawner.Listeners.Spawners;
import me.TnKnight.SilkySpawner.Menus.MenusStorage;

public class SilkySpawner extends JavaPlugin {
  public static SilkySpawner instance;
  public static String getName;
  
  @Override
  public void onEnable() {
    getName = "[" + getDescription().getName() + "] ";
    instance = this;
    if (getVersion() < 13) {
      Methods.sendLog("Unsupported server version! Disabling...", Level.SEVERE, true);
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    Methods.sendLog("Starting up, please wait...", Level.INFO, true);
    Config.startup();
    Message.startup();
    InvConfiguration.startup();
    Mobs.startup();
    CustomSymbols.loadSymbols();
    new CommandsManager();
    new Spawners(this);
    new Interactions(this);
    if (Storage.getBoolean("CustomEnchantment"))
      CustomEnchantment.Register();
    configVersion();
    if (Storage.getBoolean("auto-check")) {
      checkNewVersion();
      new BukkitRunnable() {
        @Override
        public void run() {
          checkNewVersion();
        }
      }.runTaskTimer(this, 504000, 504000);
    }
    Methods.sendLog("Plugin has been successfully loaded!", Level.INFO, true);
  }
  
  @Override
  public void onDisable() {
    if (getVersion() < 13)
      return;
    if (Storage.getBoolean("CustomEnchantment"))
      CustomEnchantment.unRegister();
    Methods.sendLog("All data saved, disabling plugin...", Level.INFO, true);
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
    if (Storage.ValidateCfg("version").equals(getDescription().getVersion()))
      return;
    File newDest = new File(getDataFolder() + File.separator + "OldFiles");
    newDest.mkdir();
    Methods.sendLog("Defferent version detected! Backing up old files..", Level.SEVERE, true);
    for (File f : Arrays.asList(Config.file, Message.file, InvConfiguration.file)) {
      Path src = Paths.get(f.getAbsolutePath());
      Path dest = Paths.get(newDest.getAbsolutePath() + File.separator + f.getName().concat(".old"));
      try {
        Files.copy(src.toFile(), dest.toFile());
        f.delete();
        saveResource(f.getName(), false);
      }
      catch (IOException e) {
        Methods.sendLog("Error occurs when trying to copy " + f.getName() + "! Please, delete or move", Level.SEVERE, true);
        Methods.sendLog("" + f.getName() + " away and let plugin create again.", Level.SEVERE, false);
      }
    }
    Config.reload();
    Message.reload();
    InvConfiguration.reload();
  }
  
  private void checkNewVersion() {
    Methods.sendLog("Checking for new version...", Level.INFO, true);
    try {
      BufferedReader file = new BufferedReader(
        new InputStreamReader(new URL("https://raw.githubusercontent.com/knighthat/Silky-Spawner/master/plugin.yml").openStream()));
      String str;
      while ((str = file.readLine()) != null)
        if (str.startsWith("version: "))
          if (!str.replace("version:", "").trim().equals(getDescription().getVersion())) {
            Methods.sendLog("A new version " + str.replace("version:", "").trim() + " is avaible. Please update!", Level.WARNING, true);
          } else
            Methods.sendLog("You're on the lastest version!", Level.INFO, true);
      file.close();
    }
    catch (IOException e) {
      Methods.sendLog("Failed! Please report to my page ASAP.", Level.SEVERE, true);
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
