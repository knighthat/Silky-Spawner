package me.TnKnight.SilkySpawner;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Files.Config;
import Files.Message;
import Utilities.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Storage {
  protected static final String nll = " can't be null, please double check!";
  protected final String allPerm = "silkyspawner.*";
  
  public static String ValidateCfg(String path) {
    Validate.notNull(Config.getConfig().getString(path), path + nll);
    return Config.getConfig().getString(path);
  }
  
  public static String Prefix() {
    return ValidateCfg("Prefix");
  }
  
  @SuppressWarnings ("deprecation")
  public static ChatColor TextColor() {
    final String path = ValidateCfg("HelpCommand.TextColor");
    if (SilkySpawner.getVersion() >= 16)
      return ChatColor.of(path);
    return ChatColor.valueOf(path);
  }
  
  public static String HoverText() {
    return ValidateCfg("HelpCommand.HoverText");
  }
  
  @SuppressWarnings ("deprecation")
  public static ChatColor HoverColor() {
    final String path = ValidateCfg("HelpCommand.HoverColor");
    if (SilkySpawner.getVersion() >= 16)
      return ChatColor.of(path);
    return ChatColor.valueOf(path);
  }
  
  public static boolean getBoolean(String path) {
    return Config.getConfig().getBoolean(path);
  }
  
  protected boolean cConfirm(Player player, String string, String usage, String label) {
    if (string != null && !string.isEmpty())
      return true;
    final String Usesage = usage.replace(usage.split(" ")[0], label);
    player.spigot().sendMessage(cBuilder(Usesage).create());
    return false;
  }
  
  protected ComponentBuilder cBuilder(String usage) {
    return new ComponentBuilder(Utils.AddColors(Message.getConfig().getString("MistypedCommand"))).append(Utils.hoverNclick(usage, usage));
  }
  
  protected boolean mConfirm(Player player, String material, String type) {
    ItemStack hand = player.getInventory().getItemInMainHand();
    if (hand != null && hand.getType().equals(Material.getMaterial(material.toUpperCase())))
      return true;
    player.sendMessage(getMsg("NotHoldingSpawner").replace("%type%", ValidateCfg(type)));
    return false;
  }
  
  protected static String getMsg(String path) {
    Validate.notNull(Message.getConfig().getString(path), path + nll);
    return Utils.AddColors(Prefix() + Message.getConfig().getString(path));
  }
  
  protected boolean permConfirm(CommandSender player, String perm) {
    if (player.hasPermission("silkyspawner." + perm) || player.hasPermission(allPerm) || (perm.startsWith("silkyspawer.command.") && player
      .hasPermission("silkyspawner.command.*")))
      return true;
    if (!perm.endsWith(".*"))
      player.sendMessage(Utils.AddColors(noPerm(perm)));
    return false;
  }
  
  protected String noPerm(String perm) {
    return getMsg("NoPerm").replace("%perm%", "silkyspawner." + perm);
  }
}
