package Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import Utilities.Methods;
import me.TnKnight.SilkySpawner.SilkySpawner;

public class CustomSymbols {
  private static FileReader file;
  private static SilkySpawner Main = SilkySpawner.instance;
  private static Map<String, String> symbols = new HashMap<>();
  
  public static void loadFile() {
    if (!(new File(Main.getDataFolder(), "symbols.txt").exists()))
      Main.saveResource("symbols.txt", false);
    try {
      file = new FileReader(new File(Main.getDataFolder(), "symbols.txt"));
    }
    catch (FileNotFoundException e) {}
  }
  
  public static void loadSymbols() {
    loadFile();
    symbols.clear();
    BufferedReader buff = new BufferedReader(file);
    String str;
    try {
      while ((str = buff.readLine()) != null)
        if (str.startsWith("[")) {
          int end = str.lastIndexOf(']') + 1;
          final String regret = str.substring(0, end);
          final String replace = str.substring(end++);
          symbols.put(regret, replace);
        }
      buff.close();
    }
    catch (IOException e) {
      final String url = "\"https://github.com/knighthat/Silky-Spawner/issues\"";
      Methods.sendLog("Cannot read the symbols.txt file! Please report to me at " + url + " ASAP.", Level.SEVERE, true);
    }
  }
  
  public static Map<String, String> getSymbols() {
    if (symbols == null || symbols.size() < 1)
      loadSymbols();
    return symbols;
  }
}
