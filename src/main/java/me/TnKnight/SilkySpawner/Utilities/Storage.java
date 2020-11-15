package me.TnKnight.SilkySpawner.Utilities;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Files.Config;
import me.TnKnight.SilkySpawner.Files.CustomSymbols;
import me.TnKnight.SilkySpawner.Files.InvConfiguration;
import me.TnKnight.SilkySpawner.Files.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Storage
{
	/*
	 * Variables
	 */
	private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-f\\d]{6}");
	private static final String notNull = " can't be null, please check again!";
	
	public static final Double SERIAL = 5.85731840133667;
	public static final String wildcard = "silkyspawner.*";
	
	private static String hexConverter(String input) {
		Matcher matcher = HEX_PATTERN.matcher(input);
		while (matcher.find()) {
			final String color = input.substring(matcher.start(), matcher.end());
			input = input.replace(color, ChatColor.of(color).toString());
			matcher = HEX_PATTERN.matcher(input);
		}
		return input;
	}
	
	/*
	 * Utils
	 */
	public static String addColors(String input) {
		if (SilkySpawner.getVersion() >= 16)
			input = hexConverter(input);
		for (String symbol : CustomSymbols.getSymbols().keySet())
			input = input.replace(symbol, CustomSymbols.getSymbols().get(symbol));
		return ChatColor.translateAlternateColorCodes('&', input);
	}
	
	public static String stripColors(final String input) {
		return ChatColor.stripColor(addColors(input));
	}
	
	public static Object validateCfg(String path) {
		Validate.notNull(Config.getConfig().get(path), path + notNull);
		return Config.getConfig().get(path);
	}
	
	public static String validateMsg(String path) {
		Validate.notNull(Message.getConfig().getString(path), path + notNull);
		return Message.getConfig().getString(path);
	}
	
	public static String validateInv(String path) {
		Validate.notNull(InvConfiguration.getConfig().getString(path), path + notNull);
		return InvConfiguration.getConfig().getString(path);
	}
	
	public static String prefix() {
		return validateCfg("Prefix").toString();
	}
	
	public static String getMsg(String path) {
		return addColors(prefix() + validateMsg(path));
	}
	
	@SuppressWarnings("deprecation")
	public static BaseComponent[] clickableMessage(String display, String receive) {
		ComponentBuilder builder = new ComponentBuilder(display).color(getDisplayColor());
		builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getHoverText()).color(getHoverColor()).create()));
		builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, receive));
		return builder.create();
	}
	
	public static String arrayToString(String[] array, int start) {
		StringBuffer string = new StringBuffer();
		for (int i = 0; i < array.length; i++)
			string.append(array[i].trim().concat((i == array.length - 1) ? "" : " "));
		return string.toString();
	}
	
	public static final boolean countingSystem(final Player player, String input, final boolean type) {
		if (player.hasPermission("silkyspawner.length.bypass") || player.hasPermission(wildcard))
			return true;
		input = (boolean) validateCfg("CountTheCodes") ? input : stripColors(input);
		final int min = (int) validateCfg("MinimumChars");
		final int max = (int) validateCfg("MaximumChars");
		int count = 0;
		if (SilkySpawner.getVersion() >= 16) {
			Matcher matcher = HEX_PATTERN.matcher(input);
			while (matcher.find()) {
				final String color = input.substring(matcher.start(), matcher.end());
				input = input.replace(color, "");
				if ((boolean) validateCfg("CountTheCodes"))
					count++;
			}
		}
		for (String symbol : CustomSymbols.getSymbols().keySet())
			input = input.replace(symbol, CustomSymbols.getSymbols().get(symbol));
		for (char Char : input.toCharArray())
			if (!String.valueOf(Char).isBlank())
				count++;
		if (count > min || count < max)
			return true;
		String message = getMsg(count < min ? "InputTooShort" : "InputTooLong");
		message = message.replace("%type%", validateCfg(type ? "Name" : "Lore").toString());
		player.sendMessage(message);
		return false;
	}
	
	public static Material getMaterial(String path) {
		final String material = validateInv(path).toUpperCase();
		boolean contains = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains(material);
		if (!material.equalsIgnoreCase("AIR") && contains)
			return Material.getMaterial(material);
		return Material.AIR;
	}
	
	public static final boolean cmdConfirm(final Player player, String string, String usage, String label) {
		if (string != notNull && !string.isEmpty())
			return true;
		final String message = usage.replace(usage.split(" ")[0], label);
		ComponentBuilder builder = new ComponentBuilder(validateMsg("MistypedCommand"));
		builder.append(clickableMessage(message, message));
		player.spigot().sendMessage(builder.create());
		return false;
	}
	
	public static final boolean permConfirm(final CommandSender sender, String[] permissions) {
		if (sender.hasPermission(wildcard))
			return true;
		for (String perm : permissions)
			if (!sender.hasPermission("silkyspawner." + perm) && !perm.endsWith(".*")) {
				sender.sendMessage(noPerm("silkyspawner." + perm));
				return false;
			}
		return true;
	}
	
	public static final boolean holdingItem(final Player player, final boolean spawner, final boolean name) {
		final String inHand = player.getInventory().getItemInMainHand().getType().toString();
		final boolean isHolding = spawner ? inHand.equals("SPAWNER") : inHand.endsWith("_PICKAXE");
		final String message = getMsg(spawner ? "NotHoldingSpawner" : "NotHoldingPickaxe")
				.replace("%type%", validateCfg(name ? "Name" : "Lore").toString());
		if (!isHolding)
			player.sendMessage(message);
		return true;
	}
	
	public static String noPerm(String permission) {
		Validate.notNull(permission);
		return addColors(validateMsg("NoPerm").replace("%perm%", permission));
	}
	
	/*
	 * Storage
	 */
	public static final String getHoverText() {
		return validateCfg("HelpCommand.HoverText").toString();
	}
	
	@SuppressWarnings("deprecation")
	public static final ChatColor getDisplayColor() {
		final String input = (String) validateCfg("HelpCommand.TextColor");
		final boolean match = HEX_PATTERN.matcher(input).matches();
		if (SilkySpawner.getVersion() >= 16 && match)
			return ChatColor.of(input);
		return ChatColor.valueOf(input.toUpperCase());
	}
	
	@SuppressWarnings("deprecation")
	public static final ChatColor getHoverColor() {
		final String input = (String) validateCfg("HelpCommand.HoverColor");
		final boolean match = HEX_PATTERN.matcher(input).matches();
		if (SilkySpawner.getVersion() >= 16 && match)
			return ChatColor.of(input);
		return ChatColor.valueOf(input.toUpperCase());
	}
	
	public static BaseComponent[] misTyped(String command, final String label) {
		ComponentBuilder builder = new ComponentBuilder(addColors(validateMsg("MistypedCommand")));
		command = command.replace(command.split(" ")[0], "/" + label);
		builder.append(clickableMessage(command, command));
		return builder.create();
	}
	
	/*
	 * Methods
	 */
	
	public static ItemStack setSpawner(String name, List<String> lore, EntityType type) {
		ItemStack spawner = new ItemStack(Material.SPAWNER);
		BlockStateMeta state = (BlockStateMeta) spawner.getItemMeta();
		state.setDisplayName(name == null ? null : addColors(name));
		if (lore != null)
			state.setLore(lore);
		CreatureSpawner creature = (CreatureSpawner) state.getBlockState();
		creature.setSpawnedType(type);
		state.setBlockState(creature);
		spawner.setItemMeta(state);
		return spawner;
	}
	
	public static void takeItem(final Player player) {
		ItemStack inHand = player.getInventory().getItemInMainHand();
		if (inHand.getAmount() > 1) {
			inHand.setAmount(inHand.getAmount() - 1);
		} else
			player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
		player.updateInventory();
	}
	
	public static void addItem(final Player player, ItemStack item, final boolean take) {
		if (take)
			takeItem(player);
		if (player.getInventory().firstEmpty() < 0) {
			final String invFull = addColors(validateMsg("InventoryFull"));
			player.sendTitle(invFull, null, 5, 60, 20);
			player.getWorld().dropItem(player.getLocation(), item);
		} else
			player.getInventory().addItem(item);
	}
	
	public static void clearArea(final Location loc, final int radius) {
		loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream().forEach(E ->
		{
			if (E.getType().equals(EntityType.ARMOR_STAND) && E.getCustomName() != null && ((ArmorStand) E).getHealth() == SERIAL)
				E.remove();
		});
	}
	
	public static void sendLog(String message, final String level, final boolean addPrefix) {
		final Level lvl = Level.parse(level.toUpperCase());
		Bukkit.getServer().getLogger().log(lvl, (addPrefix ? SilkySpawner.getName : "") + addColors(message));
	}
	
	public static String getMobName(final EntityType spawnedType) {
		return addColors(validateCfg("TypeOfCreature").toString().replace("%creature_type%", MobsList.getMobName(spawnedType)));
	}
}

