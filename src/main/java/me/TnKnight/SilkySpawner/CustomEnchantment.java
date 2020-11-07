package me.TnKnight.SilkySpawner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Utilities.Methods;
import Utilities.Utils;
import net.md_5.bungee.api.ChatColor;

public class CustomEnchantment {
	public static final Enchantment PICKDASPAWNER = new BaseCustomEnchant("spawner_picker");
	private static BaseCustomEnchant ench = new BaseCustomEnchant("spawner_picker");

	public static void Register() {
		boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(PICKDASPAWNER);
		if (!registered)
			registerEnchantment(PICKDASPAWNER);
		Methods.sendLog("Custom Enchanment successfully registered!", Level.INFO, true);
	}

	@SuppressWarnings("unchecked")
	public static void unRegister() {
		try {
			Field field = Enchantment.class.getDeclaredField("acceptingNew");
			field.setAccessible(true);
			Map<String, Enchantment> byName = (HashMap<String, Enchantment>) field.get(null);
			if (byName.containsKey(ench.getName()))
				byName.remove(ench.getName());
		} catch (Exception e) {
		}
	}

	public static void registerEnchantment(Enchantment ench) {
		try {
			Field field = Enchantment.class.getDeclaredField("acceptingNew");
			field.setAccessible(true);
			field.set(null, true);
			Enchantment.registerEnchantment(ench);
		} catch (Exception e) {
			Methods.sendLog("Failed in enabling Custom Enchantment! Please restart the server.", Level.SEVERE, true);
			Methods.sendLog("Report this error if you received this message after resrarting!", Level.SEVERE, false);
		}
	}

	public static void enchantItem(ItemStack item) {
		ItemMeta iMeta = item.getItemMeta();
		List<String> lore = new ArrayList<>(Arrays.asList(ChatColor.GRAY + Utils.StripColors(Storage.ValidateCfg("EnchantmentName")), " "));
		if (iMeta.hasLore())
			lore.addAll(iMeta.getLore());
		iMeta.setLore(lore);
		item.setItemMeta(iMeta);
		item.addEnchantment(PICKDASPAWNER, 1);
	}
}

class BaseCustomEnchant extends Enchantment {

	public BaseCustomEnchant(String key) {
		super(NamespacedKey.minecraft(key));
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment arg0) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public String getName() {
		return "SpawnerPicker";
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

}
