package Utilities;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import Files.Config;
import Files.CustomSymbols;
import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Menus.MenuManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils extends Storage {
  private static final Pattern hexPattern = Pattern.compile("#[a-fA-f\\d]{6}");
  
  public static String hexConvert(String string) {
    Matcher matcher = hexPattern.matcher(string);
    while (matcher.find()) {
      String color = string.substring(matcher.start(), matcher.end());
      string = string.replace(color, net.md_5.bungee.api.ChatColor.of(color).toString());
      matcher = hexPattern.matcher(string);
    }
    return string;
  }
  
  public static String AddColors(String string) {
    if (SilkySpawner.getVersion() >= 16)
      string = hexConvert(string);
    for (String symbol : CustomSymbols.getSymbols().keySet())
      string = string.replace(symbol, CustomSymbols.getSymbols().get(symbol));
    return ChatColor.translateAlternateColorCodes('&', string);
  }
  
  public static String StripColors(String string) {
    return ChatColor.stripColor(AddColors(string));
  }
  
  @SuppressWarnings ("deprecation")
  public static TextComponent hoverNclick(String Text, String ClickText) {
    TextComponent builder = new TextComponent(Text);
    builder.setColor(TextColor());
    builder.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ClickText));
    if (ClickText != null)
      builder.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Storage.HoverText()).color(Storage.HoverColor())
        .create()));
    return builder;
  }
  
  public static String arrayToString(String[] Array, int Start) {
    StringBuffer buffer = new StringBuffer();
    for (int loop = Start; loop < Array.length; loop++)
      buffer.append(Array[loop].concat(loop == Array.length - 1 ? "" : " "));
    return buffer.toString();
  }
  
  public static boolean charsCounting(Player player, String input, String Type) {
    input = getBoolean("CountTheCodes") ? input : StripColors(input);
    String type = ValidateCfg(Type);
    int Minimum = Config.getConfig().getInt("MinimumChars");
    int Maximum = Config.getConfig().getInt("MaximumChars");
    int count = 0;
    if (SilkySpawner.getVersion() >= 16) {
      Matcher matcher = hexPattern.matcher(input);
      while (matcher.find()) {
        String color = input.substring(matcher.start(), matcher.end());
        input = input.replace(color, "");
        matcher = hexPattern.matcher(input);
        if (getBoolean("CountTheCodes"))
          count += 2;
      }
    }
    for (String symbol : CustomSymbols.getSymbols().keySet())
      input = input.replace(symbol, CustomSymbols.getSymbols().get(symbol));
    for (int i = 0; i < input.length(); i++)
      if (!String.valueOf(input.charAt(i)).isBlank())
        count++;
    if (count < Minimum || count > Maximum) {
      player.sendMessage(getMsg(count < Minimum ? "InputTooShort" : "InputTooLong").replace("%type%", type));
      return false;
    }
    return true;
  }
  
  public static Material ItemsChecking(String path) {
    final String name = MenuManager.getInv(path).toUpperCase();
    if (name.equals("AIR") || !Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains(name))
      return Material.AIR;
    return Material.getMaterial(name);
  }
}
