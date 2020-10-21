package me.TnKnight.SilkySpawner;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.MessageYAML;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Storage {

	protected static final String empty = "&c&lYOU MUST'VE MISSED SOMETHING OVER HERE!!!";

	protected static String ValidateCfg(String path, Boolean type) {
		if (!Config.getConfig().contains(path) || Config.getConfig().getString(path) == null)
			return type ? empty : "WHITE";
		return Config.getConfig().getString(path);
	}
	protected boolean cConfirm(Player player, String string, String usage) {
		if (string != null && !string.isEmpty())
			return true;
		player.spigot().sendMessage(cBuilder(usage).create());
		return false;
	}
	protected ComponentBuilder cBuilder(String usage) {
		ComponentBuilder builder = new ComponentBuilder(Utils.AddColors(MessageYAML.getConfig().getString("MistypedCommand")));
		builder.append(Utils.hoverNclick(usage, usage));
		return builder;
	}
	protected boolean mConfirm(Player player, String material, String type) {
		ItemStack hand = player.getInventory().getItemInMainHand();
		if (hand != null && hand.getType().equals(Material.getMaterial(material.toUpperCase())))
			return true;
		player.sendMessage(Storage.getMsg("NotHoldingSpawner").replace("%type%", Config.getConfig().getString(type)));
		return false;
	}
	public static String Prefix() {
		return ValidateCfg("Prefix", true);
	}
	public static ChatColor TextColor() {
		return ChatColor.valueOf(ValidateCfg("HelpCommand.TextColor", false));
	}
	public static String HoverText() {
		return ValidateCfg("HelpCommand.HoverText", true);
	}
	public static ChatColor HoverColor() {
		return ChatColor.valueOf(ValidateCfg("HelpCommand.HoverColor", false));
	}

	public static String getConfig(String path) {
		return Utils.AddColors(Prefix() + ValidateCfg(path, true));
	}
	public static String getMsg(String path) {
		String finalString = MessageYAML.getConfig().getString(path);
		if (!MessageYAML.getConfig().contains(path) || finalString == null)
			finalString = empty;
		return Utils.AddColors(Prefix() + finalString);
	}
	public static boolean confirmPerm(Player player, String cmd) {
		if (!Config.getConfig().contains("CommandsAssistant." + cmd + ".Permission"))
			return true;
		return player.hasPermission(Config.getConfig().getString("CommandsAssistant." + cmd + ".Permission"));
	}
	public static String getPerm(String cmd) {
		return Config.getConfig().getString("CommandsAssistant." + cmd + ".Permission");
	}
}
