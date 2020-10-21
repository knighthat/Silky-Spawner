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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {
	public static String AddColors(String String) {
		return ChatColor.translateAlternateColorCodes('&', String);
	}

	public static String StripColors(String String) {
		return ChatColor.stripColor(AddColors(String));
	}

	public static TextComponent hoverNclick(String Text, String ClickText) {
		TextComponent builder = new TextComponent(Text);
		builder.setColor(Storage.TextColor());
		builder.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ClickText));
		if (ClickText != null)
			builder.setHoverEvent(
			    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Storage.HoverText()).color(Storage.HoverColor()).create()));
		return builder;
	}

	public static String arrayToString(String[] Array, int Start) {
		StringBuffer buffer = new StringBuffer();
		for (int loop = Start; loop < Array.length; loop++)
			buffer.append(Array[loop].concat(loop == Array.length - 1 ? "" : " "));
		return buffer.toString();
	}

	public static boolean charsCounting(Player player, String input, String Type) {
		input = Config.getConfig().getBoolean("CountTheCodes") ? input : StripColors(input);
		String type = Config.getConfig().getString(Type);
		int Minimum = Config.getConfig().getInt("MinimumChars");
		int Maximum = Config.getConfig().getInt("MaximumChars");
		int count = 0;
		for (int i = 0; i < input.length(); i++)
			if (!String.valueOf(input.charAt(i)).equals(" "))
				count++;
		if (count < Minimum || count > Maximum) {
			player.sendMessage(Utils.AddColors(
			    Storage.Prefix() + Config.getConfig().getString(count < Minimum ? "InputTooShort" : "InputTooLong").replace("%type%", type)));
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
}
