package me.TnKnight.SilkySpawner;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.MessageYAML;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {

	public static String Prefix = Config.getConfig().getString("Prefix");

	public static String AddColors(String String) {
		return ChatColor.translateAlternateColorCodes('&', String);
	}

	public static String StripColors(String String) {
		return ChatColor.stripColor(AddColors(String));
	}

	public static String getColor(String String) {
		return ChatColor.getLastColors(String);
	}

	public static String getMessage(String path) {
		return AddColors(Prefix + MessageYAML.getConfig().getString(path));
	}

	public static TextComponent hoverNclick(String Text, String TextColor, String HoverText, String HoverColor, String ClickText) {
		TextComponent text = new TextComponent(Text);
		text.setColor(net.md_5.bungee.api.ChatColor.valueOf(TextColor));
		if (!HoverText.equals(null))
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
			    new ComponentBuilder(HoverText)
			        .color(net.md_5.bungee.api.ChatColor.valueOf(HoverColor) != null ? net.md_5.bungee.api.ChatColor.valueOf(HoverColor)
			            : net.md_5.bungee.api.ChatColor.AQUA)
			        .italic(false).create()));
		if (!ClickText.equals(null))
			text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ClickText));
		return text;
	}

	public static String arrayToString(String[] Array, int Start) {
		StringBuffer buffer = new StringBuffer();
		for (int loop = Start; loop < Array.length; loop++)
			buffer.append(Array[loop].concat(loop == Array.length - 1 ? "" : " "));
		return buffer.toString();
	}

	public static boolean charsCounting(Player player, String input, String Type) {
		String type = Config.getConfig().getString(Type);
		int Minimum = Config.getConfig().getInt("MinimumChars");
		int Maximum = Config.getConfig().getInt("MaximumChars");
		int count = 0;
		for (int i = 0; i < input.length(); i++)
			if (!String.valueOf(input.charAt(i)).equals(" "))
				count++;
		if (count < Minimum || count > Maximum) {
			player.sendMessage(
			    Utils.AddColors(Prefix + Config.getConfig().getString(count < Minimum ? "InputTooShort" : "InputTooLong").replace("%type%", type)));
			return false;
		}
		return true;
	}

	public static ItemMeta SpawnCreature(ItemStack Item, EntityType entityType) {
		BlockStateMeta bMeta = (BlockStateMeta) Item.getItemMeta();
		CreatureSpawner cSpawner = (CreatureSpawner) bMeta.getBlockState();
		cSpawner.setSpawnedType(entityType);
		bMeta.setBlockState(cSpawner);
		return bMeta;
	}

	public static boolean ItemsChecking(String Name) {
		return Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains(Name.toUpperCase());
	}

	public static String getPermission(String command) {
		return Config.getConfig().getString("CommandsAssistant." + command + ".Permission");
	}
}
